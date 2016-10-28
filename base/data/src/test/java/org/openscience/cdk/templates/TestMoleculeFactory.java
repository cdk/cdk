/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.templates;

import javax.vecmath.Point2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * This class contains methods for generating simple organic molecules and is
 * copy of {@link MoleculeFactory} for use in tests.
 *
 * @cdk.module test-data
 */
public class TestMoleculeFactory {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(TestMoleculeFactory.class);

    public static IAtomContainer makeAlphaPinene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9
        mol.addAtom(new Atom("C")); // 10

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(0, 6, IBond.Order.SINGLE); // 7
        mol.addBond(3, 7, IBond.Order.SINGLE); // 8
        mol.addBond(5, 7, IBond.Order.SINGLE); // 9
        mol.addBond(7, 8, IBond.Order.SINGLE); // 10
        mol.addBond(7, 9, IBond.Order.SINGLE); // 11
        configureAtoms(mol);
        return mol;
    }

    /**
     * Generate an Alkane (chain of carbons with no hydrogens) of a given length.
     *
     * <p>This method was written by Stephen Tomkinson.
     *
     * @param chainLength The number of carbon atoms to have in the chain.
     * @return A molecule containing a bonded chain of carbons.
     * @cdk.created 2003-08-15
     */
    public static IAtomContainer makeAlkane(int chainLength) {
        IAtomContainer currentChain = new AtomContainer();

        //Add the initial atom
        currentChain.addAtom(new Atom("C"));

        //Add further atoms and bonds as needed, a pair at a time.
        for (int atomCount = 1; atomCount < chainLength; atomCount++) {
            currentChain.addAtom(new Atom("C"));
            currentChain.addBond(atomCount, atomCount - 1, IBond.Order.SINGLE);
        }

        return currentChain;
    }

    public static IAtomContainer makeEthylCyclohexane() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(0, 6, IBond.Order.SINGLE); // 7
        mol.addBond(6, 7, IBond.Order.SINGLE); // 8
        return mol;
    }

    /**
     * Returns cyclohexene without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C6H10/c1-2-4-6-5-3-1/h1-2H,3-6H2
     */
    public static IAtomContainer makeCyclohexene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.DOUBLE); // 6
        return mol;
    }

    /**
     * Returns cyclohexane without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C6H12/c1-2-4-6-5-3-1/h1-6H2
     */
    public static IAtomContainer makeCyclohexane() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        return mol;
    }

    /**
     * Returns cyclopentane without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C5H10/c1-2-4-5-3-1/h1-5H2
     */
    public static IAtomContainer makeCyclopentane() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.SINGLE); // 5
        return mol;
    }

    /**
     * Returns cyclobutane without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C4H8/c1-2-4-3-1/h1-4H2
     */
    public static IAtomContainer makeCyclobutane() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 0, IBond.Order.SINGLE); // 4
        return mol;
    }

    /**
     * Returns cyclobutadiene without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C4H4/c1-2-4-3-1/h1-4H
     */
    public static IAtomContainer makeCyclobutadiene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 0, IBond.Order.DOUBLE); // 4
        return mol;
    }

    public static IAtomContainer makePropylCycloPropane() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 4
        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 0, IBond.Order.SINGLE); // 3
        mol.addBond(2, 3, IBond.Order.SINGLE); // 4
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 4

        return mol;
    }

    /**
     * Returns biphenyl without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C12H10/c1-3-7-11(8-4-1)12-9-5-2-6-10-12/h1-10H
     */
    public static IAtomContainer makeBiphenyl() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9
        mol.addAtom(new Atom("C")); // 10
        mol.addAtom(new Atom("C")); // 11

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6

        mol.addBond(0, 6, IBond.Order.SINGLE); // 7
        mol.addBond(6, 7, IBond.Order.SINGLE); // 8
        mol.addBond(7, 8, IBond.Order.DOUBLE); // 5
        mol.addBond(8, 9, IBond.Order.SINGLE); // 6
        mol.addBond(9, 10, IBond.Order.DOUBLE); // 7
        mol.addBond(10, 11, IBond.Order.SINGLE); // 8
        mol.addBond(11, 6, IBond.Order.DOUBLE); // 5
        return mol;
    }

    public static IAtomContainer makePhenylEthylBenzene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9
        mol.addAtom(new Atom("C")); // 10
        mol.addAtom(new Atom("C")); // 11
        mol.addAtom(new Atom("C")); // 12
        mol.addAtom(new Atom("C")); // 13

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6

        mol.addBond(0, 6, IBond.Order.SINGLE); // 7
        mol.addBond(6, 7, IBond.Order.SINGLE); // 8
        mol.addBond(7, 8, IBond.Order.SINGLE); // 5
        mol.addBond(8, 9, IBond.Order.SINGLE); // 6
        mol.addBond(9, 10, IBond.Order.DOUBLE); // 7
        mol.addBond(10, 11, IBond.Order.SINGLE); // 8
        mol.addBond(11, 12, IBond.Order.DOUBLE); // 5
        mol.addBond(12, 13, IBond.Order.SINGLE);
        mol.addBond(13, 8, IBond.Order.DOUBLE); // 5
        return mol;
    }

    public static IAtomContainer makePhenylAmine() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("N")); // 6

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6

        mol.addBond(0, 6, IBond.Order.SINGLE); // 7
        return mol;
    }

    /* build a molecule from 4 condensed triangles */
    public static IAtomContainer make4x3CondensedRings() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 0, IBond.Order.SINGLE); // 3
        mol.addBond(2, 3, IBond.Order.SINGLE); // 4
        mol.addBond(1, 3, IBond.Order.SINGLE); // 5
        mol.addBond(3, 4, IBond.Order.SINGLE); // 6
        mol.addBond(4, 2, IBond.Order.SINGLE); // 7
        mol.addBond(4, 5, IBond.Order.SINGLE); // 8
        mol.addBond(5, 3, IBond.Order.SINGLE); // 9

        return mol;
    }

    public static IAtomContainer makeSpiroRings() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 6, IBond.Order.SINGLE); // 6
        mol.addBond(6, 0, IBond.Order.SINGLE); // 7
        mol.addBond(6, 7, IBond.Order.SINGLE); // 8
        mol.addBond(7, 8, IBond.Order.SINGLE); // 9
        mol.addBond(8, 9, IBond.Order.SINGLE); // 10
        mol.addBond(9, 6, IBond.Order.SINGLE); // 11
        return mol;
    }

    public static IAtomContainer makeBicycloRings() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(6, 0, IBond.Order.SINGLE); // 7
        mol.addBond(6, 7, IBond.Order.SINGLE); // 8
        mol.addBond(7, 3, IBond.Order.SINGLE); // 9
        return mol;
    }

    public static IAtomContainer makeFusedRings() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(5, 6, IBond.Order.SINGLE); // 7
        mol.addBond(6, 7, IBond.Order.SINGLE); // 8
        mol.addBond(7, 4, IBond.Order.SINGLE); // 9
        mol.addBond(8, 0, IBond.Order.SINGLE); // 10
        mol.addBond(9, 1, IBond.Order.SINGLE); // 11
        mol.addBond(9, 8, IBond.Order.SINGLE); // 11
        return mol;
    }

    public static IAtomContainer makeMethylDecaline() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9
        mol.addAtom(new Atom("C")); // 10

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(5, 6, IBond.Order.SINGLE); // 7
        mol.addBond(6, 7, IBond.Order.SINGLE); // 8RingSet
        mol.addBond(7, 8, IBond.Order.SINGLE); // 9
        mol.addBond(8, 9, IBond.Order.SINGLE); // 10
        mol.addBond(9, 0, IBond.Order.SINGLE); // 11
        mol.addBond(5, 10, IBond.Order.SINGLE); // 12
        return mol;

    }

    public static IAtomContainer makeEthylPropylPhenantren() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9
        mol.addAtom(new Atom("C")); // 10
        mol.addAtom(new Atom("C")); // 11
        mol.addAtom(new Atom("C")); // 12
        mol.addAtom(new Atom("C")); // 13
        mol.addAtom(new Atom("C")); // 14
        mol.addAtom(new Atom("C")); // 15
        mol.addAtom(new Atom("C")); // 16
        mol.addAtom(new Atom("C")); // 17
        mol.addAtom(new Atom("C")); // 18

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.DOUBLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 6, IBond.Order.DOUBLE); // 6
        mol.addBond(6, 7, IBond.Order.SINGLE); // 8
        mol.addBond(7, 8, IBond.Order.DOUBLE); // 9
        mol.addBond(8, 9, IBond.Order.SINGLE); // 10
        mol.addBond(9, 0, IBond.Order.DOUBLE); // 11
        mol.addBond(9, 4, IBond.Order.SINGLE); // 12
        mol.addBond(8, 10, IBond.Order.SINGLE); // 12
        mol.addBond(10, 11, IBond.Order.DOUBLE); // 12
        mol.addBond(11, 12, IBond.Order.SINGLE); // 12
        mol.addBond(12, 13, IBond.Order.DOUBLE); // 12
        mol.addBond(13, 7, IBond.Order.SINGLE); // 12
        mol.addBond(3, 14, IBond.Order.SINGLE); // 12
        mol.addBond(14, 15, IBond.Order.SINGLE); // 12
        mol.addBond(12, 16, IBond.Order.SINGLE); // 12
        mol.addBond(16, 17, IBond.Order.SINGLE); // 12
        mol.addBond(17, 18, IBond.Order.SINGLE); // 12
        configureAtoms(mol);
        return mol;
    }

    public static IAtomContainer makeSteran() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9
        mol.addAtom(new Atom("C")); // 10
        mol.addAtom(new Atom("C")); // 11
        mol.addAtom(new Atom("C")); // 12
        mol.addAtom(new Atom("C")); // 13
        mol.addAtom(new Atom("C")); // 14
        mol.addAtom(new Atom("C")); // 15
        mol.addAtom(new Atom("C")); // 16

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 6, IBond.Order.SINGLE); // 6
        mol.addBond(6, 7, IBond.Order.SINGLE); // 8
        mol.addBond(7, 8, IBond.Order.SINGLE); // 9
        mol.addBond(8, 9, IBond.Order.SINGLE); // 10
        mol.addBond(9, 0, IBond.Order.SINGLE); // 11
        mol.addBond(9, 4, IBond.Order.SINGLE); // 12
        mol.addBond(8, 10, IBond.Order.SINGLE); // 13
        mol.addBond(10, 11, IBond.Order.SINGLE); // 14
        mol.addBond(11, 12, IBond.Order.SINGLE); // 15
        mol.addBond(12, 13, IBond.Order.SINGLE); // 16
        mol.addBond(13, 7, IBond.Order.SINGLE); // 17
        mol.addBond(13, 14, IBond.Order.SINGLE); // 18
        mol.addBond(14, 15, IBond.Order.SINGLE); // 19
        mol.addBond(15, 16, IBond.Order.SINGLE); // 20
        mol.addBond(16, 12, IBond.Order.SINGLE); // 21

        configureAtoms(mol);
        return mol;
    }

    /**
     * Returns azulene without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C10H8/c1-2-5-9-7-4-8-10(9)6-3-1/h1-8H
     */
    public static IAtomContainer makeAzulene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 6, IBond.Order.SINGLE); // 6
        mol.addBond(6, 7, IBond.Order.DOUBLE); // 8
        mol.addBond(7, 8, IBond.Order.SINGLE); // 9
        mol.addBond(8, 9, IBond.Order.DOUBLE); // 10
        mol.addBond(9, 5, IBond.Order.SINGLE); // 11
        mol.addBond(9, 0, IBond.Order.SINGLE); // 12

        return mol;
    }

    /**
     * Returns indole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C8H7N/c1-2-4-8-7(3-1)5-6-9-8/h1-6,9H
     */
    public static IAtomContainer makeIndole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("N")); // 8

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 6, IBond.Order.SINGLE); // 6
        mol.addBond(6, 7, IBond.Order.DOUBLE); // 8
        mol.addBond(7, 8, IBond.Order.SINGLE); // 9
        mol.addBond(0, 5, IBond.Order.SINGLE); // 11
        mol.addBond(8, 0, IBond.Order.SINGLE); // 12

        return mol;
    }

    /**
     * Returns pyrrole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C4H5N/c1-2-4-5-3-1/h1-5H
     */
    public static IAtomContainer makePyrrole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns pyrrole anion without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C4H4N/c1-2-4-5-3-1/h1-4H/q-1
     */
    public static IAtomContainer makePyrroleAnion() {
        IAtomContainer mol = new AtomContainer();
        IAtom nitrogenAnion = new Atom("N");
        nitrogenAnion.setFormalCharge(-1);
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(nitrogenAnion); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns imidazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C3H4N2/c1-2-5-3-4-1/h1-3H,(H,4,5)/f/h4H
     */
    public static IAtomContainer makeImidazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("N")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns pyrazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C3H4N2/c1-2-4-5-3-1/h1-3H,(H,4,5)/f/h4H
     */
    public static IAtomContainer makePyrazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("N")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns 1,2,4-triazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C3H4N2/c1-2-4-5-3-1/h1-3H,(H,4,5)/f/h4H
     */
    public static IAtomContainer make124Triazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("N")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("N")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns 1,2,3-triazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C2H3N3/c1-2-4-5-3-1/h1-2H,(H,3,4,5)/f/h5H
     */
    public static IAtomContainer make123Triazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("N")); // 2
        mol.addAtom(new Atom("N")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns tetrazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/CH2N4/c1-2-4-5-3-1/h1H,(H,2,3,4,5)/f/h4H
     */
    public static IAtomContainer makeTetrazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("N")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("N")); // 2
        mol.addAtom(new Atom("N")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns Oxazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C3H3NO/c1-2-5-3-4-1/h1-3H
     */
    public static IAtomContainer makeOxazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("O")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("N")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns Isoxazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C3H3NO/c1-2-4-5-3-1/h1-3H
     */
    public static IAtomContainer makeIsoxazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("O")); // 1
        mol.addAtom(new Atom("N")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns isothiazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C3H3NS/c1-2-4-5-3-1/h1-3H
     */
    public static IAtomContainer makeIsothiazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("S")); // 1
        mol.addAtom(new Atom("N")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns thiadiazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C2H2N2S/c1-3-4-2-5-1/h1-2H
     */
    public static IAtomContainer makeThiadiazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("S")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("N")); // 3
        mol.addAtom(new Atom("N")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns oxadiazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C2H2N2O/c1-3-4-2-5-1/h1-2H
     */
    public static IAtomContainer makeOxadiazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("O")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("N")); // 3
        mol.addAtom(new Atom("N")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    /**
     * Returns pyridine without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C3H3NO/c1-2-4-5-3-1/h1-3H
     */
    public static IAtomContainer makePyridine() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6

        return mol;
    }

    /**
     * Returns pyridine oxide without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C5H5NO/c7-6-4-2-1-3-5-6/h1-5H
     */
    public static IAtomContainer makePyridineOxide() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.getAtom(1).setFormalCharge(1);
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("O")); // 6
        mol.getAtom(6).setFormalCharge(-1);

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(1, 6, IBond.Order.SINGLE); // 7

        return mol;
    }

    /**
     * Returns pyrimidine without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C4H4N2/c1-2-5-4-6-3-1/h1-4H
     */
    public static IAtomContainer makePyrimidine() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("N")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6

        return mol;
    }

    /**
     * Returns pyridazine without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C4H4N2/c1-2-4-6-5-3-1/h1-4H
     */
    public static IAtomContainer makePyridazine() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("N")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6

        return mol;
    }

    /**
     * Returns triazine without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C4H4N2/c1-2-4-6-5-3-1/h1-4H
     */
    public static IAtomContainer makeTriazine() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("N")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("N")); // 5

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6

        return mol;
    }

    /**
     * Returns thiazole without explicit hydrogens.
     *
     * @cdk.inchi InChI=1/C3H3NS/c1-2-5-3-4-1/h1-3H
     */
    public static IAtomContainer makeThiazole() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("S")); // 3
        mol.addAtom(new Atom("C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    public static IAtomContainer makeSingleRing() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        //		mol.addAtom(new Atom("C")); // 6
        //		mol.addAtom(new Atom("C")); // 7
        //		mol.addAtom(new Atom("C")); // 8
        //		mol.addAtom(new Atom("C")); // 9

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        //		mol.addBond(5, 6, IBond.Order.SINGLE); // 7
        //		mol.addBond(6, 7, IBond.Order.SINGLE); // 8
        //		mol.addBond(7, 4, IBond.Order.SINGLE); // 9
        //		mol.addBond(8, 0, IBond.Order.SINGLE); // 10
        //		mol.addBond(9, 1, IBond.Order.SINGLE); // 11

        return mol;
    }

    public static IAtomContainer makeDiamantane() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9
        mol.addAtom(new Atom("C")); // 10
        mol.addAtom(new Atom("C")); // 11
        mol.addAtom(new Atom("C")); // 12
        mol.addAtom(new Atom("C")); // 13

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(5, 6, IBond.Order.SINGLE); // 7
        mol.addBond(6, 9, IBond.Order.SINGLE); // 8
        mol.addBond(1, 7, IBond.Order.SINGLE); // 9
        mol.addBond(7, 9, IBond.Order.SINGLE); // 10
        mol.addBond(3, 8, IBond.Order.SINGLE); // 11
        mol.addBond(8, 9, IBond.Order.SINGLE); // 12
        mol.addBond(0, 10, IBond.Order.SINGLE); // 13
        mol.addBond(10, 13, IBond.Order.SINGLE); // 14
        mol.addBond(2, 11, IBond.Order.SINGLE); // 15
        mol.addBond(11, 13, IBond.Order.SINGLE); // 16
        mol.addBond(4, 12, IBond.Order.SINGLE); // 17
        mol.addBond(12, 13, IBond.Order.SINGLE); // 18

        return mol;
    }

    public static IAtomContainer makeBranchedAliphatic() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8
        mol.addAtom(new Atom("C")); // 9
        mol.addAtom(new Atom("C")); // 10
        mol.addAtom(new Atom("C")); // 11
        mol.addAtom(new Atom("C")); // 12
        mol.addAtom(new Atom("C")); // 13
        mol.addAtom(new Atom("C")); // 14
        mol.addAtom(new Atom("C")); // 15
        mol.addAtom(new Atom("C")); // 16
        mol.addAtom(new Atom("C")); // 17
        mol.addAtom(new Atom("C")); // 18

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(2, 6, IBond.Order.SINGLE); // 6
        mol.addBond(6, 7, IBond.Order.SINGLE); // 7
        mol.addBond(7, 8, IBond.Order.SINGLE); // 8
        mol.addBond(6, 9, IBond.Order.SINGLE); // 9
        mol.addBond(6, 10, IBond.Order.SINGLE); // 10
        mol.addBond(10, 11, IBond.Order.SINGLE); // 11
        mol.addBond(8, 12, IBond.Order.TRIPLE); // 12
        mol.addBond(12, 13, IBond.Order.SINGLE); // 13
        mol.addBond(11, 14, IBond.Order.SINGLE); // 14
        mol.addBond(9, 15, IBond.Order.SINGLE);
        mol.addBond(15, 16, IBond.Order.DOUBLE);
        mol.addBond(16, 17, IBond.Order.DOUBLE);
        mol.addBond(17, 18, IBond.Order.SINGLE);

        return mol;
    }

    public static IAtomContainer makeBenzene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.DOUBLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.DOUBLE); // 6
        return mol;
    }

    public static IAtomContainer makeQuinone() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("O")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("O")); // 7

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 6, IBond.Order.DOUBLE); // 6
        mol.addBond(6, 1, IBond.Order.SINGLE); // 7
        mol.addBond(4, 7, IBond.Order.DOUBLE); // 8
        return mol;
    }

    public static IAtomContainer makePiperidine() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("N"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));

        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(5, 0, IBond.Order.SINGLE);

        mol.addBond(0, 6, IBond.Order.SINGLE);

        return mol;

    }

    public static IAtomContainer makeTetrahydropyran() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));

        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(5, 0, IBond.Order.SINGLE);

        return mol;

    }

    /**
     * @cdk.inchi InChI=1/C5H5N5/c6-4-3-5(9-1-7-3)10-2-8-4/h1-2H,(H3,6,7,8,9,10)/f/h7H,6H2
     */
    public static IAtomContainer makeAdenine() {
        IAtomContainer mol = new AtomContainer(); // Adenine
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        a1.setPoint2d(new Point2d(21.0223, -17.2946));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        a2.setPoint2d(new Point2d(21.0223, -18.8093));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        a3.setPoint2d(new Point2d(22.1861, -16.6103));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "N");
        a4.setPoint2d(new Point2d(19.8294, -16.8677));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "N");
        a5.setPoint2d(new Point2d(22.2212, -19.5285));
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "N");
        a6.setPoint2d(new Point2d(19.8177, -19.2187));
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "N");
        a7.setPoint2d(new Point2d(23.4669, -17.3531));
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "N");
        a8.setPoint2d(new Point2d(22.1861, -15.2769));
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "C");
        a9.setPoint2d(new Point2d(18.9871, -18.0139));
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "C");
        a10.setPoint2d(new Point2d(23.4609, -18.8267));
        mol.addAtom(a10);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a2, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a2, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a3, a7, IBond.Order.DOUBLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a3, a8, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a4, a9, IBond.Order.DOUBLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a5, a10, IBond.Order.DOUBLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a6, a9, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a7, a10, IBond.Order.SINGLE);
        mol.addBond(b11);

        return mol;
    }

    /**
     * InChI=1/C10H8/c1-2-6-10-8-4-3-7-9(10)5-1/h1-8H
     */
    public static IAtomContainer makeNaphthalene() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        a8.setFormalCharge(0);
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        a9.setFormalCharge(0);
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        a10.setFormalCharge(0);
        mol.addAtom(a10);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a3, a4, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a4, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a5, a6, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a6, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a7, a8, IBond.Order.DOUBLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a3, a8, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a8, a9, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a9, a10, IBond.Order.DOUBLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a1, a10, IBond.Order.SINGLE);
        mol.addBond(b11);
        return mol;
    }

    /**
     * @cdk.inchi InChI=1/C14H10/c1-2-6-12-10-14-8-4-3-7-13(14)9-11(12)5-1/h1-10H
     */
    public static IAtomContainer makeAnthracene() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        a8.setFormalCharge(0);
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        a9.setFormalCharge(0);
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        a10.setFormalCharge(0);
        mol.addAtom(a10);
        IAtom a11 = builder.newInstance(IAtom.class, "C");
        a11.setFormalCharge(0);
        mol.addAtom(a11);
        IAtom a12 = builder.newInstance(IAtom.class, "C");
        a12.setFormalCharge(0);
        mol.addAtom(a12);
        IAtom a13 = builder.newInstance(IAtom.class, "C");
        a13.setFormalCharge(0);
        mol.addAtom(a13);
        IAtom a14 = builder.newInstance(IAtom.class, "C");
        a14.setFormalCharge(0);
        mol.addAtom(a14);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a3, a4, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a4, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a5, a6, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a6, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a7, a8, IBond.Order.DOUBLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a8, a9, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a9, a10, IBond.Order.DOUBLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a5, a10, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a10, a11, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = builder.newInstance(IBond.class, a11, a12, IBond.Order.DOUBLE);
        mol.addBond(b12);
        IBond b13 = builder.newInstance(IBond.class, a3, a12, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = builder.newInstance(IBond.class, a12, a13, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = builder.newInstance(IBond.class, a13, a14, IBond.Order.DOUBLE);
        mol.addBond(b15);
        IBond b16 = builder.newInstance(IBond.class, a1, a14, IBond.Order.SINGLE);
        mol.addBond(b16);
        return mol;
    }

    /**
     * octacyclo[17.2.2.2¹,⁴.2⁴,⁷.2⁷,¹⁰.2¹⁰,¹³.2¹³,¹⁶.2¹⁶,¹⁹]pentatriacontane
     * @cdk.inchi InChI=1/C35H56/c1-2-30-6-3-29(1)4-7-31(8-5-29)13-15-33(16-14-31)21-23-35(24-22-33)27-25-34(26-28-35)19-17-32(11-9-30,12-10-30)18-20-34/h1-28H2
     */
    public static IAtomContainer makeCyclophaneLike() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        a8.setFormalCharge(0);
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        a9.setFormalCharge(0);
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        a10.setFormalCharge(0);
        mol.addAtom(a10);
        IAtom a11 = builder.newInstance(IAtom.class, "C");
        a11.setFormalCharge(0);
        mol.addAtom(a11);
        IAtom a12 = builder.newInstance(IAtom.class, "C");
        a12.setFormalCharge(0);
        mol.addAtom(a12);
        IAtom a13 = builder.newInstance(IAtom.class, "C");
        a13.setFormalCharge(0);
        mol.addAtom(a13);
        IAtom a14 = builder.newInstance(IAtom.class, "C");
        a14.setFormalCharge(0);
        mol.addAtom(a14);
        IAtom a15 = builder.newInstance(IAtom.class, "C");
        a15.setFormalCharge(0);
        mol.addAtom(a15);
        IAtom a16 = builder.newInstance(IAtom.class, "C");
        a16.setFormalCharge(0);
        mol.addAtom(a16);
        IAtom a17 = builder.newInstance(IAtom.class, "C");
        a17.setFormalCharge(0);
        mol.addAtom(a17);
        IAtom a18 = builder.newInstance(IAtom.class, "C");
        a18.setFormalCharge(0);
        mol.addAtom(a18);
        IAtom a19 = builder.newInstance(IAtom.class, "C");
        a19.setFormalCharge(0);
        mol.addAtom(a19);
        IAtom a20 = builder.newInstance(IAtom.class, "C");
        a20.setFormalCharge(0);
        mol.addAtom(a20);
        IAtom a21 = builder.newInstance(IAtom.class, "C");
        a21.setFormalCharge(0);
        mol.addAtom(a21);
        IAtom a22 = builder.newInstance(IAtom.class, "C");
        a22.setFormalCharge(0);
        mol.addAtom(a22);
        IAtom a23 = builder.newInstance(IAtom.class, "C");
        a23.setFormalCharge(0);
        mol.addAtom(a23);
        IAtom a24 = builder.newInstance(IAtom.class, "C");
        a24.setFormalCharge(0);
        mol.addAtom(a24);
        IAtom a25 = builder.newInstance(IAtom.class, "C");
        a25.setFormalCharge(0);
        mol.addAtom(a25);
        IAtom a26 = builder.newInstance(IAtom.class, "C");
        a26.setFormalCharge(0);
        mol.addAtom(a26);
        IAtom a27 = builder.newInstance(IAtom.class, "C");
        a27.setFormalCharge(0);
        mol.addAtom(a27);
        IAtom a28 = builder.newInstance(IAtom.class, "C");
        a28.setFormalCharge(0);
        mol.addAtom(a28);
        IAtom a29 = builder.newInstance(IAtom.class, "C");
        a29.setFormalCharge(0);
        mol.addAtom(a29);
        IAtom a30 = builder.newInstance(IAtom.class, "C");
        a30.setFormalCharge(0);
        mol.addAtom(a30);
        IAtom a31 = builder.newInstance(IAtom.class, "C");
        a31.setFormalCharge(0);
        mol.addAtom(a31);
        IAtom a32 = builder.newInstance(IAtom.class, "C");
        a32.setFormalCharge(0);
        mol.addAtom(a32);
        IAtom a33 = builder.newInstance(IAtom.class, "C");
        a33.setFormalCharge(0);
        mol.addAtom(a33);
        IAtom a34 = builder.newInstance(IAtom.class, "C");
        a34.setFormalCharge(0);
        mol.addAtom(a34);
        IAtom a35 = builder.newInstance(IAtom.class, "C");
        a35.setFormalCharge(0);
        mol.addAtom(a35);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a4, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a5, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a6, a7, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a7, a8, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a8, a9, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a9, a10, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a10, a11, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = builder.newInstance(IBond.class, a6, a11, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = builder.newInstance(IBond.class, a9, a12, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = builder.newInstance(IBond.class, a12, a13, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = builder.newInstance(IBond.class, a13, a14, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = builder.newInstance(IBond.class, a14, a15, IBond.Order.SINGLE);
        mol.addBond(b16);
        IBond b17 = builder.newInstance(IBond.class, a15, a16, IBond.Order.SINGLE);
        mol.addBond(b17);
        IBond b18 = builder.newInstance(IBond.class, a9, a16, IBond.Order.SINGLE);
        mol.addBond(b18);
        IBond b19 = builder.newInstance(IBond.class, a14, a17, IBond.Order.SINGLE);
        mol.addBond(b19);
        IBond b20 = builder.newInstance(IBond.class, a17, a18, IBond.Order.SINGLE);
        mol.addBond(b20);
        IBond b21 = builder.newInstance(IBond.class, a18, a19, IBond.Order.SINGLE);
        mol.addBond(b21);
        IBond b22 = builder.newInstance(IBond.class, a19, a20, IBond.Order.SINGLE);
        mol.addBond(b22);
        IBond b23 = builder.newInstance(IBond.class, a20, a21, IBond.Order.SINGLE);
        mol.addBond(b23);
        IBond b24 = builder.newInstance(IBond.class, a14, a21, IBond.Order.SINGLE);
        mol.addBond(b24);
        IBond b25 = builder.newInstance(IBond.class, a19, a22, IBond.Order.SINGLE);
        mol.addBond(b25);
        IBond b26 = builder.newInstance(IBond.class, a22, a23, IBond.Order.SINGLE);
        mol.addBond(b26);
        IBond b27 = builder.newInstance(IBond.class, a23, a24, IBond.Order.SINGLE);
        mol.addBond(b27);
        IBond b28 = builder.newInstance(IBond.class, a24, a25, IBond.Order.SINGLE);
        mol.addBond(b28);
        IBond b29 = builder.newInstance(IBond.class, a25, a26, IBond.Order.SINGLE);
        mol.addBond(b29);
        IBond b30 = builder.newInstance(IBond.class, a26, a27, IBond.Order.SINGLE);
        mol.addBond(b30);
        IBond b31 = builder.newInstance(IBond.class, a27, a28, IBond.Order.SINGLE);
        mol.addBond(b31);
        IBond b32 = builder.newInstance(IBond.class, a28, a29, IBond.Order.SINGLE);
        mol.addBond(b32);
        IBond b33 = builder.newInstance(IBond.class, a3, a29, IBond.Order.SINGLE);
        mol.addBond(b33);
        IBond b34 = builder.newInstance(IBond.class, a27, a30, IBond.Order.SINGLE);
        mol.addBond(b34);
        IBond b35 = builder.newInstance(IBond.class, a30, a31, IBond.Order.SINGLE);
        mol.addBond(b35);
        IBond b36 = builder.newInstance(IBond.class, a3, a31, IBond.Order.SINGLE);
        mol.addBond(b36);
        IBond b37 = builder.newInstance(IBond.class, a27, a32, IBond.Order.SINGLE);
        mol.addBond(b37);
        IBond b38 = builder.newInstance(IBond.class, a32, a33, IBond.Order.SINGLE);
        mol.addBond(b38);
        IBond b39 = builder.newInstance(IBond.class, a24, a33, IBond.Order.SINGLE);
        mol.addBond(b39);
        IBond b40 = builder.newInstance(IBond.class, a24, a34, IBond.Order.SINGLE);
        mol.addBond(b40);
        IBond b41 = builder.newInstance(IBond.class, a34, a35, IBond.Order.SINGLE);
        mol.addBond(b41);
        IBond b42 = builder.newInstance(IBond.class, a19, a35, IBond.Order.SINGLE);
        mol.addBond(b42);
        return mol;
    }

    /**
     * octacyclo[24.2.2.2²,⁵.2⁶,⁹.2¹⁰,¹³.2¹⁴,¹⁷.2¹⁸,²¹.2²²,²⁵]dotetracontane
     * @cdk.inchi InChI=1/C42H70/c1-2-30-4-3-29(1)31-5-7-33(8-6-31)35-13-15-37(16-14-35)39-21-23-41(24-22-39)42-27-25-40(26-28-42)38-19-17-36(18-20-38)34-11-9-32(30)10-12-34/h29-42H,1-28H2
     */
    public static IAtomContainer makeGappedCyclophaneLike() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        a8.setFormalCharge(0);
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        a9.setFormalCharge(0);
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        a10.setFormalCharge(0);
        mol.addAtom(a10);
        IAtom a11 = builder.newInstance(IAtom.class, "C");
        a11.setFormalCharge(0);
        mol.addAtom(a11);
        IAtom a12 = builder.newInstance(IAtom.class, "C");
        a12.setFormalCharge(0);
        mol.addAtom(a12);
        IAtom a13 = builder.newInstance(IAtom.class, "C");
        a13.setFormalCharge(0);
        mol.addAtom(a13);
        IAtom a14 = builder.newInstance(IAtom.class, "C");
        a14.setFormalCharge(0);
        mol.addAtom(a14);
        IAtom a15 = builder.newInstance(IAtom.class, "C");
        a15.setFormalCharge(0);
        mol.addAtom(a15);
        IAtom a16 = builder.newInstance(IAtom.class, "C");
        a16.setFormalCharge(0);
        mol.addAtom(a16);
        IAtom a17 = builder.newInstance(IAtom.class, "C");
        a17.setFormalCharge(0);
        mol.addAtom(a17);
        IAtom a18 = builder.newInstance(IAtom.class, "C");
        a18.setFormalCharge(0);
        mol.addAtom(a18);
        IAtom a19 = builder.newInstance(IAtom.class, "C");
        a19.setFormalCharge(0);
        mol.addAtom(a19);
        IAtom a20 = builder.newInstance(IAtom.class, "C");
        a20.setFormalCharge(0);
        mol.addAtom(a20);
        IAtom a21 = builder.newInstance(IAtom.class, "C");
        a21.setFormalCharge(0);
        mol.addAtom(a21);
        IAtom a22 = builder.newInstance(IAtom.class, "C");
        a22.setFormalCharge(0);
        mol.addAtom(a22);
        IAtom a23 = builder.newInstance(IAtom.class, "C");
        a23.setFormalCharge(0);
        mol.addAtom(a23);
        IAtom a24 = builder.newInstance(IAtom.class, "C");
        a24.setFormalCharge(0);
        mol.addAtom(a24);
        IAtom a25 = builder.newInstance(IAtom.class, "C");
        a25.setFormalCharge(0);
        mol.addAtom(a25);
        IAtom a26 = builder.newInstance(IAtom.class, "C");
        a26.setFormalCharge(0);
        mol.addAtom(a26);
        IAtom a27 = builder.newInstance(IAtom.class, "C");
        a27.setFormalCharge(0);
        mol.addAtom(a27);
        IAtom a28 = builder.newInstance(IAtom.class, "C");
        a28.setFormalCharge(0);
        mol.addAtom(a28);
        IAtom a29 = builder.newInstance(IAtom.class, "C");
        a29.setFormalCharge(0);
        mol.addAtom(a29);
        IAtom a30 = builder.newInstance(IAtom.class, "C");
        a30.setFormalCharge(0);
        mol.addAtom(a30);
        IAtom a31 = builder.newInstance(IAtom.class, "C");
        a31.setFormalCharge(0);
        mol.addAtom(a31);
        IAtom a32 = builder.newInstance(IAtom.class, "C");
        a32.setFormalCharge(0);
        mol.addAtom(a32);
        IAtom a33 = builder.newInstance(IAtom.class, "C");
        a33.setFormalCharge(0);
        mol.addAtom(a33);
        IAtom a34 = builder.newInstance(IAtom.class, "C");
        a34.setFormalCharge(0);
        mol.addAtom(a34);
        IAtom a35 = builder.newInstance(IAtom.class, "C");
        a35.setFormalCharge(0);
        mol.addAtom(a35);
        IAtom a36 = builder.newInstance(IAtom.class, "C");
        a36.setFormalCharge(0);
        mol.addAtom(a36);
        IAtom a37 = builder.newInstance(IAtom.class, "C");
        a37.setFormalCharge(0);
        mol.addAtom(a37);
        IAtom a38 = builder.newInstance(IAtom.class, "C");
        a38.setFormalCharge(0);
        mol.addAtom(a38);
        IAtom a39 = builder.newInstance(IAtom.class, "C");
        a39.setFormalCharge(0);
        mol.addAtom(a39);
        IAtom a40 = builder.newInstance(IAtom.class, "C");
        a40.setFormalCharge(0);
        mol.addAtom(a40);
        IAtom a41 = builder.newInstance(IAtom.class, "C");
        a41.setFormalCharge(0);
        mol.addAtom(a41);
        IAtom a42 = builder.newInstance(IAtom.class, "C");
        a42.setFormalCharge(0);
        mol.addAtom(a42);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a4, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a5, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a6, a7, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a7, a8, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a8, a9, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a9, a10, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a10, a11, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = builder.newInstance(IBond.class, a11, a12, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = builder.newInstance(IBond.class, a7, a12, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = builder.newInstance(IBond.class, a10, a13, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = builder.newInstance(IBond.class, a13, a14, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = builder.newInstance(IBond.class, a14, a15, IBond.Order.SINGLE);
        mol.addBond(b16);
        IBond b17 = builder.newInstance(IBond.class, a15, a16, IBond.Order.SINGLE);
        mol.addBond(b17);
        IBond b18 = builder.newInstance(IBond.class, a16, a17, IBond.Order.SINGLE);
        mol.addBond(b18);
        IBond b19 = builder.newInstance(IBond.class, a17, a18, IBond.Order.SINGLE);
        mol.addBond(b19);
        IBond b20 = builder.newInstance(IBond.class, a13, a18, IBond.Order.SINGLE);
        mol.addBond(b20);
        IBond b21 = builder.newInstance(IBond.class, a16, a19, IBond.Order.SINGLE);
        mol.addBond(b21);
        IBond b22 = builder.newInstance(IBond.class, a19, a20, IBond.Order.SINGLE);
        mol.addBond(b22);
        IBond b23 = builder.newInstance(IBond.class, a20, a21, IBond.Order.SINGLE);
        mol.addBond(b23);
        IBond b24 = builder.newInstance(IBond.class, a21, a22, IBond.Order.SINGLE);
        mol.addBond(b24);
        IBond b25 = builder.newInstance(IBond.class, a22, a23, IBond.Order.SINGLE);
        mol.addBond(b25);
        IBond b26 = builder.newInstance(IBond.class, a23, a24, IBond.Order.SINGLE);
        mol.addBond(b26);
        IBond b27 = builder.newInstance(IBond.class, a19, a24, IBond.Order.SINGLE);
        mol.addBond(b27);
        IBond b28 = builder.newInstance(IBond.class, a22, a25, IBond.Order.SINGLE);
        mol.addBond(b28);
        IBond b29 = builder.newInstance(IBond.class, a25, a26, IBond.Order.SINGLE);
        mol.addBond(b29);
        IBond b30 = builder.newInstance(IBond.class, a26, a27, IBond.Order.SINGLE);
        mol.addBond(b30);
        IBond b31 = builder.newInstance(IBond.class, a27, a28, IBond.Order.SINGLE);
        mol.addBond(b31);
        IBond b32 = builder.newInstance(IBond.class, a28, a29, IBond.Order.SINGLE);
        mol.addBond(b32);
        IBond b33 = builder.newInstance(IBond.class, a29, a30, IBond.Order.SINGLE);
        mol.addBond(b33);
        IBond b34 = builder.newInstance(IBond.class, a25, a30, IBond.Order.SINGLE);
        mol.addBond(b34);
        IBond b35 = builder.newInstance(IBond.class, a28, a31, IBond.Order.SINGLE);
        mol.addBond(b35);
        IBond b36 = builder.newInstance(IBond.class, a31, a32, IBond.Order.SINGLE);
        mol.addBond(b36);
        IBond b37 = builder.newInstance(IBond.class, a32, a33, IBond.Order.SINGLE);
        mol.addBond(b37);
        IBond b38 = builder.newInstance(IBond.class, a33, a34, IBond.Order.SINGLE);
        mol.addBond(b38);
        IBond b39 = builder.newInstance(IBond.class, a34, a35, IBond.Order.SINGLE);
        mol.addBond(b39);
        IBond b40 = builder.newInstance(IBond.class, a35, a36, IBond.Order.SINGLE);
        mol.addBond(b40);
        IBond b41 = builder.newInstance(IBond.class, a31, a36, IBond.Order.SINGLE);
        mol.addBond(b41);
        IBond b42 = builder.newInstance(IBond.class, a34, a37, IBond.Order.SINGLE);
        mol.addBond(b42);
        IBond b43 = builder.newInstance(IBond.class, a37, a38, IBond.Order.SINGLE);
        mol.addBond(b43);
        IBond b44 = builder.newInstance(IBond.class, a38, a39, IBond.Order.SINGLE);
        mol.addBond(b44);
        IBond b45 = builder.newInstance(IBond.class, a39, a40, IBond.Order.SINGLE);
        mol.addBond(b45);
        IBond b46 = builder.newInstance(IBond.class, a3, a40, IBond.Order.SINGLE);
        mol.addBond(b46);
        IBond b47 = builder.newInstance(IBond.class, a40, a41, IBond.Order.SINGLE);
        mol.addBond(b47);
        IBond b48 = builder.newInstance(IBond.class, a41, a42, IBond.Order.SINGLE);
        mol.addBond(b48);
        IBond b49 = builder.newInstance(IBond.class, a37, a42, IBond.Order.SINGLE);
        mol.addBond(b49);
        return mol;
    }

    private static void configureAtoms(IAtomContainer mol) {
        try {
            for (IAtom atom : mol.atoms())
                atom.setImplicitHydrogenCount(null);
            Isotopes.getInstance().configureAtoms(mol);
        } catch (Exception exc) {
            logger.error("Could not configure molecule!");
            logger.debug(exc);
        }
    }

}
