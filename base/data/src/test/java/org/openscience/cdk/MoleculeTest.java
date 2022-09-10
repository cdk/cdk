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

package org.openscience.cdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.test.interfaces.AbstractMoleculeTest;

/**
 * Checks the functionality of the Molecule class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.Molecule
 */
public class MoleculeTest extends AbstractMoleculeTest {

    @BeforeAll
    public static void setUp() {
        setTestObjectBuilder(AtomContainer::new);
    }

    // test constructors

    @Test
    public void testMolecule() {
        IAtomContainer m = new AtomContainer();
        Assertions.assertNotNull(m);
    }

    @Test
    public void testMolecule_int_int_int_int() {
        IAtomContainer m = new AtomContainer(5, 5, 1, 1);
        Assertions.assertNotNull(m);
        Assertions.assertEquals(0, m.getAtomCount());
        Assertions.assertEquals(0, m.getBondCount());
        Assertions.assertEquals(0, m.getLonePairCount());
        Assertions.assertEquals(0, m.getSingleElectronCount());
    }

    @Test
    public void testMolecule_IAtomContainer() {
        IAtomContainer acetone = new org.openscience.cdk.AtomContainer();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class, "C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class, "O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class, "C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class, c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class, c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);

        IAtomContainer m = new AtomContainer(acetone);
        Assertions.assertNotNull(m);
        Assertions.assertEquals(4, m.getAtomCount());
        Assertions.assertEquals(3, m.getBondCount());
    }

}
