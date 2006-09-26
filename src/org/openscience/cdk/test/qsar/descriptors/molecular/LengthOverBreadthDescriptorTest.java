package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LengthOverBreadthDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class LengthOverBreadthDescriptorTest extends CDKTestCase {

    public LengthOverBreadthDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(LengthOverBreadthDescriptorTest.class);
    }

    public void testLOBDescriptorCholesterol() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new ReaderFactory().createReader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        IAtomContainer[] c = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = c[0];

        IMolecularDescriptor descriptor = new LengthOverBreadthDescriptor();
        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(3.560092, result.get(0), 0.001);
        Assert.assertEquals(3.560092, result.get(1), 0.001);
    }

    public void testLOBDescriptorCyclohexane() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new ReaderFactory().createReader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        IAtomContainer[] c = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = c[1];

        IMolecularDescriptor descriptor = new LengthOverBreadthDescriptor();
        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(1.1476784, result.get(0), 0.000001);
        Assert.assertEquals(1.0936984, result.get(1), 0.000001);
    }

    public void testLOBDescriptorNaphthalene() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new ReaderFactory().createReader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        IAtomContainer[] c = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = c[2];

        IMolecularDescriptor descriptor = new LengthOverBreadthDescriptor();
        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(1.3083278, result.get(0), 0.000001);
        Assert.assertEquals(1.3083278, result.get(1), 0.000001);
    }

    public void testLOBDescriptorNButane() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/lobtest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new ReaderFactory().createReader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        IAtomContainer[] c = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = c[3];

        IMolecularDescriptor descriptor = new LengthOverBreadthDescriptor();
        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(2.1251065, result.get(0), 0.000001);
        Assert.assertEquals(2.1251065, result.get(1), 0.000001);
    }

}
