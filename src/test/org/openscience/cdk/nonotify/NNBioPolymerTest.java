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
package org.openscience.cdk.nonotify;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.AbstractBioPolymerTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link NNBioPolymer}.
 *
 * @cdk.module test-nonotify
 */
public class NNBioPolymerTest extends AbstractBioPolymerTest {

    @BeforeClass public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {
            public IChemObject newTestObject() {
                return new NNBioPolymer();
            }
        });
    }

	@Test public void testNNBioPolymer() {
		IBioPolymer oBioPolymer = new NNBioPolymer();
		Assert.assertNotNull(oBioPolymer);
		Assert.assertEquals(oBioPolymer.getMonomerCount(), 0);
		
		IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
		oStrand1.setStrandName("A");
		IStrand oStrand2 = oBioPolymer.getBuilder().newInstance(IStrand.class);
		oStrand2.setStrandName("B");
		IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
		oMono2.setMonomerName(new String("HOH"));
		IMonomer oMono3 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
		oMono3.setMonomerName(new String("GLYA16"));
		IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class,"C1");
		IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class,"C2");
		IAtom oAtom3 = oBioPolymer.getBuilder().newInstance(IAtom.class,"C3");
		IAtom oAtom4 = oBioPolymer.getBuilder().newInstance(IAtom.class,"C4");
		IAtom oAtom5 = oBioPolymer.getBuilder().newInstance(IAtom.class,"C5");
		
		oBioPolymer.addAtom(oAtom1);
		oBioPolymer.addAtom(oAtom2, oStrand1);
		oBioPolymer.addAtom(oAtom3, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom4, oMono2, oStrand2);
		oBioPolymer.addAtom(oAtom5, oMono3, oStrand2);
		Assert.assertNotNull(oBioPolymer.getAtom(0));
		Assert.assertNotNull(oBioPolymer.getAtom(1));
		Assert.assertNotNull(oBioPolymer.getAtom(2));
		Assert.assertNotNull(oBioPolymer.getAtom(3));
		Assert.assertNotNull(oBioPolymer.getAtom(4));
		Assert.assertEquals(oAtom1, oBioPolymer.getAtom(0));
		Assert.assertEquals(oAtom2, oBioPolymer.getAtom(1));
		Assert.assertEquals(oAtom3, oBioPolymer.getAtom(2));
		Assert.assertEquals(oAtom4, oBioPolymer.getAtom(3));
		Assert.assertEquals(oAtom5, oBioPolymer.getAtom(4));

		Assert.assertNull(oBioPolymer.getMonomer("0815", "A"));
		Assert.assertNull(oBioPolymer.getMonomer("0815", "B"));
		Assert.assertNull(oBioPolymer.getMonomer("0815", ""));
		Assert.assertNull(oBioPolymer.getStrand(""));
		Assert.assertNotNull(oBioPolymer.getMonomer("TRP279", "A"));
		Assert.assertEquals(oMono1, oBioPolymer.getMonomer("TRP279", "A"));
		Assert.assertEquals(oBioPolymer.getMonomer("TRP279", "A").getAtomCount(), 1);
		Assert.assertNotNull(oBioPolymer.getMonomer("HOH", "B"));
		Assert.assertEquals(oMono2, oBioPolymer.getMonomer("HOH", "B"));
		Assert.assertEquals(oBioPolymer.getMonomer("HOH", "B").getAtomCount(), 1);
		Assert.assertEquals(oBioPolymer.getStrand("B").getAtomCount(), 2);
		Assert.assertEquals(oBioPolymer.getStrand("B").getMonomerCount(), 2);
		Assert.assertNull(oBioPolymer.getStrand("C"));
		Assert.assertNotNull(oBioPolymer.getStrand("B"));
	}

    // Overwrite default methods: no notifications are expected!
    
    @Test public void testNotifyChanged() {
        NNChemObjectTestHelper.testNotifyChanged(newChemObject());
    }
    @Test public void testNotifyChanged_SetFlag() {
        NNChemObjectTestHelper.testNotifyChanged_SetFlag(newChemObject());
    }
    @Test public void testNotifyChanged_SetFlags() {
        NNChemObjectTestHelper.testNotifyChanged_SetFlags(newChemObject());
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
    @Test public void testNotifyChanged_SetProperty() {
        NNChemObjectTestHelper.testNotifyChanged_SetProperty(newChemObject());
    }
    @Test public void testNotifyChanged_RemoveProperty() {
        NNChemObjectTestHelper.testNotifyChanged_RemoveProperty(newChemObject());
    }
    @Test public void testSetAtoms_removeListener() {
        NNChemObjectTestHelper.testSetAtoms_removeListener(newChemObject());
    }
}
