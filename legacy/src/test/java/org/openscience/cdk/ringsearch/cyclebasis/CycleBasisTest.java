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

import org._3pq.jgrapht.alg.ConnectivityInspector;
import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * This class tests the CycleBasis class.
 *
 * @cdk.module test-standard
 *
 * @author Ulrich Bauer &lt;baueru@cs.tum.edu&gt;
 */

class CycleBasisTest extends CDKTestCase {

    private CycleBasis  basis;
    private SimpleGraph g;

    @BeforeEach
    void setUp() {
        g = new SimpleGraph();

        g.addVertex("a");
        g.addVertex("b");
        g.addVertex("c");
        g.addVertex("d");
        g.addVertex("e");
        g.addVertex("f");
        g.addVertex("g");
        g.addVertex("h");
        g.addVertex("i");
        g.addVertex("j");
        g.addVertex("k");

        g.addEdge("a", "b");
        g.addEdge("a", "c");
        g.addEdge("b", "c");
        g.addEdge("b", "d");
        g.addEdge("c", "d");

        g.addEdge("d", "e");

        g.addEdge("d", "g");

        g.addEdge("e", "f");
        g.addEdge("e", "h");
        g.addEdge("f", "h");

        g.addEdge("i", "j");
        g.addEdge("i", "k");
        g.addEdge("j", "k");

        basis = new CycleBasis(g);
    }

    @Test
    void testCycleBasis_UndirectedGraph() {
        Assertions.assertNotNull(basis);
    }

    @Test
    void testCycles() {
        int trueCycleCount = g.edgeSet().size() - g.vertexSet().size()
                + new ConnectivityInspector(g).connectedSets().size();
        Assertions.assertEquals(trueCycleCount, basis.cycles().size());
    }

    @Test
    void testWeightVector() {
        Assertions.assertArrayEquals(basis.weightVector(), new int[]{3, 3, 3, 3});
    }

    @Test
    void testEssentialCycles() {
        Assertions.assertEquals(4, basis.essentialCycles().size());
    }

    @Test
    void testRelevantCycles() {
        Assertions.assertEquals(4, basis.relevantCycles().size());
    }

    @Test
    void testEquivalenceClasses() {
        Assertions.assertEquals(4, basis.equivalenceClasses().size());
    }

}
