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
import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.qsar.model.weka.SMOModel;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs a test for the SMOModel
 *
 * @author Mario Baseda
 * @cdk.module test-qsar
 */
public class SMOModelTest extends CDKTestCase{

	/**
	 * Constructor of the SMOModelTest object
	 */
	public SMOModelTest(){}

	/**
	 * A unit test suite for JUnit
	 *
	 * @return The test suite
	 */
	public static Test suite() {
		return new TestSuite(SMOModelTest.class);
	}

	/**
	 * @throws CDKException
	 * @throws Exception
	 * @throws QSARModelException
	 */
	public void testSMOModel() throws CDKException, java.lang.Exception, QSARModelException{
		SMOModel test = new SMOModel();
		int[] typAttrib = {Weka.NUMERIC, Weka.NUMERIC, Weka.NUMERIC};
		String[] classAttrib = {"A_", "B_", "C_"};
		double[][] x = {{10, 10, 10}, {10, -10, -10}, {-10, -10, -10},
				{11, 11, 11}, {11, -11, -11}, {-11, -11, -11}};
		Double[][] xD = new Double[x.length][x[0].length];
		for (int i = 0; i < xD.length; i++)
			for (int j = 0; j < xD[i].length; j++)
				xD[i][j] = new Double(x[i][j]);
		String[] y = {"A_", "B_", "C_", "A_", "B_", "C_"};
		String[] attrib = {"X1", "X2", "X3"};
		test.setData(attrib, typAttrib, classAttrib, y, xD);
		test.build();   
		Double[][] newx = {
				{new Double(99), new Double(89), new Double(79)},
				{new Double(19), new Double(29), new Double(39)},
		};
		test.setParameters(newx);
		test.probabilities();
		Object[][] result = test.getProbabilities();
		assertNotNull (test.attributeNames());
		assertNotNull (test.bias());
		assertNotNull(test.classAttributeNames());
		assertNotNull (test.sparseIndices());
		assertNotNull (test.sparseWeights()); 
		assertNotNull(result);
		assertEquals (false, test.getBuildLogisticModels());
		assertEquals (1.0, test.getC(), 0.001);
		assertEquals (250007, test.getCacheSize());
		assertEquals (1.0E-12, test.getEpsilon(), 0.001);
		assertEquals (1.0, test.getExponent(), 0.01);
		assertEquals (false, test.getFeatureSpaceNormalization());
		assertEquals (0.01, test.getGamma(),  0.001);
		assertEquals (false, test.getLowerOrderTerms());
		assertEquals (-1, test.getNumFolds());
		assertEquals (1, test.getRandomSeed());
		assertEquals (0.0010, test.getToleranceParameter(), 0.001);
		assertEquals (false, test.getUserRBF());
		assertEquals (3, test.numClassAttributeValues());	   
	}

	/**
	 * @throws CDKException
	 * @throws Exception
	 * @throws QSARModelException
	 */
//	public void testSMOModel2() throws CDKException, java.lang.Exception, QSARModelException {
//		SMOModel test = new SMOModel();
//		test.setData("X:\\cdk\\src\\data\\arff\\Table3.arff");
//		test.build();
//		test.setParameters("X:\\cdk\\src\\data\\arff\\Table4.arff");
//		test.probabilities();
//		Object[][] result = test.getProbabilities();
//		assertNotNull (test.attributeNames());
//		assertNotNull (test.bias());
//		assertNotNull(test.classAttributeNames());
//		assertNotNull (test.sparseIndices());
//		assertNotNull (test.sparseWeights()); 
//		assertNotNull(result);
//		assertEquals (false, test.getBuildLogisticModels());
//		assertEquals (1.0, test.getC(), 0.001);
//		assertEquals (250007, test.getCacheSize());
//		assertEquals (1.0E-12, test.getEpsilon(), 0.001);
//		assertEquals (1.0, test.getExponent(), 0.01);
//		assertEquals (false, test.getFeatureSpaceNormalization());
//		assertEquals (0.01, test.getGamma(), 0.001);
//		assertEquals (false, test.getLowerOrderTerms());
//		assertEquals (-1, test.getNumFolds());
//		assertEquals (1, test.getRandomSeed());
//		assertEquals (0.0010, test.getToleranceParameter(), 0.001);
//		assertEquals (false, test.getUserRBF());
//		assertEquals (3, test.numClassAttributeValues());
//	}
}
