package org.openscience.cdk.io.iterator;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
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
class IteratingMDLConformerReaderTest extends CDKTestCase {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(IteratingMDLConformerReaderTest.class);

    @Test
    void testSDF() {
        String filename = "iterconftest.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins,
                DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        int[] nconfs = new int[3];

        int i = 0;
        while (reader.hasNext()) {
            ConformerContainer confContainer = (ConformerContainer) reader.next();
            Assertions.assertNotNull(confContainer);
            nconfs[i++] = confContainer.size();
            molCount++;
        }

        Assertions.assertEquals(3, molCount);
        Assertions.assertEquals(3, nconfs[0]);
        Assertions.assertEquals(18, nconfs[1]);
        Assertions.assertEquals(18, nconfs[2]);
    }

    @Test
    void testRemove() {
        String filename = "iterconftest.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins,
                DefaultChemObjectBuilder.getInstance());

        reader.hasNext();
        reader.next();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            reader.remove();
        });
    }
}
