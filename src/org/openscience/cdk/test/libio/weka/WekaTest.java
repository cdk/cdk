/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-05-01 10:49:56 +0200 (Mo, 01 Mai 2006) $
 * $Revision: 6096 $
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
 * @cdk.module test-libio-weka
 * 
 */
public class WekaTest extends CDKTestCase {

    private Weka weka;
	/**
	 *  Constructor for the WekaTest object
	 *
	 */
	public  WekaTest() {
		weka = new Weka();
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
	 *  A unit test for JUnit. Test usign file arff format, algorithm = Lineal Regression
	 */
	public void test1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		LinearRegression lr = new LinearRegression();
		String[] options = new String[4];
		options[0] = "-U";                                   
		options[1] = "0";                                    
		options[2] = "-R";                                   
		options[3] = "0.00000008"; 
		lr.setOptions(options);
		weka.setDataset("data/arff/Table1.arff", lr);
		double[] result = weka.getPrediction("data/arff/Table2.arff");
		assertNotNull(result);
    }
	/**
	 *  A unit test for JUnit. Test using  Array, algorithm = Lineal Regression
	 */
	public void test2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		LinearRegression lr = new LinearRegression(); 
		String[] options = new String[4];
		options[0] = "-U";                                   
		options[1] = "0";                                    
		options[2] = "-R";                                   
		options[3] = "0.00000008"; 
		lr.setOptions(options);
		weka.setDataset("data/arff/Table1.arff", lr);
		double[] testX = {0.39,9.06,-0.11};
		double result = weka.getPrediction(testX);
//		assertNotNull(result);
    }
	/**
	 *  A unit test for JUnit. Test using Array, algorithm = Lineal Regression
	 */
	public void test3() throws ClassNotFoundException, CDKException, java.lang.Exception {
		LinearRegression lr = new LinearRegression();
		String[] attrib = {"aX","bX","cX", "PY" };
		int[] typAttrib = {Weka.NUMERIC,Weka.NUMERIC,Weka.NUMERIC,Weka.NUMERIC};
		double[][] x = {{0.39,9.62 ,-0.15 },{1.64 ,   9.77 ,  -0.13},{1.06 ,  12.56 ,  -0.16},
			{1.26 ,  10.51 ,  -0.05},{1.45 ,  10.15 ,  -0.09},{2.27 ,   9.8  ,  -0.13},
			{1.89 ,  10.54 ,  -0.05},{1.06 ,  10.15 ,   0.01},{2.59 ,   9.81 ,  -0.13},
			{2.74 ,   9.81 ,  -0.13},{2.79 ,   9.85 ,  -0.12},{0.39 ,   9.14 ,  -0.11},
			{1.64 ,   9.28 ,  -0.1}, {1.06 ,  10.34 ,   0.03},{1.06 ,  10.15 ,   0.01},
			{2.27 ,   9.31 ,  -0.09},{2.59 ,   9.31 ,  -0.09},{2.74 ,   9.31 ,  -0.09},
			{2.79 ,   9.35 ,  -0.09},{0.39 ,   9.06 ,  -0.11},{1.64 ,   9.2  ,  -0.09}};   
		double[] y = { 12.74,11.3 ,13.0 ,12.6 ,11.9 ,11.01,12.5 ,12.9 ,10.88,10.84,11.21,11.67,
				10.53,12.0 ,11.83,10.28,10.28,10.15,10.43,10.38,9.5 };
		Double[] yD = new Double[y.length];
		for(int i = 0 ; i< yD.length; i++)
			yD[i] = new Double(y[i]);
		weka.setDataset(attrib, typAttrib, yD, x, lr);
		double[] testX = {0.39,9.06,-0.11};
		double resultY = weka.getPrediction(testX);
//		assertNotNull(resultY);
    }
	/**
	 *  A unit test for JUnit. Test using Array, algorithm = J48
	 */
	public void test4() throws ClassNotFoundException, CDKException, java.lang.Exception {
		String[] options = new String[1];
		options[0] = "-U";
		J48 j48 = new J48();
		j48.setOptions(options);
		
		String[] attrib = {"aX","bX","cX"};
		int[] typAttrib = {Weka.NUMERIC,Weka.NUMERIC,Weka.NUMERIC};
		String[] classAttrib = {"A_","B_","C_"};
		double[][] x = {{10,10 ,10 },{10 ,   10 ,  -10},{-10 ,  -10 ,  -10},
				{11,11 ,11 },{11 ,   11 ,  -11},{-11 ,  -11 ,  -11}};
		String[] y = { "A_","B_" ,"C_","A_","B_" ,"C_"};
		weka.setDataset(attrib, typAttrib, classAttrib, y, x, j48);
		double[] testX = {10,10,-11};
		double resultY = weka.getPrediction(testX);
//		assertNotNull(resultY);
    }
}
