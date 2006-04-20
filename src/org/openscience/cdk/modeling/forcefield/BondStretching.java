package org.openscience.cdk.modeling.forcefield;

import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.modeling.builder3d.MMFF94ParametersCall;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  Bond Stretching calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    January 27, 2005
 *@cdk.module     forcefield
 */
public class BondStretching {

	String functionShape = " Bond Stretching ";
	
	ForceFieldTools ffTools = new ForceFieldTools();
	
	double mmff94SumEB = 0;
	GVector gradientMMFF94SumEB = null;
	GMatrix hessianMMFF94SumEB = null;
	double[] forHessian = null;
	GMatrix order2ndErrorApproximateHessianMMFF94SumEB = null;
	double[] forOrder2ndErrorApproximateHessian = null;

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

	private LoggingTool logger;


	/**
	 *  Constructor for the BondStretching object
	 */
	public BondStretching() {        
		logger = new LoggingTool(this);
	}


	/**
	 *  Set MMFF94 reference bond lengths r0IJ and the constants k2, k3, k4 for
	 *  each i-j bond in the molecule.
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94BondStretchingParameters(AtomContainer molecule, Hashtable parameterSet) throws Exception {

		//System.out.println("molecule.getAtomAt(0).getAtomTypeName() = " + molecule.getAtomAt(0).getAtomTypeName());
		IBond[] bonds = molecule.getBonds();
		bondsNumber = bonds.length;
		//System.out.println("bondsNumber = " + bondsNumber);
		bondAtomPosition = new int[bondsNumber][];
		IAtom[] atomsInBond = null;

		Vector bondData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initialize(parameterSet);

		r0 = new double[molecule.getBondCount()];
		k2 = new double[molecule.getBondCount()];
		k3 = new double[molecule.getBondCount()];
		k4 = new double[molecule.getBondCount()];

		for (int i = 0; i < bondsNumber; i++) {
			//System.out.println("bonds[" + i + "]= " + bonds[i].toString());
			atomsInBond = bonds[i].getAtoms();
			bondAtomPosition[i] = new int[atomsInBond.length];
			
			for (int j = 0; j < atomsInBond.length; j++) {
				bondAtomPosition[i][j] = molecule.getAtomNumber(atomsInBond[j]);
				//System.out.println("atomsInBond[j] = " + atomsInBond[j].toString());
				//System.out.println("atomsInBond[j].getAtomTypeName() = " + atomsInBond[j].getAtomTypeName());
				//System.out.println("bondAtomPosition[i][j] = " + bondAtomPosition[i][j]);
			}
			
			//System.out.println("atomsInBond[0].getAtomTypeName() = " + atomsInBond[0].getAtomTypeName());
			//System.out.println("atomsInBond[1].getAtomTypeName() = " + atomsInBond[1].getAtomTypeName());
			bondData = pc.getBondData(atomsInBond[0].getAtomTypeName(), atomsInBond[1].getAtomTypeName());
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

		//logger.debug("deltar.length = " + deltar.length);
		for (int i = 0; i < bondsNumber; i++) {
			r[i] = ffTools.distanceBetweenTwoAtomsFrom3xNCoordinates(coord3d, bondAtomPosition[i][0], bondAtomPosition[i][1]);
			deltar[i] = r[i] - r0[i];
			//if (deltar[i] > 0) {
			//	deltar[i] = (-1) * deltar[i];
			//}
		}
	}


	/**
	 *  Evaluate the MMFF94 bond stretching term for the given atoms cartesian coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 *@return        bond stretching value
	 */
	public double functionMMFF94SumEB(GVector coord3d) {
		/*for (int i=0; i < bondsNumber; i++) {
			logger.debug("before:	deltar[" + i + "] = " + deltar[i]);
		}*/
		
		calculateDeltar(coord3d);

		/*for (int i=0; i < bondsNumber; i++) {
			logger.debug("after:	deltar[" + i + "] = " + deltar[i]);
		}*/
		
		mmff94SumEB = 0;

		for (int i = 0; i < bondsNumber; i++) {
			mmff94SumEB = mmff94SumEB + k2[i] * Math.pow(deltar[i],2) 
							+ k3[i] * Math.pow(deltar[i],3) + k4[i] * Math.pow(deltar[i],4);
		}

		return mmff94SumEB;
	}


	/**
	 *  Set the bond lengths first derivative respect to the cartesian
	 *  coordinates of the atoms.
	 *
	 *@param  coord3d           Current molecule coordinates.
	 *@param  deltar           Difference between the current bonds and the reference bonds.
	 *@param  bondAtomPosition  Position of the bending atoms in the atoms coordinates (0:N, N: atoms number).
	 */
	public void setBondLengthsFirstDerivative(GVector coord3d, double[] deltar, int[][] bondAtomPosition) {

		dDeltar = new double[coord3d.getSize()][];

		Double forAtomNumber = null;
		int atomNumber = 0;
		int coordinate;
		for (int i = 0; i < dDeltar.length; i++) {

			dDeltar[i] = new double[deltar.length];

			forAtomNumber = new Double(i / 3);
			coordinate = i % 3;
			//logger.debug("coordinate = " + coordinate);

			atomNumber = forAtomNumber.intValue();
			//logger.debug("atomNumber = " + atomNumber);

			for (int j = 0; j < deltar.length; j++) {

				if ((bondAtomPosition[j][0] == atomNumber) | (bondAtomPosition[j][1] == atomNumber)) {
					switch (coordinate) {
									//x-coordinate
									case 0:
										dDeltar[i][j] = (coord3d.getElement(3 * bondAtomPosition[j][0]) - coord3d.getElement(3 * bondAtomPosition[j][1]))
												 / Math.sqrt(Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0]) - coord3d.getElement(3 * bondAtomPosition[j][1]), 2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 1) - coord3d.getElement(3 * bondAtomPosition[j][1] + 1), 2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 2) - coord3d.getElement(3 * bondAtomPosition[j][1] + 2), 2));
										break;
									//y-coordinate
									case 1:
										dDeltar[i][j] = (coord3d.getElement(3 * bondAtomPosition[j][0] + 1) - coord3d.getElement(3 * bondAtomPosition[j][1] + 1))
												 / Math.sqrt(Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0]) - coord3d.getElement(3 * bondAtomPosition[j][1]), 2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 1) - coord3d.getElement(3 * bondAtomPosition[j][1] + 1), 2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 2) - coord3d.getElement(3 * bondAtomPosition[j][1] + 2), 2));
										break;
									//z-coordinate
									case 2:
										dDeltar[i][j] = (coord3d.getElement(3 * bondAtomPosition[j][0] + 2) - coord3d.getElement(3 * bondAtomPosition[j][1] + 2))
												 / Math.sqrt(Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0]) - coord3d.getElement(3 * bondAtomPosition[j][1]), 2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 1) - coord3d.getElement(3 * bondAtomPosition[j][1] + 1), 2) + Math.pow(coord3d.getElement(3 * bondAtomPosition[j][0] + 2) - coord3d.getElement(3 * bondAtomPosition[j][1] + 2), 2));
										break;
					}
					if (bondAtomPosition[j][1] == atomNumber) {
						dDeltar[i][j] = (-1) * dDeltar[i][j];
					}
				} else {
					dDeltar[i][j] = 0;
				}
				//logger.debug("bond " + j + " : " + "dDeltar[" + i + "][" + j + "] = " + dDeltar[i][j]);
			}
		}
	}


	/**
	 *  Get the bond lengths first derivative respect to the cartesian coordinates of the
	 *  atoms.
	 *
	 *@return    Delta bond lengths derivative value [dimension(3xN)] [bonds Number]
	 *      
	 */
	public double[][] getBondLengthsFirstDerivative() {
		return dDeltar;
	}


	/**
	 *  Evaluate the first order partial derivative for the bond stretching given the atoms coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEB(GVector coord3d) {
		
		gradientMMFF94SumEB = new GVector(coord3d.getSize());
		
		calculateDeltar(coord3d);
		setBondLengthsFirstDerivative(coord3d, deltar, bondAtomPosition);
		
		double sumGradientEB;
		for (int i = 0; i < gradientMMFF94SumEB.getSize(); i++) {
			
			sumGradientEB = 0;
			for (int j = 0; j < bondsNumber; j++) {
				//logger.debug("dDeltar = " + dDeltar[i][j]);
				//logger.debug("gradient " + i + "bond " + j + " : " + (k2[j] * 2 * deltar[j] + k3[j] * 3 * Math.pow(deltar[j],2) + k4[j] * 4 * Math.pow(deltar[j],3)) * dDeltar[i][j]);
				sumGradientEB = sumGradientEB + (k2[j] * 2 * deltar[j] + k3[j] * 3 * Math.pow(deltar[j],2) + k4[j] * 4 * Math.pow(deltar[j],3)) * dDeltar[i][j];
				//logger.debug(sumGradientEB);
			}
			//sumGradientEB = (-1) * sumGradientEB;
			gradientMMFF94SumEB.setElement(i, sumGradientEB);
		}
		//logger.debug("gradientMMFF94 = " + gradientMMFF94SumEB);
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
	public void setBondLengthsSecondDerivative(GVector coord3d, double[] deltar, int[][] bondAtomPosition) {
		ddDeltar = new double[coord3d.getSize()][][];
		
		Double forAtomNumber = null;
		int atomNumberi;
		int atomNumberj;
		int coordinatei;
		int coordinatej;
		double ddDeltar1=0;	// ddDeltar[i][j][k] = ddDeltar1 - ddDeltar2
		double ddDeltar2=0;
		
		setBondLengthsFirstDerivative(coord3d, deltar, bondAtomPosition);
		//logger.debug("bondAtomPosition.length = " + bondAtomPosition.length);
		double[] rTemp = new double[bondAtomPosition.length];
		for (int i = 0; i < bondAtomPosition.length; i++) {
			rTemp[i] = ffTools.distanceBetweenTwoAtomsFrom3xNCoordinates(coord3d, bondAtomPosition[i][0], bondAtomPosition[i][1]);
		}
		
		for (int i=0; i<coord3d.getSize(); i++) {
			ddDeltar[i] = new double[coord3d.getSize()][];
			
			forAtomNumber = new Double(i/3);
			
			atomNumberi = forAtomNumber.intValue();
			//logger.debug("atomNumberi = " + atomNumberi);
				
			coordinatei = i % 3;
			//logger.debug("coordinatei = " + coordinatei);
				
			for (int j=0; j<coord3d.getSize(); j++) {
				ddDeltar[i][j] = new double[deltar.length];
				
				forAtomNumber = new Double(j/3);

				atomNumberj = forAtomNumber.intValue();
				//logger.debug("atomNumberj = " + atomNumberj);

				coordinatej = j % 3;
				//logger.debug("coordinatej = " + coordinatej);
				
				//logger.debug("atomj : " + molecule.getAtomAt(atomNumberj));
				
				for (int k=0; k < deltar.length; k++) {
					
					if ((bondAtomPosition[k][0] == atomNumberj) | (bondAtomPosition[k][1] == atomNumberj)) {
						if ((bondAtomPosition[k][0] == atomNumberi) | (bondAtomPosition[k][1] == atomNumberi)) {
					
							// ddDeltar1
							if (bondAtomPosition[k][0] == atomNumberj) {
								ddDeltar1 = 1;
							}
							if (bondAtomPosition[k][1] == atomNumberj) {
								ddDeltar1 = -1;
							}
							if (bondAtomPosition[k][0] == atomNumberi) {
								ddDeltar1 = ddDeltar1 * 1;
							}
							if (bondAtomPosition[k][1] == atomNumberi) {
								ddDeltar1 = ddDeltar1 * (-1);
							}
							ddDeltar1 = ddDeltar1 / rTemp[k];

							// ddDeltar2
							switch (coordinatej) {
								case 0: ddDeltar2 = (coord3d.getElement(3 * bondAtomPosition[k][0]) - coord3d.getElement(3 * bondAtomPosition[k][1]));
									//logger.debug("OK: d1 x");
									break;
								case 1:	ddDeltar2 = (coord3d.getElement(3 * bondAtomPosition[k][0] + 1) - coord3d.getElement(3 * bondAtomPosition[k][1] + 1));
									//logger.debug("OK: d1 y");
									break;
								case 2:	ddDeltar2 = (coord3d.getElement(3 * bondAtomPosition[k][0] + 2) - coord3d.getElement(3 * bondAtomPosition[k][1] + 2));
									//logger.debug("OK: d1 z");
									break;
							}
						
							if (bondAtomPosition[k][1] == atomNumberj) {
								ddDeltar2 = (-1) * ddDeltar2;
								//logger.debug("OK: bond 1");
							} 
	
							switch (coordinatei) {
								case 0: ddDeltar2 = ddDeltar2 * (coord3d.getElement(3 * bondAtomPosition[k][0]) - coord3d.getElement(3 * bondAtomPosition[k][1]));
									//logger.debug("OK: have d2 x");
									break;
								case 1:	ddDeltar2 = ddDeltar2 * (coord3d.getElement(3 * bondAtomPosition[k][0] + 1) - coord3d.getElement(3 * bondAtomPosition[k][1] + 1));
									//logger.debug("OK: have d2 y");
									break;
								case 2: ddDeltar2 = ddDeltar2 * (coord3d.getElement(3 * bondAtomPosition[k][0] + 2) - coord3d.getElement(3 * bondAtomPosition[k][1] + 2));
									//logger.debug("OK: have d2 z");
									break;
							}
							
							if (bondAtomPosition[k][1] == atomNumberi) {
								ddDeltar2 = (-1) * ddDeltar2;
								//logger.debug("OK: d2 bond 1");
							}
							
							ddDeltar2 = ddDeltar2 / Math.pow(rTemp[k],2);
							
							// ddDeltar[i][j][k]
							ddDeltar[i][j][k] = ddDeltar1 - ddDeltar2;
						} else {
							ddDeltar[i][j][k] = 0;
							//logger.debug("OK: 0");
						}
					} else {
						ddDeltar[i][j][k] = 0;
						//logger.debug("OK: 0");
					}
					//logger.debug("bond " + k + " : " + "ddDeltar[" + i + "][" + j + "][" + k + "] = " + ddDeltar[i][j][k]);
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
	 *  Evaluate the second order partial derivative (hessian) for the bond stretching given the atoms coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEB(GVector coord3d) {
		
		forHessian = new double[coord3d.getSize() * coord3d.getSize()];
		
		calculateDeltar(coord3d);
		setBondLengthsSecondDerivative(coord3d, deltar, bondAtomPosition);
		
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

		hessianMMFF94SumEB = new GMatrix(coord3d.getSize(), coord3d.getSize(),forHessian);
		//logger.debug("hessianMMFF94SumEB : " + hessianMMFF94SumEB);
	}


	/**
	 *  Get the hessian for the bond stretching.
	 *
	 *@return        Hessian value of the bond stretching term.
	 */
	public GMatrix getHessianMMFF94SumEB() {
		return hessianMMFF94SumEB;
	}


	/**
	 *  Get the hessian for the bond stretching.
	 *
	 *@return        Hessian value of the bond stretching term.
	 */
	public double[] getForHessianMMFF94SumEB() {
		return forHessian;
	}


	/**
	 *  Evaluate a 2nd order approximation of the Hessian, for the bond stretching energy term,
	 *  given the atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateHessianMMFF94SumEB(GVector coord3d) {
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
			setGradientMMFF94SumEB(xminusSigma);
			gradientAtXminusSigma.set(gradientMMFF94SumEB);
			xplusSigma.set(coord3d);
			xplusSigma.setElement(i,coord3d.getElement(i) + sigma);
			setGradientMMFF94SumEB(xplusSigma);
			gradientAtXplusSigma.set(gradientMMFF94SumEB);
			for (int j = 0; j < coord3d.getSize(); j++) {
				forHessianIndex = i*coord3d.getSize()+j;
				forOrder2ndErrorApproximateHessian[forHessianIndex] = (gradientAtXplusSigma.getElement(j) - gradientAtXminusSigma.getElement(j)) / (2 * sigma);
				//(functionMMFF94SumEB(xplusSigma) - 2 * fx + functionMMFF94SumEB(xminusSigma)) / Math.pow(sigma,2);
			}
		}
		
		order2ndErrorApproximateHessianMMFF94SumEB = new GMatrix(coord3d.getSize(), coord3d.getSize());
		order2ndErrorApproximateHessianMMFF94SumEB.set(forOrder2ndErrorApproximateHessian);
		//logger.debug("order2ndErrorApproximateHessianMMFF94SumEB : " + order2ndErrorApproximateHessianMMFF94SumEB);
	}


	/**
	 *  Get the 2nd order error approximate Hessian for the bond stretching term.
	 *
	 *
	 *@return           Bond stretching 2nd order error approximate Hessian value.
	 */
	public GMatrix get2ndOrderErrorApproximateHessianMMFF94SumEB() {
		return order2ndErrorApproximateHessianMMFF94SumEB;
	}

}

