/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.math.Primes;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class PrimesTest extends CDKTestCase {

    public PrimesTest() {
        super();
    }

    @Test
    public void testGetPrimeAt_int() {
        Assert.assertEquals(2, Primes.getPrimeAt(0));

        try {
            Primes.getPrimeAt(2229);
            Assert.fail("Should fail her, because it contains only X primes.");
        } catch (ArrayIndexOutOfBoundsException exception) {
            // OK, that should happen
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testArrayIndexOutOfBounds() {
        Primes.getPrimeAt(-1);
    }

}
