package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;
import org.openscience.cdk.*;

/**
 *  MMFF94 energy function.
 *
 *@author     vlabarta
 *@cdk.created    March 14, 2005
 *
 */
public class MMFF94EnergyFunction implements PotentialFunction {
	String energyFunctionShape = " MMFF94 energy ";
	double energy = 0;
	GVector energyGradient = new GVector(3);
	GMatrix energyHessian = null;
	
	
	ForceFieldTools fft = new ForceFieldTools();
	
	BondStretching bs = new BondStretching();
	AngleBending ab = new AngleBending();
	StretchBendInteractions sbi = new StretchBendInteractions();
	Torsions t =new Torsions();
	VanDerWaalsInteractions vdwi = new VanDerWaalsInteractions();


	/**
	 *  Constructor for the MMFF94EnergyFunction object
	 *
	 */
	public MMFF94EnergyFunction(AtomContainer molecule, Hashtable mmff94Tables) throws Exception {
		bs.setMMFF94BondStretchingParameters(molecule, mmff94Tables);
		ab.setMMFF94AngleBendingParameters(molecule, mmff94Tables);
		sbi.setMMFF94StretchBendParameters(molecule, mmff94Tables);
		t.setMMFF94TorsionsParameters(molecule, mmff94Tables);
		vdwi.setMMFF94VanDerWaalsParameters(molecule, mmff94Tables);
	}


	/**
	 *  Evaluate the MMFF94 energy function in a given 3xN point
	 *
	 *@param  coords3d  Current molecule 3xN coordinates.
	 *@return        MMFF94 energy function value.
	 */
	public double energyFunction(GVector coords3d) {
		//System.out.println("bs.functionMMFF94SumEB(coords3d) = " + bs.functionMMFF94SumEB(coords3d));
		//System.out.println("ab.functionMMFF94SumEA(coords3d) = " + ab.functionMMFF94SumEA(coords3d));
		//System.out.println("sbi.functionMMFF94SumEBA(coords3d) = " + sbi.functionMMFF94SumEBA(coords3d));
		//System.out.println("t.functionMMFF94SumET(coords3d) = " + t.functionMMFF94SumET(coords3d));
		//System.out.println("vdwi.functionMMFF94SumEvdW(coords3d) = " + vdwi.functionMMFF94SumEvdW(coords3d));
		
		energy = bs.functionMMFF94SumEB(coords3d) 
			+ ab.functionMMFF94SumEA(coords3d) + sbi.functionMMFF94SumEBA(coords3d)
			+ t.functionMMFF94SumET(coords3d) + vdwi.functionMMFF94SumEvdW(coords3d);
		//System.out.println("energy = " + energy);
		return energy;
	}


	/**
	 *  Evaluate the gradient for the MMFF94 energy function in a given 3xN point
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setEnergyGradient(GVector coords3d) {
		energyGradient.setSize(coords3d.getSize());
		
		bs.setGradientMMFF94SumEB(coords3d);
		ab.setGradientMMFF94SumEA(coords3d);
		sbi.setGradientMMFF94SumEBA(coords3d);
		t.setGradientMMFF94SumET(coords3d);
		vdwi.setGradientMMFF94SumEvdW(coords3d);
		
		for (int i=0; i<energyGradient.getSize();i++) {
			energyGradient.setElement(i, bs.getGradientMMFF94SumEB().getElement(i) 
				+ ab.getGradientMMFF94SumEA().getElement(i) + sbi.getGradientMMFF94SumEBA().getElement(i)
				+ t.getGradientMMFF94SumET().getElement(i) + vdwi.getGradientMMFF94SumEvdW().getElement(i));
		}
	}


	/**
	 *  Get the gradient for the MMFF94 energy function in a given 3xN point
	 *
	 *@return        MMFF94 energy gradient value
	 */
	public GVector getEnergyGradient() {
		return energyGradient;
	}


	/**
	 *  Evaluate the hessian for the MMFF94 energy function in a given 3xN point
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setEnergyHessian(GVector coords3d) {
		
		double [] forHessian = new double[coords3d.getSize()*coords3d.getSize()];
		
		bs.setHessianMMFF94SumEB(coords3d);
		ab.setHessianMMFF94SumEA(coords3d);
		sbi.setHessianMMFF94SumEBA(coords3d);
		t.setHessianMMFF94SumET(coords3d);
		//vdwi.setHessianMMFF94SumEvdW(coords3d);
		
		for (int i = 0; i < coords3d.getSize(); i++) {
			for (int j = 0; j < coords3d.getSize(); j++) {
				forHessian [i*coords3d.getSize()+j] = bs.getHessianMMFF94SumEB().getElement(i,j) 
					+ ab.getHessianMMFF94SumEA().getElement(i,j) + sbi.getHessianMMFF94SumEBA().getElement(i,j) 
					+ t.getHessianMMFF94SumET().getElement(i,j); //+ vdwi.getHessianMMFF94SumEvdW().getElement(i,j);
			}		
		}

		energyHessian = new GMatrix(coords3d.getSize(), coords3d.getSize(), forHessian);
	}


	/**
	 *  Get the hessian for the MMFF94 energy function in a given 3xN point.
	 *
	 *@return        MMFF94 energy hessian value
	 */
	public GMatrix getEnergyHessian() {
		return energyHessian;
	}

}
