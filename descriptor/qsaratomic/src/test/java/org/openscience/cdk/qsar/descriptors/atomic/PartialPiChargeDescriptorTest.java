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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
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
public class PartialPiChargeDescriptorTest extends AtomicDescriptorTest {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    LonePairElectronChecker                 lpcheck = new LonePairElectronChecker();

    /**
     *  Constructor for the PartialPiChargeDescriptorTest object
     *
     */
    public PartialPiChargeDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(PartialPiChargeDescriptor.class);
    }

    /**
     *  A unit test for JUnit with Ethyl Fluoride
     *
     *  @cdk.inchi InChI=1/CH3F/c1-2/h1H3
     */
    @Test
    @Category(SlowTest.class)
    public void testPartialPiChargeDescriptor_Methyl_Fluoride() throws
            Exception {
        double[] testResult = {0.0, 0.0, 0.0, 0.0, 0.0};/*
                                                         * from Petra online:
                                                         * http
                                                         * ://www2.chemie.uni
                                                         * -erlangen
                                                         * .de/services/
                                                         * petra/smiles.phtml
                                                         */
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("F"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.0001);
        }
    }

    /**
     *  A unit test for JUnit with Fluoroethylene
     *
     *  @cdk.inchi InChI=1/C2H3F/c1-2-3/h2H,1H2
     *  @cdk.bug   1959099
     */
    @Test
    @Category(SlowTest.class)
    public void testPartialPiChargeDescriptor_Fluoroethylene() throws
            Exception {
        double[] testResult = {0.0299, 0.0, -0.0299, 0.0, 0.0, 0.0};/*
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
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit with Formic Acid
     *
     *  @cdk.inchi  InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)/f/h2H
     */
    @Test
    @Category(SlowTest.class)
    public void testPartialPiChargeDescriptor_FormicAcid() throws
            Exception {
        double[] testResult = {0.0221, -0.1193, 0.0972, 0.0, 0.0};/*
                                                                   * from Petra
                                                                   * online:
                                                                   * http
                                                                   * ://www2.
                                                                   * chemie
                                                                   * .uni-erlangen
                                                                   * .
                                                                   * de/services
                                                                   * /
                                                                   * petra/smiles
                                                                   * .phtml
                                                                   */
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(0, 2, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);

        }
    }

    /**
     *  A unit test for JUnit with Fluorobenzene
     *
     *  @cdk.inchi InChI=1/C6H5F/c7-6-4-2-1-3-5-6/h1-5H
     */
    @Test
    @Category(SlowTest.class)
    public void testPartialPiChargeDescriptor_Fluorobenzene() throws
            Exception {
        double[] testResult = {0.0262, 0.0, -0.0101, 0.0, -0.006, 0.0, -0.0101, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};/*
                                                              * from Petra
                                                              * online:
                                                              * http://www2
                                                              * .chemie
                                                              * .uni-erlangen
                                                              * .de/
                                                              * services/petra
                                                              * /smiles.phtml
                                                              */
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        Integer[] params = new Integer[1];
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addBond(6, 1, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            params[0] = 6;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit with Methoxyethylene
     *
     *  @cdk.inchi InChI=1/C3H6O/c1-3-4-2/h3H,1H2,2H3
     *  @cdk.bug   1959099
     */
    @Test
    @Category(SlowTest.class)
    public void testPartialPiChargeDescriptor_Methoxyethylene() throws
            Exception {
        double[] testResult = {-0.044, 0.0, 0.044, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};/*
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
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        Integer[] params = new Integer[1];

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < 4/* mol.getAtomCount() */; i++) {
            params[0] = 6;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit with 1-Methoxybutadiene
     *
     *  @cdk.inchi InChI=1/C5H8O/c1-3-4-5-6-2/h3-5H,1H2,2H3
     *  @cdk.bug   1959099
     */
    @Test
    @Category(SlowTest.class)
    public void testPartialPiChargeDescriptor_1_Methoxybutadiene() throws Exception {
        double[] testResult = {-0.0333, 0.0, -0.0399, 0.0, 0.0733, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};/*
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
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();
            Assert.assertEquals(testResult[i], result, 0.3);
        }
    }

    /**
     * get the sign of a value
     */
    private double getSign(double d) {
        double sign = 0.0;
        if (d > 0)
            sign = 1;
        else if (d < 0) sign = -1;
        return sign;
    }

    /**
     *  A unit test for JUnit
     *  @cdk.bug   1959099
     */
    @Test
    @Category(SlowTest.class)
    public void testPartialPiChargeDescriptoCharge_1() throws Exception {
        double[] testResult = {0.0613, -0.0554, 0.0, -0.0059, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};/*
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
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("F[C+]([H])[C-]([H])[H]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        lpcheck.saturate(mol);

        for (int i = 0; i < 6; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.2);
        }
    }

    /**
     *  A unit test for JUnit : n1ccccc1
     *
     *  @cdk.inchi InChI: InChI=1/C5H5N/c1-2-4-6-5-3-1/h1-5H
     *  @cdk.bug   1959099
     */
    @Test
    @Category(SlowTest.class)
    public void testPartialPiChargeDescriptoCharge_2() throws Exception {
        double[] testResult = {-0.0822, 0.02, 0.0, 0.0423, 0.0, 0.02, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};/*
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
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();

        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "N"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(2, 3, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(3, 4, Order.DOUBLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(4, 5, Order.SINGLE);
        mol.addBond(5, 0, Order.DOUBLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit. This molecule breaks with PETRA as well.
     *  @cdk.bug   1959099
     */
    @Ignore("Bug was always present - and is not a regression. The non-charge seperated form of molecule produces the correct result.")
    public void testPartialPiChargeDescriptoCharge_3() throws Exception {
        double[] testResult = {-0.0379, -0.0032, 0.0, -0.0078, 0.0, 0.0488, 0.0, 0.0};/*
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
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C([H])[C+]([H])[C-]([H])[H]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit. This molecule breaks with PETRA as well.
     *
     *  @cdk.inchi InChI: InChI=1/C5H12O2/c1-2-7-5-3-4-6/h6H,2-5H2,1H3
     */
    @Test
    @Category(SlowTest.class)
    public void testPartialPiChargeDescripto4() throws Exception {
        double[] testResult = {0.0};/*
                                     * from Petra online:
                                     * http://www2.chemie.uni-
                                     * erlangen.de/services/petra/smiles.phtml
                                     */
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCOCCCO");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);

        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[0], result, 0.0001);
        }
    }

    /**
     *  A unit test for JUnit with
     *
     *  @cdk.inchi InChI=1/C2H5NO/c1-2(3)4/h1H3,(H2,3,4)/f/h3H2
     */
    @Test
    @Category(SlowTest.class)
    public void testArticle1() throws Exception {
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0, 0.0216, -0.1644, 0.1428, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}; /*
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
        IAtomContainer mol = sp.parseSmiles("CC(=O)N");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        Object[] object = {6, true};
        descriptor.setParameters(object);

        lpcheck.saturate(mol);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit with [H]C1=C([H])C([H])=C(C(=C1(F))C([H])([H])[H])C([H])([H])C([H])([H])C(F)=O
     *  @cdk.bug   1959099
     *
     */
    @Test
    @Category(SlowTest.class)
    public void testSousa() throws Exception {
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0914, 0.0193, -0.1107, 0.0, 0.0, 0.0, -0.0063, 0.0, -0.0101, 0.0, 0.0262, -0.0098,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}; /*
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
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "F"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "O"));
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(1, 3, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(3, 4, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//ring
        mol.addBond(4, 5, Order.SINGLE);
        //aromatic
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//ring
        mol.addBond(5, 6, Order.DOUBLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//ring
        mol.addBond(6, 7, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//ring
        mol.addBond(7, 8, Order.DOUBLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//ring
        mol.addBond(8, 9, Order.SINGLE);
        //Fluor
        mol.addAtom(builder.newInstance(IAtom.class, "F"));
        mol.addBond(9, 10, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//ring
        mol.addBond(9, 11, Order.DOUBLE);
        mol.addBond(5, 11, Order.SINGLE);

        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(11, 12, Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        Object[] object = {6, true};
        descriptor.setParameters(object);

        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.15);
        }
    }

    /**
     *  A unit test for JUnit with [H]C([H])=C([H])C([H])([H])C([H])=O
     *
     *  @cdk.inchi  InChI=1/C4H6O/c1-2-3-4-5/h2,4H,1,3H2
     *  @cdk.bug   1959099
     */
    @Test
    @Category(SlowTest.class)
    public void testBondNotConjugated() throws Exception {
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0, 0.0004, 0.0, -0.0004, 0.0, 0.0, 0.0, 0.0, 0.0277, 0.0, -0.0277}; /*
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
        IAtomContainer mol = sp.parseSmiles("[H]C([H])=C([H])C([H])([H])C([H])=O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Object[] object = {6, true};
        descriptor.setParameters(object);

        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit with [H]C([H])=C([H])C([H])([H])C([H])=O
     *
     *  @cdk.inchi InChI=1/C4H6O/c1-2-3-4-5/h2,4H,1,3H2
     */
    @Test
    @Category(SlowTest.class)
    public void testDifferentStarts() throws  Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles("C=CCC=O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        addExplicitHydrogens(mol1);
        lpcheck.saturate(mol1);

        IAtomContainer mol2 = sp.parseSmiles("O=CCC=C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        addExplicitHydrogens(mol2);
        lpcheck.saturate(mol2);

        IAtomicDescriptor descriptor1 = new PartialPiChargeDescriptor();
        IAtomicDescriptor descriptor2 = new PartialPiChargeDescriptor();

        for (int i = 0; i < 5; i++) {
            double result1 = ((DoubleResult) descriptor1.calculate(mol1.getAtom(i), mol1).getValue()).doubleValue();
            double result2 = ((DoubleResult) descriptor2.calculate(mol2.getAtom(5 - i - 1), mol2).getValue())
                    .doubleValue();
            Assert.assertFalse(Double.isNaN(result1));
            Assert.assertFalse(Double.isNaN(result2));
            Assert.assertEquals(result1, result2, 0.0001);
        }
    }

    /**
     *  A unit test for JUnit with [H]C([H])=C([H])C([H])([H])[H]
     *
     *  @cdk.inchi  InChI=1/C3H6/c1-3-2/h3H,1H2,2H3
     */
    @Test
    @Category(SlowTest.class)
    public void testBondNotConjugated1() throws Exception {
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0, -0.0009, 0.0, 0.0009, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}; /*
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
        IAtomContainer mol = sp.parseSmiles("[H]C([H])=C([H])C([H])([H])[H]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Object[] object = {6, true};
        descriptor.setParameters(object);

        lpcheck.saturate(mol);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.02);
        }
    }

    /**
     *  A unit test for JUnit with [H]C([H])=C([H])[C+]([H])[H]
     *  @cdk.bug   1959099
     */
    @Test
    @Category(SlowTest.class)
    public void testBondNotConjugated2() throws Exception {
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0, 0.25, 0.0, 0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,}; /*
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
        IAtomContainer mol = sp.parseSmiles("[H]C([H])=C([H])[C+]([H])[H]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Object[] object = {6, true};
        descriptor.setParameters(object);

        lpcheck.saturate(mol);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.29);
        }
    }

    /**
     *  A unit test for JUnit with c1ccc(cc1)n3c4ccccc4(c2ccccc23)
     *  @cdk.bug   1959099
     *
     *  @cdk.inchi
     */
    @Test
    @Category(SlowTest.class)
    public void testLangCalculation() throws Exception {
        IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1ccc(cc1)n3c4ccccc4(c2ccccc23)");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        Object[] object = {6, true};
        descriptor.setParameters(object);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertFalse(Double.isNaN(result));
        }
    }
}
