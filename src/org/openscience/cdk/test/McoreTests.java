/* $Revision: 5867 $ $Author: egonw $ $Date: 2006-03-30 02:18:21 +0200 (Thu, 30 Mar 2006) $
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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.atomtype.HybridizationMatcherTest;
import org.openscience.cdk.test.atomtype.HybridizationStateATMatcherTest;
import org.openscience.cdk.test.atomtype.StructGenAtomTypeGuesserTest;
import org.openscience.cdk.test.atomtype.StructGenMatcherTest;
import org.openscience.cdk.test.atomtype.ValencyMatcherTest;
import org.openscience.cdk.test.config.AtomTypeFactoryTest;
import org.openscience.cdk.test.config.CDKBasedAtomTypeConfiguratorTest;
import org.openscience.cdk.test.config.IsotopeFactoryTest;
import org.openscience.cdk.test.config.TXTBasedAtomTypeConfiguratorTest;
import org.openscience.cdk.test.config.atomtypes.AtomTypeHandlerTest;
import org.openscience.cdk.test.config.atomtypes.AtomTypeReaderTest;
import org.openscience.cdk.test.config.isotopes.IsotopeHandlerTest;
import org.openscience.cdk.test.config.isotopes.IsotopeReaderTest;
import org.openscience.cdk.test.exception.CDKExceptionTest;
import org.openscience.cdk.test.exception.NoSuchAtomExceptionTest;
import org.openscience.cdk.test.exception.NoSuchAtomTypeExceptionTest;
import org.openscience.cdk.test.tools.DataFeaturesTest;
import org.openscience.cdk.test.tools.LoggingToolTest;

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

        suite.addTest(CoreCoverageTest.suite());

        // make sure to check it agains src/test-core.javafiles
        // before each release!
        suite.addTest(CDKConstantsTest.suite());
        suite.addTest(DataFeaturesTest.suite());

        // cdk.atomtype
        suite.addTest(HybridizationStateATMatcherTest.suite());
        suite.addTest(HybridizationMatcherTest.suite());
        suite.addTest(StructGenMatcherTest.suite());
        suite.addTest(ValencyMatcherTest.suite());
        suite.addTest(StructGenAtomTypeGuesserTest.suite());

        // cdk.config
        suite.addTest(IsotopeFactoryTest.suite());
        suite.addTest(AtomTypeFactoryTest.suite());
        suite.addTest(CDKBasedAtomTypeConfiguratorTest.suite());
        suite.addTest(TXTBasedAtomTypeConfiguratorTest.suite());
        suite.addTest(AtomTypeReaderTest.suite());
        suite.addTest(AtomTypeHandlerTest.suite());
        suite.addTest(IsotopeReaderTest.suite());
        suite.addTest(IsotopeHandlerTest.suite());
        
        // other
        suite.addTest(CDKExceptionTest.suite());
        suite.addTest(NoSuchAtomExceptionTest.suite());
        suite.addTest(NoSuchAtomTypeExceptionTest.suite());
        suite.addTest(LoggingToolTest.suite());

        return suite;
    }
    
}
