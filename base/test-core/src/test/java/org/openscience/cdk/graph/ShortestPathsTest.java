/*
 * Copyright (C) 2012 John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.graph;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.Bond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * unit tests for ShortestPaths.
 * @author John May
 * @cdk.module test-core
 */
public class ShortestPathsTest {

    @Test
    public void testConstructor_Container_Empty() {

        ShortestPaths sp = new ShortestPaths(new AtomContainer(), new Atom());

        assertArrayEquals(new int[0], sp.pathTo(1));
        assertArrayEquals(new int[0][0], sp.pathsTo(1));
        assertThat(sp.nPathsTo(1), is(0));
        assertThat(sp.distanceTo(1), is(Integer.MAX_VALUE));

    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_Container_Null() {
        new ShortestPaths(null, new Atom());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Container_MissingAtom() {
        new ShortestPaths(TestMoleculeFactory.makeBenzene(), new Atom());
    }

    @Test
    public void testPathTo_Atom_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[]{0, 1}, paths.pathTo(simple.getAtom(1)));
        assertArrayEquals(new int[]{0, 1, 2}, paths.pathTo(simple.getAtom(2)));
        assertArrayEquals(new int[]{0, 1, 2, 3}, paths.pathTo(simple.getAtom(3)));
        assertArrayEquals(new int[]{0, 1, 4}, paths.pathTo(simple.getAtom(4)));

    }

    @Test
    public void testPathTo_Int_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[]{0, 1}, paths.pathTo(1));
        assertArrayEquals(new int[]{0, 1, 2}, paths.pathTo(2));
        assertArrayEquals(new int[]{0, 1, 2, 3}, paths.pathTo(3));
        assertArrayEquals(new int[]{0, 1, 4}, paths.pathTo(4));

    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testPathTo_Atom_Benzene() {

        IAtomContainer simple = TestMoleculeFactory.makeBenzene();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[]{0, 1, 2, 3}, paths.pathTo(simple.getAtom(3)));

    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testPathTo_Int_Benzene() {

        IAtomContainer simple = TestMoleculeFactory.makeBenzene();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[]{0, 1, 2, 3}, paths.pathTo(3));

    }

    @Test
    public void testIsPrecedingPathTo() {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        int[][] graph = GraphUtil.toAdjList(benzene);
        int[] order = new int[]{0, 1, 2, 3, 4, 5};
        ShortestPaths paths = new ShortestPaths(graph, benzene, 0, order);
        assertFalse(paths.isPrecedingPathTo(1));
        assertFalse(paths.isPrecedingPathTo(2));
        assertFalse(paths.isPrecedingPathTo(3));
        assertFalse(paths.isPrecedingPathTo(4));
        assertFalse(paths.isPrecedingPathTo(5));

        paths = new ShortestPaths(graph, benzene, 5, order);
        assertTrue(paths.isPrecedingPathTo(4));
        assertTrue(paths.isPrecedingPathTo(3));
        assertTrue(paths.isPrecedingPathTo(2));
        assertTrue(paths.isPrecedingPathTo(1));
        assertTrue(paths.isPrecedingPathTo(0));

        paths = new ShortestPaths(graph, benzene, 4, order);
        assertFalse(paths.isPrecedingPathTo(5));
        assertTrue(paths.isPrecedingPathTo(3));
        assertTrue(paths.isPrecedingPathTo(2));
        assertTrue(paths.isPrecedingPathTo(1));

        // shortest path to 0 is 4,5,0...
        assertFalse(paths.isPrecedingPathTo(0));
        //   1 - 2
        //  /     \
        // 0       3
        //  \     /
        //   5 - 4
    }

    @Test
    public void testIsPrecedingPathTo_OutOfBounds() {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        ShortestPaths paths = new ShortestPaths(benzene, benzene.getAtom(0));
        assertFalse(paths.isPrecedingPathTo(-1));
        assertFalse(paths.isPrecedingPathTo(10));
    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testPathTo_Atom_Norbornane() {
        IAtomContainer norbornane = norbornane();
        ShortestPaths paths = new ShortestPaths(norbornane, norbornane.getAtom(0));
        assertArrayEquals(new int[]{0, 1, 2, 3}, paths.pathTo(norbornane.getAtom(3)));
    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testPathTo_Int_Norbornane() {
        IAtomContainer norbornane = norbornane();
        ShortestPaths paths = new ShortestPaths(norbornane, norbornane.getAtom(0));
        assertArrayEquals(new int[]{0, 1, 2, 3}, paths.pathTo(3));
    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testPathTo_Atom_Spiroundecane() {
        IAtomContainer spiroundecane = spiroundecane();
        ShortestPaths paths = new ShortestPaths(spiroundecane, spiroundecane.getAtom(1));
        assertArrayEquals(new int[]{1, 0, 5, 4, 6, 10, 9}, paths.pathTo(spiroundecane.getAtom(9)));
    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testPathTo_Int_Spiroundecane() {
        IAtomContainer spiroundecane = spiroundecane();
        ShortestPaths paths = new ShortestPaths(spiroundecane, spiroundecane.getAtom(1));
        assertArrayEquals(new int[]{1, 0, 5, 4, 6, 10, 9}, paths.pathTo(9));
    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testPathTo_Atom_Pentadecaspiro() {

        //   3 - // ... //  - 4
        //  /        \ /         \
        // 0          x           1
        //  \        / \         /
        //   2 - // ...  // - 5

        // bridgehead atoms 0, 66, 68, 70, 72, 74, 76, 78, 80, 79, 77, 75, 73, 71, 69, 67, 1

        IAtomContainer pentadecaspiro = pentadecaspiro();
        ShortestPaths paths = new ShortestPaths(pentadecaspiro, pentadecaspiro.getAtom(0));

        // first path is determined by storage order and will always be the same
        int[] expected = new int[]{0, 2, 6, 66, 10, 14, 68, 18, 22, 70, 26, 30, 72, 34, 38, 74, 42, 46, 76, 50, 54, 78,
                58, 62, 80, 64, 60, 79, 56, 52, 77, 48, 44, 75, 40, 36, 73, 32, 28, 71, 24, 20, 69, 16, 12, 67, 8, 4, 1};

        int[] path = paths.pathTo(pentadecaspiro.getAtom(1));
        assertArrayEquals(expected, path);

    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testPathTo_Int_Pentadecaspiro() {

        //   3 - // ... //  - 4
        //  /        \ /         \
        // 0          x           1
        //  \        / \         /
        //   2 - // ...  // - 5

        // bridgehead atoms 0, 66, 68, 70, 72, 74, 76, 78, 80, 79, 77, 75, 73, 71, 69, 67, 1

        IAtomContainer pentadecaspiro = pentadecaspiro();
        ShortestPaths paths = new ShortestPaths(pentadecaspiro, pentadecaspiro.getAtom(0));

        // first path is determined by storage order and will always be the same
        int[] expected = new int[]{0, 2, 6, 66, 10, 14, 68, 18, 22, 70, 26, 30, 72, 34, 38, 74, 42, 46, 76, 50, 54, 78,
                58, 62, 80, 64, 60, 79, 56, 52, 77, 48, 44, 75, 40, 36, 73, 32, 28, 71, 24, 20, 69, 16, 12, 67, 8, 4, 1};

        int[] path = paths.pathTo(1);
        assertArrayEquals(expected, path);

    }

    @Test
    public void testPathTo_Int_OutOfBoundsIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new int[0], paths.pathTo(20));
    }

    @Test
    public void testPathTo_Int_NegativeIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new int[0], paths.pathTo(-1));
    }

    @Test
    public void testPathTo_Atom_MissingAtom() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new int[0], paths.pathTo(new Atom("C")));
    }

    @Test
    public void testPathTo_Atom_Null() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new int[0], paths.pathTo(null));
    }

    @Test
    public void testPathTo_Atom_Disconnected() {

        IAtomContainer simple = disconnected();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[]{0, 1}, paths.pathTo(simple.getAtom(1)));
        assertArrayEquals(new int[]{0, 1, 2}, paths.pathTo(simple.getAtom(2)));
        assertArrayEquals(new int[]{0, 1, 2, 3}, paths.pathTo(simple.getAtom(3)));
        assertArrayEquals(new int[]{0, 1, 4}, paths.pathTo(simple.getAtom(4)));

        // disconnect fragment should return 0 length path
        assertArrayEquals(new int[0], paths.pathTo(simple.getAtom(5)));
        assertArrayEquals(new int[0], paths.pathTo(simple.getAtom(6)));
        assertArrayEquals(new int[0], paths.pathTo(simple.getAtom(7)));
        assertArrayEquals(new int[0], paths.pathTo(simple.getAtom(8)));
        assertArrayEquals(new int[0], paths.pathTo(simple.getAtom(9)));

    }

    @Test
    public void testPathTo_Int_Disconnected() {

        IAtomContainer simple = disconnected();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[]{0, 1}, paths.pathTo(1));
        assertArrayEquals(new int[]{0, 1, 2}, paths.pathTo(2));
        assertArrayEquals(new int[]{0, 1, 2, 3}, paths.pathTo(3));
        assertArrayEquals(new int[]{0, 1, 4}, paths.pathTo(4));

        // disconnect fragment should return 0 length path
        assertArrayEquals(new int[0], paths.pathTo(5));
        assertArrayEquals(new int[0], paths.pathTo(6));
        assertArrayEquals(new int[0], paths.pathTo(7));
        assertArrayEquals(new int[0], paths.pathTo(8));
        assertArrayEquals(new int[0], paths.pathTo(9));

    }

    @Test
    public void testPathsTo_Atom_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[][]{{0, 1}}, paths.pathsTo(simple.getAtom(1)));
        assertArrayEquals(new int[][]{{0, 1, 2}}, paths.pathsTo(simple.getAtom(2)));
        assertArrayEquals(new int[][]{{0, 1, 2, 3}}, paths.pathsTo(simple.getAtom(3)));
        assertArrayEquals(new int[][]{{0, 1, 4}}, paths.pathsTo(simple.getAtom(4)));

    }

    @Test
    public void testPathsTo_Int_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[][]{{0, 1}}, paths.pathsTo(1));
        assertArrayEquals(new int[][]{{0, 1, 2}}, paths.pathsTo(2));
        assertArrayEquals(new int[][]{{0, 1, 2, 3}}, paths.pathsTo(3));
        assertArrayEquals(new int[][]{{0, 1, 4}}, paths.pathsTo(4));

    }

    @Test
    public void testPathsTo_Atom_Benzene() {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

        ShortestPaths paths = new ShortestPaths(benzene, benzene.getAtom(0));

        int[][] expected = new int[][]{{0, 1, 2, 3}, {0, 5, 4, 3}};
        assertArrayEquals(expected, paths.pathsTo(benzene.getAtom(3)));

    }

    @Test
    public void testPathsTo_Atom_Spiroundecane() {

        IAtomContainer spiroundecane = spiroundecane();

        ShortestPaths paths = new ShortestPaths(spiroundecane, spiroundecane.getAtom(1));

        //   2 -- 3   7 -- 8
        //  /      \ /      \
        // 1        4        9
        //  \      / \      /
        //   0 -- 5   6 - 10

        // path order is determined by storage order, given the same input,
        // the output order will never change
        int[][] expected = new int[][]{{1, 0, 5, 4, 6, 10, 9}, {1, 2, 3, 4, 6, 10, 9}, {1, 0, 5, 4, 7, 8, 9},
                {1, 2, 3, 4, 7, 8, 9}};

        assertArrayEquals(expected, paths.pathsTo(spiroundecane.getAtom(9)));

    }

    @Test
    public void testPathsTo_Int_Spiroundecane() {

        IAtomContainer spiroundecane = spiroundecane();

        ShortestPaths paths = new ShortestPaths(spiroundecane, spiroundecane.getAtom(1));

        //   2 -- 3   7 -- 8
        //  /      \ /      \
        // 1        4        9
        //  \      / \      /
        //   0 -- 5   6 - 10

        // path order is determined by storage order, given the same input,
        // the output order will never change
        int[][] expected = new int[][]{{1, 0, 5, 4, 6, 10, 9}, {1, 2, 3, 4, 6, 10, 9}, {1, 0, 5, 4, 7, 8, 9},
                {1, 2, 3, 4, 7, 8, 9}};

        assertArrayEquals(expected, paths.pathsTo(9));

    }

    @Test
    public void testPathsTo_Int_Benzene() {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

        ShortestPaths paths = new ShortestPaths(benzene, benzene.getAtom(0));

        int[][] expected = new int[][]{{0, 1, 2, 3}, {0, 5, 4, 3}};
        assertArrayEquals(expected, paths.pathsTo(3));

    }

    @Test
    public void testPathsTo_Atom_Norbornane() {
        IAtomContainer norbornane = norbornane();
        ShortestPaths paths = new ShortestPaths(norbornane, norbornane.getAtom(0));
        int[][] expected = new int[][]{{0, 1, 2, 3}, {0, 5, 4, 3}, {0, 6, 7, 3}};
        assertArrayEquals(expected, paths.pathsTo(norbornane.getAtom(3)));
    }

    @Test
    public void testPathsTo_Int_Norbornane() {
        IAtomContainer norbornane = norbornane();
        ShortestPaths paths = new ShortestPaths(norbornane, norbornane.getAtom(0));
        int[][] expected = new int[][]{{0, 1, 2, 3}, {0, 5, 4, 3}, {0, 6, 7, 3}};
        assertArrayEquals(expected, paths.pathsTo(3));
    }

    @Test
    public void testPathsTo_Atom_Pentadecaspiro() {

        //   3 - // ... //  - 4
        //  /        \ /         \
        // 0          x           1
        //  \        / \         /
        //   2 - // ...  // - 5

        // bridgehead atoms 0, 66, 68, 70, 72, 74, 76, 78, 80, 79, 77, 75, 73, 71, 69, 67, 1

        IAtomContainer pentadecaspiro = pentadecaspiro();
        ShortestPaths paths = new ShortestPaths(pentadecaspiro, pentadecaspiro.getAtom(0));

        int[] bridgeheads = new int[]{66, 68, 70, 72, 74, 76, 78, 80, 79, 77, 75, 73, 71, 69, 67, 1};

        // demonstrates that all paths up and beyond 65,000+ can be retrieved
        for (int i = 0; i < bridgeheads.length; i++) {

            int bridgehead = bridgeheads[i];

            int[][] path = paths.pathsTo(pentadecaspiro.getAtom(bridgehead));

            int n = (int) Math.pow(2, (i + 1));
            assertThat(path.length, is(n));

            // test is too long when more then 500 different paths
            if (n < 500) {
                for (int j = 0; j < n; j++) {

                    // check the first atom is '0' and the last atom is the 'bridgehead'
                    assertThat(path[j][0], is(0));
                    assertThat(path[j][paths.distanceTo(bridgehead)], is(bridgehead));

                    // check all paths are unique
                    for (int k = j + 1; k < n; k++) {
                        // hamcrest matcher does array comparison
                        assertThat(path[j], is(not(equalTo(path[k]))));
                    }
                }
            }

        }

    }

    @Test
    public void testPathsTo_Int_OutOfBoundsIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new int[0][0], paths.pathsTo(20));
    }

    @Test
    public void testPathsTo_Int_NegativeIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new int[0][0], paths.pathsTo(-1));
    }

    @Test
    public void testPathsTo_Atom_MissingAtom() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new int[0][0], paths.pathsTo(new Atom("C")));
    }

    @Test
    public void testPathsTo_Atom_Null() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new int[0][0], paths.pathsTo(null));
    }

    @Test
    public void testPathsTo_Atom_Disconnected() {

        IAtomContainer simple = disconnected();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[][]{{0, 1}}, paths.pathsTo(simple.getAtom(1)));
        assertArrayEquals(new int[][]{{0, 1, 2}}, paths.pathsTo(simple.getAtom(2)));
        assertArrayEquals(new int[][]{{0, 1, 2, 3}}, paths.pathsTo(simple.getAtom(3)));
        assertArrayEquals(new int[][]{{0, 1, 4}}, paths.pathsTo(simple.getAtom(4)));

        // disconnect fragment should return 0 length path
        assertArrayEquals(new int[0][0], paths.pathsTo(simple.getAtom(5)));
        assertArrayEquals(new int[0][0], paths.pathsTo(simple.getAtom(6)));
        assertArrayEquals(new int[0][0], paths.pathsTo(simple.getAtom(7)));
        assertArrayEquals(new int[0][0], paths.pathsTo(simple.getAtom(8)));
        assertArrayEquals(new int[0][0], paths.pathsTo(simple.getAtom(9)));

    }

    @Test
    public void testPathsTo_Int_Disconnected() {

        IAtomContainer simple = disconnected();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertArrayEquals(new int[][]{{0, 1}}, paths.pathsTo(1));
        assertArrayEquals(new int[][]{{0, 1, 2}}, paths.pathsTo(2));
        assertArrayEquals(new int[][]{{0, 1, 2, 3}}, paths.pathsTo(3));
        assertArrayEquals(new int[][]{{0, 1, 4}}, paths.pathsTo(4));

        // disconnect fragment should return 0 length path
        assertArrayEquals(new int[0][0], paths.pathsTo(5));
        assertArrayEquals(new int[0][0], paths.pathsTo(6));
        assertArrayEquals(new int[0][0], paths.pathsTo(7));
        assertArrayEquals(new int[0][0], paths.pathsTo(8));
        assertArrayEquals(new int[0][0], paths.pathsTo(9));

    }

    @Test
    public void testAtomsTo_Atom_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        IAtom a = simple.getAtom(0);
        IAtom b = simple.getAtom(1);
        IAtom c = simple.getAtom(2);
        IAtom d = simple.getAtom(3);
        IAtom e = simple.getAtom(4);

        assertArrayEquals(new IAtom[]{a, b}, paths.atomsTo(b));
        assertArrayEquals(new IAtom[]{a, b, c}, paths.atomsTo(c));
        assertArrayEquals(new IAtom[]{a, b, c, d}, paths.atomsTo(d));
        assertArrayEquals(new IAtom[]{a, b, e}, paths.atomsTo(e));

    }

    @Test
    public void testAtomsTo_Int_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        IAtom a = simple.getAtom(0);
        IAtom b = simple.getAtom(1);
        IAtom c = simple.getAtom(2);
        IAtom d = simple.getAtom(3);
        IAtom e = simple.getAtom(4);

        assertArrayEquals(new IAtom[]{a, b}, paths.atomsTo(1));
        assertArrayEquals(new IAtom[]{a, b, c}, paths.atomsTo(2));
        assertArrayEquals(new IAtom[]{a, b, c, d}, paths.atomsTo(3));
        assertArrayEquals(new IAtom[]{a, b, e}, paths.atomsTo(4));

    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testAtomsTo_Atom_Benzene() {

        IAtomContainer simple = TestMoleculeFactory.makeBenzene();

        IAtom c1 = simple.getAtom(0);
        IAtom c2 = simple.getAtom(1);
        IAtom c3 = simple.getAtom(2);
        IAtom c4 = simple.getAtom(3);

        ShortestPaths paths = new ShortestPaths(simple, c1);

        assertArrayEquals(new IAtom[]{c1, c2, c3, c4}, paths.atomsTo(c4));

    }

    /**
     * ensures that when multiple paths are available, one path is still
     * returned via {@link ShortestPaths#pathTo(org.openscience.cdk.interfaces.IAtom)}
     */
    @Test
    public void testAtomsTo_Int_Benzene() {

        IAtomContainer simple = TestMoleculeFactory.makeBenzene();

        IAtom c1 = simple.getAtom(0);
        IAtom c2 = simple.getAtom(1);
        IAtom c3 = simple.getAtom(2);
        IAtom c4 = simple.getAtom(3);

        ShortestPaths paths = new ShortestPaths(simple, c1);

        assertArrayEquals(new IAtom[]{c1, c2, c3, c4}, paths.atomsTo(3));

    }

    @Test
    public void testAtomsTo_Atom_Disconnected() {

        IAtomContainer simple = disconnected();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        IAtom a = simple.getAtom(0);
        IAtom b = simple.getAtom(1);
        IAtom c = simple.getAtom(2);
        IAtom d = simple.getAtom(3);
        IAtom e = simple.getAtom(4);
        IAtom f = simple.getAtom(5);
        IAtom g = simple.getAtom(6);
        IAtom h = simple.getAtom(7);
        IAtom i = simple.getAtom(8);
        IAtom j = simple.getAtom(9);

        assertArrayEquals(new IAtom[]{a, b}, paths.atomsTo(b));
        assertArrayEquals(new IAtom[]{a, b, c}, paths.atomsTo(c));
        assertArrayEquals(new IAtom[]{a, b, c, d}, paths.atomsTo(d));
        assertArrayEquals(new IAtom[]{a, b, e}, paths.atomsTo(e));

        // disconnect fragment should return 0 length path
        assertArrayEquals(new IAtom[0], paths.atomsTo(f));
        assertArrayEquals(new IAtom[0], paths.atomsTo(g));
        assertArrayEquals(new IAtom[0], paths.atomsTo(h));
        assertArrayEquals(new IAtom[0], paths.atomsTo(i));
        assertArrayEquals(new IAtom[0], paths.atomsTo(j));

    }

    @Test
    public void testAtomsTo_Int_Disconnected() {

        IAtomContainer simple = disconnected();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        IAtom a = simple.getAtom(0);
        IAtom b = simple.getAtom(1);
        IAtom c = simple.getAtom(2);
        IAtom d = simple.getAtom(3);
        IAtom e = simple.getAtom(4);

        assertArrayEquals(new IAtom[]{a, b}, paths.atomsTo(1));
        assertArrayEquals(new IAtom[]{a, b, c}, paths.atomsTo(2));
        assertArrayEquals(new IAtom[]{a, b, c, d}, paths.atomsTo(3));
        assertArrayEquals(new IAtom[]{a, b, e}, paths.atomsTo(4));

        // disconnect fragment should return 0 length path
        assertArrayEquals(new IAtom[0], paths.atomsTo(5));
        assertArrayEquals(new IAtom[0], paths.atomsTo(6));
        assertArrayEquals(new IAtom[0], paths.atomsTo(7));
        assertArrayEquals(new IAtom[0], paths.atomsTo(8));
        assertArrayEquals(new IAtom[0], paths.atomsTo(9));

    }

    @Test
    public void testAtomsTo_Int_OutOfBoundsIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new IAtom[0], paths.atomsTo(20));
    }

    @Test
    public void testAtomsTo_Int_NegativeIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new IAtom[0], paths.atomsTo(-1));
    }

    @Test
    public void testAtomsTo_Atom_MissingAtom() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new IAtom[0], paths.atomsTo(new Atom("C")));
    }

    @Test
    public void testAtomsTo_Atom_Null() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertArrayEquals(new IAtom[0], paths.atomsTo(null));
    }

    @Test
    public void testNPathsTo_Atom_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertThat(paths.nPathsTo(simple.getAtom(0)), is(1));
        assertThat(paths.nPathsTo(simple.getAtom(1)), is(1));
        assertThat(paths.nPathsTo(simple.getAtom(2)), is(1));
        assertThat(paths.nPathsTo(simple.getAtom(3)), is(1));
        assertThat(paths.nPathsTo(simple.getAtom(4)), is(1));

    }

    @Test
    public void testNPathsTo_Int_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertThat(paths.nPathsTo(1), is(1));
        assertThat(paths.nPathsTo(2), is(1));
        assertThat(paths.nPathsTo(3), is(1));
        assertThat(paths.nPathsTo(4), is(1));

    }

    @Test
    public void testNPathsTo_Atom_MissingAtom() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertThat(paths.nPathsTo(new Atom("C")), is(0));
    }

    @Test
    public void testNPathsTo_Atom_Null() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertThat(paths.nPathsTo(null), is(0));
    }

    @Test
    public void testNPathsTo_Int_OutOfBoundIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertThat(paths.nPathsTo(20), is(0));
    }

    @Test
    public void testNPathsTo_Int_NegativeIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertThat(paths.nPathsTo(-1), is(0));
    }

    @Test
    public void testNPathsTo_Atom_Disconnected() {

        IAtomContainer container = disconnected();

        ShortestPaths paths = new ShortestPaths(container, container.getAtom(0));

        assertThat(paths.nPathsTo(container.getAtom(0)), is(1));
        assertThat(paths.nPathsTo(container.getAtom(1)), is(1));
        assertThat(paths.nPathsTo(container.getAtom(2)), is(1));
        assertThat(paths.nPathsTo(container.getAtom(3)), is(1));
        assertThat(paths.nPathsTo(container.getAtom(4)), is(1));

        assertThat(paths.nPathsTo(container.getAtom(5)), is(0));
        assertThat(paths.nPathsTo(container.getAtom(6)), is(0));
        assertThat(paths.nPathsTo(container.getAtom(7)), is(0));
        assertThat(paths.nPathsTo(container.getAtom(8)), is(0));
        assertThat(paths.nPathsTo(container.getAtom(9)), is(0));

    }

    @Test
    public void testNPathsTo_Int_Disconnected() {

        IAtomContainer container = disconnected();

        ShortestPaths paths = new ShortestPaths(container, container.getAtom(0));

        assertThat(paths.nPathsTo(0), is(1));
        assertThat(paths.nPathsTo(1), is(1));
        assertThat(paths.nPathsTo(2), is(1));
        assertThat(paths.nPathsTo(3), is(1));
        assertThat(paths.nPathsTo(4), is(1));

        assertThat(paths.nPathsTo(5), is(0));
        assertThat(paths.nPathsTo(6), is(0));
        assertThat(paths.nPathsTo(7), is(0));
        assertThat(paths.nPathsTo(8), is(0));
        assertThat(paths.nPathsTo(9), is(0));

    }

    @Test
    public void testNPathsTo_Atom_Benzene() {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

        ShortestPaths paths = new ShortestPaths(benzene, benzene.getAtom(0));

        assertThat(paths.nPathsTo(benzene.getAtom(0)), is(1));
        assertThat(paths.nPathsTo(benzene.getAtom(1)), is(1));
        assertThat(paths.nPathsTo(benzene.getAtom(2)), is(1));
        assertThat(paths.nPathsTo(benzene.getAtom(3)), is(2));
        assertThat(paths.nPathsTo(benzene.getAtom(4)), is(1));
        assertThat(paths.nPathsTo(benzene.getAtom(5)), is(1));

    }

    @Test
    public void testNPathsTo_Int_Benzene() {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

        ShortestPaths paths = new ShortestPaths(benzene, benzene.getAtom(0));

        assertThat(paths.nPathsTo(0), is(1));
        assertThat(paths.nPathsTo(1), is(1));
        assertThat(paths.nPathsTo(2), is(1));
        assertThat(paths.nPathsTo(3), is(2));
        assertThat(paths.nPathsTo(4), is(1));
        assertThat(paths.nPathsTo(5), is(1));

    }

    @Test
    public void testNPathsTo_Atom_Norbornane() {

        IAtomContainer norbornane = norbornane();

        ShortestPaths paths = new ShortestPaths(norbornane, norbornane.getAtom(0));

        assertThat(paths.nPathsTo(norbornane.getAtom(0)), is(1));
        assertThat(paths.nPathsTo(norbornane.getAtom(1)), is(1));
        assertThat(paths.nPathsTo(norbornane.getAtom(2)), is(1));
        assertThat(paths.nPathsTo(norbornane.getAtom(3)), is(3));
        assertThat(paths.nPathsTo(norbornane.getAtom(4)), is(1));
        assertThat(paths.nPathsTo(norbornane.getAtom(5)), is(1));
        assertThat(paths.nPathsTo(norbornane.getAtom(6)), is(1));
        assertThat(paths.nPathsTo(norbornane.getAtom(7)), is(1));

    }

    @Test
    public void testNPathsTo_Int_Norbornane() {

        IAtomContainer norbornane = norbornane();

        ShortestPaths paths = new ShortestPaths(norbornane, norbornane.getAtom(0));

        assertThat(paths.nPathsTo(0), is(1));
        assertThat(paths.nPathsTo(1), is(1));
        assertThat(paths.nPathsTo(2), is(1));
        assertThat(paths.nPathsTo(3), is(3));
        assertThat(paths.nPathsTo(4), is(1));
        assertThat(paths.nPathsTo(5), is(1));
        assertThat(paths.nPathsTo(6), is(1));
        assertThat(paths.nPathsTo(7), is(1));

    }

    @Test
    public void testNPathsTo_Atom_Spiroundecane() {
        IAtomContainer spiroundecane = spiroundecane();
        ShortestPaths paths = new ShortestPaths(spiroundecane, spiroundecane.getAtom(1));
        assertThat(paths.nPathsTo(spiroundecane.getAtom(9)), is(4));
    }

    @Test
    public void testNPathsTo_Int_Spiroundecane() {
        IAtomContainer spiroundecane = spiroundecane();
        ShortestPaths paths = new ShortestPaths(spiroundecane, spiroundecane.getAtom(1));
        assertThat(paths.nPathsTo(9), is(4));
    }

    @Test
    public void testNPathsTo_Atom_Pentadecaspiro() {

        //   3 - // ... //  - 4
        //  /        \ /         \
        // 0         x            1
        //  \        / \         /
        //   2 - // ...  // - 5

        // bridgehead atoms 0, 66, 68, 70, 72, 74, 76, 78, 80, 79, 77, 75, 73, 71, 69, 67, 1

        IAtomContainer pentadecaspiro = pentadecaspiro();
        ShortestPaths paths = new ShortestPaths(pentadecaspiro, pentadecaspiro.getAtom(0));

        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(66)), is(2));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(68)), is(4));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(70)), is(8));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(72)), is(16));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(74)), is(32));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(76)), is(64));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(78)), is(128));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(80)), is(256));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(79)), is(512));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(77)), is(1024));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(75)), is(2048));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(73)), is(4096));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(71)), is(8192));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(69)), is(16384));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(67)), is(32768));
        assertThat(paths.nPathsTo(pentadecaspiro.getAtom(1)), is(65536));
    }

    @Test
    public void testNPathsTo_Int_Pentadecaspiro() {
        IAtomContainer pentadecaspiro = pentadecaspiro();
        ShortestPaths paths = new ShortestPaths(pentadecaspiro, pentadecaspiro.getAtom(0));

        // bridgehead atoms 0, 66, 68, 70, 72, 74, 76, 78, 80, 79, 77, 75, 73, 71, 69, 67, 1

        assertThat(paths.nPathsTo(66), is(2));
        assertThat(paths.nPathsTo(68), is(4));
        assertThat(paths.nPathsTo(70), is(8));
        assertThat(paths.nPathsTo(72), is(16));
        assertThat(paths.nPathsTo(74), is(32));
        assertThat(paths.nPathsTo(76), is(64));
        assertThat(paths.nPathsTo(78), is(128));
        assertThat(paths.nPathsTo(80), is(256));
        assertThat(paths.nPathsTo(79), is(512));
        assertThat(paths.nPathsTo(77), is(1024));
        assertThat(paths.nPathsTo(75), is(2048));
        assertThat(paths.nPathsTo(73), is(4096));
        assertThat(paths.nPathsTo(71), is(8192));
        assertThat(paths.nPathsTo(69), is(16384));
        assertThat(paths.nPathsTo(67), is(32768));
        assertThat(paths.nPathsTo(1), is(65536));
    }

    @Test
    public void testDistanceTo_Atom_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertThat(paths.distanceTo(simple.getAtom(0)), is(0));
        assertThat(paths.distanceTo(simple.getAtom(1)), is(1));
        assertThat(paths.distanceTo(simple.getAtom(2)), is(2));
        assertThat(paths.distanceTo(simple.getAtom(3)), is(3));
        assertThat(paths.distanceTo(simple.getAtom(4)), is(2));

    }

    @Test
    public void testDistanceTo_Int_Simple() {

        IAtomContainer simple = simple();

        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));

        assertThat(paths.distanceTo(0), is(0));
        assertThat(paths.distanceTo(1), is(1));
        assertThat(paths.distanceTo(2), is(2));
        assertThat(paths.distanceTo(3), is(3));
        assertThat(paths.distanceTo(4), is(2));

    }

    @Test
    public void testDistanceTo_Atom_MissingAtom() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertThat(paths.distanceTo(new Atom("C")), is(Integer.MAX_VALUE));
    }

    @Test
    public void testDistanceTo_Atom_Null() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertThat(paths.distanceTo(null), is(Integer.MAX_VALUE));
    }

    @Test
    public void testDistanceTo_Int_OutOfBoundIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertThat(paths.distanceTo(20), is(Integer.MAX_VALUE));
    }

    @Test
    public void testDistanceTo_Int_NegativeIndex() {
        IAtomContainer simple = simple();
        ShortestPaths paths = new ShortestPaths(simple, simple.getAtom(0));
        assertThat(paths.distanceTo(-1), is(Integer.MAX_VALUE));
    }

    @Test
    public void testDistanceTo_Atom_Disconnected() {

        IAtomContainer container = disconnected();

        ShortestPaths paths = new ShortestPaths(container, container.getAtom(0));

        assertThat(paths.distanceTo(container.getAtom(0)), is(0));
        assertThat(paths.distanceTo(container.getAtom(1)), is(1));
        assertThat(paths.distanceTo(container.getAtom(2)), is(2));
        assertThat(paths.distanceTo(container.getAtom(3)), is(3));
        assertThat(paths.distanceTo(container.getAtom(4)), is(2));

        assertThat(paths.distanceTo(container.getAtom(5)), is(Integer.MAX_VALUE));
        assertThat(paths.distanceTo(container.getAtom(6)), is(Integer.MAX_VALUE));
        assertThat(paths.distanceTo(container.getAtom(7)), is(Integer.MAX_VALUE));
        assertThat(paths.distanceTo(container.getAtom(8)), is(Integer.MAX_VALUE));
        assertThat(paths.distanceTo(container.getAtom(9)), is(Integer.MAX_VALUE));

    }

    @Test
    public void testDistanceTo_Int_Disconnected() {

        IAtomContainer container = disconnected();

        ShortestPaths paths = new ShortestPaths(container, container.getAtom(0));

        assertThat(paths.distanceTo(0), is(0));
        assertThat(paths.distanceTo(1), is(1));
        assertThat(paths.distanceTo(2), is(2));
        assertThat(paths.distanceTo(3), is(3));
        assertThat(paths.distanceTo(4), is(2));

        assertThat(paths.distanceTo(5), is(Integer.MAX_VALUE));
        assertThat(paths.distanceTo(6), is(Integer.MAX_VALUE));
        assertThat(paths.distanceTo(7), is(Integer.MAX_VALUE));
        assertThat(paths.distanceTo(8), is(Integer.MAX_VALUE));
        assertThat(paths.distanceTo(9), is(Integer.MAX_VALUE));

    }

    @Test
    public void testDistanceTo_Atom_Benzene() {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

        ShortestPaths paths = new ShortestPaths(benzene, benzene.getAtom(0));

        assertThat(paths.distanceTo(benzene.getAtom(0)), is(0));
        assertThat(paths.distanceTo(benzene.getAtom(1)), is(1));
        assertThat(paths.distanceTo(benzene.getAtom(2)), is(2));
        assertThat(paths.distanceTo(benzene.getAtom(3)), is(3));
        assertThat(paths.distanceTo(benzene.getAtom(4)), is(2));
        assertThat(paths.distanceTo(benzene.getAtom(5)), is(1));

    }

    @Test
    public void testDistanceTo_Int_Benzene() {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

        ShortestPaths paths = new ShortestPaths(benzene, benzene.getAtom(0));

        assertThat(paths.distanceTo(0), is(0));
        assertThat(paths.distanceTo(1), is(1));
        assertThat(paths.distanceTo(2), is(2));
        assertThat(paths.distanceTo(3), is(3));
        assertThat(paths.distanceTo(4), is(2));
        assertThat(paths.distanceTo(5), is(1));

    }

    @Test
    public void testDistanceTo_Int_Benzene_limited() {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

        ShortestPaths paths = new ShortestPaths(GraphUtil.toAdjList(benzene), benzene, 0, 2, null);

        assertThat(paths.distanceTo(0), is(0));
        assertThat(paths.distanceTo(1), is(1));
        assertThat(paths.distanceTo(2), is(2));
        assertThat(paths.distanceTo(3), is(Integer.MAX_VALUE)); // dist > 2 (our limit)
        assertThat(paths.distanceTo(4), is(2));
        assertThat(paths.distanceTo(5), is(1));
    }

    @Test
    public void testDistanceTo_Atom_Spiroundecane() {
        IAtomContainer spiroundecane = spiroundecane();
        ShortestPaths paths = new ShortestPaths(spiroundecane, spiroundecane.getAtom(1));

        //   2 -- 3   7 -- 8
        //  /      \ /      \
        // 1        4        9
        //  \      / \      /
        //   0 -- 5   6 - 10

        assertThat(paths.distanceTo(spiroundecane.getAtom(0)), is(1));
        assertThat(paths.distanceTo(spiroundecane.getAtom(2)), is(1));

        assertThat(paths.distanceTo(spiroundecane.getAtom(3)), is(2));
        assertThat(paths.distanceTo(spiroundecane.getAtom(5)), is(2));

        assertThat(paths.distanceTo(spiroundecane.getAtom(4)), is(3));

        assertThat(paths.distanceTo(spiroundecane.getAtom(6)), is(4));
        assertThat(paths.distanceTo(spiroundecane.getAtom(7)), is(4));

        assertThat(paths.distanceTo(spiroundecane.getAtom(8)), is(5));
        assertThat(paths.distanceTo(spiroundecane.getAtom(10)), is(5));

        assertThat(paths.distanceTo(spiroundecane.getAtom(9)), is(6));
    }

    @Test
    public void testDistanceTo_Int_Spiroundecane() {
        IAtomContainer spiroundecane = spiroundecane();
        ShortestPaths paths = new ShortestPaths(spiroundecane, spiroundecane.getAtom(1));

        //   2 -- 3   7 -- 8
        //  /      \ /      \
        // 1        4        9
        //  \      / \      /
        //   0 -- 5   6 - 10

        assertThat(paths.distanceTo(0), is(1));
        assertThat(paths.distanceTo(2), is(1));

        assertThat(paths.distanceTo(3), is(2));
        assertThat(paths.distanceTo(5), is(2));

        assertThat(paths.distanceTo(4), is(3));

        assertThat(paths.distanceTo(6), is(4));
        assertThat(paths.distanceTo(7), is(4));

        assertThat(paths.distanceTo(8), is(5));
        assertThat(paths.distanceTo(10), is(5));

        assertThat(paths.distanceTo(9), is(6));
    }

    @Test
    public void testDistanceTo_Atom_Pentadecaspiro() {

        //   3 - // ... //  - 4
        //  /        \ /         \
        // 0          x           1
        //  \        / \         /
        //   2 - // ...  // - 5

        // bridgehead atoms 0, 66, 68, 70, 72, 74, 76, 78, 80, 79, 77, 75, 73, 71, 69, 67, 1

        IAtomContainer pentadecaspiro = pentadecaspiro();
        ShortestPaths paths = new ShortestPaths(pentadecaspiro, pentadecaspiro.getAtom(0));

        assertThat(paths.distanceTo(pentadecaspiro.getAtom(66)), is(3));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(68)), is(6));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(70)), is(9));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(72)), is(12));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(74)), is(15));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(76)), is(18));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(78)), is(21));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(80)), is(24));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(79)), is(27));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(77)), is(30));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(75)), is(33));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(73)), is(36));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(71)), is(39));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(69)), is(42));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(67)), is(45));
        assertThat(paths.distanceTo(pentadecaspiro.getAtom(1)), is(48));
    }

    @Test
    public void testDistanceTo_Int_Pentadecaspiro() {
        IAtomContainer pentadecaspiro = pentadecaspiro();
        ShortestPaths paths = new ShortestPaths(pentadecaspiro, pentadecaspiro.getAtom(0));

        // bridgehead atoms 0, 66, 68, 70, 72, 74, 76, 78, 80, 79, 77, 75, 73, 71, 69, 67, 1

        assertThat(paths.distanceTo(66), is(3));
        assertThat(paths.distanceTo(68), is(6));
        assertThat(paths.distanceTo(70), is(9));
        assertThat(paths.distanceTo(72), is(12));
        assertThat(paths.distanceTo(74), is(15));
        assertThat(paths.distanceTo(76), is(18));
        assertThat(paths.distanceTo(78), is(21));
        assertThat(paths.distanceTo(80), is(24));
        assertThat(paths.distanceTo(79), is(27));
        assertThat(paths.distanceTo(77), is(30));
        assertThat(paths.distanceTo(75), is(33));
        assertThat(paths.distanceTo(73), is(36));
        assertThat(paths.distanceTo(71), is(39));
        assertThat(paths.distanceTo(69), is(42));
        assertThat(paths.distanceTo(67), is(45));
        assertThat(paths.distanceTo(1), is(48));
    }

    /**
     * two disconnected 2,2-dimethylpropanes
     */
    private static IAtomContainer disconnected() {
        IAtomContainer container = simple();
        container.add(simple());
        return container;
    }

    /**
     * 2,2-dimethylpropane
     */
    private static IAtomContainer simple() {

        IAtomContainer container = new AtomContainer();

        IAtom a = new Atom("C");
        IAtom b = new Atom("C");
        IAtom c = new Atom("C");
        IAtom d = new Atom("C");
        IAtom e = new Atom("C");

        IBond ab = new Bond(a, b);
        IBond bc = new Bond(b, c);
        IBond cd = new Bond(c, d);
        IBond be = new Bond(b, e);

        container.addAtom(a);
        container.addAtom(b);
        container.addAtom(c);
        container.addAtom(d);
        container.addAtom(e);

        container.addBond(ab);
        container.addBond(bc);
        container.addBond(cd);
        container.addBond(be);

        return container;

    }

    /**
     * norbornane generated with CDKSourceCodeWriter
     */
    private static IAtomContainer norbornane() {

        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a8);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a4, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a5, a6, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a7, a8, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a8, a4, IBond.Order.SINGLE);
        mol.addBond(b9);

        return mol;

    }

    /**
     * pentadecaspiro[5.2.2.2.2.2.2.2.2.2.2.2.2.2.2.5^{48}.2^{45}.2^{42}.2^{39}.2^{36}.2^{33}.2^{30}.2^{27}.2^{24}.2^{21}.2^{18}.2^{15}.2^{12}.2^{9}.2^{6}]henoctacontane
     *
     * @cdk.inchi InChI=1S/C81H132/c1-3-7-67(8-4-1)11-15-69(16-12-67)19-23-71(24-20-69)27-31-73(32-28-71)35-39-75(40-36-73)43-47-77(48-44-75)51-55-79(56-52-77)59-63-81(64-60-79)65-61-80(62-66-81)57-53-78(54-58-80)49-45-76(46-50-78)41-37-74(38-42-76)33-29-72(30-34-74)25-21-70(22-26-72)17-13-68(14-18-70)9-5-2-6-10-68/h1-66H2
     */
    public IAtomContainer pentadecaspiro() {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = new Atom("C");
        mol.addAtom(a1);
        IAtom a2 = new Atom("C");
        mol.addAtom(a2);
        IAtom a3 = new Atom("C");
        mol.addAtom(a3);
        IAtom a4 = new Atom("C");
        mol.addAtom(a4);
        IAtom a5 = new Atom("C");
        mol.addAtom(a5);
        IAtom a6 = new Atom("C");
        mol.addAtom(a6);
        IAtom a7 = new Atom("C");
        mol.addAtom(a7);
        IAtom a8 = new Atom("C");
        mol.addAtom(a8);
        IAtom a9 = new Atom("C");
        mol.addAtom(a9);
        IAtom a10 = new Atom("C");
        mol.addAtom(a10);
        IAtom a11 = new Atom("C");
        mol.addAtom(a11);
        IAtom a12 = new Atom("C");
        mol.addAtom(a12);
        IAtom a13 = new Atom("C");
        mol.addAtom(a13);
        IAtom a14 = new Atom("C");
        mol.addAtom(a14);
        IAtom a15 = new Atom("C");
        mol.addAtom(a15);
        IAtom a16 = new Atom("C");
        mol.addAtom(a16);
        IAtom a17 = new Atom("C");
        mol.addAtom(a17);
        IAtom a18 = new Atom("C");
        mol.addAtom(a18);
        IAtom a19 = new Atom("C");
        mol.addAtom(a19);
        IAtom a20 = new Atom("C");
        mol.addAtom(a20);
        IAtom a21 = new Atom("C");
        mol.addAtom(a21);
        IAtom a22 = new Atom("C");
        mol.addAtom(a22);
        IAtom a23 = new Atom("C");
        mol.addAtom(a23);
        IAtom a24 = new Atom("C");
        mol.addAtom(a24);
        IAtom a25 = new Atom("C");
        mol.addAtom(a25);
        IAtom a26 = new Atom("C");
        mol.addAtom(a26);
        IAtom a27 = new Atom("C");
        mol.addAtom(a27);
        IAtom a28 = new Atom("C");
        mol.addAtom(a28);
        IAtom a29 = new Atom("C");
        mol.addAtom(a29);
        IAtom a30 = new Atom("C");
        mol.addAtom(a30);
        IAtom a31 = new Atom("C");
        mol.addAtom(a31);
        IAtom a32 = new Atom("C");
        mol.addAtom(a32);
        IAtom a33 = new Atom("C");
        mol.addAtom(a33);
        IAtom a34 = new Atom("C");
        mol.addAtom(a34);
        IAtom a35 = new Atom("C");
        mol.addAtom(a35);
        IAtom a36 = new Atom("C");
        mol.addAtom(a36);
        IAtom a37 = new Atom("C");
        mol.addAtom(a37);
        IAtom a38 = new Atom("C");
        mol.addAtom(a38);
        IAtom a39 = new Atom("C");
        mol.addAtom(a39);
        IAtom a40 = new Atom("C");
        mol.addAtom(a40);
        IAtom a41 = new Atom("C");
        mol.addAtom(a41);
        IAtom a42 = new Atom("C");
        mol.addAtom(a42);
        IAtom a43 = new Atom("C");
        mol.addAtom(a43);
        IAtom a44 = new Atom("C");
        mol.addAtom(a44);
        IAtom a45 = new Atom("C");
        mol.addAtom(a45);
        IAtom a46 = new Atom("C");
        mol.addAtom(a46);
        IAtom a47 = new Atom("C");
        mol.addAtom(a47);
        IAtom a48 = new Atom("C");
        mol.addAtom(a48);
        IAtom a49 = new Atom("C");
        mol.addAtom(a49);
        IAtom a50 = new Atom("C");
        mol.addAtom(a50);
        IAtom a51 = new Atom("C");
        mol.addAtom(a51);
        IAtom a52 = new Atom("C");
        mol.addAtom(a52);
        IAtom a53 = new Atom("C");
        mol.addAtom(a53);
        IAtom a54 = new Atom("C");
        mol.addAtom(a54);
        IAtom a55 = new Atom("C");
        mol.addAtom(a55);
        IAtom a56 = new Atom("C");
        mol.addAtom(a56);
        IAtom a57 = new Atom("C");
        mol.addAtom(a57);
        IAtom a58 = new Atom("C");
        mol.addAtom(a58);
        IAtom a59 = new Atom("C");
        mol.addAtom(a59);
        IAtom a60 = new Atom("C");
        mol.addAtom(a60);
        IAtom a61 = new Atom("C");
        mol.addAtom(a61);
        IAtom a62 = new Atom("C");
        mol.addAtom(a62);
        IAtom a63 = new Atom("C");
        mol.addAtom(a63);
        IAtom a64 = new Atom("C");
        mol.addAtom(a64);
        IAtom a65 = new Atom("C");
        mol.addAtom(a65);
        IAtom a66 = new Atom("C");
        mol.addAtom(a66);
        IAtom a67 = new Atom("C");
        mol.addAtom(a67);
        IAtom a68 = new Atom("C");
        mol.addAtom(a68);
        IAtom a69 = new Atom("C");
        mol.addAtom(a69);
        IAtom a70 = new Atom("C");
        mol.addAtom(a70);
        IAtom a71 = new Atom("C");
        mol.addAtom(a71);
        IAtom a72 = new Atom("C");
        mol.addAtom(a72);
        IAtom a73 = new Atom("C");
        mol.addAtom(a73);
        IAtom a74 = new Atom("C");
        mol.addAtom(a74);
        IAtom a75 = new Atom("C");
        mol.addAtom(a75);
        IAtom a76 = new Atom("C");
        mol.addAtom(a76);
        IAtom a77 = new Atom("C");
        mol.addAtom(a77);
        IAtom a78 = new Atom("C");
        mol.addAtom(a78);
        IAtom a79 = new Atom("C");
        mol.addAtom(a79);
        IAtom a80 = new Atom("C");
        mol.addAtom(a80);
        IAtom a81 = new Atom("C");
        mol.addAtom(a81);
        IBond b1 = new Bond(a3, a1);
        mol.addBond(b1);
        IBond b2 = new Bond(a4, a1);
        mol.addBond(b2);
        IBond b3 = new Bond(a5, a2);
        mol.addBond(b3);
        IBond b4 = new Bond(a6, a2);
        mol.addBond(b4);
        IBond b5 = new Bond(a7, a3);
        mol.addBond(b5);
        IBond b6 = new Bond(a8, a4);
        mol.addBond(b6);
        IBond b7 = new Bond(a9, a5);
        mol.addBond(b7);
        IBond b8 = new Bond(a10, a6);
        mol.addBond(b8);
        IBond b9 = new Bond(a15, a11);
        mol.addBond(b9);
        IBond b10 = new Bond(a16, a12);
        mol.addBond(b10);
        IBond b11 = new Bond(a17, a13);
        mol.addBond(b11);
        IBond b12 = new Bond(a18, a14);
        mol.addBond(b12);
        IBond b13 = new Bond(a23, a19);
        mol.addBond(b13);
        IBond b14 = new Bond(a24, a20);
        mol.addBond(b14);
        IBond b15 = new Bond(a25, a21);
        mol.addBond(b15);
        IBond b16 = new Bond(a26, a22);
        mol.addBond(b16);
        IBond b17 = new Bond(a31, a27);
        mol.addBond(b17);
        IBond b18 = new Bond(a32, a28);
        mol.addBond(b18);
        IBond b19 = new Bond(a33, a29);
        mol.addBond(b19);
        IBond b20 = new Bond(a34, a30);
        mol.addBond(b20);
        IBond b21 = new Bond(a39, a35);
        mol.addBond(b21);
        IBond b22 = new Bond(a40, a36);
        mol.addBond(b22);
        IBond b23 = new Bond(a41, a37);
        mol.addBond(b23);
        IBond b24 = new Bond(a42, a38);
        mol.addBond(b24);
        IBond b25 = new Bond(a47, a43);
        mol.addBond(b25);
        IBond b26 = new Bond(a48, a44);
        mol.addBond(b26);
        IBond b27 = new Bond(a49, a45);
        mol.addBond(b27);
        IBond b28 = new Bond(a50, a46);
        mol.addBond(b28);
        IBond b29 = new Bond(a55, a51);
        mol.addBond(b29);
        IBond b30 = new Bond(a56, a52);
        mol.addBond(b30);
        IBond b31 = new Bond(a57, a53);
        mol.addBond(b31);
        IBond b32 = new Bond(a58, a54);
        mol.addBond(b32);
        IBond b33 = new Bond(a63, a59);
        mol.addBond(b33);
        IBond b34 = new Bond(a64, a60);
        mol.addBond(b34);
        IBond b35 = new Bond(a65, a61);
        mol.addBond(b35);
        IBond b36 = new Bond(a66, a62);
        mol.addBond(b36);
        IBond b37 = new Bond(a67, a7);
        mol.addBond(b37);
        IBond b38 = new Bond(a67, a8);
        mol.addBond(b38);
        IBond b39 = new Bond(a67, a11);
        mol.addBond(b39);
        IBond b40 = new Bond(a67, a12);
        mol.addBond(b40);
        IBond b41 = new Bond(a68, a9);
        mol.addBond(b41);
        IBond b42 = new Bond(a68, a10);
        mol.addBond(b42);
        IBond b43 = new Bond(a68, a13);
        mol.addBond(b43);
        IBond b44 = new Bond(a68, a14);
        mol.addBond(b44);
        IBond b45 = new Bond(a69, a15);
        mol.addBond(b45);
        IBond b46 = new Bond(a69, a16);
        mol.addBond(b46);
        IBond b47 = new Bond(a69, a19);
        mol.addBond(b47);
        IBond b48 = new Bond(a69, a20);
        mol.addBond(b48);
        IBond b49 = new Bond(a70, a17);
        mol.addBond(b49);
        IBond b50 = new Bond(a70, a18);
        mol.addBond(b50);
        IBond b51 = new Bond(a70, a21);
        mol.addBond(b51);
        IBond b52 = new Bond(a70, a22);
        mol.addBond(b52);
        IBond b53 = new Bond(a71, a23);
        mol.addBond(b53);
        IBond b54 = new Bond(a71, a24);
        mol.addBond(b54);
        IBond b55 = new Bond(a71, a27);
        mol.addBond(b55);
        IBond b56 = new Bond(a71, a28);
        mol.addBond(b56);
        IBond b57 = new Bond(a72, a25);
        mol.addBond(b57);
        IBond b58 = new Bond(a72, a26);
        mol.addBond(b58);
        IBond b59 = new Bond(a72, a29);
        mol.addBond(b59);
        IBond b60 = new Bond(a72, a30);
        mol.addBond(b60);
        IBond b61 = new Bond(a73, a31);
        mol.addBond(b61);
        IBond b62 = new Bond(a73, a32);
        mol.addBond(b62);
        IBond b63 = new Bond(a73, a35);
        mol.addBond(b63);
        IBond b64 = new Bond(a73, a36);
        mol.addBond(b64);
        IBond b65 = new Bond(a74, a33);
        mol.addBond(b65);
        IBond b66 = new Bond(a74, a34);
        mol.addBond(b66);
        IBond b67 = new Bond(a74, a37);
        mol.addBond(b67);
        IBond b68 = new Bond(a74, a38);
        mol.addBond(b68);
        IBond b69 = new Bond(a75, a39);
        mol.addBond(b69);
        IBond b70 = new Bond(a75, a40);
        mol.addBond(b70);
        IBond b71 = new Bond(a75, a43);
        mol.addBond(b71);
        IBond b72 = new Bond(a75, a44);
        mol.addBond(b72);
        IBond b73 = new Bond(a76, a41);
        mol.addBond(b73);
        IBond b74 = new Bond(a76, a42);
        mol.addBond(b74);
        IBond b75 = new Bond(a76, a45);
        mol.addBond(b75);
        IBond b76 = new Bond(a76, a46);
        mol.addBond(b76);
        IBond b77 = new Bond(a77, a47);
        mol.addBond(b77);
        IBond b78 = new Bond(a77, a48);
        mol.addBond(b78);
        IBond b79 = new Bond(a77, a51);
        mol.addBond(b79);
        IBond b80 = new Bond(a77, a52);
        mol.addBond(b80);
        IBond b81 = new Bond(a78, a49);
        mol.addBond(b81);
        IBond b82 = new Bond(a78, a50);
        mol.addBond(b82);
        IBond b83 = new Bond(a78, a53);
        mol.addBond(b83);
        IBond b84 = new Bond(a78, a54);
        mol.addBond(b84);
        IBond b85 = new Bond(a79, a55);
        mol.addBond(b85);
        IBond b86 = new Bond(a79, a56);
        mol.addBond(b86);
        IBond b87 = new Bond(a79, a59);
        mol.addBond(b87);
        IBond b88 = new Bond(a79, a60);
        mol.addBond(b88);
        IBond b89 = new Bond(a80, a57);
        mol.addBond(b89);
        IBond b90 = new Bond(a80, a58);
        mol.addBond(b90);
        IBond b91 = new Bond(a80, a61);
        mol.addBond(b91);
        IBond b92 = new Bond(a80, a62);
        mol.addBond(b92);
        IBond b93 = new Bond(a81, a63);
        mol.addBond(b93);
        IBond b94 = new Bond(a81, a64);
        mol.addBond(b94);
        IBond b95 = new Bond(a81, a65);
        mol.addBond(b95);
        IBond b96 = new Bond(a81, a66);
        mol.addBond(b96);
        return mol;
    }

    /**
     * spiro[5.5]undecane
     *
     * @cdk.inchi InChI=1S/C11H20/c1-3-7-11(8-4-1)9-5-2-6-10-11/h1-10H2
     */
    private static IAtomContainer spiroundecane() {

        IAtomContainer mol = new AtomContainer();
        IAtom a1 = new Atom("C");
        mol.addAtom(a1);
        IAtom a2 = new Atom("C");
        mol.addAtom(a2);
        IAtom a3 = new Atom("C");
        mol.addAtom(a3);
        IAtom a4 = new Atom("C");
        mol.addAtom(a4);
        IAtom a5 = new Atom("C");
        mol.addAtom(a5);
        IAtom a6 = new Atom("C");
        mol.addAtom(a6);
        IAtom a7 = new Atom("C");
        mol.addAtom(a7);
        IAtom a8 = new Atom("C");
        mol.addAtom(a8);
        IAtom a9 = new Atom("C");
        mol.addAtom(a9);
        IAtom a10 = new Atom("C");
        mol.addAtom(a10);
        IAtom a11 = new Atom("C");
        mol.addAtom(a11);
        IBond b1 = new Bond(a1, a2);
        mol.addBond(b1);
        IBond b2 = new Bond(a1, a6);
        mol.addBond(b2);
        IBond b3 = new Bond(a2, a3);
        mol.addBond(b3);
        IBond b4 = new Bond(a3, a4);
        mol.addBond(b4);
        IBond b5 = new Bond(a4, a5);
        mol.addBond(b5);
        IBond b6 = new Bond(a5, a6);
        mol.addBond(b6);
        IBond b7 = new Bond(a7, a5);
        mol.addBond(b7);
        IBond b8 = new Bond(a5, a8);
        mol.addBond(b8);
        IBond b9 = new Bond(a7, a11);
        mol.addBond(b9);
        IBond b10 = new Bond(a8, a9);
        mol.addBond(b10);
        IBond b11 = new Bond(a9, a10);
        mol.addBond(b11);
        IBond b12 = new Bond(a10, a11);
        mol.addBond(b12);
        return mol;

    }
}
