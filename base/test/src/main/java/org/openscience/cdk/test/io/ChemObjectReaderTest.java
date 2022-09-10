/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.io;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.io.IChemObjectReader;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * TestCase for CDK IO classes.
 *
 * @cdk.module test-io
 */
public abstract class ChemObjectReaderTest extends ChemObjectIOTest {

    protected static IChemObjectReader chemObjectIO;
    protected static String            testFile;

    public static void setChemObjectReader(IChemObjectReader aChemObjectReader, String testFile) {
        ChemObjectIOTest.setChemObjectIO(aChemObjectReader);
        ChemObjectReaderTest.chemObjectIO = aChemObjectReader;
        ChemObjectReaderTest.testFile = testFile;
    }

    @Test
    public void testSetReader_InputStream() throws Exception {
        Assertions.assertNotNull(testFile, "No test file has been set!");
        InputStream ins = ChemObjectReaderTest.class.getClassLoader().getResourceAsStream(testFile);
        // try local path
        if (ins == null)
            ins = getClass().getResourceAsStream(testFile);
        chemObjectIO.setReader(ins);
    }

    @Test
    public void testSetReader_Reader() throws Exception {
        Assertions.assertNotNull(testFile, "No test file has been set!");
        InputStream ins = ChemObjectReaderTest.class.getClassLoader().getResourceAsStream(testFile);
        // try local path
        if (ins == null)
            ins = getClass().getResourceAsStream(testFile);
        chemObjectIO.setReader(new InputStreamReader(ins));
    }

}
