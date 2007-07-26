package org.openscience.cdk.test;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.test.pharmacophore.PharmacophoreMatcherTest;

/**
 * TestSuite that runs all the sample tests for pharmacophore classes.
 *
 * @cdk.module test-pcore
 */
public class MpcoreTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("The cdk.pcore Tests");
        suite.addTest(new JUnit4TestAdapter(PharmacophoreMatcherTest.class));
        return suite;
    }

}
