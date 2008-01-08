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

import org.openscience.cdk.test.nonotify.NNAminoAcidTest;
import org.openscience.cdk.test.nonotify.NNAtomContainerSetTest;
import org.openscience.cdk.test.nonotify.NNAtomContainerTest;
import org.openscience.cdk.test.nonotify.NNAtomParityTest;
import org.openscience.cdk.test.nonotify.NNAtomTest;
import org.openscience.cdk.test.nonotify.NNAtomTypeTest;
import org.openscience.cdk.test.nonotify.NNBioPolymerTest;
import org.openscience.cdk.test.nonotify.NNBondTest;
import org.openscience.cdk.test.nonotify.NNChemFileTest;
import org.openscience.cdk.test.nonotify.NNChemModelTest;
import org.openscience.cdk.test.nonotify.NNChemObjectBuilderTest;
import org.openscience.cdk.test.nonotify.NNChemObjectTest;
import org.openscience.cdk.test.nonotify.NNChemSequenceTest;
import org.openscience.cdk.test.nonotify.NNCrystalTest;
import org.openscience.cdk.test.nonotify.NNElectronContainerTest;
import org.openscience.cdk.test.nonotify.NNElementTest;
import org.openscience.cdk.test.nonotify.NNIsotopeTest;
import org.openscience.cdk.test.nonotify.NNLonePairTest;
import org.openscience.cdk.test.nonotify.NNMoleculeSetTest;
import org.openscience.cdk.test.nonotify.NNMoleculeTest;
import org.openscience.cdk.test.nonotify.NNMonomerTest;
import org.openscience.cdk.test.nonotify.NNPolymerTest;
import org.openscience.cdk.test.nonotify.NNPseudoAtomTest;
import org.openscience.cdk.test.nonotify.NNReactionSetTest;
import org.openscience.cdk.test.nonotify.NNReactionTest;
import org.openscience.cdk.test.nonotify.NNRingSetTest;
import org.openscience.cdk.test.nonotify.NNRingTest;
import org.openscience.cdk.test.nonotify.NNSingleElectronTest;
import org.openscience.cdk.test.nonotify.NNStrandTest;

/**
 * TestSuite that runs the nonotify tests.
 *
 * @cdk.module  test-nonotify
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MnonotifyTests {
    
    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.debug Tests");
        
        suite.addTest(new JUnit4TestAdapter(NNChemObjectBuilderTest.class));
        
        suite.addTest(new JUnit4TestAdapter(NNAminoAcidTest.class));
        suite.addTest(new JUnit4TestAdapter(NNAtomContainerTest.class));
        suite.addTest(new JUnit4TestAdapter(NNAtomParityTest.class));
        suite.addTest(new JUnit4TestAdapter(NNAtomTest.class));
        suite.addTest(new JUnit4TestAdapter(NNAtomTypeTest.class));
        suite.addTest(new JUnit4TestAdapter(NNBioPolymerTest.class));
        suite.addTest(new JUnit4TestAdapter(NNBondTest.class));
        suite.addTest(new JUnit4TestAdapter(NNChemObjectTest.class));
        suite.addTest(new JUnit4TestAdapter(NNChemFileTest.class));
        suite.addTest(new JUnit4TestAdapter(NNChemModelTest.class));
        suite.addTest(new JUnit4TestAdapter(NNChemSequenceTest.class));
        suite.addTest(new JUnit4TestAdapter(NNCrystalTest.class));
        suite.addTest(new JUnit4TestAdapter(NNElectronContainerTest.class));
        suite.addTest(new JUnit4TestAdapter(NNElementTest.class));
        suite.addTest(new JUnit4TestAdapter(NNIsotopeTest.class));
        suite.addTest(new JUnit4TestAdapter(NNLonePairTest.class));
        suite.addTest(new JUnit4TestAdapter(NNMoleculeTest.class));
        suite.addTest(new JUnit4TestAdapter(NNMonomerTest.class));
        suite.addTest(new JUnit4TestAdapter(NNPseudoAtomTest.class));
        suite.addTest(new JUnit4TestAdapter(NNPolymerTest.class));
        suite.addTest(new JUnit4TestAdapter(NNReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(NNRingTest.class));
        suite.addTest(new JUnit4TestAdapter(NNRingSetTest.class));
        suite.addTest(new JUnit4TestAdapter(NNAtomContainerSetTest.class));
        suite.addTest(new JUnit4TestAdapter(NNMoleculeSetTest.class));
        suite.addTest(new JUnit4TestAdapter(NNReactionSetTest.class));
        suite.addTest(new JUnit4TestAdapter(NNSingleElectronTest.class));
        suite.addTest(new JUnit4TestAdapter(NNStrandTest.class));
        
        return suite;
    }
    
}
