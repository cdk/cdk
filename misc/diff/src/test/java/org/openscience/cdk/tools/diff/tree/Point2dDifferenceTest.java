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

import org.junit.Assert;
import org.junit.Test;

import javax.vecmath.Point2d;

/**
 * @cdk.module test-diff
 */
public class Point2dDifferenceTest {

    @Test
    public void testDiff() {
        Point2d foo = new Point2d(1.0, 2.0);
        Point2d bar = new Point2d(1.0, 5.0);
        IDifference result = Point2dDifference.construct("Foo", foo, bar);
        Assert.assertNotNull(result);
    }

    @Test
    public void testSame() {
        Point2d foo = new Point2d(1.0, 2.0);
        Point2d bar = new Point2d(1.0, 2.0);
        IDifference result = Point2dDifference.construct("Foo", foo, bar);
        Assert.assertNull(result);
    }

    @Test
    public void testTwoNull() {
        IDifference result = Point2dDifference.construct("Foo", null, null);
        Assert.assertNull(result);
    }

    @Test
    public void testOneNull() {
        Point2d bar = new Point2d(1.0, 2.0);
        IDifference result = Point2dDifference.construct("Foo", null, bar);
        Assert.assertNotNull(result);

        result = Point2dDifference.construct("Foo", bar, null);
        Assert.assertNotNull(result);
    }

    @Test
    public void testToString() {
        Point2d bar = new Point2d(1.0, 5.0);
        IDifference result = Point2dDifference.construct("Foo", null, bar);
        String diffString = result.toString();
        Assert.assertNotNull(diffString);
        StringDifferenceTest.assertOneLiner(diffString);
    }
}
