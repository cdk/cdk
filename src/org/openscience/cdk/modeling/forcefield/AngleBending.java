package org.openscience.cdk.modeling.forcefield;

import java.lang.Math;
import java.lang.Double;
import java.lang.Exception;
import java.lang.String;
import java.util.Vector;
import java.util.Hashtable;
import javax.vecmath.*;
import Jama.*;
import org.openscience.cdk.*;
import org.openscience.cdk.modeling.builder3d.*;

/**
 *  Angle bending calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    February 8, 2005
 *@cdk.module     builder3d
 */
public class AngleBending {

	String functionShape = " Angle bending ";

	double mmff94SumEA = 0;
	GVector gradientMMFF94SumEA = new GVector(3);
	GMatrix hessianMMFF94SumEA = new GMatrix(3,3);

	double[][] dDeltav = null;
	double[][][] ddDeltav = null;

	int angleNumber = 0;
	int[][] angleAtomPosition = null;

	double[] v0 = null;
	double[] k2 = null;
	double[] k3 = null;
	double[] k4 = null;
	double cb = -0.007;
	double[] v = null;
	double[] deltav = null;

	ForceFieldTools ffTools = new ForceFieldTools();


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
		
		//System.out.println("deltav.length = " + deltav.length);
		for (int i = 0; i < angleNumber; i++) {
			v[i] = ffTools.angleBetweenTwoBondsFrom3xNCoordinates(coord3d,angleAtomPosition[i][0],angleAtomPosition[i][1],angleAtomPosition[i][2]);
			//System.out.println("v[" + i + "] = " + v[i]);
			deltav[i] = v[i] - v0[i];
			//System.out.println("deltav[" + i + "]= " + deltav[i]);
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
			//System.out.println("k2[" + i + "]= " + k2[i]);
			//System.out.println("k3[" + i + "]= " + k3[i]);
			//System.out.println("deltav[" + i + "]= " + deltav[i]);

			mmff94SumEA = mmff94SumEA + k2[i] * Math.pow(deltav[i],2) 
						+ k3[i] * Math.pow(deltav[i],3);
			
			//System.out.println("mmff94SumEA = " + mmff94SumEA);
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

		for (int m = 0; m < dDeltav.length; m++) {
			
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
					
					double rji = xj.distance(xi);
					//System.out.println("rji = " + rji);
					double rjk = xj.distance(xk);
					//System.out.println("rji = " + rjk);

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
				//System.out.println("dDeltav[" + m + "][" + l + "] = " + dDeltav[m][l]);
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
	 *  Evaluate the gradient of the angle bending term for a given atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEA(GVector coord3d) {
		gradientMMFF94SumEA.setSize(coord3d.getSize());
		calculateDeltav(coord3d);
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
			//System.out.println("coordinaten = " + coordinaten);
				
			atomNumbern = forAtomNumber.intValue();
			//System.out.println("atomNumbern = " + atomNumbern);
				
			for (int m = 0; m < coord3d.getSize(); m++) {
			
				ddDeltav[n][m] = new double[angleNumber];
			
				forAtomNumber = new Double(m/3);
				coordinatem = m % 3;
				//System.out.println("coordinatem = " + coordinatem);

				atomNumberm = forAtomNumber.intValue();
				//System.out.println("atomNumberm = " + atomNumberm);

				for (int l = 0; l < angleNumber; l++) {
				
					if ((angleAtomPosition[l][0] == atomNumberm) | (angleAtomPosition[l][1] == atomNumberm) | (angleAtomPosition[l][2] == atomNumberm)) {
						if ((angleAtomPosition[l][0] == atomNumbern) | (angleAtomPosition[l][1] == atomNumbern) | (angleAtomPosition[l][2] == atomNumbern)) {

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

							//System.out.println("OK: had d1 and have the atomNumbern");
						
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
					//System.out.println("ddDeltav[" + n + "][" + m + "][" + l + "] = " + ddDeltav[n][m][l]);
				}
			}
		}
		
	}


	/**
	 *  Get the bond lengths second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Delta angle bending second derivative value [dimension(3xN)] [angles Number]
	 */
	public double[][][] getAngleBendingSecondDerivative() {
		return ddDeltav;
	}


	/**
	 *  Evaluate the hessian for the angle bending.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEA(GVector coord3d) {


		double[] forHessian = new double[coord3d.getSize() * coord3d.getSize()];
		
		calculateDeltav(coord3d);
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
			System.out.print(forHessian[n] + ", ");
			if (n % 6 == 5) {
				System.out.println("");
			}
		}*/
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

