package org.openscience.cdk.test.ringsearch.cyclebasis;
import java.util.List;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.graph.MinimalPathIterator;

import junit.framework.TestCase;
/*
 * Created on 08.07.2004
 *
 */

/**
 * @author uli
 *
 */
public class MinimalPathIteratorTest extends TestCase {
	
	public void testMinimalPathIterator() {
		 SimpleGraph g    = new SimpleGraph(  );
		 
		 g.addVertex( "a" );
		 g.addVertex( "b" );
		 g.addVertex( "c" );
		 g.addVertex( "d" );
		 g.addVertex( "e" );
		 g.addVertex( "f" );
		 g.addVertex( "g" );
		 g.addVertex( "h" );
		 g.addVertex( "i" );
		 g.addVertex( "j" );
		 g.addVertex( "k" );
		 g.addVertex( "l" );
		 
		 g.addEdge( "a", "b" );
		 g.addEdge( "b", "c" );
		 g.addEdge( "c", "d" );
		 
		 g.addEdge( "a", "e" );
		 g.addEdge( "b", "f" );
		 g.addEdge( "c", "g" );
		 g.addEdge( "d", "h" );
		 
		 g.addEdge( "e", "f" );
		 g.addEdge( "f", "g" );
		 g.addEdge( "g", "h" );
		 
		 g.addEdge( "e", "i" );
		 g.addEdge( "f", "j" );
		 g.addEdge( "g", "k" );
		 g.addEdge( "h", "l" );
		 
		 g.addEdge( "i", "j" );
		 g.addEdge( "j", "k" );
		 g.addEdge( "k", "l" );
		 
		 int count = 0;
		 for (MinimalPathIterator i = new MinimalPathIterator(g, "a", "l"); i.hasNext();) {
		 	assertTrue(((List)i.next()).size() == 5);
		 	count++;
		 }
		 
		 assertTrue(count == 10);

	}
	
}
