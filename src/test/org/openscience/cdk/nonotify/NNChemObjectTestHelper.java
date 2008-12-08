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
 * 
 */
package org.openscience.cdk.nonotify;

import org.junit.Assert;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Helper class to test the functionality of the NNChemObject.
 *
 * @cdk.module test-nonotify
 */
public class NNChemObjectTestHelper {

    public static void testNotifyChanged(IChemObjectBuilder builder) {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        Assert.assertFalse(listener.getChanged());
    }

    public static void testNotifyChanged_IChemObjectChangeEvent(IChemObjectBuilder builder) {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        Assert.assertNull(listener.getEvent());
    }

    public static void testStateChanged_IChemObjectChangeEvent(IChemObjectBuilder builder) {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        Assert.assertFalse(listener.getChanged());
        
        listener.reset();
        Assert.assertFalse(listener.getChanged());
        chemObject.setProperty("Changed", "Again");
        Assert.assertFalse(listener.getChanged());

        listener.reset();
        Assert.assertFalse(listener.getChanged());
        chemObject.setFlag(3, true);
        Assert.assertFalse(listener.getChanged());
    }

    public static void testClone_ChemObjectListeners(IChemObjectBuilder builder) throws Exception {
        IChemObject chemObject1 = builder.newChemObject();
        NNChemObjectListener listener = new NNChemObjectListener();
        chemObject1.addListener(listener);
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test lack of cloning of listeners
        Assert.assertEquals(0, chemObject1.getListenerCount());
        Assert.assertEquals(0, chemObject2.getListenerCount());
    }
    
    public static void testAddListener_IChemObjectListener(IChemObjectBuilder builder) {
        IChemObject chemObject1 = builder.newChemObject();
        Assert.assertEquals(0, chemObject1.getListenerCount());
        NNChemObjectListener listener = new NNChemObjectListener();
        chemObject1.addListener(listener);
        Assert.assertEquals(0, chemObject1.getListenerCount());
    }
    
    public static void testGetListenerCount(IChemObjectBuilder builder) {
        IChemObject chemObject1 = builder.newChemObject();
        NNChemObjectListener listener = new NNChemObjectListener();
        chemObject1.addListener(listener);
        Assert.assertEquals(0, chemObject1.getListenerCount());
    }

    public static void testRemoveListener_IChemObjectListener(IChemObjectBuilder builder) {
        IChemObject chemObject1 = builder.newChemObject();
        NNChemObjectListener listener = new NNChemObjectListener();
        chemObject1.addListener(listener);
        Assert.assertEquals(0, chemObject1.getListenerCount());
        chemObject1.removeListener(listener);
        Assert.assertEquals(0, chemObject1.getListenerCount());
    }
    
    public static void testSetNotification_true(IChemObjectBuilder builder) {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        chemObject.setNotification(true);
        
        chemObject.setID("Changed");
        Assert.assertFalse(listener.getChanged());
    }
}
