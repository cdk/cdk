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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
public class StabilizationPlusChargeDescriptorTest extends AtomicDescriptorTest {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    LonePairElectronChecker                 lpcheck = new LonePairElectronChecker();

    public StabilizationPlusChargeDescriptorTest() {
        descriptor = new StabilizationPlusChargeDescriptor();
    }

    @Before
    public void setUp() throws Exception {
        setDescriptor(StabilizationPlusChargeDescriptor.class);
    }

    /**
     *  A unit test for JUnit
     *
     * @throws Exception
     */
    @Test
    @Category(SlowTest.class)
    public void testStabilizationPlusChargeDescriptor() throws Exception {

        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.getAtom(0).setFormalCharge(-1);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.getAtom(1).setFormalCharge(1);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "F"));
        mol.addBond(1, 2, Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        DoubleResult result = ((DoubleResult) descriptor.calculate(mol.getAtom(1), mol).getValue());

        Assert.assertNotSame(0.0, result.doubleValue());
    }

    /**
     *
     */
    @Test
    @Category(SlowTest.class)
    public void testNotCharged() throws Exception {

        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.getAtom(0).setFormalCharge(-1);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addAtom(builder.newInstance(IAtom.class, "F"));
        mol.addBond(1, 2, Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        DoubleResult result = ((DoubleResult) descriptor.calculate(mol.getAtom(0), mol).getValue());

        Assert.assertEquals(0.0, result.doubleValue(), 0.00001);

    }

    /**
     *  A unit test for JUnit
     *
     * @throws Exception
     */
    @Test
    @Category(SlowTest.class)
    public void testStabilizationPlusChargeDescriptor2() throws Exception {

        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.getAtom(0).setFormalCharge(-1);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.getAtom(1).setFormalCharge(1);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "F"));
        mol.addBond(1, 2, Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        DoubleResult result = ((DoubleResult) descriptor.calculate(mol.getAtom(1), mol).getValue());

        Assert.assertNotSame(0.0, result.doubleValue());

    }

    /**
     *  A unit test for JUnit
     *
     * @throws Exception
     */
    @Test
    @Category(SlowTest.class)
    public void testStabilizationComparative() throws Exception {

        IAtomContainer mol1 = builder.newInstance(IAtomContainer.class);
        mol1.addAtom(builder.newInstance(IAtom.class, "C"));
        mol1.addAtom(builder.newInstance(IAtom.class, "C"));
        mol1.getAtom(1).setFormalCharge(1);
        mol1.addBond(0, 1, Order.SINGLE);
        mol1.addAtom(builder.newInstance(IAtom.class, "C"));
        mol1.addBond(1, 2, Order.SINGLE);
        mol1.addAtom(builder.newInstance(IAtom.class, "O"));
        mol1.addBond(1, 3, Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        addExplicitHydrogens(mol1);
        lpcheck.saturate(mol1);

        DoubleResult result1 = ((DoubleResult) descriptor.calculate(mol1.getAtom(1), mol1).getValue());

        IAtomContainer mol2 = builder.newInstance(IAtomContainer.class);
        mol2.addAtom(builder.newInstance(IAtom.class, "C"));
        mol2.addAtom(builder.newInstance(IAtom.class, "C"));
        mol2.getAtom(1).setFormalCharge(1);
        mol2.addBond(0, 1, Order.SINGLE);
        mol2.addAtom(builder.newInstance(IAtom.class, "O"));
        mol2.addBond(1, 2, Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        addExplicitHydrogens(mol2);
        lpcheck.saturate(mol2);

        DoubleResult result2 = ((DoubleResult) descriptor.calculate(mol2.getAtom(1), mol2).getValue());

        IAtomContainer mol3 = builder.newInstance(IAtomContainer.class);
        mol3.addAtom(builder.newInstance(IAtom.class, "C"));
        mol3.addAtom(builder.newInstance(IAtom.class, "C"));
        mol3.getAtom(1).setFormalCharge(1);
        mol3.addBond(0, 1, Order.SINGLE);
        mol3.addAtom(builder.newInstance(IAtom.class, "C"));
        mol3.addBond(1, 2, Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol3);
        addExplicitHydrogens(mol3);
        lpcheck.saturate(mol3);

        DoubleResult result3 = ((DoubleResult) descriptor.calculate(mol3.getAtom(1), mol3).getValue());

        Assert.assertTrue(result3.doubleValue() < result2.doubleValue());
        Assert.assertTrue(result2.doubleValue() < result1.doubleValue());
    }

    /**
     *  A unit test for JUnit with C=CCCl # C=CC[Cl+*]
     *
     *  @cdk.inchi InChI=1/C3H7Cl/c1-2-3-4/h2-3H2,1H3
     */
    @Test
    @Category(SlowTest.class)
    public void testCompareIonized() throws Exception {

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

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molB);
        addExplicitHydrogens(molB);
        lpcheck.saturate(molB);

        Assert.assertEquals(1, molB.getAtom(3).getFormalCharge(), 0.00001);
        Assert.assertEquals(1, molB.getSingleElectronCount(), 0.00001);
        Assert.assertEquals(2, molB.getLonePairCount(), 0.00001);

        double resultB = ((DoubleResult) descriptor.calculate(molB.getAtom(3), molB).getValue()).doubleValue();

        Assert.assertNotSame(resultA, resultB);
    }

}
