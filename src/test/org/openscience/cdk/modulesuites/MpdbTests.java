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
import org.openscience.cdk.coverage.PdbCoverageTest;
import org.openscience.cdk.io.PDBReaderFactoryTest;
import org.openscience.cdk.io.PDBReaderTest;
import org.openscience.cdk.io.PDBWriterTest;
import org.openscience.cdk.templates.AminoAcidsTest;
import org.openscience.cdk.tools.ProteinBuilderToolTest;

/**
 * TestSuite that runs all the sample tests for the CDK module pdb.
 *
 * @cdk.module test-pdb
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    PdbCoverageTest.class,
    AminoAcidsTest.class,
    PDBReaderTest.class,
    PDBWriterTest.class,
    ProteinBuilderToolTest.class,
    PDBReaderFactoryTest.class
})
public class MpdbTests {}
