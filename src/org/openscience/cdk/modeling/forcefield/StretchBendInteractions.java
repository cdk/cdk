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
 *  Stretch-Bend Interaction for the potential energy function
 *
 *@author     vlabarta
 *@created    February 15, 2005
 */
public class StretchBendInteractions {

	String functionShape = " Stretch-BendInteractions ";

	double mmff94SumEBA_InWishedCoordinates = 0;
	GVector gradientMMFF94SumEBA_InWishedCoordinates = new GVector(3);
	GMatrix hessianMMFF94SumEBA_InWishedCoordinates = new GMatrix(3,3);

	GVector dDeltav = new GVector(3);
	GVector dDeltarij = new GVector(3);
	GVector dDeltarkj = new GVector(3);
	
	int angleNumber = 0;

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

		v0 = new double[angleNumber];
		r0IJ = new double[angleNumber];
		r0KJ = new double[angleNumber];
		kbaIJK = new double[angleNumber];
		kbaKJI = new double[angleNumber];

		Vector stretchBendInteractionsData = null;
		Vector bondData = null;
		Vector angleData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initilize(parameterSet);
		
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
						

					}
				}
			}
		}

	}


	/**
	 *  Calculate the actual bond angles vijk, bond distances rij and rkj, and the difference with the reference angles and bonds
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 */
	public void calculateDeltarAndv(AtomContainer molecule) {

		v = new double[angleNumber];
		deltav = new double[angleNumber];
		rij = new double[angleNumber];
		rkj = new double[angleNumber];
		deltarij = new double[angleNumber];
		deltarkj = new double[angleNumber];
		
		Atom[] atomConnected = null;
		RDFProtonDescriptor rdfpdo = new RDFProtonDescriptor();
		ForceField ff = new ForceField();
		int l=-1;
		
		for (int j = 0; j < molecule.getAtomCount(); j++) {
			atomConnected = molecule.getConnectedAtoms(molecule.getAtomAt(j));
			if (atomConnected.length > 1) {
				for (int i = 0; i < atomConnected.length; i++) {
					for (int k = i+1; k < atomConnected.length; k++) {
						l += 1;
						Vector3d va = new Vector3d((Tuple3d) atomConnected[i].getPoint3d());
						Vector3d vb = new Vector3d((Tuple3d) molecule.getAtomAt(j).getPoint3d());
						Vector3d vc = new Vector3d((Tuple3d) atomConnected[k].getPoint3d());
						v[l] = rdfpdo.calculateAngleBetweenTwoLines(vb, vb, vc, vb);
						deltav[l] = v[l] - v0[l];

						rij[l] = ff.distanceBetweenTwoAtoms(atomConnected[i].getPoint3d(), molecule.getAtomAt(j).getPoint3d());
						deltarij[l] = rij[l] - r0IJ[l];

						rkj[l] = ff.distanceBetweenTwoAtoms(atomConnected[k].getPoint3d(), molecule.getAtomAt(j).getPoint3d());
						deltarkj[l] = rkj[l] - r0KJ[l];
					}
				}
			}
		}
	}


	/**
	 *  Evaluate the MMFF94 stretch-bend interaction term.
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 *@return        MMFF94 stretch-bend interaction term value.
	 */
	public double functionMMFF94SumEBA_InPoint(AtomContainer molecule) {
		calculateDeltarAndv(molecule);
		mmff94SumEBA_InWishedCoordinates = 0;
		for (int j = 0; j < angleNumber; j++) {
			mmff94SumEBA_InWishedCoordinates = mmff94SumEBA_InWishedCoordinates + 2.51210 * (kbaIJK[j] * deltarij[j] + kbaKJI[j] * deltarkj[j]) * deltav[j];
		}
		//System.out.println("mmff94SumEA_InWishedCoordinates = " + mmff94SumEA_InWishedCoordinates);
		return mmff94SumEBA_InWishedCoordinates;
	}


	/**
	 *  Evaluate the gradient of the stretch-bend interaction term. 
	 *  
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 *@return           stretch-bend interaction gradient value.
	 */
	public GVector gradientMMFF94SumEBA_InPoint(AtomContainer molecule) {

		gradientMMFF94SumEBA_InWishedCoordinates.setSize(molecule.getAtomCount() * 3);
		dDeltav.setSize(molecule.getAtomCount() * 3);
		dDeltarij.setSize(molecule.getAtomCount() * 3);
		dDeltarkj.setSize(molecule.getAtomCount() * 3);

		double sumGradientEBA;
		for (int i = 0; i < gradientMMFF94SumEBA_InWishedCoordinates.getSize(); i++) {

			sumGradientEBA = 0;
			dDeltav.setElement(i,1);                 // dDeltav : partial derivative of deltav. To change in the future
			dDeltarij.setElement(i,1);                 // dDeltarij : partial derivative of deltav. To change in the future
			dDeltarkj.setElement(i,1);                 // dDeltarkj : partial derivative of deltav. To change in the future

			for (int j = 0; j < angleNumber; j++) {

				sumGradientEBA = sumGradientEBA + (kbaIJK[j] * dDeltarij.getElement(i) + kbaKJI[j] * dDeltarkj.getElement(i)) * deltav[j] 
							+ (kbaIJK[j] * deltarij[j] + kbaKJI[j] * deltarkj[j]) * dDeltav.getElement(i);
			}
			sumGradientEBA = sumGradientEBA * 2.51210;
			
			gradientMMFF94SumEBA_InWishedCoordinates.setElement(i, sumGradientEBA);
		}
		//System.out.println("gradientMMFF94SumEBA_InWishedCoordinates = " + gradientMMFF94SumEBA_InWishedCoordinates);
		return gradientMMFF94SumEBA_InWishedCoordinates;
	}


	/**
	 *  Evaluate the hessian of the stretch-bend interaction.
	 *
	 *@param  point  Current coordinates.
	 *@return        Hessian value of the stretch-bend interaction term.
	 */
	public GMatrix hessianInPoint(GVector point) {

		double[] forHessian = new double[point.getSize() * point.getSize()];
		double sumHessianEBA = 0;

		GMatrix ddDeltav = new GMatrix(point.getSize(),point.getSize());
		ddDeltav.setZero();
		GMatrix ddDeltarij = new GMatrix(point.getSize(),point.getSize());
		ddDeltarij.setZero();
		GMatrix ddDeltarkj = new GMatrix(point.getSize(),point.getSize());
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

		hessianMMFF94SumEBA_InWishedCoordinates.setSize(point.getSize(), point.getSize());
		hessianMMFF94SumEBA_InWishedCoordinates.set(forHessian); 
		//System.out.println("hessianMMFF94SumEBA_InWishedCoordinates : " + hessianMMFF94SumEBA_InWishedCoordinates);
		return hessianMMFF94SumEBA_InWishedCoordinates;
	}

}

