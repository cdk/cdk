/* $Revision: 7672 $ $Author: egonw $ $Date: 2007-01-09 09:29:55 +0000 (Tue, 09 Jan 2007) $
 *
 * Copyright (C) 2002-2007  Stefan Kuhn <shk3@users.sf.net>
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

import java.util.BitSet;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.MFAnalyser;

/**
 * Generates an extended fingerprint for a given AtomContainer, that
 * extends the Fingerprinter with additional bits describing ring
 * features.
 *  
 * @author         shk3
 * @cdk.created    2006-01-13
 * @cdk.keyword    fingerprint
 * @cdk.keyword    similarity
 * @cdk.module     standard
 * 
 * @see            org.openscience.cdk.fingerprint.Fingerprinter
 */
public class ExtendedFingerprinter extends Fingerprinter {
	
	/**
	 * Creates a fingerprint generator of length <code>defaultSize</code>
	 * and with a search depth of <code>defaultSearchDepth</code>.
	 */
	public ExtendedFingerprinter() {
		this(defaultSize, 
		     defaultSearchDepth, null);
	}
	
	public ExtendedFingerprinter(int size) {
		this(size, defaultSearchDepth, null);
	}
	
	public ExtendedFingerprinter(int size, int searchDepth) {
		this(size, searchDepth, null);
	}
	
	/**
	 * Constructs a fingerprint generator that creates fingerprints of
	 * the given size, using a generation algorithm with the given search
	 * depth, and which uses the given AllRingsFinder to reuse previous
	 * results.
	 *
	 * @param  size        The desired size of the fingerprint
	 * @param  searchDepth The desired depth of search
	 * @param  ringFinder  The AllRingsFinder to be used by the aromaticity detection
	 */
    public ExtendedFingerprinter(int size, int searchDepth, AllRingsFinder ringFinder) {
    	super(size, searchDepth);
	}
	
	/**
	 * Generates a fingerprint of the default size for the given AtomContainer, using path and ring metrics
	 * It contains the informations from getFingerprint() and bits which tell if the structure has 0 rings, 1 or less rings,
	 * 2 or less rings ... 10 or less rings (referring to smalles set of smallest rings) and bits which tell if there is a ring with 3, 4 ... 10 atoms.
	 *
	 *@param     ac         The AtomContainer for which a Fingerprint is generated
	 *@exception Exception  Description of the Exception
	 */
	public BitSet getFingerprint(IAtomContainer ac) throws Exception {
		return this.getFingerprint(ac,null);
	}
		
	/**
	 * Generates a fingerprint of the default size for the given AtomContainer, using path and ring metrics
	 * It contains the informations from getFingerprint() and bits which tell if the structure has 0 rings, 1 or less rings,
	 * 2 or less rings ... 10 or less rings and bits which tell if there is a ring with 3, 4 ... 10 atoms.
	 * The RingSet used is passed via rs parameter. This can be an allRingsSet or a smallesSetOfSmallestRings. If none is given, a sssr is calculated.
	 *
	 *@param     ac         The AtomContainer for which a Fingerprint is generated
	 *@param     rs         A RingSet of ac (if not available, use  getExtendedFingerprint(AtomContainer ac), which does the calculation)
	 *@exception Exception  Description of the Exception
	 */
	public BitSet getFingerprint(IAtomContainer ac, IRingSet rs) throws Exception {
		BitSet bs= super.getFingerprint(ac, size-25);
		MFAnalyser mfa=new MFAnalyser(ac);
		float weight=mfa.getCanonicalMass();
		for(int i=1;i<11;i++){
			if(weight>(100*i))
				bs.set(size-26+i);
		}
		if(rs==null){
			rs=new SSSRFinder(ac).findSSSR();
		}
		for(int i=0;i<7;i++){
			if(rs.getAtomContainerCount()>i)
				bs.set(size-15+i);
		}
		for(int i=0;i<rs.getAtomContainerCount();i++){
			for(int k=3;k<11;k++){
				if(((IRing)rs.getAtomContainer(i)).getAtomCount()==k){
					bs.set(size-8+k-3);
					break;					
				}					
			}
		}
		return bs;
	}

}