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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * This Reader reads files which has one SMILES string on each
 * line, where the format is given as below:
 * <pre>
 * COC ethoxy ethane
 * </pre>
 * Thus first the SMILES, and then after the first space (or tab) on the line a title
 * that is stored as {@link CDKConstants#TITLE}. For legacy comparability the
 * title is also placed in a "SMIdbNAME" property. If a line is invalid an empty
 * molecule is inserted into the container set. The molecule with have the prop
 * {@link IteratingSMILESReader#BAD_SMILES_INPUT} set to the input line that
 * could not be read. 
 *
 * <p>For each line a molecule is generated, and multiple Molecules are
 * read as MoleculeSet.
 *
 * @cdk.module  smiles
 * @cdk.githash
 * @cdk.iooptions
 * @cdk.keyword file format, SMILES
 *
 * @see org.openscience.cdk.io.iterator.IteratingSMILESReader
 */
public class SMILESReader extends DefaultChemObjectReader {

    private BufferedReader      input  = null;
    private SmilesParser        sp     = null;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SMILESReader.class);

    /**
     * Construct a new reader from a Reader and a specified builder object.
     *
     * @param input   The Reader object from which to read structures
     */
    public SMILESReader(Reader input) {
        this.input = new BufferedReader(input);
    }

    public SMILESReader(InputStream input) {
        this(new InputStreamReader(input));
    }

    public SMILESReader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return SMILESFormat.getInstance();
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
        if (IAtomContainerSet.class.equals(classObject)) return true;
        if (IChemFile.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IChemFile.class.equals(anInterface)) return true;
            if (IAtomContainerSet.class.equals(anInterface)) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Reads the content from a XYZ input. It can only return a
     * {@link IChemObject} of type {@link IChemFile}.
     *
     * @param object class must be of type ChemFile
     *
     * @see IChemFile
     */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        sp = new SmilesParser(object.getBuilder());

        if (object instanceof IAtomContainerSet) {
            return (T) readAtomContainerSet((IAtomContainerSet) object);
        } else if (object instanceof IChemFile) {
            IChemFile file = (IChemFile) object;
            IChemSequence sequence = file.getBuilder().newInstance(IChemSequence.class);
            IChemModel chemModel = file.getBuilder().newInstance(IChemModel.class);
            chemModel.setMoleculeSet(readAtomContainerSet(file.getBuilder().newInstance(IAtomContainerSet.class)));
            sequence.addChemModel(chemModel);
            file.addChemSequence(sequence);
            return (T) file;
        } else {
            throw new CDKException("Only supported is reading of MoleculeSet objects.");
        }
    }

    // private procedures

    /**
     *  Private method that actually parses the input to read a ChemFile
     *  object.
     *
     * @param som The set of molecules that came from the file
     * @return A ChemFile containing the data parsed from input.
     */
    private IAtomContainerSet readAtomContainerSet(IAtomContainerSet som) {
        try {
            String line = input.readLine().trim();
            while (line != null) {
                logger.debug("Line: ", line);

                final String name = suffix(line);

                try {
                    IAtomContainer molecule = sp.parseSmiles(line);
                    molecule.setProperty("SMIdbNAME", name);
                    som.addAtomContainer(molecule);
                } catch (CDKException exception) {
                    logger.warn("This SMILES could not be parsed: ", line);
                    logger.warn("Because of: ", exception.getMessage());
                    logger.debug(exception);
                    IAtomContainer empty = som.getBuilder().newInstance(IAtomContainer.class, 0, 0, 0, 0);
                    empty.setProperty(IteratingSMILESReader.BAD_SMILES_INPUT, line);
                    som.addAtomContainer(empty);
                }
                if (input.ready()) {
                    line = input.readLine();
                } else {
                    line = null;
                }
            }
        } catch (Exception exception) {
            logger.error("Error while reading SMILES line: ", exception.getMessage());
            logger.debug(exception);
        }
        return som;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    /**
     * Obtain the suffix after a line containing SMILES. The suffix follows
     * any ' ' or '\t' termination characters.
     *
     * @param line input line
     * @return the suffix - or an empty line
     */
    private String suffix(final String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ' ' || c == '\t') return line.substring(i + 1);
        }
        return null;
    }
}
