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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.Arrays;

import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;

class AwtAreaTest {

    static void assertPath(Shape expected,
                           Shape actual) {
        PathIterator eIt = expected.getPathIterator(new AffineTransform());
        PathIterator aIt = actual.getPathIterator(new AffineTransform());
        double[] eData = new double[6];
        double[] aData = new double[6];
        for (;;eIt.next(),aIt.next()) {
            Assertions.assertEquals(eIt.isDone(), aIt.isDone(),
                                    "expected=" + toSvg(expected) + " actual=" + toSvg(actual));
            if (eIt.isDone()) break;
            int eType = eIt.currentSegment(eData);
            int aType = aIt.currentSegment(aData);
            Assertions.assertEquals(eType, aType,
                                    "expected=" + toSvg(expected) + " actual=" + toSvg(actual));
            switch (eType) {
                case SEG_LINETO:
                case SEG_MOVETO:
                    Assertions.assertEquals(eData[0], aData[0], 0.01,
                                            "expected=" + toSvg(expected) + " actual=" + toSvg(actual));
                    Assertions.assertEquals(eData[1], aData[1], 0.01,
                                            "expected=" + toSvg(expected) + " actual=" + toSvg(actual));
                    break;
                case SEG_CLOSE:
                    break;
                default:
                    Assertions.fail("Unexpected path segment: " + eType);
                    break;
            }

        }
    }

    @Test
    public void textExpandLine() {
        Area area = new LineElement(0, 0, 1, 1, 1, null).toArea();
        Area expanded = AwtArea.expand(area, 1);
        assertPath(pathFromSvgOps("M 0.164 -1.473 L -0.351 -1.422 " +
                                  "L -0.484 -1.422 L -1.100 -0.741 " +
                                  "L -1.497 -0.431 L -1.323 0.433 " +
                                  "L -0.146 1.963 L 0.295 2.290 " +
                                  "L 0.384 2.378 L 0.876 2.432 " +
                                  "L 1.274 2.476 L 2.100 1.741 " +
                                  "L 2.497 1.431 L 2.323 0.567 " +
                                  "L 1.146 -0.963 L 0.705 -1.290 " +
                                  "L 0.616 -1.378 L 0.430 -1.428 " +
                                  "L 0.164 -1.473 Z"),
                   expanded);
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
        System.err.println(toSvg(area));
        assertPath(pathFromSvgOps("M 0.000 -1.000 L -0.447 -0.894 " +
                                  "L -2.447 0.106 L -2.000 2.000 " +
                                  "L 2.000 2.000 L 2.707 0.293 " +
                                  "L 1.707 -0.707 L 1.000 -1.000 Z"),
                   expanded);
    }

    @Test
    public void textExpandPath2() {
        GeneralPath path = new GeneralPath(
                Arrays.asList(new MoveTo(0, 0),
                              new LineTo(-1, 0),
                              new LineTo(-2, -1),
                              new LineTo(2, -1),
                              new Close()),
                null);
        Area area = path.toArea();
        Area expanded = AwtArea.expand(area, 1);
        assertPath(pathFromSvgOps("M -2.000 -2.000 L -2.707 -0.293 " +
                                  "L -1.707 0.707 L -1.000 1.000 " +
                                  "L 0.000 1.000 L 0.447 " +
                                  "0.894 L 2.447 -0.106 " +
                                  "L 2.000 -2.000 Z"),
                   expanded);
    }

    @Test
    public void textExpandPathWithCutout() {
        // Letter 'A'
        Path2D path = pathFromSvgOps("M 9.189 -5.527 L 6.729 -11.758 " +
                                     "L 4.258 -5.527 Z M 11.357 0.000 " +
                                     "L 9.785 -4.004 L 3.662 -4.004 " +
                                     "L 2.070 0.000 L 0.166 0.000 " +
                                     "L 5.898 -14.453 L 7.930 -14.453 " +
                                     "L 13.574 0.000 Z");
        Area expanded = AwtArea.expand(new Area(path), 1);
        Path2D expected = pathFromSvgOps("M 6.727 -9.040 L 7.719 -6.527 " +
                                                 "L 5.730 -6.527 L 6.727 -9.040 " +
                                                 "Z M 5.898 -15.453 L 4.968 -14.822 " +
                                                 "L -0.764 -0.369 L 0.166 1.000 " +
                                                 "L 2.070 1.000 L 2.999 0.369 " +
                                                 "L 4.341 -3.004 L 9.103 -3.004 " +
                                                 "L 10.426 0.365 L 11.357 1.000 " +
                                                 "L 13.574 1.000 L 14.505 -0.364 " +
                                                 "L 8.861 -14.817 L 7.930 -15.453 Z");
        assertPath(expected, expanded);
    }

    private static Path2D pathFromSvgOps(String str) {
        Path2D.Double path = new Path2D.Double();
        String[] parts = str.split(" ");
        for (int i = 0; i < parts.length; i++) {
            switch (parts[i]) {
                case "M":
                    path.moveTo(Double.parseDouble(parts[++i]),
                                Double.parseDouble(parts[++i]));
                    break;
                case "L":
                    path.lineTo(Double.parseDouble(parts[++i]),
                                Double.parseDouble(parts[++i]));
                    break;
                case "C":
                    path.curveTo(Double.parseDouble(parts[++i]),
                                 Double.parseDouble(parts[++i]),
                                 Double.parseDouble(parts[++i]),
                                 Double.parseDouble(parts[++i]),
                                 Double.parseDouble(parts[++i]),
                                 Double.parseDouble(parts[++i]));
                    break;
                case "Q":
                    path.quadTo(Double.parseDouble(parts[++i]),
                                Double.parseDouble(parts[++i]),
                                Double.parseDouble(parts[++i]),
                                Double.parseDouble(parts[++i]));
                    break;
                case "Z":
                    path.closePath();
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return path;
    }

    private static String toSvg(Shape shape) {
        StringBuilder sb = new StringBuilder();
        PathIterator it = shape.getPathIterator(new AffineTransform());
        double[] data = new double[6];
        for (; !it.isDone(); it.next()) {
            if (sb.length() != 0)
                sb.append(' ');
            switch (it.currentSegment(data)) {
                case SEG_MOVETO:
                    sb.append('M').append(' ')
                      .append(String.format("%.3f", data[0])).append(' ')
                      .append(String.format("%.3f", data[1]));
                    break;
                case SEG_LINETO:
                    sb.append('L').append(' ')
                      .append(String.format("%.3f", data[0])).append(' ')
                      .append(String.format("%.3f", data[1]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    sb.append('C').append(' ')
                      .append(String.format("%.3f", data[0])).append(' ')
                      .append(String.format("%.3f", data[1])).append(' ')
                      .append(String.format("%.3f", data[2])).append(' ')
                      .append(String.format("%.3f", data[3])).append(' ')
                      .append(String.format("%.3f", data[4])).append(' ')
                      .append(String.format("%.3f", data[5]));
                    break;
                case PathIterator.SEG_QUADTO:
                    sb.append('C').append(' ')
                      .append(String.format("%.3f", data[0])).append(' ')
                      .append(String.format("%.3f", data[1])).append(' ')
                      .append(String.format("%.3f", data[2])).append(' ')
                      .append(String.format("%.3f", data[3]));
                    break;
                case PathIterator.SEG_CLOSE:
                    sb.append('Z');
                    break;
            }
        }
        return sb.toString();
    }
}