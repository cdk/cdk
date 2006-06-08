package org.openscience.cdk.test.qsar.model.R2;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.qsar.model.R2.RModel;
import org.openscience.cdk.test.CDKTestCase;


/**
 * TestSuite that checks some SJava requirements
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module test-qsar
 */
public class RJavaEnvironmentTest extends CDKTestCase {

    public RJavaEnvironmentTest() {
    }

    public static Test suite() {
        return new TestSuite(RJavaEnvironmentTest.class);
    }

    public void testRJavaEnvironment() {
        try {
            NoneModel noneModel = new NoneModel();
            assertNotNull(noneModel);

            // stop the R process
            RModel.getRengine().end();

        } catch (QSARModelException e) {
            e.printStackTrace();
        }

    }

    class NoneModel extends RModel {

        public NoneModel() throws QSARModelException {
            super();
        }

        public void build() throws QSARModelException {
        }

        public void predict() throws QSARModelException {
        }

        protected void finalize() {

        }

        public void loadModel(String fileName) throws QSARModelException {
        }

        public void loadModel(String serializedModel, String modelName) throws QSARModelException {
        }
    }

}
