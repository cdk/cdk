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
import org.junit.jupiter.api.Tag;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.diff.AtomContainerDiff;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 */
class PartialTChargePEOEDescriptorTest extends AtomicDescriptorTest {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    /**
     * Constructor for the PartialTChargePEOEDescriptorTest object
     *
     */
    PartialTChargePEOEDescriptorTest() {
    }

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(PartialTChargePEOEDescriptor.class);
    }

    /**
     * A unit test for JUnit with Ethyl Fluoride
     *
     * @cdk.inchi InChI=1/CH3F/c1-2/h1H3
     */
    @Test
    @Tag("SlowTest")
    void testPartialTChargeDescriptor_Methyl_Fluoride() throws java.lang.Exception {
        double[] testResult = { -0.2527, 0.0795, 0.0577, 0.0577, 0.0577 };/*
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
        IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        lpcheck.saturate(molecule);

        addExplicitHydrogens(molecule);
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();

            Assertions.assertEquals(testResult[i], result, 0.01);
        }
    }

    /**
     * A unit test for JUnit with Fluoroethylene
     */
    @Test
    @Tag("SlowTest")
    void testPartialTChargeDescriptor_Fluoroethylene() throws java.lang.Exception {
        double[] testResult = { -0.1839, 0.079, -0.1019, 0.0942, 0.0563, 0.0563 };/*
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
        IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("F-C=C");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();

            Assertions.assertEquals(testResult[i], result, 0.04);
        }
    }

    /**
     * A unit test for JUnit with Formic Acid
     *
     * @cdk.inchi InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)/f/h2H
     */
    @Test
    @Tag("SlowTest")
    void testPartialTChargeDescriptor_FormicAcid() throws java.lang.Exception {
        double[] testResult = { 0.2672, -0.3877, -0.2365, 0.1367, 0.2203 };/*
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
        IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();

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

            Assertions.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     * A unit test for JUnit with Fluorobenzene
     *
     * @cdk.inchi InChI=1/C6H5F/c7-6-4-2-1-3-5-6/h1-5H
     */
    @Test
    @Tag("SlowTest")
    void testPartialTChargeDescriptor_Fluorobenzene() throws java.lang.Exception {
        double[] testResult = { -0.1785, 0.1227, -0.0373, -0.0598, -0.0683 };/*
                                                                              * from
                                                                              * Petra
                                                                              * online
                                                                              * :
                                                                              * http
                                                                              * :
                                                                              * //
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
        IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();

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

        for (int i = 0; i < 5; i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();

            Assertions.assertEquals(testResult[i], result, 0.012);
        }
    }

    /**
     * A unit test for JUnit with Methoxyethylene
     *
     * @cdk.inchi InChI=1/C3H6O/c1-3-4-2/h3H,1H2,2H3
     */
    @Test
    @Tag("SlowTest")
    void testPartialTChargeDescriptor_Methoxyethylene() throws java.lang.Exception {
        double[] testResult = { -0.1211, 0.0314, -0.3121, 0.0429, 0.056, 0.056, 0.0885, 0.056, 0.056, 0.056 };/*
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
        IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();

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

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            double result = ((DoubleResult) descriptor.calculate(molecule.getAtom(i), molecule).getValue())
                    .doubleValue();

            Assertions.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     * A unit test for JUnit with 1-Methoxybutadiene
     *
     * @cdk.inchi InChI=1/C5H8O/c1-3-4-5-6-2/h3-5H,1H2,2H3
     */
    @Test
    @Tag("SlowTest")
    void testPartialTChargeDescriptor_1_Methoxybutadiene() throws java.lang.Exception {
        double[] testResult = { -0.1331, -0.0678, -0.0803, 0.0385, -0.2822, 0.0429, 0.0541, 0.0541, 0.0619, 0.0644,
                0.0891, 0.0528, 0.0528, 0.0528, 0.0528 };/*
                                                          * from Petra online:
                                                          * http
                                                          * ://www2.chemie.uni
                                                          * -erlangen
                                                          * .de/services/
                                                          * petra/smiles.phtml
                                                          */
        IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();

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

            Assertions.assertEquals(testResult[i], result, 0.3);
        }
    }

    @Test
    void testDoesNotMutateInputAtomContainer() throws Exception {
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("[H]C([H])=C([H])F");
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(0, mol.getLonePairCount());
        assertUnconfiguredAndUncharged(mol);

        IAtomContainer snapshot = mol.clone();
        IAtomContainer saturated = mol.clone();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(saturated);
        lpcheck.saturate(saturated);

        PartialTChargePEOEDescriptor firstDescriptor = new PartialTChargePEOEDescriptor();
        double[] first = new double[mol.getAtomCount()];
        for (int i = 0; i < mol.getAtomCount(); i++) {
            first[i] = ((DoubleResult) firstDescriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
        }
        String diffAfterCall = AtomContainerDiff.diff(snapshot, mol);
        int lonePairsAfterCall = mol.getLonePairCount();
        assertUnconfiguredAndUncharged(mol);

        PartialTChargePEOEDescriptor secondDescriptor = new PartialTChargePEOEDescriptor();
        PartialTChargePEOEDescriptor saturatedDescriptor = new PartialTChargePEOEDescriptor();

        boolean hasNonZeroCharge = false;
        for (int i = 0; i < mol.getAtomCount(); i++) {
            double cached = ((DoubleResult) firstDescriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            double second = ((DoubleResult) secondDescriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            double expected = ((DoubleResult) saturatedDescriptor.calculate(saturated.getAtom(i), saturated).getValue())
                    .doubleValue();
            Assertions.assertEquals(first[i], cached, 1e-9, "Cached calculation mismatch at atom " + i);
            Assertions.assertEquals(first[i], second, 1e-9, "Repeated calculation mismatch at atom " + i);
            Assertions.assertEquals(expected, first[i], 1e-9, "Pre-saturated comparison mismatch at atom " + i);
            if (Math.abs(first[i]) > 1e-4) {
                hasNonZeroCharge = true;
            }
        }
        Assertions.assertTrue(hasNonZeroCharge, "Expected non-zero total partial charges for [H]C([H])=C([H])F");
        Assertions.assertEquals(0, lonePairsAfterCall, "Input lone pairs should remain 0");
        Assertions.assertEquals("", diffAfterCall, "Input IAtomContainer must not be mutated");
    }

    @Test
    void testPreservesExistingAtomAndContainerState() throws Exception {
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("FC1=C([H])C([H])=C([H])C([H])=C1[H]");
        IAtom fAtom = mol.getAtom(0);
        Assertions.assertEquals("F", fAtom.getSymbol());

        fAtom.setCharge(0.42);
        fAtom.setAtomTypeName("CUSTOM_TYPE");
        fAtom.setID("atom-f");
        fAtom.set(IChemObject.VISITED | IChemObject.PLACED);
        fAtom.setProperty("custom.key", "custom.val");
        mol.getBond(0).set(IChemObject.PLACED | IChemObject.REACTIVE_CENTER);
        mol.set(IChemObject.AROMATIC);
        mol.setProperty("mol.key", "mol.val");
        ILonePair existingLp = builder.newInstance(ILonePair.class, fAtom);
        mol.addLonePair(existingLp);

        IAtomContainer before = mol.clone();
        IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
        DescriptorValue value = descriptor.calculate(fAtom, mol);

        Assertions.assertNotNull(value);
        Assertions.assertFalse(Double.isNaN(((DoubleResult) value.getValue()).doubleValue()));
        Assertions.assertEquals("", AtomContainerDiff.diff(before, mol), "Input IAtomContainer must not be mutated");
        Assertions.assertEquals(0.42, fAtom.getCharge(), 1e-9);
        Assertions.assertEquals("CUSTOM_TYPE", fAtom.getAtomTypeName());
        Assertions.assertEquals("atom-f", fAtom.getID());
        Assertions.assertTrue(fAtom.is(IChemObject.VISITED | IChemObject.PLACED));
        Assertions.assertFalse(fAtom.getFlag(IChemObject.HYDROGEN_BOND_ACCEPTOR));
        Assertions.assertEquals("custom.val", fAtom.getProperty("custom.key"));
        Assertions.assertNull(fAtom.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT));
        Assertions.assertNull(fAtom.getProperty("org.openscience.cdk.renderer.color"));
        Assertions.assertTrue(mol.getBond(0).is(IChemObject.PLACED | IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(mol.getFlag(IChemObject.AROMATIC));
        Assertions.assertEquals("mol.val", mol.getProperty("mol.key"));
        Assertions.assertEquals(1, mol.getLonePairCount());
        Assertions.assertSame(existingLp, mol.getLonePair(0));
    }

    @Test
    void testCalculateReturnsNaNDescriptorValueOnException() throws Exception {
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer seMol = sp.parseSmiles("C[Se]C");
        IAtom seAtom = seMol.getAtom(1);
        seAtom.setCharge(0.25);
        seAtom.setAtomTypeName("CUSTOM_SE");
        seAtom.setID("atom-se");
        seAtom.set(IChemObject.VISITED | IChemObject.PLACED);
        seAtom.setProperty("custom.key", "custom.val");
        seMol.getBond(0).set(IChemObject.PLACED | IChemObject.REACTIVE_CENTER);
        seMol.set(IChemObject.AROMATIC);
        seMol.setProperty("mol.key", "mol.val");
        IAtomContainer seBefore = seMol.clone();

        IAtomicDescriptor seDescriptor = new PartialTChargePEOEDescriptor();
        DescriptorValue seValue = seDescriptor.calculate(seAtom, seMol);

        Assertions.assertNotNull(seValue);
        Assertions.assertTrue(Double.isNaN(((DoubleResult) seValue.getValue()).doubleValue()));
        Assertions.assertNotNull(seValue.getException());
        Assertions.assertEquals(0, seMol.getLonePairCount());
        Assertions.assertEquals("", AtomContainerDiff.diff(seBefore, seMol),
                "Input state must be restored on Se error");
        Assertions.assertEquals(0.25, seAtom.getCharge(), 1e-9);
        Assertions.assertEquals("CUSTOM_SE", seAtom.getAtomTypeName());
        Assertions.assertEquals("atom-se", seAtom.getID());
        Assertions.assertTrue(seAtom.is(IChemObject.VISITED | IChemObject.PLACED));
        Assertions.assertFalse(seAtom.getFlag(IChemObject.HYDROGEN_BOND_ACCEPTOR));
        Assertions.assertEquals("custom.val", seAtom.getProperty("custom.key"));
        Assertions.assertNull(seAtom.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT));
        Assertions.assertTrue(seMol.getBond(0).is(IChemObject.PLACED | IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(seMol.getFlag(IChemObject.AROMATIC));
        Assertions.assertEquals("mol.val", seMol.getProperty("mol.key"));
    }

    /**
     * Asserts that all atoms in the container have no perceived atom type, valency,
     * hybridization, or charge.
     *
     * @param container the atom container to inspect
     */
    private static void assertUnconfiguredAndUncharged(IAtomContainer container) {
        for (IAtom atom : container.atoms()) {
            Assertions.assertNull(atom.getCharge(), "Expected null charge");
            Assertions.assertNull(atom.getAtomTypeName(), "Expected null atomTypeName");
            Assertions.assertNull(atom.getValency(), "Expected null valency");
            Assertions.assertNull(atom.getHybridization(), "Expected null hybridization");
        }
    }
}
