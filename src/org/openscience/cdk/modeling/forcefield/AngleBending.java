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
	 *@param  point  Current molecule coordinates.
	 */
	public void calculateDeltav(GVector point) {
		
		for (int i = 0; i < angleNumber; i++) {
			v[i] = ffTools.angleBetweenTwoBondsFrom3xNCoordinates(point,angleAtomPosition[i][0],angleAtomPosition[i][1],angleAtomPosition[i][2]);
			deltav[i] = v[i] - v0[i];
		}
	}


	/**
	 *  Evaluate the MMFF94 angle bending term for the given atoms coordinates
	 *
	 *@param  point  Current molecule coordinates.
	 *@return        MMFF94 angle bending term value
	 */
	public double functionMMFF94SumEA(GVector point) {
		calculateDeltav(point);
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
	 *@param  point  Current molecule coordinates.
	 */
	public void setAngleBendingFirstDerivative(GVector point) {
		
		dDeltav = new double[point.getSize()][];
		
		for (int i = 0; i < point.getSize(); i++) {
			
			dDeltav[i] = new double[angleNumber];
			
			for (int j = 0; j < angleNumber; j++) {
				dDeltav[i][j] = 1;
			}
		}
			
		/*Atom[] atomsInBond = null;
		Double forAtomNumber = null;
		int atomNumber = 0;
		int coordinate;
		for (int i = 0; i < point.getSize(); i++) {
			
			dDeltar[i] = new double[bonds.length];
			
			forAtomNumber = new Double(i/3);
			coordinate = i % 3;
			//System.out.println("coordinate = " + coordinate);

			atomNumber = forAtomNumber.intValue();
			//System.out.println("atomNumber = " + atomNumber);
			//System.out.println("atom : " + molecule.getAtomAt(atomNumber));

			for (int j = 0; j < bonds.length; j++) {

				atomsInBond = bonds[j].getAtoms();
				//System.out.println("atomsInBond[0] : " + atomsInBond[0].toString());
				//System.out.println("atomsInBond[1] : " + atomsInBond[1].toString());
				if ((molecule.getAtomNumber(atomsInBond[0]) == atomNumber) | (molecule.getAtomNumber(atomsInBond[1]) == atomNumber)) {
					switch (coordinate) {
						case 0: dDeltar[i][j] = (atomsInBond[0].getX3d() - atomsInBond[1].getX3d())
								/ Math.sqrt(Math.pow(atomsInBond[0].getX3d() - atomsInBond[1].getX3d(),2) + Math.pow(atomsInBond[0].getY3d() - atomsInBond[1].getY3d(),2) + Math.pow(atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d(),2)); 
							break;
						case 1:	dDeltar[i][j] = (atomsInBond[0].getY3d() - atomsInBond[1].getY3d())
								/ Math.sqrt(Math.pow(atomsInBond[0].getX3d() - atomsInBond[1].getX3d(),2) + Math.pow(atomsInBond[0].getY3d() - atomsInBond[1].getY3d(),2) + Math.pow(atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d(),2)); 
							break;
						case 2: dDeltar[i][j] = (atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d())
								/ Math.sqrt(Math.pow(atomsInBond[0].getX3d() - atomsInBond[1].getX3d(),2) + Math.pow(atomsInBond[0].getY3d() - atomsInBond[1].getY3d(),2) + Math.pow(atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d(),2)); 
							break;
					}
					if (molecule.getAtomNumber(atomsInBond[1]) == atomNumber) {
						dDeltar[i][j] = (-1) * dDeltar[i][j];
					}
				} else {
					dDeltar[i][j] = 0;
				}
				//System.out.println("angle " + j + " : " + "dDeltar[" + i + "][" + j + "] = " + dDeltar[i][j]);
			}
		}*/
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
	 *@param  point  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEA(GVector point) {
		gradientMMFF94SumEA.setSize(point.getSize());
		
		setAngleBendingFirstDerivative(point);
		
		double sumGradientEA;
		for (int i = 0; i < gradientMMFF94SumEA.getSize(); i++) {

			sumGradientEA = 0;
			for (int j = 0; j < angleNumber; j++) {

				sumGradientEA = sumGradientEA + (k2[j] * 2 * deltav[j] + k3[j] * 3 * Math.pow(deltav[j],2)) * dDeltav[i][j];
			}
			
			gradientMMFF94SumEA.setElement(i, sumGradientEA);
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
	 *@param  point  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEA(GVector point) {

		double[] forHessian = new double[point.getSize() * point.getSize()];
		double sumHessianEA = 0;

		GMatrix ddDeltar = new GMatrix(point.getSize(),point.getSize());
		ddDeltar.setZero();

		int forHessianIndex;
		for (int i = 0; i < point.getSize(); i++) {
			for (int j = 0; j < point.getSize(); j++) {
				for (int k = 0; k < angleNumber; k++) {
					sumHessianEA = sumHessianEA + (2 * k2[k] + 6 * k3[k] * deltav[k]) * dDeltav[i][k] * dDeltav[j][k]
							+ (k2[k] * 2 * deltav[k] + k3[k] * 3 * Math.pow(deltav[k],2)) * ddDeltar.getElement(0,0);
				}
				forHessianIndex = i*point.getSize()+j;
				forHessian[forHessianIndex] = sumHessianEA;
			}
		}

		hessianMMFF94SumEA.setSize(point.getSize(), point.getSize());
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

