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
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 * Checks the functionality of the ChemSequence class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ChemSequence
 */
public class ChemSequenceTest extends ChemObjectTest {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testChemSequence() {
        ChemSequence cs = new ChemSequence();
	Assert.assertNotNull(cs);
    }
    
    @Test public void testAddChemModel_IChemModel() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        Assert.assertEquals(3, cs.getChemModelCount());
    }

    @Test public void testRemoveChemModel_int() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        Assert.assertEquals(3, cs.getChemModelCount());
        cs.removeChemModel(1);
        Assert.assertEquals(2, cs.getChemModelCount());
    }
    
    @Test public void testGrowChemModelArray() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        Assert.assertEquals(3, cs.getChemModelCount());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel()); // this one should enfore array grow
        Assert.assertEquals(6, cs.getChemModelCount());
    }

    @Test public void testGetChemModelCount() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        Assert.assertEquals(3, cs.getChemModelCount());
    }

    @Test public void testGetChemModel_int() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        ChemModel second = new ChemModel();
        cs.addChemModel(second);
        cs.addChemModel(new ChemModel());
        
        Assert.assertEquals(second, cs.getChemModel(1));
    }

    @Test public void testChemModels() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());

        Assert.assertEquals(3, cs.getChemModelCount());
        java.util.Iterator models = cs.chemModels().iterator();
        int count = 0;
        while (models.hasNext()) {
        	Assert.assertNotNull(models.next());
        	++count;
        }
        Assert.assertEquals(3, count);
    }

    /** Test for RFC #9 */
    @Test public void testToString() {
        ChemSequence cs = new ChemSequence();
        String description = cs.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
    
    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        ChemSequence chemObject = new ChemSequence();
        chemObject.addListener(listener);
        
        chemObject.addChemModel(new ChemModel());
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

	@Test public void testClone() throws Exception {
        ChemSequence sequence = new ChemSequence();
        Object clone = sequence.clone();
        Assert.assertTrue(clone instanceof ChemSequence);
    }    
        
    @Test public void testClone_IChemModel() throws Exception {
		ChemSequence sequence = new ChemSequence();
		sequence.addChemModel(new ChemModel()); // 1
		sequence.addChemModel(new ChemModel()); // 2
		sequence.addChemModel(new ChemModel()); // 3
		sequence.addChemModel(new ChemModel()); // 4

		ChemSequence clone = (ChemSequence)sequence.clone();
		Assert.assertEquals(sequence.getChemModelCount(), clone.getChemModelCount());
		for (int f = 0; f < sequence.getChemModelCount(); f++) {
			for (int g = 0; g < clone.getChemModelCount(); g++) {
				Assert.assertNotNull(sequence.getChemModel(f));
				Assert.assertNotNull(clone.getChemModel(g));
				Assert.assertNotSame(sequence.getChemModel(f), clone.getChemModel(g));
			}
		}        
    }
}
