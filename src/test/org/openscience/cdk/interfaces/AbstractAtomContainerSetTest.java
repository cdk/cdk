/* $Revision$ $Author$ $Date$    
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
package org.openscience.cdk.interfaces;

import java.util.Comparator;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IAtomContainerSet} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractAtomContainerSetTest extends AbstractChemObjectTest {

    @Test public void testGetAtomContainerCount() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        
        Assert.assertEquals(3, som.getAtomContainerCount());
    }
    
    @Test public void testAtomContainers() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        
        Assert.assertEquals(3, som.getAtomContainerCount());
        Iterator<IAtomContainer> iter = som.atomContainers().iterator();
        int count = 0;
        while (iter.hasNext()) {
        	iter.next();
        	++count;
        	iter.remove();
        }
        Assert.assertEquals(0, som.getAtomContainerCount());
        Assert.assertEquals(3, count);
        Assert.assertFalse(iter.hasNext());
    }
    
    @Test public void testAdd_IAtomContainerSet() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        
        IAtomContainerSet tested = som.getBuilder().newInstance(IAtomContainerSet.class);
        Assert.assertEquals(0, tested.getAtomContainerCount());
        tested.add(som);
        Assert.assertEquals(3, tested.getAtomContainerCount());
    }

    @Test public void testGetAtomContainer_int() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));

        Assert.assertNotNull(som.getAtomContainer(2)); // third molecule should exist
        Assert.assertNull(som.getAtomContainer(3)); // fourth molecule must not exist
    }
    
    @Test public void testGetMultiplier_int() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));

        Assert.assertEquals(1.0, som.getMultiplier(0), 0.00001);
    }
    
    @Test public void testSetMultiplier_int_Double() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));

        Assert.assertEquals(1.0, som.getMultiplier(0), 0.00001);
        som.setMultiplier(0, 2.0);
        Assert.assertEquals(2.0, som.getMultiplier(0), 0.00001);
    }

    @Test public void testSetMultipliers_arrayDouble() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer container = som.getBuilder().newInstance(IAtomContainer.class);
        som.addAtomContainer(container);
        IAtomContainer container2 = som.getBuilder().newInstance(IAtomContainer.class);
        som.addAtomContainer(container2);

        Assert.assertEquals(1.0, som.getMultiplier(0), 0.00001);
        Assert.assertEquals(1.0, som.getMultiplier(1), 0.00001);
        Double[] multipliers = new Double[2];
        multipliers[0] = 2.0;
        multipliers[1] = 3.0;
        som.setMultipliers(multipliers);
        Assert.assertEquals(2.0, som.getMultiplier(0), 0.00001);
        Assert.assertEquals(3.0, som.getMultiplier(1), 0.00001);
    }

    @Test public void testSetMultiplier_IAtomContainer_Double() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer container = som.getBuilder().newInstance(IAtomContainer.class);
        som.addAtomContainer(container);

        Assert.assertEquals(1.0, som.getMultiplier(container), 0.00001);
        som.setMultiplier(container, 2.0);
        Assert.assertEquals(2.0, som.getMultiplier(container), 0.00001);
    }

    @Test public void testGetMultipliers() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class), 1.0);
        
        Double[] multipliers = som.getMultipliers();
        Assert.assertNotNull(multipliers);
        Assert.assertEquals(1, multipliers.length);
    }
        
    @Test public void testGetMultiplier_IAtomContainer() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));

        Assert.assertEquals(-1.0, som.getMultiplier(som.getBuilder().newInstance(IAtomContainer.class)), 0.00001);
    }
    
    @Test public void testAddAtomContainer_IAtomContainer() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));

        Assert.assertEquals(5, som.getAtomContainerCount());
        
        // now test it to make sure it properly grows the array
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));

        Assert.assertEquals(7, som.getAtomContainerCount());        
    }
    
    @Test public void testAddAtomContainer_IAtomContainer_double() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class), 2.0);
        Assert.assertEquals(1, som.getAtomContainerCount());
        Assert.assertEquals(2.0, som.getMultiplier(0), 0.00001);
    }
    
    @Test public void testGrowAtomContainerArray() {
        // this test assumes that the growSize = 5 !
        // if not, there is need for the array to grow
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));

        Assert.assertEquals(7, som.getAtomContainerCount());
    }
    
    @Test public void testGetAtomContainers() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        
        Assert.assertEquals(0, som.getAtomContainerCount());
        
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));
        som.addAtomContainer(som.getBuilder().newInstance(IAtomContainer.class));

        Assert.assertEquals(3, som.getAtomContainerCount());
        Assert.assertNotNull(som.getAtomContainer(0));
        Assert.assertNotNull(som.getAtomContainer(1));
        Assert.assertNotNull(som.getAtomContainer(2));
    }

    @Test public void testToString() {
        IAtomContainerSet containerSet = (IAtomContainerSet)newChemObject();
        String description = containerSet.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
    
     @Test public void testClone() throws Exception {
        IAtomContainerSet containerSet = (IAtomContainerSet)newChemObject();
        Object clone = containerSet.clone();
        Assert.assertTrue(clone instanceof IAtomContainerSet);
	Assert.assertNotSame(containerSet, clone);
    } 

     @Test public void testCloneDuplication() throws Exception {
         IAtomContainerSet containerSet = (IAtomContainerSet)newChemObject();
         containerSet.addAtomContainer(containerSet.getBuilder().newInstance(IAtomContainer.class));
         Object clone = containerSet.clone();
         Assert.assertTrue(clone instanceof IAtomContainerSet);
         IAtomContainerSet clonedSet = (IAtomContainerSet)clone;
         Assert.assertNotSame(containerSet, clonedSet);
         Assert.assertEquals(containerSet.getAtomContainerCount(), clonedSet.getAtomContainerCount());
     }

     @Test public void testCloneMultiplier() throws Exception {
         IAtomContainerSet containerSet = (IAtomContainerSet)newChemObject();
         containerSet.addAtomContainer(containerSet.getBuilder().newInstance(IAtomContainer.class),2);
         Object clone = containerSet.clone();
         Assert.assertTrue(clone instanceof IAtomContainerSet);
         IAtomContainerSet clonedSet = (IAtomContainerSet)clone;
         Assert.assertNotSame(containerSet, clonedSet);
         Assert.assertEquals(2, containerSet.getMultiplier(0).intValue());
         Assert.assertEquals(2, clonedSet.getMultiplier(0).intValue());
     }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IAtomContainerSet chemObject = (IAtomContainerSet)newChemObject();
        chemObject.addListener(listener);
        
        chemObject.addAtomContainer(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assert.assertTrue(listener.changed);
    }

    @Test public void testRemoveAtomContainer_IAtomContainer() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IAtomContainer.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        som.removeAtomContainer(ac1);
        Assert.assertEquals(1, som.getAtomContainerCount());
        Assert.assertEquals(ac2, som.getAtomContainer(0));
    }
    
    @Test public void testRemoveAllAtomContainers() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IAtomContainer.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        
        Assert.assertEquals(2, som.getAtomContainerCount());
        som.removeAllAtomContainers();
        Assert.assertEquals(0, som.getAtomContainerCount());
    }
    
    @Test public void testRemoveAtomContainer_int() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IAtomContainer.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        som.removeAtomContainer(0);
        Assert.assertEquals(1, som.getAtomContainerCount());
        Assert.assertEquals(ac2, som.getAtomContainer(0));
    }
    
    /*
     * @cdk.bug 2679343
     */
    @Test public void testBug2679343() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IAtomContainer.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        som.addAtomContainer(ac2);
        Assert.assertEquals(3, som.getAtomContainerCount());
        som.removeAtomContainer(ac2);
        Assert.assertEquals(1, som.getAtomContainerCount());
    }

    @Test public void testReplaceAtomContainer_int_IAtomContainer() {
    	IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer ac3 = som.getBuilder().newInstance(IAtomContainer.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        Assert.assertEquals(ac2, som.getAtomContainer(1));
        som.replaceAtomContainer(1, ac3);
        Assert.assertEquals(ac3, som.getAtomContainer(1));
    }

    @Test
    public void testSortAtomContainers_Comparator() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IAtomContainer.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        som.sortAtomContainers(new Comparator<IAtomContainer>() {
            @Override
            public int compare(IAtomContainer o1, IAtomContainer o2) {
                return 0;
            }
        });
        Assert.assertEquals(2, som.getAtomContainerCount());
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
