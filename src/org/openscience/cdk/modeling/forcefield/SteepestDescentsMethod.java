package org.openscience.cdk.modeling.forcefield;

import javax.vecmath.GVector;

//import org.openscience.cdk.tools.LoggingTool;


/**
 *  Find a direction from a point of the coordinates space using the steepest descents approach.
 *
 *@author     vlabarta
 *@cdk.module     forcefield
 *
 */
public class SteepestDescentsMethod {
	GVector sk = null;
	//private LoggingTool logger;


	public SteepestDescentsMethod() {        
		//logger = new LoggingTool(this);
	}


	/**
	 *  Constructor for the SteepestDescentsMethod object
	 *
	 *@param  coords3d  Coordinates from current point
	 */
	public SteepestDescentsMethod(GVector coords3d) {
		sk = new GVector(coords3d.getSize());
	}


	/**
	 *  sk=-gK/|gk|
	 *
	 * @param  gk  Gradient at coordinates Xk
	 */
	public void setSk(GVector gk) {
		sk.set(gk);
		sk.normalize();
		sk.scale(-1);
		//logger.debug("vectorS = " + sk);
		return;
	}

}

