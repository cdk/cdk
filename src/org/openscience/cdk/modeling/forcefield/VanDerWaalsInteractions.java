package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;

import org.openscience.cdk.*;
import org.openscience.cdk.modeling.builder3d.*;
import org.openscience.cdk.qsar.BondsToAtomDescriptor;
import org.openscience.cdk.qsar.Descriptor;
import org.openscience.cdk.qsar.result.IntegerResult;

/**			
 *  Van Der Waals Interactions calculator for the potential energy function. Include function and derivatives.
 *
 *@author     vlabarta
 *@cdk.created    February 17, 2005
 */
public class VanDerWaalsInteractions {

	String functionShape = " Van Der Waals Interactions ";

	double mmff94SumEvdW = 0;
	double ccgSumEvdWSlaterKirkwood = 0;
	double ccgSumEvdWAverage = 0;
	GVector gradientMMFF94SumEvdW = new GVector(3);
	GVector gradientCCGSumEvdWSlaterKirkwood = new GVector(3);
	GVector gradientCCGSumEvdWAverage = new GVector(3);
	GMatrix hessianMMFF94SumEvdW = new GMatrix(3,3);
	
	GVector dR = new GVector(3);	// Atom distance derivative respect to atoms coordinates

	GVector dterm1 = new GVector(3);
	GVector dterm2 = new GVector(3);
	GVector ds = new GVector(3);
	GVector dt = new GVector(3);
	GVector dIvdw = new GVector(3);
	
	//int[][] distances = null;	//Better check common atom connected
	Descriptor shortestPathBetweenToAtoms=new BondsToAtomDescriptor();
	Object[] params = {new Integer(0), new Integer(0)};
	
	int vdwInteractionNumber;
	int[][] vdWiAtomPosition = null;

	double[] eSK = null; 	// vdW well depths (mmff94: Slater-Kirkwood-based formula).
	double[] asteriskR = null;	// minimum-energy separation in angstroms (mmff94).
	double[] r = null;	// interatomic distance
	double[]  atomRadiu0;

	double[] eAv = null; 	// vdW well depths (Average).
	double[] capitalR = null;	// minimum-energy separation in angstroms (Average).
	
	double bb = 0.2;
	double microB = 12;

	// Parameters for the ccg vdWaals function
	double a = 0.07;	// buffering constants (a,b). Prevent division by 0.
	double b = 0.12;
	double n = 7;
	double m = 14;
	double nij = n;
	double mij = m - n;
	double[] t = null;
	double[] ivdw = null;
	double vdwScale14 = 1;	// Scale factor for 1-4 interactions. To take in the future from mmff94.prm files.
	
	ForceFieldTools ffTools = new ForceFieldTools();
	
	/**
	 *  Constructor for the VanDerWaalsInteractions object
	 */
	public VanDerWaalsInteractions() {}


	/**
	 *  Set CCG Van Der Waals parameters for the molecule.
	 *
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94VanDerWaalsParameters(AtomContainer molecule, Hashtable parameterSet) throws Exception {

		//distances = wnd.getShortestPathLengthBetweenAtoms((AtomContainer) molecule);
		
		vdwInteractionNumber = 0;
		for (int i=0; i<molecule.getAtomCount(); i++) {
			for (int j=i+1; j<molecule.getAtomCount(); j++) {
				params[0] = new Integer(i);
				params[1] = new Integer(j);
				shortestPathBetweenToAtoms.setParameters(params);
				//if (distances[molecule.getAtomNumber(molecule.getAtomAt(i))][molecule.getAtomNumber(molecule.getAtomAt(j))]>2) {
				if (((IntegerResult)shortestPathBetweenToAtoms.calculate(molecule).getValue()).intValue()>2){
					vdwInteractionNumber += 1;
				}
			}
		}
		//System.out.println("vdwInteractionNumber : " + vdwInteractionNumber);
		
		Vector vdwInteractionData = null;

		eSK = new double[vdwInteractionNumber];
		asteriskR = new double[vdwInteractionNumber];
		eAv = new double[vdwInteractionNumber];
		capitalR = new double[vdwInteractionNumber];
		r = new double[vdwInteractionNumber];
		atomRadiu0 = new double[molecule.getAtomCount()];
		t = new double[vdwInteractionNumber];
		ivdw = new double[vdwInteractionNumber];

		double gI;	// To eSK calculation
		double gJ;	// To eSK calculation
		double alphaI;	// To eSK calculation and asteriskR[l]
		double alphaJ;	// To eSK calculation and asteriskR[l]
		double nI;
		double nJ;
		double aaI;	// To calculate asteriskRI
		double aaJ;	// To calculate asteriskRJ
		double asteriskRI;
		double asteriskRJ;
		double gamma;	// To calculate asteriskR[l]
		double dI;
		double dJ;
		double eI;
		double eJ;
		
		vdWiAtomPosition = new int[vdwInteractionNumber][];

		int l = -1;
		for (int i=0; i<molecule.getAtomCount(); i++) {
			for (int j=i+1; j<molecule.getAtomCount(); j++) {
				params[0] = new Integer(i);
				params[1] = new Integer(j);
				shortestPathBetweenToAtoms.setParameters(params);
				//if (distances[molecule.getAtomNumber(molecule.getAtomAt(i))][molecule.getAtomNumber(molecule.getAtomAt(j))]>2) {
				if (((IntegerResult)shortestPathBetweenToAtoms.calculate(molecule).getValue()).intValue()>2){
					l += 1;
					vdwInteractionData = (Vector) parameterSet.get("data" + molecule.getAtomAt(i).getID());
					aaI = ((Double) vdwInteractionData.get(6)).doubleValue();
					gI = ((Double) vdwInteractionData.get(7)).doubleValue();
					alphaI = ((Double) vdwInteractionData.get(1)).doubleValue();
					nI = ((Double) vdwInteractionData.get(2)).doubleValue();
					eI = ((Double) vdwInteractionData.get(0)).doubleValue();
					asteriskRI = aaI * Math.pow(alphaI,0.25);
					
					vdwInteractionData = (Vector) parameterSet.get("data" + molecule.getAtomAt(j).getID());
					//System.out.println("vdwInteractionData : " + vdwInteractionData);
					aaJ = ((Double) vdwInteractionData.get(6)).doubleValue();
					gJ = ((Double) vdwInteractionData.get(7)).doubleValue();
					alphaJ = ((Double) vdwInteractionData.get(1)).doubleValue();
					nJ = ((Double) vdwInteractionData.get(2)).doubleValue();
					eJ = ((Double) vdwInteractionData.get(0)).doubleValue();
					asteriskRJ = aaJ * Math.pow(alphaJ,0.25);
					
					if (molecule.getAtomAt(i).getID() == molecule.getAtomAt(j).getID()) {
						asteriskR[l] = asteriskRI;
					} else {
						gamma = (asteriskRI - asteriskRJ) / (asteriskRI + asteriskRJ);
						asteriskR[l] = 0.5 * (asteriskRI + asteriskRJ) * (1 + bb * (1 - Math.exp(-microB * Math.pow(gamma,2))));
					}

					eSK[l] = ((181.16 * gI * gJ * alphaI * alphaJ) / (Math.sqrt(alphaI/nI) + Math.sqrt(alphaJ/nJ))) * 1 / Math.pow(asteriskR[l], 6);
					//System.out.println("eSK = " + eSK[l]);
					
					vdwInteractionData = (Vector) parameterSet.get("vdw" + molecule.getAtomAt(i).getID());
					atomRadiu0[i] = ((Double) vdwInteractionData.get(0)).doubleValue();
					vdwInteractionData = (Vector) parameterSet.get("vdw" + molecule.getAtomAt(j).getID());
					atomRadiu0[j] = ((Double) vdwInteractionData.get(0)).doubleValue();
					
					dI = 2 * atomRadiu0[i];
					dJ = 2 * atomRadiu0[j];
					capitalR[l] = (dI + dJ)/2;
					
					eAv[l] = Math.sqrt(eI * eJ);
					
					t[l] = 1;
					
					params[0] = new Integer(i);
					params[1] = new Integer(j);
					shortestPathBetweenToAtoms.setParameters(params);
					if (((IntegerResult)shortestPathBetweenToAtoms.calculate(molecule).getValue()).intValue()==3){
					//if (distances[molecule.getAtomNumber(molecule.getAtomAt(i))][molecule.getAtomNumber(molecule.getAtomAt(j))] == 3) {
						ivdw[l] = vdwScale14;
					}else {
						ivdw[l] = 1;
					}
					

					vdWiAtomPosition[l] = new int[2];
					vdWiAtomPosition[l][0] = i;
					vdWiAtomPosition[l][1] = j;
				}
			}
		}
	}


	/**
	 *  Calculate the actual Rij
	 *
	 *@param  point  Current molecule coordinates.
	 */
	public void setAtomDistance(GVector point) throws Exception{

		for (int l = 0; l < vdwInteractionNumber; l++) {

			r[l] = ffTools.calculate3dDistanceBetweenTwoAtomFrom3xNCoordinates(point, vdWiAtomPosition[l][0], vdWiAtomPosition[l][1]);
			//System.out.println("r[" + l + "]= " + r[l]);
		}
	}


	/**
	 *  Get the atom distances values (Rij).
	 *
	 *@return        Atom distance values.
	 */
	public double[] getAtomDistance() {
		return r;
	}


	/**
	 *  Evaluate the MMFF94 Van Der Waals interaction term.
	 *
	 *@param  point  Current molecule coordinates.
	 *@return        MMFF94 Van Der Waals interaction term value.
	 */
	public double functionMMFF94SumEvdW(GVector point) {
		mmff94SumEvdW = 0;
		for (int l = 0; l < vdwInteractionNumber; l++) {
			mmff94SumEvdW = mmff94SumEvdW +
							eSK[l] *
							(Math.pow(1.07 * asteriskR[l] / (r[l] + 0.07 * asteriskR[l]) ,7)) *
							((1.12 * Math.pow(asteriskR[l],7) / (Math.pow(r[l],7) + 0.12 * Math.pow(asteriskR[l],7))) - 2);
		}
		//System.out.println("mmff94SumEvdW = " + mmff94SumEvdW);
		return mmff94SumEvdW;
	}


	/**
	 *  Evaluate the CCG Van Der Waals interaction term.
	 *
	 *@param  point  Current molecule coordinates.
	 *@return        CCG Van Der Waals interaction term value.
	 */
	public double functionCCGSumEvdWSK(GVector point, double[] s) {
		ccgSumEvdWSlaterKirkwood = 0;
		double c;
		for (int l = 0; l < vdwInteractionNumber; l++) {
			c = ((1+a) * asteriskR[l]) / (r[l] + a * asteriskR[l]);
			ccgSumEvdWSlaterKirkwood = ccgSumEvdWSlaterKirkwood +
							(eSK[l] *
							(Math.pow(c,nij)) *
							((nij/mij) * ((1+b) * Math.pow(asteriskR[l],mij) / (Math.pow(r[l],mij) + b * Math.pow(asteriskR[l],mij)))   - (mij + nij)/mij) *
							s[l] * t[l] * ivdw[l]);
		}
		//System.out.println("ccgSumEvdWSlaterKirkwood = " + ccgSumEvdWSlaterKirkwood);
		return ccgSumEvdWSlaterKirkwood;
	}


	/**
	 *  Evaluate the CCG Van Der Waals interaction term.
	 *
	 *@param  point  Current molecule coordinates.
	 *@return        CCG Van Der Waals interaction term value.
	 */
	public double functionCCGSumEvdWAv(GVector point, double[] s) {
		ccgSumEvdWAverage = 0;
		double c;
		for (int l = 0; l < vdwInteractionNumber; l++) {
			c = ((1+a) * capitalR[l]) / (r[l] + a * capitalR[l]);
			ccgSumEvdWAverage = ccgSumEvdWAverage +
							(eAv[l] *
							(Math.pow(c,nij)) *
							((nij/mij) * ((1+b) * Math.pow(capitalR[l],mij) / (Math.pow(r[l],mij) + b * Math.pow(capitalR[l],mij))) - (mij + nij)/mij) *
							s[l] * t[l] * ivdw[l]);
							
		}
		//System.out.println("ccgSumEvdWAverage = " + ccgSumEvdWAverage);
		return ccgSumEvdWAverage;
	}


	/**
	 *  Set the gradient of the MMFF94 Van Der Waals interaction term.
	 *
	 *
	 *@param  point  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEvdW(GVector point) {

		gradientMMFF94SumEvdW.setSize(point.getSize());
		dR.setSize(point.getSize());
		
		double sumGradientEvdW;
		for (int i = 0; i < gradientMMFF94SumEvdW.getSize(); i++) {
			dR.setElement(i,1);                 // dR : partial derivative of Rij. To change in the future
			sumGradientEvdW = 0;
			for (int l = 0; l < vdwInteractionNumber; l++) {
				
				sumGradientEvdW = sumGradientEvdW + eSK[l] * 7 * Math.pow((1.07 * asteriskR[l]) / (r[l] + 0.07 * asteriskR[l]),6) * 
								1.07 * asteriskR[l] * (-1) * (1/Math.pow((r[l] + 0.07 * asteriskR[l]),2)) * dR.getElement(i) * 
								((1.12 * Math.pow(asteriskR[l],7) / (Math.pow(r[l],7) + 0.12 * Math.pow(asteriskR[l],7))) - 2) + 
								(Math.pow(1.07 * asteriskR[l] / (r[l] + 0.07 * asteriskR[l]) ,7)) * 
								1.12 * Math.pow(asteriskR[l],7) * 
								(-1) * (1/Math.pow((Math.pow(r[l],7) + 0.12 * Math.pow(asteriskR[l],7)),2)) * 
								7 * Math.pow(r[l],6) * dR.getElement(i); 
				
			}
			gradientMMFF94SumEvdW.setElement(i, sumGradientEvdW);
		}
		//System.out.println("gradientMMFF94SumEvdW = " + gradientMMFF94SumEvdW);
	}


	/**
	 *  Get the gradient of the MMFF94 Van Der Waals interaction term.
	 *
	 *
	 *@return           MMFF94 Van Der Waals interaction gradient value.
	 */
	public GVector getGradientMMFF94SumEvdW() {
		return gradientMMFF94SumEvdW;
	}


	/**
	 *  Evaluate the gradient of the CCG Van Der Waals interaction term.
	 *  
	 *
	 *@param  point  Current molecule coordinates.
	 *@return           CCG Van Der Waals interaction gradient value.
	 */
/*	public GVector gradientMMFF94SumEvdW(GVector point, double[] s) {

		gradientCCGSumEvdWSlaterKirkwood.setSize(molecule.getAtomCount() * 3);
		dR.setSize(molecule.getAtomCount() * 3);

		dterm1.setSize(molecule.getAtomCount() * 3);
		dterm2.setSize(molecule.getAtomCount() * 3);
		
		double c;
		double[] term1 = new double[vdwInteractionNumber];
		double[] term2 = new double[vdwInteractionNumber];
		double sumGradientEvdW;
		for (int i = 0; i < gradientCCGSumEvdWSlaterKirkwood.getSize(); i++) {

			dterm1.setElement(i,1);                 // dterm1 : partial derivative of term1. To change in the future
			dterm2.setElement(i,1);                 // dterm2 : partial derivative of term2. To change in the future
			ds.setElement(i,1);                 // ds : partial derivative of s. To change in the future
			dt.setElement(i,1);                 // dt : partial derivative of t. To change in the future
			dIvdw.setElement(i,1);                 // dIvdw : partial derivative of Ivdw. To change in the future

			sumGradientEvdW = 0;
			for (int l = 0; l < vdwInteractionNumber; l++) {
				
				c = ((1+a) * asteriskR[l]) / (r[l] + a * asteriskR[l]);
				term1[l] = Math.pow(c,nij);
				term2[l] = (nij/mij) * ((1+b) * Math.pow(asteriskR[l],mij) / (Math.pow(r[l],mij) + b * Math.pow(asteriskR[l],mij)))   - (mij + nij)/mij;
				
				sumGradientEvdW = sumGradientEvdW + (deSK.getElement(i) * term1[l] * term2[l] + eSK[l] * (dterm1.getElement(i) * term2[l] + term1[l] * dterm2.getElement(i))) * s[l] * t[l] * ivdw[l] + 
								(eSK[l] * term1[l] * term2[l]) * (ds.getElement(i) * t[l] * ivdw[l] + s[l] * (dt.getElement(i) * ivdw[l] + t[l] * dIvdw.getElement(i)));
			}
			sumGradientEvdW = sumGradientEvdW * 2.51210;
			
			gradientCCGSumEvdWSlaterKirkwood.setElement(i, sumGradientEvdW);
		}
		//System.out.println("gradientCCGSumEvdWSlaterKirkwood = " + gradientCCGSumEvdWSlaterKirkwood);
		return gradientCCGSumEvdWSlaterKirkwood;
	}*/


	/**
	 *  Evaluate the hessian of the CCG Van Der Waals interaction term.
	 *
	 *@param  point  Current molecule coordinates.
	 *@return        Hessian value of the CCG Van Der Waals interaction term.
	 */
/*	public GMatrix hessian(GVector point) {

		double[] forHessian = new double[point.getSize() * point.getSize()];
		double sumHessianEvdW = 0;

		GMatrix ddR = new GMatrix(point.getSize(),point.getSize());
		ddR.setZero();

		for (int i = 0; i < forHessian.length; i++) {
			for (int j = 0; j < vdwInteractionNumber; j++) {
				sumHessianEvdW = sumHessianEvdW + 1;
			}
			forHessian[i] = sumHessianEvdW;
		}

		hessianMMFF94SumEvdW.setSize(point.getSize(), point.getSize());
		hessianMMFF94SumEvdW.set(forHessian); 
		//System.out.println("hessianMMFF94SumEvdW : " + hessianMMFF94SumEvdW);
		return hessianMMFF94SumEvdW;
	}*/

}

