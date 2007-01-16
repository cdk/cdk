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

import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class IntegerResultTest extends CDKTestCase {
    
    public IntegerResultTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(IntegerResultTest.class);
	}

	public void testIntegerResult_int() {
		IntegerResult result = new IntegerResult(5);
		assertNotNull(result);
	}
	
	public void testToString() {
		IntegerResult result = new IntegerResult(5);
		assertEquals("5", result.toString());
	}
	
	public void testIntValue() {
		IntegerResult result = new IntegerResult(5);
		assertEquals(5, result.intValue());
	}
}


