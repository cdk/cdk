/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.exception.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.*;
import java.io.*;
import java.util.*;
import javax.vecmath.*;

/**
 *  Reads a molecule from an MDL molfile or SDF file. References: <a
 *  href="http://cdk.sf.net/biblio.html#DAL92">DAL92</a>.
 *
 * <p>From the Atom block it reads atomic coordinates, element types and
 * formal charges. From the Bond block it reads the bonds and the orders.
 * 
 * <p>Additionally, it reads M  CHG and M  ISO lines from the property
 * block.
 *
 * @author     steinbeck
 * @author     Egon Willighagen
 * @created    2000-10-02
 * @keyword    file format, MDL molfile
 * @keyword    file format, SDF
 */
public class MDLReader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private org.openscience.cdk.tools.LoggingTool logger = null;
    private IsotopeFactory isotopeFactory = null;

	/**
	 *  Contructs a new MDLReader that can read Molecule from a given InputStream
	 *
	 *@param  in  The InputStream to read from
	 */
	public MDLReader(InputStream in) {
		this(new InputStreamReader(in));
	}


	/**
	 *  Contructs a new MDLReader that can read Molecule from a given InputStream
	 *
	 *@param  in  The Reader to read from
	 */
	public MDLReader(Reader in) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        input = new BufferedReader(in);
        try {
            isotopeFactory = IsotopeFactory.getInstance();
        } catch (Exception exception) {
            logger.error("Failed to initiate isotope factory: " + exception.toString());
        }
	}


	/**
	 *  Takes an object which subclasses ChemObject, e.g.Molecule, and will read
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
		} else if (object instanceof Molecule) {
			return (ChemObject) readMolecule();
		} else {
			throw new CDKException("Only supported are ChemFile and Molecule.");
		}
	}


	/**
	 *  Read a ChemFile from a file in MDL sd format
	 *
	 *@return    The ChemFile that was read from the MDL file.
	 */
	private ChemFile readChemFile() throws CDKException {
		ChemFile chemFile = new ChemFile();
		ChemSequence chemSequence = new ChemSequence();
		ChemModel chemModel = new ChemModel();
		SetOfMolecules setOfMolecules = new SetOfMolecules();
		Molecule m = readMolecule();
		if (m != null) {
			setOfMolecules.addMolecule(m);
		}
		String str;
        try {
            if (input.ready()) {
                // apparently, this is a SDF file, continue with 
                // reading mol files
                do {
                    str = new String(input.readLine());
                    if (str.equals("$$$$") && input.ready()) {
                        m = readMolecule();

                        if (m != null) {
                            setOfMolecules.addMolecule(m);
                        }
                    } else {
                        // skip stuff between "M  END" and "$$$$" 
                    }
                } while (input.ready());
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
		double x = 0;
		double y = 0;
		double z = 0;
		int[][] conMat = new int[0][0];
		String help;
		Molecule molecule = new Molecule();
		Bond bond;
		Atom atom;
        String line = "";

		try {
            logger.info("Reading header");
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);
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
			StringBuffer strBuff = new StringBuffer(line);
			strBuff.insert(3, " ");
			StringTokenizer strTok = new StringTokenizer(strBuff.toString());
			atoms = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
			logger.debug("Atomcount: " + atoms);
			bonds = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
			logger.debug("Bondcount: " + bonds);
            
            // read ATOM block
            logger.info("Reading atom block");
			for (int f = 0; f < atoms; f++) {
                line = input.readLine(); linecount++;
                // FIXME: MDL molfile does not use whitespace, but column based
                // field! This StringBuffer should not be used!
				strBuff = new StringBuffer(line);
				strTok = new StringTokenizer(strBuff.toString().trim());
				x = new Double(strTok.nextToken()).doubleValue();
				y = new Double(strTok.nextToken()).doubleValue();
				z = new Double(strTok.nextToken()).doubleValue();
				logger.debug("Coordinates: " + x + "; " + y + "; " + z);
                String element = strTok.nextToken();
                logger.debug("Atom type: " + element);

                // try to determine Isotope
                try {
                    atom = isotopeFactory.configure(new Atom(element, new Point3d(x, y, z)));
                } catch (NullPointerException exception) {
                    logger.debug("Atom " + element + " is not an regular element. Creating a PseudoAtom.");
                    atom = new PseudoAtom(element, new Point3d(x, y, z));
                }
				atom.setPoint2D(new Point2d(x, y));
                
                // parse further fields
                String massDiffString = strTok.nextToken();
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
                
                
                String chargeCodeString = strTok.nextToken();
                logger.debug("Atom charge code: " + chargeCodeString);
                int chargeCode = Integer.parseInt(chargeCodeString);
                switch (chargeCode) {
                    case 0: 
                        // uncharged specied
                        break;
                    case 1:
                        // +3 charge
                        atom.setFormalCharge(+3);
                        break;
                    case 2:
                        // +2 charge
                        atom.setFormalCharge(+2);
                        break;
                    case 3:
                        // +1 charge
                        atom.setFormalCharge(+1);
                        break;
                    case 4:
                        // double radical
                        break;
                    case 5:
                        // -1 charge
                        atom.setFormalCharge(-1);
                        break;
                    case 6:
                        // -2 charge
                        atom.setFormalCharge(-2);
                        break;
                    case 7:
                        // -3 charge
                        atom.setFormalCharge(-3);
                        break;
                }
				molecule.addAtom(atom);
			}
            
            // read BOND block
            logger.info("Reading bond block");
			for (int f = 0; f < bonds; f++) {
                line = input.readLine(); linecount++;
				strBuff = new StringBuffer(line);
				strBuff.insert(3, " ");
				strBuff.insert(7, " ");
				strBuff.insert(11, " ");
				strBuff.insert(15, " ");
				strTok = new StringTokenizer(strBuff.toString());
				atom1 = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
				atom2 = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
				order = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
				stereo = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
				logger.debug("Bond: " + atom1 + " - " + atom2 + "; order " + order);
				if (stereo == 1)
				{
					// MDL up bond
					stereo = CDKConstants.STEREO_BOND_UP;
				} else if (stereo == 6)
				{
					// MDL down bond
					stereo = CDKConstants.STEREO_BOND_DOWN;
				} else if (stereo == 4)
        {
          //MDL bond undefined
          stereo = CDKConstants.STEREO_BOND_UNDEFINED;
        }
				// interpret CTfile's special bond orders
				Atom a1 = molecule.getAtomAt(atom1 - 1);
				Atom a2 = molecule.getAtomAt(atom2 - 1);
				if (order == 4)
				{
					// aromatic bond
					bond = new Bond(a1, a2, CDKConstants.BONDORDER_AROMATIC, stereo);
					// mark both atoms and the bond as aromatic
					bond.setFlag(CDKConstants.ISAROMATIC, true);
					a1.setFlag(CDKConstants.ISAROMATIC, true);
					a2.setFlag(CDKConstants.ISAROMATIC, true);
					molecule.addBond(bond);
				} else
				{
					bond = new Bond(a1, a2, (double) order, stereo);
					molecule.addBond(bond);
				}
			}
            
            // read PROPERTY block
            logger.info("Reading property block");
            while (input.ready()) {
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
                                isotope.setAtomicMass(absMass);
                            }
                        }
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.toString() + ") while parsing line "
                        + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        throw new CDKException("NumberFormatException in isotope information on line: " + line);
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
}

