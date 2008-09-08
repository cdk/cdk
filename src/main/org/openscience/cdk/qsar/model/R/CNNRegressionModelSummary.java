/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.qsar.model.R;


/** A class that represents a summary of a CNN regression model.
 *
 * This class essentially wraps the result of summary.nnet. As with other 
 * backend classes this class should not be instantiated directly by the 
 * user, though the various fields may be accessed with the provided 
 * methods.
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 * @deprecated 
 */
public class CNNRegressionModelSummary {

    double[] residuals;
    boolean entropy, softmax, censored;
    double value;
    int[] n;

    /**
     * Constructor for an object that wraps the return value from summary.lm.
     *
     * This should not be instantiated directly. The class is meant to be instantiated
     * from an R session
     *
     * @param n A 3 element array containing the number of neurons in the 
     * input, hidden and output layer respectively
     * @param entropy  A boolean indicating whether the entropy setting was used
     * @param softmax A boolean indicating whether the softmax setting was used
     * @param censored A boolean indicating whether the censored setting was used
     * @param value The final value of the convergenc criterion
     * @param residuals A 1-dimensional array of residual values
     */
    public CNNRegressionModelSummary( int[] n, boolean entropy, 
            boolean softmax, boolean censored, double value,
            double[] residuals) {


        this.residuals = new double[residuals.length];
        for (int i = 0; i < residuals.length; i++)
            this.residuals[i] = residuals[i];

        this.n = new int[n.length];
        for (int i = 0; i < n.length; i++) 
            this.n[i] = n[i];

        this.softmax = softmax;
        this.censored = censored;
        this.entropy = entropy;
        this.value = value;
    }
    /**
     * Constructor for an object that wraps the return value from summary.lm.
     *
     * This should not be instantiated directly. The class is meant to be instantiated
     * from an R session
     *
     * @param n A 3 element array containing the number of neurons in the 
     * input, hidden and output layer respectively
     * @param entropy  A boolean indicating whether the entropy setting was used
     * @param softmax A boolean indicating whether the softmax setting was used
     * @param censored A boolean indicating whether the censored setting was used
     * @param value The final value of the convergenc criterion
     * @param residuals A 1-dimensional array of residual values
     */
    public CNNRegressionModelSummary( double[] n, boolean entropy, 
            boolean softmax, boolean censored, double value,
            double[] residuals) {


        this.residuals = new double[residuals.length];
        for (int i = 0; i < residuals.length; i++)
            this.residuals[i] = residuals[i];

        this.n = new int[n.length];
        for (int i = 0; i < n.length; i++) 
            this.n[i] = (int)n[i];

        this.softmax = softmax;
        this.censored = censored;
        this.entropy = entropy;
        this.value = value;
    }


    /**
     * Return the residuals of the fit.
     *
     * @return A 1-dimensional array of doubles containing the 
     * residuals of the fit
     */
    public double[] getResiduals() {
        return(this.residuals);
    }


    /**
     * Return the number of neurons in the CNN layers.
     *
     * This method returns a 3-element array containing the number
     * of neurons in the input, hidden and output layer
     * respectively.
     *
     * @return A 3-element int array
     */
    public int[] getNumNeurons() {
        return(this.n);
    }

    /**
     * Return the final value of the convergence criterion.
     *
     * @return The final value of the convergence criterion
     */
    public double getValue(){
        return(this.value);
    }

    /**
     * Return whether softmax was used.
     *
     * @return A boolean indicating whether softmax was used or not
     */
    public boolean getSoftmax() {
        return(this.softmax);
    }
    /**
     * Return whether entropy was used.
     *
     * @return A boolean indicating whether entropy was used or not
     */
    public boolean getEntropy() {
        return(this.entropy);
    }
    /**
     * Return whether censored was used.
     *
     * @return A boolean indicating whether censored was used or not
     */
    public boolean getCensored() {
        return(this.censored);
    }
}

