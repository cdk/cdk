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

/*
 * Created on May 11, 2004
 *
 */
/**
 * @author uli
 *
 */
public class MinimalPathIterator implements Iterator {
	
	Object startVertex, targetVertex;
	Graph g;
	DirectedGraph shortestPathGraph;
	
	Stack edgeIteratorStack;
	Stack vertexStack;
	
	Object next;
	
	public MinimalPathIterator(SimpleGraph g, Object startVertex, Object targetVertex) {
		
		this.g = g;
		
		this.startVertex = startVertex;
		this.targetVertex = targetVertex;
		
		createShortestPathTree();
	}

	private void createShortestPathTree() {
		
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
