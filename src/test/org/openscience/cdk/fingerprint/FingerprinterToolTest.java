/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * @cdk.module test-standard
 */
public class FingerprinterToolTest extends CDKTestCase
{		
	
	public FingerprinterToolTest()
	{
		super();
	}

	
	@Test
	public void testIsSubset_BitSet_BitSet() throws java.lang.Exception
	{
		Fingerprinter fingerprinter = new Fingerprinter();
		
		IAtomContainer mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getBitFingerprint(mol).asBitSet();
		IAtomContainer frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getBitFingerprint(frag1).asBitSet();
		Assert.assertTrue(FingerprinterTool.isSubset(bs, bs1));
	}

    @Test
    public void testListDifferences_BitSet_BitSet() throws Exception{
		Fingerprinter fingerprinter = new Fingerprinter();
		
		IAtomContainer mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getBitFingerprint(mol).asBitSet();
		IAtomContainer frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getBitFingerprint(frag1).asBitSet();
		List l=FingerprinterTool.listDifferences(bs1, bs);
		Assert.assertEquals(l.size(),19);
	}
}

