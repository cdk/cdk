package org.openscience.cdk.qsar.descriptors.protein;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.protein.TaeAminoAcidDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.ProteinBuilderTool;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarprotein
 */

public class TaeAminoAcidDescriptorTest extends CDKTestCase {

	private IMolecularDescriptor descriptor;
	
    public TaeAminoAcidDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(TaeAminoAcidDescriptorTest.class);
    }

    public void setUp() {
    	descriptor = new TaeAminoAcidDescriptor();
    }

    public void testTaeAminoAcidDescriptor() throws ClassNotFoundException, CDKException, Exception {
        BioPolymer pepseq = ProteinBuilderTool.createProtein("ACDEFGH");
        DescriptorValue result = descriptor.calculate(pepseq);

        DoubleArrayResult dar = (DoubleArrayResult) result.getValue();
        assertEquals(147, dar.length());
    }
}
