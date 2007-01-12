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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.structgen.RandomStructureGeneratorTest;
import org.openscience.cdk.test.structgen.VicinitySamplerTest;
import org.openscience.cdk.test.structgen.deterministic.EquivalentClassesDeterministicGeneratorTest;
import org.openscience.cdk.test.structgen.deterministic.GENMDeterministicGeneratorTest;
import org.openscience.cdk.test.structgen.stoichastic.PartialFilledStructureMergerTest;
import org.openscience.cdk.test.structgen.stoichastic.operator.ChemGraphTest;
import org.openscience.cdk.test.structgen.stoichastic.operator.CrossoverMachineTest;

/**
 * TestSuite that runs all the sample tests for the structgen module.
 *
 * @cdk.module  test-structgen
 */
public class MstructgenTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("JUnit tests for the structgen module");

        suite.addTest(StructgenCoverageTest.suite());
        
        suite.addTest(RandomStructureGeneratorTest.suite());
        suite.addTest(VicinitySamplerTest.suite());
        suite.addTest(GENMDeterministicGeneratorTest.suite());
        suite.addTest(EquivalentClassesDeterministicGeneratorTest.suite());
        
        // structgen.stoichastic
        suite.addTest(PartialFilledStructureMergerTest.suite());
        suite.addTest(ChemGraphTest.suite());
        suite.addTest(CrossoverMachineTest.suite());

        return suite;
    }
    
}
