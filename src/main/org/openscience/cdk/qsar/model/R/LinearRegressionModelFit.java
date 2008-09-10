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
 * A class that wraps the return value from R function, lm..
 *
 * This is an internal class used by R to return the result of
 * the call to <a href="http://stat.ethz.ch/R-manual/R-patched/library/stats/html/lm.html">lm</a>.
 * As a result it should not be instantiated by the user. The actual modeling
 * class, <code>LinearRegressionModel</code>, provides acess to the various
 * fields of this object.
 * 
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 * @deprecated 
 */

public class LinearRegressionModelFit {
    double[] coeff, res, fitted;
    int rank, dfResidual;

    /**
     * Construct the object to contain a linear regression fit.
     *
     * @param coeff A 1-dimensional array of coefficients
     * @param res A 1-dimensional array of residuals
     * @param fitted A 1-dimensional array of fitted values
     * @param rank An integer indicating the rank of the fit
     * @param degreesOfFreedom The degrees of freedom
     */
    public LinearRegressionModelFit(double[] coeff, double[] res, double[] fitted, int rank, int degreesOfFreedom) {
        setCoefficients(coeff);
        setResiduals(res);
        setFitted(fitted);
        setRank(rank);
        setdfResidual(degreesOfFreedom);
    }
    
    /**
     * Get the rank of the fit.
     *
     * @return The rank of the fit
     * @see #setRank
     */
    public int getRank() { return(this.rank); }
    
    /**
     * Set the rank of the fit.
     *
     * This method should not be called outside this class
     *
     * @param rank The rank of the fit
     * @see #getRank
     */
    public void setRank(int rank) { this.rank = rank; };

    /**
     * Get the residuals of the fit.
     *
     * The number of residuals equals the number of observations used
     * to build the model
     * 
     * @return A 1-dimensional array containing the residuals.
     * @see #setResiduals
     */
    public double[] getResiduals() { return(this.res); }

    /**
     * Set the residuals of the fit.
     *
     * This method should not be called outside this class
     *
     * @param residuals A 1-dimensional array of residual values
     * @see #getResiduals
     */
    public void setResiduals(double[] residuals) { 
        this.res = new double[residuals.length];
        for (int i = 0; i < residuals.length; i++) this.res[i] = residuals[i];
    }

    /**
     * Get the fitted coefficients.
     *
     * The number of coefficients equals the number of independent
     * variables used to build the model
     * 
     * @return A 1-dimensional array containing the coefficients.
     * @see #setCoefficients
     */
    public double[] getCoefficients() { return(this.coeff); }

    /**
     * Set the fitted coefficients.
     *
     * 
     * This method should not be called outside this class
     * 
     * @param coeff A 1-dimensional array containing the coefficients.
     * @see #getCoefficients
     */
    public void setCoefficients(double[] coeff) { 
        this.coeff = new double[coeff.length];
        for (int i = 0; i < coeff.length; i++) this.coeff[i] = coeff[i];
    }

    /**
     * Get the DOF of the residuals.
     *
     * @return An integer indicating the D.O.F
     * @see #setdfResidual
     */
    public int getdfResidual() { return(this.dfResidual); }

    /**
     * Set the DOF of the residuals.
     *
     * This method should not be called outside this class
     * 
     * @param degreesOfFreedom The degrees of freedom
     * @see #getdfResidual
     */
    public void setdfResidual(int degreesOfFreedom) { this.dfResidual = degreesOfFreedom; }

    
    /**
     * Get the fitted values.
     *
     * Returns the predicted values for the observations used to 
     * build the model. The number of fitted values equals the number
     * observations used to build the model.
     *
     * @return A 1-dimensional array containing the fitted values
     * @see #setFitted
     */
    public double[] getFitted() { return(this.fitted); }

    /** 
     * Set the fitted values.
     * 
     * This method should not be called outside this class
     *
     * @param fitted A 1-dimensional array of fitted values
     * @see #getFitted
     */
    public void setFitted(double[] fitted) { 
        this.fitted = new double[fitted.length];
        for (int i = 0; i < fitted.length; i++) this.fitted[i] = fitted[i];
    }
}


    
