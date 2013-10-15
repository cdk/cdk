/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2010  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.CDKConstantsTest;
import org.openscience.cdk.CDKTest;
import org.openscience.cdk.DynamicFactoryTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcherFilesTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcherSMILESTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcherTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcherTestFileReposTest;
import org.openscience.cdk.atomtype.RepeatedCDKAtomTypeMatcherSMILESTest;
import org.openscience.cdk.config.AtomTypeFactoryTest;
import org.openscience.cdk.config.BODRIsotopesTest;
import org.openscience.cdk.config.CDKBasedAtomTypeConfiguratorTest;
import org.openscience.cdk.config.IsotopeFactoryTest;
import org.openscience.cdk.config.OWLBasedAtomTypeConfiguratorTest;
import org.openscience.cdk.config.TXTBasedAtomTypeConfiguratorTest;
import org.openscience.cdk.config.atomtypes.AtomTypeHandlerTest;
import org.openscience.cdk.config.atomtypes.AtomTypeReaderTest;
import org.openscience.cdk.config.atomtypes.OWLAtomTypeHandlerTest;
import org.openscience.cdk.config.atomtypes.OWLAtomTypeReaderTest;
import org.openscience.cdk.config.isotopes.IsotopeHandlerTest;
import org.openscience.cdk.config.isotopes.IsotopeReaderTest;
import org.openscience.cdk.coverage.CoreCoverageTest;
import org.openscience.cdk.exception.CDKExceptionTest;
import org.openscience.cdk.exception.NoSuchAtomExceptionTest;
import org.openscience.cdk.exception.NoSuchAtomTypeExceptionTest;
import org.openscience.cdk.graph.AllPairsShortestPathsTest;
import org.openscience.cdk.graph.BitMatrixTest;
import org.openscience.cdk.graph.EssentialCyclesTest;
import org.openscience.cdk.graph.GreedyBasisTest;
import org.openscience.cdk.graph.InitialCyclesTest;
import org.openscience.cdk.graph.JumboPathGraphTest;
import org.openscience.cdk.graph.MinimumCycleBasisTest;
import org.openscience.cdk.graph.PathToolsTest;
import org.openscience.cdk.graph.RegularPathGraphTest;
import org.openscience.cdk.graph.RelevantCyclesTest;
import org.openscience.cdk.graph.ShortestPathsTest;
import org.openscience.cdk.graph.SpanningTreeTest;
import org.openscience.cdk.graph.matrix.AdjacencyMatrixTest;
import org.openscience.cdk.ringsearch.JumboCyclicVertexSearchTest;
import org.openscience.cdk.ringsearch.RegularCyclicVertexSearchTest;
import org.openscience.cdk.ringsearch.RingSearchTest;
import org.openscience.cdk.stereo.TetrahedralChiralityTest;
import org.openscience.cdk.stereo.DoubleBondStereochemistryTest;
import org.openscience.cdk.tools.DataFeaturesTest;
import org.openscience.cdk.tools.LoggingToolFactoryTest;
import org.openscience.cdk.tools.SystemOutLoggingToolTest;
import org.openscience.cdk.tools.manipulator.BondManipulatorTest;
import org.openscience.cdk.tools.periodictable.ElementPTFactoryTest;
import org.openscience.cdk.tools.periodictable.ElementPTReaderTest;
import org.openscience.cdk.tools.periodictable.PeriodicTableElementTest;
import org.openscience.cdk.tools.periodictable.PeriodicTableTest;

/**
 * TestSuite that runs all the tests for the CDK core module.
 *
 * @cdk.module  test-core
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    CoreCoverageTest.class,

    CDKTest.class,
    CDKConstantsTest.class,
    DataFeaturesTest.class,

    // cdk.config
    IsotopeFactoryTest.class,
    BODRIsotopesTest.class,
    AtomTypeFactoryTest.class,
    CDKBasedAtomTypeConfiguratorTest.class,
    TXTBasedAtomTypeConfiguratorTest.class,
    OWLBasedAtomTypeConfiguratorTest.class,
    AtomTypeReaderTest.class,
    AtomTypeHandlerTest.class,
    OWLAtomTypeReaderTest.class,
    OWLAtomTypeHandlerTest.class,
    IsotopeReaderTest.class,
    IsotopeHandlerTest.class,        
    PeriodicTableElementTest.class,
    PeriodicTableTest.class,
    ElementPTFactoryTest.class,
    ElementPTReaderTest.class,

    // the CDK atom typer
    CDKAtomTypeMatcherTest.class,
    CDKAtomTypeMatcherTestFileReposTest.class,
    CDKAtomTypeMatcherFilesTest.class,
    CDKAtomTypeMatcherSMILESTest.class,
    RepeatedCDKAtomTypeMatcherSMILESTest.class,

    // other
    CDKExceptionTest.class,
    NoSuchAtomExceptionTest.class,
    NoSuchAtomTypeExceptionTest.class,
    LoggingToolFactoryTest.class,
    SystemOutLoggingToolTest.class,
    BondManipulatorTest.class,
    AdjacencyMatrixTest.class,
    PathToolsTest.class,
    TetrahedralChiralityTest.class,
    DoubleBondStereochemistryTest.class,
    SpanningTreeTest.class,
    AllPairsShortestPathsTest.class,
    ShortestPathsTest.class,
    DynamicFactoryTest.class,

    // ring search (more in test-standard)
    RegularCyclicVertexSearchTest.class,
    JumboCyclicVertexSearchTest.class,
    RingSearchTest.class,
    BitMatrixTest.class,
    InitialCyclesTest.class,
    GreedyBasisTest.class,
    RelevantCyclesTest.class,
    MinimumCycleBasisTest.class,
    EssentialCyclesTest.class,
    RegularPathGraphTest.class,
    JumboPathGraphTest.class

})
public class McoreTests {}
