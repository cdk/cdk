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
 * A class that wraps the return value from the R function, predict.lm.
 *
 * This is an internal class used by R to return the result of
 * the call to <a href="http://stat.ethz.ch/R-manual/R-patched/library/stats/html/predict.lm.html">predict.lm</a>.
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
public class LinearRegressionModelPredict {
    double[] pred, lwr, upr, sefit;
    int degreesOfFreedom;
    double residualScale;

    /**
     * Construct the object to contain  linear regression predictions.
     *
     * @param predicted A 1-dimensional array of predicted values
     * @param standardErrors A 1-dimensional array of standard errors of prediction
     * @param lowerBounds A 1-dimensional array of lower confidence bounds
     * @param upperBounds A 1-dimensional array of upper confidence bounds
     * @param degreesOfFreedom The degrees of freedom of hte predictions
     * @param residualScale The scale of the residuals
     */
    public LinearRegressionModelPredict(double[] predicted, double[] standardErrors,
            double[] lowerBounds, double[] upperBounds,
            int degreesOfFreedom, double residualScale) {
        setPredicted(predicted);
        setSEFit(standardErrors);
        setLower(lowerBounds);
        setUpper(upperBounds);
        setDF(degreesOfFreedom);
        setResidualScale(residualScale);
    }
    /**
     * Construct the object to contain  linear regression predictions.
     *
     * This is required if a single prediction was requested in which case
     * R will pass a single double value rather than an array.
     * 
     * @param predicted The predicted values
     * @param standardErrors The standard errors of prediction
     * @param lowerBounds The lower confidence bounds
     * @param upperBounds The upper confidence bounds
     * @param degreesOfFreedom The degrees of freedom of hte predictions
     * @param residualScale The scale of the residuals
     */
    public LinearRegressionModelPredict(double predicted, double standardErrors,
            double lowerBounds, double upperBounds,
            int degreesOfFreedom, double residualScale) {
        setPredicted(new double[] {predicted});
        setSEFit(new double[] {standardErrors});
        setLower(new double[] {lowerBounds});
        setUpper(new double[] {upperBounds});
        setDF(degreesOfFreedom);
        setResidualScale(residualScale);
    }

    /**
     * Get the degrees of freedom.
     *
     * @return An integer indicating the degrees of freedom
     * @see #setDF
     */
    public int getDF() { return(this.degreesOfFreedom); }

    /**
     * Set the degrees of freedom.
     *
     * This method should not be called outside this class
     *
     * @param degreesOfFreedom An integer indicating the degrees of freedom
     * @see #getDF
     */
    public void setDF(int degreesOfFreedom) { this.degreesOfFreedom = degreesOfFreedom; }

    /** 
     * Get the scale of residuals.
     *
     * @return A double indicating the residual scale
     * @see #setResidualScale
     */
    public double getResidualScale() { return(this.residualScale); }

    /**
     * Set the scale of the residuals.
     *
     * This method should not be called outside this class
     * 
     * @param scale The scale of the residuals
     * @see #getResidualScale
     */
    public void setResidualScale(double scale) { this.residualScale = scale; }

    /**
     * Get predicted values.
     *
     * Get the predictions for a set of observations from the current linear
     * regression fit
     *
     * @return A 1-dimensional array containing the predicted values
     * @see #setPredicted
     */
    public double[] getPredicted() { return(this.pred); }

    /**
     * Set the predicted values.
     *
     * This method should not be called outside this class
     *
     * @param predicted A 1-dimensional array of predicted values
     * @see #getPredicted
     */
    public void setPredicted(double[] predicted) { 
        this.pred = new double[predicted.length];
        for (int i = 0; i < predicted.length; i++) this.pred[i] = predicted[i];
    }

    /**
     * Get the lower confidence bounds.
     *
     * Gets the lower confidence bounds for the predicted values of
     * the observations
     * 
     * @return A 1-dimensional array of lower confidence bounds
     * @see #setLower
     */
    public double[] getLower() { return(this.lwr); }

    /**
     * Set the lower confidence bounds.
     *
     * This method should not be called outside this class
     *
     * @param lowerBounds A 1-dimensional array of lower confidence bounds
     * @see #getLower
     */
    public void setLower(double[] lowerBounds) { 
        this.lwr = new double[lowerBounds.length];
        for (int i = 0; i < lowerBounds.length; i++) this.lwr[i] = lowerBounds[i];
    }

    /**
     * Get the upper confidence bounds.
     *
     * Gets the upper confidence bounds for the predicted values of
     * the observations
     * 
     * @return A 1-dimensional array of upper confidence bounds
     * @see #setUpper
     */
    public double[] getUpper() { return(this.upr); }

    /**
     * Set the upper confidence bounds.
     *
     * This method should not be called outside this class
     *
     * @param upperBounds A 1-dimensional array of upper confidence bounds
     * @see #getUpper
     */
    public void setUpper(double[] upperBounds) { 
        this.upr = new double[upperBounds.length];
        for (int i = 0; i < upperBounds.length; i++) this.upr[i] = upperBounds[i];
    }


    /** 
     * Get the standard errors of prediction.
     *
     * @return A 1-dimensional array of standard errors
     * @see #setSEFit
     */
    public double[] getSEFit() { return(this.sefit); }

    /**
     * Set the standard errors of predictions.
     *
     * @param standardErrors A 1-dimensional array of standard errors
     * @see #getSEFit
     */
    public void setSEFit(double[] standardErrors) { 
        this.sefit = new double[standardErrors.length];
        for (int i = 0; i < standardErrors.length; i++) this.sefit[i] = standardErrors[i];
    }

}


