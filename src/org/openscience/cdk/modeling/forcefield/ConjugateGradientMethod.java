package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Conjugate Gradient Method for optimisation
 *
 *@author     labarta
 *@created    2004-12-16
 */
public class ConjugateGradientMethod {
	double arbitraryStepSize = 2;
	double stepSize = 2;
	GVector newCoordinates = new GVector(3);
	double 탃 = 0;
	GVector vk = new GVector(3);
	GVector vkminus1 = new GVector(3);
	GVector temporalVector = new GVector(3);

	/**
	 *  Constructor for the ConjugateGradientMethod object
	 */
	public ConjugateGradientMethod() { }


	public ConjugateGradientMethod(GVector point) {
		newCoordinates.setSize(point.getSize());
		vk.setSize(point.getSize());
		temporalVector.setSize(point.getSize());

	}


	/**
	 *  uk = gk gk / gk-1 gk-1
	 *
	 */
	public void 탃Calculation(GVector xkminus1, GVector xk,  PotentialFunction forceFieldFunction) {
		temporalVector.set(forceFieldFunction.gradientInPoint(xk));
		System.out.println("temporalVector = " + temporalVector);
		탃 = temporalVector.dot(temporalVector);
		System.out.println("탃 = " + 탃);
		temporalVector.set(forceFieldFunction.gradientInPoint(xkminus1));
		System.out.println("temporalVector = " + temporalVector);
		탃 = 탃 / temporalVector.dot(temporalVector);
		System.out.println("temporalVector = " + temporalVector);
		System.out.println("탃 = " + 탃);
		return;
	}

	/**
	 *  vk=-gk + 탃 vk-1
	 *
	 *@param  gK  Description of the Parameter
	 */
	public void setvk(GVector gk, int iterN) {

		if (iterN != 1) {
			vkminus1.set(vk);
			System.out.println("Start vk calculation with gK = " + gk + " and vk-1 = " + vkminus1);
			vk.set(gk);
			System.out.println("vector vk : vk.set(gk) : " + vk);
			vk.scale(-1);
			System.out.println("vector vk : vk.scale(-1) : " + vk);
			vkminus1.scale(탃);
			System.out.println("vector vk : vkminus1.scale(탃) : " + vkminus1);
			vk.add(vkminus1);
			System.out.println("vector vk : vk.add(vkminus1) : " + vk);
		}
		else {
		System.out.println("Start vk calculation with gk = " + gk);
		vk.set(gk);
		//vk.normalize();
		vk.scale(-1);
		System.out.println("vectorvk : " + vk);
		}
		return;
	}


	/**
	 *  Method useful for Arbitrary Step approach: Compare f(Xk) with f(Xk+1) for
	 *  decide the next step size.
	 *
	 *@param  kPoint              Coordinates of step k
	 *@param  kplus1point         Coordinates of step k+1
	 *@param  forceFieldFunction  Force field function
	 */
	public void setArbitraryStepSize(GVector kPoint, GVector kplus1Point, PotentialFunction forceFieldFunction) {

		System.out.println("f(Xk-1) : " + forceFieldFunction.functionInPoint(kPoint));
		System.out.println("f(Xk) : " + forceFieldFunction.functionInPoint(kplus1Point));
		if (forceFieldFunction.functionInPoint(kplus1Point) < forceFieldFunction.functionInPoint(kPoint)) {
			System.out.println("The energy drops and the arbitrary step size will be increase by 20% to");
			System.out.println("accelerate the convergence");
			arbitraryStepSize = 1.2 * arbitraryStepSize;
		} else {
			System.out.println("the energy was risen, the minimum was overshot, then the arbitrary step size is halved");
			arbitraryStepSize = arbitraryStepSize / 2;
		}
		System.out.println("Arbitrary step size : " + arbitraryStepSize);
		return;
	}


	/**
	 *  Gets the arbitraryStepSize attribute of the SteepestDescentsMethod object
	 *
	 *@return    The arbitraryStepSize value
	 */
	public double getArbitraryStepSize() {
		return arbitraryStepSize;
	}


	/**
	 *  xk+1= Xk + Lambdak Sk
	 *
	 *@param  oldPoint   Old coordinates of the atoms, k step
	 *@param  stepSizeK  Description of the Parameter
	 *@return            New coordinates of the atoms, k+1 step
	 */
	public GVector newCoordinatesCalculation(GVector oldCoordinates, double stepSizeK) {
		newCoordinates.set(vk);
		System.out.println("New coordinates : newCoordinates.set(vk) : " + newCoordinates);
		newCoordinates.scale(stepSizeK);
		newCoordinates.scale(-1);
		System.out.println("New coordinates : newCoordinates.scale(stepSizeK) : " + newCoordinates);
		newCoordinates.add(oldCoordinates);
		System.out.println("New coordinates : " + newCoordinates);
		return newCoordinates;
	}


	/**
	 *  Search line approach: Obtain line of search (Interception xk and slope like
	 *  Sk) *** Waiting to be completed completed
	 *
	 *@param  interception          xk
	 *@param  interceptionGradient  Sk
	 *@return                       Description of the Return Value
	 */
	public String searchLine(GVector interception, GVector interceptionGradient) {
		String lineEquation = "";
		// System.out.println("Slope of the line in the direction of the gradient in " + interception + " : " + forceFieldFunction.slopeInPoint(kCoordinates));
		return lineEquation;
	}


	/**
	 *  Search line approach: Look for 3 points along the line where the energy of
	 *  the middle point is lower than the energy of the two outer points ***
	 *  Waiting to be completed
	 *
	 *@param  line  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public String linePointsSeeking(String line) {

		String lineSegment = "";
		return lineSegment;
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
	 *  Sets the stepSize attribute of the SteepestDescentsMethod object
	 *
	 *@param  newStepSize  The new stepSize value
	 */
	public void setStepSize(double newStepSize) {
		stepSize = newStepSize;
	}


	/**
	 *  Gets the stepSize attribute of the SteepestDescentsMethod object
	 *
	 *@return    The stepSize value
	 */
	public double getStepSize() {
		return stepSize;
	}


}

