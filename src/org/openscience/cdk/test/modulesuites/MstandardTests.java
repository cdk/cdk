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

package org.openscience.cdk.test.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.test.StandardCoverageTest;
import org.openscience.cdk.test.aromaticity.AromaticityCalculatorTest;
import org.openscience.cdk.test.aromaticity.CDKHueckelAromaticityDetectorTest;
import org.openscience.cdk.test.atomtype.EStateAtomTypeMatcherTest;
import org.openscience.cdk.test.config.SymbolsTest;
import org.openscience.cdk.test.dict.DictRefTest;
import org.openscience.cdk.test.exception.InvalidSmilesExceptionTest;
import org.openscience.cdk.test.exception.UnsupportedChemObjectExceptionTest;
import org.openscience.cdk.test.fingerprint.ExtendedFingerprinterTest;
import org.openscience.cdk.test.fingerprint.FingerprinterTest;
import org.openscience.cdk.test.fingerprint.FingerprinterToolTest;
import org.openscience.cdk.test.fingerprint.GraphOnlyFingerprinterTest;
import org.openscience.cdk.test.geometry.BondToolsTest;
import org.openscience.cdk.test.geometry.CrystalGeometryToolsTest;
import org.openscience.cdk.test.geometry.GeometryToolsInternalCoordinatesTest;
import org.openscience.cdk.test.geometry.GeometryToolsTest;
import org.openscience.cdk.test.graph.*;
import org.openscience.cdk.test.graph.invariant.CanonicalLabelerTest;
import org.openscience.cdk.test.graph.invariant.MorganNumbersToolsTest;
import org.openscience.cdk.test.graph.matrix.ConnectionMatrixTest;
import org.openscience.cdk.test.graph.rebond.BsptTest;
import org.openscience.cdk.test.graph.rebond.PointTest;
import org.openscience.cdk.test.graph.rebond.RebondToolTest;
import org.openscience.cdk.test.isomorphism.IsomorphismTesterTest;
import org.openscience.cdk.test.isomorphism.UniversalIsomorphismTesterTest;
import org.openscience.cdk.test.isomorphism.mcss.RGraphTest;
import org.openscience.cdk.test.isomorphism.mcss.RMapTest;
import org.openscience.cdk.test.isomorphism.mcss.RNodeTest;
import org.openscience.cdk.test.math.FortranFormatTest;
import org.openscience.cdk.test.math.MathToolsTest;
import org.openscience.cdk.test.math.PrimesTest;
import org.openscience.cdk.test.math.RandomNumbersToolTest;
import org.openscience.cdk.test.qsar.DescriptorSpecificationTest;
import org.openscience.cdk.test.qsar.DescriptorValueTest;
import org.openscience.cdk.test.qsar.result.*;
import org.openscience.cdk.test.ringsearch.*;
import org.openscience.cdk.test.ringsearch.cyclebasis.CycleBasisTest;
import org.openscience.cdk.test.ringsearch.cyclebasis.SimpleCycleBasisTest;
import org.openscience.cdk.test.ringsearch.cyclebasis.SimpleCycleTest;
import org.openscience.cdk.test.smiles.InvPairTest;
import org.openscience.cdk.test.tools.*;
import org.openscience.cdk.test.tools.manipulator.*;
import org.openscience.cdk.test.validate.ProblemMarkerTest;

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
        suite.addTest(new JUnit4TestAdapter(AromaticityCalculatorTest.class));
        suite.addTest(new JUnit4TestAdapter(DictRefTest.class));
        suite.addTest(CrystalGeometryToolsTest.suite());
        suite.addTest(GeometryToolsTest.suite());
        suite.addTest(MinimalPathIteratorTest.suite());
        suite.addTest(new JUnit4TestAdapter(ConnectivityCheckerTest.class));
        suite.addTest(BiconnectivityInspectorTest.suite());
        suite.addTest(UniversalIsomorphismTesterTest.suite());
        suite.addTest(IsomorphismTesterTest.suite());
        suite.addTest(RMapTest.suite());
        suite.addTest(RGraphTest.suite());
        suite.addTest(RNodeTest.suite());
        suite.addTest(new JUnit4TestAdapter(MathToolsTest.class));
        suite.addTest(AllRingsFinderTest.suite());
        suite.addTest(RingPartitionerTest.suite());
        suite.addTest(SimpleCycleTest.suite());
        suite.addTest(SimpleCycleBasisTest.suite());
        suite.addTest(CycleBasisTest.suite());
        suite.addTest(DeAromatizationToolTest.suite());
        suite.addTest(FormatStringBufferTest.suite());
        suite.addTest(new JUnit4TestAdapter(IDCreatorTest.class));
        suite.addTest(new JUnit4TestAdapter(MFAnalyserTest.class));
        suite.addTest(MoleculeSetManipulatorTest.suite());
        suite.addTest(new JUnit4TestAdapter(ReactionManipulatorTest.class));
        suite.addTest(AtomContainerManipulatorTest.suite());
        suite.addTest(RingSetManipulatorTest.suite());
        suite.addTest(new JUnit4TestAdapter(RingManipulatorTest.class));
        suite.addTest(AtomContainerSetManipulatorTest.suite());
        suite.addTest(MoleculeSetManipulatorTest.suite());
        suite.addTest(RebondToolTest.suite());
        suite.addTest(CanonicalLabelerTest.suite());
        // from cdk.test.fingerprint
        suite.addTest(FingerprinterTest.suite());
        suite.addTest(GraphOnlyFingerprinterTest.suite());
        suite.addTest(ExtendedFingerprinterTest.suite());
        suite.addTest(FingerprinterToolTest.suite());
        suite.addTest(ProblemMarkerTest.suite());
        suite.addTest(SymbolsTest.suite());
        suite.addTest(GeometryToolsInternalCoordinatesTest.suite());
        suite.addTest(BondToolsTest.suite());
        suite.addTest(new JUnit4TestAdapter(UnsupportedChemObjectExceptionTest.class));
        suite.addTest(new JUnit4TestAdapter(InvalidSmilesExceptionTest.class));
        suite.addTest(AminoAcidManipulatorTest.suite());
        suite.addTest(AtomTypeManipulatorTest.suite());
        suite.addTest(ChemFileManipulatorTest.suite());
        suite.addTest(ChemModelManipulatorTest.suite());
        suite.addTest(ChemSequenceManipulatorTest.suite());
        suite.addTest(ReactionSetManipulatorTest.suite());
        suite.addTest(RingSizeComparatorTest.suite());
        suite.addTest(ElementComparatorTest.suite());
        suite.addTest(new JUnit4TestAdapter(BFSShortestPathTest.class));
        suite.addTest(new JUnit4TestAdapter(MoleculeGraphsTest.class));
        suite.addTest(new JUnit4TestAdapter(ConnectionMatrixTest.class));
        suite.addTest(BsptTest.suite());
        suite.addTest(PointTest.suite());
        suite.addTest(BiconnectivityInspectorTest.suite());
        suite.addTest(MorganNumbersToolsTest.suite());
        suite.addTest(new JUnit4TestAdapter(PathTest.class));
        suite.addTest(new JUnit4TestAdapter(QueueTest.class));
        suite.addTest(SSSRFinderTest.suite());
        suite.addTest(DoubleArrayResultTest.suite());
        suite.addTest(IntegerArrayResultTest.suite());
        suite.addTest(IntegerResultTest.suite());
        suite.addTest(DoubleResultTest.suite());
        suite.addTest(BooleanResultTest.suite());
        suite.addTest(DoubleArrayResultTypeTest.suite());
        suite.addTest(IntegerArrayResultTypeTest.suite());
        suite.addTest(IntegerResultTypeTest.suite());
        suite.addTest(DoubleResultTypeTest.suite());
        suite.addTest(BooleanResultTypeTest.suite());
        suite.addTest(DescriptorValueTest.suite());
        suite.addTest(DescriptorSpecificationTest.suite());
        suite.addTest(new JUnit4TestAdapter(FortranFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PrimesTest.class));
        suite.addTest(new JUnit4TestAdapter(RandomNumbersToolTest.class));
        suite.addTest(InvPairTest.suite());
        suite.addTest(new JUnit4TestAdapter(EStateAtomTypeMatcherTest.class));
        suite.addTest(AtomContainerComparatorTest.suite());
        suite.addTest(new JUnit4TestAdapter(CDKHueckelAromaticityDetectorTest.class));

        return suite;
    }
    
}
