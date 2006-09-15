/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.test.qsar.model.weka;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the tests for the CDK libio-weka module.
 *
 * @cdk.module test-qsar
 * @cdk.depends junit.jar
 */
public class QSARWekaModelTests {
    /**
     * Constructor of the QSARWekaModelTests object
     */
    public QSARWekaModelTests() {
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("CDK standard Tests");

        suite.addTest(J48WModelTest.suite());
        suite.addTest(LinearRegressionWModelTest.suite());
        return suite;
    }
}
