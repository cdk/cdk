/* $Revision: 6228 $ $Author: egonw $ $Date: 2006-05-11 18:34:42 +0200 (Thu, 11 May 2006) $
 * 
 * Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.qsar.model.weka;

import org.openscience.cdk.qsar.model.IModel;
import org.openscience.cdk.qsar.model.QSARModelException;

/** Base class for modeling classes that use weka methods as the backend.
 *
 * This cannot be directly instantiated as its sole function is
 * to initialize the weka algorithms.
 * Any class that builds models using weka algorithms should be a subclass of this.
 *
 * @author     Miguel Rojas
 * @cdk.module qsar
 * @cdk.svnrev  $Revision: 9162 $
 */
public interface IWekaModel extends IModel {

	/**
     * Parses a given list of options. The parameters are determited from weka. And are specific for each
     * algorithm.
     *
     * @param options An Array of strings containing the options 
     * @throws QSARModelException if the options are of the wrong type for the given modeling function
     * 
     */
    abstract public void setOptions(String[] options) throws QSARModelException;
    
    /**
     * Get the current settings of the classifier. The parameters are determited from weka. And are specific for each
     * algorithm.
     *
     * @return An Array of strings containing the options 
     * @throws QSARModelException if the options are of the wrong type for the given modeling function
     * 
     */
    abstract public String[] getOptions() throws QSARModelException;
    
    
    /**
     * Specifies the parameters to predict. In this case will be the dependent varibles.
     * It's found into cdk.src
     * 
     * @param  path  A String specifying the path of the file, format arff, which contians 
     * 				 the dependent values with whose to predict. It's found into cdk.src
     * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
     * 
     */
    abstract public void setParametersCDK(String path) throws QSARModelException;
    

    /**
     * Specifies the parameters to predict. In this case will be the independent varibles.
     * 
     * @param  x  A Array Object containing the independent variable.
     * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
     */
    abstract public void setParameters(Object[][] x) throws QSARModelException;


    /**
     * Returns the predicted values for the prediction set. 
     *
     * This function only returns meaningful results if the <code>predict</code>
     * method of this class has been called.
     *
     * @return A Object[] containing the predicted values
     */
    abstract public Object[] getPredictPredicted();

    
}


