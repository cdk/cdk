package org.openscience.cdk.qsar.descriptors.molecular;

import junit.framework.Assert;
import org.junit.Before;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.List;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class LengthOverBreadthDescriptorTest extends MolecularDescriptorTest {

    public LengthOverBreadthDescriptorTest() {
    }


    @Before
    public void setUp() throws Exception {
        setDescriptor(LengthOverBreadthDescriptor.class);
    }

    public void testLOBDescriptorCholesterol() throws Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(3.560092, result.get(0), 0.001);
        Assert.assertEquals(3.560092, result.get(1), 0.001);
    }

    public void testLOBDescriptorCyclohexane() throws Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(1);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(1.1476784, result.get(0), 0.01);
        Assert.assertEquals(1.0936984, result.get(1), 0.01);
    }

    public void testLOBDescriptorNaphthalene() throws Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(2);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(1.3083278, result.get(0), 0.01);
        Assert.assertEquals(1.3083278, result.get(1), 0.01);
    }

    public void testLOBDescriptorNButane() throws Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(3);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(2.1251065, result.get(0), 0.000001);
        Assert.assertEquals(2.1251065, result.get(1), 0.000001);
    }

    /**
     * @cdk.bug 1965254
     */
    public void testLOBDescriptor2() throws Exception {
        String filename = "data/mdl/lobtest2.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);


        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        Assert.assertNotNull(result);
    }

}
