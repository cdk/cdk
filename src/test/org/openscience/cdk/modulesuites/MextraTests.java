/* $Revision$ $Author$ $Date$
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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CloneAtomContainerTest;
import org.openscience.cdk.PeriodicTableElementTest;
import org.openscience.cdk.VariousTests;
import org.openscience.cdk.coverage.ExtraCoverageTest;
import org.openscience.cdk.geometry.RDFCalculatorTest;
import org.openscience.cdk.geometry.alignment.KabschAlignmentTest;
import org.openscience.cdk.index.CASNumberTest;
import org.openscience.cdk.io.ShelXWriterTest;
import org.openscience.cdk.similarity.DistanceMomentTest;
import org.openscience.cdk.similarity.TanimotoTest;
import org.openscience.cdk.tools.BremserPredictorTest;
import org.openscience.cdk.tools.DeAromatizationToolTest;
import org.openscience.cdk.tools.HOSECodeAnalyserTest;
import org.openscience.cdk.tools.PeriodicTableTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module  test-extra
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MextraTests {
    
    public static Test suite( )
    {
        TestSuite suite= new TestSuite("All CDK extra Tests");

        suite.addTest(ExtraCoverageTest.suite());

        suite.addTest(new JUnit4TestAdapter(PeriodicTableElementTest.class));
        suite.addTest(new JUnit4TestAdapter(PeriodicTableTest.class));

        // Individual Tests
        suite.addTest(new JUnit4TestAdapter(CloneAtomContainerTest.class));
        // from cdk.test.geometry
        suite.addTest(new JUnit4TestAdapter(RDFCalculatorTest.class));
        // from cdk.test.geometry.align
        suite.addTest(new JUnit4TestAdapter(KabschAlignmentTest.class));
        // from cdk.test.index
        suite.addTest(new JUnit4TestAdapter(CASNumberTest.class));
        // from cdk.test.isomorphism
        // from cdk.test.math
        // from cdk.test.similarity
        suite.addTest(new JUnit4TestAdapter(TanimotoTest.class));
        suite.addTest(new JUnit4TestAdapter(DistanceMomentTest.class));
        suite.addTest(new JUnit4TestAdapter(HOSECodeAnalyserTest.class));
        suite.addTest(new JUnit4TestAdapter(DeAromatizationToolTest.class));
        suite.addTest(new JUnit4TestAdapter(ShelXWriterTest.class));
        suite.addTest(new JUnit4TestAdapter(BremserPredictorTest.class));

        // Below are the tests that are not always possible to execute, because
        // the class might not be compiled (depending on Ant and Java VM versions).

        // from cdk.test.iupac
        try {
            Class testClass = ClassLoader.getSystemClassLoader().loadClass("org.openscience.cdk.iupac.ParserTest");
            suite.addTest(new TestSuite(testClass));
            System.out.println("Found IUPAC Parser test.");
        } catch (Exception exception) {
            // ok, do without. Probably compiled with Ant < 1.6
            System.out.println("Could not load the IUPAC Parser test: " + exception.getMessage());
        }

        // other
        suite.addTest(new JUnit4TestAdapter(VariousTests.class));        
        
        return suite;
    }
    
}
