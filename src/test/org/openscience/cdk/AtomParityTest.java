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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.AbstractAtomParityTest;

/**
 * Checks the functionality of the AtomParity class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.AtomParity
 */
public class AtomParityTest extends AbstractAtomParityTest {

    @BeforeClass public static void setUp() {
        setBuilder(DefaultChemObjectBuilder.getInstance());
    }

    @Test public void testAtomParity_IAtom_IAtom_IAtom_IAtom_IAtom_int() {
        IAtom carbon = getBuilder().newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = getBuilder().newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = getBuilder().newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = getBuilder().newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = getBuilder().newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Assert.assertNotNull(parity);
    }
    
	@Test public void testClone() throws Exception {
        IAtom carbon = getBuilder().newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = getBuilder().newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = getBuilder().newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = getBuilder().newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = getBuilder().newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Object clone = parity.clone();
        Assert.assertTrue(clone instanceof AtomParity);
    }    

    @Test public void testClone_SurroundingAtoms() throws Exception {
        IAtom carbon = getBuilder().newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = getBuilder().newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = getBuilder().newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = getBuilder().newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = getBuilder().newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);

		AtomParity clone = (AtomParity)parity.clone();
		org.openscience.cdk.interfaces.IAtom[] atoms = parity.getSurroundingAtoms();
		org.openscience.cdk.interfaces.IAtom[] atomsClone = clone.getSurroundingAtoms();
        Assert.assertEquals(atoms.length, atomsClone.length);
		for (int f = 0; f < atoms.length; f++) {
			for (int g = 0; g < atomsClone.length; g++) {
				Assert.assertNotNull(atoms[f]);
				Assert.assertNotNull(atomsClone[g]);
				Assert.assertNotSame(atoms[f], atomsClone[g]);
			}
		}        
    }
    
    @Test public void testClone_IAtom() throws Exception {
        IAtom carbon = getBuilder().newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = getBuilder().newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = getBuilder().newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = getBuilder().newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = getBuilder().newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);

		AtomParity clone = (AtomParity)parity.clone();
        Assert.assertNotSame(parity.getAtom(), clone.getAtom());
    }
}
