package org.openscience.cdk.test.qsar.model.weka;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the tests for the CDK libio-weka module.
 *
 * @cdk.module test-qsar
 * @cdk.depends junit.jar
 */
public class QSARWekaModelTests {
    /**
     * Constructor of the QSARWekaModelTests object
     */
    public QSARWekaModelTests() {
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("CDK standard Tests");

        suite.addTest(J48WModelTest.suite());
        suite.addTest(LinearRegressionWModelTest.suite());
        return suite;
    }
}
