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
package org.openscience.cdk.interfaces;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IChemSequence} implementations.
 *
 * @cdk.module test-interfaces
 */
public class IChemSequenceTest extends IChemObjectTest {

    @Test public void testAddChemModel_IChemModel() {
        IChemSequence cs = getBuilder().newChemSequence();
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        Assert.assertEquals(3, cs.getChemModelCount());
    }

    @Test public void testRemoveChemModel_int() {
        IChemSequence cs = getBuilder().newChemSequence();
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        Assert.assertEquals(3, cs.getChemModelCount());
        cs.removeChemModel(1);
        Assert.assertEquals(2, cs.getChemModelCount());
    }
    
    @Test public void testGrowChemModelArray() {
        IChemSequence cs = getBuilder().newChemSequence();
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        Assert.assertEquals(3, cs.getChemModelCount());
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel()); // this one should enfore array grow
        Assert.assertEquals(6, cs.getChemModelCount());
    }

    @Test public void testGetChemModelCount() {
        IChemSequence cs = getBuilder().newChemSequence();
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        Assert.assertEquals(3, cs.getChemModelCount());
    }

    @Test public void testGetChemModel_int() {
        IChemSequence cs = getBuilder().newChemSequence();
        cs.addChemModel(getBuilder().newChemModel());
        IChemModel second = getBuilder().newChemModel();
        cs.addChemModel(second);
        cs.addChemModel(getBuilder().newChemModel());
        
        Assert.assertEquals(second, cs.getChemModel(1));
    }

    @Test public void testChemModels() {
        IChemSequence cs = getBuilder().newChemSequence();
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());
        cs.addChemModel(getBuilder().newChemModel());

        Assert.assertEquals(3, cs.getChemModelCount());
        Iterator<IChemModel> models = cs.chemModels().iterator();
        int count = 0;
        while (models.hasNext()) {
        	Assert.assertNotNull(models.next());
        	++count;
        }
        Assert.assertEquals(3, count);
    }

    /** Test for RFC #9 */
    @Test public void testToString() {
        IChemSequence cs = getBuilder().newChemSequence();
        String description = cs.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
    
    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemSequence chemObject = getBuilder().newChemSequence();
        chemObject.addListener(listener);
        
        chemObject.addChemModel(getBuilder().newChemModel());
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
	    IChemSequence sequence = getBuilder().newChemSequence();
        Object clone = sequence.clone();
        Assert.assertTrue(clone instanceof IChemSequence);
    }    
        
    @Test public void testClone_IChemModel() throws Exception {
		IChemSequence sequence = getBuilder().newChemSequence();
		sequence.addChemModel(getBuilder().newChemModel()); // 1
		sequence.addChemModel(getBuilder().newChemModel()); // 2
		sequence.addChemModel(getBuilder().newChemModel()); // 3
		sequence.addChemModel(getBuilder().newChemModel()); // 4

		IChemSequence clone = (IChemSequence)sequence.clone();
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
