package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
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

class MDEDescriptorTest extends MolecularDescriptorTest {

    MDEDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(MDEDescriptor.class);
    }

    @Test
    void testMDE1() throws Exception {
        String filename = "mdeotest.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assertions.assertEquals(0.0000, result.get(MDEDescriptor.MDEO11), 0.0001);
        Assertions.assertEquals(1.1547, result.get(MDEDescriptor.MDEO12), 0.0001);
        Assertions.assertEquals(2.9416, result.get(MDEDescriptor.MDEO22), 0.0001);
    }
}
