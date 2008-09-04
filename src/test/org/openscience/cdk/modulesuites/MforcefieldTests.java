/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
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

import org.openscience.cdk.charges.MMFF94PartialChargesTest;
import org.openscience.cdk.coverage.ForcefieldCoverageTest;
import org.openscience.cdk.modeling.forcefield.ForceFieldTests;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test-forcefield
 */
public class MforcefieldTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The CDK force field module Tests");
        
        suite.addTest(ForcefieldCoverageTest.suite());
        
        suite.addTest(new JUnit4TestAdapter(ForceFieldTests.class));
        suite.addTest(new JUnit4TestAdapter(MMFF94PartialChargesTest.class));

        return suite;
    }

}
