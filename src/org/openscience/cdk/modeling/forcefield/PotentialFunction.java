package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Interface for a potential energy function to optimise in cdk/modeling/ForceField
 *
 * @author     vlabarta
 * 
 */

public interface PotentialFunction {
	double functionInWishedPoint = 0;	//	Function value in a point 
	GVector gradientInWishedPoint = new GVector(3);	//	Gradient value in a point

//	GVector getGradientInWishedPoint();

//	double getFunctionInWishedPoint();

//	double slopeInWishedPoint();
	
	/**
	 *  Evaluate the potential energy function in a given point
	 *
	 * @return    Function value in the wished point
	 */
	double functionInPoint(GVector point);


	/**
	 *  Evaluate the gradient of the potential energy function in a given point
	 *
	 * @return    Gradient value in the wished point
	 */
	GVector gradientInPoint(GVector point);


}


