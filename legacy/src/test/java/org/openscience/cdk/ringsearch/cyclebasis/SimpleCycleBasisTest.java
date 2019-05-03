/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.ringsearch.cyclebasis;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * This class tests the SimpleCycleBasis class.
 *
 * @cdk.module test-standard
 *
 * @author Ulrich Bauer &lt;baueru@cs.tum.edu&gt;
 */
public class SimpleCycleBasisTest extends CDKTestCase {

    private static SimpleGraph      g;
    private static SimpleCycleBasis basis;

    @Before
    public void setUp() {
        g = new SimpleGraph();
        g.addVertex("a");
        g.addVertex("b");
        g.addVertex("c");
        g.addVertex("d");
        g.addVertex("e");
        g.addVertex("f");
        g.addVertex("g");
        g.addVertex("h");

        g.addEdge("a", "b");
        g.addEdge("a", "c");
        g.addEdge("b", "c");
        g.addEdge("b", "d");
        g.addEdge("b", "e");
        g.addEdge("b", "g");
        g.addEdge("c", "d");
        g.addEdge("c", "e");
        g.addEdge("c", "f");
        g.addEdge("c", "h");
        g.addEdge("d", "e");
        g.addEdge("d", "g");
        g.addEdge("e", "f");
        g.addEdge("e", "h");
        g.addEdge("f", "h");

        basis = new SimpleCycleBasis(g);

    }

    @Test
    public void testSimpleCycleBasis() {
        Assert.assertTrue(basis.cycles().size() == g.edgeSet().size() - g.vertexSet().size() + 1);
    }

    @Test
    public void testSimpleCycleBasisCompleteGraph() {
        g = new SimpleGraph();
        g.addVertex("a");
        g.addVertex("b");
        g.addVertex("c");
        g.addVertex("d");
        g.addVertex("e");

        g.addEdge("a", "b");
        g.addEdge("a", "c");
        g.addEdge("a", "d");
        g.addEdge("a", "e");
        g.addEdge("b", "c");
        g.addEdge("b", "d");
        g.addEdge("b", "e");
        g.addEdge("c", "d");
        g.addEdge("c", "e");
        g.addEdge("d", "e");

        basis = new SimpleCycleBasis(g);
        Assert.assertEquals(g.edgeSet().size() - g.vertexSet().size() + 1, basis.cycles().size());
        Assert.assertArrayEquals(basis.weightVector(), new int[]{3, 3, 3, 3, 3, 3});
        Assert.assertEquals(10, basis.relevantCycles().size());
        Assert.assertEquals(0, basis.essentialCycles().size());
        Assert.assertEquals(1, basis.equivalenceClasses().size());
    }

    @Test
    public void testWeightVector() {
        Assert.assertArrayEquals(basis.weightVector(), new int[]{3, 3, 3, 3, 3, 3, 3, 3});
    }

    @Test
    public void testRelevantCycles() {
        Assert.assertEquals(10, basis.relevantCycles().size());
    }

    @Test
    public void testEssentialCycles() {
        Assert.assertEquals(2, basis.essentialCycles().size());
    }

    @Test
    public void testEquivalenceClasses() {
        Assert.assertEquals(4, basis.equivalenceClasses().size());
    }

    @Test
    public void testEquivalenceClassesEmptyIntersection() {
        SimpleGraph h = new SimpleGraph();

        h.addVertex("a");
        h.addVertex("b");
        h.addVertex("c");
        h.addVertex("d");
        h.addVertex("e");
        h.addVertex("f");
        h.addVertex("g");
        h.addVertex("h");
        h.addVertex("i");
        h.addVertex("j");
        h.addVertex("k");
        h.addVertex("l");
        h.addVertex("m");
        h.addVertex("n");
        h.addVertex("o");
        h.addVertex("p");
        h.addVertex("q");
        h.addVertex("r");
        h.addVertex("s");
        h.addVertex("t");
        h.addVertex("u");
        h.addVertex("v");
        h.addVertex("w");

        h.addEdge("a", "b");
        h.addEdge("a", "c");
        h.addEdge("a", "g");
        h.addEdge("b", "d");
        h.addEdge("b", "m");
        h.addEdge("c", "d");
        h.addEdge("c", "e");
        h.addEdge("c", "h");
        h.addEdge("d", "f");
        h.addEdge("d", "l");
        h.addEdge("e", "f");
        h.addEdge("e", "i");
        h.addEdge("e", "j");
        h.addEdge("f", "j");
        h.addEdge("f", "k");
        h.addEdge("g", "h");
        h.addEdge("g", "v");
        h.addEdge("g", "r");
        h.addEdge("h", "i");
        h.addEdge("h", "p");
        h.addEdge("h", "t");
        h.addEdge("i", "j");
        h.addEdge("i", "n");
        h.addEdge("j", "k");
        h.addEdge("j", "n");
        h.addEdge("j", "o");
        h.addEdge("k", "l");
        h.addEdge("k", "o");
        h.addEdge("l", "m");
        h.addEdge("l", "q");
        h.addEdge("l", "u");
        h.addEdge("m", "s");
        h.addEdge("m", "w");
        h.addEdge("n", "o");
        h.addEdge("n", "p");
        h.addEdge("o", "q");
        h.addEdge("p", "q");
        h.addEdge("p", "r");
        h.addEdge("q", "s");
        h.addEdge("r", "s");
        h.addEdge("t", "u");
        h.addEdge("v", "w");

        CycleBasis b = new CycleBasis(h);

        Assert.assertEquals(19, b.equivalenceClasses().size());
        Assert.assertEquals(18, b.essentialCycles().size());

    }

    /*
     * public void testInverseBinaryMatrix() { boolean[][] a = new boolean[][] {
     * {true,true,true}, {true,false,true}, {false,false,true} }; boolean[][]
     * inv = new boolean[][] { {false,true,true}, {true,true,false},
     * {false,false,true} }; boolean[][] r =
     * SimpleCycleBasis.inverseBinaryMatrix(a, 3); for (int i=0; i<3; i++) { for
     * (int j=i; j<3; j++) { Assert.assertTrue(inv[i][j] == r[i][j]); } } }
     * public void testConstructKernelVector() { boolean[][] am = new
     * boolean[][] {{true,false,true},{false,true,false}}; boolean[] u =
     * SimpleCycleBasis.constructKernelVector(3, am, 2);
     * Assert.assertTrue(Arrays.equals(
     * SimpleCycleBasis.constructKernelVector(3, am, 2), new boolean[]
     * {true,false,true} )); }
     */
}
