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
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;

import javax.vecmath.Point2d;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertTrue;
import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import static org.openscience.cdk.stereo.CyclicCarbohydrateRecognition.Turn;
import static org.openscience.cdk.stereo.CyclicCarbohydrateRecognition.Turn.Left;
import static org.openscience.cdk.stereo.CyclicCarbohydrateRecognition.Turn.Right;

public class CyclicCarbohydrateRecognitionTest {

    @Test public void haworthAnticlockwise() throws Exception {
        org.hamcrest.MatcherAssert.assertThat(CyclicCarbohydrateRecognition.turns(new Point2d[]{
                new Point2d(4.1, 3.0),
                new Point2d(3.3, 2.6),
                new Point2d(3.3, 1.8),
                new Point2d(4.1, 1.4),
                new Point2d(4.8, 1.8),
                new Point2d(4.8, 2.6),
        }), is(new Turn[]{Left,  Left,  Left,  Left,  Left,  Left}));
    }


    @Test public void haworthClockwise() throws Exception {
        org.hamcrest.MatcherAssert.assertThat(CyclicCarbohydrateRecognition.turns(new Point2d[]{
                new Point2d(4.1, 3.0),
                new Point2d(4.8, 2.6),
                new Point2d(4.8, 1.8),
                new Point2d(4.1, 1.4),
                new Point2d(3.3, 1.8),
                new Point2d(3.3, 2.6)
        }), is(new Turn[]{Right,  Right,  Right,  Right,  Right,  Right}));
    }

    @Test public void chairAnticlockwise() throws Exception {
        org.hamcrest.MatcherAssert.assertThat(CyclicCarbohydrateRecognition.turns(new Point2d[]{
                new Point2d(0.9, 2.6),
                new Point2d(0.1, 2.4),
                new Point2d(0.2, 3.1),
                new Point2d(0.5, 2.9),
                new Point2d(1.3, 3.1),
                new Point2d(1.7, 2.4)
        }), is(new Turn[]{Left, Right, Right, Left, Right, Right}));
    }

    @Test public void chairClockwise() throws Exception {
        org.hamcrest.MatcherAssert.assertThat(CyclicCarbohydrateRecognition.turns(new Point2d[]{
                new Point2d(1.7, 2.4),
                new Point2d(1.3, 3.1),
                new Point2d(0.5, 2.9),
                new Point2d(0.2, 3.1),
                new Point2d(0.1, 2.4),
                new Point2d(0.9, 2.6)
        }), is(new Turn[]{Left, Left, Right, Left, Left, Right}));
    }
    

    @Test public void boatAnticlockwise() throws Exception {
        org.hamcrest.MatcherAssert.assertThat(CyclicCarbohydrateRecognition.turns(new Point2d[]{
                new Point2d(3.3, 3.8),
                new Point2d(2.1, 3.8),
                new Point2d(1.6, 4.9),
                new Point2d(2.3, 4.2),
                new Point2d(3.1, 4.2),
                new Point2d(3.8, 4.8)
        }), is(new Turn[]{Right, Right, Right, Left, Left, Right}));
    }

    @Test public void boatClockwise() throws Exception {
        org.hamcrest.MatcherAssert.assertThat(CyclicCarbohydrateRecognition.turns(new Point2d[]{
                new Point2d(3.8, 4.8),
                new Point2d(3.1, 4.2),
                new Point2d(2.3, 4.2),
                new Point2d(1.6, 4.9),
                new Point2d(2.1, 3.8),
                new Point2d(3.3, 3.8)
        }), is(new Turn[]{Left, Right, Right, Left, Left, Left}));
    }

    /**
     * @cdk.inchi InChI=1/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6-/s2
     */
    @Test public void betaDGlucose_Haworth() throws Exception {
        IAtomContainer m = new AtomContainer(12, 12, 0, 0);
        m.addAtom(atom("C", 1, 4.16d, 1.66d));
        m.addAtom(atom("C", 1, 3.75d, 0.94d));
        m.addAtom(atom("C", 1, 4.16d, 0.23d));
        m.addAtom(atom("C", 1, 5.05d, 0.23d));
        m.addAtom(atom("C", 1, 5.46d, 0.94d));
        m.addAtom(atom("O", 0, 5.05d, 1.66d));
        m.addAtom(atom("O", 1, 5.46d, 1.77d));
        m.addAtom(atom("C", 2, 4.16d, 2.48d));
        m.addAtom(atom("O", 1, 3.45d, 2.89d));
        m.addAtom(atom("O", 1, 3.75d, 0.12d));
        m.addAtom(atom("O", 1, 4.16d, 1.05d));
        m.addAtom(atom("O", 1, 5.05d, -0.60d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(0, 7, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);
        m.addBond(1, 9, IBond.Order.SINGLE);
        m.addBond(2, 10, IBond.Order.SINGLE);
        m.addBond(3, 11, IBond.Order.SINGLE);
        
        EdgeToBondMap                 bondMap = EdgeToBondMap.withSpaceFor(m);
        int[][]                       graph   = GraphUtil.toAdjList(m, bondMap);
        Stereocenters stereocenters = new Stereocenters(m, graph, bondMap);
        stereocenters.checkSymmetry();
        CyclicCarbohydrateRecognition recon = new CyclicCarbohydrateRecognition(m, graph, bondMap,
                                                                                stereocenters);

        List<IStereoElement> elements = recon.recognise(Collections.singleton(Projection.Haworth));
        assertTetrahedralCenter(elements.get(0),
                                m.getAtom(1),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(1), m.getAtom(0), m.getAtom(9), m.getAtom(2));
        assertTetrahedralCenter(elements.get(1),
                                m.getAtom(2),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(10), m.getAtom(1), m.getAtom(2), m.getAtom(3));
        assertTetrahedralCenter(elements.get(2),
                                m.getAtom(3),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(3), m.getAtom(2), m.getAtom(11), m.getAtom(4));
        assertTetrahedralCenter(elements.get(3),
                                m.getAtom(4),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(6), m.getAtom(3), m.getAtom(4), m.getAtom(5));
        assertTetrahedralCenter(elements.get(4),
                                m.getAtom(0),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(7), m.getAtom(5), m.getAtom(0), m.getAtom(1));
    }
    
    /**
     * @cdk.inchi InChI=1/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6-/s2
     */
    @Test public void betaDGlucose_Chair() throws Exception {
        IAtomContainer m = new AtomContainer(12, 12, 0, 0);
        m.addAtom(atom("C", 1, -0.77d, 10.34d));
        m.addAtom(atom("C", 1, 0.03d, 10.13d));
        m.addAtom(atom("O", 0, 0.83d, 10.34d));
        m.addAtom(atom("C", 1, 1.24d, 9.63d));
        m.addAtom(atom("C", 1, 0.44d, 9.84d));
        m.addAtom(atom("C", 1, -0.35d, 9.63d));
        m.addAtom(atom("O", 1, 0.86d, 9.13d));
        m.addAtom(atom("O", 1, 2.04d, 9.84d));
        m.addAtom(atom("C", 2, -0.68d, 10.54d));
        m.addAtom(atom("O", 1, -0.68d, 11.37d));
        m.addAtom(atom("O", 1, -1.48d, 9.93d));
        m.addAtom(atom("O", 1, -1.15d, 9.84d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 0, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);
        m.addBond(1, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(0, 10, IBond.Order.SINGLE);
        m.addBond(5, 11, IBond.Order.SINGLE);


        EdgeToBondMap                 bondMap = EdgeToBondMap.withSpaceFor(m);
        int[][]                       graph   = GraphUtil.toAdjList(m, bondMap);
        Stereocenters stereocenters = new Stereocenters(m, graph, bondMap);
        stereocenters.checkSymmetry();
        CyclicCarbohydrateRecognition recon = new CyclicCarbohydrateRecognition(m, graph, bondMap,
                                                                                stereocenters);


        List<IStereoElement> elements = recon.recognise(Collections.singleton(Projection.Chair));
        assertTetrahedralCenter(elements.get(0),
                                m.getAtom(1),
                                ITetrahedralChirality.Stereo.CLOCKWISE,
                                m.getAtom(8), m.getAtom(0), m.getAtom(1), m.getAtom(2));
        assertTetrahedralCenter(elements.get(1),
                                m.getAtom(3),
                                ITetrahedralChirality.Stereo.CLOCKWISE,
                                m.getAtom(7), m.getAtom(2), m.getAtom(3), m.getAtom(4));
        assertTetrahedralCenter(elements.get(2),
                                m.getAtom(4),
                                ITetrahedralChirality.Stereo.CLOCKWISE,
                                m.getAtom(4), m.getAtom(3), m.getAtom(6), m.getAtom(5));
        assertTetrahedralCenter(elements.get(3),
                                m.getAtom(5),
                                ITetrahedralChirality.Stereo.CLOCKWISE,
                                m.getAtom(11), m.getAtom(4), m.getAtom(5), m.getAtom(0));
        assertTetrahedralCenter(elements.get(4),
                                m.getAtom(0),
                                ITetrahedralChirality.Stereo.CLOCKWISE,
                                m.getAtom(0), m.getAtom(5), m.getAtom(10), m.getAtom(1));
    }

    /**
     * @cdk.inchi InChI=1/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6-/s2
     */
    @Test public void betaDGlucoseWithExplicitHydrogens_Haworth() throws Exception {
        IAtomContainer m = new AtomContainer(17, 17, 0, 0);
        m.addAtom(atom("C", 0, 4.16d, 1.66d));
        m.addAtom(atom("C", 0, 3.75d, 0.94d));
        m.addAtom(atom("C", 0, 4.16d, 0.23d));
        m.addAtom(atom("C", 0, 5.05d, 0.23d));
        m.addAtom(atom("C", 0, 5.46d, 0.94d));
        m.addAtom(atom("O", 0, 5.05d, 1.66d));
        m.addAtom(atom("O", 1, 5.46d, 1.48d));
        m.addAtom(atom("C", 2, 4.16d, 2.20d));
        m.addAtom(atom("O", 1, 3.45d, 2.61d));
        m.addAtom(atom("O", 1, 3.74d, 0.50d));
        m.addAtom(atom("O", 1, 4.16d, 0.77d));
        m.addAtom(atom("O", 1, 5.04d, -0.21d));
        m.addAtom(atom("H", 0, 4.15d, -0.21d));
        m.addAtom(atom("H", 0, 5.05d, 0.77d));
        m.addAtom(atom("H", 0, 5.45d, 0.50d));
        m.addAtom(atom("H", 0, 3.75d, 1.48d));
        m.addAtom(atom("H", 0, 4.17d, 1.15d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(0, 7, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);
        m.addBond(1, 9, IBond.Order.SINGLE);
        m.addBond(2, 10, IBond.Order.SINGLE);
        m.addBond(3, 11, IBond.Order.SINGLE);
        m.addBond(2, 12, IBond.Order.SINGLE);
        m.addBond(3, 13, IBond.Order.SINGLE);
        m.addBond(4, 14, IBond.Order.SINGLE);
        m.addBond(1, 15, IBond.Order.SINGLE);
        m.addBond(0, 16, IBond.Order.SINGLE);
        
        EdgeToBondMap      bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]            graph      = GraphUtil.toAdjList(m, bondMap);

        Stereocenters stereocenters = new Stereocenters(m, graph, bondMap);
        stereocenters.checkSymmetry();
        CyclicCarbohydrateRecognition recon = new CyclicCarbohydrateRecognition(m, graph, bondMap,
                                                                                stereocenters);

        List<IStereoElement> elements = recon.recognise(Collections.singleton(Projection.Haworth));
        assertTetrahedralCenter(elements.get(0),
                                m.getAtom(1),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(15), m.getAtom(0), m.getAtom(9), m.getAtom(2));
        assertTetrahedralCenter(elements.get(1),
                                m.getAtom(2),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(10), m.getAtom(1), m.getAtom(12), m.getAtom(3));
        assertTetrahedralCenter(elements.get(2),
                                m.getAtom(3),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(13), m.getAtom(2), m.getAtom(11), m.getAtom(4));
        assertTetrahedralCenter(elements.get(3),
                                m.getAtom(4),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(6), m.getAtom(3), m.getAtom(14), m.getAtom(5));
        assertTetrahedralCenter(elements.get(4),
                                m.getAtom(0),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(7), m.getAtom(5), m.getAtom(16), m.getAtom(1));
        
    }

    /**
     * Example from: http://www.google.com/patents/WO2008025160A1?cl=en
     * @cdk.inchi InChI=1S/C13H26O5/c1-4-10-7-11(17-6-5-16-3)9(2)18-12(8-14)13(10)15/h9-15H,4-8H2,1-3H3/t9-,10+,11+,12+,13-/m0/s1
     */
    @Test public void oxpene() throws Exception {
        IAtomContainer m = new AtomContainer(18, 18, 0, 0);
        m.addAtom(atom("C", 1, 1.39d, 3.65d));
        m.addAtom(atom("C", 2, 2.22d, 3.65d));
        m.addAtom(atom("C", 1, 2.93d, 4.07d));
        m.addAtom(atom("C", 1, 0.68d, 4.07d));
        m.addAtom(atom("C", 1, 1.01d, 4.63d));
        m.addAtom(atom("C", 1, 2.52d, 4.64d));
        m.addAtom(atom("O", 0, 1.76d, 4.89d));
        m.addAtom(atom("O", 1, 0.68d, 3.24d));
        m.addAtom(atom("C", 2, 1.01d, 5.45d));
        m.addAtom(atom("O", 1, 0.18d, 5.45d));
        m.addAtom(atom("C", 3, 2.52d, 5.46d));
        m.addAtom(atom("O", 0, 2.93d, 3.24d));
        m.addAtom(atom("C", 2, 1.39d, 4.48d));
        m.addAtom(atom("C", 3, 2.22d, 4.48d));
        m.addAtom(atom("C", 2, 3.76d, 3.24d));
        m.addAtom(atom("C", 2, 4.34d, 2.66d));
        m.addAtom(atom("O", 0, 5.16d, 2.66d));
        m.addAtom(atom("C", 3, 5.58d, 3.37d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(2, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);
        m.addBond(4, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(5, 10, IBond.Order.SINGLE);
        m.addBond(2, 11, IBond.Order.SINGLE);
        m.addBond(0, 12, IBond.Order.SINGLE);
        m.addBond(12, 13, IBond.Order.SINGLE);
        m.addBond(11, 14, IBond.Order.SINGLE);
        m.addBond(14, 15, IBond.Order.SINGLE);
        m.addBond(15, 16, IBond.Order.SINGLE);
        m.addBond(16, 17, IBond.Order.SINGLE);
        EdgeToBondMap      bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]            graph      = GraphUtil.toAdjList(m, bondMap);

        Stereocenters stereocenters = new Stereocenters(m, graph, bondMap);
        stereocenters.checkSymmetry();
        CyclicCarbohydrateRecognition recon = new CyclicCarbohydrateRecognition(m, graph, bondMap,
                                                                                stereocenters);

        List<IStereoElement> elements = recon.recognise(Collections.singleton(Projection.Haworth));
        assertTetrahedralCenter(elements.get(0),
                                m.getAtom(2),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(2), m.getAtom(1), m.getAtom(11), m.getAtom(5));
        assertTetrahedralCenter(elements.get(1),
                                m.getAtom(5),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(10), m.getAtom(2), m.getAtom(5), m.getAtom(6));
        assertTetrahedralCenter(elements.get(2),
                                m.getAtom(4),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(8), m.getAtom(6), m.getAtom(4), m.getAtom(3));
        assertTetrahedralCenter(elements.get(3),
                                m.getAtom(3),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(3), m.getAtom(4), m.getAtom(7), m.getAtom(0));
        assertTetrahedralCenter(elements.get(4),
                                m.getAtom(0),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(12), m.getAtom(3), m.getAtom(0), m.getAtom(1));
    }

    /**
     * @cdk.inchi InChI=1S/C10H16N5O13P3/c11-8-5-9(13-2-12-8)15(3-14-5)10-7(17)6(16)4(26-10)1-25-30(21,22)28-31(23,24)27-29(18,19)20/h2-4,6-7,10,16-17H,1H2,(H,21,22)(H,23,24)(H2,11,12,13)(H2,18,19,20)/t4-,6-,7-,10-/m1/s1
     */
    @Test public void atp_Haworth() throws Exception {
        IAtomContainer m = new AtomContainer(31, 33, 0, 0);
        m.addAtom(atom("O", 0, 2.56d, -6.46d));
        m.addAtom(atom("C", 1, 1.90d, -6.83d));
        m.addAtom(atom("C", 1, 2.15d, -7.46d));
        m.addAtom(atom("C", 1, 2.98d, -7.46d));
        m.addAtom(atom("C", 1, 3.23d, -6.83d));
        m.addAtom(atom("C", 2, 1.90d, -6.00d));
        m.addAtom(atom("O", 0, 1.18d, -5.59d));
        m.addAtom(atom("O", 1, 2.15d, -8.29d));
        m.addAtom(atom("O", 1, 2.98d, -8.29d));
        m.addAtom(atom("P", 0, 0.36d, -5.59d));
        m.addAtom(atom("O", 0, -0.47d, -5.59d));
        m.addAtom(atom("O", 0, 0.36d, -4.76d));
        m.addAtom(atom("O", 1, 0.36d, -6.41d));
        m.addAtom(atom("P", 0, -1.29d, -5.59d));
        m.addAtom(atom("O", 0, -2.12d, -5.59d));
        m.addAtom(atom("O", 0, -1.29d, -4.76d));
        m.addAtom(atom("O", 1, -1.29d, -6.41d));
        m.addAtom(atom("P", 0, -2.94d, -5.59d));
        m.addAtom(atom("O", 1, -3.77d, -5.59d));
        m.addAtom(atom("O", 0, -2.94d, -4.76d));
        m.addAtom(atom("O", 1, -2.94d, -6.41d));
        m.addAtom(atom("C", 0, 4.73d, -4.51d));
        m.addAtom(atom("C", 0, 4.02d, -4.92d));
        m.addAtom(atom("C", 0, 4.02d, -5.75d));
        m.addAtom(atom("N", 0, 4.73d, -6.16d));
        m.addAtom(atom("N", 0, 5.44d, -5.75d));
        m.addAtom(atom("C", 1, 5.44d, -4.92d));
        m.addAtom(atom("C", 1, 2.75d, -5.33d));
        m.addAtom(atom("N", 0, 3.23d, -4.67d));
        m.addAtom(atom("N", 2, 4.73d, -3.68d));
        m.addAtom(atom("N", 0, 3.23d, -6.00d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.SINGLE);
        m.addBond(2, 7, IBond.Order.SINGLE);
        m.addBond(3, 8, IBond.Order.SINGLE);
        m.addBond(6, 9, IBond.Order.SINGLE);
        m.addBond(9, 10, IBond.Order.SINGLE);
        m.addBond(9, 11, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(9, 12, IBond.Order.SINGLE);
        m.addBond(13, 14, IBond.Order.SINGLE);
        m.addBond(13, 15, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(13, 16, IBond.Order.SINGLE);
        m.addBond(10, 13, IBond.Order.SINGLE);
        m.addBond(17, 18, IBond.Order.SINGLE);
        m.addBond(17, 19, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(17, 20, IBond.Order.SINGLE);
        m.addBond(14, 17, IBond.Order.SINGLE);
        m.addBond(21, 22, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(22, 23, IBond.Order.SINGLE);
        m.addBond(23, 24, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(24, 25, IBond.Order.SINGLE);
        m.addBond(25, 26, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(21, 26, IBond.Order.SINGLE);
        m.addBond(27, 28, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(22, 28, IBond.Order.SINGLE);
        m.addBond(21, 29, IBond.Order.SINGLE);
        m.addBond(4, 30, IBond.Order.SINGLE);
        m.addBond(30, 27, IBond.Order.SINGLE);
        m.addBond(23, 30, IBond.Order.SINGLE);
        
        EdgeToBondMap      bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]            graph      = GraphUtil.toAdjList(m, bondMap);

        Stereocenters stereocenters = new Stereocenters(m, graph, bondMap);
        stereocenters.checkSymmetry();
        CyclicCarbohydrateRecognition recon = new CyclicCarbohydrateRecognition(m, graph, bondMap,
                                                                                stereocenters);

        List<IStereoElement> elements = recon.recognise(Collections.singleton(Projection.Haworth));
        assertTetrahedralCenter(elements.get(0),
                                m.getAtom(1),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(5), m.getAtom(0), m.getAtom(1), m.getAtom(2));
        assertTetrahedralCenter(elements.get(1),
                                m.getAtom(2),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(2), m.getAtom(1), m.getAtom(7), m.getAtom(3));
        assertTetrahedralCenter(elements.get(2),
                                m.getAtom(3),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(3), m.getAtom(2), m.getAtom(8), m.getAtom(4));
        assertTetrahedralCenter(elements.get(3),
                                m.getAtom(4),
                                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE,
                                m.getAtom(30), m.getAtom(3), m.getAtom(4), m.getAtom(0));
    }
    
    /**
     * avoid false positive
     * @cdk.inchi InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2
     */
    @Test public void hexopyranose() {
        IAtomContainer m = new AtomContainer(12, 12, 0, 0);
        m.addAtom(atom("O", 1, 0.00d, 2.48d));
        m.addAtom(atom("C", 2, 0.71d, 2.06d));
        m.addAtom(atom("C", 1, 0.71d, 1.24d));
        m.addAtom(atom("O", 0, 1.43d, 0.82d));
        m.addAtom(atom("C", 1, 1.43d, -0.00d));
        m.addAtom(atom("O", 1, 2.14d, -0.41d));
        m.addAtom(atom("C", 1, 0.71d, -0.41d));
        m.addAtom(atom("O", 1, 0.71d, -1.24d));
        m.addAtom(atom("C", 1, -0.00d, 0.00d));
        m.addAtom(atom("O", 1, -0.71d, -0.41d));
        m.addAtom(atom("C", 1, 0.00d, 0.83d));
        m.addAtom(atom("O", 1, -0.71d, 1.24d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(2, 1, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addBond(6, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(8, 10, IBond.Order.SINGLE);
        m.addBond(2, 10, IBond.Order.SINGLE);
        m.addBond(10, 11, IBond.Order.SINGLE);
        EdgeToBondMap      bondMap    = EdgeToBondMap.withSpaceFor(m);
        int[][]            graph      = GraphUtil.toAdjList(m, bondMap);

        Stereocenters stereocenters = new Stereocenters(m, graph, bondMap);
        stereocenters.checkSymmetry();
        CyclicCarbohydrateRecognition recon = new CyclicCarbohydrateRecognition(m, graph, bondMap,
                                                                                stereocenters);

        assertTrue(recon.recognise(Collections.singleton(Projection.Haworth)).isEmpty());
    }

    /**
     * Given a chair projection of beta-D-glucose we rotate it from -80 -> +80
     * and check the interpretation is the same. Going upside down inverts all
     * configurations.
     * 
     * @cdk.inchi InChI=1/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5?,6-/s2
     */
    @Test public void betaDGlucose_Chair_Rotated() throws Exception {
        IAtomContainer m = new AtomContainer(12, 12, 0, 0);
        m.addAtom(atom("C", 1, -0.77d, 10.34d));
        m.addAtom(atom("C", 1, 0.03d, 10.13d));
        m.addAtom(atom("O", 0, 0.83d, 10.34d));
        m.addAtom(atom("C", 1, 1.24d, 9.63d));
        m.addAtom(atom("C", 1, 0.44d, 9.84d));
        m.addAtom(atom("C", 1, -0.35d, 9.63d));
        m.addAtom(atom("O", 1, 0.86d, 9.13d));
        m.addAtom(atom("O", 1, 2.04d, 9.84d));
        m.addAtom(atom("C", 2, -0.68d, 10.54d));
        m.addAtom(atom("O", 1, -0.68d, 11.37d));
        m.addAtom(atom("O", 1, -1.48d, 9.93d));
        m.addAtom(atom("O", 1, -1.15d, 9.84d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 0, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);
        m.addBond(1, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(0, 10, IBond.Order.SINGLE);
        m.addBond(5, 11, IBond.Order.SINGLE);

        Point2d center = GeometryUtil.get2DCenter(m);
        GeometryUtil.rotate(m, center, Math.toRadians(-80));

        for (int i = 0; i < 30; i++) {
            GeometryUtil.rotate(m, center, Math.toRadians(5));
            
            EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(m);
            int[][] graph = GraphUtil.toAdjList(m, bondMap);
            Stereocenters stereocenters = new Stereocenters(m, graph, bondMap);
            stereocenters.checkSymmetry();
            CyclicCarbohydrateRecognition recon = new CyclicCarbohydrateRecognition(m, graph, bondMap,
                                                                                    stereocenters);


            List<IStereoElement> elements = recon.recognise(Collections.singleton(Projection.Chair));
            m.setStereoElements(elements);

            assertTetrahedralCenter(elements.get(0),
                                    m.getAtom(1),
                                    ITetrahedralChirality.Stereo.CLOCKWISE,
                                    m.getAtom(8), m.getAtom(0), m.getAtom(1), m.getAtom(2));
            assertTetrahedralCenter(elements.get(1),
                                    m.getAtom(3),
                                    ITetrahedralChirality.Stereo.CLOCKWISE,
                                    m.getAtom(7), m.getAtom(2), m.getAtom(3), m.getAtom(4));
            assertTetrahedralCenter(elements.get(2),
                                    m.getAtom(4),
                                    ITetrahedralChirality.Stereo.CLOCKWISE,
                                    m.getAtom(4), m.getAtom(3), m.getAtom(6), m.getAtom(5));
            assertTetrahedralCenter(elements.get(3),
                                    m.getAtom(5),
                                    ITetrahedralChirality.Stereo.CLOCKWISE,
                                    m.getAtom(11), m.getAtom(4), m.getAtom(5), m.getAtom(0));
            assertTetrahedralCenter(elements.get(4),
                                    m.getAtom(0),
                                    ITetrahedralChirality.Stereo.CLOCKWISE,
                                    m.getAtom(0), m.getAtom(5), m.getAtom(10), m.getAtom(1));
        }
    }

    /**
     * p-menthane (CHEBI:25826)
     * @cdk.inchi InChI=1S/C10H20/c1-8(2)10-6-4-9(3)5-7-10/h8-10H,4-7H2,1-3H3
     */
    @Test public void haworthFalsePositive() {
        IAtomContainer m = new AtomContainer(10, 10, 0, 0);
        m.addAtom(atom("C", 2, -0.71d, 0.41d));
        m.addAtom(atom("C", 2, 0.71d, -0.41d));
        m.addAtom(atom("C", 2, 0.71d, 0.41d));
        m.addAtom(atom("C", 2, -0.71d, -0.41d));
        m.addAtom(atom("C", 1, 0.00d, 0.82d));
        m.addAtom(atom("C", 3, 0.00d, 1.65d));
        m.addAtom(atom("C", 3, -0.71d, -2.06d));
        m.addAtom(atom("C", 1, -0.00d, -1.65d));
        m.addAtom(atom("C", 3, 0.71d, -2.06d));
        m.addAtom(atom("C", 1, -0.00d, -0.83d));
        m.addBond(9, 3, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(1, 9, IBond.Order.SINGLE);
        m.addBond(4, 0, IBond.Order.SINGLE);
        m.addBond(2, 1, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE);
        m.addBond(9, 7, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(7, 6, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);

        EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(m);
        int[][] graph = GraphUtil.toAdjList(m, bondMap);
        Stereocenters stereocenters = new Stereocenters(m, graph, bondMap);
        stereocenters.checkSymmetry();
        CyclicCarbohydrateRecognition recon = new CyclicCarbohydrateRecognition(m, graph, bondMap,
                                                                                stereocenters);


        List<IStereoElement> elements = recon.recognise(Collections.singleton(Projection.Haworth));
        assertTrue(elements.isEmpty());
    }

    /**
     * prolinate (CHEBI:32871)
     * @cdk.cite InChI=1S/C5H9NO2/c7-5(8)4-2-1-3-6-4/h4,6H,1-3H2,(H,7,8)/p-1
     */
    @Test public void requireAtLeastTwoProjectedSubstituents() {
        IAtomContainer m = new AtomContainer(8, 8, 0, 0);
        m.addAtom(atom("O", 0, -0.71d, 1.24d));
        m.addAtom(atom("C", 0, 0.00d, 0.83d));
        m.addAtom(atom("O", 0, 0.71d, 1.24d));
        m.addAtom(atom("C", 1, 0.00d, 0.00d));
        m.addAtom(atom("C", 2, -0.67d, -0.48d));
        m.addAtom(atom("C", 2, -0.41d, -1.27d));
        m.addAtom(atom("C", 2, 0.41d, -1.27d));
        m.addAtom(atom("N", 1, 0.67d, -0.48d));
        m.addBond(6, 5, IBond.Order.SINGLE);
        m.addBond(1, 0, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(2, 1, IBond.Order.SINGLE);
        m.addBond(3, 1, IBond.Order.SINGLE);
        m.addBond(5, 4, IBond.Order.SINGLE);
        m.addBond(4, 3, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(m);
        int[][] graph = GraphUtil.toAdjList(m, bondMap);
        Stereocenters stereocenters = new Stereocenters(m, graph, bondMap);
        stereocenters.checkSymmetry();
        CyclicCarbohydrateRecognition recon = new CyclicCarbohydrateRecognition(m, graph, bondMap,
                                                                                stereocenters);

        List<IStereoElement> elements = recon.recognise(Collections.singleton(Projection.Haworth));
        assertTrue(elements.isEmpty());
    }

    static void assertTetrahedralCenter(IStereoElement element,
                                        IAtom focus,
                                        ITetrahedralChirality.Stereo winding,
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
