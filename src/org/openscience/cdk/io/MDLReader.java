/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.AtomContainerManipulator;
import org.openscience.cdk.tools.IsotopeFactory;

/**
 * Reads a molecule from an MDL MOL or SDF file [DAL92].
 *
 * <p>From the Atom block it reads atomic coordinates, element types and
 * formal charges. From the Bond block it reads the bonds and the orders.
 * Additionally, it reads 'M  CHG', 'G  ' and 'M  ISO' lines from the 
 * property block.
 *
 * <p>If all z coordinates are 0.0, then the xy coordinates are taken as
 * 2D, otherwise the coordinates are read as 3D.
 *
 * <p>References: <a href="http://cdk.sf.net/biblio.html#DAL92">DAL92</a>.
 *
 * @cdkPackage io
 *
 * @author     steinbeck
 * @author     Egon Willighagen
 * @created    2000-10-02
 * @cdk.keyword    file format, MDL molfile
 * @cdk.keyword    file format, SDF
 */
public class MDLReader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private org.openscience.cdk.tools.LoggingTool logger = null;
    private IsotopeFactory isotopeFactory = null;

    private BooleanIOSetting forceReadAs3DCoords;
    
    public MDLReader() {
        this(new StringReader(""));
    }
    
	/**
	 *  Contructs a new MDLReader that can read Molecule from a given InputStream.
	 *
	 *@param  in  The InputStream to read from
	 */
	public MDLReader(InputStream in) {
		this(new InputStreamReader(in));
	}

    public String getFormatName() {
        return "MDL Mol/SDF";
    }

	/**
	 *  Contructs a new MDLReader that can read Molecule from a given Reader.
	 *
	 *@param  in  The Reader to read from
	 */
	public MDLReader(Reader in) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        input = new BufferedReader(in);
        initIOSettings();
        try {
            isotopeFactory = IsotopeFactory.getInstance();
        } catch (Exception exception) {
            logger.error("Failed to initiate isotope factory: ", exception.getMessage());
            logger.debug(exception);
        }
	}


	/**
	 *  Takes an object which subclasses ChemObject, e.g. Molecule, and will read
	 *  this (from file, database, internet etc). If the specific implementation
	 *  does not support a specific ChemObject it will throw an Exception.
	 *
	 *@param  object                              The object that subclasses
	 *      ChemObject
	 *@return                                     The ChemObject read
	 *@exception  CDKException
	 */
	public ChemObject read(ChemObject object) throws CDKException {
		if (object instanceof ChemFile) {
			return (ChemObject) readChemFile();
        } else if (object instanceof ChemModel) {
            return (ChemObject) readChemModel();
		} else if (object instanceof Molecule) {
			return (ChemObject) readMolecule();
		} else {
			throw new CDKException("Only supported are ChemFile and Molecule.");
		}
	}


	/**
	 * Read a ChemFile from a file in MDL SDF format.
	 *
	 * @return    The ChemFile that was read from the MDL file.
	 */
    private ChemFile readChemFile() throws CDKException {
        ChemFile chemFile = new ChemFile();
        ChemSequence chemSequence = new ChemSequence();
        ChemModel chemModel = readChemModel();
        logger.debug("Adding ChemModel to ChemSequence");
        logger.debug("#models (array): " + chemSequence.getChemModels().length);
        logger.debug("#models (count): " + chemSequence.getChemModelCount());
        chemSequence.addChemModel(chemModel);
        logger.debug("#models (array): " + chemSequence.getChemModels().length);
        logger.debug("#models (count): " + chemSequence.getChemModelCount());
        logger.debug("Adding ChemSequence to ChemFile");
        logger.debug("#sequences (array): " + chemFile.getChemSequences().length);
        logger.debug("#sequences (count): " + chemFile.getChemSequenceCount());
        chemFile.addChemSequence(chemSequence);
        logger.debug("#sequences (array): " + chemFile.getChemSequences().length);
        logger.debug("#sequences (count): " + chemFile.getChemSequenceCount());
        return chemFile;
    }
    
    private ChemModel readChemModel() throws CDKException {
		ChemModel chemModel = new ChemModel();
		SetOfMolecules setOfMolecules = new SetOfMolecules();
		Molecule m = readMolecule();
		if (m != null) {
			setOfMolecules.addMolecule(m);
		}
		String str;
        try {
            String line = input.readLine();
            logger.debug("line: " + line);
            if (input.ready() && line != null) {
                // apparently, this is a SDF file, continue with 
                // reading mol files
                do {
                    str = new String(line);
                    if (str.equals("$$$$") && input.ready() && line != null) {
                        m = readMolecule();

                        if (m != null) {
                            setOfMolecules.addMolecule(m);
                        }
                    } else {
                        // skip stuff between "M  END" and "$$$$" 
                    }
                    line = input.readLine();
                } while (input.ready() && line != null);
            }
        } catch (CDKException cdkexc) {
            throw cdkexc;
        } catch (Exception exception) {
            String error = "Error while parsing SDF";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error);
        }
		try {
			input.close();
		} catch (Exception exc) {
            String error = "Error while closing file: " + exc.toString();
            logger.error(error);
			throw new CDKException(error);
		}
		chemModel.setSetOfMolecules(setOfMolecules);
		return chemModel;
	}



	/**
	 *  Read a Molecule from a file in MDL sd format
	 *
	 *@return    The Molecule that was read from the MDL file.
	 */
	private Molecule readMolecule() throws CDKException {
        logger.debug("Reading new molecule");
        int linecount = 0;
        int atoms = 0;
        int bonds = 0;
        int atom1 = 0;
        int atom2 = 0;
        int order = 0;
        int stereo = 0;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        double totalZ = 0.0;
        int[][] conMat = new int[0][0];
        String help;
        Molecule molecule = new Molecule();
        Bond bond;
        Atom atom;
        String line = "";
        
        try {
            logger.info("Reading header");
            line = input.readLine(); linecount++;
            if (line == null) {
                return molecule;
            }
            logger.debug("Line " + linecount + ": " + line);

            if (line.startsWith("$$$$")) {
                logger.debug("File is empty, returning empty molecule");
                return molecule;
            }
            if (line.length() > 0) {
                molecule.setProperty(CDKConstants.TITLE, line);
            }
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);
            if (line.length() > 0) {
                molecule.setProperty(CDKConstants.REMARK, line);
            }
            
            logger.info("Reading rest of file");
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);
            atoms = Integer.valueOf(line.substring(0,3).trim()).intValue();
            logger.debug("Atomcount: " + atoms);
            bonds = Integer.valueOf(line.substring(3,6).trim()).intValue();
            logger.debug("Bondcount: " + bonds);
            
            // read ATOM block
            logger.info("Reading atom block");
            for (int f = 0; f < atoms; f++) {
                line = input.readLine(); linecount++;
                x = new Double(line.substring( 0,10).trim()).doubleValue();
                y = new Double(line.substring(10,20).trim()).doubleValue();
                z = new Double(line.substring(20,30).trim()).doubleValue();
                totalZ += z;
                logger.debug("Coordinates: " + x + "; " + y + "; " + z);
                String element = line.substring(31,34).trim();
                logger.debug("Atom type: " + element);

                // try to determine Isotope
                try {
                    atom = isotopeFactory.configure(new Atom(element));
                } catch (NullPointerException exception) {
                    logger.debug("Atom " + element + " is not an regular element. Creating a PseudoAtom.");
                    atom = new PseudoAtom(element);
                }
                // store as 3D for now, convert to 2D (if totalZ == 0.0) later
                atom.setPoint3D(new Point3d(x, y, z));
                
                // parse further fields
                String massDiffString = line.substring(34,36).trim();
                logger.debug("Mass difference: " + massDiffString);
                if (!(atom instanceof PseudoAtom)) {
                    try {
                        int massDiff = Integer.parseInt(massDiffString);
                        if (massDiff != 0) {
                            Isotope major = isotopeFactory.getMajorIsotope(element);
                            atom.setAtomicNumber(major.getAtomicNumber() + massDiff);
                        }
                    } catch (Exception exception) {
                        logger.error("Could not parse mass difference field");
                    }
                } else {
                    logger.error("Cannot set mass difference for a non-element!");
                }
                
                
                String chargeCodeString = line.substring(36,39).trim();
                logger.debug("Atom charge code: " + chargeCodeString);
                int chargeCode = Integer.parseInt(chargeCodeString);
                if (chargeCode == 0) {
                    // uncharged species
                } else if (chargeCode == 1) {
                    atom.setFormalCharge(+3);
                } else if (chargeCode == 2) {
                        atom.setFormalCharge(+2);
                } else if (chargeCode == 3) {
                        atom.setFormalCharge(+1);
                } else if (chargeCode == 4) {
                } else if (chargeCode == 5) {
                        atom.setFormalCharge(-1);
                } else if (chargeCode == 6) {
                        atom.setFormalCharge(-2);
                } else if (chargeCode == 7) {
                        atom.setFormalCharge(-3);
                }
                
                try {
                    String reactionAtomIDString = line.substring(50,53).trim();
                    logger.debug("Parsing mapping id: " + reactionAtomIDString);
                    try {
                        int reactionAtomID = Integer.parseInt(reactionAtomIDString);
                        if (reactionAtomID != 0) {
                            atom.setID(reactionAtomIDString);
                        }
                    } catch (Exception exception) {
                        logger.error("Mapping number " + reactionAtomIDString + " is not an integer.");
                        logger.debug(exception);
                    }
                } catch (Exception exception) {
                    // older mol files don't have all these fields...
                    logger.warn("A few fields are missing. Older MDL MOL file?");
                }
                
                molecule.addAtom(atom);
            }
            
            // convert to 2D, if totalZ == 0
            if (totalZ == 0.0 && !forceReadAs3DCoords.isSet()) {
                logger.info("Total 3D Z is 0.0, interpreting it as a 2D structure");
                Atom[] atomsToUpdate = molecule.getAtoms();
                for (int f = 0; f<atomsToUpdate.length; f++) {
                    Atom atomToUpdate = atomsToUpdate[f];
                    Point3d p3d = atomToUpdate.getPoint3D();
                    atomToUpdate.setPoint2D(new Point2d(p3d.x, p3d.y));
                    atomToUpdate.setPoint3D(null);
                }
            }
            
            // read BOND block
            logger.info("Reading bond block");
            for (int f = 0; f < bonds; f++) {
                line = input.readLine(); linecount++;
                atom1 = java.lang.Integer.valueOf(line.substring(0,3).trim()).intValue();
                atom2 = java.lang.Integer.valueOf(line.substring(3,6).trim()).intValue();
                order = java.lang.Integer.valueOf(line.substring(6,9).trim()).intValue();
                stereo = java.lang.Integer.valueOf(line.substring(9,12).trim()).intValue();
                logger.debug("Bond: " + atom1 + " - " + atom2 + "; order " + order);
                if (stereo == 1) {
                    // MDL up bond
                    stereo = CDKConstants.STEREO_BOND_UP;
                } else if (stereo == 6) {
                    // MDL down bond
                    stereo = CDKConstants.STEREO_BOND_DOWN;
                } else if (stereo == 4) {
                    //MDL bond undefined
                    stereo = CDKConstants.STEREO_BOND_UNDEFINED;
                }
                // interpret CTfile's special bond orders
                Atom a1 = molecule.getAtomAt(atom1 - 1);
                Atom a2 = molecule.getAtomAt(atom2 - 1);
                if (order == 4) {
                    // aromatic bond
                    bond = new Bond(a1, a2, CDKConstants.BONDORDER_AROMATIC, stereo);
                    // mark both atoms and the bond as aromatic
                    bond.setFlag(CDKConstants.ISAROMATIC, true);
                    a1.setFlag(CDKConstants.ISAROMATIC, true);
                    a2.setFlag(CDKConstants.ISAROMATIC, true);
                    molecule.addBond(bond);
                } else {
                    bond = new Bond(a1, a2, (double) order, stereo);
                    molecule.addBond(bond);
                }
            }
            
            // read PROPERTY block
            logger.info("Reading property block");
            while (input.ready() && line != null) {
                line = input.readLine(); linecount++;
                if ("M  END".equals(line)) break;
                
                boolean lineRead = false;
                if (line.startsWith("M  CHG")) {
                    // FIXME: if this is encountered for the first time, all
                    // atom charges should be set to zero first!
                    int infoCount = Integer.parseInt(line.substring(6,9).trim());
                    StringTokenizer st = new StringTokenizer(line.substring(9));
                    for (int i=1; i <= infoCount; i++) {
                        String token = st.nextToken();
                        int atomNumber = Integer.parseInt(token.trim());
                        token = st.nextToken();
                        int charge = Integer.parseInt(token.trim());
                        molecule.getAtomAt(atomNumber - 1).setFormalCharge(charge);
                    }
                } else if (line.startsWith("M  ISO")) {
                    try {
                        String countString = line.substring(6,9).trim();
                        int infoCount = Integer.parseInt(countString);
                        StringTokenizer st = new StringTokenizer(line.substring(9));
                        for (int i=1; i <= infoCount; i++) {
                            int atomNumber = Integer.parseInt(st.nextToken().trim());
                            int absMass = Integer.parseInt(st.nextToken().trim());
                            if (absMass != 0) { 
                                Atom isotope = molecule.getAtomAt(atomNumber - 1);
                                isotope.setMassNumber(absMass);
                            }
                        }
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.toString() + ") while parsing line "
                        + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        throw new CDKException("NumberFormatException in isotope information on line: " + line);
                    }
                } else if (line.startsWith("G  ")) {
                    try {
                        String atomNumberString = line.substring(3,6).trim();
                        int atomNumber = Integer.parseInt(atomNumberString);
                        String whatIsThisString = line.substring(6,9).trim();
                    
                        String atomName = input.readLine();
                        
                        // convert Atom into a PseudoAtom
                        Atom prevAtom = molecule.getAtomAt(atomNumber - 1);
                        PseudoAtom pseudoAtom = new PseudoAtom(atomName);
                        if (prevAtom.getPoint2D() != null) {
                            pseudoAtom.setPoint2D(prevAtom.getPoint2D());
                        }
                        if (prevAtom.getPoint3D() != null) {
                            pseudoAtom.setPoint3D(prevAtom.getPoint3D());
                        }
                        AtomContainerManipulator.replaceAtomByAtom(molecule, prevAtom, pseudoAtom);
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.toString() + ") while parsing line "
                        + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        throw new CDKException("NumberFormatException in group information on line: " + line);
                    }
                }
                if (!lineRead) {
                    logger.warn("Skipping line in property block: " + line);
                }
            }
		} catch (CDKException exception) {
            throw exception;
		} catch (Exception exception) {
            String error = "Error while parsing line " + linecount + ": " + line + " in property block.";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error);
		}
		return molecule;
	}
    
    public void close() throws IOException {
        input.close();
    }
    
    private void initIOSettings() {
        forceReadAs3DCoords = new BooleanIOSetting("ForceReadAs3DCoordinates", IOSetting.LOW,
          "Should coordinates always be read as 3D?", 
          "no");
    }
    
    private void customizeJob() {
        fireIOSettingQuestion(forceReadAs3DCoords);
    }

    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[1];
        settings[0] = forceReadAs3DCoords;
        return settings;
    }
}

