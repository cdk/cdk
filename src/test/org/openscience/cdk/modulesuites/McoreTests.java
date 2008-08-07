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

import org.openscience.cdk.CDKConstantsTest;
import org.openscience.cdk.config.AtomTypeFactoryTest;
import org.openscience.cdk.config.CDKBasedAtomTypeConfiguratorTest;
import org.openscience.cdk.config.IsotopeFactoryTest;
import org.openscience.cdk.config.OWLBasedAtomTypeConfiguratorTest;
import org.openscience.cdk.config.TXTBasedAtomTypeConfiguratorTest;
import org.openscience.cdk.config.atomtypes.AtomTypeHandlerTest;
import org.openscience.cdk.config.atomtypes.AtomTypeReaderTest;
import org.openscience.cdk.config.atomtypes.OWLAtomTypeHandlerTest;
import org.openscience.cdk.config.atomtypes.OWLAtomTypeReaderTest;
import org.openscience.cdk.config.isotopes.IsotopeHandlerTest;
import org.openscience.cdk.config.isotopes.IsotopeReaderTest;
import org.openscience.cdk.coverage.CoreCoverageTest;
import org.openscience.cdk.exception.CDKExceptionTest;
import org.openscience.cdk.exception.NoSuchAtomExceptionTest;
import org.openscience.cdk.exception.NoSuchAtomTypeExceptionTest;
import org.openscience.cdk.tools.DataFeaturesTest;
import org.openscience.cdk.tools.LoggingToolTest;

/**
 * TestSuite that runs all the tests for the CDK core module.
 *
 * @cdk.module  test-core
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class McoreTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("CDK core Tests");

        suite.addTest(new JUnit4TestAdapter(CoreCoverageTest.class));

        // make sure to check it against src/test-core.javafiles
        // before each release!
        suite.addTest(new JUnit4TestAdapter(CDKConstantsTest.class));
        suite.addTest(new JUnit4TestAdapter(DataFeaturesTest.class));

        // cdk.config
        suite.addTest(new JUnit4TestAdapter(IsotopeFactoryTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomTypeFactoryTest.class));
        suite.addTest(new JUnit4TestAdapter(CDKBasedAtomTypeConfiguratorTest.class));
        suite.addTest(new JUnit4TestAdapter(TXTBasedAtomTypeConfiguratorTest.class));
        suite.addTest(new JUnit4TestAdapter(OWLBasedAtomTypeConfiguratorTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomTypeReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomTypeHandlerTest.class));
        suite.addTest(new JUnit4TestAdapter(OWLAtomTypeReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(OWLAtomTypeHandlerTest.class));
        suite.addTest(new JUnit4TestAdapter(IsotopeReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(IsotopeHandlerTest.class));
        
        // other
        suite.addTest(new JUnit4TestAdapter(CDKExceptionTest.class));
        suite.addTest(new JUnit4TestAdapter(NoSuchAtomExceptionTest.class));
        suite.addTest(new JUnit4TestAdapter(NoSuchAtomTypeExceptionTest.class));
        suite.addTest(new JUnit4TestAdapter(LoggingToolTest.class));

        return suite;
    }
    
}
