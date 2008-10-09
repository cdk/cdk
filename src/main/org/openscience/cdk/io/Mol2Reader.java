/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.Symbols;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.Mol2Format;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Reads a molecule from an Mol2 file, such as written by Sybyl.
 * See the specs <a href="http://www.tripos.com/data/support/mol2.pdf">here</a>.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author         Egon Willighagen
 * @cdk.created    2003-08-21
 *
 * @cdk.keyword    file format, Mol2
 */
@TestClass("org.openscience.cdk.io.Mol2ReaderTest")
public class Mol2Reader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private LoggingTool logger = null;

    /**
     * Contructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public Mol2Reader(Reader in) {
        logger = new LoggingTool(this);
        input = new BufferedReader(in);
    }

    public Mol2Reader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public Mol2Reader() {
        this(new StringReader(""));
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return Mol2Format.getInstance();
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
		for (int i=0; i<interfaces.length; i++) {
			if (IChemModel.class.equals(interfaces[i])) return true;
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
    Class superClass = classObject.getSuperclass();
    if (superClass != null) return this.accepts(superClass);
		return false;
	}

    /**
     * Takes an object which subclasses IChemObject, e.g.Molecule, and will read
     * this from from the Reader. If the specific implementation
     * does not support a specific IChemObject it will throw an Exception.
     *
     * @param  object The object that subclasses IChemObject
     * @return        The IChemObject read
     * @exception     CDKException
     */
     public IChemObject read(IChemObject object) throws CDKException {
         if (object instanceof IChemFile) {
             IChemFile file = (IChemFile)object;
             IChemSequence sequence = file.getBuilder().newChemSequence();
             IChemModel model = file.getBuilder().newChemModel();
             IMoleculeSet moleculeSet = file.getBuilder().newMoleculeSet();
             moleculeSet.addMolecule(readMolecule(
                 model.getBuilder().newMolecule()
             ));
             model.setMoleculeSet(moleculeSet);
             sequence.addChemModel(model);
             file.addChemSequence(sequence);
             return file;
         } else if (object instanceof IChemModel) {
             IChemModel model = (IChemModel)object;
             IMoleculeSet moleculeSet = model.getBuilder().newMoleculeSet();
             moleculeSet.addMolecule(readMolecule(
                 model.getBuilder().newMolecule()
             ));
             model.setMoleculeSet(moleculeSet);
             return model;
         } else if (object instanceof IMolecule) {
             IMolecule molecule = (IMolecule)object;
        	 return readMolecule(molecule);
         } else {
             throw new CDKException("Only supported is ChemModel, and not " +
                 object.getClass().getName() + "."
             );
         }
     }
     
     @TestMethod("testAccepts")
    public boolean accepts(IChemObject object) {
         if (object instanceof IChemFile) {
             return true;
         } else if (object instanceof IChemModel) {
             return true;
         } else if (object instanceof IMolecule) {
             return true;
         }
         return false;
     }


    /**
     * Read a Reaction from a file in MDL RXN format
     *
     * @return  The Reaction that was read from the MDL file.
     */
    private IMolecule readMolecule(IMolecule molecule) throws CDKException {
        AtomTypeFactory atFactory = null;
        try {
            atFactory = AtomTypeFactory.getInstance(
                "org/openscience/cdk/config/data/mol2_atomtypes.xml", molecule.getBuilder()
            );
        } catch (Exception exception) {
            String error = "Could not instantiate an AtomTypeFactory";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
        try {
            String line = input.readLine();
            int atomCount = 0;
            int bondCount = 0;
            while (line != null) {
                if (line.startsWith("@<TRIPOS>MOLECULE")) {
                    logger.info("Reading molecule block");
                    // second line has atom/bond counts?
                    input.readLine(); // disregard the name line
                    String counts = input.readLine();
                    StringTokenizer tokenizer = new StringTokenizer(counts);
                    try {
                        atomCount = Integer.parseInt(tokenizer.nextToken());
                    } catch (NumberFormatException nfExc) {
                        String error = "Error while reading atom count from MOLECULE block";
                        logger.error(error);
                        logger.debug(nfExc);
                        throw new CDKException(error, nfExc);
                    }
                    if (tokenizer.hasMoreTokens()) {
                        try {
                            bondCount = Integer.parseInt(tokenizer.nextToken());
                        } catch (NumberFormatException nfExc) {
                            String error = "Error while reading atom and bond counts";
                            logger.error(error);
                            logger.debug(nfExc);
                            throw new CDKException(error, nfExc);
                        }
                    } else {
                        bondCount = 0;
                    }
                    logger.info("Reading #atoms: ", atomCount);
                    logger.info("Reading #bonds: ", bondCount);
                    
                    logger.warn("Not reading molecule qualifiers");
                } else if (line.startsWith("@<TRIPOS>ATOM")) {
                    logger.info("Reading atom block");
                    for (int i=0; i<atomCount; i++) {
                        line = input.readLine().trim();
                        StringTokenizer tokenizer = new StringTokenizer(line);
                        tokenizer.nextToken(); // disregard the id token
                        String nameStr = tokenizer.nextToken();
                        String xStr = tokenizer.nextToken();
                        String yStr = tokenizer.nextToken();
                        String zStr = tokenizer.nextToken();
                        String atomTypeStr = tokenizer.nextToken();
                        
                    	// fix OpenBabel atom type codes to SYBYL specification
                    	if ("S.o2".equals(atomTypeStr)) atomTypeStr = "S.O2";  
                    	if ("S.o".equals(atomTypeStr)) atomTypeStr = "S.O";

                        IAtom atom = molecule.getBuilder().newAtom("X");
                        IAtomType atomType;
                        try {
                        	atomType = atFactory.getAtomType(atomTypeStr);
                        } catch (Exception exception) {
                        	// ok, *not* an mol2 atom type
                        	atomType = null;
                        }
                        // Maybe it is just an element
                        if (atomType == null && isElementSymbol(atomTypeStr)) {
                        	atom.setSymbol(atomTypeStr);
                        } else {                        	
                            if (atomType == null) {
                                atomType = atFactory.getAtomType("X");
                            	logger.error("Could not find specified atom type: ", atomTypeStr);
                            }
                            AtomTypeManipulator.configure(atom, atomType);
                        }

                        atom.setID(nameStr);
                        atom.setAtomTypeName(atomTypeStr);
                        try {
                            double x = Double.parseDouble(xStr);
                            double y = Double.parseDouble(yStr);
                            double z = Double.parseDouble(zStr);
                            atom.setPoint3d(new Point3d(x, y, z));
                        } catch (NumberFormatException nfExc) {
                            String error = "Error while reading atom coordinates";
                            logger.error(error);
                            logger.debug(nfExc);
                            throw new CDKException(error, nfExc);
                        }
                        molecule.addAtom(atom);
                    }
                } else if (line.startsWith("@<TRIPOS>BOND")) {
                    logger.info("Reading bond block");
                    for (int i=0; i<bondCount; i++) {
                        line = input.readLine();
                        StringTokenizer tokenizer = new StringTokenizer(line);
                        tokenizer.nextToken(); // disregard the id token
                        String atom1Str = tokenizer.nextToken();
                        String atom2Str = tokenizer.nextToken();
                        String orderStr = tokenizer.nextToken();
                        try {
                            int atom1 = Integer.parseInt(atom1Str);
                            int atom2 = Integer.parseInt(atom2Str);
                            if ("nc".equals(orderStr)) {
                            	// do not connect the atoms
                            } else {
                        		IBond bond = molecule.getBuilder().newBond(
                        			molecule.getAtom(atom1-1),
                        			molecule.getAtom(atom2-1)
                        		);
                            	if ("1".equals(orderStr)) {
                            		bond.setOrder(CDKConstants.BONDORDER_SINGLE);
                            	} else if ("2".equals(orderStr)) {
                            		bond.setOrder(CDKConstants.BONDORDER_DOUBLE);
                            	} else if ("3".equals(orderStr)) {
                            		bond.setOrder(CDKConstants.BONDORDER_TRIPLE);
                            	} else if ("am".equals(orderStr) || "ar".equals(orderStr)) {
                            		bond.setOrder(CDKConstants.BONDORDER_SINGLE);
                            		bond.setFlag(CDKConstants.ISAROMATIC, true);
                            		bond.getAtom(0).setFlag(CDKConstants.ISAROMATIC, true);
                            		bond.getAtom(1).setFlag(CDKConstants.ISAROMATIC, true);
                            	} else if ("du".equals(orderStr)) {
                            		bond.setOrder(CDKConstants.BONDORDER_SINGLE);
                            	} else if ("un".equals(orderStr)) {
                            		bond.setOrder(CDKConstants.BONDORDER_SINGLE);
                            	}
                            	molecule.addBond(bond);
                            }
                        } catch (NumberFormatException nfExc) {
                            String error = "Error while reading bond information";
                            logger.error(error);
                            logger.debug(nfExc);
                            throw new CDKException(error, nfExc);
                        }
                    }
                }
                line = input.readLine();
            }
        } catch (IOException exception) {
            String error = "Error while reading general structure";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
        return molecule;
    }
    
    private boolean isElementSymbol(String atomTypeStr) {
    	for (int i=1; i<Symbols.KNOWN_ELEMENTS; i++) {
    		if (Symbols.byAtomicNumber[i].equals(atomTypeStr)) return true;
    	}
		return false;
	}

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }
}

