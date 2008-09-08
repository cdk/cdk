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


/** A class that represents a summary of a linear regression model.
 *
 * This class essentially wraps the result of summar.lm. As with other 
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
public class LinearRegressionModelSummary {

    double[] residuals;
    double[][] coeff; // rows - vars, cols - stats
    double rsq, adjrsq, sigma;
    int df;
    int numdf, dendf;
    double fstat;

    String[] colNames;
    String[] rowNames;

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
     * Constructor for an object that wraps the return value from summary.lm.
     *
     * This should not be instantiated directly. The class is meant to be instantiated
     * from an R session
     *
     * @param residuals An array of residuals
     * @param coeff An array of coeffs and associated statistics
     * @param coeffColNames The names of the columns for the coefficient matrix
     * @param coeffRowNames The names of the rows for the coefficient matrix
     * @param sigma The residual error
     * @param df The degrees of freedom
     * @param rsq The R^2 value
     * @param adjrsq The adjusted R^2 value
     * @param fstat The value of the F-statistic
     */
    public LinearRegressionModelSummary( double[] residuals , double coeff[],
            double sigma, double rsq, double adjrsq, int df,
            double[] fstat, String[] coeffRowNames, String[] coeffColNames) {


        this.residuals = new double[residuals.length];
        for (int i = 0; i < residuals.length; i++)
            this.residuals[i] = residuals[i];
        
        this.coeff = vectorToMatrix(coeff, coeff.length/4, 4);

        
        this.colNames  = new String[coeffColNames.length];
        this.rowNames  = new String[coeffRowNames.length];
        for (int i = 0; i < coeffColNames.length; i++) this.colNames[i] = coeffColNames[i];
        for (int i = 0; i < coeffRowNames.length; i++) this.rowNames[i] = coeffRowNames[i];
        
        
        this.sigma = sigma;
        this.df = df;
        this.rsq = rsq;
        this.adjrsq = adjrsq;
        this.numdf = (int)fstat[1];
        this.dendf = (int)fstat[2];
        this.fstat = fstat[0];
        
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
     * Returns the coefficients and associated statistics.
     *
     * This method will return the coefficients as well as the standard
     * error in the coefficients, t-values and p-values corresponding to the 
     * t-values. Thus the return value is a 2D array of doubles, with rows equal
     * to the number of coefficients (ie 1+num predictor variables) and 4 columns
     * containing the estimated coefficients and the above statistics, in the 
     * order mentioned above.
     *
     * @return A 2-D array of doubles containing the estimated coefficients and
     * associated statistics
     */
    public double[][] getCoeff() {
        return(this.coeff);
    }

    /**
     * Returns the R^2 value.
     *
     * @return The R^2 value
     */
    public double getRSQ() {
        return(this.rsq);
    }

    /**
     * Return the adjusted R^2 value.
     *
     * This statistic is generally a better indicator than plain R^2
     *
     * @return The adjusted R^2 value
     */
    public double getAdjRSQ() {
        return(this.adjrsq);
    }

    /** 
     * Return the residual standard error.
     *
     * This method returns the residual standard error and the associated degrees
     * of freedom, in a 2 element array of doubles.
     *
     * @return A 2 element array of doubles containing the residual error and
     * DoF
     */
    public double[] getSigma() {
        double[] ret = {this.sigma, this.df};
        return( ret );
    }

    /**
     * Returns the value of the F-statistic.
     *
     * @return The F-statistic
     * @see #getFStatisticDF
     */
    public double getFStatistic() {
        return(this.fstat);
    }

    /**
     * Returns the degrees of freedom (DoF) for which the F-statistic was calculated.
     *
     * @return A 2 element int[]. The first element is the DoF of the numerator
     * and the second element is the DoF of the denominator
     * @see #getFStatistic
     */
    public int[] getFStatisticDF() {
        int[] ret = {this.numdf, this.dendf};
        return( ret );
    }
            
}

