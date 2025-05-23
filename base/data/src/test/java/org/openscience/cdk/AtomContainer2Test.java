/*
 * Copyright (c) 2017 John Mayfield <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.openscience.cdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractAtomContainerTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Checks the functionality of the AtomContainer.
 *
 */
class AtomContainer2Test extends AbstractAtomContainerTest {

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
        IAtom a1 = ac.getBuilder().newInstance(IAtom.class, "C");
        IAtom a2 = ac.getBuilder().newInstance(IAtom.class, "C");
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(ac.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE));
        ac.addLonePair(ac.getBuilder().newInstance(ILonePair.class, ac.getBuilder().newInstance(IAtom.class, "N")));
    }

    @Test
    void testAtomContainer() {
        // create an empty container with in the constructor defined array lengths
        IAtomContainer container = new AtomContainer();

        Assertions.assertEquals(0, container.getAtomCount());
        Assertions.assertEquals(0, container.getBondCount());

        // test whether the ElectronContainer is correctly initialized
        IAtom a1 = container.getBuilder().newInstance(IAtom.class, "C");
        IAtom a2 = container.getBuilder().newInstance(IAtom.class, "C");
        container.addAtom(a1);
        container.addAtom(a2);
        container.addBond(container.getBuilder().newInstance(IBond.class,
                                                             a1, a2,
                                                             IBond.Order.DOUBLE));
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

    @Test
    void testAtomGetBond() {
        IAtomContainer mol = (IAtomContainer) newChemObject();
        IAtom          a1 = mol.getBuilder().newAtom();
        IAtom          a2 = mol.getBuilder().newAtom();
        IAtom          a3 = mol.getBuilder().newAtom();
        a1.setSymbol("CH3");
        a2.setSymbol("CH2");
        a3.setSymbol("OH");
        mol.addAtom(a1);
        mol.addAtom(a2);
        mol.addAtom(a3);
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        assertThat(mol.getBond(0),
                   is(mol.getAtom(0).getBond(mol.getAtom(1))));
        assertThat(mol.getBond(1),
                   is(mol.getAtom(1).getBond(mol.getAtom(2))));
        Assertions.assertNull(mol.getAtom(0).getBond(mol.getAtom(2)));
    }

    @Test
    void testAtom_getTotalHydrogenCount() {
        IAtomContainer mol = (IAtomContainer) newChemObject();
        IAtom h1 = mol.newAtom(IAtom.H, 0); // H
        IAtom c1 = mol.newAtom(IAtom.C, 2); // CH2
        IAtom h2 = mol.newAtom(IAtom.H, 0); // H
        mol.newBond(h1, c1);
        mol.newBond(c1, h2);
        Assertions.assertEquals(0, h1.getImplicitHydrogenCount());
        Assertions.assertEquals(2, c1.getImplicitHydrogenCount());
        Assertions.assertEquals(0, h2.getImplicitHydrogenCount());
        Assertions.assertEquals(0, h1.getTotalHydrogenCount());
        Assertions.assertEquals(4, c1.getTotalHydrogenCount());
        Assertions.assertEquals(0, h2.getTotalHydrogenCount());
    }

    @Test
    void testAtom_getTotalHydrogenCount_B2H6() {
        IAtomContainer mol = (IAtomContainer) newChemObject();
        IAtom b1 = mol.newAtom(IAtom.B, 2); // BH2
        IAtom b2 = mol.newAtom(IAtom.B, 2); // BH2
        IAtom h1 = mol.newAtom(IAtom.H, 0); // H
        IAtom h2 = mol.newAtom(IAtom.H, 0); // H
        mol.newBond(b1, h1);
        mol.newBond(b1, h2);
        mol.newBond(b2, h1);
        mol.newBond(b2, h2);
        Assertions.assertEquals(2, b1.getImplicitHydrogenCount());
        Assertions.assertEquals(2, b2.getImplicitHydrogenCount());
        Assertions.assertEquals(0, h1.getImplicitHydrogenCount());
        Assertions.assertEquals(0, h2.getImplicitHydrogenCount());
        Assertions.assertEquals(4, b1.getTotalHydrogenCount());
        Assertions.assertEquals(4, b2.getTotalHydrogenCount());
        Assertions.assertEquals(0, h1.getTotalHydrogenCount());
        Assertions.assertEquals(0, h2.getTotalHydrogenCount());
    }

    @Test
    void testAtom_getTotalHydrogenCountNull() {
        IAtomContainer mol = (IAtomContainer) newChemObject();
        IAtom a1 = mol.newAtom(IAtom.H, 0); // H
        IAtom a2 = mol.newAtom(IAtom.C, 2); // CH2
        IAtom a3 = mol.newAtom(IAtom.H, 0); // H
        mol.newBond(a1, a2);
        mol.newBond(a2, a3);
        a2.setImplicitHydrogenCount(null);
        Assertions.assertEquals(0, mol.getAtom(0).getImplicitHydrogenCount());
        Assertions.assertNull(mol.getAtom(1).getImplicitHydrogenCount());
        Assertions.assertEquals(0, mol.getAtom(2).getImplicitHydrogenCount());
        Assertions.assertEquals(0, mol.getAtom(0).getTotalHydrogenCount());
        Assertions.assertNull(mol.getAtom(1).getTotalHydrogenCount());
        Assertions.assertEquals(0, mol.getAtom(2).getTotalHydrogenCount());
    }

    @Test
    void testAtom_neighbors() {
        IAtomContainer mol = (IAtomContainer) newChemObject();
        IAtom h1 = mol.newAtom(IAtom.H, 0); // H
        IAtom c1 = mol.newAtom(IAtom.C, 2); // CH2
        IAtom h2 = mol.newAtom(IAtom.H, 0); // H
        mol.newBond(h1, c1);
        mol.newBond(c1, h2);
        Iterable<IAtom> nbors = c1.neighbors();
        List<IAtom> result = new ArrayList<>();
        for (IAtom nbor : nbors)
            result.add(nbor);
        Assertions.assertEquals(Arrays.asList(h1,h2), result);
        // ensure iterator resets
        for (IAtom nbor : nbors)
            result.add(nbor);
        Assertions.assertEquals(Arrays.asList(h1,h2,h1,h2), result);
    }
}
