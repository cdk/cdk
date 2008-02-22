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

package org.openscience.cdk.test.debug;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.openscience.cdk.debug.DebugChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.test.DefaultChemObjectBuilderTest;

/**
 * Checks the functionality of the Crystal.
 *
 * @cdk.module test-datadebug
 */
public class DebugChemObjectBuilderTest extends DefaultChemObjectBuilderTest {

    @BeforeClass public static void setUp() {
    	DefaultChemObjectBuilderTest.rootObject = new org.openscience.cdk.debug.DebugChemObject();
    }

    @org.junit.Test public void testGetInstance() {
    	Object builder = DebugChemObjectBuilder.getInstance();
    	Assert.assertNotNull(builder);
    	Assert.assertTrue(builder instanceof IChemObjectBuilder);
    	Assert.assertTrue(builder instanceof DebugChemObjectBuilder);
    }
    
}
