/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;

import java.util.BitSet;

/**
 * Fingerprinter that gives a bit set which has a size equal to the number
 * of substructures it was constructed from. A set bit indicates that that
 * substructure was found at least once in the molecule for which the 
 * fingerprint was calculated.
 * 
 * @author       egonw
 * @cdk.created  2005-12-30
 *
 * @cdk.keyword  fingerprint
 * @cdk.keyword  similarity
 * 
 * @cdk.module   experimental
 * @cdk.svnrev  $Revision$
 */
public class SubstructureFingerprinter implements IFingerprinter {

	private IAtomContainerSet substructureSet;

    /**
     * Set up the fingerprinter to use the fragments from {@link org.openscience.cdk.fingerprint.StandardSubstructureSets}.
     */
    public SubstructureFingerprinter() {
        try {
            this.substructureSet = StandardSubstructureSets.getFunctionalGroupSubstructureSet();
        } catch (Exception e) {
            substructureSet = null;
        }
    }

    /**
     * Set up the fingerprinter to use a user-defined set of fragments.
     *
     * @param substructureSet The collection of fragments to look for
     */
    public SubstructureFingerprinter(IAtomContainerSet substructureSet) {
		this.substructureSet = substructureSet;
	}
	
	/**
	 * Calculates the substructure fingerprint for the given AtomContainer.
	 */
	public BitSet getFingerprint(IAtomContainer ac) throws Exception {

        if (substructureSet == null) {
            throw new CDKException("No substructures were defined");
        }
        
        int bitsetLength = substructureSet.getAtomContainerCount();
		BitSet fingerPrint = new BitSet(bitsetLength);
		
		IAtomContainer substructure;
		for (int i=0; i<bitsetLength; i++) {
			substructure = substructureSet.getAtomContainer(i);
			if (UniversalIsomorphismTester.isSubgraph(ac, substructure)) 
				fingerPrint.set(i, true);
		}
		
		return fingerPrint;
	}

	public int getSize() {
		return substructureSet.getAtomContainerCount();
	}

}
