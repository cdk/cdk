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

	double mmff94SumEA_InWishedCoordinates = 0;
	GVector gradientMMFF94SumEA_InWishedCoordinates = new GVector(3);
	GMatrix hessianMMFF94SumEA_InWishedCoordinates = new GMatrix(3,3);

	GVector dDeltav = new GVector(3);

	int angleNumber = 0;

	double[] v0 = null;
	double[] k2 = null;
	double[] k3 = null;
	double[] k4 = null;
	double cb = -0.007;
	double[] v = null;
	double[] deltav = null;


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
					}
				}
			}
		}

	}


	/**
	 *  Calculate the actual bond angles vijk and the difference with the reference angles.
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 */
	public void calculateDeltav(AtomContainer molecule) {

		v = new double[angleNumber];
		deltav = new double[angleNumber];
		
		Atom[] atomConnected = null;
		RDFProtonDescriptor rdfpdo = new RDFProtonDescriptor();
		int l=-1;
		
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			atomConnected = molecule.getConnectedAtoms(molecule.getAtomAt(i));
			if (atomConnected.length > 1) {
				for (int j = 0; j < atomConnected.length; j++) {
					for (int k = j+1; k < atomConnected.length; k++) {
						l += 1;
						Vector3d va = new Vector3d((Tuple3d) atomConnected[j].getPoint3d());
						Vector3d vb = new Vector3d((Tuple3d) molecule.getAtomAt(i).getPoint3d());
						Vector3d vc = new Vector3d((Tuple3d) atomConnected[k].getPoint3d());
						v[l] = rdfpdo.calculateAngleBetweenTwoLines(vb, vb, vc, vb);
						deltav[l] = v[l] - v0[l];
					}
				}
			}
		}
	}


	/**
	 *  Evaluate the MMFF94 angle bending term for the given atoms coordinates
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 *@return        MMFF94 angle bending term value
	 */
	public double functionMMFF94SumEA_InPoint(AtomContainer molecule) {
		calculateDeltav(molecule);
		mmff94SumEA_InWishedCoordinates = 0;
		for (int i = 0; i < angleNumber; i++) {
			mmff94SumEA_InWishedCoordinates = mmff94SumEA_InWishedCoordinates + k2[i] * Math.pow(deltav[i],2) 
											+ k3[i] * Math.pow(deltav[i],3);
		}
		//System.out.println("mmff94SumEA_InWishedCoordinates = " + mmff94SumEA_InWishedCoordinates);
		return mmff94SumEA_InWishedCoordinates;
	}


	/**
	 *  Evaluate the gradient of the angle bending term for a given atoms
	 *  coordinates
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 *@return           Angle bending gradient value
	 */
	public GVector gradientMMFF94SumEA_InPoint(AtomContainer molecule) {

		gradientMMFF94SumEA_InWishedCoordinates.setSize(molecule.getAtomCount() * 3);
		dDeltav.setSize(molecule.getAtomCount() * 3);
		double fordDeltav = 1;

		double sumGradientEA;
		for (int i = 0; i < gradientMMFF94SumEA_InWishedCoordinates.getSize(); i++) {

			dDeltav.setElement(i,1);                 // dDeltav : partial derivative of deltav. To change in the future
			sumGradientEA = 0;

			for (int j = 0; j < angleNumber; j++) {

				sumGradientEA = sumGradientEA + (k2[j] * 2 * deltav[j] + k3[j] * 3 * Math.pow(deltav[j],2)) * dDeltav.getElement(i);
			}
			
			gradientMMFF94SumEA_InWishedCoordinates.setElement(i, sumGradientEA);
		}

		//System.out.println("gradientMMFF94SumEA_InWishedCoordinates : " + gradientMMFF94SumEA_InWishedCoordinates);
		return gradientMMFF94SumEA_InWishedCoordinates;
	}


	/**
	 *  Evaluate the hessian for the angle bending.
	 *
	 *@param  point  Current coordinates
	 *@return        Hessian value of the angle bending term.
	 */
	public GMatrix hessianInPoint(GVector point) {

		double[] forHessian = new double[point.getSize() * point.getSize()];
		double sumHessianEA = 0;

		GMatrix ddDeltar = new GMatrix(point.getSize(),point.getSize());
		ddDeltar.setZero();

		for (int i = 0; i < forHessian.length; i++) {
			for (int j = 0; j < angleNumber; j++) {
				sumHessianEA = sumHessianEA + (2 * k2[j] + 6 * k3[j] * deltav[j]) * dDeltav.getElement(j) * dDeltav.getElement(0)
							+ (k2[j] * 2 * deltav[j] + k3[j] * 3 * Math.pow(deltav[j],2)) * ddDeltar.getElement(0,0);
			}
			forHessian[i] = sumHessianEA;
		}

		hessianMMFF94SumEA_InWishedCoordinates.setSize(point.getSize(), point.getSize());
		hessianMMFF94SumEA_InWishedCoordinates.set(forHessian); 
		//System.out.println("hessianMMFF94SumEA_InWishedCoordinates : " + hessianMMFF94SumEA_InWishedCoordinates);
		return hessianMMFF94SumEA_InWishedCoordinates;
	}

}

