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

package org.openscience.cdk.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.debug.DebugAminoAcidTest;
import org.openscience.cdk.debug.DebugAtomContainerSetTest;
import org.openscience.cdk.debug.DebugAtomContainerTest;
import org.openscience.cdk.debug.DebugAtomParityTest;
import org.openscience.cdk.debug.DebugAtomTest;
import org.openscience.cdk.debug.DebugAtomTypeTest;
import org.openscience.cdk.debug.DebugBioPolymerTest;
import org.openscience.cdk.debug.DebugBondTest;
import org.openscience.cdk.debug.DebugChemFileTest;
import org.openscience.cdk.debug.DebugChemModelTest;
import org.openscience.cdk.debug.DebugChemObjectBuilderTest;
import org.openscience.cdk.debug.DebugChemObjectTest;
import org.openscience.cdk.debug.DebugChemSequenceTest;
import org.openscience.cdk.debug.DebugCrystalTest;
import org.openscience.cdk.debug.DebugElectronContainerTest;
import org.openscience.cdk.debug.DebugElementTest;
import org.openscience.cdk.debug.DebugFragmentAtomTest;
import org.openscience.cdk.debug.DebugIsotopeTest;
import org.openscience.cdk.debug.DebugLonePairTest;
import org.openscience.cdk.debug.DebugMoleculeSetTest;
import org.openscience.cdk.debug.DebugMoleculeTest;
import org.openscience.cdk.debug.DebugMonomerTest;
import org.openscience.cdk.debug.DebugPDBAtomTest;
import org.openscience.cdk.debug.DebugPDBMonomerTest;
import org.openscience.cdk.debug.DebugPDBPolymerTest;
import org.openscience.cdk.debug.DebugPDBStructureTest;
import org.openscience.cdk.debug.DebugPolymerTest;
import org.openscience.cdk.debug.DebugPseudoAtomTest;
import org.openscience.cdk.debug.DebugReactionSetTest;
import org.openscience.cdk.debug.DebugReactionTest;
import org.openscience.cdk.debug.DebugRingSetTest;
import org.openscience.cdk.debug.DebugRingTest;
import org.openscience.cdk.debug.DebugSingleElectronTest;
import org.openscience.cdk.debug.DebugStrandTest;

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
        suite.addTest(new JUnit4TestAdapter(DebugChemObjectTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugChemFileTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugChemModelTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugChemSequenceTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugCrystalTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugElectronContainerTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugElementTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugFragmentAtomTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugIsotopeTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugLonePairTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugMoleculeTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugMonomerTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugPseudoAtomTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugPolymerTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugRingTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugRingSetTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugAtomContainerSetTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugMoleculeSetTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugReactionSetTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugSingleElectronTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugStrandTest.class));
        
        // tests from test.protein.data
        suite.addTest(new JUnit4TestAdapter(DebugPDBAtomTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugPDBMonomerTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugPDBPolymerTest.class));
        suite.addTest(new JUnit4TestAdapter(DebugPDBStructureTest.class));

        return suite;
    }
    
}
