/* Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.interfaces;

import javax.vecmath.Vector3d;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link ICrystal} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractCrystalTest extends AbstractAtomContainerTest {

    @Test
    @Override
    public void testAdd_IAtomContainer() {
        ICrystal crystal = (ICrystal) newChemObject();

        IAtomContainer acetone = crystal.getBuilder().newInstance(IAtomContainer.class);
        IAtom c1 = crystal.getBuilder().newInstance(IAtom.class, "C");
        IAtom c2 = crystal.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = crystal.getBuilder().newInstance(IAtom.class, "O");
        IAtom c3 = crystal.getBuilder().newInstance(IAtom.class, "C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = crystal.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.SINGLE);
        IBond b2 = crystal.getBuilder().newInstance(IBond.class, c1, o, IBond.Order.DOUBLE);
        IBond b3 = crystal.getBuilder().newInstance(IBond.class, c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);

        crystal.add(acetone);
        Assert.assertEquals(4, crystal.getAtomCount());
        Assert.assertEquals(3, crystal.getBondCount());
    }

    @Test
    @Override
    public void testAddAtom_IAtom() {
        ICrystal crystal = (ICrystal) newChemObject();
        IAtom c1 = crystal.getBuilder().newInstance(IAtom.class, "C");
        crystal.addAtom(c1);
        Assert.assertEquals(1, crystal.getAtomCount());
    }

    @Test
    public void testSetA_Vector3d() {
        ICrystal crystal = (ICrystal) newChemObject();

        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        Assert.assertEquals(1.0, a.x, 0.001);
        Assert.assertEquals(2.0, a.y, 0.001);
        Assert.assertEquals(3.0, a.z, 0.001);
    }

    @Test
    public void testGetA() {
        ICrystal crystal = (ICrystal) newChemObject();

        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        Assert.assertNotNull(a);
    }

    @Test
    public void testGetB() {
        ICrystal crystal = (ICrystal) newChemObject();

        crystal.setB(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getB();
        Assert.assertNotNull(a);
    }

    @Test
    public void testGetC() {
        ICrystal crystal = (ICrystal) newChemObject();

        crystal.setC(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getC();
        Assert.assertNotNull(a);
    }

    @Test
    public void testSetB_Vector3d() {
        ICrystal crystal = (ICrystal) newChemObject();

        crystal.setB(new Vector3d(1.0, 2.0, 3.0));
        Vector3d b = crystal.getB();
        Assert.assertEquals(1.0, b.x, 0.001);
        Assert.assertEquals(2.0, b.y, 0.001);
        Assert.assertEquals(3.0, b.z, 0.001);
    }

    @Test
    public void testSetC_Vector3d() {
        ICrystal crystal = (ICrystal) newChemObject();

        crystal.setC(new Vector3d(1.0, 2.0, 3.0));
        Vector3d c = crystal.getC();
        Assert.assertEquals(1.0, c.x, 0.001);
        Assert.assertEquals(2.0, c.y, 0.001);
        Assert.assertEquals(3.0, c.z, 0.001);
    }

    @Test
    public void testSetSpaceGroup_String() {
        ICrystal crystal = (ICrystal) newChemObject();
        String spacegroup = "P 2_1 2_1 2_1";
        crystal.setSpaceGroup(spacegroup);
        Assert.assertEquals(spacegroup, crystal.getSpaceGroup());
    }

    @Test
    public void testGetSpaceGroup() {
        ICrystal crystal = (ICrystal) newChemObject();
        String spacegroup = "P 2_1 2_1 2_1";
        crystal.setSpaceGroup(spacegroup);
        Assert.assertNotNull(crystal.getSpaceGroup());
        Assert.assertEquals(spacegroup, crystal.getSpaceGroup());
    }

    @Test
    public void testSetZ_Integer() {
        ICrystal crystal = (ICrystal) newChemObject();
        int z = 2;
        crystal.setZ(z);
        Assert.assertEquals(z, crystal.getZ().intValue());
    }

    @Test
    public void testGetZ() {
        testSetZ_Integer();
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        ICrystal crystal = (ICrystal) newChemObject();
        String description = crystal.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    @Override
    public void testClone() throws Exception {
        ICrystal crystal = (ICrystal) newChemObject();
        Object clone = crystal.clone();
        Assert.assertTrue(clone instanceof ICrystal);
    }

    @Test
    public void testClone_Axes() throws Exception {
        ICrystal crystal1 = (ICrystal) newChemObject();
        Vector3d axes = new Vector3d(1.0, 2.0, 3.0);
        crystal1.setA(axes);
        ICrystal crystal2 = (ICrystal) crystal1.clone();

        // test cloning of axes
        crystal1.getA().x = 5.0;
        Assert.assertEquals(1.0, crystal2.getA().x, 0.001);
    }

    @Test
    public void testSetZeroAxes() {
        ICrystal crystal = (ICrystal) newChemObject();

        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        Assert.assertNotNull(a);
    }

}
