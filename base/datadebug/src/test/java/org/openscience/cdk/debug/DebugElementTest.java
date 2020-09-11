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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractElementTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

import static org.hamcrest.CoreMatchers.is;

/**
 * Checks the functionality of {@link DebugElement}.
 *
 * @cdk.module test-datadebug
 */
public class DebugElementTest extends AbstractElementTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new DebugElement();
            }
        });
    }

    @Test
    public void testDebugElement() {
        IElement e = new DebugElement();
        Assert.assertTrue(e instanceof IChemObject);
    }

    @Test
    public void testDebugElement_IElement() {
        IElement element = new DebugElement();
        IElement e = new DebugElement(element);
        Assert.assertTrue(e instanceof IChemObject);
    }

    @Test
    public void testDebugElement_String() {
        IElement e = new DebugElement("C");
        Assert.assertEquals("C", e.getSymbol());
    }

    @Test
    public void testElement_X() {
        IElement e = new DebugElement("X");
        Assert.assertEquals("R", e.getSymbol());
        // and it should not throw exceptions
        Assert.assertNotNull(e.getAtomicNumber());
        org.hamcrest.MatcherAssert.assertThat(e.getAtomicNumber(), is(0));
    }

    @Test
    public void testDebugElement_String_int() {
        IElement e = new DebugElement("H", 1);
        Assert.assertEquals("H", e.getSymbol());
        Assert.assertEquals(1, e.getAtomicNumber().intValue());
    }

    @Test
    public void compareSymbol() {
        DebugElement e1 = new DebugElement(new String("H"), 1);
        DebugElement e2 = new DebugElement(new String("H"), 1);
        Assert.assertTrue(e1.compare(e2));
    }

    @Test
    public void compareAtomicNumber() {
        DebugElement e1 = new DebugElement("H", new Integer(1));
        DebugElement e2 = new DebugElement("H", new Integer(1));
        Assert.assertTrue(e1.compare(e2));
    }

    @Test
    public void compareDiffSymbol() {
        DebugElement e1 = new DebugElement(new String("H"), 1);
        DebugElement e2 = new DebugElement(new String("C"), 12);
        Assert.assertFalse(e1.compare(e2));
    }

    @Test
    public void compareDiffAtomicNumber() {
        DebugElement e1 = new DebugElement(new String("H"), 1);
        DebugElement e2 = new DebugElement(new String("H"), 0);
        Assert.assertFalse(e1.compare(e2));
    }
}
