/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-06 21:14:11 +0200 (Thu, 06 Apr 2006) $
 * $Revision: 5899 $
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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.debug.DebugAminoAcidTest;
import org.openscience.cdk.test.debug.DebugAtomContainerSetTest;
import org.openscience.cdk.test.debug.DebugAtomContainerTest;
import org.openscience.cdk.test.debug.DebugAtomParityTest;
import org.openscience.cdk.test.debug.DebugAtomTest;
import org.openscience.cdk.test.debug.DebugAtomTypeTest;
import org.openscience.cdk.test.debug.DebugBioPolymerTest;
import org.openscience.cdk.test.debug.DebugBondTest;
import org.openscience.cdk.test.debug.DebugChemFileTest;
import org.openscience.cdk.test.debug.DebugChemModelTest;
import org.openscience.cdk.test.debug.DebugChemObjectBuilderTest;
import org.openscience.cdk.test.debug.DebugChemObjectTest;
import org.openscience.cdk.test.debug.DebugChemSequenceTest;
import org.openscience.cdk.test.debug.DebugCrystalTest;
import org.openscience.cdk.test.debug.DebugElectronContainerTest;
import org.openscience.cdk.test.debug.DebugElementTest;
import org.openscience.cdk.test.debug.DebugIsotopeTest;
import org.openscience.cdk.test.debug.DebugLonePairTest;
import org.openscience.cdk.test.debug.DebugMoleculeSetTest;
import org.openscience.cdk.test.debug.DebugMoleculeTest;
import org.openscience.cdk.test.debug.DebugMonomerTest;
import org.openscience.cdk.test.debug.DebugPolymerTest;
import org.openscience.cdk.test.debug.DebugPseudoAtomTest;
import org.openscience.cdk.test.debug.DebugReactionSetTest;
import org.openscience.cdk.test.debug.DebugReactionTest;
import org.openscience.cdk.test.debug.DebugRingSetTest;
import org.openscience.cdk.test.debug.DebugRingTest;
import org.openscience.cdk.test.debug.DebugSingleElectronTest;
import org.openscience.cdk.test.debug.DebugStrandTest;

/**
 * TestSuite that runs the datadebug tests.
 *
 * @cdk.module  test-datadebug
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MdatadebugTests {
    
    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.debug Tests");
        
        suite.addTest(new JUnit4TestAdapter(DebugChemObjectBuilderTest.class));
        
        suite.addTest(new JUnit4TestAdapter(DebugAminoAcidTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugAtomContainerTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugAtomParityTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugAtomTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugAtomTypeTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugBioPolymerTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugBondTest.class));
        suite.addTest(DebugChemObjectTest.suite());
        suite.addTest(new JUnit4TestAdapter(DebugChemFileTest.class));
        suite.addTest(DebugChemModelTest.suite());
        suite.addTest(DebugChemSequenceTest.suite());
        suite.addTest(DebugCrystalTest.suite());
        suite.addTest(DebugElectronContainerTest.suite());
        suite.addTest(DebugElementTest.suite());
        suite.addTest(DebugIsotopeTest.suite());
        suite.addTest(DebugLonePairTest.suite());
        suite.addTest(DebugMoleculeTest.suite());
        suite.addTest(DebugMonomerTest.suite());
        suite.addTest(DebugPseudoAtomTest.suite());
        suite.addTest(DebugPolymerTest.suite());
        suite.addTest(DebugReactionTest.suite());
        suite.addTest(DebugRingTest.suite());
        suite.addTest(DebugRingSetTest.suite());
        suite.addTest(new JUnit4TestAdapter(DebugAtomContainerSetTest.class));
        suite.addTest(DebugMoleculeSetTest.suite());
        suite.addTest(DebugReactionSetTest.suite());
        suite.addTest(DebugSingleElectronTest.suite());
        suite.addTest(DebugStrandTest.suite());
        
        return suite;
    }
    
}
