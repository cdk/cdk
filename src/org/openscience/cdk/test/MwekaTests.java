package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.libio.weka.WekaTest;
import org.openscience.cdk.test.qsar.descriptors.atomic.IPAtomicDescriptorTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.IPMolecularDescriptorTest;

/**
 * TestSuite that runs all the tests for the CDK reaction module.
 *
 * @cdk.module  test-weka
 * @cdk.depends weka.jar
 * @cdk.depends junit.jar
 */
public class MwekaTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("CDK weka Tests");

        // Individual Tests
        
        suite.addTest(WekaTest.suite());

        suite.addTest(IPAtomicDescriptorTest.suite());
        suite.addTest(IPMolecularDescriptorTest.suite());
        suite.addTest(IPAtomicDescriptorTest.suite());

        return suite;
    }
    
}
