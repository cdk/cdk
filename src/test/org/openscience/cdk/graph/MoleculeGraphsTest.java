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
package org.openscience.cdk.graph;


import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.graph.MoleculeGraphs;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.NewCDKTestCase;

/**
 * @cdk.module test-standard
 */
public class MoleculeGraphsTest extends NewCDKTestCase {
    
    public MoleculeGraphsTest() {
        super();
    }

	/**
	 * Tests that the jgrapht graph has as many vertices as atoms,
	 * and as many edges as bonds in alpha-pinene.
	 */
    @Test
    public void testGetMoleculeGraph_IAtomContainer() {
		IMolecule apinene = MoleculeFactory.makeAlphaPinene();
		SimpleGraph graph = MoleculeGraphs.getMoleculeGraph(apinene);
		Assert.assertEquals(apinene.getAtomCount(), graph.vertexSet().size());
		Assert.assertEquals(apinene.getBondCount(), graph.edgeSet().size());
	}
	
}


