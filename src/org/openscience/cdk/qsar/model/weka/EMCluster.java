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
import weka.clusterers.EM;

import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

/** 
 * EM assigns a probability distribution to each instance which indicates the probability of it 
 * belonging to each of the clusters by using Weka library. 
 * EM can decide how many clusters to create by cross validation, or you may specify apriori 
 * how many clusters to generate.
 * 
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     EM em = new EM();
 *     em.setOptions(options);
 *     em.setData(attrib, typAttrib, classAttrib, data);
 *     em.build();
 *     
 * } catch (Exception e) {
 *     System.out.println(e.toString());
 * }
 * </pre>
 * Other option is set the data from a file format arff.
 * <pre>
 * EM em = new EM();
 * em.setData("/some/where/dataTraining.arff");
 * </pre>
 * <p>Valid options are (according weka library):</p>
 * <p>-V: Verbose</p>
 * <p>-N: Specify the number of clusters to generate. If omitted, EM will use cross 
 *        validation to select the number of clusters automatically</p>
 * <p>-I: Terminate after this many iterations if EM has not converged.</p>
 * <p>-S: Specify random number seed</p>
 * <p>-M: Set the minimum allowable standard deviation for normal density calculation.</p>
 *
 * @author      Mario Baseda
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsarweka
 * @cdk.svnrev  $Revision: 9162 $
 * @see Weka
 * 
 * @cdk.keyword clusterers
 * @cdk.keyword EM
 */
public class EMCluster {
	/** An instance containing the data which should be clustering as arff file.*/
	private static Instances data;
	/**Array of strings containing the options*/
	private String[] options;
	/**new instance of clusterer*/
	private EM em = new EM();
	Weka weka = new Weka();
	/**An Array Object containing the independent variable*/
	private Object[][] newX = null;
	/**A String specifying the path of the file, format arff,
	 * which contains the new independent values*/
	private String pathNewX = null;
	/** results of the density*/
	private Object[][] results = null;

	/**
	 * Constructor of the EMCluster object
	 */
	public EMCluster(){}

	/**
	 * Parses a given list of options. The parameters are determited from weka. 
	 * 
	 * <p>Valid options are (according weka library):</p>
	 * <p>-V: Verbose</p>
	 * <p>-N: Specify the number of clusters to generate. If omitted, EM will use cross 
	 *        validation to select the number of clusters automatically</p>
	 * <p>-I: Terminate after this many iterations if EM has not converged.</p>
	 * <p>-S: Specify random number seed</p>
	 * <p>-M: Set the minimum allowable standard deviation for normal density calculation.</p>
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
	 * @param attrib	  A string array containing the attributs
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
	 * Builds the cluster.
	 *
	 * @throws Exception if errors occur in data types. See
	 * the corresponding method in subclasses of this class for further details.
	 */
	public void build() throws Exception {
		try {
			if(options != null)
//				set the options
				em.setOptions(options);
//			build the clusterer
			em.buildClusterer(data);
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
	 * @param  newX  A 2D Array Object containing the new values.
	 * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
	 */
	public void setParameters(Object[][] newX) throws QSARModelException {
		this.newX = newX;
	}

	/** Computes the log of the conditional density (per cluster) for a given instance
	 *  This function only returns meaningful results if the <code>build</code>
	 *  method of this class has been called.
	 *	
	 * @throws Exception if the density could not be computed successfully
	 */
	public void logDensityPerClusterForInstance() throws Exception{
		try{		
			if(pathNewX != null){
				BufferedReader br = new BufferedReader(new FileReader(pathNewX));
				Instances test = new Instances(br);
				results = new Object[test.numInstances()][];
				for(int i = 0 ; i < test.numInstances(); i++){
					double[] result = em.distributionForInstance(test.instance(i));
					results[i] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						results[i][z] = new Double(result[z]);
					}
				}
			}
			else if(newX != null){
				results = new Object[newX.length][];
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
					double[] result = em.distributionForInstance(instance);
					results[j] = new Object[result.length];
					for (int z = 0; z < result.length; z++){
						results[j][z] = new Double(result[z]);
					}
				}
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
	}

	/**
	 *  Returns the log of the conditional density (per cluster) for a given instance. 
	 *  This function only returns meaningful results if the <code>logDensityPerClusterForInstance</code>
	 *  method of this class has been called.
	 * @return An Object[][] containing the density
	 */
	public Object[][] getLogDensityPerClusterForInstance(){
		return results;
	}

	/**
	 * Returns the number of clusters. 
	 *
	 * @return the number of builded cluster as integer
	 * @throws Exception if number of clusters could not be returned successfully
	 */
	public int numberOfCluster() throws Exception{
		return em.numberOfClusters();
	}

	/**
	 * Returns the cluster priors. 
	 *
	 * @return the prior probability for each cluster as double array
	 */
	public double[] clusterPriors() {
		return em.getClusterPriors();
	}

	/**
	 * Return the normal distributions for the cluster models  
	 *
	 * @return the normal distributions for the cluster models as double 3D array
	 */
	public double[][][] getClusterModelsNumericAtts(){
		return em.getClusterModelsNumericAtts();
	}

	/**
	 * Return the priors for the clusters 
	 *
	 * @return the prior for the clusters as double array
	 */
	public double[] getClusterPriors() {
		return em.getClusterPriors();
	}

	/**
	 * Get debug mode 
	 *
	 * @return true or false
	 */
	public boolean getDebug() {
		return em.getDebug();
	}

	/**
	 * Get the maximum number of iterations 
	 *
	 * @return the number of iterations as integer
	 */
	public int getMaxIterations() {
		return em.getMaxIterations();
	}

	/**
	 * Get the minimum allowable standard deviation. 
	 *
	 * @return the minumum allowable standard deviation as double
	 */
	public double getMinStdDev() {
		return em.getMinStdDev();
	}

	/**
	 * Get the number of clusters 
	 *
	 * @return the number of clusters as integer
	 */
	public int getNumClusters() {
		return em.getNumClusters();
	}

	/**
	 * Gets the current settings of EM 
	 *
	 * @return an array of strings containing the options

	 */
	public String[] getOptions() {
		return em.getOptions();
	}

	/**
	 * Get the random number seed  
	 *
	 * @return the seed as integer

	 */
	public int getSeed() {
		return em.getSeed();
	}
}
