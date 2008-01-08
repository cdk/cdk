/* $RCSfile$
 * $Author: egonw $    
 * $Date: 2006-08-01 21:13:42 +0200 (Tue, 01 Aug 2006) $    
 * $Revision: 6718 $
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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;

/**
 * Checks the functionality of the MoleculeSet class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.MoleculeSet
 */
public class MoleculeSetTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public MoleculeSetTest(String name) {
        super(name);
    }

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(MoleculeSetTest.class);
    }
    
    public void testGetMoleculeCount() {
        IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        
        assertEquals(3, som.getMoleculeCount());
    }
    
    public void testGetMolecule_int() {
        IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        assertNotNull(som.getMolecule(2)); // third molecule should exist
        assertNull(som.getMolecule(3)); // fourth molecule must not exist
    }
    
    public void testAddMolecule_IMolecule() {
        IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        assertEquals(5, som.getMoleculeCount());
        
        // now test it to make sure it properly grows the array
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        assertEquals(7, som.getMoleculeCount());        
    }
    
    public void testAdd_IMoleculeSet() {
        IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        IMoleculeSet som2 = builder.newMoleculeSet();
        som2.add(som);
        
        assertEquals(5, som2.getMoleculeCount());
    }
    
    public void testSetMolecules_arrayIMolecule() {
        IMoleculeSet som = builder.newMoleculeSet();
        
        IMolecule[] set = new IMolecule[5];
        set[0] = builder.newMolecule();
        set[1] = builder.newMolecule();
        set[2] = builder.newMolecule();
        set[3] = builder.newMolecule();
        set[4] = builder.newMolecule();
        
        assertEquals(0, som.getMoleculeCount());
        som.setMolecules(set);
        assertEquals(5, som.getMoleculeCount());
    }
    
    public void testMolecules() {
    	IMoleculeSet som = builder.newMoleculeSet();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        assertEquals(3, som.getMoleculeCount());
        java.util.Iterator mols = som.molecules();
        int count = 0;
        while (mols.hasNext()) {
        	count++;
        	mols.next();
        }
        assertEquals(3, count);
        mols = som.molecules();
        while (mols.hasNext()) {
        	mols.next();
        	mols.remove();
        }
        assertEquals(0, som.getMoleculeCount());
    }
    
    public void testGrowMoleculeArray() {
        // this test assumes that the growSize = 5 !
        // if not, there is need for the array to grow
        IMoleculeSet som = builder.newMoleculeSet();
        
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        assertEquals(7, som.getAtomContainerCount());
    }
    
    public void testMoleculeSet() {
        IMoleculeSet som = builder.newMoleculeSet();
        assertNotNull(som);
        assertEquals(0, som.getMoleculeCount());
    }
    
    public void testGetMolecules() {
        IMoleculeSet som = builder.newMoleculeSet();
        
        assertEquals(0, som.getAtomContainerCount());
        
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        assertEquals(3, som.getAtomContainerCount());
        assertNotNull(som.getMolecule(0));
        assertNotNull(som.getMolecule(1));
        assertNotNull(som.getMolecule(2));
    }

    public void testToString() {
        IMoleculeSet som = builder.newMoleculeSet();
        String description = som.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testClone() throws Exception {
        IMoleculeSet som = builder.newMoleculeSet();
        Object clone = som.clone();
        assertTrue(clone instanceof IMoleculeSet);
	assertNotSame(som, clone);
    }   
    
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IMoleculeSet chemObject = builder.newMoleculeSet();
        chemObject.addListener(listener);
        
        chemObject.addMolecule(builder.newMolecule());
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
