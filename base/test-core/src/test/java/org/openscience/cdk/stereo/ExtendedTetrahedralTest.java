/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.stereo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo.CLOCKWISE;

/**
 * @author John May
 */
final class ExtendedTetrahedralTest {

    @Test
    void peripheralsAreCopied() {
        IAtom focus = mock(IAtom.class);
        IAtom[] peripherals = new IAtom[]{mock(IAtom.class), mock(IAtom.class), mock(IAtom.class), mock(IAtom.class)};
        ExtendedTetrahedral element = new ExtendedTetrahedral(focus, peripherals, CLOCKWISE);

        // modifying this array does not change the one in the structure
        peripherals[0] = peripherals[1] = peripherals[2] = peripherals[3] = null;
        Assertions.assertNotNull(element.peripherals()[0]);
        Assertions.assertNotNull(element.peripherals()[1]);
        Assertions.assertNotNull(element.peripherals()[2]);
        Assertions.assertNotNull(element.peripherals()[3]);
    }

    @Test
    void peripheralsAreNotModifable() {
        IAtom focus = mock(IAtom.class);
        IAtom[] peripherals = new IAtom[]{mock(IAtom.class), mock(IAtom.class), mock(IAtom.class), mock(IAtom.class)};
        ExtendedTetrahedral element = new ExtendedTetrahedral(focus, peripherals, CLOCKWISE);

        // modifying this array does not change the one in the structure
        peripherals = element.peripherals();
        peripherals[0] = peripherals[1] = peripherals[2] = peripherals[3] = null;
        Assertions.assertNotNull(element.peripherals()[0]);
        Assertions.assertNotNull(element.peripherals()[1]);
        Assertions.assertNotNull(element.peripherals()[2]);
        Assertions.assertNotNull(element.peripherals()[3]);
    }

    @Test
    void nonCumulatedAtomThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    IAtomContainer ac = new AtomContainer();
                                    ac.addAtom(new Atom("C"));
                                    ac.addAtom(new Atom("C"));
                                    ac.addAtom(new Atom("C"));
                                    ac.addBond(0, 1, IBond.Order.SINGLE);
                                    ac.addBond(1, 2, IBond.Order.SINGLE);
                                    ExtendedTetrahedral.findTerminalAtoms(ac, ac.getAtom(0));
                                });
    }

    @Test
    void terminalAtomsAreFoundUnordered() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.DOUBLE);
        ac.addBond(1, 2, IBond.Order.DOUBLE);
        IAtom[] terminals = ExtendedTetrahedral.findTerminalAtoms(ac, ac.getAtom(1));
        // note order may change
        assertThat(terminals[0], is(ac.getAtom(0)));
        assertThat(terminals[1], is(ac.getAtom(2)));
    }

    @Test
    void terminalAtomsAreFoundOrdered() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.DOUBLE);
        ac.addBond(2, 3, IBond.Order.DOUBLE);
        ac.addBond(3, 4, IBond.Order.SINGLE);

        ExtendedTetrahedral element = new ExtendedTetrahedral(ac.getAtom(2), new IAtom[]{ac.getAtom(4), ac.getAtom(3),
                ac.getAtom(1), ac.getAtom(0)}, CLOCKWISE);

        IAtom[] terminals = element.findTerminalAtoms(ac);
        assertThat(terminals[0], is(ac.getAtom(3)));
        assertThat(terminals[1], is(ac.getAtom(1)));
    }

    @Test
    void noBuilder() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
                                    IAtom focus = mock(IAtom.class);
                                    IAtom[] peripherals = new IAtom[]{mock(IAtom.class), mock(IAtom.class), mock(IAtom.class), mock(IAtom.class)};
                                    ExtendedTetrahedral element = new ExtendedTetrahedral(focus, peripherals, CLOCKWISE);
                                    element.getBuilder();
                                });
    }

    @Test
    void containsAnAtom() {
        IAtom focus = mock(IAtom.class);
        IAtom[] peripherals = new IAtom[]{mock(IAtom.class), mock(IAtom.class), mock(IAtom.class), mock(IAtom.class)};
        ExtendedTetrahedral element = new ExtendedTetrahedral(focus, peripherals, CLOCKWISE);
        Assertions.assertTrue(element.contains(focus));
        Assertions.assertTrue(element.contains(peripherals[0]));
        Assertions.assertTrue(element.contains(peripherals[1]));
        Assertions.assertTrue(element.contains(peripherals[2]));
        Assertions.assertTrue(element.contains(peripherals[3]));

        Assertions.assertFalse(element.contains(mock(IAtom.class)));
    }

    // trival access
    @Test
    void noOperation() {}

}
