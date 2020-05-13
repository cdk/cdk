/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash.stereo;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openscience.cdk.interfaces.IBond.Stereo.DOWN;
import static org.openscience.cdk.interfaces.IBond.Stereo.NONE;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class GeometricTetrahedralEncoderFactoryTest {

    @Test
    public void testCreate_2D() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);
        IAtom h5 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);
        when(container.getAtom(4)).thenReturn(h5);

        Point2d p1 = new Point2d(1.23, -0.29);
        Point2d p2 = new Point2d(-0.30, -0.29);
        Point2d p3 = new Point2d(2.00, -1.63);
        Point2d p4 = new Point2d(2.00, 1.03);
        Point2d p5 = new Point2d(2.32, -0.29);

        when(c1.getPoint2d()).thenReturn(p1);
        when(o2.getPoint2d()).thenReturn(p2);
        when(n3.getPoint2d()).thenReturn(p3);
        when(c4.getPoint2d()).thenReturn(p4);
        when(h5.getPoint2d()).thenReturn(p5);

        IBond c1c4 = mock(IBond.class);
        IBond c1o2 = mock(IBond.class);
        IBond c1n3 = mock(IBond.class);
        IBond c1h5 = mock(IBond.class);

        int[][] graph = new int[][]{{1, 2, 3, 4}, {0}, {0}, {0}, {0}};

        when(container.getConnectedBondsList(c1)).thenReturn(Arrays.asList(c1c4, c1o2, c1n3, c1h5));

        // let's say c1 is a chiral carbon
        when(c1.getHybridization()).thenReturn(IAtomType.Hybridization.SP3);
        // with a hatch bond from c1 to n3
        when(c1n3.getStereo()).thenReturn(DOWN);
        when(c1n3.getBegin()).thenReturn(c1);
        when(c1n3.getEnd()).thenReturn(n3);
        when(c1o2.getStereo()).thenReturn(NONE);
        when(c1o2.getBegin()).thenReturn(c1);
        when(c1o2.getEnd()).thenReturn(o2);
        when(c1c4.getStereo()).thenReturn(NONE);
        when(c1c4.getBegin()).thenReturn(c1);
        when(c1c4.getEnd()).thenReturn(c4);
        when(c1h5.getStereo()).thenReturn(NONE);
        when(c1h5.getBegin()).thenReturn(c1);
        when(c1h5.getEnd()).thenReturn(h5);

        StereoEncoder encoder = new GeometricTetrahedralEncoderFactory().create(container, graph);

        assertThat(extractEncoders(encoder).size(), is(1));

        GeometricParity geometricParity = getGeometricParity(extractEncoders(encoder).get(0));

        assertTrue(geometricParity instanceof Tetrahedral2DParity);

        assertThat(coords2D(geometricParity), CoreMatchers.is(new Point2d[]{p2, p3, p4, p5}));

    }

    @Test
    public void testCreate_2D_Implicit() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(4);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);

        Point2d p1 = new Point2d(1.23, -0.29);
        Point2d p2 = new Point2d(-0.30, -0.29);
        Point2d p3 = new Point2d(2.00, -1.63);
        Point2d p4 = new Point2d(2.00, 1.03);

        when(c1.getPoint2d()).thenReturn(p1);
        when(o2.getPoint2d()).thenReturn(p2);
        when(n3.getPoint2d()).thenReturn(p3);
        when(c4.getPoint2d()).thenReturn(p4);

        IBond c1c4 = mock(IBond.class);
        IBond c1o2 = mock(IBond.class);
        IBond c1n3 = mock(IBond.class);
        IBond c1h5 = mock(IBond.class);

        int[][] graph = new int[][]{{1, 2, 3}, {0}, {0}, {0},};

        when(container.getConnectedBondsList(c1)).thenReturn(Arrays.asList(c1c4, c1o2, c1n3));

        // let's say c1 is a chiral carbon
        when(c1.getHybridization()).thenReturn(IAtomType.Hybridization.SP3);
        // with a hatch bond from c1 to n3
        when(c1n3.getStereo()).thenReturn(DOWN);
        when(c1n3.getBegin()).thenReturn(c1);
        when(c1n3.getEnd()).thenReturn(n3);
        when(c1o2.getStereo()).thenReturn(NONE);
        when(c1o2.getBegin()).thenReturn(c1);
        when(c1o2.getEnd()).thenReturn(o2);
        when(c1c4.getStereo()).thenReturn(NONE);
        when(c1c4.getBegin()).thenReturn(c1);
        when(c1c4.getEnd()).thenReturn(c4);

        StereoEncoder encoder = new GeometricTetrahedralEncoderFactory().create(container, graph);

        assertThat(extractEncoders(encoder).size(), is(1));

        GeometricParity geometricParity = getGeometricParity(extractEncoders(encoder).get(0));

        assertTrue(geometricParity instanceof Tetrahedral2DParity);

        assertThat(coords2D(geometricParity), CoreMatchers.is(new Point2d[]{p2, p3, p4, p1 // p1 is from central atom
                }));

    }

    @Test
    public void testCreate_3D() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);
        IAtom h5 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);
        when(container.getAtom(4)).thenReturn(h5);

        Point3d p1 = new Point3d(1.23, -0.29, 0);
        Point3d p2 = new Point3d(-0.30, -0.29, 0);
        Point3d p3 = new Point3d(2.00, -1.63, 0);
        Point3d p4 = new Point3d(2.00, 1.03, 0);
        Point3d p5 = new Point3d(2.32, -0.29, 0);

        when(c1.getPoint3d()).thenReturn(p1);
        when(o2.getPoint3d()).thenReturn(p2);
        when(n3.getPoint3d()).thenReturn(p3);
        when(c4.getPoint3d()).thenReturn(p4);
        when(h5.getPoint3d()).thenReturn(p5);

        IBond c1c4 = mock(IBond.class);
        IBond c1o2 = mock(IBond.class);
        IBond c1n3 = mock(IBond.class);
        IBond c1h5 = mock(IBond.class);

        int[][] graph = new int[][]{{1, 2, 3, 4}, {0}, {0}, {0}, {0}};

        when(container.getConnectedBondsList(c1)).thenReturn(Arrays.asList(c1c4, c1o2, c1n3, c1h5));

        // let's say c1 is a chiral carbon
        when(c1.getHybridization()).thenReturn(IAtomType.Hybridization.SP3);
        // with a hatch bond from c1 to n3
        when(c1n3.getStereo()).thenReturn(NONE);
        when(c1n3.getBegin()).thenReturn(c1);
        when(c1n3.getEnd()).thenReturn(n3);
        when(c1o2.getStereo()).thenReturn(NONE);
        when(c1o2.getBegin()).thenReturn(c1);
        when(c1o2.getEnd()).thenReturn(o2);
        when(c1c4.getStereo()).thenReturn(NONE);
        when(c1c4.getBegin()).thenReturn(c1);
        when(c1c4.getEnd()).thenReturn(c4);
        when(c1h5.getStereo()).thenReturn(NONE);
        when(c1h5.getBegin()).thenReturn(c1);
        when(c1h5.getEnd()).thenReturn(h5);

        StereoEncoder encoder = new GeometricTetrahedralEncoderFactory().create(container, graph);

        assertThat(extractEncoders(encoder).size(), is(1));

        GeometricParity geometricParity = getGeometricParity(extractEncoders(encoder).get(0));

        assertTrue(geometricParity instanceof Tetrahedral3DParity);

        assertThat(coords3D(geometricParity), CoreMatchers.is(new Point3d[]{p2, p3, p4, p5}));

    }

    @Test
    public void testCreate_3D_Implicit() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(4);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);

        Point3d p1 = new Point3d(1.23, -0.29, 0);
        Point3d p2 = new Point3d(-0.30, -0.29, 0);
        Point3d p3 = new Point3d(2.00, -1.63, 0);
        Point3d p4 = new Point3d(2.00, 1.03, 0);

        when(c1.getPoint3d()).thenReturn(p1);
        when(o2.getPoint3d()).thenReturn(p2);
        when(n3.getPoint3d()).thenReturn(p3);
        when(c4.getPoint3d()).thenReturn(p4);

        IBond c1c4 = mock(IBond.class);
        IBond c1o2 = mock(IBond.class);
        IBond c1n3 = mock(IBond.class);

        int[][] graph = new int[][]{{1, 2, 3}, {0}, {0}, {0}};

        when(container.getConnectedBondsList(c1)).thenReturn(Arrays.asList(c1c4, c1o2, c1n3));

        // let's say c1 is a chiral carbon
        when(c1.getHybridization()).thenReturn(IAtomType.Hybridization.SP3);
        // with a hatch bond from c1 to n3
        when(c1n3.getStereo()).thenReturn(NONE);
        when(c1n3.getBegin()).thenReturn(c1);
        when(c1n3.getEnd()).thenReturn(n3);
        when(c1o2.getStereo()).thenReturn(NONE);
        when(c1o2.getBegin()).thenReturn(c1);
        when(c1o2.getEnd()).thenReturn(o2);
        when(c1c4.getStereo()).thenReturn(NONE);
        when(c1c4.getBegin()).thenReturn(c1);
        when(c1c4.getEnd()).thenReturn(c4);

        StereoEncoder encoder = new GeometricTetrahedralEncoderFactory().create(container, graph);

        assertThat(extractEncoders(encoder).size(), is(1));

        GeometricParity geometricParity = getGeometricParity(extractEncoders(encoder).get(0));

        assertTrue(geometricParity instanceof Tetrahedral3DParity);

        assertThat(coords3D(geometricParity), CoreMatchers.is(new Point3d[]{p2, p3, p4, p1 // p1 = central atom
                }));

    }

    @Test
    public void testCreate_NonSP3() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);
        IAtom h5 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);
        when(container.getAtom(4)).thenReturn(h5);

        when(c1.getPoint2d()).thenReturn(new Point2d(1.23, -0.29));
        when(o2.getPoint2d()).thenReturn(new Point2d(-0.30, -0.29));
        when(n3.getPoint2d()).thenReturn(new Point2d(2.00, -1.63));
        when(c4.getPoint2d()).thenReturn(new Point2d(2.00, 1.03));
        when(h5.getPoint2d()).thenReturn(new Point2d(2.32, -0.29));

        IBond c1c4 = mock(IBond.class);
        IBond c1o2 = mock(IBond.class);
        IBond c1n3 = mock(IBond.class);
        IBond c1h5 = mock(IBond.class);

        int[][] graph = new int[][]{{1, 2, 3, 4}, {0}, {0}, {0}, {0}};

        when(container.getConnectedBondsList(c1)).thenReturn(Arrays.asList(c1c4, c1o2, c1n3, c1h5));

        // ATOM is not SP3
        // when(c1.getHybridization()).thenReturn(IAtomType.Hybridization.SP3);
        // with a hatch bond from c1 to n3
        when(c1n3.getStereo()).thenReturn(DOWN);
        when(c1n3.getBegin()).thenReturn(c1);
        when(c1n3.getEnd()).thenReturn(n3);
        when(c1o2.getStereo()).thenReturn(NONE);
        when(c1o2.getBegin()).thenReturn(c1);
        when(c1o2.getEnd()).thenReturn(o2);
        when(c1c4.getStereo()).thenReturn(NONE);
        when(c1c4.getBegin()).thenReturn(c1);
        when(c1c4.getEnd()).thenReturn(c4);
        when(c1h5.getStereo()).thenReturn(NONE);
        when(c1h5.getBegin()).thenReturn(c1);
        when(c1h5.getEnd()).thenReturn(h5);

        StereoEncoder encoder = new GeometricTetrahedralEncoderFactory().create(container, graph);

        assertThat(encoder, is(StereoEncoder.EMPTY));

    }

    @Test
    public void testCreate_NoStereoBonds() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);
        IAtom h5 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);
        when(container.getAtom(4)).thenReturn(h5);

        when(c1.getPoint2d()).thenReturn(new Point2d(1.23, -0.29));
        when(o2.getPoint2d()).thenReturn(new Point2d(-0.30, -0.29));
        when(n3.getPoint2d()).thenReturn(new Point2d(2.00, -1.63));
        when(c4.getPoint2d()).thenReturn(new Point2d(2.00, 1.03));
        when(h5.getPoint2d()).thenReturn(new Point2d(2.32, -0.29));

        IBond c1c4 = mock(IBond.class);
        IBond c1o2 = mock(IBond.class);
        IBond c1n3 = mock(IBond.class);
        IBond c1h5 = mock(IBond.class);

        int[][] graph = new int[][]{{1, 2, 3, 4}, {0}, {0}, {0}, {0}};

        when(container.getConnectedBondsList(c1)).thenReturn(Arrays.asList(c1c4, c1o2, c1n3, c1h5));

        // ATOM is not SP3
        when(c1.getHybridization()).thenReturn(IAtomType.Hybridization.SP3);
        // with a hatch bond from c1 to n3
        //when(c1n3.getStereo()).thenReturn(DOWN);
        when(c1n3.getStereo()).thenReturn(NONE);
        when(c1n3.getBegin()).thenReturn(c1);
        when(c1n3.getEnd()).thenReturn(n3);
        when(c1o2.getStereo()).thenReturn(NONE);
        when(c1o2.getBegin()).thenReturn(c1);
        when(c1o2.getEnd()).thenReturn(o2);
        when(c1c4.getStereo()).thenReturn(NONE);
        when(c1c4.getBegin()).thenReturn(c1);
        when(c1c4.getEnd()).thenReturn(c4);
        when(c1h5.getStereo()).thenReturn(NONE);
        when(c1h5.getBegin()).thenReturn(c1);
        when(c1h5.getEnd()).thenReturn(h5);

        StereoEncoder encoder = new GeometricTetrahedralEncoderFactory().create(container, graph);

        assertThat(encoder, is(StereoEncoder.EMPTY));

    }

    @Test
    public void testCreate_WrongDegree() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);
        IAtom h5 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);
        when(container.getAtom(4)).thenReturn(h5);

        when(c1.getPoint2d()).thenReturn(new Point2d(1.23, -0.29));
        when(o2.getPoint2d()).thenReturn(new Point2d(-0.30, -0.29));
        when(n3.getPoint2d()).thenReturn(new Point2d(2.00, -1.63));
        when(c4.getPoint2d()).thenReturn(new Point2d(2.00, 1.03));
        when(h5.getPoint2d()).thenReturn(new Point2d(2.32, -0.29));

        IBond c1c4 = mock(IBond.class);
        IBond c1o2 = mock(IBond.class);
        IBond c1n3 = mock(IBond.class);
        IBond c1h5 = mock(IBond.class);

        int[][] graph = new int[][]{{1, 2}, // 3, 4}, ignore these
                {0}, {0}, {0}, {0}};

        when(container.getConnectedBondsList(c1)).thenReturn(Arrays.asList(c1c4, c1o2, c1n3, c1h5));

        // ATOM is not SP3
        when(c1.getHybridization()).thenReturn(IAtomType.Hybridization.SP3);
        // with a hatch bond from c1 to n3
        when(c1n3.getStereo()).thenReturn(DOWN);
        when(c1n3.getBegin()).thenReturn(c1);
        when(c1n3.getEnd()).thenReturn(n3);
        when(c1o2.getStereo()).thenReturn(NONE);
        when(c1o2.getBegin()).thenReturn(c1);
        when(c1o2.getEnd()).thenReturn(o2);
        when(c1c4.getStereo()).thenReturn(NONE);
        when(c1c4.getBegin()).thenReturn(c1);
        when(c1c4.getEnd()).thenReturn(c4);
        when(c1h5.getStereo()).thenReturn(NONE);
        when(c1h5.getBegin()).thenReturn(c1);
        when(c1h5.getEnd()).thenReturn(h5);

        StereoEncoder encoder = new GeometricTetrahedralEncoderFactory().create(container, graph);

        assertThat(encoder, is(StereoEncoder.EMPTY));

    }

    private static Point2d[] coords2D(GeometricParity parity) {
        if (parity instanceof Tetrahedral2DParity) {
            Field field = null;
            try {
                field = parity.getClass().getDeclaredField("coordinates");
                field.setAccessible(true);
                return (Point2d[]) field.get(parity);
            } catch (NoSuchFieldException e) {
                System.err.println(e.getMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    private static Point3d[] coords3D(GeometricParity parity) {
        if (parity instanceof Tetrahedral3DParity) {
            Field field = null;
            try {
                field = parity.getClass().getDeclaredField("coordinates");
                field.setAccessible(true);
                return (Point3d[]) field.get(parity);
            } catch (NoSuchFieldException e) {
                System.err.println(e.getMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    private static GeometricParity getGeometricParity(StereoEncoder encoder) {
        if (encoder instanceof GeometryEncoder) {
            Field field = null;
            try {
                field = encoder.getClass().getDeclaredField("geometric");
                field.setAccessible(true);
                return (GeometricParity) field.get(encoder);
            } catch (NoSuchFieldException e) {
                System.err.println(e.getMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    private static List<StereoEncoder> extractEncoders(StereoEncoder encoder) {
        if (encoder instanceof MultiStereoEncoder) {
            Field field = null;
            try {
                field = encoder.getClass().getDeclaredField("encoders");
                field.setAccessible(true);
                return (List<StereoEncoder>) field.get(encoder);
            } catch (NoSuchFieldException e) {
                System.err.println(e.getMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
        return Collections.emptyList();
    }

}
