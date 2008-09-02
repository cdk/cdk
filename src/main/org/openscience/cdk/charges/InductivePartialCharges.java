/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.charges;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point3d;
import java.io.IOException;

/**
 * The calculation of the inductive partial atomic charges and equalization of
 * effective electronegativities is based on {@cdk.cite CHE03}.
 *
 * @author      mfe4
 * @cdk.module  charges
 * @cdk.svnrev  $Revision$
 * @cdk.created 2004-11-03
 * @cdk.keyword partial atomic charges
 * @cdk.keyword charge distribution
 * @cdk.keyword electronegativity
 */
@TestClass("org.openscience.cdk.charges.InductivePartialChargesTest")
public class InductivePartialCharges implements IChargeCalculator {

	private static double[] pauling;
	private IsotopeFactory ifac = null;
	private AtomTypeFactory factory = null;
	private LoggingTool logger;


	/**
	 *  Constructor for the InductivePartialCharges object
	 *
	 *@exception  IOException             Description of the Exception
	 *@exception  ClassNotFoundException  Description of the Exception
	 */
	public InductivePartialCharges() throws IOException, ClassNotFoundException {
		if (pauling == null) {
			// pauling ElEn :
			// second position is H, last is Ac
			pauling = new double[]{0, 2.1, 0, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 0,
					0.9, 1.2, 1.5, 1.8, 2.1, 2.5, 3.0, 0, 0.8, 1.0, 1.3, 1.5, 1.6, 1.6, 1.5, 1.8,
					1.8, 1.8, 1.9, 1.6, 1.6, 1.8, 2.0, 2.4, 2.8, 0, 0.8, 1.0, 1.3, 1.4, 1.6, 1.8,
					1.9, 2.2, 2.2, 2.2, 1.9, 1.7, 1.7, 1.8, 1.9, 2.1, 2.5, 0.7, 0.9, 1.1, 1.3, 1.5,
					1.7, 1.9, 2.2, 2.2, 2.2, 2.4, 1.9, 1.8, 1.8, 1.9, 2.0, 2.2, 0, 0.7, 0.9, 1.1};
		}
		logger = new LoggingTool(this);
	}


	/**
	 *  Main method, set charget as atom properties
	 *
	 *@param  ac             AtomContainer
	 *@return                AtomContainer
	 *@exception  Exception  Description of the Exception
	 */
    @TestMethod("testInductivePartialCharges")
    public IAtomContainer assignInductivePartialCharges(IAtomContainer ac) throws Exception {
        if (factory == null) {
            factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt", 
                ac.getBuilder());
        }

		int stepsLimit = 9;
		org.openscience.cdk.interfaces.IAtom[] atoms = AtomContainerManipulator.getAtomArray(ac);
		double[] pChInch = new double[atoms.length * (stepsLimit + 1)];
		double[] ElEn = new double[atoms.length * (stepsLimit + 1)];
		double[] pCh = new double[atoms.length * (stepsLimit + 1)];
		double[] startEE = getPaulingElectronegativities(ac, true);
		for (int e = 0; e < atoms.length; e++) {
			ElEn[e] = startEE[e];
			//logger.debug("INDU: initial EE "+startEE[e]);
		}
		//double tmp1 = 0;
		//double tmp2 = 0;
		for (int s = 1; s < 10; s++) {
			for (int a = 0; a < atoms.length; a++) {
				pChInch[a + (s * atoms.length)] = getAtomicChargeIncrement(ac, a, ElEn, s);
				pCh[a + (s * atoms.length)] = pChInch[a + (s * atoms.length)] + pCh[a + ((s-1) * atoms.length)];
				ElEn[a + (s * atoms.length)] = ElEn[a + ((s - 1) * atoms.length)] + (pChInch[a + (s * atoms.length)] / getAtomicSoftnessCore(ac, a));
				if (s == 9) {
					atoms[a].setProperty("InductivePartialCharge", new Double(pCh[a + (s * atoms.length)]));
					atoms[a].setProperty("EffectiveAtomicElectronegativity", new Double(ElEn[a + (s * atoms.length)]));
				}
				//tmp1 = pCh[a + (s * atoms.length)];
				//tmp2 = ElEn[a + (s * atoms.length)];
				//logger.debug("DONE step " + s + ", atom " + atoms[a].getSymbol() + ", ch " + tmp1 + ", ee " + tmp2);
			}
		}
		return ac;
	}

    @TestMethod("testCalculateCharges_IAtomContainer")
    public void calculateCharges(IAtomContainer container) throws CDKException {
    	try {
	        this.assignInductivePartialCharges(container);
        } catch (Exception exception) {
	        throw new CDKException(
	        	"Could not calculate inductive partial charges: " +
	        	exception.getMessage(), exception
	        );
        }
    }

	/**
	 *  Gets the paulingElectronegativities attribute of the
	 *  InductivePartialCharges object
	 *
	 *@param  ac             AtomContainer
	 *@param  modified       if true, some values are modified by following the reference
	 *@return                The pauling electronegativities
	 *@exception  Exception  Description of the Exception
	 */
    @TestMethod("testGetPaulingElectronegativities")
    public double[] getPaulingElectronegativities(IAtomContainer ac, boolean modified) throws CDKException {
		double[] paulingElectronegativities = new double[ac.getAtomCount()];
		IElement element = null;
		String symbol = null;
		int atomicNumber = 0;
		try {
			ifac = IsotopeFactory.getInstance(ac.getBuilder());
			for (int i = 0; i < ac.getAtomCount(); i++) {
				IAtom atom = ac.getAtom(i);
				symbol = ac.getAtom(i).getSymbol();
				element = ifac.getElement(symbol);
				atomicNumber = element.getAtomicNumber();
				if (modified) {
					if (symbol.equals("Cl")) {
						paulingElectronegativities[i] = 3.28;
					} else if (symbol.equals("Br")) {
						paulingElectronegativities[i] = 3.13;
					} else if (symbol.equals("I")) {
						paulingElectronegativities[i] = 2.93;
					} else if (symbol.equals("H")) {
						paulingElectronegativities[i] = 2.10;
					} else if (symbol.equals("C")) {
						if (ac.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
							// Csp3
							paulingElectronegativities[i] = 2.20;
						} else if (ac.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
							paulingElectronegativities[i] = 2.31;
						} else {
							paulingElectronegativities[i] = 3.15;
						}
					} else if (symbol.equals("O")) {
						if (ac.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
							// Osp3
							paulingElectronegativities[i] = 3.20;
						} else if (ac.getMaximumBondOrder(atom) != IBond.Order.SINGLE) {
							paulingElectronegativities[i] = 4.34;
						}
					} else if (symbol.equals("Si")) {
						paulingElectronegativities[i] = 1.99;
					} else if (symbol.equals("S")) {
						paulingElectronegativities[i] = 2.74;
					} else if (symbol.equals("N")) {
						paulingElectronegativities[i] = 2.59;
					} else {
						paulingElectronegativities[i] = pauling[atomicNumber];
					}
				} else {
					paulingElectronegativities[i] = pauling[atomicNumber];
				}
			}
			return paulingElectronegativities;
		} catch (Exception ex1) {
			logger.debug(ex1);
			throw new CDKException("Problems with IsotopeFactory due to " + ex1.toString(), ex1);
		}
	}


	
	/**
	 *  Gets the atomicSoftnessCore attribute of the InductivePartialCharges object
	 *
	 *@param  ac                AtomContainer
	 *@param  atomPosition      position of target atom
	 *@return                   The atomicSoftnessCore value
	 *@exception  CDKException  Description of the Exception
	 */
	 // this method returns the result of the core of the equation of atomic softness
	 // that can be used for qsar descriptors and during the iterative calculation
	 // of effective electronegativity
    @TestMethod("testGetAtomicSoftness")
    public double getAtomicSoftnessCore(IAtomContainer ac, int atomPosition) throws CDKException {
		org.openscience.cdk.interfaces.IAtom target = null;
		double core = 0;
		double radiusTarget = 0;
		target = ac.getAtom(atomPosition);
		double partial = 0;
		double radius = 0;
		String symbol = null;
		IAtomType type = null;
		try {
			symbol = ac.getAtom(atomPosition).getSymbol();
			type = factory.getAtomType(symbol);
			if (getCovalentRadius(symbol, ac.getMaximumBondOrder(target)) > 0) {
				radiusTarget = getCovalentRadius(symbol, ac.getMaximumBondOrder(target));
			} else {
				radiusTarget = type.getCovalentRadius();
			}

		} catch (Exception ex1) {
			logger.debug(ex1);
			throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString());
		}

		java.util.Iterator atoms = ac.atoms().iterator();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (!target.equals(atom)) {
				symbol = atom.getSymbol();
				partial = 0;
				try {
					type = factory.getAtomType(symbol);
				} catch (Exception ex1) {
					logger.debug(ex1);
					throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString());
				}
				if (getCovalentRadius(symbol, ac.getMaximumBondOrder(atom)) > 0) {
					radius = getCovalentRadius(symbol, ac.getMaximumBondOrder(atom));
				} else {
					radius = type.getCovalentRadius();
				}
				partial += radius * radius;
				partial += (radiusTarget * radiusTarget);
				partial = partial / (calculateSquaredDistanceBetweenTwoAtoms(target, atom));
				core += partial;
			}
		}
		core = 2 * core;
		core = 0.172 * core;
		return core;
	}


	// this method returns the partial charge increment for a given atom
	/**
	 *  Gets the atomicChargeIncrement attribute of the InductivePartialCharges
	 *  object
	 *
	 *@param  ac                AtomContainer
	 *@param  atomPosition      position of target atom
	 *@param  ElEn              electronegativity of target atom
	 *@param  as        step in iteration
	 *@return                   The atomic charge increment fot the target atom
	 *@exception  CDKException  Description of the Exception
	 */
	private double getAtomicChargeIncrement(IAtomContainer ac, int atomPosition, double[] ElEn, int as) throws CDKException {
		org.openscience.cdk.interfaces.IAtom[] allAtoms = null;
		org.openscience.cdk.interfaces.IAtom target = null;
		double incrementedCharge = 0;
		double radiusTarget = 0;
		target = ac.getAtom(atomPosition);
		//logger.debug("ATOM "+target.getSymbol()+" AT POSITION "+atomPosition);
		allAtoms = AtomContainerManipulator.getAtomArray(ac);
		double tmp = 0;
		double radius = 0;
		String symbol = null;
		IAtomType type = null;
		try {
			symbol = target.getSymbol();
			type = factory.getAtomType(symbol);
			if (getCovalentRadius(symbol, ac.getMaximumBondOrder(target)) > 0) {
				radiusTarget = getCovalentRadius(symbol, ac.getMaximumBondOrder(target));
			} else {
				radiusTarget = type.getCovalentRadius();
			}
		} catch (Exception ex1) {
			logger.debug(ex1);
			throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString());
		}

		for (int a = 0; a < allAtoms.length; a++) {
			if (!target.equals(allAtoms[a])) {
				tmp = 0;
				symbol = allAtoms[a].getSymbol();
				try {
					type = factory.getAtomType(symbol);
				} catch (Exception ex1) {
					logger.debug(ex1);
					throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString());
				}
				if (getCovalentRadius(symbol, ac.getMaximumBondOrder(allAtoms[a])) > 0) {
					radius = getCovalentRadius(symbol, ac.getMaximumBondOrder(allAtoms[a]));
				} else {
					radius = type.getCovalentRadius();
				}
				tmp = (ElEn[a + ((as - 1) * allAtoms.length)] - ElEn[atomPosition + ((as - 1) * allAtoms.length)]);
				tmp = tmp * ((radius * radius) + (radiusTarget * radiusTarget));
				tmp = tmp / (calculateSquaredDistanceBetweenTwoAtoms(target, allAtoms[a]));
				incrementedCharge += tmp;
				//if(actualStep==1)
				//logger.debug("INDU: particular atom "+symbol+ ", radii: "+ radius+ " - " + radiusTarget+", dist: "+calculateSquaredDistanceBetweenTwoAtoms(target, allAtoms[a]));
			}
		}
		incrementedCharge = 0.172 * incrementedCharge;
		//logger.debug("Increment: " +incrementedCharge);
		return incrementedCharge;
	}


	/**
	 *  Gets the covalentRadius attribute of the InductivePartialCharges object
	 *
	 *@param  symbol        symbol of the atom
	 *@param  maxBondOrder  its max bond order
	 *@return               The covalentRadius value given by the reference
	 */
	private double getCovalentRadius(String symbol, IBond.Order maxBondOrder) {
		double radiusTarget = 0;
		if (symbol.equals("F")) {
			radiusTarget = 0.64;
		} else if (symbol.equals("Cl")) {
			radiusTarget = 0.99;
		} else if (symbol.equals("Br")) {
			radiusTarget = 1.14;
		} else if (symbol.equals("I")) {
			radiusTarget = 1.33;
		} else if (symbol.equals("H")) {
			radiusTarget = 0.30;
		} else if (symbol.equals("C")) {
			if (maxBondOrder == IBond.Order.SINGLE) {
				// Csp3
				radiusTarget = 0.77;
			} else if (maxBondOrder == IBond.Order.DOUBLE) {
				radiusTarget = 0.67;
			} else {
				radiusTarget = 0.60;
			}
		} else if (symbol.equals("O")) {
			if (maxBondOrder == IBond.Order.SINGLE) {
				// Csp3
				radiusTarget = 0.66;
			} else if (maxBondOrder != IBond.Order.SINGLE) {
				radiusTarget = 0.60;
			}
		} else if (symbol.equals("Si")) {
			radiusTarget = 1.11;
		} else if (symbol.equals("S")) {
			radiusTarget = 1.04;
		} else if (symbol.equals("N")) {
			radiusTarget = 0.70;
		} else {
			radiusTarget = 0;
		}
		return radiusTarget;
	}


	/**
	 *  Evaluate the square of the Euclidean distance between two atoms.
	 *
	 *@param  atom1  first atom
	 *@param  atom2  second atom
	 *@return        squared distance between the 2 atoms
	 */
	private double calculateSquaredDistanceBetweenTwoAtoms(org.openscience.cdk.interfaces.IAtom atom1, org.openscience.cdk.interfaces.IAtom atom2) {
		double distance = 0;
		double tmp = 0;
		Point3d firstPoint = atom1.getPoint3d();
		Point3d secondPoint = atom2.getPoint3d();
		tmp = firstPoint.distance(secondPoint);distance = tmp * tmp;
		return distance;
	}
}

