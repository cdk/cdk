package org.openscience.cdk.test.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
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
	 */

	public TestPotentialFunction() { }


	/**
	 *  Constructor for the TestPotentialFunction object
	 *
	 *@param  point  Coordinates from current point
	 */
	public TestPotentialFunction(GVector point) {
		System.out.println("function shape : " + functionShape);
		System.out.println("gradient shape : " + gradientShape);
		gradientInWishedPoint.setSize(point.getSize());
		//System.out.println("gradientInWishedPoint.getSize() : " + gradientInWishedPoint.getSize());
	}


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


//	public getFunctionInWishedPoint

	/**
	 *  Evaluate the gradient for the potential energy function in a given point
	 *
	 *@param  point  Coordinates from current point
	 *@return        Gradient value
	 */
	public GVector gradientInPoint(GVector point) {
	//	System.out.println("Evaluation of the gradient in point: " + point);
		gradientInWishedPoint.setElement(0, 2 * (point.getElement(0)));
		gradientInWishedPoint.setElement(1, 4 * (point.getElement(1)));

		/*
		 *  firstDerivativeInWichedPoint.setElementAt(Point.elementAt(0), 0);
		 *  firstDerivativeInWichedPoint.setElementAt(Point.elementAt(1), 1);
		 *  System.out.println("First Derivative at point " + Point + "is " + firstDerivativeInWichedPoint);
		 */
		return gradientInWishedPoint;
	}

}

