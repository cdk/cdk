package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.List;

/**
 * TestSuite that runs a test for the MDEDescriptor.
 *
 * @cdk.module test-qsarmolecular
 */

public class MDEDescriptorTest extends MolecularDescriptorTest {

    public MDEDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(MDEDescriptor.class);
    }

    @Test
    public void testMDE1() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/mdeotest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        for (int i = 0; i < 19; i++)
            System.out.println(result.get(i));

        Assert.assertEquals(0.0000, result.get(MDEDescriptor.MDEO11), 0.0001);
        Assert.assertEquals(1.1547, result.get(MDEDescriptor.MDEO12), 0.0001);
        Assert.assertEquals(2.9416, result.get(MDEDescriptor.MDEO22), 0.0001);
    }
}
