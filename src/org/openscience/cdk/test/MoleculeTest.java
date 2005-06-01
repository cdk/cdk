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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.*;

/**
 * Checks the funcitonality of the Molecule class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.Molecule
 */
public class MoleculeTest extends CDKTestCase {

    public MoleculeTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(MoleculeTest.class);
    }
    
    // test constructors
    
    public void testMolecule() {
        Molecule m = new Molecule();
        assertTrue(m != null);
    }

    public void testMolecule_int_int() {
        Molecule m = new Molecule(5,5);
        assertTrue(m != null);
        assertEquals(0, m.getAtoms().length);
        assertEquals(0, m.getElectronContainers().length);
    }

    public void testMolecule_AtomContainer() {
        AtomContainer acetone = new AtomContainer();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Molecule m = new Molecule(acetone);
        assertTrue(m != null);
        assertEquals(4, m.getAtomCount());
        assertEquals(3, m.getBondCount());
    }

	public void testClone() {
        Molecule molecule = new Molecule();
        Object clone = molecule.clone();
        assertTrue(clone instanceof Molecule);
	assertNotSame(molecule, clone);
    }    

    /** Test for RFC #9 */
    public void testToString() {
        Molecule m = new Molecule();
        String description = m.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
