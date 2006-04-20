package org.openscience.cdk.modeling.forcefield;

import java.util.Hashtable;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  MMFF94 energy function.
 *
 *@author     vlabarta
 *@cdk.created    March 14, 2005
 *@cdk.module     forcefield
 *
 */
public class MMFF94EnergyFunction implements IPotentialFunction {
	String energyFunctionShape = " MMFF94 energy ";
	double energy = 0;
	GVector energyGradient = null;
	GVector order2ndErrorApproximateEnergyGradient = new GVector(3);
	GVector order5thErrorApproximateEnergyGradient = new GVector(3);
	GMatrix energyHessian = null;
	double[] forHessian = null;
	
	ForceFieldTools fft = new ForceFieldTools();
	private LoggingTool logger;
	
	BondStretching bs = new BondStretching();
	AngleBending ab = new AngleBending();
	StretchBendInteractions sbi = new StretchBendInteractions();
	Torsions t =new Torsions();
	VanDerWaalsInteractions vdwi = new VanDerWaalsInteractions();
	ElectrostaticInteractions ei = new ElectrostaticInteractions();
	int count = 0;

	/**
	 *  Constructor for the MMFF94EnergyFunction object
	 *
	 */
	public MMFF94EnergyFunction(AtomContainer molecule, Hashtable mmff94Tables) throws Exception {
		bs.setMMFF94BondStretchingParameters(molecule, mmff94Tables);
		ab.setMMFF94AngleBendingParameters(molecule, mmff94Tables,true);
		sbi.setMMFF94StretchBendParameters(molecule, mmff94Tables,false);
		t.setMMFF94TorsionsParameters(molecule, mmff94Tables);
		vdwi.setMMFF94VanDerWaalsParameters(molecule, mmff94Tables);        
		ei.setMMFF94ElectrostaticParameters(molecule, mmff94Tables);
		
		logger = new LoggingTool(this);
	}


	/**
	 *  Evaluate the MMFF94 energy function for a given 3xN point
	 *
	 *@param  coords3d  Current molecule 3xN coordinates.
	 *@return        MMFF94 energy function value.
	 */
	public double energyFunction(GVector coords3d) {

		vdwi.setFunctionMMFF94SumEvdW(coords3d);
		sbi.setFunctionMMFF94SumEBA(coords3d);
		
		energy = bs.functionMMFF94SumEB(coords3d)
			+ ab.functionMMFF94SumEA(coords3d)
			+ sbi.getFunctionMMFF94SumEBA()
			+ t.functionMMFF94SumET(coords3d)
			+ vdwi.getFunctionMMFF94SumEvdW()
			+ ei.functionMMFF94SumEQ(coords3d)
			;
		
		count += 1;
/*		if (count == 1 | count % 50 == 0) {
			System.out.println("count = " + count);
			System.out.println("bs.functionMMFF94SumEB(coords3d) = " + bs.functionMMFF94SumEB(coords3d));
			System.out.println("ab.functionMMFF94SumEA(coords3d) = " + ab.functionMMFF94SumEA(coords3d));
			System.out.println("sbi.functionMMFF94SumEBA(coords3d)) = " + sbi.getFunctionMMFF94SumEBA());
			System.out.println("t.functionMMFF94SumET(coords3d) = " + t.functionMMFF94SumET(coords3d));
			System.out.println("vdwi.functionMMFF94SumEvdW(coords3d) = " + vdwi.getFunctionMMFF94SumEvdW());
			System.out.println("ei.functionMMFF94SumEQ(coords3d) = " + ei.functionMMFF94SumEQ(coords3d));
			System.out.println("energy = " + energy);
		}
		
		moleculeCurrentCoordinates.set(coord3d);
		
*/		return energy;
	}


	/**
	 *  Evaluate the gradient for the MMFF94 energy function in a given 3xN point
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setEnergyGradient(GVector coords3d) {
		//setOrder2ndErrorApproximateEnergyGradient(coords3d);
		
		//logger.debug("coords3d : " + coords3d);
		energyGradient = new GVector(coords3d.getSize());
		
		bs.setGradientMMFF94SumEB(coords3d);
		ab.set2ndOrderErrorApproximateGradientMMFF94SumEA(coords3d);
		//ab.set5thOrderErrorApproximateGradientMMFF94SumEA(coords3d);
		//sbi.setGradientMMFF94SumEBA(coords3d);
		sbi.set2ndOrderErrorApproximateGradientMMFF94SumEBA(coords3d);
		t.set2ndOrderErrorApproximateGradientMMFF94SumET(coords3d);
		//t.set5thOrderErrorApproximateGradientMMFF94SumET(coords3d);
		vdwi.setGradientMMFF94SumEvdW(coords3d);
		ei.setGradientMMFF94SumEQ(coords3d);
		
		//logger.debug("bs.getGradientMMFF94SumEB() = " + bs.getGradientMMFF94SumEB());
		//logger.debug("ab.getGradientMMFF94SumEA() = " + ab.getGradientMMFF94SumEA());
		
		for (int i=0; i < energyGradient.getSize(); i++) {
			energyGradient.setElement(i, 
				bs.getGradientMMFF94SumEB().getElement(i) 
				+ ab.get2ndOrderErrorApproximateGradientMMFF94SumEA().getElement(i)
				//+ sbi.getGradientMMFF94SumEBA().getElement(i)
				+ sbi.get2ndOrderErrorApproximateGradientMMFF94SumEBA().getElement(i)
				+ t.get2ndOrderErrorApproximateGradientMMFF94SumET().getElement(i)
				+ vdwi.getGradientMMFF94SumEvdW().getElement(i)
				+ ei.getGradientMMFF94SumEQ().getElement(i)
				);
		}
	}


	/**
	 *  Get the gradient for the MMFF94 energy function in a given 3xN point
	 *
	 *@return        MMFF94 energy gradient value
	 */
	public GVector getEnergyGradient() {
		return energyGradient;
		//return order2ndErrorApproximateEnergyGradient;
	}


	/**
	 *  Evaluate the 2nd order error approximate gradient for the MMFF94 energy function in a given 3xN point.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setOrder2ndErrorApproximateEnergyGradient(GVector coords3d) {
		//logger.debug("coords3d : " + coords3d);
		order2ndErrorApproximateEnergyGradient.setSize(coords3d.getSize());
		double sigma = Math.pow(0.0000000000000001,0.3333);
		GVector xplusSigma = new GVector(coords3d.getSize());
		GVector xminusSigma = new GVector(coords3d.getSize());
		
		for (int i=0; i < order2ndErrorApproximateEnergyGradient.getSize(); i++) {
			xplusSigma.set(coords3d);
			xplusSigma.setElement(i,coords3d.getElement(i) + sigma);
			xminusSigma.set(coords3d);
			xminusSigma.setElement(i,coords3d.getElement(i) - sigma);
			order2ndErrorApproximateEnergyGradient.setElement(i, (energyFunction(xplusSigma) - energyFunction(xminusSigma)) / (2 * sigma));
		}
		//logger.debug("order2ndErrorApproximateEnergyGradient : " + order2ndErrorApproximateEnergyGradient);
	}


	/**
	 *  Get the 2nd order error approximate gradient for the MMFF94 energy function.
	 *
	 *@return        2nd order error approximate MMFF94 energy gradient value
	 */
	public GVector getOrder2ndErrorApproximateEnergyGradient() {
		return order2ndErrorApproximateEnergyGradient;
	}


	/**
	 *  Evaluate the 5th order error approximate gradient for the MMFF94 energy function, given a 3xN point
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setOrder5thErrorApproximateEnergyGradient(GVector coords3d) {
		//logger.debug("coords3d : " + coords3d);
		order5thErrorApproximateEnergyGradient.setSize(coords3d.getSize());
		double sigma = Math.pow(0.0000000000000001,0.2);
		GVector xplusSigma = new GVector(coords3d.getSize());
		GVector xminusSigma = new GVector(coords3d.getSize());
		GVector xplus2Sigma = new GVector(coords3d.getSize());
		GVector xminus2Sigma = new GVector(coords3d.getSize());
		
		for (int i=0; i < order5thErrorApproximateEnergyGradient.getSize(); i++) {
			xplusSigma.set(coords3d);
			xplusSigma.setElement(i,coords3d.getElement(i) + sigma);
			xminusSigma.set(coords3d);
			xminusSigma.setElement(i,coords3d.getElement(i) - sigma);
			xplus2Sigma.set(coords3d);
			xplus2Sigma.setElement(i,coords3d.getElement(i) + 2 * sigma);
			xminus2Sigma.set(coords3d);
			xminus2Sigma.setElement(i,coords3d.getElement(i) - 2 * sigma);
			order5thErrorApproximateEnergyGradient.setElement(i, (8 * (energyFunction(xplusSigma) - energyFunction(xminusSigma)) - (energyFunction(xplus2Sigma) - energyFunction(xminus2Sigma))) / (12 * sigma));
		}
			
		//logger.debug("order5thErrorApproximateEnergyGradient : " + order5thErrorApproximateEnergyGradient);
	}


	/**
	 *  Get the 5th order error approximate gradient for the MMFF94 energy function.
	 *
	 *@return        5th order error approximate MMFF94 energy gradient value.
	 */
	public GVector getOrder5thErrorApproximateEnergyGradient() {
		return order5thErrorApproximateEnergyGradient;
	}


	/**
	 *  Evaluate the hessian for the MMFF94 energy function in a given 3xN point
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setEnergyHessian(GVector coords3d) {
		
		forHessian = new double[coords3d.getSize() * coords3d.getSize()];
		
		bs.setHessianMMFF94SumEB(coords3d);
		//bs.set2ndOrderErrorApproximateHessianMMFF94SumEB(coords3d);
		//ab.setHessianMMFF94SumEA(coords3d);
		ab.set2ndOrderErrorApproximateHessianMMFF94SumEA(coords3d);
		sbi.setHessianMMFF94SumEBA(coords3d);
		//t.setHessianMMFF94SumET(coords3d);
		t.set2ndOrderErrorApproximateHessianMMFF94SumET(coords3d);
		vdwi.setHessianMMFF94SumEvdW(coords3d);
		ei.setHessianMMFF94SumEQ(coords3d);
		
		for (int i = 0; i < coords3d.getSize(); i++) {
			for (int j = 0; j < coords3d.getSize(); j++) {
				forHessian[i*coords3d.getSize()+j] = 
					bs.getHessianMMFF94SumEB().getElement(i,j)
					//bs.get2ndOrderErrorApproximateHessianMMFF94SumEB().getElement(i,j);
					//+ ab.getHessianMMFF94SumEA().getElement(i,j)	//not working
					+ ab.get2ndOrderErrorApproximateHessianMMFF94SumEA().getElement(i,j)
					+ sbi.getHessianMMFF94SumEBA().getElement(i,j)
					//+ t.getHessianMMFF94SumET().getElement(i,j)	//not working
					+ t.get2ndOrderErrorApproximateHessianMMFF94SumET().getElement(i,j)
					+ vdwi.getHessianMMFF94SumEvdW().getElement(i,j)
					+ ei.getHessianMMFF94SumEQ().getElement(i,j)
					;
			}		
		}
		energyHessian = new GMatrix(coords3d.getSize(), coords3d.getSize(), forHessian);
	}


	/**
	 *  Get the hessian for the MMFF94 energy function.
	 *
	 *@return        MMFF94 energy hessian value
	 */
	public GMatrix getEnergyHessian() {
		return energyHessian;
	}


	/**
	 *  Get the hessian for the MMFF94 energy function.
	 *
	 *@return        MMFF94 energy hessian value
	 */
	public double[] getForEnergyHessian() {
		return forHessian;
	}

}
