/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.graph;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.graph.BFSShortestPath;
import org.openscience.cdk.graph.MoleculeGraphs;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.NewCDKTestCase;

import java.util.List;

/**
 * @cdk.module test-standard
 */
public class BFSShortestPathTest extends NewCDKTestCase {
    
    public BFSShortestPathTest() {
        super();
    }
    
	@Test
	public void testFindPathBetween_Graph_Object_Object() {
		IMolecule apinene = MoleculeFactory.makeAlphaPinene();
		SimpleGraph graph = MoleculeGraphs.getMoleculeGraph(apinene);
		Object startVertex = graph.vertexSet().toArray()[0];
		Object endVertex = graph.vertexSet().toArray()[5];
		List list = BFSShortestPath.findPathBetween(graph, startVertex, endVertex);
		Assert.assertTrue(list.size() > 0);
	}
}


