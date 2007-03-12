package org.openscience.cdk.test.similarity;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-extra
 */
public class DistanceMomentTest extends CDKTestCase
{

    boolean standAlone = false;
    //private static LoggingTool logger = new LoggingTool(TanimotoTest.class);

    public DistanceMomentTest(String name)
    {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(DistanceMomentTest.class);
    }

   public void test3DSim1() {
       fail();
   }
    
}
