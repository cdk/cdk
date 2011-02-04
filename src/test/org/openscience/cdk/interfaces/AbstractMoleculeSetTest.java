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
package org.openscience.cdk.interfaces;

import java.util.Comparator;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IMoleculeSet} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractMoleculeSetTest extends AbstractAtomContainerSetTest {

    @Test public void testGetMoleculeCount() {
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        
        Assert.assertEquals(3, som.getMoleculeCount());
    }
    
    @Test public void testGetMolecule_int() {
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertNotNull(som.getMolecule(2)); // third molecule should exist
        Assert.assertNull(som.getMolecule(3)); // fourth molecule must not exist
    }
    
    @Test public void testAddMolecule_IMolecule() {
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(5, som.getMoleculeCount());
        
        // now test it to make sure it properly grows the array
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(7, som.getMoleculeCount());        
    }
    
    @Test public void testAdd_IMoleculeSet() {
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));

        IMoleculeSet som2 = (IMoleculeSet)newChemObject();
        som2.add(som);
        
        Assert.assertEquals(5, som2.getMoleculeCount());
    }
    
    @Test public void testSetMolecules_arrayIMolecule() {
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        
        IMolecule[] set = new IMolecule[5];
        set[0] = som.getBuilder().newInstance(IMolecule.class);
        set[1] = som.getBuilder().newInstance(IMolecule.class);
        set[2] = som.getBuilder().newInstance(IMolecule.class);
        set[3] = som.getBuilder().newInstance(IMolecule.class);
        set[4] = som.getBuilder().newInstance(IMolecule.class);
        
        Assert.assertEquals(0, som.getMoleculeCount());
        som.setMolecules(set);
        Assert.assertEquals(5, som.getMoleculeCount());
    }
    
    @Test public void testMolecules() {
    	IMoleculeSet som = (IMoleculeSet)newChemObject();
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
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
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(7, som.getAtomContainerCount());
    }
    
    @Test public void testMoleculeSet() {
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        Assert.assertNotNull(som);
        Assert.assertEquals(0, som.getMoleculeCount());
    }
    
    @Test public void testGetMolecules() {
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        
        Assert.assertEquals(0, som.getAtomContainerCount());
        
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));
        som.addMolecule(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(3, som.getAtomContainerCount());
        Assert.assertNotNull(som.getMolecule(0));
        Assert.assertNotNull(som.getMolecule(1));
        Assert.assertNotNull(som.getMolecule(2));
    }

    @Test public void testToString() {
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        String description = som.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test public void testClone() throws Exception {
        IMoleculeSet som = (IMoleculeSet)newChemObject();
        Object clone = som.clone();
        Assert.assertTrue(clone instanceof IMoleculeSet);
	Assert.assertNotSame(som, clone);
    }

    @Test public void testCloneButKeepOriginalIntact() throws CloneNotSupportedException{
        IMoleculeSet moleculeSet = (IMoleculeSet)newChemObject();
        IMolecule mol = moleculeSet.getBuilder().newInstance(IMolecule.class);
        moleculeSet.addAtomContainer(mol);
        //we test that the molecule added is actually in the moleculeSet
        Assert.assertSame(mol, moleculeSet.getAtomContainer(0));
        moleculeSet.clone();
        //after the clone, the molecule added should still be in the moleculeSet
        Assert.assertSame(mol, moleculeSet.getAtomContainer(0));
    }

    @Test public void testCloneDuplication() throws Exception {
        IMoleculeSet moleculeSet = (IMoleculeSet)newChemObject();
        moleculeSet.addMolecule(moleculeSet.getBuilder().newInstance(IMolecule.class));
        Object clone = moleculeSet.clone();
        Assert.assertTrue(clone instanceof IMoleculeSet);
        IMoleculeSet clonedSet = (IMoleculeSet)clone;
        Assert.assertNotSame(moleculeSet, clonedSet);
        Assert.assertEquals(moleculeSet.getMoleculeCount(), clonedSet.getMoleculeCount());
    } 

    @Test public void testCloneMultiplier() throws Exception {
        IMoleculeSet moleculeSet = (IMoleculeSet)newChemObject();
        moleculeSet.addMolecule(moleculeSet.getBuilder().newInstance(IMolecule.class));
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
        IMoleculeSet chemObject = (IMoleculeSet)newChemObject();
        chemObject.addListener(listener);
        
        chemObject.addMolecule(chemObject.getBuilder().newInstance(IMolecule.class));
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

    /**
     * @cdk.bug 2784182
     */
    @Test(expected=IllegalArgumentException.class)
    public void noClassCastException() throws Exception {
        IMoleculeSet set = (IMoleculeSet)newChemObject();
        IAtomContainer container = set.getBuilder().newInstance(IAtomContainer.class);
        set.addAtomContainer(container);
        IMolecule molecule = set.getMolecule(0);
        Assert.assertNotNull(molecule);
    }

    /**
     * @cdk.bug 2784182
     */
    @Test(expected=IllegalArgumentException.class)
    public void noClassCastException2() throws Exception {
        IMoleculeSet set = (IMoleculeSet)newChemObject();
        IAtomContainerSet set2 = set.getBuilder().newInstance(IAtomContainerSet.class);
        IAtomContainer container = set.getBuilder().newInstance(IAtomContainer.class);
        set2.addAtomContainer(container);
        set.add(set2);
    }

    @Override @Test public void testGetAtomContainerCount() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(3, som.getAtomContainerCount());
    }

    @Override @Test public void testAtomContainers() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

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

    @Override @Test public void testAdd_IAtomContainerSet() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        IAtomContainerSet tested = som.getBuilder().newInstance(IAtomContainerSet.class);
        Assert.assertEquals(0, tested.getAtomContainerCount());
        tested.add(som);
        Assert.assertEquals(3, tested.getAtomContainerCount());
    }

    @Override @Test public void testGetAtomContainer_int() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertNotNull(som.getAtomContainer(2)); // third molecule should exist
        Assert.assertNull(som.getAtomContainer(3)); // fourth molecule must not exist
    }

    @Override @Test public void testGetMultiplier_int() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(1.0, som.getMultiplier(0), 0.00001);
    }

    @Override @Test public void testSetMultiplier_int_Double() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(1.0, som.getMultiplier(0), 0.00001);
        som.setMultiplier(0, 2.0);
        Assert.assertEquals(2.0, som.getMultiplier(0), 0.00001);
    }

    @Override @Test public void testSetMultipliers_arrayDouble() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer container = som.getBuilder().newInstance(IMolecule.class);
        som.addAtomContainer(container);
        IAtomContainer container2 = som.getBuilder().newInstance(IMolecule.class);
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

    @Override @Test public void testSetMultiplier_IAtomContainer_Double() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer container = som.getBuilder().newInstance(IMolecule.class);
        som.addAtomContainer(container);

        Assert.assertEquals(1.0, som.getMultiplier(container), 0.00001);
        som.setMultiplier(container, 2.0);
        Assert.assertEquals(2.0, som.getMultiplier(container), 0.00001);
    }

    @Override @Test public void testAddAtomContainer_IAtomContainer() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(5, som.getAtomContainerCount());

        // now test it to make sure it properly grows the array
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(7, som.getAtomContainerCount());
    }

    @Override @Test public void testGrowAtomContainerArray() {
        // this test assumes that the growSize = 5 !
        // if not, there is need for the array to grow
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();

        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(7, som.getAtomContainerCount());
    }

    @Override @Test public void testGetAtomContainers() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();

        Assert.assertEquals(0, som.getAtomContainerCount());

        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(3, som.getAtomContainerCount());
        Assert.assertNotNull(som.getAtomContainer(0));
        Assert.assertNotNull(som.getAtomContainer(1));
        Assert.assertNotNull(som.getAtomContainer(2));
    }

    @Override @Test public void testRemoveAtomContainer_IAtomContainer() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IMolecule.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IMolecule.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        som.removeAtomContainer(ac1);
        Assert.assertEquals(1, som.getAtomContainerCount());
        Assert.assertEquals(ac2, som.getAtomContainer(0));
    }

    @Override @Test public void testRemoveAllAtomContainers() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IMolecule.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IMolecule.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);

        Assert.assertEquals(2, som.getAtomContainerCount());
        som.removeAllAtomContainers();
        Assert.assertEquals(0, som.getAtomContainerCount());
    }

    @Override @Test public void testRemoveAtomContainer_int() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IMolecule.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IMolecule.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        som.removeAtomContainer(0);
        Assert.assertEquals(1, som.getAtomContainerCount());
        Assert.assertEquals(ac2, som.getAtomContainer(0));
    }

    /*
     * @cdk.bug 2679343
     */
    @Override @Test public void testBug2679343() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IMolecule.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IMolecule.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        som.addAtomContainer(ac2);
        Assert.assertEquals(3, som.getAtomContainerCount());
        som.removeAtomContainer(ac2);
        Assert.assertEquals(1, som.getAtomContainerCount());
    }

    @Override @Test public void testReplaceAtomContainer_int_IAtomContainer() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        IAtomContainer ac1 = som.getBuilder().newInstance(IMolecule.class);
        IAtomContainer ac2 = som.getBuilder().newInstance(IMolecule.class);
        IAtomContainer ac3 = som.getBuilder().newInstance(IMolecule.class);
        som.addAtomContainer(ac1);
        som.addAtomContainer(ac2);
        Assert.assertEquals(ac2, som.getAtomContainer(1));
        som.replaceAtomContainer(1, ac3);
        Assert.assertEquals(ac3, som.getAtomContainer(1));
    }

    @Override @Test public void testGetMultiplier_IAtomContainer() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class));

        Assert.assertEquals(-1.0, som.getMultiplier(som.getBuilder().newInstance(IAtomContainer.class)), 0.00001);
    }

    @Override @Test public void testGetMultipliers() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class), 1.0);

        Double[] multipliers = som.getMultipliers();
        Assert.assertNotNull(multipliers);
        Assert.assertEquals(1, multipliers.length);
    }

    @Override @Test public void testAddAtomContainer_IAtomContainer_double() {
        IAtomContainerSet som = (IAtomContainerSet)newChemObject();
        som.addAtomContainer(som.getBuilder().newInstance(IMolecule.class), 2.0);
        Assert.assertEquals(1, som.getAtomContainerCount());
        Assert.assertEquals(2.0, som.getMultiplier(0), 0.00001);
    }

    @Test
    public void testSortAtomContainers_Comparator() {
        IAtomContainerSet som = (IMoleculeSet)newChemObject();
        IMolecule ac1 = som.getBuilder().newInstance(IMolecule.class);
        IMolecule ac2 = som.getBuilder().newInstance(IMolecule.class);
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
}
