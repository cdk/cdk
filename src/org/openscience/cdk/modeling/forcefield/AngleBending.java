package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;

import org.openscience.cdk.*;
import org.openscience.cdk.modeling.builder3d.*;
import org.openscience.cdk.qsar.RDFProtonDescriptor;

/**
 *  Angle bending calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    February 8, 2005
 */
public class AngleBending {

	String functionShape = " Angle bending ";

	ForceFieldTools ffTools = new ForceFieldTools();

	double mmff94SumEA = 0;
	GVector gradientMMFF94SumEA = new GVector(3);
	GMatrix hessianMMFF94SumEA = new GMatrix(3,3);

	int angleNumber = 0;
	int[][] angleAtomPosition = null;

	double[] v0 = null;
	double[] k2 = null;
	double[] k3 = null;
	double[] k4 = null;
	double cb = -0.007;
	double[] v = null;
	double[] deltav = null;

	double[][] dDeltav = null;
	double[][][] ddDeltav = null;


	/**
	 *  Constructor for the AngleBending object
	 */
	public AngleBending() { }


	/**
	 *  Set MMFF94 reference angle v0IJK and the constants k2, k3, k4 for each
	 *  i-j-k angle in the molecule.
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94AngleBendingParameters(AtomContainer molecule, Hashtable parameterSet) throws Exception {

		Atom[] atomConnected = null;
		
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			atomConnected = molecule.getConnectedAtoms(molecule.getAtomAt(i));
			if (atomConnected.length > 1) {
				for (int j = 0; j < atomConnected.length; j++) {
					for (int k = j+1; k < atomConnected.length; k++) {
						angleNumber += 1;
					}
				}
			}
		}
		//System.out.println("angleNumber = " + angleNumber);

		Vector angleData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initilize(parameterSet);
		
		v0 = new double[angleNumber];
		k2 = new double[angleNumber];
		k3 = new double[angleNumber];
		k4 = new double[angleNumber];
		
		angleAtomPosition = new int[angleNumber][];

		int l = -1;
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			atomConnected = molecule.getConnectedAtoms(molecule.getAtomAt(i));
			if (atomConnected.length > 1) {
				for (int j = 0; j < atomConnected.length; j++) {
					for (int k = j+1; k < atomConnected.length; k++) {
						angleData = pc.getAngleData(atomConnected[j].getID(), molecule.getAtomAt(i).getID(), atomConnected[k].getID());
						//System.out.println("angleData : " + angleData);
						l += 1;
						v0[l] = ((Double) angleData.get(0)).doubleValue();
						k2[l] = ((Double) angleData.get(1)).doubleValue();
						k3[l] = ((Double) angleData.get(2)).doubleValue();
						//k4[l] = ((Double) angleData.get(3)).doubleValue();

						//System.out.println("v0[" + l + "] = " + v0[l]);
						//System.out.println("k2[" + l + "] = " + k2[l]);
						//System.out.println("k3[" + l + "] = " + k3[l]);
						//System.out.println("k4[" + l + "] = " + k4[l]);
						
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


	}


	/**
	 *  Calculate the actual bond angles vijk and the difference with the reference angles.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void calculateDeltav(GVector coord3d) {
		
		for (int i = 0; i < angleNumber; i++) {
			v[i] = ffTools.angleBetweenTwoBondsFrom3xNCoordinates(coord3d,angleAtomPosition[i][0],angleAtomPosition[i][1],angleAtomPosition[i][2]);
			deltav[i] = v[i] - v0[i];
		}
	}


	/**
	 *  Evaluate the MMFF94 angle bending term for the given atoms coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 *@return        MMFF94 angle bending term value
	 */
	public double functionMMFF94SumEA(GVector coord3d) {
		calculateDeltav(coord3d);
		mmff94SumEA = 0;
		for (int i = 0; i < angleNumber; i++) {
			mmff94SumEA = mmff94SumEA + k2[i] * Math.pow(deltav[i],2) 
											+ k3[i] * Math.pow(deltav[i],3);
		}
		//System.out.println("mmff94SumEA = " + mmff94SumEA);
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

		for (int m = 0; m < coord3d.getSize(); m++) {
			
			dDeltav[m] = new double[angleNumber];
			
			forAtomNumber = new Double(m/3);
			coordinate = m % 3;
			//System.out.println("coordinate = " + coordinate);

			atomNumber = forAtomNumber.intValue();
			//System.out.println("atomNumber = " + atomNumber);

			for (int l = 0; l < angleNumber; l++) {
				
				if ((angleAtomPosition[l][0] == atomNumber) | (angleAtomPosition[l][1] == atomNumber) | (angleAtomPosition[l][2] == atomNumber)) {

					Point3d xi = new Point3d(coord3d.getElement(3 * angleAtomPosition[l][0]), coord3d.getElement(3 * angleAtomPosition[l][0] + 1),coord3d.getElement( 3 * angleAtomPosition[l][0] + 2));
					//System.out.println("xi = " + xi);
					Point3d xj = new Point3d(coord3d.getElement(3 * angleAtomPosition[l][1]), coord3d.getElement(3 * angleAtomPosition[l][1] + 1),coord3d.getElement( 3 * angleAtomPosition[l][1] + 2));
					//System.out.println("xj = " + xj);
					Point3d xk = new Point3d(coord3d.getElement(3 * angleAtomPosition[l][2]), coord3d.getElement(3 * angleAtomPosition[l][2] + 1),coord3d.getElement( 3 * angleAtomPosition[l][2] + 2));
					//System.out.println("xk = " + xk);
				
					Vector3d xij = new Vector3d();
					xij.sub(xi,xj);
					//System.out.println("xij = " + xij);
					Vector3d xkj = new Vector3d();
					xkj.sub(xk,xj);
					//System.out.println("xkj = " + xkj);
					
					double rij = xi.distance(xj);
					//System.out.println("rij = " + rij);
					double rkj = xk.distance(xj);
					//System.out.println("rkj = " + rkj);

					dDeltav[m][l] = (-1/Math.sqrt(1-Math.pow(xij.dot(xkj)/(rij * rkj),2))) * (1/(Math.pow(rij,2) * Math.pow(rkj,2))); 

					if (angleAtomPosition[l][0] == atomNumber) {

						switch (coordinate) {
							case 0: dDeltav[m][l] = dDeltav[m][l] * ((xk.x-xj.x) * rij * rkj - (xij.dot(xkj)) * rkj * (1/rij));
								break;
							case 1:	dDeltav[m][l] = dDeltav[m][l] * ((xk.y-xj.y) * rij * rkj - (xij.dot(xkj)) * rkj * (1/rij));
								break;
							case 2: dDeltav[m][l] = dDeltav[m][l] * ((xk.z-xj.z) * rij * rkj - (xij.dot(xkj)) * rkj * (1/rij));
								break;
						}
					}
					if (angleAtomPosition[l][1] == atomNumber) {

						switch (coordinate) {
							case 0: dDeltav[m][l] = dDeltav[m][l] * ((2 * xj.x - xk.x - xi.x) * rij * rkj - (xij.dot(xkj)) * ((-1/rij) * rkj + (-1/rkj) * rij));
								break;
							case 1:	dDeltav[m][l] = dDeltav[m][l] * ((2 * xj.y - xk.y - xi.y) * rij * rkj - (xij.dot(xkj)) * ((-1/rij) * rkj + (-1/rkj) * rij));
								break;
							case 2: dDeltav[m][l] = dDeltav[m][l] * ((2 * xj.z - xk.z - xi.z) * rij * rkj - (xij.dot(xkj)) * ((-1/rij) * rkj + (-1/rkj) * rij));
								break;
						}
					}
					if (angleAtomPosition[l][2] == atomNumber) {

						switch (coordinate) {
							case 0: dDeltav[m][l] = dDeltav[m][l] * ((xi.x-xj.x) * rij * rkj - (xij.dot(xkj)) * rij * (1/rkj));
								break;
							case 1:	dDeltav[m][l] = dDeltav[m][l] * ((xi.y-xj.y) * rij * rkj - (xij.dot(xkj)) * rij * (1/rkj));
								break;
							case 2: dDeltav[m][l] = dDeltav[m][l] * ((xi.z-xj.z) * rij * rkj - (xij.dot(xkj)) * rij * (1/rkj));
								break;
						}
					}

				}
				else {
					dDeltav[m][l] = 0;
				}
				//System.out.println("dDeltav[" + m + "][" + l + "] = " + dDeltav[m][l]);
			}
		}
		
	}


	/**
	 *  Get the bond lengths derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Delta angle bending first derivative value [dimension(3xN)] [angles Number]
	 */
	public double[][] getAngleBendingFirstDerivative() {
		return dDeltav;
	}


	/**
	 *  Evaluate the gradient of the angle bending term for a given atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEA(GVector coord3d) {
		gradientMMFF94SumEA.setSize(coord3d.getSize());
		
		setAngleBendingFirstDerivative(coord3d);
		
		double sumGradientEA;
		for (int m = 0; m < gradientMMFF94SumEA.getSize(); m++) {

			sumGradientEA = 0;
			for (int l = 0; l < angleNumber; l++) {

				sumGradientEA = sumGradientEA + (k2[l] * 2 * deltav[l] + k3[l] * 3 * Math.pow(deltav[l],2)) * dDeltav[m][l];
			}
			
			gradientMMFF94SumEA.setElement(m, sumGradientEA);
		}

		//System.out.println("gradientMMFF94SumEA : " + gradientMMFF94SumEA);
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
	 *  Evaluate the hessian for the angle bending.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEA(GVector coord3d) {

		double[] forHessian = new double[coord3d.getSize() * coord3d.getSize()];
		double sumHessianEA = 0;

		GMatrix ddDeltar = new GMatrix(coord3d.getSize(),coord3d.getSize());
		ddDeltar.setZero();

		int forHessianIndex;
		for (int i = 0; i < coord3d.getSize(); i++) {
			for (int j = 0; j < coord3d.getSize(); j++) {
				for (int k = 0; k < angleNumber; k++) {
					sumHessianEA = sumHessianEA + (2 * k2[k] + 6 * k3[k] * deltav[k]) * dDeltav[i][k] * dDeltav[j][k]
							+ (k2[k] * 2 * deltav[k] + k3[k] * 3 * Math.pow(deltav[k],2)) * ddDeltar.getElement(0,0);
				}
				forHessianIndex = i*coord3d.getSize()+j;
				forHessian[forHessianIndex] = sumHessianEA;
			}
		}

		hessianMMFF94SumEA.setSize(coord3d.getSize(), coord3d.getSize());
		hessianMMFF94SumEA.set(forHessian); 
		//System.out.println("hessianMMFF94SumEA : " + hessianMMFF94SumEA);
	}


	/**
	 *  Get the hessian for the angle bending.
	 *
	 *@return        Hessian value of the angle bending term.
	 */
	public GMatrix getHessianMMFF94SumEA() {
		return hessianMMFF94SumEA;
	}

}

