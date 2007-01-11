/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2000-2007  Yongquan Han
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.math;

/**
 * @author     Yongquan Han
 * @cdk.module standard
 */
public class MinMax {

	/**
	 * Analog of Math.max that returns the largest int value in an array of ints.
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
	 * Analog of Math.min that returns the largest int value in an array of ints.
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




