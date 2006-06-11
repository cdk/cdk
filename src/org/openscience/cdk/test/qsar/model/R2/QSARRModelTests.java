package org.openscience.cdk.test.qsar.model.R2;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all Model tests.
 *
 * @author Rajarshi Guha
 * @cdk.module test-qsar
 */
public class QSARRModelTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("All QSAR rJava Based Modeling Tests");
        try {

            Class testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.test.qsar.model.R2.RJavaEnvironmentTest");
            suite.addTest(new TestSuite(testClass));
            testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.test.qsar.model.R2.LinearRegressionModelTest");
            suite.addTest(new TestSuite(testClass));
            testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.test.qsar.model.R2.CNNRegressionModelTest");
            suite.addTest(new TestSuite(testClass));
            System.out.println("Found RJava, running R tests...");
        } catch (ClassNotFoundException exception) {
            System.out.println("RJava is not found, skipping R tests...");
        } catch (Exception exception) {
            System.out.println("Could not load an R model test: " + exception.getMessage());
            exception.printStackTrace();
        }
        return suite;
    }
}
