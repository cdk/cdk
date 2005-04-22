/*
 * Created on Oct 19, 2004
 *
 */
package org.openscience.cdk.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.Graph;
import org._3pq.jgrapht.traverse.BreadthFirstIterator;

/**
 * @author uli
 *
 * @cdk.module standard
 */
public final class BFSShortestPath {
    private BFSShortestPath(  ) {} // ensure non-instantiability.

    public static List findPathBetween( Graph graph, Object startVertex,
            Object endVertex ) {
            MyBreadthFirstIterator iter =
                new MyBreadthFirstIterator( graph, startVertex );

            while( iter.hasNext(  ) ) {
                Object vertex = iter.next(  );

                if( vertex.equals( endVertex ) ) {
                    return createPath( iter, endVertex );
                }
            }

            return null;
        }

    private static List createPath( MyBreadthFirstIterator iter, Object endVertex ) {
        List path = new ArrayList(  );

        while( true ) {
            Edge edge = iter.getSpanningTreeEdge( endVertex );

            if( edge == null ) {
                break;
            }

            path.add( edge );
            endVertex = edge.oppositeVertex( endVertex );
        }

        Collections.reverse( path );

        return path;
    }
    
    
	private static class MyBreadthFirstIterator extends BreadthFirstIterator {

		public MyBreadthFirstIterator(Graph g, Object startVertex) {
			super(g, startVertex);
		}
		
	    protected void encounterVertex( Object vertex, Edge edge ) {
	        super.encounterVertex( vertex, edge );
	        putSeenData( vertex, edge );
	    }

	    public Edge getSpanningTreeEdge( Object vertex ) {
	        return (Edge) getSeenData( vertex );
	    }
		
	}
}
