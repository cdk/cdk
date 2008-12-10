/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 * Checks the functionality of the ChemModel class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ChemModel
 */
public class ChemModelTest extends ChemObjectTest {

    @BeforeClass public static void setUp() {
        setBuilder(DefaultChemObjectBuilder.getInstance());
    }

    @Test public void testChemModel() {
	    IChemModel chemModel = getBuilder().newChemModel();
	    Assert.assertNotNull(chemModel);
    }

    @Test public void testSetMoleculeSet_IMoleculeSet() {
	    IChemModel chemModel = getBuilder().newChemModel();
	    MoleculeSet crystal = new MoleculeSet();
        chemModel.setMoleculeSet(crystal);
        Assert.assertEquals(crystal, chemModel.getMoleculeSet());
    }
    @Test public void testGetMoleculeSet() {
    	testSetMoleculeSet_IMoleculeSet();
    }
    
    @Test public void testSetReactionSet_IReactionSet() {
	    IChemModel chemModel = getBuilder().newChemModel();
	    ReactionSet crystal = new ReactionSet();
        chemModel.setReactionSet(crystal);
        Assert.assertEquals(crystal, chemModel.getReactionSet());
    }
    @Test public void testGetReactionSet() {
    	testSetReactionSet_IReactionSet();
    }
    
    @Test public void testSetRingSet_IRingSet() {
	    IChemModel chemModel = getBuilder().newChemModel();
	    RingSet crystal = new RingSet();
        chemModel.setRingSet(crystal);
        Assert.assertEquals(crystal, chemModel.getRingSet());
    }
    @Test public void testGetRingSet() {
        testSetRingSet_IRingSet();
    }
    
    @Test public void testSetCrystal_ICrystal() {
	    IChemModel chemModel = getBuilder().newChemModel();
	    Crystal crystal = new Crystal();
        chemModel.setCrystal(crystal);
        Assert.assertEquals(crystal, chemModel.getCrystal());
    }
    @Test public void testGetCrystal() {
        testSetCrystal_ICrystal();
    }
    
    @Test public void testToString() {
        IChemModel model = getBuilder().newChemModel();
        String description = model.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

	@Test public void testClone() throws Exception {
        IChemModel model = getBuilder().newChemModel();
        Object clone = model.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof ChemModel);
    }    
        
    @Test public void testClone_IMoleculeSet() throws Exception {
        IChemModel model = getBuilder().newChemModel();
        IChemModel clone = (IChemModel)model.clone();
        Assert.assertNull(clone.getMoleculeSet());
        
		model.setMoleculeSet(new MoleculeSet());
        clone = (IChemModel)model.clone();
        Assert.assertNotNull(clone.getMoleculeSet());
        Assert.assertNotSame(model.getMoleculeSet(), clone.getMoleculeSet());
    }

    @Test public void testClone_IReactionSet() throws Exception {
        IChemModel model = getBuilder().newChemModel();
        IChemModel clone = (IChemModel)model.clone();
        Assert.assertNull(clone.getReactionSet());
        
		model.setReactionSet(new ReactionSet());
        clone = (IChemModel)model.clone();
        Assert.assertNotNull(clone.getReactionSet());
        Assert.assertNotSame(model.getReactionSet(), clone.getReactionSet());
    }

    @Test public void testClone_Crystal() throws Exception {
		IChemModel model = getBuilder().newChemModel();
        IChemModel clone = (IChemModel)model.clone();
        Assert.assertNull(clone.getCrystal());
        
		model.setCrystal(getBuilder().newCrystal());
        clone = (IChemModel)model.clone();
        Assert.assertNotNull(clone.getCrystal());
        Assert.assertNotSame(model.getCrystal(), clone.getCrystal());
    }

    @Test public void testClone_RingSet() throws Exception {
        IChemModel model = getBuilder().newChemModel();
        IChemModel clone = (IChemModel)model.clone();
        Assert.assertNull(clone.getRingSet());
        
		model.setRingSet(new RingSet());
        clone = (IChemModel)model.clone();
        Assert.assertNotNull(clone.getRingSet());
        Assert.assertNotSame(model.getRingSet(), clone.getRingSet());
    }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = getBuilder().newChemModel();
        chemObject.addListener(listener);
        
        chemObject.setMoleculeSet(getBuilder().newMoleculeSet());
        Assert.assertTrue(listener.changed);
        
        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setReactionSet(getBuilder().newReactionSet());
        Assert.assertTrue(listener.changed);
        
        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setCrystal(getBuilder().newCrystal());
        Assert.assertTrue(listener.changed);
        
        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setRingSet(getBuilder().newRingSet());
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
