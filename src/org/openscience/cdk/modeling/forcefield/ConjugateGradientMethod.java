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
	double uk = 0;
	GVector vk = new GVector(3);
	GVector vkminus1 = new GVector(3);

	/**
	 *  Constructor for the ConjugateGradientMethod object
	 */
	public ConjugateGradientMethod() { }


	public ConjugateGradientMethod(GVector point) {
		vk.setSize(point.getSize());
		vkminus1.setSize(point.getSize());
	}


	/**
	 *  uk = gk gk / gk-1 gk-1
	 *
	 */
	public void setuk(GVector xkminus1, GVector xk,  PotentialFunction forceFieldFunction) {
		GVector temporalVector = new GVector(forceFieldFunction.gradientInPoint(xk));
		uk = temporalVector.dot(temporalVector);
		temporalVector.set(forceFieldFunction.gradientInPoint(xkminus1));
		uk = uk / temporalVector.dot(temporalVector);
		//System.out.println("uk = " + uk);
		return;
	}

	/**
	 *  vk=-gk + uk vk-1
	 *
	 * @param  gk  Gradient at coordinates Xk
	 * @param  iterNumber  Iteration number
	*/
	public void setvk(GVector gk, int iterNumber) {

		if (iterNumber != 1) {
			vkminus1.set(vk);
			vk.set(gk);
			vk.scale(-1);
			vkminus1.scale(uk);
			vk.add(vkminus1);
			//System.out.println("vector vk : " + vk);
		}
		else {
		vk.set(gk);
		vk.scale(-1);
		//System.out.println("vectorvk : " + vk);
		}
		return;
	}

}

