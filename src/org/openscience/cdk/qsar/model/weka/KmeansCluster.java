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

import java.io.BufferedReader;
import java.io.FileReader;

import weka.core.Instance;
import weka.core.Instances;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

/** 
 * A clustering class that provides the k means Cluster
 * using Weka library.
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     KmeansCluster kMeans = new KmeansCluster();
 *     kMeans.setOptions(options);
 *     kMeans.setData(attrib, typAttrib, classAttrib, data);
 *     kMeans.build();
 *     
 * } catch (Exception e) {
 *     System.out.println(e.toString());
 * }
 * </pre>
 * Other option is set the data from a file format arff.
 * <pre>
 * KmeansCluster kMeans = new KmeansCluster();
 * kMeans.setData("/some/where/dataTraining.arff");
 * </pre>
 * <p>Valid options are (according weka library):</p>
 * <p>-N: Specify the number of clusters to generate.</p>
 * <p>-S: Specify random number seed.</p>
 *
 * @author      Mario Baseda
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsarweka
 * @cdk.svnrev  $Revision: 9162 $
 * @see Weka
 * 
 * @cdk.keyword clusterers
 * @cdk.keyword SimpleKMeans
 */
public class KmeansCluster{ // implements IWekaModel{
	/** An instance containing the data which should be clustering as arff file.*/
	private static Instances data;
	/**Array of strings containing the options*/
	private String[] options;
	/**new instance of clusterer*/
	private SimpleKMeans kMeans = new SimpleKMeans();
	Weka weka = new Weka();
	/**An Array Object containing the independent variable*/
	private Object[][] newX = null;
	/**A String specifying the path of the file, format arff,
	 * which contains the new independent values*/
	private String pathNewX = null;
	/** results of the classifying*/
	private Object[] results = null;

	/**
	 * Constructor of the KmeansCluster object
	 */
	public KmeansCluster(){}

	/**
	 * Parses a given list of options. The parameters are determited from weka. 
	 * 
	 * <p>Valid options are (according weka library):</p>
	 * <p>-N: Specify the number of clusters to generate.</p>
	 * <p>-S: Specify random number seed.</p>
	 *
	 * @param options    An Array of strings containing the options 
	 * @throws Exception if the options are of the wrong type for the given modeling function
	 * 
	 */
	public void setOptions(String[] options) throws Exception{
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
	 * @param attrib  A string array containing the attributs
	 * @param typAttrib   Attribute type: NUMERICAL or NOMINAL.
	 * @param classAttrib String with a list of the attribut classes.
	 * @param y           An array containing the dependent variable (class value).
	 * @param x           A 2D array containing the independent variable (for example: qsar results). 
	 * @throws Exception  if it is unable to parse the data
	 * 
	 */
	public void setData(String[] attrib, int[] typAttrib, String[] classAttrib, Object[] y, Object[][] x) throws Exception{
		data = weka.setDataset(attrib,typAttrib,classAttrib,y,x,null);	
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

	/**
	 * Builds the cluster.
	 *
	 * @throws Exception if errors occur in data types. See
	 * the corresponding method in subclasses of this class for further details.
	 */
	public void build() throws Exception {
		try {
			if(options != null)
//				set the options
				kMeans.setOptions(options);
//			build the clusterer
			kMeans.buildClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/** Classifies a given instance.
	 *  This function only returns meaningful results if the <code>build</code>
	 *  method of this class has been called.
	 *	
	 * @throws Exception if instance could not be classified successfully
	 */
	public void clusterInstance() throws Exception{
		try{		
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				results = new Object[test.numInstances()];
				for(int i = 0 ; i < test.numInstances(); i++){
					results[i] = new Double(kMeans.clusterInstance(test.instance(i)));
				}
			}
			else if(newX != null){
				results = new Object[newX.length];
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
					results[j] = new Double(kMeans.clusterInstance(instance));
				}
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
	}

	/**
	 *  Returns the number of the assigned cluster if the class is enumerated, otherwise the predicted value. 
	 *  This function only returns meaningful results if the <code>clusterInstance</code>
	 *  method of this class has been called.
	 * @return An Object[] containing the assigned cluster as Double values 
	 *  if the class is enumerated, otherwise the predicted values. 
	 */
	public Object[] getClusterInstance(){
		return results;
	}

	/**
	 * Returns the number of clusters. 
	 *
	 * @return the number of builded cluster as integer
	 * @throws Exception 
	 */
	public int numberOfCluster() throws Exception{
		return kMeans.numberOfClusters();
	}

	/**
	 * Returns the cluster sum of squared errors. 
	 *
	 * @return the sum of squared errors of the cluster as double
	 */
	public double getSquaredError(){
		return kMeans.getSquaredError();
	}

	/**
	 * Returns the cluster centroid values. 
	 *
	 * @return the cluster centroid values as 2D double array
	 * @throws Exception 
	 */
	public double[][] getClusterCentroids() throws Exception{
		double[][] results = new double[kMeans.numberOfClusters()][];
		for (int x = 0; x < kMeans.numberOfClusters(); x++){
			results[x] = new double[kMeans.getClusterCentroids().instance(x).numValues()];
			results[x] = kMeans.getClusterCentroids().instance(x).toDoubleArray();
		}
		return results;
	}

	/**
	 * Returns the cluster standard deviations. 
	 *
	 * @return the cluster standard deviations as 2D double array
	 * @throws Exception 
	 */
	public double[][] getClusterStandardDevs() throws Exception{
		double[][] results = new double[kMeans.numberOfClusters()][];
		for (int x = 0; x < kMeans.numberOfClusters(); x++){
			results[x] = new double[kMeans.getClusterStandardDevs().instance(x).numValues()];
			results[x] = kMeans.getClusterStandardDevs().instance(x).toDoubleArray();
		}
		return results;
	}

	/**
	 * Returns the cluster sizes. 
	 *
	 * @return the cluster sizes as integer array
	 */
	public int[] getClusterSizes() {
		return kMeans.getClusterSizes();
	}

	/**
	 * Returns the cluster. 
	 *
	 * @return the builded Clusterer
	 */
	public Clusterer getClusterer(){
		return kMeans;
	}
	
	/**
	 * Gets the current settings of the model 
	 *
	 * @return an array of strings containing the options

	 */
	public String[] getOptions() {
		return kMeans.getOptions();
	}
}
