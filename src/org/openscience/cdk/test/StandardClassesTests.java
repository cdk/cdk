/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.test.config.AtomTypeFactoryTest;
import org.openscience.cdk.test.config.IsotopeFactoryTest;
import org.openscience.cdk.test.geometry.CrystalGeometryToolsTest;
import org.openscience.cdk.test.isomorphism.IsomorphismTesterTest;
import org.openscience.cdk.test.smiles.SmilesParserTest;
import org.openscience.cdk.test.tools.manipulator.ReactionManipulatorTest;

/**
 * TestSuite that runs all the sample tests
 *
 * @cdk.module test-extra
 */
public class StandardClassesTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.standard Tests");
        suite.addTest(AtomTypeFactoryTest.suite());
        suite.addTest(IsotopeFactoryTest.suite());
        suite.addTest(CrystalGeometryToolsTest.suite());
        suite.addTest(SmilesParserTest.suite());
        suite.addTest(ReactionManipulatorTest.suite());
        suite.addTest(IsomorphismTesterTest.suite());
        return suite;
    }

}
