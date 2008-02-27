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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.formula.MolecularFormulaManipulator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;

/**
 * Generates an extended fingerprint for a given AtomContainer, that
 * extends the Fingerprinter with additional bits describing ring
 * features.
 *  
 * @author         shk3
 * @cdk.created    2006-01-13
 * @cdk.keyword    fingerprint
 * @cdk.keyword    similarity
 * @cdk.module     extra
 * @cdk.svnrev     $Revision: 9162 $
 * 
 * @see            org.openscience.cdk.fingerprint.Fingerprinter
 */
@TestClass("org.openscience.cdk.fingerprint.ExtendedFingerprinterTest")
public class ExtendedFingerprinter implements IFingerprinter {

	private final int RESERVED_BITS = 25;
	
	private Fingerprinter fingerprinter = null;

	/**
	 * Creates a fingerprint generator of length <code>defaultSize</code>
	 * and with a search depth of <code>defaultSearchDepth</code>.
	 */
	public ExtendedFingerprinter() {
		this(Fingerprinter.defaultSize, 
			 Fingerprinter.defaultSearchDepth);
	}
	
	public ExtendedFingerprinter(int size) {
		this(size, Fingerprinter.defaultSearchDepth);
	}
	
	/**
	 * Constructs a fingerprint generator that creates fingerprints of
	 * the given size, using a generation algorithm with the given search
	 * depth.
	 *
	 * @param  size        The desired size of the fingerprint
	 * @param  searchDepth The desired depth of search
	 */
	public ExtendedFingerprinter(int size, int searchDepth) {
    	this.fingerprinter = new Fingerprinter(size-RESERVED_BITS, searchDepth);
	}
	
	/**
	 * Generates a fingerprint of the default size for the given AtomContainer, using path and ring metrics
	 * It contains the informations from getFingerprint() and bits which tell if the structure has 0 rings, 1 or less rings,
	 * 2 or less rings ... 10 or less rings (referring to smalles set of smallest rings) and bits which tell if there is a ring with 3, 4 ... 10 atoms.
	 *
	 *@param     ac         The AtomContainer for which a Fingerprint is generated
	 *@exception Exception  Description of the Exception
	 */
    @TestMethod("testGetFingerprint_IAtomContainer")
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
    @TestMethod("testGetFingerprint_IAtomContainer_IRingSet")
    public BitSet getFingerprint(IAtomContainer ac, IRingSet rs) throws Exception {
		BitSet bs = fingerprinter.getFingerprint(ac);
		int size = this.getSize();
		double weight = MolecularFormulaManipulator.getTotalNaturalAbundance(MolecularFormulaManipulator.getMolecularFormula(ac));
		for(int i=1;i<11;i++){
			if(weight>(100*i))
				bs.set(size-26+i); // 26 := RESERVED_BITS+1
		}
		if(rs==null){
			rs=new AllRingsFinder().findAllRings(ac);
		}
		for(int i=0;i<7;i++){
			if(rs.getAtomContainerCount()>i)
				bs.set(size-15+i); // 15 := RESERVED_BITS+1+10 mass bits
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

    @TestMethod("testGetSize")
    public int getSize() {
		return fingerprinter.getSize()+RESERVED_BITS;
	}

}