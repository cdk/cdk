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

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.test.qsar.DescriptorEngineTest;
import org.openscience.cdk.test.qsar.model.R2.RJavaEnvironmentTest;
import org.openscience.cdk.test.qsar.model.weka.J48WModelTest;
import org.openscience.cdk.test.qsar.model.weka.LinearRegressionWModelTest;
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

        suite.addTest(DescriptorEngineTest.suite());
        
//      from cdk.test.qsar.model.R2
        suite.addTest(org.openscience.cdk.test.qsar.model.R2.CNNRegressionModelTest.suite());
        suite.addTest(org.openscience.cdk.test.qsar.model.R2.LinearRegressionModelTest.suite());
        suite.addTest(org.openscience.cdk.test.qsar.model.R2.QSARRModelTests.suite());
        suite.addTest(RJavaEnvironmentTest.suite());

//      from cdk.test.qsar.model.R2
        suite.addTest(J48WModelTest.suite());
        suite.addTest(LinearRegressionWModelTest.suite());
        suite.addTest(QSARWekaModelTests.suite());

        return suite;
    }

}
