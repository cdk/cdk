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
package org.openscience.cdk.templates;

import java.io.FileInputStream;
import java.io.IOException;

import javax.vecmath.Point2d;

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * This class contains methods for generating simple organic molecules.
 *
 * @cdk.keyword templates
 * @cdk.githash
 */
public class MoleculeFactory {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(MoleculeFactory.class);

    public static IAtomContainer makeAlphaPinene(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 10

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
     *
     * @cdk.created 2003-08-15
     */
    public static IAtomContainer makeAlkane(IChemObjectBuilder builder, int chainLength) {
        IAtomContainer currentChain = builder.newInstance(IAtomContainer.class);

        //Add the initial atom
        currentChain.addAtom(builder.newInstance(IAtom.class, "C"));

        //Add further atoms and bonds as needed, a pair at a time.
        for (int atomCount = 1; atomCount < chainLength; atomCount++) {
            currentChain.addAtom(builder.newInstance(IAtom.class, "C"));
            currentChain.addBond(atomCount, atomCount - 1, IBond.Order.SINGLE);
        }

        return currentChain;
    }

    public static IAtomContainer makeEthylCyclohexane(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8

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
    public static IAtomContainer makeCyclohexene(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6

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
    public static IAtomContainer makeCyclohexane(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6

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
    public static IAtomContainer makeCyclopentane(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5

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
    public static IAtomContainer makeCyclobutane(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer makeCyclobutadiene(IChemObjectBuilder builder) {
    	IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 0, IBond.Order.DOUBLE); // 4
        return mol;
    }

    public static IAtomContainer makePropylCycloPropane(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
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
    public static IAtomContainer makeBiphenyl(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 10
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 11

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

    public static IAtomContainer makePhenylEthylBenzene(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 10
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 11
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 12
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 13

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

    public static IAtomContainer makePhenylAmine(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 6

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
    public static IAtomContainer make4x3CondensedRings(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6

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

    public static IAtomContainer makeSpiroRings(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9

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

    public static IAtomContainer makeBicycloRings(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7

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

    public static IAtomContainer makeFusedRings(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9

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

    public static IAtomContainer makeMethylDecaline(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 10

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

    public static IAtomContainer makeEthylPropylPhenantren(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 10
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 11
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 12
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 13
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 14
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 15
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 16
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 17
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 18

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

    public static IAtomContainer makeSteran(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 10
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 11
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 12
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 13
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 14
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 15
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 16

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
    public static IAtomContainer makeAzulene(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9

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
    public static IAtomContainer makeIndole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 8

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
    public static IAtomContainer makePyrrole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer makePyrroleAnion(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom nitrogenAnion = builder.newInstance(IAtom.class, "N");
        nitrogenAnion.setFormalCharge(-1);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(nitrogenAnion); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer makeImidazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer makePyrazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer make124Triazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 4

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
    public static IAtomContainer make123Triazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer makeTetrazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer makeOxazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "O")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer makeIsoxazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "O")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer makeIsothiazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "S")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

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
    public static IAtomContainer makeThiadiazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "S")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 4

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
    public static IAtomContainer makeOxadiazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "O")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 4

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
    public static IAtomContainer makePyridine(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5

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
    public static IAtomContainer makePyridineOxide(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.getAtom(1).setFormalCharge(1);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "O")); // 6
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
    public static IAtomContainer makePyrimidine(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5

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
    public static IAtomContainer makePyridazine(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5

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
    public static IAtomContainer makeTriazine(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 5

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
    public static IAtomContainer makeThiazole(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "N")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "S")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 0, IBond.Order.DOUBLE); // 5

        return mol;
    }

    public static IAtomContainer makeSingleRing(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        //		mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        //		mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        //		mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        //		mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9

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

    public static IAtomContainer makeDiamantane(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 10
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 11
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 12
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 13

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

    public static IAtomContainer makeBranchedAliphatic(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 7
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 8
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 9
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 10
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 11
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 12
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 13
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 14
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 15
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 16
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 17
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 18

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

    public static IAtomContainer makeBenzene(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.DOUBLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.DOUBLE); // 6
        return mol;
    }

    public static IAtomContainer makeQuinone(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "O")); // 0
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 1
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 2
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 3
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 4
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 5
        mol.addAtom(builder.newInstance(IAtom.class, "C")); // 6
        mol.addAtom(builder.newInstance(IAtom.class, "O")); // 7

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

    public static IAtomContainer makePiperidine(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "N"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "H"));

        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(5, 0, IBond.Order.SINGLE);

        mol.addBond(0, 6, IBond.Order.SINGLE);

        return mol;

    }

    public static IAtomContainer makeTetrahydropyran(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "O"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));

        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(5, 0, IBond.Order.SINGLE);

        return mol;

    }

    public static IAtomContainer loadMolecule(IChemObjectBuilder builder, String inFile) {
        MDLReader mr = null;
        IChemFile chemFile = null;
        IChemSequence chemSequence = null;
        IChemModel chemModel = null;
        IAtomContainerSet setOfMolecules = null;
        IAtomContainer molecule = null;
        try {
            FileInputStream fis = new FileInputStream(inFile);
            mr = new MDLReader(fis);
            chemFile = mr.read(builder.newInstance(IChemFile.class));
            mr.close();
            chemSequence = chemFile.getChemSequence(0);
            chemModel = chemSequence.getChemModel(0);
            setOfMolecules = chemModel.getMoleculeSet();
            molecule = setOfMolecules.getAtomContainer(0);
            for (int i = 0; i < molecule.getAtomCount(); i++) {
                molecule.getAtom(i).setPoint2d(null);
            }
        } catch (CDKException | IOException exc) {
            // we just return null if something went wrong
            logger.error("An exception occured while loading a molecule: " + inFile);
            logger.debug(exc);
        }

        return molecule;
    }

    /**
     * @cdk.inchi InChI=1/C5H5N5/c6-4-3-5(9-1-7-3)10-2-8-4/h1-2H,(H3,6,7,8,9,10)/f/h7H,6H2
     */
    public static IAtomContainer makeAdenine(IChemObjectBuilder builder) {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class); // Adenine
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

    private static void configureAtoms(IAtomContainer mol) {
        try {
            Isotopes.getInstance().configureAtoms(mol);
        } catch (Exception exc) {
            logger.error("Could not configure molecule!");
            logger.debug(exc);
        }
    }

}
