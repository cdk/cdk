/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2008  The Chemistry Development Kit (CDK) project
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

import java.util.Iterator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;

/**
 * Checks the functionality of the MoleculeSet class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.MoleculeSet
 */
public class MoleculeSetTest extends AtomContainerTest {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testGetMoleculeCount() {
        IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        
        Assert.assertEquals(3, som.getMoleculeCount());
    }
    
    @Test public void testGetMolecule_int() {
        IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        Assert.assertNotNull(som.getMolecule(2)); // third molecule should exist
        Assert.assertNull(som.getMolecule(3)); // fourth molecule must not exist
    }
    
    @Test public void testAddMolecule_IMolecule() {
        IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        Assert.assertEquals(5, som.getMoleculeCount());
        
        // now test it to make sure it properly grows the array
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        Assert.assertEquals(7, som.getMoleculeCount());        
    }
    
    @Test public void testAdd_IMoleculeSet() {
        IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        IMoleculeSet som2 = builder.newMoleculeSet();
        som2.add(som);
        
        Assert.assertEquals(5, som2.getMoleculeCount());
    }
    
    @Test public void testSetMolecules_arrayIMolecule() {
        IMoleculeSet som = builder.newMoleculeSet();
        
        IMolecule[] set = new IMolecule[5];
        set[0] = builder.newMolecule();
        set[1] = builder.newMolecule();
        set[2] = builder.newMolecule();
        set[3] = builder.newMolecule();
        set[4] = builder.newMolecule();
        
        Assert.assertEquals(0, som.getMoleculeCount());
        som.setMolecules(set);
        Assert.assertEquals(5, som.getMoleculeCount());
    }
    
    @Test public void testMolecules() {
    	IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        Assert.assertEquals(3, som.getMoleculeCount());
        Iterator<IAtomContainer> mols = som.molecules().iterator();
        int count = 0;
        while (mols.hasNext()) {
        	count++;
        	mols.next();
        }
        Assert.assertEquals(3, count);
        mols = som.molecules().iterator();
        while (mols.hasNext()) {
        	mols.next();
        	mols.remove();
        }
        Assert.assertEquals(0, som.getMoleculeCount());
    }
    
    @Test public void testGrowMoleculeArray() {
        // this test assumes that the growSize = 5 !
        // if not, there is need for the array to grow
        IMoleculeSet som = builder.newMoleculeSet();
        
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        Assert.assertEquals(7, som.getAtomContainerCount());
    }
    
    @Test public void testMoleculeSet() {
        IMoleculeSet som = builder.newMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(0, som.getMoleculeCount());
    }
    
    @Test public void testGetMolecules() {
        IMoleculeSet som = builder.newMoleculeSet();
        
        Assert.assertEquals(0, som.getAtomContainerCount());
        
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        Assert.assertEquals(3, som.getAtomContainerCount());
        Assert.assertNotNull(som.getMolecule(0));
        Assert.assertNotNull(som.getMolecule(1));
        Assert.assertNotNull(som.getMolecule(2));
    }

    @Test public void testToString() {
        IMoleculeSet som = builder.newMoleculeSet();
        String description = som.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test public void testClone() throws Exception {
        IMoleculeSet som = builder.newMoleculeSet();
        Object clone = som.clone();
        Assert.assertTrue(clone instanceof IMoleculeSet);
	Assert.assertNotSame(som, clone);
    }
    
    @Test public void testCloneDuplication() throws Exception {
        IMoleculeSet moleculeSet = builder.newMoleculeSet();
        moleculeSet.addMolecule(builder.newMolecule());
        Object clone = moleculeSet.clone();
        Assert.assertTrue(clone instanceof IMoleculeSet);
        IMoleculeSet clonedSet = (IMoleculeSet)clone;
        Assert.assertNotSame(moleculeSet, clonedSet);
        Assert.assertEquals(moleculeSet.getMoleculeCount(), clonedSet.getMoleculeCount());
    } 

    @Test public void testCloneMultiplier() throws Exception {
        IMoleculeSet moleculeSet = builder.newMoleculeSet();
        moleculeSet.addMolecule(builder.newMolecule());
        moleculeSet.setMultiplier(moleculeSet.getMolecule(0), 2.0);
        Object clone = moleculeSet.clone();
        Assert.assertTrue(clone instanceof IMoleculeSet);
        IMoleculeSet clonedSet = (IMoleculeSet)clone;
        Assert.assertNotSame(moleculeSet, clonedSet);
        Assert.assertEquals(2, moleculeSet.getMultiplier(0).intValue());
        Assert.assertEquals(2, clonedSet.getMultiplier(0).intValue());
    }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IMoleculeSet chemObject = builder.newMoleculeSet();
        chemObject.addListener(listener);
        
        chemObject.addMolecule(builder.newMolecule());
        Assert.assertTrue(listener.changed);
    }

    private class ChemObjectListenerImpl implements IChemObjectListener {
        private boolean changed;
        
        private ChemObjectListenerImpl() {
            changed = false;
        }
        
        @Test public void stateChanged(IChemObjectChangeEvent e) {
            changed = true;
        }
        
        @Test public void reset() {
            changed = false;
        }
    }

}
