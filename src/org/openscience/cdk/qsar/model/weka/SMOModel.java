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
import weka.classifiers.functions.SMO;
import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

import java.io.BufferedReader;
import java.io.FileReader;

/** 
 * A modelling class that provides the John C. Platt's sequential minimal optimization algorithm (SMO)
 * for training a support vector classifier using Weka library.
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     SMO smo = new SMO();
 *     smo.setOptions(options);
 *     smo.setData(attrib, typAttrib, classAttrib, data);
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
 * <p>Valid options are (according weka library):</p>
 * <p>-C num: The complexity constant C. (default 1)</p>
 * <p>-E num: The exponent for the polynomial kernel. (default 1)</p>
 * <p>-G num: Gamma for the RBF kernel. (default 0.01)</p>
 * <p>-N <0|1|2>: Whether to 0=normalize/1=standardize/2=neither. (default 0=normalize)</p>
 * <p>-F: Feature-space normalization (only for non-linear polynomial kernels).</p>
 * <p>-O: Use lower-order terms (only for non-linear polynomial kernels).</p>
 * <p>-R: Use the RBF kernel. (default poly)</p>
 * <p>-A num: Sets the size of the kernel cache. Should be a prime number. (default 250007, use 0 for full cache)</p> 
 * <p>-L num: Sets the tolerance parameter. (default 1.0e-3)</p>
 * <p>-P num: Sets the epsilon for round-off error. (default 1.0e-12)</p>
 * <p>-M: Fit logistic models to SVM outputs.</p>
 * <p>-V num: Number of folds for cross-validation used to generate data for logistic models. (default -1, use training data)</p>
 * <p>-W num: Random number seed for cross-validation. (default 1)</p> 


 *
 * @author      Mario Baseda
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsarweka
 * @cdk.svnrev  $Revision: 9162 $
 * @see Weka
 * 
 * @cdk.keyword classifiers, funktions, SMO
 */
public class SMOModel {
	/** An instance containing the data which should be classifier as arff file.*/
	private static Instances data;
	/**Array of strings containing the options*/
	private String[] options;
	/**new instance of classifier*/
	private SMO smo = new SMO();
	private Weka weka = new Weka();
	/**An Array Object containing the independent variable*/
	private Object[][] newX = null;
	/**A String specifying the path of the file, format arff,
	 * which contains the new independent values*/
	private String pathNewX = null;
	/** An array object which contains the probabilitiesof the new values*/
	private Object[][] object = null;


	/**
	 * Constructor of the SMO object
	 */
	public SMOModel(){}

	/**
	 * Parses a given list of options. The parameters are determited from weka. 
	 * 
	 * <p>Valid options are (according weka library):</p>
	 * <p>Valid options are (according weka library):</p>
	 * <p>-C num: The complexity constant C. (default 1)</p>
	 * <p>-E num: The exponent for the polynomial kernel. (default 1)</p>
	 * <p>-G num: Gamma for the RBF kernel. (default 0.01)</p>
	 * <p>-N <0|1|2>: Whether to 0=normalize/1=standardize/2=neither. (default 0=normalize)</p>
	 * <p>-F: Feature-space normalization (only for non-linear polynomial kernels).</p>
	 * <p>-O: Use lower-order terms (only for non-linear polynomial kernels).</p>
	 * <p>-R: Use the RBF kernel. (default poly)</p>
	 * <p>-A num: Sets the size of the kernel cache. Should be a prime number. (default 250007, use 0 for full cache)</p> 
	 * <p>-L num: Sets the tolerance parameter. (default 1.0e-3)</p>
	 * <p>-P num: Sets the epsilon for round-off error. (default 1.0e-12)</p>
	 * <p>-M: Fit logistic models to SVM outputs.</p>
	 * <p>-V num: Number of folds for cross-validation used to generate data for logistic models. (default -1, use training data)</p>
	 * <p>-W num: Random number seed for cross-validation. (default 1)</p> 
	 *
	 * @param options    An Array of strings containing the options 
	 * @throws QSARModelException if the options are of the wrong type for the given modeling function
	 * 
	 */
	public void setOptions(String[] options) throws QSARModelException{
		this.options = options;
	}

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
	 * 
	 * @param typAttrib   Attribute type: NUMERICAL or NOMINAL.
	 * @param classAttrib String with a list of the attribut classes.
	 * @param y           An array containing the dependent variable (class value).
	 * @param x           A 2D array containing the independent variable (for example: qsar results). 
	 * @param attrib	  A string array containing the attributs
	 * @throws Exception  if it is unable to parse the data
	 * 
	 */
	public void setData(String[] attrib, int[] typAttrib, String[] classAttrib, Object[] y, Object[][] x) throws Exception{
		data = weka.setDataset(attrib,typAttrib,classAttrib,y,x,null);	
	}

	/**
	 * Builds the model.
	 *
	 * @throws Exception if errors occur in data types. See
	 * the corresponding method in subclasses of this class for further details.
	 */
	public void build() throws Exception {
		try {
			if(options != null)
//				set the options
				smo.setOptions(options);
//			build the model
			smo.buildClassifier(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Specifies the new parameters as arff file.
	 * 
	 * @param  path  A String specifying the path of the file, format arff, which contians 
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


	/** Estimates the probabilities for the given instance, which was built from the new parameters before.
	 *
	 * @throws Exception
	 */
	public void probabilities() throws Exception{
		try{
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				this.object = new Object[test.numInstances()][];
				for(int i = 0 ; i < test.numInstances(); i++){
					double[] result = smo.distributionForInstance(test.instance(i));
					this.object[i] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						this.object[i][z] = new Double(result[z]);
					}
				}
			}
			else if(newX != null){
				this.object = new Object[newX.length][];
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
					double[] result = smo.distributionForInstance(instance);
					this.object[j] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						this.object[j][z] = new Double(result[z]);
					}
				}
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Returns the probabilities of the new parameters. 
	 *
	 * This function only returns meaningful results if the <code>probabilities</code>
	 * method of this class has been called.
	 *
	 * @return An Object[][] containing the probabilities values as Double
	 */
	public Object[][] getProbabilities() {
		return this.object;
	}

	/** Returns the attribute names
	 * @return A String[][][] containing the attribute names
	 */
	public String[][][] attributeNames(){
		return smo.attributeNames();	
	}

	/** Returns the bias of each binary SMO
	 * @return A double[][] containing the bias of each binary SMO
	 */
	public double[][] bias(){
		return smo.bias();
	}

	/**Returns the class attribute names
	 * @return A String[] containing the class attribute names
	 */
	public String[] classAttributeNames(){
		return smo.classAttributeNames();
	}

	/**Get the value of buildLogisticModels
	 * @return true or false
	 */
	public boolean getBuildLogisticModels(){
		return smo.getBuildLogisticModels();
	}

	/**Get the value of C
	 * @return A double containig the value of C
	 */
	public double getC(){
		return smo.getC();
	}

	/**Get the size of the kernel cache
	 * @return An integer containing the size of the kernel cache
	 */
	public int getCacheSize(){
		return smo.getCacheSize();
	}

	/**Get the value of epsilon
	 * @return A double containing the value of epsilon
	 */
	public double getEpsilon(){
		return smo.getEpsilon();
	}

	/**Get the value of exponent
	 * @return A double containing the value of exponent
	 */
	public double getExponent(){
		return smo.getExponent();
	}

	/**Check whether feature spaces is being normalized.
	 * @return true or false
	 */
	public boolean getFeatureSpaceNormalization() throws Exception{
		return smo.getFeatureSpaceNormalization();
	}

	/**Get the value of gamma
	 * @return A double containing the value of gamma
	 */
	public double getGamma(){
		return smo.getGamma();
	}

	/**Check whether lower-order terms are being used
	 * @return true or false
	 */
	public boolean getLowerOrderTerms(){
		return smo.getLowerOrderTerms();
	}

	/**Get the value of numFolds.
	 * @return An integer containing the value of numFolds
	 */
	public int getNumFolds(){
		return smo.getNumFolds();
	}

	/**Get the value of randomSeed
	 * @return An integer containing the value of randomSeed
	 */
	public int getRandomSeed(){
		return smo.getRandomSeed();
	}

	/**Get the value of tolerance parameter
	 * @return A double containig the value of tolerance parameter
	 */
	public double getToleranceParameter(){
		return smo.getToleranceParameter();
	}

	/**Check if the RBF kernel is to be used
	 * @return true or false
	 */
	public boolean getUserRBF(){
		return smo.getUseRBF();
	}

	/**Return the number of class attribute values
	 * @return An integer containing the class attribute values
	 */
	public int numClassAttributeValues(){
		return smo.numClassAttributeValues();
	}

	/**Returns the indices in sparse format
	 * @return An int[][][] containing the indices in sparse format
	 */
	public int[][][] sparseIndices(){
		return smo.sparseIndices();
	}

	/**Returns the weights in sparse format.
	 * @return A double[][][] containing the weights in sparse format
	 */
	public double[][][] sparseWeights(){
		return smo.sparseWeights();
	}

	/**
	 * Get the current settings of the classifier. The parameters are determited from weka. And are specific for each
	 * algorithm.
	 *
	 * @return An Array of strings containing the options 
	 * @throws QSARModelException if the options are of the wrong type for the given modeling function
	 * 
	 */
	public String[] getOptions() throws QSARModelException{
		return this.options;
	}
}

