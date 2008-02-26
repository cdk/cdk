/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2004-2007  Ulrich Bauer <baueru@cs.tum.edu>
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
package org.openscience.cdk.ringsearch.cyclebasis;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.UndirectedGraph;
import org._3pq.jgrapht.graph.UndirectedSubgraph;
import org.openscience.cdk.annotations.TestClass;

import java.util.*;

/**
 * A cycle in a graph G is a subgraph in which every vertex has even degree.
 *
 * @author Ulrich Bauer <baueru@cs.tum.edu>
 * 
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword smallest-set-of-rings
 * @cdk.keyword ring search
 * 
 * @cdk.builddepends jgrapht-0.5.3.jar
 * @cdk.depends jgrapht-0.5.3.jar
 */
@TestClass("org.openscience.cdk.ringsearch.cyclebasis.SimpleCycleTest")
public class SimpleCycle extends UndirectedSubgraph {

    private static final long serialVersionUID = -3330742084804445688L;

    /**
	 * Constructs a cycle in a graph consisting of the specified edges.
	 *
	 * @param   g the graph in which the cycle is contained
	 * @param   edges the edges of the cycle
	 */
	public SimpleCycle (UndirectedGraph g, Collection edges) {
		this(g, new HashSet(edges));
	}
	
	/**
	 * Constructs a cycle in a graph consisting of the specified edges.
	 *
	 * @param   g the graph in which the cycle is contained
	 * @param   edges the edges of the cycle
	 */
	public SimpleCycle (UndirectedGraph g, Set edges) {
		super(g, inducedVertices(edges), edges);
	}
	
	static private Set inducedVertices(Set edges) {
		Set inducedVertices = new HashSet();
		for (Iterator i = edges.iterator(); i.hasNext();) {
			Edge edge = (Edge) i.next();
			inducedVertices.add(edge.getSource());
			inducedVertices.add(edge.getTarget());
		}
		return inducedVertices;
	}
	
	/**
	 * Returns the sum of the weights of all edges in this cycle.
	 *
	 * @return the sum of the weights of all edges in this cycle
	 */
	public double weight() {
		double result = 0;
		Iterator edgeIterator = edgeSet().iterator();
		while (edgeIterator.hasNext()) {
			result += ((Edge)edgeIterator.next()).getWeight();
		}
		return result;
	}
	
	/**
	 * Returns a list of the vertices contained in this cycle.
	 * The vertices are in the order of a traversal of the cycle.
	 *
	 * @return a list of the vertices contained in this cycle
	 */
	public List vertexList() {
		List vertices = new ArrayList(edgeSet().size());
		
		Object startVertex = vertexSet().iterator().next();
		
		Object vertex = startVertex;
		Object previousVertex = null;
		Object nextVertex = null;
		
		while (nextVertex != startVertex) {
			vertices.add(vertex);
			
			Edge edge = (Edge) edgesOf(vertex).get(0);
			nextVertex = edge.oppositeVertex(vertex);
			
			if (nextVertex==previousVertex) {
				edge = (Edge) edgesOf(vertex).get(1);
				nextVertex = edge.oppositeVertex(vertex);
			}
			
			previousVertex = vertex;
			vertex = nextVertex;
			
		}
			
		
		return vertices;
	}
	
	public boolean equals(Object obj) {
		return (obj instanceof SimpleCycle && edgeSet().equals(((SimpleCycle) obj).edgeSet()));
	}

	public String toString() {
		return vertexList().toString();
	}
	
	public int hashCode() {
		return edgeSet().hashCode();
	}

}
