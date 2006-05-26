/* $RCSfile$
 * $Author: egonw $ 
 * $Date: 2006-05-01 10:43:42 +0200 (Mo, 01 Mai 2006) $
 * $Revision: 6095 $
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.libio.weka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;


/**
 * <p>Weka class is a library which use the program WEKA: a Machine Learning Project.</p>
 * To inizalizate weka class is typically done like: <pre>
 *  Classifier lr = new LinearRegression(); 
 *  weka.setDataset("/some/where/dataTraining.arff", lr);
 *  String testARFF = "/some/where/dataTest.arff";
 *  double[] result = weka.getPrediction(testARFF);
 *  </pre>
 *  You have also the possibility to introduce directly values, done like:
 *  <pre>
 *  Classifier lr = new LinearRegression();
 *   String[] attrib = {"aX","bX","cX","PY" };
 *   int[] typAttrib = {Weka.NUMERIC,Weka.NUMERIC,Weka.NUMERIC,Weka.NUMERIC, };
 *   double[][] x = {{0.39,9.62 ,-0.15 },
 *                   {1.64,9.77 ,-0.13},
 *                   {1.06,12.56,-0.16},
 *   double[] y = {12.74,11.3 ,13.0};
 *  weka.setDataset(attrib, typAttrib, y, x, lr);
 *  double[] testX = {0.39,9.06,-0.11};
 *  double resultY = weka.getPrediction(testX);
 *  </pre>
 * @author      Miguel Rojas
 * @cdk.created 2006-05-23
 * @cdk.module  libio-weka
 * @cdk.keyword   weka,Machine Learning
 * @cdk.depends   weka.jar
 */
public class Weka {
	
	public static final int NUMERIC = 0;
	public static final int NOMINAL = 1;

	/** type of classifier*/
	private Classifier classifier;
	
	private Instances labeled;
	/**
     * Constructor of the Weka
     */
    public Weka() {
    }
    /**
     * Set the file format arff to analize which contains the dataset and the type of classifier
     *  
     * @param setDataset  Path of the dataset file format arff to train
     * @param classifier  Type of Classifier
     * @return            The Instances value
     * @throws Exception 
     */
    public Instances setDataset(String pathTable, Classifier classifier) throws Exception{
    	this.classifier = classifier;
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(pathTable);
    	Reader insr = new InputStreamReader(ins);
        Instances instances = new Instances(new BufferedReader(insr));
		instances.setClassIndex(instances.numAttributes() - 1);
		
		labeled = new Instances(instances);
		classifier.buildClassifier(labeled);

		for (int i = 0; i < instances.numInstances(); i++) {
		  double clsLabel = classifier.classifyInstance(instances.instance(i));
		  labeled.instance(i).setClassValue(clsLabel);
		}
		return labeled;
    }
    /**
     * 
     * Set the array which contains the dataset and the type of classifier. This method
     * will be used for classifier which work with numerical values.
     *  
     * @param attrib      String with the attribut names
     * @param typAttrib   Attribute type: NUMERICAL or NOMINAL. 
     * @param x           An array of independent variables. The observations should be in the rows
     *                    and the variables should be in the columns
     * @param y           An array containing the dependent variable. It is possible numeric or string.
     * @param classifier  Type of Classifier
     * @return            The Instances value
     * @throws Exception
     */
    public Instances setDataset(String[] attrib, int[] typAttrib, Object[]y, double[][] x, Classifier classifier) throws Exception{
    	return setDataset(attrib, typAttrib ,null,y,x,classifier);
    }
    /**
     * 
     * Set the array which contains the dataset and the type of classifier.This method
     * will be used for classifier which work with String values.
     *  
     * @param attrib      String with the attribut names.
     * @param typAttrib   Attribute type: NUMERICAL or NOMINAL.
     * @param classAttrib String with the attribut class.
     * @param x           An array of independent variables. The observations should be in the rows
     *                    and the variables should be in the columns
     * @param y           An array containing the dependent variable. It is possible numeric or string.
     * @param classifier  Type of classifier
     * @return            The Instances value
     * @throws Exception
     */
    public Instances setDataset(String[] attrib, int[] typAttrib, String[] classAttrib, Object[]y, double[][] x, Classifier classifier) throws Exception{
    	this.classifier = classifier;
    	Reader reader = createAttributes(attrib,typAttrib,classAttrib,y,x);
    	Instances instances = new Instances(reader);
    	instances.setClassIndex(instances.numAttributes() - 1);
		labeled = new Instances(instances);
		classifier.buildClassifier(labeled);

		for (int i = 0; i < instances.numInstances(); i++) {
		  double clsLabel = classifier.classifyInstance(instances.instance(i));
		  labeled.instance(i).setClassValue(clsLabel);
		}
    		return labeled;
    }
    /**
     * Return of the predicted value
     *  
     * @param value   An array of independent variables which contians the values with whose to test
     * @return	      Result of the prediction
     * @throws Exception 
     */
    public double getPrediction(double[] value) throws Exception{
    	Instance instance = new Instance(labeled.numAttributes());
    	instance.setDataset(labeled);
    	for(int i = 0 ; i < value.length ; i++)
        	instance.setValue(i, value[i]);
//    	instance.setValue(value.length, 0.0);
		return classifier.classifyInstance(instance);
    }
    /**
     * Return of the predicted value
     *  
     * @param pathARRF  path of the file format arff which contians the values with whose to test.
     * @return	        Result of the prediction.
     * @throws Exception 
     */
    public double[] getPrediction(String pathARFF) throws Exception{
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(pathARFF);
    	Reader insr = new InputStreamReader(ins);
        Instances test = new Instances(new BufferedReader(insr));
    	double[] result = new double[test.numInstances()];
    	for(int i = 0 ; i < test.numInstances(); i++){
    		result[i] = classifier.classifyInstance(test.instance(i));
    	}
		return result;
    }
    /**
     * create a Reader with necessary attributes to iniziate a Instances for weka.
     * 
     * @param attrib      String with the attribut class
     * @param typAttrib   Attribute type: NOMINAL or NUMERIC.
     * @param y           An array containing the independent variable. 
     * @param x           An array of dependent variables.
     * @return            The Reader containing the attributes
     * @throws IOException
     */
    private Reader createAttributes(String[] attrib, int[] typAttrib, String[] classAttrib, Object[]y, double[][] x) throws IOException{
    	String string ="@relation table1 \n";
    	for(int i = 0; i < attrib.length ; i++){
    		string += ("@attribute "+attrib[i]);
    		if(typAttrib[i] == NUMERIC)
    			string += " numeric \n";
    		else if(typAttrib[i] == NOMINAL)
    			string += " string \n";
    	}
    	
    	if(classAttrib != null){
	    	string += "@attribute class ";
	    	string += "{";
	    	for(int i = 0; i < classAttrib.length ; i++){
	    		string += (classAttrib[i]);
	    		if(i != classAttrib.length -1)
	    			string += ",";
	    	}
	    	string += "}\n";
	    	}

    	string += ("@data ");
    	if(x != null && y != null){
    		for(int j = 0 ; j < x.length; j++){
    	    	for(int i = 0 ; i < x[0].length ; i++){
    	    		string += x[j][i]+",";
    	    	}
   	    		string += y[j]+", \n";
    		}
    	}
    	
    	
    	Reader reader = new StringReader(string);
    	return reader;
    }
}