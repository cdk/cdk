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
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.test.interfaces.AbstractAtomContainerTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;

/**
 * Checks the functionality of the AtomContainer.
 *
 */
class AtomContainerTest extends AbstractAtomContainerTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(AtomContainer::new);
    }

    @Test
    void testAtomContainer_int_int_int_int() {
        // create an empty container with predefined
        // array lengths
        IAtomContainer ac = new AtomContainer(5, 6, 1, 2);

        Assertions.assertEquals(0, ac.getAtomCount());
        Assertions.assertEquals(0, ac.getElectronContainerCount());

        // test whether the ElectronContainer is correctly initialized
        IAtom a1 = ac.newAtom();
        IAtom a2 = ac.newAtom();
        ac.addBond(ac.getBuilder().newInstance(IBond.class,
                                               a1,
                                               a2, IBond.Order.DOUBLE));
        ac.addLonePair(ac.getBuilder().newInstance(ILonePair.class, ac.getBuilder().newInstance(IAtom.class, "N")));
    }

    @Test
    void testAtomContainer() {
        // create an empty container with in the constructor defined array lengths
        IAtomContainer container = new AtomContainer();

        Assertions.assertEquals(0, container.getAtomCount());
        Assertions.assertEquals(0, container.getBondCount());

        // test whether the ElectronContainer is correctly initialized
        IAtom a1 = container.newAtom();
        IAtom a2 = container.newAtom();
        container.addBond(container.getBuilder().newInstance(IBond.class,
                a1,
                a2, IBond.Order.DOUBLE));
        container.addLonePair(container.getBuilder().newInstance(ILonePair.class,
                container.getBuilder().newInstance(IAtom.class, "N")));
    }

    @Test
    void testAtomContainer_IAtomContainer() {
        IAtomContainer acetone = newChemObject().getBuilder().newInstance(IAtomContainer.class);
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

        IAtomContainer container = new AtomContainer(acetone);
        Assertions.assertEquals(4, container.getAtomCount());
        Assertions.assertEquals(3, container.getBondCount());
    }

    /**
     * Tests the double bond stereochemistry update (carriers and conformation) after an atom removal.
     */
    @Test
    void testStereoUpdate() {
        // we use the following variable names to refer to the
        // double bond atoms and substituents
        // x       y
        //  \     /
        //   u = v
        //  /     \
        // w       z
        IAtomContainer mol = newChemObject().getBuilder().newInstance(IAtomContainer.class);
        IAtom u = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom v = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom w = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom x = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom y = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom z = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(u);
        mol.addAtom(v);
        mol.addAtom(w);
        mol.addAtom(x);
        mol.addAtom(y);
        mol.addAtom(z);
        IBond uv = mol.getBuilder().newInstance(IBond.class, u, v, IBond.Order.DOUBLE);
        IBond uw = mol.getBuilder().newInstance(IBond.class, u, w, IBond.Order.SINGLE);
        IBond ux = mol.getBuilder().newInstance(IBond.class, u, x, IBond.Order.SINGLE);
        IBond vy = mol.getBuilder().newInstance(IBond.class, v, y, IBond.Order.SINGLE);
        IBond vz = mol.getBuilder().newInstance(IBond.class, v, z, IBond.Order.SINGLE);
        mol.addBond(uv);
        mol.addBond(uw);
        mol.addBond(ux);
        mol.addBond(vy);
        mol.addBond(vz);
        mol.addStereoElement(new DoubleBondStereochemistry(uv, new IBond[]{ux, vz}, DoubleBondStereochemistry.Conformation.OPPOSITE));
        for (IStereoElement<?, ?> elem : mol.stereoElements()) {
            Assertions.assertTrue(elem.getCarriers().contains(ux));
            Assertions.assertTrue(elem.getCarriers().contains(vz));
            Assertions.assertEquals(uv, elem.getFocus());
            Assertions.assertEquals(IDoubleBondStereochemistry.Conformation.OPPOSITE, ((IDoubleBondStereochemistry) elem).getStereo());
        }
        mol.removeAtom(z);
        for (IStereoElement<?, ?> elem : mol.stereoElements()) {
            Assertions.assertTrue(elem.getCarriers().contains(ux));
            //the other carrier is updated to vy
            Assertions.assertTrue(elem.getCarriers().contains(vy));
            //the configuration therefore needs to be TOGETHER now
            Assertions.assertEquals(IDoubleBondStereochemistry.Conformation.TOGETHER, ((IDoubleBondStereochemistry) elem).getStereo());
        }
    }
}
