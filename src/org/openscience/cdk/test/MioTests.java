/* $RCSfile: $    
 * $Author: egonw $    
 * $Date: 2006-03-30 00:42:34 +0200 (Thu, 30 Mar 2006) $    
 * $Revision: 5865 $
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.io.CrystClustReaderTest;
import org.openscience.cdk.test.io.GamessReaderTest;
import org.openscience.cdk.test.io.Gaussian98ReaderTest;
import org.openscience.cdk.test.io.GhemicalReaderTest;
import org.openscience.cdk.test.io.HINReaderTest;
import org.openscience.cdk.test.io.INChIPlainTextReaderTest;
import org.openscience.cdk.test.io.INChIReaderTest;
import org.openscience.cdk.test.io.MDLRXNReaderTest;
import org.openscience.cdk.test.io.MDLRXNWriterTest;
import org.openscience.cdk.test.io.MDLReaderTest;
import org.openscience.cdk.test.io.MDLWriterTest;
import org.openscience.cdk.test.io.Mol2ReaderTest;
import org.openscience.cdk.test.io.PDBReaderTest;
import org.openscience.cdk.test.io.ReaderFactoryTest;
import org.openscience.cdk.test.io.SDFReaderTest;
import org.openscience.cdk.test.io.SMILESReaderTest;
import org.openscience.cdk.test.io.ShelXReaderTest;
import org.openscience.cdk.test.io.cml.CMLIOTests;
import org.openscience.cdk.test.io.iterator.IteratingMDLReaderTest;
import org.openscience.cdk.test.io.iterator.IteratingSMILESReaderTest;

/**
 * TestSuite that runs all the sample tests for the cdk.io package.
 *
 * @cdk.module test-io
 */
public class MioTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.io Tests");
        
        suite.addTest(CMLIOTests.suite());
        suite.addTest(CrystClustReaderTest.suite());
        suite.addTest(GamessReaderTest.suite());
        suite.addTest(Gaussian98ReaderTest.suite());
        suite.addTest(GhemicalReaderTest.suite());
        suite.addTest(HINReaderTest.suite());
        //suite.addTest(IChIReaderTest.suite());
        suite.addTest(INChIReaderTest.suite());
        suite.addTest(INChIPlainTextReaderTest.suite());
        suite.addTest(MDLReaderTest.suite());
          suite.addTest(SDFReaderTest.suite());
        suite.addTest(MDLWriterTest.suite());
        suite.addTest(MDLRXNReaderTest.suite());
        suite.addTest(MDLRXNWriterTest.suite());
        suite.addTest(Mol2ReaderTest.suite());
        suite.addTest(PDBReaderTest.suite());
        suite.addTest(ShelXReaderTest.suite());
        suite.addTest(SMILESReaderTest.suite());
        //suite.addTest(VASPReaderTest.suite()); Class is in experimental
        /* suite.addTest(ZMatrixReaderTest.suite()); This is not a JUnit test yet! */
        
        suite.addTest(ReaderFactoryTest.suite());
        
        // cdk.io.iterator package
        suite.addTest(IteratingMDLReaderTest.suite());
        suite.addTest(IteratingSMILESReaderTest.suite());
        return suite;
    }

}
