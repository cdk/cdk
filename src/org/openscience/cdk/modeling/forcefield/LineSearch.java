package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Search line approach: Obtain line where the function decrease
 *  (The line have Interception xk and slope on the direction of vk, e.g. the gradient for steepest descents method)
 *
 *@author     labarta
 *@created    4-01-2005
 */
public class LineSearch {
	double stepSize = 2;
	GVector kplus1Point1 = new GVector(3);
	GVector kplus1Point2 = new GVector(3);
	GVector kplus1Point3 = new GVector(3);
	GVector direction = new GVector(3);
	double minimumLambda = 0;
	double maximumLambda = 2;


	/**
	 *  Constructor for the LineSearch object
	 */
	public LineSearch() { }


	/**
	 *  Constructor for the LineSearch object
	 *
	 *@param  point  Description of the Parameter
	 */
	public LineSearch(GVector point) {
		kplus1Point1.setSize(point.getSize());
		kplus1Point2.setSize(point.getSize());
		kplus1Point3.setSize(point.getSize());
		direction.setSize(point.getSize());
	}


	/**
	 *  Search line approach: Look for 3 points along the line where the energy of
	 *  the middle point is lower than the energy of the two outer points
	 *  The bracketing phase determines the range of points
	 *  on the line to be searched. The bracket corresponds to an interval
	 *  specifying the range of values of Lambda.
	 *
	 *@param  kPoint              Description of the Parameter
	 *@param  directionVector     Description of the Parameter
	 *@param  forceFieldFunction  Description of the Parameter
	 */
	public void bracketingTheMinimum(GVector kPoint, GVector directionVector, PotentialFunction forceFieldFunction) {

		System.out.println("Start line search: ls.bracketingTheMinimum");
		stepSize = 2;
		System.out.println("f(Xk) : " + forceFieldFunction.functionInPoint(kPoint));
		System.out.println("Initial step size : lambda = " + stepSize);

		kplus1Point1.set(kPoint);
		minimumLambda = 0;
		kplus1Point2.set(kPoint);
		direction.set(directionVector);
		direction.scale(stepSize);
		kplus1Point2.add(direction);
		maximumLambda = stepSize;

		System.out.println("forceFieldFunction.functionInPoint(kplus1Point2) = " + forceFieldFunction.functionInPoint(kplus1Point2));
		System.out.println("forceFieldFunction.functionInPoint(kPoint) = " + forceFieldFunction.functionInPoint(kPoint));
		//System.out.println("if (forceFieldFunction.functionInPoint(kplus1Point2) < forceFieldFunction.functionInPoint(kPoint))");

		boolean finish = false;
		if (forceFieldFunction.functionInPoint(kplus1Point2) < forceFieldFunction.functionInPoint(kPoint)) {

			System.out.println("The energy decrease with the current step size. The stepsize will be increase by 20%");
			stepSize = 1.2 * stepSize;
			kplus1Point3.set(kPoint);
			direction.set(directionVector);
			direction.scale(stepSize);
			kplus1Point3.add(direction);
			maximumLambda = stepSize;

			System.out.println("kplus1Point1 = " + kplus1Point1);
			System.out.println("kplus1Point2 = " + kplus1Point2);
			System.out.println("kplus1Point3 = " + kplus1Point3);
			System.out.println("minimumLambda = " + minimumLambda);
			System.out.println("maximumLambda = " + maximumLambda);

			while (finish == false) {

				if (forceFieldFunction.functionInPoint(kplus1Point3) > forceFieldFunction.functionInPoint(kplus1Point2)) {
					finish = true;
				} else {
					kplus1Point1.set(kplus1Point2);
					minimumLambda = stepSize / 1.2;
					kplus1Point2.set(kplus1Point3);
					stepSize = 1.2 * stepSize;
					kplus1Point3.set(kPoint);
					direction.set(directionVector);
					direction.scale(stepSize);
					kplus1Point3.add(direction);
					maximumLambda = stepSize;

					System.out.println("kplus1Point1 = " + kplus1Point1);
					System.out.println("kplus1Point2 = " + kplus1Point2);
					System.out.println("kplus1Point3 = " + kplus1Point3);
					System.out.println("minimumLambda = " + minimumLambda);
					System.out.println("maximumLambda = " + maximumLambda);
				}
			}
		} else {
			while (finish == false) {
				System.out.println("The energy increase with the current step size. The step size will be halve");
				kplus1Point3.set(kplus1Point2);
				maximumLambda = stepSize;
				stepSize = stepSize / 2;
				kplus1Point2.set(kPoint);
				direction.set(directionVector);
				direction.scale(stepSize);
				kplus1Point2.add(direction);

				System.out.println("kplus1Point1 = " + kplus1Point1);
				System.out.println("kplus1Point2 = " + kplus1Point2);
				System.out.println("kplus1Point3 = " + kplus1Point3);
				System.out.println("minimumLambda = " + minimumLambda);
				System.out.println("maximumLambda = " + maximumLambda);

				if (forceFieldFunction.functionInPoint(kplus1Point2) < forceFieldFunction.functionInPoint(kPoint)) {
					finish = true;
				}
				if (maximumLambda < 0.01) {
					finish = true;
				}
			}
			/*
			 *  if ((forceFieldFunction.functionInPoint(kplus1Point2) < forceFieldFunction.functionInPoint(kPoint)) &
			 *  (forceFieldFunction.functionInPoint(kplus1Point2) < forceFieldFunction.functionInPoint(kplus1Point3))) {
			 *  }
			 *  else {
			 *  }
			 */
		}

		return;
	}


	/**
	 *  Gets the stepSize attribute of the LineSearch object
	 *
	 *@return    The stepSize value
	 */
	public double getStepSize() {
		return stepSize;
	}


	/**
	 *  xk+1= Xk + Lambdak Sk
	 *
	 *@param  oldCoordinates   Description of the Parameter
	 *@param  currentStepSize  Description of the Parameter
	 *@return                  New coordinates of the atoms, k+1 step
	 */
	public GVector coordinatesCalculation(GVector oldCoordinates, double currentStepSize) {
		GVector coordinates = new GVector(oldCoordinates.getSize());
		return coordinates;
	}


	/**
	 *  Search line approach: Minimize The fitted function on the line *** Waiting
	 *  to be completed
	 *
	 *@param  function  Description of the Parameter
	 *@param  segment   Description of the Parameter
	 *@return           Description of the Return Value
	 */
	public Vector minimizeFitFunction(String function, String segment) {

		Vector fitFunctionMinimum = new Vector();
		return fitFunctionMinimum;
	}


	/**
	 *  Search line approach: Fit a function of order p with the three point of the
	 *  line // To check comment *** Waiting to be completed
	 *
	 *@param  point1  Description of the Parameter
	 *@param  point2  Description of the Parameter
	 *@param  point3  Description of the Parameter
	 *@param  order   Description of the Parameter
	 *@return         Description of the Return Value
	 */
	public String fitFunction(Vector point1, Vector point2, Vector point3, byte order) {

		String fitFunctionShape = "";
		return fitFunctionShape;
	}


	/**
	 */
	public void setStepSize() {
		return;
	}

}

