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

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.Graph;
import org._3pq.jgrapht.UndirectedGraph;
import org._3pq.jgrapht.alg.ConnectivityInspector;
import org._3pq.jgrapht.graph.SimpleGraph;
import org._3pq.jgrapht.graph.Subgraph;

/**
 * Finds the biconnected components of a graph.
 * 
 * Two edges belong to the same biconnected component if and only if they are 
 * identical or both belong to a simple cycle.
 * 
 * @author Ulrich Bauer <baueru@cs.tum.edu>
 * 
 *
 * @cdk.module standard
 *
 * @cdk.builddepends jgrapht-0.5.3.jar
 * @cdk.depends jgrapht-0.5.3.jar
 */
public class BiconnectivityInspector {
	List          m_biconnectedSets;
	Map           m_vertexToConnectedSet;
	private UndirectedGraph graph;
	
	/**
	 * Creates a biconnectivity inspector for the specified undirected graph.
	 * @param g the specified graph
	 */
	
	public BiconnectivityInspector(UndirectedGraph g) {
		
		graph = g;
	}
	
	private List lazyFindBiconnectedSets(  ) {
		
		if( m_biconnectedSets == null ) {
			m_biconnectedSets = new ArrayList();
			
			Iterator connectedSets = 
				new ConnectivityInspector(graph).connectedSets().iterator();
			
			while (connectedSets.hasNext()) {
				Graph subgraph = new Subgraph(graph, (Set) connectedSets.next(), null);
				
				// do DFS
				
				// Stack for the DFS
				Stack vertexStack = new Stack();
				
				Set visitedVertices = new HashSet();
				Map parent = new HashMap();
				List dfsVertices = new ArrayList();
				
				Set treeEdges = new HashSet();
				
				Object currentVertex = subgraph.vertexSet().toArray()[0];
				
				vertexStack.push(currentVertex);
				visitedVertices.add(currentVertex);			
				
				while (!vertexStack.isEmpty()) {
					
					currentVertex = vertexStack.pop();
					
					Object parentVertex = parent.get(currentVertex);
					
					if (parentVertex != null) {
						Edge edge = subgraph.getEdge(parentVertex, currentVertex);
						
						// tree edge
						treeEdges.add(edge);
					}
					
					visitedVertices.add(currentVertex);
					
					dfsVertices.add(currentVertex);
					
					Iterator edges = subgraph.edgesOf(currentVertex).iterator();
					while (edges.hasNext()) {
						// find a neighbour vertex of the current vertex 
						Edge edge = (Edge)edges.next();
						
						if (!treeEdges.contains(edge)) {
							
							Object nextVertex = edge.oppositeVertex(currentVertex);
							
							if (!visitedVertices.contains(nextVertex)) {
								
								vertexStack.push(nextVertex);
								
								parent.put(nextVertex, currentVertex);
								
							} else {
								// non-tree edge
							}
							
						}
						
					}
				}
				
				// DFS is finished. Now create the auxiliary graph h
				// Add all the tree edges as vertices in h
				SimpleGraph h = new SimpleGraph();
				
				h.addAllVertices(treeEdges);
				
				visitedVertices.clear();
				
				Set connected = new HashSet();
				
				
				for (Iterator it = dfsVertices.iterator(); it.hasNext();) {
					Object v = it.next();
					
					visitedVertices.add(v);
					
					// find all adjacent non-tree edges
					for (Iterator adjacentEdges = subgraph.edgesOf(v).iterator();
					adjacentEdges.hasNext();) {
						Edge l = (Edge) adjacentEdges.next();
						if (!treeEdges.contains(l)) {
							h.addVertex(l);
							Object u = l.oppositeVertex(v);
							
							// we need to check if (u,v) is a back-edge
							if (!visitedVertices.contains(u)) {
								
								
								while (u != v) {
									Object pu = parent.get(u);
									Edge f = subgraph.getEdge(u, pu);
									
									h.addEdge(f, l);
									
									if (!connected.contains(f)) {
										connected.add(f);
										u = pu;
									} else {
										u = v;
									}
								}
							}
						}
					}
					
					
				}
				
				
				ConnectivityInspector connectivityInspector = 
					new ConnectivityInspector(h);
				
				m_biconnectedSets.addAll(connectivityInspector.connectedSets());
				
			}
		}
		
		return m_biconnectedSets;
	}
	
	/**
	 * Returns a list of <code>Set</code>s, where each set contains all edge that are
	 * in the same biconnected component. All graph edges occur in exactly one set. 
	 * @return a list of <code>Set</code>s, where each set contains all edge that are
	 * in the same biconnected component
	 */
	public List biconnectedSets(  ) {
		return lazyFindBiconnectedSets(  );
	}
	
	/*
	public List hopcroftTarjanKnuthFindBiconnectedSets() {
		Map rank;
		Map parent;
		Map untagged;
		Map link;
		Stack activeStack;
		Map min;
		
		int nn;
		
		
		
		
		
		return m_biconnectedSets;
	}
	*/
}
