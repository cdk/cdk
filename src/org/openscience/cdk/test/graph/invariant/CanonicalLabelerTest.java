/* $Revision: 7593 $ $Author: egonw $ $Date: 2006-12-29 16:23:19 +0100 (Fri, 29 Dec 2006) $    
 * 
 * Copyright (C) 2006  Egon Willighagen <ewilligh@uni-koeln.de>
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
 * 
 */
package org.openscience.cdk.test.graph.invariant;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.graph.invariant.CanonicalLabeler;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.InvPair;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the CanonicalLabeler.
 *
 * @cdk.module test-standard
 */
public class CanonicalLabelerTest extends CDKTestCase {
	
	private SmilesParser parser;
	private CanonicalLabeler labeler;
	
	public CanonicalLabelerTest(String name) {
		super(name);
	}
	
	public void setUp() {
		parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		labeler = new CanonicalLabeler();
	}

	public static Test suite() {
		return new TestSuite(CanonicalLabelerTest.class);
	}
	
	public void testSomeMolecule() throws Exception {
		IMolecule molecule = parser.parseSmiles("CC(=O)CBr");
		labeler.canonLabel(molecule);
		Iterator atoms = molecule.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			assertNotNull(atom.getProperty("CanonicalLable"));
		}
		assertEquals(4, ((Long)molecule.getAtom(0).getProperty("CanonicalLable")).intValue());
		assertEquals(3, ((Long)molecule.getAtom(1).getProperty("CanonicalLable")).intValue());
		assertEquals(1, ((Long)molecule.getAtom(2).getProperty("CanonicalLable")).intValue());
		assertEquals(5, ((Long)molecule.getAtom(3).getProperty("CanonicalLable")).intValue());
		assertEquals(2, ((Long)molecule.getAtom(4).getProperty("CanonicalLable")).intValue());
	}

	/**
	 * Ordering of original should not matter, so the same SMILES
	 * with a different atom order as the test above.
	 * 
	 * @throws Exception
	 * @see testSomeMolecule()
	 */
	public void testSomeMoleculeWithDifferentStartingOrder() throws Exception {
		IMolecule molecule = parser.parseSmiles("O=C(C)CBr");
		labeler.canonLabel(molecule);
		Iterator atoms = molecule.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			assertNotNull(atom.getProperty("CanonicalLable"));
		}
		assertEquals(1, ((Long)molecule.getAtom(0).getProperty("CanonicalLable")).intValue());
		assertEquals(3, ((Long)molecule.getAtom(1).getProperty("CanonicalLable")).intValue());
		assertEquals(4, ((Long)molecule.getAtom(2).getProperty("CanonicalLable")).intValue());
		assertEquals(5, ((Long)molecule.getAtom(3).getProperty("CanonicalLable")).intValue());
		assertEquals(2, ((Long)molecule.getAtom(4).getProperty("CanonicalLable")).intValue());
	}
}
