/* $RCSfile$    
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

package org.openscience.cdk.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.coverage.Builder3dCoverageTest;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3dTest;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3DTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test-builder3d
 */
public class Mbuilder3dTests {

    public static Test suite () {
        TestSuite suite = new TestSuite("The CDK builder3d module Tests");
        
        suite.addTest(Builder3dCoverageTest.suite());
        
        suite.addTest(new JUnit4TestAdapter(ModelBuilder3dTest.class));
        suite.addTest(new JUnit4TestAdapter(TemplateHandler3DTest.class));
        
        return suite;
    }

}
