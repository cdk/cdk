/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org._3pq.jgrapht.DirectedGraph;
import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.Graph;
import org._3pq.jgrapht.edge.DirectedEdge;
import org._3pq.jgrapht.graph.DefaultDirectedGraph;
import org._3pq.jgrapht.graph.SimpleGraph;
import org._3pq.jgrapht.traverse.ClosestFirstIterator;

/**
 * Iterates over all shortest paths between two vertices in an undirected, unweighted graph.
 * 
 * @author Ulrich Bauer <baueru@cs.tum.edu>
 * 
 *
 * @cdk.module standard
 *
 * @cdk.builddepends jgrapht-0.5.3.jar
 * @cdk.depends jgrapht-0.5.3.jar
 */
public class MinimalPathIterator implements Iterator {
	
	Object startVertex, targetVertex;
	Graph g;
	DirectedGraph shortestPathGraph;
	
	Stack edgeIteratorStack;
	Stack vertexStack;
	
	Object next;
	
	/**
	 * Creates a minimal path iterator for the specified undirected graph.
	 * @param g the specified graph
	 * @param startVertex the start vertex for the paths
	 * @param targetVertex the target vertex for the paths
	 */
	public MinimalPathIterator(SimpleGraph g, Object startVertex, Object targetVertex) {
		
		this.g = g;
		
		this.startVertex = startVertex;
		this.targetVertex = targetVertex;
		
		createShortestPathGraph();
	}

	private void createShortestPathGraph() {
		
		shortestPathGraph = new DefaultDirectedGraph();
		shortestPathGraph.addAllVertices(g.vertexSet());
		Set nonTreeEdges = new HashSet(g.edgeSet());
		
		Map distanceMap = new HashMap();
		distanceMap.put(targetVertex, new Integer(0));
		
		for (ClosestFirstIterator iter = new ClosestFirstIterator(g, targetVertex); iter.hasNext(); ) {
			Object vertex = iter.next();
			Edge treeEdge = iter.getSpanningTreeEdge(vertex);
			if (treeEdge != null) {
				Object parent = treeEdge.oppositeVertex(vertex);
				shortestPathGraph.addEdge(vertex, parent);
				distanceMap.put(vertex, new Integer(((Integer)distanceMap.get(parent)).intValue() + 1));
				
				nonTreeEdges.remove(treeEdge);
			}
		}
		
		//System.out.println(shortestPathGraph);
		//System.out.println(distanceMap);
		//System.out.println(nonTreeEdges);
		
		for (Iterator iter = nonTreeEdges.iterator(); iter.hasNext(); ) {
			Edge edge = (Edge)iter.next();
			Object u = edge.getSource();
			Object v = edge.getTarget();
			int uDist = ((Integer)distanceMap.get(u)).intValue();
			int vDist = ((Integer)distanceMap.get(v)).intValue();
			if (uDist == vDist + 1) {
				shortestPathGraph.addEdge(new DirectedEdge(u, v));
			} else if (vDist == uDist + 1) {
				shortestPathGraph.addEdge(new DirectedEdge(v, u));
			}
		}

		//System.out.println(shortestPathGraph);
		
		
		
		
		Iterator edgeIterator = shortestPathGraph.outgoingEdgesOf(startVertex).iterator();
		
		edgeIteratorStack = new Stack();
		edgeIteratorStack.push(edgeIterator);

		vertexStack = new Stack();
		vertexStack.push(startVertex);
		
	}
	
	public boolean hasNext() {
		
		if (next == null) {
			
			while (next == null && !edgeIteratorStack.isEmpty()) {
				Iterator edgeIterator = (Iterator) edgeIteratorStack.peek();
				Object currentVertex = vertexStack.peek();
				
				//System.out.println(currentVertex);
				
				if (edgeIterator.hasNext()) {
					Edge edge = (Edge)edgeIterator.next();
					currentVertex = edge.oppositeVertex(currentVertex);
					edgeIterator = shortestPathGraph.outgoingEdgesOf(currentVertex).iterator();
					
					edgeIteratorStack.push(edgeIterator);
					vertexStack.push(currentVertex);					
					
				} else {
					if (currentVertex == targetVertex) {
						next = edgeList(g, vertexStack);
					}
					edgeIteratorStack.pop();
					vertexStack.pop();
				}
				
			}
			
			
		} 

		return (next != null);
		
	}
		
	public Object next() {
		if (hasNext()) {
			Object result = next;
			next = null;
			return result;
		}
		else {
			return null;
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private List edgeList(Graph g, List vertexList) {
		List edgeList = new ArrayList(vertexList.size()-1);
		Iterator vertices = vertexList.iterator();
		Object currentVertex = vertices.next();
		while (vertices.hasNext()) {
			Object nextVertex = vertices.next();
			edgeList.add(g.getAllEdges(currentVertex, nextVertex).get(0));
			currentVertex = nextVertex;
		}
		
		return edgeList;
		
	}
}
