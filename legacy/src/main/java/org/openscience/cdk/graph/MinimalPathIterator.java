/* Copyright (C) 2004-2007  Ulrich Bauer <ulrich.bauer@alumni.tum.de>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;


import org._3pq.jgrapht.DirectedGraph;
import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.Graph;
import org._3pq.jgrapht.graph.DefaultDirectedGraph;
import org._3pq.jgrapht.graph.SimpleGraph;
import org._3pq.jgrapht.traverse.BreadthFirstIterator;

/**
 * Iterates over all shortest paths between two vertices in an undirected, unweighted graph.
 *
 * @author Ulrich Bauer &lt;ulrich.bauer@alumni.tum.de&gt;
 *
 *
 * @cdk.module standard
 * @cdk.githash
 * @deprecated use {@link ShortestPaths}
 */
@Deprecated
public class MinimalPathIterator implements Iterator {

    private Object        sourceVertex, targetVertex;
    private Graph         g;
    private DirectedGraph shortestPathGraph;

    private Stack         edgeIteratorStack;
    private Stack         vertexStack;

    private Object        next;

    /**
     * Creates a minimal path iterator for the specified undirected graph.
     * @param g the specified graph
     * @param sourceVertex the start vertex for the paths
     * @param targetVertex the target vertex for the paths
     */
    public MinimalPathIterator(SimpleGraph g, Object sourceVertex, Object targetVertex) {

        this.g = g;

        this.sourceVertex = sourceVertex;
        this.targetVertex = targetVertex;

        createShortestPathGraph();
    }

    private void createShortestPathGraph() {

        /*
         * shortestPathGraph = new DefaultDirectedGraph();
         * //shortestPathGraph.addAllVertices(g.vertexSet()); LinkedList queue =
         * new LinkedList(); //encounter target vertex
         * queue.addLast(targetVertex);
         * shortestPathGraph.addVertex(targetVertex); int distance = 0; Object
         * firstVertexOfNextLevel = targetVertex; Collection verticesOfNextLevel
         * = new ArrayList(); while (!queue.isEmpty()) { //provide next vertex
         * Object vertex = queue.removeFirst(); if (vertex ==
         * firstVertexOfNextLevel) { distance++; firstVertexOfNextLevel = null;
         * verticesOfNextLevel.clear(); } //add unseen children of next vertex
         * List edges = g.edgesOf(vertex); for(Iterator i = edges.iterator();
         * i.hasNext();) { Edge e = (Edge) i.next( ); Object opposite =
         * e.oppositeVertex(vertex); if
         * (!shortestPathGraph.containsVertex(opposite)) { //encounter vertex
         * queue.addLast(opposite); shortestPathGraph.addVertex(opposite);
         * verticesOfNextLevel.add(opposite); if (firstVertexOfNextLevel ==
         * null) { firstVertexOfNextLevel = opposite; } } if
         * (verticesOfNextLevel.contains(opposite)) {
         * shortestPathGraph.addEdge(opposite, vertex); } } }
         */

        shortestPathGraph = new DefaultDirectedGraph();
        shortestPathGraph.addVertex(targetVertex);

        // This map gives the distance of a vertex to the target vertex
        Map distanceMap = new HashMap();

        for (MyBreadthFirstIterator iter = new MyBreadthFirstIterator(g, targetVertex); iter.hasNext();) {
            Object vertex = iter.next();
            shortestPathGraph.addVertex(vertex);

            int distance = iter.level;
            distanceMap.put(vertex, Integer.valueOf(distance));

            for (Iterator edges = g.edgesOf(vertex).iterator(); edges.hasNext();) {
                Edge edge = (Edge) edges.next();
                Object opposite = edge.oppositeVertex(vertex);
                if (distanceMap.get(opposite) != null) {
                    if (((Integer) distanceMap.get(opposite)).intValue() + 1 == distance) {
                        shortestPathGraph.addVertex(opposite);
                        shortestPathGraph.addEdge(vertex, opposite);
                    }
                }
            }

            if (vertex == sourceVertex) {
                break;
            }
        }

        Iterator edgeIterator = shortestPathGraph.outgoingEdgesOf(sourceVertex).iterator();

        edgeIteratorStack = new Stack();
        edgeIteratorStack.push(edgeIterator);

        vertexStack = new Stack();
        vertexStack.push(sourceVertex);

    }

    //	private void createShortestPathWeightedGraph() {
    //		shortestPathGraph = new DefaultDirectedGraph();
    //		//shortestPathGraph.addAllVertices(g.vertexSet());
    //		shortestPathGraph.addVertex(targetVertex);
    //
    //		// This map gives the distance of a vertex to the target vertex
    //		Map distanceMap = new HashMap();
    //		distanceMap.put(targetVertex, new Integer(0));
    //
    //		for (ClosestFirstIterator iter = new ClosestFirstIterator(g, targetVertex); iter.hasNext(); ) {
    //			Object vertex = iter.next();
    //			shortestPathGraph.addVertex(vertex);
    //
    //			Edge treeEdge = iter.getSpanningTreeEdge(vertex);
    //
    //			// in the first iteration, vertex is the target vertex; therefore no tree edge exists
    //			if (treeEdge != null) {
    //				Object parent = treeEdge.oppositeVertex(vertex);
    //				int distance = ((Integer)distanceMap.get(parent)).intValue() + 1;
    //				distanceMap.put(vertex, new Integer(distance));
    //
    //				for (Iterator edges = g.edgesOf(vertex).iterator(); edges.hasNext();) {
    //					Edge edge = (Edge) edges.next();
    //					Object opposite = edge.oppositeVertex(vertex);
    //					if (distanceMap.get(opposite) != null) {
    //						if (((Integer) distanceMap.get(opposite)).intValue() + 1 == distance) {
    //							shortestPathGraph.addVertex(opposite);
    //							shortestPathGraph.addEdge(vertex, opposite);
    //						}
    //					}
    //				}
    //			}
    //			if (vertex == sourceVertex) {
    //				break;
    //			}
    //		}
    //
    //		Iterator edgeIterator = shortestPathGraph.outgoingEdgesOf(sourceVertex).iterator();
    //
    //		edgeIteratorStack = new Stack();
    //		edgeIteratorStack.push(edgeIterator);
    //
    //		vertexStack = new Stack();
    //		vertexStack.push(sourceVertex);
    //
    //	}

    @Override
    public boolean hasNext() {

        if (next == null) {

            while (next == null && !edgeIteratorStack.isEmpty()) {
                Iterator edgeIterator = (Iterator) edgeIteratorStack.peek();
                Object currentVertex = vertexStack.peek();

                //logger.debug(currentVertex);

                if (edgeIterator.hasNext()) {
                    Edge edge = (Edge) edgeIterator.next();
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

    @Override
    public Object next() {
        if (hasNext()) {
            Object result = next;
            next = null;
            return result;
        } else {
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private List edgeList(Graph g, List vertexList) {
        List edgeList = new ArrayList(vertexList.size() - 1);
        Iterator vertices = vertexList.iterator();
        Object currentVertex = vertices.next();
        while (vertices.hasNext()) {
            Object nextVertex = vertices.next();
            edgeList.add(g.getAllEdges(currentVertex, nextVertex).get(0));
            currentVertex = nextVertex;
        }

        return edgeList;

    }

    private static class MyBreadthFirstIterator extends BreadthFirstIterator {

        public MyBreadthFirstIterator(Graph g, Object startVertex) {
            super(g, startVertex);
        }

        int            level = -1;
        private Object firstVertexOfNextLevel;

        @Override
        protected void encounterVertex(Object vertex, Edge edge) {
            super.encounterVertex(vertex, edge);
            if (firstVertexOfNextLevel == null) {
                firstVertexOfNextLevel = vertex;
            }
        }

        @Override
        protected Object provideNextVertex() {
            Object nextVertex = super.provideNextVertex();
            if (firstVertexOfNextLevel == nextVertex) {
                firstVertexOfNextLevel = null;
                level++;
            }
            return nextVertex;
        }

    }
}
