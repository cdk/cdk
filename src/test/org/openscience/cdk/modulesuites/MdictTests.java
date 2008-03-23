/* $Revision: 10506 $ $Author: egonw $ $Date: 2008-03-22 16:10:12 +0100 (Sat, 22 Mar 2008) $
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.dict.DictDBTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module  test-dict
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MdictTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("All CDK dict module Tests");

        // from cdk.dict
        suite.addTest(new JUnit4TestAdapter(DictDBTest.class));
        
        return suite;
    }
    
}
