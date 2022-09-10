package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.config.Isotopes;
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

    public LengthOverBreadthDescriptorTest() {}

    @BeforeEach
    public void setUp() throws Exception {
        setDescriptor(LengthOverBreadthDescriptor.class);
    }

    @Test
    public void testLOBDescriptorCholesterol() throws Exception {
        String filename = "lobtest.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);
        Isotopes.getInstance().configureAtoms(ac);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(3.5029, result.get(0), 0.001);
        Assert.assertEquals(3.5029, result.get(1), 0.001);
    }

    @Test
    public void testLOBDescriptorCyclohexane() throws Exception {
        String filename = "lobtest.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(1);
        Isotopes.getInstance().configureAtoms(ac);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(1.1476784, result.get(0), 0.01);
        Assert.assertEquals(1.0936984, result.get(1), 0.01);
    }

    @Test
    public void testLOBDescriptorNaphthalene() throws Exception {
        String filename = "lobtest.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(2);

        Isotopes.getInstance().configureAtoms(ac);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(1.3083278, result.get(0), 0.01);
        Assert.assertEquals(1.3083278, result.get(1), 0.01);
    }

    @Test
    public void testLOBDescriptorNButane() throws Exception {
        String filename = "lobtest.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(3);
        Isotopes.getInstance().configureAtoms(ac);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(2.0880171, result.get(0), 0.000001);
        Assert.assertEquals(2.0880171, result.get(1), 0.000001);
    }

    /**
     * @cdk.bug 1965254
     */
    @Test
    public void testLOBDescriptor2() throws Exception {
        String filename = "lobtest2.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        Assert.assertNotNull(result);
    }

}
