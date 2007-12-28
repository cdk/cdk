/* $RCSfile: $    
 * $Author: egonw $    
 * $Date: 2006-03-30 00:42:34 +0200 (Thu, 30 Mar 2006) $    
 * $Revision: 5865 $
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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.test.io.*;
import org.openscience.cdk.test.io.cml.CMLIOTests;
import org.openscience.cdk.test.io.iterator.IteratingMDLConformerReaderTest;
import org.openscience.cdk.test.io.iterator.IteratingMDLReaderTest;

/**
 * TestSuite that runs all the sample tests for the cdk.io package.
 *
 * @cdk.module test-io
 */
public class MioTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("The cdk.io Tests");

        suite.addTest(CMLIOTests.suite());
        suite.addTest(CrystClustReaderTest.suite());
        suite.addTest(CDKSourceCodeWriterTest.suite());
        suite.addTest(GamessReaderTest.suite());
        suite.addTest(Gaussian98ReaderTest.suite());
        suite.addTest(GhemicalReaderTest.suite());
        suite.addTest(HINReaderTest.suite());
        //suite.addTest(IChIReaderTest.suite());
        suite.addTest(INChIReaderTest.suite());
        suite.addTest(INChIPlainTextReaderTest.suite());
        suite.addTest(MDLReaderTest.suite());
        suite.addTest(MDLV2000ReaderTest.suite());
        suite.addTest(MDLV3000ReaderTest.suite());
        suite.addTest(SDFReaderTest.suite());
        suite.addTest(MDLWriterTest.suite());
        suite.addTest(MDLRXNReaderTest.suite());
        suite.addTest(MDLRXNV3000ReaderTest.suite());
        suite.addTest(MDLRXNWriterTest.suite());
        suite.addTest(Mol2ReaderTest.suite());
        suite.addTest(PDBWriterTest.suite());
        suite.addTest(PMPReaderTest.suite());
        suite.addTest(ShelXReaderTest.suite());
        suite.addTest(ShelXWriterTest.suite());
        //suite.addTest(VASPReaderTest.suite()); Class is in experimental
        /* suite.addTest(ZMatrixReaderTest.suite()); This is not a JUnit test yet! */
        suite.addTest(XYZReaderTest.suite());
        suite.addTest(XYZWriterTest.suite());

        suite.addTest(ReaderFactoryTest.suite());
        suite.addTest(WriterFactoryTest.suite());
        suite.addTest(ChemObjectIOInstantionTests.suite());

        // cdk.io.iterator package
        suite.addTest(IteratingMDLReaderTest.suite());
        suite.addTest(IteratingMDLConformerReaderTest.suite());
        return suite;
    }

}
