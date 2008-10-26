package org.openscience.cdk.qsar.descriptors.protein;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.ProteinBuilderTool;

/**
 * TestSuite that runs test for the TAE descriptors
 *
 * @cdk.module test-qsarprotein
 * 
 */
public class TaeAminoAcidDescriptorTest extends CDKTestCase {

	private IMolecularDescriptor descriptor;
	
    @BeforeClass public void setUp() {
    	descriptor = new TaeAminoAcidDescriptor();
    }

    @Test public void testTaeAminoAcidDescriptor() throws ClassNotFoundException, CDKException, Exception {
        BioPolymer pepseq = ProteinBuilderTool.createProtein("ACDEFGH");
        DescriptorValue result = descriptor.calculate(pepseq);

        DoubleArrayResult dar = (DoubleArrayResult) result.getValue();
        Assert.assertEquals(147, dar.length());
    }
}
