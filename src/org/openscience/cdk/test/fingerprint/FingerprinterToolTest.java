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
package org.openscience.cdk.test.fingerprint;

import java.util.BitSet;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.fingerprint.FingerprinterTool;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module test-standard
 */
public class FingerprinterToolTest extends CDKTestCase
{
	
	private static LoggingTool logger = new LoggingTool(FingerprinterToolTest.class);
	
	public FingerprinterToolTest(String name)
	{
		super(name);
	}

	
	public static Test suite() {
		return new TestSuite(FingerprinterToolTest.class);
	}

	public void testIsSubset_BitSet_BitSet() throws java.lang.Exception
	{
		Fingerprinter fingerprinter = new Fingerprinter();
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		assertTrue(FingerprinterTool.isSubset(bs, bs1));
	}
	
	public void testListDifferences_BitSet_BitSet() throws Exception{
		Fingerprinter fingerprinter = new Fingerprinter();
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		List l=FingerprinterTool.listDifferences(bs1, bs);
		this.assertEquals(l.size(),19);
	}
}

