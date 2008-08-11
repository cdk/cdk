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

import org.openscience.cdk.atomtype.SybylAtomTypeMatcherTest;
import org.openscience.cdk.config.atomtypes.OWLAtomTypeMappingHandlerTest;
import org.openscience.cdk.config.atomtypes.OWLAtomTypeMappingReaderTest;
import org.openscience.cdk.coverage.AtomtypeCoverageTest;

/**
 * TestSuite that runs all the tests for the CDK core module.
 *
 * @cdk.module  test-atomtype
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MatomtypeTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("CDK atomtype Tests");

        suite.addTest(new JUnit4TestAdapter(AtomtypeCoverageTest.class));

        // cdk.config.atomtype
        suite.addTest(new JUnit4TestAdapter(OWLAtomTypeMappingHandlerTest.class));
        suite.addTest(new JUnit4TestAdapter(OWLAtomTypeMappingReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(SybylAtomTypeMatcherTest.class));

        return suite;
    }
    
}
