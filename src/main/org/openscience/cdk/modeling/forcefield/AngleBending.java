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
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.modeling.builder3d.MMFF94ParametersCall;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;


/**
 *  Angle bending calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    February 8, 2005
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 */
public class AngleBending {

	String functionShape = " Angle bending ";

	double mmff94SumEA = 0;
	GVector gradientMMFF94SumEA = null;
	GVector order2ndErrorApproximateGradientMMFF94SumEA = null;
	GVector order5thErrorApproximateGradientMMFF94SumEA = null;
	GMatrix hessianMMFF94SumEA = null;
	double[] forHessian = null;
	GMatrix order2ndErrorApproximateHessianMMFF94SumEA = null;
	double[] forOrder2ndErrorApproximateHessian = null;

	double[][] dDeltav = null;
	double[][] angleBendingOrder2ndErrorApproximateGradient = null;
	double[][][] ddDeltav = null;
	double[][][] angleBendingOrder2ndErrorApproximateHessian = null;

	int angleNumber = 0;
	int[][] angleAtomPosition = null;

	double[] v0 = null;
	double[] k2 = null;
	double[] k3 = null;
	double[] k4 = null;
	double cb = -0.007;
	double[] currentCoordinates_v = null;
	double[] currentCoordinates_deltav = null;
	double[] v = null;
	double[] deltav = null;
	
	boolean angleBending;
	
	GVector moleculeCurrentCoordinates = null;
	boolean[] changeAtomCoordinates = null;
	int changedCoordinates;



	/**
	 *  Constructor for the AngleBending object
	 */
	public AngleBending() {
	        //logger = new LoggingTool(this);
	}

	public void setAngleBendingFlag(boolean flag){
		angleBending=flag;
	}
	
	/**
	 *  Set MMFF94 reference angle v0IJK and the constants k2, k3, k4 for each
	 *  i-j-k angle in the molecule.
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94AngleBendingParameters(IAtomContainer molecule, Map parameterSet, boolean angleBendingFlag ) throws Exception {

		//logger.debug("setMMFF94AngleBendingParameters");		
		
		IAtom[] atomConnected = null;
		angleBending=angleBendingFlag;
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			atomConnected = AtomContainerManipulator.getAtomArray(molecule.getConnectedAtomsList(molecule.getAtom(i)));
			if (atomConnected.length > 1) {
				for (int j = 0; j < atomConnected.length; j++) {
					for (int k = j+1; k < atomConnected.length; k++) {
						angleNumber += 1;
					}
				}
			}
		}
		//logger.debug("angleNumber = " + angleNumber);

		List angleData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initialize(parameterSet);
		
		v0 = new double[angleNumber];
		k2 = new double[angleNumber];
		k3 = new double[angleNumber];
		k4 = new double[angleNumber];
		
		angleAtomPosition = new int[angleNumber][];

		String angleType;
		IBond bondIJ = null;
		IBond bondKJ = null;
		String bondIJType;
		String bondKJType;
		int l = -1;
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			atomConnected = AtomContainerManipulator.getAtomArray(molecule.getConnectedAtomsList(molecule.getAtom(i)));
			if (atomConnected.length > 1) {
				for (int j = 0; j < atomConnected.length; j++) {
					for (int k = j+1; k < atomConnected.length; k++) {
						l += 1;
						bondIJ = molecule.getBond(atomConnected[j], molecule.getAtom(i));
						bondIJType = bondIJ.getProperty("MMFF94 bond type").toString();
						//logger.debug("bondIJType = " + bondIJType);

						bondKJ = molecule.getBond(atomConnected[k], molecule.getAtom(i));
						bondKJType = bondKJ.getProperty("MMFF94 bond type").toString();
						//logger.debug("bondKJType = " + bondKJType);
						
						angleType = "0";
						if ((bondIJType == "1") | (bondKJType == "1")) {
							angleType = "1";
						}  
						if ((bondIJType == "1") & (bondKJType == "1")) {
							angleType = "2";
						}  
						
						//logger.debug(angleType + ", " + atomConnected[j].getAtomTypeName() + ", " + molecule.getAtom(i).getAtomTypeName() + ", " + atomConnected[k].getAtomTypeName());
						angleData = pc.getAngleData(angleType, atomConnected[j].getAtomTypeName(), molecule.getAtom(i).getAtomTypeName(), atomConnected[k].getAtomTypeName());
						//logger.debug("angleData : " + angleData);
						v0[l] = ((Double) angleData.get(0)).doubleValue();
						k2[l] = ((Double) angleData.get(1)).doubleValue();
						k3[l] = ((Double) angleData.get(2)).doubleValue();
						//k4[l] = ((Double) angleData.get(3)).doubleValue();

						//logger.debug("v0[" + l + "] = " + v0[l]);
						//logger.debug("k2[" + l + "] = " + k2[l]);
						//logger.debug("k3[" + l + "] = " + k3[l]);
						//logger.debug("k4[" + l + "] = " + k4[l]);
						
						angleAtomPosition[l] = new int[3];
						angleAtomPosition[l][0] = molecule.getAtomNumber(atomConnected[j]);
						angleAtomPosition[l][1] = i;
						angleAtomPosition[l][2] = molecule.getAtomNumber(atomConnected[k]);

					}
				}
			}
		}

		v = new double[angleNumber];
		deltav = new double[angleNumber];

		this.moleculeCurrentCoordinates = new GVector(3 * molecule.getAtomCount());
		for (int i=0; i<moleculeCurrentCoordinates.getSize(); i++) {
			this.moleculeCurrentCoordinates.setElement(i,1E10);
		} 

		this.changeAtomCoordinates = new boolean[molecule.getAtomCount()];
		for (int i=0; i < molecule.getAtomCount(); i++) {
			this.changeAtomCoordinates[i] = false;
		}

	}


	/**
	 *  Calculate the actual bond angles vijk and the difference with the reference angles.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setDeltav(GVector coord3d) {
		changedCoordinates = 0;
		//logger.debug("Setting Deltav");
		for (int i=0; i < changeAtomCoordinates.length; i++) {
			this.changeAtomCoordinates[i] = false;
		}
		this.moleculeCurrentCoordinates.sub(coord3d);
		for (int i = 0; i < this.moleculeCurrentCoordinates.getSize(); i++) {
			//logger.debug("this.moleculeCurrentCoordinates.getElement(i) = " + this.moleculeCurrentCoordinates.getElement(i));
			if (Math.abs(this.moleculeCurrentCoordinates.getElement(i)) > 0) {
				changeAtomCoordinates[i/3] = true;
				changedCoordinates = changedCoordinates + 1;
				//logger.debug("changeAtomCoordinates[" + i/3 + "] = " + changeAtomCoordinates[i/3]);
				i = i + (2 - i % 3);
			}
		}
		//logger.debug("currentCoordinates_deltav.length = " + currentCoordinates_deltav.length);

		for (int i = 0; i < angleNumber; i++) {
			if ((changeAtomCoordinates[angleAtomPosition[i][0]] == true) | 
				(changeAtomCoordinates[angleAtomPosition[i][1]] == true) | 
				(changeAtomCoordinates[angleAtomPosition[i][2]] == true))		{
				
				v[i] = ForceFieldTools.angleBetweenTwoBondsFrom3xNCoordinates(coord3d,angleAtomPosition[i][0],angleAtomPosition[i][1],angleAtomPosition[i][2]);
				//logger.debug("currentCoordinates_v[" + i + "] = " + currentCoordinates_v[i]);
				//logger.debug("v0[" + i + "] = " + v0[i]);
				deltav[i] = v[i] - v0[i];
				if (deltav[i] > 0 & angleBending) {
					deltav[i]= (-1) * deltav[i]; 
				}else if (deltav[i] < 0 & !angleBending){
					deltav[i]= (-1) * deltav[i]; 
				}
				/*if (Math.abs(currentCoordinates_deltav[i]) < 0.05) {
				 logger.debug("currentCoordinates_deltav[" + i + "]= " + currentCoordinates_deltav[i]);
				 }*/
			}
			//else {System.out.println("v[" + i + "] remain the same");}
		/*if 	(changedCoordinates == changeAtomCoordinates.length) {
		 		for (int m = 0; m < torsionNumber; m++) {
			 		System.out.println("phi[" + m + "] = " + Math.toDegrees(phi[m]));
			 	}
		}
		*/
		}
		moleculeCurrentCoordinates.set(coord3d);
	}



	/**
	 *  Get the current difference between the bond angles vijk and the reference angles.
	 *
	 *@return  Difference between the current bond angles vijk and the reference angles.
	 */
	public double[] getDeltav() {
		return deltav;	
	}


	/**
	 *  Evaluate the MMFF94 angle bending term for the given atoms coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 *@return        MMFF94 angle bending term value
	 */
	public double functionMMFF94SumEA(GVector coord3d) {
		//this.angleBending=true;
		this.setDeltav(coord3d);
		mmff94SumEA = 0;
		for (int i = 0; i < angleNumber; i++) {
			//logger.debug("k2[" + i + "]= " + k2[i]);
			//logger.debug("k3[" + i + "]= " + k3[i]);
			//logger.debug("currentCoordinates_deltav[" + i + "]= " + currentCoordinates_deltav[i]);
			//logger.debug("For Angle " + i + " : " + k2[i] * Math.pow(currentCoordinates_deltav[i],2) + k3[i] * Math.pow(currentCoordinates_deltav[i],3));

			mmff94SumEA = mmff94SumEA + k2[i] * Math.pow(deltav[i],2) 
						+ k3[i] * Math.pow(deltav[i],3);
						
			//mmff94SumEA = Math.abs(mmff94SumEA);
			
			//logger.debug("mmff94SumEA = " + mmff94SumEA);
		}
		//mmff94SumEA = Math.abs(mmff94SumEA);
		//logger.debug("mmff94SumEA = " + mmff94SumEA);
		return mmff94SumEA;
	}


	/**
	 *  Calculate the angle bending first derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setAngleBendingFirstDerivative(GVector coord3d) {
		
		dDeltav = new double[coord3d.getSize()][];
		
		Double forAtomNumber = null;
		int atomNumber = 0;
		int coordinate;

		for (int m = 0; m < dDeltav.length; m++) {
			
			dDeltav[m] = new double[angleNumber];
			
			forAtomNumber = new Double(m/3);
			coordinate = m % 3;
			//logger.debug("coordinate = " + coordinate);

			atomNumber = forAtomNumber.intValue();
			//logger.debug("atomNumber = " + atomNumber);

			for (int l = 0; l < angleNumber; l++) {
				
				if ((angleAtomPosition[l][0] == atomNumber) | (angleAtomPosition[l][1] == atomNumber) | (angleAtomPosition[l][2] == atomNumber)) {

					Point3d xi = new Point3d(coord3d.getElement(3 * angleAtomPosition[l][0]), coord3d.getElement(3 * angleAtomPosition[l][0] + 1),coord3d.getElement( 3 * angleAtomPosition[l][0] + 2));
					//logger.debug("xi = " + xi);
					Point3d xj = new Point3d(coord3d.getElement(3 * angleAtomPosition[l][1]), coord3d.getElement(3 * angleAtomPosition[l][1] + 1),coord3d.getElement( 3 * angleAtomPosition[l][1] + 2));
					//logger.debug("xj = " + xj);
					Point3d xk = new Point3d(coord3d.getElement(3 * angleAtomPosition[l][2]), coord3d.getElement(3 * angleAtomPosition[l][2] + 1),coord3d.getElement( 3 * angleAtomPosition[l][2] + 2));
					//logger.debug("xk = " + xk);
				
					Vector3d xij = new Vector3d();
					xij.sub(xi,xj);
					//logger.debug("xij = " + xij);
					Vector3d xkj = new Vector3d();
					xkj.sub(xk,xj);
					//logger.debug("xkj = " + xkj);
					
					double rji = xj.distance(xi);
					//logger.debug("rji = " + rji);
					double rjk = xj.distance(xk);
					//logger.debug("rji = " + rjk);

					dDeltav[m][l] = (-1/Math.sqrt(1-Math.pow(xij.dot(xkj)/(rji * rjk),2))) * (1/(Math.pow(rji,2) * Math.pow(rjk,2))); 

					if (angleAtomPosition[l][0] == atomNumber) {

						switch (coordinate) {
							case 0: dDeltav[m][l] = dDeltav[m][l] * ((xk.x-xj.x) * rji * rjk - (xij.dot(xkj)) * rjk * (-(xj.x-xi.x)/rji));
								break;
							case 1:	dDeltav[m][l] = dDeltav[m][l] * ((xk.y-xj.y) * rji * rjk - (xij.dot(xkj)) * rjk * (-(xj.y-xi.y)/rji));
								break;
							case 2: dDeltav[m][l] = dDeltav[m][l] * ((xk.z-xj.z) * rji * rjk - (xij.dot(xkj)) * rjk * (-(xj.z-xi.z)/rji));
								break;
						}
					}
					if (angleAtomPosition[l][1] == atomNumber) {

						switch (coordinate) {
							case 0: dDeltav[m][l] = dDeltav[m][l] * ((2 * xj.x - xk.x - xi.x) * rji * rjk - (xij.dot(xkj)) * (((xj.x-xi.x)/rji) * rjk + ((xj.x-xk.x)/rjk) * rji));
								break;
							case 1:	dDeltav[m][l] = dDeltav[m][l] * ((2 * xj.y - xk.y - xi.y) * rji * rjk - (xij.dot(xkj)) * (((xj.y-xi.y)/rji) * rjk + ((xj.y-xk.y)/rjk) * rji));
								break;
							case 2: dDeltav[m][l] = dDeltav[m][l] * ((2 * xj.z - xk.z - xi.z) * rji * rjk - (xij.dot(xkj)) * (((xj.z-xi.z)/rji) * rjk + ((xj.z-xk.z)/rjk) * rji));
								break;
						}
					}

					if (angleAtomPosition[l][2] == atomNumber) {

						switch (coordinate) {
							case 0: dDeltav[m][l] = dDeltav[m][l] * ((xi.x-xj.x) * rji * rjk - (xij.dot(xkj)) * rji * (-(xj.x-xk.x)/rjk));
								break;
							case 1:	dDeltav[m][l] = dDeltav[m][l] * ((xi.y-xj.y) * rji * rjk - (xij.dot(xkj)) * rji * (-(xj.y-xk.y)/rjk));
								break;
							case 2: dDeltav[m][l] = dDeltav[m][l] * ((xi.z-xj.z) * rji * rjk - (xij.dot(xkj)) * rji * (-(xj.z-xk.z)/rjk));
								break;
						}
					}

				}
				else {
					dDeltav[m][l] = 0;
				}
				//logger.debug("dDeltav[" + m + "][" + l + "] = " + dDeltav[m][l]);
			}
		}
		
	}


	/**
	 *  Get the bond lengths first derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Delta angle bending first derivative value [dimension(3xN)] [angles Number]
	 */
	public double[][] getAngleBendingFirstDerivative() {
		return dDeltav;
	}


	/**
	 *  Set a 2nd order approximation of the gradient, for the angle bending, given the atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setAngleBending2ndOrderErrorApproximateGradient(GVector coord3d) {
		angleBendingOrder2ndErrorApproximateGradient = new double[coord3d.getSize()][];
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		/*boolean[] sigmaChange = new boolean[coord3d.getSize()];
		for (int i=1; i < sigmaChange.length; i++) {sigmaChange[i] = false;}
		*/
		double[] deltavInXplusSigma = null;
		double[] deltavInXminusSigma = null;
		for (int m = 0; m < angleBendingOrder2ndErrorApproximateGradient.length; m++) {
			angleBendingOrder2ndErrorApproximateGradient[m] = new double[angleNumber];
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			setDeltav(xplusSigma);
			deltavInXplusSigma = getDeltav();
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			setDeltav(xminusSigma);
			deltavInXminusSigma = getDeltav();
			for (int l=0; l < angleNumber; l++) {
				angleBendingOrder2ndErrorApproximateGradient[m][l] = (deltavInXplusSigma[l] - deltavInXminusSigma[l]) / (2 * sigma);
			}
		}
			
		//logger.debug("order2ndErrorApproximateGradientMMFF94SumEA : " + order2ndErrorApproximateGradientMMFF94SumEA);
	}


	/**
	 *  Get the angle bending 2nd order error approximate gradient.
	 *
	 *
	 *@return           Angle bending 2nd order error approximate gradient value.
	 */
	public double[][] getAngleBending2ndOrderErrorApproximateGradient() {
		return angleBendingOrder2ndErrorApproximateGradient;
	}


	/**
	 *  Evaluate the gradient of the angle bending term for a given atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEA(GVector coord3d) {
		gradientMMFF94SumEA = new GVector(coord3d.getSize());

/*		boolean[] sigmaChange = new boolean[coord3d.getSize()];
		for (int i=1; i < sigmaChange.length; i++) {sigmaChange[i] = false;}
*/
		setDeltav(coord3d);
		setAngleBendingFirstDerivative(coord3d);
		
		double sumGradientEA;
		for (int m = 0; m < gradientMMFF94SumEA.getSize(); m++) {

			sumGradientEA = 0;
			for (int l = 0; l < angleNumber; l++) {

				sumGradientEA = sumGradientEA + (k2[l] * 2 * deltav[l] + k3[l] * 3 * Math.pow(deltav[l],2)) * dDeltav[m][l];
			}
			
			gradientMMFF94SumEA.setElement(m, sumGradientEA);
		}

		//logger.debug("gradientMMFF94SumEA : " + gradientMMFF94SumEA);
	}


	/**
	 *  Get the gradient of the angle bending term.
	 *
	 *
	 *@return           Angle bending gradient value
	 */
	public GVector getGradientMMFF94SumEA() {
		return gradientMMFF94SumEA;
	}


	/**
	 *  Evaluate a 2nd order approximation of the gradient, of the angle bending term, for a given atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateGradientMMFF94SumEA(GVector coord3d) {
		order2ndErrorApproximateGradientMMFF94SumEA = new GVector(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		boolean[] sigmaChange = new boolean[coord3d.getSize()];
		for (int i=1; i < sigmaChange.length; i++) {sigmaChange[i] = false;}
		
		for (int m = 0; m < order2ndErrorApproximateGradientMMFF94SumEA.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			order2ndErrorApproximateGradientMMFF94SumEA.setElement(m,(functionMMFF94SumEA(xplusSigma) - functionMMFF94SumEA(xminusSigma)) / (2 * sigma));
		}
			
		//logger.debug("order2ndErrorApproximateGradientMMFF94SumEA : " + order2ndErrorApproximateGradientMMFF94SumEA);
	}


	/**
	 *  Get the 2nd order error approximate gradient of the angle bending term.
	 *
	 *
	 *@return           Angle bending 2nd order error approximate gradient value.
	 */
	public GVector get2ndOrderErrorApproximateGradientMMFF94SumEA() {
		return order2ndErrorApproximateGradientMMFF94SumEA;
	}


	/**
	 *  Evaluate a 5th order error approximation of the gradient, of the angle bending term, for a given atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set5thOrderErrorApproximateGradientMMFF94SumEA(GVector coord3d) {
		order5thErrorApproximateGradientMMFF94SumEA = new GVector(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.2);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		GVector xplus2Sigma = new GVector(coord3d.getSize());
		GVector xminus2Sigma = new GVector(coord3d.getSize());
		boolean[] sigmaChange = new boolean[coord3d.getSize()];
		for (int i=1; i < sigmaChange.length; i++) {sigmaChange[i] = false;}
		
		for (int m=0; m < order5thErrorApproximateGradientMMFF94SumEA.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			xplus2Sigma.set(coord3d);
			xplus2Sigma.setElement(m,coord3d.getElement(m) + 2 * sigma);
			xminus2Sigma.set(coord3d);
			xminus2Sigma.setElement(m,coord3d.getElement(m) - 2 * sigma);
			order5thErrorApproximateGradientMMFF94SumEA.setElement(m, (8 * (functionMMFF94SumEA(xplusSigma) - functionMMFF94SumEA(xminusSigma)) 
					- (functionMMFF94SumEA(xplus2Sigma) - functionMMFF94SumEA(xminus2Sigma))) / (12 * sigma));
		}
			
		//logger.debug("order5thErrorApproximateGradientMMFF94SumEA : " + order5thErrorApproximateGradientMMFF94SumEA);
	}


	/**
	 *  Get the 5 order approximate gradient of the angle bending term.
	 *
	 *@return        Angle bending 5 order approximate gradient value.
	 */
	public GVector get5thOrderErrorApproximateGradientMMFF94SumEA() {
		return order5thErrorApproximateGradientMMFF94SumEA;
	}


	/**
	 *  Calculate the angle bending second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setAngleBendingSecondDerivative(GVector coord3d) {
		
		ddDeltav = new double[coord3d.getSize()][][];
		
		Double forAtomNumber = null;
		int atomNumbern;
		int atomNumberm;
		int coordinaten;
		int coordinatem;
		
		double ddDeltav1;
		double ddDeltav2;
		
		double ddDeltav2a;
		double ddDeltav2b;
		
		double ddDeltav2a1;
		double ddDeltav2a2;
		double ddDeltav2a3;
		double ddDeltav2a4;

		double ddDeltav2a1a;
		double ddDeltav2a1b;
		
		double ddDeltav2a4a1;
		double ddDeltav2a4a1a;
		double ddDeltav2a4a1b;
		double ddDeltav2a4a1c;
		double ddDeltav2a4a2;

		double ddDeltav2a4b;

		double ddDeltav2a4c1;
		double ddDeltav2a4c1a;
		double ddDeltav2a4c1b;
		double ddDeltav2a4c1c;
		double ddDeltav2a4c2;

		double ddDeltav2a4d;

		setAngleBendingFirstDerivative(coord3d);
		
		for (int n=0; n<coord3d.getSize(); n++) {
			ddDeltav[n] = new double[coord3d.getSize()][];
			
			forAtomNumber = new Double(n/3);
			coordinaten = n % 3;
			//logger.debug("coordinaten = " + coordinaten);
				
			atomNumbern = forAtomNumber.intValue();
			//logger.debug("atomNumbern = " + atomNumbern);
				
			for (int m = 0; m < coord3d.getSize(); m++) {
			
				ddDeltav[n][m] = new double[angleNumber];
			
				forAtomNumber = new Double(m/3);
				coordinatem = m % 3;
				//logger.debug("coordinatem = " + coordinatem);

				atomNumberm = forAtomNumber.intValue();
				//logger.debug("atomNumberm = " + atomNumberm);

				for (int l = 0; l < angleNumber; l++) {
				
					if ((angleAtomPosition[l][0] == atomNumberm) | (angleAtomPosition[l][1] == atomNumberm) | (angleAtomPosition[l][2] == atomNumberm)) {
						if ((angleAtomPosition[l][0] == atomNumbern) | (angleAtomPosition[l][1] == atomNumbern) | (angleAtomPosition[l][2] == atomNumbern)) {

							Point3d xi = new Point3d(coord3d.getElement(3 * angleAtomPosition[l][0]), coord3d.getElement(3 * angleAtomPosition[l][0] + 1),coord3d.getElement( 3 * angleAtomPosition[l][0] + 2));
							//logger.debug("xi = " + xi);
							Point3d xj = new Point3d(coord3d.getElement(3 * angleAtomPosition[l][1]), coord3d.getElement(3 * angleAtomPosition[l][1] + 1),coord3d.getElement( 3 * angleAtomPosition[l][1] + 2));
							//logger.debug("xj = " + xj);
							Point3d xk = new Point3d(coord3d.getElement(3 * angleAtomPosition[l][2]), coord3d.getElement(3 * angleAtomPosition[l][2] + 1),coord3d.getElement( 3 * angleAtomPosition[l][2] + 2));
							//logger.debug("xk = " + xk);
				
							Vector3d xij = new Vector3d();
							xij.sub(xi,xj);
							//logger.debug("xij = " + xij);
							Vector3d xkj = new Vector3d();
							xkj.sub(xk,xj);
							//logger.debug("xkj = " + xkj);
					
							double rij = xi.distance(xj);
							//logger.debug("rij = " + rij);
							double rkj = xk.distance(xj);
							//logger.debug("rkj = " + rkj);

							ddDeltav1 = (-1/Math.sqrt(Math.pow(1-Math.pow(xij.dot(xkj)/(rij * rkj),2),3)))
									* (xij.dot(xkj)/(rij * rkj))
									* (1/(Math.pow(rij,2) * Math.pow(rkj,2)));
							
							ddDeltav2 = (-1/Math.sqrt(1-Math.pow(xij.dot(xkj)/(rij * rkj),2))) 
									* (1/(Math.pow(rij,4) * Math.pow(rkj,4)));
							
							ddDeltav2a = Math.pow(rij,2) * Math.pow(rkj,2);
							
							ddDeltav2a1 = rij * rkj;
							ddDeltav2a1a = 0;
							ddDeltav2a1b = 0;
							ddDeltav2a2 = 0;
							ddDeltav2a3 = 0;
							ddDeltav2a4 = xij.dot(xkj);
							ddDeltav2a4a1a = 0;
							ddDeltav2a4a1b = 0;
							ddDeltav2a4a1c = 0;
							ddDeltav2a4a2 = 0;
							ddDeltav2b = 0;

							ddDeltav2a4a1 = 0;

							ddDeltav2a4b = 0;

							ddDeltav2a4c1 = 0;
							ddDeltav2a4c1a = 0;
							ddDeltav2a4c1b = 0;
							ddDeltav2a4c1c = 0;
							ddDeltav2a4c2 = 0;

							ddDeltav2a4d = 0;

							//logger.debug("OK: had d1 and have the atomNumbern");
						
							if (angleAtomPosition[l][0] == atomNumberm) {

								switch (coordinatem) {
									case 0: ddDeltav1 = ddDeltav1 * ((xk.x-xj.x) * rij * rkj - (xij.dot(xkj)) * rkj * ((xi.x-xj.x)/rij));
										ddDeltav2b = (xk.x-xj.x) * rij * rkj - (xij.dot(xkj)) * rkj * ((xi.x-xj.x)/rij);
										ddDeltav2a1a = 1;
										ddDeltav2a1b = 0;
										ddDeltav2a2 = xk.x-xj.x;
										ddDeltav2a3 = rkj * ((xi.x-xj.x)/rij);
										ddDeltav2a4a1a = 1;
										ddDeltav2a4a2 = xi.x-xj.x;
										ddDeltav2a4c1a = 0;
										ddDeltav2a4c2 = 0;
										ddDeltav2a4b = (xi.x-xj.x)/rij;
										ddDeltav2a4d = 0;
										break;
									case 1:	ddDeltav1 = ddDeltav1 * ((xk.y-xj.y) * rij * rkj - (xij.dot(xkj)) * rkj * ((xi.y-xj.y)/rij));
										ddDeltav2b = (xk.y-xj.y) * rij * rkj - (xij.dot(xkj)) * rkj * ((xi.y-xj.y)/rij);
										ddDeltav2a1a = 1;
										ddDeltav2a1b = 0;
										ddDeltav2a2 = xk.y-xj.y;
										ddDeltav2a3 = rkj * ((xi.y-xj.y)/rij);
										ddDeltav2a4a1b = 1;
										ddDeltav2a4a2 = xi.y-xj.y;
										ddDeltav2a4c1b = 0;
										ddDeltav2a4c2 = 0;
										ddDeltav2a4b = (xi.y-xj.y)/rij;
										ddDeltav2a4d = 0;
										break;
									case 2: ddDeltav1 = ddDeltav1 * ((xk.z-xj.z) * rij * rkj - (xij.dot(xkj)) * rkj * ((xi.z-xj.z)/rij));
										ddDeltav2b = (xk.z-xj.z) * rij * rkj - (xij.dot(xkj)) * rkj * ((xi.z-xj.z)/rij);
										ddDeltav2a1a = 1;
										ddDeltav2a1b = 0;
										ddDeltav2a2 = xk.z-xj.z;
										ddDeltav2a3 = rkj * ((xi.z-xj.z)/rij);
										ddDeltav2a4a1c = 1;
										ddDeltav2a4a2 = xi.z-xj.z;
										ddDeltav2a4c1c = 0;
										ddDeltav2a4c2 = 0;
										ddDeltav2a4b = (xi.z-xj.z)/rij;
										ddDeltav2a4d = 0;
										break;
								}
							}
							if (angleAtomPosition[l][1] == atomNumberm) {

								switch (coordinatem) {
									case 0: ddDeltav1 = ddDeltav1 * ((2 * xj.x - xk.x - xi.x) * rij * rkj - (xij.dot(xkj)) * ((-(xi.x-xj.x)/rij) * rkj + (-(xk.x-xj.x)/rkj) * rij));
										ddDeltav2b = (2 * xj.x - xk.x - xi.x) * rij * rkj - (xij.dot(xkj)) * ((-(xi.x-xj.x)/rij) * rkj + (-(xk.x-xj.x)/rkj) * rij);
										ddDeltav2a1a = -1;
										ddDeltav2a1b = -1;
										ddDeltav2a2 = 2 * xj.x - xk.x - xi.x;
										ddDeltav2a3 = (-(xi.x-xj.x)/rij) * rkj + (-(xk.x-xj.x)/rkj) * rij;
										ddDeltav2a4a1a = -1;
										ddDeltav2a4a2 = -(xi.x-xj.x);
										ddDeltav2a4c1a = -1;
										ddDeltav2a4c2 = -(xk.x-xj.x);
										ddDeltav2a4b = -(xi.x-xj.x)/rij;
										ddDeltav2a4d = -(xk.x-xj.x)/rkj;
										break;
									case 1:	ddDeltav1 = ddDeltav1 * ((2 * xj.y - xk.y - xi.y) * rij * rkj - (xij.dot(xkj)) * ((-(xi.y-xj.y)/rij) * rkj + (-(xk.y-xj.y)/rkj) * rij));
										ddDeltav2b = (2 * xj.y - xk.y - xi.y) * rij * rkj - (xij.dot(xkj)) * ((-(xi.y-xj.y)/rij) * rkj + (-(xk.y-xj.y)/rkj) * rij);
										ddDeltav2a1a = -1;
										ddDeltav2a1b = -1;
										ddDeltav2a2 = 2 * xj.y - xk.y - xi.y;
										ddDeltav2a3 = (-(xi.y-xj.y)/rij) * rkj + (-(xk.y-xj.y)/rkj) * rij;
										ddDeltav2a4a1b = -1;
										ddDeltav2a4a2 = -(xi.y-xj.y);
										ddDeltav2a4c1b = -1;
										ddDeltav2a4c2 = -(xk.y-xj.y);
										ddDeltav2a4b = -(xi.y-xj.y)/rij;
										ddDeltav2a4d = -(xk.y-xj.y)/rkj;
										break;
									case 2: ddDeltav1 = ddDeltav1 * ((2 * xj.z - xk.z - xi.z) * rij * rkj - (xij.dot(xkj)) * ((-(xi.z-xj.z)/rij) * rkj + (-(xk.z-xj.z)/rkj) * rij));
										ddDeltav2b = (2 * xj.z - xk.z - xi.z) * rij * rkj - (xij.dot(xkj)) * ((-(xi.z-xj.z)/rij) * rkj + (-(xk.z-xj.z)/rkj) * rij);
										ddDeltav2a1a = -1;
										ddDeltav2a1b = -1;
										ddDeltav2a2 = 2 * xj.z - xk.z - xi.z;
										ddDeltav2a3 = (-(xi.z-xj.z)/rij) * rkj + (-(xk.z-xj.z)/rkj) * rij;
										ddDeltav2a4a1c = -1;
										ddDeltav2a4a2 = -(xi.z-xj.z);
										ddDeltav2a4c1c = -1;
										ddDeltav2a4c2 = -(xk.z-xj.z);
										ddDeltav2a4b = -(xi.z-xj.z)/rij;
										ddDeltav2a4d = -(xk.z-xj.z)/rkj;
										break;
								}
							}
							if (angleAtomPosition[l][2] == atomNumberm) {

								switch (coordinatem) {
									case 0: ddDeltav1 = ddDeltav1 * ((xi.x-xj.x) * rij * rkj - (xij.dot(xkj)) * rij * ((xk.x-xj.x)/rkj));
										ddDeltav2b = (xi.x-xj.x) * rij * rkj - (xij.dot(xkj)) * rij * ((xk.x-xj.x)/rkj);
										ddDeltav2a1a = 0;
										ddDeltav2a1b = 1;
										ddDeltav2a2 = xi.x-xj.x;
										ddDeltav2a3 = rij * ((xk.x-xj.x)/rkj);
										ddDeltav2a4a1a = 0;
										ddDeltav2a4a2 = 0;
										ddDeltav2a4c1a = 1;
										ddDeltav2a4c2 = xk.x-xj.x;
										ddDeltav2a4b = 0;
										ddDeltav2a4d = (xk.x-xj.x)/rkj;
										break;
									case 1:	ddDeltav1 = ddDeltav1 * ((xi.y-xj.y) * rij * rkj - (xij.dot(xkj)) * rij * ((xk.y-xj.y)/rkj));
										ddDeltav2b = (xi.y-xj.y) * rij * rkj - (xij.dot(xkj)) * rij * ((xk.y-xj.y)/rkj);
										ddDeltav2a1a = 0;
										ddDeltav2a1b = 1;
										ddDeltav2a2 = xi.y-xj.y;
										ddDeltav2a3 = rij * ((xk.y-xj.y)/rkj);
										ddDeltav2a4a1b = 0;
										ddDeltav2a4a2 = 0;
										ddDeltav2a4c1b = 1;
										ddDeltav2a4c2 = xk.y-xj.y;
										ddDeltav2a4b = 0;
										ddDeltav2a4d = (xk.y-xj.y)/rkj;
										break;
									case 2: ddDeltav1 = ddDeltav1 * ((xi.z-xj.z) * rij * rkj - (xij.dot(xkj)) * rij * ((xk.z-xj.z)/rkj));
										ddDeltav2b = (xi.z-xj.z) * rij * rkj - (xij.dot(xkj)) * rij * ((xk.z-xj.z)/rkj);
										ddDeltav2a1a = 0;
										ddDeltav2a1b = 1;
										ddDeltav2a2 = xi.z-xj.z;
										ddDeltav2a3 = rij * ((xk.z-xj.z)/rkj);
										ddDeltav2a4a1c = 0;
										ddDeltav2a4a2 = 0;
										ddDeltav2a4c1c = 1;
										ddDeltav2a4c2 = xk.z-xj.z;
										ddDeltav2a4b = 0;
										ddDeltav2a4d = (xk.z-xj.z)/rkj;
										break;
								}
							}
							
							if (angleAtomPosition[l][0] == atomNumbern) {

								switch (coordinaten) {
									case 0: ddDeltav1 = ddDeltav1 * ((xk.x-xj.x) * rij * rkj - (xij.dot(xkj)) * rkj * ((xi.x-xj.x)/rij));
										ddDeltav2b = ddDeltav2b * 2 * rij * ((xi.x-xj.x)/rij) * Math.pow(rkj,2);
										ddDeltav2a1a = ddDeltav2a1a * 0;
										ddDeltav2a1b = ddDeltav2a1b * 1;
										ddDeltav2a2 = ddDeltav2a2 * rkj * ((xi.x-xj.x)/rij);
										ddDeltav2a3 = ddDeltav2a3 * (xk.x-xj.x);
										ddDeltav2a4a1a = ddDeltav2a4a1a * 1;
										ddDeltav2a4a2 = ddDeltav2a4a2 * ((xi.x-xj.x)/rij);
										ddDeltav2a4c1a = ddDeltav2a4c1a * 0;
										ddDeltav2a4c2 = ddDeltav2a4c2 * 0;
										ddDeltav2a4b = ddDeltav2a4b * 0;
										ddDeltav2a4d = ddDeltav2a4d * ((xi.x-xj.x)/rij);
										break;
									case 1:	ddDeltav1 = ddDeltav1 * ((xk.y-xj.y) * rij * rkj - (xij.dot(xkj)) * rkj * ((xi.y-xj.y)/rij));
										ddDeltav2b = ddDeltav2b * 2 * rij * ((xi.y-xj.y)/rij) * Math.pow(rkj,2);
										ddDeltav2a1a = ddDeltav2a1a * 0;
										ddDeltav2a1b = ddDeltav2a1b * 1;
										ddDeltav2a2 = ddDeltav2a2 * rkj * ((xi.y-xj.y)/rij);
										ddDeltav2a3 = ddDeltav2a3 * (xk.y-xj.y);
										ddDeltav2a4a1b = ddDeltav2a4a1b * 1;
										ddDeltav2a4a2 = ddDeltav2a4a2 * ((xi.y-xj.y)/rij);
										ddDeltav2a4c1b = ddDeltav2a4c1b * 0;
										ddDeltav2a4c2 = ddDeltav2a4c2 * 0;
										ddDeltav2a4b = ddDeltav2a4b * 0;
										ddDeltav2a4d = ddDeltav2a4d * ((xi.y-xj.y)/rij);
										break;
									case 2: ddDeltav1 = ddDeltav1 * ((xk.z-xj.z) * rij * rkj - (xij.dot(xkj)) * rkj * ((xi.z-xj.z)/rij));
										ddDeltav2b = ddDeltav2b * 2 * rij * ((xi.z-xj.z)/rij) * Math.pow(rkj,2);
										ddDeltav2a1a = ddDeltav2a1a * 0;
										ddDeltav2a1b = ddDeltav2a1b * 1;
										ddDeltav2a2 = ddDeltav2a2 * rkj * ((xi.z-xj.z)/rij);
										ddDeltav2a3 = ddDeltav2a3 * (xk.z-xj.z);
										ddDeltav2a4a1c = ddDeltav2a4a1c * 1;
										ddDeltav2a4a2 = ddDeltav2a4a2 * ((xi.z-xj.z)/rij);
										ddDeltav2a4c1c = ddDeltav2a4c1c * 0;
										ddDeltav2a4c2 = ddDeltav2a4c2 * 0;
										ddDeltav2a4b = ddDeltav2a4b * 0;
										ddDeltav2a4d = ddDeltav2a4d * ((xi.z-xj.z)/rij);
										break;
								}
							}
							if (angleAtomPosition[l][1] == atomNumbern) {

								switch (coordinaten) {
									case 0: ddDeltav1 = ddDeltav1 * ((2 * xj.x - xk.x - xi.x) * rij * rkj - (xij.dot(xkj)) * ((-(xi.x-xj.x)/rij) * rkj + (-(xk.x-xj.x)/rkj) * rij));
										ddDeltav2b = ddDeltav2b * (2 * rij * (-(xi.x-xj.x)/rij) * Math.pow(rkj,2) + Math.pow(rij,2) * 2 * rkj * (-(xk.x-xj.x)/rkj));
										ddDeltav2a1a = ddDeltav2a1a * -1;
										ddDeltav2a1b = ddDeltav2a1b * -1;
										ddDeltav2a2 = ddDeltav2a2 * ((-(xi.x-xj.x)/rij) * rkj + (-(xk.x-xj.x)/rkj) * rij);
										ddDeltav2a3 = ddDeltav2a3 * (2 * xj.x - xk.x - xi.x);
										ddDeltav2a4a1a = ddDeltav2a4a1a * (-1);
										ddDeltav2a4a2 = ddDeltav2a4a2 * (-(xi.x-xj.x)/rij);
										ddDeltav2a4c1a = ddDeltav2a4c1a * (-1);
										ddDeltav2a4c2 = ddDeltav2a4c2 * (-(xk.x-xj.x)/rkj);
										ddDeltav2a4b = ddDeltav2a4b * (-(xk.x-xj.x)/rkj);
										ddDeltav2a4d = ddDeltav2a4d * (-(xi.x-xj.x)/rij);
										break;
									case 1:	ddDeltav1 = ddDeltav1 * ((2 * xj.y - xk.y - xi.y) * rij * rkj - (xij.dot(xkj)) * ((-(xi.y-xj.y)/rij) * rkj + (-(xk.y-xj.y)/rkj) * rij));
										ddDeltav2b = ddDeltav2b * (2 * rij * (-(xi.y-xj.y)/rij) * Math.pow(rkj,2) + Math.pow(rij,2) * 2 * rkj * (-(xk.y-xj.y)/rkj));
										ddDeltav2a1a = ddDeltav2a1a * -1;
										ddDeltav2a1b = ddDeltav2a1b * -1;
										ddDeltav2a2 = ddDeltav2a2 * ((-(xi.y-xj.y)/rij) * rkj + (-(xk.y-xj.y)/rkj) * rij);
										ddDeltav2a3 = ddDeltav2a3 * (2 * xj.y - xk.y - xi.y);
										ddDeltav2a4a1b = ddDeltav2a4a1b * (-1);
										ddDeltav2a4a2 = ddDeltav2a4a2 * (-(xi.y-xj.y)/rij);
										ddDeltav2a4c1b = ddDeltav2a4c1b * (-1);
										ddDeltav2a4c2 = ddDeltav2a4c2 * (-(xk.y-xj.y)/rkj);
										ddDeltav2a4b = ddDeltav2a4b * (-(xk.y-xj.y)/rkj);
										ddDeltav2a4d = ddDeltav2a4d * (-(xi.y-xj.y)/rij);
										break;
									case 2: ddDeltav1 = ddDeltav1 * ((2 * xj.z - xk.z - xi.z) * rij * rkj - (xij.dot(xkj)) * ((-(xi.z-xj.z)/rij) * rkj + (-(xk.z-xj.z)/rkj) * rij));
										ddDeltav2b = ddDeltav2b * (2 * rij * (-(xi.z-xj.z)/rij) * Math.pow(rkj,2) + Math.pow(rij,2) * 2 * rkj * (-(xk.z-xj.z)/rkj));
										ddDeltav2a1a = ddDeltav2a1a * -1;
										ddDeltav2a1b = ddDeltav2a1b * -1;
										ddDeltav2a2 = ddDeltav2a2 * ((-(xi.z-xj.z)/rij) * rkj + (-(xk.z-xj.z)/rkj) * rij);
										ddDeltav2a3 = ddDeltav2a3 * (2 * xj.z - xk.z - xi.z);
										ddDeltav2a4a1c = ddDeltav2a4a1c * (-1);
										ddDeltav2a4a2 = ddDeltav2a4a2 * (-(xi.z-xj.z)/rij);
										ddDeltav2a4c1c = ddDeltav2a4c1c * (-1);
										ddDeltav2a4c2 = ddDeltav2a4c2 * (-(xk.z-xj.z)/rkj);
										ddDeltav2a4b = ddDeltav2a4b * (-(xk.z-xj.z)/rkj);
										ddDeltav2a4d = ddDeltav2a4d * (-(xi.z-xj.z)/rij);
										break;
								}
							}
							if (angleAtomPosition[l][2] == atomNumbern) {

								switch (coordinaten) {
									case 0: ddDeltav1 = ddDeltav1 * ((xi.x-xj.x) * rij * rkj - (xij.dot(xkj)) * rij * ((xk.x-xj.x)/rkj));
										ddDeltav2b = ddDeltav2b * Math.pow(rij,2) * 2 * rkj * ((xk.x-xj.x)/rkj);
										ddDeltav2a1a = ddDeltav2a1a * 1;
										ddDeltav2a1b = ddDeltav2a1b * 0;
										ddDeltav2a2 = ddDeltav2a2 * rij * ((xk.x-xj.x)/rkj);
										ddDeltav2a3 = ddDeltav2a3 * (xi.x-xj.x);
										ddDeltav2a4a1a = ddDeltav2a4a1a * 0;
										ddDeltav2a4a2 = ddDeltav2a4a2 * 0;
										ddDeltav2a4c1a = ddDeltav2a4c1a * 1;
										ddDeltav2a4c2 = ddDeltav2a4c2 * ((xk.x-xj.x)/rkj);
										ddDeltav2a4b = ddDeltav2a4b * ((xk.x-xj.x)/rkj);
										ddDeltav2a4d = ddDeltav2a4d * 0;
										break;
									case 1:	ddDeltav1 = ddDeltav1 * ((xi.y-xj.y) * rij * rkj - (xij.dot(xkj)) * rij * ((xk.y-xj.y)/rkj));
										ddDeltav2b = ddDeltav2b * Math.pow(rij,2) * 2 * rkj * ((xk.y-xj.y)/rkj);
										ddDeltav2a1a = ddDeltav2a1a * 1;
										ddDeltav2a1b = ddDeltav2a1b * 0;
										ddDeltav2a2 = ddDeltav2a2 * rij * ((xk.y-xj.y)/rkj);
										ddDeltav2a3 = ddDeltav2a3 * (xi.y-xj.y);
										ddDeltav2a4a1b = ddDeltav2a4a1b * 0;
										ddDeltav2a4a2 = ddDeltav2a4a2 * 0;
										ddDeltav2a4c1b = ddDeltav2a4c1b * 1;
										ddDeltav2a4c2 = ddDeltav2a4c2 * ((xk.y-xj.y)/rkj);
										ddDeltav2a4b = ddDeltav2a4b * ((xk.y-xj.y)/rkj);
										ddDeltav2a4d = ddDeltav2a4d * 0;
										break;
									case 2: ddDeltav1 = ddDeltav1 * ((xi.z-xj.z) * rij * rkj - (xij.dot(xkj)) * rij * ((xk.z-xj.z)/rkj));
										ddDeltav2b = ddDeltav2b * Math.pow(rij,2) * 2 * rkj * ((xk.z-xj.z)/rkj);
										ddDeltav2a1a = ddDeltav2a1a * 1;
										ddDeltav2a1b = ddDeltav2a1b * 0;
										ddDeltav2a2 = ddDeltav2a2 * rij * ((xk.z-xj.z)/rkj);
										ddDeltav2a3 = ddDeltav2a3 * (xi.z-xj.z);
										ddDeltav2a4a1c = ddDeltav2a4a1c * 0;
										ddDeltav2a4a2 = ddDeltav2a4a2 * 0;
										ddDeltav2a4c1c = ddDeltav2a4c1c * 1;
										ddDeltav2a4c2 = ddDeltav2a4c2 * ((xk.z-xj.z)/rkj);
										ddDeltav2a4b = ddDeltav2a4b * ((xk.z-xj.z)/rkj);
										ddDeltav2a4d = ddDeltav2a4d * 0;
										break;
								}
							}

							ddDeltav2a4a1 = (ddDeltav2a4a1a + ddDeltav2a4a1b + ddDeltav2a4a1c) * rij;
							ddDeltav2a4c1 = (ddDeltav2a4c1a + ddDeltav2a4c1b + ddDeltav2a4c1c) * rkj;
							ddDeltav2a4 = ddDeltav2a4 * (((ddDeltav2a4a1 - ddDeltav2a4a2) / Math.pow(rij,2)) * rkj + ddDeltav2a4b + ((ddDeltav2a4c1 + ddDeltav2a4c2) / Math.pow(rkj,2)) * rij + ddDeltav2a4d);
							ddDeltav2 = ddDeltav2 * (((ddDeltav2a1a + ddDeltav2a1b) * ddDeltav2a1 + ddDeltav2a2 - ddDeltav2a3 - ddDeltav2a4) * ddDeltav2a - ddDeltav2b);
							ddDeltav[n][m][l] = ddDeltav1 + ddDeltav2;
						}
					}
					else {
						ddDeltav[n][m][l] = 0;
					}
					//logger.debug("ddDeltav[" + n + "][" + m + "][" + l + "] = " + ddDeltav[n][m][l]);
				}
			}
		}
		
	}


	/**
	 *  Get the angle bending second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Delta angle bending second derivative value [dimension(3xN)] [angles Number]
	 */
	public double[][][] getAngleBendingSecondDerivative() {
		return ddDeltav;
	}


	/**
	 *  Evaluate a 2nd order approximation of the Hessian, for the angle bending,
	 *  given the atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setAngleBending2ndOrderErrorApproximateHessian(GVector coord3d) {
		angleBendingOrder2ndErrorApproximateHessian = new double[coord3d.getSize()][][];
		
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xminusSigma = new GVector(coord3d.getSize());
		GVector xplusSigma = new GVector(coord3d.getSize());
		double[][] gradientAtXminusSigma = null;
		double[][] gradientAtXplusSigma = null;
		
		for (int i = 0; i < coord3d.getSize(); i++) {
			xminusSigma.set(coord3d);
			xminusSigma.setElement(i,coord3d.getElement(i) - sigma);
			setAngleBending2ndOrderErrorApproximateGradient(xminusSigma);
			gradientAtXminusSigma = this.getAngleBending2ndOrderErrorApproximateGradient();
			xplusSigma.set(coord3d);
			xplusSigma.setElement(i,coord3d.getElement(i) + sigma);
			setAngleBending2ndOrderErrorApproximateGradient(xplusSigma);
			gradientAtXplusSigma = this.getAngleBending2ndOrderErrorApproximateGradient();
			angleBendingOrder2ndErrorApproximateHessian[i] = new double[coord3d.getSize()][];
			for (int j = 0; j < coord3d.getSize(); j++) {
				angleBendingOrder2ndErrorApproximateHessian[i][j] = new double[angleNumber];
				for (int k=0; k < angleNumber; k++) {
					angleBendingOrder2ndErrorApproximateHessian[i][j][k] = (gradientAtXplusSigma[j][k] - gradientAtXminusSigma[j][k]) / (2 * sigma);
				}
			}
		}
	}


	/**
	 *  Get the 2nd order error approximate Hessian for the angle bending.
	 *
	 *
	 *@return           Angle bending 2nd order error approximate Hessian values.
	 */
	public double[][][] getAngleBending2ndOrderErrorApproximateHessian() {
		return angleBendingOrder2ndErrorApproximateHessian;
	}


	/**
	 *  Evaluate the hessian for the angle bending.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEA(GVector coord3d) {

		double[] forHessian = new double[coord3d.getSize() * coord3d.getSize()];
		
		/*boolean[] sigmaChange = new boolean[coord3d.getSize()];
		for (int i=1; i < sigmaChange.length; i++) {sigmaChange[i] = false;}
		*/
		
		setDeltav(coord3d);
		setAngleBendingSecondDerivative(coord3d);
		
		double sumHessianEA = 0;

		int forHessianIndex;

		for (int n = 0; n < coord3d.getSize(); n++) {
			for (int m= 0; m < coord3d.getSize(); m++) {
				for (int l = 0; l < angleNumber; l++) {
					sumHessianEA = sumHessianEA + (2 * k2[l] + 6 * k3[l] * deltav[l]) * dDeltav[n][l] * dDeltav[m][l]
							+ (k2[l] * 2 * deltav[l] + k3[l] * 3 * Math.pow(deltav[l],2)) * ddDeltav[n][m][l];
				}

				forHessianIndex = n*coord3d.getSize()+m;
				forHessian[forHessianIndex] = sumHessianEA;
			}
		}
		
		/*for (int n = 0; n < forHessian.length; n++) {
			logger.debug(forHessian[n] + ", ");
			if (n % 6 == 5) {
				logger.debug("");
			}
		}*/
		
		hessianMMFF94SumEA = new GMatrix(coord3d.getSize(), coord3d.getSize(), forHessian);
		//logger.debug("hessianMMFF94SumEA : " + hessianMMFF94SumEA);
		
		NewtonRaphsonMethod nrm = new NewtonRaphsonMethod();
		nrm.hessianEigenValues(forHessian, coord3d.getSize());
		
	}


	/**
	 *  Get the hessian for the angle bending.
	 *
	 *@return        Hessian value of the angle bending term.
	 */
	public GMatrix getHessianMMFF94SumEA() {
		return hessianMMFF94SumEA;
	}


	/**
	 *  Evaluate a 2nd order approximation of the Hessian, for the angle bending energy term,
	 *  given the atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateHessianMMFF94SumEA(GVector coord3d) {
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
			setGradientMMFF94SumEA(xminusSigma);
			gradientAtXminusSigma.set(gradientMMFF94SumEA);
			xplusSigma.set(coord3d);
			xplusSigma.setElement(i,coord3d.getElement(i) + sigma);
			setGradientMMFF94SumEA(xplusSigma);
			gradientAtXplusSigma.set(gradientMMFF94SumEA);
			for (int j = 0; j < coord3d.getSize(); j++) {
				forHessianIndex = i*coord3d.getSize()+j;
				forOrder2ndErrorApproximateHessian[forHessianIndex] = (gradientAtXplusSigma.getElement(j) - gradientAtXminusSigma.getElement(j)) / (2 * sigma);
				//(functionMMFF94SumEA(xplusSigma) - 2 * fx + functionMMFF94SumEA(xminusSigma)) / Math.pow(sigma,2);
			}
		}
		
		order2ndErrorApproximateHessianMMFF94SumEA = new GMatrix(coord3d.getSize(), coord3d.getSize());
		order2ndErrorApproximateHessianMMFF94SumEA.set(forOrder2ndErrorApproximateHessian);
		//logger.debug("order2ndErrorApproximateHessianMMFF94SumEA : " + order2ndErrorApproximateHessianMMFF94SumEA);
	}


	/**
	 *  Get the 2nd order error approximate Hessian for the angle bending energy term.
	 *
	 *
	 *@return           Angle bending energy 2nd order error approximate Hessian value.
	 */
	public GMatrix get2ndOrderErrorApproximateHessianMMFF94SumEA() {
		return order2ndErrorApproximateHessianMMFF94SumEA;
	}

}

