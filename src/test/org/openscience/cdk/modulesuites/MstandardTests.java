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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openscience.cdk.aromaticity.AromaticityCalculatorTest;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetectorTest;
import org.openscience.cdk.aromaticity.DoubleBondAcceptingAromaticityDetectorTest;
import org.openscience.cdk.atomtype.EStateAtomTypeMatcherTest;
import org.openscience.cdk.coverage.StandardCoverageTest;
import org.openscience.cdk.dict.DictRefTest;
import org.openscience.cdk.exception.InvalidSmilesExceptionTest;
import org.openscience.cdk.exception.UnsupportedChemObjectExceptionTest;
import org.openscience.cdk.fingerprint.FingerprinterTest;
import org.openscience.cdk.fingerprint.FingerprinterToolTest;
import org.openscience.cdk.fingerprint.GraphOnlyFingerprinterTest;
import org.openscience.cdk.fingerprint.HybridizationFingerprinterTest;
import org.openscience.cdk.geometry.BondToolsTest;
import org.openscience.cdk.geometry.CrystalGeometryToolsTest;
import org.openscience.cdk.geometry.volume.VABCVolumeTest;
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
import org.openscience.cdk.stereo.StereoToolTest;
import org.openscience.cdk.tools.ElementComparatorTest;
import org.openscience.cdk.tools.FormatStringBufferTest;
import org.openscience.cdk.tools.HOSECodeGeneratorTest;
import org.openscience.cdk.tools.IDCreatorTest;
import org.openscience.cdk.tools.LonePairElectronCheckerTest;
import org.openscience.cdk.tools.manipulator.AminoAcidManipulatorTest;
import org.openscience.cdk.tools.manipulator.AtomContainerComparatorBy2DCenterTest;
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
@RunWith(value=Suite.class)
@SuiteClasses(value={
    StandardCoverageTest.class,
    AromaticityCalculatorTest.class,
    DictRefTest.class,
    CrystalGeometryToolsTest.class,
    MinimalPathIteratorTest.class,
    ConnectivityCheckerTest.class,
    UniversalIsomorphismTesterTest.class,
    IsomorphismTesterTest.class,
    RMapTest.class,
    RGraphTest.class,
    RNodeTest.class,
    MathToolsTest.class,
    AllRingsFinderTest.class,
    RingPartitionerTest.class,
    SimpleCycleTest.class,
    SimpleCycleBasisTest.class,
    CycleBasisTest.class,
    FormatStringBufferTest.class,
    IDCreatorTest.class,
    MoleculeSetManipulatorTest.class,
    ReactionManipulatorTest.class,
    AtomContainerManipulatorTest.class,
    RingSetManipulatorTest.class,
    RingManipulatorTest.class,
    AtomContainerSetManipulatorTest.class,
    MoleculeSetManipulatorTest.class,
    AtomContainerComparatorBy2DCenterTest.class,
    RebondToolTest.class,
    CanonicalLabelerTest.class,
    // from cdk.test.fingerprint
    FingerprinterTest.class,
    HybridizationFingerprinterTest.class,
    GraphOnlyFingerprinterTest.class,
    FingerprinterToolTest.class,
    ProblemMarkerTest.class,
    BondToolsTest.class,
    UnsupportedChemObjectExceptionTest.class,
    InvalidSmilesExceptionTest.class,
    AminoAcidManipulatorTest.class,
    AtomTypeManipulatorTest.class,
    ChemFileManipulatorTest.class,
    ChemModelManipulatorTest.class,
    ChemSequenceManipulatorTest.class,
    ReactionSetManipulatorTest.class,
    RingSizeComparatorTest.class,
    ElementComparatorTest.class,
    BFSShortestPathTest.class,
    MoleculeGraphsTest.class,
    ConnectionMatrixTest.class,
    BsptTest.class,
    PointTest.class,
    BiconnectivityInspectorTest.class,
    MorganNumbersToolsTest.class,
    PathTest.class,
    QueueTest.class,
    SSSRFinderTest.class,
    DoubleArrayResultTest.class,
    IntegerArrayResultTest.class,
    IntegerResultTest.class,
    DoubleResultTest.class,
    BooleanResultTest.class,
    DoubleArrayResultTypeTest.class,
    IntegerArrayResultTypeTest.class,
    IntegerResultTypeTest.class,
    DoubleResultTypeTest.class,
    BooleanResultTypeTest.class,
    DescriptorValueTest.class,
    DescriptorSpecificationTest.class,
    FortranFormatTest.class,
    PrimesTest.class,
    RandomNumbersToolTest.class,
    InvPairTest.class,
    EStateAtomTypeMatcherTest.class,
    AtomContainerComparatorTest.class,
    CDKHueckelAromaticityDetectorTest.class,
    DoubleBondAcceptingAromaticityDetectorTest.class,
    HOSECodeGeneratorTest.class,
    LonePairElectronCheckerTest.class ,
    StereoToolTest.class,
    VABCVolumeTest.class
})
public class MstandardTests {}
