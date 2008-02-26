/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Utility class to create a molecule graph for use with jgrapht.
 * 
 * @author Ulrich Bauer <baueru@cs.tum.edu>
 * 
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @cdk.builddepends jgrapht-0.5.3.jar
 * @cdk.depends jgrapht-0.5.3.jar
 */
@TestClass("org.openscience.cdk.graph.MoleculeGraphsTest")
public class MoleculeGraphs {
	// make class non-instantiable
	private MoleculeGraphs() {}
	
	/**
	 * Creates a molecule graph for use with jgrapht.
	 * Bond orders are not respected.
	 * 
	 * @param molecule the specified molecule
	 * @return a graph representing the molecule
	 */
    @TestMethod("testGetMoleculeGraph_IAtomContainer")
    static public SimpleGraph getMoleculeGraph(IAtomContainer molecule) {
		SimpleGraph graph = new SimpleGraph();
		for (int i=0; i<molecule.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = molecule.getAtom(i);
			graph.addVertex(atom);
		}
		
		for (int i=0; i<molecule.getBondCount(); i++	) {
			org.openscience.cdk.interfaces.IBond bond = molecule.getBond(i);
			
			/*
			int order = (int) bond.getOrder();
			for (int j=0; j<order; j++) {
				graph.addEdge(bond.getAtoms()[0], bond.getAtoms()[1]);
			}
			*/
			graph.addEdge(bond.getAtom(0), bond.getAtom(1));
		}
		return graph;
	}
	
	/*
	static public String asString(Graph molGraph) {
		StringBuffer buf = new StringBuffer();
		buf.append("[");

	        Iterator i = molGraph.vertexSet().iterator();
	        boolean hasNext = i.hasNext();
	        while (hasNext) {
	            Atom o = (Atom) i.next();
	            buf.append(o.getSymbol());
	            hasNext = i.hasNext();
	            if (hasNext)
	                buf.append(", ");
	        }

		buf.append("]");
		
		return "(" + buf.toString() + ", " + molGraph.edgeSet().toString(  ) + ")";
	}
	*/
}
