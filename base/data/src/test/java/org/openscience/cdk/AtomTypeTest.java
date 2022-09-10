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
package org.openscience.cdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.test.interfaces.AbstractAtomTypeTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.test.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the AtomType class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.AtomType
 */
public class AtomTypeTest extends AbstractAtomTypeTest {

    @BeforeAll
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new AtomType("C");
            }
        });
    }

    @Test
    public void testAtomType_String() {
        IAtomType at = new AtomType("C");
        Assertions.assertEquals("C", at.getSymbol());
    }

    @Test
    public void testAtomType_IElement() {
        IElement element = newChemObject().getBuilder().newInstance(IElement.class, "C");
        IAtomType at = new AtomType(element);
        Assertions.assertEquals("C", at.getSymbol());
    }

    @Test
    public void testAtomType_String_String() {
        IAtomType at = new AtomType("C4", "C");
        Assertions.assertEquals("C", at.getSymbol());
        Assertions.assertEquals("C4", at.getAtomTypeName());
    }

    @Test
    public void testCompare() {
        IAtomType at = new AtomType("C4", "C");
        if (at instanceof org.openscience.cdk.AtomType) {
            org.openscience.cdk.AtomType at1 = (org.openscience.cdk.AtomType) at;
            IAtomType at2 = at.getBuilder().newInstance(IAtomType.class, "C3", "C");
            Assertions.assertFalse(at1.compare("C4"));
            Assertions.assertFalse(at1.compare(at2));
        }
    }

    @Test
    @Override
    public void testCompare_Object() {
        IAtomType someAt = new AtomType("C");
        if (someAt instanceof org.openscience.cdk.AtomType) {
            org.openscience.cdk.AtomType at = (org.openscience.cdk.AtomType) someAt;
            Assertions.assertTrue(at.compare(at));
            IAtomType hydrogen = someAt.getBuilder().newInstance(IAtomType.class, "H");
            Assertions.assertFalse(at.compare(hydrogen));
            Assertions.assertFalse(at.compare("Li"));
        }
    }

    @Test
    public void testCompare_AtomTypeName() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setAtomTypeName("C4");
        at2.setAtomTypeName("C4");
        Assertions.assertTrue(at1.compare(at2));
    }

    @Test
    public void testCompare_DiffAtomTypeName() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setAtomTypeName("C4");
        at2.setAtomTypeName("C3");
        Assertions.assertFalse(at1.compare(at2));
    }

    @Test
    public void testCompare_BondOrderSum() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setBondOrderSum(1.5);
        at2.setBondOrderSum(1.5);
        Assertions.assertTrue(at1.compare(at2));
    }

    @Test
    public void testCompare_DiffBondOrderSum() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setBondOrderSum(1.5);
        at2.setBondOrderSum(2.0);
        Assertions.assertFalse(at1.compare(at2));
    }
}
