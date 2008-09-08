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

import org.openscience.cdk.qsar.model.QSARModelException;

import java.util.HashMap;

/** 
 * A modeling class that provides a computational neural network regression model.
 *
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
 *     
 *     double fitValue = cnnrm.getFitValue();
 *     
 *     cnnrm.setParameters("newdata", newx);
 *     cnnrm.setParameters("type", "raw");
 *     cnnrm.predict();
 *
 *     double[][] preds = cnnrm.getPredictPredicted();
 * } catch (QSARModelException qme) {
 *     System.out.println(qme.toString());
 * }
 * </pre>
 * The above code snippet builds a 3-3-1 CNN model.
 * Multiple output neurons are easily
 * specified by supplying a matrix for y (i.e., double[][]) with the output variables
 * in the columns. 
 * <p>
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
 * <p>
 * In general the <code>getFit*</code> methods provide access to results from the fit
 * and <code>getPredict*</code> methods provide access to results from the prediction (i.e.,
 * prediction using the model on new data). The values returned correspond to the various 
 * values returned by the <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a> and
 * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/predict.nnet.html" target="_top">predict.nnet</a> functions
 * in R
 * <p>
 * See {@link RModel} for details regarding the R and SJava environment.
 * 
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 * 
 * @cdk.keyword neural network
 * @cdk.keyword regression
 * @deprecated
 */
public class CNNRegressionModel extends RModel {
    
    public static int globalID = 0;
    private int currentID;
    private CNNRegressionModelFit modelfit = null;
    private CNNRegressionModelPredict modelpredict = null;

    private HashMap params = null;
    private int noutput = 0;
    private int nvar = 0;

    private void setDefaults() {
        // lets set the default values of the arguments that are specified
        // to have default values in ?nnet

        // these params are vectors that depend on user defined stuff
        // so as a default we set them to FALSE so R can check if these
        // were not set
        this.params.put("subset", new Boolean(false));
        this.params.put("mask", new Boolean(false) );
        this.params.put("Wts", new Boolean(false));
        this.params.put("weights", new Boolean(false));

        this.params.put("linout", new Boolean(true)); // we want only regression
        this.params.put("entropy", new Boolean(false));
        this.params.put("softmax",new Boolean(false));
        this.params.put("censored", new Boolean(false));
        this.params.put("skip", new Boolean(false));
        this.params.put("rang", new Double(0.7));
        this.params.put("decay", new Double(0.0));
        this.params.put("maxit", Integer.valueOf(100));
        this.params.put("Hess", new Boolean(false));
        this.params.put("trace", new Boolean(false)); // no need to see output
        this.params.put("MaxNWts", Integer.valueOf(1000));
        this.params.put("abstol", new Double(1.0e-4));
        this.params.put("reltol", new Double(1.0e-8));
    }        

    /**
     * Constructs a CNNRegressionModel object.
     *
     * This constructor allows the user to simply set up an instance of a CNN
     * regression modeling class. This constructor simply sets the name for this
     * instance. It is expected all the relevent parameters for modeling will be
     * set at a later point.
     * <p>
     * Other parameters that are required to be set should be done via
     * calls to <code>setParameters</code>. A number of parameters are set to the
     * defaults as specified in the manpage for 
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a>.
     *
     */
    public CNNRegressionModel() {
        super();
        this.params = new HashMap();
        this.currentID = CNNClassificationModel.globalID;
        CNNClassificationModel.globalID++;
        this.setModelName("cdkCNNModel"+this.currentID);
        this.setDefaults();
    }

    /**
     * Constructs a CNNRegressionModel object.
     *
     * This constructor allows the user to specify the dependent and
     * independent variables along with the number of hidden layer neurons.
     * This constructor is suitable for cases when there is a single output 
     * neuron. If the number of rows of the design matrix is not equal to 
     * the number of observations in y an exception will be thrown.
     * <p>
     * Other parameters that are required to be set should be done via
     * calls to <code>setParameters</code>. A number of parameters are set to the
     * defaults as specified in the manpage for 
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a>.
     *
     * @param x An array of independent variables. Observations should be in
     * the rows and variables in the columns.
     * @param y An array (single column) of observed values
     * @param size The number of hidden layer neurons
     * @throws QSARModelException if the number of observations in x and y do not match
     */
    public CNNRegressionModel(double[][] x, double[] y, int size) throws QSARModelException {
        super();
        this.params = new HashMap();
        this.currentID = CNNRegressionModel.globalID;
        CNNRegressionModel.globalID++;
        this.setModelName("cdkCNNModel"+this.currentID);

        int nrow = y.length;
        int ncol = x[0].length;
        
        if (nrow != x.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }

        this.nvar = ncol;
        this.noutput = 1;
        
        Double[][] xx = new Double[nrow][ncol];
        Double[][] yy = new Double[nrow][1];

        for (int i = 0; i < nrow; i++) {
            yy[i][0] = new Double(y[i]);
            for (int j = 0; j < ncol; j++) {
                xx[i][j] = new Double(x[i][j]);
            }
        }
        this.params.put("x", xx);
        this.params.put("y", yy);
        this.params.put("size", Integer.valueOf(size));
        this.setDefaults();
    }
    
    /**
     * Constructs a CNNRegressionModel object.
     *
     * This constructor allows the user to specify the dependent and
     * independent variables along with the number of hidden layer neurons.
     * This constructor is suitable for cases when there are multiple output 
     * neuron. If the number of rows of the design matrix is not equal to 
     * the number of observations in y an exception will be thrown.
     * <p>
     * Other parameters that are required to be set should be done via
     * calls to <code>setParameters</code>. A number of parameters are set to the
     * defaults as specified in the manpage for 
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a>.
     *
     * @param x An array of independent variables. Observations should be in
     * the rows and variables in the columns.
     * @param y An array (multiple columns) of observed values
     * @param size The number of hidden layer neurons
     * @throws QSARModelException if the number of observations in x and y do not match
     */
    public CNNRegressionModel(double[][] x, double[][] y, int size) throws QSARModelException{
        super();
        this.params = new HashMap();
        this.currentID = CNNRegressionModel.globalID;
        CNNRegressionModel.globalID++;
        this.setModelName("cdkCNNModel"+this.currentID);

        int nrow = y.length;
        int ncol = x[0].length;
        
        if (nrow != x.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }

        this.nvar = ncol;
        this.noutput = y[0].length;
        
        Double[][] xx = new Double[nrow][ncol];
        Double[][] yy = new Double[nrow][this.noutput];

        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) {
                xx[i][j] = new Double(x[i][j]);
            }
        }
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < this.noutput; j++) {
                yy[i][j] = new Double(y[i][j]);
            }
        }
        this.params.put("x", xx);
        this.params.put("y", yy);
        this.params.put("size", Integer.valueOf(size));
        this.setDefaults();
    }

    
    /**
     * Sets parameters required for building a linear model or using one for prediction.
     *
     * This function allows the caller to set the various parameters available
     * for the 
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">nnet</a>
     * and 
     * <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/predict.nnet.html" target="_top">predict.nnet</a>
     * R routines. See the R help pages for the details of the available
     * parameters.
     * 
     * @param key A String containing the name of the parameter as described in the 
     * R help pages
     * @param obj An Object containing the value of the parameter
     * @throws QSARModelException if the type of the supplied value does not match the 
     * expected type
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
                noutput = ((Double[][])obj)[0].length;
            }
        }
        if (key.equals("x")) {
            if (!(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'x' object must be Double[][]");
            } else { 
                nvar = ((Double[][])obj)[0].length; 
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
            if ( !(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'newdata' object must be Double[][]");
            }
        }
        this.params.put(key,obj);
    }

    /**
     * Fits a CNN regression model.
     *
     * This method calls the R function to fit a CNN regression model
     * to the specified dependent and independent variables. If an error
     * occurs in the R session, an exception is thrown.
     * <p>
     * Note that, this method should be called prior to calling the various get
     * methods to obtain information regarding the fit.
     */
    public void build() throws QSARModelException {
        try {
            this.modelfit = (CNNRegressionModelFit)revaluator.call("buildCNN", 
                    new Object[]{ getModelName(), this.params });
        } catch (Exception re) {
            throw new QSARModelException(re.toString());
        }
    }

    /**
     * Uses a fitted model to predict the response for new observations.
     *
     * This function uses a previously fitted model to obtain predicted values
     * for a new set of observations. If the model has not been fitted prior to this
     * call an exception will be thrown. Use <code>setParameters</code>
     * to set the values of the independent variable for the new observations. You can also
     * set the <code>type</code> argument (see <a href="http://www.maths.lth.se/help/R/.R/library/nnet/html/nnet.html" target="_top">here</a>). 
     * However, since this class performs CNN regression, the default setting (<code>type='raw'</code>) is sufficient.
     */
    public void predict() throws QSARModelException {
        if (this.modelfit == null) 
            throw new QSARModelException("Before calling predict() you must fit the model using build()");

        Double[][] newx = (Double[][])this.params.get("newdata");
        if (newx[0].length != this.nvar) {
            throw new QSARModelException("Number of independent variables used for prediction must match those used for fitting");
        }
            
        try {
            this.modelpredict = (CNNRegressionModelPredict)revaluator.call("predictCNN",
                    new Object[]{ getModelName(), this.params });
        } catch (Exception re) {
            throw new QSARModelException(re.toString());
        }
    }
    
    /**
     * Returns an object summarizing the CNN regression model.
     *
     * The return object simply wraps the fields from the summary.nnet
     * return value. Various details can be extracted from the return object,
     * See {@link CNNRegressionModelSummary} for more details.
     *
     * @return A summary for the CNN regression model
     * @throws QSARModelException if the model has not been built prior to a call
     * to this method.
     */
    public CNNRegressionModelSummary summary() throws QSARModelException {
        if (this.modelfit == null) 
            throw new QSARModelException("Before calling summary() you must fit the model using build()");

        CNNRegressionModelSummary s = null;
        try {
            s = (CNNRegressionModelSummary)revaluator.call("summaryModel",
                    new Object[]{ getModelName() });
        } catch (Exception re) {
            throw new QSARModelException(re.toString());
        }
        return(s);
    }
        
    /**
     * Loads a CNNRegresionModel object from disk in to the current session.
     * 
     *
     * @param fileName The disk file containing the model
     * @throws QSARModelException if the model being loaded is not a CNN regression model
     * object
     */
    public void loadModel(String fileName) throws QSARModelException {
        // should probably check that the filename does exist
        Object model = (Object)revaluator.call("loadModel", new Object[]{ (Object)fileName });
        String modelName = (String)revaluator.call("loadModel.getName", new Object[] { (Object)fileName });

        if (model.getClass().getName().equals("org.openscience.cdk.qsar.model.R.CNNRegressionModelFit")) {
            this.modelfit = (CNNRegressionModelFit)model;
            this.setModelName(modelName);
            Integer tmp = (Integer)revaluator.eval(modelName+"$n[1]");
            nvar = tmp.intValue();
        } else throw new QSARModelException("The loaded model was not a CNNRegressionModel");
    }
    /**
     * Loads an CNNRegressionModel object from a serialized string into the current session.
     *
     * @param serializedModel A String containing the serialized version of the model
     * @param modelName A String indicating the name of the model in the R session
     * @throws QSARModelException if the model being loaded is not a CNN regression model
     * object
     */
    public void  loadModel(String serializedModel, String modelName) throws QSARModelException {
        // should probably check that the fileName does exist
        Object model = (Object)revaluator.call("unserializeModel", new Object[]{ (Object)serializedModel, (Object)modelName });
        String modelname = modelName;

        if (model.getClass().getName().equals("org.openscience.cdk.qsar.model.R.CNNRegressionModelFit")) {
            this.modelfit =(CNNRegressionModelFit)model;
            this.setModelName(modelname);
            Double tmp = (Double)revaluator.eval(modelName+"$n[1]");
            nvar = (int)tmp.doubleValue();
        } else throw new QSARModelException("The loaded model was not a CNNRegressionModel");
    }

    /**
     * Gets final value of the fitting criteria.
     *
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * 
     * @return  A double indicating the  value of the fitting criterion plus weight decay term.
     */
    public double getFitValue() {
        return(this.modelfit.getValue());
    }

    /**
     * Gets optimized weights for the model.
     *
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * 
     * @return  A double[] containing the weights. The number of weights will be
     * equal to <center>(Ni * Nh) + (Nh * No) + Nh + No</center> where Ni, Nh and No
     * are the number of input, hidden and output neurons.
     */
    public double[] getFitWeights() {
        return(this.modelfit.getWeights());
    }
    /**
     * Gets fitted values from the final model.
     *
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * 
     * @return  A double[][] containing the fitted values for each output neuron
     * in the columns. Note that even if a single output neuron was specified during
     * model building the return value is still a 2D array (with a single column).
     */
    public double[][] getFitFitted() {
        return(this.modelfit.getFitted());
    }
    /**
     * Gets residuals for the fitted values from the final model.
     *
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * 
     * @return  A double[][] containing the residuals for each output neuron
     * in the columns. Note that even if a single output neuron was specified during
     * model building the return value is still a 2D array (with a single column).
     */
    public double[][] getFitResiduals() {
        return(this.modelfit.getResiduals());
    }
    /**
     * Gets the Hessian of the measure of fit.
     *
     * If the <code>Hess</code> option was set to TRUE before the call to build
     * then the CNN routine will return the Hessian of the measure of fit at the best set of
     * weights found.  * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * 
     * @return  A double[][] containing the Hessian. It will be a square array
     * with dimensions equal to the Nwt x Nwt, where Nwt is the total number of weights
     * in the CNN model.
     */
    public double[][] getFitHessian() {
        return(this.modelfit.getHessian());
    }

    /**
     * Gets predicted values for new data using a previously built model.
     *
     * This method only returns meaningful results if the <code>build</code>
     * method of this class has been previously called.
     * 
     * @return  A double[][] containing the predicted for each output neuron
     * in the columns. Note that even if a single output neuron was specified during
     * model building the return value is still a 2D array (with a single column).
     */
    public double[][] getPredictPredicted() {
        return(this.modelpredict.getPredicted());
    }


}
