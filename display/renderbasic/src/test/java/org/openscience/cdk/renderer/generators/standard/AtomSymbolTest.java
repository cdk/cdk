/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.generators.standard;

import org.junit.Test;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.renderer.generators.standard.AtomSymbol.SymbolAlignment.Center;
import static org.openscience.cdk.renderer.generators.standard.AtomSymbol.SymbolAlignment.Left;
import static org.openscience.cdk.renderer.generators.standard.AtomSymbol.SymbolAlignment.Right;

public class AtomSymbolTest {

    private final Font font = new Font("Verdana", Font.PLAIN, 12);

    @Test
    public void alignToCenter() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        AtomSymbol symbol = new AtomSymbol(outline, Collections.<TextOutline> emptyList());
        assertCloseTo(outline.getCenter(), symbol.alignTo(Center).getAlignmentCenter(), 0.01);
    }

    @Test
    public void alignToLeft() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        AtomSymbol symbol = new AtomSymbol(outline, Collections.<TextOutline> emptyList());
        assertCloseTo(outline.getFirstGlyphCenter(), symbol.alignTo(Left).getAlignmentCenter(), 0.01);
    }

    @Test
    public void alignToRight() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        AtomSymbol symbol = new AtomSymbol(outline, Collections.<TextOutline> emptyList());
        assertCloseTo(outline.getLastGlyphCenter(), symbol.alignTo(Right).getAlignmentCenter(), 0.01);
    }

    @Test
    public void testGetOutlines() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        AtomSymbol symbol = new AtomSymbol(outline, Collections.<TextOutline> emptyList());
        Rectangle outlineBounds = outline.getOutline().getBounds();
        Rectangle symbolBounds = symbol.getOutlines().get(0).getBounds();
        assertThat(outlineBounds.getX(), closeTo(symbolBounds.getX(), 0.01));
        assertThat(outlineBounds.getY(), closeTo(symbolBounds.getY(), 0.01));
        assertThat(outlineBounds.getMaxX(), closeTo(symbolBounds.getMaxX(), 0.01));
        assertThat(outlineBounds.getMaxY(), closeTo(symbolBounds.getMaxY(), 0.01));
    }

    @Test
    public void testGetOutlinesWithAdjunct() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        TextOutline adjunct = new TextOutline("H", font);
        AtomSymbol symbol = new AtomSymbol(outline, Arrays.asList(adjunct));
        Rectangle outlineBounds = adjunct.getOutline().getBounds();
        Rectangle symbolBounds = symbol.getOutlines().get(1).getBounds();
        assertThat(outlineBounds.getX(), closeTo(symbolBounds.getX(), 0.01));
        assertThat(outlineBounds.getY(), closeTo(symbolBounds.getY(), 0.01));
        assertThat(outlineBounds.getMaxX(), closeTo(symbolBounds.getMaxX(), 0.01));
        assertThat(outlineBounds.getMaxY(), closeTo(symbolBounds.getMaxY(), 0.01));
    }

    @Test
    public void testGetConvexHull() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        AtomSymbol symbol = new AtomSymbol(outline, Collections.<TextOutline> emptyList());
        ConvexHull outlineHull = ConvexHull.ofShape(outline.getOutline());
        ConvexHull symbolHull = symbol.getConvexHull();

        Rectangle2D outlineBounds = outlineHull.outline().getBounds2D();
        Rectangle2D symbolBounds = symbolHull.outline().getBounds2D();

        assertThat(outlineBounds.getX(), closeTo(symbolBounds.getX(), 0.01));
        assertThat(outlineBounds.getY(), closeTo(symbolBounds.getY(), 0.01));
        assertThat(outlineBounds.getMaxX(), closeTo(symbolBounds.getMaxX(), 0.01));
        assertThat(outlineBounds.getMaxY(), closeTo(symbolBounds.getMaxY(), 0.01));
    }

    @Test
    public void testResize() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        AtomSymbol symbol = new AtomSymbol(outline, Collections.<TextOutline> emptyList());
        AtomSymbol transformed = symbol.resize(2, 2);
        Rectangle2D orgBounds = outline.getBounds();
        Rectangle2D newBounds = transformed.getOutlines().get(0).getBounds2D();
        assertThat(newBounds.getX(), closeTo(orgBounds.getX() - orgBounds.getWidth() / 2, 0.01));
        assertThat(newBounds.getY(), closeTo(orgBounds.getY() - orgBounds.getHeight() / 2, 0.01));
        assertThat(newBounds.getMaxX(), closeTo(orgBounds.getMaxX() + orgBounds.getWidth() / 2, 0.01));
        assertThat(newBounds.getMaxY(), closeTo(orgBounds.getMaxY() + orgBounds.getHeight() / 2, 0.01));
    }

    @Test
    public void testCenter() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        AtomSymbol symbol = new AtomSymbol(outline, Collections.<TextOutline> emptyList());
        AtomSymbol transformed = symbol.center(2, 2);
        Rectangle2D oBounds = outline.getBounds();
        Rectangle2D newBounds = transformed.getOutlines().get(0).getBounds2D();

        double dx = 2 - oBounds.getCenterX();
        double dy = 2 - oBounds.getCenterY();

        assertThat(newBounds.getX(), closeTo(oBounds.getMinX() + dx, 0.01));
        assertThat(newBounds.getY(), closeTo(oBounds.getMinY() + dy, 0.01));
        assertThat(newBounds.getMaxX(), closeTo(oBounds.getMaxX() + dx, 0.01));
        assertThat(newBounds.getMaxY(), closeTo(oBounds.getMaxY() + dy, 0.01));
    }

    @Test
    public void testTranslate() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        AtomSymbol symbol = new AtomSymbol(outline, Collections.<TextOutline> emptyList());
        AtomSymbol transformed = symbol.translate(4, 2);
        Rectangle2D orgBounds = symbol.getOutlines().get(0).getBounds2D();
        Rectangle2D newBounds = transformed.getOutlines().get(0).getBounds2D();
        assertThat(newBounds.getX(), closeTo(orgBounds.getX() + 4, 0.01));
        assertThat(newBounds.getY(), closeTo(orgBounds.getY() + 2, 0.01));
        assertThat(newBounds.getMaxX(), closeTo(orgBounds.getMaxX() + 4, 0.01));
        assertThat(newBounds.getMaxY(), closeTo(orgBounds.getMaxY() + 2, 0.01));
    }

    @Test
    public void testTranslateAdjunct() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        TextOutline adjunct = new TextOutline("H", font);
        AtomSymbol symbol = new AtomSymbol(outline, Arrays.asList(adjunct));
        AtomSymbol transformed = symbol.translate(4, 2);
        Rectangle2D orgBounds = symbol.getOutlines().get(0).getBounds2D();
        Rectangle2D newBounds = transformed.getOutlines().get(0).getBounds2D();
        assertThat(newBounds.getX(), closeTo(orgBounds.getX() + 4, 0.01));
        assertThat(newBounds.getY(), closeTo(orgBounds.getY() + 2, 0.01));
        assertThat(newBounds.getMaxX(), closeTo(orgBounds.getMaxX() + 4, 0.01));
        assertThat(newBounds.getMaxY(), closeTo(orgBounds.getMaxY() + 2, 0.01));
    }

    void assertCloseTo(Point2D actual, Point2D expected, double epsilon) {
        assertThat(actual.getX(), closeTo(expected.getX(), epsilon));
        assertThat(actual.getY(), closeTo(expected.getY(), epsilon));
    }
}
