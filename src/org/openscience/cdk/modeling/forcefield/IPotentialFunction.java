package org.openscience.cdk.modeling.forcefield;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;


/**
 *  Interface for a potential energy function to optimise in cdk/modeling/forcefield
 *
 * @author     vlabarta
 *@cdk.module     builder3d
 * 
 */

public interface IPotentialFunction {
	String energyFunctionShape = "";
	double energy = 0;			//Energy function in a 3xN point
	GVector energyGradient = null;		//Gradient of the energy function in a 3xN point.
	GMatrix energyHessian = null;
	double[] forHessian = null;


	/**
	 *  Evaluate the potential energy function for a given point
	 *
	 *@param  coords3d  Current molecule 3xN coordinates.
	 * @return    Energy function value in the wished 3xN point.
	 */
	double energyFunction(GVector coords3d);


	/**
	 *  Evaluate the gradient of the potential energy function in a given point.
	 *
	 *@param  coords3d  Current molecule 3xN coordinates.
	 */
	void setEnergyGradient(GVector coords3d);


	/**
	 *  Get the gradient of the potential energy function in a given point.
	 *
	 * @return    Gradient energy value in the wished point.
	 */
	GVector getEnergyGradient();


	/**
	 *  Evaluate the hessian of the potential energy function in a given point.
	 *
	 *@param  coords3d  Current molecule 3xN coordinates.
	 */
	void setEnergyHessian(GVector coords3d);


	/**
	 *  Get the hessian of the potential energy function in a given point.
	 *
	 * @return    Hessian energy value in the wished point.
	 */
	GMatrix getEnergyHessian();


	/**
	 *  Get the hessian of the potential energy function in a given point.
	 *
	 *@return        Hessian energy value in the wished point.
	 */
	double[] getForEnergyHessian();

}
