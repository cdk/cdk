package org.openscience.cdk.modeling.forcefield;

import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.modeling.builder3d.MMFF94ParametersCall;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  Torsions calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    March 2, 2005
 *@cdk.module     builder3d
 */
public class Torsions {

	String functionShape = " Torsions ";

	double mmff94SumET = 0;
	GVector gradientMMFF94SumET = new GVector(3);
	GVector order2ndErrorApproximateGradientMMFF94SumET = new GVector(3);
	GVector order5thErrorApproximateGradientMMFF94SumET = new GVector(3);
	GMatrix hessianMMFF94SumET = null;
	double[] forHessian = null;
	GMatrix order2ndErrorApproximateHessianMMFF94SumET = null;
	double[] forOrder2ndErrorApproximateHessian = null;

	GVector dPhi = new GVector(3);
	
	int torsionNumber = 0;
	int[][] torsionAtomPosition = null;

	double[] v1 = null;
	double[] v2 = null;
	double[] v3 = null;
	double[] phi = null;
	
	org.openscience.cdk.interfaces.IBond[] bond = null;
	IAtom[] atomInBond = null;
	org.openscience.cdk.interfaces.IBond[] bondConnectedBefore = null;
	org.openscience.cdk.interfaces.IBond[] bondConnectedAfter = null;

	ForceFieldTools ffTools = new ForceFieldTools();
	private LoggingTool logger;


	/**
	 *  Constructor for the Torsions object
	 */
	public Torsions() {        
		logger = new LoggingTool(this);
	}


	/**
	 *  Set MMFF94 constants V1, V2 and V3 for each i-j, j-k and k-l bonded pairs in the molecule.
	 *
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94TorsionsParameters(AtomContainer molecule, Hashtable parameterSet) throws Exception {

		bond = molecule.getBonds();
		for (int b=0; b<bond.length; b++) {
			atomInBond = bond[b].getAtoms();
			bondConnectedBefore = molecule.getConnectedBonds(atomInBond[0]);
			if (bondConnectedBefore.length > 1) {
				bondConnectedAfter = molecule.getConnectedBonds(atomInBond[1]);
				if (bondConnectedAfter.length > 1) {
					for (int bb=0; bb<bondConnectedBefore.length; bb++) {
						if (bondConnectedBefore[bb].compare(bond[b])) {}
						else {
							for (int ba=0; ba<bondConnectedAfter.length; ba++) {
								if (bondConnectedAfter[ba].compare(bond[b])) {}
								else {
									torsionNumber += 1;
									//logger.debug("atomi : " + bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]).getAtomTypeName());
									//logger.debug("atomj : " + atomInBond[0].getAtomTypeName());
									//logger.debug("atomk : " + atomInBond[1].getAtomTypeName());
									//logger.debug("atoml : " + bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]).getAtomTypeName());
								}
							}
						}
					}
				}
			}
		}
		//logger.debug("torsionNumber = " + torsionNumber);

		Vector torsionsData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initilize(parameterSet);
		
		v1 = new double[torsionNumber];
		v2 = new double[torsionNumber];
		v3 = new double[torsionNumber];

		torsionAtomPosition = new int[torsionNumber][];

		int m = -1;
		for (int b=0; b<bond.length; b++) {
			atomInBond = bond[b].getAtoms();
			bondConnectedBefore = molecule.getConnectedBonds(atomInBond[0]);
			if (bondConnectedBefore.length > 1) {
				bondConnectedAfter = molecule.getConnectedBonds(atomInBond[1]);
				if (bondConnectedAfter.length > 1) {
					for (int bb=0; bb<bondConnectedBefore.length; bb++) {
						if (bondConnectedBefore[bb].compare(bond[b])) {}
						else {
							for (int ba=0; ba<bondConnectedAfter.length; ba++) {
								if (bondConnectedAfter[ba].compare(bond[b])) {}
								else {
									m += 1;
									torsionAtomPosition[m] = new int[4];
									torsionAtomPosition[m][0] = molecule.getAtomNumber(bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]));
									torsionAtomPosition[m][1] = molecule.getAtomNumber(atomInBond[0]);
									torsionAtomPosition[m][2] = molecule.getAtomNumber(atomInBond[1]);
									torsionAtomPosition[m][3] = molecule.getAtomNumber(bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]));
									
									//logger.debug("torsionAtomPosition[" + m + "]: " + bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]).getSymbol() 
											//+ ", "+ atomInBond[0].getSymbol() + ", " + atomInBond[1].getSymbol() + ", " 
											//+ bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]).getSymbol());
									
									torsionsData = (Vector)parameterSet.get("torsion" + bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]).getAtomTypeName() + ";" 
																+ atomInBond[0].getAtomTypeName() + ";" 
																+ atomInBond[1].getAtomTypeName() + ";" 
																+ bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]).getAtomTypeName());
									if (torsionsData == null) {
										torsionsData = (Vector)parameterSet.get("torsion" + bondConnectedAfter[ba].getConnectedAtom(atomInBond[1]).getAtomTypeName() + ";"
																+ atomInBond[1].getAtomTypeName() + ";" 
																+ atomInBond[0].getAtomTypeName() + ";" 
																+ bondConnectedBefore[bb].getConnectedAtom(atomInBond[0]).getAtomTypeName());
									}
									
									//logger.debug("torsionsData " + m + ": " + torsionsData);
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
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setPhi(GVector coords3d) {

		phi = new double[torsionNumber];
		
		for (int m = 0; m < torsionNumber; m++) {
			
			phi[m] = ffTools.torsionAngleFrom3xNCoordinates(coords3d, torsionAtomPosition[m][0], torsionAtomPosition[m][1], 
						torsionAtomPosition[m][2], torsionAtomPosition[m][3]);
			//logger.debug("phi[" + m + "] : " + phi[m]);	
		}
	}


	/**
	 *  Evaluate the MMFF94 torsions term.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 *@return        MMFF94 torsions term value.
	 */
	public double functionMMFF94SumET(GVector coords3d) {
		setPhi(coords3d);
		mmff94SumET = 0;
		double torsionEnergy=0;
		for (int m = 0; m < torsionNumber; m++) {
			torsionEnergy = v1[m] * (1 + Math.cos(phi[m])) + v2[m] * (1 - Math.cos(2 * phi[m])) + v3[m] * (1 + Math.cos(3 * phi[m]));
			//logger.debug("phi[" + m + "] = " + Math.toDegrees(phi[m]) + ", cph" + Math.cos(phi[m]) + ", c2ph" + Math.cos(2 * phi[m]) + ", c3ph" + Math.cos(3 * phi[m]) + ", te=" + torsionEnergy);
			//if (torsionEnergy < 0) {
			//	torsionEnergy= (-1) * torsionEnergy;
			//}
			mmff94SumET = mmff94SumET + torsionEnergy;
			
			//mmff94SumET = mmff94SumET + v1[m] * (1 + phi[m]) + v2[m] * (1 - 2 * phi[m]) + v3[m] * (1 + 3 * phi[m]);
		}
		//logger.debug("mmff94SumET = " + mmff94SumET);
		return mmff94SumET;
	}


	/**
	 *  Evaluate the gradient of the torsions term.
	 *  
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumET(GVector coords3d) {

		gradientMMFF94SumET.setSize(coords3d.getSize());
		setPhi(coords3d);
		dPhi.setSize(coords3d.getSize());

		double sumGradientET;
		for (int i = 0; i < gradientMMFF94SumET.getSize(); i++) {

			sumGradientET = 0;
			dPhi.setElement(i,1);                 // dPhi : partial derivative of phi. To change in the future

			for (int m = 0; m < torsionNumber; m++) {

				sumGradientET = sumGradientET - v1[m] * Math.sin(phi[m]) * dPhi.getElement(i) + 
					v2[m] * Math.sin(2 * phi[m]) * 2 * dPhi.getElement(i) - 
					v3[m] * Math.sin(3 * phi[m]) * 3 * dPhi.getElement(i);
			}
			gradientMMFF94SumET.setElement(i, sumGradientET);
		}
		//logger.debug("gradientMMFF94SumET = " + gradientMMFF94SumET);
	}


	/**
	 *  Get the gradient of the torsions term. 
	 *  
	 *
	 *@return           torsions gradient value.
	 */
	public GVector getGradientMMFF94SumET() {
		return gradientMMFF94SumET;
	}


	/**
	 *  Evaluate a 2nd order error approximation of the gradient, for the torsion term, 
	 *  given the atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateGradientMMFF94SumET(GVector coord3d) {
		order2ndErrorApproximateGradientMMFF94SumET.setSize(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		
		for (int m = 0; m < order2ndErrorApproximateGradientMMFF94SumET.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			order2ndErrorApproximateGradientMMFF94SumET.setElement(m,(functionMMFF94SumET(xplusSigma) - functionMMFF94SumET(xminusSigma)) / (2 * sigma));
		}
			
		//logger.debug("order2ndErrorApproximateGradientMMFF94SumET : " + order2ndErrorApproximateGradientMMFF94SumET);
	}


	/**
	 *  Get the 2nd order error approximate gradient of the torsion term.
	 *
	 *
	 *@return           torsion approximate gradient value
	 */
	public GVector get2ndOrderErrorApproximateGradientMMFF94SumET() {
		return order2ndErrorApproximateGradientMMFF94SumET;
	}


	/**
	 *  Evaluate an 5 order approximation of the gradient, of the torsion term, 
	 *  given the atoms coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set5thOrderApproximateGradientMMFF94SumET(GVector coord3d) {
		order5thErrorApproximateGradientMMFF94SumET.setSize(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.2);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		GVector xplus2Sigma = new GVector(coord3d.getSize());
		GVector xminus2Sigma = new GVector(coord3d.getSize());
		
		for (int m=0; m < order5thErrorApproximateGradientMMFF94SumET.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			xplus2Sigma.set(coord3d);
			xplus2Sigma.setElement(m,coord3d.getElement(m) + 2 * sigma);
			xminus2Sigma.set(coord3d);
			xminus2Sigma.setElement(m,coord3d.getElement(m) - 2 * sigma);
			order5thErrorApproximateGradientMMFF94SumET.setElement(m, (8 * (functionMMFF94SumET(xplusSigma) - functionMMFF94SumET(xminusSigma)) - (functionMMFF94SumET(xplus2Sigma) - functionMMFF94SumET(xminus2Sigma))) / (12 * sigma));
		}
			
		//logger.debug("order5thErrorApproximateGradientMMFF94SumET : " + order5thErrorApproximateGradientMMFF94SumET);
	}


	/**
	 *  Get the 5th order error approximate gradient of the torsion term.
	 *
	 *@return        Torsion 5th order error approximate gradient value.
	 */
	public GVector get5thOrderErrorApproximateGradientMMFF94SumET() {
		return order5thErrorApproximateGradientMMFF94SumET;
	}


	/**
	 *  Evaluate the hessian of the torsions.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumET(GVector coords3d) {

		double[] forHessian = new double[coords3d.getSize() * coords3d.getSize()];
		setPhi(coords3d);
		double[] ddPhi = new double[coords3d.getSize() * coords3d.getSize()];
		
		double sumHessianET = 0;
		for (int i = 0; i < coords3d.getSize(); i++) {
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

		hessianMMFF94SumET.setSize(coords3d.getSize(), coords3d.getSize());
		hessianMMFF94SumET.set(forHessian); 
		//logger.debug("hessianMMFF94SumET : " + hessianMMFF94SumET);
	}


	/**
	 *  Get the hessian of the torsions.
	 *
	 *@return        Hessian value of the torsions term.
	 */
	public GMatrix getHessianMMFF94SumET() {
		return hessianMMFF94SumET;
	}


	/**
	 *  Evaluate a 2nd order approximation of the Hessian, for the torsion energy term,
	 *  given the atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateHessianMMFF94SumET(GVector coord3d) {
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
			setGradientMMFF94SumET(xminusSigma);
			gradientAtXminusSigma.set(gradientMMFF94SumET);
			xplusSigma.set(coord3d);
			xplusSigma.setElement(i,coord3d.getElement(i) + sigma);
			setGradientMMFF94SumET(xplusSigma);
			gradientAtXplusSigma.set(gradientMMFF94SumET);
			for (int j = 0; j < coord3d.getSize(); j++) {
				forHessianIndex = i*coord3d.getSize()+j;
				forOrder2ndErrorApproximateHessian[forHessianIndex] = (gradientAtXplusSigma.getElement(j) - gradientAtXminusSigma.getElement(j)) / (2 * sigma);
				//(functionMMFF94SumET(xplusSigma) - 2 * fx + functionMMFF94SumET(xminusSigma)) / Math.pow(sigma,2);
			}
		}
		
		order2ndErrorApproximateHessianMMFF94SumET = new GMatrix(coord3d.getSize(), coord3d.getSize());
		order2ndErrorApproximateHessianMMFF94SumET.set(forOrder2ndErrorApproximateHessian);
		//logger.debug("order2ndErrorApproximateHessianMMFF94SumET : " + order2ndErrorApproximateHessianMMFF94SumET);
	}


	/**
	 *  Get the 2nd order error approximate Hessian for the torsion term.
	 *
	 *
	 *@return           Torsion 2nd order error approximate Hessian value.
	 */
	public GMatrix get2ndOrderErrorApproximateHessianMMFF94SumET() {
		return order2ndErrorApproximateHessianMMFF94SumET;
	}

}

