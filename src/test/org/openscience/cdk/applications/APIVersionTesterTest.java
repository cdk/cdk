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
package org.openscience.cdk.applications;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;

/**
 * Checks the functionality of the APIVersionTester.
 *
 * @cdk.module test-extra
 */
public class APIVersionTesterTest extends NewCDKTestCase {

    @Test public void testIsBiggerOrEqual() {
        Assert.assertTrue(APIVersionTester.isBiggerOrEqual("1.6", "1.6"));
        Assert.assertTrue(APIVersionTester.isBiggerOrEqual("1.6", "1.12"));
        Assert.assertFalse(APIVersionTester.isBiggerOrEqual("1.12", "1.7"));
    }

    @Test public void testIsSmaller() {
    	Assert.assertFalse(APIVersionTester.isSmaller("1.6", "1.6"));
    	Assert.assertFalse(APIVersionTester.isSmaller("1.6", "1.12"));
    	Assert.assertTrue(APIVersionTester.isSmaller("1.12", "1.7"));
    }

}
