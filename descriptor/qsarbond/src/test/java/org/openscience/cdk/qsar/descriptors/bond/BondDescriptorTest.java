/* Copyright (C) 2007-2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar.descriptors.bond;

import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.test.qsar.DescriptorTest;
import org.openscience.cdk.tools.diff.BondDiff;

/**
 * Tests for bond descriptors.
 *
 * @cdk.module test-qsarbond
 */
abstract class BondDescriptorTest extends DescriptorTest<IBondDescriptor> {

    IBondDescriptor descriptor;

    BondDescriptorTest() {}

    @Override
    public void setDescriptor(Class<? extends IBondDescriptor> descriptorClass) throws Exception {
        if (descriptor == null) {
            Object descriptor = descriptorClass.newInstance();
            if (!(descriptor instanceof IBondDescriptor)) {
                throw new CDKException("The passed descriptor class must be a IBondDescriptor");
            }
            this.descriptor = (IBondDescriptor) descriptor;
        }
        super.setDescriptor(descriptorClass);
    }

    @Test
    void testCalculate_IBond_IAtomContainer() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();

        DescriptorValue v = null;
        try {
            v = descriptor.calculate(mol.getBond(0), mol);
        } catch (Exception e) {
            Assertions.fail("A descriptor must not throw an exception");
        }
        Assertions.assertNotNull(v);
        Assertions.assertNotSame(0, v.getValue().length(), "The descriptor did not calculate any value.");
    }

    /**
     * Checks if the given labels are consistent.
     *
     * @throws Exception Passed on from calculate.
     */
    @Test
    void testLabels() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();

        DescriptorValue v = descriptor.calculate(mol.getBond(0), mol);
        Assertions.assertNotNull(v);
        String[] names = v.getNames();
        Assertions.assertNotNull(names, "The descriptor must return labels using the getNames() method.");
        Assertions.assertNotSame(0, names.length, "At least one label must be given.");
        for (String name : names) {
            Assertions.assertNotNull(name, "A descriptor label may not be null.");
            Assertions.assertNotSame(0, name.length(), "The label string must not be empty.");
            //        	System.out.println("Label: " + names[i]);
        }
        Assertions.assertNotNull(v.getValue());
        int valueCount = v.getValue().length();
        Assertions.assertEquals(names.length, valueCount, "The number of labels must equals the number of values.");
    }

    /**
    * Check if the names obtained directly from the descriptor without
    * calculation match those obtained from the descriptor value object.
    * Also ensure that the number of actual values matches the length
    * of the names
    */
    @Test
    void testNamesConsistency() {
        IAtomContainer mol = someoneBringMeSomeWater();

        String[] names1 = descriptor.getDescriptorNames();
        DescriptorValue v = descriptor.calculate(mol.getBond(1), mol);
        String[] names2 = v.getNames();

        Assertions.assertEquals(names1.length, names2.length);
        Assertions.assertArrayEquals(names1, names2);

        int valueCount = v.getValue().length();
        Assertions.assertEquals(valueCount, names1.length);
    }

    @Test
    void testCalculate_NoModifications() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();
        IBond bond = mol.getBond(0);
        IBond clone = mol.getBond(0).clone();
        descriptor.calculate(bond, mol);
        String diff = BondDiff.diff(clone, bond);
        Assertions.assertEquals(0, diff.length(), "(" + descriptor.getClass().toString()
                + ") The descriptor must not change the passed bond in any respect, but found this diff: " + diff);
    }

    private IAtomContainer someoneBringMeSomeWater() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        c1.setPoint3d(new Point3d(0.0, 0.0, 0.0));
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
        h1.setPoint3d(new Point3d(1.0, 0.0, 0.0));
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
        h2.setPoint3d(new Point3d(-1.0, 0.0, 0.0));
        mol.addAtom(c1);
        mol.addAtom(h1);
        mol.addAtom(h2);
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        return mol;
    }

}
