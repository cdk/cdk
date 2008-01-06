/* $Revision$ $Author$ $Date$    
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
 */
package org.openscience.cdk.test.debug;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.debug.DebugChemObjectBuilder;
import org.openscience.cdk.test.AtomContainerTest;

/**
 * Checks the funcitonality of the AtomContainer.
 *
 * @cdk.module test-datadebug
 */
public class DebugAtomContainerTest extends AtomContainerTest {

    @BeforeClass public static void setUp() {
    	AtomContainerTest.builder = DebugChemObjectBuilder.getInstance();
    }

    @Test public void testSetAtoms_arrayIAtom() {
        super.testSetAtoms_arrayIAtom();
    }

	@Test public void testClone() throws Exception {
        super.testClone();
    }    
        
    @Test public void testClone_IAtom() throws Exception {
    	super.testClone_IAtom();
    }
    
	@Test public void testClone_IAtom2() throws Exception {
		super.testClone_IAtom2();
	}

    @Test public void testClone_IBond() throws Exception {
    	super.testClone_IBond();
	}

    @Test public void testClone_IBond2() throws Exception {
    	super.testClone_IBond2();
	}

    @Test public void testClone_IBond3() throws Exception {
    	super.testClone_IBond3();
	}

    @Test public void testClone_ILonePair() throws Exception {
    	super.testClone_ILonePair();
	}

    @Test public void testGetConnectedElectronContainersList_IAtom() {
    	super.testGetConnectedElectronContainersList_IAtom();
    }

    @Test public void testGetConnectedBondsList_IAtom() {
        super.testGetConnectedBondsList_IAtom();
    }

    @Test public void testGetConnectedLonePairsList_IAtom() {
    	super.testGetConnectedLonePairsList_IAtom();
    }

    
    @Test public void testRemoveAtomAndConnectedElectronContainers_IAtom() {
        super.testRemoveAtomAndConnectedElectronContainers_IAtom();
    }

    @Test public void testGetAtomCount() {
        super.testGetAtomCount();
    }
    
    @Test public void testGetBondCount() {
        super.testGetBondCount();
    }
    
    @Test public void testAtomContainer_int_int_int_int() {
        super.testAtomContainer_int_int_int_int();
    }

    @Test public void testAtomContainer() {
        super.testAtomContainer();
    }

    @Test public void testAtomContainer_IAtomContainer() {
        super.testAtomContainer_IAtomContainer();
    }
    
    @Test public void testAdd_IAtomContainer() {
        super.testAdd_IAtomContainer();
    }
    
    @Test public void testRemove_IAtomContainer() throws Exception {
        super.testRemove_IAtomContainer();
    }
    
    @Test public void testRemoveAllElements() {
    	super.testRemoveAllElements();
    }
    
    @Test public void testRemoveAtom_int() {
    	super.testRemoveAtom_int();
    }
    
    @Test public void testRemoveAtom_IAtom() {
    	super.testRemoveAtom_IAtom();
    }
    
    @Test public void testSetAtom_int_IAtom() {
    	super.testSetAtom_int_IAtom();
    }
    
    @Test public void testGetAtom_int() {
    	super.testGetAtom_int();
    }
    
    @Test public void testGetBond_int() {
    	super.testGetBond_int();
    }
    
    @Test public void testGetElectronContainerCount() {
    	super.testGetElectronContainerCount();
    }
    
    @Test public void testRemoveAllBonds() {
    	super.testRemoveAllBonds();
    }
    
    @Test public void testRemoveAllElectronContainers() {
    	super.testRemoveAllElectronContainers();
    }
    
    @Test public void testAddAtom_IAtom() {
    	super.testAddAtom_IAtom();
    }

    @Test public void testAtoms() {
    	super.testAtoms();
    }

    @Test public void testBonds() {
    	super.testBonds();
    }
    
    @Test public void testLonePairs() {
    	super.testLonePairs();
    }

    @Test public void testSingleElectrons() {
    	super.testSingleElectrons();
    }
    
    @Test public void testElectronContainers() {
    	super.testElectronContainers();
    }
    
    @Test public void testContains_IAtom() {
    	super.testContains_IAtom();
    }

    @Test public void testAddLonePair_int() {
    	super.testAddLonePair_int();
    }

    @Test public void testGetMaximumBondOrder_IAtom() {
    	super.testGetMaximumBondOrder_IAtom();
    }

    @Test public void testGetMinimumBondOrder_IAtom() {
    	super.testGetMinimumBondOrder_IAtom();
    }

    @Test public void testRemoveElectronContainer_int() {
    	super.testRemoveElectronContainer_int();
    }

    @Test public void testRemoveElectronContainer_IElectronContainer() {
    	super.testRemoveElectronContainer_IElectronContainer();
    }

    @Test public void testAddBond_IBond() {
    	super.testAddBond_IBond();
    }

    @Test public void testAddElectronContainer_IElectronContainer() {
    	super.testAddElectronContainer_IElectronContainer();
    }

    @Test public void testGetSingleElectron_IAtom() {
    	super.testGetSingleElectron_IAtom();
    }

    @Test public void testRemoveBond_IAtom_IAtom() {
    	super.testRemoveBond_IAtom_IAtom();
    }

    @Test public void testAddBond_int_int_IBond_Order() {
    	super.testAddBond_int_int_IBond_Order();
    }

    @Test public void testAddBond_int_int_IBond_Order_int() {
    	super.testAddBond_int_int_IBond_Order_int();
    }

    @Test public void testContains_IElectronContainer() {
    	super.testContains_IElectronContainer();
    }
    
    @Test public void testGetFirstAtom() {
    	super.testGetFirstAtom();
    }

    @Test public void testGetLastAtom() {
    	super.testGetLastAtom();
    }
    
    @Test public void testGetAtomNumber_IAtom() {
    	super.testGetAtomNumber_IAtom();
    }
    
    @Test public void testGetBondNumber_IBond() {
    	super.testGetBondNumber_IBond();
    }
    
    @Test public void testGetBondNumber_IAtom_IAtom() {
    	super.testGetBondNumber_IAtom_IAtom();
    }
    
    @Test public void testGetBond_IAtom_IAtom() {
    	super.testGetBond_IAtom_IAtom();
    }
    
    @Test public void testGetConnectedAtomsList_IAtom() {
    	super.testGetConnectedAtomsList_IAtom();
    }
    
    @Test public void testGetConnectedAtomsCount_IAtom() {
    	super.testGetConnectedAtomsCount_IAtom();
    }
    
    @Test public void testGetLonePairCount() {
    	super.testGetLonePairCount();
    }

    @Test public void testGetConnectedLonePairsCount_IAtom() {
    	super.testGetConnectedLonePairsCount_IAtom();
    }

    @Test public void testGetBondOrderSum_IAtom() {
    	super.testGetBondOrderSum_IAtom();
    }
    
    @Test public void testGetBondCount_IAtom() {
    	super.testGetBondCount_IAtom();
    }
    
    @Test public void testGetBondCount_int() {
    	super.testGetBondCount_int();
    }
    
    @Test public void testGetAtomParity_IAtom() {
    	super.testGetAtomParity_IAtom();
    }

    @Test public void testToString() {
    	super.testToString();
    }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
    	super.testStateChanged_IChemObjectChangeEvent();
    }

    @Test public void testAddAtomParity_IAtomParity() {
    	super.testAddAtomParity_IAtomParity();
    }
    
    @Test public void testGetConnectedSingleElectronsCount_IAtom() {
    	super.testGetConnectedSingleElectronsCount_IAtom();
    }
    
    @Test public void testAddLonePair_ILonePair() {
    	super.testAddLonePair_ILonePair();
    }
    
    @Test public void testAddSingleElectron_ISingleElectron() {
    	super.testAddSingleElectron_ISingleElectron();
    }
    
    @Test public void testRemoveBond_int() {
    	super.testRemoveBond_int();
    }
    
    @Test public void testContains_IBond() {
    	super.testContains_IBond();
    }
    
    @Test public void testAddSingleElectron_int() {
    	super.testAddSingleElectron_int();
    }
    
    @Test public void testGetConnectedSingleElectronsList_IAtom() {
    	super.testGetConnectedSingleElectronsCount_IAtom();
    }
    
    @Test public void testRemoveBond_IBond() {
    	super.testRemoveBond_IBond();
    }
    
    @Test public void testGetConnectedBondsCount_IAtom() {
    	super.testGetConnectedBondsCount_IAtom();
    }
    
    @Test public void testGetConnectedBondsCount_int() {
    	super.testGetConnectedBondsCount_int();
    }
    
    @Test public void testSetBonds_arrayIBond() {
    	super.testSetBonds_arrayIBond();
    }
    
    @Test public void testGetLonePair_int() {
    	super.testGetLonePair_int();
    }
    
    @Test public void testGetSingleElectron_int() {
    	super.testGetSingleElectron_int();
    }
    
    @Test public void testGetLonePairNumber_ILonePair() {
    	super.testGetLonePairNumber_ILonePair();
    }
    
    @Test public void testGetSingleElectronNumber_ISingleElectron() {
    	super.testGetSingleElectronNumber_ISingleElectron();
    }
    
    @Test public void testGetElectronContainer_int() {
    	super.testGetElectronContainer_int();
    }
    
    @Test public void testGetSingleElectronCount() {
    	super.testGetSingleElectronCount();
    }
    
    @Test public void testRemoveLonePair_int() {
    	super.testRemoveLonePair_int();
    }
    
    @Test public void testRemoveLonePair_ILonePair() {
    	super.testRemoveLonePair_ILonePair();
    }
    
    @Test public void testRemoveSingleElectron_int() {
    	super.testRemoveSingleElectron_int();
    }
    
    @Test public void testRemoveSingleElectron_ISingleElectron() {
    	super.testRemoveSingleElectron_ISingleElectron();
    }
    
    @Test public void testContains_ILonePair() {
    	super.testContains_ILonePair();
    }
    
    @Test public void testContains_ISingleElectron() {
    	super.testContains_ISingleElectron();
    }
}
