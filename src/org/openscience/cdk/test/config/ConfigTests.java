/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.config;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.config.atomtypes.AtomTypeHandlerTest;
import org.openscience.cdk.test.config.atomtypes.AtomTypeReaderTest;
import org.openscience.cdk.test.config.isotopes.IsotopeHandlerTest;
import org.openscience.cdk.test.config.isotopes.IsotopeReaderTest;

/**
 * TestSuite that runs all the sample tests in the
 * org.openscience.cdk.config package.
 *
 * @cdk.module test
 */
public class ConfigTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.config Tests");
        suite.addTest(AtomTypeFactoryTest.suite());
        suite.addTest(IsotopeFactoryTest.suite());
        suite.addTest(TXTBasedAtomTypeConfiguratorTest.suite());
        suite.addTest(CDKBasedAtomTypeConfiguratorTest.suite());
        // config.isotopes package
        suite.addTest(IsotopeHandlerTest.suite());
        suite.addTest(IsotopeReaderTest.suite());
        // config.atomtypes package
        suite.addTest(AtomTypeHandlerTest.suite());
        suite.addTest(AtomTypeReaderTest.suite());
        return suite;
    }

}
