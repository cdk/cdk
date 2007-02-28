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
import weka.classifiers.functions.MultilayerPerceptron;

import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

import java.io.BufferedReader;
import java.io.FileReader;

/** 
 * A modelling class that uses backpropagation to classify instances using Weka library.
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     MultilayerPerceptron mp = new MultilayerPerceptron();
 *     mp.setOptions(options);
 *     mp.setData(attrib, typAttrib, classAttrib, data);
 *     mp.build();
 *     
 * } catch (Exception e) {
 *     System.out.println(e.toString());
 * }
 * </pre>
 * Other option is set the data from a file format arff.
 * <pre>
 * MultilayerPerceptron mp = new MultilayerPerceptron();
 * mp.setData("/some/where/dataTraining.arff");
 * </pre>
 * <p>Valid options are (according weka library):</p>
 * <p>-L num: Set the learning rate. (default 0.3)</p>
 * <p>-M num: Set the momentum (default 0.2)</p>
 * <p>-N num: Set the number of epochs to train through. (default 500)</p>
 * <p>-V num: Set the percentage size of the validation set from the training to use 
 *            (default 0 (no validation set is used, instead num of epochs is used)</p>
 * <p>-S num: Set the seed for the random number generator. (default 0)</p>
 * <p>-E num: Set the threshold for the number of consequetive errors allowed
 *  during validation testing. (default 20) </p>
 * <p>-G: Bring up a GUI for the neural net.</p>
 * <p>-A: Do not automatically create the connections in the net. (can only be used if -G is specified) </p> 
 * <p>-B: Do Not automatically preprocess the instances with a nominal to binary filter</p>
 * <p>-H str: Set the number of nodes to be used on each layer. Each number represents
 *            its own layer and the num of nodes on that layer. Each number should be comma seperated.
 *            There are also the wildcards 'a', 'i', 'o', 't' (default 4) </p>
 * <p>-C: Do not automatically Normalize the class if it's numeric.</p>
 * <p>-I: Do not automatically Normalize the attributes</p>
 * <p>-R: Do not allow the network to be automatically reset</p> 
 * <p>-D: Cause the learning rate to decay as training is done</p> 
 *
 * @author      Mario Baseda
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsar-weka
 * @see Weka
 * 
 * @cdk.keyword classifiers, funktions, MultilayerPerceptron
 */
public class MultilayerPerceptronModel {
	/** An instance containing the data which should be classifier as arff file.*/
	private static Instances data;
	/**Array of strings containing the options*/
	private String[] options;
	/**new instance of classifier*/
	private MultilayerPerceptron mp = new MultilayerPerceptron();
	private Weka weka = new Weka();
	/**An Array Object containing the independent variable*/
	private Object[][] newX = null;
	/**A String specifying the path of the file, format arff,
	 * which contains the new independent values*/
	private String pathNewX = null;
	/** An array object which contains the results of the prediction*/
	private Object[][] object = null;


	/**
	 * Constructor of the MultilayerPerceptron object
	 */
	public MultilayerPerceptronModel(){}

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
	 * <p>-L num: Set the learning rate. (default 0.3)</p>
	 * <p>-M num: Set the momentum (default 0.2)</p>
	 * <p>-N num: Set the number of epochs to train through. (default 500)</p>
	 * <p>-V num: Set the percentage size of the validation set from the training to use 
	 *            (default 0 (no validation set is used, instead num of epochs is used)</p>
	 * <p>-S num: Set the seed for the random number generator. (default 0)</p>
	 * <p>-E num: Set the threshold for the number of consequetive errors allowed
	 *  		  during validation testing. (default 20) </p>
	 * <p>-G: Bring up a GUI for the neural net.</p>
	 * <p>-A: Do not automatically create the connections in the net. (can only be used if -G is specified) </p> 
	 * <p>-B: Do Not automatically preprocess the instances with a nominal to binary filter</p>
	 * <p>-H str: Set the number of nodes to be used on each layer. Each number represents
	 *            its own layer and the num of nodes on that layer. Each number should be comma seperated.
	 *            There are also the wildcards 'a', 'i', 'o', 't' (default 4) </p>
	 * <p>-C: Do not automatically Normalize the class if it's numeric.</p>
	 * <p>-I: Do not automatically Normalize the attributes</p>
	 * <p>-R: Do not allow the network to be automatically reset</p> 
	 * <p>-D: Cause the learning rate to decay as training is done</p>  
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
				mp.setOptions(options);
//			build the model
			mp.buildClassifier(data);
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
	 * @throws QSARModelException if the parameters are of the wrong type for the given modeling functionn
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


	/** Predict the class of an instance once a classification model has been built with the buildClassifier call
	 *
	 * @throws Exception
	 */
	public void predict() throws Exception{
		try{
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				this.object = new Object[test.numInstances()][];
				for(int i = 0 ; i < test.numInstances(); i++){
					double[] result = mp.distributionForInstance(test.instance(i));
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
					double[] result = mp.distributionForInstance(instance);
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
	 * This function only returns meaningful results if the <code>predict</code>
	 * method of this class has been called.
	 *
	 * @return An Object[][] containing the probabilities of each class type
	 */
	public Object[][] getPredictPredicted() {
		return this.object;
	}

	/**Get the hidden layers
	 * @return A string containig the hidden layers
	 */
	public String getHiddenLayers(){
		return mp.getHiddenLayers();
	}

	/**Get the value of the learning rate
	 * @return A double containig the value of the learning rate
	 */
	public double getLearningRate(){
		return mp.getLearningRate();
	}

	/**Get the value of the momentum
	 * @return A double containig the value of the momentum
	 */
	public double getMomentum(){
		return mp.getMomentum();
	}

	/**Get the value of auto build
	 * @return true or false
	 */
	public boolean getAutoBuild(){
		return mp.getAutoBuild();
	}

	/**Get the value of decay
	 * @return true or false
	 */
	public boolean getDecay(){
		return mp.getDecay();
	}

	/**Get the value of GUI
	 * @return true or false
	 */
	public boolean getGUI(){
		return mp.getGUI();
	}

	/**Get the value of NominalToBinaryFilter
	 * @return true or false
	 */
	public boolean getNominalToBinaryFilter(){
		return mp.getNominalToBinaryFilter();
	}

	/**Get the value of NormalizeAttributes
	 * @return true or false
	 */
	public boolean getNormalizeAttributes(){
		return mp.getNormalizeAttributes();
	}

	/**Get the value of NormalizeNumericClass
	 * @return true or false
	 */
	public boolean getNormalizeNumericClass(){
		return mp.getNormalizeNumericClass();
	}

	/**Gets the current settings of NeuralNet
	 * @return A String[] containig the settings of NeuralNet
	 */
	public String[] getOptions(){
		return mp.getOptions();
	}

	/**Get the value of the random seed
	 * @return A long containig the value of the random seed
	 */
	public long getRandomSeed(){
		return mp.getRandomSeed();
	}

	/**Get the number of epochs to train through
	 * @return An double containig the number of epochs to train through
	 */
	public double getTrainingTime(){
		return mp.getTrainingTime();
	}

	/**Get the percentage size of the validation set
	 * @return An double containig the percentage size of the validation seth
	 */
	public double getValidationSetSize(){
		return mp.getValidationSetSize();
	}

	/**Get the threshold used for validation testing
	 * @return An double containig the threshold used for validation testing
	 */
	public double getValidationThreshold(){
		return mp.getValidationThreshold();
	}

	/**Get the flag for reseting the network
	 * @return true or false
	 */
	public boolean getReset(){
		return mp.getReset();
	}
}
