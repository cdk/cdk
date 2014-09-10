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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarbond
 */
public class BondPartialSigmaChargeDescriptorTest extends BondDescriptorTest {

    public BondPartialSigmaChargeDescriptorTest() {
        descriptor = new BondPartialSigmaChargeDescriptor();
    }

    @Before
    public void setUp() throws Exception {
        setDescriptor(BondPartialSigmaChargeDescriptor.class);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testBondSigmaElectronegativityDescriptor() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {0.3323, 0.0218};/*
                                                * from Petra online:
                                                * http://www2.
                                                * chemie.uni-erlangen
                                                * .de/services
                                                * /petra/smiles.phtml
                                                */

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CF");
        addExplicitHydrogens(mol);

        for (int i = 0; i < 2; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }

    }

    /**
     *  A unit test for JUnit with Methyl chloride
     */
    @Test
    public void testBondSigmaElectronegativityDescriptor_Methyl_chloride() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {0.2137, 0.0075};/*
                                                * from Petra online:
                                                * http://www2.
                                                * chemie.uni-erlangen
                                                * .de/services
                                                * /petra/smiles.phtml
                                                */

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCl");
        addExplicitHydrogens(mol);
        for (int i = 0; i < 2; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit with Allyl bromide
     */
    @Test
    public void testBondSigmaElectronegativityDescriptor_Allyl_bromide() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {0.0265, 0.1268, 0.1872, 0.1564, 0.1564, 0.1347, 0.0013, 0.0013}; /*
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
        addExplicitHydrogens(mol);

        for (int i = 0; i < 8; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.03);
        }
    }

    /**
     *  A unit test for JUnit with Isopentyl iodide
     */
    @Test
    public void testBondSigmaElectronegativityDescriptor_Isopentyl_iodide() throws ClassNotFoundException,
            CDKException, java.lang.Exception {
        double testResult = 0.0165; /*
                                     * from Petra online:
                                     * http://www2.chemie.uni-
                                     * erlangen.de/services/petra/smiles.phtml
                                     */

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(C)(C)CCI");
        addExplicitHydrogens(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getBond(0), mol).getValue()).doubleValue();
        Assert.assertEquals(testResult, result, 0.001);
    }

    /**
     *  A unit test for JUnit with Ethoxy ethane
     */
    @Test
    public void testBondSigmaElectronegativityDescriptor_Ethoxy_ethane() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {0.0864, 0.4262, 0.4262, 0.0864, 0.0662, 0.0662, 0.0662, 0.0104, 0.0104}; /*
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
        addExplicitHydrogens(mol);

        for (int i = 0; i < 8; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.002);
        }
    }

    /**
     *  A unit test for JUnit with Ethanolamine
     */
    @Test
    public void testBondSigmaElectronegativityDescriptor_Ethanolamine() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {0.3463, 0.0274, 0.448, 0.448, 0.448}; /*
                                                                      * from
                                                                      * Petra
                                                                      * online:
                                                                      * http
                                                                      * ://www2
                                                                      * .chemie
                                                                      * .uni
                                                                      * -erlangen
                                                                      * .
                                                                      * de/services
                                                                      * /
                                                                      * petra/smiles
                                                                      * .phtml
                                                                      */

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NCCO");
        addExplicitHydrogens(mol);

        for (int i = 0; i < 5; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.06);
        }
    }

    /**
     *  A unit test for JUnit with Allyl mercaptan
     */
    @Test
    public void testBondSigmaElectronegativityDescriptor_Allyl_mercaptan() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {0.0203, 0.0921, 0.1835, 0.1569, 0.3593, 8.5917}; /*
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
        addExplicitHydrogens(mol);

        for (int i = 0; i < 4; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getBond(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.005);
        }
    }
}
