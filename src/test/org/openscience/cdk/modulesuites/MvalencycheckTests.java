/* $Revision$ $Author$ $Date$
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
import org.openscience.cdk.coverage.ValencycheckCoverageTest;
import org.openscience.cdk.tools.CDKHydrogenAdderTest;
import org.openscience.cdk.tools.CDKValencyCheckerTest;
import org.openscience.cdk.tools.DeduceBondOrderTestFromExplicitHydrogens;
import org.openscience.cdk.tools.SaturationCheckerTest;

/**
 * TestSuite that runs all the tests for the valency module.
 *
 * @cdk.module  test-valencycheck
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MvalencycheckTests {
    
    public static Test suite( ) {
        TestSuite suite= new TestSuite("All valencycheck Tests");
        
        suite.addTest(ValencycheckCoverageTest.suite());

        suite.addTest(new JUnit4TestAdapter(SaturationCheckerTest.class));
        suite.addTest(DeduceBondOrderTestFromExplicitHydrogens.suite());
        
        // the next generation valency tools that rely on CDKAtomTypeMatcher
        suite.addTest(new JUnit4TestAdapter(CDKHydrogenAdderTest.class));
        suite.addTest(CDKValencyCheckerTest.suite());
        
        return suite;
    }
    
}
