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
import org.openscience.cdk.ChemFile;
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
public class ChemFileTest extends CDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testChemFile() {
        ChemFile cs = new ChemFile();
        Assert.assertNotNull(cs);
    }

    @Test public void testAddChemSequence_IChemSequence() {
        ChemFile cs = new ChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        Assert.assertEquals(3, cs.getChemSequenceCount());
    }
    
    @Test public void testRemoveChemSequence_int() {
    	ChemFile cs = new ChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        Assert.assertEquals(3, cs.getChemSequenceCount());
        cs.removeChemSequence(1);
        Assert.assertEquals(2, cs.getChemSequenceCount());
    }
    
    @Test public void testGetChemSequence_int() {
        ChemFile cs = new ChemFile();
        cs.addChemSequence(new ChemSequence());
        ChemSequence second = new ChemSequence();
        cs.addChemSequence(second);
        cs.addChemSequence(new ChemSequence());
        Assert.assertEquals(second, cs.getChemSequence(1));
    }
    
    @Test public void testGrowChemSequenceArray() {
        ChemFile cs = new ChemFile();
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
        ChemFile cs = new ChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());

        Assert.assertNotNull(cs.chemSequences());
        Assert.assertEquals(3, cs.getChemSequenceCount());
    }

    @Test public void testGetChemSequenceCount() {
        ChemFile cs = new ChemFile();
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
        cs.addChemSequence(new ChemSequence());
 
        Assert.assertEquals(3, cs.getChemSequenceCount());
    }

    /** Test for RFC #9 */
    @Test public void testToString() {
        ChemFile cs = new ChemFile();
        String description = cs.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        ChemFile chemObject = new ChemFile();
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
        ChemFile file = new ChemFile();
        Object clone = file.clone();
        Assert.assertTrue(clone instanceof ChemFile);
    }    
        
    @Test public void testClone_ChemSequence() throws Exception {
		ChemFile file = new ChemFile();
		file.addChemSequence(new ChemSequence()); // 1
		file.addChemSequence(new ChemSequence()); // 2
		file.addChemSequence(new ChemSequence()); // 3
		file.addChemSequence(new ChemSequence()); // 4

		ChemFile clone = (ChemFile)file.clone();
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
