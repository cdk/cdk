package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Find a direction from a point of the coordinates space using the conjugate gradient approach.
 *
 *@author     vlabarta
 *
 */
public class ConjugateGradientMethod {
	double arbitraryStepSize = 2;
	double stepSize = 2;
	GVector newCoordinates = new GVector(3);
	double 탃 = 0;
	GVector vk = new GVector(3);
	GVector vkminus1 = new GVector(3);

	/**
	 *  Constructor for the ConjugateGradientMethod object
	 */
	public ConjugateGradientMethod() { }


	public ConjugateGradientMethod(GVector point) {
		newCoordinates.setSize(point.getSize());
		vk.setSize(point.getSize());
	}


	/**
	 *  uk = gk gk / gk-1 gk-1
	 *
	 */
	public void set탃(GVector xkminus1, GVector xk,  PotentialFunction forceFieldFunction) {
		GVector temporalVector = new GVector(forceFieldFunction.gradientInPoint(xk));
		//System.out.println("temporalVector = " + temporalVector);
		탃 = temporalVector.dot(temporalVector);
		//System.out.println("탃 = " + 탃);
		temporalVector.set(forceFieldFunction.gradientInPoint(xkminus1));
		//System.out.println("temporalVector = " + temporalVector);
		탃 = 탃 / temporalVector.dot(temporalVector);
		//System.out.println("temporalVector = " + temporalVector);
		System.out.println("탃 = " + 탃);
		return;
	}

	/**
	 *  vk=-gk + 탃 vk-1
	 *
	 * @param  gk  Gradient at coordinates Xk
	 * @param  iterNumber  Iteration number
	*/
	public void setvk(GVector gk, int iterNumber) {

		if (iterNumber != 1) {
			vkminus1.set(vk);
			System.out.println("Start vk calculation with gK = " + gk + " and vk-1 = " + vkminus1);
			vk.set(gk);
			//System.out.println("vector vk : vk.set(gk) : " + vk);
			vk.scale(-1);
			//System.out.println("vector vk : vk.scale(-1) : " + vk);
			vkminus1.scale(탃);
			//System.out.println("vector vk : vkminus1.scale(탃) : " + vkminus1);
			vk.add(vkminus1);
			//System.out.println("vector vk : vk.add(vkminus1) : " + vk);
			System.out.println("vector vk : " + vk);
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
	 *@param  kPoint              Coordinates from current point, xk
	 *@param  kplus1Point         Coordinates from step k+1
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
	 * @return    The arbitraryStepSize value
	 */
	public double getArbitraryStepSize() {
		return arbitraryStepSize;
	}


	/**
	 *  xk+1= Xk + Lambdak Sk
	 *
	 *@param  oldCoordinates   Old coordinates of the atoms, k step
	 *@param  stepSizeK  Step size estimated
	 *@return            New coordinates of the atoms, k+1 step
	 */
	public GVector setNewCoordinates(GVector oldCoordinates, double stepSizeK) {
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

