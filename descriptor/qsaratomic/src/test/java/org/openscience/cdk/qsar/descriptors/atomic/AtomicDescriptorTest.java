/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar.descriptors.atomic;

import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.test.qsar.DescriptorTest;
import org.openscience.cdk.tools.diff.AtomDiff;

/**
 * Tests for molecular descriptors.
 *
 * @cdk.module test-qsaratomic
 */
abstract class AtomicDescriptorTest extends DescriptorTest<IAtomicDescriptor> {

    IAtomicDescriptor descriptor;

    AtomicDescriptorTest() {}

    @Override
    public void setDescriptor(Class<? extends IAtomicDescriptor> descriptorClass) throws Exception {
        if (descriptor == null) {
            Object descriptor = descriptorClass.newInstance();
            if (!(descriptor instanceof IAtomicDescriptor)) {
                throw new CDKException("The passed descriptor class must be a IAtomicDescriptor");
            }
            this.descriptor = (IAtomicDescriptor) descriptor;
        }
        super.setDescriptor(descriptorClass);
    }

    @Test
    @Tag("SlowTest")
    void testCalculate_IAtomContainer() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();

        DescriptorValue v = null;
        try {
            v = descriptor.calculate(mol.getAtom(1), mol);
        } catch (Exception e) {
            Assertions.fail("A descriptor must not throw an exception");
        }
        Assertions.assertNotNull(v);
        assert v != null;
        Assertions.assertNotSame(0, v.getValue().length(), "The descriptor did not calculate any value.");
    }

    /**
     * Checks if the given labels are consistent.
     *
     * @throws Exception Passed on from calculate.
     */
    @Test
    @Tag("SlowTest")
    void testLabels() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();

        DescriptorValue v = descriptor.calculate(mol.getAtom(1), mol);
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
    @Tag("SlowTest")
    void testNamesConsistency() {
        IAtomContainer mol = someoneBringMeSomeWater();

        String[] names1 = descriptor.getDescriptorNames();
        DescriptorValue v = descriptor.calculate(mol.getAtom(1), mol);
        String[] names2 = v.getNames();

        Assertions.assertEquals(names1.length, names2.length, "(" + descriptor.getClass().toString() + ") fails. ");
        Assertions.assertArrayEquals(names1, names2);

        int valueCount = v.getValue().length();
        Assertions.assertEquals(valueCount, names1.length);
    }

    @Test
    @Tag("SlowTest")
    void testCalculate_NoModifications() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();
        IAtom atom = mol.getAtom(1);
        IAtom clone = mol.getAtom(1).clone();
        descriptor.calculate(atom, mol);
        String diff = AtomDiff.diff(clone, atom);
        Assertions.assertEquals(0, diff.length(), "The descriptor must not change the passed atom in any respect, but found this diff: "
                + diff);
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
