/* $Revision: 7635 $ $Author: egonw $ $Date: 2007-01-04 18:32:54 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.atomtype.StructGenAtomTypeGuesserTest;
import org.openscience.cdk.atomtype.StructGenMatcherTest;
import org.openscience.cdk.coverage.StructgenCoverageTest;
import org.openscience.cdk.structgen.RandomStructureGeneratorTest;
import org.openscience.cdk.structgen.VicinitySamplerTest;
import org.openscience.cdk.structgen.stochastic.PartialFilledStructureMergerTest;
import org.openscience.cdk.structgen.stochastic.operator.ChemGraphTest;
import org.openscience.cdk.structgen.stochastic.operator.CrossoverMachineTest;

/**
 * TestSuite that runs all the sample tests for the structgen module.
 *
 * @cdk.module  test-structgen
 */
public class MstructgenTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("JUnit tests for the structgen module");

        suite.addTest(StructgenCoverageTest.suite());

        suite.addTest(new JUnit4TestAdapter(StructGenMatcherTest.class));
        suite.addTest(new JUnit4TestAdapter(StructGenAtomTypeGuesserTest.class));

        suite.addTest(RandomStructureGeneratorTest.suite());
        suite.addTest(VicinitySamplerTest.suite());
        
        // structgen.stoichastic
        suite.addTest(PartialFilledStructureMergerTest.suite());
        suite.addTest(new JUnit4TestAdapter(ChemGraphTest.class));
        suite.addTest(new JUnit4TestAdapter(CrossoverMachineTest.class));

        return suite;
    }
    
}
