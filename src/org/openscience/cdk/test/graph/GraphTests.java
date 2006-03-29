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
package org.openscience.cdk.test.graph;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the sample tests in the
 * org.openscience.cdk.graph package.
 *
 * @cdk.module test-extra
 */
public class GraphTests {

    public static Test suite () {
        TestSuite suite = new TestSuite("The cdk.graph Tests");
        suite.addTest(ConnectivityCheckerTest.suite());
        suite.addTest(PathToolsTest.suite());
	suite.addTest(AtomContainerPermutorTest.suite());
        //suite.addTest(BiconnectivityInspectorTest.suite());
        //suite.addTest(MinimalPathIteratorTest.suite());
        //suite.addTest(RebondToolTest.suite());
        //suite.addTest(EquivalentClassPartitionerTest.suite());
        //suite.addTest(MorganNumberToolsTest.suite());
        //suite.addTest(ConjugatedPiSystemsDetectorTest.suite());
        return suite;
    }

}
