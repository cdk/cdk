/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.dict.Dictionary;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.dict.Entry;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.test.qsar.DescriptorTest;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.diff.AtomContainerDiff;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point3d;

/**
 * Tests for molecular descriptors.
 *
 * @cdk.module test-qsarmolecular
 */
public abstract class MolecularDescriptorTest extends DescriptorTest<IMolecularDescriptor> {

    private static final DictionaryDatabase dictDB = new DictionaryDatabase();
    private static final Dictionary         dict   = dictDB.getDictionary("descriptor-algorithms");

    protected MolecularDescriptorTest() {}

    private Number[] getAtomFlags(IAtomContainer mol) {
        Number[] flags = new Number[mol.getAtomCount()];
        for (int i = 0; i < mol.getAtomCount(); i++) {
            flags[i] = mol.getAtom(i).getFlagValue();
        }
        return flags;
    }

    private Number[] getBondFlags(IAtomContainer mol) {
        Number[] flags = new Number[mol.getBondCount()];
        for (int i = 0; i < mol.getBondCount(); i++) {
            flags[i] = mol.getBond(i).getFlagValue();
        }
        return flags;
    }

    @Test
    void descriptorDoesNotChangeFlags() throws CDKException {
        IAtomContainer mol = TestMoleculeFactory.makeBenzene();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Cycles.markRingAtomsAndBonds(mol);
        Number   mflags = mol.getFlagValue();
        Number[] aflags = getAtomFlags(mol);
        Number[] bflags = getBondFlags(mol);
        descriptor.calculate(mol);
        org.hamcrest.MatcherAssert.assertThat("Molecule flags were modified by descriptor!",
                          mol.getFlagValue(), CoreMatchers.is(mflags));
        org.hamcrest.MatcherAssert.assertThat("Molecule's Atom flags were modified by descriptor!",
                          getAtomFlags(mol), CoreMatchers.is(aflags));
        org.hamcrest.MatcherAssert.assertThat("Molecule's Bond flags were modified by descriptor!",
                          getBondFlags(mol), CoreMatchers.is(bflags));
    }

    @Test
    void testDescriptorIdentifierExistsInOntology() {
        Entry ontologyEntry = dict.getEntry(descriptor.getSpecification().getSpecificationReference()
                .substring(dict.getNS().length()).toLowerCase());
        Assertions.assertNotNull(ontologyEntry);
    }

    @Test
    void testCalculate_IAtomContainer() {
        IAtomContainer mol = null;
        try {
            mol = someoneBringMeSomeWater(DefaultChemObjectBuilder.getInstance());
        } catch (Exception e) {
            Assertions.fail("Error in generating the test molecule");
        }

        DescriptorValue v = null;
        try {
            v = descriptor.calculate(mol);
        } catch (Exception e) {
            Assertions.fail("A descriptor must not throw an exception. Exception was:\n" + e.getMessage());
        }
        Assertions.assertNotNull(v);
        Assertions.assertTrue(0 != v.getValue().length(), "The descriptor did not calculate any value.");
    }

    @Test
    void testCalculate_NoModifications() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater(DefaultChemObjectBuilder.getInstance());
        IAtomContainer clone = mol.clone();
        descriptor.calculate(mol);
        String diff = AtomContainerDiff.diff(clone, mol);
        Assertions.assertEquals(0, diff.length(), "The descriptor must not change the passed molecule in any respect, but found this diff: "
                + diff);
    }

    /**
     * Checks if the given labels are consistent.
     *
     * @throws Exception Passed on from calculate.
     */
    @Test
    void testLabels() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater(DefaultChemObjectBuilder.getInstance());

        DescriptorValue v = descriptor.calculate(mol);
        Assertions.assertNotNull(v);
        String[] names = v.getNames();
        Assertions.assertNotNull(names, "The descriptor must return labels using the getNames() method.");
        Assertions.assertNotSame(0, names.length, "At least one label must be given.");
        for (String name : names) {
            Assertions.assertNotNull(name, "A descriptor label may not be null.");
            Assertions.assertNotSame(0, name.length(), "The label string must not be empty.");
            //        	System.out.println("Label: " + names[i]);
        }
        Assertions.assertNotNull(v.getValue());
        int valueCount = v.getValue().length();
        Assertions.assertEquals(names.length, valueCount, "The number of labels must equals the number of values.");
    }

    /**
    * Check if the names obtained directly from the decsriptor without
    * calculation match those obtained from the descriptor value object.
    * Also ensure that the number of actual values matches the length
    * of the names
    */
    @Test
    void testNamesConsistency() throws Exception {
        IAtomContainer mol = someoneBringMeSomeWater(DefaultChemObjectBuilder.getInstance());

        String[] names1 = descriptor.getDescriptorNames();
        DescriptorValue v = descriptor.calculate(mol);
        String[] names2 = v.getNames();

        Assertions.assertEquals(names1.length, names2.length);
        Assertions.assertArrayEquals(names1, names2);

        int valueCount = v.getValue().length();
        Assertions.assertEquals(valueCount, names1.length);
    }

    @Test
    void testGetDescriptorResultType() throws Exception {
        IDescriptorResult result = descriptor.getDescriptorResultType();
        Assertions.assertNotNull(result, "The getDescriptorResultType() must not be null.");

        IAtomContainer mol = someoneBringMeSomeWater(DefaultChemObjectBuilder.getInstance());
        DescriptorValue v = descriptor.calculate(mol);

        Assertions.assertTrue(result.getClass().getName().contains(v.getValue().getClass().getName()), "The getDescriptorResultType() is inconsistent with the calculated descriptor results");
        Assertions.assertEquals(v.getValue().length(), result.length(), "The specified getDescriptorResultType() length does not match the actually calculated result vector length");
    }

    @Test
    void testTakeIntoAccountImplicitHydrogens() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer methane1 = builder.newInstance(IAtomContainer.class);
        IAtom c1 = builder.newInstance(IAtom.class, "C");
        c1.setImplicitHydrogenCount(4);
        methane1.addAtom(c1);

        IAtomContainer methane2 = builder.newInstance(IAtomContainer.class);
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        methane2.addAtom(c2);
        IAtom h1 = builder.newInstance(IAtom.class, "H");
        methane2.addAtom(h1);
        IAtom h2 = builder.newInstance(IAtom.class, "H");
        methane2.addAtom(h2);
        IAtom h3 = builder.newInstance(IAtom.class, "H");
        methane2.addAtom(h3);
        IAtom h4 = builder.newInstance(IAtom.class, "H");
        methane2.addAtom(h4);
        methane2.addBond(0, 1, Order.SINGLE);
        methane2.addBond(0, 2, Order.SINGLE);
        methane2.addBond(0, 3, Order.SINGLE);
        methane2.addBond(0, 4, Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane2);
        addImplicitHydrogens(methane1);
        addImplicitHydrogens(methane2);

        IDescriptorResult v1 = descriptor.calculate(methane1).getValue();
        IDescriptorResult v2 = descriptor.calculate(methane2).getValue();

        String errorMessage = "("
                + descriptor.getClass().toString()
                + ") The descriptor does not give the same results depending on whether hydrogens are implicit or explicit.";
        assertEqualOutput(v1, v2, errorMessage);
    }

    @Test
    void testTakeIntoAccountImplicitHydrogensInEthane() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer ethane1 = builder.newInstance(IAtomContainer.class);
        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        c1.setImplicitHydrogenCount(3);
        c2.setImplicitHydrogenCount(3);
        ethane1.addAtom(c1);
        ethane1.addAtom(c2);
        ethane1.addBond(0, 1, Order.SINGLE);

        IAtomContainer ethane2 = builder.newInstance(IAtomContainer.class);
        IAtom c3 = builder.newInstance(IAtom.class, "C");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        ethane2.addAtom(c3);
        ethane2.addAtom(c4);

        IAtom h1 = builder.newInstance(IAtom.class, "H");
        ethane2.addAtom(h1);
        IAtom h2 = builder.newInstance(IAtom.class, "H");
        ethane2.addAtom(h2);
        IAtom h3 = builder.newInstance(IAtom.class, "H");
        ethane2.addAtom(h3);

        IAtom h4 = builder.newInstance(IAtom.class, "H");
        IAtom h5 = builder.newInstance(IAtom.class, "H");
        IAtom h6 = builder.newInstance(IAtom.class, "H");
        ethane2.addAtom(h4);
        ethane2.addAtom(h5);
        ethane2.addAtom(h6);

        ethane2.addBond(0, 1, Order.SINGLE);
        ethane2.addBond(0, 2, Order.SINGLE);
        ethane2.addBond(0, 3, Order.SINGLE);
        ethane2.addBond(0, 4, Order.SINGLE);

        ethane2.addBond(1, 5, Order.SINGLE);
        ethane2.addBond(1, 6, Order.SINGLE);
        ethane2.addBond(1, 7, Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ethane1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ethane2);
        addImplicitHydrogens(ethane1);
        addImplicitHydrogens(ethane2);

        IDescriptorResult v1 = descriptor.calculate(ethane1).getValue();
        IDescriptorResult v2 = descriptor.calculate(ethane2).getValue();

        String errorMessage = "("
                + descriptor.getClass().toString()
                + ") The descriptor does not give the same results depending on whether hydrogens are implicit or explicit.";
        assertEqualOutput(v1, v2, errorMessage);
    }

    /**
     * Checks that the results of the first and the second descriptor results
     * are identical.
     *
     * @param v1           first {@link org.openscience.cdk.qsar.result.IDescriptorResult}
     * @param v2           second {@link org.openscience.cdk.qsar.result.IDescriptorResult}
     * @param errorMessage error message to report when the results are not the same
     */
    private void assertEqualOutput(IDescriptorResult v1, IDescriptorResult v2, String errorMessage) {
        if (v1 instanceof IntegerResult) {
            Assertions.assertEquals(((IntegerResult) v1).intValue(), ((IntegerResult) v2).intValue(), errorMessage);
        } else if (v1 instanceof DoubleResult) {
            Assertions.assertEquals(((DoubleResult) v1).doubleValue(), ((DoubleResult) v2).doubleValue(), 0.00001, errorMessage);
        } else if (v1 instanceof BooleanResult) {
            Assertions.assertEquals(((BooleanResult) v1).booleanValue(), ((BooleanResult) v2).booleanValue(), errorMessage);
        } else if (v1 instanceof DoubleArrayResult) {
            DoubleArrayResult da1 = (DoubleArrayResult) v1;
            DoubleArrayResult da2 = (DoubleArrayResult) v2;
            for (int i = 0; i < da1.length(); i++) {
                Assertions.assertEquals(da1.get(i), da2.get(i), 0.00001, errorMessage);
            }
        } else if (v1 instanceof IntegerArrayResult) {
            IntegerArrayResult da1 = (IntegerArrayResult) v1;
            IntegerArrayResult da2 = (IntegerArrayResult) v2;
            for (int i = 0; i < da1.length(); i++) {
                Assertions.assertEquals(da1.get(i), da2.get(i), errorMessage);
            }
        }
    }

    @Test
    void testImplementationIndependence() throws Exception {
        IAtomContainer water1 = someoneBringMeSomeWater(DefaultChemObjectBuilder.getInstance());
        IAtomContainer water2 = someoneBringMeSomeWater(SilentChemObjectBuilder.getInstance());

        IDescriptorResult v1 = descriptor.calculate(water1).getValue();
        IDescriptorResult v2 = descriptor.calculate(water2).getValue();

        String errorMessage = "(" + descriptor.getClass().toString()
                + ") The descriptor does not give the same results depending on "
                + "the actual IChemObject implementation set (data, nonotify).";
        assertEqualOutput(v1, v2, errorMessage);
    }

    @Test
    void testAtomContainerHandling() throws Exception {
        IAtomContainer water1 = someoneBringMeSomeWater(DefaultChemObjectBuilder.getInstance());
        // creates an AtomContainer with the atoms / bonds from water1
        IAtomContainer water2 = SilentChemObjectBuilder.getInstance().newAtomContainer();
        water2.add(water1);

        IDescriptorResult v1 = descriptor.calculate(water1).getValue();
        IDescriptorResult v2 = descriptor.calculate(water2).getValue();

        String errorMessage = "(" + descriptor.getClass().toString()
                + ") The descriptor does not give the same results depending on "
                + "it being passed an IAtomContainer or an IAtomContainer.";
        assertEqualOutput(v1, v2, errorMessage);
    }

    /**
     * Descriptors should not throw Exceptions on disconnected structures,
     * but return NA instead.
     */
    @Test
    void testDisconnectedStructureHandling() throws Exception {
        IAtomContainer disconnected = SilentChemObjectBuilder.getInstance()
                                                             .newAtomContainer();
        IAtom chloride = new Atom("Cl");
        chloride.setFormalCharge(-1);
        disconnected.addAtom(chloride);
        IAtom sodium = new Atom("Na");
        sodium.setFormalCharge(+1);
        disconnected.addAtom(sodium);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(disconnected);
        addImplicitHydrogens(disconnected);

        IDescriptorResult v1 = descriptor.calculate(disconnected).getValue();
    }

    @Disabled
    @Test
    void testTakeIntoAccountBondHybridization() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer ethane1 = builder.newInstance(IAtomContainer.class);
        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        ethane1.addAtom(c1);
        ethane1.addAtom(c2);
        ethane1.addBond(0, 1, Order.DOUBLE);

        IAtomContainer ethane2 = builder.newInstance(IAtomContainer.class);
        IAtom c3 = builder.newInstance(IAtom.class, "C");
        c3.setHybridization(IAtomType.Hybridization.SP2);
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        c4.setHybridization(IAtomType.Hybridization.SP2);
        ethane2.addAtom(c3);
        ethane2.addAtom(c4);
        ethane2.addBond(0, 1, Order.SINGLE);

        IDescriptorResult v1 = descriptor.calculate(ethane1).getValue();
        IDescriptorResult v2 = descriptor.calculate(ethane2).getValue();

        String errorMessage = "("
                + descriptor.getClass().toString()
                + ") The descriptor does not give the same results depending on whether bond order or atom type are considered.";
        assertEqualOutput(v1, v2, errorMessage);
    }

    private IAtomContainer someoneBringMeSomeWater(IChemObjectBuilder builder) throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom c1 = builder.newInstance(IAtom.class, "O");
        c1.setPoint3d(new Point3d(0.0, 0.0, 0.0));
        IAtom h1 = builder.newInstance(IAtom.class, "H");
        h1.setPoint3d(new Point3d(1.0, 0.0, 0.0));
        IAtom h2 = builder.newInstance(IAtom.class, "H");
        h2.setPoint3d(new Point3d(-1.0, 0.0, 0.0));
        mol.addAtom(c1);
        mol.addAtom(h1);
        mol.addAtom(h2);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        return mol;
    }

}
