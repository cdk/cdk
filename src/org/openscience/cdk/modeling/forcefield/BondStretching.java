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
	
	double mmff94SumEB = 0;
	GVector gradientMMFF94SumEB = new GVector(3);
	GMatrix hessianMMFF94SumEB = new GMatrix(3,3);

	int bondsNumber;
	int[][] bondAtomPosition = null;

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
	 *  each i-j bond in the molecule.
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94BondStretchingParameters(AtomContainer molecule, Hashtable parameterSet) throws Exception {

		Bond[] bonds = molecule.getBonds();
		bondsNumber = bonds.length;
		bondAtomPosition = new int[bondsNumber][];
		Atom[] atomsInBond = null;

		Vector bondData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initilize(parameterSet);

		r0 = new double[molecule.getBondCount()];
		k2 = new double[molecule.getBondCount()];
		k3 = new double[molecule.getBondCount()];
		k4 = new double[molecule.getBondCount()];

		for (int i = 0; i < bondsNumber; i++) {

			atomsInBond = bonds[i].getAtoms();
			bondAtomPosition[i] = new int[atomsInBond.length];
			
			for (int j = 0; j < atomsInBond.length; j++) {
				bondAtomPosition[i][j] = molecule.getAtomNumber(atomsInBond[j]);
			}
			
			//System.out.println("atomsInBond " + i + " : " + atomsInBond);
			bondData = pc.getBondData(atomsInBond[0].getID(), atomsInBond[1].getID());
			//System.out.println("bondData : " + bondData);
			r0[i] = ((Double) bondData.get(0)).doubleValue();
			k2[i] = ((Double) bondData.get(1)).doubleValue();
			k3[i] = ((Double) bondData.get(2)).doubleValue();
			k4[i] = ((Double) bondData.get(3)).doubleValue();
		}
		
		r = new double[bondsNumber];
		deltar = new double[bondsNumber];
		

	}


	/**
	 *  Calculate the actual bond distance rij and the difference with the reference bond distances.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void calculateDeltar(GVector coord3d) {

		//System.out.println("deltar.length = " + deltar.length);
		for (int i = 0; i < bondsNumber; i++) {
			r[i] = ffTools.distanceBetweenTwoAtomsFrom3xNCoordinates(coord3d, bondAtomPosition[i][0], bondAtomPosition[i][1]);
			deltar[i] = r[i] - r0[i];
		}
	}


	/**
	 *  Evaluate the MMFF94 bond stretching term for the given atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 *@return        bond stretching value
	 */
	public double functionMMFF94SumEB(GVector coord3d) {
		/*for (int i=0; i < bondsNumber; i++) {
			System.out.println("before:	deltar[" + i + "] = " + deltar[i]);
		}*/
		
		calculateDeltar(coord3d);

		/*for (int i=0; i < bondsNumber; i++) {
			System.out.println("after:	deltar[" + i + "] = " + deltar[i]);
		}*/
		
		mmff94SumEB = 0;

		for (int i = 0; i < bondsNumber; i++) {
			mmff94SumEB = mmff94SumEB + k2[i] * Math.pow(deltar[i],2) 
							+ k3[i] * Math.pow(deltar[i],3) + k4[i] * Math.pow(deltar[i],4);
		}

		return mmff94SumEB;
	}


	/**
	 *  Calculate the bond lengths first derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setBondLengthsFirstDerivative(GVector coord3d) {
		
		dDeltar = new double[coord3d.getSize()][];
		
		Double forAtomNumber = null;
		int atomNumber = 0;
		int coordinate;
		for (int i = 0; i < coord3d.getSize(); i++) {
			
			dDeltar[i] = new double[bondsNumber];
			
			forAtomNumber = new Double(i/3);
			coordinate = i % 3;
			//System.out.println("coordinate = " + coordinate);

			atomNumber = forAtomNumber.intValue();
			//System.out.println("atomNumber = " + atomNumber);

			for (int j = 0; j < bondsNumber; j++) {

				if ((bondAtomPosition[j][0] == atomNumber) | (bondAtomPosition[j][1] == atomNumber)) {
					switch (coordinate) {
						case 0: dDeltar[i][j] = (coord3d.getElement(3 * bondAtomPosition[j][0]) - coord3d.getElement(3 * bondAtomPosition[j][1]))
								/ Math.sqrt(Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0]) - coord3d.getElement(3 * bondAtomPosition[j][1]),2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 1) - coord3d.getElement(3 * bondAtomPosition[j][1] + 1),2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 2) - coord3d.getElement(3 * bondAtomPosition[j][1] + 2),2)); 
							break;
						case 1:	dDeltar[i][j] = (coord3d.getElement(3 * bondAtomPosition[j][0] + 1) - coord3d.getElement(3 * bondAtomPosition[j][1] + 1))
								/ Math.sqrt(Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0]) - coord3d.getElement(3 * bondAtomPosition[j][1]),2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 1) - coord3d.getElement(3 * bondAtomPosition[j][1] + 1),2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 2) - coord3d.getElement(3 * bondAtomPosition[j][1] + 2),2)); 
							break;
						case 2: dDeltar[i][j] = (coord3d.getElement(3 * bondAtomPosition[j][0] + 2) - coord3d.getElement(3 * bondAtomPosition[j][1] + 2))
								/ Math.sqrt(Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0]) - coord3d.getElement(3 * bondAtomPosition[j][1]),2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 1) - coord3d.getElement(3 * bondAtomPosition[j][1] + 1),2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 2) - coord3d.getElement(3 * bondAtomPosition[j][1] + 2),2)); 
							break;
					}
					if (bondAtomPosition[j][1] == atomNumber) {
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
	public double[][] getBondLengthsFirstDerivative() {
		return dDeltar;
	}


	/**
	 *  Evaluate the gradient for the bond stretching in a given atoms coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEB(GVector coord3d) {
		
		gradientMMFF94SumEB.setSize(coord3d.getSize());
		setBondLengthsFirstDerivative(coord3d);
		calculateDeltar(coord3d);
		
		double sumGradientEB;
		for (int i = 0; i < gradientMMFF94SumEB.getSize(); i++) {
			
			sumGradientEB = 0;
			for (int j = 0; j < bondsNumber; j++) {

				sumGradientEB = sumGradientEB + (k2[j] * 2 * deltar[j] + k3[j] * 3 * Math.pow(deltar[j],2) + k4[j] * 4 * Math.pow(deltar[j],3)) * dDeltar[i][j];
			}
			gradientMMFF94SumEB.setElement(i, sumGradientEB);
		}
	}


	/**
	 *  Get the gradient for the bond stretching in a given atoms coordinates
	 *
	 *@return           Bond stretching gradient value
	 */
	public GVector getGradientMMFF94SumEB() {
		return gradientMMFF94SumEB;
	}


	/**
	 *  Calculate the bond lengths second derivative respect to the cartesian coordinates of the atoms.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setBondLengthsSecondDerivative(GVector coord3d) {
		ddDeltar = new double[coord3d.getSize()][][];
		
		Double forAtomNumber = null;
		int atomNumberi;
		int atomNumberj;
		int coordinatei;
		int coordinatej;
		double ddDeltar1;
		double ddDeltar2;
		
		setBondLengthsFirstDerivative(coord3d);
		
		for (int i=0; i<coord3d.getSize(); i++) {
			ddDeltar[i] = new double[coord3d.getSize()][];
			
			forAtomNumber = new Double(i/3);
			coordinatei = i % 3;
			//System.out.println("coordinatei = " + coordinatei);
				
			atomNumberi = forAtomNumber.intValue();
			//System.out.println("atomNumberi = " + atomNumberi);
				
			for (int j=0; j<coord3d.getSize(); j++) {
				ddDeltar[i][j] = new double[bondsNumber];
				
				forAtomNumber = new Double(j/3);
				coordinatej = j % 3;
				//System.out.println("coordinatej = " + coordinatej);
				
				atomNumberj = forAtomNumber.intValue();
				//System.out.println("atomNumberj = " + atomNumberj);
				//System.out.println("atomj : " + molecule.getAtomAt(atomNumberj));
				
				for (int k=0; k < bondsNumber; k++) {
					
					if ((bondAtomPosition[k][0] == atomNumberj) | (bondAtomPosition[k][1] == atomNumberj)) {
						if ((bondAtomPosition[k][0] == atomNumberi) | (bondAtomPosition[k][1] == atomNumberi)) {
							ddDeltar1 = (-1) / Math.pow(r[k],3);
							ddDeltar2 = 1 / r[k];
							//System.out.println("OK: had d1 and have the atomNumberi");
					
							switch (coordinatej) {
								case 0: ddDeltar1 = (coord3d.getElement(3 * bondAtomPosition[k][0]) - coord3d.getElement(3 * bondAtomPosition[k][1])) * ddDeltar[i][j][k];
									//System.out.println("OK: d1 x");
									break;
								case 1:	ddDeltar1 = (coord3d.getElement(3 * bondAtomPosition[k][0] + 1) - coord3d.getElement(3 * bondAtomPosition[k][1] + 1)) * ddDeltar[i][j][k];
									//System.out.println("OK: d1 y");
									break;
								case 2:	ddDeltar1 = (coord3d.getElement(3 * bondAtomPosition[k][0] + 2) - coord3d.getElement(3 * bondAtomPosition[k][1] + 2)) * ddDeltar[i][j][k];
									//System.out.println("OK: d1 z");
									break;
							}
						
							if (bondAtomPosition[k][1] == atomNumberj) {
								ddDeltar1 = (-1) * ddDeltar1;
								ddDeltar2 = (-1) * ddDeltar2;
								//System.out.println("OK: bond 1");
							} 
	
							switch (coordinatei) {
								case 0: ddDeltar1 = ddDeltar1 * (coord3d.getElement(3 * bondAtomPosition[k][0]) - coord3d.getElement(3 * bondAtomPosition[k][1]));
									//System.out.println("OK: have d2 x");
									break;
								case 1:	ddDeltar1 = ddDeltar1 * (coord3d.getElement(3 * bondAtomPosition[k][0] + 1) - coord3d.getElement(3 * bondAtomPosition[k][1] + 1));
									//System.out.println("OK: have d2 y");
									break;
								case 2: ddDeltar1 = ddDeltar1 * (coord3d.getElement(3 * bondAtomPosition[k][0] + 2) - coord3d.getElement(3 * bondAtomPosition[k][1] + 2));
									//System.out.println("OK: have d2 z");
									break;
							}
							
							if (bondAtomPosition[k][1] == atomNumberi) {
								ddDeltar1 = (-1) * ddDeltar1;
								ddDeltar2 = (-1) * ddDeltar2;
								//System.out.println("OK: d2 bond 1");
							}
							
							ddDeltar[i][j][k] = ddDeltar1 + ddDeltar2;
						}
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
	 public double[][][] getBondLengthsSecondDerivative() {
		return ddDeltar;
	}


	/**
	 *  Evaluate the hessian for the bond stretching.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEB(GVector coord3d) {
		
		double[] forHessian = new double[coord3d.getSize() * coord3d.getSize()];
		setBondLengthsSecondDerivative(coord3d);
		calculateDeltar(coord3d);
		
		double sumHessianEB;
		int forHessianIndex;
		for (int i = 0; i < coord3d.getSize(); i++) {
			for (int j = 0; j < coord3d.getSize(); j++) {
				sumHessianEB = 0;
				for (int k = 0; k < bondsNumber; k++) {
					sumHessianEB = sumHessianEB + (2 * k2[k] + 6 * k3[k] * deltar[k] + 12 * k4[k] * Math.pow(deltar[k],2)) * dDeltar[i][k] * dDeltar[j][k]
								+ (k2[k] * 2 * deltar[k] + k3[k] * 3 * Math.pow(deltar[k],2) + k4[k] * 4 * Math.pow(deltar[k],3)) * ddDeltar[i][j][k];
				}
				forHessianIndex = i*coord3d.getSize()+j;
				forHessian[forHessianIndex] = sumHessianEB;
			}
		}

		hessianMMFF94SumEB.setSize(coord3d.getSize(), coord3d.getSize());
		hessianMMFF94SumEB.set(forHessian); 
		//System.out.println("hessianMMFF94SumEB : " + hessianMMFF94SumEB);
	}


	/**
	 *  Get the hessian for the bond stretching.
	 *
	 *@return        Hessian value of the bond stretching term.
	 */
	public GMatrix getHessianMMFF94SumEB() {
		return hessianMMFF94SumEB;
	}


}

