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
 * 
 */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.SetOfAtomContainers;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;

/**
 * Checks the funcitonality of the SetOfMolecules class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.SetOfMolecules
 */
public class SetOfAtomContainersTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public SetOfAtomContainersTest(String name) {
        super(name);
    }

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(SetOfAtomContainersTest.class);
    }
    
    public void testSetOfAtomContainers() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        assertNotNull(som);
        assertEquals(0, som.getAtomContainerCount());
    }
    
    public void testGetAtomContainerCount() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        
        assertEquals(3, som.getAtomContainerCount());
    }
    
    public void testAdd_SetOfAtomContainers() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        
        SetOfAtomContainers tested = builder.newSetOfAtomContainers();
        assertEquals(0, tested.getAtomContainerCount());
        tested.add(som);
        assertEquals(3, tested.getAtomContainerCount());
    }

    public void testGetAtomContainer_int() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());

        assertNotNull(som.getAtomContainer(2)); // third molecule should exist
        assertNull(som.getAtomContainer(3)); // fourth molecule must not exist
    }
    
    public void testGetMultiplier_int() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        som.addAtomContainer(builder.newAtomContainer());

        assertEquals(1.0, som.getMultiplier(0), 0.00001);
    }
    
    public void testSetMultiplier_int_double() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        som.addAtomContainer(builder.newAtomContainer());

        assertEquals(1.0, som.getMultiplier(0), 0.00001);
        som.setMultiplier(0, 2.0);
        assertEquals(2.0, som.getMultiplier(0), 0.00001);
    }

    public void testSetMultipliers_arraydouble() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        IAtomContainer container = builder.newAtomContainer();
        som.addAtomContainer(container);
        IAtomContainer container2 = builder.newAtomContainer();
        som.addAtomContainer(container2);

        assertEquals(1.0, som.getMultiplier(0), 0.00001);
        assertEquals(1.0, som.getMultiplier(1), 0.00001);
        double[] multipliers = new double[2];
        multipliers[0] = 2.0;
        multipliers[1] = 3.0;
        som.setMultipliers(multipliers);
        assertEquals(2.0, som.getMultiplier(0), 0.00001);
        assertEquals(3.0, som.getMultiplier(1), 0.00001);
    }

    public void testSetMultiplier_IAtomContainer_double() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        IAtomContainer container = builder.newAtomContainer();
        som.addAtomContainer(container);

        assertEquals(1.0, som.getMultiplier(container), 0.00001);
        som.setMultiplier(container, 2.0);
        assertEquals(2.0, som.getMultiplier(container), 0.00001);
    }

    public void testGetMultipliers() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        som.addAtomContainer(builder.newAtomContainer(), 1.0);
        
        double[] multipliers = som.getMultipliers();
        assertNotNull(multipliers);
        assertEquals(1, multipliers.length);
    }
        
    public void testGetMultiplier_IAtomContainer() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        som.addAtomContainer(builder.newAtomContainer());

        assertEquals(-1.0, som.getMultiplier(builder.newAtomContainer()), 0.00001);
    }
    
    public void testAddAtomContainer_IAtomContainer() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());

        assertEquals(5, som.getAtomContainerCount());
        
        // now test it to make sure it properly grows the array
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());

        assertEquals(7, som.getAtomContainerCount());        
    }
    
    public void testAddAtomContainer_IAtomContainer_double() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        som.addAtomContainer(builder.newAtomContainer(), 2.0);
        assertEquals(1, som.getAtomContainerCount());
        assertEquals(2.0, som.getMultiplier(0), 0.00001);
    }
    
    public void testGrowAtomContainerArray() {
        // this test assumes that the growSize = 5 !
        // if not, there is need for the array to grow
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());

        org.openscience.cdk.interfaces.IAtomContainer[] mols = som.getAtomContainers();
        assertEquals(7, mols.length);
    }
    
    public void testGetAtomContainers() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        
        org.openscience.cdk.interfaces.IAtomContainer[] mols = som.getAtomContainers();
        assertEquals(0, mols.length);
        
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());
        som.addAtomContainer(builder.newAtomContainer());

        mols = som.getAtomContainers();
        assertEquals(3, mols.length);
        assertNotNull(mols[0]);
        assertNotNull(mols[1]);
        assertNotNull(mols[2]);
    }

    public void testToString() {
        SetOfAtomContainers containerSet = builder.newSetOfAtomContainers();
        String description = containerSet.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
    
     public void testClone() {
        SetOfAtomContainers containerSet = builder.newSetOfAtomContainers();
        Object clone = containerSet.clone();
        assertTrue(clone instanceof SetOfAtomContainers);
	assertNotSame(containerSet, clone);
    } 

    public void testStateChanged_ChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        SetOfAtomContainers chemObject = builder.newSetOfAtomContainers();
        chemObject.addListener(listener);
        
        chemObject.addAtomContainer(builder.newAtomContainer());
        assertTrue(listener.changed);
    }

    public void testRemoveAtomContainer_IAtomContainer() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        IAtomContainer ac1 = builder.newAtomContainer();
        IAtomContainer ac2 = builder.newAtomContainer();
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        som.removeAtomContainer(ac1);
        assertEquals(1, som.getAtomContainerCount());
        assertEquals(ac2, som.getAtomContainer(0));
    }
    
    public void testRemoveAllAtomContainers() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        IAtomContainer ac1 = builder.newAtomContainer();
        IAtomContainer ac2 = builder.newAtomContainer();
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        
        assertEquals(2, som.getAtomContainerCount());
        som.removeAllAtomContainers();
        assertEquals(0, som.getAtomContainerCount());
    }
    
    public void testRemoveAtomContainer_int() {
        SetOfAtomContainers som = builder.newSetOfAtomContainers();
        IAtomContainer ac1 = builder.newAtomContainer();
        IAtomContainer ac2 = builder.newAtomContainer();
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        som.removeAtomContainer(0);
        assertEquals(1, som.getAtomContainerCount());
        assertEquals(ac2, som.getAtomContainer(0));
    }
    
    private class ChemObjectListenerImpl implements IChemObjectListener {
        private boolean changed;
        
        private ChemObjectListenerImpl() {
            changed = false;
        }
        
        public void stateChanged(IChemObjectChangeEvent e) {
            changed = true;
        }
        
        public void reset() {
            changed = false;
        }
    }
}
