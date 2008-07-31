/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
 * TestSuite that tests if all public methods in the sdg
 * module are tested.
 *
 * @cdk.module test-sdg
 */
public class SdgCoverageTest extends CoverageAnnotationTest {

    private final static String CLASS_LIST = "sdg.javafiles";
    
    public SdgCoverageTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        super.loadClassList(CLASS_LIST);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(SdgCoverageTest.class);
        return suite;
    }

    public void testCoverage() {
        assertTrue(super.runCoverageTest());
    }

}
