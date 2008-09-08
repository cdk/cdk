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
package org.openscience.cdk.qsar.model.R2;

import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.tools.LoggingTool;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;

import java.io.File;
import java.util.HashMap;

/**
 * A modeling class that provides a linear least squares regression model.
 * <p/>
 * When instantiated this class ensures that the R/Java interface has been
 * initialized. The response and independent variables can be specified at construction
 * time or via the <code>setParameters</code> method. The actual fitting procedure is carried out by <code>build</code> after which
 * the model may be used to make predictions.
 * <p/>
 * Currently, the design of the class is quite sparse as it does not allow subsetting,
 * variable names, setting of contrasts and so on.
 * It is also assumed that the values of all the variables are defined (i.e., not such that
 * they are <a href="http://stat.ethz.ch/R-manual/R-patched/library/base/html/NA.html">NA</a>
 * in an R session).
 * The use of
 * this class is shown in the following code snippet
 * <pre>
 * double[][] x;
 * double[] y;
 * try {
 *     LinearRegressionModel lrm = new LinearRegressionModel(x,y);
 *     lrm.build();
 *     lrm.setParameters("newdata", newx);
 *     lrm.setParameters("interval", "confidence");
 *     lrm.predict();
 * } catch (QSARModelException qme) {
 *     System.out.println(qme.toString());
 * }
 * double[] fitted = lrm.getFittedValues()
 * double[] predicted = lrm.getModelPredict().asList.at("fit").asDoubleArray();
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
 * <p/>
 * See {@link RModel} for details regarding the R and rJava environment.
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 * @cdk.keyword linear regression
 * @cdk.keyword R
 */

public class LinearRegressionModel extends org.openscience.cdk.qsar.model.R2.RModel {

    private static int globalID = 0;
    private int nvar = 0;

    private RList modelPredict = null;

    private static LoggingTool logger;

    /**
     * Constructs a LinearRegressionModel object.
     * <p/>
     * The constructor simply instantiates the model ID. Dependent and independent variables
     * should be set via setParameters().
     * <p/>
     * An important feature of the current implementation is that <i>all</i> the
     * independent variables are used during the fit. Furthermore no subsetting is possible.
     * As a result when setting these via setParameters() the caller should specify only
     * the variables and observations that will be used for the fit.
     */
    public LinearRegressionModel() throws QSARModelException {
        super();
        logger = new LoggingTool(this);
        params = new HashMap();
        int currentID = LinearRegressionModel.globalID;
        org.openscience.cdk.qsar.model.R2.LinearRegressionModel.globalID++;
        this.setModelName("cdkLMModel" + currentID);
    }

    /**
     * Constructs a LinearRegressionModel object.
     * <p/>
     * The constructor allows the user to specify the
     * dependent and independent variables. The length of the dependent variable
     * array should equal the number of rows of the independent variable matrix. If this
     * is not the case an exception will be thrown.
     * <p/>
     * An important feature of the current implementation is that <i>all</i> the
     * independent variables are used during the fit. Furthermore no subsetting is possible.
     * As a result when creating an instance of this object the caller should specify only
     * the variables and observations that will be used for the fit.
     *
     * @param xx An array of independent variables. The observations should be in the rows
     *           and the variables should be in the columns
     * @param yy an array containing the dependent variable
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the number of observations in x and y do not match
     */
    public LinearRegressionModel(double[][] xx, double[] yy) throws QSARModelException {
        super();

        params = new HashMap();
        int currentID = LinearRegressionModel.globalID;
        LinearRegressionModel.globalID++;
        this.setModelName("cdkLMModel" + currentID);

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
     * <p/>
     * The constructor allows the user to specify the
     * dependent and independent variables as well as weightings for
     * the observations.
     * <p/>
     * The length of the dependent variable
     * array should equal the number of rows of the independent variable matrix. If this
     * is not the case an exception will be thrown.
     * <p/>
     * An important feature of the current implementation is that <i>all</i> the
     * independent variables are used during the fit. Furthermore no subsetting is possible.
     * As a result when creating an instance of this object the caller should specify only
     * the variables and observations that will be used for the fit.
     *
     * @param xx      An array of independent variables. The observations should be in the rows
     *                and the variables should be in the columns
     * @param yy      an array containing the dependent variable
     * @param weights Specifies the weights for each observation. Unit weights are equivilant
     *                to OLS
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the number of observations in x and y do not match
     */
    public LinearRegressionModel(double[][] xx, double[] yy, double[] weights) throws QSARModelException {
        super();

        params = new HashMap();

        int currentID = LinearRegressionModel.globalID;
        org.openscience.cdk.qsar.model.R2.LinearRegressionModel.globalID++;
        this.setModelName("cdkLMModel" + currentID);

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

    /**
     * Fits a linear regression model.
     * <p/>
     * This method calls the R function to fit a linear regression model
     * to the specified dependent and independent variables. If an error
     * occurs in the R session, an exception is thrown.
     * <p/>
     * Note that, this method should be called prior to calling the various get
     * methods to obtain information regarding the fit.
     */
    public void build() throws QSARModelException {
        // lets do some checks in case stuff was set via setParameters()
        Double[][] x;
        Double[] y, weights;
        x = (Double[][]) this.params.get("x");
        y = (Double[]) this.params.get("y");
        weights = (Double[]) this.params.get("weights");
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
        String paramVarName = loadParametersIntoRSession();
        String cmd = "buildLM(\"" + getModelName() + "\", " + paramVarName + ")";
        REXP ret = rengine.eval(cmd);
        if (ret == null) {
            logger.debug("Error in buildLM");
            throw new QSARModelException("Error in buildLM");
        }

        // remove the parameter list
        rengine.eval("rm(" + paramVarName + ")");

        // save the model object on the Java side
        modelObject = ret.asList();
    }


    /**
     * Sets parameters required for building a linear model or using one for prediction.
     * <p/>
     * This function allows the caller to set the various parameters available
     * for the lm() and predict.lm() R routines. See the R help pages for the details of the available
     * parameters.
     *
     * @param key A String containing the name of the parameter as described in the
     *            R help pages
     * @param obj An Object containing the value of the parameter
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the type of the supplied value does not match the
     *          expected type
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
            if (!(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'newdata' object must be Double[][]");
            }
        }
        this.params.put(key, obj);
    }


    /**
     * Uses a fitted model to predict the response for new observations.
     * <p/>
     * This function uses a previously fitted model to obtain predicted values
     * for a new set of observations. If the model has not been fitted prior to this
     * call an exception will be thrown. Use <code>setParameters</code>
     * to set the values of the independent variable for the new observations and the
     * interval type.
     *
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the model has not been built prior to a call
     *          to this method. Also if the number of independent variables specified for prediction
     *          is not the same as specified during model building
     */
    public void predict() throws QSARModelException {

        if (modelObject == null)
            throw new QSARModelException("Before calling predict() you must fit the model using build()");

        Double[][] newx = (Double[][]) params.get("newdata");
        if (newx[0].length != nvar) {
            throw new QSARModelException("Number of independent variables used for prediction must match those used for fitting");
        }

        String pn = loadParametersIntoRSession();
        REXP ret = rengine.eval("predictLM(\"" + getModelName() + "\", " + pn + ")");
        if (ret == null) throw new QSARModelException("Error occured in prediction");

        // remove the parameter list
        rengine.eval("rm(" + pn + ")");

        modelPredict = ret.asList();
    }

    /**
     * Get the R object obtained from <code>predict.lm()</code>.
     *
     * @return The result of the prediction. Contains a number of fields corresponding to
     *         predicted values, SE and other items depending on the parameters that we set.
     *         Note that the call to <code>predict.lm()</code> is performde with <code>se.fit = TRUE</code>
     */
    public RList getModelPredict() {
        return modelPredict;
    }

    /**
     * Returns an <code>RList</code> object summarizing the linear regression model.
     * <p/>
     * The return object can be queried via the <code>RList</code> methods to extract the
     * required components.
     *
     * @return A summary for the linear regression model
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the model has not been built prior to a call
     *          to this method
     */
    public RList summary() throws QSARModelException {
        if (modelObject == null)
            throw new QSARModelException("Before calling summary() you must fit the model using build()");

        REXP ret = rengine.eval("summary(" + getModelName() + ")");
        if (ret == null) {
            logger.debug("Error in summary()");
            throw new QSARModelException("Error in summary()");
        }
        return ret.asList();
    }


    /**
     * Loads an LinearRegressionModel object from disk in to the current session.
     *
     * @param fileName The disk file containing the model
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the model being loaded is not a linear regression model
     *          object  or the file does not exist
     */
    public void loadModel(String fileName) throws QSARModelException {
        File f = new File(fileName);
        if (!f.exists()) throw new QSARModelException(fileName + " does not exist");

        rengine.assign("tmpFileName", fileName);
        REXP ret = rengine.eval("loadModel(tmpFileName)");
        if (ret == null) throw new QSARModelException("Model could not be loaded");

        String name = ret.asList().at("name").asString();
        if (!isOfClass(name, "lm")) {
            removeObject(name);
            throw new QSARModelException("Loaded object was not of class \'lm\'");
        }

        modelObject = ret.asList().at("model").asList();
        setModelName(name);
        nvar = getCoefficients().length - 1; // since the intercept is also returned
    }

    /**
     * Loads an LinearRegressionModel object from a serialized string into the current session.
     *
     * @param serializedModel A String containing the serialized version of the model
     * @param modelName       A String indicating the name of the model in the R session
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the model being loaded is not a linear regression model
     *          object
     */
    public void loadModel(String serializedModel, String modelName) throws QSARModelException {
        rengine.assign("tmpSerializedModel", serializedModel);
        rengine.assign("tmpModelName", modelName);
        REXP ret = rengine.eval("unserializeModel(tmpSerializedModel, tmpModelName)");

        if (ret == null) throw new QSARModelException("Model could not be unserialized");

        String name = ret.asList().at("name").asString();
        if (!isOfClass(name, "lm")) {
            removeObject(name);
            throw new QSARModelException("Loaded object was not of class \'lm\'");
        }

        modelObject = ret.asList().at("model").asList();
        setModelName(name);
        nvar = getCoefficients().length - 1; // as the intercept is also returned
    }

// Autogenerated code: assumes that 'modelObject' is
// a RList object


    /**
     * Gets the <code>assign</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the assign field
     */
    public int[] getAssign() {
        return modelObject.at("assign").asIntArray();
    }

    /**
     * Gets the <code>coefficients</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the coefficients field
     */
    public double[] getCoefficients() {
        return modelObject.at("coefficients").asDoubleArray();
    }

    /**
     * Gets the <code>df.residual</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the df.residual field
     */
    public int getDfResidual() {
        return modelObject.at("df.residual").asInt();
    }

    /**
     * Gets the <code>effects</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the effects field
     */
    public double[] getEffects() {
        return modelObject.at("effects").asDoubleArray();
    }

    /**
     * Gets the <code>fitted.values</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the fitted.values field
     */
    public double[] getFittedValues() {
        return modelObject.at("fitted.values").asDoubleArray();
    }

    /**
     * Gets the <code>model</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the model field
     */
    public RList getModel() {
        return modelObject.at("model").asList();
    }

    /**
     * Gets the <code>qr</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the qr field
     */
    public RList getQr() {
        return modelObject.at("qr").asList();
    }

    /**
     * Gets the <code>rank</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the rank field
     */
    public int getRank() {
        return modelObject.at("rank").asInt();
    }

    /**
     * Gets the <code>residuals</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the residuals field
     */
    public double[] getResiduals() {
        return modelObject.at("residuals").asDoubleArray();
    }

    /**
     * Gets the <code>xlevels</code> field of an <code>'lm'</code> object.
     *
     * @return The value of the xlevels field
     */
    public RList getXlevels() {
        return modelObject.at("xlevels").asList();
    }


}
