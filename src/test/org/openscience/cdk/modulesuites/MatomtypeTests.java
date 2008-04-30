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
package org.openscience.cdk.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomtypeCoverageTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcherTest;
import org.openscience.cdk.atomtype.HybridizationMatcherTest;
import org.openscience.cdk.atomtype.HybridizationStateATMatcherTest;
import org.openscience.cdk.atomtype.ValencyMatcherTest;
import org.openscience.cdk.graph.PathToolsTest;
import org.openscience.cdk.graph.SpanningTreeTest;
import org.openscience.cdk.graph.matrix.AdjacencyMatrixTest;
import org.openscience.cdk.tools.manipulator.BondManipulatorTest;

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

        // basic helper algorithms
        suite.addTest(new JUnit4TestAdapter(BondManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(AdjacencyMatrixTest.class));
        suite.addTest(new JUnit4TestAdapter(PathToolsTest.class));
        suite.addTest(new JUnit4TestAdapter(SpanningTreeTest.class));
        
        // cdk.atomtype
        suite.addTest(new JUnit4TestAdapter(CDKAtomTypeMatcherTest.class));
        suite.addTest(new JUnit4TestAdapter(HybridizationStateATMatcherTest.class));
        suite.addTest(new JUnit4TestAdapter(HybridizationMatcherTest.class));
        suite.addTest(new JUnit4TestAdapter(ValencyMatcherTest.class));

        return suite;
    }
    
}
