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

import org.openscience.cdk.math.Primes;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class PrimesTest extends CDKTestCase {
    
    public PrimesTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(PrimesTest.class);
	}

	public void testGetPrimeAt_int() {
		assertEquals(2, Primes.getPrimeAt(0));
		
		try {
			Primes.getPrimeAt(2229);
			fail("Should fail her, because it contains only X primes.");
		} catch (ArrayIndexOutOfBoundsException exception) {
			// OK, that should happen
		}
	}
	
	public void testArrayIndexOutOfBounds() {
		try {
			Primes.getPrimeAt(-1);
			fail("Should fail her, because only positive integers are accepted");
		} catch (ArrayIndexOutOfBoundsException exception) {
			// OK, that should happen
		}
}
	
}


