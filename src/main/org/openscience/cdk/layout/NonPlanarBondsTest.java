/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.layout;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.stereo.TetrahedralChirality;

import javax.vecmath.Point2d;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 * @cdk.module test-sdg
 */
public class NonPlanarBondsTest {

    // [C@H](C)(N)O
    @Test public void clockwise_implH_1() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, 0.00d, 1.50d));
        m.addAtom(atom("C", 3, 0.00d, 0.00d));
        m.addAtom(atom("N", 2, -1.30d, 2.25d));
        m.addAtom(atom("O", 1, 1.30d, 2.25d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0),
                                                    new IAtom[]{m.getAtom(0),
                                                                m.getAtom(1),
                                                                m.getAtom(2),
                                                                m.getAtom(3)},
                                                    ITetrahedralChirality.Stereo.CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(1).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@H](CC)(N)O
    // N is favoured over CC
    @Test public void clockwise_implH_2() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("C", 1, -1.30d, 2.25d));
        m.addAtom(atom("C", 2, 0.00d, 1.50d));
        m.addAtom(atom("C", 3, 0.00d, 0.00d));
        m.addAtom(atom("N", 2, -1.30d, 3.75d));
        m.addAtom(atom("O", 1, -2.60d, 1.50d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0),
                                                    new IAtom[]{m.getAtom(0),
                                                                m.getAtom(1),
                                                                m.getAtom(3),
                                                                m.getAtom(4)},
                                                    ITetrahedralChirality.Stereo.CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(1).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@H](C)(N)O
    @Test public void anticlockwise_implH_1() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, 0.00d, 1.50d));
        m.addAtom(atom("C", 3, 0.00d, 0.00d));
        m.addAtom(atom("N", 2, -1.30d, 2.25d));
        m.addAtom(atom("O", 1, 1.30d, 2.25d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0),
                                                    new IAtom[]{m.getAtom(0),
                                                                m.getAtom(1),
                                                                m.getAtom(2),
                                                                m.getAtom(3)},
                                                    ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(1).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@H](CC)(N)O
    // N is favoured over CC
    @Test public void anticlockwise_implH_2() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("C", 1, -1.30d, 2.25d));
        m.addAtom(atom("C", 2, 0.00d, 1.50d));
        m.addAtom(atom("C", 3, 0.00d, 0.00d));
        m.addAtom(atom("N", 2, -1.30d, 3.75d));
        m.addAtom(atom("O", 1, -2.60d, 1.50d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0),
                                                    new IAtom[]{m.getAtom(0),
                                                                m.getAtom(1),
                                                                m.getAtom(3),
                                                                m.getAtom(4)},
                                                    ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(1).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@@](CCC)(C)(N)O
    @Test public void clockwise_1() {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 0, -1.47d, 3.62d));
        m.addAtom(atom("C", 2, -1.13d, 2.16d));
        m.addAtom(atom("C", 2, 0.30d, 1.72d));
        m.addAtom(atom("C", 3, 0.64d, 0.26d));
        m.addAtom(atom("C", 3, -2.90d, 4.06d));
        m.addAtom(atom("N", 2, 0.03d, 3.70d));
        m.addAtom(atom("O", 1, -1.28d, 5.11d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0),
                                                    new IAtom[]{m.getAtom(1),
                                                                m.getAtom(4),
                                                                m.getAtom(5),
                                                                m.getAtom(6)},
                                                    ITetrahedralChirality.Stereo.CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(4).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(5).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@@](CCC)(C1)(C)C1 (favour acyclic)
    @Test public void clockwise_2() {
        IAtomContainer m = new AtomContainer(8, 8, 0, 0);
        m.addAtom(atom("C", 0, -0.96d, -1.04d));
        m.addAtom(atom("C", 2, 0.18d, -0.08d));
        m.addAtom(atom("C", 2, -0.08d, 1.40d));
        m.addAtom(atom("C", 3, 1.07d, 2.36d));
        m.addAtom(atom("C", 2, -1.71d, -2.34d));
        m.addAtom(atom("C", 3, -2.11d, -0.08d));
        m.addAtom(atom("C", 1, -0.21d, -2.34d));
        m.addAtom(atom("C", 3, 1.08d, -3.09d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0),
                                                    new IAtom[]{m.getAtom(1),
                                                                m.getAtom(4),
                                                                m.getAtom(5),
                                                                m.getAtom(6)},
                                                    ITetrahedralChirality.Stereo.CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(4).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(5).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@](CCC)(C)(N)O
    @Test public void anticlockwise_1() {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 0, -1.47d, 3.62d));
        m.addAtom(atom("C", 2, -1.13d, 2.16d));
        m.addAtom(atom("C", 2, 0.30d, 1.72d));
        m.addAtom(atom("C", 3, 0.64d, 0.26d));
        m.addAtom(atom("C", 3, -2.90d, 4.06d));
        m.addAtom(atom("N", 2, 0.03d, 3.70d));
        m.addAtom(atom("O", 1, -1.28d, 5.11d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0),
                                                    new IAtom[]{m.getAtom(1),
                                                                m.getAtom(4),
                                                                m.getAtom(5),
                                                                m.getAtom(6)},
                                                    ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(4).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(5).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@](CCC)(C1)(C)C1 (favour acyclic)
    @Test public void anticlockwise_2() {
        IAtomContainer m = new AtomContainer(8, 8, 0, 0);
        m.addAtom(atom("C", 0, -0.96d, -1.04d));
        m.addAtom(atom("C", 2, 0.18d, -0.08d));
        m.addAtom(atom("C", 2, -0.08d, 1.40d));
        m.addAtom(atom("C", 3, 1.07d, 2.36d));
        m.addAtom(atom("C", 2, -1.71d, -2.34d));
        m.addAtom(atom("C", 3, -2.11d, -0.08d));
        m.addAtom(atom("C", 1, -0.21d, -2.34d));
        m.addAtom(atom("C", 3, 1.08d, -3.09d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0),
                                                    new IAtom[]{m.getAtom(1),
                                                                m.getAtom(4),
                                                                m.getAtom(5),
                                                                m.getAtom(6)},
                                                    ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(4).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(5).getStereo(), is(IBond.Stereo.NONE));
    }
    
    static IAtom atom(String symbol, int hCount, double x, double y) {
        IAtom a = new Atom(symbol);
        a.setImplicitHydrogenCount(hCount);
        a.setPoint2d(new Point2d(x, y));
        return a;
    }
    
}
