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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.stereo.TetrahedralChirality;

import javax.vecmath.Point2d;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.OPPOSITE;

/**
 * @author John May
 * @cdk.module test-sdg
 */
public class NonPlanarBondsTest {

    // [C@H](C)(N)O
    @Test
    public void clockwise_implH_1() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, 0.00d, 1.50d));
        m.addAtom(atom("C", 3, 0.00d, 0.00d));
        m.addAtom(atom("N", 2, -1.30d, 2.25d));
        m.addAtom(atom("O", 1, 1.30d, 2.25d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0), new IAtom[]{m.getAtom(0), m.getAtom(1), m.getAtom(2),
                m.getAtom(3)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(1).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@H](CC)(N)O
    // N is favoured over CC
    @Test
    public void clockwise_implH_2() {
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
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0), new IAtom[]{m.getAtom(0), m.getAtom(1), m.getAtom(3),
                m.getAtom(4)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(1).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@H](C)(N)O
    @Test
    public void anticlockwise_implH_1() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, 0.00d, 1.50d));
        m.addAtom(atom("C", 3, 0.00d, 0.00d));
        m.addAtom(atom("N", 2, -1.30d, 2.25d));
        m.addAtom(atom("O", 1, 1.30d, 2.25d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0), new IAtom[]{m.getAtom(0), m.getAtom(1), m.getAtom(2),
                m.getAtom(3)}, ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(1).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@H](CC)(N)O
    // N is favoured over CC
    @Test
    public void anticlockwise_implH_2() {
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
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0), new IAtom[]{m.getAtom(0), m.getAtom(1), m.getAtom(3),
                m.getAtom(4)}, ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(1).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@@](CCC)(C)(N)O
    @Test
    public void clockwise_1() {
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
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0), new IAtom[]{m.getAtom(1), m.getAtom(4), m.getAtom(5),
                m.getAtom(6)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(4).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(5).getStereo(), is(IBond.Stereo.DOWN));
    }

    // [C@@](CCC)(C1)(C)C1 (favour acyclic)
    @Test
    public void clockwise_2() {
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
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0), new IAtom[]{m.getAtom(1), m.getAtom(4), m.getAtom(5),
                m.getAtom(6)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(4).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(5).getStereo(), is(IBond.Stereo.NONE));
    }

    // [C@](CCC)(C)(N)O
    @Test
    public void anticlockwise_1() {
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
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0), new IAtom[]{m.getAtom(1), m.getAtom(4), m.getAtom(5),
                m.getAtom(6)}, ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(4).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(5).getStereo(), is(IBond.Stereo.UP));
    }

    // [C@](CCC)(C1)(C)C1 (favour acyclic)
    @Test
    public void anticlockwise_2() {
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
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0), new IAtom[]{m.getAtom(1), m.getAtom(4), m.getAtom(5),
                m.getAtom(6)}, ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(4).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(5).getStereo(), is(IBond.Stereo.NONE));
    }

    @Test
    public void nonPlanarBondsForAntiClockwsieExtendedTetrahedral() throws CDKException {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.56d, 0.78d));
        m.addAtom(atom("C", 0, -1.13d, 1.49d));
        m.addAtom(atom("C", 0, -0.31d, 1.47d));
        m.addAtom(atom("C", 0, 0.52d, 1.46d));
        m.addAtom(atom("C", 3, 0.94d, 2.17d));
        m.addAtom(atom("H", 0, 0.92d, 0.74d));
        m.addAtom(atom("H", 0, -1.53d, 2.21d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(2, 3, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE);
        m.addBond(3, 5, IBond.Order.SINGLE);
        m.addStereoElement(new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(0), m.getAtom(6), m.getAtom(4),
                m.getAtom(5)}, ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(m.getAtom(1), m.getAtom(0)).getStereo(), is(IBond.Stereo.DOWN));
        assertThat(m.getBond(m.getAtom(1), m.getAtom(6)).getStereo(), is(IBond.Stereo.UP));
    }

    @Test
    public void nonPlanarBondsForClockwsieExtendedTetrahedral() throws CDKException {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.56d, 0.78d));
        m.addAtom(atom("C", 0, -1.13d, 1.49d));
        m.addAtom(atom("C", 0, -0.31d, 1.47d));
        m.addAtom(atom("C", 0, 0.52d, 1.46d));
        m.addAtom(atom("C", 3, 0.94d, 2.17d));
        m.addAtom(atom("H", 0, 0.92d, 0.74d));
        m.addAtom(atom("H", 0, -1.53d, 2.21d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(2, 3, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE);
        m.addBond(3, 5, IBond.Order.SINGLE);
        m.addStereoElement(new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(0), m.getAtom(6), m.getAtom(4),
                m.getAtom(5)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(m.getAtom(1), m.getAtom(0)).getStereo(), is(IBond.Stereo.UP));
        assertThat(m.getBond(m.getAtom(1), m.getAtom(6)).getStereo(), is(IBond.Stereo.DOWN));
    }

    @Test
    public void clockwiseSortShouldHandleExactlyOppositeAtoms() throws Exception {
        IAtomContainer m = new AtomContainer(8, 7, 0, 0);
        m.addAtom(atom("C", 0, 4.50d, -14.84d));
        m.addAtom(atom("C", 3, 4.51d, -13.30d));
        m.addAtom(atom("C", 2, 4.93d, -14.13d));
        m.addAtom(atom("C", 2, 3.68d, -14.81d));
        m.addAtom(atom("O", 0, 4.05d, -15.54d));
        m.addAtom(atom("O", 1, 3.23d, -15.50d));
        m.addAtom(atom("C", 3, 5.32d, -14.86d));
        m.addAtom(atom("C", 3, 4.45d, -16.27d));
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(2, 1, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(3, 5, IBond.Order.SINGLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addBond(7, 4, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(0), new IAtom[]{m.getAtom(2), m.getAtom(4), m.getAtom(6),
                m.getAtom(3),}, ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.UP));
    }
    
    
    // ethene is left alone and not marked as crossed
    @Test public void dontCrossEtheneDoubleBond() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 2, 0.000, 0.000));
        m.addAtom(atom("C", 2, 1.299, -0.750));
        m.addBond(0, 1, IBond.Order.DOUBLE);
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
    }

    /**
     * @cdk.inchi InChI=1S/C4H8O/c1-3-4(2)5/h3H2,1-2H3
     */
    @Test public void dontMarkTerminalBonds() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 3, 0.000, 0.000));
        m.addAtom(atom("C", 0, 1.299, -0.750));
        m.addAtom(atom("C", 2, 2.598, -0.000));
        m.addAtom(atom("C", 3, 3.897, -0.750));
        m.addAtom(atom("O", 0, 1.299, -2.250));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(1, 4, IBond.Order.DOUBLE);
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(1).getStereo(), is(IBond.Stereo.NONE));
        assertThat(m.getBond(2).getStereo(), is(IBond.Stereo.NONE));
    }

    /**
     * @cdk.inchi InChI=1S/C4H8/c1-3-4-2/h3-4H,1-2H3
     */
    @Test public void markBut2eneWithWavyBond() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 3, 0.000, 0.000));
        m.addAtom(atom("C", 1, 1.299, -0.750));
        m.addAtom(atom("C", 1, 2.598, -0.000));
        m.addAtom(atom("C", 3, 3.897, -0.750));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(IBond.Stereo.UP_OR_DOWN));
    }

    /**
     * @cdk.inchi InChI=1S/C8H12/c1-3-5-7-8-6-4-2/h3-8H,1-2H3/b5-3+,6-4+,8-7?
     */
    @Test public void useCrossedBondIfNeeded() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 3, 0.000, 0.000));
        m.addAtom(atom("C", 1, 1.299, -0.750));
        m.addAtom(atom("C", 1, 2.598, -0.000));
        m.addAtom(atom("C", 1, 3.897, -0.750));
        m.addAtom(atom("C", 1, 5.196, -0.000));
        m.addAtom(atom("C", 1, 6.495, -0.750));
        m.addAtom(atom("C", 1, 7.794, -0.000));
        m.addAtom(atom("C", 3, 9.093, -0.750));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.DOUBLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.DOUBLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addStereoElement(new DoubleBondStereochemistry(m.getBond(1),
                                                         new IBond[]{
                                                                 m.getBond(0),
                                                                 m.getBond(2)
                                                         },
                                                         OPPOSITE));
        m.addStereoElement(new DoubleBondStereochemistry(m.getBond(5),
                                                         new IBond[]{
                                                                 m.getBond(4),
                                                                 m.getBond(6)
                                                         },
                                                         OPPOSITE));
        NonplanarBonds.assign(m);
        assertThat(m.getBond(3).getStereo(), is(IBond.Stereo.E_OR_Z));
    }

    /**
     * @cdk.inchi InChI=1S/C6H14S/c1-5-7(4)6(2)3/h5H2,1-4H3/t7-/m0/s1 
     */                                                                  
    @Test
    public void dontMarkTetrahedralCentresWithDoubleBondsAsUnspecified() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 3, 2.598, 1.500));
        m.addAtom(atom("S", 0, 2.598, -0.000));
        m.addAtom(atom("C", 1, 1.299, -0.750));
        m.addAtom(atom("C", 3, 0.000, 0.000));
        m.addAtom(atom("C", 2, 3.897, -0.750));
        m.addAtom(atom("C", 3, 5.196, -0.000));
        m.addBond(1, 0, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(1, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addStereoElement(new TetrahedralChirality(m.getAtom(1),
                                                    new IAtom[]{m.getAtom(0), m.getAtom(1), m.getAtom(2), m.getAtom(4)},
                                                    ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));

        NonplanarBonds.assign(m);
        assertThat(m.getBond(0).getStereo(), is(not(IBond.Stereo.UP_OR_DOWN)));
        assertThat(m.getBond(2).getStereo(), is(not(IBond.Stereo.UP_OR_DOWN)));
        assertThat(m.getBond(3).getStereo(), is(not(IBond.Stereo.UP_OR_DOWN)));
    }
    
    @Test
    public void dontMarkRingBondsInBezeneAsUnspecified() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 1, -1.299, 0.750));
        m.addAtom(atom("C", 1, 0.000, 1.500));
        m.addAtom(atom("C", 1, 1.299, 0.750));
        m.addAtom(atom("C", 1, 1.299, -0.750));
        m.addAtom(atom("C", 1, 0.000, -1.500));
        m.addAtom(atom("C", 1, -1.299, -0.750));
        m.addBond(0, 1, IBond.Order.DOUBLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.DOUBLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        NonplanarBonds.assign(m);
        for (IBond bond : m.bonds()) {
            assertThat(bond.getStereo(), is(IBond.Stereo.NONE));   
        }
    }

    /**
     * {@code SMILES: *CN=C(N)N}
     */
    @Test
    public void dontMarkGuanidineAsUnspecified() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("R", 0, 0.00, 0.00));
        m.addAtom(atom("C", 2, 1.30, -0.75));
        m.addAtom(atom("N", 0, 2.60, -0.00));
        m.addAtom(atom("C", 0, 3.90, -0.75));
        m.addAtom(atom("N", 2, 5.20, -0.00));
        m.addAtom(atom("N", 2, 3.90, -2.25));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(3, 5, IBond.Order.SINGLE);
        NonplanarBonds.assign(m);
        for (IBond bond : m.bonds())
            assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
    }

    /**
     * {@code SMILES: *CN=C(CCC)CCC[H]}
     */
    @Test
    public void dontUnspecifiedDueToHRepresentation() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("R", 0, 0.00, 0.00));
        m.addAtom(atom("C", 2, 1.30, -0.75));
        m.addAtom(atom("N", 0, 2.60, -0.00));
        m.addAtom(atom("C", 0, 3.90, -0.75));
        m.addAtom(atom("C", 2, 3.90, -2.25));
        m.addAtom(atom("C", 2, 2.60, -3.00));
        m.addAtom(atom("C", 3, 2.60, -4.50));
        m.addAtom(atom("C", 2, 5.20, -0.00));
        m.addAtom(atom("C", 2, 6.50, -0.75));
        m.addAtom(atom("C", 2, 7.79, -0.00));
        m.addAtom(atom("H", 0, 9.09, -0.75));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(9, 10, IBond.Order.SINGLE);
        NonplanarBonds.assign(m);
        for (IBond bond : m.bonds())
            assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
    }

    /**
     * {@code SMILES: *CN=C(CCC)CCC}
     */
    @Test
    public void dontMarkUnspecifiedForLinearEqualChains() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("R", 0, 0.00, -0.00));
        m.addAtom(atom("C", 2, 1.30, -0.75));
        m.addAtom(atom("N", 0, 2.60, -0.00));
        m.addAtom(atom("C", 0, 3.90, -0.75));
        m.addAtom(atom("C", 2, 5.20, -0.00));
        m.addAtom(atom("C", 2, 6.50, -0.75));
        m.addAtom(atom("C", 3, 7.79, -0.00));
        m.addAtom(atom("C", 2, 3.90, -2.25));
        m.addAtom(atom("C", 2, 5.20, -3.00));
        m.addAtom(atom("C", 3, 5.20, -4.50));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        NonplanarBonds.assign(m);
        for (IBond bond : m.bonds())
            assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
    }

    /**
     * {@code SMILES: *CN=C1CCCCC1}
     */
    @Test
    public void markUnspecifiedForCyclicLigands() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("R", 0, -4.22, 3.05));
        m.addAtom(atom("C", 2, -2.92, 2.30));
        m.addAtom(atom("N", 0, -1.62, 3.05));
        m.addAtom(atom("C", 0, -0.32, 2.30));
        m.addAtom(atom("C", 2, -0.33, 0.80));
        m.addAtom(atom("C", 2, 0.97, 0.05));
        m.addAtom(atom("C", 2, 2.27, 0.80));
        m.addAtom(atom("C", 2, 2.27, 2.30));
        m.addAtom(atom("C", 2, 0.97, 3.05));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);
        m.addBond(3, 8, IBond.Order.SINGLE);
        Cycles.markRingAtomsAndBonds(m);
        NonplanarBonds.assign(m);
        int wavyCount = 0;
        for (IBond bond : m.bonds())
            if (bond.getStereo() == IBond.Stereo.UP_OR_DOWN)
                wavyCount++;
        assertThat(wavyCount, is(1));
    }

    /**
     * {@code SMILES: *CN=C(CCC)CCN}
     */
    @Test
    public void unspecifiedMarkedOnDifferentLigands() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("R", 0, 0.00, -0.00));
        m.addAtom(atom("C", 2, 1.30, -0.75));
        m.addAtom(atom("N", 0, 2.60, -0.00));
        m.addAtom(atom("C", 0, 3.90, -0.75));
        m.addAtom(atom("C", 2, 5.20, -0.00));
        m.addAtom(atom("C", 2, 6.50, -0.75));
        m.addAtom(atom("C", 3, 7.79, -0.00));
        m.addAtom(atom("C", 2, 3.90, -2.25));
        m.addAtom(atom("C", 2, 5.20, -3.00));
        m.addAtom(atom("N", 2, 5.20, -4.50));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        NonplanarBonds.assign(m);
        int wavyCount = 0;
        for (IBond bond : m.bonds())
            if (bond.getStereo() == IBond.Stereo.UP_OR_DOWN)
                wavyCount++;
        assertThat(wavyCount, is(1));
    }

    /**
     * {@code SMILES: O=C4C=C2[C@]([C@@]1([H])CC[C@@]3([C@@]([H])(O)CC[C@@]3([H])[C@]1([H])CC2)C)(C)CC4}
     */
    @Test
    public void testosterone() throws CDKException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("O=C4C=C2[C@]([C@@]1([H])CC[C@@]3([C@@]([H])(O)CC[C@@]3([H])[C@]1([H])CC2)C)(C)CC4");
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.generateCoordinates(mol);
        int wedgeCount = 0;
        for (IBond bond : mol.bonds())
            if (bond.getStereo() == IBond.Stereo.UP || bond.getStereo() == IBond.Stereo.DOWN)
                wedgeCount++;
        assertThat(wedgeCount, is(7));
    }

    /**
     * {@code SMILES: CN(C)(C)=CC}
     */
    @Test
    public void noWavyBondForCisTransNv5() throws CDKException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("CN(C)(C)=CC");
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.generateCoordinates(mol);
        for (IBond bond : mol.bonds()) {
            assertThat(bond.getStereo(), is(not(IBond.Stereo.UP_OR_DOWN)));
            assertThat(bond.getStereo(), is(not(IBond.Stereo.UP_OR_DOWN_INVERTED)));
        }
    }

    static IAtom atom(String symbol, int hCount, double x, double y) {
        IAtom a = new Atom(symbol);
        a.setImplicitHydrogenCount(hCount);
        a.setPoint2d(new Point2d(x, y));
        return a;
    }

}
