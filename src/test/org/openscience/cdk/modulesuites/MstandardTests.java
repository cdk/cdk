/* $Revision$ $Author$ $Date$
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

import org.openscience.cdk.aromaticity.AromaticityCalculatorTest;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetectorTest;
import org.openscience.cdk.atomtype.EStateAtomTypeMatcherTest;
import org.openscience.cdk.config.SymbolsTest;
import org.openscience.cdk.coverage.StandardCoverageTest;
import org.openscience.cdk.dict.DictRefTest;
import org.openscience.cdk.exception.InvalidSmilesExceptionTest;
import org.openscience.cdk.exception.UnsupportedChemObjectExceptionTest;
import org.openscience.cdk.fingerprint.FingerprinterTest;
import org.openscience.cdk.fingerprint.FingerprinterToolTest;
import org.openscience.cdk.fingerprint.GraphOnlyFingerprinterTest;
import org.openscience.cdk.geometry.BondToolsTest;
import org.openscience.cdk.geometry.CrystalGeometryToolsTest;
import org.openscience.cdk.graph.BFSShortestPathTest;
import org.openscience.cdk.graph.BiconnectivityInspectorTest;
import org.openscience.cdk.graph.ConnectivityCheckerTest;
import org.openscience.cdk.graph.MinimalPathIteratorTest;
import org.openscience.cdk.graph.MoleculeGraphsTest;
import org.openscience.cdk.graph.invariant.CanonicalLabelerTest;
import org.openscience.cdk.graph.invariant.MorganNumbersToolsTest;
import org.openscience.cdk.graph.matrix.ConnectionMatrixTest;
import org.openscience.cdk.graph.rebond.BsptTest;
import org.openscience.cdk.graph.rebond.PointTest;
import org.openscience.cdk.graph.rebond.RebondToolTest;
import org.openscience.cdk.isomorphism.IsomorphismTesterTest;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTesterTest;
import org.openscience.cdk.isomorphism.mcss.RGraphTest;
import org.openscience.cdk.isomorphism.mcss.RMapTest;
import org.openscience.cdk.isomorphism.mcss.RNodeTest;
import org.openscience.cdk.math.FortranFormatTest;
import org.openscience.cdk.math.MathToolsTest;
import org.openscience.cdk.math.PrimesTest;
import org.openscience.cdk.math.RandomNumbersToolTest;
import org.openscience.cdk.qsar.DescriptorSpecificationTest;
import org.openscience.cdk.qsar.DescriptorValueTest;
import org.openscience.cdk.qsar.result.BooleanResultTest;
import org.openscience.cdk.qsar.result.BooleanResultTypeTest;
import org.openscience.cdk.qsar.result.DoubleArrayResultTest;
import org.openscience.cdk.qsar.result.DoubleArrayResultTypeTest;
import org.openscience.cdk.qsar.result.DoubleResultTest;
import org.openscience.cdk.qsar.result.DoubleResultTypeTest;
import org.openscience.cdk.qsar.result.IntegerArrayResultTest;
import org.openscience.cdk.qsar.result.IntegerArrayResultTypeTest;
import org.openscience.cdk.qsar.result.IntegerResultTest;
import org.openscience.cdk.qsar.result.IntegerResultTypeTest;
import org.openscience.cdk.ringsearch.AllRingsFinderTest;
import org.openscience.cdk.ringsearch.PathTest;
import org.openscience.cdk.ringsearch.QueueTest;
import org.openscience.cdk.ringsearch.RingPartitionerTest;
import org.openscience.cdk.ringsearch.SSSRFinderTest;
import org.openscience.cdk.ringsearch.cyclebasis.CycleBasisTest;
import org.openscience.cdk.ringsearch.cyclebasis.SimpleCycleBasisTest;
import org.openscience.cdk.ringsearch.cyclebasis.SimpleCycleTest;
import org.openscience.cdk.smiles.InvPairTest;
import org.openscience.cdk.tools.ElementComparatorTest;
import org.openscience.cdk.tools.FormatStringBufferTest;
import org.openscience.cdk.tools.HOSECodeGeneratorTest;
import org.openscience.cdk.tools.IDCreatorTest;
import org.openscience.cdk.tools.LonePairElectronCheckerTest;
import org.openscience.cdk.tools.manipulator.AminoAcidManipulatorTest;
import org.openscience.cdk.tools.manipulator.AtomContainerComparatorTest;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulatorTest;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulatorTest;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulatorTest;
import org.openscience.cdk.tools.manipulator.ChemFileManipulatorTest;
import org.openscience.cdk.tools.manipulator.ChemModelManipulatorTest;
import org.openscience.cdk.tools.manipulator.ChemSequenceManipulatorTest;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulatorTest;
import org.openscience.cdk.tools.manipulator.ReactionManipulatorTest;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulatorTest;
import org.openscience.cdk.tools.manipulator.RingManipulatorTest;
import org.openscience.cdk.tools.manipulator.RingSetManipulatorTest;
import org.openscience.cdk.tools.manipulator.RingSizeComparatorTest;
import org.openscience.cdk.validate.ProblemMarkerTest;

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
        
        // make sure to check it against src/test-standard.files
        // before each release!
        suite.addTest(new JUnit4TestAdapter(AromaticityCalculatorTest.class));
        suite.addTest(new JUnit4TestAdapter(DictRefTest.class));
        suite.addTest(new JUnit4TestAdapter(CrystalGeometryToolsTest.class));
        suite.addTest(new JUnit4TestAdapter(MinimalPathIteratorTest.class));
        suite.addTest(new JUnit4TestAdapter(ConnectivityCheckerTest.class));
        suite.addTest(new JUnit4TestAdapter(UniversalIsomorphismTesterTest.class));
        suite.addTest(new JUnit4TestAdapter(IsomorphismTesterTest.class));
        suite.addTest(new JUnit4TestAdapter(RMapTest.class));
        suite.addTest(new JUnit4TestAdapter(RGraphTest.class));
        suite.addTest(new JUnit4TestAdapter(RNodeTest.class));
        suite.addTest(new JUnit4TestAdapter(MathToolsTest.class));
        suite.addTest(new JUnit4TestAdapter(AllRingsFinderTest.class));
        suite.addTest(new JUnit4TestAdapter(RingPartitionerTest.class));
        suite.addTest(new JUnit4TestAdapter(SimpleCycleTest.class));
        suite.addTest(new JUnit4TestAdapter(SimpleCycleBasisTest.class));
        suite.addTest(new JUnit4TestAdapter(CycleBasisTest.class));
        suite.addTest(new JUnit4TestAdapter(FormatStringBufferTest.class));
        suite.addTest(new JUnit4TestAdapter(IDCreatorTest.class));
        suite.addTest(new JUnit4TestAdapter(MoleculeSetManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(ReactionManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomContainerManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(RingSetManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(RingManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomContainerSetManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(MoleculeSetManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(RebondToolTest.class));
        suite.addTest(new JUnit4TestAdapter(CanonicalLabelerTest.class));
        // from cdk.test.fingerprint
        suite.addTest(new JUnit4TestAdapter(FingerprinterTest.class));
        suite.addTest(new JUnit4TestAdapter(GraphOnlyFingerprinterTest.class));
        suite.addTest(new JUnit4TestAdapter(FingerprinterToolTest.class));
        suite.addTest(new JUnit4TestAdapter(ProblemMarkerTest.class));
        suite.addTest(new JUnit4TestAdapter(SymbolsTest.class));
        suite.addTest(new JUnit4TestAdapter(BondToolsTest.class));
        suite.addTest(new JUnit4TestAdapter(UnsupportedChemObjectExceptionTest.class));
        suite.addTest(new JUnit4TestAdapter(InvalidSmilesExceptionTest.class));
        suite.addTest(new JUnit4TestAdapter(AminoAcidManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomTypeManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(ChemFileManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(ChemModelManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(ChemSequenceManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(ReactionSetManipulatorTest.class));
        suite.addTest(new JUnit4TestAdapter(RingSizeComparatorTest.class));
        suite.addTest(new JUnit4TestAdapter(ElementComparatorTest.class));
        suite.addTest(new JUnit4TestAdapter(BFSShortestPathTest.class));
        suite.addTest(new JUnit4TestAdapter(MoleculeGraphsTest.class));
        suite.addTest(new JUnit4TestAdapter(ConnectionMatrixTest.class));
        suite.addTest(new JUnit4TestAdapter(BsptTest.class));
        suite.addTest(new JUnit4TestAdapter(PointTest.class));
        suite.addTest(new JUnit4TestAdapter(BiconnectivityInspectorTest.class));
        suite.addTest(new JUnit4TestAdapter(MorganNumbersToolsTest.class));
        suite.addTest(new JUnit4TestAdapter(PathTest.class));
        suite.addTest(new JUnit4TestAdapter(QueueTest.class));
        suite.addTest(new JUnit4TestAdapter(SSSRFinderTest.class));
        suite.addTest(new JUnit4TestAdapter(DoubleArrayResultTest.class));
        suite.addTest(new JUnit4TestAdapter(IntegerArrayResultTest.class));
        suite.addTest(new JUnit4TestAdapter(IntegerResultTest.class));
        suite.addTest(new JUnit4TestAdapter(DoubleResultTest.class));
        suite.addTest(new JUnit4TestAdapter(BooleanResultTest.class));
        suite.addTest(new JUnit4TestAdapter(DoubleArrayResultTypeTest.class));
        suite.addTest(new JUnit4TestAdapter(IntegerArrayResultTypeTest.class));
        suite.addTest(new JUnit4TestAdapter(IntegerResultTypeTest.class));
        suite.addTest(new JUnit4TestAdapter(DoubleResultTypeTest.class));
        suite.addTest(new JUnit4TestAdapter(BooleanResultTypeTest.class));
        suite.addTest(new JUnit4TestAdapter(DescriptorValueTest.class));
        suite.addTest(new JUnit4TestAdapter(DescriptorSpecificationTest.class));
        suite.addTest(new JUnit4TestAdapter(FortranFormatTest.class));
        suite.addTest(new JUnit4TestAdapter(PrimesTest.class));
        suite.addTest(new JUnit4TestAdapter(RandomNumbersToolTest.class));
        suite.addTest(new JUnit4TestAdapter(InvPairTest.class));
        suite.addTest(new JUnit4TestAdapter(EStateAtomTypeMatcherTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomContainerComparatorTest.class));
        suite.addTest(new JUnit4TestAdapter(CDKHueckelAromaticityDetectorTest.class));
        suite.addTest(new JUnit4TestAdapter(HOSECodeGeneratorTest.class));
        suite.addTest(new JUnit4TestAdapter(LonePairElectronCheckerTest.class));

        return suite;
    }
    
}
