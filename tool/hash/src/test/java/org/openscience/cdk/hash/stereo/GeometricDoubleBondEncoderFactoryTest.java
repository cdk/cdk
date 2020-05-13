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

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openscience.cdk.hash.stereo.GeometricDoubleBondEncoderFactory.geometric;
import static org.openscience.cdk.hash.stereo.GeometricDoubleBondEncoderFactory.moveToBack;
import static org.openscience.cdk.hash.stereo.GeometricDoubleBondEncoderFactory.permutation;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class GeometricDoubleBondEncoderFactoryTest {

    @Test
    public void testCreate() throws Exception {

        IAtomContainer mol = mock(IAtomContainer.class);

        // a       d    0       3
        //  \     /      \     /
        //   b = c        1 = 2
        //  /     \      /     \
        // e       f    4       5
        IAtom a = mock(IAtom.class); // 0
        IAtom b = mock(IAtom.class); // 1
        IAtom c = mock(IAtom.class); // 2
        IAtom d = mock(IAtom.class); // 3
        IAtom e = mock(IAtom.class); // 4
        IAtom f = mock(IAtom.class); // 5

        when(mol.indexOf(a)).thenReturn(0);
        when(mol.indexOf(b)).thenReturn(1);
        when(mol.indexOf(c)).thenReturn(2);
        when(mol.indexOf(d)).thenReturn(3);
        when(mol.indexOf(e)).thenReturn(4);
        when(mol.indexOf(f)).thenReturn(5);

        when(mol.getAtom(0)).thenReturn(a);
        when(mol.getAtom(1)).thenReturn(b);
        when(mol.getAtom(2)).thenReturn(c);
        when(mol.getAtom(3)).thenReturn(d);
        when(mol.getAtom(4)).thenReturn(e);
        when(mol.getAtom(5)).thenReturn(f);

        when(b.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);
        when(c.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);

        when(a.getPoint2d()).thenReturn(new Point2d());
        when(b.getPoint2d()).thenReturn(new Point2d());
        when(c.getPoint2d()).thenReturn(new Point2d());
        when(d.getPoint2d()).thenReturn(new Point2d());
        when(e.getPoint2d()).thenReturn(new Point2d());
        when(f.getPoint2d()).thenReturn(new Point2d());

        IBond ba = mock(IBond.class);
        IBond be = mock(IBond.class);
        IBond bc = mock(IBond.class);
        IBond cd = mock(IBond.class);
        IBond cf = mock(IBond.class);

        when(ba.getBegin()).thenReturn(b);
        when(ba.getEnd()).thenReturn(a);
        when(be.getBegin()).thenReturn(b);
        when(be.getEnd()).thenReturn(e);
        when(bc.getBegin()).thenReturn(b);
        when(bc.getEnd()).thenReturn(c);
        when(cd.getBegin()).thenReturn(c);
        when(cd.getEnd()).thenReturn(d);
        when(cf.getBegin()).thenReturn(c);
        when(cf.getEnd()).thenReturn(f);

        when(bc.getOrder()).thenReturn(IBond.Order.DOUBLE);
        when(mol.bonds()).thenReturn(Arrays.asList(ba, be, bc, cd, cf));

        when(mol.getConnectedBondsList(a)).thenReturn(Arrays.asList(ba));
        when(mol.getConnectedBondsList(b)).thenReturn(Arrays.asList(ba, bc, be));
        when(mol.getConnectedBondsList(c)).thenReturn(Arrays.asList(bc, cd, cf));
        when(mol.getConnectedBondsList(d)).thenReturn(Arrays.asList(cd));
        when(mol.getConnectedBondsList(e)).thenReturn(Arrays.asList(be));
        when(mol.getConnectedBondsList(f)).thenReturn(Arrays.asList(cf));

        StereoEncoderFactory factory = new GeometricDoubleBondEncoderFactory();

        int[][] g = new int[][]{{1}, {0, 2, 4}, {1, 3, 5}, {2}, {1}, {2}};

        assertTrue(factory.create(mol, g) instanceof MultiStereoEncoder);
    }

    @Test
    public void testCreate_NoCoordinates() throws Exception {

        IAtomContainer mol = mock(IAtomContainer.class);

        // a       d    0       3
        //  \     /      \     /
        //   b = c        1 = 2
        //  /     \      /     \
        // e       f    4       5
        IAtom a = mock(IAtom.class); // 0
        IAtom b = mock(IAtom.class); // 1
        IAtom c = mock(IAtom.class); // 2
        IAtom d = mock(IAtom.class); // 3
        IAtom e = mock(IAtom.class); // 4
        IAtom f = mock(IAtom.class); // 5

        when(mol.indexOf(a)).thenReturn(0);
        when(mol.indexOf(b)).thenReturn(1);
        when(mol.indexOf(c)).thenReturn(2);
        when(mol.indexOf(d)).thenReturn(3);
        when(mol.indexOf(e)).thenReturn(4);
        when(mol.indexOf(f)).thenReturn(5);

        when(mol.getAtom(0)).thenReturn(a);
        when(mol.getAtom(1)).thenReturn(b);
        when(mol.getAtom(2)).thenReturn(c);
        when(mol.getAtom(3)).thenReturn(d);
        when(mol.getAtom(4)).thenReturn(e);
        when(mol.getAtom(5)).thenReturn(f);

        when(b.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);
        when(c.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);

        IBond ba = mock(IBond.class);
        IBond be = mock(IBond.class);
        IBond bc = mock(IBond.class);
        IBond cd = mock(IBond.class);
        IBond cf = mock(IBond.class);

        when(ba.getBegin()).thenReturn(b);
        when(ba.getEnd()).thenReturn(a);
        when(be.getBegin()).thenReturn(b);
        when(be.getEnd()).thenReturn(e);
        when(bc.getBegin()).thenReturn(b);
        when(bc.getEnd()).thenReturn(c);
        when(cd.getBegin()).thenReturn(c);
        when(cd.getEnd()).thenReturn(d);
        when(cf.getBegin()).thenReturn(c);
        when(cf.getEnd()).thenReturn(f);

        when(bc.getOrder()).thenReturn(IBond.Order.DOUBLE);
        when(mol.bonds()).thenReturn(Arrays.asList(ba, be, bc, cd, cf));

        when(mol.getConnectedBondsList(a)).thenReturn(Arrays.asList(ba));
        when(mol.getConnectedBondsList(b)).thenReturn(Arrays.asList(ba, bc, be));
        when(mol.getConnectedBondsList(c)).thenReturn(Arrays.asList(bc, cd, cf));
        when(mol.getConnectedBondsList(d)).thenReturn(Arrays.asList(cd));
        when(mol.getConnectedBondsList(e)).thenReturn(Arrays.asList(be));
        when(mol.getConnectedBondsList(f)).thenReturn(Arrays.asList(cf));

        StereoEncoderFactory factory = new GeometricDoubleBondEncoderFactory();

        int[][] g = new int[][]{{1}, {0, 2, 4}, {1, 3, 5}, {2}, {1}, {2}};

        assertTrue(factory.create(mol, g) == StereoEncoder.EMPTY);
    }

    @Test
    public void testGeometric_2D() throws Exception {
        IAtom l = mock(IAtom.class); // 0
        IAtom r = mock(IAtom.class); // 1
        IAtom l1 = mock(IAtom.class); // 2
        IAtom l2 = mock(IAtom.class); // 3
        IAtom r1 = mock(IAtom.class); // 4
        IAtom r2 = mock(IAtom.class); // 5

        IAtomContainer m = mock(IAtomContainer.class);

        when(m.getAtom(0)).thenReturn(l);
        when(m.getAtom(1)).thenReturn(r);
        when(m.getAtom(2)).thenReturn(l1);
        when(m.getAtom(3)).thenReturn(l2);
        when(m.getAtom(4)).thenReturn(r1);
        when(m.getAtom(5)).thenReturn(r2);

        when(l.getPoint2d()).thenReturn(new Point2d());
        when(r.getPoint2d()).thenReturn(new Point2d());
        when(l1.getPoint2d()).thenReturn(new Point2d());
        when(l2.getPoint2d()).thenReturn(new Point2d());
        when(r1.getPoint2d()).thenReturn(new Point2d());
        when(r2.getPoint2d()).thenReturn(new Point2d());

        GeometricParity p = geometric(m, 0, 1, 2, 3, 4, 5);
        assertTrue(p instanceof DoubleBond2DParity);
    }

    @Test
    public void testGeometric_3D() throws Exception {
        IAtom l = mock(IAtom.class); // 0
        IAtom r = mock(IAtom.class); // 1
        IAtom l1 = mock(IAtom.class); // 2
        IAtom l2 = mock(IAtom.class); // 3
        IAtom r1 = mock(IAtom.class); // 4
        IAtom r2 = mock(IAtom.class); // 5

        IAtomContainer m = mock(IAtomContainer.class);

        when(m.getAtom(0)).thenReturn(l);
        when(m.getAtom(1)).thenReturn(r);
        when(m.getAtom(2)).thenReturn(l1);
        when(m.getAtom(3)).thenReturn(l2);
        when(m.getAtom(4)).thenReturn(r1);
        when(m.getAtom(5)).thenReturn(r2);

        when(l.getPoint3d()).thenReturn(new Point3d());
        when(r.getPoint3d()).thenReturn(new Point3d());
        when(l1.getPoint3d()).thenReturn(new Point3d());
        when(l2.getPoint3d()).thenReturn(new Point3d());
        when(r1.getPoint3d()).thenReturn(new Point3d());
        when(r2.getPoint3d()).thenReturn(new Point3d());

        GeometricParity p = geometric(m, 0, 1, 2, 3, 4, 5);
        assertTrue(p instanceof DoubleBond3DParity);
    }

    @Test
    public void testPermutation_SingleSubstituents() throws Exception {
        // for a double atom with only one substituent the permutation parity
        // should be the identity (i.e. 1)
        assertThat(permutation(new int[]{1, 2}), is(PermutationParity.IDENTITY));
    }

    @Test
    public void testPermutation_TwoSubstituents() throws Exception {
        PermutationParity p = permutation(new int[]{1, 2, 0});
        assertTrue(p instanceof BasicPermutationParity);
        Field field = p.getClass().getDeclaredField("indices");
        field.setAccessible(true);
        assertArrayEquals((int[]) field.get(p), new int[]{1, 2});
    }

    @Test
    public void testMoveToBack() throws Exception {
        assertThat(moveToBack(new int[]{0, 1, 2}, 0), is(new int[]{1, 2, 0}));
        assertThat(moveToBack(new int[]{0, 1, 2}, 1), is(new int[]{0, 2, 1}));
        assertThat(moveToBack(new int[]{0, 1, 2}, 2), is(new int[]{0, 1, 2}));
        assertThat(moveToBack(new int[]{0, 1, 2, 4, 5, 6}, 2), is(new int[]{0, 1, 4, 5, 6, 2}));
    }

    @Test
    public void testAccept_Hybridization() throws Exception {

        IAtom atom = mock(IAtom.class);
        IBond a = mock(IBond.class);
        IBond b = mock(IBond.class);
        IBond c = mock(IBond.class);

        when(a.getOrder()).thenReturn(IBond.Order.DOUBLE);

        List<IBond> bonds = Arrays.asList(a, b, c);
        assertFalse(GeometricDoubleBondEncoderFactory.accept(atom, bonds));

        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);

        assertTrue(GeometricDoubleBondEncoderFactory.accept(atom, bonds));
    }

    @Test
    public void testAccept_QueryBond() throws Exception {

        IAtom atom = mock(IAtom.class);
        IBond a = mock(IBond.class);
        IBond b = mock(IBond.class);
        IBond c = mock(IBond.class);

        List<IBond> bonds = Arrays.asList(a, b, c);

        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);
        when(a.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertTrue(GeometricDoubleBondEncoderFactory.accept(atom, bonds));
        when(b.getStereo()).thenReturn(IBond.Stereo.UP_OR_DOWN);
        assertFalse(GeometricDoubleBondEncoderFactory.accept(atom, bonds));
        when(b.getStereo()).thenReturn(IBond.Stereo.UP_OR_DOWN_INVERTED);
        assertFalse(GeometricDoubleBondEncoderFactory.accept(atom, bonds));
    }

    @Test
    public void testAccept_CumulatedDoubleBond() throws Exception {

        IAtom atom = mock(IAtom.class);
        IBond a = mock(IBond.class);
        IBond b = mock(IBond.class);
        IBond c = mock(IBond.class);

        List<IBond> bonds = Arrays.asList(a, b, c);

        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);
        when(a.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertTrue(GeometricDoubleBondEncoderFactory.accept(atom, bonds));
        when(b.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertFalse(GeometricDoubleBondEncoderFactory.accept(atom, bonds));
    }

    @Test
    public void testAccept_NoSubstituents() throws Exception {

        IAtom atom = mock(IAtom.class);
        IBond a = mock(IBond.class);

        List<IBond> bonds = Arrays.asList(a);

        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);
        when(a.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertFalse(GeometricDoubleBondEncoderFactory.accept(atom, bonds));
    }
}
