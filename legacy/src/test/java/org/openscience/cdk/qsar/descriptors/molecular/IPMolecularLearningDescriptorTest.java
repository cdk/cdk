/* Copyright (C) 2008  Miguel Rojas <miguel.rojas@uni-koeln.de>
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarionpot
 */
public class IPMolecularLearningDescriptorTest extends MolecularDescriptorTest {

    private SmilesParser            sp      = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    private IChemObjectBuilder      builder = SilentChemObjectBuilder.getInstance();
    private LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    /**
     *  Constructor for the IPMolecularLearningDescriptorTest object
     *
     */
    public IPMolecularLearningDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        super.setDescriptor(IPMolecularLearningDescriptor.class);
    }

    @Test
    public void testIPMolecularLearningDescriptor() throws Exception {
        Assert.assertNotNull(descriptor);
    }

    /**
     *  A unit test for JUnit with CC(C)C(C)C
     *
     *  @cdk.inchi InChI=1/C6H14/c1-5(2)6(3)4/h5-6H,1-4H3
     */
    @Test
    @Category(SlowTest.class)
    public void testIPDescriptor0() throws Exception {

        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(1, 2, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(1, 3, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(3, 4, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(3, 5, Order.SINGLE);

        addExplicitHydrogens(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        lpcheck.saturate(mol);

        IPMolecularLearningDescriptor descriptor = new IPMolecularLearningDescriptor();
        double result = ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 0.0;

        Assert.assertEquals(resultAccordingNIST, result, 0.0001);
    }

    /**
     *  A unit test for JUnit with C-Cl
     *
     */
    @Test
    @Category(SlowTest.class)
    public void testIPDescriptor_1() throws Exception {

        IAtomContainer mol = sp.parseSmiles("C-Cl");

        addExplicitHydrogens(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        lpcheck.saturate(mol);

        IPMolecularLearningDescriptor descriptor = new IPMolecularLearningDescriptor();
        double result = ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 11.26;

        Assert.assertEquals(resultAccordingNIST, result, 0.53);
    }

    /**
     *  A unit test for JUnit with COCCCC=O
     */
    @Test
    @Category(SlowTest.class)
    public void testIPDescriptor_2() throws Exception {

        IAtomContainer mol = sp.parseSmiles("COCCCC=O");

        addExplicitHydrogens(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        lpcheck.saturate(mol);

        IPMolecularLearningDescriptor descriptor = new IPMolecularLearningDescriptor();
        DoubleArrayResult dar = ((DoubleArrayResult) (descriptor).calculatePlus(mol).getValue());

        double resultAccordingNIST = 9.37;

        Assert.assertEquals(2, dar.length());
        Assert.assertEquals(resultAccordingNIST, dar.get(0), 0.3);
    }

    /**
     *  A unit test for JUnit with C=CCC(=O)CC
     */
    @Test
    @Category(SlowTest.class)
    public void testIPDescriptor_3() throws Exception {

        IAtomContainer mol = sp.parseSmiles("C=CCCC(=O)C");

        addExplicitHydrogens(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        lpcheck.saturate(mol);

        IPMolecularLearningDescriptor descriptor = new IPMolecularLearningDescriptor();
        DoubleArrayResult dar = ((DoubleArrayResult) ((IPMolecularLearningDescriptor) descriptor).calculatePlus(mol)
                .getValue());

        double resultAccordingNIST = 9.50;
        Assert.assertEquals(2, dar.length());
        Assert.assertEquals(resultAccordingNIST, dar.get(0), 0.6);

    }

    //    /**
    //     * A unit test for JUnit with C-Cl
    //     *
    //     * @throws ClassNotFoundException
    //     * @throws CDKException
    //     * @throws java.lang.Exception
    //     */
    //    @Test public void testIPDescriptorReaction() throws ClassNotFoundException, CDKException, java.lang.Exception{
    //    	IAtomContainer mol = sp.parseSmiles("C-Cl");
    //
    //		addExplicitHydrogens(mol);
    //		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    //		lpcheck.saturate(mol);
    //
    //		descriptor.calculate(mol);
    //
    //		IReactionSet reactionSet = ((IPMolecularLearningDescriptor)descriptor).getReactionSet();
    //		double resultAccordingNIST = 11.26;
    //
    //		double result = ((Double) reactionSet.getReaction(0).getProperty("IonizationEnergy")).doubleValue();
    //        Assert.assertEquals(1, reactionSet.getReactionCount());
    //        Assert.assertEquals(resultAccordingNIST, result, 0.53);
    //    }
    //    /**
    //     * A unit test for JUnit with CCCC
    //     *
    //     * @throws ClassNotFoundException
    //     * @throws CDKException
    //     * @throws java.lang.Exception
    //     */
    //    @Test public void testIPDescriptorReaction2() throws ClassNotFoundException, CDKException, java.lang.Exception{
    //    	IAtomContainer mol = sp.parseSmiles("CCCC");
    //
    //		addExplicitHydrogens(mol);
    //		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    //		lpcheck.saturate(mol);
    //
    //		descriptor.calculate(mol);
    //
    //		IReactionSet reactionSet = ((IPMolecularLearningDescriptor)descriptor).getReactionSet();
    //
    //        Assert.assertEquals(0, reactionSet.getReactionCount());
    //    }
    //    /**
    //     * A unit test for JUnit with CCC#CCCO
    //     *
    //     * @throws ClassNotFoundException
    //     * @throws CDKException
    //     * @throws java.lang.Exception
    //     */
    //    @Test public void testIPDescriptorReaction3() throws ClassNotFoundException, CDKException, java.lang.Exception{
    //    	IAtomContainer mol = sp.parseSmiles("CCC#CCCO");
    //
    //		addExplicitHydrogens(mol);
    //		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    //		lpcheck.saturate(mol);
    //
    //		descriptor.calculate(mol);
    //
    //		IReactionSet reactionSet = ((IPMolecularLearningDescriptor)descriptor).getReactionSet();
    //
    //        Assert.assertEquals(3, reactionSet.getReactionCount());
    //    }
    //    /**
    //     * A unit test for JUnit with CCC#CCCO
    //     *
    //     * @throws ClassNotFoundException
    //     * @throws CDKException
    //     * @throws java.lang.Exception
    //     */
    //    @Test public void testIPDescriptorReaction4() throws ClassNotFoundException, CDKException, java.lang.Exception{
    //    	IAtomContainer mol = sp.parseSmiles("CCCCC=CO");
    //
    //		addExplicitHydrogens(mol);
    //		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    //		lpcheck.saturate(mol);
    //
    //		descriptor.calculate(mol);
    //
    //		IReactionSet reactionSet = ((IPMolecularLearningDescriptor)descriptor).getReactionSet();
    //
    //        Assert.assertEquals(3, reactionSet.getReactionCount());
    //    }
    /**
     *
     *  A unit test for JUnit with Triclosan. Oc2cc(ccc2(Oc1ccc(cc1Cl)Cl))Cl
     *
     * @cdk.inchi InChI=1S/C12H7Cl3O2/c13-7-1-3-11(9(15)5-7)17-12-4-2-8(14)6-10(12)16/h1-6,16H
     * @cdk.bug 2787332
     *
     * @throws org.openscience.cdk.exception.CDKException
     */
    @Test
    @Category(SlowTest.class)
    public void testBug_2787332_triclosan() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//0
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//1
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//2
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//3
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//4
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//5
        mol.addAtom(builder.newInstance(IAtom.class, "O"));//6
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//7
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//8
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//9
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//10
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//11
        mol.addAtom(builder.newInstance(IAtom.class, "C"));//12
        mol.addAtom(builder.newInstance(IAtom.class, "Cl"));//13
        mol.addAtom(builder.newInstance(IAtom.class, "Cl"));//14
        mol.addAtom(builder.newInstance(IAtom.class, "O"));//15
        mol.addAtom(builder.newInstance(IAtom.class, "Cl"));//16

        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addBond(2, 3, Order.SINGLE);
        mol.addBond(3, 4, Order.DOUBLE);
        mol.addBond(4, 5, Order.SINGLE);
        mol.addBond(5, 0, Order.DOUBLE);
        mol.addBond(5, 6, Order.SINGLE);
        mol.addBond(6, 7, Order.SINGLE);
        mol.addBond(7, 8, Order.DOUBLE);
        mol.addBond(8, 9, Order.SINGLE);
        mol.addBond(9, 10, Order.DOUBLE);
        mol.addBond(10, 11, Order.SINGLE);
        mol.addBond(11, 12, Order.DOUBLE);
        mol.addBond(12, 7, Order.SINGLE);
        mol.addBond(0, 13, Order.SINGLE);
        mol.addBond(2, 14, Order.SINGLE);
        mol.addBond(10, 16, Order.SINGLE);
        mol.addBond(12, 15, Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);

        IPMolecularLearningDescriptor descriptor = new IPMolecularLearningDescriptor();
        DoubleArrayResult dar = ((DoubleArrayResult) ((IPMolecularLearningDescriptor) descriptor).calculatePlus(mol)
                .getValue());

        Assert.assertEquals(6, dar.length());
    }
}
