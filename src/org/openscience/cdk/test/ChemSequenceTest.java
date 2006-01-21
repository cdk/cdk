/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;

/**
 * Checks the funcitonality of the ChemSequence class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.ChemSequence
 */
public class ChemSequenceTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public ChemSequenceTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(ChemSequenceTest.class);
    }
    
    public void testChemSequence() {
        ChemSequence cs = new ChemSequence();
	assertNotNull(cs);
    }
    
    public void testAddChemModel_IChemModel() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        assertEquals(3, cs.getChemModelCount());
    }

    public void testGrowChemModelArray() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        assertEquals(3, cs.getChemModelCount());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel()); // this one should enfore array grow
        assertEquals(6, cs.getChemModelCount());
    }

    public void testGetChemModelCount() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        assertEquals(3, cs.getChemModelCount());
    }

    public void testGetChemModel_int() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        ChemModel second = new ChemModel();
        cs.addChemModel(second);
        cs.addChemModel(new ChemModel());
        
        assertEquals(second, cs.getChemModel(1));
    }

    public void testGetChemModels() {
        ChemSequence cs = new ChemSequence();
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());
        cs.addChemModel(new ChemModel());

        assertEquals(3, cs.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel[] models = cs.getChemModels();
        assertEquals(3, models.length);
        assertNotNull(models[0]);
        assertNotNull(models[1]);
        assertNotNull(models[2]);
    }

    /** Test for RFC #9 */
    public void testToString() {
        ChemSequence cs = new ChemSequence();
        String description = cs.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
    
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        ChemSequence chemObject = new ChemSequence();
        chemObject.addListener(listener);
        
        chemObject.addChemModel(new ChemModel());
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

	public void testClone() {
        ChemSequence sequence = new ChemSequence();
        Object clone = sequence.clone();
        assertTrue(clone instanceof ChemSequence);
    }    
        
    public void testClone_IChemModel() {
		ChemSequence sequence = new ChemSequence();
		sequence.addChemModel(new ChemModel()); // 1
		sequence.addChemModel(new ChemModel()); // 2
		sequence.addChemModel(new ChemModel()); // 3
		sequence.addChemModel(new ChemModel()); // 4

		ChemSequence clone = (ChemSequence)sequence.clone();
		assertEquals(sequence.getChemModelCount(), clone.getChemModelCount());
		for (int f = 0; f < sequence.getChemModelCount(); f++) {
			for (int g = 0; g < clone.getChemModelCount(); g++) {
				assertNotNull(sequence.getChemModel(f));
				assertNotNull(clone.getChemModel(g));
				assertNotSame(sequence.getChemModel(f), clone.getChemModel(g));
			}
		}        
    }
}
