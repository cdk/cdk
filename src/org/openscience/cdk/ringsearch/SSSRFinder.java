package org.openscience.cdk.ringsearch;
import java.util.Iterator;
import java.util.List;

import org._3pq.jgrapht.UndirectedGraph;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.graph.MoleculeGraphs;
import org.openscience.cdk.ringsearch.cyclebasis.Cycle;
import org.openscience.cdk.ringsearch.cyclebasis.CycleBasis;

/*
 * Created on Jul 5, 2004
 *
 */

/**
 * @author uli
 *
 */
public class SSSRFinder {

    public SSSRFinder() {
    }
    
	public RingSet findSSSR(Molecule mol)
	{
		RingSet sssr = new RingSet();
		
		UndirectedGraph molGraph = MoleculeGraphs.getMoleculeGraph(mol);
		
		CycleBasis cycleBasis = new CycleBasis(molGraph);
		
		Iterator cycleIterator = cycleBasis.cycles().iterator();
		
		while (cycleIterator.hasNext()) {
			Cycle cycle = (Cycle) cycleIterator.next();
			
			Ring ring = new Ring();
			
			List vertices = cycle.vertices();
			
			Atom[] atoms = new Atom[vertices.size()];
			atoms[0] = (Atom) vertices.get(0);
			for (int i = 1; i < vertices.size(); i++) {
				atoms[i] = (Atom) vertices.get(i);
				ring.addElectronContainer(new Bond(atoms[i - 1], atoms[i], 1));
			}
			ring.addElectronContainer(new Bond(atoms[vertices.size() - 1], atoms[0], 1));
			ring.setAtoms(atoms);

			sssr.add(ring);
		}
		
		return sssr;	  
	}
}
