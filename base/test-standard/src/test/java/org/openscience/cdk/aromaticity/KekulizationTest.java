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

package org.openscience.cdk.aromaticity;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;
import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;
import static org.openscience.cdk.interfaces.IBond.Order.UNSET;

/**
 * @author John May
 */
public class KekulizationTest {

    @Test
    public void benzene() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 0, UNSET, true));
        assertBondOrders(m, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    // when a double bond is already set, it is not moved
    @Test
    public void benzeneWithExistingDoubleBond() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, DOUBLE, true)); // <-- already set
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 0, UNSET, true));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE);
    }

    @Test
    public void pyrrole() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("N", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 0, UNSET, true));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    @Test
    public void pyrroleWithExplicitHydrogen() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("N", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("H", 0, false));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 0, UNSET, true));
        m.addBond(bond(m, 0, 5, SINGLE, false));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, SINGLE);
    }

    /** Hydrogens must be present - otherwise the kekulisation is ambiguous. */
    @Test(expected = CDKException.class)
    public void pyrroleWithMissingHydrogen() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("N", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 0, UNSET, true));
        assertBondOrders(m);
    }

    /** @cdk.inchi InChI=1S/C10H8/c1-2-5-9-7-4-8-10(9)6-3-1/h1-8H */
    @Test
    public void azulene() throws Exception {
        IAtomContainer m = new AtomContainer(10, 11, 0, 0);
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 6, UNSET, true));
        m.addBond(bond(m, 2, 6, UNSET, true));
        m.addBond(bond(m, 6, 7, UNSET, true));
        m.addBond(bond(m, 7, 8, UNSET, true));
        m.addBond(bond(m, 8, 9, UNSET, true));
        m.addBond(bond(m, 0, 9, UNSET, true));
        assertBondOrders(m, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    /** @cdk.inchi InChI=1S/C5H5NO/c7-6-4-2-1-3-5-6/h1-5H */
    @Test
    public void pyridineOxide() throws Exception {
        IAtomContainer m = new AtomContainer(7, 7, 0, 0);
        m.addAtom(atom("O", 0, false));
        m.addAtom(atom("N", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.getAtom(0).setFormalCharge(-1);
        m.getAtom(1).setFormalCharge(+1);
        m.addBond(bond(m, 0, 1, SINGLE, false));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 6, UNSET, true));
        m.addBond(bond(m, 1, 6, UNSET, true));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    /** @cdk.inchi InChI=1S/C5H5NO/c7-6-4-2-1-3-5-6/h1-5H */
    @Test
    public void pyridineOxideNonChargeSeparated() throws Exception {
        IAtomContainer m = new AtomContainer(7, 7, 0, 0);
        m.addAtom(atom("O", 0, false));
        m.addAtom(atom("N", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, DOUBLE, false));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 6, UNSET, true));
        m.addBond(bond(m, 1, 6, UNSET, true));
        assertBondOrders(m, DOUBLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    @Test
    public void furane() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("O", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 0, UNSET, true));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    /**
     * As seen in: CHEBI:30858
     *
     * @cdk.inchi InChI=1S/C4H4Te/c1-2-4-5-3-1/h1-4H
     */
    @Test
    public void tellurophene() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("Te", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 0, UNSET, true));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    /**
     * @cdk.inchi InChI=1S/C5H5/c1-2-4-5-3-1/h1-5H/q-1
     */
    @Test
    public void carbonAnion() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.getAtom(0).setFormalCharge(-1);
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 0, UNSET, true));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    /**
     * @cdk.inchi InChI=1S/C7H7/c1-2-4-6-7-5-3-1/h1-7H/q+1
     */
    @Test
    public void tropylium() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.getAtom(0).setFormalCharge(+1);
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 6, UNSET, true));
        m.addBond(bond(m, 6, 0, UNSET, true));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    /**
     * example seen in: CHEMBL141536
     *
     * @cdk.inchi InChI=1S/C5H5Se/c1-2-4-6-5-3-1/h1-5H/q+1
     */
    @Test
    public void seleniumCation() throws Exception {
        IAtomContainer m = new AtomContainer(6, 6, 0, 0);
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("Se", 0, true));
        m.getAtom(5).setFormalCharge(+1);
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 0, UNSET, true));
        assertBondOrders(m, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    /**
     * example seen in: CHEMBL13520
     *
     * @cdk.inchi InChI=1/C11H9N3O3S/c1-18(17)9-5-3-2-4-8(9)14-6-7(10(15)16)12-11(14)13-18/h2-6H,1H3,(H,15,16)
     */
    @Test
    public void sixValentSulphur() throws Exception {
        IAtomContainer m = new AtomContainer(18, 20, 0, 0);
        m.addAtom(atom("C", 3, false));
        m.addAtom(atom("S", 0, true));
        m.addAtom(atom("O", 0, false));
        m.addAtom(atom("N", 0, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("N", 0, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("N", 0, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 0, false));
        m.addAtom(atom("O", 0, false));
        m.addAtom(atom("O", 1, false));
        m.addBond(bond(m, 0, 1, SINGLE, false));
        m.addBond(bond(m, 1, 2, DOUBLE, false));
        m.addBond(bond(m, 1, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 6, UNSET, true));
        m.addBond(bond(m, 6, 7, UNSET, true));
        m.addBond(bond(m, 7, 8, UNSET, true));
        m.addBond(bond(m, 4, 8, UNSET, true));
        m.addBond(bond(m, 8, 9, UNSET, true));
        m.addBond(bond(m, 9, 10, UNSET, true));
        m.addBond(bond(m, 10, 11, UNSET, true));
        m.addBond(bond(m, 11, 12, UNSET, true));
        m.addBond(bond(m, 12, 13, UNSET, true));
        m.addBond(bond(m, 13, 14, UNSET, true));
        m.addBond(bond(m, 1, 14, UNSET, true));
        m.addBond(bond(m, 9, 14, UNSET, true));
        m.addBond(bond(m, 6, 15, SINGLE, false));
        m.addBond(bond(m, 15, 16, DOUBLE, false));
        m.addBond(bond(m, 15, 17, SINGLE, false));
        assertBondOrders(m, SINGLE, DOUBLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, SINGLE, SINGLE, DOUBLE,
                SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, SINGLE, SINGLE, DOUBLE, SINGLE);
    }

    /**
     * @cdk.inchi InChI=1S/C12H10/c1-3-7-11(8-4-1)12-9-5-2-6-10-12/h1-10H
     */
    @Test
    public void biphenyl() throws Exception {
        IAtomContainer m = new AtomContainer(12, 13, 0, 0);
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 0, 5, UNSET, true));
        m.addBond(bond(m, 5, 6, UNSET, true));
        m.addBond(bond(m, 6, 7, UNSET, true));
        m.addBond(bond(m, 7, 8, UNSET, true));
        m.addBond(bond(m, 8, 9, UNSET, true));
        m.addBond(bond(m, 9, 10, UNSET, true));
        m.addBond(bond(m, 10, 11, UNSET, true));
        m.addBond(bond(m, 6, 11, UNSET, true));
        assertBondOrders(m, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE,
                DOUBLE, SINGLE);
    }

    /**
     * @cdk.inchi InChI=1S/C6H4O2/c7-5-1-2-6(8)4-3-5/h1-4H
     */
    @Test
    public void quinone() throws Exception {
        IAtomContainer m = new AtomContainer(8, 8, 0, 0);
        m.addAtom(atom("O", 0, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("O", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 4, 6, UNSET, true));
        m.addBond(bond(m, 6, 7, UNSET, true));
        m.addBond(bond(m, 1, 7, UNSET, true));
        assertBondOrders(m, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    /**
     * InChI=1S/C13H10/c1-3-7-12-10(5-1)9-11-6-2-4-8-13(11)12/h1-8H,9H2
     */
    @Test
    public void fluorene() throws Exception {
        IAtomContainer m = new AtomContainer(13, 15, 0, 0);
        m.addAtom(atom("C", 2, false));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 0, true));
        m.addBond(bond(m, 0, 1, SINGLE, false));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 6, UNSET, true));
        m.addBond(bond(m, 1, 6, UNSET, true));
        m.addBond(bond(m, 6, 7, UNSET, true));
        m.addBond(bond(m, 7, 8, UNSET, true));
        m.addBond(bond(m, 8, 9, UNSET, true));
        m.addBond(bond(m, 9, 10, UNSET, true));
        m.addBond(bond(m, 10, 11, UNSET, true));
        m.addBond(bond(m, 11, 12, UNSET, true));
        m.addBond(bond(m, 0, 12, SINGLE, false));
        m.addBond(bond(m, 7, 12, UNSET, true));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE, SINGLE, DOUBLE, SINGLE, DOUBLE,
                SINGLE, DOUBLE, SINGLE, SINGLE);
    }

    /** @cdk.inchi InChI=1S/C5H5B/c1-2-4-6-5-3-1/h1-5H */
    @Test
    public void borinine() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("B", 0, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 5, UNSET, true));
        m.addBond(bond(m, 5, 0, UNSET, true));
        assertBondOrders(m, DOUBLE, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    // e.g. CHEMBL422679
    @Test public void sulfurCation() throws Exception {
        IAtomContainer m = new AtomContainer(5, 5, 0, 0);
        m.addAtom(atom("S", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.addAtom(atom("C", 1, true));
        m.getAtom(0).setFormalCharge(+1);
        m.addBond(bond(m, 0, 1, UNSET, true));
        m.addBond(bond(m, 1, 2, UNSET, true));
        m.addBond(bond(m, 2, 3, UNSET, true));
        m.addBond(bond(m, 3, 4, UNSET, true));
        m.addBond(bond(m, 4, 0, UNSET, true));
        assertBondOrders(m, SINGLE, DOUBLE, SINGLE, DOUBLE, SINGLE);
    }

    void assertBondOrders(IAtomContainer ac, IBond.Order... expected) throws Exception {
        Kekulization.kekulize(ac);
        IBond[] bonds = AtomContainerManipulator.getBondArray(ac);
        IBond.Order[] actual = new IBond.Order[bonds.length];
        for (int i = 0; i < bonds.length; i++)
            actual[i] = bonds[i].getOrder();
        assertThat(actual, is(expected));
    }

    static IAtom atom(String symbol, int h, boolean arom) {
        IAtom a = new Atom(symbol);
        a.setImplicitHydrogenCount(h);
        a.setFlag(CDKConstants.ISAROMATIC, arom);
        return a;
    }

    static IBond bond(IAtomContainer m, int v, int w, IBond.Order ord, boolean arom) {
        IBond b = new Bond(m.getAtom(v), m.getAtom(w));
        b.setOrder(ord);
        b.setFlag(CDKConstants.ISAROMATIC, arom);
        return b;
    }
}
