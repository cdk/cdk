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
import weka.filters.unsupervised.instance.Randomize;

import org.openscience.cdk.libio.weka.Weka;
import org.openscience.cdk.qsar.model.QSARModelException;

/** 
 * This filter randomly shuffles the order of instances passed through it. 
 * The random number generator is reset with the seed value whenever setInputFormat() is called. 
 * 
 * The use of this class is shown in the following code snippet
 * <pre>
 * try {
 *     FilterUnSupervisedRandomize filter = new FilterUnSupervisedRandomize();
 *     filter.setOptions(options);
 *     filter.setData(attrib, typAttrib, classAttrib, data);
 *     filter.build();
 *     
 * } catch (Exception e) {
 *     System.out.println(e.toString());
 * }
 * </pre>
 * Other option is set the data from a file format arff.
 * <pre>
 * FilterUnSupervisedRandomize filter = new FilterUnSupervisedRandomize();
 * filter.setData("/some/where/dataTraining.arff");
 * </pre>
 * <p>Valid options are (according weka library):</p>
 * <p>-S num: Specify the random number seed (default 42).</p>
 *
 * @author      Mario Baseda
 * @cdk.require weka.jar
 * @cdk.license GPL
 * @cdk.module  qsarweka
 * @cdk.svnrev  $Revision: 9162 $
 * @see Weka
 * 
 * @cdk.keyword Filter
 * @cdk.keyword UnSupervisedFilter, Randomize
 */
public class FilterUnSupervisedRandomize {
	/** An instance containing the data as arff file.*/
	private static Instances data;
	/**Array of strings containing the options*/
	private String[] options;
	private Randomize filter = new Randomize();
	Weka weka = new Weka();

	/**
	 * Constructor of the FilterUnSupervisedRandomize object
	 */
	public FilterUnSupervisedRandomize(){}

	/**
	 * Parses a given list of options. The parameters are determited from weka. 
	 * 
	 * <p>Valid options are (according weka library):</p>
	 * <p>-S num: Specify the random number seed (default 42).</p>
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
	 * The execute method for the unsupervised randomize filter
	 *
	 * @throws Exception if errors occur in data types. See
	 * the corresponding method in subclasses of this class for further details.
	 */
	public void build() throws Exception {
		try {
			if(options != null)
//				set the options
				filter.setOptions(options);
//			setInputFormat
			filter.setInputFormat(data);
			for (int i = 0; i < data.numInstances(); i++) {
				filter.input(data.instance(i));
			}
//			Signify that this batch of input to the filter is finished
			filter.batchFinished();
			Instances newData = filter.getOutputFormat();
			Instance processed;
			while ((processed = filter.output()) != null) {
				newData.add(processed);
			}
		} catch (Exception exception){
			exception.printStackTrace();
		}
	}

	/**
	 * Returns the current settings of the filter. 
	 *
	 * @return an array of strings containing the options
	 */
	public String[] getOptions(){
		return filter.getOptions();
	}

	/**
	 * Returns the random number seed. 
	 *
	 * @return the random number seed as integer
	 */
	public int getRandomSeed(){
		return filter.getRandomSeed();
	}
}
