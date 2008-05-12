/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net
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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.SMARTSCoverageTest;
import org.openscience.cdk.isomorphism.SMARTSTest;

/**
 * TestSuite that runs all the sample tests for experimental classes.
 *
 * @cdk.module test-smarts
 */
public class MsmartsTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.smarts Tests");

        suite.addTest(new JUnit4TestAdapter(SMARTSCoverageTest.class));
        
        suite.addTest(SMARTSTest.suite());
        
        try {
            Class testClass = ClassLoader.getSystemClassLoader().loadClass("org.openscience.cdk.smiles.smarts.parser.ParserTest");
            suite.addTest(new TestSuite(testClass));
            testClass = ClassLoader.getSystemClassLoader().loadClass("org.openscience.cdk.smiles.smarts.parser.SMARTSSearchTest");
            suite.addTest(new TestSuite(testClass));
            testClass = ClassLoader.getSystemClassLoader().loadClass("org.openscience.cdk.smiles.smarts.parser.RecursiveTest");
            suite.addTest(new TestSuite(testClass));
            testClass = ClassLoader.getSystemClassLoader().loadClass("org.openscience.cdk.smiles.smarts.parser.visitor.SmartsDumpVisitorTest");
            suite.addTest(new TestSuite(testClass));
            testClass = ClassLoader.getSystemClassLoader().loadClass("org.openscience.cdk.smiles.smarts.parser.visitor.SmartsQueryVisitorTest");
            suite.addTest(new TestSuite(testClass));
            testClass = ClassLoader.getSystemClassLoader().loadClass("org.openscience.cdk.smiles.smarts.SMARTSQueryToolTest");
            suite.addTest(new JUnit4TestAdapter(testClass));
        } catch (Exception exception) {
            // ok, does not exist, just skip
            System.out.println("Could not load the SMARTS Parser test: " + exception.getMessage());
        }
        return suite;
    }

}
