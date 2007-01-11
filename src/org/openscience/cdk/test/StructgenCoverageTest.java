/* $Revision: 7635 $ $Author: egonw $ $Date: 2007-01-04 18:32:54 +0100 (Thu, 04 Jan 2007) $
 * 
 * Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that performs a simple coverage test of the structgen module.
 *
 * @cdk.module test-structgen
 */
public class StructgenCoverageTest extends CoverageTest {

    private final static String CLASS_LIST = "structgen.javafiles";
    
    public StructgenCoverageTest(String name){
        super(name);
    }

    public void setUp() {
        super.setUp();
        try {
            super.loadClassList(CLASS_LIST);
        } catch (Exception exception) {
            fail("Could not load classes to test: " + exception.getMessage());
        }
    }

    public static Test suite() {
        return new TestSuite(StructgenCoverageTest.class);
    }

    public void testCoverage() {
        super.runCoverageTest();
    }
}
