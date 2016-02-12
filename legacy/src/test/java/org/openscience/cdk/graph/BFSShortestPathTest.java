/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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

import java.util.List;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * @cdk.module test-standard
 */
public class BFSShortestPathTest extends CDKTestCase {

    public BFSShortestPathTest() {
        super();
    }

    @Test
    public void testFindPathBetween_Graph_Object_Object() {
        IAtomContainer apinene = TestMoleculeFactory.makeAlphaPinene();
        SimpleGraph graph = MoleculeGraphs.getMoleculeGraph(apinene);
        Object startVertex = graph.vertexSet().toArray()[0];
        Object endVertex = graph.vertexSet().toArray()[5];
        List list = BFSShortestPath.findPathBetween(graph, startVertex, endVertex);
        Assert.assertTrue(list.size() > 0);
    }
}
