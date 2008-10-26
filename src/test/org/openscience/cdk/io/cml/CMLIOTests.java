/* $RCSfile$
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
package org.openscience.cdk.io.cml;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite for testing the CML reading capabilities. The writing functionality
 * is tested by <code>MlibiocmlTests</code>.
 *
 * @cdk.module test-io
 * 
 * @see org.openscience.cdk.modulesuites.MlibiocmlTests
 */
public class CMLIOTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.io.cml Tests");
        suite.addTest(new JUnit4TestAdapter(CMLStackTest.class));
        
        suite.addTest(new JUnit4TestAdapter(JumboTest.class));
        // suite.addTest(JmolTest.suite());
        suite.addTest(new JUnit4TestAdapter(JChemPaintTest.class));
        suite.addTest(new JUnit4TestAdapter(CMLFragmentsTest.class));
        suite.addTest(new JUnit4TestAdapter(CML23FragmentsTest.class)); // schema23 20060209
        suite.addTest(new JUnit4TestAdapter(Jumbo46CMLFragmentsTest.class));
        
        return suite;
    }

}
