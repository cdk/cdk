package org.openscience.cdk.test.ringsearch.cyclebasis;
import java.util.List;

import junit.framework.TestCase;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.graph.BiconnectivityInspector;

/**
 * @author uli
 *
 */
public class BiconnectivityInspectorTest extends TestCase {
		
	public void testBiconnectedSets() {
		SimpleGraph g = new SimpleGraph(  );
		
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
		 
		 g.addEdge( "a", "b" );
		 g.addEdge( "a", "c" );
		 g.addEdge( "b", "c" );
		 g.addEdge( "b", "d" );
		 g.addEdge( "c", "d" );
		 
		 g.addEdge( "d", "e" );
		 
		 g.addEdge( "d", "g" );
		 
		 g.addEdge( "e", "f" );
		 g.addEdge( "e", "h" );
		 g.addEdge( "f", "h" );
		 
		 g.addEdge( "i", "j" );
		 g.addEdge( "i", "k" );
		 g.addEdge( "j", "k" );
		
		
		BiconnectivityInspector bci = new BiconnectivityInspector(g);
		List connectedSets = bci.biconnectedSets();
				
		assertTrue(connectedSets.size() == 5);
	}
	
}
