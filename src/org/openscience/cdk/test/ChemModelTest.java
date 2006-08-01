/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;

/**
 * Checks the funcitonality of the ChemModel class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ChemModel
 */
public class ChemModelTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public ChemModelTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(ChemModelTest.class);
    }
    
    public void testChemModel() {
	    ChemModel chemModel = new ChemModel();
	    assertNotNull(chemModel);
    }

    public void testSetSetOfMolecules_IMoleculeSet() {
	    ChemModel chemModel = new ChemModel();
	    SetOfMolecules crystal = new SetOfMolecules();
        chemModel.setSetOfMolecules(crystal);
        assertEquals(crystal, chemModel.getSetOfMolecules());
    }
    public void testGetSetOfMolecules() {
    	testSetSetOfMolecules_IMoleculeSet();
    }
    
    public void testSetSetOfReactions_IReactionSet() {
	    ChemModel chemModel = new ChemModel();
	    SetOfReactions crystal = new SetOfReactions();
        chemModel.setSetOfReactions(crystal);
        assertEquals(crystal, chemModel.getSetOfReactions());
    }
    public void testGetSetOfReactions() {
    	testSetSetOfReactions_IReactionSet();
    }
    
    public void testSetRingSet_IRingSet() {
	    ChemModel chemModel = new ChemModel();
	    RingSet crystal = new RingSet();
        chemModel.setRingSet(crystal);
        assertEquals(crystal, chemModel.getRingSet());
    }
    public void testGetRingSet() {
        testSetRingSet_IRingSet();
    }
    
    public void testSetCrystal_ICrystal() {
	    ChemModel chemModel = new ChemModel();
	    Crystal crystal = new Crystal();
        chemModel.setCrystal(crystal);
        assertEquals(crystal, chemModel.getCrystal());
    }
    public void testGetCrystal() {
        testSetCrystal_ICrystal();
    }
    
    public void testToString() {
        ChemModel model = new ChemModel();
        String description = model.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

	public void testClone() throws Exception {
        ChemModel model = new ChemModel();
        Object clone = model.clone();
        assertNotNull(clone);
        assertTrue(clone instanceof ChemModel);
    }    
        
    public void testClone_IMoleculeSet() throws Exception {
		ChemModel model = new ChemModel();
        ChemModel clone = (ChemModel)model.clone();
        assertNull(clone.getSetOfMolecules());
        
		model.setSetOfMolecules(new SetOfMolecules());
        clone = (ChemModel)model.clone();
        assertNotNull(clone.getSetOfMolecules());
        assertNotSame(model.getSetOfMolecules(), clone.getSetOfMolecules());
    }

    public void testClone_IReactionSet() throws Exception {
		ChemModel model = new ChemModel();
        ChemModel clone = (ChemModel)model.clone();
        assertNull(clone.getSetOfReactions());
        
		model.setSetOfReactions(new SetOfReactions());
        clone = (ChemModel)model.clone();
        assertNotNull(clone.getSetOfReactions());
        assertNotSame(model.getSetOfReactions(), clone.getSetOfReactions());
    }

    public void testClone_Crystal() throws Exception {
		ChemModel model = new ChemModel();
        ChemModel clone = (ChemModel)model.clone();
        assertNull(clone.getCrystal());
        
		model.setCrystal(new Crystal());
        clone = (ChemModel)model.clone();
        assertNotNull(clone.getCrystal());
        assertNotSame(model.getCrystal(), clone.getCrystal());
    }

    public void testClone_RingSet() throws Exception {
		ChemModel model = new ChemModel();
        ChemModel clone = (ChemModel)model.clone();
        assertNull(clone.getRingSet());
        
		model.setRingSet(new RingSet());
        clone = (ChemModel)model.clone();
        assertNotNull(clone.getRingSet());
        assertNotSame(model.getRingSet(), clone.getRingSet());
    }

    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        ChemModel chemObject = new ChemModel();
        chemObject.addListener(listener);
        
        chemObject.setSetOfMolecules(new SetOfMolecules());
        assertTrue(listener.changed);
        
        listener.reset();
        assertFalse(listener.changed);
        chemObject.setSetOfReactions(new SetOfReactions());
        assertTrue(listener.changed);
        
        listener.reset();
        assertFalse(listener.changed);
        chemObject.setCrystal(new Crystal());
        assertTrue(listener.changed);
        
        listener.reset();
        assertFalse(listener.changed);
        chemObject.setRingSet(new RingSet());
        assertTrue(listener.changed);
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
