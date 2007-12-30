/* $Revision: 6067 $ $Author: egonw $ $Date: 2006-04-21 10:59:31 +0200 (Fr, 21 Apr 2006) $
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.ChargesCoverageTest;
import org.openscience.cdk.test.charges.GasteigerMarsiliPartialChargesTest;
import org.openscience.cdk.test.charges.InductivePartialChargesTest;
import org.openscience.cdk.test.charges.PolarizabilityTest;

/**
 * TestSuite that runs all the sample tests for the charges module.
 *
 * @cdk.module test-charges
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MchargesTests {

    public static Test suite() {

        TestSuite suite = new TestSuite("All Tests for the 'charges' module");

        suite.addTest(new JUnit4TestAdapter(ChargesCoverageTest.class));	

//      from cdk.test.qsar.charges
        suite.addTest(GasteigerMarsiliPartialChargesTest.suite());
        suite.addTest(InductivePartialChargesTest.suite());
        suite.addTest(PolarizabilityTest.suite());
        
        return suite;
    }

}
