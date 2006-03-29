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
package org.openscience.cdk.test.tools.manipulator;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the sample tests in the
 * org.openscience.cdk.tools package.
 *
 * @cdk.module test
 */
public class ManipulatorsTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.tools.manipulator Tests");
        suite.addTest(ReactionManipulatorTest.suite());
        suite.addTest(SetOfMoleculesManipulatorTest.suite());
        suite.addTest(AtomContainerManipulatorTest.suite());
        suite.addTest(SetOfAtomContainersManipulatorTest.suite());
        return suite;
    }

}
