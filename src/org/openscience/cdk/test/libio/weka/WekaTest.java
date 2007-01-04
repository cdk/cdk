/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-05-01 10:49:56 +0200 (Mo, 01 Mai 2006) $
 * $Revision: 6096 $
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@slists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.test.libio.weka;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.test.CDKTestCase;

import weka.classifiers.functions.LinearRegression;
import weka.classifiers.trees.J48;

/**
 * TestCase for Weka class.
 *
 * @cdk.module test-weka
 */
public class WekaTest extends CDKTestCase {

	/**
	 *  Constructor for the WekaTest object
	 *
	 */
	public  WekaTest() {
	}
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(WekaTest.class);
	}
	/**
	 *  A unit test for JUnit. Test and prediction using file arff format, algorithm = Lineal Regression
	 */
	public void test1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		LinearRegression lr = new LinearRegression();
		String[] options = new String[4];
		options[0] = "-U";                                   
		options[1] = "0";                                    
		options[2] = "-R";                                   
		options[3] = "0.00000008"; 
		lr.setOptions(options);
		Weka weka = new Weka();
		weka.setDatasetCDK("data/arff/Table1.arff", lr);
		Object[] result = weka.getPredictionCDK("data/arff/Table2.arff");
		assertNotNull(result);
    }
	/**
	 *  A unit test for JUnit. Test using file arrf and prdiction using Array, 
	 *  algorithm = Lineal Regression
	 */
	public void test2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		LinearRegression lr = new LinearRegression(); 
		String[] options = new String[4];
		options[0] = "-U";                                   
		options[1] = "0";                                    
		options[2] = "-R";                                   
		options[3] = "0.00000008"; 
		lr.setOptions(options);
		Weka weka = new Weka();
		weka.setDatasetCDK("data/arff/Table1.arff", lr);
		Object[][] testX = {{new Double(2),new Double(2)},
			{new Double(5),new Double(5)}
		};
		Object[] result = weka.getPrediction(testX);
		assertNotNull(result);
    }
	/**
	 *  A unit test for JUnit. Test and prediction using Array, algorithm = Lineal Regression
	 */
	public void test3() throws ClassNotFoundException, CDKException, java.lang.Exception {
		LinearRegression lr = new LinearRegression();
		String[] attrib = {"X2","X1", "Y" };
		int[] typAttrib = {Weka.NUMERIC,Weka.NUMERIC,Weka.NUMERIC};
		double[][] x = {{ 1,1},{3,3},{4,4},{6,6} };
		Double[][] xD = new Double[x.length][x[0].length];
		for(int i = 0 ; i< xD.length; i++)
			for(int j = 0 ; j < xD[i].length ; j++)
				xD[i][j] = new Double(x[i][j]);
		
		double[] y = { 0,2,3,5};
        Double[] yD = new Double[y.length];
		for(int i = 0 ; i< yD.length; i++)
			yD[i] = new Double(y[i]);

		Weka weka = new Weka();
		weka.setDataset(attrib, typAttrib, yD, xD, lr);
		Double[][] testX = {{new Double(2),new Double(2)},
				{new Double(5),new Double(5)}
			};
		Object[] result = weka.getPrediction(testX);
		assertNotNull(result);
    }
	/**
	 *  A unit test for JUnit. Test prediction using Array, algorithm = J48
	 */
	public void test4() throws ClassNotFoundException, CDKException, java.lang.Exception {
		String[] options = new String[1];
		options[0] = "-U";
		J48 j48 = new J48();
		j48.setOptions(options);
		
		String[] attrib = {"aX","bX","cX"};
		int[] typAttrib = {Weka.NUMERIC,Weka.NUMERIC,Weka.NUMERIC};
		String[] classAttrib = {"A_","B_","C_"};
		double[][] x = {{10,10 ,10 },{10 ,   -10 ,  -10},{-10 ,  -10 ,  -10},
				{11,11 ,11 },{11 ,   -11 ,  -11},{-11 ,  -11 ,  -11}};
		Double[][] xD = new Double[x.length][x[0].length];
		for(int i = 0 ; i< xD.length; i++)
			for(int j = 0 ; j < xD[i].length ; j++)
				xD[i][j] = new Double(x[i][j]);
		String[] y = { "A_","B_" ,"C_","A_","B_" ,"C_"};
		Weka weka = new Weka();
		weka.setDataset(attrib, typAttrib, classAttrib, y, xD, j48);
		Double[][] testX = {{new Double(11),new Double(-11),new Double(-11)},
				{new Double(-10),new Double(-10),new Double(-10)}};
		Object[] resultY = weka.getPrediction(testX);
		assertNotNull(resultY);
    }
}
