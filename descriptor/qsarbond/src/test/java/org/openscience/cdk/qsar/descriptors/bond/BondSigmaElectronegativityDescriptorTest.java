/* Copyright (C) 2004-2008  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *                          Egon Willighagen <egonw@users.sf.net>
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
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarbond
 */
class BondSigmaElectronegativityDescriptorTest extends BondDescriptorTest {

    BondSigmaElectronegativityDescriptorTest() {
        descriptor = new BondSigmaElectronegativityDescriptor();
    }

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(BondSigmaElectronegativityDescriptor.class);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testBondSigmaElectronegativityDescriptor() throws java.lang.Exception {
        double[] testResult = {2.5882, 1.1894};/*
                                                * from Petra online:
                                                * http://www2.
                                                * chemie.uni-erlangen
                                                * .de/services
                                                * /petra/smiles.phtml
                                                */
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CF");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(mol);

        for (int i = 0; i < 2; i++) {
            params[0] = 6;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assertions.assertEquals(testResult[i], result, 0.01);
        }

    }

    /**
     *  A unit test for JUnit with Methyl chloride
     */
    @Test
    void testBondSigmaElectronegativityDescriptor_Methyl_chloride() throws java.lang.Exception {
        double[] testResult = {2.1612, 0.8751};/*
                                                * from Petra online:
                                                * http://www2.
                                                * chemie.uni-erlangen
                                                * .de/services
                                                * /petra/smiles.phtml
                                                */

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCl");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        for (int i = 0; i < 2; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assertions.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit with Allyl bromide
     */
    @Test
    void testBondSigmaElectronegativityDescriptor_Allyl_bromide() throws java.lang.Exception {
        double[] testResult = {0.2396, 0.3635, 1.7086, 0.3635, 0.338, 0.574, 0.969, 0.969}; /*
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

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCBr");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        for (int i = 0; i < 8; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assertions.assertEquals(testResult[i], result, 0.03);
        }
    }

    /**
     *  A unit test for JUnit with Isopentyl iodide
     */
    @Test
    void testBondSigmaElectronegativityDescriptor_Isopentyl_iodide() throws java.lang.Exception {
        double testResult = 0.1482; /*
                                     * from Petra online:
                                     * http://www2.chemie.uni-
                                     * erlangen.de/services/petra/smiles.phtml
                                     */

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(C)(C)CCI");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        Assertions.assertEquals(testResult, result, 0.001);
    }

    /**
     *  A unit test for JUnit with Ethoxy ethane
     */
    @Test
    void testBondSigmaElectronegativityDescriptor_Ethoxy_ethane() throws java.lang.Exception {
        double[] testResult = {0.7939, 1.0715, 1.0715, 0.7939, 0.2749, 0.2749, 0.2749, 0.8796, 0.8796}; /*
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

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCOCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        for (int i = 0; i < 8; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assertions.assertEquals(testResult[i], result, 0.002);
        }
    }

    /**
     *  A unit test for JUnit with Ethanolamine
     */
    @Test
    void testBondSigmaElectronegativityDescriptor_Ethanolamine() throws java.lang.Exception {
        double[] testResult = {0.0074, 0.3728, 0.8547, 0.2367, 0.2367}; /*
                                                                         * from
                                                                         * Petra
                                                                         * online
                                                                         * :
                                                                         * http
                                                                         * :/
                                                                         * /www2
                                                                         * .
                                                                         * chemie
                                                                         * .uni-
                                                                         * erlangen
                                                                         * .de/
                                                                         * services
                                                                         * /
                                                                         * petra
                                                                         * /
                                                                         * smiles
                                                                         * .
                                                                         * phtml
                                                                         */

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NCCO");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        for (int i = 0; i < 5; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assertions.assertEquals(testResult[i], result, 0.06);
        }
    }

    /**
     *  A unit test for JUnit with Allyl mercaptan
     */
    @Test
    void testBondSigmaElectronegativityDescriptor_Allyl_mercaptan() throws java.lang.Exception {
        double[] testResult = {0.1832, 0.0143, 0.5307, 0.3593, 0.3593, 8.5917}; /*
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

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCS");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        for (int i = 0; i < 4; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assertions.assertEquals(testResult[i], result, 0.005);
        }
    }
}
