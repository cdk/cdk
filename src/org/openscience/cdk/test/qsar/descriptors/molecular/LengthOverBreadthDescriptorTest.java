package org.openscience.cdk.test.qsar.descriptors.molecular;

import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.descriptors.molecular.LengthOverBreadthDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class LengthOverBreadthDescriptorTest extends MolecularDescriptorTest {

    public LengthOverBreadthDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(LengthOverBreadthDescriptorTest.class);
    }

    public void setUp() {
    	descriptor = new LengthOverBreadthDescriptor();
    }

    public void testLOBDescriptorCholesterol() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(3.560092, result.get(0), 0.001);
        Assert.assertEquals(3.560092, result.get(1), 0.001);
    }

    public void testLOBDescriptorCyclohexane() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(1);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(1.1476784, result.get(0), 0.000001);
        Assert.assertEquals(1.0936984, result.get(1), 0.000001);
    }

    public void testLOBDescriptorNaphthalene() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(2);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(1.3083278, result.get(0), 0.000001);
        Assert.assertEquals(1.3083278, result.get(1), 0.000001);
    }

    public void testLOBDescriptorNButane() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(3);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(2.1251065, result.get(0), 0.000001);
        Assert.assertEquals(2.1251065, result.get(1), 0.000001);
    }

}
