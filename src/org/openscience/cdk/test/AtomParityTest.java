/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomParity;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Checks the functionality of the AtomParity class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.AtomParity
 */
public class AtomParityTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public AtomParityTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(AtomParityTest.class);
    }
    
    public void testAtomParity_IAtom_IAtom_IAtom_IAtom_IAtom_int() {
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
        assertNotNull(parity);
    }
    
    public void testGetAtom() {
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
        assertEquals(carbon, parity.getAtom());
    }
    
    public void testGetSurroundingAtoms() {
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
        assertEquals(4, neighbors.length);
        assertEquals(carbon1, neighbors[0]);
        assertEquals(carbon2, neighbors[1]);
        assertEquals(carbon3, neighbors[2]);
        assertEquals(carbon4, neighbors[3]);
    }
    
    public void testGetParity() {
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
        assertEquals(parityInt, parity.getParity());
    }
    
    /** Test for RFC #9 */
    public void testToString() {
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
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

	public void testClone() {
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
        assertTrue(clone instanceof AtomParity);
    }    
        
    public void testClone_SurroundingAtoms() {
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
        assertEquals(atoms.length, atomsClone.length);
		for (int f = 0; f < atoms.length; f++) {
			for (int g = 0; g < atomsClone.length; g++) {
				assertNotNull(atoms[f]);
				assertNotNull(atomsClone[g]);
				assertNotSame(atoms[f], atomsClone[g]);
			}
		}        
    }
    
    public void testClone_IAtom() {
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
        assertNotSame(parity.getAtom(), clone.getAtom());
    }
}
