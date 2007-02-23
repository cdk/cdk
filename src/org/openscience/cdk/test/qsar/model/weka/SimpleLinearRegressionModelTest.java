/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2007 by Mario Baseda <mariobaseda@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.openscience.cdk.test.qsar.model.weka;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.qsar.model.weka.LinearRegressionWModel;
import org.openscience.cdk.qsar.model.weka.SimpleLinearRegressionModel;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs a test for the SimpleLinearRegressionModel
 *
 * @author Mario Baseda
 * @cdk.module test-qsar
 */
public class SimpleLinearRegressionModelTest extends CDKTestCase{

	/**
	 * Constructor of the SimpleLinearRegressionModelTest object
	 */
	public SimpleLinearRegressionModelTest() {}

	/**
	 * A unit test suite for JUnit
	 *
	 * @return The test suite
	 */
	public static Test suite() {
		return new TestSuite(SimpleLinearRegressionModelTest.class);
	}

	/**
	 * @throws CDKException
	 * @throws Exception
	 * @throws QSARModelException
	 */
	public void testSimpleLinearRegressionModel1() throws CDKException, java.lang.Exception, QSARModelException {

		double[][] x = {{1, 1}, {3, 3}, {4, 4}, {6, 6}};
		Double[][] xD = new Double[x.length][x[0].length];
		for (int i = 0; i < xD.length; i++)
			for (int j = 0; j < xD[i].length; j++)
				xD[i][j] = new Double(x[i][j]);
		double[] y = {0, 2, 3, 5};

		Double[] yD = new Double[y.length];
		for (int i = 0; i < yD.length; i++)
			yD[i] = new Double(y[i]);

		SimpleLinearRegressionModel slrm = new SimpleLinearRegressionModel(yD, xD);
		slrm.build();

		/* Test predictions */
		Double[][] newx = {
				{new Double(2), new Double(2)},
				{new Double(5), new Double(5)},
		};

		slrm.setParameters(newx);
		slrm.predict();

		Double[] preds = (Double[]) slrm.getPredictPredicted();
		assertEquals((preds[0]).doubleValue(), 1.0, 0.001);
		assertEquals((preds[1]).doubleValue(), 4.0, 0.001);
	}

	/**
	 * @throws CDKException
	 * @throws Exception
	 * @throws QSARModelException
	 */
//	public void testSimpleLinearRegressionWModel2() throws CDKException, java.lang.Exception, QSARModelException {
//		SimpleLinearRegressionModel slrm = new SimpleLinearRegressionModel("X:\\cdk\\src\\data\\arff\\Table1.arff");
//		String[] options = new String[4];
//		slrm.build();
//		slrm.setParameters("X:\\cdk\\src\\data\\arff\\Table2.arff");
//		slrm.predict();
//		Double[] result = (Double[]) slrm.getPredictPredicted();
//		assertNotNull(result);
//		assertEquals((result[0]).doubleValue(), 1.0, 0.001);
//		assertEquals((result[1]).doubleValue(), 4.0, 0.001);
//	}
}
