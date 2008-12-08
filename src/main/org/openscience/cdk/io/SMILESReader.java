/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This Reader reads files which has one SMILES string on each
 * line, where the format is given as below:
 * <pre>
 * COC ethoxy ethane
 * </pre>
 * Thus first the SMILES, and then after the first space (or tab) on the line a title
 * that is stored as "SMIdbNAME" property in the Molecule.
 *
 * <p>For each line a molecule is generated, and multiple Molecules are
 * read as MoleculeSet.
 *
 * @cdk.module  smiles
 * @cdk.svnrev  $Revision$
 * @cdk.keyword file format, SMILES
 *
 * @see org.openscience.cdk.io.iterator.IteratingSMILESReader
 */
@TestClass("org.openscience.cdk.io.SMILESReaderTest")
public class SMILESReader extends DefaultChemObjectReader {

    private BufferedReader input = null;
    private SmilesParser sp = null;
    private LoggingTool logger;

    /* 
     * construct a new reader from a Reader type object
     *
     * @param input reader from which input is read
     */
    public SMILESReader(Reader input) {
        logger = new LoggingTool(this);
        this.input = new BufferedReader(input);
        sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    public SMILESReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public SMILESReader() {
        this(new StringReader(""));
    }

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return SMILESFormat.getInstance();
    }
    
    @TestMethod("testSetReader_Reader")
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @TestMethod("testSetReader_InputStream")
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
        for (Class anInterface : interfaces) {
            if (IChemFile.class.equals(anInterface)) return true;
            if (IMoleculeSet.class.equals(anInterface)) return true;
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
    @TestMethod("testReading,testReadingSmiFile_1,testReadingSmiFile_2,testReadingSmiFile_3")
    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IMoleculeSet) {
            return readMoleculeSet((IMoleculeSet)object);
        } else if (object instanceof IChemFile) {
            IChemFile file = (IChemFile)object;
            IChemSequence sequence = file.getBuilder().newChemSequence();
            IChemModel chemModel = file.getBuilder().newChemModel();
            chemModel.setMoleculeSet(readMoleculeSet(
            	file.getBuilder().newMoleculeSet()
            ));
            sequence.addChemModel(chemModel);
            file.addChemSequence(sequence);
            return file;
        } else {
            throw new CDKException("Only supported is reading of MoleculeSet objects.");
        }
    }

    // private procedures

    /**
     *  Private method that actually parses the input to read a ChemFile
     *  object.
     *
     * @param som The set of molecules that came fron the file
     * @return A ChemFile containing the data parsed from input.
     */
    private IMoleculeSet readMoleculeSet(IMoleculeSet som) {
        try {
            String line = input.readLine().trim();
            while (line != null) {
                logger.debug("Line: ", line);

                String[] tokens = line.split("[\\s\\t]+",2);
                if (tokens.length > 2) throw new Exception("Malformed line");

                String SMILES = tokens[0];
                String name = null;
                if (tokens.length == 2) name = tokens[1];

                logger.debug("Line contains SMILES and name: ", SMILES, " + " , name);

                try {
                    IMolecule molecule = sp.parseSmiles(SMILES);
                    som.addMolecule(molecule);
                    if (name != null) {
                        molecule.setProperty("SMIdbNAME", name);
                    }
                } catch (Exception exception) {
                    logger.warn("This SMILES could not be parsed: ", SMILES);
                    logger.warn("Because of: ", exception.getMessage());
                    logger.debug(exception);
                }
                if (input.ready()) { line = input.readLine(); } else { line = null; }
            }
        } catch (Exception exception) {
            logger.error("Error while reading SMILES line: ", exception.getMessage());
            logger.debug(exception);
        }
        return som;
    }
    
    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }
}
