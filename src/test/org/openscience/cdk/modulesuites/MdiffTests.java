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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DiffCoverageTest;
import org.openscience.cdk.tools.diff.AbstractChemObjectDiffTest;
import org.openscience.cdk.tools.diff.AtomTypeDiffTest;
import org.openscience.cdk.tools.diff.ElementDiffTest;
import org.openscience.cdk.tools.diff.IsotopeDiffTest;

/**
 * TestSuite that runs all the JUnit tests for the diff module.
 *
 * @cdk.module test-diff
 */
public class MdiffTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The CDK diff module Tests");

        suite.addTest(new JUnit4TestAdapter(DiffCoverageTest.class));	
        
        suite.addTest(new JUnit4TestAdapter(AbstractChemObjectDiffTest.class));

        suite.addTest(new JUnit4TestAdapter(ElementDiffTest.class));
        suite.addTest(new JUnit4TestAdapter(IsotopeDiffTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomTypeDiffTest.class));

        return suite;
    }

}
