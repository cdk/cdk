/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.event.ChemObjectChangeEvent;
import org.openscience.cdk.ChemObjectListener;

/**
 * Checks the funcitonality of the SetOfMolecules class.
 *
 * @cdk.module test
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
    
    public void testGetMolecule_int() {
        SetOfMolecules som = new SetOfMolecules();
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());

        assertNotNull(som.getMolecule(2)); // third molecule should exist
        assertNull(som.getMolecule(3)); // fourth molecule must not exist
    }
    
    public void testAddMolecule_Molecule() {
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
    
    public void testAdd_SetOfMolecules() {
        SetOfMolecules som = new SetOfMolecules();
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());
        som.addMolecule(new Molecule());

	SetOfMolecules som2 = new SetOfMolecules();
	som2.add(som);
	
        assertEquals(5, som2.getMoleculeCount());
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
    
    public void testSetOfMolecules() {
        SetOfMolecules som = new SetOfMolecules();
	assertNotNull(som);
        assertEquals(0, som.getMoleculeCount());
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

    public void testToString() {
        SetOfMolecules som = new SetOfMolecules();
        String description = som.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testStateChanged_ChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        SetOfMolecules chemObject = new SetOfMolecules();
        chemObject.addListener(listener);
        
        chemObject.addMolecule(new Molecule());
        assertTrue(listener.changed);
    }

    private class ChemObjectListenerImpl implements ChemObjectListener {
        private boolean changed;
        
        private ChemObjectListenerImpl() {
            changed = false;
        }
        
        public void stateChanged(ChemObjectChangeEvent e) {
            changed = true;
        }
        
        public void reset() {
            changed = false;
        }
    }

}
