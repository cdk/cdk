/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test;

import java.util.Hashtable;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemObjectListener;
import org.openscience.cdk.event.ChemObjectChangeEvent;

/**
 * TestCase for the ChemObject class.
 *
 * @cdk.module test
 *
 * @author Edgar Luttmann <edgar@uni-paderborn.de>
 * @cdk.created 2001-08-09
 */
public class ChemObjectTest extends TestCase {

	public ChemObjectTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ChemObjectTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(ChemObjectTest.class));
	}

    public void testChemObject() {
        ChemObject chemObject = new ChemObject();
        assertNotNull(chemObject);
    }
    
    public void testSetProperty_Object_Object() {
        ChemObject chemObject = new ChemObject();
        String cDescription = new String("description");
        String cProperty = new String("property");
        chemObject.setProperty(cDescription, cProperty);
        assertEquals(cProperty, chemObject.getProperty(cDescription));
    }

    public void testSetProperties_Hashtable() {
        ChemObject chemObject = new ChemObject();
        Hashtable props = new Hashtable();
        String cDescription = new String("description");
        String cProperty = new String("property");
        props.put(cDescription, cProperty);
        chemObject.setProperties(props);
        assertEquals(cProperty, chemObject.getProperty(cDescription));
    }

    public void testGetProperties() {
        ChemObject chemObject = new ChemObject();
        assertNotNull(chemObject.getProperties());
        assertEquals(0, chemObject.getProperties().size());
    }
    public void testLazyProperies() {
        testGetProperties();
    }

    public void testGetProperty_Object() {
        ChemObject chemObject = new ChemObject();
        assertNull(chemObject.getProperty("dummy"));
    }

    public void testRemoveProperty_Object() {
        ChemObject chemObject = new ChemObject();
        String cDescription = new String("description");
        String cProperty = new String("property");
        chemObject.setProperty(cDescription, cProperty);
        assertNotNull(chemObject.getProperty(cDescription));
        chemObject.removeProperty(cDescription);
        assertNull(chemObject.getProperty(cDescription));
    }

    public void testSetID_String() {
        ChemObject chemObject = new ChemObject();
        String id = "objectX";
        chemObject.setID(id);
        assertEquals(id, chemObject.getID());
    }
    
    public void testGetID() {
        ChemObject chemObject = new ChemObject();
        assertNull(chemObject.getID());
    }
    
    public void testSetFlag_int_boolean() {
        ChemObject chemObject = new ChemObject();
        chemObject.setFlag(0, true);
        assertTrue(chemObject.getFlag(0));
    }
    public void testGetFlag_int() {
        testSetFlag_int_boolean();
    }
    
    public void testClone() {
        ChemObject chemObject = new ChemObject();
        chemObject.setFlag(3, true);
        
        // test cloning of itself
        Object clone = chemObject.clone();
        assertTrue(clone instanceof ChemObject);
    }
    
    public void testClone_Flags() {
        ChemObject chemObject1 = new ChemObject();
        chemObject1.setFlag(3, true);
        ChemObject chemObject2 = (ChemObject)chemObject1.clone();

        // test cloning of flags field
        chemObject2.setFlag(3, false);
        assertTrue(chemObject1.getFlag(3));
    }

    public void testClone_Identifier() {
        ChemObject chemObject1 = new ChemObject();
        chemObject1.setID("co1");
        ChemObject chemObject2 = (ChemObject)chemObject1.clone();

        // test cloning of identifier field
        chemObject2.setID("co2");
        assertEquals("co1", chemObject1.getID());
    }
    
    public void testClone_Properties() {
        ChemObject chemObject1 = new ChemObject();
        Hashtable props1 = new Hashtable();
        chemObject1.setProperties(props1);
        ChemObject chemObject2 = (ChemObject)chemObject1.clone();

        // test cloning of properties field
        Hashtable props2 = new Hashtable();
        props2.put("key", "value");
        chemObject2.setProperties(props2);
        assertEquals(props1, chemObject1.getProperties());
        assertEquals(1, chemObject2.getProperties().size());
        assertEquals(0, chemObject1.getProperties().size());
    }
    
    public void testClone_Properties2() {
        ChemObject chemObject1 = new ChemObject();
        Hashtable props1 = new Hashtable();
        Atom atom = new Atom("C");
        props1.put("atom", atom);
        chemObject1.setProperties(props1);
        ChemObject chemObject2 = (ChemObject)chemObject1.clone();

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
        ChemObject chemObject1 = new ChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        ChemObject chemObject2 = (ChemObject)chemObject1.clone();

        // test lack of cloning of listeners
        assertEquals(1, chemObject1.getListenerCount());
        assertEquals(0, chemObject2.getListenerCount());
    }
    
    public void testAddListener_ChemObjectListener() {
        ChemObject chemObject1 = new ChemObject();
        assertEquals(0, chemObject1.getListenerCount());
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        assertEquals(1, chemObject1.getListenerCount());
    }
    
    public void testRemoveListener_ChemObjectListener() {
        ChemObject chemObject1 = new ChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        assertEquals(1, chemObject1.getListenerCount());
        chemObject1.removeListener(listener);
        assertEquals(0, chemObject1.getListenerCount());
    }
    
    public void testGetListenerCount() {
        ChemObject chemObject1 = new ChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        assertEquals(1, chemObject1.getListenerCount());
    }

    class DummyChemObjectListener implements ChemObjectListener {
        public void stateChanged(ChemObjectChangeEvent event) {};
    }
    
   public void testShallowCopy() {
        ChemObject chemObject = new ChemObject();
        Object clone = chemObject.clone();
        assertNotNull(clone);
        assertTrue(clone instanceof ChemObject);
    }

    public void testCompare_Object() {
        ChemObject chemObject = new ChemObject();
        assertTrue(chemObject.compare(chemObject));
        ChemObject secondObject = new ChemObject();
        assertTrue(chemObject.compare(secondObject));
        secondObject.setID("second");
        assertFalse(chemObject.compare(secondObject));
    }
    
    public void testStateChanged_ChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        ChemObject chemObject = new ChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        assertTrue(listener.changed);
    }
    
    public void testNotifyChanged() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        ChemObject chemObject = new ChemObject();
        chemObject.addListener(listener);
        
        chemObject.setID("Changed");
        assertTrue(listener.changed);
    }
    
    private class ChemObjectListenerImpl implements ChemObjectListener {
        private boolean changed;
        
        private ChemObjectListenerImpl() {
            changed = false;
        }
        
        public void stateChanged(ChemObjectChangeEvent e) {
            changed = true;
        }
        
        public void reset() {
            changed = false;
        }
    }
}
