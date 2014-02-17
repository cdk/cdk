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
import org.openscience.cdk.CloneAtomContainerTest;
import org.openscience.cdk.config.XMLIsotopeFactoryTest;
import org.openscience.cdk.config.isotopes.IsotopeHandlerTest;
import org.openscience.cdk.config.isotopes.IsotopeReaderTest;
import org.openscience.cdk.coverage.ExtraCoverageTest;
import org.openscience.cdk.geometry.RDFCalculatorTest;
import org.openscience.cdk.geometry.alignment.KabschAlignmentTest;
import org.openscience.cdk.graph.invariant.EquivalentClassPartitionerTest;
import org.openscience.cdk.index.CASNumberTest;
import org.openscience.cdk.io.CrystClustReaderTest;
import org.openscience.cdk.io.ExtraReaderFactoryTest;
import org.openscience.cdk.io.GamessReaderTest;
import org.openscience.cdk.io.INChIPlainTextReaderTest;
import org.openscience.cdk.io.INChIReaderTest;
import org.openscience.cdk.io.ShelXWriterTest;
import org.openscience.cdk.io.iterator.IteratingMDLConformerReaderTest;
import org.openscience.cdk.io.random.RandomAccessTest;
import org.openscience.cdk.iupac.ParserTest;
import org.openscience.cdk.reaction.ReactionChainTest;
import org.openscience.cdk.tools.BremserPredictorTest;
import org.openscience.cdk.tools.DeAromatizationToolTest;
import org.openscience.cdk.tools.HOSECodeAnalyserTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module  test-extra
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    ExtraCoverageTest.class,
    XMLIsotopeFactoryTest.class,
    IsotopeReaderTest.class,
    IsotopeHandlerTest.class,        
    CloneAtomContainerTest.class,
    RDFCalculatorTest.class,
    KabschAlignmentTest.class,
    CASNumberTest.class,
    HOSECodeAnalyserTest.class,
    DeAromatizationToolTest.class,
    ShelXWriterTest.class,
    CrystClustReaderTest.class,
    GamessReaderTest.class,
    INChIReaderTest.class,
    INChIPlainTextReaderTest.class,
    IteratingMDLConformerReaderTest.class,
    ExtraReaderFactoryTest.class,
    RandomAccessTest.class,
    BremserPredictorTest.class,
    ParserTest.class,
    ReactionChainTest.class,
    EquivalentClassPartitionerTest.class,
})
public class MextraTests {}
