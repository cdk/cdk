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
public class TestPotentialFunction {
	// instants variables
	String functionShape = " f(X,Y) = X2 + 2 Y2 ";
	String gradientShape = " g = ( 2x , 4y )";
	double functionInWishedPoint = 0;
	GVector wishedPoint = new GVector(3);
	GVector gradientInWishedPoint = new GVector(3);
	double slopeInWishedPoint = 1;


	// constructors

	/**
	 *  Constructor for the FunctionExample object
	 *
	 *@param  point  GVector stores 3D coordinates
	 */
	
	 public TestPotentialFunction() {
	 }
	 
	 public TestPotentialFunction(GVector point) {

		wishedPoint.setSize(point.getSize());
		wishedPoint.set(point);

		gradientInWishedPoint.setSize(point.getSize());
		gradientInWishedPoint.set(point);

	}


	// methods

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public double evaluateInPoint(GVector point) {
		functionInWishedPoint = ((point.getElement(0)) * (point.getElement(0))) + (2 * (point.getElement(1)) * (point.getElement(1)));
		return functionInWishedPoint;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public GVector gradientInPoint(GVector point) {
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
	 *@return    Description of the Return Value
	 */
	public double slopeInPoint(GVector point) {
		slopeInWishedPoint = (gradientInWishedPoint.getElement(1)) / (gradientInWishedPoint.getElement(0));
		return slopeInWishedPoint;
	}

}
