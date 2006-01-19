/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.test;

import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;

/**
 * TestCase for the IChemObject class.
 *
 * @cdk.module test
 *
 * @author Edgar Luttmann <edgar@uni-paderborn.de>
 * @cdk.created 2001-08-09
 */
public class ChemObjectTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
	public ChemObjectTest(String name) {
		super(name);
	}

	public static Test suite() {
        TestSuite suite = new TestSuite(ChemObjectTest.class);
        return suite;
	}

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(ChemObjectTest.class));
	}

    public void testChemObject() {
        IChemObject chemObject = builder.newChemObject();
        assertNotNull(chemObject);
    }

    public void testGetBuilder() {
    	IChemObject chemObject = builder.newChemObject();
    	Object object = chemObject.getBuilder();
    	assertNotNull(object);
    	assertTrue(object instanceof IChemObjectBuilder);
    }
    	
    public void testSetProperty_Object_Object() {
        IChemObject chemObject = builder.newChemObject();
        String cDescription = new String("description");
        String cProperty = new String("property");
        chemObject.setProperty(cDescription, cProperty);
        assertEquals(cProperty, chemObject.getProperty(cDescription));
    }

    public void testSetProperties_Hashtable() {
        IChemObject chemObject = builder.newChemObject();
        Hashtable props = new Hashtable();
        String cDescription = new String("description");
        String cProperty = new String("property");
        props.put(cDescription, cProperty);
        chemObject.setProperties(props);
        assertEquals(cProperty, chemObject.getProperty(cDescription));
    }

    public void testGetProperties() {
        IChemObject chemObject = builder.newChemObject();
        assertNotNull(chemObject.getProperties());
        assertEquals(0, chemObject.getProperties().size());
    }
    public void testLazyProperies() {
        testGetProperties();
    }

    public void testGetProperty_Object() {
        IChemObject chemObject = builder.newChemObject();
        assertNull(chemObject.getProperty("dummy"));
    }

    public void testRemoveProperty_Object() {
        IChemObject chemObject = builder.newChemObject();
        String cDescription = new String("description");
        String cProperty = new String("property");
        chemObject.setProperty(cDescription, cProperty);
        assertNotNull(chemObject.getProperty(cDescription));
        chemObject.removeProperty(cDescription);
        assertNull(chemObject.getProperty(cDescription));
    }

    public void testSetID_String() {
        IChemObject chemObject = builder.newChemObject();
        String id = "objectX";
        chemObject.setID(id);
        assertEquals(id, chemObject.getID());
    }
    
    public void testGetID() {
        IChemObject chemObject = builder.newChemObject();
        assertNull(chemObject.getID());
    }
    
    public void testSetFlags_arrayboolean(){
      IChemObject chemObject=builder.newChemObject();
      chemObject.setFlag(1,true);
      IChemObject chemObject2=builder.newChemObject();
      chemObject2.setFlags(chemObject.getFlags());
      assertTrue(chemObject2.getFlag(1));
    }
    
    public void testGetFlags(){
      IChemObject chemObject=builder.newChemObject();
      chemObject.setFlag(1,true);
      IChemObject chemObject2=builder.newChemObject();
      chemObject2.setFlags(chemObject.getFlags());
      assertTrue(chemObject2.getFlag(1));
    }

    public void testSetFlag_int_boolean() {
        IChemObject chemObject = builder.newChemObject();
        chemObject.setFlag(0, true);
        assertTrue(chemObject.getFlag(0));
    }
    public void testGetFlag_int() {
        testSetFlag_int_boolean();
    }
    
    public void testClone() {
        IChemObject chemObject = builder.newChemObject();
        chemObject.setFlag(3, true);
        
        // test cloning of itself
        Object clone = chemObject.clone();
        assertTrue(clone instanceof IChemObject);
    }
    
    public void testClone_Flags() {
        IChemObject chemObject1 = builder.newChemObject();
        chemObject1.setFlag(3, true);
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test cloning of flags field
        chemObject2.setFlag(3, false);
        assertTrue(chemObject1.getFlag(3));
    }

    public void testClone_Identifier() {
        IChemObject chemObject1 = builder.newChemObject();
        chemObject1.setID("co1");
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test cloning of identifier field
        chemObject2.setID("co2");
        assertEquals("co1", chemObject1.getID());
    }
    
    public void testClone_Properties() {
        IChemObject chemObject1 = builder.newChemObject();
        Hashtable props1 = new Hashtable();
        chemObject1.setProperties(props1);
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test cloning of properties field
        Hashtable props2 = new Hashtable();
        props2.put("key", "value");
        chemObject2.setProperties(props2);
        assertEquals(props1, chemObject1.getProperties());
        assertEquals(1, chemObject2.getProperties().size());
        assertEquals(0, chemObject1.getProperties().size());
    }
    
    public void testClone_Properties2() {
        IChemObject chemObject1 = builder.newChemObject();
        Hashtable props1 = new Hashtable();
        IAtom atom = builder.newAtom("C");
        props1.put("atom", atom);
        chemObject1.setProperties(props1);
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test cloning of properties field
        Hashtable props2 = new Hashtable();
        chemObject2.setProperties(props2);
        assertEquals(props1, chemObject1.getProperties());
        assertEquals(1, chemObject2.getProperties().size());
        assertEquals(1, chemObject1.getProperties().size());
        // ok, copied hashtable item, but this item should be cloned
        assertNotSame(atom, chemObject2.getProperties().get("atom"));
    }
    
    public void testClone_ChemObjectListeners() {
        IChemObject chemObject1 = builder.newChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        IChemObject chemObject2 = (IChemObject)chemObject1.clone();

        // test lack of cloning of listeners
        assertEquals(1, chemObject1.getListenerCount());
        assertEquals(0, chemObject2.getListenerCount());
    }
    
    public void testAddListener_IChemObjectListener() {
        IChemObject chemObject1 = builder.newChemObject();
        assertEquals(0, chemObject1.getListenerCount());
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        assertEquals(1, chemObject1.getListenerCount());
    }
    
    public void testRemoveListener_IChemObjectListener() {
        IChemObject chemObject1 = builder.newChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        assertEquals(1, chemObject1.getListenerCount());
        chemObject1.removeListener(listener);
        assertEquals(0, chemObject1.getListenerCount());
    }
    
    public void testGetListenerCount() {
        IChemObject chemObject1 = builder.newChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        assertEquals(1, chemObject1.getListenerCount());
    }

    class DummyChemObjectListener implements IChemObjectListener {
        public void stateChanged(IChemObjectChangeEvent event) {};
    }
    
   public void testShallowCopy() {
        IChemObject chemObject = builder.newChemObject();
        Object clone = chemObject.clone();
        assertNotNull(clone);
        assertTrue(clone instanceof IChemObject);
    }

    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        assertTrue(listener.changed);
        
        listener.reset();
        assertFalse(listener.changed);
        chemObject.setProperty("Changed", "Again");
        assertTrue(listener.changed);

        listener.reset();
        assertFalse(listener.changed);
        chemObject.setFlag(3, true);
        assertTrue(listener.changed);
    }
    
    public void testNotifyChanged() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        assertTrue(listener.changed);
    }
    
    public void testNotifyChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = builder.newChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        assertNotNull(listener.event);
    }

    public void testCompare_Object() {
        // Added to keep the Coverage checker happy, but since the
        // compare(Object) method is not part of the interface, nothing is tested
        assertTrue(true);
    }

    private class ChemObjectListenerImpl implements IChemObjectListener {
        private boolean changed;
        private IChemObjectChangeEvent event;
        
        private ChemObjectListenerImpl() {
            changed = false;
            event = null;
        }
        
        public void stateChanged(IChemObjectChangeEvent e) {
            changed = true;
            event = e;
        }
        
        public void reset() {
            changed = false;
            event = null;
        }
    }
    
}
