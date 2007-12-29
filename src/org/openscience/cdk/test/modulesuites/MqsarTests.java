/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-21 10:59:31 +0200 (Fr, 21 Apr 2006) $
 * $Revision: 6067 $
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.qsar.DescriptorEngineTest;
import org.openscience.cdk.test.qsar.descriptors.molecular.AminoAcidCountDescriptorTest;
import org.openscience.cdk.test.qsar.model.R2.RJavaEnvironmentTest;
import org.openscience.cdk.test.qsar.model.weka.QSARWekaModelTests;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test-qsar
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MqsarTests {

    public static Test suite() {

        TestSuite suite = new TestSuite("All QSAR Tests");

        // Individual Tests - Please add correlatively	
        suite.addTest(AminoAcidCountDescriptorTest.suite());

        suite.addTest(DescriptorEngineTest.suite());
        
        String rhome = System.getenv("R_HOME");
        String ldlibrarypath = System.getenv("LD_LIBRARY_PATH");

        if (rhome != null && rhome.equals("") &&
        	ldlibrarypath != null && ldlibrarypath.equals("")) {

//      	from cdk.test.qsar.model.R2
        	suite.addTest(org.openscience.cdk.test.qsar.model.R2.CNNRegressionModelTest.suite());
        	suite.addTest(org.openscience.cdk.test.qsar.model.R2.LinearRegressionModelTest.suite());
        	suite.addTest(org.openscience.cdk.test.qsar.model.R2.QSARRModelTests.suite());
        	suite.addTest(RJavaEnvironmentTest.suite());
        }

//      from cdk.test.qsar.model.R2
 
        suite.addTest(QSARWekaModelTests.suite());

        return suite;
    }

}
