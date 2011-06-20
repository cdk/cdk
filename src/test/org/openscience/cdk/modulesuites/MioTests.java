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
import org.openscience.cdk.coverage.IoCoverageTest;
import org.openscience.cdk.io.CDKSourceCodeWriterTest;
import org.openscience.cdk.io.CIFReaderTest;
import org.openscience.cdk.io.CTXReaderTest;
import org.openscience.cdk.io.ChemObjectIOInstantionTests;
import org.openscience.cdk.io.CrystClustReaderTest;
import org.openscience.cdk.io.GamessReaderTest;
import org.openscience.cdk.io.Gaussian98ReaderTest;
import org.openscience.cdk.io.GhemicalReaderTest;
import org.openscience.cdk.io.HINReaderTest;
import org.openscience.cdk.io.INChIPlainTextReaderTest;
import org.openscience.cdk.io.INChIReaderTest;
import org.openscience.cdk.io.MDLRXNReaderTest;
import org.openscience.cdk.io.MDLRXNV2000ReaderTest;
import org.openscience.cdk.io.MDLRXNV3000ReaderTest;
import org.openscience.cdk.io.MDLRXNWriterTest;
import org.openscience.cdk.io.MDLReaderTest;
import org.openscience.cdk.io.MDLV2000ReaderTest;
import org.openscience.cdk.io.MDLV3000ReaderTest;
import org.openscience.cdk.io.MDLV2000WriterTest;
import org.openscience.cdk.io.Mol2ReaderTest;
import org.openscience.cdk.io.Mol2WriterTest;
import org.openscience.cdk.io.PCCompoundASNReaderTest;
import org.openscience.cdk.io.PDBWriterTest;
import org.openscience.cdk.io.PMPReaderTest;
import org.openscience.cdk.io.ReaderFactoryTest;
import org.openscience.cdk.io.SDFReaderTest;
import org.openscience.cdk.io.SDFWriterTest;
import org.openscience.cdk.io.SMILESWriterTest;
import org.openscience.cdk.io.ShelXReaderTest;
import org.openscience.cdk.io.WriterFactoryTest;
import org.openscience.cdk.io.XYZReaderTest;
import org.openscience.cdk.io.XYZWriterTest;
import org.openscience.cdk.io.cml.CMLIOTests;
import org.openscience.cdk.io.iterator.IteratingMDLConformerReaderTest;
import org.openscience.cdk.io.iterator.IteratingMDLReaderTest;
import org.openscience.cdk.io.iterator.IteratingPCCompoundASNReaderTest;
import org.openscience.cdk.io.iterator.IteratingPCCompoundXMLReaderTest;
import org.openscience.cdk.io.iterator.IteratingPCSubstancesXMLReaderTest;

/**
 * TestSuite that runs all the sample tests for the cdk.io package.
 *
 * @cdk.module test-io
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    IoCoverageTest.class,
    CMLIOTests.class,
    CrystClustReaderTest.class,
    CDKSourceCodeWriterTest.class,
    CIFReaderTest.class,
    CTXReaderTest.class,
    GamessReaderTest.class,
    Gaussian98ReaderTest.class,
    GhemicalReaderTest.class,
    HINReaderTest.class,
    INChIReaderTest.class,
    INChIPlainTextReaderTest.class,
    MDLReaderTest.class,
    MDLV2000ReaderTest.class,
    MDLV3000ReaderTest.class,
    SDFReaderTest.class,
    MDLV2000WriterTest.class,
    SDFWriterTest.class,
    MDLRXNReaderTest.class,
    MDLRXNV2000ReaderTest.class,
    MDLRXNV3000ReaderTest.class,
    MDLRXNWriterTest.class,
    Mol2ReaderTest.class,
    Mol2WriterTest.class,
    PCCompoundASNReaderTest.class,
    PDBWriterTest.class,
    PMPReaderTest.class,
    ShelXReaderTest.class,
    SMILESWriterTest.class,
    XYZReaderTest.class,
    XYZWriterTest.class,

    ReaderFactoryTest.class,
    WriterFactoryTest.class,
    ChemObjectIOInstantionTests.class,

        // cdk.io.iterator package
    IteratingMDLReaderTest.class,
    IteratingMDLConformerReaderTest.class,
    IteratingPCCompoundASNReaderTest.class,
    IteratingPCCompoundXMLReaderTest.class,
    IteratingPCSubstancesXMLReaderTest.class        
})
public class MioTests {}