package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.libio.weka.WekaTest;

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
        return suite;
    }
    
}
