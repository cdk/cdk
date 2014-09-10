/* Copyright (C) 2012  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.group;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * @author maclean
 * @cdk.module test-group
 *
 */
public class PermutationGroupTest extends CDKTestCase {

    // the first 7 factorials
    private final static int[] lookup = {1, 1, 2, 6, 24, 120, 720, 5040};

    private int factorial(int n) {
        if (n < lookup.length) {
            return lookup[n];
        } else {
            return f(n);
        }
    }

    private int f(int n) {
        if (n == 1) {
            return 1;
        } else if (n < lookup.length) {
            return lookup[n];
        } else {
            return f(n - 1) * n;
        }
    }

    private PermutationGroup getCubeGroup() {
        int size = 8;

        // the group of the cube
        Permutation p1 = new Permutation(1, 3, 5, 7, 0, 2, 4, 6);
        Permutation p2 = new Permutation(1, 3, 0, 2, 5, 7, 4, 6);
        List<Permutation> generators = new ArrayList<Permutation>();
        generators.add(p1);
        generators.add(p2);
        return new PermutationGroup(size, generators);
    }

    @Test
    public void testTheFactorialFunction() {
        Assert.assertEquals(40320, factorial(8));
    }

    @Test
    public void sizeConstructor() {
        int size = 4;
        PermutationGroup group = new PermutationGroup(size);
        Assert.assertEquals(size, group.getSize());
    }

    @Test
    public void baseConstructor() {
        int size = 4;
        Permutation base = new Permutation(size);
        PermutationGroup group = new PermutationGroup(base);
        Assert.assertEquals(size, group.getSize());
    }

    @Test
    public void generatorConstructor() {
        int size = 4;
        Permutation p1 = new Permutation(1, 0, 2, 3);
        Permutation p2 = new Permutation(1, 2, 3, 0);
        List<Permutation> generators = new ArrayList<Permutation>();
        generators.add(p1);
        generators.add(p2);
        PermutationGroup group = new PermutationGroup(size, generators);
        Assert.assertEquals(size, group.getSize());
        Assert.assertEquals(factorial(size), group.order());
    }

    @Test
    public void makeSymNTest() {
        int size = 4;
        PermutationGroup sym = PermutationGroup.makeSymN(size);
        Assert.assertEquals(size, sym.getSize());
        Assert.assertEquals(factorial(size), sym.order());
    }

    @Test
    public void getSizeTest() {
        int size = 4;
        PermutationGroup group = new PermutationGroup(size);
        Assert.assertEquals(size, group.getSize());
    }

    @Test
    public void orderTest() {
        int size = 5;
        PermutationGroup sym = PermutationGroup.makeSymN(size);
        Assert.assertEquals(factorial(size), sym.order());
    }

    @Test
    public void getTest() {
        int size = 6;
        // group that could represent a hexagon (numbered clockwise from top)
        // p1 = a flip across the vertical, p2 = flip across the horizontal
        Permutation p1 = new Permutation(0, 5, 4, 3, 2, 1);
        Permutation p2 = new Permutation(3, 2, 1, 0, 5, 4);
        List<Permutation> generators = new ArrayList<Permutation>();
        generators.add(p1);
        generators.add(p2);
        PermutationGroup group = new PermutationGroup(size, generators);

        // the permutations in U0 all have 0 in the orbit of i
        // but fixing 0 cannot fix 1, so there is no such perm
        int uIndex = 0;
        int uSubIndex = 1;
        Permutation u01 = group.get(uIndex, uSubIndex);
        Assert.assertNull(u01);

        // however, 0 and 3 are in the same orbit by both flips
        uSubIndex = 3;
        Permutation u03 = group.get(uIndex, uSubIndex);
        List<Integer> orbit = u03.getOrbit(0);
        Assert.assertTrue(orbit.contains(uSubIndex));
    }

    @Test
    public void getLeftTransversalTest() {
        PermutationGroup group = getCubeGroup();
        List<Permutation> transversal = group.getLeftTransversal(1);
        Assert.assertEquals(3, transversal.size());
    }

    @Test
    public void testTransversal() {
        int size = 4;
        // Sym(n) : make the total symmetry group
        PermutationGroup group = PermutationGroup.makeSymN(size);

        // Aut(G) : make the automorphism group for a graph
        Permutation p1 = new Permutation(2, 1, 0, 3);
        Permutation p2 = new Permutation(0, 3, 2, 1);
        List<Permutation> generators = new ArrayList<Permutation>();
        generators.add(p1);
        generators.add(p2);
        PermutationGroup subgroup = new PermutationGroup(size, generators);

        // generate the traversal
        List<Permutation> transversal = group.transversal(subgroup);

        int subgroupOrder = (int) subgroup.order();
        int groupOrder = (int) group.order();
        int transversalSize = transversal.size();

        // check that |Aut(G)| / |Sym(N)| = |Transversal|
        Assert.assertEquals(factorial(size), groupOrder);
        Assert.assertEquals(groupOrder / subgroupOrder, transversalSize);
    }

    @Test
    public void applyTest() {
        final List<Permutation> all = new ArrayList<Permutation>();
        int size = 4;
        PermutationGroup group = PermutationGroup.makeSymN(size);
        group.apply(new PermutationGroup.Backtracker() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void applyTo(Permutation p) {
                all.add(p);
            }
        });
        Assert.assertEquals(factorial(size), all.size());
    }

    @Test
    public void apply_FinishEarlyTest() {
        final List<Permutation> all = new ArrayList<Permutation>();
        final int max = 5; // stop after this many seen
        int size = 4;
        PermutationGroup group = PermutationGroup.makeSymN(size);
        group.apply(new PermutationGroup.Backtracker() {

            @Override
            public boolean isFinished() {
                return all.size() >= max;
            }

            @Override
            public void applyTo(Permutation p) {
                all.add(p);
            }
        });
        Assert.assertEquals(max, all.size());
    }

    @Test
    public void allTest() {
        int size = 4;
        PermutationGroup group = PermutationGroup.makeSymN(size);
        List<Permutation> all = group.all();
        Assert.assertEquals(factorial(size), all.size());
    }

    @Test
    public void test_SuccessTest() {
        PermutationGroup group = getCubeGroup();
        Permutation p = new Permutation(6, 7, 4, 5, 2, 3, 0, 1);
        int position = group.test(p);
        // this means p is a member of G
        Assert.assertTrue(position == group.getSize());
    }

    @Test
    public void test_FailureTest() {
        PermutationGroup group = getCubeGroup();
        Permutation p = new Permutation(1, 2, 3, 4, 0, 6, 7, 5);
        int position = group.test(p);
        // this means p is not in G
        Assert.assertTrue(position < group.getSize());
    }

    @Test
    public void enterTest() {
        int size = 4;
        PermutationGroup group = new PermutationGroup(size);
        group.enter(new Permutation(1, 0, 3, 2));
        Assert.assertEquals(2, group.order());
    }

    @Test
    public void changeBaseTest() {
        int size = 4;
        PermutationGroup group = new PermutationGroup(size);
        group.enter(new Permutation(1, 0, 3, 2));
        group.changeBase(new Permutation(size));
        Assert.assertEquals(2, group.order());
    }
}
