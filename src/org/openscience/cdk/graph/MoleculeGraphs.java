package org.openscience.cdk.graph;
import java.util.Iterator;

import org._3pq.jgrapht.Graph;
import org._3pq.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;

/*
 * Created on May 22, 2004
 *
 */
/**
 * @author uli
 *
 */
public class MoleculeGraphs {
	private MoleculeGraphs() {}
	
	static public SimpleGraph getMoleculeGraph(Molecule molecule) {
		SimpleGraph graph = new SimpleGraph();
		for (int i=0; i<molecule.getAtomCount(); i++	) {
			Atom atom = molecule.getAtoms()[i];
			graph.addVertex(atom);
		}
		
		for (int i=0; i<molecule.getBondCount(); i++	) {
			Bond bond = molecule.getBonds()[i];
			int order = (int) bond.getOrder();
			
			/*
			for (int j=0; j<order; j++) {
				graph.addEdge(bond.getAtoms()[0], bond.getAtoms()[1]);
			}
			*/
			graph.addEdge(bond.getAtoms()[0], bond.getAtoms()[1]);
		}
		return graph;
	}
	
	static public String asString(Graph molGraph) {
		StringBuffer buf = new StringBuffer();
		buf.append("[");

	        Iterator i = molGraph.vertexSet().iterator();
	        boolean hasNext = i.hasNext();
	        while (hasNext) {
	            Atom o = (Atom) i.next();
	            buf.append(o.getSymbol());
	            hasNext = i.hasNext();
	            if (hasNext)
	                buf.append(", ");
	        }

		buf.append("]");
		
		return "(" + buf.toString() + ", " + molGraph.edgeSet().toString(  ) + ")";
	}
}
