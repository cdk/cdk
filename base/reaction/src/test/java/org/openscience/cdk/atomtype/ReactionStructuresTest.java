/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.atomtype;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.reaction.type.AdductionSodiumLPReactionTest;
import org.openscience.cdk.reaction.type.ElectronImpactNBEReaction;
import org.openscience.cdk.reaction.type.HeterolyticCleavageSBReactionTest;
import org.openscience.cdk.reaction.type.HomolyticCleavageReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteInitiationHReactionTest;
import org.openscience.cdk.reaction.type.SharingChargeDBReactionTest;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @cdk.module test-reaction
 */
public class ReactionStructuresTest extends CDKTestCase {

    private final static IChemObjectBuilder builder;
    private final static CDKAtomTypeMatcher matcher;

    static {
        builder = SilentChemObjectBuilder.getInstance();
        matcher = CDKAtomTypeMatcher.getInstance(builder);
    }

    /**
     * Constructor of the ReactionStructuresTest.
     */
    public ReactionStructuresTest() {
        super();
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       SharingChargeDBReactionTest#testAtomTypesMolecule1()
     */
    @Test
    public void testM0() throws Exception {

        //COMPOUND
        //[C*]=C-C
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addSingleElectron(new SingleElectron(molecule.getAtom(0)));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 4, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(2, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(2, 6, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(2, 7, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.radical.sp2", "C.sp2", "C.sp3", "H", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, molecule.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = molecule.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(molecule, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HeterolyticCleavageSBReactionTest#testCspSingleB()
     */
    @Test
    public void testM4() throws Exception {
        //Smiles("C#[C+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(1).setFormalCharge(+1);
        expected1.addBond(0, 1, IBond.Order.TRIPLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp", "C.plus.sp1", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }

    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HomolyticCleavageReactionTest#testCsp2SingleB()
     */
    @Test
    public void testM5() throws Exception {
        //Smiles("C=[C*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(1, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp2", "C.radical.sp2", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }

    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HomolyticCleavageReactionTest#testCsp2SingleB()
     */
    @Test
    public void testM6() throws Exception {
        //Smiles("C#[C*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addBond(0, 1, IBond.Order.TRIPLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp", "C.radical.sp1", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HomolyticCleavageReactionTest#testCsp2DoubleB()
     */
    @Test
    public void testM7() throws Exception {
        //Smiles("C[C*][C*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(2)));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(0, 5, IBond.Order.SINGLE);
        expected1.addBond(1, 6, IBond.Order.SINGLE);
        expected1.addBond(2, 7, IBond.Order.SINGLE);
        expected1.addBond(2, 8, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "C.radical.planar", "C.radical.planar", "H", "H", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HomolyticCleavageReactionTest#testCspDoubleB()
     */
    @Test
    public void testM8() throws Exception {
        //Smiles("C=[C*][C*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(2)));
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(2, 5, IBond.Order.SINGLE);
        expected1.addBond(2, 6, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp2", "C.radical.sp2", "C.radical.planar", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HomolyticCleavageReactionTest#testNsp3SingleB()
     */
    @Test
    public void testM9() throws Exception {
        //Smiles("C[N*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(1, 5, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "N.sp3.radical", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HomolyticCleavageReactionTest#testNsp2SingleB()
     */
    @Test
    public void testM10() throws Exception {
        //Smiles("C=[N*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp2", "N.sp2.radical", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HomolyticCleavageReactionTest#testOsp2SingleB()
     */
    @Test
    public void testM13() throws Exception {
        //Smiles("[O+*][C*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "O"));
        expected1.getAtom(0).setFormalCharge(1);
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(0)));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addBond(1, 3, IBond.Order.SINGLE);

        String[] expectedTypes = {"O.plus.radical", "C.radical.planar", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HomolyticCleavageReactionTest#testFspSingleB()
     */
    @Test
    public void testM14() throws Exception {
        //Smiles("[F*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "F"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(0)));

        String[] expectedTypes = {"F.radical"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       HomolyticCleavageReactionTest#testOsp2SingleB()
     */
    @Test
    public void testM15() throws Exception {
        //Smiles("C[O*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "O"));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "O.sp3.radical", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       ElectronImpactNBEReaction#testNsp2SingleB()
     */
    @Test
    public void testM17() throws Exception {
        //Smiles("[N*+]=C")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.getAtom(0).setFormalCharge(1);
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(0)));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(1, 3, IBond.Order.SINGLE);
        expected1.addBond(1, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"N.plus.sp2.radical", "C.sp2", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       ElectronImpactNBEReaction#testNsp3SingleB()
     */
    @Test
    public void testM18() throws Exception {
        //Smiles("C[N*+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.getAtom(1).setFormalCharge(1);
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(1, 5, IBond.Order.SINGLE);
        expected1.addBond(1, 6, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "N.plus.sp3.radical", "H", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       ElectronImpactNBEReaction#testNsp3SingleB()
     */
    @Test
    public void testM19() throws Exception {
        //Smiles("C=[N*+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.getAtom(1).setFormalCharge(1);
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(1, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp2", "N.plus.sp2.radical", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       RadicalSiteInitiationHReactionTest#testManuallyCentreActive()
     */
    @Test
    public void testM20() throws Exception {
        //Smiles("H*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(0)));

        String[] expectedTypes = {"H.radical"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     */
    @Test
    public void testM21() throws Exception {
        //Smiles("NaH")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "Na"));
        expected1.addBond(0, 1, IBond.Order.SINGLE);

        String[] expectedTypes = {"H", "Na"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see AdductionSodiumLPReactionTest
     */
    @Test
    public void testM22() throws Exception {
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "O"));
        expected1.getAtom(0).setFormalCharge(1);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "Na"));
        expected1.addBond(1, 3, IBond.Order.SINGLE);
        expected1.addBond(2, 4, IBond.Order.SINGLE);
        expected1.addBond(2, 5, IBond.Order.SINGLE);
        expected1.addBond(2, 6, IBond.Order.SINGLE);
        expected1.addBond(0, 7, IBond.Order.SINGLE);

        String[] expectedTypes = {"O.plus.sp2", "C.sp2", "C.sp3", "H", "H", "H", "H", "Na"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see AdductionSodiumLPReactionTest
     */
    @Test
    public void testM23() throws Exception {
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IPseudoAtom.class, "R"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(3).setFormalCharge(1);
        expected1.addBond(2, 3, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(3, 4, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(4, 5, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(5, 6, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(1, 7, IBond.Order.SINGLE);
        expected1.addBond(1, 8, IBond.Order.SINGLE);
        expected1.addBond(2, 9, IBond.Order.SINGLE);
        expected1.addBond(2, 10, IBond.Order.SINGLE);
        expected1.addBond(3, 11, IBond.Order.SINGLE);
        expected1.addBond(4, 12, IBond.Order.SINGLE);
        expected1.addBond(4, 13, IBond.Order.SINGLE);
        expected1.addBond(5, 14, IBond.Order.SINGLE);
        expected1.addBond(5, 15, IBond.Order.SINGLE);
        expected1.addBond(6, 16, IBond.Order.SINGLE);
        expected1.addBond(6, 17, IBond.Order.SINGLE);
        expected1.addBond(6, 18, IBond.Order.SINGLE);

        String[] expectedTypes = {"X", "C.sp3", "C.sp3", "C.plus.planar", "C.sp3", "C.sp3", "C.sp3", "H", "H", "H",
                "H", "H", "H", "H", "H", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see HomolyticCleavageReactionTest#testNsp2DoubleB
     */
    @Test
    public void testM24() throws Exception {
        //Smiles("C[N*]-[C*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(2)));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(0, 5, IBond.Order.SINGLE);
        expected1.addBond(2, 6, IBond.Order.SINGLE);
        expected1.addBond(2, 7, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "N.sp3.radical", "C.radical.planar", "H", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see HomolyticCleavageReactionTest#testNsp2DoubleB
     */
    @Test
    public void testM25() throws Exception {
        //Smiles("C[O*]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "O"));
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);

        String[] expectedTypes = {"C.sp3", "O.sp3.radical", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see HomolyticCleavageReactionTest#testNsp2DoubleB
     */
    @Test
    public void testM26() throws Exception {
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "F"));
        expected1.getAtom(0).setFormalCharge(1);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(2).setFormalCharge(-1);
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(2, 3, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(3, 4, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(4, 5, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(5, 6, IBond.Order.DOUBLE);
        expected1.addBond(6, 1, IBond.Order.SINGLE);
        addExplicitHydrogens(expected1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(expected1);

        String[] expectedTypes = {"F.plus.sp2", "C.sp2", "C.minus.planar", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "H",
                "H", "H", "H", "H"

        };
        Assert.assertEquals(expectedTypes.length, expected1.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = expected1.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expected1, nextAtom);

            Assert.assertNotNull("Missing atom type for: " + nextAtom + " " + i + " expected: " + expectedTypes[i],
                    perceivedType);

            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
            nextAtom.setHybridization(null);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
            IAtomType type = matcher.findMatchingAtomType(expected1, nextAtom);
            Assert.assertNotNull(type);
        }
    }
}
