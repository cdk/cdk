package org.openscience.cdk.test.ringsearch.cyclebasis;
import java.util.Arrays;

import junit.framework.TestCase;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.ringsearch.cyclebasis.CycleBasis;
import org.openscience.cdk.ringsearch.cyclebasis.SimpleCycleBasis;
/*
 * Created on 08.07.2004
 *
 */

/**
 * @author uli
 *
 */
public class SimpleCycleBasisTest extends TestCase {

	SimpleGraph g;
	SimpleCycleBasis basis;

	protected void setUp() {
		g = new SimpleGraph();
		g.addVertex( "a" );
		g.addVertex( "b" );
		g.addVertex( "c" );
		g.addVertex( "d" );
		g.addVertex( "e" );
		g.addVertex( "f" );
		g.addVertex( "g" );
		g.addVertex( "h" );
		
		g.addEdge( "a", "b" );
		g.addEdge( "a", "c" );
		g.addEdge( "b", "c" );
		g.addEdge( "b", "d" );
		g.addEdge( "b", "e" );
		g.addEdge( "b", "g" );
		g.addEdge( "c", "d" );
		g.addEdge( "c", "e" );
		g.addEdge( "c", "f" );
		g.addEdge( "c", "h" );
		g.addEdge( "d", "e" );
		g.addEdge( "d", "g" );
		g.addEdge( "e", "f" );
		g.addEdge( "e", "h" );
		g.addEdge( "f", "h" );
		
		basis = new SimpleCycleBasis( g );

	}
	
	public void testSimpleCycleBasis() {
		assertTrue(basis.cycles().size() == g.edgeSet().size() - g.vertexSet().size() + 1);
	}
	
	public void testSimpleCycleBasisCompleteGraph() {
		g = new SimpleGraph();
		g.addVertex( "a" );
		g.addVertex( "b" );
		g.addVertex( "c" );
		g.addVertex( "d" );
		g.addVertex( "e" );
		
		g.addEdge( "a", "b" );
		g.addEdge( "a", "c" );
		g.addEdge( "a", "d" );
		g.addEdge( "a", "e" );
		g.addEdge( "b", "c" );
		g.addEdge( "b", "d" );
		g.addEdge( "b", "e" );
		g.addEdge( "c", "d" );
		g.addEdge( "c", "e" );
		g.addEdge( "d", "e" );
		
		basis = new SimpleCycleBasis( g );
		assertTrue(basis.cycles().size() == g.edgeSet().size() - g.vertexSet().size() + 1);
		assertTrue(Arrays.equals(basis.weightVector(), new double[] {3.,3.,3.,3.,3.,3.}) );
		assertTrue(basis.relevantCycles().size() == 10);
		assertTrue(basis.essentialCycles().size() == 0);
		assertTrue(basis.equivalenceClasses().size() == 1);
	}
	
	public void testWeightVector() {
		assertTrue(Arrays.equals(basis.weightVector(), new double[] {3.,3.,3.,3.,3.,3.,3.,3.}) );
	}
	
	public void testRelevantCycles() {
		assertTrue(basis.relevantCycles().size() == 10);
	}
	
	public void testEssentialCycles() {
		assertTrue(basis.essentialCycles().size() == 2);
	}
	
	public void testEquivalenceClasses() {
		assertTrue(basis.equivalenceClasses().size() == 4);
	}
		
	public void testEquivalenceClassesEmptyIntersection() {
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
		g.addVertex( "l" );
		g.addVertex( "m" );
		g.addVertex( "n" );
		g.addVertex( "o" );
		g.addVertex( "p" );
		g.addVertex( "q" );
		g.addVertex( "r" );
		g.addVertex( "s" );
		g.addVertex( "t" );
		g.addVertex( "u" );
		g.addVertex( "v" );
		g.addVertex( "w" );
		
		g.addEdge( "a", "b" );
		g.addEdge( "a", "c" );
		g.addEdge( "a", "g" );
		g.addEdge( "b", "d" );
		g.addEdge( "b", "m" );
		g.addEdge( "c", "d" );
		g.addEdge( "c", "e" );
		g.addEdge( "c", "h" );
		g.addEdge( "d", "f" );
		g.addEdge( "d", "l" );
		g.addEdge( "e", "f" );
		g.addEdge( "e", "i" );
		g.addEdge( "e", "j" );
		g.addEdge( "f", "j" );
		g.addEdge( "f", "k" );
		g.addEdge( "g", "h" );
		g.addEdge( "g", "v" );
		g.addEdge( "g", "r" );
		g.addEdge( "h", "i" );
		g.addEdge( "h", "p" );
		g.addEdge( "h", "t" );
		g.addEdge( "i", "j" );
		g.addEdge( "i", "n" );
		g.addEdge( "j", "k" );
		g.addEdge( "j", "n" );
		g.addEdge( "j", "o" );
		g.addEdge( "k", "l" );
		g.addEdge( "k", "o" );
		g.addEdge( "l", "m" );
		g.addEdge( "l", "q" );
		g.addEdge( "l", "u" );
		g.addEdge( "m", "s" );
		g.addEdge( "m", "w" );
		g.addEdge( "n", "o" );
		g.addEdge( "n", "p" );
		g.addEdge( "o", "q" );
		g.addEdge( "p", "q" );
		g.addEdge( "p", "r" );
		g.addEdge( "q", "s" );
		g.addEdge( "r", "s" );
		g.addEdge( "t", "u" );
		g.addEdge( "v", "w" );
		
		CycleBasis basis = new CycleBasis( g );

		assertTrue(basis.equivalenceClasses().size() == 19);		
		assertTrue(basis.essentialCycles().size() == 18);
		
	}

	/*
	public void testInverseBinaryMatrix() {
		boolean[][] a = new boolean[][] {
				{true,true,true},
				{true,false,true},
				{false,false,true}
		};
		
		boolean[][] inv = new boolean[][] {
				{false,true,true},
				{true,true,false},
				{false,false,true}
		};
		
		boolean[][] r = SimpleCycleBasis.inverseBinaryMatrix(a, 3);
		
		for (int i=0; i<3; i++) {
			for (int j=i; j<3; j++) {
				assertTrue(inv[i][j] == r[i][j]);
			}
		}
	}
	
	public void testConstructKernelVector() {
		boolean[][] am = new boolean[][] {{true,false,true},{false,true,false}};
		boolean[] u = SimpleCycleBasis.constructKernelVector(3, am, 2);
		assertTrue(Arrays.equals(
				SimpleCycleBasis.constructKernelVector(3, am, 2),
				new boolean[] {true,false,true}
		));
	}
	*/
}
