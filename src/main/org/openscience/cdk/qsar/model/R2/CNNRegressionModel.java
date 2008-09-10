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
import org.rosuda.JRI.RBool;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;

import java.io.File;
import java.util.HashMap;

/**
 * A modeling class that provides a computational neural network regression model.
 * <p/>
 * When instantiated this class ensures that the R/Java interface has been
 * initialized. The response and independent variables can be specified at construction
 * time or via the <code>setParameters</code> method.
 * The actual fitting procedure is carried out by <code>build</code> after which
 * the model may be used to make predictions, via <code>predict</code>. An example of the use
 * of this class is shown below:
 * <pre>
 * double[][] x;
 * double[] y;
 * Double[] wts;
 * Double[][] newx;
 * ...
 * try {
 *     CNNRegressionModel cnnrm = new CNNRegressionModel(x,y,3);
 *     cnnrm.setParameters("Wts",wts);
 *     cnnrm.build();
 * <p/>
 *     double fitValue = cnnrm.getFitValue();
 * <p/>
 *     cnnrm.setParameters("newdata", newx);
 *     cnnrm.setParameters("type", "raw");
 *     cnnrm.predict();
 * <p/>
 *     double[][] preds = cnnrm.getPredictPredicted();
 * } catch (QSARModelException qme) {
 *     System.out.println(qme.toString());
 * }
 * </pre>
 * The above code snippet builds a 3-3-1 CNN model.
 * Multiple output neurons are easily
 * specified by supplying a matrix for y (i.e., double[][]) with the output variables
 * in the columns.
 * <p/>
 * Nearly all the arguments to
 * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet()</a> are
 * supported via the <code>setParameters</code> method. The table below lists the names of the arguments,
 * the expected type of the argument and the default setting for the arguments supported by this wrapper class.
 * <center>
 * <table border=1 cellpadding=5>
 * <THEAD>
 * <tr>
 * <th>Name</th><th>Java Type</th><th>Default</th><th>Notes</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr><td>x</td><td>Double[][]</td><td>None</td><td>This must be set by the caller via the constructors or via <code>setParameters</code></td></tr>
 * <tr><td>y</td><td>Double[][]</td><td>None</td><td>This must be set by the caller via the constructors or via <code>setParameters</code></td></tr>
 * <tr><td>weights</td><td>Double[]</td><td>rep(1,nobs)</td><td>The default case weights is a vector of 1's equal in length to the number of observations, nobs</td></tr>
 * <tr><td>size</td><td>Integer</td><td>None</td><td>This must be set by the caller via the constructors or via <code>setParameters</code></td></tr>
 * <tr><td>subset</td><td>Integer[]</td><td>1:nobs</td><td>This is supposed to be an index vector specifying which observations are to be used in building the model. The default indicates that all should be used</td></tr>
 * <tr><td>Wts</td><td>Double[]</td><td>runif(1,nwt)</td><td>The initial weight vector is set to a random vector of length equal to the number of weights if not set by the user</td></tr>
 * <tr><td>mask</td><td>Boolean[]</td><td>rep(TRUE,nwt)</td><td>All weights are to be optimized unless otherwise specified by the user</td></tr>
 * <tr><td>linout</td><td>Boolean</td><td>TRUE</td><td>Since this class performs regression this need not be changed</td></tr>
 * <tr><td>entropy</td><td>Boolean</td><td>FALSE</td><td></td></tr>
 * <tr><td>softmax</td><td>Boolean</td><td>FALSE</td><td></td></tr>
 * <tr><td>censored</td><td>Boolean</td><td>FALSE</td><td></td></tr>
 * <tr><td>skip</td><td>Boolean</td><td>FALSE</td><td></td></tr>
 * <tr><td>rang</td><td>Double</td><td>0.7</td><td></td></tr>
 * <tr><td>decay</td><td>Double</td><td>0.0</td><td></td></tr>
 * <tr><td>maxit</td><td>Integer</td><td>100</td><td></td></tr>
 * <tr><td>Hess</td><td>Boolean</td><td>FALSE</td><td></td></tr>
 * <tr><td>trace</td><td>Boolean</td><td>TRUE</td><td></td></tr>
 * <tr><td>MaxNWts</td><td>Integer</td><td>1000</td><td></td></tr>
 * <tr><td>abstol</td><td>Double</td><td>1.0e-4</td><td></td></tr>
 * <tr><td>reltol</td><td>Double</td><td>1.0e-8</td><td></td></tr>
 * </tbody>
 * </table>
 * </center>
 * <p/>
 * The values returned correspond to the various
 * values returned by the <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a> and
 * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/predict.nnet.html" target="_top">predict.nnet</a> functions
 * in R
 * <p/>
 * See {@link org.openscience.cdk.qsar.model.R.RModel} for details regarding the R and Java environment.
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 * @cdk.keyword neural network
 * @cdk.keyword R
 */

public class CNNRegressionModel extends RModel {
    public static int globalID = 0;
    private int noutput = 0;
    private int nvar = 0;

    private double[][] modelPredict = null;

    private static LoggingTool logger;

    private void setDefaults() {
        // lets set the default values of the arguments that are specified
        // to have default values in ?nnet

        // these params are vectors that depend on user defined stuff
        // so as a default we set them to FALSE so R can check if these
        // were not set
        this.params.put("subset", Boolean.FALSE);
        this.params.put("mask", Boolean.FALSE);
        this.params.put("Wts", Boolean.FALSE);
        this.params.put("weights", Boolean.FALSE);

        this.params.put("linout", Boolean.TRUE); // we want only regression
        this.params.put("entropy", Boolean.FALSE);
        this.params.put("softmax", Boolean.FALSE);
        this.params.put("censored", Boolean.FALSE);
        this.params.put("skip", Boolean.FALSE);
        this.params.put("rang", new Double(0.7));
        this.params.put("decay", new Double(0.0));
        this.params.put("maxit", Integer.valueOf(100));
        this.params.put("Hess", Boolean.FALSE);
        this.params.put("trace", Boolean.FALSE); // no need to see output
        this.params.put("MaxNWts", Integer.valueOf(1000));
        this.params.put("abstol", new Double(1.0e-4));
        this.params.put("reltol", new Double(1.0e-8));
    }

    /**
     * Constructs a CNNRegressionModel object.
     * <p/>
     * This constructor allows the user to simply set up an instance of a CNN
     * regression modeling class. This constructor simply sets the name for this
     * instance. It is expected all the relevent parameters for modeling will be
     * set at a later point.
     * <p/>
     * Other parameters that are required to be set should be done via
     * calls to <code>setParameters</code>. A number of parameters are set to the
     * defaults as specified in the manpage for
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a>.
     */
    public CNNRegressionModel() throws QSARModelException {
        super();
        logger = new LoggingTool(this);

        params = new HashMap();
        int currentID = CNNRegressionModel.globalID;
        CNNRegressionModel.globalID++;
        setModelName("cdkCNNModel" + currentID);
        setDefaults();


    }


    /**
     * Constructs a CNNRegressionModel object.
     * <p/>
     * This constructor allows the user to specify the dependent and
     * independent variables along with the number of hidden layer neurons.
     * This constructor is suitable for cases when there is a single output
     * neuron. If the number of rows of the design matrix is not equal to
     * the number of observations in y an exception will be thrown.
     * <p/>
     * Other parameters that are required to be set should be done via
     * calls to <code>setParameters</code>. A number of parameters are set to the
     * defaults as specified in the manpage for
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a>.
     *
     * @param x    An array of independent variables. Observations should be in
     *             the rows and variables in the columns.
     * @param y    An array (single column) of observed values
     * @param size The number of hidden layer neurons
     * @throws QSARModelException if the number of observations in x and y do not match
     */
    public CNNRegressionModel(double[][] x, double[] y, int size) throws QSARModelException {
        super();
        logger = new LoggingTool(this);

        params = new HashMap();
        int currentID = CNNRegressionModel.globalID;
        CNNRegressionModel.globalID++;
        setModelName("cdkCNNModel" + currentID);

        int nrow = y.length;
        int ncol = x[0].length;

        if (nrow != x.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }

        nvar = ncol;
        noutput = 1;

        Double[][] xx = new Double[nrow][ncol];
        Double[][] yy = new Double[nrow][1];

        for (int i = 0; i < nrow; i++) {
            yy[i][0] = new Double(y[i]);
            for (int j = 0; j < ncol; j++) {
                xx[i][j] = new Double(x[i][j]);
            }
        }
        params.put("x", xx);
        params.put("y", yy);
        params.put("size", Integer.valueOf(size));
        setDefaults();
    }

    /**
     * Constructs a CNNRegressionModel object.
     * <p/>
     * This constructor allows the user to specify the dependent and
     * independent variables along with the number of hidden layer neurons.
     * This constructor is suitable for cases when there are multiple output
     * neuron. If the number of rows of the design matrix is not equal to
     * the number of observations in y an exception will be thrown.
     * <p/>
     * Other parameters that are required to be set should be done via
     * calls to <code>setParameters</code>. A number of parameters are set to the
     * defaults as specified in the manpage for
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a>.
     *
     * @param x    An array of independent variables. Observations should be in
     *             the rows and variables in the columns.
     * @param y    An array (multiple columns) of observed values
     * @param size The number of hidden layer neurons
     * @throws QSARModelException if the number of observations in x and y do not match
     */
    public CNNRegressionModel(double[][] x, double[][] y, int size) throws QSARModelException {
        super();
        logger = new LoggingTool(this);

        params = new HashMap();
        int currentID = CNNRegressionModel.globalID;
        CNNRegressionModel.globalID++;
        setModelName("cdkCNNModel" + currentID);

        int nrow = y.length;
        int ncol = x[0].length;

        if (nrow != x.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }

        nvar = ncol;
        noutput = y[0].length;

        Double[][] xx = new Double[nrow][ncol];
        Double[][] yy = new Double[nrow][noutput];

        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) {
                xx[i][j] = new Double(x[i][j]);
            }
        }
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < noutput; j++) {
                yy[i][j] = new Double(y[i][j]);
            }
        }
        params.put("x", xx);
        params.put("y", yy);
        params.put("size", Integer.valueOf(size));
        setDefaults();
    }


    /**
     * Sets parameters required for building a CNN model or using one for prediction.
     * <p/>
     * This function allows the caller to set the various parameters available
     * for the
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a>
     * and
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/predict.nnet.html" target="_top">predict.nnet</a>
     * R routines. See the R help pages for the details of the available
     * parameters.
     *
     * @param key A String containing the name of the parameter as described in the
     *            R help pages
     * @param obj An Object containing the value of the parameter
     * @throws QSARModelException if the type of the supplied value does not match the
     *                            expected type
     */
    public void setParameters(String key, Object obj) throws QSARModelException {
        // since we know the possible values of key we should check the coresponding
        // objects and throw errors if required. Note that this checking can't really check
        // for values (such as number of variables in the X matrix to build the model and the
        // X matrix to make new predictions) - these should be checked in functions that will
        // use these parameters. The main checking done here is for the class of obj and
        // some cases where the value of obj is not dependent on what is set before it

        if (key.equals("y")) {
            if (!(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'y' object must be Double[][]");
            } else {
                noutput = ((Double[][]) obj)[0].length;
            }
        }
        if (key.equals("x")) {
            if (!(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'x' object must be Double[][]");
            } else {
                nvar = ((Double[][]) obj)[0].length;
            }
        }
        if (key.equals("weights")) {
            if (!(obj instanceof Double[])) {
                throw new QSARModelException("The class of the 'weights' object must be Double[]");
            }
        }
        if (key.equals("size")) {
            if (!(obj instanceof Integer)) {
                throw new QSARModelException("The class of the 'size' object must be Integer");
            }
        }
        if (key.equals("subset")) {
            if (!(obj instanceof Integer[])) {
                throw new QSARModelException("The class of the 'size' object must be Integer[]");
            }
        }
        if (key.equals("Wts")) {
            if (!(obj instanceof Double[])) {
                throw new QSARModelException("The class of the 'Wts' object must be Double[]");
            }
        }
        if (key.equals("mask")) {
            if (!(obj instanceof Boolean[])) {
                throw new QSARModelException("The class of the 'mask' object must be Boolean[]");
            }
        }
        if (key.equals("linout") ||
                key.equals("entropy") ||
                key.equals("softmax") ||
                key.equals("censored") ||
                key.equals("skip") ||
                key.equals("Hess") ||
                key.equals("trace")) {
            if (!(obj instanceof Boolean)) {
                throw new QSARModelException("The class of the 'trace|skip|Hess|linout|entropy|softmax|censored' object must be Boolean");
            }
        }
        if (key.equals("rang") ||
                key.equals("decay") ||
                key.equals("abstol") ||
                key.equals("reltol")) {
            if (!(obj instanceof Double)) {
                throw new QSARModelException("The class of the 'reltol|abstol|decay|rang' object must be Double");
            }
        }
        if (key.equals("maxit") ||
                key.equals("MaxNWts")) {
            if (!(obj instanceof Integer)) {
                throw new QSARModelException("The class of the 'maxit|MaxNWts' object must be Integer");
            }
        }

        if (key.equals("newdata")) {
            if (!(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'newdata' object must be Double[][]");
            }
        }
        params.put(key, obj);
    }

    /**
     * Fits a CNN regression model.
     * <p/>
     * This method calls the R function to fit a CNN regression model
     * to the specified dependent and independent variables. If an error
     * occurs in the R session, an exception is thrown.
     * <p/>
     * Note that, this method should be called prior to calling the various get
     * methods to obtain information regarding the fit.
     */
    public void build() throws QSARModelException {
        Double[][] x;
        Double[][] y;
        x = (Double[][]) this.params.get("x");
        y = (Double[][]) this.params.get("y");
        if (x.length != y.length)
            throw new QSARModelException("Number of observations does not match number of rows in the design matrix");
        if (nvar == 0) nvar = x[0].length;

        // lets build the model
        String paramVarName = loadParametersIntoRSession();
        String cmd = "buildCNN(\"" + getModelName() + "\", " + paramVarName + ")";
        REXP ret = rengine.eval(cmd);
        if (ret == null) {
            CNNRegressionModel.logger.debug("Error in buildCNN");
            throw new QSARModelException("Error in buildCNN");
        }

        // remove the parameter list
        rengine.eval("rm(" + paramVarName + ")");

        // save the model object on the Java side
        modelObject = ret.asList();
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
        REXP ret = rengine.eval("predicCNN(\"" + getModelName() + "\", " + pn + ")");
        if (ret == null) throw new QSARModelException("Error occured in prediction");

        // remove the parameter list
        rengine.eval("rm(" + pn + ")");

        modelPredict = ret.asDoubleMatrix();
    }

    /**
     * Get the matrix of predicted values obtained from <code>predict.nnet<code>.
     *
     * @return The result of the prediction.
     */
    public double[][] getPredictions() {
        return modelPredict;
    }

    /**
     * Returns an <code>RList</code> object summarizing the nnet regression model.
     * <p/>
     * The return object can be queried via the <code>RList</code> methods to extract the
     * required components.
     *
     * @return A summary for the nnet regression model
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
     * Loads a <code>'nnet'</code> object from disk in to the current session.
     *
     * @param fileName The disk file containing the model
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the model being loaded is not a <code>'nnet'</code> model
     *          object  or the file does not exist
     */
    public void loadModel(String fileName) throws QSARModelException {
        File f = new File(fileName);
        if (!f.exists()) throw new QSARModelException(fileName + " does not exist");

        rengine.assign("tmpFileName", fileName);
        REXP ret = rengine.eval("loadModel(tmpFileName)");
        if (ret == null) throw new QSARModelException("Model could not be loaded");

        String name = ret.asList().at("name").asString();
        if (!isOfClass(name, "nnet")) {
            removeObject(name);
            throw new QSARModelException("Loaded object was not of class \'nnet\'");
        }

        modelObject = ret.asList().at("model").asList();
        setModelName(name);
        nvar = (int) getN()[0];
        noutput = (int) getN()[2];
    }

    /**
     * Loads a  <code>'nnet'</code> object from a serialized string into the current session.
     *
     * @param serializedModel A String containing the serialized version of the model
     * @param modelName       A String indicating the name of the model in the R session
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the model being loaded is not a <code>'nnet'</code> model
     *          object
     */
    public void loadModel(String serializedModel, String modelName) throws QSARModelException {
        rengine.assign("tmpSerializedModel", serializedModel);
        rengine.assign("tmpModelName", modelName);
        REXP ret = rengine.eval("unserializeModel(tmpSerializedModel, tmpModelName)");

        if (ret == null) throw new QSARModelException("Model could not be unserialized");

        String name = ret.asList().at("name").asString();
        if (!isOfClass(name, "nnet")) {
            removeObject(name);
            throw new QSARModelException("Loaded object was not of class \'nnet\'");
        }

        modelObject = ret.asList().at("model").asList();
        setModelName(name);
        nvar = (int) getN()[0];
        noutput = (int) getN()[2];
    }

// Autogenerated code: assumes that 'modelObject' is
// a RList object


    /**
     * Gets the <code>censored</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the censored field
     */
    public RBool getCensored() {
        return modelObject.at("censored").asBool();
    }

    /**
     * Gets the <code>conn</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the conn field
     */
    public double[] getConn() {
        return modelObject.at("conn").asDoubleArray();
    }

    /**
     * Gets the <code>decay</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the decay field
     */
    public double getDecay() {
        return modelObject.at("decay").asDouble();
    }

    /**
     * Gets the <code>entropy</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the entropy field
     */
    public RBool getEntropy() {
        return modelObject.at("entropy").asBool();
    }

    /**
     * Gets the <code>fitted.values</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the fitted.values field
     */
    public double[][] getFittedValues() {
        return modelObject.at("fitted.values").asDoubleMatrix();
    }

    /**
     * Gets the <code>n</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the n field
     */
    public double[] getN() {
        return modelObject.at("n").asDoubleArray();
    }

    /**
     * Gets the <code>nconn</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the nconn field
     */
    public double[] getNconn() {
        return modelObject.at("nconn").asDoubleArray();
    }

    /**
     * Gets the <code>nsunits</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the nsunits field
     */
    public double getNsunits() {
        return modelObject.at("nsunits").asDouble();
    }

    /**
     * Gets the <code>nunits</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the nunits field
     */
    public double getNunits() {
        return modelObject.at("nunits").asDouble();
    }

    /**
     * Gets the <code>residuals</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the residuals field
     */
    public double[][] getResiduals() {
        return modelObject.at("residuals").asDoubleMatrix();
    }

    /**
     * Gets the <code>softmax</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the softmax field
     */
    public RBool getSoftmax() {
        return modelObject.at("softmax").asBool();
    }

    /**
     * Gets the <code>value</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the value field
     */
    public double getValue() {
        return modelObject.at("value").asDouble();
    }

    /**
     * Gets the <code>wts</code> field of an <code>'nnet'</code> object.
     *
     * @return The value of the wts field
     */
    public double[] getWts() {
        return modelObject.at("wts").asDoubleArray();
    }


}
