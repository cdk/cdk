/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.io;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.IChemObjectWriter;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

/**
 * TestCase for {@link org.openscience.cdk.io.IChemObjectWriter} implementations.
 *
 * @cdk.module test-io
 */
public abstract class ChemObjectWriterTest extends ChemObjectIOTest {

    protected static IChemObjectWriter chemObjectIO;

    public static void setChemObjectWriter(IChemObjectWriter aChemObjectWriter) {
        setChemObjectIO(aChemObjectWriter);
        ChemObjectWriterTest.chemObjectIO = aChemObjectWriter;
    }


    /**
     * Unit tests that iterates over all common objects that can be
     * serialized and tests that if it is marked as accepted with
     * <code>accepts</code>, that it can actually be written too.
     */
    @Test
    public void testAcceptsWriteConsistency() throws CDKException {
        Assert.assertNotNull("The IChemObjectWriter is not set.", chemObjectIO);
        for (IChemObject object : acceptableChemObjects()) {
            if (chemObjectIO.accepts(object.getClass())) {
                StringWriter writer = new StringWriter();
                chemObjectIO.setWriter(writer);
                try {
                    chemObjectIO.write(object);
                } catch (CDKException exception) {
                    if (exception.getMessage().contains("Only supported")) {
                        Assert.fail("IChemObject of type " + object.getClass().getName() + " is marked as "
                                + "accepted, but failed to be written.");
                    } else {
                        throw exception;
                    }
                }
            }
        }
    }

    @Test
    public void testSetWriter_Writer() throws Exception {
        Assert.assertNotNull("No IChemObjectWriter has been set!", chemObjectIO);
        StringWriter testWriter = new StringWriter();
        chemObjectIO.setWriter(testWriter);
    }

    @Test
    public void testSetWriter_OutputStream() throws Exception {
        Assert.assertNotNull("No IChemObjectWriter has been set!", chemObjectIO);
        ByteArrayOutputStream testStream = new ByteArrayOutputStream();
        chemObjectIO.setWriter(testStream);
    }
}
