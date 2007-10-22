/* $Revision: 6228 $ $Author: egonw $ $Date: 2006-05-11 18:34:42 +0200 (Thu, 11 May 2006) $
 *
 * Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.model.weka;

import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

import weka.classifiers.functions.LinearRegression;
/** 
 * A modeling class that provides a linear least squares regression model using Weka library.
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     LinearRegressionWModel lrm = new LinearRegressionWModel(x,y); or LinearRegressionWModel lrm = new LinearRegressionWModel(typAttrib,classAttrib,x,y,attrib);
 *     lrm.setOptions(options);
 *     lrm.build();
 *     lrm.setParameters(newX);
 *     lrm.predict();
 *     Double[] predictedvalues = (Double[])lrm.getPredictPredicted();
 * 
 * } catch (QSARModelException qme) {
 *     System.out.println(qme.toString());
 * }
 * </pre>
 * Other option is set the data from a file format arff.
 * <pre>
 * LinearRegressionWModel lrm = new LinearRegressionWModel("/some/where/dataTraining.arff");
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
 * <p>Valid options are (according weka library):</p>
 * <p>-D: Produce debugging output.</p>
 * <p>-S num: </br> Set the attriute selection method to use. 1=None, 2=Greedy(default 0=M5' method)
 * <p>-C: Do no try to eleminate colinear attributes</p>
 * <p>-R num: The ridge parameter(default 1.0e-8)</p>
 * 
 * @author      Miguel Rojas
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsar-weka
 * @cdk.svnrev  $Revision: 9162 $
 * @see Weka
 * 
 * @cdk.keyword regression, linear
 */
public class LinearRegressionWModel implements IWekaModel{
	/**Dependent variable */
	private Object[] y;
	/**Independent variable */
	private Object[][] x;
	private Weka weka = null;
	/**Array of strings containing the options*/
	private String[] options;
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
	 * Constructor of the LinearRegressionWModel object from varibles
	 * @param y  An array containing the dependent variable.
	 * @param x  An double array containing the independent variable.
	 */
	public LinearRegressionWModel(Object[] y, Object[][] x){
		this.y = y;
		this.x = x;
	}

	/**
	 * Constructor of the LinearRegressionWModel object from varibles
	 * 
	 * @param y  An array containing the dependent variable (class value).
	 * @param x  A 2D array containing the independent variable (for example: qsar results).
	 * @param typAttrib  An integer array containing the attribute type
	 * @param attrib	 A string array containing the attributs
	 */
	public LinearRegressionWModel(int[] typAttrib, Object[] y, Object[][] x, String[] attrib){
		this.y = y;
		this.x = x;
		this.typAttrib = typAttrib;
		this.attrib = attrib;
		setAttrib = true;
	}
	/**
	 * Constructor of the LinearRegressionWModel object from file
	 * @param pathTest Path of the dataset file format arff to train
	 */
	public LinearRegressionWModel(String pathTest){
		this.pathTest  = pathTest;
	}

	/**
	 * Parses a given list of options. The parameters are determited from weka. 
	 * 
	 * <p>Valid options are (according weka library):</p>
	 * <p>-D: Produce debugging output.</p>
	 * <p>-S num: </br> Set the attriute selection method to use. 1=None, 2=Greedy(default 0=M5' method)
	 * <p>-C: Do no try to eleminate colinear attributes</p>
	 * <p>-R num: The ridge parameter(default 1.0e-8)</p>
	 * 
	 * @param options An Array of strings containing the options 
	 * @throws QSARModelException if the options are of the wrong type for the given modeling function
	 * 
	 */
	public void setOptions(String[] options) throws QSARModelException {
		this.options = options;
	}
	/**
	 * Get the current settings of the classifier. The parameters are determited from weka. And are specific for each
	 * algorithm.
	 *
	 * @return An Array of strings containing the options 
	 * @throws QSARModelException if the options are of the wrong type for the given modeling function
	 * 
	 */
	public String[] getOptions() throws QSARModelException {
		return options;
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
			LinearRegression lr = new LinearRegression();
			if(options != null)
				lr.setOptions(options);

			if(pathTest != null){
				weka.setDatasetCDK(pathTest, lr);
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
				weka.setDataset(attrib,typAttrib,y,x,lr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Specifies the parameters to predict. In this case will be the dependent varibles.
	 * It's found into cdk.src
	 * 
	 * @param  path  A String specifying the path of the file, format arff, which contians 
	 * 				 the dependent values with whose to predict. It's found into cdk.src
	 * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
	 * 
	 */
	public void setParametersCDK(String path) throws QSARModelException {
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
				Object[] object = weka.getPredictionCDK(pathNewX);
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
