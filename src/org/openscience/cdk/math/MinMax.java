package org.openscience.cdk.math;

import java.lang.Math.*;

/* @author     Yongquan Han 
 * @Copyright (c) 2000-2001 SENECA Project, MPI of Chemical Ecology, Jena.
 * All Rights Reserved.
 */
public class MinMax {

	/**
	 * Analog of Math.max that returns the largest int value in an array of ints
	 *
	 * @param   values  the values to be searched for the largest value among them
	 * @return   the largest value among a set of given values  
	 */
	public static double max(double[] values)
	{
		double max = values[0];
		for (int f = 0; f < values.length; f++)
			if (values[f] > max)
				max = values[f];
			return max;
	}

	/**
	 * Analog of Math.min that returns the largest int value in an array of ints
	 *
	 * @param   values  the values to be searched for the smallest value among them
	 * @return   the smallest value among a set of given values  
	 */
	public static double min(double[] values)
	{
		double min = values[0];
		for (int f = 0; f < values.length; f++)
			if (values[f] < min)
				min = values[f];
			return min;
	}		
}




