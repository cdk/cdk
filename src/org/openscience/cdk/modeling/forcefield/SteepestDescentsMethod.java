package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Find a direction from a point of the coordinates space using the steepest descents approach.
 *
 *@author     vlabarta
 *
 */
public class SteepestDescentsMethod {
	GVector sk = new GVector(3);


	public SteepestDescentsMethod() {}


	/**
	 *  Constructor for the SteepestDescentsMethod object
	 *
	 *@param  point  Coordinates from current point
	 */
	public SteepestDescentsMethod(GVector point) {
		sk.setSize(point.getSize());
	}


	/**
	 *  sk=-gK/|gk|
	 *
	 * @param  gk  Gradient at coordinates Xk
	 * @param  iterNumber  Iteration number
	 */
	public void setSk(GVector gk, int iterNumber) {

		sk.set(gk);
		sk.normalize();
		sk.scale(-1);
		//System.out.println("vectorS" + iterNumber + " = " + sk);
		return;
	}

}

