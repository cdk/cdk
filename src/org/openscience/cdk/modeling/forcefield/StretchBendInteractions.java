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
 *  Stretch-Bend Interaction calculator for the potential energy function. Include function and derivatives.
 *
 * @author      vlabarta
 * @cdk.created 2005-02-15
 */
public class StretchBendInteractions {

	String functionShape = " Stretch-BendInteractions ";

	double mmff94SumEBA = 0;
	GVector gradientMMFF94SumEBA = new GVector(3);
	GMatrix hessianMMFF94SumEBA = new GMatrix(3,3);

	GVector dDeltav = new GVector(3);
	GVector dDeltarij = new GVector(3);
	GVector dDeltarkj = new GVector(3);
	
	int angleNumber = 0;
	int[][] angleAtomPosition = null;

	double[] v0 = null;
	double[] r0IJ = null;
	double[] r0KJ = null;
	double[] kbaIJK = null;
	double[] kbaKJI = null;
	double[] v = null;
	double[] deltav = null;
	double[] rij = null;
	double[] rkj = null;
	double[] deltarij = null;
	double[] deltarkj = null;

	ForceFieldTools ffTools = new ForceFieldTools();

	/**
	 *  Constructor for the StretchBendInteractions object
	 */
	public StretchBendInteractions() { }


	/**
	 *  Set MMFF94 constants kbaIJK and kbaKJI for each i-j-k angle in the molecule.
	 *
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94StretchBendParameters(AtomContainer molecule, Hashtable parameterSet) throws Exception {

		Atom[] atomConnected = null;
		
		for (int j = 0; j < molecule.getAtomCount(); j++) {
			atomConnected = molecule.getConnectedAtoms(molecule.getAtomAt(j));
			if (atomConnected.length > 1) {
				for (int i = 0; i < atomConnected.length; i++) {
					for (int k = i+1; k < atomConnected.length; k++) {
						angleNumber += 1;
					}
				}
			}
		}
		//System.out.println("angleNumber = " + angleNumber);

		Vector stretchBendInteractionsData = null;
		Vector bondData = null;
		Vector angleData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initilize(parameterSet);
		
		v0 = new double[angleNumber];
		r0IJ = new double[angleNumber];
		r0KJ = new double[angleNumber];
		kbaIJK = new double[angleNumber];
		kbaKJI = new double[angleNumber];

		angleAtomPosition = new int[angleNumber][];

		int l = -1;
		for (int j = 0; j < molecule.getAtomCount(); j++) {
			atomConnected = molecule.getConnectedAtoms(molecule.getAtomAt(j));
			if (atomConnected.length > 1) {
				for (int i = 0; i < atomConnected.length; i++) {
					for (int k = i+1; k < atomConnected.length; k++) {
						stretchBendInteractionsData = pc.getBondAngleInteractionData(atomConnected[i].getID(), molecule.getAtomAt(j).getID(), atomConnected[k].getID());
						//System.out.println("stretchBendInteractionsData : " + stretchBendInteractionsData);
						l += 1;
						kbaIJK[l] = ((Double) stretchBendInteractionsData.get(0)).doubleValue();
						kbaKJI[l] = ((Double) stretchBendInteractionsData.get(1)).doubleValue();

						//System.out.println("kbaIJK[" + l + "] = " + kbaIJK[l]);
						//System.out.println("kbaKJI[" + l + "] = " + kbaKJI[l]);
						
						angleData = pc.getAngleData(atomConnected[i].getID(), molecule.getAtomAt(j).getID(), atomConnected[k].getID());		
						v0[l] = ((Double) angleData.get(0)).doubleValue();

						bondData = pc.getBondData(atomConnected[i].getID(), molecule.getAtomAt(j).getID());
						r0IJ[l] = ((Double) bondData.get(0)).doubleValue();
						bondData = pc.getBondData(atomConnected[k].getID(), molecule.getAtomAt(j).getID());
						r0KJ[l] = ((Double) bondData.get(0)).doubleValue();
						
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
		rij = new double[angleNumber];
		rkj = new double[angleNumber];
		deltarij = new double[angleNumber];
		deltarkj = new double[angleNumber];


	}


	/**
	 *  Calculate the actual bond angles vijk, bond distances rij and rkj, and the difference with the reference angles and bonds
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void calculateDeltarAndv(GVector coords3d) {

		for (int i = 0; i < angleNumber; i++) {
			v[i] = ffTools.angleBetweenTwoBondsFrom3xNCoordinates(coords3d, angleAtomPosition[i][0],angleAtomPosition[i][1],angleAtomPosition[i][2]);
			deltav[i] = v[i] - v0[i];

			rij[i] = ffTools.distanceBetweenTwoAtomsFrom3xNCoordinates(coords3d, angleAtomPosition[i][0], angleAtomPosition[i][1]);
			deltarij[i] = rij[i] - r0IJ[i];

			rkj[i] = ffTools.distanceBetweenTwoAtomsFrom3xNCoordinates(coords3d, angleAtomPosition[i][2], angleAtomPosition[i][1]);
			deltarkj[i] = rkj[i] - r0KJ[i];
		}
	}


	/**
	 *  Evaluate the MMFF94 stretch-bend interaction term.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 *@return        MMFF94 stretch-bend interaction term value.
	 */
	public double functionMMFF94SumEBA(GVector coords3d) {
		calculateDeltarAndv(coords3d);
		mmff94SumEBA = 0;
		for (int j = 0; j < angleNumber; j++) {
			mmff94SumEBA = mmff94SumEBA + 2.51210 * (kbaIJK[j] * deltarij[j] + kbaKJI[j] * deltarkj[j]) * deltav[j];
		}
		//System.out.println("mmff94SumEBA = " + mmff94SumEBA);
		return mmff94SumEBA;
	}


	/**
	 *  Evaluate the gradient of the stretch-bend interaction term. 
	 *  
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEBA(GVector coords3d) {

		gradientMMFF94SumEBA.setSize(coords3d.getSize());
		dDeltav.setSize(coords3d.getSize());
		dDeltarij.setSize(coords3d.getSize());
		dDeltarkj.setSize(coords3d.getSize());

		double sumGradientEBA;
		for (int i = 0; i < gradientMMFF94SumEBA.getSize(); i++) {

			sumGradientEBA = 0;
			dDeltav.setElement(i,1);                 // dDeltav : partial derivative of deltav. To change in the future
			dDeltarij.setElement(i,1);                 // dDeltarij : partial derivative of deltav. To change in the future
			dDeltarkj.setElement(i,1);                 // dDeltarkj : partial derivative of deltav. To change in the future

			for (int j = 0; j < angleNumber; j++) {

				sumGradientEBA = sumGradientEBA + (kbaIJK[j] * dDeltarij.getElement(i) + kbaKJI[j] * dDeltarkj.getElement(i)) * deltav[j] 
							+ (kbaIJK[j] * deltarij[j] + kbaKJI[j] * deltarkj[j]) * dDeltav.getElement(i);
			}
			sumGradientEBA = sumGradientEBA * 2.51210;
			
			gradientMMFF94SumEBA.setElement(i, sumGradientEBA);
		}
		//System.out.println("gradientMMFF94SumEBA = " + gradientMMFF94SumEBA);
	}


	/**
	 *  Get the gradient of the stretch-bend interaction term. 
	 *  
	 *
	 *@return           stretch-bend interaction gradient value.
	 */
	public GVector getGradientMMFF94SumEBA() {
		return gradientMMFF94SumEBA;
	}


	/**
	 *  Evaluate the hessian of the stretch-bend interaction.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEBA(GVector coords3d) {

		double[] forHessian = new double[coords3d.getSize() * coords3d.getSize()];
		double sumHessianEBA = 0;

		GMatrix ddDeltav = new GMatrix(coords3d.getSize(),coords3d.getSize());
		ddDeltav.setZero();
		GMatrix ddDeltarij = new GMatrix(coords3d.getSize(),coords3d.getSize());
		ddDeltarij.setZero();
		GMatrix ddDeltarkj = new GMatrix(coords3d.getSize(),coords3d.getSize());
		ddDeltarkj.setZero();

		for (int i = 0; i < forHessian.length; i++) {
			for (int j = 0; j < angleNumber; j++) {
				sumHessianEBA = sumHessianEBA + (kbaIJK[j] * ddDeltarij.getElement(0,0) + kbaKJI[j] * ddDeltarkj.getElement(0,0)) * deltav[j] 
							+ (kbaIJK[j] * dDeltarij.getElement(0) + kbaKJI[j] * dDeltarkj.getElement(0)) * dDeltav.getElement(0) 
							+ (kbaIJK[j] * dDeltarij.getElement(0) + kbaKJI[j] * dDeltarkj.getElement(0)) * dDeltav.getElement(0) 
							+ (kbaIJK[j] * deltarij[j] + kbaKJI[j] * deltarkj[j]) * ddDeltav.getElement(0,0);
			}
			forHessian[i] = sumHessianEBA;
		}

		hessianMMFF94SumEBA.setSize(coords3d.getSize(), coords3d.getSize());
		hessianMMFF94SumEBA.set(forHessian); 
		//System.out.println("hessianMMFF94SumEBA : " + hessianMMFF94SumEBA);
	}


	/**
	 *  Get the hessian of the stretch-bend interaction.
	 *
	 *@return        Hessian value of the stretch-bend interaction term.
	 */
	public GMatrix getHessianMMFF94SumEBA() {
		return hessianMMFF94SumEBA;
	}

}

