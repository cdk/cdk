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
package org.openscience.cdk.test.modulesuites;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.AtomtypeCoverageTest;
import org.openscience.cdk.test.atomtype.CDKAtomTypeMatcherTest;
import org.openscience.cdk.test.atomtype.HybridizationMatcherTest;
import org.openscience.cdk.test.atomtype.HybridizationStateATMatcherTest;
import org.openscience.cdk.test.atomtype.StructGenAtomTypeGuesserTest;
import org.openscience.cdk.test.atomtype.StructGenMatcherTest;
import org.openscience.cdk.test.atomtype.ValencyMatcherTest;
import org.openscience.cdk.test.graph.PathToolsTest;
import org.openscience.cdk.test.graph.SpanningTreeTest;
import org.openscience.cdk.test.graph.matrix.AdjacencyMatrixTest;

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

        suite.addTest(AtomtypeCoverageTest.suite());

        // basic helper algorithms
        suite.addTest(AdjacencyMatrixTest.suite());
        suite.addTest(PathToolsTest.suite());
        suite.addTest(SpanningTreeTest.suite());
        
        // cdk.atomtype
        suite.addTest(CDKAtomTypeMatcherTest.suite());
        suite.addTest(HybridizationStateATMatcherTest.suite());
        suite.addTest(HybridizationMatcherTest.suite());
        suite.addTest(StructGenMatcherTest.suite());
        suite.addTest(ValencyMatcherTest.suite());
        suite.addTest(StructGenAtomTypeGuesserTest.suite());

        return suite;
    }
    
}
