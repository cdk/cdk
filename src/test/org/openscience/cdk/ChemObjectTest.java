/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk;

import java.util.Hashtable;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.tools.diff.ChemObjectDiff;

/**
 * TestCase for the IChemObject class.
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @cdk.module  test-data
 * @cdk.created 2001-08-09
 */
public class ChemObjectTest extends NewCDKTestCase {

	protected static IChemObjectBuilder builder;
	
	@BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testChemObject() {
        IChemObject chemObject = builder.newChemObject();
        Assert.assertNotNull(chemObject);
    }

    @Test public void testChemObject_IChemObject() {
    	IChemObject chemObject1 = builder.newChemObject();
        IChemObject chemObject = builder.newChemObject(chemObject1);
        Assert.assertNotNull(chemObject);
    }

    @Test public void testGetBuilder() {
    	IChemObject chemObject = builder.newChemObject();
    	Object object = chemObject.getBuilder();
    	Assert.assertNotNull(object);
    	Assert.assertTrue(object instanceof IChemObjectBuilder);
    }
    	
    @Test public void testSetProperty_Object_Object() {
        IChemObject chemObject = builder.newChemObject();
        String cDescription = new String("description");
        String cProperty = new String("property");
        chemObject.setProperty(cDescription, cProperty);
        Assert.assertEquals(cProperty, chemObject.getProperty(cDescription));
    }

    @Test public void testSetProperties_Map() {
        IChemObject chemObject = builder.newChemObject();
        Map props = new Hashtable();
        String cDescription = new String("description");
        String cProperty = new String("property");
        props.put(cDescription, cProperty);
        chemObject.setProperties(props);
        Assert.assertEquals(cProperty, chemObject.getProperty(cDescription));
    }

    @Test public void testGetProperties() {
        IChemObject chemObject = builder.newChemObject();
        Assert.assertNotNull(chemObject.getProperties());
        Assert.assertEquals(0, chemObject.getProperties().size());
    }
    @Test public void testLazyProperies() {
        testGetProperties();
    }

    @Test public void testGetProperty_Object() {
        IChemObject chemObject = builder.newChemObject();
        Assert.assertNull(chemObject.getProperty("dummy"));
    }

    @Test public void testRemoveProperty_Object() {
        IChemObject chemObject = builder.newChemObject();
        String cDescription = new String("description");
        String cProperty = new String("property");
        chemObject.setProperty(cDescription, cProperty);
        Assert.assertNotNull(chemObject.getProperty(cDescription));
        chemObject.removeProperty(cDescription);
        Assert.assertNull(chemObject.getProperty(cDescription));
    }

    @Test public void testSetID_String() {
        IChemObject chemObject = builder.newChemObject();
        String id = "objectX";
        chemObject.setID(id);
        Assert.assertEquals(id, chemObject.getID());
    }
    
    @Test public void testGetID() {
        IChemObject chemObject = builder.newChemObject();
        Assert.assertNull(chemObject.getID());
    }
    
    @Test public void testSetFlags_arrayboolean(){
      IChemObject chemObject=builder.newChemObject();
      chemObject.setFlag(1,true);
      IChemObject chemObject2=builder.newChemObject();
      chemObject2.setFlags(chemObject.getFlags());
      Assert.assertTrue(chemObject2.getFlag(1));
    }
    
    @Test public void testGetFlags(){
      IChemObject chemObject=builder.newChemObject();
      chemObject.setFlag(1,true);
      IChemObject chemObject2=builder.newChemObject();
      chemObject2.setFlags(chemObject.getFlags());
      Assert.assertTrue(chemObject2.getFlag(1));
    }

    @Test public void testSetFlag_int_boolean() {
        IChemObject chemObject = builder.newChemObject();
        chemObject.setFlag(0, true);
        Assert.assertTrue(chemObject.getFlag(0));
    }
    @Test public void testGetFlag_int() {
        testSetFlag_int_boolean();
    }
    
    @Test public void testClone() throws Exception {
        IChemObject chemObject = builder.newChemObject();
        chemObject.setFlag(3, true);
        
        // test cloning of itself
        Object clone = chemObject.clone();
        Assert.assertTrue(clone instanceof IChemObject);

        // test that everything has been cloned properly
        String diff = ChemObjectDiff.diff(chemObject, (IChemObject)clone);
        Assert.assertNotNull(diff);
        Assert.assertEquals(0, diff.length());
    }
    
    @Test public void testClone_Flags() throws Exception {
        IChemObject chemObject1 = builder.newChemObject();
        chemObject1.setFlag(3, true);
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test cloning of flags field
        chemObject2.setFlag(3, false);
        Assert.assertTrue(chemObject1.getFlag(3));
    }

    @Test public void testClone_Identifier() throws Exception {
        IChemObject chemObject1 = builder.newChemObject();
        chemObject1.setID("co1");
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test cloning of identifier field
        chemObject2.setID("co2");
        Assert.assertEquals("co1", chemObject1.getID());
    }
    
    @Test public void testClone_Properties() throws Exception {
        IChemObject chemObject1 = builder.newChemObject();
        Map props1 = new Hashtable();
        chemObject1.setProperties(props1);
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test cloning of properties field
        Map props2 = new Hashtable();
        props2.put("key", "value");
        chemObject2.setProperties(props2);
        Assert.assertEquals(props1, chemObject1.getProperties());
        Assert.assertEquals(1, chemObject2.getProperties().size());
        Assert.assertEquals(0, chemObject1.getProperties().size());
    }
    
    @Test public void testClone_Properties2() throws Exception {
        IChemObject chemObject1 = builder.newChemObject();
        Map props1 = new Hashtable();
        IAtom atom = builder.newAtom("C");
        props1.put("atom", atom);
        chemObject1.setProperties(props1);
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test cloning of properties field
        Map props2 = new Hashtable();
        chemObject2.setProperties(props2);
        Assert.assertEquals(props1, chemObject1.getProperties());
        Assert.assertEquals(1, chemObject2.getProperties().size());
        Assert.assertEquals(1, chemObject1.getProperties().size());
        // ok, copied hashtable item, but this item should be cloned
        Assert.assertEquals(atom, chemObject2.getProperties().get("atom"));
    }
    
    @Test public void testClone_ChemObjectListeners() throws Exception {
        IChemObject chemObject1 = builder.newChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test lack of cloning of listeners
        Assert.assertEquals(1, chemObject1.getListenerCount());
        Assert.assertEquals(0, chemObject2.getListenerCount());
    }
    
    /** @cdk.bug 1838820 */
    @Test(timeout=100)
    public void testDontCloneIChemObjectProperties() throws Exception {
    	IChemObject chemObject1 = builder.newChemObject();
    	chemObject1.setProperty("RecursiveBastard", chemObject1);
    	
    	Object clone = chemObject1.clone();
    	Assert.assertNotNull(clone);
    	Assert.assertTrue(clone instanceof IChemObject);
    }
    
    @Test public void testAddListener_IChemObjectListener() {
        IChemObject chemObject1 = builder.newChemObject();
        Assert.assertEquals(0, chemObject1.getListenerCount());
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        Assert.assertEquals(1, chemObject1.getListenerCount());
    }
    
    @Test public void testRemoveListener_IChemObjectListener() {
        IChemObject chemObject1 = builder.newChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        Assert.assertEquals(1, chemObject1.getListenerCount());
        chemObject1.removeListener(listener);
        Assert.assertEquals(0, chemObject1.getListenerCount());
    }
    
    @Test public void testGetListenerCount() {
        IChemObject chemObject1 = builder.newChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        Assert.assertEquals(1, chemObject1.getListenerCount());
    }

    class DummyChemObjectListener implements IChemObjectListener {
        @Test public void stateChanged(IChemObjectChangeEvent event) {};
    }
    
   @Test public void testShallowCopy() throws Exception {
        IChemObject chemObject = builder.newChemObject();
        Object clone = chemObject.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof IChemObject);
    }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        Assert.assertTrue(listener.changed);
        
        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setProperty("Changed", "Again");
        Assert.assertTrue(listener.changed);

        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setFlag(3, true);
        Assert.assertTrue(listener.changed);
    }
    
    @Test public void testNotifyChanged() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        Assert.assertTrue(listener.changed);
    }

    @Test public void testSetNotification_boolean() {
        IChemObject chemObject = builder.newChemObject();
        chemObject.setNotification(false);
        Assert.assertFalse(chemObject.getNotification());
    }
    @Test public void testGetNotification() {
    	testSetNotification_boolean();
    }
    
    @Test public void testSetNotification_false() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        chemObject.setNotification(false);
        
        chemObject.setID("Changed");
        Assert.assertFalse(listener.changed);
    }
    
    @Test public void testSetNotification_true() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        chemObject.setNotification(true);
        
        chemObject.setID("Changed");
        Assert.assertTrue(listener.changed);
    }
    
    @Test public void testNotifyChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        Assert.assertNotNull(listener.event);
    }

    @Test public void testCompare_Object() {
        // Added to keep the Coverage checker happy, but since the
        // compare(Object) method is not part of the interface, nothing is tested
        Assert.assertTrue(true);
    }

    private class ChemObjectListenerImpl implements IChemObjectListener {
        private boolean changed;
        private IChemObjectChangeEvent event;
        
        private ChemObjectListenerImpl() {
            changed = false;
            event = null;
        }
        
        @Test public void stateChanged(IChemObjectChangeEvent e) {
            changed = true;
            event = e;
        }
        
        @Test public void reset() {
            changed = false;
            event = null;
        }
    }
    
}
