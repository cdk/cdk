/*
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.openscience.cdk.qsar.model.R;

import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.qsar.model.R.LinearRegressionModelFit;
import org.openscience.cdk.qsar.model.R.LinearRegressionModelPredict;

import org.omegahat.R.Java.REvaluator;
import org.omegahat.R.Java.ROmegahatInterpreter;


/** 
 * A modeling class that provides a linear least squares regression model.
 *
 * When instantiated this class ensures that the R/Java interface has been 
 * initialized. The response and independent variables can be specified at construction
 * time. The actual fitting procedure is carried out by <code>build</code> after which 
 * the model may be used to make predictions.
 * <p>
 * Currently, the design of the class is quite sparse as it does not allow subsetting,
 * variable names, setting of contrasts and so on. 
 * It is also assumed that the values of all the variables are defined (i.e., not such that 
 * they are <a href="http://stat.ethz.ch/R-manual/R-patched/library/base/html/NA.html">NA</a>
 * in an R session).
 * The use of
 * this class is shown in the following code snippet
 * <pre>
 * try {
 *     LinearRegressionModel lrm = new LinearRegressionModel(x,y);
 *     lrm.build();
 *     lrm.setPredictionParameters(newx, "confidence");
 *     lrm.predict();
 * } catch (QSARModelException qme) {
 *     System.out.println(qme.toString());
 * }
 * double[] fitted = lrm.getFitFitted();
 * double[] predictedvalues = lrm.getPredictPredicted();
 * </pre>
 * In general the <code>getFit*</code> methods provide access to results from the fit
 * and <code>getPredict*</code> methods provide access to results from the prediction (i.e.,
 * prediction using the model on new data). The values returned correspond to the various 
 * values returned by the <a href="http://stat.ethz.ch/R-manual/R-patched/library/stats/html/lm.html">lm</a>
 * and <a href="http://stat.ethz.ch/R-manual/R-patched/library/stats/html/predict.lm.html">predict.lm</a>
 * functions in R.
 * <p>
 * See {@link RModel} for details regarding the R and SJava environment.
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 */

public class LinearRegressionModel extends RModel {

    private static int globalID = 0;
    private int currentID;
    private LinearRegressionModelFit modelfit = null;
    private LinearRegressionModelPredict modelpredict = null;

    private double[] y = null;
    private double[][] x = null;
    private double[] weights = null;

    private String interval = "";
    private double[][] newx = null;

    /**
     * Constructs a LinearRegressionModel object.
     *
     * The constructor allows the user to specify the 
     * dependent and independent variables. The length of the dependent variable
     * array should equal the number of rows of the independent variable matrix. If this 
     * is not the case an exception will be thrown.
     * <p>
     * An important feature of the current implementation is that <i>all</i> the 
     * independent variables are used during the fit. Furthermore no subsetting is possible.
     * As a result when creating an instance of this object the caller should specify only
     * the variables and observations that will be used for the fit.
     * 
     * @param xx An array of independent variables. The observations should be in the rows
     * and the variables should be in the columns
     * @param yy an array containing the dependent variable
     */
    public LinearRegressionModel(double[][] xx, double[] yy) throws QSARModelException{
        super();

        this.currentID = this.globalID;
        this.globalID++;

        int nrow = yy.length;
        int ncol = xx[0].length;

        if (nrow != xx.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }
        
        this.x = new double[ncol][nrow];
        this.y = new double[nrow];
        this.weights = new double[nrow];

        for (int i = 0; i < nrow; i++) {
            this.y[i] = yy[i];
            this.weights[i] = 1.0;
        }
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) 
                this.x[j][i] = xx[i][j];
        }
    }
        

    /**
     * Constructs a LinearRegressionModel object.
     *
     * The constructor allows the user to specify the 
     * dependent and independent variables as well as weightings for
     * the observations. 
     * <p>
     * The length of the dependent variable
     * array should equal the number of rows of the independent variable matrix. If this 
     * is not the case an exception will be thrown.
     * <p>
     * An important feature of the current implementation is that <i>all</i> the 
     * independent variables are used during the fit. Furthermore no subsetting is possible.
     * As a result when creating an instance of this object the caller should specify only
     * the variables and observations that will be used for the fit.
     * 
     * @param xx An array of independent variables. The observations should be in the rows
     * and the variables should be in the columns
     * @param yy an array containing the dependent variable
     * @param weights Specifies the weights for each observation. Unit weights are equivilant
     * to OLS
     */
    public LinearRegressionModel(double[][] xx, double[] yy, double[] weights) throws QSARModelException{
        super();

        this.currentID = this.globalID;
        this.globalID++;

        int nrow = yy.length;
        int ncol = xx[0].length;

        if (nrow != xx.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }
        
        this.x = new double[ncol][nrow];
        this.y = new double[nrow];
        this.weights = new double[nrow];

        for (int i = 0; i < nrow; i++) {
            this.y[i] = yy[i];
            this.weights[i] = weights[i];
        }
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) 
                this.x[j][i] = xx[i][j];
        }
    }

    protected void finalize() {
        revaluator.voidEval("rm("+this.getModelName()+",pos=1)");
    }
        
    
    /**
     * Get the name of the model.
     *
     * This function returns the name of the variable that the actual
     * linear model is stored in within the R session. In general this is 
     * not used for the end user. In the future this might be changed 
     * to a private method.
     *
     * @return String containing the name of the R variable
     */
    public String getModelName() {
        return("cdkLMModel"+currentID);
    }

    /**
     * Fits a linear regression model.
     *
     * This method calls the R function to fit a linear regression model
     * to the specified dependent and independent variables. If an error
     * occurs in the R session, an exception is thrown.
     * <p>
     * Note that, this method should be called prior to calling the various get
     * methods to obtain information regarding the fit.
     */
    public void build() throws QSARModelException {
        try {
            this.modelfit = (LinearRegressionModelFit)revaluator.call("buildLM", 
                    new Object[]{ getModelName(), this.x, this.y, this.weights });
        } catch (Exception re) {
            throw new QSARModelException(re.toString());
        }
    }


    /**
     * Sets parameters for the prediction routine.
     *
     * This function allows the caller to set the independent variables for
     * a new set of observations for which we want to make predictions. In addition
     * the user can also specify the type of interval calculation to be returned -
     * confidence or prediction.
     *
     * @param newx A matrix containing the values of the independent variables for
     * the new observations. The number of columns should match the number of columns
     * of the independent variable matrix used when fitting the model
     * @param interval A String indicating which type of interval calculation is to be used. Possible
     * values are 'confidence' or 'prediction'
     */
    public void setPredictionParameters(double[][] newx, String interval) throws QSARModelException {
        if (!(interval.equals("confidence") || interval.equals("prediction"))) {
            throw new QSARModelException("The type of interval must be: prediction or confidence");
        } else {
            this.interval = interval;
        }
        int nrow = newx.length;
        int ncol = newx[0].length;

        if (ncol != this.x.length) 
            throw new QSARModelException("Number of independent variables used for prediction must match those used for fitting");

        this.newx = new double[ncol][nrow];
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) 
                this.newx[j][i] = newx[i][j];
        }
    }
       
        
    /**
     * Uses a fitted model to predict the response for new observations.
     *
     * This function uses a previously fitted model to obtain predicted values
     * for a new set of observations. If the model has not been fitted prior to this
     * call an exception will be thrown. Use <code>setPredictionParameters</code>
     * to set the values of the independent variable for the new observations and the
     * interval type.
     */
    public void predict() throws QSARModelException {
        if (this.modelfit == null) 
            throw new QSARModelException("Before calling predict() you must fit the model using build()");

        try {
            this.modelpredict = (LinearRegressionModelPredict)revaluator.call("predictLM",
                    new Object[]{ getModelName(), this.newx, this.interval });
        } catch (Exception re) {
            throw new QSARModelException(re.toString());
        }
    }

    /* interface to fit object */

    /**
     * Gets the rank of the fitted linear model.
     *
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * 
     * @return An integer indicating the rank
     */
    public int getFitRank() { return(this.modelfit.getRank()); }

    /**
     * Returns the residuals.
     *
     * The residuals are the response minus the fitted values.
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * @return A double[] contaning the residuals for each observation
     */
    public double[] getFitResiduals() { return(this.modelfit.getResiduals()); }
    
    /**
     * Returns the estimated coefficients.
     *
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * @return A double[] containing the coefficients
     */
    public double[] getFitCoefficients() { return(this.modelfit.getCoefficients()); }

    /**
     * Returns the residual degrees of freedom.
     *
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * @return An integr indicating the residual degrees of freedom
     */
    public int getFitDFResidual() { return(this.modelfit.getdfResidual()); }

    /**
     * Returns the fitted mean values.
     *
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * @return A double[] containing the fitted  values
     */
    public double[] getFitFitted() { return(this.modelfit.getFitted()); }
    




    /* interface to predict object */

    /**
     * Returns the degrees of freedom for residual.
     *
     * @return An integer indicating degrees of freedom
     */
    public int getPredictDF() { return(this.modelpredict.getDF()); }

    /**
     * Returns the residual standard deviations.
     *
     * @return A double indicating residual standard deviations
     */
    public double getPredictResidualScale() { return(this.modelpredict.getResidualScale()); }

    /**
     * Returns the predicted values for the prediction set. 
     *
     * This function only returns meaningful results if the <code>predict</code>
     * method of this class has been called.
     *
     * @return A double[] containing the predicted values
     */
    public double[] getPredictPredicted() { return(this.modelpredict.getPredicted()); }

    /**
     * Returns the lower prediction bounds. 
     *
     * By default the bounds (both lower and upper) are confidence bounds. However
     * the call to <code>predict</code> can specify prediction bounds.
     * This function only returns meaningful results if the <code>predict</code>
     * method of this class has been called.
     *
     * @return A double[] containing the lower bounds for the predictions
     */
    public double[] getPredictLowerBound() { return(this.modelpredict.getLower()); }

    /**
     * Returns the upper prediction bounds. 
     *
     * By default the bounds (both lower and upper) are confidence bounds. However
     * the call to <code>predict</code> can specify prediction bounds.
     * This function only returns meaningful results if the <code>predict</code>
     * method of this class has been called.
     *
     * @return A double[] containing the upper bounds for the predictions
     */
    public double[] getPredictUpperBound() { return(this.modelpredict.getUpper()); }

    /** 
     * Returns the standard error of predictions.
     *
     * This function only returns meaningful results if the <code>predict</code>
     * method of this class has been called.
     *
     * @return A double[] containing the standard error of predictions.
     */
    public double[] getPredictSEPredictions() { return(this.modelpredict.getSEFit()); }
}
