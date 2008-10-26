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
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 * Checks the functionality of the ChemModel class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ChemModel
 */
public class ChemModelTest extends CDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testChemModel() {
	    ChemModel chemModel = new ChemModel();
	    Assert.assertNotNull(chemModel);
    }

    @Test public void testSetMoleculeSet_IMoleculeSet() {
	    ChemModel chemModel = new ChemModel();
	    MoleculeSet crystal = new MoleculeSet();
        chemModel.setMoleculeSet(crystal);
        Assert.assertEquals(crystal, chemModel.getMoleculeSet());
    }
    @Test public void testGetMoleculeSet() {
    	testSetMoleculeSet_IMoleculeSet();
    }
    
    @Test public void testSetReactionSet_IReactionSet() {
	    ChemModel chemModel = new ChemModel();
	    ReactionSet crystal = new ReactionSet();
        chemModel.setReactionSet(crystal);
        Assert.assertEquals(crystal, chemModel.getReactionSet());
    }
    @Test public void testGetReactionSet() {
    	testSetReactionSet_IReactionSet();
    }
    
    @Test public void testSetRingSet_IRingSet() {
	    ChemModel chemModel = new ChemModel();
	    RingSet crystal = new RingSet();
        chemModel.setRingSet(crystal);
        Assert.assertEquals(crystal, chemModel.getRingSet());
    }
    @Test public void testGetRingSet() {
        testSetRingSet_IRingSet();
    }
    
    @Test public void testSetCrystal_ICrystal() {
	    ChemModel chemModel = new ChemModel();
	    Crystal crystal = new Crystal();
        chemModel.setCrystal(crystal);
        Assert.assertEquals(crystal, chemModel.getCrystal());
    }
    @Test public void testGetCrystal() {
        testSetCrystal_ICrystal();
    }
    
    @Test public void testToString() {
        ChemModel model = new ChemModel();
        String description = model.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

	@Test public void testClone() throws Exception {
        ChemModel model = new ChemModel();
        Object clone = model.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof ChemModel);
    }    
        
    @Test public void testClone_IMoleculeSet() throws Exception {
		ChemModel model = new ChemModel();
        ChemModel clone = (ChemModel)model.clone();
        Assert.assertNull(clone.getMoleculeSet());
        
		model.setMoleculeSet(new MoleculeSet());
        clone = (ChemModel)model.clone();
        Assert.assertNotNull(clone.getMoleculeSet());
        Assert.assertNotSame(model.getMoleculeSet(), clone.getMoleculeSet());
    }

    @Test public void testClone_IReactionSet() throws Exception {
		ChemModel model = new ChemModel();
        ChemModel clone = (ChemModel)model.clone();
        Assert.assertNull(clone.getReactionSet());
        
		model.setReactionSet(new ReactionSet());
        clone = (ChemModel)model.clone();
        Assert.assertNotNull(clone.getReactionSet());
        Assert.assertNotSame(model.getReactionSet(), clone.getReactionSet());
    }

    @Test public void testClone_Crystal() throws Exception {
		ChemModel model = new ChemModel();
        ChemModel clone = (ChemModel)model.clone();
        Assert.assertNull(clone.getCrystal());
        
		model.setCrystal(new Crystal());
        clone = (ChemModel)model.clone();
        Assert.assertNotNull(clone.getCrystal());
        Assert.assertNotSame(model.getCrystal(), clone.getCrystal());
    }

    @Test public void testClone_RingSet() throws Exception {
		ChemModel model = new ChemModel();
        ChemModel clone = (ChemModel)model.clone();
        Assert.assertNull(clone.getRingSet());
        
		model.setRingSet(new RingSet());
        clone = (ChemModel)model.clone();
        Assert.assertNotNull(clone.getRingSet());
        Assert.assertNotSame(model.getRingSet(), clone.getRingSet());
    }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        ChemModel chemObject = new ChemModel();
        chemObject.addListener(listener);
        
        chemObject.setMoleculeSet(new MoleculeSet());
        Assert.assertTrue(listener.changed);
        
        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setReactionSet(new ReactionSet());
        Assert.assertTrue(listener.changed);
        
        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setCrystal(new Crystal());
        Assert.assertTrue(listener.changed);
        
        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setRingSet(new RingSet());
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
