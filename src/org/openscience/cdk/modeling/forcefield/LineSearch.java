package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  The line-search method searches along the line containing the current point, xk, parallel to the search direction
 *
 *@author     vlabarta
 *
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
	 *@param  point  Coordinates from current point
	 */
	public LineSearch(GVector point) {
		kplus1Point1.setSize(point.getSize());
		kplus1Point2.setSize(point.getSize());
		kplus1Point3.setSize(point.getSize());
		direction.setSize(point.getSize());
	}


	/**
	 *  Bracketing the minimum: The bracketing phase determines the range of points on the line to be searched.
	 *  Look for 3 points along the line where the energy of the middle point is lower than the energy of the two outer points.
	 *  The bracket corresponds to an interval specifying the range of values of Lambda.
	 *
	 *@param  kPoint              Current point, xk
	 *@param  directionVector     Search direction
	 *@param  forceFieldFunction  Potential energy function
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
				if (maximumLambda < 0.001) {
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
	 *@param  oldCoordinates   Coordinates of the previous step, k
	 *@param  currentStepSize  Step size estimated
	 *@return                  New coordinates of the atoms, k+1 step
	 */
	public GVector setCoordinates(GVector oldCoordinates, double currentStepSize) {
		GVector coordinates = new GVector(oldCoordinates.getSize());
		return coordinates;
	}


	/**
	 *  Minimize The fitted function on the line *** Waiting to be completed
	 *
	 *@return           Minimun of the fitted function on the line segment
	 */
	public GVector minimizeFitFunction() {

		GVector fitFunctionMinimum = new GVector(3);
		return fitFunctionMinimum;
	}


	/**
	 *  Fit a function of order p using the three points in the line *** Waiting to be completed
	 *
	 *@param  point1  Point 1 in the line
	 *@param  point2  Point 2 in the line
	 *@param  point3  Point 3 in the line
	 *@param  order   Order of the function to be fit
	 */
	public void fitFunction(Vector point1, Vector point2, Vector point3, byte order) {

		return;
	}


	/**
	 */
	public void setStepSize() {
		return;
	}

}

