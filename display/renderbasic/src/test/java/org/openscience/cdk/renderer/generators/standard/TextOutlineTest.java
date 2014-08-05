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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.*;

public class TextOutlineTest {

    private final Font font = new Font("Verdana", Font.PLAIN, 12);

    @Test
    public void getOutline() throws Exception {
        // not sure how to test... we have a complex shape with floating point
        // values?
    }

    @Test
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
        TextOutline clOutline = new TextOutline("Cl", font).translate(5, 0);
        Rectangle2D bounds = clOutline.getBounds();
        assertThat(bounds.getX(), closeTo(5.67, 0.01));
        assertThat(bounds.getY(), closeTo(-9.12, 0.01));
        assertThat(bounds.getWidth(), closeTo(9.90, 0.01));
        assertThat(bounds.getHeight(), closeTo(9.28, 0.01));
    }

    @Test
    public void boundsTransformedWithYTranslation() throws Exception {
        TextOutline clOutline = new TextOutline("Cl", font).translate(0, -5);
        Rectangle2D bounds = clOutline.getBounds();
        assertThat(bounds.getX(), closeTo(0.67, 0.01));
        assertThat(bounds.getY(), closeTo(-14.12, 0.01));
        assertThat(bounds.getWidth(), closeTo(9.90, 0.01));
        assertThat(bounds.getHeight(), closeTo(9.28, 0.01));
    }

    @Test
    public void untransformedCenter() throws Exception {
        TextOutline clOutline = new TextOutline("Cl", font);
        Point2D center = clOutline.getCenter();
        assertThat(center.getX(), closeTo(5.62, 0.01));
        assertThat(center.getY(), closeTo(-4.47, 0.01));
    }

    @Test
    public void transformedCenter() throws Exception {
        TextOutline clOutline = new TextOutline("Cl", font).translate(5, 5);
        Point2D center = clOutline.getCenter();
        assertThat(center.getX(), closeTo(10.62, 0.01));
        assertThat(center.getY(), closeTo(0.52, 0.01));
    }

    @Test
    public void testGetFirstGlyphCenter() throws Exception {
        TextOutline clOutline = new TextOutline("Cl", font);
        Point2D center = clOutline.getFirstGlyphCenter();
        assertThat(center.getX(), closeTo(4.29, 0.01));
        assertThat(center.getY(), closeTo(-4.36, 0.01));
    }

    @Test
    public void testGetLastGlyphCenter() throws Exception {
        TextOutline clOutline = new TextOutline("Cl", font);
        Point2D center = clOutline.getLastGlyphCenter();
        assertThat(center.getX(), closeTo(10.02, 0.01));
        assertThat(center.getY(), closeTo(-4.55, 0.01));
    }

    @Test
    public void resizeModifiesBounds() throws Exception {
        TextOutline clOutline = new TextOutline("Cl", font).resize(2, 2);
        Rectangle2D bounds = clOutline.getBounds();
        assertThat(bounds.getX(), closeTo(-4.27, 0.01));
        assertThat(bounds.getY(), closeTo(-13.75, 0.01));
        assertThat(bounds.getWidth(), closeTo(19.80, 0.01));
        assertThat(bounds.getHeight(), closeTo(18.55, 0.01));
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
        assertThat(new TextOutline("Cl", font).toString(), is("Cl [x=0.67, y=-9.12, w=9.90, h=9.28]"));
    }
}
