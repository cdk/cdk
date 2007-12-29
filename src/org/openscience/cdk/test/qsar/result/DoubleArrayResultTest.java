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
package org.openscience.cdk.test.qsar.result;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class DoubleArrayResultTest extends CDKTestCase {
    
    public DoubleArrayResultTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(DoubleArrayResultTest.class);
	}

	public void testDoubleArrayResult_int() {
		DoubleArrayResult result = new DoubleArrayResult(5);
		assertNotNull(result);
		assertEquals(5, result.length());
	}
	
	public void testDoubleArrayResult() {
		DoubleArrayResult result = new DoubleArrayResult();
		assertNotNull(result);
		assertEquals(0, result.length());
	}
	
	public void testSize() {
		DoubleArrayResult result = new DoubleArrayResult();
		assertNotNull(result);
		assertEquals(0, result.length());
		result.add(5);
		assertEquals(1, result.length());
	}

	public void testAdd_double() {
		DoubleArrayResult result = new DoubleArrayResult();
		assertNotNull(result);
		assertEquals("", result.toString());
		result.add(5);
		result.add(2);
		result.add(-3);
		assertEquals(3, result.length());
	}

	public void testToString() {
		DoubleArrayResult result = new DoubleArrayResult();
		assertNotNull(result);
		assertEquals("", result.toString());
		result.add(5);
		assertEquals("5.0", result.toString());
		result.add(2);
		assertEquals("5.0,2.0", result.toString());
		result.add(-3);
		assertEquals("5.0,2.0,-3.0", result.toString());
	}

	public void testGet_int() {
		DoubleArrayResult result = new DoubleArrayResult();
		assertNotNull(result);
		assertEquals("", result.toString());
		result.add(5);
		assertEquals(5, result.get(0), 0.000001);
		result.add(2);
		assertEquals(5, result.get(0), 0.000001);
		assertEquals(2, result.get(1), 0.000001);
		result.add(-1);
		assertEquals(5, result.get(0), 0.000001);
		assertEquals(2, result.get(1), 0.000001);
		assertEquals(-1, result.get(2), 0.000001);
	}

}


