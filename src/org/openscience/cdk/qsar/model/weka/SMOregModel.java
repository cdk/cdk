/* $RCSfile$
 * $Author: mariobaseda $
 * $Date: 2007-01-03 17:57:14 +0100 (We, 03 Jan 2007) $
 * $Revision: 5602 $
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

import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.functions.SMOreg;
import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

import java.io.BufferedReader;
import java.io.FileReader;

/** 
 * A modelling class that provides the Alex J.Smola and Bernhard Scholkopf 
 * sequential minimal optimization algorithm for training a support vector regression 
 * using Weka library (polynomial or RBF kernels). This implementation globally replaces all missing 
 * values and transforms nominal attributes into binary ones. It also normalizes all
 * attributes by default. 
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     SMOreg smoreg = new SMOreg();
 *     smo.setData(attrib, typAttrib, classAttrib, data); or smo.setData(x, y);
 *     smo.build();
 *     
 * } catch (Exception e) {
 *     System.out.println(e.toString());
 * }
 * </pre>
 * Other option is set the data from a file format arff.
 * <pre>
 * SMO smo = new SMO();
 * smo.setData("/some/where/dataTraining.arff");
 * </pre>
 *
 * @author      Mario Baseda
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsar-weka
 * @cdk.svnrev  $Revision: 9162 $
 * @see Weka
 * 
 * @cdk.keyword classifiers, funktions, SMOreg
 */
public class SMOregModel {
	/** An instance containing the data which should be classifier as arff file.*/
	private static Instances data;
	/**new instance of classifier*/
	private SMOreg smoreg = new SMOreg();
	private Weka weka = new Weka();
	/**An Array Object containing the independent variable*/
	private Object[][] newX = null;
	/**A String specifying the path of the file, format arff,
	 * which contains the new independent values*/
	private String pathNewX = null;
	/** An array object which contains the classifacation of the new values*/
	private Object[] object = null;

	/**
	 * Constructor of the SMOregModel object
	 */
	public SMOregModel(){}

	/**
	 * Set the variable data to the arff file 
	 *
	 * @param filename   The path of the file, format arff 
	 * @throws Exception if the parameters are of the wrong type for the given modeling function
	 * 
	 */
	public void setData(String filename)throws Exception{
		data = weka.setDataset(filename, null); 
	}

	/**
	 * Parses a given list of data to an arff file, and set the variable data on it. 
	 * @param y  An array containing the dependent variable.
	 * @param x  An double array containing the independent variable.
	 */
	public void setData(Object[] y, Object[][] x) throws Exception{
		String[] attrib = new String[x[0].length+1];
		int[] typAttrib = new int[x[0].length+1];
		for(int i = 0 ; i < x[0].length; i++){
			attrib[i] = "X"+i;
			typAttrib[i] = Weka.NUMERIC;
		}
		attrib[x[0].length] = "Y";
		typAttrib[x[0].length] = Weka.NUMERIC;
		data = weka.setDataset(attrib,typAttrib,y,x,null);
	}

	/**
	 * Parses a given list of data to an arff file, and set the variable data on it.  
	 * 
	 * @param typAttrib   Attribute type: NUMERICAL or NOMINAL.
	 * @param classAttrib String with a list of the attribut classes.
	 * @param y           An array containing the dependent variable (class value).
	 * @param x           A 2D array containing the independent variable (for example: qsar results). 
	 * @param attrib	  A string array containing the attributs
	 * @throws Exception  if it is unable to parse the data
	 * 
	 */
	public void setData(int[] typAttrib, Object[] y, Object[][] x, String[] attrib) throws Exception{
		data = weka.setDataset(attrib,typAttrib,y,x,null);	
	}

	/**
	 * Builds the model.
	 *
	 * @throws Exception if errors occur in data types. See
	 * the corresponding method in subclasses of this class for further details.
	 */
	public void build() throws Exception {
		try {
//			build the model
			smoreg.buildClassifier(data);
		} catch (Exception e) {
			e.printStackTrace();
		};
		return;
	}

	/**
	 * Specifies the new parameters as arff file.
	 * 
	 * @param  path  A String specifying the path of the file, format arff, which contains 
	 * 				 the new values.
	 * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
	 * 
	 */
	public void setParameters(String path) throws QSARModelException {
		this.pathNewX = path;
	}

	/**
	 * Specifies the new parameters as 2D array object.
	 * 
	 * @param  newX  A 2D array Object containing the new values.
	 * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
	 */
	public void setParameters(Object[][] newX) throws QSARModelException {
		this.newX = newX;
	}

	/** Classifies a given instance, which was built from the new parameters before.
	 *
	 * @throws Exception
	 */
	public void classifyInstance() throws Exception{
		try{
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				this.object = new Object[test.numInstances()];
				for(int i = 0 ; i < test.numInstances(); i++){
					double result = smoreg.classifyInstance(test.instance(i));
					this.object[i] = new Double(result);	
				}
			}
			else if(newX != null){
				this.object = new Object[newX.length];
				for(int j = 0 ; j < newX.length ; j++){
					Instance instance = new Instance(data.numAttributes());
					instance.setDataset(data);
					for(int i = 0 ; i < newX[0].length ; i++){
						if(instance.attribute(i).isNumeric())
							instance.setValue(i, ((Double)newX[j][i]).doubleValue());
						else if(instance.attribute(i).isString())
							instance.setValue(i, ""+newX[j][i]);
					}
					instance.setValue(newX[0].length, 0.0);
					double result = smoreg.classifyInstance(instance);
					this.object[j] = new Double(result);	
				}
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Returns the classification of the new parameters. 
	 *
	 * This function only returns meaningful results if the <code>classifyInstance</code>
	 * method of this class has been called.
	 *
	 * @return An Object[] containing the classification values as Double
	 */
	public Object[] getClassification() {
		return this.object;
	}

	/**Get the value of C
	 * @return A double containig the value of C
	 */
	public double getC(){
		return smoreg.getC();
	}

	/**Get the size of the kernel cache
	 * @return An integer containing the size of the kernel cache
	 */
	public int getCacheSize(){
		return smoreg.getCacheSize();
	}

	/**Get the value of epsilon
	 * @return A double containing the value of epsilon
	 */
	public double getEpsilon(){
		return smoreg.getEpsilon();
	}

	/**Get the value of exponent
	 * @return A double containing the value of exponent
	 */
	public double getExponent(){
		return smoreg.getExponent();
	}

	/**Check whether feature spaces is being normalized.
	 * @return true or false
	 */
	public boolean getFeatureSpaceNormalization() throws Exception{
		return smoreg.getFeatureSpaceNormalization();
	}

	/**Get the value of gamma
	 * @return A double containing the value of gamma
	 */
	public double getGamma(){
		return smoreg.getGamma();
	}

	/**Check whether lower-order terms are being used
	 * @return true or false
	 */
	public boolean getLowerOrderTerms(){
		return smoreg.getLowerOrderTerms();
	}

	/**Get the value of tolerance parameter
	 * @return A double containig the value of tolerance parameter
	 */
	public double getToleranceParameter(){
		return smoreg.getToleranceParameter();
	}

	/**Check if the RBF kernel is to be used
	 * @return true or false
	 */
	public boolean getUserRBF(){
		return smoreg.getUseRBF();
	}

	/**Get the value of eps
	 * @return A double containing the value of eps
	 */
	public double getEps(){
		return smoreg.getEps();
	}
}
