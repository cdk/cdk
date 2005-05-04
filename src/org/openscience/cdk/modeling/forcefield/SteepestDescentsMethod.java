package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  Find a direction from a point of the coordinates space using the steepest descents approach.
 *
 *@author     vlabarta
 *@cdk.module     builder3d
 *
 */
public class SteepestDescentsMethod {
	GVector sk = new GVector(3);
	private LoggingTool logger;


	public SteepestDescentsMethod() {        
		logger = new LoggingTool(this);
	}


	/**
	 *  Constructor for the SteepestDescentsMethod object
	 *
	 *@param  coords3d  Coordinates from current point
	 */
	public SteepestDescentsMethod(GVector coords3d) {
		sk.setSize(coords3d.getSize());
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
		//logger.debug("vectorS" + iterNumber + " = " + sk);
		return;
	}

}

