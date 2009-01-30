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

import java.util.List;
import java.util.Map;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.BondsToAtomDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;


/**			
 *  Van Der Waals Interactions calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    February 17, 2005
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 */
public class VanDerWaalsInteractions {

	String functionShape = " Van Der Waals Interactions ";

	GVector currentCoordinates = null;
	double mmff94SumEvdW = 0;
	double[] vdWE1 = null;
	double[] vdWE2 = null;
	double ccgSumEvdWSlaterKirkwood = 0;
	double ccgSumEvdWAverage = 0;
	GVector gradientMMFF94SumEvdW = null;
	double[] vdWEG1 = null;
	double[] vdWEG2 = null;
	GVector order2ndErrorApproximateGradientMMFF94SumEvdW = null;
	GVector order5thErrorApproximateGradientMMFF94SumEvdW = null;
	GVector gradientCCGSumEvdWSlaterKirkwood = null;
	GVector gradientCCGSumEvdWAverage = null;
	GMatrix hessianMMFF94SumEvdW = null;
	double[] forHessian = null;
	
	double[][] dR = null;	// Atom distance first order derivative respect to atoms coordinates
	double[][][] ddR = null;

	GVector dterm1 = null;
	GVector dterm2 = null;
	GVector ds = null;
	GVector dt = null;
	GVector dIvdw = null;
	
	//int[][] distances = null;	//Better check common atom connected
	IAtomicDescriptor shortestPathBetweenToAtoms = new BondsToAtomDescriptor();
	Object[] params = {Integer.valueOf(0)};
	
	int vdwInteractionNumber;
	int[][] vdWiAtomPosition = null;

	double[] eSK = null; 	// vdW well depths (mmff94: Slater-Kirkwood-based formula).
	double[] asteriskR = null;	// minimum-energy separation in angstroms (mmff94).
	double[] r = null;	// interatomic distance
	double[]  atomRadiu0;

	double[] eAv = null; 	// vdW well depths (Average).
	double[] capitalR = null;	// minimum-energy separation in angstroms (Average).
	
	double bb = 0.2;
	double microB = 12;

	// Parameters for the ccg vdWaals function
	double a = 0.07;	// buffering constants (a,b). Prevent division by 0.
	double b = 0.12;
	double n = 7;
	double m = 14;
	double nij = n;
	double mij = m - n;
	double[] t = null;
	double[] ivdw = null;
	double vdwScale14 = 1;	// Scale factor for 1-4 interactions. To take in the future from mmff94.prm files.
	
	//private LoggingTool logger;
	
	/**
	 *  Constructor for the VanDerWaalsInteractions object
	 */
	public VanDerWaalsInteractions() {        
		//logger = new LoggingTool(this);
	}


	/**
	 *  Set CCG Van Der Waals parameters for the molecule.
	 *
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94VanDerWaalsParameters(IAtomContainer molecule, Map parameterSet) throws Exception {

		//distances = wnd.getShortestPathLengthBetweenAtoms((AtomContainer) molecule);
		
		vdwInteractionNumber = 0;
		for (int i=0; i<molecule.getAtomCount(); i++) {
			for (int j=i+1; j<molecule.getAtomCount(); j++) {
				params[0] = Integer.valueOf(j);
				shortestPathBetweenToAtoms.setParameters(params);
				//if (distances[molecule.getAtomNumber(molecule.getAtomAt(i))][molecule.getAtomNumber(molecule.getAtomAt(j))]>2) {
				if (((IntegerResult)shortestPathBetweenToAtoms.calculate(molecule.getAtom(i),molecule).getValue()).intValue()>2){
					vdwInteractionNumber += 1;
				}
			}
		}
		//logger.debug("vdwInteractionNumber : " + vdwInteractionNumber);
		
		List vdwInteractionData = null;

		eSK = new double[vdwInteractionNumber];
		asteriskR = new double[vdwInteractionNumber];
		eAv = new double[vdwInteractionNumber];
		capitalR = new double[vdwInteractionNumber];
		r = new double[vdwInteractionNumber];
		atomRadiu0 = new double[molecule.getAtomCount()];
		t = new double[vdwInteractionNumber];
		ivdw = new double[vdwInteractionNumber];

		double gI;	// To eSK calculation
		double gJ;	// To eSK calculation
		double alphaI;	// To eSK calculation and asteriskR[l]
		double alphaJ;	// To eSK calculation and asteriskR[l]
		double nI;
		double nJ;
		double aaI;	// To calculate asteriskRI
		double aaJ;	// To calculate asteriskRJ
		double asteriskRI;
		double asteriskRJ;
		double gamma;	// To calculate asteriskR[l]
		double dI;
		double dJ;
		double eI;
		double eJ;
		
		vdWiAtomPosition = new int[vdwInteractionNumber][];

		int l = -1;
		for (int i=0; i<molecule.getAtomCount(); i++) {
			for (int j=i+1; j<molecule.getAtomCount(); j++) {
				params[0] = Integer.valueOf(j);
				shortestPathBetweenToAtoms.setParameters(params);
				//if (distances[molecule.getAtomNumber(molecule.getAtomAt(i))][molecule.getAtomNumber(molecule.getAtomAt(j))]>2) {
				if (((IntegerResult)shortestPathBetweenToAtoms.calculate(molecule.getAtom(i),molecule).getValue()).intValue()>2){
					l += 1;
					vdwInteractionData = (List) parameterSet.get("data" + molecule.getAtom(i).getAtomTypeName());
					//logger.debug("vdwInteractionData " + l + " : " + vdwInteractionData);
					aaI = ((Double) vdwInteractionData.get(6)).doubleValue();
					gI = ((Double) vdwInteractionData.get(7)).doubleValue();
					alphaI = ((Double) vdwInteractionData.get(1)).doubleValue();
					nI = ((Double) vdwInteractionData.get(2)).doubleValue();
					eI = ((Double) vdwInteractionData.get(0)).doubleValue();
					asteriskRI = aaI * Math.pow(alphaI,0.25);
					
					vdwInteractionData = (List) parameterSet.get("data" + molecule.getAtom(j).getAtomTypeName());
					//logger.debug("vdwInteractionData : " + vdwInteractionData);
					aaJ = ((Double) vdwInteractionData.get(6)).doubleValue();
					gJ = ((Double) vdwInteractionData.get(7)).doubleValue();
					alphaJ = ((Double) vdwInteractionData.get(1)).doubleValue();
					nJ = ((Double) vdwInteractionData.get(2)).doubleValue();
					eJ = ((Double) vdwInteractionData.get(0)).doubleValue();
					asteriskRJ = aaJ * Math.pow(alphaJ,0.25);
					
					if (molecule.getAtom(i).getAtomTypeName() == molecule.getAtom(j).getAtomTypeName()) {
						asteriskR[l] = asteriskRI;
					} else {
						gamma = (asteriskRI - asteriskRJ) / (asteriskRI + asteriskRJ);
						asteriskR[l] = 0.5 * (asteriskRI + asteriskRJ) * (1 + bb * (1 - Math.exp((-1) * microB * Math.pow(gamma,2))));
					}

					eSK[l] = ((181.16 * gI * gJ * alphaI * alphaJ) / (Math.sqrt(alphaI/nI) + Math.sqrt(alphaJ/nJ))) * 1 / Math.pow(asteriskR[l], 6);
					//logger.debug("eSK = " + eSK[l]);
					
					vdwInteractionData = (List) parameterSet.get("vdw" + molecule.getAtom(i).getAtomTypeName());
					//logger.debug("vdwInteractionData " + l + " : " + vdwInteractionData);
					atomRadiu0[i] = ((Double) vdwInteractionData.get(0)).doubleValue();
					vdwInteractionData = (List) parameterSet.get("vdw" + molecule.getAtom(j).getAtomTypeName());
					atomRadiu0[j] = ((Double) vdwInteractionData.get(0)).doubleValue();
					
					dI = 2 * atomRadiu0[i];
					dJ = 2 * atomRadiu0[j];
					capitalR[l] = (dI + dJ)/2;
					
					eAv[l] = Math.sqrt(eI * eJ);
					
					t[l] = 1;
					
					params[0] = Integer.valueOf(j);
					shortestPathBetweenToAtoms.setParameters(params);
					if (((IntegerResult)shortestPathBetweenToAtoms.calculate(molecule.getAtom(i),molecule).getValue()).intValue()==3){
					//if (distances[molecule.getAtomNumber(molecule.getAtomAt(i))][molecule.getAtomNumber(molecule.getAtomAt(j))] == 3) {
						ivdw[l] = vdwScale14;
					}else {
						ivdw[l] = 1;
					}
					

					vdWiAtomPosition[l] = new int[2];
					vdWiAtomPosition[l][0] = i;
					vdWiAtomPosition[l][1] = j;
				}
			}
		}
		
		currentCoordinates = new GVector(molecule.getAtomCount());
		vdWE1 = new double[vdwInteractionNumber];
		vdWE2 = new double[vdwInteractionNumber];
		vdWEG1 = new double[vdwInteractionNumber];
		vdWEG2 = new double[vdwInteractionNumber];

	}


	/**
	 *  Calculate the actual Rij
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setAtomDistance(GVector coords3d) {

		for (int l = 0; l < vdwInteractionNumber; l++) {

			r[l] = ForceFieldTools.distanceBetweenTwoAtomsFrom3xNCoordinates(coords3d, vdWiAtomPosition[l][0], vdWiAtomPosition[l][1]);
			//logger.debug("r[" + l + "]= " + r[l]);
		}
	}


	/**
	 *  Get the atom distances values (Rij).
	 *
	 *@return        Atom distance values.
	 */
	public double[] getAtomDistance() {
		return r;
	}


	/**
	 *  Evaluate the MMFF94 Van Der Waals interaction energy given the atoms cartesian coordinates.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setFunctionMMFF94SumEvdW(GVector coords3d) {
		if (currentCoordinates.equals(coords3d)) {} 
		else {
			currentCoordinates.set(coords3d);
			setAtomDistance(coords3d);
			mmff94SumEvdW = 0;
			for (int l = 0; l < vdwInteractionNumber; l++) {
				//logger.debug(" ");
				//logger.debug("eSK[" + l + "] = " + eSK[l]);
				//logger.debug("asteriskR[" + l + "] = " + asteriskR[l]);
				//logger.debug("r[" + l + "] = " + r[l]);
				//logger.debug("term rest with minus 2 : " + (1.12 * Math.pow(asteriskR[l],7) / (Math.pow(r[l],7) + 0.12 * Math.pow(asteriskR[l],7))));
				//logger.debug("vdwInteraction energy = " + (eSK[l] *
					//(Math.pow(1.07 * asteriskR[l] / (r[l] + 0.07 * asteriskR[l]) ,7)) *
					//((1.12 * Math.pow(asteriskR[l],7) / (Math.pow(r[l],7) + 0.12 * Math.pow(asteriskR[l],7))) - 2)));
				vdWE1[l] = eSK[l] * (Math.pow(1.07 * asteriskR[l] / (r[l] + 0.07 * asteriskR[l]) ,7));
				vdWE2[l] = (1.12 * Math.pow(asteriskR[l],7) / (Math.pow(r[l],7) + 0.12 * Math.pow(asteriskR[l],7))) - 2;
				mmff94SumEvdW = mmff94SumEvdW + vdWE1[l] * vdWE2[l];
				//logger.debug("mmff94SumEvdW = " + mmff94SumEvdW);
			}
			//mmff94SumEvdW = Math.abs(mmff94SumEvdW);
		}
		//logger.debug("mmff94SumEvdW = " + mmff94SumEvdW);
	}


	/**
	 *  Get the MMFF94 Van Der Waals interaction energy for the current atoms coordinates.
	 *
	 *@return        MMFF94 Van Der Waals interaction energy value.
	 */
	public double getFunctionMMFF94SumEvdW() {
		return mmff94SumEvdW;
	}


	/**
	 *  Evaluate the CCG Van Der Waals interaction term.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 *@return        CCG Van Der Waals interaction term value.
	 */
	public double functionCCGSumEvdWSK(GVector coords3d, double[] s) {
		if (currentCoordinates.equals(coords3d)) {} 
		else {
			setAtomDistance(coords3d);
			ccgSumEvdWSlaterKirkwood = 0;
			double c;
			for (int l = 0; l < vdwInteractionNumber; l++) {
				c = ((1+a) * asteriskR[l]) / (r[l] + a * asteriskR[l]);
				ccgSumEvdWSlaterKirkwood = ccgSumEvdWSlaterKirkwood +
								(eSK[l] *
								(Math.pow(c,nij)) *
								((nij/mij) * ((1+b) * Math.pow(asteriskR[l],mij) / (Math.pow(r[l],mij) + b * Math.pow(asteriskR[l],mij)))   - (mij + nij)/mij) *
								s[l] * t[l] * ivdw[l]);
			}
		}
		//logger.debug("ccgSumEvdWSlaterKirkwood = " + ccgSumEvdWSlaterKirkwood);
		return ccgSumEvdWSlaterKirkwood;
	}


	/**
	 *  Evaluate the CCG Van Der Waals interaction term.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 *@return        CCG Van Der Waals interaction term value.
	 */
	public double functionCCGSumEvdWAv(GVector coords3d, double[] s) {
		if (currentCoordinates.equals(coords3d)) {} 
		else {
			setAtomDistance(coords3d);
			ccgSumEvdWAverage = 0;
			double c;
			for (int l = 0; l < vdwInteractionNumber; l++) {
				c = ((1+a) * capitalR[l]) / (r[l] + a * capitalR[l]);
				ccgSumEvdWAverage = ccgSumEvdWAverage +
								(eAv[l] *
								(Math.pow(c,nij)) *
								((nij/mij) * ((1+b) * Math.pow(capitalR[l],mij) / (Math.pow(r[l],mij) + b * Math.pow(capitalR[l],mij))) - (mij + nij)/mij) *
								s[l] * t[l] * ivdw[l]);
								
			}
		}
		//logger.debug("ccgSumEvdWAverage = " + ccgSumEvdWAverage);
		return ccgSumEvdWAverage;
	}


	/**
	 *  Calculate the atoms distances (Rij) first derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setAtomsDistancesFirstOrderDerivative(GVector coord3d) {
		
		dR = new double[coord3d.getSize()][];
		
		Double forAtomNumber = null;
		int atomNumber = 0;
		int coordinate;
		for (int i = 0; i < dR.length; i++) {
			
			dR[i] = new double[vdwInteractionNumber];
			
			forAtomNumber = new Double(i/3);
			coordinate = i % 3;
			//logger.debug("coordinate = " + coordinate);

			atomNumber = forAtomNumber.intValue();
			//logger.debug("atomNumber = " + atomNumber);

			for (int j = 0; j < vdwInteractionNumber; j++) {

				if ((vdWiAtomPosition[j][0] == atomNumber) | (vdWiAtomPosition[j][1] == atomNumber)) {
					switch (coordinate) {
						//x-coordinate
						case 0: dR[i][j] = (coord3d.getElement(3 * vdWiAtomPosition[j][0]) - coord3d.getElement(3 * vdWiAtomPosition[j][1]))
								/ Math.sqrt(Math.pow(coord3d.getElement(3 * vdWiAtomPosition[j][0]) - coord3d.getElement(3 * vdWiAtomPosition[j][1]),2) + Math.pow(coord3d.getElement(3 * vdWiAtomPosition[j][0] + 1) - coord3d.getElement(3 * vdWiAtomPosition[j][1] + 1),2) + Math.pow(coord3d.getElement(3 * vdWiAtomPosition[j][0] + 2) - coord3d.getElement(3 * vdWiAtomPosition[j][1] + 2),2)); 
							break;
						//y-coordinate
						case 1:	dR[i][j] = (coord3d.getElement(3 * vdWiAtomPosition[j][0] + 1) - coord3d.getElement(3 * vdWiAtomPosition[j][1] + 1))
								/ Math.sqrt(Math.pow(coord3d.getElement(3 * vdWiAtomPosition[j][0]) - coord3d.getElement(3 * vdWiAtomPosition[j][1]),2) + Math.pow(coord3d.getElement(3 * vdWiAtomPosition[j][0] + 1) - coord3d.getElement(3 * vdWiAtomPosition[j][1] + 1),2) + Math.pow(coord3d.getElement(3 * vdWiAtomPosition[j][0] + 2) - coord3d.getElement(3 * vdWiAtomPosition[j][1] + 2),2)); 
							break;
						//z-coordinate
						case 2: dR[i][j] = (coord3d.getElement(3 * vdWiAtomPosition[j][0] + 2) - coord3d.getElement(3 * vdWiAtomPosition[j][1] + 2))
								/ Math.sqrt(Math.pow(coord3d.getElement(3 * vdWiAtomPosition[j][0]) - coord3d.getElement(3 * vdWiAtomPosition[j][1]),2) + Math.pow(coord3d.getElement(3 * vdWiAtomPosition[j][0] + 1) - coord3d.getElement(3 * vdWiAtomPosition[j][1] + 1),2) + Math.pow(coord3d.getElement(3 * vdWiAtomPosition[j][0] + 2) - coord3d.getElement(3 * vdWiAtomPosition[j][1] + 2),2)); 
							break;
					}
					if (vdWiAtomPosition[j][1] == atomNumber) {
						dR[i][j] = (-1) * dR[i][j];
					}
				} else {
					dR[i][j] = 0;
				}
				//logger.debug("vdW Interaction " + j + " : " + "dR[" + i + "][" + j + "] = " + dR[i][j]);
			}
		}
	}


	/**
	 *  Get the atoms distances first order derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Atoms distances first order derivative value [dimension(3xN)] [vdW interaction number]
	 */
	public double[][] getAtomsDistancesFirstDerivative() {
		return dR;
	}


	/**
	 *  Set the gradient of the MMFF94 Van Der Waals interaction term.
	 *
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEvdW(GVector coords3d) {

		gradientMMFF94SumEvdW = new GVector(coords3d.getSize());
		if (currentCoordinates.equals(coords3d)) {} 
		else {setFunctionMMFF94SumEvdW(coords3d);}

		setAtomsDistancesFirstOrderDerivative(coords3d);
		for (int l = 0; l < vdwInteractionNumber; l++) {
			vdWEG1[l] = eSK[l] * 7 * Math.pow((1.07 * asteriskR[l]) / (r[l] + 0.07 * asteriskR[l]),6) * 
					1.07 * asteriskR[l] * (-1) * (1/Math.pow((r[l] + 0.07 * asteriskR[l]),2));
			vdWEG2[l] = 1.12 * Math.pow(asteriskR[l],7) * 
					(-1) * (1/Math.pow((Math.pow(r[l],7) + 0.12 * Math.pow(asteriskR[l],7)),2)) * 7 * Math.pow(r[l],6);
		}
		
		double sumGradientEvdW;
		for (int i = 0; i < gradientMMFF94SumEvdW.getSize(); i++) {
			sumGradientEvdW = 0;
			for (int l = 0; l < vdwInteractionNumber; l++) {
				sumGradientEvdW = sumGradientEvdW + vdWEG1[l] * dR[i][l] * vdWE2[l] + vdWE1[l] * vdWEG2[l]  * dR[i][l];
			}
			gradientMMFF94SumEvdW.setElement(i, sumGradientEvdW);
		}
		//logger.debug("gradientMMFF94SumEvdW = " + gradientMMFF94SumEvdW);
	}


	/**
	 *  Get the gradient of the MMFF94 Van Der Waals interaction term.
	 *
	 *
	 *@return           MMFF94 Van Der Waals interaction gradient value.
	 */
	public GVector getGradientMMFF94SumEvdW() {
		return gradientMMFF94SumEvdW;
	}


	/**
	 *  Evaluate a 2nd order error approximation of the gradient, for the Van Der Waals interaction term, given the atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateGradientMMFF94SumEvdW(GVector coord3d) {
		order2ndErrorApproximateGradientMMFF94SumEvdW = new GVector(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		double fxplusSigma = 0;
		double fxminusSigma = 0;
		
		for (int m = 0; m < order2ndErrorApproximateGradientMMFF94SumEvdW.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			setFunctionMMFF94SumEvdW(xplusSigma);
			fxplusSigma = getFunctionMMFF94SumEvdW();
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			setFunctionMMFF94SumEvdW(xminusSigma);
			fxminusSigma = getFunctionMMFF94SumEvdW();
			order2ndErrorApproximateGradientMMFF94SumEvdW.setElement(m,(fxplusSigma - fxminusSigma) / (2 * sigma));
		}
			
		//logger.debug("order2ndErrorApproximateGradientMMFF94SumEvdW : " + order2ndErrorApproximateGradientMMFF94SumEvdW);
	}


	/**
	 *  Get the 2nd order error approximate gradient for the Van Der Waals interaction term.
	 *
	 *
	 *@return           Van Der Waals interaction 2nd order error approximate gradient value
	 */
	public GVector get2ndOrderErrorApproximateGradientMMFF94SumEvdW() {
		return order2ndErrorApproximateGradientMMFF94SumEvdW;
	}


	/**
	 *  Evaluate a 5th order error approximation of the gradient, for the Van Der Waals interaction term, given the atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set5thOrderErrorApproximateGradientMMFF94SumEvdW(GVector coord3d) {
		order5thErrorApproximateGradientMMFF94SumEvdW = new GVector(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.2);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		GVector xplus2Sigma = new GVector(coord3d.getSize());
		GVector xminus2Sigma = new GVector(coord3d.getSize());
		double fxplusSigma = 0;
		double fxminusSigma = 0;
		double fxplus2Sigma = 0;
		double fxminus2Sigma = 0;
		
		for (int m=0; m < order5thErrorApproximateGradientMMFF94SumEvdW.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			setFunctionMMFF94SumEvdW(xplusSigma);
			fxplusSigma = getFunctionMMFF94SumEvdW();
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			setFunctionMMFF94SumEvdW(xminusSigma);
			fxminusSigma = getFunctionMMFF94SumEvdW();
			xplus2Sigma.set(coord3d);
			xplus2Sigma.setElement(m,coord3d.getElement(m) + 2 * sigma);
			setFunctionMMFF94SumEvdW(xplus2Sigma);
			fxplus2Sigma = getFunctionMMFF94SumEvdW();
			xminus2Sigma.set(coord3d);
			xminus2Sigma.setElement(m,coord3d.getElement(m) - 2 * sigma);
			setFunctionMMFF94SumEvdW(xminus2Sigma);
			fxminus2Sigma = getFunctionMMFF94SumEvdW();
			order5thErrorApproximateGradientMMFF94SumEvdW.setElement(m, (8 * (fxplusSigma - fxminusSigma) - (fxplus2Sigma - fxminus2Sigma)) / (12 * sigma));
		}
			
		//logger.debug("order5thErrorApproximateGradientMMFF94SumEvdW : " + order5thErrorApproximateGradientMMFF94SumEvdW);
	}


	/**
	 *  Get the 5th order error approximate gradient for the Van Der Waals interaction term.
	 *
	 *@return        Torsion 5th order error approximate gradient value.
	 */
	public GVector get5OrderApproximateGradientMMFF94SumEvdW() {
		return order5thErrorApproximateGradientMMFF94SumEvdW;
	}


	/**
	 *  Evaluate the gradient of the CCG Van Der Waals interaction term.
	 *  
	 *
	 *@param  coords3d  Current molecule coordinates.
	 *@return           CCG Van Der Waals interaction gradient value.
	 */
/*	public GVector gradientMMFF94SumEvdW(GVector coords3d, double[] s) {

		gradientCCGSumEvdWSlaterKirkwood.setSize(molecule.getAtomCount() * 3);
		setAtomDistance(coords3d);
		dR.setSize(molecule.getAtomCount() * 3);

		dterm1.setSize(molecule.getAtomCount() * 3);
		dterm2.setSize(molecule.getAtomCount() * 3);
		
		double c;
		double[] term1 = new double[vdwInteractionNumber];
		double[] term2 = new double[vdwInteractionNumber];
		double sumGradientEvdW;
		for (int i = 0; i < gradientCCGSumEvdWSlaterKirkwood.getSize(); i++) {

			dterm1.setElement(i,1);                 // dterm1 : partial derivative of term1. To change in the future
			dterm2.setElement(i,1);                 // dterm2 : partial derivative of term2. To change in the future
			ds.setElement(i,1);                 // ds : partial derivative of s. To change in the future
			dt.setElement(i,1);                 // dt : partial derivative of t. To change in the future
			dIvdw.setElement(i,1);                 // dIvdw : partial derivative of Ivdw. To change in the future

			sumGradientEvdW = 0;
			for (int l = 0; l < vdwInteractionNumber; l++) {
				
				c = ((1+a) * asteriskR[l]) / (r[l] + a * asteriskR[l]);
				term1[l] = Math.pow(c,nij);
				term2[l] = (nij/mij) * ((1+b) * Math.pow(asteriskR[l],mij) / (Math.pow(r[l],mij) + b * Math.pow(asteriskR[l],mij)))   - (mij + nij)/mij;
				
				sumGradientEvdW = sumGradientEvdW + (deSK.getElement(i) * term1[l] * term2[l] + eSK[l] * (dterm1.getElement(i) * term2[l] + term1[l] * dterm2.getElement(i))) * s[l] * t[l] * ivdw[l] + 
								(eSK[l] * term1[l] * term2[l]) * (ds.getElement(i) * t[l] * ivdw[l] + s[l] * (dt.getElement(i) * ivdw[l] + t[l] * dIvdw.getElement(i)));
			}
			sumGradientEvdW = sumGradientEvdW * 2.51210;
			
			gradientCCGSumEvdWSlaterKirkwood.setElement(i, sumGradientEvdW);
		}
		//logger.debug("gradientCCGSumEvdWSlaterKirkwood = " + gradientCCGSumEvdWSlaterKirkwood);
		return gradientCCGSumEvdWSlaterKirkwood;
	}*/


	/**
	 *  Evaluate the hessian of the CCG Van Der Waals interaction term.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 *@return        Hessian value of the CCG Van Der Waals interaction term.
	 */
/*	public GMatrix hessian(GVector coords3d) {

		double[] forHessian = new double[coords3d.getSize() * coords3d.getSize()];
		setAtomDistance(coords3d);
		double sumHessianEvdW = 0;

		GMatrix ddR = new GMatrix(coords3d.getSize(),coords3d.getSize());
		ddR.setZero();

		for (int i = 0; i < forHessian.length; i++) {
			for (int j = 0; j < vdwInteractionNumber; j++) {
				sumHessianEvdW = sumHessianEvdW + 1;
			}
			forHessian[i] = sumHessianEvdW;
		}

		hessianMMFF94SumEvdW.setSize(coords3d.getSize(), coords3d.getSize());
		hessianMMFF94SumEvdW.set(forHessian); 
		//logger.debug("hessianMMFF94SumEvdW : " + hessianMMFF94SumEvdW);
		return hessianMMFF94SumEvdW;
	}*/


	/**
	 *  Calculate the bond lengths second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setBondLengthsSecondDerivative(GVector coord3d) {
		ddR = new double[coord3d.getSize()][][];
		
		Double forAtomNumber = null;
		int atomNumberi;
		int atomNumberj;
		int coordinatei;
		int coordinatej;
		double ddR1=0;	// ddR[i][j][k] = ddR1 - ddR2
		double ddR2=0;
		
		setAtomsDistancesFirstOrderDerivative(coord3d);
		
		for (int i=0; i<coord3d.getSize(); i++) {
			ddR[i] = new double[coord3d.getSize()][];
			
			forAtomNumber = new Double(i/3);
			
			atomNumberi = forAtomNumber.intValue();
			//logger.debug("atomNumberi = " + atomNumberi);
				
			coordinatei = i % 3;
			//logger.debug("coordinatei = " + coordinatei);
				
			for (int j=0; j<coord3d.getSize(); j++) {
				ddR[i][j] = new double[vdwInteractionNumber];
				
				forAtomNumber = new Double(j/3);

				atomNumberj = forAtomNumber.intValue();
				//logger.debug("atomNumberj = " + atomNumberj);

				coordinatej = j % 3;
				//logger.debug("coordinatej = " + coordinatej);
				
				//logger.debug("atomj : " + molecule.getAtomAt(atomNumberj));
				
				for (int k=0; k < vdwInteractionNumber; k++) {
					
					if ((vdWiAtomPosition[k][0] == atomNumberj) | (vdWiAtomPosition[k][1] == atomNumberj)) {
						if ((vdWiAtomPosition[k][0] == atomNumberi) | (vdWiAtomPosition[k][1] == atomNumberi)) {
					
							// ddR1
							if (vdWiAtomPosition[k][0] == atomNumberj) {
								ddR1 = 1;
							}
							if (vdWiAtomPosition[k][1] == atomNumberj) {
								ddR1 = -1;
							}
							if (vdWiAtomPosition[k][0] == atomNumberi) {
								ddR1 = ddR1 * 1;
							}
							if (vdWiAtomPosition[k][1] == atomNumberi) {
								ddR1 = ddR1 * (-1);
							}
							ddR1 = ddR1 / r[k];

							// ddR2
							switch (coordinatej) {
								case 0: ddR2 = (coord3d.getElement(3 * vdWiAtomPosition[k][0]) - coord3d.getElement(3 * vdWiAtomPosition[k][1]));
									//logger.debug("OK: d1 x");
									break;
								case 1:	ddR2 = (coord3d.getElement(3 * vdWiAtomPosition[k][0] + 1) - coord3d.getElement(3 * vdWiAtomPosition[k][1] + 1));
									//logger.debug("OK: d1 y");
									break;
								case 2:	ddR2 = (coord3d.getElement(3 * vdWiAtomPosition[k][0] + 2) - coord3d.getElement(3 * vdWiAtomPosition[k][1] + 2));
									//logger.debug("OK: d1 z");
									break;
							}
						
							if (vdWiAtomPosition[k][1] == atomNumberj) {
								ddR2 = (-1) * ddR2;
								//logger.debug("OK: bond 1");
							} 
	
							switch (coordinatei) {
								case 0: ddR2 = ddR2 * (coord3d.getElement(3 * vdWiAtomPosition[k][0]) - coord3d.getElement(3 * vdWiAtomPosition[k][1]));
									//logger.debug("OK: have d2 x");
									break;
								case 1:	ddR2 = ddR2 * (coord3d.getElement(3 * vdWiAtomPosition[k][0] + 1) - coord3d.getElement(3 * vdWiAtomPosition[k][1] + 1));
									//logger.debug("OK: have d2 y");
									break;
								case 2: ddR2 = ddR2 * (coord3d.getElement(3 * vdWiAtomPosition[k][0] + 2) - coord3d.getElement(3 * vdWiAtomPosition[k][1] + 2));
									//logger.debug("OK: have d2 z");
									break;
							}
							
							if (vdWiAtomPosition[k][1] == atomNumberi) {
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
					//logger.debug("bond " + k + " : " + "ddR[" + i + "][" + j + "][" + k + "] = " + ddR[i][j][k]);
				}
			}
		}	
	}


	/**
	 *  Get the bond lengths second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Bond lengths second derivative value [dimension(3xN)] [bonds Number]
	 */
	 public double[][][] getBondLengthsSecondDerivative() {
		return ddR;
	}


	/**
	 *  Evaluate the second order partial derivative (hessian) for the van der Waals interactions given the atoms coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEvdW(GVector coord3d) {
		
		forHessian = new double[coord3d.getSize() * coord3d.getSize()];
		
		if (currentCoordinates.equals(coord3d)) {} 
		else {setFunctionMMFF94SumEvdW(coord3d);}
		
		setBondLengthsSecondDerivative(coord3d);
		
		double sumHessianEvdW;
		int forHessianIndex;
		double vdWEHessian1 = 0;
		double vdWEHessian2 = 0;
		double vdWESD1 = 0;
		double vdWESD2 = 0;
		for (int i = 0; i < coord3d.getSize(); i++) {
			for (int j = 0; j < coord3d.getSize(); j++) {
				sumHessianEvdW = 0;
				for (int k = 0; k < vdwInteractionNumber; k++) {
					vdWESD1 = 89.47 * eSK[k] * Math.pow(asteriskR[k], 7) * (1/Math.pow(r[k] + 0.07 * asteriskR[k], 9)) * dR[i][k];
					vdWESD2 = -7.84 * Math.pow(asteriskR[k],7) * (6 * Math.pow(r[k],5) * dR[i][k] * Math.pow(Math.pow(r[k],7) + 0.12 * Math.pow(asteriskR[k],7),2) - Math.pow(r[k],12) * 14 * (Math.pow(r[k],7) + 0.12 * Math.pow(asteriskR[k],7)) * dR[i][k])/Math.pow(Math.pow(r[k],7) + 0.12 * Math.pow(asteriskR[k],7),4);
					vdWEHessian1 = (vdWESD1 * dR[j][k] + vdWEG1[k] * ddR[i][j][k]) * vdWE2[k] + vdWEG1[k] * (vdWEG2[k] * dR[i][k]);
					vdWEHessian2 = (vdWEG1[k] * dR[i][k]) * (vdWEG2[k]) + (vdWE1[k]) * (vdWESD2 * dR[j][k] + vdWEG2[k] * ddR[i][j][k]);
					sumHessianEvdW = sumHessianEvdW + (vdWEHessian1 + vdWEHessian2);
				}
				forHessianIndex = i*coord3d.getSize()+j;
				forHessian[forHessianIndex] = sumHessianEvdW;
			}
		}
		
		hessianMMFF94SumEvdW = new GMatrix(coord3d.getSize(), coord3d.getSize(), forHessian);
		//logger.debug("hessianMMFF94SumEvdW : " + hessianMMFF94SumEvdW);
	}


	/**
	 *  Get the hessian for the van der Waals interactions.
	 *
	 *@return        Hessian value of the van der Waals interactions term.
	 */
	public GMatrix getHessianMMFF94SumEvdW() {
		return hessianMMFF94SumEvdW;
	}


	/**
	 *  Get the hessian for the van der Waals interactions.
	 *
	 *@return        Hessian value of the van der Waals interactions term.
	 */
	public double[] getForHessianMMFF94SumEvdW() {
		return forHessian;
	}


}

