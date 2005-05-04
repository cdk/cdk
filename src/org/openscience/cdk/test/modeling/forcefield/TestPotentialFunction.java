package org.openscience.cdk.test.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;
import org.openscience.cdk.*;
import org.openscience.cdk.modeling.forcefield.*;

/**
 * Potential function for testing forcefield classes.
 *
 * @author     vlabarta
 *
 * @cdk.module test
 */
public class TestPotentialFunction implements PotentialFunction {
	String energyFunctionShape = " f(X,Y) = X2 + 2 Y2 ";
	String gradientShape = " g = ( 2x , 4y )";
	double energy = 0;
	GVector energyGradient = new GVector(3);
	GMatrix energyHessian = null;


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
		energyGradient.setElement(0, 2 * (point.getElement(0)));
		energyGradient.setElement(1, 4 * (point.getElement(1)));
	}


	/**
	 *  Get the gradient for the potential energy function in a given point
	 *
	 *@return        Gradient value
	 */
	public GVector getEnergyGradient() {
		return energyGradient;
	}


	/**
	 *  Evaluate the hessian for the potential energy function in a given point
	 *
	 *@param  point  Current molecule coordinates.
	 */
	public void setEnergyHessian(GVector point) {
		double[] forHessian = {2,0,0,0,4,0,0,0,1};
		energyHessian = new GMatrix(3, 3, forHessian);
	}


	/**
	 *  Get the hessian for the potential energy function in a given point
	 *
	 *@return        Hessian value
	 */
	public GMatrix getEnergyHessian() {
		return energyHessian;
	}

}
