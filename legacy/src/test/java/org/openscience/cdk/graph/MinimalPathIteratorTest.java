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
package org.openscience.cdk.graph;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

import java.util.List;

/**
 * This class tests the MinimalPathIteratorTest class.
 *
 * @cdk.module test-standard
 *
 * @author Ulrich Bauer &lt;baueru@cs.tum.edu&gt;
 */
public class MinimalPathIteratorTest extends CDKTestCase {

    public SimpleGraph g;

    @Before
    public void createGraph() {
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
        g.addVertex("l");

        g.addVertex("m");
        g.addVertex("n");

        g.addEdge("a", "b");
        g.addEdge("b", "c");
        g.addEdge("c", "d");

        g.addEdge("a", "e");
        g.addEdge("b", "f");
        g.addEdge("c", "g");
        g.addEdge("d", "h");

        g.addEdge("e", "f");
        g.addEdge("f", "g");
        g.addEdge("g", "h");

        g.addEdge("e", "i");
        g.addEdge("f", "j");
        g.addEdge("g", "k");
        g.addEdge("h", "l");

        g.addEdge("i", "j");
        g.addEdge("j", "k");
        g.addEdge("k", "l");

        g.addEdge("l", "m");
        g.addEdge("l", "n");
        g.addEdge("m", "n");
    }

    @Test
    public void testMinimalPathIterator() {
        int count = 0;
        for (MinimalPathIterator i = new MinimalPathIterator(g, "a", "l"); i.hasNext();) {
            Assert.assertTrue(((List) i.next()).size() == 5);
            count++;
        }
        Assert.assertEquals(10, count);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        for (MinimalPathIterator i = new MinimalPathIterator(g, "a", "l"); i.hasNext();) {
            Assert.assertTrue(((List) i.next()).size() == 5);
            i.remove();
        }
    }

}
