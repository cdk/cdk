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
import org.openscience.cdk.CDKConstantsTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcherFilesTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcherSMILESTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcherTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcherTestFileReposTest;
import org.openscience.cdk.config.AtomTypeFactoryTest;
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
import org.openscience.cdk.graph.PathToolsTest;
import org.openscience.cdk.graph.SpanningTreeTest;
import org.openscience.cdk.graph.matrix.AdjacencyMatrixTest;
import org.openscience.cdk.tools.DataFeaturesTest;
import org.openscience.cdk.tools.LoggingToolFactoryTest;
import org.openscience.cdk.tools.manipulator.BondManipulatorTest;

/**
 * TestSuite that runs all the tests for the CDK core module.
 *
 * @cdk.module  test-core
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    CoreCoverageTest.class,

    CDKConstantsTest.class,
    DataFeaturesTest.class,

    // cdk.config
    IsotopeFactoryTest.class,
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

    // the CDK atom typer
    CDKAtomTypeMatcherTest.class,
    CDKAtomTypeMatcherTestFileReposTest.class,
    CDKAtomTypeMatcherFilesTest.class,
    CDKAtomTypeMatcherSMILESTest.class,

    // other
    CDKExceptionTest.class,
    NoSuchAtomExceptionTest.class,
    NoSuchAtomTypeExceptionTest.class,
    LoggingToolFactoryTest.class,
    BondManipulatorTest.class,
    AdjacencyMatrixTest.class,
    PathToolsTest.class,
    SpanningTreeTest.class
})
public class McoreTests {}
