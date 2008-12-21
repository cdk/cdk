/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.nonotify;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IPDBPolymerTest;
import org.openscience.cdk.interfaces.IStrand;

/**
 * Checks the functionality of the {@link NNPDBPolymer}.
 *
 * @cdk.module test-nonotify
 */
public class NNPDBPolymerTest extends IPDBPolymerTest {

    @BeforeClass public static void setUp() {
        setChemObject(new NNPDBPolymer());
    }

	@Test public void testNNPDBPolymer() {
		IPDBPolymer pdbPolymer = new NNPDBPolymer();
		Assert.assertNotNull(pdbPolymer);
		Assert.assertEquals(pdbPolymer.getMonomerCount(), 0);
		
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IStrand oStrand2 = getBuilder().newStrand();
		oStrand2.setStrandName("B");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = getBuilder().newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IMonomer oMono3 = getBuilder().newMonomer();
		oMono3.setMonomerName(new String("GLYA16"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		IPDBAtom oPDBAtom3 = getBuilder().newPDBAtom("C3");
		IPDBAtom oPDBAtom4 = getBuilder().newPDBAtom("C4");
		IPDBAtom oPDBAtom5 = getBuilder().newPDBAtom("C5");
		
		pdbPolymer.addAtom(oPDBAtom1);
		pdbPolymer.addAtom(oPDBAtom2, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom4, oMono2, oStrand2);
		pdbPolymer.addAtom(oPDBAtom5, oMono3, oStrand2);
		Assert.assertNotNull(pdbPolymer.getAtom(0));
		Assert.assertNotNull(pdbPolymer.getAtom(1));
		Assert.assertNotNull(pdbPolymer.getAtom(2));
		Assert.assertNotNull(pdbPolymer.getAtom(3));
		Assert.assertNotNull(pdbPolymer.getAtom(4));
		Assert.assertEquals(oPDBAtom1, pdbPolymer.getAtom(0));
		Assert.assertEquals(oPDBAtom2, pdbPolymer.getAtom(1));
		Assert.assertEquals(oPDBAtom3, pdbPolymer.getAtom(2));
		Assert.assertEquals(oPDBAtom4, pdbPolymer.getAtom(3));
		Assert.assertEquals(oPDBAtom5, pdbPolymer.getAtom(4));

		Assert.assertNull(pdbPolymer.getMonomer("0815", "A"));
		Assert.assertNull(pdbPolymer.getMonomer("0815", "B"));
		Assert.assertNull(pdbPolymer.getMonomer("0815", ""));
		Assert.assertNull(pdbPolymer.getStrand(""));
		Assert.assertNotNull(pdbPolymer.getMonomer("TRP279", "A"));
		Assert.assertEquals(oMono1, pdbPolymer.getMonomer("TRP279", "A"));
		Assert.assertEquals(pdbPolymer.getMonomer("TRP279", "A").getAtomCount(), 1);
		Assert.assertNotNull(pdbPolymer.getMonomer("HOH", "B"));
		Assert.assertEquals(oMono2, pdbPolymer.getMonomer("HOH", "B"));
		Assert.assertEquals(pdbPolymer.getMonomer("HOH", "B").getAtomCount(), 1);
		Assert.assertEquals(pdbPolymer.getStrand("B").getAtomCount(), 2);
		Assert.assertEquals(pdbPolymer.getStrand("B").getMonomerCount(), 2);
		Assert.assertNull(pdbPolymer.getStrand("C"));
		Assert.assertNotNull(pdbPolymer.getStrand("B"));
	}

	// Overwrite default methods: no notifications are expected!
    
    @Test public void testNotifyChanged() {
        NNChemObjectTestHelper.testNotifyChanged(newChemObject());
    }
    @Test public void testNotifyChanged_IChemObjectChangeEvent() {
        NNChemObjectTestHelper.testNotifyChanged_IChemObjectChangeEvent(newChemObject());
    }
    @Test public void testStateChanged_IChemObjectChangeEvent() {
        NNChemObjectTestHelper.testStateChanged_IChemObjectChangeEvent(newChemObject());
    }
    @Test public void testClone_ChemObjectListeners() throws Exception {
        NNChemObjectTestHelper.testClone_ChemObjectListeners(newChemObject());
    }
    @Test public void testAddListener_IChemObjectListener() {
        NNChemObjectTestHelper.testAddListener_IChemObjectListener(newChemObject());
    }
    @Test public void testGetListenerCount() {
        NNChemObjectTestHelper.testGetListenerCount(newChemObject());
    }
    @Test public void testRemoveListener_IChemObjectListener() {
        NNChemObjectTestHelper.testRemoveListener_IChemObjectListener(newChemObject());
    }
    @Test public void testSetNotification_true() {
        NNChemObjectTestHelper.testSetNotification_true(newChemObject());
    }
}
