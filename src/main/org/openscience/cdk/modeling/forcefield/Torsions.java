/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Violeta Labarta <vlabarta@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.modeling.forcefield;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.modeling.builder3d.MMFF94ParametersCall;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;


/**
 *  Torsions calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    March 2, 2005
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 */
public class Torsions {

	String functionShape = " Torsions ";

	double mmff94SumET = 0;
	GVector gradientMMFF94SumET = new GVector(3);
	GVector dPhi = new GVector(3);
	
	GVector order2ndErrorApproximateGradientMMFF94SumET = new GVector(3);
	GVector order5thErrorApproximateGradientMMFF94SumET = new GVector(3);
	GVector xplusSigma = null;
	GVector xminusSigma = null;
	double sigma = Math.pow(0.000000000000001,0.33);
	
	GMatrix hessianMMFF94SumET = null;
	double[] forHessian = null;
	GMatrix order2ndErrorApproximateHessianMMFF94SumET = null;
	double[] forOrder2ndErrorApproximateHessian = null;

	int torsionNumber = 0;
	int[][] torsionAtomPosition = null;

	double[] v1 = null;
	double[] v2 = null;
	double[] v3 = null;
	double[] phi = null;
	
	IBond[] bond = null;
	IAtom[] atomInBond = null;
	IBond[] bondConnectedBefore = null;
	IBond[] bondConnectedAfter = null;


	//private LoggingTool logger;

	GVector moleculeCurrentCoordinates = null;
	boolean[] changeAtomCoordinates = null;
	int changedCoordinates;


	/**
	 *  Constructor for the Torsions object
	 */
	public Torsions() {        
		//logger = new LoggingTool(this);
	}


	/**
	 *  Set MMFF94 constants V1, V2 and V3 for each i-j, j-k and k-l bonded pairs in the molecule.
	 *
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94TorsionsParameters(IAtomContainer molecule, Map parameterSet) throws Exception {

		//logger.debug("setMMFF94TorsionsParameters");

        // looks like we need the bonds in an array for the rest of the class
        bond = new IBond[molecule.getBondCount()];
        int counter = 0;
        Iterator bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond aBond = (IBond) bonds.next();
            bond[counter] = aBond;
            counter++;
        }
                
		for (int b=0; b<bond.length; b++) {
			atomInBond = BondManipulator.getAtomArray(bond[b]);
			bondConnectedBefore = AtomContainerManipulator.getBondArray(molecule.getConnectedBondsList(atomInBond[0]));
			if (bondConnectedBefore.length > 1) {
				bondConnectedAfter = AtomContainerManipulator.getBondArray(molecule.getConnectedBondsList(atomInBond[1]));
				if (bondConnectedAfter.length > 1) {
					for (int bb=0; bb<bondConnectedBefore.length; bb++) {
						if (bondConnectedBefore[bb].compare(bond[b])) {}
						else {
							for (int ba=0; ba<bondConnectedAfter.length; ba++) {
								if (bondConnectedAfter[ba].compare(bond[b])) {}
								else {
									if (bondConnectedBefore[bb].isConnectedTo(bondConnectedAfter[ba])) {}
									else {
										torsionNumber += 1;
										//logger.debug("atomi(" + torsionNumber + ") : " + bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]).getAtomTypeName());
										//logger.debug("atomj(" + torsionNumber + ") : " + atomInBond[0].getAtomTypeName());
										//logger.debug("atomk(" + torsionNumber + ") : " + atomInBond[1].getAtomTypeName());
										//logger.debug("atoml(" + torsionNumber + ") : " + bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]).getAtomTypeName());
									}	
								}
							}
						}
					}
				}
			}
		}
		//logger.debug("torsionNumber = " + torsionNumber);

		List torsionsData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initialize(parameterSet);
		
		v1 = new double[torsionNumber];
		v2 = new double[torsionNumber];
		v3 = new double[torsionNumber];

		torsionAtomPosition = new int[torsionNumber][];

		String torsionType;
		int m = -1;
		for (int b=0; b<bond.length; b++) {
			atomInBond = BondManipulator.getAtomArray(bond[b]);
			bondConnectedBefore = AtomContainerManipulator.getBondArray(molecule.getConnectedBondsList(atomInBond[0]));
			if (bondConnectedBefore.length > 1) {
				bondConnectedAfter = AtomContainerManipulator.getBondArray(molecule.getConnectedBondsList(atomInBond[1]));
				if (bondConnectedAfter.length > 1) {
					for (int bb=0; bb<bondConnectedBefore.length; bb++) {
						if (bondConnectedBefore[bb].compare(bond[b])) {}
						else {
							for (int ba=0; ba<bondConnectedAfter.length; ba++) {
								if (bondConnectedAfter[ba].compare(bond[b])) {}
								else {
									if (bondConnectedBefore[bb].isConnectedTo(bondConnectedAfter[ba])) {}
									else {
										m += 1;
										torsionAtomPosition[m] = new int[4];
										torsionAtomPosition[m][0] = molecule.getAtomNumber(bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]));
										torsionAtomPosition[m][1] = molecule.getAtomNumber(atomInBond[0]);
										torsionAtomPosition[m][2] = molecule.getAtomNumber(atomInBond[1]);
										torsionAtomPosition[m][3] = molecule.getAtomNumber(bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]));
									
										/*System.out.println("torsion " + m + " : " + bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]).getFlag(CDKConstants.ISINRING) + "(" + torsionAtomPosition[m][0] + "), " + 
												atomInBond[0].getFlag(CDKConstants.ISINRING) + "(" + torsionAtomPosition[m][1] + "), " + atomInBond[1].getFlag(CDKConstants.ISINRING) + "(" + torsionAtomPosition[m][2] + "), " + 
												bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]).getFlag(CDKConstants.ISINRING) + "(" + torsionAtomPosition[m][3] + ")");		
									    */
										/*System.out.println("torsionAtomPosition[" + m + "]: " + bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]).getSymbol() 
											+ ", "+ atomInBond[0].getSymbol() + ", " + atomInBond[1].getSymbol() + ", " 
											+ bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]).getSymbol());
									    */
 
										torsionType = "0";
										if (bond[b].getProperty("MMFF94 bond type").toString() == "1") {
											torsionType = "1";
										}
										else if ((bond[b].getProperty("MMFF94 bond type").toString() == "0") & 
												((bondConnectedBefore[bb].getProperty("MMFF94 bond type").toString() == "1") |
												(bondConnectedAfter[ba].getProperty("MMFF94 bond type").toString() == "1"))) {
											torsionType = "2";
										}

										/*System.out.println("torsion " + m + " : " + torsionType + " " + bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]).getAtomTypeName() + "(" + torsionAtomPosition[m][0] + "), " + 
										atomInBond[0].getAtomTypeName() + "(" + torsionAtomPosition[m][1] + "), " + atomInBond[1].getAtomTypeName() + "(" + torsionAtomPosition[m][2] + "), " + 
										bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]).getAtomTypeName() + "(" + torsionAtomPosition[m][3] + ")");
										*/
										torsionsData = pc.getTorsionData(torsionType, bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]).getAtomTypeName(), 
												atomInBond[0].getAtomTypeName(), atomInBond[1].getAtomTypeName(), bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]).getAtomTypeName());
									
										//logger.debug("torsionsData " + m + ": " + torsionsData);
										v1[m] = ((Double) torsionsData.get(0)).doubleValue();
										v2[m] = /*(-1) * */((Double) torsionsData.get(1)).doubleValue();
										v3[m] = ((Double) torsionsData.get(2)).doubleValue();

									}
								}	
							}
						}
					}
				}
			}
		}

		phi = new double[torsionNumber];

		this.moleculeCurrentCoordinates = new GVector(3 * molecule.getAtomCount());
		for (int i=0; i<moleculeCurrentCoordinates.getSize(); i++) {
			this.moleculeCurrentCoordinates.setElement(i,1E10);
		} 

		this.changeAtomCoordinates = new boolean[molecule.getAtomCount()];

	}


	/**
	 *  Calculate the actual phi
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setPhi(GVector coords3d) {
		changedCoordinates = 0;
		//logger.debug("Setting Phi");
		for (int i=0; i < changeAtomCoordinates.length; i++) {
			this.changeAtomCoordinates[i] = false;
		}
		this.moleculeCurrentCoordinates.sub(coords3d);
		for (int i = 0; i < this.moleculeCurrentCoordinates.getSize(); i++) {
			//logger.debug("moleculeCurrentCoordinates " + i + " = " + this.moleculeCurrentCoordinates.getElement(i));
			if (Math.abs(this.moleculeCurrentCoordinates.getElement(i)) > 0) {
				changeAtomCoordinates[i/3] = true;
				changedCoordinates = changedCoordinates + 1;
				//logger.debug("changeAtomCoordinates[" + i/3 + "] = " + changeAtomCoordinates[i/3]);
				i = i + (2 - i % 3);
			}
		}

		for (int m = 0; m < torsionNumber; m++) {
			if ((changeAtomCoordinates[torsionAtomPosition[m][0]] == true) | 
					(changeAtomCoordinates[torsionAtomPosition[m][1]] == true) | 
					(changeAtomCoordinates[torsionAtomPosition[m][2]] == true) |
					(changeAtomCoordinates[torsionAtomPosition[m][3]] == true))		{
			
				phi[m] = ForceFieldTools.torsionAngleFrom3xNCoordinates(coords3d, torsionAtomPosition[m][0], torsionAtomPosition[m][1], 
							torsionAtomPosition[m][2], torsionAtomPosition[m][3]);
			} 
			//else {System.out.println("phi was no recalculated");}
		}
		/*if 	(changedCoordinates == changeAtomCoordinates.length) {
			for (int m = 0; m < torsionNumber; m++) {
				System.out.println("phi[" + m + "] = " + Math.toDegrees(phi[m]));
			}
		}
		*/
		moleculeCurrentCoordinates.set(coords3d);
		
	}


	/**
	 *  Evaluate the MMFF94 torsions term.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 *@return        MMFF94 torsions term value.
	 */
	public double functionMMFF94SumET(GVector coords3d) {
		//logger.debug("SetPhi for torsion energy evaluation");
		setPhi(coords3d);
		mmff94SumET = 0;
		double torsionEnergy=0;
		for (int m = 0; m < torsionNumber; m++) {
			//logger.debug("phi[" + m + "] = " + Math.round(Math.toDegrees(phi[m])) + ",	cos(phi[" + m + "]) = " + Math.round(Math.cos(phi[m])) + ",	cos(2 * phi[" + m + "]) = " + Math.round(Math.cos(2 * phi[m])) + ",	cos(3 * phi[" + m + "]) = " + Math.round(Math.cos(3 * phi[m]))); 
			torsionEnergy = v1[m] * (1 + Math.cos(phi[m])) + v2[m] * (1 - Math.cos(2 * phi[m])) + v3[m] * (1 + Math.cos(3 * phi[m]));
			//logger.debug("phi[" + m + "] = " + Math.toDegrees(phi[m]) + ", cph" + Math.cos(phi[m]) + ", c2ph" + Math.cos(2 * phi[m]) + ", c3ph" + Math.cos(3 * phi[m]) + ", te=" + torsionEnergy);
			//if (torsionEnergy < 0) {
			//	torsionEnergy= (-1) * torsionEnergy;
			//}
			mmff94SumET = mmff94SumET + torsionEnergy;
			
			//mmff94SumET = mmff94SumET + v1[m] * (1 + phi[m]) + v2[m] * (1 - 2 * phi[m]) + v3[m] * (1 + 3 * phi[m]);
		}
		//logger.debug("mmff94SumET = " + mmff94SumET);
		return mmff94SumET;
	}


	/**
	 *  Evaluate the gradient of the torsions term.
	 *  
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumET(GVector coords3d) {

		gradientMMFF94SumET.setSize(coords3d.getSize());
		//logger.debug("Set phi for torsion energy gradient calculation");
		setPhi(coords3d);
		dPhi.setSize(coords3d.getSize());

		double sumGradientET;
		for (int i = 0; i < gradientMMFF94SumET.getSize(); i++) {

			sumGradientET = 0;
			dPhi.setElement(i,1);                 // dPhi : partial derivative of phi. To change in the future

			for (int m = 0; m < torsionNumber; m++) {

				sumGradientET = sumGradientET - v1[m] * Math.sin(phi[m]) * dPhi.getElement(i) + 
					v2[m] * Math.sin(2 * phi[m]) * 2 * dPhi.getElement(i) - 
					v3[m] * Math.sin(3 * phi[m]) * 3 * dPhi.getElement(i);
			}
			gradientMMFF94SumET.setElement(i, sumGradientET);
		}
		//logger.debug("gradientMMFF94SumET = " + gradientMMFF94SumET);
	}


	/**
	 *  Get the gradient of the torsions term. 
	 *  
	 *
	 *@return           torsions gradient value.
	 */
	public GVector getGradientMMFF94SumET() {
		return gradientMMFF94SumET;
	}


	/**
	 *  Evaluate a 2nd order error approximation of the gradient, for the torsion term, 
	 *  given the atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateGradientMMFF94SumET(GVector coord3d) {
		//logger.debug("Set the approximative gradient of the torsion energy");
		order2ndErrorApproximateGradientMMFF94SumET.setSize(coord3d.getSize());
		xplusSigma = new GVector(coord3d.getSize());
		xminusSigma = new GVector(coord3d.getSize());
		
		for (int m = 0; m < order2ndErrorApproximateGradientMMFF94SumET.getSize(); m++) { 
			//logger.debug("m = " + m);
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			order2ndErrorApproximateGradientMMFF94SumET.setElement(m,(functionMMFF94SumET(xplusSigma) - functionMMFF94SumET(xminusSigma)) / (2 * sigma));
		}
			
		//logger.debug("order2ndErrorApproximateGradientMMFF94SumET : " + order2ndErrorApproximateGradientMMFF94SumET);
	}


	/**
	 *  Get the 2nd order error approximate gradient of the torsion term.
	 *
	 *
	 *@return           torsion approximate gradient value
	 */
	public GVector get2ndOrderErrorApproximateGradientMMFF94SumET() {
		return order2ndErrorApproximateGradientMMFF94SumET;
	}


	/**
	 *  Evaluate an 5 order approximation of the gradient, of the torsion term, 
	 *  given the atoms coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set5thOrderApproximateGradientMMFF94SumET(GVector coord3d) {
		order5thErrorApproximateGradientMMFF94SumET.setSize(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.2);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		GVector xplus2Sigma = new GVector(coord3d.getSize());
		GVector xminus2Sigma = new GVector(coord3d.getSize());
		
		for (int m=0; m < order5thErrorApproximateGradientMMFF94SumET.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			xplus2Sigma.set(coord3d);
			xplus2Sigma.setElement(m,coord3d.getElement(m) + 2 * sigma);
			xminus2Sigma.set(coord3d);
			xminus2Sigma.setElement(m,coord3d.getElement(m) - 2 * sigma);
			order5thErrorApproximateGradientMMFF94SumET.setElement(m, (8 * (functionMMFF94SumET(xplusSigma) - functionMMFF94SumET(xminusSigma)) - (functionMMFF94SumET(xplus2Sigma) - functionMMFF94SumET(xminus2Sigma))) / (12 * sigma));
		}
			
		//logger.debug("order5thErrorApproximateGradientMMFF94SumET : " + order5thErrorApproximateGradientMMFF94SumET);
	}


	/**
	 *  Get the 5th order error approximate gradient of the torsion term.
	 *
	 *@return        Torsion 5th order error approximate gradient value.
	 */
	public GVector get5thOrderErrorApproximateGradientMMFF94SumET() {
		return order5thErrorApproximateGradientMMFF94SumET;
	}


	/**
	 *  Evaluate the hessian of the torsions.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumET(GVector coords3d) {

		double[] forHessian = new double[coords3d.getSize() * coords3d.getSize()];
		setPhi(coords3d);
		double[] ddPhi = new double[coords3d.getSize() * coords3d.getSize()];
		
		double sumHessianET = 0;
		for (int i = 0; i < coords3d.getSize(); i++) {
			for (int j = 0; j < dPhi.getSize(); j++) {
				ddPhi[i*j] = 0;
				for (int m = 0; m < torsionNumber; m++) {
					sumHessianET = sumHessianET - v1[m] * (Math.cos(phi[m]) * dPhi.getElement(i) * dPhi.getElement(j) + Math.sin(phi[m]) * ddPhi[i*j]) +
					2 * v2[m] * (Math.cos(2 * phi[m]) * 2 * dPhi.getElement(i) * dPhi.getElement(j) + Math.sin(2 * phi[m]) * ddPhi[i*j]) -
					3 * v3[m] * (Math.cos(3 * phi[m]) * 3 * dPhi.getElement(i) * dPhi.getElement(j) + Math.sin(3 * phi[m]) * ddPhi[i*j]);
				}
			}
			forHessian[i] = 0.5 * sumHessianET;
		}

		hessianMMFF94SumET.setSize(coords3d.getSize(), coords3d.getSize());
		hessianMMFF94SumET.set(forHessian); 
		//logger.debug("hessianMMFF94SumET : " + hessianMMFF94SumET);
	}


	/**
	 *  Get the hessian of the torsions.
	 *
	 *@return        Hessian value of the torsions term.
	 */
	public GMatrix getHessianMMFF94SumET() {
		return hessianMMFF94SumET;
	}


	/**
	 *  Evaluate a 2nd order approximation of the Hessian, for the torsion energy term,
	 *  given the atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateHessianMMFF94SumET(GVector coord3d) {
		forOrder2ndErrorApproximateHessian = new double[coord3d.getSize() * coord3d.getSize()];
		
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xminusSigma = new GVector(coord3d.getSize());
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector gradientAtXminusSigma = new GVector(coord3d.getSize());
		GVector gradientAtXplusSigma = new GVector(coord3d.getSize());
		
		int forHessianIndex;
		for (int i = 0; i < coord3d.getSize(); i++) {
			xminusSigma.set(coord3d);
			xminusSigma.setElement(i,coord3d.getElement(i) - sigma);
			setGradientMMFF94SumET(xminusSigma);
			gradientAtXminusSigma.set(gradientMMFF94SumET);
			xplusSigma.set(coord3d);
			xplusSigma.setElement(i,coord3d.getElement(i) + sigma);
			setGradientMMFF94SumET(xplusSigma);
			gradientAtXplusSigma.set(gradientMMFF94SumET);
			for (int j = 0; j < coord3d.getSize(); j++) {
				forHessianIndex = i*coord3d.getSize()+j;
				forOrder2ndErrorApproximateHessian[forHessianIndex] = (gradientAtXplusSigma.getElement(j) - gradientAtXminusSigma.getElement(j)) / (2 * sigma);
				//(functionMMFF94SumET(xplusSigma) - 2 * fx + functionMMFF94SumET(xminusSigma)) / Math.pow(sigma,2);
			}
		}
		
		order2ndErrorApproximateHessianMMFF94SumET = new GMatrix(coord3d.getSize(), coord3d.getSize());
		order2ndErrorApproximateHessianMMFF94SumET.set(forOrder2ndErrorApproximateHessian);
		//logger.debug("order2ndErrorApproximateHessianMMFF94SumET : " + order2ndErrorApproximateHessianMMFF94SumET);
	}


	/**
	 *  Get the 2nd order error approximate Hessian for the torsion term.
	 *
	 *
	 *@return           Torsion 2nd order error approximate Hessian value.
	 */
	public GMatrix get2ndOrderErrorApproximateHessianMMFF94SumET() {
		return order2ndErrorApproximateHessianMMFF94SumET;
	}

}

