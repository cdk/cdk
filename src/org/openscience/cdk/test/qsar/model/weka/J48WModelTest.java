/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.qsar.model.weka.J48WModel;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs a test for the J48WModel
 *
 * @author Miguel Rojas
 * @cdk.module test-qsar
 */
public class J48WModelTest extends CDKTestCase {
    /**
     * Constructor of the J48WModelTest object
     */
    public J48WModelTest() {
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(J48WModelTest.class);
    }

    /**
     * @throws CDKException
     * @throws Exception
     * @throws QSARModelException
     */
    public void testJ48WModel1() throws CDKException, java.lang.Exception, QSARModelException {
        int[] typAttrib = {Weka.NUMERIC, Weka.NUMERIC, Weka.NUMERIC};
        String[] classAttrib = {"A_", "B_", "C_"};
        double[][] x = {{10, 10, 10}, {10, -10, -10}, {-10, -10, -10},
                {11, 11, 11}, {11, -11, -11}, {-11, -11, -11}};
        Double[][] xD = new Double[x.length][x[0].length];
        for (int i = 0; i < xD.length; i++)
            for (int j = 0; j < xD[i].length; j++)
                xD[i][j] = new Double(x[i][j]);
        String[] y = {"A_", "B_", "C_", "A_", "B_", "C_"};

        J48WModel j48 = new J48WModel(typAttrib, classAttrib, y, xD);
        String[] options = new String[1];
        options[0] = "-U";
        j48.setOptions(options);
        j48.build();

        /* Test predictions */
        Double[][] testX = {{new Double(11), new Double(-11), new Double(-11)},
                {new Double(-10), new Double(-10), new Double(-10)}};

        j48.setParameters(testX);
        j48.predict();

        String[] preds = (String[]) j48.getPredictPredicted();
        assertEquals(preds[0], "B_");
        assertEquals(preds[1], "C_");
    }

    /**
     * @throws CDKException
     * @throws Exception
     * @throws QSARModelException
     */
    public void testJ48WModel2() throws CDKException, java.lang.Exception, QSARModelException {
        J48WModel j48 = new J48WModel("data/arff/Table3.arff");
        String[] options = new String[1];
        options[0] = "-U";
        j48.setOptions(options);
        j48.build();
        Double[][] testX = {{new Double(11), new Double(-11), new Double(-11)},
                {new Double(-10), new Double(-10), new Double(-10)}};
        j48.setParameters(testX);
        j48.predict();
        String[] preds = (String[]) j48.getPredictPredicted();
        assertEquals(preds[0], "B_");
        assertEquals(preds[1], "C_");
    }
}

