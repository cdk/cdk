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
package org.openscience.cdk.debug;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractAtomTypeTest;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.test.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link DebugAtomType}.
 *
 * @cdk.module test-datadebug
 */
public class DebugAtomTypeTest extends AbstractAtomTypeTest {

    @BeforeAll
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new DebugAtomType("C");
            }
        });
    }

    @Test
    public void testDebugAtomType_String() {
        IAtomType at = new DebugAtomType("C");
        Assertions.assertEquals("C", at.getSymbol());
    }

    @Test
    public void testDebugAtomType_IElement() {
        IElement element = new DebugElement("C");
        IAtomType at = element.getBuilder().newInstance(IAtomType.class, element);
        Assertions.assertEquals("C", at.getSymbol());
    }

    @Test
    public void testDebugAtomType_String_String() {
        IAtomType at = new DebugAtomType("C4", "C");
        Assertions.assertEquals("C", at.getSymbol());
        Assertions.assertEquals("C4", at.getAtomTypeName());
    }

    @Test
    public void testCompare_AtomTypeName() {
        DebugAtomType at1 = new DebugAtomType("C");
        DebugAtomType at2 = new DebugAtomType("C");
        at1.setAtomTypeName("C4");
        at2.setAtomTypeName("C4");
        Assertions.assertTrue(at1.compare(at2));
    }

    @Test
    public void testCompare_DiffAtomTypeName() {
        DebugAtomType at1 = new DebugAtomType("C");
        DebugAtomType at2 = new DebugAtomType("C");
        at1.setAtomTypeName("C4");
        at2.setAtomTypeName("C3");
        Assertions.assertFalse(at1.compare(at2));
    }

    @Test
    public void testCompare_BondOrderSum() {
        DebugAtomType at1 = new DebugAtomType("C");
        DebugAtomType at2 = new DebugAtomType("C");
        at1.setBondOrderSum(1.5);
        at2.setBondOrderSum(1.5);
        Assertions.assertTrue(at1.compare(at2));
    }

    @Test
    public void testCompare_DiffBondOrderSum() {
        DebugAtomType at1 = new DebugAtomType("C");
        DebugAtomType at2 = new DebugAtomType("C");
        at1.setBondOrderSum(1.5);
        at2.setBondOrderSum(2.0);
        Assertions.assertFalse(at1.compare(at2));
    }
}
