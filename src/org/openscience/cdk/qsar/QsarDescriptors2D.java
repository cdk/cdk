/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.qsar.QsarDescriptors;
import java.lang.Math;
import java.lang.Exception;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.smiles.smarts.*;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import java.util.Vector;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 *  Description of the Class
 *
 *@author     mfe4
 *@created    November 13, 2004
 */
public class QsarDescriptors2D {

	/**
	 *  Constructor for the QsarDescriptors2D object
	 */
	public QsarDescriptors2D() { }


	/**
	 *  Gets the mW attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The mW value
	 */

	public double getMW(AtomContainer ac) {
		double mw = 0;
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			mw += atoms[i].getExactMass();
			mw += (atoms[i].getHydrogenCount() * 1.00782504);
		}
		return mw;
	}


	/**
	 *  Gets the carbonCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The carbonCount value
	 */
	public int getCarbonCount(AtomContainer ac) {
		QsarDescriptors carbonCount = new QsarDescriptors();
		return carbonCount.getAtomCount(ac, "C");
	}


	/**
	 *  Gets the nitrogenCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The nitrogenCount value
	 */
	public int getNitrogenCount(AtomContainer ac) {
		QsarDescriptors nitrogenCount = new QsarDescriptors();
		return nitrogenCount.getAtomCount(ac, "N");
	}


	/**
	 *  Gets the oxygenCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The oxygenCount value
	 */
	public int getOxygenCount(AtomContainer ac) {
		QsarDescriptors oxygenCount = new QsarDescriptors();
		return oxygenCount.getAtomCount(ac, "O");
	}


	/**
	 *  Gets the phosphorousCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The phosphorousCount value
	 */
	public int getPhosphorousCount(AtomContainer ac) {
		QsarDescriptors phosphorousCount = new QsarDescriptors();
		return phosphorousCount.getAtomCount(ac, "P");
	}


	/**
	 *  Gets the sulphurCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The sulphurCount value
	 */
	public int getSulphurCount(AtomContainer ac) {
		QsarDescriptors sulphurCount = new QsarDescriptors();
		return sulphurCount.getAtomCount(ac, "S");
	}


	/**
	 *  Gets the fluorineCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The fluorineCount value
	 */
	public int getFluorineCount(AtomContainer ac) {
		QsarDescriptors fluorineCount = new QsarDescriptors();
		return fluorineCount.getAtomCount(ac, "F");
	}


	/**
	 *  Gets the boronCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The boronCount value
	 */
	public int getBoronCount(AtomContainer ac) {
		QsarDescriptors boronCount = new QsarDescriptors();
		return boronCount.getAtomCount(ac, "B");
	}


	/**
	 *  Gets the iodineCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The iodineCount value
	 */
	public int getIodineCount(AtomContainer ac) {
		QsarDescriptors iodineCount = new QsarDescriptors();
		return iodineCount.getAtomCount(ac, "I");
	}


	/**
	 *  Gets the bromineCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The bromineCount value
	 */
	public int getBromineCount(AtomContainer ac) {
		QsarDescriptors bromineCount = new QsarDescriptors();
		return bromineCount.getAtomCount(ac, "Br");
	}


	/**
	 *  Gets the clorineCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The clorineCount value
	 */
	public int getClorineCount(AtomContainer ac) {
		QsarDescriptors clorineCount = new QsarDescriptors();
		return clorineCount.getAtomCount(ac, "Cl");
	}


	/**
	 *  Gets the hCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The hCount value
	 */
	public int getHCount(AtomContainer ac) {
		int hCount = 0;
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (ac.getAtomAt(i).getSymbol().equals("H")) {
				hCount += 1;
			} else {
				hCount += ac.getAtomAt(i).getHydrogenCount();
			}
		}
		return hCount;
	}


	/**
	 *  Gets the allAtomsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The allAtomsCount value
	 */
	public int getAllAtomsCount(AtomContainer ac) {
		int allAtomsCount = 0;
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			allAtomsCount += ac.getAtomAt(i).getHydrogenCount();
			allAtomsCount += 1;
		}
		return allAtomsCount;
	}


	/**
	 *  Gets the heavyCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The heavyCount value
	 */
	public int getHeavyAtomsCount(AtomContainer ac) {
		int heavyAtomsCount = 0;
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (ac.getAtomAt(i).getExactMass() > 1.00782504) {
				heavyAtomsCount += 1;
			}
		}
		return heavyAtomsCount;
	}


	/**
	 *  Gets the heavyBondsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The heavyBondsCount value
	 */

	public int getHeavyBondsCount(AtomContainer ac) {
		int heavyBondsCount = 0;
		Bond[] bonds = ac.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			Atom[] atoms = ac.getBondAt(i).getAtoms();
			if ((atoms[0].getExactMass() > 1.00782504) && (atoms[1].getExactMass() > 1.00782504)) {
				heavyBondsCount += 1;
			}
		}
		return heavyBondsCount;
	}



	// FCharge is the sum of all formal charges
	/**
	 *  Gets the fCharge attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The fCharge value
	 */
	public double getFCharge(AtomContainer ac) {
		double fcharge = 0;
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			fcharge += ac.getAtomAt(i).getFormalCharge();
		}
		return fcharge;
	}


	/**
	 *  Gets the singleBondsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The singleBondsCount value
	 */
	public double getSingleBondsCount(AtomContainer ac) {
		QsarDescriptors singleBondsCount = new QsarDescriptors();
		return singleBondsCount.getBondCount(ac, CDKConstants.BONDORDER_SINGLE);
	}


	/**
	 *  Gets the doubleBondsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The doubleBondsCount value
	 */
	public double getDoubleBondsCount(AtomContainer ac) {
		QsarDescriptors doubleBondsCount = new QsarDescriptors();
		return doubleBondsCount.getBondCount(ac, CDKConstants.BONDORDER_DOUBLE);
	}


	/**
	 *  Gets the tripleBondsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The tripleBondsCount value
	 */
	public double getTripleBondsCount(AtomContainer ac) {
		QsarDescriptors tripleBondsCount = new QsarDescriptors();
		return tripleBondsCount.getBondCount(ac, CDKConstants.BONDORDER_TRIPLE);
	}


	/**
	 *  Gets the aromaticAtomsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac                                                     Description of
	 *      the Parameter
	 *@param  rs                                                     Description of
	 *      the Parameter
	 *@return                                                        The
	 *      aromaticAtomsCount value
	 *@exception  org.openscience.cdk.exception.NoSuchAtomException  Description of
	 *      the Exception
	 */
	public int getAromaticAtomsCount(AtomContainer ac, RingSet rs) throws org.openscience.cdk.exception.NoSuchAtomException {
		int aroCount = 0;
		HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (ac.getAtomAt(i).getFlag(CDKConstants.ISAROMATIC)) {
				aroCount += 1;
			}
		}
		return aroCount;
	}


	/**
	 *  Gets the aromaticAtomsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac                                                     Description of
	 *      the Parameter
	 *@return                                                        The
	 *      aromaticAtomsCount value
	 *@exception  org.openscience.cdk.exception.NoSuchAtomException  Description of
	 *      the Exception
	 */
	public int getAromaticAtomsCount(AtomContainer ac) throws org.openscience.cdk.exception.NoSuchAtomException {
		int aroCount = 0;

		RingSet rs = (new AllRingsFinder()).findAllRings(ac);
		HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (ac.getAtomAt(i).getFlag(CDKConstants.ISAROMATIC)) {
				aroCount += 1;
			}
		}
		return aroCount;
	}


	/**
	 *  Gets the aromaticBondsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac                                                     Description of
	 *      the Parameter
	 *@return                                                        The
	 *      aromaticBondsCount value
	 *@exception  org.openscience.cdk.exception.NoSuchAtomException  Description of
	 *      the Exception
	 */
	public int getAromaticBondsCount(AtomContainer ac) throws org.openscience.cdk.exception.NoSuchAtomException {
		int b_aroCount = 0;
		RingSet rs = (new AllRingsFinder()).findAllRings(ac);
		HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
		Bond[] bonds = ac.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			if (ac.getBondAt(i).getFlag(CDKConstants.ISAROMATIC)) {
				b_aroCount += 1;
			}
		}
		return b_aroCount;
	}


	/**
	 *  Gets the aromaticBondsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac                                                     Description of
	 *      the Parameter
	 *@param  rs                                                     Description of
	 *      the Parameter
	 *@return                                                        The
	 *      aromaticBondsCount value
	 *@exception  org.openscience.cdk.exception.NoSuchAtomException  Description of
	 *      the Exception
	 */
	public int getAromaticBondsCount(AtomContainer ac, RingSet rs) throws org.openscience.cdk.exception.NoSuchAtomException {
		int b_aroCount = 0;
		HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
		Bond[] bonds = ac.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			if (ac.getBondAt(i).getFlag(CDKConstants.ISAROMATIC)) {
				b_aroCount += 1;
			}
		}
		return b_aroCount;
	}


	//VAdjMa = vertex adijacency information magnitude
	// = 1+ log2 m,
	// where m = bonds between heavy atoms
	//VAdjMa = 0 when m = 0.
	/**
	 *  Gets the vAdjMa attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The vAdjMa value
	 */
	public double getVAdjMa(AtomContainer ac) {
		int magnitude = this.getHeavyBondsCount(ac);
		double vadjMa = 0;
		if (magnitude > 0) {
			vadjMa += (Math.log(magnitude) / Math.log(2)) + 1;
		}
		return vadjMa;
	}


	/**
	 *  Gets the zagreb attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@return     The zagreb value
	 */
	public double getZagreb(AtomContainer ac) {
		double zagreb = 0;
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			int atomDegree = 0;
			Atom[] neighboors = ac.getConnectedAtoms(atoms[i]);
			for (int a = 0; a < neighboors.length; a++) {
				if (!neighboors[a].getSymbol().equals("H")) {
					atomDegree += 1;
				}
			}
			zagreb += (atomDegree * atomDegree);
		}
		return zagreb;
	}


	/**
	 *  Gets the atomDegree attribute of the QsarDescriptors2D object
	 *
	 *@param  ac  Description of the Parameter
	 *@param  at  Description of the Parameter
	 *@return     The atomDegree value
	 */
	public int getAtomDegree(AtomContainer ac, Atom at) {
		int degree = 0;
		Atom[] neighboors = ac.getConnectedAtoms(at);
		for (int a = 0; a < neighboors.length; a++) {
			if (neighboors[a].getExactMass() > 1.1) {
				degree += 1;
			}
		}
		return degree;
	}



	/**
	 *  Gets the rotatableBondsCount attribute of the QsarDescriptors2D object A
	 *  bond is defined as Rotatable by ths Daylight SMARTS:
	 *  [!$(*#*)&!D1]-&!@[!$(*#*)&!D1] this means that a bond is a rotatable bond
	 *  when each atom has not triple bonds and has not degree = 1 (= it is not
	 *  terminal) and the bond is single and not in a ring
	 *
	 *@param  ac                       Description of the Parameter
	 *@param  includeTerminals         Description of the Parameter
	 *@return                          The rotatableBondsCount value
	 *@exception  NoSuchAtomException  Description of the Exception
	 */

	public int getRotatableBondsCount(AtomContainer ac, boolean includeTerminals) throws NoSuchAtomException {
		int rotatableBondsCount = 0;
		RingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		ringSet = arf.findAllRings(ac);
		Bond[] bonds = ac.getBonds();

		for (int f = 0; f < bonds.length; f++) {
			Vector ringsWithThisBond = ringSet.getRings(bonds[f]);
			if (ringsWithThisBond.size() > 0) {
				bonds[f].setFlag(CDKConstants.ISINRING, true);
			}
		}
		for (int i = 0; i < bonds.length; i++) {

			Atom[] atoms = ac.getBondAt(i).getAtoms();
			if (bonds[i].getOrder() == CDKConstants.BONDORDER_SINGLE) {
				if ((ac.getMaximumBondOrder(atoms[0]) < 3.0) && (ac.getMaximumBondOrder(atoms[1]) < 3.0)) {
					if (bonds[i].getFlag(CDKConstants.ISINRING) == false) {
						// IT IS NOT WORKING!!!!
						int neighboor0 = ac.getBondCount(atoms[0]);
						int neighboor1 = ac.getBondCount(atoms[1]);
						int degree0 = neighboor0;
						int degree1 = neighboor1;
						if ((degree0 == 1) || (degree1 == 1)) {
							if (includeTerminals == true) {
								rotatableBondsCount += 1;
							}
							if (includeTerminals == false) {
								rotatableBondsCount += 0;
							}
						} else {
							rotatableBondsCount += 1;
						}
					}
				}
			}
		}
		return rotatableBondsCount;
	}


	/**
	 *  Gets the rotatableBondsCount attribute of the QsarDescriptors2D object
	 *
	 *@param  ac                       Description of the Parameter
	 *@param  ringSet                  Description of the Parameter
	 *@param  includeTerminals         Description of the Parameter
	 *@return                          The rotatableBondsCount value
	 *@exception  NoSuchAtomException  Description of the Exception
	 */
	public int getRotatableBondsCount(AtomContainer ac, RingSet ringSet, boolean includeTerminals) throws NoSuchAtomException {
		int rotatableBondsCount = 0;
		Bond[] bonds = ac.getBonds();

		for (int f = 0; f < bonds.length; f++) {
			Vector ringsWithThisBond = ringSet.getRings(bonds[f]);
			if (ringsWithThisBond.size() > 0) {
				bonds[f].setFlag(CDKConstants.ISINRING, true);
			}
		}
		for (int i = 0; i < bonds.length; i++) {

			Atom[] atoms = ac.getBondAt(i).getAtoms();
			if (bonds[i].getOrder() == CDKConstants.BONDORDER_SINGLE) {
				if ((ac.getMaximumBondOrder(atoms[0]) < 3.0) && (ac.getMaximumBondOrder(atoms[1]) < 3.0)) {
					if (bonds[i].getFlag(CDKConstants.ISINRING) == false) {
						// IT IS NOT WORKING!!!!
						int neighboor0 = ac.getBondCount(atoms[0]);
						int neighboor1 = ac.getBondCount(atoms[1]);
						int degree0 = neighboor0;
						int degree1 = neighboor1;
						if ((degree0 == 1) || (degree1 == 1)) {
							if (includeTerminals == true) {
								rotatableBondsCount += 1;
							}
							if (includeTerminals == false) {
								rotatableBondsCount += 0;
							}
						} else {
							rotatableBondsCount += 1;
						}
					}
				}
			}
		}
		return rotatableBondsCount;
	}

	
	
	public double[] getProtonPartialChargesAt(AtomContainer ac, int atomPosition) throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule mol = new Molecule(ac);
		GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		peoe.assignGasteigerMarsiliPartialCharges(mol, true);
		Atom target = mol.getAtomAt(atomPosition);
		Atom[] neighboors = mol.getConnectedAtoms(target);
		double[] protonPartialCharge = new double[neighboors.length];
		int counter = 0;
		for (int i = 0; i < neighboors.length; i++){
			System.err.println(neighboors[i].getSymbol());
			if(neighboors[i].getSymbol().equals("H")) {
				protonPartialCharge[counter] = neighboors[i].getCharge();
				counter++;
			}
		}
		return protonPartialCharge;
	}
}

