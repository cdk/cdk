package org.openscience.cdk.test.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;
import org.openscience.cdk.*;
import org.openscience.cdk.modeling.forcefield.*;

/**
 *  Potential function for testing forcefield classes
 *
 *@author     vlabarta
 *
 */
public class TestPotentialFunction implements PotentialFunction {
	String functionShape = " f(X,Y) = X2 + 2 Y2 ";
	String gradientShape = " g = ( 2x , 4y )";
	double functionInWishedPoint = 0;
	GVector gradientInWishedPoint = new GVector(3);


	/**
	 *  Constructor for the TestPotentialFunction object
	 *
	 *@param  point  Coordinates from current point
	 */
	public TestPotentialFunction() {}


	/**
	 *  Evaluate the potential energy function in a given point
	 *
	 *@param  point  Coordinates from current point
	 *@return        Function value
	 */
	public double functionInPoint(GVector point) {
		functionInWishedPoint = ((point.getElement(0)) * (point.getElement(0))) + (2 * (point.getElement(1)) * (point.getElement(1)));
		return functionInWishedPoint;
	}


	/**
	 *  Evaluate the gradient for the potential energy function in a given point
	 *
	 *@param  point  Coordinates from current point
	 *@return        Gradient value
	 */
	public GVector gradientInPoint(GVector point) {
		gradientInWishedPoint.setElement(0, 2 * (point.getElement(0)));
		gradientInWishedPoint.setElement(1, 4 * (point.getElement(1)));
		return gradientInWishedPoint;
	}


	/**
	 *  Evaluate the hessian for the potential energy function in a given point
	 *
	 *@param  point  Coordinates from current point
	 *@return        Hessian value
	 */
	public GMatrix hessianInPoint(GVector point) {
		double[] forHessian = {2,0,0,0,4,0,0,0,1};
		GMatrix hessianInWishedPoint = new GMatrix(3, 3, forHessian);
		return hessianInWishedPoint;
	}

}
