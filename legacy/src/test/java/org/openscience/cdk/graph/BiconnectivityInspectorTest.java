/* Copyright (C) 2004-2007  Ulrich Bauer <baueru@cs.tum.edu>
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
package org.openscience.cdk.graph;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.graph.BiconnectivityInspector;
import org.openscience.cdk.CDKTestCase;

import java.util.List;

/**
 * This class tests the BiconnectivityInspector class.
 *
 * @cdk.module test-standard
 *
 * @author Ulrich Bauer &lt;baueru@cs.tum.edu&gt;
 */
public class BiconnectivityInspectorTest extends CDKTestCase {

    @Test
    public void testBiconnectedSets() {
        SimpleGraph g = new SimpleGraph();

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

        BiconnectivityInspector bci = new BiconnectivityInspector(g);
        List connectedSets = bci.biconnectedSets();

        Assert.assertTrue(connectedSets.size() == 5);
    }

}
