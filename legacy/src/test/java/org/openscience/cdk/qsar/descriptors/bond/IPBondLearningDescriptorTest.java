/* Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.bond;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR bond tests.
 *
 * @cdk.module test-qsarionpot
 */
class IPBondLearningDescriptorTest extends CDKTestCase {

    private IPBondLearningDescriptor descriptor;
    private final LonePairElectronChecker  lpcheck = new LonePairElectronChecker();

    /**
     *  Constructor for the IPBondLearningDescriptorTest object
     *
     */
    IPBondLearningDescriptorTest() {
        descriptor = new IPBondLearningDescriptor();
    }

    @BeforeEach
    void setUp() throws Exception {
        descriptor = new IPBondLearningDescriptor();
    }

    /**
    *  A unit test for JUnit
    */
    @Test
    void testIPBondLearningDescriptor() {
        Assertions.assertNotNull(descriptor);
    }

    /**
     *  A unit test for JUnit with CCCC=CCCCC
     */
    @Test
    @Tag("SlowTest")
    void testIPDescriptor_1() throws Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC=CCCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(3), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.80;
        Assertions.assertEquals(result, resultAccordingNIST, 0.051);
    }

    /**
     *  A unit test for JUnit with CC1CCC=C1
     */
    @Test
    @Tag("SlowTest")
    void testIPDescriptor_2() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC1CCC=C1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(4), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.95;
        Assertions.assertEquals(result, resultAccordingNIST, 0.1);
    }

    /**
     *  A unit test for JUnit with C=CCCCC
     */
    @Test
    @Tag("SlowTest")
    void testIPDescriptor_3() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.44;
        Assertions.assertEquals(result, resultAccordingNIST, 0.3);
    }

    /**
     * A unit test for JUnit with C=CCCCC
     *
     */
    @Test
    @Tag("SlowTest")
    void testIPDescriptorReaction1() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.44;

        Assertions.assertEquals(resultAccordingNIST, result, 0.3);

    }

    /**
     * A unit test for JUnit with CCCCCC
     *
     */
    @Test
    @Tag("SlowTest")
    void testIPDescriptorReaction2() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 0.0;

        Assertions.assertEquals(resultAccordingNIST, result, 0.0001);
    }

    /**
     * A unit test for JUnit with C#CCC
     *
     */
    @Test
    @Tag("SlowTest")
    void testIPTripleDescriptor1() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C#CCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.44;
        Assertions.assertEquals(resultAccordingNIST, result, 0.75);
    }

    /**
     * A unit test for JUnit with C(#CC(C)(C)C)C(C)(C)C
     *
     */
    @Test
    @Tag("SlowTest")
    void testIPTripleDescriptor2() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(#CC(C)(C)C)C(C)(C)C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.98;
        Assertions.assertEquals(resultAccordingNIST, result, 0.1);
    }

    /**
     * A unit test for JUnit with C=C(C=CC)C
     *
     */
    @Disabled("IonizationPotentialTool now deprecated due to bugs")
    void testIPConjugatedDescriptor1() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=C(C=CC)C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.47;
        Assertions.assertEquals(resultAccordingNIST, result, 0.6);

        result = ((DoubleResult) descriptor.calculate(mol.getBond(2), mol).getValue()).doubleValue();
        resultAccordingNIST = 8.47;
        Assertions.assertEquals(resultAccordingNIST, result, 0.4);
    }

    /**
     * A unit test for JUnit with C=CC=C
     *
     */
    @Test
    @Tag("SlowTest")
    void testIPPySystemReaction1() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CC=C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.072;
        Assertions.assertEquals(resultAccordingNIST, result, 2.11);

    }

}
