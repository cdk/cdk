package org.openscience.cdk.test.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;
import org.openscience.cdk.modeling.forcefield.*;

/**
 *  Potential function for testing
 *
 *@author     labarta
 *@created    2004-12-03
 */
public class TestPotentialFunction implements PotentialFunction {
	String functionShape = " f(X,Y) = X2 + 2 Y2 ";
	String gradientShape = " g = ( 2x , 4y )";
	double functionInWishedPoint = 0;
	GVector gradientInWishedPoint = new GVector(3);
	double slopeInWishedPoint = 1;


	/**
	 *  Constructor for the TestPotentialFunction object
	 *
	 */

	public TestPotentialFunction() { }


	/**
	 *  Constructor for the TestPotentialFunction object
	 *
	 *@param  point  Description of the Parameter
	 */
	public TestPotentialFunction(GVector point) {
		System.out.println("function shape : " + functionShape);
		System.out.println("gradient shape : " + gradientShape);
		gradientInWishedPoint.setSize(point.getSize());
		//System.out.println("gradientInWishedPoint.getSize() : " + gradientInWishedPoint.getSize());
	}


	/**
	 *  Description of the Method
	 *
	 *@param  point  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	public double functionInPoint(GVector point) {
		functionInWishedPoint = ((point.getElement(0)) * (point.getElement(0))) + (2 * (point.getElement(1)) * (point.getElement(1)));
		return functionInWishedPoint;
	}


//	public getFunctionInWishedPoint

	/**
	 *  Description of the Method
	 *
	 *@param  point  Description of the Parameter
	 *@return        Description of the Return Value
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


	/**
	 *  Description of the Method
	 *
	 *@param  point  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	public double slopeInPoint(GVector point) {
		slopeInWishedPoint = (gradientInWishedPoint.getElement(1)) / (gradientInWishedPoint.getElement(0));
		return slopeInWishedPoint;
	}
}

