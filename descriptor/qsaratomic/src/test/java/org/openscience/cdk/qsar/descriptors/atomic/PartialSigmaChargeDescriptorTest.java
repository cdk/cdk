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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
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
public class PartialSigmaChargeDescriptorTest extends AtomicDescriptorTest {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    LonePairElectronChecker                 lpcheck = new LonePairElectronChecker();

    /**
     *  Constructor for the PartialSigmaChargeDescriptorTest object
     *
     */
    public PartialSigmaChargeDescriptorTest() {}

    /**
     *  A unit test suite for JUnit
     *
     *@return    The test suite
     */

    @Before
    public void setUp() throws Exception {
        setDescriptor(PartialSigmaChargeDescriptor.class);
        Integer[] params = {6};
        descriptor.setParameters(params);
    }

    /**
     *  A unit test for JUnit with Fluoroethylene
     *
     *  @cdk.inchi InChI=1/C2H3F/c1-2-3/h2H,1H2
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Fluoroethylene() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {-0.2138, 0.079, 0.0942, -0.072, 0.0563, 0.0563};/*
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

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 3, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(3, 5, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.003);
        }
    }

    /**
     *  A unit test for JUnit with Ethyl Fluoride
     *
     *  @cdk.inchi InChI=1/CH3F/c1-2/h1H3
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Methyl_Floride() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {0.07915, -0.25264, 0.05783, 0.05783, 0.05783};/*
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

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("F"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        addExplicitHydrogens(molecule);
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.001);
        }
    }

    /**
     *  A unit test for JUnit with Methyl chloride
     *
     *  @cdk.inchi  InChI=1/CH3Cl/c1-2/h1H3
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Methyl_chloride() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {0.0382, -0.1755, 0.0457, 0.0457, 0.0457};/*
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

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("Cl"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.001);
        }
    }

    /**
     *  A unit test for JUnit with Methyl chloride
     *
     *  @cdk.inchi  InChI=1/CH3Br/c1-2/h1H3
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Methyl_bromide() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {0.021, -0.1448, 0.0413, 0.0413, 0.0413};/*
                                                                        * from
                                                                        * Petra
                                                                        * online
                                                                        * :
                                                                        * http:
                                                                        * //
                                                                        * www2.
                                                                        * chemie
                                                                        * .uni-
                                                                        * erlangen
                                                                        * .de/
                                                                        * services
                                                                        * /
                                                                        * petra/
                                                                        * smiles
                                                                        * .phtml
                                                                        */

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("Br"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }
    }

    /**
     *  A unit test for JUnit with Methyl iodide
     *
     *  @cdk.inchi
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Methyl_iodide() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {-0.0116, -0.0892, 0.0336, 0.0336, 0.0336};/*
                                                                          * from
                                                                          * Petra
                                                                          * online
                                                                          * :
                                                                          * http
                                                                          * :
                                                                          * //www2
                                                                          * .
                                                                          * chemie
                                                                          * .
                                                                          * uni-
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

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("I"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.001);
        }
    }

    /**
     *  A unit test for JUnit with Allyl bromide
     *
     *  @cdk.inchi  InChI=1/C3H5Br/c1-2-3-4/h2H,1,3H2
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Allyl_bromide() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double testResult = -0.1366;/*
                                     * from Petra online:
                                     * http://www2.chemie.uni-
                                     * erlangen.de/services/petra/smiles.phtml
                                     */

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "Br"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(3), molecule).getValue()).doubleValue();
        Assert.assertEquals(testResult, result, 0.01);
    }

    /**
     *  A unit test for JUnit with Isopentyl iodide
     *
     *  @cdk.inchi  InChI=1/C5H11I/c1-5(2)3-4-6/h5H,3-4H2,1-2H3
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Isopentyl_iodide() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {-0.0458, -0.0623, -0.0623, -0.0415, 0.0003, -0.0855}; /*
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
        IAtomContainer mol = sp.parseSmiles("C(C)(C)CCI");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        for (int i = 0; i < 6; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.001);
        }
    }

    /**
     *  A unit test for JUnit with Ethoxy ethane
     *
     *  @cdk.inchi  InChI=1/C4H10O/c1-3-5-4-2/h3-4H2,1-2H3
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Ethoxy_ethane() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double testResult = -0.3809; /*
                                      * from Petra online:
                                      * http://www2.chemie.uni
                                      * -erlangen.de/services/petra/smiles.phtml
                                      */

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(2), molecule).getValue()).doubleValue();
        Assert.assertEquals(testResult, result, 0.01);
    }

    /**
     *  A unit test for JUnit with Ethanolamine
     *
     *  @cdk.inchi  InChI=1/C2H7NO/c3-1-2-4/h4H,1-3H2
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Ethanolamine() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {-0.3293, 0.017, 0.057, -0.3943}; /*
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

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < 4; i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }
    }

    /**
     *  A unit test for JUnit with Allyl mercaptan
     *
     *  @cdk.inchi  InChI=1/C3H6S/c1-2-3-4/h2,4H,1,3H2
     */
    @Test
    public void testPartialSigmaChargeDescriptor_Allyl_mercaptan() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {-0.1031, -0.0828, 0.0093, -0.1742}; /*
                                                                    * from Petra
                                                                    * online:
                                                                    * http
                                                                    * ://www2
                                                                    * .chemie
                                                                    * .uni
                                                                    * -erlangen
                                                                    * .de
                                                                    * /services
                                                                    * /petra
                                                                    * /smiles
                                                                    * .phtml
                                                                    */

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "S"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < 4; i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }
    }

    /**
     *  A unit test for JUnit with
     */
    @Test
    public void testPartialSigmaChargeDescriptor1() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double[] testResult = {-0.2138, 0.079, 0.0942, -0.072, 0.0563, 0.0563}; /*
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
        IAtomContainer mol = sp.parseSmiles("[F+]=C([H])[C-]([H])[H]");

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.003);
        }
    }

    /**
     *  A unit test for JUnit with
     */
    @Test
    public void testPartialSigmaChargeDescriptor2() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double[] testResult = {-0.3855, -0.0454, 0.0634, -0.0544, -0.0391, -0.0391}; /*
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
        IAtomContainer mol = sp.parseSmiles("O=C([H])[C-]([H])[H]");
        Integer[] object = {6};
        descriptor.setParameters(object);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.2);
        }
    }

    /**
     *  A unit test for JUnit with
     */
    @Test
    public void testPartialSigmaChargeDescriptor3() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double[] testResult = {-0.3855, -0.0454, 0.0634, -0.0544, -0.0391, -0.0391}; /*
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
        IAtomContainer mol = sp.parseSmiles("[O-]C([H])=C([H])[H]");

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.2);
        }
    }

    /**
     *  A unit test for JUnit with
     *
     *  @cdk.inchi  InChI=1/CH2O/c1-2/h1H2
     */
    @Test
    public void testPartialSigmaChargeDescriptor4() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double[] testResult = {-0.3041, 0.1055, 0.0993, 0.0993}; /*
                                                                  * from Petra
                                                                  * online:
                                                                  * http:
                                                                  * //www2.chemie
                                                                  * .
                                                                  * uni-erlangen
                                                                  * .
                                                                  * de/services/
                                                                  * petra
                                                                  * /smiles.
                                                                  * phtml
                                                                  */

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 3, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.003);
        }
    }

    /**
     *  A unit test for JUnit with
     *
     *  @cdk.inchi InChI=1/C2H4O/c1-2-3/h2H,1H3
     */
    @Test
    public void testPartialSigmaChargeDescriptor5() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double[] testResult = {-0.3291, 0.144, 0.1028, -0.0084, 0.0303, 0.0303, 0.0303}; /*
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

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(3, 5, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        Integer[] object = {6};
        descriptor.setParameters(object);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.03);
        }
    }

    /**
     *  A unit test for JUnit with
     */
    @Test
    public void testPartialSigmaChargeDescriptor6() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double[] testResult = {-0.4331, -0.1067, 0.0133, 0.0133, 0.0133}; /*
                                                                           * from
                                                                           * Petra
                                                                           * online
                                                                           * :
                                                                           * http
                                                                           * :
                                                                           * //www2
                                                                           * .
                                                                           * chemie
                                                                           * .
                                                                           * uni
                                                                           * -
                                                                           * erlangen
                                                                           * .
                                                                           * de/
                                                                           * services
                                                                           * /
                                                                           * petra
                                                                           * /
                                                                           * smiles
                                                                           * .
                                                                           * phtml
                                                                           */
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[O-]C([H])([H])[H]");
        Integer[] object = {6};
        descriptor.setParameters(object);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //			logger.debug(mol.getAtom(i).getSymbol()+",result: "+result);
            Assert.assertEquals(testResult[i], result, 0.3);
        }
    }

    /**
     *  A unit test for JUnit with [H]c1[n-][c+]([H])c([H])c([H])c1([H])
     */
    @Test
    public void testPartialSigmaChargeDescriptor7() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double[] testResult = {0.0835, 0.0265, -0.2622, 0.0265, 0.0835, -0.0444, 0.064, -0.0596, 0.0626, -0.0444, 0.064}; /*
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
        sp.kekulise(false);
        IAtomContainer mol = sp.parseSmiles("[H]c1[n-][c+]([H])c([H])c([H])c1([H])");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Integer[] object = {6};
        descriptor.setParameters(object);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            //			logger.debug(mol.getAtom(i).getSymbol()+",result: "+result);
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }
}
