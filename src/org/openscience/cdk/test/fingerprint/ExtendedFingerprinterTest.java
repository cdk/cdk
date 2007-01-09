/* $Revision: 7657 $ $Author: egonw $ $Date: 2007-01-06 11:30:46 +0100 (Sat, 06 Jan 2007) $    
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

import java.io.IOException;
import java.io.StringReader;
import java.util.BitSet;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.ExtendedFingerprinter;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class ExtendedFingerprinterTest extends CDKTestCase {
	
	public ExtendedFingerprinterTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ExtendedFingerprinterTest.class);
	}

	public void testExtendedFingerprinter() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter();
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		assertTrue(Fingerprinter.isSubset(bs, bs1));
	}
	
    /* ethanolamine */
	private static final String ethanolamine = "\n\n\n  4  3  0     0  0  0  0  0  0  1 V2000\n    2.5187   -0.3500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n    0.0938   -0.3500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n    1.3062    0.3500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -1.1187    0.3500    0.0000 O   0  0  0  0  0  0  0  0  0  0\n  2  3  1  0  0  0  0\n  2  4  1  0  0  0  0\n  1  3  1  0  0  0  0\nM  END\n";

    /* 2,4-diamino-5-hydroxypyrimidin-dihydrochlorid */
	private static final String molecule_test_2 = "\n\n\n 13 11  0     0  0  0  0  0  0  1 V2000\n   -0.5145   -1.0500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -1.7269   -1.7500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n   -2.9393   -1.0500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -2.9393    0.3500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -1.7269    1.0500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -0.5145    0.3500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n   -4.1518    1.0500    0.0000 O   0  0  0  0  0  0  0  0  0  0\n   -4.1518   -1.7500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n    0.6980   -1.7500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n   -4.1518    2.4500    0.0000 H   0  0  0  0  0  1  0  0  0  0\n   -5.3642    3.1500    0.0000 Cl  0  0  0  0  0  0  0  0  0  0\n   -4.1518   -3.1500    0.0000 H   0  0  0  0  0  1  0  0  0  0\n   -5.3642   -3.8500    0.0000 Cl  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0  0  0  0\n  2  3  2  0  0  0  0\n  3  4  1  0  0  0  0\n  4  5  2  0  0  0  0\n  5  6  1  0  0  0  0\n  1  6  2  0  0  0  0\n  4  7  1  0  0  0  0\n  3  8  1  0  0  0  0\n  1  9  1  0  0  0  0\n 10 11  1  0  0  0  0\n 12 13  1  0  0  0  0\nM  END\n";

	/**
	 * This basic test case show that some molecules will not be considered
	 * as subset of each other by Fingerprint.isSubset(), for the getFingerprint(),
	 * despite they are sub graph of each other according to
	 * UniversalIsomorphismTester.isSubgraph().
	 *
	 * @author  Hugo Lafayette <hugo.lafayette@laposte.net>
	 *
	 * @throws  CloneNotSupportedException
	 * @throws  Exception
	 * 
	 * @cdk.bug 1626894
	 */
    public static void testExtendedFingerPrint() throws CloneNotSupportedException, Exception {
    	IFingerprinter fingerprinter = new ExtendedFingerprinter();

    	Molecule mol1 = createMolecule(molecule_test_2);
    	Molecule mol2 = createMolecule(ethanolamine);
    	assertTrue("SubGraph does NOT match", UniversalIsomorphismTester.isSubgraph(mol1, mol2));

    	BitSet bs3 = fingerprinter.getFingerprint((IAtomContainer) mol1.clone());
    	BitSet bs4 = fingerprinter.getFingerprint((IAtomContainer) mol2.clone());

    	assertTrue("Subset (with extended fingerprint) does NOT match", Fingerprinter.isSubset(bs3, bs4));

    }

    private static Molecule createMolecule(String molecule) throws IOException, CDKException {
    	Molecule structure = null;
    	if (molecule != null) {
    		IChemObjectReader reader = new MDLV2000Reader(new StringReader(molecule));
    		assertNotNull("Could not create reader", reader);
    		if (reader.accepts(Molecule.class)) {
    			structure = (Molecule) reader.read(new Molecule());
    		}
    	}
    	return structure;
    }

}

