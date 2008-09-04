/* $RCSfile$    
 * $Author: egonw $    
 * $Date: 2008-07-27 19:02:01 +0200 (Sun, 27 Jul 2008) $    
 * $Revision: 11792 $
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.coverage;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test-datadebug
 */
public class DatadebugCoverageTest extends CoverageTest {

    private final static String CLASS_LIST = "datadebug.javafiles";
    
    public DatadebugCoverageTest(String name){
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        super.loadClassList(CLASS_LIST);
    }

    public static Test suite() {
        return new TestSuite(DatadebugCoverageTest.class);
    }

    public void testCoverage() {
        super.runCoverageTest();
    }
}
