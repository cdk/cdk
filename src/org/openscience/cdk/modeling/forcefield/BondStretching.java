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
	
	double[][] dDeltar = null;
	double[][][] ddDeltar = null;


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
	 *@param  molecule       The molecule like an AtomContainer object.
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
	 *@param  molecule       The molecule like an AtomContainer object.
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
	 *  Calculate the bond lengths first derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@return        Delta bond lengths derivative value [dimension(3xN)] [bonds Number]
	 */
	public void setBondLengthsFirstDerivative_InPoint(AtomContainer molecule) {
		
		GVector point = new GVector(ffTools.getCoordinates3xNVector(molecule));
		
		dDeltar = new double[point.getSize()][];
		
		Atom[] atomsInBond = null;
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
				//System.out.println("bond " + j + " : " + "dDeltar[" + i + "][" + j + "] = " + dDeltar[i][j]);
			}
		}
	}


	/**
	 *  Get the bond lengths derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Delta bond lengths derivative value [dimension(3xN)] [bonds Number]
	 */
	public double[][] getBondLengthsFirstDerivative_InPoint() {
		return dDeltar;
	}


	/**
	 *  Evaluate the gradient for the bond stretching in a given atoms coordinates
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 */
	public void setGradientMMFF94SumEB_InPoint(AtomContainer molecule) {
		
		GVector point = new GVector(ffTools.getCoordinates3xNVector(molecule)); 
		gradientMMFF94SumEB_InWishedCoordinates.setSize(point.getSize());
		
		setBondLengthsFirstDerivative_InPoint(molecule);
		
		double sumGradientEB;
		for (int i = 0; i < gradientMMFF94SumEB_InWishedCoordinates.getSize(); i++) {
			
			sumGradientEB = 0;
			for (int j = 0; j < bonds.length; j++) {

				sumGradientEB = sumGradientEB + (k2[j] * 2 * deltar[j] + k3[j] * 3 * Math.pow(deltar[j],2) + k4[j] * 4 * Math.pow(deltar[j],3)) * dDeltar[i][j];
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
	 *  Calculate the bond lengths second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 */
	public void setBondLengthsSecondDerivative_InPoint(AtomContainer molecule) {
		GVector point = new GVector(ffTools.getCoordinates3xNVector(molecule));
		ddDeltar = new double[point.getSize()][][];
		
		Atom[] atomsInBond = null;
		Double forAtomNumber = null;
		int atomNumberi;
		int atomNumberj;
		int coordinatei;
		int coordinatej;
		double ddDeltar1;
		double ddDeltar2;
		
		if (dDeltar == null) {
			setBondLengthsFirstDerivative_InPoint(molecule);
		}
		
		for (int i=0; i<point.getSize(); i++) {
			ddDeltar[i] = new double[point.getSize()][];
			
			forAtomNumber = new Double(i/3);
			coordinatei = i % 3;
			//System.out.println("coordinatei = " + coordinatei);
				
			atomNumberi = forAtomNumber.intValue();
			//System.out.println("atomNumberi = " + atomNumberi);
			//System.out.println("atomi : " + molecule.getAtomAt(atomNumberi));
				
			for (int j=0; j<point.getSize(); j++) {
				ddDeltar[i][j] = new double[bonds.length];
				
				forAtomNumber = new Double(j/3);
				coordinatej = j % 3;
				//System.out.println("coordinatej = " + coordinatej);
				
				atomNumberj = forAtomNumber.intValue();
				//System.out.println("atomNumberj = " + atomNumberj);
				//System.out.println("atomj : " + molecule.getAtomAt(atomNumberj));
				
				for (int k=0; k < bonds.length; k++) {
					atomsInBond = bonds[k].getAtoms();
					//System.out.println("atomsInBond[0] : " + atomsInBond[0].toString());
					//System.out.println("atomsInBond[1] : " + atomsInBond[1].toString());
					
					if ((molecule.getAtomNumber(atomsInBond[0]) == atomNumberj) | (molecule.getAtomNumber(atomsInBond[1]) == atomNumberj)) {
						//System.out.println("r[" + k + "] = " + r[k]);
						ddDeltar1 = (-1) / Math.pow(r[k],3);
						ddDeltar2 = 1 / r[k];
						//System.out.println("OK: had d1");
						
						switch (coordinatej) {
							case 0: ddDeltar1 = (atomsInBond[0].getX3d() - atomsInBond[1].getX3d()) * ddDeltar[i][j][k];
								//System.out.println("OK: d1 x");
								break;
							case 1:	ddDeltar1 = (atomsInBond[0].getY3d() - atomsInBond[1].getY3d()) * ddDeltar[i][j][k];
								//System.out.println("OK: d1 y");
								break;
							case 2:	ddDeltar1 = (atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d()) * ddDeltar[i][j][k];
								//System.out.println("OK: d1 z");
								break;
						}
						
						if (molecule.getAtomNumber(atomsInBond[1]) == atomNumberj) {
								ddDeltar1 = (-1) * ddDeltar1;
								ddDeltar2 = (-1) * ddDeltar2;
								//System.out.println("OK: bond 1");
						} 
	
						if ((molecule.getAtomNumber(atomsInBond[0]) == atomNumberi) | (molecule.getAtomNumber(atomsInBond[1]) == atomNumberi)) {
							switch (coordinatei) {
								case 0: ddDeltar1 = ddDeltar1 * (atomsInBond[0].getX3d() - atomsInBond[1].getX3d());
									//System.out.println("OK: have d2 x");
									break;
								case 1:	ddDeltar1 = ddDeltar1 * (atomsInBond[0].getY3d() - atomsInBond[1].getY3d());
									//System.out.println("OK: have d2 y");
									break;
								case 2: ddDeltar1 = ddDeltar1 * (atomsInBond[0].getZ3d() - atomsInBond[1].getZ3d());
									//System.out.println("OK: have d2 z");
									break;
							}
							if (molecule.getAtomNumber(atomsInBond[1]) == atomNumberi) {
								ddDeltar1 = (-1) * ddDeltar1;
								ddDeltar2 = (-1) * ddDeltar2;
								//System.out.println("OK: d2 bond 1");
							}
						}
						ddDeltar[i][j][k] = ddDeltar1 + ddDeltar2;
					} else {
						ddDeltar[i][j][k] = 0;
						//System.out.println("OK: 0");
					}
					//System.out.println("bond " + k + " : " + "ddDeltar[" + i + "][" + j + "][" + k + "] = " + ddDeltar[i][j][k]);
				}
			}
		}	
	}


	/**
	 *  Get the bond lengths second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@return        Bond lengths second derivative value [dimension(3xN)] [bonds Number]
	 */
	 public double[][][] getBondLengthsSecondDerivative_InPoint() {
		return ddDeltar;
	}


	/**
	 *  Evaluate the hessian for the bond stretching.
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 */
	public void setHessianInPoint(AtomContainer molecule) {
		
		GVector point = new GVector(ffTools.getCoordinates3xNVector(molecule)); 
		
		double[] forHessian = new double[point.getSize() * point.getSize()];
		
		setBondLengthsSecondDerivative_InPoint(molecule);
		
		double sumHessianEB;
		int forHessianIndex;
		for (int i = 0; i < point.getSize(); i++) {
			for (int j = 0; j < point.getSize(); j++) {
				sumHessianEB = 0;
				for (int k = 0; k < bonds.length; k++) {
					sumHessianEB = sumHessianEB + (2 * k2[k] + 6 * k3[k] * deltar[k] + 12 * k4[k] * Math.pow(deltar[k],2)) * dDeltar[i][k] * dDeltar[j][k]
								+ (k2[k] * 2 * deltar[k] + k3[k] * 3 * Math.pow(deltar[k],2) + k4[k] * 4 * Math.pow(deltar[k],3)) * ddDeltar[i][j][k];
				}
				forHessianIndex = i*point.getSize()+j;
				forHessian[forHessianIndex] = sumHessianEB;
			}
		}

		hessianMMFF94SumEB_InWishedCoordinates.setSize(point.getSize(), point.getSize());
		hessianMMFF94SumEB_InWishedCoordinates.set(forHessian); 
		//System.out.println("hessianMMFF94SumEB_InWishedCoordinates : " + hessianMMFF94SumEB_InWishedCoordinates);
	}


	/**
	 *  Get the hessian for the bond stretching.
	 *
	 *@return        Hessian value of the bond stretching term.
	 */
	public GMatrix getHessianInPoint() {
		return hessianMMFF94SumEB_InWishedCoordinates;
	}


}

