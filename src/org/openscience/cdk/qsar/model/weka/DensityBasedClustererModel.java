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
import weka.clusterers.DensityBasedClusterer;
import weka.clusterers.EM;

import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

import java.io.BufferedReader;
import java.io.FileReader;

/** 
 * Abstract clustering model that produces an estimate of the membership in each cluster using the Weka library. 
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     DensityBasedClusterer dbc = new EM();
 *     dbc.setData(attrib, typAttrib, classAttrib, data);
 *     dbc.build();
 *     
 * } catch (Exception e) {
 *     System.out.println(e.toString());
 * }
 * </pre>
 *
 * Other option is set the data from a file format arff.
 * <pre>
 * DensityBasedClusterer dbc = new EM();
 * dbc.setData("/some/where/dataTraining.arff");
 * </pre>
 *
 * @author      Mario Baseda
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsar-weka
 * @cdk.svnrev  $Revision: 9162 $
 * @see Weka
 * 
 * @cdk.keyword clusterers, DensityBasedClusterer
 */
public class DensityBasedClustererModel {
	/** An instance containing the data which should be classifier as arff file.*/
	private static Instances data;
	/**new instance of clusterer*/
	private DensityBasedClusterer dbc = new EM();
	private Weka weka = new Weka();
	/**An Array Object containing the independent variable*/
	private Object[][] newX = null;
	/**A String specifying the path of the file, format arff,
	 * which contains the new independent values*/
	private String pathNewX = null;

	/**
	 * Constructor of the NaiveBayesModel object
	 */
	public DensityBasedClustererModel(){}

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
	 * @param attrib	  A string array containing the attributs
	 * @param typAttrib   Attribute type: NUMERICAL or NOMINAL.
	 * @param classAttrib String with a list of the attribut class.
	 * @param y           An array containing the dependent variable (class value).
	 * @param x           A 2D array containing the independent variable (for example: qsar results). 
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
//			build the model
			dbc.buildClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**Returns the prior probability of each cluster
	 * @return A double[] containing the prior probability of each cluster
	 * @throws Exception
	 */
	public double[] clusterPriors() throws Exception{
		return dbc.clusterPriors();
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
	 * @param  newX  A 2D Array Object containing the new values.
	 * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
	 */
	public void setParameters(Object[][] newX) throws QSARModelException {
		this.newX = newX;
	}

	/** Returns the cluster probability distribution for an instance
	 *	
	 * @return object An Object[][] containing the cluster probability distribution for an instance
	 * @throws Exception if distribution could not be computed successfully
	 */
	public Object[][] distributionForInstance() throws Exception{
		Object[][] object = null;
		try{		
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				object = new Object[test.numInstances()][];
				for(int i = 0 ; i < test.numInstances(); i++){
					double[] result = dbc.distributionForInstance(test.instance(i));
					object[i] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						object[i][z] = new Double(result[z]);
					}
				}
			}
			else if(newX != null){
				object = new Object[newX.length][];
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
					double[] result = dbc.distributionForInstance(instance);
					object[j] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						object[j][z] = new Double(result[z]);
					}
				}
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
		return object;
	}

	/** Computes the density for a given instance
	 *
	 * @return object An Object[] containing the density
	 * @throws Exception if the density could not be computed successfully
	 */
	public Object[] logDensityForInstance() throws Exception{
		Object[] object = null;
		try{
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				object = new Object[test.numInstances()];
				for(int i = 0 ; i < test.numInstances(); i++){
					double result = dbc.logDensityForInstance(test.instance(i));
					object[i] = new Double(result);	
				}
			}
			else if(newX != null){
				object = new Object[newX.length];
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
					double result = dbc.logDensityForInstance(instance);


					object[j] = new Double(result);
				}
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
		return object;
	}

	/** Computes the log of the conditional density (per cluster) for a given instance
	 * 
	 * @return object An Object [][] containing the instance to compute the density for
	 * @throws Exception if the density could not be computed successfully
	 */
	public Object[][] logDensityPerClusterForInstance() throws Exception{
		Object[][] object = null;
		try{	
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				object = new Object[test.numInstances()][];
				for(int i = 0 ; i < test.numInstances(); i++){
					double[] result = dbc.logDensityPerClusterForInstance(test.instance(i));
					object[i] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						object[i][z] = new Double(result[z]);
					}
				}
			}
			else if(newX != null){
				object = new Object[newX.length][];
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
					double[] result = dbc.logDensityPerClusterForInstance(instance);
					object[j] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						object[j][z] = new Double(result[z]);
					}
				}
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
		return object;
	}

	/** Returns the logs of the joint densities for a given instance
	 * 
	 * @return object An Object[][] containing the array of values 
	 * @throws Exception if values could not be computed
	 */
	public Object[][] logJointDensitiesForInstance() throws Exception{
		Object[][] object = null;
		try{		
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				object = new Object[test.numInstances()][];
				for(int i = 0 ; i < test.numInstances(); i++){
					double[] result = dbc.logJointDensitiesForInstance(test.instance(i));
					object[i] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						object[i][z] = new Double(result[z]);
					}
				}
			}
			else if(newX != null){
				object = new Object[newX.length][];
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
					double[] result = dbc.logJointDensitiesForInstance(instance);
					object[j] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						object[j][z] = new Double(result[z]);
					}
				}
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
		return object;
	}
}
