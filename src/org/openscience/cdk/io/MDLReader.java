/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *@author     steinbeck
 *@created    October 2, 2000
 *@keyword    file format, MDL molfile
 *@keyword    file format, SDF
 */
public class MDLReader implements ChemObjectReader
{

	BufferedReader input;

	private org.openscience.cdk.tools.LoggingTool logger;

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
                    if (str.equals("$$$$")) {
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
        } catch (Exception exc) {
            String error = "Error while parsing SDF: " + exc.toString();
            logger.error(error);
            exc.printStackTrace();
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
		chemSequence.addChemModel(chemModel);
		chemFile.addChemSequence(chemSequence);

		return chemFile;
	}



	/**
	 *  Read a Molecule from a file in MDL sd format
	 *
	 *@return    The Molecule that was read from the MDL file.
	 */
	private Molecule readMolecule() throws CDKException {
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
			String title = new String(input.readLine() + "\n" + input.readLine() + "\n" + input.readLine());
			molecule.setProperty(CDKConstants.TITLE, title);
			StringBuffer strBuff = new StringBuffer(input.readLine());
			strBuff.insert(3, " ");
			StringTokenizer strTok = new StringTokenizer(strBuff.toString());
			atoms = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
			logger.debug("Atomcount: " + atoms);
			bonds = java.lang.Integer.valueOf(strTok.nextToken()).intValue();
			logger.debug("Bondcount: " + bonds);
            
            // read ATOM block
            logger.info("Reading atom block");
			for (int f = 0; f < atoms; f++) {
                line = input.readLine();
				strBuff = new StringBuffer(line);
				strTok = new StringTokenizer(strBuff.toString().trim());
				x = new Double(strTok.nextToken()).doubleValue();
				y = new Double(strTok.nextToken()).doubleValue();
				z = new Double(strTok.nextToken()).doubleValue();
				logger.debug("Coordinates: " + x + "; " + y + "; " + z);
                String element = strTok.nextToken();
                logger.debug("Atom type: " + element);
				atom = new Atom(element, new Point3d(x, y, z));
				atom.setPoint2D(new Point2d(x, y));
				// elemfact.configure(atom);

                // parse further fields
                String dummy = strTok.nextToken();
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
                line = input.readLine();
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
				}
				// interpret CTfile's special bond orders
				Atom a1 = molecule.getAtomAt(atom1 - 1);
				Atom a2 = molecule.getAtomAt(atom2 - 1);
				if (order == 4)
				{
					// aromatic bond
					bond = new Bond(a1, a2, CDKConstants.BONDORDER_AROMATIC, stereo);
					// mark both atoms and the bond as aromatic
					bond.flags[CDKConstants.ISAROMATIC] = true;
					a1.flags[CDKConstants.ISAROMATIC] = true;
					a2.flags[CDKConstants.ISAROMATIC] = true;
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
                line = input.readLine();
                if ("M  END".equals(line)) break;
                
                boolean lineRead = false;
                if (line.startsWith("M  CHG")) {
                    // FIXME: if this is encountered for the first time, all
                    // atom charges should be set to zero first!
                    int infoCount = Integer.parseInt(line.substring(6,9).trim());
                    StringTokenizer st = new StringTokenizer(line.substring(9));
                    for (int i=1; i <= infoCount; i++) {
                        String token = st.nextToken();
                        System.out.println("T1:" + token);
                        int atomNumber = Integer.parseInt(token.trim());
                        token = st.nextToken();
                        System.out.println("T2:" + token);
                        int charge = Integer.parseInt(token.trim());
                        molecule.getAtomAt(atomNumber - 1).setFormalCharge(charge);
                    }
                } else if (line.startsWith("M  ISO")) {
                    int infoCount = Integer.parseInt(line.substring(6,8));
                    for (int i=1; i <= infoCount; i++) {
                        StringTokenizer st = new StringTokenizer(line.substring(9));
                        int atomNumber = Integer.parseInt(st.nextToken());
                        int mass = Integer.parseInt(st.nextToken());
                        molecule.getAtomAt(atomNumber - 1).setAtomicMass(mass);
                    }
                }
                if (!lineRead) {
                    logger.warn("Skipping line in property block: " + line);
                }
            }
		} catch (Exception e) {
            String error = "Error (" + e.toString() + ") while parsing line: " + line;
			logger.error(error);
            throw new CDKException(error);
		}
		return molecule;
	}
}

