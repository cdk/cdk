/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
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

package org.openscience.cdk.hash.stereo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.vecmath.Point2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import org.junit.Test;

/**
 * Some isolated test of the cumulative encoder factory, check out {@link
 * org.openscience.cdk.hash.HashCodeScenariosTest} for actual examples encoding
 * allene and cumulene.
 *
 * @author John May
 * @cdk.module test-hash
 */
public class GeometricCumulativeDoubleBondFactoryTest {

    private static IAtom carbonAt(double x, double y) {
        IAtom atom = new Atom("C");
        atom.setPoint2d(new Point2d(x, y));
        return atom;
    }

    @Test
    public void testCreate() throws Exception {
        IAtomContainer m = new AtomContainer();
        m.addAtom(carbonAt(-0.2994, 3.2084));
        m.addAtom(carbonAt(-1.1244, 3.2084));
        m.addAtom(carbonAt(-1.9494, 3.2084));
        m.addAtom(carbonAt(-2.3619, 2.4939));
        m.addAtom(carbonAt(0.1131, 3.9228));
        m.addBond(new Bond(m.getAtom(0), m.getAtom(1), IBond.Order.DOUBLE));
        m.addBond(new Bond(m.getAtom(1), m.getAtom(2), IBond.Order.DOUBLE));
        m.addBond(new Bond(m.getAtom(2), m.getAtom(3)));
        m.addBond(new Bond(m.getAtom(0), m.getAtom(4)));

        StereoEncoderFactory factory = new GeometricCumulativeDoubleBondFactory();
        // graph not used
        StereoEncoder encoder = factory.create(m, null);
        assertThat(encoder, is(instanceOf(MultiStereoEncoder.class)));
    }

    @Test
    public void testAxialEncoder_Empty() throws Exception {
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom start = mock(IAtom.class);
        IAtom end = mock(IAtom.class);
        when(container.getConnectedBondsList(start)).thenReturn(Collections.<IBond> emptyList());
        when(container.getConnectedBondsList(end)).thenReturn(Collections.<IBond> emptyList());
        assertNull(GeometricCumulativeDoubleBondFactory.axialEncoder(container, start, end));
    }

    @Test
    public void testElevation_Atom_Up() throws Exception {
        IAtom a1 = mock(IAtom.class);
        IAtom a2 = mock(IAtom.class);
        IBond bond = mock(IBond.class);
        when(bond.getStereo()).thenReturn(IBond.Stereo.UP);
        when(bond.getBegin()).thenReturn(a1);
        when(bond.getEnd()).thenReturn(a2);
        assertThat(GeometricCumulativeDoubleBondFactory.elevation(bond, a1), is(+1));
        assertThat(GeometricCumulativeDoubleBondFactory.elevation(bond, a2), is(-1));
    }

    @Test
    public void testElevation_Atom_Down() throws Exception {
        IAtom a1 = mock(IAtom.class);
        IAtom a2 = mock(IAtom.class);
        IBond bond = mock(IBond.class);
        when(bond.getStereo()).thenReturn(IBond.Stereo.DOWN);
        when(bond.getBegin()).thenReturn(a1);
        when(bond.getEnd()).thenReturn(a2);
        assertThat(GeometricCumulativeDoubleBondFactory.elevation(bond, a1), is(-1));
        assertThat(GeometricCumulativeDoubleBondFactory.elevation(bond, a2), is(+1));
    }

    @Test
    public void testElevation_null() throws Exception {
        IBond bond = mock(IBond.class);
        assertThat(GeometricCumulativeDoubleBondFactory.elevation(bond), is(0));
    }

    @Test
    public void testElevation_Up() throws Exception {
        IBond bond = mock(IBond.class);
        when(bond.getStereo()).thenReturn(IBond.Stereo.UP);
        assertThat(GeometricCumulativeDoubleBondFactory.elevation(bond), is(+1));
    }

    @Test
    public void testElevation_Down() throws Exception {
        IBond bond = mock(IBond.class);
        when(bond.getStereo()).thenReturn(IBond.Stereo.DOWN);
        assertThat(GeometricCumulativeDoubleBondFactory.elevation(bond), is(-1));
    }
}
