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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.stereo;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.Bond;

import javax.vecmath.Point2d;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo.ANTI_CLOCKWISE;

public class FischerRecognitionTest {

    /**
     * @cdk.inchi InChI=1/C3H6O3/c4-1-3(6)2-5/h1,3,5-6H,2H2/t3-/s2
     */
    @Test public void recogniseRightHandedGlyceraldehyde() throws Exception {
        IAtomContainer m = new AtomContainer(8, 7, 0, 0);
        m.addAtom(atom("C", 0, 0.80d, 1.24d));
        m.addAtom(atom("C", 0, 0.80d, 0.42d));
        m.addAtom(atom("O", 1, 0.09d, 1.66d));
        m.addAtom(atom("O", 0, 1.52d, 1.66d));
        m.addAtom(atom("O", 1, 1.63d, 0.42d));
        m.addAtom(atom("C", 2, 0.80d, -0.41d));
        m.addAtom(atom("H", 0, -0.02d, 0.42d));
        m.addAtom(atom("O", 1, 1.52d, -0.82d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(1, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE);
        m.addBond(5, 7, IBond.Order.SINGLE);

        EdgeToBondMap     bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]           graph      = GraphUtil.toAdjList(m, bondMap);
        FischerRecognition recogniser = new FischerRecognition(m,
                                                               graph,
                                                               bondMap,
                                                               Stereocenters.of(m));
        List<IStereoElement> elements = recogniser.recognise(Collections.singleton(Projection.Fischer));
        org.hamcrest.MatcherAssert.assertThat(elements.size(), is(1));
        assertTetrahedralCenter(elements.get(0),
                                m.getAtom(1),
                                ANTI_CLOCKWISE,
                                m.getAtom(0), m.getAtom(4), m.getAtom(5), m.getAtom(6));
    }

    /**
     * @cdk.inchi InChI=1S/C3H6O3/c4-1-3(6)2-5/h1,3,5-6H,2H2/t3-/m1/s1
     */
    @Test public void recogniseLeftHandedGlyceraldehyde() throws Exception {
        IAtomContainer m = new AtomContainer(8, 7, 0, 0);
        m.addAtom(atom("C", 0, 0.80d, 1.24d));
        m.addAtom(atom("C", 0, 0.80d, 0.42d));
        m.addAtom(atom("O", 1, 0.09d, 1.66d));
        m.addAtom(atom("O", 0, 1.52d, 1.66d));
        m.addAtom(atom("O", 0, -0.02d, 0.42d));
        m.addAtom(atom("C", 2, 0.80d, -0.41d));
        m.addAtom(atom("H", 1, 1.63d, 0.42d));
        m.addAtom(atom("O", 1, 1.52d, -0.82d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(1, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE);
        m.addBond(5, 7, IBond.Order.SINGLE);

        EdgeToBondMap     bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]           graph      = GraphUtil.toAdjList(m, bondMap);
        FischerRecognition recogniser = new FischerRecognition(m,
                                                               graph,
                                                               bondMap,
                                                               Stereocenters.of(m));
        List<IStereoElement> elements = recogniser.recognise(Collections.singleton(Projection.Fischer));
        org.hamcrest.MatcherAssert.assertThat(elements.size(), is(1));
        assertTetrahedralCenter(elements.get(0),
                                m.getAtom(1),
                                ANTI_CLOCKWISE,
                                m.getAtom(0), m.getAtom(6), m.getAtom(5), m.getAtom(4));
    }

    /**
     * @cdk.inchi InChI=1/C3H6O3/c4-1-3(6)2-5/h1,3,5-6H,2H2/t3-/s2
     */
    @Test public void recogniseRightHandedGlyceraldehydeWithImplicitHydrogen() throws Exception {
        IAtomContainer m = new AtomContainer(8, 7, 0, 0);
        m.addAtom(atom("C", 0, 0.80d, 1.24d));
        m.addAtom(atom("C", 1, 0.80d, 0.42d));
        m.addAtom(atom("O", 1, 0.09d, 1.66d));
        m.addAtom(atom("O", 0, 1.52d, 1.66d));
        m.addAtom(atom("O", 1, 1.63d, 0.42d));
        m.addAtom(atom("C", 2, 0.80d, -0.41d));
        m.addAtom(atom("O", 1, 1.52d, -0.82d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(1, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.SINGLE);

        EdgeToBondMap     bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]           graph      = GraphUtil.toAdjList(m, bondMap);
        FischerRecognition recogniser = new FischerRecognition(m,
                                                               graph,
                                                               bondMap,
                                                               Stereocenters.of(m));
        List<IStereoElement> elements = recogniser.recognise(Collections.singleton(Projection.Fischer));
        org.hamcrest.MatcherAssert.assertThat(elements.size(), is(1));
        assertTetrahedralCenter(elements.get(0),
                                m.getAtom(1),
                                ANTI_CLOCKWISE,
                                m.getAtom(0), m.getAtom(4), m.getAtom(5), m.getAtom(1));
    }

    /**
     * @cdk.inchi InChI=1S/C6H14O6/c7-1-3(9)5(11)6(12)4(10)2-8/h3-12H,1-2H2/t3-,4-,5-,6-/m1/s1
     */
    @Test public void mannitol() throws CDKException {
        IAtomContainer m = new AtomContainer(12, 11, 0, 0);
        m.addAtom(atom("C", 2, -0.53d, 6.25d));
        m.addAtom(atom("C", 1, -0.53d, 5.42d));
        m.addAtom(atom("O", 1, 0.18d, 6.66d));
        m.addAtom(atom("O", 1, -1.36d, 5.42d));
        m.addAtom(atom("C", 1, -0.53d, 4.60d));
        m.addAtom(atom("O", 1, -1.36d, 4.60d));
        m.addAtom(atom("C", 1, -0.53d, 3.77d));
        m.addAtom(atom("O", 1, 0.29d, 3.77d));
        m.addAtom(atom("C", 1, -0.53d, 2.95d));
        m.addAtom(atom("O", 1, 0.29d, 2.95d));
        m.addAtom(atom("C", 2, -0.53d, 2.12d));
        m.addAtom(atom("O", 1, 0.05d, 1.54d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(1, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addBond(6, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(8, 10, IBond.Order.SINGLE);
        m.addBond(10, 11, IBond.Order.SINGLE);

        EdgeToBondMap     bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]           graph      = GraphUtil.toAdjList(m, bondMap);
        FischerRecognition recogniser = new FischerRecognition(m,
                                                               graph,
                                                               bondMap,
                                                               Stereocenters.of(m));
        List<IStereoElement> elements = recogniser.recognise(Collections.singleton(Projection.Fischer));

        org.hamcrest.MatcherAssert.assertThat(elements.size(), is(4));
        assertTetrahedralCenter(elements.get(0),
                                m.getAtom(1),
                                ANTI_CLOCKWISE,
                                m.getAtom(0), m.getAtom(1), m.getAtom(4), m.getAtom(3));
        assertTetrahedralCenter(elements.get(1),
                                m.getAtom(4),
                                ANTI_CLOCKWISE,
                                m.getAtom(1), m.getAtom(4), m.getAtom(6), m.getAtom(5));
        assertTetrahedralCenter(elements.get(2),
                                m.getAtom(6),
                                ANTI_CLOCKWISE,
                                m.getAtom(4), m.getAtom(7), m.getAtom(8), m.getAtom(6));
        assertTetrahedralCenter(elements.get(3),
                                m.getAtom(8),
                                ANTI_CLOCKWISE,
                                m.getAtom(6), m.getAtom(9), m.getAtom(10), m.getAtom(8));

        m.setStereoElements(elements);
    }

    @Test public void obtainCardinalBonds() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 0.80d, 1.24d);
        IAtom east = atom("O", 1, 1.63d, 0.42d);
        IAtom south = atom("C", 2, 0.80d, -0.41d);
        IAtom west = atom("H", 0, -0.02d, 0.42d);

        IBond[] expected = new IBond[]{
                new Bond(focus, north),
                new Bond(focus, east),
                new Bond(focus, south),
                new Bond(focus, west)
        };

        IBond[] actual = FischerRecognition.cardinalBonds(focus,
                                                          new IBond[]{expected[1],
                                                                      expected[2],
                                                                      expected[3],
                                                                      expected[0]}
                                                         );
        org.hamcrest.MatcherAssert.assertThat(expected, is(actual));
    }

    /**
     * In reality, bonds may not be perfectly orthogonal. Here the N, E, S, and
     * W atoms are all slightly offset from the focus.
     */
    @Test public void obtainNonPerfectCardinalBonds() {

        IAtom focus = atom("C", 0, -0.40d, 3.37d);

        IAtom north = atom("C", 0, -0.43d, 4.18d);
        IAtom east = atom("O", 1, 0.44d, 3.33d);
        IAtom south = atom("C", 2, -0.42d, 2.65d);
        IAtom west = atom("H", 0, -1.21d, 3.36d);

        IBond[] expected = new IBond[]{
                new Bond(focus, north),
                new Bond(focus, east),
                new Bond(focus, south),
                new Bond(focus, west)
        };

        IBond[] actual = FischerRecognition.cardinalBonds(focus,
                                                          new IBond[]{expected[1],
                                                                      expected[2],
                                                                      expected[3],
                                                                      expected[0]}
                                                         );
        org.hamcrest.MatcherAssert.assertThat(expected, is(actual));
    }

    @Test public void createCenterWithFourNeighbors() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 0.80d, 1.24d);
        IAtom east = atom("O", 1, 1.63d, 0.42d);
        IAtom south = atom("C", 2, 0.80d, -0.41d);
        IAtom west = atom("H", 0, -0.02d, 0.42d);

        IBond[] bonds = new IBond[]{
                new Bond(focus, south),
                new Bond(focus, west),
                new Bond(focus, north),
                new Bond(focus, east)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        org.hamcrest.MatcherAssert.assertThat(element.getChiralAtom(), is(sameInstance(focus)));
        org.hamcrest.MatcherAssert.assertThat(element.getStereo(), is(ANTI_CLOCKWISE));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[0], is(sameInstance(north)));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[1], is(sameInstance(east)));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[2], is(sameInstance(south)));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[3], is(sameInstance(west)));
    }

    @Test public void createCenterWithThreeNeighbors_right() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 0.80d, 1.24d);
        IAtom east  = atom("O", 1, 1.63d, 0.42d);
        IAtom south = atom("C", 2, 0.80d, -0.41d);

        IBond[] bonds = new IBond[]{
                new Bond(focus, south),
                new Bond(focus, north),
                new Bond(focus, east)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        org.hamcrest.MatcherAssert.assertThat(element.getChiralAtom(), is(sameInstance(focus)));
        org.hamcrest.MatcherAssert.assertThat(element.getStereo(), is(ANTI_CLOCKWISE));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[0], is(sameInstance(north)));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[1], is(sameInstance(east)));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[2], is(sameInstance(south)));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[3], is(sameInstance(focus)));
    }

    @Test public void createCenterWithThreeNeighbors_left() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 0.80d, 1.24d);
        IAtom south = atom("C", 2, 0.80d, -0.41d);
        IAtom west  = atom("O", 1, -0.02d, 0.42d);

        IBond[] bonds = new IBond[]{
                new Bond(focus, south),
                new Bond(focus, north),
                new Bond(focus, west)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        org.hamcrest.MatcherAssert.assertThat(element.getChiralAtom(), is(sameInstance(focus)));
        org.hamcrest.MatcherAssert.assertThat(element.getStereo(), is(ANTI_CLOCKWISE));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[0], is(sameInstance(north)));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[1], is(sameInstance(focus)));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[2], is(sameInstance(south)));
        org.hamcrest.MatcherAssert.assertThat(element.getLigands()[3], is(sameInstance(west)));
    }

    @Test public void doNotCreateCenterWhenNorthIsMissing() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom east = atom("O", 1, 1.63d, 0.42d);
        IAtom south = atom("C", 2, 0.80d, -0.41d);
        IAtom west = atom("H", 0, -0.02d, 0.42d);

        IBond[] bonds = new IBond[]{
                new Bond(focus, south),
                new Bond(focus, west),
                new Bond(focus, east)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        Assert.assertNull(element);
    }

    @Test public void doNotCreateCenterWhenSouthIsMissing() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 0.80d, 1.24d);
        IAtom east = atom("O", 1, 1.63d, 0.42d);
        IAtom west = atom("H", 0, -0.02d, 0.42d);

        IBond[] bonds = new IBond[]{
                new Bond(focus, north),
                new Bond(focus, west),
                new Bond(focus, east)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        Assert.assertNull(element);
    }

    @Test public void doNotCreateCenterWhenNorthIsOffCenter() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 1d, 1.24d);
        IAtom east = atom("O", 1, 1.63d, 0.42d);
        IAtom south = atom("C", 2, 0.80d, -0.41d);
        IAtom west = atom("H", 0, -0.02d, 0.42d);

        IBond[] bonds = new IBond[]{
                new Bond(focus, north),
                new Bond(focus, south),
                new Bond(focus, west),
                new Bond(focus, east)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        Assert.assertNull(element);
    }

    @Test public void doNotCreateCenterWhenSouthIsOffCenter() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 0.80d, 1.24d);
        IAtom east = atom("O", 1, 1.63d, 0.42d);
        IAtom south = atom("C", 2, 1d, -0.41d);
        IAtom west = atom("H", 0, -0.02d, 0.42d);

        IBond[] bonds = new IBond[]{
                new Bond(focus, north),
                new Bond(focus, south),
                new Bond(focus, west),
                new Bond(focus, east)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        Assert.assertNull(element);
    }

    @Test public void doNotCreateCenterWhenEastIsOffCenter() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 0.80d, 1.24d);
        IAtom east = atom("O", 1, 1.63d, 0.8d);
        IAtom south = atom("C", 2, 0.80d, -0.41d);
        IAtom west = atom("H", 0, -0.02d, 0.42d);

        IBond[] bonds = new IBond[]{
                new Bond(focus, north),
                new Bond(focus, south),
                new Bond(focus, west),
                new Bond(focus, east)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        Assert.assertNull(element);
    }


    @Test public void doNotCreateCenterWhenWestIsOffCenter() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 0.80d, 1.24d);
        IAtom east = atom("O", 1, 1.63d, 0.8d);
        IAtom south = atom("C", 2, 0.80d, -0.41d);
        IAtom west = atom("H", 0, -0.02d, 0.42d);

        IBond[] bonds = new IBond[]{
                new Bond(focus, north),
                new Bond(focus, south),
                new Bond(focus, west),
                new Bond(focus, east)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        Assert.assertNull(element);
    }

    @Test public void doNotCreateCenterWhenEastAndWestAreMissing() {

        IAtom focus = atom("C", 0, 0.80d, 0.42d);

        IAtom north = atom("C", 0, 0.80d, 1.24d);
        IAtom south = atom("C", 2, 0.80d, -0.41d);
        
        IBond[] bonds = new IBond[]{
                new Bond(focus, north),
                new Bond(focus, south)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        Assert.assertNull(element);
    }

    // rotate
    @Test public void doNotCreateCenterWhenRotated() {

        IAtom focus = atom("C", 0, 0.44d, 3.30d);
        
        IAtom north = atom("C", 3, -0.16d, 3.86d);
        IAtom east = atom("O", 1, 1.00d, 3.90d);
        IAtom south = atom("C", 3, 1.05d, 2.74d);
        IAtom west = atom("H", 0, -0.12d, 2.70d);


        IBond[] bonds = new IBond[]{
                new Bond(focus, west),
                new Bond(focus, north),
                new Bond(focus, south),
                new Bond(focus, east)
        };

        ITetrahedralChirality element = FischerRecognition.newTetrahedralCenter(focus,
                                                                                bonds);
        Assert.assertNull(element);
    }

    /**
     * asperaculin A (CHEBI:68202)
     * @cdk.inchi InChI=1S/C15H20O5/c1-12(2)6-13(3)7-19-10(16)9-15(13)8(12)4-5-14(15,18)11(17)20-9/h8-9,18H,4-7H2,1-3H3/t8-,9+,13+,14+,15?/m0/s1 
     */
    @Test public void ignoreCyclicStereocenters() {
        IAtomContainer m = new AtomContainer(22, 25, 0, 0);
        m.addAtom(atom("C", 0, 6.87d, -5.59d));
        m.addAtom(atom("C", 0, 6.87d, -6.61d));
        m.addAtom(atom("C", 0, 7.82d, -5.62d));
        m.addAtom(atom("C", 0, 6.87d, -4.59d));
        m.addAtom(atom("O", 0, 8.18d, -6.34d));
        m.addAtom(atom("C", 0, 7.62d, -6.91d));
        m.addAtom(atom("C", 0, 5.90d, -5.59d));
        m.addAtom(atom("C", 0, 8.39d, -5.06d));
        m.addAtom(atom("C", 0, 5.60d, -4.80d));
        m.addAtom(atom("C", 2, 6.16d, -4.24d));
        m.addAtom(atom("O", 0, 8.22d, -4.29d));
        m.addAtom(atom("C", 2, 6.10d, -6.90d));
        m.addAtom(atom("C", 2, 5.54d, -6.29d));
        m.addAtom(atom("C", 2, 7.46d, -4.07d));
        m.addAtom(atom("O", 0, 7.79d, -7.72d));
        m.addAtom(atom("O", 0, 9.18d, -5.29d));
        m.addAtom(atom("O", 1, 6.87d, -7.44d));
        m.addAtom(atom("C", 3, 6.76d, -3.77d));
        m.addAtom(atom("C", 3, 4.82d, -5.07d));
        m.addAtom(atom("C", 3, 5.19d, -4.08d));
        m.addAtom(atom("H", 0, 8.64d, -5.76d));
        m.addAtom(atom("H", 0, 5.08d, -5.69d));
        m.addBond(1, 0, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(3, 0, IBond.Order.SINGLE);
        m.addBond(4, 2, IBond.Order.SINGLE);
        m.addBond(5, 1, IBond.Order.SINGLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addBond(7, 2, IBond.Order.SINGLE);
        m.addBond(8, 6, IBond.Order.SINGLE);
        m.addBond(9, 3, IBond.Order.SINGLE);
        m.addBond(10, 7, IBond.Order.SINGLE);
        m.addBond(11, 1, IBond.Order.SINGLE);
        m.addBond(12, 6, IBond.Order.SINGLE);
        m.addBond(13, 3, IBond.Order.SINGLE);
        m.addBond(14, 5, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(15, 7, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(1, 16, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(3, 17, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(18, 8, IBond.Order.SINGLE);
        m.addBond(19, 8, IBond.Order.SINGLE);
        m.addBond(2, 20, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(6, 21, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(5, 4, IBond.Order.SINGLE);
        m.addBond(11, 12, IBond.Order.SINGLE);
        m.addBond(10, 13, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);

        EdgeToBondMap     bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]           graph      = GraphUtil.toAdjList(m, bondMap);
        FischerRecognition recogniser = new FischerRecognition(m,
                                                               graph,
                                                               bondMap,
                                                               Stereocenters.of(m));
        Assert.assertTrue(recogniser.recognise(Collections.singleton(Projection.Fischer)).isEmpty());
    }

    /**
     * atrolactic acid (CHEBI:50392)
     * @cdk.inchi InChI=1S/C9H10O3/c1-9(12,8(10)11)7-5-3-2-4-6-7/h2-6,12H,1H3,(H,10,11)
     */
    @Test public void horizontalBondsMustBeTerminal() {
        IAtomContainer m = new AtomContainer(12, 12, 0, 0);
        m.addAtom(atom("C", 0, 12.71d, -16.51d));
        m.addAtom(atom("C", 1, 12.30d, -17.22d));
        m.addAtom(atom("C", 1, 11.47d, -17.22d));
        m.addAtom(atom("C", 1, 11.06d, -16.51d));
        m.addAtom(atom("C", 1, 11.47d, -15.79d));
        m.addAtom(atom("C", 1, 12.30d, -15.79d));
        m.addAtom(atom("O", 1, 13.54d, -17.33d));
        m.addAtom(atom("C", 0, 13.54d, -16.51d));
        m.addAtom(atom("C", 0, 14.36d, -16.51d));
        m.addAtom(atom("O", 1, 14.77d, -17.22d));
        m.addAtom(atom("O", 0, 14.77d, -15.79d));
        m.addAtom(atom("C", 3, 13.54d, -15.68d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(7, 6, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(8, 10, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(0, 7, IBond.Order.SINGLE);
        m.addBond(11, 7, IBond.Order.SINGLE);
        
        EdgeToBondMap     bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]           graph      = GraphUtil.toAdjList(m, bondMap);
        FischerRecognition recogniser = new FischerRecognition(m,
                                                               graph,
                                                               bondMap,
                                                               Stereocenters.of(m));
        Assert.assertTrue(recogniser.recognise(Collections.singleton(Projection.Fischer)).isEmpty());
    }

    static void assertTetrahedralCenter(IStereoElement element,
                                        IAtom focus,
                                        Stereo winding,
                                        IAtom ... neighbors) {
        org.hamcrest.MatcherAssert.assertThat(element, is(instanceOf(ITetrahedralChirality.class)));
        ITetrahedralChirality actual = (ITetrahedralChirality) element;
        org.hamcrest.MatcherAssert.assertThat(actual.getChiralAtom(), is(sameInstance(focus)));
        org.hamcrest.MatcherAssert.assertThat(actual.getStereo(), is(winding));
        org.hamcrest.MatcherAssert.assertThat(actual.getLigands(), is(neighbors));
    }

    static IAtom atom(String symbol, int h, double x, double y) {
        IAtom a = new Atom(symbol);
        a.setImplicitHydrogenCount(h);
        a.setPoint2d(new Point2d(x, y));
        return a;
    }

}
