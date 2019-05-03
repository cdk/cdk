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

package org.openscience.cdk.forcefield.mmff;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.AtomContainer;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;

/**
 * Unit tests for MMFF symbolic atom types. This class primarily tests preconditions and some
 * failing cases from old implementations. The atom types of the MMFF validation suite is tested by
 * {@link MmffAtomTypeValidationSuiteTest}.
 */
public class MmffAtomTypeMatcherTest {

    static MmffAtomTypeMatcher INSTANCE = new MmffAtomTypeMatcher();

    @Test(expected = IllegalArgumentException.class)
    public void hydrogenCountMustBeDefined() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(new Atom("C"));
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("H"));
        container.addBond(0, 1, SINGLE);
        container.addBond(0, 2, SINGLE);
        container.addBond(0, 3, SINGLE);
        container.addBond(0, 4, SINGLE);
        container.getAtom(0).setImplicitHydrogenCount(null);
        INSTANCE.symbolicTypes(container);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hydrogenCountMustBeExplicit() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(new Atom("C"));
        container.getAtom(0).setImplicitHydrogenCount(4);
        INSTANCE.symbolicTypes(container);
    }

    @Test(expected = IllegalArgumentException.class)
    public void aromaticCompoundsAreRejected() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(new Atom("C"));
        container.getAtom(0).setImplicitHydrogenCount(4);
        container.getAtom(0).setFlag(CDKConstants.ISAROMATIC, true);
        INSTANCE.symbolicTypes(container);
    }

    /**
     * This test ensures a unit from the old ForceFieldConfigurator passes. The nitrogen should be
     * 'NC=O' and we see this is the case. SMILES: CC(C)C1CCC(CC1)C(=O)NC(Cc1ccccc1)C(=O)O
     *
     * @cdk.bug #3523240
     */
    @Test
    public void bug3523240IsResolved() throws Exception {
        IAtomContainer container = new AtomContainer(50, 51, 0, 0);
        container.addAtom(atom("H", 0));
        container.addAtom(atom("O", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("O", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("N", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("O", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.SINGLE);
        container.addBond(2, 3, IBond.Order.DOUBLE);
        container.addBond(2, 4, IBond.Order.SINGLE);
        container.addBond(4, 5, IBond.Order.SINGLE);
        container.addBond(4, 6, IBond.Order.SINGLE);
        container.addBond(6, 7, IBond.Order.SINGLE);
        container.addBond(6, 8, IBond.Order.SINGLE);
        container.addBond(8, 9, IBond.Order.DOUBLE);
        container.addBond(8, 10, IBond.Order.SINGLE);
        container.addBond(10, 11, IBond.Order.SINGLE);
        container.addBond(10, 12, IBond.Order.SINGLE);
        container.addBond(12, 13, IBond.Order.SINGLE);
        container.addBond(12, 14, IBond.Order.SINGLE);
        container.addBond(12, 15, IBond.Order.SINGLE);
        container.addBond(15, 16, IBond.Order.SINGLE);
        container.addBond(15, 17, IBond.Order.SINGLE);
        container.addBond(15, 18, IBond.Order.SINGLE);
        container.addBond(18, 19, IBond.Order.SINGLE);
        container.addBond(18, 20, IBond.Order.SINGLE);
        container.addBond(20, 21, IBond.Order.SINGLE);
        container.addBond(20, 22, IBond.Order.SINGLE);
        container.addBond(20, 23, IBond.Order.SINGLE);
        container.addBond(10, 23, IBond.Order.SINGLE);
        container.addBond(23, 24, IBond.Order.SINGLE);
        container.addBond(23, 25, IBond.Order.SINGLE);
        container.addBond(18, 26, IBond.Order.SINGLE);
        container.addBond(26, 27, IBond.Order.SINGLE);
        container.addBond(26, 28, IBond.Order.SINGLE);
        container.addBond(28, 29, IBond.Order.SINGLE);
        container.addBond(28, 30, IBond.Order.SINGLE);
        container.addBond(28, 31, IBond.Order.SINGLE);
        container.addBond(26, 32, IBond.Order.SINGLE);
        container.addBond(32, 33, IBond.Order.SINGLE);
        container.addBond(32, 34, IBond.Order.SINGLE);
        container.addBond(32, 35, IBond.Order.SINGLE);
        container.addBond(4, 36, IBond.Order.SINGLE);
        container.addBond(36, 37, IBond.Order.SINGLE);
        container.addBond(36, 38, IBond.Order.SINGLE);
        container.addBond(36, 39, IBond.Order.SINGLE);
        container.addBond(39, 40, IBond.Order.DOUBLE);
        container.addBond(40, 41, IBond.Order.SINGLE);
        container.addBond(40, 42, IBond.Order.SINGLE);
        container.addBond(42, 43, IBond.Order.SINGLE);
        container.addBond(42, 44, IBond.Order.DOUBLE);
        container.addBond(44, 45, IBond.Order.SINGLE);
        container.addBond(44, 46, IBond.Order.SINGLE);
        container.addBond(46, 47, IBond.Order.SINGLE);
        container.addBond(46, 48, IBond.Order.DOUBLE);
        container.addBond(39, 48, IBond.Order.SINGLE);
        container.addBond(48, 49, IBond.Order.SINGLE);
        String[] expected = {"HOCO", "OC=O", "COO", "O=CO", "CR", "HC", "NC=O", "HNCO", "C=ON", "O=CN", "CR", "HC",
                "CR", "HC", "HC", "CR", "HC", "HC", "CR", "HC", "CR", "HC", "HC", "CR", "HC", "HC", "CR", "HC", "CR",
                "HC", "HC", "HC", "CR", "HC", "HC", "HC", "CR", "HC", "HC", "CB", "CB", "HC", "CB", "HC", "CB", "HC",
                "CB", "HC", "CB", "HC"};
        String[] actual = INSTANCE.symbolicTypes(container);
        Assert.assertArrayEquals(expected, actual);
    }

    /**
     * This test ensures a unit from the old ForceFieldConfigurator passes. The nitrogen should be
     * 'NO2', it was previously assigned 'N2OX'. SMILES: CC[N+](=O)[O-]
     *
     * @cdk.bug #3524734
     */
    @Test
    public void bug3524734IsResolved() throws Exception {
        IAtomContainer container = new AtomContainer(10, 9, 0, 0);
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("N", 0));
        container.addAtom(atom("O", 0));
        container.addAtom(atom("O", 0));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.SINGLE);
        container.addBond(1, 3, IBond.Order.SINGLE);
        container.addBond(1, 4, IBond.Order.SINGLE);
        container.addBond(4, 5, IBond.Order.SINGLE);
        container.addBond(4, 6, IBond.Order.SINGLE);
        container.addBond(4, 7, IBond.Order.SINGLE);
        container.addBond(7, 8, IBond.Order.SINGLE);
        container.addBond(7, 9, IBond.Order.DOUBLE);

        String[] expected = {"HC", "CR", "HC", "HC", "CR", "HC", "HC", "NO2", "O2N", "O2N"};
        String[] actual = INSTANCE.symbolicTypes(container);
        Assert.assertArrayEquals(expected, actual);
    }

    /**
     * An old test from ForceFieldConfigurator. The expected atom types listed in that test are
     * don't seem right, here 'CONN' and 'NC=O' is definitely correct. Previously the test expected
     * N2OX but this is for nitrogen cations so '*[NH+]([O-])*', NC=O is more likely to be correct.
     */
    @Test
    public void hydroxyurea() {
        IAtomContainer container = new AtomContainer(9, 8, 0, 0);
        container.addAtom(atom("H", 0));
        container.addAtom(atom("O", 0));
        container.addAtom(atom("N", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("O", 0));
        container.addAtom(atom("N", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.SINGLE);
        container.addBond(2, 3, IBond.Order.SINGLE);
        container.addBond(2, 4, IBond.Order.SINGLE);
        container.addBond(4, 5, IBond.Order.DOUBLE);
        container.addBond(4, 6, IBond.Order.SINGLE);
        container.addBond(6, 7, IBond.Order.SINGLE);
        container.addBond(6, 8, IBond.Order.SINGLE);
        String[] expected = {"HO", "-O-", "NC=O", "HNCO", "CONN", "O=CN", "NC=O", "HNCO", "HNCO"};
        String[] actual = INSTANCE.symbolicTypes(container);
        Assert.assertArrayEquals(expected, actual);
    }

    /**
     * The MMFF articles mention H2 as a special case for assigning hydrogen types. However it is
     * not mentioned what type they are assigned. This test simply shows molecular hydrogens don't
     * break the assignment and are set to null.
     */
    @Test
    public void molecularHydrogenDoesNotBreakAssignment() {
        IAtomContainer container = new AtomContainer(2, 1, 0, 0);
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addBond(0, 1, SINGLE);
        String[] expected = {null, null};
        String[] actual = INSTANCE.symbolicTypes(container);
        Assert.assertArrayEquals(expected, actual);
    }

    /**
     * MMFF94AtomTypeMatcherTest.testFindMatchingAtomType_IAtomContainer_IAtom_Methylamine. The
     * nitrogen was being assigned NPYL by MMFF94AtomTypeMatcherTest. It is now assigned 'NR:
     * NITROGEN IN ALIPHATIC AMINES'.
     */
    @Test
    public void methylamine() {
        IAtomContainer container = new AtomContainer(7, 6, 0, 0);
        container.addAtom(atom("H", 0));
        container.addAtom(atom("N", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.SINGLE);
        container.addBond(1, 3, IBond.Order.SINGLE);
        container.addBond(3, 4, IBond.Order.SINGLE);
        container.addBond(3, 5, IBond.Order.SINGLE);
        container.addBond(3, 6, IBond.Order.SINGLE);
        String[] expected = {"HNR", "NR", "HNR", "CR", "HC", "HC", "HC"};
        String[] actual = INSTANCE.symbolicTypes(container);
        Assert.assertArrayEquals(expected, actual);
    }

    /**
     * MMFF94AtomTypeMatcherTest.testSthi would not assign STHI in thiophene. This is no longer the
     * case.
     */
    @Test
    public void thiophene() {
        IAtomContainer container = new AtomContainer(9, 9, 0, 0);
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("S", 0));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.DOUBLE);
        container.addBond(2, 3, IBond.Order.SINGLE);
        container.addBond(2, 4, IBond.Order.SINGLE);
        container.addBond(4, 5, IBond.Order.SINGLE);
        container.addBond(4, 6, IBond.Order.DOUBLE);
        container.addBond(6, 7, IBond.Order.SINGLE);
        container.addBond(6, 8, IBond.Order.SINGLE);
        container.addBond(1, 8, IBond.Order.SINGLE);
        String[] expected = {"HC", "C5A", "C5B", "HC", "C5B", "HC", "C5A", "HC", "STHI"};
        String[] actual = INSTANCE.symbolicTypes(container);
        Assert.assertArrayEquals(expected, actual);
    }

    /**
     * MMFF94AtomTypeMatcherTest.testOar would not assign OFUR in thiophene. This is no longer the
     * case. Note the CDK used 'Oar' instead of the actual 'OFUR' type.
     */
    @Test
    public void furane() {
        IAtomContainer container = new AtomContainer(9, 9, 0, 0);
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("O", 0));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.DOUBLE);
        container.addBond(2, 3, IBond.Order.SINGLE);
        container.addBond(2, 4, IBond.Order.SINGLE);
        container.addBond(4, 5, IBond.Order.SINGLE);
        container.addBond(4, 6, IBond.Order.DOUBLE);
        container.addBond(6, 7, IBond.Order.SINGLE);
        container.addBond(6, 8, IBond.Order.SINGLE);
        container.addBond(1, 8, IBond.Order.SINGLE);
        String[] expected = {"HC", "C5A", "C5B", "HC", "C5B", "HC", "C5A", "HC", "OFUR"};
        String[] actual = INSTANCE.symbolicTypes(container);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void methane() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(atom("C", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addAtom(atom("H", 0));
        container.addBond(0, 1, SINGLE);
        container.addBond(0, 2, SINGLE);
        container.addBond(0, 3, SINGLE);
        container.addBond(0, 4, SINGLE);
        String[] expected = {"CR", "HC", "HC", "HC", "HC"};
        String[] actual = INSTANCE.symbolicTypes(container);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test(expected = IOException.class)
    public void invalidSmilesThrowsIOExceptionForTokenManagerError() throws IOException {
        String row = "INVALID.SMILES X";
        ByteArrayInputStream in = new ByteArrayInputStream(row.getBytes());
        try {
            MmffAtomTypeMatcher.loadPatterns(in);
        } finally {
            in.close();
        }
    }

    @Test(expected = IOException.class)
    public void invalidSmilesThrowsIOExceptionForIllegalArgument() throws IOException {
        String row = "23 X";
        ByteArrayInputStream in = new ByteArrayInputStream(row.getBytes());
        try {
            MmffAtomTypeMatcher.loadPatterns(in);
        } finally {
            in.close();
        }
    }

    static IAtom atom(String symb, int h) {
        IAtom atom = new org.openscience.cdk.silent.Atom(symb);
        atom.setImplicitHydrogenCount(h);
        return atom;
    }
}
