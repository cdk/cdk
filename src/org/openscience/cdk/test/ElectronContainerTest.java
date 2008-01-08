/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElectronContainer;

/**
 * Checks the functionality of the ElectronContainer class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ElectronContainer
 */
public class ElectronContainerTest extends NewCDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testElectronContainer() {
        IElectronContainer ec = builder.newElectronContainer();
        Assert.assertNotNull(ec);
        Assert.assertEquals(0, ec.getElectronCount());
    }
    
    @Test public void testSetElectronCount_int() {
        IElectronContainer ec = builder.newElectronContainer();
        ec.setElectronCount(3);
        Assert.assertEquals(3, ec.getElectronCount());
    }
    @Test public void testGetElectronCount() {
        testSetElectronCount_int();
    }

    @Test public void testClone() throws Exception {
        IElectronContainer ec = builder.newElectronContainer();
        ec.setElectronCount(2);
        Object clone = ec.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof IElectronContainer);
        Assert.assertEquals(ec.getElectronCount(), ((IElectronContainer)clone).getElectronCount());
    }
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    @Test public void testToString() {
        IElectronContainer at = builder.newElectronContainer();
        String description = at.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
}
