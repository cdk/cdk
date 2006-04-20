package org.openscience.cdk.test.modeling.forcefield;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

import org.openscience.cdk.modeling.forcefield.IPotentialFunction;

/**
 * Potential function for testing forcefield classes.
 *
 * @author     vlabarta
 *
 * @cdk.module test-forcefield
 */
public class TestPotentialFunction implements IPotentialFunction {
	String energyFunctionShape = " f(X,Y) = X2 + 2 Y2 ";
	String gradientShape = " g = ( 2x , 4y )";
	double energy = 0;
	GVector energyGradient = null;
	GVector order2ndErrorApproximateGradient = null;
	GMatrix energyHessian = null;
	double[] forHessian = null;
	GMatrix order2ndErrorApproximateHessian = null;
	double[] forOrder2ndErrorApproximateHessian = null;


	/**
	 *  Constructor for the TestPotentialFunction object
	 *
	 *@param  point  Current molecule coordinates.
	 */
	public TestPotentialFunction() {}


	/**
	 *  Evaluate the potential energy function in a given point.
	 *
	 *@param  point  Current molecule coordinates.
	 *@return        Function value
	 */
	public double energyFunction(GVector point) {
		energy = ((point.getElement(0)) * (point.getElement(0))) + (2 * (point.getElement(1)) * (point.getElement(1)));
		return energy;
	}


	/**
	 *  Evaluate the gradient for the potential energy function in a given point
	 *
	 *@param  point  Current molecule coordinates.
	 */
	public void setEnergyGradient(GVector point) {
		/*energyGradient = new GVector(point.getSize());
		energyGradient.setElement(0, 2 * (point.getElement(0)));
		energyGradient.setElement(1, 4 * (point.getElement(1)));
		*/
		set2ndOrderErrorApproximateGradient(point);
	}


	/**
	 *  Get the gradient for the potential energy function in a given point
	 *
	 *@return        Gradient value
	 */
	public GVector getEnergyGradient() {
		//return energyGradient;
		return order2ndErrorApproximateGradient;
	}


	/**
	 *  Evaluate a 2nd order approximation of the gradient
	 *
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateGradient(GVector point) {
		order2ndErrorApproximateGradient = new GVector(point.getSize());
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xplusSigma = new GVector(point.getSize());
		GVector xminusSigma = new GVector(point.getSize());
		
		for (int m = 0; m < order2ndErrorApproximateGradient.getSize(); m++) {
			xplusSigma.set(point);
			xplusSigma.setElement(m,point.getElement(m) + sigma);
			xminusSigma.set(point);
			xminusSigma.setElement(m,point.getElement(m) - sigma);
			order2ndErrorApproximateGradient.setElement(m,(energyFunction(xplusSigma) - energyFunction(xminusSigma)) / (2 * sigma));
		}
			
		//logger.debug("order2ndErrorApproximateGradient : " + order2ndErrorApproximateGradient);
	}


	/**
	 *  Get the 2nd order error approximate gradient of the angle bending term.
	 *
	 *
	 *@return           Angle bending 2nd order error approximate gradient value.
	 */
	public GVector get2ndOrderErrorApproximateGradient() {
		return order2ndErrorApproximateGradient;
	}


	/**
	 *  Evaluate the hessian for the potential energy function in a given point
	 *
	 *@param  point  Current molecule coordinates.
	 */
	public void setEnergyHessian(GVector point) {
		/*double[] forHessian = {2,0,0,0,4,0,0,0,1};
		energyHessian = new GMatrix(3, 3, forHessian);
		*/
		set2ndOrderErrorApproximateHessian(point);
	}


	/**
	 *  Get the hessian for the potential energy function in a given point
	 *
	 *@return        Hessian value
	 */
	public GMatrix getEnergyHessian() {
		//return energyHessian;
		return order2ndErrorApproximateHessian;
	}


	/**
	 *  Get the hessian of the potential energy function in a given point.
	 *
	 *@return        Hessian energy value in the wished point.
	 */
	 public double[] getForEnergyHessian() {
		 //return forHessian;
		 return forOrder2ndErrorApproximateHessian;
	 }


	/**
	 *  Evaluate a 2nd order approximation of the Hessian
	 *  given the atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateHessian(GVector coord3d) {
		forOrder2ndErrorApproximateHessian = new double[coord3d.getSize() * coord3d.getSize()];
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		GVector gradientAtXplusSigma = new GVector(coord3d.getSize());
		GVector gradientAtXminusSigma = new GVector(coord3d.getSize());
		
		int forHessianIndex;
		for (int i = 0; i < coord3d.getSize(); i++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(i,coord3d.getElement(i) + sigma);
			setEnergyGradient(xplusSigma);
			gradientAtXplusSigma.set(this.getEnergyGradient());
			xminusSigma.set(coord3d);
			xminusSigma.setElement(i,coord3d.getElement(i) - sigma);
			setEnergyGradient(xminusSigma);
			gradientAtXminusSigma.set(this.getEnergyGradient());
			for (int j = 0; j < coord3d.getSize(); j++) {
				forHessianIndex = i*coord3d.getSize()+j;
				forOrder2ndErrorApproximateHessian[forHessianIndex] = (gradientAtXplusSigma.getElement(j) - gradientAtXminusSigma.getElement(j)) / (2 * sigma);
				//(energyFunction(xplusSigma) - 2 * fx + energyFunction(xminusSigma)) / Math.pow(sigma,2);
			}
		}
		forOrder2ndErrorApproximateHessian[8] = 1;
		order2ndErrorApproximateHessian = new GMatrix(coord3d.getSize(), coord3d.getSize());
		order2ndErrorApproximateHessian.set(forOrder2ndErrorApproximateHessian);
		//logger.debug("order2ndErrorApproximateHessian : " + order2ndErrorApproximateHessian);
	}


	/**
	 *  Get the 2nd order error approximate Hessian
	 *
	 *
	 *@return           2nd order error approximate Hessian value.
	 */
	public GMatrix get2ndOrderErrorApproximateHessian() {
		return order2ndErrorApproximateHessian;
	}

}
