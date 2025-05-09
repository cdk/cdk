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
package org.openscience.cdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractBondTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Checks the functionality of the Bond class.
 *
 * @see org.openscience.cdk.Bond
 */
class BondTest extends AbstractBondTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(Bond::new);
    }

    @Test
    void testBond() {
        IBond bond = new Bond();
        Assertions.assertEquals(0, bond.getAtomCount());
        Assertions.assertNull(bond.getBegin());
        Assertions.assertNull(bond.getEnd());
        Assertions.assertNull(bond.getOrder());
        Assertions.assertEquals(IBond.Stereo.NONE, bond.getStereo());
    }

    @Test
    void testBond_arrayIAtom() {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IAtom atom3 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom4 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom5 = object.getBuilder().newInstance(IAtom.class, "C");

        IBond bond1 = new Bond(new IAtom[]{atom1, atom2, atom3, atom4, atom5});
        Assertions.assertEquals(5, bond1.getAtomCount());
        Assertions.assertEquals(atom1, bond1.getBegin());
        Assertions.assertEquals(atom2, bond1.getEnd());
    }

    @Test
    void testBond_arrayIAtom_IBond_Order() {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IAtom atom3 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom4 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom5 = object.getBuilder().newInstance(IAtom.class, "C");

        IBond bond1 = new Bond(new IAtom[]{atom1, atom2, atom3, atom4, atom5}, IBond.Order.SINGLE);
        Assertions.assertEquals(5, bond1.getAtomCount());
        Assertions.assertEquals(atom1, bond1.getBegin());
        Assertions.assertEquals(atom2, bond1.getEnd());
        Assertions.assertEquals(IBond.Order.SINGLE, bond1.getOrder());
    }

    @Test
    void testBond_IAtom_IAtom() {
        IChemObject object = newChemObject();
        IAtom c = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = object.getBuilder().newInstance(IAtom.class, "O");
        IBond bond = new Bond(c, o);

        Assertions.assertEquals(2, bond.getAtomCount());
        Assertions.assertEquals(c, bond.getBegin());
        Assertions.assertEquals(o, bond.getEnd());
        Assertions.assertEquals(IBond.Order.SINGLE, bond.getOrder());
        Assertions.assertEquals(IBond.Stereo.NONE, bond.getStereo());
    }

    @Test
    void testBond_IAtom_IAtom_IBond_Order() {
        IChemObject object = newChemObject();
        IAtom c = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = object.getBuilder().newInstance(IAtom.class, "O");
        IBond bond = new Bond(c, o, IBond.Order.DOUBLE);

        Assertions.assertEquals(2, bond.getAtomCount());
        Assertions.assertEquals(c, bond.getBegin());
        Assertions.assertEquals(o, bond.getEnd());
        Assertions.assertTrue(bond.getOrder() == IBond.Order.DOUBLE);
        Assertions.assertEquals(IBond.Stereo.NONE, bond.getStereo());
    }

    @Test
    void testBond_IAtom_IAtom_IBond_Order_IBond_Stereo() {
        IChemObject object = newChemObject();
        IAtom c = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = object.getBuilder().newInstance(IAtom.class, "O");
        IBond bond = new Bond(c, o, IBond.Order.SINGLE, IBond.Stereo.UP);

        Assertions.assertEquals(2, bond.getAtomCount());
        Assertions.assertEquals(c, bond.getBegin());
        Assertions.assertEquals(o, bond.getEnd());
        Assertions.assertTrue(bond.getOrder() == IBond.Order.SINGLE);
        Assertions.assertEquals(IBond.Stereo.UP, bond.getStereo());
    }

    @Test
    @Override
    public void testCompare_Object() {
        IChemObject object = newChemObject();
        IAtom c = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = object.getBuilder().newInstance(IAtom.class, "O");

        IBond b = new Bond(c, o, IBond.Order.DOUBLE); // C=O bond
        IBond b2 = new Bond(c, o, IBond.Order.DOUBLE); // same C=O bond

        Assertions.assertTrue(b.compare(b2));
    }

}
