/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.ChemObjectBuilder;

/**
 * Checks the funcitonality of the Molecule class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.Molecule
 */
public class MoleculeTest extends CDKTestCase {

	protected ChemObjectBuilder builder;
	
    public MoleculeTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(MoleculeTest.class);
    }
    
    // test constructors
    
    public void testMolecule() {
        Molecule m = builder.newMolecule();
        assertTrue(m != null);
    }

    public void testMolecule_int_int() {
        Molecule m = builder.newMolecule(5,5);
        assertTrue(m != null);
        assertEquals(0, m.getAtoms().length);
        assertEquals(0, m.getElectronContainers().length);
    }

    public void testMolecule_IAtomContainer() {
        IAtomContainer acetone = new org.openscience.cdk.AtomContainer();
        IAtom c1 = builder.newAtom("C");
        IAtom c2 = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        IAtom c3 = builder.newAtom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = builder.newBond(c1, c2,1);
        Bond b2 = builder.newBond(c1, o, 2);
        Bond b3 = builder.newBond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Molecule m = builder.newMolecule(acetone);
        assertTrue(m != null);
        assertEquals(4, m.getAtomCount());
        assertEquals(3, m.getBondCount());
    }

	public void testClone() {
        Molecule molecule = builder.newMolecule();
        Object clone = molecule.clone();
        assertTrue(clone instanceof Molecule);
	assertNotSame(molecule, clone);
    }    

    /** Test for RFC #9 */
    public void testToString() {
        Molecule m = builder.newMolecule();
        String description = m.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
