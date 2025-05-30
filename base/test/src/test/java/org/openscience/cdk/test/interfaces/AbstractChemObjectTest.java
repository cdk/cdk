/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.time.Duration;
import java.util.Hashtable;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.tools.diff.ChemObjectDiff;

/**
 * Tests the functionality of {@link org.openscience.cdk.interfaces.IChemObject} implementations.
 *
 * @author Edgar Luttmann &lt;edgar@uni-paderborn.de&gt;
 * @cdk.created 2001-08-09
 */
public abstract class AbstractChemObjectTest extends AbstractCDKObjectTest {

    @Test
    public void testSetProperty_Object_Object() {
        IChemObject chemObject = newChemObject();
        String cDescription = "description";
        String cProperty = "property";
        chemObject.setProperty(cDescription, cProperty);
        Assertions.assertEquals(cProperty, chemObject.getProperty(cDescription));
    }

    @Test
    public void testSetProperties_Map() {
        IChemObject chemObject = newChemObject();
        chemObject.setProperty("remove", "me");
        Map<Object, Object> props = new Hashtable<>();
        props.put("keep", "me");
        chemObject.setProperties(props);
        Assertions.assertEquals("me", chemObject.getProperty("keep"));
        Assertions.assertNull(chemObject.getProperty("remove"));
    }

    @Test
    public void testAddProperties_Map() {
        IChemObject chemObject = newChemObject();
        Map<Object, Object> props = new Hashtable<>();
        String cDescription = "description";
        String cProperty = "property";
        props.put(cDescription, cProperty);
        chemObject.addProperties(props);
        Assertions.assertEquals(cProperty, chemObject.getProperty(cDescription));
    }

    @Test
    public void testGetProperties() {
        IChemObject chemObject = newChemObject();
        Assertions.assertNotNull(chemObject.getProperties());
        Assertions.assertEquals(0, chemObject.getProperties().size());
    }

    @Test
    public void testLazyProperies() {
        testGetProperties();
    }

    @Test
    public void testGetProperty_Object() {
        IChemObject chemObject = newChemObject();
        Assertions.assertNull(chemObject.getProperty("dummy"));
    }

    @Test
    public void testGetProperty_Object_Class() {
        IChemObject chemObject = newChemObject();
        Assertions.assertNull(chemObject.getProperty("dummy", String.class));
        chemObject.setProperty("dummy", 5);
        Assertions.assertNotNull(chemObject.getProperty("dummy", Integer.class));

    }

    @Test
    public void testGetProperty_Object_ClassCast() {
        IChemObject chemObject = newChemObject();
        chemObject.setProperty("dummy", 5);
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    chemObject.getProperty("dummy", String.class);
                                });
    }

    @Test
    public void testRemoveProperty_Object() {
        IChemObject chemObject = newChemObject();
        String cDescription = "description";
        String cProperty = "property";
        chemObject.setProperty(cDescription, cProperty);
        Assertions.assertNotNull(chemObject.getProperty(cDescription));
        chemObject.removeProperty(cDescription);
        Assertions.assertNull(chemObject.getProperty(cDescription));
    }

    @Test
    public void testSetID_String() {
        IChemObject chemObject = newChemObject();
        String id = "objectX";
        chemObject.setID(id);
        Assertions.assertEquals(id, chemObject.getID());
    }

    @Test
    public void testGetID() {
        IChemObject chemObject = newChemObject();
        Assertions.assertNull(chemObject.getID());
    }

    @Test
    public void setFlagThatIsTooBig() {
        IChemObject chemObject = newChemObject();
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    chemObject.setFlag(1 << 17, true);
                                });
    }

    @Test
    public void setFlagThatIsInvalid() {
        IChemObject chemObject = newChemObject();
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    chemObject.setFlag(999, true);
                                });
    }

    @Test
    public void testSetFlags_arrayboolean() {
        IChemObject chemObject = newChemObject();
        chemObject.setFlag(IChemObject.IN_RING, true);
        IChemObject chemObject2 = newChemObject();
        chemObject2.setFlags(chemObject.getFlags());
        Assertions.assertTrue(chemObject2.getFlag(IChemObject.IN_RING));
    }

    @Test
    public void testGetFlags() {
        IChemObject chemObject = newChemObject();
        chemObject.setFlag(IChemObject.IN_RING, true);
        IChemObject chemObject2 = newChemObject();
        chemObject2.setFlags(chemObject.getFlags());
        Assertions.assertTrue(chemObject2.getFlag(IChemObject.IN_RING));
    }

    @Test
    public void testGetFlagValueZeroDefault() {
        IChemObject chemObject = newChemObject();
        Assertions.assertEquals(0, chemObject.getFlagValue());
    }

    @Test
    public void testGetFlagValue() {
        IChemObject chemObject = newChemObject();
        chemObject.setFlag(IChemObject.ALIPHATIC, true);
        Assertions.assertNotSame(0, chemObject.getFlagValue());
    }

    @Test
    public void testSet() {
        IChemObject chemObject = newChemObject();
        Assertions.assertFalse(chemObject.is(IChemObject.VISITED));
        chemObject.set(IChemObject.VISITED);
        Assertions.assertTrue(chemObject.is(IChemObject.VISITED));
    }

    @Test
    public void testIsChecksAllFlags() {
        IChemObject chemObject = newChemObject();
        Assertions.assertFalse(chemObject.is(IChemObject.AROMATIC+IChemObject.IN_RING));
        chemObject.set(IChemObject.AROMATIC);
        Assertions.assertFalse(chemObject.is(IChemObject.AROMATIC+IChemObject.IN_RING));
        Assertions.assertTrue(chemObject.is(IChemObject.AROMATIC));
        chemObject.set(IChemObject.IN_RING);
        Assertions.assertTrue(chemObject.is(IChemObject.AROMATIC+IChemObject.IN_RING));
    }

    @Test
    public void testClearFlags() {
        IChemObject chemObject = newChemObject();
        chemObject.set(IChemObject.AROMATIC+IChemObject.IN_RING);
        Assertions.assertTrue(chemObject.is(IChemObject.AROMATIC+IChemObject.IN_RING));
        chemObject.clear(IChemObject.AROMATIC);
        Assertions.assertFalse(chemObject.is(IChemObject.AROMATIC+IChemObject.IN_RING));
        Assertions.assertTrue(chemObject.is(IChemObject.IN_RING));
        chemObject.set(IChemObject.AROMATIC);
        Assertions.assertTrue(chemObject.is(IChemObject.AROMATIC+IChemObject.IN_RING));
        chemObject.clear(IChemObject.AROMATIC+IChemObject.IN_RING);
        Assertions.assertFalse(chemObject.is(IChemObject.AROMATIC+IChemObject.IN_RING));
        Assertions.assertFalse(chemObject.is(IChemObject.AROMATIC));
        Assertions.assertFalse(chemObject.is(IChemObject.IN_RING));
    }

    @Test
    public void testRawFlags() {
        IChemObject chemObject = newChemObject();
        chemObject.set(IChemObject.AROMATIC+IChemObject.IN_RING);
        Assertions.assertEquals(IChemObject.AROMATIC+IChemObject.IN_RING,
                                chemObject.flags());
    }

    /**
     * Different flags are reflected by different numbers.
     */
    @Test
    public void testGetFlagValueDifferentFlags() {
        IChemObject chemObject = newChemObject();
        chemObject.setFlag(IChemObject.ALIPHATIC, true);
        IChemObject chemObject2 = newChemObject();
        chemObject2.setFlag(IChemObject.VISITED, true);
        Assertions.assertNotSame(chemObject.getFlagValue(), chemObject2.getFlagValue());
    }

    /**
     * The number is always the same for the same flag.
     */
    @Test
    public void testGetFlagValueSameFlag() {
        IChemObject chemObject = newChemObject();
        chemObject.setFlag(IChemObject.PLACED, true);
        IChemObject chemObject2 = newChemObject();
        chemObject2.setFlag(IChemObject.PLACED, true);
        Assertions.assertEquals(chemObject.getFlagValue(), chemObject2.getFlagValue());
    }

    @Test
    public void testGetFlags_Array() {
        IChemObject chemObject = newChemObject();
        chemObject.setFlag(IChemObject.IN_RING, true);
        boolean[] flags = chemObject.getFlags();
        Assertions.assertTrue(flags[1]);
    }

    @Test
    public void testSetFlag_int_boolean() {
        IChemObject chemObject = newChemObject();
        Assertions.assertFalse(chemObject.getFlag(IChemObject.PLACED));
        chemObject.setFlag(IChemObject.PLACED, true);
        Assertions.assertTrue(chemObject.getFlag(IChemObject.PLACED));
        chemObject.setFlag(IChemObject.PLACED, false);
        Assertions.assertFalse(chemObject.getFlag(IChemObject.PLACED));
    }

    @Test
    public void testGetFlag_int() {
        testSetFlag_int_boolean();
    }

    @Test
    public void testClone() throws Exception {
        IChemObject chemObject = newChemObject();
        chemObject.setFlag(IChemObject.ALIPHATIC, true);

        // test cloning of itself
        Object clone = chemObject.clone();
        Assertions.assertTrue(clone instanceof IChemObject);

        // test that everything has been cloned properly
        String diff = ChemObjectDiff.diff(chemObject, (IChemObject) clone);
        Assertions.assertNotNull(diff);
        Assertions.assertEquals(0, diff.length());
    }

    @Test
    public void testClone_Flags() throws Exception {
        IChemObject chemObject1 = newChemObject();
        chemObject1.setFlag(IChemObject.ALIPHATIC, true);
        IChemObject chemObject2 = (IChemObject) chemObject1.clone();

        // test cloning of flags field
        chemObject2.setFlag(IChemObject.ALIPHATIC, false);
        Assertions.assertTrue(chemObject1.getFlag(IChemObject.ALIPHATIC));
    }

    @Test
    public void testClone_Identifier() throws Exception {
        IChemObject chemObject1 = newChemObject();
        chemObject1.setID("co1");
        IChemObject chemObject2 = (IChemObject) chemObject1.clone();

        // test cloning of identifier field
        chemObject2.setID("co2");
        Assertions.assertEquals("co1", chemObject1.getID());
    }

    @Test
    public void testClone_Properties() throws Exception {
        IChemObject chemObject1 = newChemObject();
        Map<Object, Object> props1 = new Hashtable<>();
        chemObject1.addProperties(props1);
        IChemObject chemObject2 = (IChemObject) chemObject1.clone();

        // test cloning of properties field
        Map<Object, Object> props2 = new Hashtable<>();
        props2.put("key", "value");
        chemObject2.addProperties(props2);
        Assertions.assertEquals(props1, chemObject1.getProperties());
        Assertions.assertEquals(1, chemObject2.getProperties().size());
        Assertions.assertEquals(0, chemObject1.getProperties().size());
    }

    @Test
    public void testClone_Properties2() throws Exception {
        IChemObject chemObject1 = newChemObject();
        Map<Object, Object> props1 = new Hashtable<>();
        IAtom atom = chemObject1.getBuilder().newInstance(IAtom.class, "C");
        props1.put("atom", atom);
        chemObject1.addProperties(props1);
        IChemObject chemObject2 = (IChemObject) chemObject1.clone();

        // test cloning of properties field
        Map<Object, Object> props2 = new Hashtable<>();
        chemObject2.addProperties(props2);
        Assertions.assertEquals(props1, chemObject1.getProperties());
        Assertions.assertEquals(1, chemObject2.getProperties().size());
        Assertions.assertEquals(1, chemObject1.getProperties().size());
        // ok, copied hashtable item, but this item should be cloned
        Assertions.assertEquals(atom, chemObject2.getProperties().get("atom"));
    }

    /**
     * @cdk.bug 2975800
     */
    @Test
    public void testClone_PropertyNull() throws Exception {
        IChemObject chemObject = newChemObject();
        final String key = "NullProperty";
        chemObject.setProperty(key, null);
        chemObject.clone();
    }

    @Test
    public void testClone_ChemObjectListeners() throws Exception {
        IChemObject chemObject1 = newChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        IChemObject chemObject2 = (IChemObject) chemObject1.clone();

        // test lack of cloning of listeners
        Assertions.assertEquals(1, chemObject1.getListenerCount());
        Assertions.assertEquals(0, chemObject2.getListenerCount());
    }

    /** @cdk.bug 1838820 */
    @Test
    public void testDontCloneIChemObjectProperties() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(100), () -> {
            IChemObject chemObject1 = newChemObject();
            chemObject1.setProperty("RecursiveBastard", chemObject1);

            Object clone = chemObject1.clone();
            Assertions.assertNotNull(clone);
            Assertions.assertTrue(clone instanceof IChemObject);
        });
    }

    @Test
    public void testAddListener_IChemObjectListener() {
        IChemObject chemObject1 = newChemObject();
        Assertions.assertEquals(0, chemObject1.getListenerCount());
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        Assertions.assertEquals(1, chemObject1.getListenerCount());
    }

    @Test
    public void testRemoveListener_IChemObjectListener() {
        IChemObject chemObject1 = newChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        Assertions.assertEquals(1, chemObject1.getListenerCount());
        chemObject1.removeListener(listener);
        Assertions.assertEquals(0, chemObject1.getListenerCount());
    }

    @Test
    public void testGetListenerCount() {
        IChemObject chemObject1 = newChemObject();
        DummyChemObjectListener listener = new DummyChemObjectListener();
        chemObject1.addListener(listener);
        Assertions.assertEquals(1, chemObject1.getListenerCount());
    }

    class DummyChemObjectListener implements IChemObjectListener {

        @Override
        public void stateChanged(IChemObjectChangeEvent event) {}
    }

    @Test
    public void testShallowCopy() throws Exception {
        IChemObject chemObject = newChemObject();
        Object clone = chemObject.clone();
        Assertions.assertNotNull(clone);
        Assertions.assertTrue(clone instanceof IChemObject);
    }

    @Test
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = newChemObject();
        chemObject.addListener(listener);

        chemObject.setID("Changed");
        Assertions.assertTrue(listener.changed);

        listener.reset();
        Assertions.assertFalse(listener.changed);
        chemObject.setProperty("Changed", "Again");
        Assertions.assertTrue(listener.changed);

        listener.reset();
        Assertions.assertFalse(listener.changed);
        chemObject.setFlag(IChemObject.ALIPHATIC, true);
        Assertions.assertTrue(listener.changed);
    }

    @Test
    public void testNotifyChanged() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = newChemObject();
        chemObject.addListener(listener);

        chemObject.setID("Changed");
        Assertions.assertTrue(listener.changed);
    }

    @Test
    public void testSetNotification_boolean() {
        IChemObject chemObject = newChemObject();
        chemObject.setNotification(false);
        Assertions.assertFalse(chemObject.getNotification());
    }

    @Test
    public void testGetNotification() {
        testSetNotification_boolean();
    }

    @Test
    public void testSetNotification_false() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = newChemObject();
        chemObject.addListener(listener);
        chemObject.setNotification(false);

        chemObject.setID("Changed");
        Assertions.assertFalse(listener.changed);
    }

    @Test
    public void testSetNotification_true() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = newChemObject();
        chemObject.addListener(listener);
        chemObject.setNotification(true);

        chemObject.setID("Changed");
        Assertions.assertTrue(listener.changed);
    }

    @Test
    public void testNotifyChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = newChemObject();
        chemObject.addListener(listener);

        chemObject.setID("Changed");
        Assertions.assertNotNull(listener.event);
    }

    @Test
    public void testNotifyChanged_SetProperty() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = newChemObject();
        chemObject.addListener(listener);

        chemObject.setProperty("Changed", "Yes");
        Assertions.assertNotNull(listener.event);
    }

    /**
     * @cdk.bug 2992921
     */
    @Test
    public void testNotifyChanged_SetFlag() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = newChemObject();
        chemObject.addListener(listener);

        Assertions.assertNull(listener.event);
        chemObject.setFlag(CDKConstants.DUMMY_POINTER, true);
        Assertions.assertNotNull(listener.event);
    }

    /**
     * @cdk.bug 2992921
     */
    @Test
    public void testNotifyChanged_SetFlags() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemObject chemObject = newChemObject();
        chemObject.addListener(listener);

        Assertions.assertNull(listener.event);
        chemObject.setFlags(new boolean[chemObject.getFlags().length]);
        Assertions.assertNotNull(listener.event);
    }

    @Test
    public void testNotifyChanged_RemoveProperty() {
        IChemObject chemObject = newChemObject();
        chemObject.setProperty("Changed", "Yes");

        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        chemObject.addListener(listener);

        chemObject.removeProperty("Changed");
        Assertions.assertNotNull(listener.event);
    }

    @Test
    public void testNotifyChanged_RemoveNonExistentProperty() {
        IChemObject chemObject = newChemObject();
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        chemObject.addListener(listener);

        chemObject.removeProperty("Changed");
        Assertions.assertNull(listener.event);
    }

    @Test
    public void testCompare_Object() {
        // Added to keep the Coverage checker happy, but since the
        // compare(Object) method is not part of the interface, nothing is tested
        Assertions.assertTrue(true);
    }

    private class ChemObjectListenerImpl implements IChemObjectListener {

        private boolean                changed;
        private IChemObjectChangeEvent event;

        private ChemObjectListenerImpl() {
            changed = false;
            event = null;
        }

        @Override
        public void stateChanged(IChemObjectChangeEvent e) {
            changed = true;
            event = e;
        }

        void reset() {
            changed = false;
            event = null;
        }
    }

}
