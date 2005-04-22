package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Find a direction from a point of the 3xN coordinates space using the conjugate gradient approach.
 *
 *@author     vlabarta
 *@cdk.module     builder3d
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


	public ConjugateGradientMethod(GVector coords3d) {
		vk.setSize(coords3d.getSize());
		vkminus1.setSize(coords3d.getSize());
	}


	/**
	 *  uk = gk gk / gk-1 gk-1
	 *
	 */
	public void setuk(GVector xkminus1, GVector xk,  PotentialFunction forceFieldFunction) {
		forceFieldFunction.setEnergyGradient(xk);
		GVector temporalVector = new GVector(forceFieldFunction.getEnergyGradient());
		uk = temporalVector.dot(temporalVector);
		forceFieldFunction.setEnergyGradient(xkminus1);
		temporalVector.set(forceFieldFunction.getEnergyGradient());
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

