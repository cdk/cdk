/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.SetOfAtomContainers;
import org.openscience.cdk.event.ChemObjectChangeEvent;
import org.openscience.cdk.ChemObjectListener;

/**
 * Checks the funcitonality of the SetOfMolecules class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.SetOfMolecules
 */
public class SetOfAtomContainersTest extends TestCase {

    public SetOfAtomContainersTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(SetOfAtomContainersTest.class);
    }
    
    public void testSetOfAtomContainers() {
        SetOfAtomContainers som = new SetOfAtomContainers();
        assertNotNull(som);
        assertEquals(0, som.getAtomContainerCount());
    }
    
    public void testGetAtomContainerCount() {
        SetOfAtomContainers som = new SetOfAtomContainers();
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        
        assertEquals(3, som.getAtomContainerCount());
    }
    
    public void testGetAtomContainer_int() {
        SetOfAtomContainers som = new SetOfAtomContainers();
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());

        assertNotNull(som.getAtomContainer(2)); // third molecule should exist
        assertNull(som.getAtomContainer(3)); // fourth molecule must not exist
    }
    
    public void testGetMultiplier_int() {
        SetOfAtomContainers som = new SetOfAtomContainers();
        som.addAtomContainer(new AtomContainer());

        assertEquals(1.0, som.getMultiplier(0), 0.00001);
    }
    
    public void testGetMultipliers() {
        SetOfAtomContainers som = new SetOfAtomContainers();
        som.addAtomContainer(new AtomContainer(), 1.0);

	double[] multipliers = som.getMultipliers();
	assertNotNull(multipliers);
        assertEquals(1, multipliers.length);
    }
        
    public void testGetMultiplier_AtomContainer() {
        SetOfAtomContainers som = new SetOfAtomContainers();
        som.addAtomContainer(new AtomContainer());

        assertEquals(-1.0, som.getMultiplier(new AtomContainer()), 0.00001);
    }
    
    public void testAddAtomContainer_AtomContainer() {
        SetOfAtomContainers som = new SetOfAtomContainers();
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());

        assertEquals(5, som.getAtomContainerCount());
        
        // now test it to make sure it properly grows the array
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());

        assertEquals(7, som.getAtomContainerCount());        
    }
    
    public void testAddAtomContainer_AtomContainer_double() {
        SetOfAtomContainers som = new SetOfAtomContainers();
        som.addAtomContainer(new AtomContainer(), 2.0);
        assertEquals(1, som.getAtomContainerCount());
        assertEquals(2.0, som.getMultiplier(0), 0.00001);
    }
    
    public void testGrowAtomContainerArray() {
        // this test assumes that the growSize = 5 !
        // if not, there is need for the array to grow
        SetOfAtomContainers som = new SetOfAtomContainers();
        
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());

        AtomContainer[] mols = som.getAtomContainers();
        assertEquals(7, mols.length);
    }
    
    public void testGetAtomContainers() {
        SetOfAtomContainers som = new SetOfAtomContainers();
        
        AtomContainer[] mols = som.getAtomContainers();
        assertEquals(0, mols.length);
        
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());
        som.addAtomContainer(new AtomContainer());

        mols = som.getAtomContainers();
        assertEquals(3, mols.length);
        assertNotNull(mols[0]);
        assertNotNull(mols[1]);
        assertNotNull(mols[2]);
    }

    public void testToString() {
        SetOfAtomContainers containerSet = new SetOfAtomContainers();
        String description = containerSet.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testStateChanged_ChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        SetOfAtomContainers chemObject = new SetOfAtomContainers();
        chemObject.addListener(listener);
        
        chemObject.addAtomContainer(new AtomContainer());
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
