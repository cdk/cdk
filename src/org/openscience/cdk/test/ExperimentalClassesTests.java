/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.test.io.VASPReaderTest;
import org.openscience.cdk.test.tools.HydrogenAdder2Test;
import org.openscience.cdk.test.tools.ValencyCheckerTest;
import org.openscience.cdk.test.isomorphism.SMARTSTest;

/**
 * TestSuite that runs all the sample tests for experimental classes.
 *
 * @cdk.module test
 */
public class ExperimentalClassesTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.experimental Tests");
        suite.addTest(AssociationTest.suite());
        suite.addTest(HydrogenAdder2Test.suite());
        suite.addTest(ValencyCheckerTest.suite());
        suite.addTest(VASPReaderTest.suite());
        suite.addTest(SMARTSTest.suite());
        
        ExperimentalClassesTests thisTest = new ExperimentalClassesTests();
        try {
            TestCase test = (TestCase)thisTest.getClass().getClassLoader().loadClass("org.openscience.cdk.test.iupac.ParserTest").newInstance();
            TestSuite testSuite = new TestSuite(test.getClass());
            suite.addTest(testSuite);
        } catch (Exception exception) {
            // ok, does not exist, just skip
            System.out.println("Could not load the IUPAC Parser test: " + exception.getMessage());
        }
        try {
            TestCase test = (TestCase)thisTest.getClass().getClassLoader().loadClass("org.openscience.cdk.test.smiles.smarts.ParserTest").newInstance();
            TestSuite testSuite = new TestSuite(test.getClass());
            suite.addTest(testSuite);
        } catch (Exception exception) {
            // ok, does not exist, just skip
            System.out.println("Could not load the SMARTS Parser test: " + exception.getMessage());
        }
        return suite;
    }

}
