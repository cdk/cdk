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
import org.openscience.cdk.interfaces.IAtom;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Above;
import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Below;
import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Left;
import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Right;

public class HydrogenPositionTest {

    @Test
    public void cardinalDirectionForNorthIsBelow() throws Exception {
        assertThat(HydrogenPosition.usingCardinalDirection(new Vector2d(0, 1)), is(Below));
    }

    @Test
    public void cardinalDirectionForNorthEastIsLeft() throws Exception {
        assertThat(HydrogenPosition.usingCardinalDirection(new Vector2d(1, 1)), is(Left));
    }

    @Test
    public void cardinalDirectionForEastIsLeft() throws Exception {
        assertThat(HydrogenPosition.usingCardinalDirection(new Vector2d(1, 0)), is(Left));
    }

    @Test
    public void cardinalDirectionForSouthEastIsLeft() throws Exception {
        assertThat(HydrogenPosition.usingCardinalDirection(new Vector2d(1, -1)), is(Left));
    }

    @Test
    public void cardinalDirectionForSouthIsAbove() throws Exception {
        assertThat(HydrogenPosition.usingCardinalDirection(new Vector2d(0, -1)), is(Above));
    }

    @Test
    public void cardinalDirectionForSouthWestIsRight() throws Exception {
        assertThat(HydrogenPosition.usingCardinalDirection(new Vector2d(-1, -1)), is(Right));
    }

    @Test
    public void cardinalDirectionForWestIsRight() throws Exception {
        assertThat(HydrogenPosition.usingCardinalDirection(new Vector2d(-1, 0)), is(Right));
    }

    @Test
    public void cardinalDirectionForNorthWestIsRight() throws Exception {
        assertThat(HydrogenPosition.usingCardinalDirection(new Vector2d(-1, 0)), is(Right));
    }

    @Test
    public void hydrogensAppearBeforeOxygen() throws Exception {
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(8);
        assertThat(HydrogenPosition.usingDefaultPlacement(atom), is(Left));
    }

    @Test
    public void hydrogensAppearBeforeSulfur() throws Exception {
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(16);
        assertThat(HydrogenPosition.usingDefaultPlacement(atom), is(Left));
    }

    @Test
    public void hydrogensAppearAfterNitrogen() throws Exception {
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(7);
        assertThat(HydrogenPosition.usingDefaultPlacement(atom), is(Right));
    }

    @Test
    public void hydrogensAppearAfterCarbon() throws Exception {
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(6);
        assertThat(HydrogenPosition.usingDefaultPlacement(atom), is(Right));
    }

    @Test
    public void hydrogensAppearAfterWhenBondIsFromLeft() throws Exception {
        IAtom atom1 = mock(IAtom.class);
        IAtom atom2 = mock(IAtom.class);
        when(atom1.getPoint2d()).thenReturn(new Point2d(0, 0));
        when(atom2.getPoint2d()).thenReturn(new Point2d(-1, 0));
        assertThat(HydrogenPosition.position(atom1, Arrays.asList(atom2)), is(Right));
    }

    @Test
    public void hydrogensAppearBeforeWhenBondIsFromRight() throws Exception {
        IAtom atom1 = mock(IAtom.class);
        IAtom atom2 = mock(IAtom.class);
        when(atom1.getPoint2d()).thenReturn(new Point2d(0, 0));
        when(atom2.getPoint2d()).thenReturn(new Point2d(1, 0));
        assertThat(HydrogenPosition.position(atom1, Arrays.asList(atom2)), is(Left));
    }

    @Test
    public void usingCardinalDirection() throws Exception {
        IAtom atom1 = mock(IAtom.class);
        IAtom atom2 = mock(IAtom.class);
        IAtom atom3 = mock(IAtom.class);
        when(atom1.getPoint2d()).thenReturn(new Point2d(0, 0));
        when(atom2.getPoint2d()).thenReturn(new Point2d(1, 1));
        when(atom3.getPoint2d()).thenReturn(new Point2d(1, -1));
        assertThat(HydrogenPosition.position(atom1, Arrays.asList(atom2, atom3)), is(Left));
    }

    @Test
    public void useDefaultPlacementWithNoBonds() throws Exception {
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(8);
        assertThat(HydrogenPosition.position(atom, Collections.<IAtom> emptyList()), is(Left));
    }

    @Test
    public void values() throws Exception {
        assertThat(HydrogenPosition.values(), is(new HydrogenPosition[]{Right, Left, Above, Below}));
    }

    @Test
    public void valueOf() throws Exception {
        assertThat(HydrogenPosition.valueOf("Above"), is(HydrogenPosition.Above));
    }

    @Test
    public void angularExtentRight() throws Exception {
        double theta = Math.toRadians(60);
        List<Vector2d> vectors = Arrays.asList(new Vector2d(-1, 0), new Vector2d(Math.cos(theta), Math.sin(theta)),
                new Vector2d(Math.cos(-theta), Math.sin(-theta)));
        assertThat(HydrogenPosition.usingAngularExtent(vectors), is(Right));
    }

    @Test
    public void angularExtentLeft() throws Exception {
        double theta = Math.toRadians(120);
        List<Vector2d> vectors = Arrays.asList(new Vector2d(1, 0), new Vector2d(Math.cos(theta), Math.sin(theta)),
                new Vector2d(Math.cos(-theta), Math.sin(-theta)));
        assertThat(HydrogenPosition.usingAngularExtent(vectors), is(Left));
    }

    @Test
    public void angularExtentBelow() throws Exception {
        double theta1 = Math.toRadians(210);
        double theta2 = Math.toRadians(330);
        List<Vector2d> vectors = Arrays.asList(new Vector2d(0, 1), new Vector2d(Math.cos(theta1), Math.sin(theta1)),
                new Vector2d(Math.cos(theta2), Math.sin(theta2)));
        assertThat(HydrogenPosition.usingAngularExtent(vectors), is(Below));
    }

    @Test
    public void angularExtentAbove() throws Exception {
        double theta1 = Math.toRadians(30);
        double theta2 = Math.toRadians(150);
        List<Vector2d> vectors = Arrays.asList(new Vector2d(0, -1), new Vector2d(Math.cos(theta1), Math.sin(theta1)),
                new Vector2d(Math.cos(theta2), Math.sin(theta2)));
        assertThat(HydrogenPosition.usingAngularExtent(vectors), is(Above));
    }

    @Test
    public void symmetric() throws Exception {
        // all extents are the same so 'Right' is chosen in preference
        List<Vector2d> vectors = Arrays.asList(new Vector2d(1, 1), new Vector2d(1, -1), new Vector2d(-1, 1),
                new Vector2d(-1, -1));
        assertThat(HydrogenPosition.usingAngularExtent(vectors), is(Right));
    }

    @Test
    public void largestExtent() throws Exception {
        // the largest extents here are above and below
        List<Vector2d> vectors = Arrays.asList(
                new Vector2d(Math.cos(Math.toRadians(30)), Math.sin(Math.toRadians(30))),
                new Vector2d(Math.cos(Math.toRadians(-30)), Math.sin(Math.toRadians(-30))),
                new Vector2d(Math.cos(Math.toRadians(150)), Math.sin(Math.toRadians(150))),
                new Vector2d(Math.cos(Math.toRadians(-150)), Math.sin(Math.toRadians(-150))));
        assertThat(HydrogenPosition.usingAngularExtent(vectors), is(Above));
    }

}
