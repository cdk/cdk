/* $RCSfile: $    
 * $Author$    
 * $Date$    
 * $Revision$
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
import org.openscience.cdk.coverage.SmilesCoverageTest;
import org.openscience.cdk.fingerprint.LingoFingerprinterTest;
import org.openscience.cdk.io.MoSSOutputReaderTest;
import org.openscience.cdk.io.SMILESReaderTest;
import org.openscience.cdk.io.iterator.IteratingSMILESReaderTest;
import org.openscience.cdk.smiles.DeduceBondSystemToolTest;
import org.openscience.cdk.smiles.FixBondOrdersToolTest;
import org.openscience.cdk.smiles.SmilesGeneratorTest;
import org.openscience.cdk.smiles.SmilesParserTest;
import org.openscience.cdk.normalize.NormalizerTest;

/**
 * TestSuite that runs all the sample tests for the SMILES functionality.
 *
 * @cdk.module test-smiles
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    SmilesCoverageTest.class,
    SMILESReaderTest.class,
    IteratingSMILESReaderTest.class,
    DeduceBondSystemToolTest.class,
    MoSSOutputReaderTest.class,
    SmilesParserTest.class,
    SmilesGeneratorTest.class,
    NormalizerTest.class,
    LingoFingerprinterTest.class,
    FixBondOrdersToolTest.class
})
public class MsmilesTests {}
