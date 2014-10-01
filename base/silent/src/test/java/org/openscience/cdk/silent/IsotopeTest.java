/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2012  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractIsotopeTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link Isotope}.
 *
 * @cdk.module test-silent
 */
public class IsotopeTest extends AbstractIsotopeTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new Isotope(new Element());
            }
        });
    }

    @Test
    public void testIsotope_String() {
        IIsotope i = new Isotope("C");
        Assert.assertEquals("C", i.getSymbol());
    }

    @Test
    public void testIsotope_IElement() {
        IElement element = newChemObject().getBuilder().newInstance(IElement.class, "C");
        IIsotope i = new Isotope(element);
        Assert.assertEquals("C", i.getSymbol());
    }

    @Test
    public void testIsotope_int_String_int_double_double() {
        IIsotope i = new Isotope(6, "C", 12, 12.001, 80.0);
        Assert.assertEquals(12, i.getMassNumber().intValue());
        Assert.assertEquals("C", i.getSymbol());
        Assert.assertEquals(6, i.getAtomicNumber().intValue());
        Assert.assertEquals(12.001, i.getExactMass(), 0.001);
        Assert.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }

    @Test
    public void testIsotope_String_int() {
        IIsotope i = new Isotope("C", 12);
        Assert.assertEquals(12, i.getMassNumber().intValue());
        Assert.assertEquals("C", i.getSymbol());
    }

    @Test
    public void testIsotope_int_String_double_double() {
        IIsotope i = new Isotope(6, "C", 12.001, 80.0);
        Assert.assertEquals("C", i.getSymbol());
        Assert.assertEquals(6, i.getAtomicNumber().intValue());
        Assert.assertEquals(12.001, i.getExactMass(), 0.001);
        Assert.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }

    @Test
    public void testCompare_MassNumber() {
        Isotope iso = new Isotope("C");
        iso.setMassNumber(12);
        Isotope iso2 = new Isotope("C");
        iso2.setMassNumber((int) 12.0);
        Assert.assertTrue(iso.compare(iso2));
    }

    @Test
    public void testCompare_MassNumberIntegers() {
        Isotope iso = new Isotope("C");
        iso.setMassNumber(new Integer(12));
        Isotope iso2 = new Isotope("C");
        iso2.setMassNumber(new Integer(12));
        Assert.assertTrue(iso.compare(iso2));
    }

    @Test
    public void testCompare_MassNumberIntegers_ValueOf() {
        Isotope iso = new Isotope("C");
        iso.setMassNumber(Integer.valueOf(12));
        Isotope iso2 = new Isotope("C");
        iso2.setMassNumber(Integer.valueOf(12));
        Assert.assertTrue(iso.compare(iso2));
    }

    @Test
    public void testCompare_ExactMass() {
        Isotope iso = new Isotope("C");
        iso.setExactMass(12.000000);
        Isotope iso2 = new Isotope("C");
        iso2.setExactMass(12.0);
        Assert.assertTrue(iso.compare(iso2));
    }

    @Test
    public void testCompare_NaturalAbundance() {
        Isotope iso = new Isotope("C");
        iso.setNaturalAbundance(12.000000);
        Isotope iso2 = new Isotope("C");
        iso2.setNaturalAbundance(12.0);
        Assert.assertTrue(iso.compare(iso2));
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
}
