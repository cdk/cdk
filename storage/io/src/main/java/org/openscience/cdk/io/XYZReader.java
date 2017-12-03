/* Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.XYZFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Reads an object from XYZ formated input.
 *
 * <p>This class is based on Dan Gezelter's XYZReader from Jmol
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @cdk.keyword file format, XYZ
 */
public class XYZReader extends DefaultChemObjectReader {

    private BufferedReader      input;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(XYZReader.class);

    /**
     * Construct a new reader from a Reader type object.
     *
     * @param input reader from which input is read
     */
    public XYZReader(Reader input) {
        this.input = new BufferedReader(input);
    }

    public XYZReader(InputStream input) {
        this(new InputStreamReader(input));
    }

    public XYZReader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return XYZFormat.getInstance();
    }

    @Override
    public void setReader(Reader input) throws CDKException {
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
     * reads the content from a XYZ input. It can only return a
     * IChemObject of type ChemFile
     *
     * @param object class must be of type ChemFile
     *
     * @see IChemFile
     */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            return (T) readChemFile((IChemFile) object);
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    // private procedures

    /**
     *  Private method that actually parses the input to read a ChemFile
     *  object.
     *
     * @return A ChemFile containing the data parsed from input.
     */
    private IChemFile readChemFile(IChemFile file) {
        IChemSequence chemSequence = file.getBuilder().newInstance(IChemSequence.class);

        int number_of_atoms = 0;
        StringTokenizer tokenizer;

        try {
            String line = input.readLine();
            while (input.ready() && line != null) {
                // parse frame by frame
                tokenizer = new StringTokenizer(line, "\t ,;");

                String token = tokenizer.nextToken();
                number_of_atoms = Integer.parseInt(token);
                String info = input.readLine();

                IChemModel chemModel = file.getBuilder().newInstance(IChemModel.class);
                IAtomContainerSet setOfMolecules = file.getBuilder().newInstance(IAtomContainerSet.class);

                IAtomContainer m = file.getBuilder().newInstance(IAtomContainer.class);
                m.setTitle(info);

                for (int i = 0; i < number_of_atoms; i++) {
                    line = input.readLine();
                    if (line == null) break;
                    if (line.startsWith("#") && line.length() > 1) {
                        Object comment = m.getProperty(CDKConstants.COMMENT);
                        if (comment == null) {
                            comment = "";
                        }
                        comment = comment.toString() + line.substring(1).trim();
                        m.setProperty(CDKConstants.COMMENT, comment);
                        logger.debug("Found and set comment: ", comment);
                        i--; // a comment line does not count as an atom
                    } else {
                        double x = 0.0f, y = 0.0f, z = 0.0f;
                        double charge = 0.0f;
                        tokenizer = new StringTokenizer(line, "\t ,;");
                        int fields = tokenizer.countTokens();

                        if (fields < 4) {
                            // this is an error but cannot throw exception
                        } else {
                            String atomtype = tokenizer.nextToken();
                            x = (new Double(tokenizer.nextToken())).doubleValue();
                            y = (new Double(tokenizer.nextToken())).doubleValue();
                            z = (new Double(tokenizer.nextToken())).doubleValue();

                            if (fields == 8) charge = (new Double(tokenizer.nextToken())).doubleValue();

                            IAtom atom = file.getBuilder().newInstance(IAtom.class, atomtype, new Point3d(x, y, z));
                            atom.setCharge(charge);
                            m.addAtom(atom);
                        }
                    }
                }

                setOfMolecules.addAtomContainer(m);
                chemModel.setMoleculeSet(setOfMolecules);
                chemSequence.addChemModel(chemModel);
                line = input.readLine();
            }
            file.addChemSequence(chemSequence);
        } catch (IOException e) {
            // should make some noise now
            file = null;
            logger.error("Error while reading file: ", e.getMessage());
            logger.debug(e);
        }
        return file;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
