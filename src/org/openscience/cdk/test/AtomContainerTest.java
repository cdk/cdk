/*
 * $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.templates.MoleculeFactory;
import java.util.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * Checks the funcitonality of the AtomTypeFactory
 */
 
public class AtomContainerTest extends TestCase {

    public AtomContainerTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(AtomContainerTest.class);
    }

    public void testSetAtoms() {
        Atom[] atoms = new Atom[4];
        atoms[0] = new Atom("C");
        atoms[1] = new Atom("C");
        atoms[2] = new Atom("C");
        atoms[3] = new Atom("O");
        AtomContainer ac = new AtomContainer();
        ac.setAtoms(atoms);
        
        assertEquals(4, ac.getAtomCount());
    }

    /**
     * Only test wether the atoms are correctly cloned.
     */
	public void testClone() {
		Molecule molecule = MoleculeFactory.makeAlphaPinene();
		Molecule clonedMol = (Molecule)molecule.clone();
		assertTrue(molecule.getAtomCount() == clonedMol.getAtomCount());
		for (int f = 0; f < molecule.getAtomCount(); f++) {
			for (int g = 0; g < clonedMol.getAtomCount(); g++) {
				assertNotNull(molecule.getAtomAt(f));
				assertNotNull(clonedMol.getAtomAt(g));
				assertTrue(molecule.getAtomAt(f) != clonedMol.getAtomAt(g));
			}
		}
	}

}
