/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.diff.tree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module test-diff
 */
public class BondOrderDifferenceTest {

    @Test
    public void testDiff() {
        IDifference result = BondOrderDifference.construct("Foo", IBond.Order.SINGLE, IBond.Order.DOUBLE);
        Assertions.assertNotNull(result);
    }

    @Test
    public void testSame() {
        IDifference result = BondOrderDifference.construct("Foo", IBond.Order.SINGLE, IBond.Order.SINGLE);
        Assertions.assertNull(result);
    }

    @Test
    public void testTwoNull() {
        IDifference result = BondOrderDifference.construct("Foo", null, null);
        Assertions.assertNull(result);
    }

    @Test
    public void testOneNull() {
        IDifference result = BondOrderDifference.construct("Foo", null, IBond.Order.SINGLE);
        Assertions.assertNotNull(result);

        result = BondOrderDifference.construct("Foo", IBond.Order.SINGLE, null);
        Assertions.assertNotNull(result);
    }

    @Test
    public void testToString() {
        IDifference result = BondOrderDifference.construct("Foo", null, IBond.Order.SINGLE);
        String diffString = result.toString();
        Assertions.assertNotNull(diffString);
        StringDifferenceTest.assertOneLiner(diffString);
    }
}
