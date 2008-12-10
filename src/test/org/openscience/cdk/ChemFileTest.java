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
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 * Checks the functionality of the ChemSequence class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ChemSequence
 */
public class ChemFileTest extends ChemObjectTest {

    @BeforeClass public static void setUp() {
        setBuilder(DefaultChemObjectBuilder.getInstance());
    }

    @Test public void testChemFile() {
        IChemFile cs = getBuilder().newChemFile();
        Assert.assertNotNull(cs);
    }

    @Test public void testAddChemSequence_IChemSequence() {
        IChemFile cs = getBuilder().newChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        Assert.assertEquals(3, cs.getChemSequenceCount());
    }
    
    @Test public void testRemoveChemSequence_int() {
    	IChemFile cs = getBuilder().newChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        Assert.assertEquals(3, cs.getChemSequenceCount());
        cs.removeChemSequence(1);
        Assert.assertEquals(2, cs.getChemSequenceCount());
    }
    
    @Test public void testGetChemSequence_int() {
        IChemFile cs = getBuilder().newChemFile();
        cs.addChemSequence(new ChemSequence());
        ChemSequence second = new ChemSequence();
        cs.addChemSequence(second);
        cs.addChemSequence(new ChemSequence());
        Assert.assertEquals(second, cs.getChemSequence(1));
    }
    
    @Test public void testGrowChemSequenceArray() {
        IChemFile cs = getBuilder().newChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        Assert.assertEquals(3, cs.getChemSequenceCount());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence()); // this one should enfore array grow
        Assert.assertEquals(6, cs.getChemSequenceCount());
    }

    @Test public void testChemSequences() {
        IChemFile cs = getBuilder().newChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());

        Assert.assertNotNull(cs.chemSequences());
        Assert.assertEquals(3, cs.getChemSequenceCount());
    }

    @Test public void testGetChemSequenceCount() {
        IChemFile cs = getBuilder().newChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
 
        Assert.assertEquals(3, cs.getChemSequenceCount());
    }

    /** Test for RFC #9 */
    @Test public void testToString() {
        IChemFile cs = getBuilder().newChemFile();
        String description = cs.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemFile chemObject = getBuilder().newChemFile();
        chemObject.addListener(listener);
        
        chemObject.addChemSequence(new ChemSequence());
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
        IChemFile file = getBuilder().newChemFile();
        Object clone = file.clone();
        Assert.assertTrue(clone instanceof ChemFile);
    }    
        
    @Test public void testClone_ChemSequence() throws Exception {
		IChemFile file = getBuilder().newChemFile();
		file.addChemSequence(new ChemSequence()); // 1
		file.addChemSequence(new ChemSequence()); // 2
		file.addChemSequence(new ChemSequence()); // 3
		file.addChemSequence(new ChemSequence()); // 4

		IChemFile clone = (IChemFile)file.clone();
		Assert.assertEquals(file.getChemSequenceCount(), clone.getChemSequenceCount());
		for (int f = 0; f < file.getChemSequenceCount(); f++) {
			for (int g = 0; g < clone.getChemSequenceCount(); g++) {
				Assert.assertNotNull(file.getChemSequence(f));
				Assert.assertNotNull(clone.getChemSequence(g));
				Assert.assertNotSame(file.getChemSequence(f), clone.getChemSequence(g));
			}
		}        
    }
}
