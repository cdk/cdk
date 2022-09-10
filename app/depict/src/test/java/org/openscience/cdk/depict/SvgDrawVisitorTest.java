/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.depict;

import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.elements.TextElement;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SvgDrawVisitorTest {

    @Test
    public void empty() {
        String empty = new SvgDrawVisitor(50, 50, Depiction.UNITS_MM).toString();
        assertThat(empty, is("<?xml version='1.0' encoding='UTF-8'?>\n"
                             + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
                             + "<svg version='1.2' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='50.0mm' height='50.0mm' viewBox='0 0 50.0 50.0'>\n"
                             + "  <desc>Generated by the Chemistry Development Kit (http://github.com/cdk)</desc>\n"
                             + "</svg>\n"));
    }

    @Test
    public void markedElement() {
        final SvgDrawVisitor visitor = new SvgDrawVisitor(50, 50, Depiction.UNITS_MM);
        visitor.visit(MarkedElement.markup(new LineElement(0, 0, 1, 1, 0.5, Color.RED),
                                           "test-class"));
        assertThat(visitor.toString(), is("<?xml version='1.0' encoding='UTF-8'?>\n"
                                          + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
                                          + "<svg version='1.2' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='50.0mm' height='50.0mm' viewBox='0 0 50.0 50.0'>\n"
                                          + "  <desc>Generated by the Chemistry Development Kit (http://github.com/cdk)</desc>\n"
                                          + "  <g stroke-linecap='round' stroke-linejoin='round'>\n"
                                          + "    <line x1='.0' y1='.0' x2='1.0' y2='1.0' stroke='#FF0000' stroke-width='.5'/>\n"
                                          + "  </g>\n"
                                          + "</svg>\n"));
    }

    @Test
    public void translatedLine() {
        final SvgDrawVisitor visitor = new SvgDrawVisitor(50, 50, Depiction.UNITS_MM);
        visitor.visit(new LineElement(0, 0, 1, 1, 0.5, Color.RED));
        visitor.setTransform(AffineTransform.getTranslateInstance(10, 10));
        visitor.visit(new LineElement(0, 0, 1, 1, 0.5, Color.RED));
        assertThat(visitor.toString(), is("<?xml version='1.0' encoding='UTF-8'?>\n"
                                          + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
                                          + "<svg version='1.2' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='50.0mm' height='50.0mm' viewBox='0 0 50.0 50.0'>\n"
                                          + "  <desc>Generated by the Chemistry Development Kit (http://github.com/cdk)</desc>\n"
                                          + "  <g stroke-linecap='round' stroke-linejoin='round'>\n"
                                          + "    <line x1='.0' y1='.0' x2='1.0' y2='1.0' stroke='#FF0000' stroke-width='.5'/>\n"
                                          + "    <line x1='10.0' y1='10.0' x2='11.0' y2='11.0' stroke='#FF0000' stroke-width='.5'/>\n"
                                          + "  </g>\n"
                                          + "</svg>\n"));
    }

    @Test
    public void scaledStroke() {
        final SvgDrawVisitor visitor = new SvgDrawVisitor(50, 50, Depiction.UNITS_MM);
        visitor.visit(new LineElement(0, 0, 1, 1, 0.5, Color.RED));
        visitor.setTransform(AffineTransform.getScaleInstance(2, 2));
        visitor.visit(new LineElement(0, 0, 1, 1, 0.5, Color.RED));
        assertThat(visitor.toString(), is("<?xml version='1.0' encoding='UTF-8'?>\n"
                                          + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
                                          + "<svg version='1.2' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='50.0mm' height='50.0mm' viewBox='0 0 50.0 50.0'>\n"
                                          + "  <desc>Generated by the Chemistry Development Kit (http://github.com/cdk)</desc>\n"
                                          + "  <g stroke-linecap='round' stroke-linejoin='round'>\n"
                                          + "    <line x1='.0' y1='.0' x2='1.0' y2='1.0' stroke='#FF0000' stroke-width='.5'/>\n"
                                          + "    <line x1='.0' y1='.0' x2='2.0' y2='2.0' stroke='#FF0000' stroke-width='1.0'/>\n"
                                          + "  </g>\n"
                                          + "</svg>\n"));
    }

    @Test
    public void filledPath() {
        final SvgDrawVisitor visitor = new SvgDrawVisitor(50, 50, Depiction.UNITS_MM);
        visitor.visit(GeneralPath.shapeOf(new RoundRectangle2D.Double(0,0,10,10,2,2), Color.BLUE));
        assertThat(visitor.toString(), is("<?xml version='1.0' encoding='UTF-8'?>\n"
                                          + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
                                          + "<svg version='1.2' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='50.0mm' height='50.0mm' viewBox='0 0 50.0 50.0'>\n"
                                          + "  <desc>Generated by the Chemistry Development Kit (http://github.com/cdk)</desc>\n"
                                          + "  <g stroke-linecap='round' stroke-linejoin='round'>\n"
                                          + "    <path d='M.0 1.0v8.0c.0 .55 .45 1.0 1.0 1.0h8.0c.55 .0 1.0 -.45 1.0 -1.0v-8.0c.0 -.55 -.45 -1.0 -1.0 -1.0h-8.0c-.55 .0 -1.0 .45 -1.0 1.0z' stroke='none' fill='#0000FF'/>\n"
                                          + "  </g>\n"
                                          + "</svg>\n"));
    }

    @Test
    public void transformedPath() {
        final SvgDrawVisitor visitor = new SvgDrawVisitor(50, 50, Depiction.UNITS_MM);
        visitor.setTransform(AffineTransform.getTranslateInstance(15, 15));
        visitor.visit(GeneralPath.shapeOf(new RoundRectangle2D.Double(0, 0, 10, 10, 2, 2), Color.BLUE));
        assertThat(visitor.toString(), is("<?xml version='1.0' encoding='UTF-8'?>\n"
                                          + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
                                          + "<svg version='1.2' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='50.0mm' height='50.0mm' viewBox='0 0 50.0 50.0'>\n"
                                          + "  <desc>Generated by the Chemistry Development Kit (http://github.com/cdk)</desc>\n"
                                          + "  <g stroke-linecap='round' stroke-linejoin='round'>\n"
                                          + "    <path d='M15.0 16.0v8.0c.0 .55 .45 1.0 1.0 1.0h8.0c.55 .0 1.0 -.45 1.0 -1.0v-8.0c.0 -.55 -.45 -1.0 -1.0 -1.0h-8.0c-.55 .0 -1.0 .45 -1.0 1.0z' stroke='none' fill='#0000FF'/>\n"
                                          + "  </g>\n"
                                          + "</svg>\n"));
    }

    @Test
    public void textElements() {
        final SvgDrawVisitor visitor = new SvgDrawVisitor(100, 100, Depiction.UNITS_MM);
        visitor.visit(new TextElement(50, 50, "PNG < EPS < SVG", Color.RED));
        assertThat(visitor.toString(), is("<?xml version='1.0' encoding='UTF-8'?>\n"
                                          + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
                                          + "<svg version='1.2' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='100.0mm' height='100.0mm' viewBox='0 0 100.0 100.0'>\n"
                                          + "  <desc>Generated by the Chemistry Development Kit (http://github.com/cdk)</desc>\n"
                                          + "  <g stroke-linecap='round' stroke-linejoin='round'>\n"
                                          + "    <text  x='50.0' y='50.0' fill='#FF0000' text-anchor='middle'>PNG &lt; EPS &lt; SVG</text>\n"
                                          + "  </g>\n"
                                          + "</svg>\n"));
    }

    @Test
    public void rectElements() {
        final SvgDrawVisitor visitor = new SvgDrawVisitor(100, 100, Depiction.UNITS_MM);
        visitor.visit(new RectangleElement(0,0,100,100, Color.WHITE));
        assertThat(visitor.toString(), is("<?xml version='1.0' encoding='UTF-8'?>\n"
                                          + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
                                          + "<svg version='1.2' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='100.0mm' height='100.0mm' viewBox='0 0 100.0 100.0'>\n"
                                          + "  <desc>Generated by the Chemistry Development Kit (http://github.com/cdk)</desc>\n"
                                          + "  <g stroke-linecap='round' stroke-linejoin='round'>\n"
                                          + "    <rect x='.0' y='-100.0' width='100.0' height='100.0' fill='none' stroke='#FFFFFF'/>\n"
                                          + "  </g>\n"
                                          + "</svg>\n"));
    }

    @Test
    public void testTransparencyLocaleEncoding() {
        final SvgDrawVisitor visitor = new SvgDrawVisitor(50, 50, Depiction.UNITS_MM);
        visitor.setTransform(AffineTransform.getTranslateInstance(15, 15));
        visitor.visit(GeneralPath.shapeOf(new RoundRectangle2D.Double(0, 0, 10, 10, 2, 2), new Color(255,0,0,126)));
        assertThat(visitor.toString(), StringContains.containsString("rgba(255,0,0,0.49)"));
    }

}
