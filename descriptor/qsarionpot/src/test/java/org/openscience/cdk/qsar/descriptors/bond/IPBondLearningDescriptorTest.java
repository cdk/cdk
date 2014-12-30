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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
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
public class IPBondLearningDescriptorTest extends BondDescriptorTest {

    private IPBondLearningDescriptor descriptor;
    private LonePairElectronChecker  lpcheck = new LonePairElectronChecker();

    /**
     *  Constructor for the IPBondLearningDescriptorTest object
     *
     */
    public IPBondLearningDescriptorTest() {
        descriptor = new IPBondLearningDescriptor();
    }

    @Before
    public void setUp() throws Exception {
        setDescriptor(IPBondLearningDescriptor.class);
    }

    /**
    *  A unit test for JUnit
    */
    @Test
    public void testIPBondLearningDescriptor() {
        Assert.assertNotNull(descriptor);
    }

    /**
     *  A unit test for JUnit with CCCC=CCCCC
     */
    @Test
    @Category(SlowTest.class)
    public void testIPDescriptor_1() throws Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC=CCCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(3), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.80;
        Assert.assertEquals(result, resultAccordingNIST, 0.051);
    }

    /**
     *  A unit test for JUnit with CC1CCC=C1
     */
    @Test
    @Category(SlowTest.class)
    public void testIPDescriptor_2() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC1CCC=C1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(4), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.95;
        Assert.assertEquals(result, resultAccordingNIST, 0.1);
    }

    /**
     *  A unit test for JUnit with C=CCCCC
     */
    @Test
    @Category(SlowTest.class)
    public void testIPDescriptor_3() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.44;
        Assert.assertEquals(result, resultAccordingNIST, 0.3);
    }

    /**
     * A unit test for JUnit with C=CCCCC
     *
     */
    @Test
    @Category(SlowTest.class)
    public void testIPDescriptorReaction1() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.44;

        Assert.assertEquals(resultAccordingNIST, result, 0.3);

    }

    /**
     * A unit test for JUnit with CCCCCC
     *
     */
    @Test
    @Category(SlowTest.class)
    public void testIPDescriptorReaction2() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 0.0;

        Assert.assertEquals(resultAccordingNIST, result, 0.0001);
    }

    /**
     * A unit test for JUnit with C#CCC
     *
     */
    @Test
    @Category(SlowTest.class)
    public void testIPTripleDescriptor1() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C#CCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.44;
        Assert.assertEquals(resultAccordingNIST, result, 0.75);
    }

    /**
     * A unit test for JUnit with C(#CC(C)(C)C)C(C)(C)C
     *
     */
    @Test
    @Category(SlowTest.class)
    public void testIPTripleDescriptor2() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(#CC(C)(C)C)C(C)(C)C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.98;
        Assert.assertEquals(resultAccordingNIST, result, 0.1);
    }

    /**
     * A unit test for JUnit with C=C(C=CC)C
     *
     */
    @Ignore("IonizationPotentialTool now deprecated due to bugs")
    public void testIPConjugatedDescriptor1() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=C(C=CC)C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.47;
        Assert.assertEquals(resultAccordingNIST, result, 0.6);

        result = ((DoubleResult) descriptor.calculate(mol.getBond(2), mol).getValue()).doubleValue();
        resultAccordingNIST = 8.47;
        Assert.assertEquals(resultAccordingNIST, result, 0.4);
    }

    /**
     * A unit test for JUnit with C=CC=C
     *
     */
    @Test
    @Category(SlowTest.class)
    public void testIPPySystemReaction1() throws java.lang.Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CC=C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.072;
        Assert.assertEquals(resultAccordingNIST, result, 2.11);

    }

}
