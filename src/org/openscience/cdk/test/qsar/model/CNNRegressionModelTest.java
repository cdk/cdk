package org.openscience.cdk.test.qsar.model;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.model.R.CNNRegressionModel;
import org.openscience.cdk.qsar.model.QSARModelException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestSuite that runs a test for the CNNRegressionModel
 *
 * @cdk.module test
 */
 
public class CNNRegressionModelTest extends TestCase {
	
	public  CNNRegressionModelTest() {}
    
	public static Test suite() {
		return new TestSuite(CNNRegressionModelTest.class);
	}
    
        public void testCNNRegressionModelInstantiate() throws CDKException, java.lang.Exception, QSARModelException {
            CNNRegressionModel cnnrm = new CNNRegressionModel();
            assertTrue(cnnrm.revaluator != null);
        }
}

