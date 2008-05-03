/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 2006-2007  Egon Willighagen <ewilligh@uni-koeln.de>
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
package org.openscience.cdk.graph.invariant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.graph.invariant.CanonicalLabeler;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.InvPair;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.NewCDKTestCase;

import java.util.Iterator;

/**
 * Checks the functionality of the CanonicalLabeler.
 *
 * @cdk.module test-standard
 */
public class CanonicalLabelerTest extends NewCDKTestCase {
	
	private SmilesParser parser;
	private CanonicalLabeler labeler;
	
	public CanonicalLabelerTest() {
		super();
	}

    @Before
    public void setUp() {
		parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		labeler = new CanonicalLabeler();
	}


	@Test
	public void testCanonicalLabeler() {
		// assume setup worked
		Assert.assertNotNull(labeler);
	}

    @Test
    public void testCanonLabel_IAtomContainer() throws Exception {
		IMolecule molecule = parser.parseSmiles("CC(=O)CBr");
		labeler.canonLabel(molecule);
		Iterator atoms = molecule.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			Assert.assertNotNull(atom.getProperty(InvPair.CANONICAL_LABEL));
		}
		Assert.assertEquals(4, ((Long)molecule.getAtom(0).getProperty(InvPair.CANONICAL_LABEL)).intValue());
		Assert.assertEquals(3, ((Long)molecule.getAtom(1).getProperty(InvPair.CANONICAL_LABEL)).intValue());
		Assert.assertEquals(1, ((Long)molecule.getAtom(2).getProperty(InvPair.CANONICAL_LABEL)).intValue());
		Assert.assertEquals(5, ((Long)molecule.getAtom(3).getProperty(InvPair.CANONICAL_LABEL)).intValue());
		Assert.assertEquals(2, ((Long)molecule.getAtom(4).getProperty(InvPair.CANONICAL_LABEL)).intValue());
	}

	/**
	 * Ordering of original should not matter, so the same SMILES
	 * with a different atom order as the test above.
	 * 
	 * @throws Exception
	 * @see testSomeMolecule()
	 */
    @Test
    public void testSomeMoleculeWithDifferentStartingOrder() throws Exception {
		IMolecule molecule = parser.parseSmiles("O=C(C)CBr");
		labeler.canonLabel(molecule);
		Iterator atoms = molecule.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			Assert.assertNotNull(atom.getProperty(InvPair.CANONICAL_LABEL));
		}
		Assert.assertEquals(1, ((Long)molecule.getAtom(0).getProperty(InvPair.CANONICAL_LABEL)).intValue());
		Assert.assertEquals(3, ((Long)molecule.getAtom(1).getProperty(InvPair.CANONICAL_LABEL)).intValue());
		Assert.assertEquals(4, ((Long)molecule.getAtom(2).getProperty(InvPair.CANONICAL_LABEL)).intValue());
		Assert.assertEquals(5, ((Long)molecule.getAtom(3).getProperty(InvPair.CANONICAL_LABEL)).intValue());
		Assert.assertEquals(2, ((Long)molecule.getAtom(4).getProperty(InvPair.CANONICAL_LABEL)).intValue());
	}
}
