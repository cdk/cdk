/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.formats.INChIPlainTextFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Reads the content of a IUPAC/NIST Chemical Identifier (INChI) plain text
 * document. This reader parses output generated with INChI 1.12beta like:
 * <pre>
 *
 * Input_File: "E:\Program Files\INChI\inchi-samples\Figure04.mol"
 *
 * Structure: 1
 * INChI=1.12Beta/C6H6/c1-2-4-6-5-3-1/h1-6H
 * AuxInfo=1.12Beta/0/N:1,2,3,4,5,6/E:(1,2,3,4,5,6)/rA:6CCCCCC/rB:s1;d1;d2;s3;s4d5;/rC:5.6378,-4.0013,0;5.6378,-5.3313,0;4.4859,-3.3363,0;4.4859,-5.9963,0;3.3341,-4.0013,0;3.3341,-5.3313,0;
 * </pre>
 *
 * @cdk.module extra
 * @cdk.githash
 * @cdk.iooptions
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 * @cdk.created 2004-08-01
 *
 * @cdk.keyword file format, INChI
 * @cdk.keyword chemical identifier
 * @cdk.require java1.4+
 *
 * @see     org.openscience.cdk.io.INChIReader
 */
public class INChIPlainTextReader extends DefaultChemObjectReader {

    private BufferedReader            input;
    private INChIContentProcessorTool inchiTool;

    /**
     * Construct a INChI reader from a Reader object.
     *
     * @param input the Reader with the content
     */
    public INChIPlainTextReader(Reader input) {
        this.init();
        setReader(input);
        inchiTool = new INChIContentProcessorTool();
    }

    public INChIPlainTextReader(InputStream input) {
        this(new InputStreamReader(input));
    }

    public INChIPlainTextReader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return INChIPlainTextFormat.getInstance();
    }

    @Override
    public void setReader(Reader input) {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    /**
     * Initializes this reader.
     */
    private void init() {}

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IChemFile.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IChemFile.class.equals(interfaces[i])) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Reads a IChemObject of type object from input.
     * Supported types are: ChemFile.
     *
     * @param  object type of requested IChemObject
     * @return the content in a ChemFile object
     */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            return (T) readChemFile((IChemFile) object);
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    // private functions

    /**
     * Reads a ChemFile object from input.
     *
     * @return ChemFile with the content read from the input
     */
    private IChemFile readChemFile(IChemFile cf) throws CDKException {
        // have to do stuff here
        try {
            String line = null;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("INChI=") || line.startsWith("InChI=")) {
                    // ok, the fun starts
                    cf = cf.getBuilder().newInstance(IChemFile.class);
                    // ok, we need to parse things like:
                    // INChI=1.12Beta/C6H6/c1-2-4-6-5-3-1/h1-6H
                    final String INChI = line.substring(6);
                    StringTokenizer tokenizer = new StringTokenizer(INChI, "/");
                    // ok, we expect 4 tokens
                    tokenizer.nextToken(); // 1.12Beta not stored since never used
                    final String formula = tokenizer.nextToken(); // C6H6
                    String connections = null;
                    if (tokenizer.hasMoreTokens()) {
                        connections = tokenizer.nextToken().substring(1); // 1-2-4-6-5-3-1
                    }
                    //final String hydrogens = tokenizer.nextToken().substring(1); // 1-6H

                    IAtomContainer parsedContent = inchiTool.processFormula(
                            cf.getBuilder().newInstance(IAtomContainer.class), formula);
                    if (connections != null)
                        inchiTool.processConnections(connections, parsedContent, -1);

                    IAtomContainerSet moleculeSet = cf.getBuilder().newInstance(IAtomContainerSet.class);
                    moleculeSet.addAtomContainer(cf.getBuilder().newInstance(IAtomContainer.class, parsedContent));
                    IChemModel model = cf.getBuilder().newInstance(IChemModel.class);
                    model.setMoleculeSet(moleculeSet);
                    IChemSequence sequence = cf.getBuilder().newInstance(IChemSequence.class);
                    sequence.addChemModel(model);
                    cf.addChemSequence(sequence);
                }
            }
        } catch (IOException | IllegalArgumentException exception) {
            exception.printStackTrace();
            throw new CDKException("Error while reading INChI file: " + exception.getMessage(), exception);
        }
        return cf;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
