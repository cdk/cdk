package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;

import org.openscience.cdk.*;
import org.openscience.cdk.modeling.builder3d.*;

/**
 *  Torsions calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    March 2, 2005
 */
public class Torsions {

	String functionShape = " Torsions ";

	double mmff94SumET_InWishedCoordinates = 0;
	GVector gradientMMFF94SumET_InWishedCoordinates = new GVector(3);
	GMatrix hessianMMFF94SumET_InWishedCoordinates = new GMatrix(3,3);

	GVector dPhi = new GVector(3);
	
	int torsionNumber = 0;

	double[] v1 = null;
	double[] v2 = null;
	double[] v3 = null;
	double[] phi = null;
	
	Bond[] bonds = null;
	Atom[] atomsInBond = null;
	Bond[] bondsConnectedBefore = null;
	Bond[] bondsConnectedAfter = null;

	ForceFieldTools ffTools = new ForceFieldTools();


	/**
	 *  Constructor for the Torsions object
	 */
	public Torsions() { }


	/**
	 *  Set MMFF94 constants V1, V2 and V3 for each i-j, j-k and k-l bonded pairs in the molecule.
	 *
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94TorsionsParameters(AtomContainer molecule, Hashtable parameterSet) throws Exception {

		bonds = molecule.getBonds();
		for (int b=0; b<bonds.length; b++) {
			atomsInBond = bonds[b].getAtoms();
			bondsConnectedBefore = molecule.getConnectedBonds(atomsInBond[0]);
			if (bondsConnectedBefore.length > 1) {
				bondsConnectedAfter = molecule.getConnectedBonds(atomsInBond[1]);
				if (bondsConnectedAfter.length > 1) {
					for (int bb=0; bb<bondsConnectedBefore.length; bb++) {
						if (bondsConnectedBefore[bb].compare(bonds[b])) {}
						else {
							for (int ba=0; ba<bondsConnectedAfter.length; ba++) {
								if (bondsConnectedAfter[ba].compare(bonds[b])) {}
								else {
									torsionNumber += 1;
									//System.out.println("atomi : " + bondsConnectedBefore[bb].getConnectedAtom(atomsInBond[0]).getID());
									//System.out.println("atomj : " + atomsInBond[0].getID());
									//System.out.println("atomk : " + atomsInBond[1].getID());
									//System.out.println("atoml : " + bondsConnectedAfter[ba].getConnectedAtom(atomsInBond[1]).getID());
								}
							}
						}
					}
				}
			}
		}
		//System.out.println("torsionNumber = " + torsionNumber);

		v1 = new double[torsionNumber];
		v2 = new double[torsionNumber];
		v3 = new double[torsionNumber];

		Vector torsionsData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initilize(parameterSet);
		
		int m = -1;
		for (int b=0; b<bonds.length; b++) {
			atomsInBond = bonds[b].getAtoms();
			bondsConnectedBefore = molecule.getConnectedBonds(atomsInBond[0]);
			if (bondsConnectedBefore.length > 1) {
				bondsConnectedAfter = molecule.getConnectedBonds(atomsInBond[1]);
				if (bondsConnectedAfter.length > 1) {
					for (int bb=0; bb<bondsConnectedBefore.length; bb++) {
						if (bondsConnectedBefore[bb].compare(bonds[b])) {}
						else {
							for (int ba=0; ba<bondsConnectedAfter.length; ba++) {
								if (bondsConnectedAfter[ba].compare(bonds[b])) {}
								else {
									torsionsData = (Vector)parameterSet.get("torsion" + bondsConnectedBefore[bb].getConnectedAtom(atomsInBond[0]).getID() + ";" 
																+ atomsInBond[0].getID() + ";" 
																+ atomsInBond[1].getID() + ";" 
																+ bondsConnectedAfter[ba].getConnectedAtom(atomsInBond[1]).getID());
									//System.out.println("torsionsData : " + torsionsData);
									m += 1;
									v1[m] = ((Double) torsionsData.get(0)).doubleValue();
									v2[m] = ((Double) torsionsData.get(1)).doubleValue();
									v3[m] = ((Double) torsionsData.get(2)).doubleValue();
								}
							}
						}
					}
				}
			}
		}

	}


	/**
	 *  Calculate the actual phi
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 */
	public void setPhi(AtomContainer molecule) {

		phi = new double[torsionNumber];
		
		int m=-1;
		for (int b=0; b<bonds.length; b++) {
			atomsInBond = bonds[b].getAtoms();
			bondsConnectedBefore = molecule.getConnectedBonds(atomsInBond[0]);
			if (bondsConnectedBefore.length > 1) {
				bondsConnectedAfter = molecule.getConnectedBonds(atomsInBond[1]);
				if (bondsConnectedAfter.length > 1) {
					for (int bb=0; bb<bondsConnectedBefore.length; bb++) {
						if (bondsConnectedBefore[bb].compare(bonds[b])) {}
						else {
							for (int ba=0; ba<bondsConnectedAfter.length; ba++) {
								if (bondsConnectedAfter[ba].compare(bonds[b])) {}
								else {
									m += 1;
									phi[m] = ffTools.torsionAngle(bondsConnectedBefore[bb].getConnectedAtom(atomsInBond[0]), 
											atomsInBond[0], atomsInBond[1], bondsConnectedAfter[ba].getConnectedAtom(atomsInBond[1]));
									//System.out.println("phi : " + phi[m]);	
								}
							}
						}
					}
				}
			}
		}
	}


	/**
	 *  Evaluate the MMFF94 torsions term.
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 *@return        MMFF94 torsions term value.
	 */
	public double functionMMFF94SumET_InPoint(AtomContainer molecule) {
		setPhi(molecule);
		mmff94SumET_InWishedCoordinates = 0;
		for (int m = 0; m < torsionNumber; m++) {
			mmff94SumET_InWishedCoordinates = mmff94SumET_InWishedCoordinates + 0.5 * (v1[m] * (1 + Math.cos(phi[m])) + v2[m] * (1 - Math.cos(2 * phi[m])) + v3[m] * (1 + Math.cos(3 * phi[m])));
		}
		//System.out.println("mmff94SumET_InWishedCoordinates = " + mmff94SumET_InWishedCoordinates);
		return mmff94SumET_InWishedCoordinates;
	}


	/**
	 *  Evaluate the gradient of the torsions term. 
	 *  
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 *@return           torsions gradient value.
	 */
	public GVector gradientMMFF94SumET_InPoint(AtomContainer molecule) {

		gradientMMFF94SumET_InWishedCoordinates.setSize(molecule.getAtomCount() * 3);
		dPhi.setSize(molecule.getAtomCount() * 3);

		double sumGradientET;
		for (int i = 0; i < gradientMMFF94SumET_InWishedCoordinates.getSize(); i++) {

			sumGradientET = 0;
			dPhi.setElement(i,1);                 // dPhi : partial derivative of phi. To change in the future

			for (int m = 0; m < torsionNumber; m++) {

				sumGradientET = sumGradientET - v1[m] * Math.sin(phi[m]) * dPhi.getElement(i) + 
					v2[m] * Math.sin(2 * phi[m]) * 2 * dPhi.getElement(i) - 
					v3[m] * Math.sin(3 * phi[m]) * 3 * dPhi.getElement(i);
			}
			sumGradientET = sumGradientET * 0.5;
			
			gradientMMFF94SumET_InWishedCoordinates.setElement(i, sumGradientET);
		}
		//System.out.println("gradientMMFF94SumET_InWishedCoordinates = " + gradientMMFF94SumET_InWishedCoordinates);
		return gradientMMFF94SumET_InWishedCoordinates;
	}


	/**
	 *  Evaluate the hessian of the torsions.
	 *
	 *@param  point  Current coordinates.
	 *@return        Hessian value of the torsions term.
	 */
	public GMatrix hessianInPoint(GVector point) {

		double[] forHessian = new double[point.getSize() * point.getSize()];
		double[] ddPhi = new double[point.getSize() * point.getSize()];
		
		double sumHessianET = 0;
		for (int i = 0; i < point.getSize(); i++) {
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

		hessianMMFF94SumET_InWishedCoordinates.setSize(point.getSize(), point.getSize());
		hessianMMFF94SumET_InWishedCoordinates.set(forHessian); 
		//System.out.println("hessianMMFF94SumET_InWishedCoordinates : " + hessianMMFF94SumET_InWishedCoordinates);
		return hessianMMFF94SumET_InWishedCoordinates;
	}

}

