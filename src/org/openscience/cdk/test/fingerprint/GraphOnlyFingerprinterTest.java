/* $Revision: 7674 $ $Author: egonw $ $Date: 2007-01-09 10:52:27 +0100 (Tue, 09 Jan 2007) $    
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.FingerprinterTool;
import org.openscience.cdk.fingerprint.GraphOnlyFingerprinter;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.NewCDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

import java.io.IOException;
import java.io.StringReader;
import java.util.BitSet;

/**
 * @cdk.module test-standard
 */
public class GraphOnlyFingerprinterTest extends NewCDKTestCase {

	private static LoggingTool logger = new LoggingTool(FingerprinterTest.class);

	public GraphOnlyFingerprinterTest() {
		super();
	}

    @Test
    public void testFingerprint() throws Exception {
		SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		IFingerprinter printer = new GraphOnlyFingerprinter();
		
		BitSet bs1 = printer.getFingerprint(parser.parseSmiles("C=C-C#N"));
		System.out.println("----");
		BitSet bs2 = printer.getFingerprint(parser.parseSmiles("CCCN"));
		
		Assert.assertEquals(bs1, bs2);
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
	 * 
	 * @see testExtendedFingerPrint()
	 */
    @Test
    public static void testFingerPrint() throws Exception {
    	IFingerprinter printer = new GraphOnlyFingerprinter();

    	Molecule mol1 = createMolecule(molecule_test_2);
    	Molecule mol2 = createMolecule(ethanolamine);
    	Assert.assertTrue("SubGraph does NOT match", UniversalIsomorphismTester.isSubgraph(mol1, mol2));

    	BitSet bs1 = printer.getFingerprint((IAtomContainer) mol1.clone());
    	BitSet bs2 = printer.getFingerprint((IAtomContainer) mol2.clone());

    	Assert.assertTrue("Subset (with fingerprint) does NOT match", FingerprinterTool.isSubset(bs1, bs2));

    	// Match OK
    	logger.debug("Subset (with fingerprint) does match");
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

