/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
class PiElectronegativityDescriptorTest extends AtomicDescriptorTest {

    private final IChemObjectBuilder      builder = SilentChemObjectBuilder.getInstance();
    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    /**
     *  Constructor for the PiElectronegativityDescriptorTest object
     *
     */
    PiElectronegativityDescriptorTest() {}

    /**
     *  A unit test suite for JUnit
     *
     *@return    The test suite
     */

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(PiElectronegativityDescriptor.class);
    }

    /**
     *  A unit test for JUnit with Methyl Fluoride
     */
    @Test
    void testPiElectronegativityDescriptor_Methyl_Fluoride() throws
            java.lang.Exception {
        double[] testResult = {3.9608, 0.0, 0.0, 0.0, 0.0};/*
                                                            * from Petra online:
                                                            * http
                                                            * ://www2.chemie.
                                                            * uni-
                                                            * erlangen.de/services
                                                            * /
                                                            * petra/smiles.phtml
                                                            */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("FC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            params[0] = 10;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //			logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.03);
            }
        }
    }

    /**
     *  A unit test for JUnit with Methyl Chloride
     */
    @Test
    void testPiElectronegativityDescriptor_Methyl_Chloride() throws
            java.lang.Exception {
        double[] testResult = {4.7054, 0.0, 0.0, 0.0, 0.0};/*
                                                            * from Petra
                                                            * onlimoleculene:
                                                            * http
                                                            * ://www2.chemie.
                                                            * uni-
                                                            * erlangen.de/services
                                                            * /
                                                            * petra/smiles.phtml
                                                            */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("ClC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            params[0] = 10;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //			logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.01);
            }
        }
    }

    /**
     *  A unit test for JUnit with Methyl iodide
     */
    @Test
    void testPiElectronegativityDescriptor_Methyl_Iodide() throws
            java.lang.Exception {
        double[] testResult = {4.1951, 0.0, 0.0, 0.0, 0.0};/*
                                                            * from Petra
                                                            * onlimoleculene:
                                                            * http
                                                            * ://www2.chemie.
                                                            * uni-
                                                            * erlangen.de/services
                                                            * /
                                                            * petra/smiles.phtml
                                                            */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("IC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            params[0] = 10;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //			logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.01);
            }
        }
    }

    /**
     *  A unit test for JUnit with Methyl Bromide
     */
    @Test
    void testPiElectronegativityDescriptor_Methyl_Bromide() throws
            java.lang.Exception {
        double[] testResult = {3.8922, 0.0, 0.0, 0.0, 0.0};/*
                                                            * from Petra online:
                                                            * http
                                                            * ://www2.chemie.
                                                            * uni-
                                                            * erlangen.de/services
                                                            * /
                                                            * petra/smiles.phtml
                                                            */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("BrC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            params[0] = 10;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //			logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.03);
            }
        }
    }

    /**
     *  A unit test for JUnit with Methyl Alcohol
     */
    @Test
    void testPiElectronegativityDescriptor_Methyl_Alcohol() throws
            java.lang.Exception {
        double[] testResult = {3.1138, 0.0, 0.0, 0.0, 0.0};/*
                                                            * from Petra online:
                                                            * http
                                                            * ://www2.chemie.
                                                            * uni-
                                                            * erlangen.de/services
                                                            * /
                                                            * petra/smiles.phtml
                                                            */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("OC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < 4; i++) {
            params[0] = 10;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //			logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.01);
            }
        }
    }

    /**
     *  A unit test for JUnit with Formaldehyde
     */
    @Test
    void testPiElectronegativityDescriptor_Formaldehyde() throws
            java.lang.Exception {
        double[] testResult = {6.3012, 8.0791, 0.0, 0.0, 0.0};/*
                                                               * from Petra
                                                               * online:
                                                               * http://www2
                                                               * .chemie
                                                               * .uni-erlangen
                                                               * .de
                                                               * /services/petra
                                                               * /smiles.phtml
                                                               */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            params[0] = 10;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //			logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.55);
            }
        }
    }

    /**
     *  A unit test for JUnit with Ethylene
     */
    @Test
    void testPiElectronegativityDescriptor_Ethylene() throws
            java.lang.Exception {

        double[] testResult = {5.1519, 5.1519, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};/*
                                                                              * from
                                                                              * Petra
                                                                              * online
                                                                              * :
                                                                              * http
                                                                              * :
                                                                              * /
                                                                              * /
                                                                              * www2
                                                                              * .
                                                                              * chemie
                                                                              * .
                                                                              * uni
                                                                              * -
                                                                              * erlangen
                                                                              * .
                                                                              * de
                                                                              * /
                                                                              * services
                                                                              * /
                                                                              * petra
                                                                              * /
                                                                              * smiles
                                                                              * .
                                                                              * phtml
                                                                              */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=C");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);
        for (int i = 0; i < 3; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();

            //	        logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.02);
            }
        }
    }

    /**
     *  A unit test for JUnit with Fluoroethylene
     */
    @Test
    void testPiElectronegativityDescriptor_Fluoroethylene() throws
            java.lang.Exception {
        double[] testResult = {4.7796, 5.9414, 5.0507, 0.0, 0.0, 0.0};/*
                                                                       * from
                                                                       * Petra
                                                                       * online:
                                                                       * http
                                                                       * ://www2
                                                                       * .
                                                                       * chemie.
                                                                       * uni
                                                                       * -erlangen
                                                                       * .
                                                                       * de/services
                                                                       * /petra/
                                                                       * smiles
                                                                       * .phtml
                                                                       */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("F-C=C");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < 3; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //	        logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.7);
            }
        }
    }

    /**
     *  A unit test for JUnit with Formic Acid
     */
    @Test
    void testPiElectronegativityDescriptor_FormicAcid() throws
            java.lang.Exception {
        double[] testResult = {6.8954, 7.301, 4.8022, 0.0, 0.0};/*
                                                                 * from Petra
                                                                 * online:
                                                                 * http:/
                                                                 * /www2.chemie
                                                                 * .uni
                                                                 * -erlangen.
                                                                 * de/services
                                                                 * /petra
                                                                 * /smiles.phtml
                                                                 */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(=O)O");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //			logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 2);
            }
        }
    }

    /**
     *  A unit test for JUnit with Methoxyethylene
     */
    @Test
    void testPiElectronegativityDescriptor_Methoxyethylene() throws
            java.lang.Exception {
        double[] testResult = {4.916, 5.7345, 3.971, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};/*
                                                                                         * from
                                                                                         * Petra
                                                                                         * online
                                                                                         * :
                                                                                         * http
                                                                                         * :
                                                                                         * /
                                                                                         * /
                                                                                         * www2
                                                                                         * .
                                                                                         * chemie
                                                                                         * .
                                                                                         * uni
                                                                                         * -
                                                                                         * erlangen
                                                                                         * .
                                                                                         * de
                                                                                         * /
                                                                                         * services
                                                                                         * /
                                                                                         * petra
                                                                                         * /
                                                                                         * smiles
                                                                                         * .
                                                                                         * phtml
                                                                                         */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=C-O-C");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //	        logger.debug("result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.5);
            }
        }
    }

    /**
     *  A unit test for JUnit with F[C+][C-]
     */
    @Test
    void testPiElectronegativity1() throws java.lang.Exception {
        double[] testResult = {5.1788, 5.465, 5.2475, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};/*
                                                                                          * from
                                                                                          * Petra
                                                                                          * online
                                                                                          * :
                                                                                          * http
                                                                                          * :
                                                                                          * /
                                                                                          * /
                                                                                          * www2
                                                                                          * .
                                                                                          * chemie
                                                                                          * .
                                                                                          * uni
                                                                                          * -
                                                                                          * erlangen
                                                                                          * .
                                                                                          * de
                                                                                          * /
                                                                                          * services
                                                                                          * /
                                                                                          * petra
                                                                                          * /
                                                                                          * smiles
                                                                                          * .
                                                                                          * phtml
                                                                                          */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("F[C+][C-]");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //	        logger.debug(mol.getAtomAt(i).getSymbol()+"-result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 2.0);
            }
        }
    }

    /**
     *  A unit test for JUnit with CCOCCCO
     */
    @Test
    void testPiElectronegativity2() throws java.lang.Exception {
        double[] testResult = {0.0, 0.0, 3.2849, 0.0, 0.0, 0.0, 3.2849, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0};/*
                                * from Petra online:
                                * http://www2.chemie.uni-erlangen
                                * .de/services/petra/smiles.phtml
                                */
        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCOCCCO");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //	        logger.debug(mol.getAtom(i).getSymbol()+"-result: "+result);
            if (result == 0.0)
                Assertions.assertEquals(testResult[i], result, 0.0001);
            else {
                Assertions.assertTrue(result != 0.0);
                Assertions.assertEquals(testResult[i], result, 0.2);
            }
        }
    }

    /**
     *  A unit test for JUnit with CCCCl # CCC[Cl+*]
     *
     *  @cdk.inchi InChI=1/C3H7Cl/c1-2-3-4/h2-3H2,1H3
     */
    @Test
    void testCompareIonized() throws java.lang.Exception {

        IAtomContainer molA = builder.newInstance(IAtomContainer.class);
        molA.addAtom(builder.newInstance(IAtom.class, "C"));
        molA.addAtom(builder.newInstance(IAtom.class, "C"));
        molA.addBond(0, 1, IBond.Order.SINGLE);
        molA.addAtom(builder.newInstance(IAtom.class, "C"));
        molA.addBond(1, 2, IBond.Order.SINGLE);
        molA.addAtom(builder.newInstance(IAtom.class, "Cl"));
        molA.addBond(2, 3, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molA);
        addExplicitHydrogens(molA);
        lpcheck.saturate(molA);

        double resultA = ((DoubleResult) descriptor.calculate(molA.getAtom(3), molA).getValue()).doubleValue();

        IAtomContainer molB = builder.newInstance(IAtomContainer.class);
        molB.addAtom(builder.newInstance(IAtom.class, "C"));
        molB.addAtom(builder.newInstance(IAtom.class, "C"));
        molB.addBond(0, 1, IBond.Order.SINGLE);
        molB.addAtom(builder.newInstance(IAtom.class, "C"));
        molB.addBond(1, 2, IBond.Order.SINGLE);
        molB.addAtom(builder.newInstance(IAtom.class, "Cl"));
        molB.getAtom(3).setFormalCharge(1);
        molB.addSingleElectron(3);
        molB.addLonePair(3);
        molB.addLonePair(3);
        molB.addBond(2, 3, IBond.Order.SINGLE);

        addExplicitHydrogens(molB);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molB);
        lpcheck.saturate(molB);

        Assertions.assertEquals(1, molB.getAtom(3).getFormalCharge(), 0.00001);
        Assertions.assertEquals(1, molB.getSingleElectronCount(), 0.00001);
        Assertions.assertEquals(2, molB.getLonePairCount(), 0.00001);

        IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        double resultB = ((DoubleResult) descriptor.calculate(molB.getAtom(3), molB).getValue()).doubleValue();

        Assertions.assertEquals(resultA, resultB, 0.00001);
    }

}
