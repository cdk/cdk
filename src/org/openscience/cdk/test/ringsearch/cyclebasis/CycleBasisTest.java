package org.openscience.cdk.test.ringsearch.cyclebasis;
import java.util.Arrays;

import org._3pq.jgrapht.alg.ConnectivityInspector;
import org._3pq.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.ringsearch.cyclebasis.CycleBasis;

import junit.framework.TestCase;
/*
 * Created on 08.07.2004
 *
 */

/**
 * @author uli
 *
 */
public class CycleBasisTest extends TestCase {
	
	CycleBasis basis;
	SimpleGraph g;

	protected void setUp() {
		g = new SimpleGraph(  );
		
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
		
		basis = new CycleBasis(g);
	}
	
	public void testCycleBasis() {
		assertTrue(basis.cycles().size() == 
			g.edgeSet().size() - g.vertexSet().size() 
			+ new ConnectivityInspector(g).connectedSets().size());
	}
		
	public void testWeightVector() {
		assertTrue(Arrays.equals(basis.weightVector(), new double[] {3.,3.,3.,3.}) );
	}
	
	public void testEssentialCycles() {
		assertTrue(basis.essentialCycles().size() == 4);
	}
	
	public void testRelevantCycles() {
		assertTrue(basis.relevantCycles().size() == 4);
	}
	
	public void testEquivalenceClasses() {
		assertTrue(basis.equivalenceClasses().size() == 4);
	}
	
}
