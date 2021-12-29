/* Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.interfaces;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IElectronContainer} implementations.
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
