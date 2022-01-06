/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IElectronContainer;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IElectronContainer} implementations.
 *
 * @cdk.module test-interfaces
 *
 * @see org.openscience.cdk.ElectronContainer
 */
public abstract class AbstractElectronContainerTest extends AbstractChemObjectTest {

    @Test
    public void testSetElectronCount_Integer() {
        IElectronContainer ec = (IElectronContainer) newChemObject();
        ec.setElectronCount(3);
        Assert.assertEquals(3, ec.getElectronCount().intValue());
    }

    @Test
    public void testGetElectronCount() {
        testSetElectronCount_Integer();
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IElectronContainer ec = (IElectronContainer) newChemObject();
        ec.setElectronCount(2);
        Object clone = ec.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof IElectronContainer);
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    public void testToString() {
        IElectronContainer at = (IElectronContainer) newChemObject();
        String description = at.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
}
