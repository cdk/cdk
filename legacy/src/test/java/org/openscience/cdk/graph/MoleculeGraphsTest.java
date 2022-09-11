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

import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * @cdk.module test-standard
 */
class MoleculeGraphsTest extends CDKTestCase {

    MoleculeGraphsTest() {
        super();
    }

    /**
     * Tests that the jgrapht graph has as many vertices as atoms,
     * and as many edges as bonds in alpha-pinene.
     */
    @Test
    void testGetMoleculeGraph_IAtomContainer() {
        IAtomContainer apinene = TestMoleculeFactory.makeAlphaPinene();
        SimpleGraph graph = MoleculeGraphs.getMoleculeGraph(apinene);
        Assertions.assertEquals(apinene.getAtomCount(), graph.vertexSet().size());
        Assertions.assertEquals(apinene.getBondCount(), graph.edgeSet().size());
    }

}
