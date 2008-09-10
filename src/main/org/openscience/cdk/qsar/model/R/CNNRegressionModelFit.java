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

/**
 * A class that wraps the return value from R function, nnet().
 *
 * This is an internal class used by R to return the result of
 * the call to <a href="http://stat.ethz.ch/R-manual/R-patched/library/nnet/html/nnet.html">nnet</a>.
 * As a result it should not be instantiated by the user. The actual modeling
 * class, <code>CNNRegressionModel</code>, provides acess to the various
 * fields of this object.
 * 
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 * @deprecated
 */

public class CNNRegressionModelFit {
    private int noutput; // number of output neurons (== number of classes)
    private double[] weights;
    private double[][] fitted;
    private double[][] residuals;
    private double value;
    private double[][] hessian = null;

    private double[][] vectorToMatrix(double[] v, int nrow, int ncol) {
        double[][] m = new double[nrow][ncol];
        for (int i = 0; i < ncol; i++) {
            for (int j = 0; j < nrow; j++) {
                m[j][i] = v[j + i*nrow];
            }
        }
        return(m);
    }
    
    /**
     * Constructs an object to contain a CNN regression fit.
     *
     * This class should not be instantiated directly and is only
     * required withtin R
     *
     * @param noutput The number of output neurons (ie the number of predicted variables)
     * @param nobs The number of observations
     * @param weights A 1-dimensional array containing the weights and biases
     * @param fitted A 1-dimensional array containing the fitted values
     * @param residual A 1-dimensional array containing the residuals of the fitted values
     * @param value The final value of the cost function
     * @param hessian A 1-dimensional array containing the Hessian
     */
    public CNNRegressionModelFit(
            int noutput, 
            int nobs,
            double[] weights, 
            double[] fitted, double[] residual, 
            double value,
            double[] hessian) {

        // dimensions of hessian = nwt x nwt
        // dimensions of fitted, residual = nobs x noutput
        // also note that matrices come in as columnwise 1D arrays

        this.noutput = noutput;
        setWeights(weights);
        setResiduals(vectorToMatrix(residual, nobs,noutput));
        setFitted(vectorToMatrix(fitted, nobs,noutput));
        setValue(value);
        setHessian(vectorToMatrix(hessian,weights.length,weights.length));
    }
    /**
     * Constructs an object to contain a CNN regression fit.
     *
     * This class should not be instantiated directly and is only
     * required withtin R
     *
     * @param noutput The number of output neurons (ie the number of predicted variables)
     * @param nobs The number of observations
     * @param weights A 1-dimensional array containing the weights and biases
     * @param fitted A 1-dimensional array containing the fitted values
     * @param residual A 1-dimensional array containing the residuals of the fitted values
     * @param value The final value of the cost function
     */
    public CNNRegressionModelFit(
            int noutput, 
            int nobs,
            double[] weights, 
            double[] fitted, double[] residual, 
            double value) {
        this.noutput = noutput;
        setWeights(weights);
        setResiduals(vectorToMatrix(residual, nobs,noutput));
        setFitted(vectorToMatrix(fitted, nobs,noutput));
        setValue(value);
    }

    /**
     * Get the final value of the cost function.
     *
     * This method should not be called outside this class
     *
     * @return The final value of the cost function
     * @see #setValue
     */
    public double getValue() {
        return(this.value);
    }
    /**
     * Set the final value of the cost function.
     *
     * This method should not be called outside this class
     *
     * @param value The value of the cost function at convergence
     * @see #getValue
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Get the Hessian for the final network.
     * 
     * This method should not be called outside this class
     *
     * @return A 2-dimensional array containing the hessian
     * @see #setHessian
     */
    public double[][] getHessian() { return(this.hessian); }
    /**
     * Set the Hessian for the final network.
     * 
     * This method should not be called outside this class
     *
     * @param theHessian A 2-dimensional array containing the hessian
     * @see #getHessian
     */
    public void setHessian(double[][] theHessian) { 
        if (theHessian == null) return;
        this.hessian = new double[theHessian.length][this.noutput];
        for (int i = 0; i < theHessian.length; i++) {
            for (int j = 0; j < this.noutput; j++) {
                this.hessian[i][j] = theHessian[i][j];
            }
        }
    }

    /**
     * Get the weights and biases of the final network.
     * 
     * This method should not be called outside this class
     *
     * @return A 1-dimensional array of weights and biases
     * @see #setWeights
     */
    public double[] getWeights() { return(this.weights); }
    /**
     * Set the weights and biases of the final network.
     * 
     * This method should not be called outside this class
     *
     * @param weights A 1-dimensional array of weights and biases
     * @see #getWeights
     */
    public void setWeights(double[] weights) {
        this.weights = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            this.weights[i] = weights[i];
        }
    }
    /**
     * Get the residuals of the fit.
     * 
     * This method should not be called outside this class
     *
     * @return A 2-dimensional array of residuals. The rows contain the
     * observations and the columns contain the predicted variables
     * @see #setResiduals
     */
    public double[][] getResiduals() { return(this.residuals); }
    /**
     * Set the residuals of the fit.
     * 
     * This method should not be called outside this class
     *
     * @param residuals A 2-dimensional array of residuals. The rows contain the
     * observations and the columns contain the predicted variables
     * @see #getResiduals
     */
    public void setResiduals(double[][] residuals) { 
        this.residuals = new double[residuals.length][this.noutput];
        for (int i = 0; i < residuals.length; i++) {
            for (int j = 0; j < this.noutput; j++) {
                this.residuals[i][j] = residuals[i][j];
            }
        }
    }
    /**
     * Get the fitted values.
     * 
     * This method should not be called outside this class
     *
     * @return A 2-dimensional array of residuals. The rows contain the
     * observations and the columns contain the predicted variables
     * @see #setFitted
     */
    public double[][] getFitted() { return(this.fitted); }
    /**
     * Set the fitted values.
     * 
     * This method should not be called outside this class
     *
     * @param fitted A 2-dimensional array of residuals. The rows contain the
     * observations and the columns contain the predicted variables
     * @see #getFitted
     */
    public void setFitted(double[][] fitted) { 
        this.fitted = new double[fitted.length][this.noutput];
        for (int i = 0; i < fitted.length; i++) {
            for (int j = 0; j < this.noutput; j++) {
                this.fitted[i][j] = fitted[i][j];
            }
        }
    }
}


    
