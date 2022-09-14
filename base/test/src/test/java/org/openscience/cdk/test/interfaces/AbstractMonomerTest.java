/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IMonomer;

/**
 * TestCase for {@link org.openscience.cdk.interfaces.IMonomer} implementations.
 *
 * @cdk.module test-interfaces
 *
 * @author Edgar Luttman &lt;edgar@uni-paderborn.de&gt;
 * @cdk.created 2001-08-09
 */
public abstract class AbstractMonomerTest extends AbstractAtomContainerTest {

    @Test
    public void testSetMonomerName_String() {
        IMonomer m = (IMonomer) newChemObject();
        m.setMonomerName("TRP279");
        Assertions.assertEquals("TRP279", m.getMonomerName());
    }

    @Test
    public void testGetMonomerName() {
        testSetMonomerName_String();
    }

    @Test
    public void testSetMonomerType_String() {
        IMonomer oMonomer = (IMonomer) newChemObject();
        oMonomer.setMonomerType("TRP");
        Assertions.assertEquals("TRP", oMonomer.getMonomerType());
    }

    @Test
    public void testGetMonomerType() {
        testSetMonomerType_String();
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IMonomer oMonomer = (IMonomer) newChemObject();
        oMonomer.setMonomerType("TRP");
        String description = oMonomer.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue('\n' != description.charAt(i));
            Assertions.assertTrue('\r' != description.charAt(i));
        }
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IMonomer oMonomer = (IMonomer) newChemObject();
        Object clone = oMonomer.clone();
        Assertions.assertTrue(clone instanceof IMonomer);
        Assertions.assertNotSame(oMonomer, clone);
    }
}
