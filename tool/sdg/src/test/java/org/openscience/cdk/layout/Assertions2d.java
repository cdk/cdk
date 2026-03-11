/* Copyright (C) 2026  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.layout;

import org.junit.jupiter.api.Assertions;

import javax.vecmath.Point2d;

public class Assertions2d {

    /**
     * Compares two Point2d objects, and asserts that the XY coordinates
     * are identical within the given error.
     *
     * @param p1    first Point2d
     * @param p2    second Point2d
     * @param error maximal allowed error
     */
    public static void assertEquals(Point2d p1, Point2d p2, double error) {
        Assertions.assertNotNull(p1, "The expected Point2d is null");
        Assertions.assertNotNull(p2, "The tested Point2d is null");
        Assertions.assertEquals(p1.x, p2.x, error);
        Assertions.assertEquals(p1.y, p2.y, error);
    }
}
