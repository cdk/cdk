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
package org.openscience.cdk.graph.rebond;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
class PointTest extends CDKTestCase {

    PointTest() {
        super();
    }

    @Test
    void testPoint_double_double_double() {
        Point point = new Point(0.1, 0.2, 0.3);
        Assertions.assertNotNull(point);
    }

    @Test
    void testGetDimValue_int() {
        Point point = new Point(0.1, 0.2, 0.3);
        Assertions.assertEquals(0.1, point.getDimValue(0), 0.0001);
        Assertions.assertEquals(0.2, point.getDimValue(1), 0.0001);
        Assertions.assertEquals(0.3, point.getDimValue(2), 0.0001);
    }

    @Test
    void testToString() {
        Point point = new Point(0.1, 0.2, 0.3);
        Assertions.assertEquals("<0.1,0.2,0.3>", point.toString());
    }

}
