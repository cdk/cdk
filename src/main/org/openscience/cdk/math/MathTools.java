/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Class providing convenience methods for simple mathematical operations.
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.math.MathToolsTest")
public class MathTools {
	
	/**
	 * Analog of Math.max that returns the largest double value in an array of dpubles.
	 *
	 * @param   values  the values to be searched for the largest value among them
	 * @return   the largest value among a set of given values  
	 */
    @TestMethod("testMax_arraydouble")
    public static double max(double[] values)
	{
		double max = values[0];
        for (double value : values)
            if (value > max)
                max = value;
			return max;
	}

	/**
	 * Analog of Math.min that returns the largest double value in an array of double.
	 *
	 * @param   values  the values to be searched for the smallest value among them
	 * @return   the smallest value among a set of given values  
	 */
    @TestMethod("testMin_arraydouble")
    public static double min(double[] values)
	{
		double min = values[0];
        for (double value : values)
            if (value < min)
                min = value;
			return min;
	}
	
	/**
	 * Analog of Math.max that returns the largest int value in an array of ints
	 **/
    @TestMethod("testMax_arrayint")
    public static int max(int[] values)
	{
		int max = values[0];
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
		return max;
	}

	/**
	 * Analog of Math.max that returns the largest int value in an array of ints
	 **/
    @TestMethod("testMin_arrayint")
    public static int min(int[] values)
	{
		int min = values[0];
        for (int value : values) {
            if (value < min) {
                min = value;
            }
        }
		return min;
	}

    @TestMethod("testIsOdd_int")
    public static boolean isOdd(int intValue) {
        return !MathTools.isEven(intValue);
    }

    @TestMethod("testIsEven_int")
    public static boolean isEven(int intValue) {
        return Math.floor((double) intValue / 2.0) * 2.0 == (double) intValue;
    }
    
}

