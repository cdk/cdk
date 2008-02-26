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

package org.openscience.cdk.ringsearch.cyclebasis;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.UndirectedGraph;
import org._3pq.jgrapht.alg.DijkstraShortestPath;
import org._3pq.jgrapht.graph.UndirectedSubgraph;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.graph.BiconnectivityInspector;

import java.util.*;

/**
 * A minimum basis of all cycles in a graph.
 * All cycles in a graph G can be constructed from the basis cycles by binary
 * addition of their invidence vectors.
 * 
 * A minimum cycle basis is a Matroid.
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

@TestClass("org.openscience.cdk.ringsearch.cyclebasis.CycleBasisTest")
public class CycleBasis {
		
	//private List cycles = new Vector();
	private List mulitEdgeCycles = new Vector();
	private List multiEdgeList = new Vector();

	private SimpleCycleBasis cachedCycleBasis;
	
	//private List edgeList = new Vector();
	//private List multiEdgeList = new Vector();
	private UndirectedGraph baseGraph;
	private List subgraphBases = new Vector();
		
	/**
	 * Constructs a minimum cycle basis of a graph.
	 *
	 * @param   g the graph for the cycle basis
	 */
	public CycleBasis (UndirectedGraph g) {
		
		baseGraph = g;
				
		// We construct a simple graph out of the input (multi-)graph
		// as a subgraph with no multiedges.
		// The removed edges are collected in multiEdgeList
		// Moreover, shortest cycles through these edges are constructed and
		// collected in mulitEdgeCycles
		
		UndirectedGraph simpleGraph = new UndirectedSubgraph(g, null, null);
		
		// Iterate over the edges and discard all edges with the same source and target
		for (Iterator it = g.edgeSet().iterator(); it.hasNext();) {
			Edge edge = (Edge) it.next();
			Object u = edge.getSource();
			Object v = edge.getTarget();
			List edges = simpleGraph.getAllEdges(u, v);
			if (edges.size() > 1) {
				// Multiple edges between u and v.
				// Keep the edge with the least weight
				
				
				Edge minEdge = edge;
				for (Iterator jt = edges.iterator(); jt.hasNext();) {
					Edge nextEdge = (Edge) jt.next();
					minEdge = nextEdge.getWeight() < minEdge.getWeight()
							? nextEdge
							: minEdge;
				}
				
				//  ...and remove the others.
				for (Iterator jt = edges.iterator(); jt.hasNext();) {
					Edge nextEdge = (Edge) jt.next();
					if (nextEdge != minEdge) {
						// Remove edge from the graph
						simpleGraph.removeEdge(nextEdge);
						
						// Create a new cycle through this edge by finding 
						// a shortest path between the vertices of the edge
						Set edgesOfCycle = new HashSet();
						edgesOfCycle.add(nextEdge);
						edgesOfCycle.addAll(DijkstraShortestPath.findPathBetween(simpleGraph, u, v));
						
						multiEdgeList.add(nextEdge);
						mulitEdgeCycles.add(new SimpleCycle(baseGraph, edgesOfCycle));
						
					} 
				}
					
			}
		}
		
		List biconnectedComponents = new BiconnectivityInspector(simpleGraph).biconnectedSets();
		
		for (Iterator it = biconnectedComponents.iterator(); it.hasNext();) {
			Set edges = (Set) it.next();
			
			if (edges.size() > 1) {
				Set vertices = new HashSet();
				for (Iterator edgeIt = edges.iterator(); edgeIt.hasNext();) {
					Edge edge = (Edge) edgeIt.next();
					vertices.add(edge.getSource());
					vertices.add(edge.getTarget());
				}
				UndirectedGraph subgraph = new UndirectedSubgraph(simpleGraph, vertices, edges);
				
				SimpleCycleBasis cycleBasis = new SimpleCycleBasis(subgraph);
				
				subgraphBases.add(cycleBasis);
			} else {
				Edge edge = (Edge) edges.iterator().next();
				multiEdgeList.add(edge);
			}
		}
	}
	

    @TestMethod("testWeightVector")
    public int[] weightVector() {
		SimpleCycleBasis basis = simpleBasis();
		List cycles = basis.cycles();
		
		int[] result = new int[cycles.size()];
		for (int i=0; i<cycles.size(); i++) {
			SimpleCycle cycle = (SimpleCycle) cycles.get(i);
			result[i] = (int) cycle.weight();
		}
		Arrays.sort(result);
		
		return result;
	}
	
	private SimpleCycleBasis simpleBasis() {
		if (cachedCycleBasis == null) {
			List cycles = new ArrayList();
			List edgeList = new ArrayList();

            for (Object subgraphBase1 : subgraphBases) {
                SimpleCycleBasis subgraphBase = (SimpleCycleBasis) subgraphBase1;
                cycles.addAll(subgraphBase.cycles());
                edgeList.addAll(subgraphBase.edges());
            }
			
			cycles.addAll(mulitEdgeCycles);
			edgeList.addAll(multiEdgeList);

			//edgeList.addAll(baseGraph.edgeSet());
			
			
			cachedCycleBasis = new SimpleCycleBasis(cycles, edgeList, baseGraph);
		}
		
		return cachedCycleBasis;
		
	}

	/**
	 * Returns the cycles that form the cycle basis.
	 *
	 * @return a <Code>Collection</code> of the basis cycles
	 */

    @TestMethod("testCycles")
    public Collection cycles() {
		return simpleBasis().cycles();
	}
	
	/**
	 * Returns the essential cycles of this cycle basis.
	 * A essential cycle is contained in every minimum cycle basis of a graph.
	 *
	 * @return a <Code>Collection</code> of the essential cycles
	 */

    @TestMethod("testEssentialCycles")
    public Collection essentialCycles() {
		Collection result = new HashSet();
		//minimize();

        for (Object subgraphBase : subgraphBases) {
            SimpleCycleBasis cycleBasis = (SimpleCycleBasis) subgraphBase;
            result.addAll(cycleBasis.essentialCycles());
        }
		
		return result;
	}

	/**
	 * Returns the essential cycles of this cycle basis.
	 * A relevant cycle is contained in some minimum cycle basis of a graph.
	 *
	 * @return a <Code>Map</code> mapping each relevant cycles to the corresponding
	 * basis cycle in this basis
	 */

    @TestMethod("testRelevantCycles")
    public Map relevantCycles() {
		Map result = new HashMap();
		//minimize();

        for (Object subgraphBase : subgraphBases) {
            SimpleCycleBasis cycleBasis = (SimpleCycleBasis) subgraphBase;
            result.putAll(cycleBasis.relevantCycles());
        }
		
		return result;
	}
	
	/**
	 * Returns the connected components of this cycle basis, in regard to matroid theory.
	 * Two cycles belong to the same commected component if there is a circuit (a minimal 
	 * dependent set) containing both cycles.
	 *
	 * @return a <Code>List</code> of <Code>Set</code>s consisting of the cycles in a
	 * equivalence class.
	 */

    @TestMethod("testEquivalenceClasses")
    public List equivalenceClasses() {
		List result = new ArrayList();
		//minimize();

        for (Object subgraphBase : subgraphBases) {
            SimpleCycleBasis cycleBasis = (SimpleCycleBasis) subgraphBase;
            result.addAll(cycleBasis.equivalenceClasses());
        }
		
		return result;
	}

}
