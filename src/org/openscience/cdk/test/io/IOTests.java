/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk.test.io;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.io.cml.CMLIOTests;
import org.openscience.cdk.test.io.iterator.IteratingMDLReaderTest;

/**
 * TestSuite that runs all the sample tests for the cdk.io package.
 *
 * @cdk.module test
 */
public class IOTests {

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
        suite.addTest(MDLWriterTest.suite());
        suite.addTest(MDLRXNReaderTest.suite());
        suite.addTest(Mol2ReaderTest.suite());
        suite.addTest(PDBReaderTest.suite());
        suite.addTest(ShelXReaderTest.suite());
        suite.addTest(SMILESReaderTest.suite());
        //suite.addTest(VASPReaderTest.suite());
        /* suite.addTest(ZMatrixReaderTest.suite()); This is not a JUnit test yet! */
        
        suite.addTest(ReaderFactoryTest.suite());
        
        // cdk.io.iterator package
        suite.addTest(IteratingMDLReaderTest.suite());
        return suite;
    }

}
