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

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.DescriptorTest;
import org.openscience.cdk.tools.diff.AtomDiff;

/**
 * Tests for molecular descriptors.
 *
 * @cdk.module test-qsaratomic
 */
public abstract class AtomicDescriptorTest extends DescriptorTest<IAtomicDescriptor> {

    protected IAtomicDescriptor descriptor;

    public AtomicDescriptorTest() {}

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
    @Category(SlowTest.class)
    public void testCalculate_IAtomContainer() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();

        DescriptorValue v = null;
        try {
            v = descriptor.calculate(mol.getAtom(1), mol);
        } catch (Exception e) {
            Assert.fail("A descriptor must not throw an exception");
        }
        Assert.assertNotNull(v);
        assert v != null;
        Assert.assertNotSame("The descriptor did not calculate any value.", 0, v.getValue().length());
    }

    /**
     * Checks if the given labels are consistent.
     *
     * @throws Exception Passed on from calculate.
     */
    @Test
    @Category(SlowTest.class)
    public void testLabels() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();

        DescriptorValue v = descriptor.calculate(mol.getAtom(1), mol);
        Assert.assertNotNull(v);
        String[] names = v.getNames();
        Assert.assertNotNull("The descriptor must return labels using the getNames() method.", names);
        Assert.assertNotSame("At least one label must be given.", 0, names.length);
        for (String name : names) {
            Assert.assertNotNull("A descriptor label may not be null.", name);
            Assert.assertNotSame("The label string must not be empty.", 0, name.length());
            //        	System.out.println("Label: " + names[i]);
        }
        Assert.assertNotNull(v.getValue());
        int valueCount = v.getValue().length();
        Assert.assertEquals("The number of labels must equals the number of values.", names.length, valueCount);
    }

    /**
     * Check if the names obtained directly from the descriptor without
     * calculation match those obtained from the descriptor value object.
     * Also ensure that the number of actual values matches the length
     * of the names
     */
    @Test
    @Category(SlowTest.class)
    public void testNamesConsistency() {
        IAtomContainer mol = someoneBringMeSomeWater();

        String[] names1 = descriptor.getDescriptorNames();
        DescriptorValue v = descriptor.calculate(mol.getAtom(1), mol);
        String[] names2 = v.getNames();

        Assert.assertEquals("(" + descriptor.getClass().toString() + ") fails. ", names1.length, names2.length);
        Assert.assertArrayEquals(names1, names2);

        int valueCount = v.getValue().length();
        Assert.assertEquals(valueCount, names1.length);
    }

    @Test
    @Category(SlowTest.class)
    public void testCalculate_NoModifications() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater();
        IAtom atom = mol.getAtom(1);
        IAtom clone = (IAtom) mol.getAtom(1).clone();
        descriptor.calculate(atom, mol);
        String diff = AtomDiff.diff(clone, atom);
        Assert.assertEquals("The descriptor must not change the passed atom in any respect, but found this diff: "
                + diff, 0, diff.length());
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
