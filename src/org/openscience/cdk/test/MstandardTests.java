/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-30 02:18:21 +0200 (Thu, 30 Mar 2006) $
 * $Revision: 5867 $
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

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.applications.APIVersionTesterTest;
import org.openscience.cdk.test.aromaticity.HueckelAromaticityDetectorTest;
import org.openscience.cdk.test.dict.DictRefTest;
import org.openscience.cdk.test.fingerprint.ExtendedFingerprinterTest;
import org.openscience.cdk.test.fingerprint.FingerprinterTest;
import org.openscience.cdk.test.geometry.CrystalGeometryToolsTest;
import org.openscience.cdk.test.geometry.GeometryToolsTest;
import org.openscience.cdk.test.graph.ConnectivityCheckerTest;
import org.openscience.cdk.test.graph.PathToolsTest;
import org.openscience.cdk.test.graph.invariant.CanonicalLabelerTest;
import org.openscience.cdk.test.graph.rebond.RebondToolTest;
import org.openscience.cdk.test.isomorphism.IsomorphismTesterTest;
import org.openscience.cdk.test.isomorphism.UniversalIsomorphismTesterTest;
import org.openscience.cdk.test.math.MathToolsTest;
import org.openscience.cdk.test.ringsearch.AllRingsFinderTest;
import org.openscience.cdk.test.ringsearch.RingPartitionerTest;
import org.openscience.cdk.test.tools.DeAromatizationToolTest;
import org.openscience.cdk.test.tools.FormatStringBufferTest;
import org.openscience.cdk.test.tools.IDCreatorTest;
import org.openscience.cdk.test.tools.MFAnalyserTest;
import org.openscience.cdk.test.tools.manipulator.AtomContainerManipulatorTest;
import org.openscience.cdk.test.tools.manipulator.AtomContainerSetManipulatorTest;
import org.openscience.cdk.test.tools.manipulator.MoleculeSetManipulatorTest;
import org.openscience.cdk.test.tools.manipulator.ReactionManipulatorTest;
import org.openscience.cdk.test.tools.manipulator.RingSetManipulatorTest;

/**
 * TestSuite that runs all the tests for the CDK standard module.
 *
 * @cdk.module  test-standard
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MstandardTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("CDK standard Tests");

        suite.addTest(StandardCoverageTest.suite());
        
        // make sure to check it agains src/test-standard.files
        // before each release!
        suite.addTest(APIVersionTesterTest.suite());
        suite.addTest(HueckelAromaticityDetectorTest.suite());
        suite.addTest(DictRefTest.suite());
        suite.addTest(CrystalGeometryToolsTest.suite());
        suite.addTest(GeometryToolsTest.suite());
        suite.addTest(PathToolsTest.suite());
        // suite.addTest(MinimalPathIteratorTest.suite());
        suite.addTest(ConnectivityCheckerTest.suite());
        // suite.addTest(BiconnectivityInspectorTest.suite());
        suite.addTest(UniversalIsomorphismTesterTest.suite());
        suite.addTest(IsomorphismTesterTest.suite());
        suite.addTest(MathToolsTest.suite());
        suite.addTest(AllRingsFinderTest.suite());
        suite.addTest(RingPartitionerTest.suite());
        // suite.addTest(SimpleCycleBasisTest.suite());
        // suite.addTest(CycleBasisTest.suite());
        suite.addTest(DeAromatizationToolTest.suite());
        suite.addTest(FormatStringBufferTest.suite());
        suite.addTest(IDCreatorTest.suite());
        suite.addTest(MFAnalyserTest.suite());
        suite.addTest(MoleculeSetManipulatorTest.suite());
        suite.addTest(ReactionManipulatorTest.suite());
        suite.addTest(AtomContainerManipulatorTest.suite());
        suite.addTest(RingSetManipulatorTest.suite());
        suite.addTest(AtomContainerSetManipulatorTest.suite());
        suite.addTest(MoleculeSetManipulatorTest.suite());
        suite.addTest(RebondToolTest.suite());
        suite.addTest(CanonicalLabelerTest.suite());
        // from cdk.test.fingerprint
        suite.addTest(FingerprinterTest.suite());
        suite.addTest(ExtendedFingerprinterTest.suite());

        return suite;
    }
    
}
