package org.openscience.cdk.io.iterator;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.io.iterator.IteratingMDLConformerReader;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

import java.io.InputStream;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class IteratingMDLConformerReaderTest extends CDKTestCase {

    private LoggingTool logger;

    public IteratingMDLConformerReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(IteratingMDLConformerReaderTest.class);
    }

    public void testSDF() throws Exception {
        String filename = "data/mdl/iterconftest.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins, DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        int[] nconfs = new int[3];

        int i = 0;
        while (reader.hasNext()) {
            ConformerContainer confContainer = (ConformerContainer) reader.next();
            assertNotNull(confContainer);
            nconfs[i++] = confContainer.size();
            molCount++;
        }

        assertEquals(3, molCount);
        assertEquals(3, nconfs[0]);
        assertEquals(18, nconfs[1]);
        assertEquals(18, nconfs[2]);
    }



}
