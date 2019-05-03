package org.openscience.cdk.io.iterator;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-extra
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class IteratingMDLConformerReaderTest extends CDKTestCase {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(IteratingMDLConformerReaderTest.class);

    @Test
    public void testSDF() throws Exception {
        String filename = "data/mdl/iterconftest.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins,
                DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        int[] nconfs = new int[3];

        int i = 0;
        while (reader.hasNext()) {
            ConformerContainer confContainer = (ConformerContainer) reader.next();
            Assert.assertNotNull(confContainer);
            nconfs[i++] = confContainer.size();
            molCount++;
        }

        Assert.assertEquals(3, molCount);
        Assert.assertEquals(3, nconfs[0]);
        Assert.assertEquals(18, nconfs[1]);
        Assert.assertEquals(18, nconfs[2]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        String filename = "data/mdl/iterconftest.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins,
                DefaultChemObjectBuilder.getInstance());

        reader.hasNext();
        reader.next();
        reader.remove();

    }
}
