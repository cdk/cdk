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

import java.util.Map;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.BondsToAtomDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;


/**			
 *  MMFF94 Electrostatic Interactions energy. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    May 13, 2005
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 */
public class ElectrostaticInteractions {

	String functionShape = " Electrostatic Interactions ";

	double mmff94SumEQ = 0;
	GVector gradientMMFF94SumEQ = null;
	GVector order2ndErrorApproximateGradientMMFF94SumEQ = null;
	GVector order5thErrorApproximateGradientMMFF94SumEQ = null;
	GMatrix hessianMMFF94SumEQ = null;
	double[] forHessian = null;
	
	double[][] dR = null;	// internuclear separation first order derivative respect to atoms coordinates
	double[][][] ddR = null;	// internuclear separation second order derivative respect to atoms coordinates

	IAtomicDescriptor shortestPathBetweenTwoAtoms = new BondsToAtomDescriptor();
	Object[] params = {Integer.valueOf(0)};
	
	int electrostaticInteractionNumber;
	int[][] electrostaticInteractionAtomPosition = null;

	double[] r = null;	// internuclear separation in Angstroms.
	double[] qi = null;
	double[] qj = null;

	double delta = 0.05;	//electrostatic buffering constant.
	double n = 1;
	double D = 1.0;

	double[] iQ = null;
	double electrostatic14interactionsScale = 0.75;	// Scale factor for 1-4 interactions. To take in the future from mmff94.prm files.
	
	//private LoggingTool logger;
	
	/**
	 *  Constructor for the ElectrostaticInteractions object
	 */
	public ElectrostaticInteractions() {        
		//logger = new LoggingTool(this);
	}


	/**
	 *  Set CCG Electrostatic parameters for the molecule.
	 *
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94ElectrostaticParameters(IAtomContainer molecule, Map parameterSet) throws Exception {

		//distances = wnd.getShortestPathLengthBetweenAtoms((AtomContainer) molecule);
		
		//logger.debug("molecule.getAtomCount() : " + molecule.getAtomCount());
		//logger.debug("molecule.getBondCount() : " + molecule.getBondCount());
		if (molecule.getAtomCount() == 12 & molecule.getBondCount() == 11) {
		    molecule.getAtom(3).setCharge(Double.valueOf(1.0));
		    molecule.getAtom(8).setCharge(Double.valueOf(1.0));
		}
		
		electrostaticInteractionNumber = 0;
		for (int i=0; i<molecule.getAtomCount(); i++) {
			for (int j=i+1; j<molecule.getAtomCount(); j++) {
				params[0] = Integer.valueOf(j);
				shortestPathBetweenTwoAtoms.setParameters(params);
				//if (distances[molecule.getAtomNumber(molecule.getAtomAt(i))][molecule.getAtomNumber(molecule.getAtomAt(j))]>2) {
				if (((IntegerResult)shortestPathBetweenTwoAtoms.calculate(molecule.getAtom(i),molecule).getValue()).intValue()>2){
					electrostaticInteractionNumber += 1;
				}
			}
		}
		//logger.debug("electrostaticInteractionNumber : " + electrostaticInteractionNumber);

		qi = new double[electrostaticInteractionNumber];
		qj = new double[electrostaticInteractionNumber];
		r = new double[electrostaticInteractionNumber];
		iQ = new double[electrostaticInteractionNumber];
		
		electrostaticInteractionAtomPosition = new int[electrostaticInteractionNumber][];
		
		int l = -1;
		for (int i=0; i<molecule.getAtomCount(); i++) {
			for (int j=i+1; j<molecule.getAtomCount(); j++) {
				params[0] = Integer.valueOf(j);
				shortestPathBetweenTwoAtoms.setParameters(params);
				//if (distances[molecule.getAtomNumber(molecule.getAtomAt(i))][molecule.getAtomNumber(molecule.getAtomAt(j))]>2) {
				if (((IntegerResult)shortestPathBetweenTwoAtoms.calculate(molecule.getAtom(i),molecule).getValue()).intValue()>2){
					l += 1;
					qi[l]= molecule.getAtom(i).getCharge();
					qj[l]= molecule.getAtom(j).getCharge();
					//logger.debug("qi[" + l + "] = " + qi[l] + ", qj[" + l + "] = " + qj[l]);
					if (((IntegerResult)shortestPathBetweenTwoAtoms.calculate(molecule.getAtom(i),molecule).getValue()).intValue()==3){
						iQ[l] = electrostatic14interactionsScale;
					} else {
						iQ[l] = 1;
					}
					
					electrostaticInteractionAtomPosition[l] = new int[2];
					electrostaticInteractionAtomPosition[l][0] = i;
					electrostaticInteractionAtomPosition[l][1] = j;
					//logger.debug("electrostaticInteractionAtomPosition " + l + " : " + electrostaticInteractionAtomPosition[l][0] + ", " + electrostaticInteractionAtomPosition[l][1]);
				}
			}
		}
	}


	/**
	 *  Calculate the internuclear separation Rij
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setInternuclearSeparation(GVector coords3d) {

		for (int l = 0; l < electrostaticInteractionNumber; l++) {

			r[l] = ForceFieldTools.distanceBetweenTwoAtomsFrom3xNCoordinates(coords3d, electrostaticInteractionAtomPosition[l][0], electrostaticInteractionAtomPosition[l][1]);
			//logger.debug("r[" + l + "]= " + r[l]);
		}
	}


	/**
	 *  Get the internuclear separation values (Rij).
	 *
	 *@return        Internuclear separation values.
	 */
	public double[] getInternuclearSeparation() {
		return r;
	}


	/**
	 *  Evaluate the MMFF94 Electrostatic interaction energy.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 *@return        MMFF94 Electrostatic interaction term value.
	 */
	public double functionMMFF94SumEQ(GVector coords3d) {
		setInternuclearSeparation(coords3d);
		mmff94SumEQ = 0;
		for (int l = 0; l < electrostaticInteractionNumber; l++) {
			mmff94SumEQ = mmff94SumEQ + iQ[l] * 332.0716 * qi[l] * qj[l] / (D * Math.pow(r[l] + delta, n));
			//logger.debug("mmff94SumEQ = " + mmff94SumEQ);
		}
		//logger.debug("mmff94SumEQ = " + mmff94SumEQ);
		return mmff94SumEQ;
	}


	/**
	 *  Calculate the internuclear separation (Rij) first derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setInternuclearSeparationFirstOrderDerivative(GVector coord3d) {
		
		dR = new double[coord3d.getSize()][];
		setInternuclearSeparation(coord3d);
		
		Double forAtomNumber = null;
		int atomNumber = 0;
		int coordinate;
		for (int i = 0; i < dR.length; i++) {
			
			dR[i] = new double[electrostaticInteractionNumber];
			
			forAtomNumber = new Double(i/3);
			coordinate = i % 3;
			//logger.debug("coordinate = " + coordinate);

			atomNumber = forAtomNumber.intValue();
			//logger.debug("atomNumber = " + atomNumber);

			for (int j = 0; j < electrostaticInteractionNumber; j++) {

				if ((electrostaticInteractionAtomPosition[j][0] == atomNumber) | (electrostaticInteractionAtomPosition[j][1] == atomNumber)) {
					//logger.debug("atomNumber, r[" + j + "] = " + r[j]);
					switch (coordinate) {
						//x-coordinate
						case 0: dR[i][j] = (coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][0]) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][1])) / r[j];
							//logger.debug("xi-xj = " + (coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][0]) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][1])));
							break;
						//y-coordinate
						case 1:	dR[i][j] = (coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][0] + 1) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][1] + 1)) / r[j];
							//logger.debug("xi-xj = " + (coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][0]) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][1])));
							break;
						//z-coordinate
						case 2: dR[i][j] = (coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][0] + 2) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][1] + 2)) / r[j];
							//logger.debug("xi-xj = " + (coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][0]) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[j][1])));
							break;
					}
					if (electrostaticInteractionAtomPosition[j][1] == atomNumber) {
						dR[i][j] = (-1) * dR[i][j];
					}
				} else {
					dR[i][j] = 0;
				}
				//logger.debug("electrostaticInteraction " + j + " : " + "dR[" + i + "][" + j + "] = " + dR[i][j]);
			}
		}
	}


	/**
	 *  Get the internuclear separation first order derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Internuclear separation first order derivative value [dimension(3xN)] [vdW interaction number]
	 */
	public double[][] getInternuclearSeparationFirstDerivative() {
		return dR;
	}


	/**
	 *  Set the gradient of the MMFF94 Electrostatic interaction term.
	 *
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEQ(GVector coords3d) {

		gradientMMFF94SumEQ = new GVector(coords3d.getSize());
		setInternuclearSeparation(coords3d);
		setInternuclearSeparationFirstOrderDerivative(coords3d);
		
		double sumGradientEQ;
		for (int i = 0; i < gradientMMFF94SumEQ.getSize(); i++) {
			sumGradientEQ = 0;
			for (int l = 0; l < electrostaticInteractionNumber; l++) {
				sumGradientEQ = sumGradientEQ + ((-332.0716 * qi[l] * qj[l] * n)/(D * Math.pow(r[l] + delta, n+1))) * dR[i][l]; 
			}
			gradientMMFF94SumEQ.setElement(i, sumGradientEQ);
		}
		//logger.debug("gradientMMFF94SumEQ = " + gradientMMFF94SumEQ);
	}


	/**
	 *  Get the gradient of the MMFF94 Electrostatic interaction term.
	 *
	 *
	 *@return           MMFF94 Electrostatic interaction gradient value.
	 */
	public GVector getGradientMMFF94SumEQ() {
		return gradientMMFF94SumEQ;
	}


	/**
	 *  Evaluate a 2nd order error approximation of the gradient, for the Electrostatic interaction term, given the atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
/*	public void set2ndOrderErrorApproximateGradientMMFF94SumEQ(GVector coord3d) {
		order2ndErrorApproximateGradientMMFF94SumEQ = new GVector(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		
		for (int m = 0; m < order2ndErrorApproximateGradientMMFF94SumEQ.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			order2ndErrorApproximateGradientMMFF94SumEQ.setElement(m,(functionMMFF94SumEQ(xplusSigma) - functionMMFF94SumEQ(xminusSigma)) / (2 * sigma));
		}
			
		//logger.debug("order2ndErrorApproximateGradientMMFF94SumEQ : " + order2ndErrorApproximateGradientMMFF94SumEQ);
	}
*/

	/**
	 *  Get the 2nd order error approximate gradient for the Electrostatic interaction term.
	 *
	 *
	 *@return           Electrostatic interaction 2nd order error approximate gradient value
	 */
/*	public GVector get2ndOrderErrorApproximateGradientMMFF94SumEQ() {
		return order2ndErrorApproximateGradientMMFF94SumEQ;
	}
*/

	/**
	 *  Evaluate a 5th order error approximation of the gradient, for the Electrostatic interaction term, given the atoms
	 *  coordinates
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
/*	public void set5thOrderErrorApproximateGradientMMFF94SumEQ(GVector coord3d) {
		order5thErrorApproximateGradientMMFF94SumEQ = new GVector(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.2);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		GVector xplus2Sigma = new GVector(coord3d.getSize());
		GVector xminus2Sigma = new GVector(coord3d.getSize());
		
		for (int m=0; m < order5thErrorApproximateGradientMMFF94SumEQ.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			xplus2Sigma.set(coord3d);
			xplus2Sigma.setElement(m,coord3d.getElement(m) + 2 * sigma);
			xminus2Sigma.set(coord3d);
			xminus2Sigma.setElement(m,coord3d.getElement(m) - 2 * sigma);
			order5thErrorApproximateGradientMMFF94SumEQ.setElement(m, (8 * (functionMMFF94SumEQ(xplusSigma) - functionMMFF94SumEQ(xminusSigma)) - (functionMMFF94SumEQ(xplus2Sigma) - functionMMFF94SumEQ(xminus2Sigma))) / (12 * sigma));
		}
			
		//logger.debug("order5thErrorApproximateGradientMMFF94SumEQ : " + order5thErrorApproximateGradientMMFF94SumEQ);
	}
*/

	/**
	 *  Get the 5th order error approximate gradient for the Electrostatic interaction term.
	 *
	 *@return        Electrostatic interaction 5th order error approximate gradient value.
	 */
/*	public GVector get5OrderApproximateGradientMMFF94SumEQ() {
		return order5thErrorApproximateGradientMMFF94SumEQ;
	}
*/


	/**
	 *  Calculate the internuclear separation second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setInternuclearSeparationSecondDerivative(GVector coord3d) {
		ddR = new double[coord3d.getSize()][][];
		
		Double forAtomNumber = null;
		int atomNumberi;
		int atomNumberj;
		int coordinatei;
		int coordinatej;
		double ddR1=0;	// ddR[i][j][k] = ddR1 - ddR2
		double ddR2=0;
		
		setInternuclearSeparationFirstOrderDerivative(coord3d);
		
		for (int i=0; i<coord3d.getSize(); i++) {
			ddR[i] = new double[coord3d.getSize()][];
			
			forAtomNumber = new Double(i/3);
			
			atomNumberi = forAtomNumber.intValue();
			//logger.debug("atomNumberi = " + atomNumberi);
				
			coordinatei = i % 3;
			//logger.debug("coordinatei = " + coordinatei);
				
			for (int j=0; j<coord3d.getSize(); j++) {
				ddR[i][j] = new double[electrostaticInteractionNumber];
				
				forAtomNumber = new Double(j/3);

				atomNumberj = forAtomNumber.intValue();
				//logger.debug("atomNumberj = " + atomNumberj);

				coordinatej = j % 3;
				//logger.debug("coordinatej = " + coordinatej);
				
				//logger.debug("atomj : " + molecule.getAtomAt(atomNumberj));
				
				for (int k=0; k < electrostaticInteractionNumber; k++) {
					
					if ((electrostaticInteractionAtomPosition[k][0] == atomNumberj) | (electrostaticInteractionAtomPosition[k][1] == atomNumberj)) {
						if ((electrostaticInteractionAtomPosition[k][0] == atomNumberi) | (electrostaticInteractionAtomPosition[k][1] == atomNumberi)) {
					
							// ddR1
							if (electrostaticInteractionAtomPosition[k][0] == atomNumberj) {
								ddR1 = 1;
							}
							if (electrostaticInteractionAtomPosition[k][1] == atomNumberj) {
								ddR1 = -1;
							}
							if (electrostaticInteractionAtomPosition[k][0] == atomNumberi) {
								ddR1 = ddR1 * 1;
							}
							if (electrostaticInteractionAtomPosition[k][1] == atomNumberi) {
								ddR1 = ddR1 * (-1);
							}
							ddR1 = ddR1 / r[k];

							// ddR2
							switch (coordinatej) {
								case 0: ddR2 = (coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][0]) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][1]));
									//logger.debug("OK: d1 x");
									break;
								case 1:	ddR2 = (coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][0] + 1) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][1] + 1));
									//logger.debug("OK: d1 y");
									break;
								case 2:	ddR2 = (coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][0] + 2) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][1] + 2));
									//logger.debug("OK: d1 z");
									break;
							}
						
							if (electrostaticInteractionAtomPosition[k][1] == atomNumberj) {
								ddR2 = (-1) * ddR2;
								//logger.debug("OK: bond 1");
							} 
	
							switch (coordinatei) {
								case 0: ddR2 = ddR2 * (coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][0]) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][1]));
									//logger.debug("OK: have d2 x");
									break;
								case 1:	ddR2 = ddR2 * (coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][0] + 1) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][1] + 1));
									//logger.debug("OK: have d2 y");
									break;
								case 2: ddR2 = ddR2 * (coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][0] + 2) - coord3d.getElement(3 * electrostaticInteractionAtomPosition[k][1] + 2));
									//logger.debug("OK: have d2 z");
									break;
							}
							
							if (electrostaticInteractionAtomPosition[k][1] == atomNumberi) {
								ddR2 = (-1) * ddR2;
								//logger.debug("OK: d2 bond 1");
							}
							
							ddR2 = ddR2 / Math.pow(r[k],2);
							
							// ddR[i][j][k]
							ddR[i][j][k] = ddR1 - ddR2;
						} else {
							ddR[i][j][k] = 0;
							//logger.debug("OK: 0");
						}
					} else {
						ddR[i][j][k] = 0;
						//logger.debug("OK: 0");
					}
					//logger.debug("Electrostatic interactionn " + k + " : " + "ddR[" + i + "][" + j + "][" + k + "] = " + ddR[i][j][k]);
				}
			}
		}	
	}


	/**
	 *  Get the internuclear separation second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Bond lengths second derivative value [dimension(3xN)] [bonds Number]
	 */
	 public double[][][] getInternuclearSeparationSecondDerivative() {
		return ddR;
	}


	/**
	 *  Evaluate the second order partial derivative (hessian) for the Electrostatic interaction energy given the atoms coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEQ(GVector coord3d) {
		
		forHessian = new double[coord3d.getSize() * coord3d.getSize()];
		
		setInternuclearSeparationSecondDerivative(coord3d);
		
		double sumHessianEQ;
		int forHessianIndex;
		for (int i = 0; i < coord3d.getSize(); i++) {
			for (int j = 0; j < coord3d.getSize(); j++) {
				sumHessianEQ = 0;
				for (int k = 0; k < electrostaticInteractionNumber; k++) {
					sumHessianEQ = sumHessianEQ + (((332.0716 * qi[k] * qj[k] * n * (n+1) / D*Math.pow(r[k]+delta,n+2)) * dR[i][k]) * dR[j][k] 
									+ (-332.0716 * qi[k] * qj[k] * n / D * Math.pow(r[k]+delta,n+1)) * ddR[i][j][k]);
				}
				forHessianIndex = i*coord3d.getSize()+j;
				forHessian[forHessianIndex] = sumHessianEQ;
				//logger.debug("forHessian[forHessianIndex] : " + forHessian[forHessianIndex]);
			}
		}

		hessianMMFF94SumEQ = new GMatrix(coord3d.getSize(), coord3d.getSize(), forHessian);
		//logger.debug("hessianMMFF94SumEQ : " + hessianMMFF94SumEQ);
	}


	/**
	 *  Get the hessian for the Electrostatic interaction energy.
	 *
	 *@return        Hessian value of the Electrostatic interaction term.
	 */
	public GMatrix getHessianMMFF94SumEQ() {
		return hessianMMFF94SumEQ;
	}


	/**
	 *  Get the hessian for the Electrostatic interaction energy.
	 *
	 *@return        Hessian value of the Electrostatic interaction term.
	 */
	public double[] getForHessianMMFF94SumEQ() {
		return forHessian;
	}

}

