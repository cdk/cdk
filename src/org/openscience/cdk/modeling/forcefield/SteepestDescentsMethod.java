package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Steepest Descents Method for optimisation
 *
 *@author     Labarta
 *@created    2004-12-03
 */
public class SteepestDescentsMethod {
	double arbitraryStepSize = 2;
	double stepSize = 2;
	GVector newCoordinates = new GVector(3);
	GVector sk = new GVector(3);


	public SteepestDescentsMethod() {}


	/**
	 *  Constructor for the SteepestDescentsMethod object
	 *
	 *@param  point  Description of the Parameter
	 */
	public SteepestDescentsMethod(GVector point) {
		newCoordinates.setSize(point.getSize());
		sk.setSize(point.getSize());
	}


	/**
	 *  sk=-gK/|gk|
	 *
	 *@param  gK  Description of the Parameter
	 */
	public void setSk(GVector gk) {

		System.out.println("Start sk calculation with gK = " + gk);
		sk.set(gk);
		sk.normalize();
		sk.scale(-1);
		System.out.println("vectorSk : " + sk);
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
			arbitraryStepSize = arbitraryStepSize + (arbitraryStepSize * 0.2);
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
	public GVector setNewCoordinates(GVector oldCoordinates, double stepSizeK) {
		newCoordinates.set(sk);
		newCoordinates.scale(stepSizeK);
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

