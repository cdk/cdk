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

import java.util.HashMap;

/** 
 * A modeling class that provides a linear least squares regression model.
 *
 * When instantiated this class ensures that the R/Java interface has been 
 * initialized. The response and independent variables can be specified at construction
 * time or via the <code>setParameters</code> method. The actual fitting procedure is carried out by <code>build</code> after which 
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
 *     lrm.setParameters("newdata", newx);
 *     lrm.setParameters("interval", "confidence");
 *     lrm.predict();
 * } catch (QSARModelException qme) {
 *     System.out.println(qme.toString());
 * }
 * double[] fitted = lrm.getFitFitted();
 * double[] predictedvalues = lrm.getPredictPredicted();
 * </pre>
 * Note that when making predictions, the new X matrix and interval type can be set by calls
 * to setParameters(). In general, the arguments for lm() and predict.lm() can be set via
 * calls to setParameters(). The following table lists the parameters that can be set and their 
 * expected types. More detailed informationis available in the R documentation.
 * <center>
 * <table border=1 cellpadding=5>
 * <THEAD>
 * <tr>
 * <th>Name</th><th>Java Type</th><th>Notes</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>x</td><td>Double[][]</td><td></td>
 * </tr>
 * <tr>
 * <td>y</td><td>Double[]</td><td>Length should be equal to the rows of x</td>
 * </tr>
 * <tr>
 * <td>weights</td><td>Double[]</td><td>Length should be equal to rows of x</td>
 * </tr>
 * <tr>
 * <td>newdata</td><td>Double[][]</td><td>Number of columns should be the same as in x</td>
 * </tr>
 * <tr>
 * <td>interval</td><td>String</td><td>Can be 'confidence' or 'predicton'</td>
 * </tr>
 * </tbody>
 * </table>
 * </center>
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

    private HashMap params = null;
    private int nvar = 0;
    
    /**
     * Constructs a LinearRegressionModel object.
     *
     * The constructor simply instantiates the model ID. Dependent and independent variables
     * should be set via setParameters(). 
     * <p>
     * An important feature of the current implementation is that <i>all</i> the 
     * independent variables are used during the fit. Furthermore no subsetting is possible.
     * As a result when setting these via setParameters() the caller should specify only
     * the variables and observations that will be used for the fit.
     */
    public LinearRegressionModel(){
        super();

        this.params = new HashMap();

        this.currentID = this.globalID;
        this.globalID++;
        this.setModelName("cdkLMModel"+this.currentID);
    }

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

        this.params = new HashMap();

        this.currentID = this.globalID;
        this.globalID++;
        this.setModelName("cdkLMModel"+this.currentID);

        int nrow = yy.length;
        this.nvar = xx[0].length;

        if (nrow != xx.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }
        
        Double[][] x = new Double[nrow][this.nvar];
        Double[] y = new Double[nrow];
        Double[] weights = new Double[nrow];

        for (int i = 0; i < nrow; i++) {
            y[i] = new Double(yy[i]);
            weights[i] = new Double(1.0);
        }
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < this.nvar; j++) 
                x[i][j] = new Double(xx[i][j]);
        }

        params.put("x", x);
        params.put("y", y);
        params.put("weights", weights);
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

        this.params = new HashMap();

        this.currentID = this.globalID;
        this.globalID++;
        this.setModelName("cdkLMModel"+this.currentID);

        int nrow = yy.length;
        this.nvar = xx[0].length;

        if (nrow != xx.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }
        if (nrow != weights.length) {
            throw new QSARModelException("The length of the weight vector does not match the number of rows of the design matrix");
        }
        
        Double[][] x = new Double[nrow][this.nvar];
        Double[] y = new Double[nrow];
        Double[] wts = new Double[nrow];

        for (int i = 0; i < nrow; i++) {
            y[i] = new Double(yy[i]);
            wts[i] = new Double(weights[i]);
        }
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < this.nvar; j++) 
                x[i][j] = new Double(xx[i][j]);
        }
        params.put("x", x);
        params.put("y", y);
        params.put("weights", wts);
    }

    protected void finalize() {
        revaluator.voidEval("rm("+this.getModelName()+",pos=1)");
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
        // lets do some checks in case stuff was set via setParameters()
        Double[][] x;
        Double[] y,weights;
        x = (Double[][])this.params.get("x");
        y = (Double[])this.params.get("y");
        weights = (Double[])this.params.get("weights");
        if (this.nvar == 0) this.nvar = x[0].length;
        else {
            if (y.length != x.length) {
                throw new QSARModelException("Number of observations does no match number of rows in the design matrix");
            }
            if (weights.length != y.length) {
                throw new QSARModelException("The weight vector must have the same length as the number of observations");
            }
        }

        // lets build the model
        try {
            this.modelfit = (LinearRegressionModelFit)revaluator.call("buildLM", 
                    new Object[]{ getModelName(), this.params });
        } catch (Exception re) {
            throw new QSARModelException(re.toString());
        }
    }


    /**
     * Sets parameters required for building a linear model or using one for prediction.
     *
     * This function allows the caller to set the various parameters available
     * for the lm() and predict.lm() R routines. See the R help pages for the details of the available
     * parameters.
     * 
     * @param key A String containing the name of the parameter as described in the 
     * R help pages
     * @param obj An Object containing the value of the parameter
     */
    public void setParameters(String key, Object obj) throws QSARModelException {
        // since we know the possible values of key we should check the coresponding
        // objects and throw errors if required. Note that this checking can't really check
        // for values (such as number of variables in the X matrix to build the model and the
        // X matrix to make new predictions) - these should be checked in functions that will
        // use these parameters. The main checking done here is for the class of obj and
        // some cases where the value of obj is not dependent on what is set before it

        if (key.equals("y")) {
            if (!(obj instanceof Double[])) {
                throw new QSARModelException("The class of the 'y' object must be Double[]");
            }
        }
        if (key.equals("x")) {
            if (!(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'x' object must be Double[][]");
            }
        }
        if (key.equals("weights")) {
            if (!(obj instanceof Double[])) {
                throw new QSARModelException("The class of the 'weights' object must be Double[]");
            }
        }
        if (key.equals("interval")) {
            if (!(obj instanceof String)) {
                throw new QSARModelException("The class of the 'interval' object must be String");
            }
            if (!(obj.equals("confidence") || obj.equals("prediction"))) {
                throw new QSARModelException("The type of interval must be: prediction or confidence");
            }
        }
        if (key.equals("newdata")) {
            if ( !(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'newdata' object must be Double[][]");
            }
        }
        this.params.put(key,obj);
    }
       
        
    /**
     * Uses a fitted model to predict the response for new observations.
     *
     * This function uses a previously fitted model to obtain predicted values
     * for a new set of observations. If the model has not been fitted prior to this
     * call an exception will be thrown. Use <code>setParameters</code>
     * to set the values of the independent variable for the new observations and the
     * interval type.
     */
    public void predict() throws QSARModelException {
        if (this.modelfit == null) 
            throw new QSARModelException("Before calling predict() you must fit the model using build()");

        Double[][] newx = (Double[][])this.params.get(new String("newdata"));
        if (newx[0].length != this.nvar) {
            throw new QSARModelException("Number of independent variables used for prediction must match those used for fitting");
        }
            
        try {
            this.modelpredict = (LinearRegressionModelPredict)revaluator.call("predictLM",
                    new Object[]{ getModelName(), this.params });
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
