package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

class RdfileReaderTest {

    @Test
    void testRdfile_oneRecord_Rxn() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rxn.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        Map<String,String> expectedData = new LinkedHashMap<>();
        final String key1 = "Identifier";
        final String value1 = "141";
        final String key2 = "LongMultilineDatum";
        final String value2 = "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9AXjGOe9gSgO0I7" +
                "u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB";
        expectedData.put(key1, value1);
        expectedData.put(key2, value2);

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertNull(rdfileRecord.getExternalRegistryNumber());
        Assertions.assertNull(rdfileRecord.getInternalRegistryNumber());
        Assertions.assertNull(rdfileRecord.getMolfile());
        Assertions.assertEquals(expectedData.get(key1), rdfileRecord.getDatum(key1), "Expected '" + value1 + "' as a value for the key '" + key1 + "'");
        Assertions.assertEquals(expectedData.get(key2), rdfileRecord.getDatum(key2),
                "Expected '" + value2 + "' as a value for the key '" + key2 + "'");
        Assertions.assertEquals(2, rdfileRecord.getData().size());
        Assertions.assertIterableEquals(expectedData.entrySet(), rdfileRecord.getData().entrySet());

        // tear down
        rdfileReader.close();
    }
}