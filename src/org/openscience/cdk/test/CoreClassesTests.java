/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test
 */
public class CoreClassesTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.core Tests");
        suite.addTest(AtomContainerTest.suite());
        suite.addTest(AtomEnumerationTest.suite());
        suite.addTest(AtomParityTest.suite());
        suite.addTest(AtomTest.suite());
        suite.addTest(AtomTypeTest.suite());
        suite.addTest(BioPolymerTest.suite());
        suite.addTest(BondTest.suite());
        suite.addTest(ChemModelTest.suite());
        suite.addTest(ChemObjectTest.suite());
        suite.addTest(ChemSequenceTest.suite());
        suite.addTest(CrystalTest.suite());
        suite.addTest(ElectronContainerTest.suite());
        suite.addTest(ElementTest.suite());
        suite.addTest(IsotopeTest.suite());
        suite.addTest(LonePairTest.suite());
        suite.addTest(MoleculeTest.suite());
        suite.addTest(MonomerTest.suite());
        suite.addTest(PolymerTest.suite());
        suite.addTest(PseudoAtomTest.suite());
        suite.addTest(ReactionTest.suite());
        suite.addTest(RingTest.suite());
        suite.addTest(RingSetTest.suite());
        suite.addTest(SetOfAtomContainersTest.suite());
        suite.addTest(SetOfMoleculesTest.suite());
        suite.addTest(SingleElectronTest.suite());
        suite.addTest(ChangeEventPropagationTest.suite());
        return suite;
    }

}
