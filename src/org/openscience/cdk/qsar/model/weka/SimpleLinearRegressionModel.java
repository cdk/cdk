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
package org.openscience.cdk.qsar.model.weka;

import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

import weka.classifiers.functions.SimpleLinearRegression;

/** 
 * A modeling class that provides a simple linear least squares regression model using Weka library.
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     SimpleLinearRegressionModel slrm = new SimpleLinearRegressionModel(x,y); or SimpleLinearRegressionModel slrm = new SimpleLinearRegressionModel(typAttrib,classAttrib,x,y,attrib);
 *     slrm.build();
 *     slrm.setParameters(newX);
 *     slrm.predict();
 *     Double[] predictedvalues = (Double[])slrm.getPredictPredicted();
 * 
 * } catch (QSARModelException qme) {
 *     System.out.println(qme.toString());
 * }
 * </pre>
 * Other option is set the data from a file format arff.
 * <pre>
 * SimpleLinearRegressionModel slrm = new SimpleLinearRegressionModel("/some/where/dataTraining.arff");
 * </pre>
 * Note that when making predictions, the new X matrix must be set by calls
 * to setParameters(). The following table lists the parameters that can be set and their 
 * expected types. 
 * <center>
 * <table border=1 cellpadding=5>
 * <THEAD>
 * <tr>
 * <th>Name</th><th>Java Type</th><th>Notes</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>x</td><td>Double[][]</td><td></td>
 * </tr>
 * <tr>
 * <td>y</td><td>Double[]</td><td>Length should be equal to the rows of x</td>
 * </tr>
 * <tr>
 * <td>newX</td><td>Double[][]</td><td>Number of columns should be the same as in x</td>
 * </tr>
 * <tr>
 * </tbody>
 * </table>
 * </center>
 * 
 * @author      Mario Baseda
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsarweka
 * @cdk.svnrev  $Revision: 9162 $
 * @see Weka
 * 
 * @cdk.keyword regression, simple linear
 */
public class SimpleLinearRegressionModel {
	/**Dependent variable */
	private Object[] y;
	/**Independent variable */
	private Object[][] x;
	private Weka weka = null;
	/**A String specifying the path of the file, format arff,
	 * which contians the variables and attributes with whose to test.*/
	private String pathTest = null;
	/** results of the prediction*/
	private Double[] results;
	/**A Array Object containing the independent variable*/
	private Object[][] newX = null;
	/**A String specifying the path of the file, format arff,
	 * which contians the independent values with whose to predict.*/
	private String pathNewX = null;
	/**Attribute type: NUMERICAL or NOMINAL*/
	private int[] typAttrib;
	/** String with the attributs*/
	private String[]attrib;
	/** Boolean if the attributs was set*/
	private boolean setAttrib = false;

	/**
	 * Constructor of the SimpleLinearRegressionModel object from varibles
	 * @param y  An array containing the dependent variable.
	 * @param x  An double array containing the independent variable.
	 */
	public SimpleLinearRegressionModel(Object[] y, Object[][] x){
		this.y = y;
		this.x = x;
	}

	/**
	 * Constructor of the SimpleLinearRegressionModel object from varibles
	 * @param y  An array containing the dependent variable (class value).
	 * @param x  A 2D array containing the independent variable (for example: qsar results).
	 * @param typAttrib  An integer array containing the attribute type
	 * @param attrib  A string array containing the attributs
	 */
	public SimpleLinearRegressionModel(int[] typAttrib, Object[] y, Object[][] x, String[] attrib){
		this.y = y;
		this.x = x;
		this.typAttrib = typAttrib;
		this.attrib = attrib;
		setAttrib = true;
	}

	/**
	 * Constructor of the SimpleLinearRegressionModel object from file
	 * @param pathTest Path of the dataset file format arff to train
	 */
	public SimpleLinearRegressionModel(String pathTest){
		this.pathTest  = pathTest;
	}

	/**
	 * Builds (trains) the model.
	 *
	 * @throws QSARModelException if errors occur in data types, calls to the R session. See
	 * the corresponding method in subclasses of this class for further details.
	 */
	public void build() throws QSARModelException {
		weka = new Weka();
		try {
			SimpleLinearRegression slr = new SimpleLinearRegression();

			if(pathTest != null){
				weka.setDataset(pathTest, slr);
			}else{
				if (!(setAttrib)){
					this.attrib = new String[x[0].length+1];
					this.typAttrib = new int[x[0].length+1];
					for(int i = 0 ; i < x[0].length; i++){
						attrib[i] = "X"+i;
						typAttrib[i] = Weka.NUMERIC;
					}
					attrib[x[0].length] = "Y";
					typAttrib[x[0].length] = Weka.NUMERIC;
				}
				weka.setDataset(attrib,typAttrib,y,x,slr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Specifies the parameters to predict. In this case will be the dependent varibles.
	 * 
	 * @param  path  A String specifying the path of the file, format arff, which contians 
	 * 				 the dependent values with whose to predict.
	 * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
	 * 
	 */
	public void setParameters(String path) throws QSARModelException {
		this.pathNewX = path;
	}

	/**
	 * Specifies the parameters to predict. In this case will be the independent varibles.
	 * 
	 * @param  newX  A 2D array Object containing the independent variable.
	 * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
	 */
	public void setParameters(Object[][] newX) throws QSARModelException {
		this.newX = newX;
	}

	/**
	 * Makes predictions using a previously built model.
	 *
	 * @throws QSARModelException if errors occur in data types, calls to the R session. See
	 * the corresponding method in subclasses of this class for further details.
	 */
	public void predict() throws QSARModelException {
		try{
			if(pathNewX != null){
				Object[] object = weka.getPrediction(pathNewX);
				results = new Double[object.length];
				for(int i = 0 ; i < object.length; i++){
					results[i] = (Double)object[i];
				}
			}
			else if(newX != null){
				Object[] object = weka.getPrediction(newX);
				results = new Double[object.length];
				for(int i = 0 ; i < object.length; i++){
					results[i] = (Double)object[i];
				}
			}

		} catch ( Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Returns the predicted values for the prediction set. 
	 *
	 * This function only returns meaningful results if the <code>predict</code>
	 * method of this class has been called.
	 *
	 * @return A Double[] containing the predicted values
	 */
	public Object[] getPredictPredicted() {
		return results;
	}
}
