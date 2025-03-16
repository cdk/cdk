/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.io;

import org.openscience.cdk.io.ISimpleChemObjectReader;

/**
 * TestCase for CDK IO classes.
 *
 */
public abstract class SimpleChemObjectReaderTest extends ChemObjectReaderTest {

    protected static ISimpleChemObjectReader chemObjectIO;

    public static void setSimpleChemObjectReader(ISimpleChemObjectReader aSimpelChemObjectReader, String testFile) {
        setChemObjectReader(aSimpelChemObjectReader, testFile);
        SimpleChemObjectReaderTest.chemObjectIO = aSimpelChemObjectReader;
    }

//    @Test
//    public void testRead_IChemObject() throws Exception {
//        Assert.assertNotNull("No test file has been set!", testFile);
//
//        boolean read = false;
//        for (IChemObject object : acceptableChemObjects()) {
//            if (chemObjectIO.accepts(object.getClass())) {
//                InputStream ins = SimpleChemObjectReaderTest.class.getClassLoader().getResourceAsStream(testFile);
//                chemObjectIO.setReader(ins);
//                IChemObject readObject = chemObjectIO.read(object);
//                chemObjectIO.close();
//                Assert.assertNotNull("Failed attempt to read the file as " + object.getClass().getName(), readObject);
//                read = true;
//            }
//        }
//        if (!read) {
//            Assert.fail("Reading an IChemObject from the Reader did not work properly.");
//        }
//    }

}
