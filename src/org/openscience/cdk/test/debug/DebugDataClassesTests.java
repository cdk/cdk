/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.debug;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test
 */
public class DebugDataClassesTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.debug Tests");
        
        suite.addTest(DebugChemObjectBuilderTest.suite());
        
        suite.addTest(DebugAminoAcidTest.suite());
        suite.addTest(DebugAtomContainerTest.suite());
        suite.addTest(DebugAtomTest.suite());
        suite.addTest(DebugAtomTypeTest.suite());
        suite.addTest(DebugBioPolymerTest.suite());
        suite.addTest(DebugBondTest.suite());
        suite.addTest(DebugChemObjectTest.suite());
        suite.addTest(DebugChemFileTest.suite());
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
        suite.addTest(DebugSetOfAtomContainersTest.suite());
        suite.addTest(DebugSetOfMoleculesTest.suite());
        suite.addTest(DebugSetOfReactionsTest.suite());
        suite.addTest(DebugSingleElectronTest.suite());
        suite.addTest(DebugStrandTest.suite());
        
        return suite;
    }

}
