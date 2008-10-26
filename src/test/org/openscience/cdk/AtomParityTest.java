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
import org.openscience.cdk.AtomParity;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Checks the functionality of the AtomParity class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.AtomParity
 */
<<<<<<< HEAD:src/test/org/openscience/cdk/AtomParityTest.java
public class AtomParityTest extends ChemObjectTest {
=======
public class AtomParityTest extends CDKTestCase {
>>>>>>> bbc19522071c1b78697779bddcd7509e9314667e:src/test/org/openscience/cdk/AtomParityTest.java

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testAtomParity_IAtom_IAtom_IAtom_IAtom_IAtom_int() {
        IAtom carbon = builder.newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = builder.newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = builder.newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = builder.newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = builder.newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Assert.assertNotNull(parity);
    }
    
    @Test public void testGetAtom() {
        IAtom carbon = builder.newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = builder.newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = builder.newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = builder.newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = builder.newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Assert.assertEquals(carbon, parity.getAtom());
    }
    
    @Test public void testGetSurroundingAtoms() {
        IAtom carbon = builder.newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = builder.newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = builder.newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = builder.newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = builder.newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        org.openscience.cdk.interfaces.IAtom[] neighbors = parity.getSurroundingAtoms();
        Assert.assertEquals(4, neighbors.length);
        Assert.assertEquals(carbon1, neighbors[0]);
        Assert.assertEquals(carbon2, neighbors[1]);
        Assert.assertEquals(carbon3, neighbors[2]);
        Assert.assertEquals(carbon4, neighbors[3]);
    }
    
    @Test public void testGetParity() {
        IAtom carbon = builder.newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = builder.newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = builder.newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = builder.newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = builder.newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Assert.assertEquals(parityInt, parity.getParity());
    }
    
    /** Test for RFC #9 */
    @Test public void testToString() {
        IAtom carbon = builder.newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = builder.newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = builder.newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = builder.newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = builder.newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        String description = parity.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

	@Test public void testClone() throws Exception {
        IAtom carbon = builder.newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = builder.newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = builder.newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = builder.newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = builder.newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Object clone = parity.clone();
        Assert.assertTrue(clone instanceof AtomParity);
    }    
        
    @Test public void testClone_SurroundingAtoms() throws Exception {
        IAtom carbon = builder.newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = builder.newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = builder.newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = builder.newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = builder.newAtom("C");
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
        IAtom carbon = builder.newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = builder.newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = builder.newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = builder.newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = builder.newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);

		AtomParity clone = (AtomParity)parity.clone();
        Assert.assertNotSame(parity.getAtom(), clone.getAtom());
    }
}
