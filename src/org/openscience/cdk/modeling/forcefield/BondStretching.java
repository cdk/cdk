package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;

import org.openscience.cdk.*;
import org.openscience.cdk.modeling.builder3d.*;
import org.openscience.cdk.geometry.GeometryTools;

/**
 *  Bond Stretching calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    January 27, 2005
 */
public class BondStretching {

	String functionShape = " Bond Stretching ";
	
	ForceFieldTools ffTools = new ForceFieldTools();
	
	double mmff94SumEB_InWishedCoordinates = 0;
	GVector gradientMMFF94SumEB_InWishedCoordinates = new GVector(3);
	GMatrix hessianMMFF94SumEB_InWishedCoordinates = new GMatrix(3,3);

	Bond[] bonds = null;	// Bonds in the molecule

	double[] r0 = null;	// Force field parameters
	double[] k2 = null;
	double[] k3 = null;
	double[] k4 = null;
	double cs = -2;
	
	double[] r = null;	// The actual bond lengths
	double[] deltar = null;	// The difference between actual and reference bond lengths
	
	GVector dDeltar = new GVector(3);


	/**
	 *  Constructor for the BondStretching object
	 */
	public BondStretching() { }


	/**
	 *  Set MMFF94 reference bond lengths r0IJ and the constants k2, k3, k4 for
	 *  each i-j bond in a molecule.
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94BondStretchingParameters(AtomContainer molecule, Hashtable parameterSet) throws Exception {

		bonds = molecule.getBonds();
		Atom[] atomsInBond = null;

		Vector bondData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initilize(parameterSet);

		r0 = new double[molecule.getBondCount()];
		k2 = new double[molecule.getBondCount()];
		k3 = new double[molecule.getBondCount()];
		k4 = new double[molecule.getBondCount()];

		for (int i = 0; i < bonds.length; i++) {

			atomsInBond = bonds[i].getAtoms();
			//System.out.println("atomsInBond " + i + " : " + atomsInBond);
			bondData = pc.getBondData(atomsInBond[0].getID(), atomsInBond[1].getID());
			//System.out.println("bondData : " + bondData);
			r0[i] = ((Double) bondData.get(0)).doubleValue();
			k2[i] = ((Double) bondData.get(1)).doubleValue();
			k3[i] = ((Double) bondData.get(2)).doubleValue();
			k4[i] = ((Double) bondData.get(3)).doubleValue();
		}
	}


	/**
	 *  Calculate the actual bond distance rij and the difference with the reference bond distances.
	 *
	 *@param  point  Current coordinates
	 */
	public void calculateDeltar(AtomContainer molecule) {

		bonds = molecule.getBonds();
		Atom[] atomsInBond = null;
		
		r = new double[bonds.length];
		deltar = new double[r.length];
		
		for (int i = 0; i < bonds.length; i++) {

			atomsInBond = bonds[i].getAtoms();
			r[i] = ffTools.distanceBetweenTwoAtoms(atomsInBond[0], atomsInBond[1]);
			deltar[i] = r[i] - r0[i];
		}
	}


	/**
	 *  Evaluate the MMFF94 bond stretching term for the given atoms coordinates
	 *
	 *@param  point  Current coordinates
	 *@return        bond stretching value
	 */
	public double functionMMFF94SumEB_InPoint(AtomContainer molecule) {

		calculateDeltar(molecule);

		mmff94SumEB_InWishedCoordinates = 0;

		for (int i = 0; i < bonds.length; i++) {
			mmff94SumEB_InWishedCoordinates = mmff94SumEB_InWishedCoordinates + k2[i] * Math.pow(deltar[i],2) 
							+ k3[i] * Math.pow(deltar[i],3) + k4[i] * Math.pow(deltar[i],4);
		}

		return mmff94SumEB_InWishedCoordinates;
	}


	/**
	 *  Evaluate the gradient for the bond stretching in a given atoms coordinates
	 *
	 *@param  molecule  Description of the Parameter
	 */
	public void setGradientMMFF94SumEB_InPoint(AtomContainer molecule) {
		
		GVector point = new GVector(ffTools.getCoordinates3xNVector(molecule)); 
		
		gradientMMFF94SumEB_InWishedCoordinates.setSize(point.getSize());
		dDeltar.setSize(point.getSize());
		
		Atom[] atomsInBond = null;
		Double forAtomNumber = null;
		int atomNumber = 0;
		double sumGradientEB;
		for (int i = 0; i < gradientMMFF94SumEB_InWishedCoordinates.getSize(); i++) {
			
			forAtomNumber = new Double(i/3);
			int coordinate = i % 3;
			//System.out.println("coordinate = " + coordinate);

			atomNumber = forAtomNumber.intValue();
			//System.out.println("atomNumber = " + atomNumber);
			//System.out.println("atom : " + molecule.getAtomAt(atomNumber));

			sumGradientEB = 0;
			for (int j = 0; j < bonds.length; j++) {

				atomsInBond = bonds[j].getAtoms();
				//System.out.println("atomsInBond = " + atomsInBond);
				//System.out.println("atomsInBond[0] : " + atomsInBond[0]);
				//System.out.println("atomsInBond[1] : " + atomsInBond[1]);
				if (molecule.getAtomNumber(atomsInBond[0]) == atomNumber) {
					switch (coordinate) {
						case 0: //System.out.println("00");
							dDeltar.setElement(i,(2 * (atomsInBond[0].getX3d() - atomsInBond[1].getX3d()))
								/2 * Math.sqrt(Math.pow(atomsInBond[0].getX3d() - atomsInBond[1].getX3d(),2) + Math.pow(atomsInBond[0].getY3d() - atomsInBond[1].getY3d(),2) + Math.pow(atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d(),2))); 
							//System.out.println("OK");
							break;
						case 1:	//System.out.println("01");
							dDeltar.setElement(i,(2 * (atomsInBond[0].getY3d() - atomsInBond[1].getY3d()))
								/2 * Math.sqrt(Math.pow(atomsInBond[0].getX3d() - atomsInBond[1].getX3d(),2) + Math.pow(atomsInBond[0].getY3d() - atomsInBond[1].getY3d(),2) + Math.pow(atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d(),2))); 
							//System.out.println("OK");
							break;
						case 2: //System.out.println("02");
							dDeltar.setElement(i,(2 * (atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d()))
								/2 * Math.sqrt(Math.pow(atomsInBond[0].getX3d() - atomsInBond[1].getX3d(),2) + Math.pow(atomsInBond[0].getY3d() - atomsInBond[1].getY3d(),2) + Math.pow(atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d(),2))); 
							//System.out.println("OK");
							break;
					}		
				} else if (molecule.getAtomNumber(atomsInBond[1]) == atomNumber) {
						switch (coordinate) {
							case 0: //System.out.println("10");
								dDeltar.setElement(i,(2 * (atomsInBond[0].getX3d() - atomsInBond[1].getX3d()) * (-1))
									/2 * Math.sqrt(Math.pow(atomsInBond[0].getX3d() - atomsInBond[1].getX3d(),2) + Math.pow(atomsInBond[0].getY3d() - atomsInBond[1].getY3d(),2) + Math.pow(atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d(),2))); 
								//System.out.println("OK");
								break;
							case 1:	//System.out.println("11");
								dDeltar.setElement(i,(2 * (atomsInBond[0].getY3d() - atomsInBond[1].getY3d()) * (-1))
									/2 * Math.sqrt(Math.pow(atomsInBond[0].getX3d() - atomsInBond[1].getX3d(),2) + Math.pow(atomsInBond[0].getY3d() - atomsInBond[1].getY3d(),2) + Math.pow(atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d(),2))); 
								//System.out.println("OK");
								break;
							case 2: //System.out.println("12");
								dDeltar.setElement(i,(2 * (atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d()) * (-1))
									/2 * Math.sqrt(Math.pow(atomsInBond[0].getX3d() - atomsInBond[1].getX3d(),2) + Math.pow(atomsInBond[0].getY3d() - atomsInBond[1].getY3d(),2) + Math.pow(atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d(),2))); 
								//System.out.println("OK");
								break;
						}
				} else {
					dDeltar.setElement(i,0);
					//System.out.println("OK");
				}
				//System.out.println("bond " + j + " : " + "dDeltar.getElement(" + i + ") = " + dDeltar.getElement(i));
				
				sumGradientEB = sumGradientEB + (k2[j] * 2 * deltar[j] + k3[j] * 3 * Math.pow(deltar[j],2) + k4[j] * 4 * Math.pow(deltar[j],3)) * dDeltar.getElement(i);
			}
			gradientMMFF94SumEB_InWishedCoordinates.setElement(i, sumGradientEB);
		}
	}


	/**
	 *  Get the gradient for the bond stretching in a given atoms coordinates
	 *
	 *@return           Bond stretching gradient value
	 */
	public GVector getGradientMMFF94SumEB_InWishedCoordinates() {
		return gradientMMFF94SumEB_InWishedCoordinates;
	}


	/**
	 *  Evaluate the hessian for the bond stretching.
	 *
	 *@param  point  Current coordinates
	 *@return        Hessian value of the bond stretching term.
	 */
	public GMatrix hessianInPoint(AtomContainer molecule) {
		
		GVector point = new GVector(ffTools.getCoordinates3xNVector(molecule)); 
		
		double[] forHessian = new double[point.getSize() * point.getSize()];
		double sumHessianEB = 0;
		
		GMatrix ddDeltar = new GMatrix(point.getSize(),point.getSize());
		ddDeltar.setZero();

		for (int i = 0; i < forHessian.length; i++) {
			for (int j = 0; j < bonds.length; j++) {
				sumHessianEB = sumHessianEB + (2 * k2[j] + 6 * k3[j] * deltar[j] + 12 * k4[j] * Math.pow(deltar[j],2)) * dDeltar.getElement(0) * dDeltar.getElement(0) 
							+ (k2[j] * 2 * deltar[j] + k3[j] * 3 * Math.pow(deltar[j],2) + k4[j] * 4 * Math.pow(deltar[j],3)) * ddDeltar.getElement(0,0);
			}
			forHessian[i] = sumHessianEB;
		}

		hessianMMFF94SumEB_InWishedCoordinates.setSize(point.getSize(), point.getSize());
		hessianMMFF94SumEB_InWishedCoordinates.set(forHessian); 
		//System.out.println("hessianMMFF94SumEB_InWishedCoordinates : " + hessianMMFF94SumEB_InWishedCoordinates);
		return hessianMMFF94SumEB_InWishedCoordinates;
	}

}

