package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Interface for a potential energy function to optimise in cdk/modeling/ForceField
 *
 *@author     labarta
 *@created    December 9, 2004
 */

public interface PotentialFunction {
	String functionShape = "Description of the function";	//	Description of the function
	String gradientShape = "Description of the gradient";	//	Description of the gradient
	GVector wishedPoint = new GVector(3);	//	Coordinates of the wished point. Make GVector size: 3*("Atoms Number")
	double functionInWishedPoint = 0;	//	Function value in a point 
	GVector gradientInWishedPoint = new GVector(3);	//	Gradient value in a point
	double slopeInWishedPoint=1;	// Slope value in a point. The slope is from a line that intercept this point 
					//and have direction equal to the gradient of this point


	/**
	 *  Description of the Method
	 *
	 *@return    Function value in a wished point
	 */
	double functionInPoint();


	/**
	 *  Description of the Method
	 *
	 *@return    Gradient value in a wished point
	 */
	GVector gradientInPoint();


	/**
	 *  Description of the Method
	 *
	 *@return    Slope of the line, that intercept a wished point and have slope equal to the gradient in this point
	 */
	double slopeInPoint();
}


