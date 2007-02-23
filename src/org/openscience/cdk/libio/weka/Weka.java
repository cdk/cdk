/* $Revision: 6228 $ $Author: egonw $ $Date: 2006-05-11 18:34:42 +0200 (Thu, 11 May 2006) $
 *
 * Copyright (C) 2006-2007  Miguel Rojas-Cherto <miguelrojasch@users.sf.net>
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
package org.openscience.cdk.libio.weka;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.io.*;
import java.util.Vector;


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
 *   String[] attrib = {"X1","X2","X3","Y" };
 *   int[] typAttrib = {Weka.NUMERIC,Weka.NUMERIC,Weka.NUMERIC,Weka.NUMERIC, };
 *  weka.setDataset(attrib, typAttrib, y, x, lr);
 *  double[] resultY = weka.getPrediction(testX);
 *  </pre>
 *  
 * @author      Miguel Rojas
 * @cdk.created 2006-05-23
 * @cdk.license GPL
 * @cdk.module  qsar-weka
 * @cdk.keyword weka, machine learning
 * @cdk.depends weka.jar
 */
public class Weka {

	public static final int NUMERIC = 0;
	public static final int NOMINAL = 1;
	public static final int REGULAR = 2;
	public static final int DATA = 3;
	public static final int STRING = 4;

	/** type of classifier*/
	private Classifier classifier = null;
	/** Class for handling an ordered set of weighted instances*/
	private Instances instances;
	/**String with the attribut class*/
	private String[] classAttrib = null;
	/**
	 * Constructor of the Weka
	 */
	public Weka() {
	}
	/**
	 * Set the file format arff to analize which contains the dataset and the type of classifier.
	 * The file is found into the src.
	 *  
	 * @param pathTable   Path of the dataset file format arff to train
	 * @param classifier  Type of Classifier
	 * @return            The Instances value
	 * @throws Exception 
	 */
	public Instances setDatasetCDK(String pathTable, Object object) throws Exception{
		if (object instanceof Classifier) this.classifier = (Classifier) object;
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(pathTable);
		BufferedReader insr = new BufferedReader(new InputStreamReader(ins));
		this.classAttrib = extractClass(insr);

		ins = this.getClass().getClassLoader().getResourceAsStream(pathTable);
		insr = new BufferedReader(new InputStreamReader(ins));
		return createInstance(insr);

	}
	/**
	 * Set the file format arff to analize which contains the dataset and the type of classifier.
	 * 
	 *  
	 * @param pathTable   Path of the dataset file format arff to train
	 * @param classifier  Type of Classifier
	 * @return            The Instances value
	 * @throws Exception 
	 */
	public Instances setDataset(String pathTable, Object object) throws Exception{
		if (object instanceof Classifier) this.classifier = (Classifier) object;
		BufferedReader insr = new BufferedReader(new FileReader(pathTable));
		this.classAttrib = extractClass(insr);

		insr = new BufferedReader(new FileReader(pathTable));
		return createInstance(insr);

	}
	private Instances createInstance(BufferedReader insr) throws Exception{
		instances = new Instances(insr);
		instances.setClassIndex(instances.numAttributes() - 1);
		if (classifier != null) classifier.buildClassifier(instances);
		return instances;
	}
	/** 
	 * Extract the class name attribute manually from the file
	 * 
	 * @param input  The BufferedReader
	 * @return      Array with the class attributes
	 */
	private String[] extractClass(BufferedReader input) {
		Vector attribV = new Vector();
		String[] classAttrib = null;
		String line;
		try {
			while ((line = input.readLine()) != null) {
				if(line.startsWith("@attribute class {")){
					int strlen = line.length();
					String line_ = null;
					out:
						for (int i = 0; i < strlen; i++){
							switch(line.charAt(i)){
							case '{':
								line_ = line.substring(i);
								break out;
							}
						}
					StringBuffer edited = new StringBuffer();
					strlen = line_.length();
					edited = new StringBuffer();
					for (int i = 0; i < strlen; i++){
						switch(line_.charAt(i)){
						case '"':
							break;
						case ',':
							attribV.add(edited.toString());
							edited = new StringBuffer();
							break;
						case '{':
							break;
						case '}':
							attribV.add(edited.toString());
							break;
						default:
							edited.append(line_.charAt(i));
						}
					}

				}
			}
			if(attribV.size() > 0){
				classAttrib = new String[attribV.size()];
				attribV.copyInto(classAttrib);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classAttrib;
	}
	/**
	 * 
	 * Set the array which contains the dataset and the type of classifier. This method
	 * will be used for classifier which work with numerical values.
	 *  
	 * @param attrib      String with the attribut names
	 * @param typAttrib   Attribute type: NUMERICAL or NOMINAL. 
	 * @param y           An array containing the dependent variable. It is possible numeric or string.
	 * @param x           An array of independent variables. The observations should be in the rows
	 *                    and the variables should be in the columns
	 * @param classifier  Type of Classifier
	 * @return            The Instances value
	 * @throws Exception
	 */
	public Instances setDataset(String[] attrib, int[] typAttrib, Object[]y, Object[][] x, Classifier classifier) throws Exception{
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
	 * @param y           An array containing the dependent variable. It is possible numeric or string.
	 * @param x           An array of independent variables. The observations should be in the rows
	 *                    and the variables should be in the columns
	 * @param classifier  Type of classifier
	 * @return            The Instances value
	 * @throws Exception
	 */
	public Instances setDataset(String[] attrib, int[] typAttrib, String[] classAttrib, Object[]y, Object[][] x, Object object) throws Exception{
		if (object instanceof Classifier) this.classifier = (Classifier) object;
		this.classAttrib = classAttrib;
		Reader reader = createAttributes(attrib,typAttrib,classAttrib,y,x);
		instances = new Instances(reader);
		instances.setClassIndex(instances.numAttributes() - 1);
		if (object instanceof Classifier) classifier.buildClassifier(instances);
		return instances;
	}
	/**
	 * Return of the predicted value
	 *  
	 * @param value   An array of independent variables which contians the values with whose to test
	 * @return	      Result of the prediction
	 * @throws Exception 
	 */
	public Object[] getPrediction(Object[][] value) throws Exception{
		Object[] object = new Object[value.length];
		for(int j = 0 ; j < value.length ; j++){
			Instance instance = new Instance(instances.numAttributes());
			instance.setDataset(instances);
			for(int i = 0 ; i < value[0].length ; i++){
				if(instance.attribute(i).isNumeric())
					instance.setValue(i, ((Double)value[j][i]).doubleValue());
				else if(instance.attribute(i).isString())
					instance.setValue(i, ""+value[j][i]);
			}
			instance.setValue(value[0].length, 0.0);
			double result = classifier.classifyInstance(instance);
			if(classAttrib != null){
				object[j] = classAttrib[(new Double(result)).intValue()];
			}
			else
				object[j] = new Double(result);
		}
		return object;
	}
	/**
	 * Return of the predicted value. The file is found into src.
	 *  
	 * @param pathARFF  path of the file format arff which contians the values with whose to test.
	 * @return	        Result of the prediction.
	 * @throws Exception 
	 */
	public Object[] getPredictionCDK(String pathARFF) throws Exception{
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(pathARFF);
		Reader insr = new InputStreamReader(ins);
		return createObjects(new BufferedReader(insr));
	}

	/**
	 * Return of the predicted value.
	 *  
	 * @param pathARFF  path of the file format arff which contians the values with whose to test.
	 * @return	        Result of the prediction.
	 * @throws Exception 
	 */
	public Object[] getPrediction(String pathARFF) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(pathARFF));

		return createObjects(br);
	}
	/**
	 * iniziate the object.
	 * 
	 * @param br The BufferedReader
	 * @return An Array of objects: classAttrib and Double
	 * @throws Exception 
	 */
	private Object[] createObjects(BufferedReader br) throws Exception{
		Instances test = new Instances(br);
		Object[] object = new Object[test.numInstances()];
		for(int i = 0 ; i < test.numInstances(); i++){
			double result = classifier.classifyInstance(test.instance(i));
			if(classAttrib != null)
				object[i] = classAttrib[(new Double(result)).intValue()];
			else
				object[i] = new Double(result);
		}
		return object;
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
	private Reader createAttributes(String[] attrib, int[] typAttrib, String[] classAttrib, Object[]y, Object[][] x) throws IOException{
		String string ="@relation table1 \n";
		for(int i = 0; i < attrib.length ; i++){
			string += ("@attribute "+attrib[i]);
			if(typAttrib[i] == NUMERIC)
				string += " numeric \n";
			else if(typAttrib[i] == NOMINAL)
				string += " string \n";
			else if(typAttrib[i] == DATA)
				string += " data \n";
			else if(typAttrib[i] == REGULAR)
				string += " regular \n";
			else if(typAttrib[i] == STRING)
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
					if (x[j][i] instanceof Double) {
						if (((Double)x[j][i]).isNaN()) {
							string += "?,";
							continue;
						}
					}
					string += x[j][i]+",";
				}
				string += y[j]+ ", \n";
			}
		}
		if(x != null && y == null){
			for(int j = 0 ; j < x.length; j++){
				for(int i = 0 ; i < x[0].length ; i++){
					if (x[j][i] instanceof Double) {
						if (((Double)x[j][i]).isNaN()) {
							string += "?,";
							continue;
						}
					}
					string += x[j][i]+",";
				}
				string += "\n";
			}
		}
		return (Reader)new StringReader(string);			
	}

	/**
	 * get the value which belongs this position in the classification
	 * @param result Position in the classification
	 * @return       Real value
	 */
	/*private double[] getValue(double[] result) {
		Instance instance = instances.instance(0);
		instance.numClasses();
		return null;
	}*/
}
