/* $RCSfile$
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
import java.util.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * Checks the funcitonality of the SetOfMolecules class.
 *
 * @see org.openscience.cdk.SetOfMolecules
 */
public class SetOfMoleculesTest extends TestCase {

    public SetOfMoleculesTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(SetOfMoleculesTest.class);
    }
    
    public void testGetMoleculeCount() {
        SetOfMolecules som = new SetOfMolecules();
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        
        assertEquals(3, som.getMoleculeCount());
    }
    
    public void testGetMolecule() {
        SetOfMolecules som = new SetOfMolecules();
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());

        assertNotNull(som.getMolecule(2)); // third molecule should exist
        assertNull(som.getMolecule(3)); // fourth molecule must not exist
    }
    
    public void testAddMolecule() {
        SetOfMolecules som = new SetOfMolecules();
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());

        assertEquals(5, som.getMoleculeCount());
        
        // now test it to make sure it properly grows the array
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());

        assertEquals(7, som.getMoleculeCount());        
    }
    
    public void testGrowMoleculeArray() {
        // this test assumes that the growSize = 5 !
        // if not, there is need for the array to grow
        SetOfMolecules som = new SetOfMolecules();
        
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());

        Molecule[] mols = som.getMolecules();
        assertEquals(7, mols.length);
    }
    
    public void testGetMolecules() {
        SetOfMolecules som = new SetOfMolecules();
        
        Molecule[] mols = som.getMolecules();
        assertEquals(0, mols.length);
        
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());

        mols = som.getMolecules();
        assertEquals(3, mols.length);
        assertNotNull(mols[0]);
        assertNotNull(mols[1]);
        assertNotNull(mols[2]);
    }
}
