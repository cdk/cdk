/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.ringsearch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org._3pq.jgrapht.UndirectedGraph;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.graph.MoleculeGraphs;
import org.openscience.cdk.ringsearch.cyclebasis.CycleBasis;
import org.openscience.cdk.ringsearch.cyclebasis.SimpleCycle;

/**
 * Finds the Smallest Set of Smallest Rings. 
 * This is an implementation of an algorithm 
 * by Franziska Berger, Peter Gritzmann, and Sven deVries, TU M&uuml;nchen,
 * {@cdk.cite BGdV04a}.
 * 
 * Additional related algorithms from {@cdk.cite BGdV04b}.
 *
 * @author Ulrich Bauer <baueru@cs.tum.edu>
 * 
 *
 * @cdk.module standard
 *
 * @cdk.keyword smallest-set-of-rings
 * @cdk.keyword ring search
 * 
 * @cdk.builddepends jgrapht-0.5.3.jar
 * @cdk.depends jgrapht-0.5.3.jar
 */

public class SSSRFinder {

	private Molecule mol;
	private CycleBasis cycleBasis;
	
	/**
	 * Constructs a SSSRFinder.
	 *
	 * @deprecated Replaced by {@link #SSSRFinder(Molecule)}
	 */
	public SSSRFinder() {
	}
	
	/**
	 * Constructs a SSSRFinder for a specified molecule.
	 *
	 * @param   mol the molecule to be searched for rings 
	 */
	public SSSRFinder(Molecule mol) {
		this.mol = mol;
	}
	
	/**
	 * Finds a Smallest Set of Smallest Rings.
	 * The returned set is not uniquely defined.
	 *
	 * @return      a RingSet containing the SSSR   
	 */
	public RingSet findSSSR() {
		if (mol==null) {
			return null;
		}
		
		return toRingSet(mol, cycleBasis().cycles());	  

	}
	
	/**
	 * Finds the Set of Essential Rings.
	 * These rings are contained in every possible SSSR.
	 * The returned set is uniquely defined.
	 *
	 * @return      a RingSet containing the Essential Rings
	 */
	public RingSet findEssentialRings() {
		if (mol==null) {
			return null;
		}
		
		return toRingSet(mol, cycleBasis().essentialCycles());	  

	}
	
	/**
	 * Finds the Set of Relevant Rings.
	 * These rings are contained in everry possible SSSR.
	 * The returned set is uniquely defined.
	 *
	 * @return      a RingSet containing the Relevant Rings
	 */
	public RingSet findRelevantRings() {
		if (mol==null) {
			return null;
		}
		
		return toRingSet(mol, cycleBasis().relevantCycles().keySet());	  

	}
	
	/**
	 * Finds the "interchangeability" equivalence classes.
	 * The interchangeability relation is described in [GLS00].
	 *
	 * @return      a List of RingSets containing the rings in an equivalence class    
	 */
	public List findEquivalenceClasses() {
		if (mol==null) {
			return null;
		}
		
		List equivalenceClasses = new ArrayList();
		for (Iterator i=cycleBasis().equivalenceClasses().iterator(); i.hasNext();) {
			equivalenceClasses.add(toRingSet(mol, (Collection) i.next()));
		}
		
		return equivalenceClasses;	  

	}
	
	/**
	 * Returns a vector containing the lengths of the rings in a SSSR.
	 * The vector is uniquely defined for any SSSR of a molecule.
	 *
	 * @return An <code>int[]</code> containing the length of the rings in a SSSR
	 */
	public int[] getSSSRWeightVector() {
		return cycleBasis().weightVector();
	}
	
	/**
	 * Returns a vector containing the size of the "interchangeability" equivalence classes.
	 * The vector is uniquely defined for any SSSR of a molecule.
	 *
	 * @return An <code>int[]</code> containing the size of the equivalence classes in a SSSR
	 */
	public int[] getEquivalenceClassesSizeVector() {
		List equivalenceClasses = cycleBasis().equivalenceClasses();
		int[] result = new int[equivalenceClasses.size()];
		for (int i=0; i<equivalenceClasses.size(); i++) {
			result[i] = ((Collection)equivalenceClasses.get(i)).size();
		}
		return result;
	}
	
	
	
	/**
	 * Finds a Smallest Set of Smallest Rings.
	 * The returned set is not uniquely defined.
	 *
	 * @deprecated replaced by {@link #findSSSR()}
	 * @param   mol the molecule to be searched for rings 
	 * @return      a RingSet containing the SSSR
	 */
	static public RingSet findSSSR(Molecule mol)
	{
		RingSet sssr = new RingSet();
		
		UndirectedGraph molGraph = MoleculeGraphs.getMoleculeGraph(mol);
		
		CycleBasis cycleBasis = new CycleBasis(molGraph);
		
		return toRingSet(mol, cycleBasis.cycles());  
	}
	
	private CycleBasis cycleBasis() {
		if (cycleBasis==null) {
			UndirectedGraph molGraph = MoleculeGraphs.getMoleculeGraph(mol);
			
			cycleBasis = new CycleBasis(molGraph);
		}
		return cycleBasis;
	}
	
	private static RingSet toRingSet(Molecule mol, Collection cycles) {
		
		RingSet ringSet = new RingSet();

		Iterator cycleIterator = cycles.iterator();
		
		while (cycleIterator.hasNext()) {
			SimpleCycle cycle = (SimpleCycle) cycleIterator.next();
			
			Ring ring = new Ring();
			
			List vertices = cycle.vertexList();
			
			Atom[] atoms = new Atom[vertices.size()];
			atoms[0] = (Atom) vertices.get(0);
			for (int i = 1; i < vertices.size(); i++) {
				atoms[i] = (Atom) vertices.get(i);
				ring.addElectronContainer(mol.getBond(atoms[i-1], atoms[i]));
			}
			ring.addElectronContainer(mol.getBond(atoms[vertices.size() - 1], atoms[0]));
			ring.setAtoms(atoms);

			ringSet.add(ring);
		}
		
		return ringSet;	  

	}

}
