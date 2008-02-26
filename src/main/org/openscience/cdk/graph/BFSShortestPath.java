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

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.Graph;
import org._3pq.jgrapht.traverse.BreadthFirstIterator;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class that finds the shortest bond path between two atoms using
 * a breadth first search.
 *
 * @author uli
 *
 * @cdk.module  standard
 * @cdk.svnrev  $Revision$
 * @cdk.created 2004-10-19
 */
@TestClass("org.openscience.cdk.graph.BFSShortestPathTest")
public final class BFSShortestPath {
    
    private BFSShortestPath(  ) {} // ensure non-instantiability.

    @TestMethod("testFindPathBetween_Graph_Object_Object")
    public static List<Edge> findPathBetween( Graph graph, Object startVertex,
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

    private static List<Edge> createPath( MyBreadthFirstIterator iter, Object endVertex ) {
        List<Edge> path = new ArrayList<Edge>(  );

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
