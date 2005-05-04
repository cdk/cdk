package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;
import org.openscience.cdk.*;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  MMFF94 energy function.
 *
 *@author     vlabarta
 *@cdk.created    March 14, 2005
 *@cdk.module     builder3d
 *
 */
public class MMFF94EnergyFunction implements PotentialFunction {
	String energyFunctionShape = " MMFF94 energy ";
	double energy = 0;
	GVector energyGradient = new GVector(3);
	GVector order2ndApproximateEnergyGradient = new GVector(3);
	GVector order5ApproximateEnergyGradient = new GVector(3);
	GMatrix energyHessian = null;
	
	
	ForceFieldTools fft = new ForceFieldTools();
	private LoggingTool logger;
	
	BondStretching bs = new BondStretching();
	AngleBending ab = new AngleBending();
	//StretchBendInteractions sbi = new StretchBendInteractions();
	Torsions t =new Torsions();
	//VanDerWaalsInteractions vdwi = new VanDerWaalsInteractions();


	/**
	 *  Constructor for the MMFF94EnergyFunction object
	 *
	 */
	public MMFF94EnergyFunction(AtomContainer molecule, Hashtable mmff94Tables) throws Exception {
		//logger.debug(molecule.getAtomCount() + " "+mmff94Tables.size());
		bs.setMMFF94BondStretchingParameters(molecule, mmff94Tables);
		ab.setMMFF94AngleBendingParameters(molecule, mmff94Tables);
		//sbi.setMMFF94StretchBendParameters(molecule, mmff94Tables);
		t.setMMFF94TorsionsParameters(molecule, mmff94Tables);
		//vdwi.setMMFF94VanDerWaalsParameters(molecule, mmff94Tables);        
		logger = new LoggingTool(this);
	}


	/**
	 *  Evaluate the MMFF94 energy function for a given 3xN point
	 *
	 *@param  coords3d  Current molecule 3xN coordinates.
	 *@return        MMFF94 energy function value.
	 */
	public double energyFunction(GVector coords3d) {
		//logger.debug("bs.functionMMFF94SumEB(coords3d) = " + bs.functionMMFF94SumEB(coords3d));
		//logger.debug("ab.functionMMFF94SumEA(coords3d) = " + ab.functionMMFF94SumEA(coords3d));
		//logger.debug("sbi.functionMMFF94SumEBA(coords3d) = " + sbi.functionMMFF94SumEBA(coords3d));
		//logger.debug("t.functionMMFF94SumET(coords3d) = " + t.functionMMFF94SumET(coords3d));
		//logger.debug("vdwi.functionMMFF94SumEvdW(coords3d) = " + vdwi.functionMMFF94SumEvdW(coords3d));
		
		energy = bs.functionMMFF94SumEB(coords3d) 
			+ ab.functionMMFF94SumEA(coords3d) // + sbi.functionMMFF94SumEBA(coords3d)
			+ t.functionMMFF94SumET(coords3d) ; //+ vdwi.functionMMFF94SumEvdW(coords3d);
		
		//logger.debug("energy = " + energy);
		return energy;
	}


	/**
	 *  Evaluate the gradient for the MMFF94 energy function in a given 3xN point
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setEnergyGradient(GVector coords3d) {
		//setOrder2ndApproximateEnergyGradient(coords3d);
		
		//logger.debug("coords3d : " + coords3d);
		energyGradient.setSize(coords3d.getSize());
		
		bs.setGradientMMFF94SumEB(coords3d);
		ab.set2ndOrderApproximateGradientMMFF94SumEA(coords3d);
		//ab.set5thOrderApproximateGradientMMFF94SumEA(coords3d);
		//sbi.setGradientMMFF94SumEBA(coords3d);
		t.set2ndOrderApproximateGradientMMFF94SumET(coords3d);
		//t.set5thOrderApproximateGradientMMFF94SumET(coords3d);
		//vdwi.setGradientMMFF94SumEvdW(coords3d);
		
		//logger.debug("bs.getGradientMMFF94SumEB() = " + bs.getGradientMMFF94SumEB());
		//logger.debug("ab.getGradientMMFF94SumEA() = " + ab.getGradientMMFF94SumEA());
		
		for (int i=0; i < energyGradient.getSize(); i++) {
			energyGradient.setElement(i, 
				bs.getGradientMMFF94SumEB().getElement(i) 
				+ ab.get2ndOrderApproximateGradientMMFF94SumEA().getElement(i) // + sbi.getGradientMMFF94SumEBA().getElement(i)
				+ t.get2ndOrderApproximateGradientMMFF94SumET().getElement(i) ); // + vdwi.getGradientMMFF94SumEvdW().getElement(i));
		}
	}


	/**
	 *  Get the gradient for the MMFF94 energy function in a given 3xN point
	 *
	 *@return        MMFF94 energy gradient value
	 */
	public GVector getEnergyGradient() {
		return energyGradient;
		//return order2ndApproximateEnergyGradient;
	}


	/**
	 *  Evaluate the order 2 approximate gradient for the MMFF94 energy function in a given 3xN point.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setOrder2ndApproximateEnergyGradient(GVector coords3d) {
		//logger.debug("coords3d : " + coords3d);
		order2ndApproximateEnergyGradient.setSize(coords3d.getSize());
		double sigma = Math.pow(0.0000000000000001,0.3333);
		GVector xplusSigma = new GVector(coords3d.getSize());
		GVector xminusSigma = new GVector(coords3d.getSize());
		
		for (int i=0; i < order2ndApproximateEnergyGradient.getSize(); i++) {
			xplusSigma.set(coords3d);
			xplusSigma.setElement(i,coords3d.getElement(i) + sigma);
			xminusSigma.set(coords3d);
			xminusSigma.setElement(i,coords3d.getElement(i) - sigma);
			order2ndApproximateEnergyGradient.setElement(i, (energyFunction(xplusSigma) - energyFunction(xminusSigma)) / (2 * sigma));
		}
		//logger.debug("order2ndApproximateEnergyGradient : " + order2ndApproximateEnergyGradient);
	}


	/**
	 *  Get the order 2 approximate gradient for the MMFF94 energy function in a given 3xN point.
	 *
	 *@return        Order 2 approximate MMFF94 energy gradient value
	 */
	public GVector getOrder2ndApproximateEnergyGradient() {
		return order2ndApproximateEnergyGradient;
	}


	/**
	 *  Evaluate the order 5 approximate gradient for the MMFF94 energy function in a given 3xN point
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setOrder5ApproximateEnergyGradient(GVector coords3d) {
		//logger.debug("coords3d : " + coords3d);
		order5ApproximateEnergyGradient.setSize(coords3d.getSize());
		double sigma = Math.pow(0.0000000000000001,0.2);
		GVector xplusSigma = new GVector(coords3d.getSize());
		GVector xminusSigma = new GVector(coords3d.getSize());
		GVector xplus2Sigma = new GVector(coords3d.getSize());
		GVector xminus2Sigma = new GVector(coords3d.getSize());
		
		for (int i=0; i < order5ApproximateEnergyGradient.getSize(); i++) {
			xplusSigma.set(coords3d);
			xplusSigma.setElement(i,coords3d.getElement(i) + sigma);
			xminusSigma.set(coords3d);
			xminusSigma.setElement(i,coords3d.getElement(i) - sigma);
			xplus2Sigma.set(coords3d);
			xplus2Sigma.setElement(i,coords3d.getElement(i) + 2 * sigma);
			xminus2Sigma.set(coords3d);
			xminus2Sigma.setElement(i,coords3d.getElement(i) - 2 * sigma);
			order5ApproximateEnergyGradient.setElement(i, (8 * (energyFunction(xplusSigma) - energyFunction(xminusSigma)) - (energyFunction(xplus2Sigma) - energyFunction(xminus2Sigma))) / (12 * sigma));
		}
			
		//logger.debug("order5ApproximateEnergyGradient : " + order5ApproximateEnergyGradient);
	}


	/**
	 *  Get the order 5 approximate gradient for the MMFF94 energy function in a given 3xN point
	 *
	 *@return        Order 5 approximate MMFF94 energy gradient value
	 */
	public GVector getOrder5ApproximateEnergyGradient() {
		return order5ApproximateEnergyGradient;
	}


	/**
	 *  Evaluate the hessian for the MMFF94 energy function in a given 3xN point
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setEnergyHessian(GVector coords3d) {
		
		double [] forHessian = new double[coords3d.getSize() * coords3d.getSize()];
		
		bs.setHessianMMFF94SumEB(coords3d);
		ab.setHessianMMFF94SumEA(coords3d);
		//sbi.setHessianMMFF94SumEBA(coords3d);
		//t.setHessianMMFF94SumET(coords3d);
		//vdwi.setHessianMMFF94SumEvdW(coords3d);
		
		for (int i = 0; i < coords3d.getSize(); i++) {
			for (int j = 0; j < coords3d.getSize(); j++) {
				forHessian [i*coords3d.getSize()+j] = 
					bs.getHessianMMFF94SumEB().getElement(i,j) 
					+ ab.getHessianMMFF94SumEA().getElement(i,j)// + sbi.getHessianMMFF94SumEBA().getElement(i,j) 
					;//+ t.getHessianMMFF94SumET().getElement(i,j) ;//+ vdwi.getHessianMMFF94SumEvdW().getElement(i,j);
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
