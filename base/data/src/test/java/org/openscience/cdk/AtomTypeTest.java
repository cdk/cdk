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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.AbstractAtomTypeTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the AtomType class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.AtomType
 */
public class AtomTypeTest extends AbstractAtomTypeTest {

    @BeforeClass
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
        Assert.assertEquals("C", at.getSymbol());
    }

    @Test
    public void testAtomType_IElement() {
        IElement element = newChemObject().getBuilder().newInstance(IElement.class, "C");
        IAtomType at = new AtomType(element);
        Assert.assertEquals("C", at.getSymbol());
    }

    @Test
    public void testAtomType_String_String() {
        IAtomType at = new AtomType("C4", "C");
        Assert.assertEquals("C", at.getSymbol());
        Assert.assertEquals("C4", at.getAtomTypeName());
    }

    @Test
    public void testCompare() {
        IAtomType at = new AtomType("C4", "C");
        if (at instanceof org.openscience.cdk.AtomType) {
            org.openscience.cdk.AtomType at1 = (org.openscience.cdk.AtomType) at;
            IAtomType at2 = at.getBuilder().newInstance(IAtomType.class, "C3", "C");
            Assert.assertFalse(at1.compare("C4"));
            Assert.assertFalse(at1.compare(at2));
        }
    }

    @Test
    @Override
    public void testCompare_Object() {
        IAtomType someAt = new AtomType("C");
        if (someAt instanceof org.openscience.cdk.AtomType) {
            org.openscience.cdk.AtomType at = (org.openscience.cdk.AtomType) someAt;
            Assert.assertTrue(at.compare(at));
            IAtomType hydrogen = someAt.getBuilder().newInstance(IAtomType.class, "H");
            Assert.assertFalse(at.compare(hydrogen));
            Assert.assertFalse(at.compare("Li"));
        }
    }

    @Test
    public void testCompare_AtomTypeName() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setAtomTypeName(new String("C4"));
        at2.setAtomTypeName(new String("C4"));
        Assert.assertTrue(at1.compare(at2));
    }

    @Test
    public void testCompare_DiffAtomTypeName() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setAtomTypeName(new String("C4"));
        at2.setAtomTypeName(new String("C3"));
        Assert.assertFalse(at1.compare(at2));
    }

    @Test
    public void testCompare_BondOrderSum() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setBondOrderSum(1.5);
        at2.setBondOrderSum(1.5);
        Assert.assertTrue(at1.compare(at2));
    }

    @Test
    public void testCompare_DiffBondOrderSum() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setBondOrderSum(1.5);
        at2.setBondOrderSum(2.0);
        Assert.assertFalse(at1.compare(at2));
    }
}
