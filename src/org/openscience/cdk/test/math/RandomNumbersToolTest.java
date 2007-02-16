/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.math;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.math.RandomNumbersTool;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class RandomNumbersToolTest extends CDKTestCase {
    
    public RandomNumbersToolTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(RandomNumbersToolTest.class);
	}
	
	public void testGetRandomSeed() {
		testSetRandomSeed_long();
	}
	public void testSetRandomSeed_long() {
		long seed = System.currentTimeMillis();
		RandomNumbersTool.setRandomSeed(seed);
		assertEquals(seed, RandomNumbersTool.getRandomSeed());
	}

	public void testRandomInt() {
		int random = RandomNumbersTool.randomInt();
		assertTrue(random == 0 || random == 1);
	}
	
	public void testRandomBoolean() {
		boolean random = RandomNumbersTool.randomBoolean();
		assertTrue(random == true || random == false);
	}
	
	public void testRandomLong() {
		long random = RandomNumbersTool.randomLong();
		assertTrue(random >= 0l);
		assertTrue(random <= 1l);
	}
	
	public void testRandomLong_Long_Long() {
		long lower_limit = 2l;
		long upper_limit = 4l;
		long random = RandomNumbersTool.randomLong(lower_limit, upper_limit);
		assertTrue(random >= lower_limit);
		assertTrue(random <= upper_limit);
	}
	
	public void testRandomDouble() {
		double random = RandomNumbersTool.randomDouble();
		assertTrue(random >= 0.0);
		assertTrue(random <= 1.0);
	}
	
	public void testRandomDouble_double_double() {
		double lower_limit = 2.0;
		double upper_limit = 4.0;
		double random = RandomNumbersTool.randomDouble(lower_limit, upper_limit);
		assertTrue(random >= lower_limit);
		assertTrue(random <= upper_limit);
	}
	
	public void testRandomFloat() {
		float random = RandomNumbersTool.randomFloat();
		assertTrue(random >= 0.0);
		assertTrue(random <= 1.0);
	}
	
	public void testRandomFloat_Float_Float() {
		float lower_limit = (float)2.0;
		float upper_limit = (float)4.0;
		float random = RandomNumbersTool.randomFloat(lower_limit, upper_limit);
		assertTrue(random >= lower_limit);
		assertTrue(random <= upper_limit);
	}
	
	public void testRandomBit() {
		int random = RandomNumbersTool.randomBit();
		assertTrue(random == 0 || random == 1);
	}
	
	public void testRandomInt_int_int() {
		int random = RandomNumbersTool.randomInt(0,5);
		assertTrue(random == 0 || random == 1 || random == 2 ||
				   random == 3 || random == 4 || random == 5);
	}

}


