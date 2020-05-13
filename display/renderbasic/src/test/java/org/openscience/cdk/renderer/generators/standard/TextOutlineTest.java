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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Ignore;
import org.junit.Test;

public class TextOutlineTest {

    private final Font font = new Font("Verdana", Font.PLAIN, 12);

    @Test
    public void getOutline() throws Exception {
        // not sure how to test... we have a complex shape with floating point
        // values?
    }

    @Ignore("Font bounds vary between systems")
    public void untransformedBounds() throws Exception {
        TextOutline clOutline = new TextOutline("Cl", font);
        Rectangle2D bounds = clOutline.getBounds();
        assertThat(bounds.getX(), closeTo(0.67, 0.01));
        assertThat(bounds.getY(), closeTo(-9.12, 0.01));
        assertThat(bounds.getWidth(), closeTo(9.90, 0.01));
        assertThat(bounds.getHeight(), closeTo(9.28, 0.01));
    }

    @Test
    public void boundsTransformedWithXTranslation() throws Exception {
        TextOutline original = new TextOutline("Cl", font);
        TextOutline transformed = original.translate(5, 0);
        Rectangle2D oBounds = original.getBounds();
        Rectangle2D tBounds = transformed.getBounds();
        assertThat(tBounds.getX(), closeTo(oBounds.getX() + 5, 0.01));
        assertThat(tBounds.getY(), closeTo(oBounds.getY(), 0.01));
        assertThat(tBounds.getWidth(), closeTo(oBounds.getWidth(), 0.01));
        assertThat(tBounds.getHeight(), closeTo(oBounds.getHeight(), 0.01));
    }

    @Test
    public void boundsTransformedWithYTranslation() throws Exception {
        TextOutline original = new TextOutline("Cl", font);
        TextOutline transformed = original.translate(0, -5);
        Rectangle2D oBounds = original.getBounds();
        Rectangle2D tBounds = transformed.getBounds();
        assertThat(tBounds.getX(), closeTo(oBounds.getX(), 0.01));
        assertThat(tBounds.getY(), closeTo(oBounds.getY() - 5, 0.01));
        assertThat(tBounds.getWidth(), closeTo(oBounds.getWidth(), 0.01));
        assertThat(tBounds.getHeight(), closeTo(oBounds.getHeight(), 0.01));
    }

    @Ignore("Font bounds vary between systems")
    public void untransformedCenter() throws Exception {
        TextOutline clOutline = new TextOutline("Cl", font);
        Point2D center = clOutline.getCenter();
        assertThat(center.getX(), closeTo(5.62, 0.01));
        assertThat(center.getY(), closeTo(-4.47, 0.01));
    }

    @Test
    public void transformedCenter() throws Exception {
        TextOutline original = new TextOutline("Cl", font);
        TextOutline transformed = original.translate(0, -5);
        Point2D oCenter = original.getCenter();
        Point2D tCenter = transformed.getCenter();
        assertThat(tCenter.getX(), closeTo(oCenter.getX(), 0.01));
        assertThat(tCenter.getY(), closeTo(oCenter.getY() - 5, 0.01));
    }

    @Test
    public void testGetFirstGlyphCenter() throws Exception {
        TextOutline original = new TextOutline("Cl", font);
        Point2D oCenter = original.getCenter();
        Point2D tCenter = original.getFirstGlyphCenter();
        assertThat(tCenter.getX(), lessThan(oCenter.getX()));
    }

    @Test
    public void testGetLastGlyphCenter() throws Exception {
        TextOutline original = new TextOutline("Cl", font);
        Point2D oCenter = original.getCenter();
        Point2D tCenter = original.getLastGlyphCenter();
        assertThat(tCenter.getX(), greaterThan(oCenter.getX()));
    }

    @Test
    public void resizeModifiesBounds() throws Exception {
        TextOutline original = new TextOutline("Cl", font);
        TextOutline transformed = original.resize(2, 2);
        Rectangle2D oBounds = original.getBounds();
        Rectangle2D tBounds = transformed.getBounds();
        assertThat(tBounds.getX(), closeTo(oBounds.getX() - oBounds.getWidth() / 2, 0.01));
        assertThat(tBounds.getY(), closeTo(oBounds.getY() - oBounds.getHeight() / 2, 0.01));
        assertThat(tBounds.getWidth(), closeTo(oBounds.getWidth() * 2, 0.01));
        assertThat(tBounds.getHeight(), closeTo(oBounds.getHeight() * 2, 0.01));
    }

    @Test
    public void resizeMaintainsCenter() throws Exception {
        TextOutline clOutline = new TextOutline("Cl", font);
        Point2D orgCenter = clOutline.getCenter();
        Point2D newCenter = clOutline.resize(21, 5).getCenter();
        assertThat(orgCenter.getX(), closeTo(newCenter.getX(), 0.01));
        assertThat(orgCenter.getY(), closeTo(newCenter.getY(), 0.01));
    }

    @Test
    public void firstAndLastCenterIsTheSameForSingleLetterOutline() throws Exception {
        TextOutline oOutline = new TextOutline("O", font);
        Point2D firstCenter = oOutline.getFirstGlyphCenter();
        Point2D lastCenter = oOutline.getLastGlyphCenter();
        assertThat(firstCenter.getX(), closeTo(lastCenter.getX(), 0.01));
        assertThat(firstCenter.getY(), closeTo(lastCenter.getY(), 0.01));
    }

    @Test
    public void testToString() throws Exception {
        TextOutline outline = new TextOutline("Cl", font);
        Rectangle2D bounds = outline.getBounds();
        assertThat(outline.toString(), is("Cl [x=" + toString(bounds.getX()) + ", y=" + toString(bounds.getY())
                + ", w=" + toString(bounds.getWidth()) + ", h=" + toString(bounds.getHeight()) + "]"));
    }

    static String toString(double x) {
        return String.format("%.2f", x);
    }
}
