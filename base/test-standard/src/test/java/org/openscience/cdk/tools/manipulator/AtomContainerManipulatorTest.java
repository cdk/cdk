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
package org.openscience.cdk.tools.manipulator;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.stereo.Octahedral;
import org.openscience.cdk.stereo.SquarePlanar;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.silent.PseudoAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.openscience.cdk.tools.manipulator.AtomContainerManipulator.*;

/**
 */
class AtomContainerManipulatorTest extends CDKTestCase {

    private IAtomContainer ac;

    @BeforeEach
    void setUp() {
        ac = TestMoleculeFactory.makeAlphaPinene();
    }

    @Test
    void testExtractSubstructure() throws CloneNotSupportedException {
        IAtomContainer source = TestMoleculeFactory.makeEthylCyclohexane();
        IAtomContainer ringSubstructure = AtomContainerManipulator.extractSubstructure(source, 0, 1, 2, 3, 4, 5);
        assertThat(ringSubstructure.getAtomCount(), is(6));
        assertThat(ringSubstructure.getBondCount(), is(6));
    }

    @Test
    void testCopy_completeCopy_sourceAtomContainer2_destinationAtomContainer() {
        // arrange
        IAtomContainer atomContainerSource = SilentChemObjectBuilder.getInstance().newAtomContainer();
        // CC(=O)CC(=O)O
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addBond(0, 1, Order.SINGLE);
        atomContainerSource.addBond(1, 2, Order.DOUBLE);
        atomContainerSource.addBond(1, 3, Order.SINGLE);
        atomContainerSource.addBond(3, 4, Order.SINGLE);
        atomContainerSource.addBond(4, 5, Order.DOUBLE);
        atomContainerSource.addBond(4, 6, Order.SINGLE);
        assertThat(atomContainerSource.getAtomCount(), is(7));
        assertThat(atomContainerSource.getBondCount(), is(6));
        IAtomContainer atomContainerDestination = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        // act
        AtomContainerManipulator.copy(atomContainerDestination, atomContainerSource, x -> true, x -> true);

        // assert
        assertThat(atomContainerDestination.getAtomCount(), is(7));
        assertThat(atomContainerDestination.getBondCount(), is(6));
        assertThat(atomContainerDestination.getAtom(0).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(1).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(2).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(3).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(4).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(5).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(6).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(1)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(2)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(3)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(3), atomContainerDestination.getAtom(4)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(5)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(6)).getOrder(), is(Order.SINGLE));
    }

    @Test
    void testCopy_completeCopy_sourceAtomContainer2_destinationAtomContainer2() {
        // arrange
        IAtomContainer atomContainerSource = SilentChemObjectBuilder.getInstance().newAtomContainer();
        // CC(=O)CC(=O)O
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addBond(0, 1, Order.SINGLE);
        atomContainerSource.addBond(1, 2, Order.DOUBLE);
        atomContainerSource.addBond(1, 3, Order.SINGLE);
        atomContainerSource.addBond(3, 4, Order.SINGLE);
        atomContainerSource.addBond(4, 5, Order.DOUBLE);
        atomContainerSource.addBond(4, 6, Order.SINGLE);
        assertThat(atomContainerSource.getAtomCount(), is(7));
        assertThat(atomContainerSource.getBondCount(), is(6));
        IAtomContainer atomContainerDestination = SilentChemObjectBuilder.getInstance().newAtomContainer();

        // act
        AtomContainerManipulator.copy(atomContainerDestination, atomContainerSource, x -> true, x -> true);

        // assert
        assertThat(atomContainerDestination.getAtomCount(), is(7));
        assertThat(atomContainerDestination.getBondCount(), is(6));
        assertThat(atomContainerDestination.getAtom(0).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(1).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(2).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(3).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(4).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(5).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(6).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(1)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(2)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(3)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(3), atomContainerDestination.getAtom(4)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(5)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(6)).getOrder(), is(Order.SINGLE));
    }

    @Test
    void testCopy_completeCopy_sourceAtomContainer_destinationAtomContainer2() {
        // arrange
        IAtomContainer atomContainerSource = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        // CC(=O)CC(=O)O
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addBond(0, 1, Order.SINGLE);
        atomContainerSource.addBond(1, 2, Order.DOUBLE);
        atomContainerSource.addBond(1, 3, Order.SINGLE);
        atomContainerSource.addBond(3, 4, Order.SINGLE);
        atomContainerSource.addBond(4, 5, Order.DOUBLE);
        atomContainerSource.addBond(4, 6, Order.SINGLE);
        assertThat(atomContainerSource.getAtomCount(), is(7));
        assertThat(atomContainerSource.getBondCount(), is(6));
        IAtomContainer atomContainerDestination = SilentChemObjectBuilder.getInstance().newAtomContainer();

        // act
        AtomContainerManipulator.copy(atomContainerDestination, atomContainerSource, x -> true, x -> true);

        // assert
        assertThat(atomContainerDestination.getAtomCount(), is(7));
        assertThat(atomContainerDestination.getBondCount(), is(6));
        assertThat(atomContainerDestination.getAtom(0).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(1).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(2).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(3).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(4).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(5).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(6).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(1)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(2)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(3)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(3), atomContainerDestination.getAtom(4)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(5)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(6)).getOrder(), is(Order.SINGLE));
    }

    @Test
    void testCopy_completeCopy_sourceAtomContainer_destinationAtomContainer() {
        // arrange
        IAtomContainer atomContainerSource = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        // CC(=O)CC(=O)O
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("C"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addAtom(new Atom("O"));
        atomContainerSource.addBond(0, 1, Order.SINGLE);
        atomContainerSource.addBond(1, 2, Order.DOUBLE);
        atomContainerSource.addBond(1, 3, Order.SINGLE);
        atomContainerSource.addBond(3, 4, Order.SINGLE);
        atomContainerSource.addBond(4, 5, Order.DOUBLE);
        atomContainerSource.addBond(4, 6, Order.SINGLE);
        assertThat(atomContainerSource.getAtomCount(), is(7));
        assertThat(atomContainerSource.getBondCount(), is(6));
        IAtomContainer atomContainerDestination = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        // act
        AtomContainerManipulator.copy(atomContainerDestination, atomContainerSource, x -> true, x -> true);

        // assert
        assertThat(atomContainerDestination.getAtomCount(), is(7));
        assertThat(atomContainerDestination.getBondCount(), is(6));
        assertThat(atomContainerDestination.getAtom(0).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(1).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(2).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(3).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(4).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(5).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(6).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(1)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(2)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(3)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(3), atomContainerDestination.getAtom(4)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(5)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(6)).getOrder(), is(Order.SINGLE));
    }

    @Test
    void testCopy_partialDisconnectedCopy_atomPredicate() {
        // arrange
        IAtomContainer atomContainerSource = SilentChemObjectBuilder.getInstance().newAtomContainer();
        // CC(=O)C=CC(=O)N
        atomContainerSource.addAtom(new Atom("C")); // #0
        atomContainerSource.addAtom(new Atom("C")); // #1
        atomContainerSource.addAtom(new Atom("O")); // #2
        atomContainerSource.addAtom(new Atom("C")); // #3
        atomContainerSource.addAtom(new Atom("C")); // #4
        atomContainerSource.addAtom(new Atom("C")); // #5
        atomContainerSource.addAtom(new Atom("O")); // #6
        atomContainerSource.addAtom(new Atom("N")); // #7
        atomContainerSource.addBond(0, 1, Order.SINGLE); // #0
        atomContainerSource.addBond(1, 2, Order.DOUBLE); // #1
        atomContainerSource.addBond(1, 3, Order.SINGLE); // #2
        atomContainerSource.addBond(3, 4, Order.DOUBLE); // #3
        atomContainerSource.addBond(4, 5, Order.SINGLE); // #4
        atomContainerSource.addBond(5, 6, Order.DOUBLE); // #5
        atomContainerSource.addBond(5, 7, Order.SINGLE); // #6
        assertThat(atomContainerSource.getAtomCount(), is(8));
        assertThat(atomContainerSource.getBondCount(), is(7));
        IAtomContainer atomContainerDestination = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Set<IAtom> atomsToInclude = IntStream.of(1,2,3,5,7).mapToObj(atomContainerSource::getAtom).collect(Collectors.toSet());

        // act
        AtomContainerManipulator.copy(atomContainerDestination, atomContainerSource, atomsToInclude::contains);

        // assert
        assertThat(atomContainerDestination.getAtomCount(), is(5));
        assertThat(atomContainerDestination.getBondCount(), is(3));
        assertThat(atomContainerDestination.getAtom(0).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(1).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(2).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(3).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(4).getAtomicNumber(), is(7));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(1)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(2)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(3), atomContainerDestination.getAtom(4)).getOrder(), is(Order.SINGLE));
    }

    @Test
    void testCopy_partialDisconnectedCopy_atomCollection() {
        // arrange
        IAtomContainer atomContainerSource = SilentChemObjectBuilder.getInstance().newAtomContainer();
        // CC(=O)C=CC(=O)N
        atomContainerSource.addAtom(new Atom("C")); // #0
        atomContainerSource.addAtom(new Atom("C")); // #1
        atomContainerSource.addAtom(new Atom("O")); // #2
        atomContainerSource.addAtom(new Atom("C")); // #3
        atomContainerSource.addAtom(new Atom("C")); // #4
        atomContainerSource.addAtom(new Atom("C")); // #5
        atomContainerSource.addAtom(new Atom("O")); // #6
        atomContainerSource.addAtom(new Atom("N")); // #7
        atomContainerSource.addBond(0, 1, Order.SINGLE); // #0
        atomContainerSource.addBond(1, 2, Order.DOUBLE); // #1
        atomContainerSource.addBond(1, 3, Order.SINGLE); // #2
        atomContainerSource.addBond(3, 4, Order.DOUBLE); // #3
        atomContainerSource.addBond(4, 5, Order.SINGLE); // #4
        atomContainerSource.addBond(5, 6, Order.DOUBLE); // #5
        atomContainerSource.addBond(5, 7, Order.SINGLE); // #6
        assertThat(atomContainerSource.getAtomCount(), is(8));
        assertThat(atomContainerSource.getBondCount(), is(7));
        IAtomContainer atomContainerDestination = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Set<IAtom> atomsToInclude = IntStream.of(1,2,3,5,7).mapToObj(atomContainerSource::getAtom).collect(Collectors.toSet());

        // act
        AtomContainerManipulator.copy(atomContainerDestination, atomContainerSource, atomsToInclude);

        // assert
        assertThat(atomContainerDestination.getAtomCount(), is(5));
        assertThat(atomContainerDestination.getBondCount(), is(3));
        assertThat(atomContainerDestination.getAtom(0).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(1).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(2).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(3).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(4).getAtomicNumber(), is(7));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(1)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(2)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(3), atomContainerDestination.getAtom(4)).getOrder(), is(Order.SINGLE));
    }

    @Test
    void testExtractSubstructure_partialDisconnectedCopy() {
        // arrange
        IAtomContainer atomContainerSource = SilentChemObjectBuilder.getInstance().newAtomContainer();
        // CC(=O)C=CC(=O)N
        atomContainerSource.addAtom(new Atom("C")); // #0
        atomContainerSource.addAtom(new Atom("C")); // #1
        atomContainerSource.addAtom(new Atom("O")); // #2
        atomContainerSource.addAtom(new Atom("C")); // #3
        atomContainerSource.addAtom(new Atom("C")); // #4
        atomContainerSource.addAtom(new Atom("C")); // #5
        atomContainerSource.addAtom(new Atom("O")); // #6
        atomContainerSource.addAtom(new Atom("N")); // #7
        atomContainerSource.addBond(0, 1, Order.SINGLE); // #0
        atomContainerSource.addBond(1, 2, Order.DOUBLE); // #1
        atomContainerSource.addBond(1, 3, Order.SINGLE); // #2
        atomContainerSource.addBond(3, 4, Order.DOUBLE); // #3
        atomContainerSource.addBond(4, 5, Order.SINGLE); // #4
        atomContainerSource.addBond(5, 6, Order.DOUBLE); // #5
        atomContainerSource.addBond(5, 7, Order.SINGLE); // #6
        assertThat(atomContainerSource.getAtomCount(), is(8));
        assertThat(atomContainerSource.getBondCount(), is(7));
        Set<IAtom> atomsToInclude = IntStream.of(1,2,3,5,7).mapToObj(atomContainerSource::getAtom).collect(Collectors.toSet());

        // act
        IAtomContainer atomContainerDestination = AtomContainerManipulator.extractSubstructure(atomContainerSource, atomsToInclude);

        // assert
        assertThat(atomContainerDestination.getAtomCount(), is(5));
        assertThat(atomContainerDestination.getBondCount(), is(3));
        assertThat(atomContainerDestination.getAtom(0).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(1).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(2).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(3).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(4).getAtomicNumber(), is(7));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(1)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(0), atomContainerDestination.getAtom(2)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(3), atomContainerDestination.getAtom(4)).getOrder(), is(Order.SINGLE));
    }

    @Test
    void testCopy_partialDisconnectedCopy_bondPredicate(){
        // arrange
        IAtomContainer atomContainerSource = SilentChemObjectBuilder.getInstance().newAtomContainer();
        // CC(=O)C=CC(=O)N
        atomContainerSource.addAtom(new Atom("C")); // #0
        atomContainerSource.addAtom(new Atom("C")); // #1
        atomContainerSource.addAtom(new Atom("O")); // #2
        atomContainerSource.addAtom(new Atom("C")); // #3
        atomContainerSource.addAtom(new Atom("C")); // #4
        atomContainerSource.addAtom(new Atom("C")); // #5
        atomContainerSource.addAtom(new Atom("O")); // #6
        atomContainerSource.addAtom(new Atom("N")); // #7
        atomContainerSource.addBond(0, 1, Order.SINGLE); // #0
        atomContainerSource.addBond(1, 2, Order.DOUBLE); // #1
        atomContainerSource.addBond(1, 3, Order.SINGLE); // #2
        atomContainerSource.addBond(3, 4, Order.DOUBLE); // #3
        atomContainerSource.addBond(4, 5, Order.SINGLE); // #4
        atomContainerSource.addBond(5, 6, Order.DOUBLE); // #5
        atomContainerSource.addBond(5, 7, Order.SINGLE); // #6
        assertThat(atomContainerSource.getAtomCount(), is(8));
        assertThat(atomContainerSource.getBondCount(), is(7));
        IAtomContainer atomContainerDestination = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Set<IBond> bondsToInclude = IntStream.of(1,4,5,6).mapToObj(atomContainerSource::getBond).collect(Collectors.toSet());

        // act
        AtomContainerManipulator.copy(atomContainerDestination, atomContainerSource, x -> true, bondsToInclude::contains);

        // assert
        assertThat(atomContainerDestination.getAtomCount(), is(8));
        assertThat(atomContainerDestination.getBondCount(), is(4));
        assertThat(atomContainerDestination.getAtom(0).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(1).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(2).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(3).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(4).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(5).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(6).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(7).getAtomicNumber(), is(7));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(2)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(5)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(5), atomContainerDestination.getAtom(6)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(5), atomContainerDestination.getAtom(7)).getOrder(), is(Order.SINGLE));
    }

    @Test
    void testCopy_partialDisconnectedCopy_atomPredicate_bondPredicate(){
        // arrange
        IAtomContainer atomContainerSource = SilentChemObjectBuilder.getInstance().newAtomContainer();
        // CC(=O)C=CC(=O)N
        atomContainerSource.addAtom(new Atom("C")); // #0
        atomContainerSource.addAtom(new Atom("C")); // #1
        atomContainerSource.addAtom(new Atom("O")); // #2
        atomContainerSource.addAtom(new Atom("C")); // #3
        atomContainerSource.addAtom(new Atom("C")); // #4
        atomContainerSource.addAtom(new Atom("C")); // #5
        atomContainerSource.addAtom(new Atom("O")); // #6
        atomContainerSource.addAtom(new Atom("N")); // #7
        atomContainerSource.addBond(0, 1, Order.SINGLE); // #0
        atomContainerSource.addBond(1, 2, Order.DOUBLE); // #1
        atomContainerSource.addBond(1, 3, Order.SINGLE); // #2
        atomContainerSource.addBond(3, 4, Order.DOUBLE); // #3
        atomContainerSource.addBond(4, 5, Order.SINGLE); // #4
        atomContainerSource.addBond(5, 6, Order.DOUBLE); // #5
        atomContainerSource.addBond(5, 7, Order.SINGLE); // #6
        assertThat(atomContainerSource.getAtomCount(), is(8));
        assertThat(atomContainerSource.getBondCount(), is(7));
        IAtomContainer atomContainerDestination = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Set<IAtom> atomsToInclude = IntStream.of(0,1,2,4,5,7).mapToObj(atomContainerSource::getAtom).collect(Collectors.toSet());
        Set<IBond> bondsToInclude = IntStream.of(1,4,5,6).mapToObj(atomContainerSource::getBond).collect(Collectors.toSet());

        // act
        AtomContainerManipulator.copy(atomContainerDestination, atomContainerSource, atomsToInclude::contains, bondsToInclude::contains);

        // assert
        assertThat(atomContainerDestination.getAtomCount(), is(6));
        assertThat(atomContainerDestination.getBondCount(), is(3));
        assertThat(atomContainerDestination.getAtom(0).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(1).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(2).getAtomicNumber(), is(8));
        assertThat(atomContainerDestination.getAtom(3).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(4).getAtomicNumber(), is(6));
        assertThat(atomContainerDestination.getAtom(5).getAtomicNumber(), is(7));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(1), atomContainerDestination.getAtom(2)).getOrder(), is(Order.DOUBLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(3), atomContainerDestination.getAtom(4)).getOrder(), is(Order.SINGLE));
        assertThat(atomContainerDestination.getBond(atomContainerDestination.getAtom(4), atomContainerDestination.getAtom(5)).getOrder(), is(Order.SINGLE));
    }

    /**
     * @cdk.bug 1254
     */
    @Test
    void testGetTotalHydrogenCount_IAtomContainer() throws IOException, ClassNotFoundException, CDKException {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(5, mol.getBondCount());
        // total includes explicit and implicit (we don't have any implicit to 4 is expected)
        Assertions.assertEquals(4, getTotalHydrogenCount(mol));
    }

    @Test
    void testConvertImplicitToExplicitHydrogens_IAtomContainer() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(2);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setImplicitHydrogenCount(2);
        mol.addBond(0, 1, Order.DOUBLE);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(5, mol.getBondCount());
    }

    @Test
    void testConvertImplicitToExplicitHydrogens_IAtomContainer2() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethane
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.getAtom(1).setImplicitHydrogenCount(3);
        mol.addBond(0, 1, Order.SINGLE);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Assertions.assertEquals(8, mol.getAtomCount());
        Assertions.assertEquals(7, mol.getBondCount());
    }

    @Test
    void testGetTotalHydrogenCount_IAtomContainer_zeroImplicit() throws IOException, ClassNotFoundException,
            CDKException {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setImplicitHydrogenCount(0);
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(5, mol.getBondCount());

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(5, mol.getBondCount());
    }

    @Test
    void testGetTotalHydrogenCount_IAtomContainer_nullImplicit() throws IOException, ClassNotFoundException,
            CDKException {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(null);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setImplicitHydrogenCount(null);
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(5, mol.getBondCount());

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(5, mol.getBondCount());
    }

    @Test
    void testGetTotalHydrogenCount_ImplicitHydrogens() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom carbon = new Atom("C");
        carbon.setImplicitHydrogenCount(4);
        mol.addAtom(carbon);
        Assertions.assertEquals(4, getTotalHydrogenCount(mol));
    }

    @Test
    void testRemoveHydrogens_IAtomContainer() throws IOException, ClassNotFoundException, CDKException {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);
        for (IAtom atom : mol.atoms())
            atom.setImplicitHydrogenCount(0);
        mol.setFlag(IChemObject.AROMATIC, true);

        Assertions.assertEquals(6, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assertions.assertEquals(2, ac.getAtomCount());
        Assertions.assertTrue(ac.getFlag(IChemObject.AROMATIC));
    }

    @Test
    void dontSuppressHydrogensOnPseudoAtoms() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // *[H]
        mol.addAtom(new PseudoAtom("*"));
        mol.addAtom(new Atom("H"));
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        mol.addBond(0, 1, Order.SINGLE);
        Assertions.assertEquals(2, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assertions.assertEquals(2, ac.getAtomCount());
    }

    @Test
    void suppressHydrogensKeepsRadicals() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // *[H]
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        mol.getAtom(2).setImplicitHydrogenCount(1);
        mol.getAtom(3).setImplicitHydrogenCount(1);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);
        mol.addBond(0, 3, Order.SINGLE);
        mol.addSingleElectron(0);
        Assertions.assertEquals(4, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getSingleElectronCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assertions.assertEquals(1, ac.getAtomCount());
        Assertions.assertEquals(1, ac.getSingleElectronCount());
    }

    private IAtomContainer getChiralMolTemplate() {
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("Cl"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("Br"));
        molecule.addAtom(new Atom("H"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("H"));
        molecule.addAtom(new Atom("H"));
        molecule.addAtom(new Atom("Cl"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        molecule.addBond(1, 4, IBond.Order.SINGLE);
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addBond(4, 6, IBond.Order.SINGLE);
        molecule.addBond(4, 7, IBond.Order.SINGLE);

        return molecule;
    }

    @Test
    void testRemoveNonChiralHydrogens_StereoElement() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        IAtom[] ligands = new IAtom[]{molecule.getAtom(4), molecule.getAtom(3), molecule.getAtom(2),
                molecule.getAtom(0)};

        TetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1), ligands,
                ITetrahedralChirality.Stereo.CLOCKWISE);
        molecule.addStereoElement(chirality);

        Assertions.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assertions.assertEquals(6, ac.getAtomCount());
    }

    @Test
    void testRemoveNonChiralHydrogens_StereoParity() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        molecule.getAtom(1).setStereoParity(CDKConstants.STEREO_ATOM_PARITY_MINUS);

        Assertions.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assertions.assertEquals(6, ac.getAtomCount());
    }

    @Test
    void testRemoveNonChiralHydrogens_StereoBond() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        molecule.getBond(2).setStereo(IBond.Stereo.UP);

        Assertions.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assertions.assertEquals(6, ac.getAtomCount());
    }

    @Test
    void testRemoveNonChiralHydrogens_StereoBondHeteroAtom() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        molecule.getBond(3).setStereo(IBond.Stereo.UP);

        Assertions.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assertions.assertEquals(6, ac.getAtomCount());
    }

    @Test
    void testRemoveNonChiralHydrogens_IAtomContainer() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();

        Assertions.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assertions.assertEquals(5, ac.getAtomCount());
    }

    @Test
    void testRemoveHydrogensZeroHydrogenCounts() throws IOException, ClassNotFoundException, CDKException {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("Br"));
        mol.addAtom(new Atom("Br"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);

        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(1).setImplicitHydrogenCount(0);
        mol.getAtom(2).setImplicitHydrogenCount(0);
        mol.getAtom(3).setImplicitHydrogenCount(0);
        mol.getAtom(4).setImplicitHydrogenCount(0);
        mol.getAtom(5).setImplicitHydrogenCount(0);

        Assertions.assertEquals(6, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assertions.assertEquals(4, ac.getAtomCount());
        Assertions.assertNotNull(ac.getAtom(0).getImplicitHydrogenCount());
        Assertions.assertNotNull(ac.getAtom(1).getImplicitHydrogenCount());
        Assertions.assertNotNull(ac.getAtom(2).getImplicitHydrogenCount());
        Assertions.assertNotNull(ac.getAtom(3).getImplicitHydrogenCount());
        Assertions.assertEquals(0, ac.getAtom(0).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(2, ac.getAtom(1).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(0, ac.getAtom(2).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(0, ac.getAtom(3).getImplicitHydrogenCount().intValue());
    }

    @Test
    void testGetAllIDs_IAtomContainer() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setID("a1");
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setID("a2");
        mol.addAtom(new Atom("H"));
        mol.getAtom(2).setID("a3");
        mol.addAtom(new Atom("H"));
        mol.getAtom(3).setID("a4");
        mol.addAtom(new Atom("H"));
        mol.getAtom(4).setID("a5");
        mol.addAtom(new Atom("H"));
        mol.getAtom(5).setID("a6");

        List<String> ids = AtomContainerManipulator.getAllIDs(mol);
        Assertions.assertEquals(6, ids.size());
        Assertions.assertTrue(ids.contains("a1"));
        Assertions.assertTrue(ids.contains("a2"));
        Assertions.assertTrue(ids.contains("a3"));
        Assertions.assertTrue(ids.contains("a4"));
        Assertions.assertTrue(ids.contains("a5"));
        Assertions.assertTrue(ids.contains("a6"));
    }

    @Test
    void testGetAtomArray_IAtomContainer() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));

        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol);
        Assertions.assertEquals(6, atoms.length);
        Assertions.assertEquals(mol.getAtom(0), atoms[0]);
        Assertions.assertEquals(mol.getAtom(1), atoms[1]);
        Assertions.assertEquals(mol.getAtom(2), atoms[2]);
        Assertions.assertEquals(mol.getAtom(3), atoms[3]);
        Assertions.assertEquals(mol.getAtom(4), atoms[4]);
        Assertions.assertEquals(mol.getAtom(5), atoms[5]);
    }

    @Test
    void testGetAtomArray_List() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(IChemObject.AROMATIC, true);

        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol.getConnectedAtomsList(mol.getAtom(0)));
        Assertions.assertEquals(3, atoms.length);
        Assertions.assertEquals(mol.getAtom(1), atoms[0]);
        Assertions.assertEquals(mol.getAtom(2), atoms[1]);
        Assertions.assertEquals(mol.getAtom(3), atoms[2]);
    }

    @Test
    void testGetBondArray_List() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(IChemObject.AROMATIC, true);

        IBond[] bonds = AtomContainerManipulator.getBondArray(mol.getConnectedBondsList(mol.getAtom(0)));
        Assertions.assertEquals(3, bonds.length);
        Assertions.assertEquals(mol.getBond(0), bonds[0]);
        Assertions.assertEquals(mol.getBond(1), bonds[1]);
        Assertions.assertEquals(mol.getBond(2), bonds[2]);
    }

    @Test
    void testGetBondArray_IAtomContainer() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(IChemObject.AROMATIC, true);

        IBond[] bonds = AtomContainerManipulator.getBondArray(mol);
        Assertions.assertEquals(5, bonds.length);
        Assertions.assertEquals(mol.getBond(0), bonds[0]);
        Assertions.assertEquals(mol.getBond(1), bonds[1]);
        Assertions.assertEquals(mol.getBond(2), bonds[2]);
        Assertions.assertEquals(mol.getBond(3), bonds[3]);
        Assertions.assertEquals(mol.getBond(4), bonds[4]);
    }

    @Test
    void testGetAtomById_IAtomContainer_String() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setID("a1");
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setID("a2");
        mol.addAtom(new Atom("H"));
        mol.getAtom(2).setID("a3");
        mol.addAtom(new Atom("H"));
        mol.getAtom(3).setID("a4");
        mol.addAtom(new Atom("H"));
        mol.getAtom(4).setID("a5");
        mol.addAtom(new Atom("H"));
        mol.getAtom(5).setID("a6");
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(IChemObject.AROMATIC, true);

        Assertions.assertEquals(mol.getAtom(0), getAtomById(mol, "a1"));
        Assertions.assertEquals(mol.getAtom(1), getAtomById(mol, "a2"));
        Assertions.assertEquals(mol.getAtom(2), getAtomById(mol, "a3"));
        Assertions.assertEquals(mol.getAtom(3), getAtomById(mol, "a4"));
        Assertions.assertEquals(mol.getAtom(4), getAtomById(mol, "a5"));
        Assertions.assertEquals(mol.getAtom(5), getAtomById(mol, "a6"));
    }

    /**
     * Test removeHydrogens for B2H6, which contains two multiply bonded H.
     * The old behaviour would removed these but now the bridged hydrogens are
     * kept.
     */
    @Test
    void testRemoveHydrogensBorane() throws Exception {
        IAtomContainer borane = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "B"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "B"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addBond(0, 2, Order.SINGLE);
        borane.addBond(1, 2, Order.SINGLE);
        borane.addBond(2, 3, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(2, 4, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(3, 5, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(4, 5, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(5, 6, Order.SINGLE);
        borane.addBond(5, 7, Order.SINGLE);
        for (IAtom atom : borane.atoms())
            atom.setImplicitHydrogenCount(0);
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(borane);

        // bridged hydrogens are now kept
        Assertions.assertEquals(4, ac.getAtomCount(), "incorrect atom count");
        Assertions.assertEquals(4, ac.getBondCount(), "incorrect bond count");
        for (IAtom atom : ac.atoms()) {
            if (atom.getAtomicNumber() == 1) continue;
            Assertions.assertEquals(2, atom.getImplicitHydrogenCount().intValue(), "incorrect hydrogen count");
        }
    }

    /**
     * Test total formal charge.
     *
     */
    @Test
    void testGetTotalFormalCharge_IAtomContainer() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalFormalCharge(mol);

        Assertions.assertEquals(1, totalCharge);
    }

    /**
     * Test total Exact Mass.
     *
     */
    @Test
    void testGetTotalExactMass_IAtomContainer() throws Exception {

        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("CCl");
        mol.getAtom(0).setExactMass(12.00);
        mol.getAtom(1).setExactMass(34.96885268);
        double totalExactMass = AtomContainerManipulator.getTotalExactMass(mol);

        Assertions.assertEquals(49.992327775, totalExactMass, 0.000001);
    }

    @Test
    void getNaturalExactMassNeedsHydrogens() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
            IAtom atom = new Atom("C");
            atom.setImplicitHydrogenCount(null);
            mol.addAtom(atom);
            AtomContainerManipulator.getNaturalExactMass(mol);
        });
    }

    @Test
    void getNaturalExactMassNeedsAtomicNumber() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
            mol.addAtom(new Atom("C"));
            mol.getAtom(0).setAtomicNumber(null);
            AtomContainerManipulator.getNaturalExactMass(mol);
        });
    }

    @Test
    void testGetNaturalExactMass_IAtomContainer() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("Cl"));

        mol.getAtom(0).setImplicitHydrogenCount(4);
        mol.getAtom(1).setImplicitHydrogenCount(1);

        double expectedMass = 0.0;
        expectedMass += Isotopes.getInstance().getNaturalMass(builder.newInstance(IElement.class, "C"));
        expectedMass += Isotopes.getInstance().getNaturalMass(builder.newInstance(IElement.class, "Cl"));
        expectedMass += 5 * Isotopes.getInstance().getNaturalMass(builder.newInstance(IElement.class, "H"));

        double totalExactMass = AtomContainerManipulator.getNaturalExactMass(mol);

        Assertions.assertEquals(expectedMass, totalExactMass, 0.000001);
    }

    /**
     * Test total natural abundance.
     *
     */
    @Test
    void testGetTotalNaturalAbundance_IAtomContainer() throws Exception {

        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("CCl");
        mol.getAtom(0).setNaturalAbundance(98.93);
        mol.getAtom(1).setNaturalAbundance(75.78);
        double totalAbudance = AtomContainerManipulator.getTotalNaturalAbundance(mol);

        Assertions.assertEquals(0.749432, totalAbudance, 0.000001);
    }

    /**
     * Test total positive formal charge.
     *
     */
    @Test
    void testGetTotalPositiveFormalCharge_IAtomContainer() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalPositiveFormalCharge(mol);

        Assertions.assertEquals(2, totalCharge);
    }

    /**
     * Test total negative formal charge.
     *
     */
    @Test
    void testGetTotalNegativeFormalCharge_IAtomContainer() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalNegativeFormalCharge(mol);

        Assertions.assertEquals(-1, totalCharge);
    }

    @Test
    void testGetIntersection_IAtomContainer_IAtomContainer() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom o = builder.newInstance(IAtom.class, "O");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        IAtom c3 = builder.newInstance(IAtom.class, "C");

        IBond b1 = builder.newInstance(IBond.class, c1, o);
        IBond b2 = builder.newInstance(IBond.class, o, c2);
        IBond b3 = builder.newInstance(IBond.class, c2, c3);

        IAtomContainer container1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container1.addAtom(c1);
        container1.addAtom(o);
        container1.addAtom(c2);
        container1.addBond(b1);
        container1.addBond(b2);
        IAtomContainer container2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container2.addAtom(o);
        container2.addAtom(c3);
        container2.addAtom(c2);
        container2.addBond(b3);
        container2.addBond(b2);

        IAtomContainer intersection = AtomContainerManipulator.getIntersection(container1, container2);
        Assertions.assertEquals(2, intersection.getAtomCount());
        Assertions.assertEquals(1, intersection.getBondCount());
        Assertions.assertTrue(intersection.contains(b2));
        Assertions.assertTrue(intersection.contains(o));
        Assertions.assertTrue(intersection.contains(c2));
    }

    @Test
    void testPerceiveAtomTypesAndConfigureAtoms() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(new Atom("R"));

        // the next should not throw an exception
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        } catch (CDKException e) {
            Assertions.fail("The percieveAtomTypesAndConfigureAtoms must not throw exceptions when no atom type is perceived.");
        }
    }

    @Test
    void testPerceiveAtomTypesAndConfigureUnsetProperties() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom("C");
        atom.setExactMass(13.0);
        container.addAtom(atom);
        IAtomType type = new AtomType("C");
        type.setAtomTypeName("C.sp3");
        type.setExactMass(12.0);

        AtomContainerManipulator.percieveAtomTypesAndConfigureUnsetProperties(container);
        Assertions.assertNotNull(atom.getExactMass());
        Assertions.assertEquals(13.0, atom.getExactMass(), 0.1);
        Assertions.assertNotNull(atom.getAtomTypeName());
        Assertions.assertEquals("C.sp3", atom.getAtomTypeName());
    }

    @Test
    void testClearConfig() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);
        container.addBond(new Bond(atom1, atom2, IBond.Order.SINGLE));
        container.addBond(new Bond(atom2, atom3, IBond.Order.SINGLE));

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        for (IAtom atom : container.atoms()) {
            Assertions.assertTrue(atom.getAtomTypeName() != CDKConstants.UNSET);
            Assertions.assertTrue(atom.getHybridization() != CDKConstants.UNSET);
        }

        AtomContainerManipulator.clearAtomConfigurations(container);
        for (IAtom atom : container.atoms()) {
            Assertions.assertTrue(atom.getAtomTypeName() == CDKConstants.UNSET);
            Assertions.assertTrue(atom.getHybridization() == CDKConstants.UNSET);
        }
    }

    @Test
    void atomicNumberIsNotCleared() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);
        container.addBond(new Bond(atom1, atom2, IBond.Order.SINGLE));
        container.addBond(new Bond(atom2, atom3, IBond.Order.SINGLE));

        AtomContainerManipulator.clearAtomConfigurations(container);
        for (IAtom atom : container.atoms()) {
            Assertions.assertNotNull(atom.getAtomicNumber());
        }
    }

    @Test
    void testGetMaxBondOrder() {
        Assertions.assertEquals(Order.DOUBLE, getMaximumBondOrder(ac));
    }

    @Test
    void testGetSBE() {
        Assertions.assertEquals(12, getSingleBondEquivalentSum(ac));
    }

    @Test
    void testGetTotalCharge() throws IOException, ClassNotFoundException, CDKException {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));

        double totalCharge = AtomContainerManipulator.getTotalCharge(container);

        Assertions.assertEquals(1.0, totalCharge, 0.01);
    }

    /**
     * @cdk.bug 1254
     */
    @Test
    void testCountExplicitH_Null_IAtom() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            AtomContainerManipulator.countExplicitHydrogens(null,
                                                            DefaultChemObjectBuilder.getInstance()
                                                                                    .newInstance(IAtom.class));
        });
    }

    /**
     * @cdk.bug 1254
     */
    @Test
    void testCountExplicitH_IAtomContainer_Null() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            AtomContainerManipulator.countExplicitHydrogens(
                    DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class), null);
        });
    }

    @Test
    void testCountExplicitH() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));

        Assertions.assertEquals(0, countExplicitHydrogens(container, atom1));
        Assertions.assertEquals(0, countExplicitHydrogens(container, atom2));

        for (int i = 0; i < 3; i++) {
            IAtom h = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
            container.addAtom(h);
            container.addBond(new Bond(atom1, h, Order.SINGLE));
        }
        Assertions.assertEquals(3, countExplicitHydrogens(container, atom1));
    }

    @Test
    void testCountH() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));

        // no atom type perception, so implicit count is 0
        Assertions.assertEquals(0, countHydrogens(container, atom1));
        Assertions.assertEquals(0, countHydrogens(container, atom2));

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder ha = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
        ha.addImplicitHydrogens(container);

        Assertions.assertEquals(3, countHydrogens(container, atom1));
        Assertions.assertEquals(2, countHydrogens(container, atom2));

        for (int i = 0; i < 3; i++) {
            IAtom h = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
            container.addAtom(h);
            container.addBond(new Bond(atom1, h, Order.SINGLE));
        }
        Assertions.assertEquals(6, countHydrogens(container, atom1));

    }

    /**
     * @cdk.bug 1254
     */
    @Test
    void testGetImplicitHydrogenCount_unperceived() throws Exception {
        IAtomContainer container = TestMoleculeFactory.makeAdenine();
        Assertions.assertEquals(0, getImplicitHydrogenCount(container), "Container has not been atom-typed - should have 0 implicit hydrogens");
    }

    /**
     * @cdk.bug 1254
     */
    @Test
    void testGetImplicitHydrogenCount_null() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            AtomContainerManipulator.getImplicitHydrogenCount(null);
        });
    }

    /**
     * @cdk.bug 1254
     */
    @Test
    void testGetImplicitHydrogenCount_adenine() throws Exception {
        IAtomContainer container = TestMoleculeFactory.makeAdenine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance()).addImplicitHydrogens(container);
        Assertions.assertEquals(5, getImplicitHydrogenCount(container), "Adenine should have 5 implicit hydrogens");

    }

    @Test
    void testReplaceAtom() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));

        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Br");

        AtomContainerManipulator.replaceAtomByAtom(container, atom2, atom3);
        Assertions.assertEquals(atom3, container.getAtom(1));
    }

    @Test
    void testReplaceAtom_lonePair() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));
        container.addLonePair(1);

        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Br");

        AtomContainerManipulator.replaceAtomByAtom(container, atom2, atom3);
        Assertions.assertEquals(atom3, container.getLonePair(0).getAtom());
    }

    @Test
    void testReplaceAtom_singleElectron() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));
        container.addSingleElectron(1);

        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Br");

        AtomContainerManipulator.replaceAtomByAtom(container, atom2, atom3);
        Assertions.assertEquals(atom3, container.getSingleElectron(0).getAtom());
    }

    @Test
    void testReplaceAtom_stereochemistry() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("N[C@H](CC)O");
        IAtom newAtom = bldr.newInstance(IAtom.class, "Cl");
        newAtom.setImplicitHydrogenCount(0);
        AtomContainerManipulator.replaceAtomByAtom(mol, mol.getAtom(0), newAtom);
        assertThat(SmilesGenerator.isomeric().create(mol), is("Cl[C@H](CC)O"));
    }

    @Test
    void testGetHeavyAtoms_IAtomContainer() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        container.addAtom(builder.newInstance(IAtom.class, "C"));
        for (int i = 0; i < 4; i++)
            container.addAtom(builder.newInstance(IAtom.class, "H"));
        container.addAtom(builder.newInstance(IAtom.class, "O"));
        Assertions.assertEquals(2, getHeavyAtoms(container).size());
    }

    /**
     * Test removeHydrogensPreserveMultiplyBonded for B2H6, which contains two multiply bonded H.
     *
     */
    @Test
    void testRemoveHydrogensPreserveMultiplyBonded() throws Exception {
        IAtomContainer borane = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "B"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "B"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addBond(0, 2, Order.SINGLE);
        borane.addBond(1, 2, Order.SINGLE);
        borane.addBond(2, 3, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(2, 4, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(3, 5, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(4, 5, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(5, 6, Order.SINGLE);
        borane.addBond(5, 7, Order.SINGLE);
        for (IAtom atom : borane.atoms())
            atom.setImplicitHydrogenCount(0);
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(borane);

        // Should be two connected Bs with H-count == 2 and two explicit Hs.
        Assertions.assertEquals(4, ac.getAtomCount(), "incorrect atom count");
        Assertions.assertEquals(4, ac.getBondCount(), "incorrect bond count");

        int b = 0;
        int h = 0;
        for (int i = 0; i < ac.getAtomCount(); i++) {
            final org.openscience.cdk.interfaces.IAtom atom = ac.getAtom(i);
            String sym = atom.getSymbol();
            if (sym.equals("B")) {
                // Each B has two explicit and two implicit H.
                b++;
                Assertions.assertEquals(2, atom.getImplicitHydrogenCount().intValue(), "incorrect hydrogen count");
                List<IAtom> nbs = ac.getConnectedAtomsList(atom);
                Assertions.assertEquals(2, nbs.size(), "incorrect connected count");
                Assertions.assertEquals("H", nbs.get(0).getSymbol(), "incorrect bond");
                Assertions.assertEquals("H", nbs.get(1).getSymbol(), "incorrect bond");
            } else if (sym.equals("H")) {
                h++;
            }
        }
        Assertions.assertEquals(2, b, "incorrect no. Bs");
        Assertions.assertEquals(2, h, "incorrect no. Hs");
    }

    @Test
    void testCreateAnyAtomAnyBondAtomContainer_IAtomContainer() throws Exception {
        String smiles = "c1ccccc1";
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(smiles);
        mol = AtomContainerManipulator.createAllCarbonAllSingleNonAromaticBondAtomContainer(mol);
        String smiles2 = "C1CCCCC1";
        IAtomContainer mol2 = sp.parseSmiles(smiles2);
        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(mol, mol2));
    }

    @Test
    void testAnonymise() throws Exception {

        IAtomContainer cyclohexane = TestMoleculeFactory.makeCyclohexane();

        cyclohexane.getAtom(0).setSymbol("O");
        cyclohexane.getAtom(2).setSymbol("O");
        cyclohexane.getAtom(1).setAtomTypeName("remove me");
        cyclohexane.getAtom(3).setFlag(IChemObject.AROMATIC, true);
        cyclohexane.getAtom(4).setImplicitHydrogenCount(2);
        cyclohexane.getBond(0).setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        cyclohexane.getBond(1).setFlag(IChemObject.AROMATIC, true);

        IAtomContainer anonymous = AtomContainerManipulator.anonymise(cyclohexane);

        Assertions.assertTrue(new UniversalIsomorphismTester().isIsomorph(anonymous, TestMoleculeFactory.makeCyclohexane()));

        assertThat(anonymous.getAtom(0).getSymbol(), is("C"));
        assertThat(anonymous.getAtom(2).getSymbol(), is("C"));
        Assertions.assertNull(anonymous.getAtom(1).getAtomTypeName());
        assertThat(anonymous.getAtom(4).getImplicitHydrogenCount(), is(0));
        Assertions.assertFalse(anonymous.getAtom(3).getFlag(IChemObject.AROMATIC));

        Assertions.assertFalse(anonymous.getBond(1).getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(anonymous.getBond(1).getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void skeleton() throws Exception {

        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        IAtomContainer skeleton = AtomContainerManipulator.skeleton(adenine);

        assertThat(skeleton, is(not(sameInstance(adenine))));

        for (IBond bond : skeleton.bonds())
            assertThat(bond.getOrder(), is(IBond.Order.SINGLE));

        for (int i = 0; i < skeleton.getAtomCount(); i++) {
            assertThat(skeleton.getAtom(i).getSymbol(), is(adenine.getAtom(i).getSymbol()));
        }
    }

    /**
     * https://sourceforge.net/p/cdk/mailman/message/20639023/
     * @cdk.bug  1969156
     */
    @Test
    void testOverWriteConfig() throws Exception {
        String filename = "lobtest2.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = cList.get(0);

        Map<IAtom,Double> exactMass = new HashMap<>();

        Isotopes.getInstance().configureAtoms(ac);

        for (IAtom atom : ac.atoms()) {
            exactMass.put(atom, atom.getExactMass());
        }

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);

        for (IAtom atom : ac.atoms()) {
            Double expected = exactMass.get(atom);
            Double actual   = atom.getExactMass();
            if (expected == null)
                Assertions.assertNull(actual);
            else
                org.hamcrest.MatcherAssert.assertThat(actual,
                                  is(closeTo(expected, 0.001)));
        }
    }

    @Test
    void setSingleOrDoubleFlags() {
        IAtomContainer biphenyl = TestMoleculeFactory.makeBiphenyl();
        for (IBond bond : biphenyl.bonds()) {
            bond.setFlag(IChemObject.AROMATIC, true);
        }
        AtomContainerManipulator.setSingleOrDoubleFlags(biphenyl);
        Assertions.assertTrue(biphenyl.getFlag(IChemObject.SINGLE_OR_DOUBLE));
        for (IAtom atom : biphenyl.atoms()) {
            Assertions.assertTrue(biphenyl.getFlag(IChemObject.SINGLE_OR_DOUBLE));
        }
        int n = 0;
        for (IBond bond : biphenyl.bonds()) {
            n += bond.getFlag(IChemObject.SINGLE_OR_DOUBLE) ? 1 : 0;
        }
        // 13 bonds - the one which joins the two rings is now marked as single
        // or double
        assertThat(n, is(12));
    }

    /**
     * Molecular hydrogen is found in the first batch of PubChem entries, and
     * removal of hydrogen should simply return an empty IAtomContainer, not
     * throw an NullPointerException.
     *
     * - note now molecular hydrogen is preserved to avoid information loss.
     *
     * @cdk.bug 2366528
     */
    @Test
    void testRemoveHydrogensFromMolecularHydrogen() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer(); // molecular hydrogen
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.SINGLE);

        Assertions.assertEquals(2, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assertions.assertEquals(2, ac.getAtomCount());
    }

    @Test
    void testBondOrderSum() throws InvalidSmilesException {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("C=CC");
        double bosum = AtomContainerManipulator.getBondOrderSum(mol, mol.getAtom(0));
        Assertions.assertEquals(2.0, bosum, 0.001);
        bosum = AtomContainerManipulator.getBondOrderSum(mol, mol.getAtom(1));
        Assertions.assertEquals(3.0, bosum, 0.001);
        bosum = AtomContainerManipulator.getBondOrderSum(mol, mol.getAtom(2));
        Assertions.assertEquals(1.0, bosum, 0.001);

    }

    @Test
    void convertExplicitHydrogen_chiralCarbon() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer m = smipar.parseSmiles("C[C@H](CC)O");

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(m);

        assertThat(SmilesGenerator.isomeric().create(m), is("C([C@](C(C([H])([H])[H])([H])[H])(O[H])[H])([H])([H])[H]"));
    }

    @Test
    void convertExplicitHydrogen_sulfoxide() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer m = smipar.parseSmiles("[S@](=O)(C)CC");

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(m);

        assertThat(SmilesGenerator.isomeric().create(m), is("[S@](=O)(C([H])([H])[H])C(C([H])([H])[H])([H])[H]"));
    }

    @Test
    void removeHydrogens_chiralCarbon1() throws Exception {
        assertRemoveH("C[C@@](CC)([H])O", "C[C@H](CC)O");
    }

    @Test
    void removeHydrogens_chiralCarbon2() throws Exception {
        assertRemoveH("C[C@@]([H])(CC)O", "C[C@@H](CC)O");
    }

    @Test
    void removeHydrogens_chiralCarbon3() throws Exception {
        assertRemoveH("C[C@@](CC)(O)[H]", "C[C@@H](CC)O");
    }

    @Test
    void removeHydrogens_chiralCarbon4() throws Exception {
        assertRemoveH("[H][C@@](C)(CC)O", "[C@@H](C)(CC)O");
    }

    @Test
    void removeHydrogens_db_trans1() throws Exception {
        assertRemoveH("C/C([H])=C([H])/C", "C/C=C/C");
        assertRemoveH("C\\C([H])=C([H])\\C", "C/C=C/C");
    }

    @Test
    void removeHydrogens_db_cis1() throws Exception {
        assertRemoveH("C/C([H])=C([H])\\C", "C/C=C\\C");
        assertRemoveH("C\\C([H])=C([H])/C", "C/C=C\\C");
    }

    @Test
    void removeHydrogens_db_trans2() throws Exception {
        assertRemoveH("CC(/[H])=C([H])/C", "C/C=C/C");
    }

    @Test
    void removeHydrogens_db_cis2() throws Exception {
        assertRemoveH("CC(\\[H])=C([H])/C", "C/C=C\\C");
    }

    @Test
    void removeHydrogens_db_trans3() throws Exception {
        assertRemoveH("CC(/[H])=C(\\[H])C", "C/C=C/C");
    }

    @Test
    void removeHydrogens_db_cis3() throws Exception {
        assertRemoveH("CC(\\[H])=C(\\[H])C", "C/C=C\\C");
    }

    // hydrogen isotopes should not be removed
    @Test
    void removeHydrogens_isotopes() throws Exception {
        assertRemoveH("C([H])([2H])([3H])[H]", "C([2H])[3H]");
    }

    // hydrogens with charge should not be removed
    @Test
    void removeHydrogens_ions() throws Exception {
        assertRemoveH("C([H])([H+])([H-])[H]", "C([H+])[H-]");
    }

    @Test
    void removeHydrogens_molecularH() throws Exception {
        assertRemoveH("[H][H]", "[H][H]");
        assertRemoveH("[HH]", "[H][H]");
    }

    @Test
    void testSgroupSuppressionSRU() throws Exception {
        assertRemoveH("CCC([H])CC |Sg:n:1,2,3,4:n:ht|",
                      "CCCCC |Sg:n:1,2,3:n:ht|");
    }

    @Test
    void testSgroupSuppressionSRUUpdated() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("CCC([H])CC |Sg:n:1,2,3,4:n:ht|");
        AtomContainerManipulator.suppressHydrogens(mol);
        Collection<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        Assertions.assertNotNull(sgroups);
        assertThat(sgroups.size(), is(1));
        Sgroup sgroup = sgroups.iterator().next();
        assertThat(sgroup.getAtoms().size(), is(3));
    }

    @Test
    void testSgroupSuppressionPositionalVariation() throws Exception {
        assertRemoveH("*[H].C1=CC=CC=C1 |m:0:2.3.4|",
                      "*[H].C1=CC=CC=C1 |m:0:2.3.4|");
    }

    @Test
    void testSgroupSuppressionSRUCrossingBond() throws Exception {
        assertRemoveH("CCC[H] |Sg:n:2:n:ht|",
                      "CCC[H] |Sg:n:2:n:ht|");
    }

    @Test
    void keepStereoGroup() throws Exception {
        assertRemoveH("C[C@]([H])(O)CC |o1:1|",
                      "C[C@H](O)CC |o1:1|");
    }

    @Test
    void keepStereoGroup2() throws Exception {
        assertAddH("C[C@H](O)CC |o1:1|",
                   "C([C@](O[H])(C(C([H])([H])[H])([H])[H])[H])([H])([H])[H] |o1:1|");
    }

    @Test
    void molecularWeight() throws InvalidSmilesException, IOException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[13CH4]CO");
        double molecularWeight = AtomContainerManipulator.getMolecularWeight(mol);
        double naturalExactMass = AtomContainerManipulator.getNaturalExactMass(mol);
        Isotopes isotopes = Isotopes.getInstance();
        for (IAtom atom : mol.atoms()) {
            if (atom.getMassNumber() == null)
                atom.setExactMass(isotopes.getMajorIsotope(atom.getAtomicNumber())
                                          .getExactMass());
            else
                isotopes.configure(atom);
        }
        double exactMass = AtomContainerManipulator.getTotalExactMass(mol);
        assertThat(molecularWeight, closeTo(48.069, 0.001));
        assertThat(naturalExactMass, closeTo(47.076, 0.001));
        assertThat(exactMass, closeTo(48.053, 0.001));
    }

    @Test
    void removeBondStereo() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[2H]/C=C/[H]");
        AtomContainerManipulator.suppressHydrogens(mol);
        assertThat(mol.stereoElements().iterator().hasNext(),
                   CoreMatchers.is(false));
    }

    @Test
    void keep1Hisotopes() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[2H]/C=C/[1H]");
        AtomContainerManipulator.suppressHydrogens(mol);
        assertThat(mol.getAtomCount(), is(4));
    }

    // util for testing hydrogen removal using SMILES
    static void assertRemoveH(String smiIn, String smiExp) throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer m = smipar.parseSmiles(smiIn);

        String smiAct = new SmilesGenerator(SmiFlavor.Default).create(AtomContainerManipulator.removeHydrogens(m));

        assertThat(smiAct, is(smiExp));
    }

    static void assertAddH(String smiIn, String smiExp) throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer m = smipar.parseSmiles(smiIn);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(m);
        String smiAct = new SmilesGenerator(SmiFlavor.Default).create(m);

        assertThat(smiAct, is(smiExp));
    }

    @Test
    void getMassC6Br6() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("Brc1c(Br)c(Br)c(Br)c(Br)c1Br");
        assertThat(AtomContainerManipulator.getMass(mol, MolWeight),
                   closeTo(551.485, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MolWeightIgnoreSpecified),
                   closeTo(551.485, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MonoIsotopic),
                   closeTo(545.510, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MostAbundant),
                   closeTo(551.503, 0.001));
    }

    @Test
    void getMassCranbin() {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol =
                MolecularFormulaManipulator.getAtomContainer("C202H315N55O64S6",
                                                              bldr);
        assertThat(AtomContainerManipulator.getMass(mol, MolWeight),
                   closeTo(4730.397, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MolWeightIgnoreSpecified),
                   closeTo(4730.397, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MonoIsotopic),
                   closeTo(4727.140, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MostAbundant),
                   closeTo(4729.147, 0.001));
    }

    @Test
    void getMassBadIsotopes() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = new SmilesParser(bldr).parseSmiles("[24cH]1[24cH][24cH][24cH][24cH][24cH]1");
        assertThat(AtomContainerManipulator.getMass(mol, MolWeight),
                closeTo(150.0476, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MolWeightIgnoreSpecified),
                closeTo(78.1120, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MonoIsotopic),
                closeTo(150.0469, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MostAbundant),
                closeTo(150.0469, 0.001));
    }

    @Test
    void getMassCranbinSpecIsotopes() {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol =
                MolecularFormulaManipulator.getAtomContainer("[12]C200[13]C2[1]H315[14]N55[16]O64[32]S6",
                                                             bldr);
        assertThat(AtomContainerManipulator.getMass(mol, MolWeight),
                   closeTo(4729.147, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MolWeightIgnoreSpecified),
                   closeTo(4730.397, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MonoIsotopic),
                   closeTo(4729.147, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MostAbundant),
                   closeTo(4729.147, 0.001));
    }

    @Test
    void getMassCranbinMixedSpecIsotopes() {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol =
                MolecularFormulaManipulator.getAtomContainer("C200[13]C2H315N55O64S6",
                                                             bldr);
        assertThat(AtomContainerManipulator.getMass(mol, MolWeight),
                   closeTo(4732.382, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MolWeightIgnoreSpecified),
                   closeTo(4730.397, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MonoIsotopic),
                   closeTo(4729.147, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MostAbundant),
                   closeTo(4731.154, 0.001));
    }

    // can't put these test in cdk-formula since we can't access SMILES and it's a bit verbose
    // to construct the molecules as needed
    @Test
    void getFormulaMultiattach() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[Ru]([P](CCC1=CC=CC=C1)(C2CCCCC2)C3CCCCC3)(Cl)(Cl)*.C1(=CC=C(C=C1)C(C)C)C |m:24:25.26.27.28.29.30|");
        String mf = MolecularFormulaManipulator.getString(MolecularFormulaManipulator.getMolecularFormula(mol));
        assertThat(mf, CoreMatchers.is("C30H45Cl2PRu"));
    }

    @Test
    void getFormulaAttach() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("*c1cc(*)ccc1 |$_AP1;;;;R;$|");
        String mf = MolecularFormulaManipulator.getString(MolecularFormulaManipulator.getMolecularFormula(mol));
        assertThat(mf, CoreMatchers.is("C6H4R"));
    }

    @Test
    public void suppressInorganicHydrogens() throws CDKException {
        String smi = "C[Pt@SP3](F)(Cl)[H]";
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        AtomContainerManipulator.suppressHydrogens(mol);
        SquarePlanar sp = null;
        for (IStereoElement<?,?> se : mol.stereoElements()) {
            if (se instanceof SquarePlanar)
                sp = (SquarePlanar)se;
        }
        Assertions.assertNotNull(sp);
        List<IAtom> carriers = sp.getCarriers();
        Assertions.assertEquals(carriers.get(0), mol.getAtom(0));
        Assertions.assertEquals(carriers.get(1), mol.getAtom(2));
        Assertions.assertEquals(carriers.get(2), mol.getAtom(3));
        Assertions.assertEquals(carriers.get(3), sp.getFocus()); // was H!
        Assertions.assertEquals(1, sp.getFocus().getImplicitHydrogenCount());
    }

    @Test
    public void suppressInorganicHydrogensMultipleH() throws CDKException {
        String smi = "C[Co@OH1H](F)(Cl)([H])[H]";
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        AtomContainerManipulator.suppressHydrogens(mol);
        Octahedral oc = null;
        for (IStereoElement<?,?> se : mol.stereoElements()) {
            if (se instanceof Octahedral)
                oc = (Octahedral) se;
        }
        Assertions.assertNotNull(oc);
        List<IAtom> carriers = oc.getCarriers();
        Assertions.assertEquals(carriers.get(0), mol.getAtom(0));
        Assertions.assertEquals(carriers.get(1), oc.getFocus());
        Assertions.assertEquals(carriers.get(2), mol.getAtom(2));
        Assertions.assertEquals(carriers.get(3), mol.getAtom(3));
        Assertions.assertEquals(carriers.get(4), oc.getFocus()); // was H!
        Assertions.assertEquals(carriers.get(5), oc.getFocus()); // was H!
        Assertions.assertEquals(3, oc.getFocus().getImplicitHydrogenCount());
    }



    @Test
    public void suppressInorganicHydrogens2() throws CDKException {
        String smi = "C[Pt@SP3](F)(Cl)[H]";
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        AtomContainerManipulator.suppressHydrogens(mol);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
        Assertions.assertEquals("C[Pt@SP2H](F)Cl", smigen.create(mol));
    }

    @Test
    public void suppressInorganicHydrogens3() throws CDKException {
        String smi = "C[Pt@OH1]([H])([H])([H])([H])C";
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        AtomContainerManipulator.suppressHydrogens(mol);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
        Assertions.assertEquals("C[Pt@OH1H4]C", smigen.create(mol));
    }

    @Test
    public void suppressInorganicHydrogens4() throws CDKException {
        String smi = "C[Pt@OH1](Cl)(Cl)(Cl)(Cl)[H]";
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        AtomContainerManipulator.suppressHydrogens(mol);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
        Assertions.assertEquals("C[Pt@OH25H](Cl)(Cl)(Cl)Cl", smigen.create(mol));
    }

    @Test
    public void suppressInorganicHydrogens5() throws CDKException {
        String smi = "C[Pt@TB1](Cl)(Cl)(Cl)[H]";
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        AtomContainerManipulator.suppressHydrogens(mol);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
        Assertions.assertEquals("C[Pt@TB7H](Cl)(Cl)Cl", smigen.create(mol));
    }

    @Test
    public void convertImplicitToExplicit() throws CDKException {
        String smi = "C[Pt@OH25H2](Cl)(Cl)Cl";
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
        Assertions.assertEquals("C([Pt@](Cl)(Cl)(Cl)([H])[H])([H])([H])[H]", smigen.create(mol));
    }

    @Test
    public void convertImplicitToExplicit2() throws CDKException {
        String smi = "C[Pt@OH25H2](Cl)Cl"; // also a lone pair!
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
        Assertions.assertEquals("C([Pt@OH25](Cl)(Cl)([H])[H])([H])([H])[H]", smigen.create(mol));
    }
}
