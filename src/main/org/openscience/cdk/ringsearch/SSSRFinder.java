/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2004-2007  Ulrich Bauer <baueru@cs.tum.edu>
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
 */
package org.openscience.cdk.ringsearch;

import org._3pq.jgrapht.UndirectedGraph;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.graph.MoleculeGraphs;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.cyclebasis.CycleBasis;
import org.openscience.cdk.ringsearch.cyclebasis.SimpleCycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Finds the Smallest Set of Smallest Rings. 
 * This is an implementation of an algorithm 
 * by Franziska Berger, Peter Gritzmann, and Sven deVries, TU M&uuml;nchen,
 * {@cdk.cite BGdV04a}.
 * 
 * <p>Additional related algorithms from {@cdk.cite BGdV04b}.
 *
 * @author Ulrich Bauer <baueru@cs.tum.edu>
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword smallest-set-of-rings
 * @cdk.keyword ring search
 * @cdk.dictref blue-obelisk:findSmallestSetOfSmallestRings_Berger
 * 
 * @cdk.builddepends jgrapht-0.5.3.jar
 * @cdk.depends jgrapht-0.5.3.jar
 */
@TestClass("org.openscience.cdk.ringsearch.SSSRFinderTest")
public class SSSRFinder {

	private IAtomContainer atomContainer;
	private CycleBasis cycleBasis;
	
	/**
	 * Constructs a SSSRFinder for a specified molecule.
	 *
	 * @param   container the molecule to be searched for rings 
	 */
	public SSSRFinder(IAtomContainer container) {
		this.atomContainer = container;
	}
	
	/**
	 * Finds a Smallest Set of Smallest Rings.
	 * The returned set is not uniquely defined.
	 *
	 * @return      a RingSet containing the SSSR   
	 */
    @TestMethod("testFindSSSR,testFindSSSR_IAtomContainer")
    public IRingSet findSSSR() {
		if (atomContainer==null) {
			return null;
		}
		IRingSet ringSet = toRingSet(atomContainer, cycleBasis().cycles());
//		atomContainer.setProperty(CDKConstants.SMALLEST_RINGS, ringSet);
		return ringSet;	  

	}
	
	/**
	 * Finds the Set of Essential Rings.
	 * These rings are contained in every possible SSSR.
	 * The returned set is uniquely defined.
	 *
	 * @return      a RingSet containing the Essential Rings
	 */
	public IRingSet findEssentialRings() {
		if (atomContainer==null) {
			return null;
		}
		IRingSet ringSet = toRingSet(atomContainer, cycleBasis().cycles());
//		atomContainer.setProperty(CDKConstants.ESSENTIAL_RINGS, ringSet);
		return ringSet;
	}
	
	/**
	 * Finds the Set of Relevant Rings.
	 * These rings are contained in every possible SSSR.
	 * The returned set is uniquely defined.
	 *
	 * @return      a RingSet containing the Relevant Rings
	 */
	public IRingSet findRelevantRings() {
		if (atomContainer==null) {
			return null;
		}
		
		IRingSet ringSet = toRingSet(atomContainer, cycleBasis().cycles());
//		atomContainer.setProperty(CDKConstants.RELEVANT_RINGS, ringSet);
		return ringSet;	  
	}
	
	/**
	 * Finds the "interchangeability" equivalence classes.
	 * The interchangeability relation is described in [GLS00].
	 *
	 * @return      a List of RingSets containing the rings in an equivalence class    
	 */
	public List findEquivalenceClasses() {
		if (atomContainer==null) {
			return null;
		}
		
		List<IRingSet> equivalenceClasses = new ArrayList<IRingSet>();
        for (Object o : cycleBasis().equivalenceClasses()) {
            equivalenceClasses.add(toRingSet(atomContainer, (Collection) o));
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
	
	private CycleBasis cycleBasis() {
		if (cycleBasis==null) {
			UndirectedGraph molGraph = MoleculeGraphs.getMoleculeGraph(atomContainer);
			
			cycleBasis = new CycleBasis(molGraph);
		}
		return cycleBasis;
	}
	
	private static IRingSet toRingSet(IAtomContainer container, Collection cycles) {
		
		IRingSet ringSet = container.getBuilder().newRingSet();

		Iterator cycleIterator = cycles.iterator();
		
		while (cycleIterator.hasNext()) {
			SimpleCycle cycle = (SimpleCycle) cycleIterator.next();
			
			IRing ring = container.getBuilder().newRing();
			
			List vertices = cycle.vertexList();
			
			IAtom[] atoms = new IAtom[vertices.size()];
			atoms[0] = (IAtom) vertices.get(0);
            for (int i = 1; i < vertices.size(); i++) {
				atoms[i] = (IAtom) vertices.get(i);
				ring.addElectronContainer(container.getBond(atoms[i-1], atoms[i]));
			}

            for (IAtom atom : atoms) atom.setFlag(CDKConstants.ISINRING, true);

            ring.addElectronContainer(container.getBond(atoms[vertices.size() - 1], atoms[0]));
			ring.setAtoms(atoms);

			ringSet.addAtomContainer(ring);
		}
		
		return ringSet;	  

	}

}
