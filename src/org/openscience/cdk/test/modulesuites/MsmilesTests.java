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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.io.SMILESReaderTest;
import org.openscience.cdk.test.io.iterator.IteratingSMILESReaderTest;
import org.openscience.cdk.test.smiles.DeduceBondSystemToolTest;
import org.openscience.cdk.test.smiles.SmilesGeneratorTest;
import org.openscience.cdk.test.smiles.SmilesParserTest;
import org.openscience.cdk.test.tools.NormalizerTest;

/**
 * TestSuite that runs all the sample tests for the SMILES functionality.
 *
 * @cdk.module test-smiles
 */
public class MsmilesTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("The SMILES Tests");

        // IO classes
        suite.addTest(SMILESReaderTest.suite());
        suite.addTest(IteratingSMILESReaderTest.suite());
        // from cdk.test.smiles
        suite.addTest(DeduceBondSystemToolTest.suite());
        suite.addTest(SmilesGeneratorTest.suite());
        suite.addTest(new JUnit4TestAdapter(SmilesParserTest.class));
        // from cdk.tools
        suite.addTest(NormalizerTest.suite());

        return suite;
    }

}
