package org.openscience.cdk.test.qsar.model;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.model.R.LinearRegressionModel;
import org.openscience.cdk.qsar.model.QSARModelException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestSuite that runs a test for the LinearRegressionModel
 *
 * @cdk.module test
 */
 
public class LinearRegressionModelTest extends TestCase {
	
	public  LinearRegressionModelTest() {}
    
	public static Test suite() {
		return new TestSuite(LinearRegressionModelTest.class);
	}
    
        public void testLinearRegressionModelInstantiate() throws CDKException, java.lang.Exception, QSARModelException {
            LinearRegressionModel lrm = new LinearRegressionModel();
            assertTrue(lrm.revaluator != null);
        }

/*
        public void testSJavaInterface() throws CDKException, QSARModelException {
            LinearRegressionModel lrm = new LinearRegressionModel();
            double[][] jm = {{1.,2.,3.},{4.,5.,6.},{5.,6.,7.}};
            lrm.revaluator.call("printJavadoubleArray2D", new Object[]{jm});
        }
        */
}

