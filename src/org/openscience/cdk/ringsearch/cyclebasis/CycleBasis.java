package org.openscience.cdk.ringsearch.cyclebasis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.UndirectedGraph;
import org._3pq.jgrapht.alg.DijkstraShortestPath;
import org._3pq.jgrapht.graph.UndirectedSubgraph;
import org.openscience.cdk.graph.BiconnectivityInspector;

/*
 * Created on Apr 18, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author uli
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CycleBasis {
		
	//private List cycles = new Vector();
	private List mulitEdgeCycles = new Vector();
	private List multiEdgeList = new Vector();

	private SimpleCycleBasis cachedCycleBasis;
	
	//private List edgeList = new Vector();
	//private List multiEdgeList = new Vector();
	private UndirectedGraph baseGraph;
	private boolean isMinimized = false;
	private List subgraphBases = new Vector();
		
	public CycleBasis (UndirectedGraph aGraph) {
		
		baseGraph = aGraph;
				
		// We construct a simple graph out of the input (multi-)graph
		// as a subgraph with no multiedges.
		// The removed edges are collected in multiEdgeList
		// Moreover, shortest cycles through these edges are constructed and
		// collected in mulitEdgeCycles
		
		UndirectedGraph simpleGraph = new UndirectedSubgraph(aGraph, null, null);
		
		// Iterate over the edges and discard all edges with the same source and target
		for (Iterator it = aGraph.edgeSet().iterator(); it.hasNext();) {
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
						mulitEdgeCycles.add(new Cycle(baseGraph, edgesOfCycle));
						
					} 
				}
					
			}
		}
		
		long time = System.currentTimeMillis(  );
		
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
	
	
		

/*	
	public void minimize() {
		if (isMinimized) 
			return;
		
		for (Iterator it = subgraphBases.iterator(); it.hasNext();) {
			SimpleCycleBasis basis = (SimpleCycleBasis) it.next();
			basis.minimize();
		}

		isMinimized = true;
	}
	
*/	
	
	public void printIncidenceMatrix() {
		SimpleCycleBasis basis = simpleBasis();
		List edgeList = basis.edgeList;
		
		/*
		for (int j=0; j<edgeList.size(); j++) {
			System.out.print(((Edge) edgeList.get(j)).getSource());
		}
		System.out.println();
		for (int j=0; j<edgeList.size(); j++) {
			System.out.print(((Edge) edgeList.get(j)).getTarget());
		}
		System.out.println();
		for (int j=0; j<edgeList.size(); j++) {
			System.out.print('-');
		}
		System.out.println();
		*/
		
        boolean[][] incidMatr = basis.getCycleEdgeIncidenceMatrix();
		for (int i=0; i<incidMatr.length; i++) {
			for (int j=0; j<incidMatr[i].length; j++) {
				System.out.print(incidMatr[i][j]?1:0);
			}
			System.out.println();
		}
	}

	public double[] weightVector() {
		SimpleCycleBasis basis = simpleBasis();
		List cycles = basis.cycles;
		
		double[] result = new double[cycles.size()];
		for (int i=0; i<cycles.size(); i++) {
			Cycle cycle = (Cycle) cycles.get(i);
			result[i] = cycle.weight();
		}
		Arrays.sort(result);
		
		return result;
	}
	
	public SimpleCycleBasis simpleBasis() {
		if (cachedCycleBasis == null) {
			List cycles = new ArrayList();
			List edgeList = new ArrayList();
			
			for (Iterator it = subgraphBases.iterator(); it.hasNext();) {
				SimpleCycleBasis subgraphBase = (SimpleCycleBasis) it.next();
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

	public Collection cycles() {
		return simpleBasis().cycles;
	}
	
	public Collection essentialCycles() {
		Collection result = new HashSet();
		//minimize();
		
		for (Iterator it = subgraphBases.iterator(); it.hasNext();) {
			SimpleCycleBasis cycleBasis = (SimpleCycleBasis) it.next();
			result.addAll(cycleBasis.essentialCycles());
		}
		
		return result;
	}

	public Map relevantCycles() {
		Map result = new HashMap();
		//minimize();
		
		for (Iterator it = subgraphBases.iterator(); it.hasNext();) {
			SimpleCycleBasis cycleBasis = (SimpleCycleBasis) it.next();
			result.putAll(cycleBasis.relevantCycles());
		}
		
		return result;
	}
	
	public List equivalenceClasses() {
		List result = new ArrayList();
		//minimize();
		
		for (Iterator it = subgraphBases.iterator(); it.hasNext();) {
			SimpleCycleBasis cycleBasis = (SimpleCycleBasis) it.next();
			result.addAll(cycleBasis.equivalenceClasses());
		}
		
		return result;
	}

}
