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
import org.openscience.cdk.qsar.model.weka.SMOregModel;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs a test for the SMOregModel
 *
 * @author Mario Baseda
 * @cdk.module test-qsar
 */
public class SMOregModelTest extends CDKTestCase{

	/**
	 * Constructor of the SMOregModelTest object
	 */
	public SMOregModelTest(){}

	/**
	 * A unit test suite for JUnit
	 *
	 * @return The test suite
	 */
	public static Test suite() {
		return new TestSuite(SMOregModelTest.class);
	}

	/**
	 * @throws CDKException
	 * @throws Exception
	 * @throws QSARModelException
	 */
	public void testSMOregModel() throws CDKException, java.lang.Exception, QSARModelException{
		SMOregModel test = new SMOregModel();
		double[][] x = {{10, 10, 10}, {10, -10, -10}, {-10, -10, -10},
				{11, 11, 11}, {11, -11, -11}, {-11, -11, -11}};
		Double[][] xD = new Double[x.length][x[0].length];
		for (int i = 0; i < xD.length; i++)
			for (int j = 0; j < xD[i].length; j++)
				xD[i][j] = new Double(x[i][j]);
		Object[] y = {new Double(100), new Double(200), new Double(300), new Double(100), new Double(200), new Double(300)};
		test.setData(y, xD);
		test.build();      
		Double[][] newx = {
				{new Double(99), new Double(89), new Double(79)},
				{new Double(19), new Double(29), new Double(39)},
		};
		test.setParameters(newx);
		test.classifyInstance();
		Object[] result = test.getClassification();
		assertNotNull (result);
		assertEquals (1.0, test.getC(), 0.001);
		assertEquals (250007, test.getCacheSize());
		assertEquals (0.0010, test.getEpsilon(), 0.001);
		assertEquals (1.0, test.getExponent(), 0.01);
		assertEquals (false, test.getFeatureSpaceNormalization());
		assertEquals (0.01, test.getGamma(), 0.001);
		assertEquals (false, test.getLowerOrderTerms());
		assertEquals (0.0010, test.getToleranceParameter(), 0.001);
		assertEquals (false, test.getUserRBF());
		assertEquals (1.0E-12, test.getEps(), 0.001); 
	}

	/**
	 * @throws CDKException
	 * @throws Exception
	 * @throws QSARModelException
	 */
//	public void testSMOregModel2() throws CDKException, java.lang.Exception, QSARModelException {
//		SMOregModel test = new SMOregModel();
//		test.setData("X:\\cdk\\src\\data\\arff\\Table1.arff");
//		test.build();
//		test.setParameters("X:\\cdk\\src\\data\\arff\\Table2.arff");
//		test.classifyInstance();
//		Object[] result = test.getClassification();
//		assertNotNull (result);
//		assertEquals (1.0, test.getC(), 0.001);
//		assertEquals (250007, test.getCacheSize());
//		assertEquals (0.0010, test.getEpsilon(), 0.001);;
//		assertEquals (1.0, test.getExponent(), 0.01);
//		assertEquals (false, test.getFeatureSpaceNormalization());
//		assertEquals (0.01, test.getGamma(), 0.001);
//		assertEquals (false, test.getLowerOrderTerms());
//		assertEquals (0.0010, test.getToleranceParameter(), 0.001);
//		assertEquals (false, test.getUserRBF());
//		assertEquals (1.0E-121, test.getEps(), 0.001); 
//	}
}
