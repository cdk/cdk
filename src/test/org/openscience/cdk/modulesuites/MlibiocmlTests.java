/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.coverage.LibiocmlCoverageTest;
import org.openscience.cdk.libio.cml.ConvertorTest;

/**
 * TestSuite that runs all the unit tests for the CDK module libiocml.
 *
 * @cdk.module test-libiocml
 */
public class MlibiocmlTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("libiocml module Tests");
        
        suite.addTest(LibiocmlCoverageTest.suite());
        
        suite.addTest(ConvertorTest.suite());
        
        // the following classes require Java 1.5 (or better)
        if (System.getProperty("java.version").startsWith("1.5") ||
        	System.getProperty("java.version").startsWith("1.6") ||
        	System.getProperty("java.version").startsWith("1.7")) {
            System.out.println("Found required Java 1.5 (or better), so running the CML2 tests.");
            try {
                Class testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.io.cml.CML2Test");
                suite.addTest(new TestSuite(testClass));
            } catch (Exception exception) {
                // ok, do without. Probably compiled not Java 1.4
                System.out.println("Could not load the CML2 test: " + exception.getMessage());
                exception.printStackTrace();
            }
            try {
                Class testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.io.cml.CML2WriterTest");
                suite.addTest(new TestSuite(testClass));
            } catch (Exception exception) {
                // ok, do without. Probably compiled not Java 1.4
                System.out.println("Could not load the CML2Writer test: " + exception.getMessage());
                exception.printStackTrace();
            }
            try {
                Class testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.io.cml.CMLRoundTripTest");
                suite.addTest(new TestSuite(testClass));
            } catch (Exception exception) {
                // ok, do without. Probably compiled not Java 1.4
                System.out.println("Could not load the CML Roundtrip test: " + exception.getMessage());
                exception.printStackTrace();
            }
        } else {
        	System.out.println("Did not find the required Java 1.5 (or better), so not running the CML2 tests.");
        }
        
        return suite;
    }

}
