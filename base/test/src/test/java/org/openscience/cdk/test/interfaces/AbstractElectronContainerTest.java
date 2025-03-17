/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IElectronContainer;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IElectronContainer} implementations.
 *
 *
 * @see org.openscience.cdk.ElectronContainer
 */
public abstract class AbstractElectronContainerTest extends AbstractChemObjectTest {

    @Test
    public void testSetElectronCount_Integer() {
        IElectronContainer ec = (IElectronContainer) newChemObject();
        ec.setElectronCount(3);
        Assertions.assertEquals(3, ec.getElectronCount().intValue());
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
        Assertions.assertNotNull(clone);
        Assertions.assertTrue(clone instanceof IElectronContainer);
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    public void testToString() {
        IElectronContainer at = (IElectronContainer) newChemObject();
        String description = at.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }
}
