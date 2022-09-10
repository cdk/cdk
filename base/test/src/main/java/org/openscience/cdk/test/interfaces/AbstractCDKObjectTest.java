/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Tests the functionality of {@link org.openscience.cdk.interfaces.ICDKObject} implementations.
 *
 * @cdk.module  test-interfaces
 * @cdk.created 2010-10-22
 */
public abstract class AbstractCDKObjectTest extends CDKTestCase {

    private static ITestObjectBuilder builder;

    /**
     * Sets the {@link ITestObjectBuilder} that constructs new test objects with
     * {@link #newChemObject()}.
     *
     * @param builder ITestChemObject that instantiates new test objects
     */
    public static void setTestObjectBuilder(ITestObjectBuilder builder) {
        AbstractCDKObjectTest.builder = builder;
    }

    public static IChemObject newChemObject() {
        return AbstractCDKObjectTest.builder.newTestObject();
    }

    @Test
    public void testGetBuilder() {
        IChemObject chemObject = newChemObject();
        Object object = chemObject.getBuilder();
        Assertions.assertNotNull(object);
        Assertions.assertTrue(object instanceof IChemObjectBuilder);
    }
}
