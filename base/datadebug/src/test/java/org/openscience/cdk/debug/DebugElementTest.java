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
 */
package org.openscience.cdk.debug;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractElementTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;

import static org.hamcrest.CoreMatchers.is;

/**
 * Checks the functionality of {@link DebugElement}.
 *
 */
class DebugElementTest extends AbstractElementTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(DebugElement::new);
    }

    @Test
    void testDebugElement() {
        IElement e = new DebugElement();
        Assertions.assertTrue(e instanceof IChemObject);
    }

    @Test
    void testDebugElement_IElement() {
        IElement element = new DebugElement();
        IElement e = new DebugElement(element);
        Assertions.assertTrue(e instanceof IChemObject);
    }

    @Test
    void testDebugElement_String() {
        IElement e = new DebugElement("C");
        Assertions.assertEquals("C", e.getSymbol());
    }

    @Test
    void testElement_X() {
        IElement e = new DebugElement("X");
        Assertions.assertEquals("R", e.getSymbol());
        // and it should not throw exceptions
        Assertions.assertNotNull(e.getAtomicNumber());
        org.hamcrest.MatcherAssert.assertThat(e.getAtomicNumber(), is(0));
    }

    @Test
    void testDebugElement_String_int() {
        IElement e = new DebugElement("H", 1);
        Assertions.assertEquals("H", e.getSymbol());
        Assertions.assertEquals(1, e.getAtomicNumber().intValue());
    }

    @Test
    void compareSymbol() {
        DebugElement e1 = new DebugElement("H", 1);
        DebugElement e2 = new DebugElement("H", 1);
        Assertions.assertTrue(e1.compare(e2));
    }

    @Test
    void compareAtomicNumber() {
        DebugElement e1 = new DebugElement("H", 1);
        DebugElement e2 = new DebugElement("H", 1);
        Assertions.assertTrue(e1.compare(e2));
    }

    @Test
    void compareDiffSymbol() {
        DebugElement e1 = new DebugElement("H", 1);
        DebugElement e2 = new DebugElement("C", 12);
        Assertions.assertFalse(e1.compare(e2));
    }

    @Test
    void compareDiffAtomicNumber() {
        DebugElement e1 = new DebugElement("H", 1);
        DebugElement e2 = new DebugElement("H", 0);
        Assertions.assertFalse(e1.compare(e2));
    }
}
