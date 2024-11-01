/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.renderer.generators.standard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.path.Close;
import org.openscience.cdk.renderer.elements.path.LineTo;
import org.openscience.cdk.renderer.elements.path.MoveTo;
import org.openscience.cdk.renderer.elements.path.PathElement;
import org.openscience.cdk.renderer.elements.path.Type;

import java.awt.geom.Area;
import java.util.Arrays;

class AwtAreaTest {

    static void assertMoveTo(PathElement e, double x, double y) {
        double[] coords = new double[6];
        e.points(coords);
        Assertions.assertEquals(e.type, Type.MoveTo);
        Assertions.assertEquals(x, coords[0], 0.1);
        Assertions.assertEquals(y, coords[1], 0.1);
    }

    static void assertLineTo(PathElement e, double x, double y) {
        double[] coords = new double[6];
        e.points(coords);
        Assertions.assertEquals(e.type, Type.LineTo);
        Assertions.assertEquals(x, coords[0], 0.1);
        Assertions.assertEquals(y, coords[1], 0.1);
    }

    static void assertClose(PathElement e) {
        Assertions.assertEquals(e.type, Type.Close);
    }

    @Test
    public void textExpandLine() {
        Area area = new LineElement(0, 0, 1, 1, 1, null).toArea(false);
        Area expanded = AwtArea.expand(area, 1);
        GeneralPath actual = GeneralPath.shapeOf(expanded, null);
        Assertions.assertEquals(actual.elements.size(),
                                12);
        assertMoveTo(actual.elements.get(0), 1.06, -1.06);
        assertLineTo(actual.elements.get(1), -1.35, -0.35);
        assertLineTo(actual.elements.get(2), -1.354, 0.354);
        assertLineTo(actual.elements.get(3), -1.061, 1.061);
        assertLineTo(actual.elements.get(4), -0.061, 2.061);
        assertLineTo(actual.elements.get(5), 0.646, 2.354);
        assertLineTo(actual.elements.get(6), 1.354, 2.354);
        assertLineTo(actual.elements.get(7), 2.354, 1.354);
        assertLineTo(actual.elements.get(8), 2.354, 0.646);
        assertLineTo(actual.elements.get(9), 2.061, -0.061);
        assertLineTo(actual.elements.get(10), 1.061, -1.061);
        assertClose(actual.elements.get(11));
    }

    @Test
    public void textExpandPath() {
        GeneralPath path = new GeneralPath(
                Arrays.asList(new MoveTo(0, 0),
                              new LineTo(1, 0),
                              new LineTo(2, 1),
                              new LineTo(-2, 1),
                              new Close()),
                null);
        Area area = path.toArea();
        Area expanded = AwtArea.expand(area, 1);
        GeneralPath actual = GeneralPath.shapeOf(expanded, null);
        Assertions.assertEquals(actual.elements.size(),
                                8);
        assertMoveTo(actual.elements.get(0), -0.477, -0.894);
        assertLineTo(actual.elements.get(1), -2.447, 0.106);
        assertLineTo(actual.elements.get(2), -2.000, 2.000);
        assertLineTo(actual.elements.get(3), 2.000, 2.000);
        assertLineTo(actual.elements.get(4), 2.707, 0.293);
        assertLineTo(actual.elements.get(5), 1.707, -0.707);
        assertLineTo(actual.elements.get(6), -0.447, -0.894);
        assertClose(actual.elements.get(7));
    }
}