package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.TaeAminoAcidDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.ProteinBuilderTool;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class TaeAminoAcidDescriptorTest extends CDKTestCase {

    public TaeAminoAcidDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(TaeAminoAcidDescriptorTest.class);
    }

    public void testTaeAminoAcidDescriptor() throws ClassNotFoundException, CDKException, Exception {
        IMolecularDescriptor descriptor = new TaeAminoAcidDescriptor();


        BioPolymer pepseq = ProteinBuilderTool.createProtein("ACDEFGH");
        DescriptorValue result = descriptor.calculate(pepseq);

        DoubleArrayResult dar = (DoubleArrayResult) result.getValue();
        assertTrue(dar.length() == 147);
    }
}
