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
import weka.classifiers.bayes.NaiveBayes;

import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

import java.io.BufferedReader;
import java.io.FileReader;

/** 
 * A modelling class for a Naive Bayes classifier using estimator classes from the Weka library. 
 * Numeric estimator precision values are chosen based on analysis of the training data. 
 * For this reason, the classifier is not an UpdateableClassifier.
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     NaiveBayes nb = new NaivesBayes();
 *     nb.setOptions(options);
 *     nb.setData(attrib, typAttrib, classAttrib, data);
 *     nb.build();
 *     
 * } catch (Exception e) {
 *     System.out.println(e.toString());
 * }
 * </pre>
 *
 * !!!Other option is set the data from a file format arff.!!! 
 * !!!!!!!!!!!!!THIS OPTION SUPPLIES AN UNSUSPECTED BUG BY CALCULATING THE PROBABILITIES!!!!!!!!!!!!!!
 * <pre>
 * !!!MultilayerPerceptron mp = new MultilayerPerceptron();!!!
 * !!!mp.setData("/some/where/dataTraining.arff");!!!
 * !!!!!!!!!!!!!THIS OPTION SUPPLIES AN UNSUSPECTED BUG BY CALCULATING THE PROBABILITIES!!!!!!!!!!!!!!
 * </pre>
 * <p>Valid options are (according weka library):</p>
 * <p>-K: Use kernel estimation for modelling numeric attributes rather than a single normal distribution.</p>
 * <p>-D: Use supervised discretization to process numeric attributes</p>
 *
 * @author      Mario Baseda
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsar-weka
 * @cdk.svnrev  $Revision: 9162 $
 * @see Weka
 * 
 * @cdk.keyword classifiers, bayes, NaiveBayes
 */
public class NaiveBayesModel {
	/** An instance containing the data which should be classifier as arff file.*/
	private static Instances data;
	/**Array of strings containing the options*/
	private String[] options;
	/**new instance of classifier*/
	private NaiveBayes nb = new NaiveBayes();
	private Weka weka = new Weka();
	/**An Array Object containing the independent variable*/
	private Object[][] newX = null;
	/**A String specifying the path of the file, format arff,
	 * which contains the new independent values*/
	private String pathNewX = null;
	/** An array object which contains the results of the probabilities*/
	private Object[][] object = null;


	/**
	 * Constructor of the NaiveBayesModel object
	 */
	public NaiveBayesModel(){}

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
	 * Parses a given list of options. The parameters are determited from weka. 
	 * 
	 * <p>Valid options are (according weka library):</p>
	 * <p>-K: Use kernel estimation for modelling numeric attributes rather than a single normal distribution.</p>
	 * <p>-D: Use supervised discretization to process numeric attributes</p>  
	 *
	 * @param options    An Array of strings containing the options 
	 * @throws QSARModelException if the options are of the wrong type for the given modeling function
	 * 
	 */
	public void setOptions(String[] options) throws QSARModelException{
		this.options = options;
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
				nb.setOptions(options);
//			build the model
			nb.buildClassifier(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
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


	/** Calculates the class membership probabilities for the given instance once a classification 
	 *  model has been built with the buildClassifier call
	 *
	 * @throws Exception
	 */
	public void probabilities() throws Exception{
		try{
			if(pathNewX != null){
//				This supplies an unsuspected bug (Array index out of bounce error)
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				this.object = new Object[test.numInstances()][];
				for(int i = 0 ; i < test.numInstances(); i++){
					// Here occures the bug
					double[] result = nb.distributionForInstance(test.instance(i));
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
					double[] result = nb.distributionForInstance(instance);
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
	 * Returns the probabilities of each class type. 
	 *
	 * This function only returns meaningful results if the <code>probabilities</code>
	 * method of this class has been called.
	 *
	 * @return An Object[][] containing the probabilities of each class type
	 */
	public Object[][] getProbabilities() {
		return this.object;
	}

	/** Updates the classifier with the given instance once a classification model has been built with the buildClassifier call
	 *
	 * @throws Exception
	 */
	public void updateClassifier() throws Exception{
		try{
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				for(int i = 0 ; i < test.numInstances(); i++){
					nb.updateClassifier(test.instance(i));
				}
			}
			else if(newX != null){
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
					nb.updateClassifier(instance);
				}
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
	}

	/**Gets the current settings of the classifier
	 * @return A String[] containig the settings of NeuralNet
	 */
	public String[] getOptions(){
		return nb.getOptions();
	}

	/**Gets if kernel estimator is being used
	 * @return true or false
	 */
	public boolean getUseKernelEstimator(){
		return nb.getUseKernelEstimator();
	}

	/**Get whether supervised discretization is to be used
	 * @return true or false
	 */
	public boolean getUseSupervisedDiscretization(){
		return nb.getUseSupervisedDiscretization();
	}
}
