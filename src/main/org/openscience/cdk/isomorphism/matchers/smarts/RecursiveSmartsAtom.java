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
package org.openscience.cdk.isomorphism.matchers.smarts;

import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This matches recursive smarts atoms. 
 *
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class RecursiveSmartsAtom extends SMARTSAtom {
	private static final long serialVersionUID = 1L;
	private final static LoggingTool logger = new LoggingTool(
			RecursiveSmartsAtom.class);
	/**
	 * AtomContainer of the target molecule to which this recursive smarts query trying to match
	 */
	private IAtomContainer atomContainer = null;
	
	/**
	 * The IQueryAtomContainer created by parsing the recursive smarts
	 */
	private IQueryAtomContainer recursiveQuery = null;
	
	/**
	 * BitSet that records which atom in the target molecule matches the
	 * recursive smarts
	 */
	private BitSet bitSet = null;
	
	/**
	 * Creates a new instance
	 *
	 * @param query
	 */
	public RecursiveSmartsAtom(IQueryAtomContainer query) {
		super();
		this.recursiveQuery = query;
	}
	
    /* (non-Javadoc)
     * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
     */
    public boolean matches(IAtom atom) {
    	if (recursiveQuery.getAtomCount() == 1) { // only one atom. Then just match that atom
    		return ((IQueryAtom)recursiveQuery.getAtom(0)).matches(atom);
    	}
    	
    	// Check wither atomContainer has been set
    	if (atomContainer == null) {
    		logger.error("In RecursiveSmartsAtom, atomContainer can't be null! You must set it before matching");
    		return false;
    	}
    	
    	// initialize bitsets
    	if (bitSet == null) {
    		try {
    			initilizeBitSets();
    		} catch (CDKException cex) {
        		logger.error("Error found when matching recursive smarts: " + cex.getMessage());
        		return false;
    		}
    	}
    	int atomNumber = atomContainer.getAtomNumber(atom);
        return bitSet.get(atomNumber);
    }
    
    /**
     * This method calculates all possible matches of this recursive smarts
     * to the AtomContainer. It set the index of the first atom of each match
     * in the bitset to be true.
     * 
     * @throws CDKException
     */
    private void initilizeBitSets() throws CDKException {
		List<List<RMap>> bondMappings = null;
		bondMappings = UniversalIsomorphismTester.getSubgraphMaps(atomContainer, recursiveQuery);

		bitSet = new BitSet(atomContainer.getAtomCount());
		
		for (List<RMap> bondMapping : bondMappings) {
			Collections.sort(bondMapping, new Comparator<RMap>() {
				public int compare(RMap r1, RMap r2) {
					if (r1.getId2() > r2.getId2()) return 1;
					else if (r1.getId2() == r2.getId2()) return 0;
					else return -1;
				}
			});
			RMap rmap0 = bondMapping.get(0);
			IBond bond0 = atomContainer.getBond(rmap0.getId1());
			IAtom atom0 = bond0.getAtom(0);
			IAtom atom1 = bond0.getAtom(1);				
			IBond qbond0 = recursiveQuery.getBond(rmap0.getId2());
			IQueryAtom qatom0 = (IQueryAtom)qbond0.getAtom(0);
			IQueryAtom qatom1 = (IQueryAtom)qbond0.getAtom(1);
			
			if ( (qatom0.matches(atom0) && qatom1.matches(atom1))
					&& (qatom0.matches(atom1) && qatom1.matches(atom0)) ) { // they match each other no matter what order
				if (bondMapping.size() > 1) { // look for the second bond
					IBond bond1 = atomContainer.getBond(bondMapping.get(1).getId1());
					IBond qbond1 = recursiveQuery.getBond(bondMapping.get(1).getId2());
					if (recursiveQuery.getAtomNumber(qatom0) == 0) {
						if (qbond1.contains(qatom0) && bond1.contains(atom0)) { // atom0 <-> qatom0
							bitSet.set(atomContainer.getAtomNumber(atom0), true);
						} else if (qbond1.contains(qatom0) && bond1.contains(atom1)) { // atom1 <-> qatom0
							bitSet.set(atomContainer.getAtomNumber(atom1), true);
						} else if (!qbond1.contains(qatom0) && bond1.contains(atom0)) { // ! (qatom0 <-> atom0)
							bitSet.set(atomContainer.getAtomNumber(atom1), true);
						} else { // (!qbond1.contains(qatom0) && bond1.contains(atom1) // ! (qatom0 <-> atom1 ) 
							bitSet.set(atomContainer.getAtomNumber(atom0), true);							
						}
					} else {
						if (qbond1.contains(qatom1) && bond1.contains(atom0)) { 
							bitSet.set(atomContainer.getAtomNumber(atom0), true);
						} else if (qbond1.contains(qatom1) && bond1.contains(atom1)) {
							bitSet.set(atomContainer.getAtomNumber(atom1), true);
						} else if (!qbond1.contains(qatom1) && bond1.contains(atom1)) {
							bitSet.set(atomContainer.getAtomNumber(atom0), true);
						} else {
							bitSet.set(atomContainer.getAtomNumber(atom1), true);							
						}						
					}
				} else {
					// both matches
					bitSet.set(atomContainer.getAtomNumber(atom1), true);
					bitSet.set(atomContainer.getAtomNumber(atom0), true);
				}
			} else {
				if (recursiveQuery.getAtomNumber(qatom0) == 0) { // starts from qatom1
					if (qatom0.matches(atom0) && qatom1.matches(atom1)) {
						bitSet.set(atomContainer.getAtomNumber(atom0), true);
					} else {
						bitSet.set(atomContainer.getAtomNumber(atom1), true);
					}
				} else { // qatom1 is the first atom
					if (qatom0.matches(atom1) && qatom1.matches(atom0)) {
						bitSet.set(atomContainer.getAtomNumber(atom0), true);
					} else {
						bitSet.set(atomContainer.getAtomNumber(atom1), true);
					}
				}
			}
		}    		
    }

	public IQueryAtomContainer getRecursiveQuery() {
		return recursiveQuery;
	}

	public void setRecursiveQuery(IQueryAtomContainer query) {
		this.recursiveQuery = query;
	}

	public IAtomContainer getAtomContainer() {
		return atomContainer;
	}

	public void setAtomContainer(IAtomContainer atomContainer) {
		this.atomContainer = atomContainer;
		this.bitSet = null; // new atom container, reset matching bitset.
	}
}
