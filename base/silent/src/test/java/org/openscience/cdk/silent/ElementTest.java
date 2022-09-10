/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.silent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.test.interfaces.AbstractElementTest;

import static org.hamcrest.CoreMatchers.is;

/**
 * Checks the functionality of {@link Element}.
 *
 * @cdk.module test-silent
 */
public class ElementTest extends AbstractElementTest {

    @BeforeAll
    public static void setUp() {
        setTestObjectBuilder(Element::new);
    }

    @Test
    public void testElement() {
        IElement e = new Element();
        Assertions.assertTrue(e instanceof IChemObject);
    }

    @Test
    public void testElement_IElement() {
        IElement element = new Element();
        IElement e = element.getBuilder().newInstance(IElement.class, element);
        Assertions.assertTrue(e instanceof IChemObject);
    }

    @Test
    public void testElement_String() {
        IElement e = new Element("C");
        Assertions.assertEquals("C", e.getSymbol());
    }

    @Test
    public void testElement_X() {
        IElement e = new Element("X");
        Assertions.assertEquals("R", e.getSymbol());
        // and it should not throw exceptions
        Assertions.assertNotNull(e.getAtomicNumber());
        org.hamcrest.MatcherAssert.assertThat(e.getAtomicNumber(), is(0));
    }

    @Test
    public void testElement_String_Integer() {
        IElement e = new Element("H", 1);
        Assertions.assertEquals("H", e.getSymbol());
        Assertions.assertEquals(1, e.getAtomicNumber().intValue());
    }

    // Overwrite default methods: no notifications are expected!

    @Test
    @Override
    public void testNotifyChanged() {
        ChemObjectTestHelper.testNotifyChanged(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_SetFlag() {
        ChemObjectTestHelper.testNotifyChanged_SetFlag(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_SetFlags() {
        ChemObjectTestHelper.testNotifyChanged_SetFlags(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_IChemObjectChangeEvent() {
        ChemObjectTestHelper.testNotifyChanged_IChemObjectChangeEvent(newChemObject());
    }

    @Test
    @Override
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectTestHelper.testStateChanged_IChemObjectChangeEvent(newChemObject());
    }

    @Test
    @Override
    public void testClone_ChemObjectListeners() throws Exception {
        ChemObjectTestHelper.testClone_ChemObjectListeners(newChemObject());
    }

    @Test
    @Override
    public void testAddListener_IChemObjectListener() {
        ChemObjectTestHelper.testAddListener_IChemObjectListener(newChemObject());
    }

    @Test
    @Override
    public void testGetListenerCount() {
        ChemObjectTestHelper.testGetListenerCount(newChemObject());
    }

    @Test
    @Override
    public void testRemoveListener_IChemObjectListener() {
        ChemObjectTestHelper.testRemoveListener_IChemObjectListener(newChemObject());
    }

    @Test
    @Override
    public void testSetNotification_true() {
        ChemObjectTestHelper.testSetNotification_true(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_SetProperty() {
        ChemObjectTestHelper.testNotifyChanged_SetProperty(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_RemoveProperty() {
        ChemObjectTestHelper.testNotifyChanged_RemoveProperty(newChemObject());
    }

    @Test
    public void compareSymbol() {
        Element e1 = new Element("H", 1);
        Element e2 = new Element("H", 1);
        Assertions.assertTrue(e1.compare(e2));
    }

    @Test
    public void compareAtomicNumber() {
        Element e1 = new Element("H", 1);
        Element e2 = new Element("H", 1);
        Assertions.assertTrue(e1.compare(e2));
    }

    @Test
    public void compareDiffSymbol() {
        Element e1 = new Element("H", 1);
        Element e2 = new Element("C", 12);
        Assertions.assertFalse(e1.compare(e2));
    }

    @Test
    public void compareDiffAtomicNumber() {
        Element e1 = new Element("H", 1);
        Element e2 = new Element("H", null);
        Assertions.assertFalse(e1.compare(e2));
    }
}
