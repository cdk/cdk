package org.openscience.cdk.ringsearch.cyclebasis;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.Graph;
import org._3pq.jgrapht.UndirectedGraph;
import org._3pq.jgrapht.graph.UndirectedSubgraph;

/*
 * Created on Apr 18, 2004
 *
 */

/**
 * @author uli
 *
 */
public class Cycle extends UndirectedSubgraph {

	public Cycle (UndirectedGraph g, Collection edges) {
		this(g, new HashSet(edges));
	}
	
	public Cycle (UndirectedGraph g, Set edges) {
		super(g, inducedVertices(g, edges), edges);
	}
	
	static private Set inducedVertices(Graph g, Set edges) {
		Set inducedVertices = new HashSet();
		for (Iterator i = edges.iterator(); i.hasNext();) {
			Edge edge = (Edge) i.next();
			inducedVertices.add(edge.getSource());
			inducedVertices.add(edge.getTarget());
		}
		return inducedVertices;
	}
	
	public boolean hasEdge(Edge edge) {
		return containsEdge(edge);
	}

	public double weight() {
		double result = 0;
		Iterator edgeIterator = edgeSet().iterator();
		while (edgeIterator.hasNext()) {
			result += ((Edge)edgeIterator.next()).getWeight();
		}
		return result;
	}
	
	public Collection edges() {
		return edgeSet();
	}
	
	public List vertices() {
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
		return (obj instanceof Cycle && edgeSet().equals(((Cycle) obj).edgeSet()));
	}

	public String toString() {
		return vertices().toString();
	}
	
    public int hashCode() {
        return edgeSet().hashCode();
}

	
}
