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
import org.openscience.cdk.qsar.model.R.CNNRegressionModelFit;
import org.openscience.cdk.qsar.model.R.CNNRegressionModelPredict;

import org.omegahat.R.Java.REvaluator;
import org.omegahat.R.Java.ROmegahatInterpreter;

import java.util.HashMap;

/** 
 * A modeling class that provides a computational neural network regression model.
 * <p>
 * See {@link RModel} for details regarding the R and SJava environment.
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 */

public class CNNRegressionModel extends RModel {
    
    private static int globalID = 0;
    private int currentID;
    private CNNRegressionModelFit modelfit = null;
    private CNNRegressionModelPredict modelpredict = null;

    private HashMap params = null;
    private int noutput = 0;
    private int nvar = 0;

    private void setDefaults() {
        // lets set the default values of the arguments that are specified
        // to have default values in ?nnet
        this.params.put("subset", new Integer(0));
        this.params.put("mask", new Boolean(false) );
        this.params.put("Wts", new Double(0));

        this.params.put("linout", new Boolean(true)); // we want only regression
        this.params.put("entropy", new Boolean(false));
        this.params.put("softmax",new Boolean(false));
        this.params.put("censored", new Boolean(false));
        this.params.put("skip", new Boolean(false));
        this.params.put("rang", new Double(0.7));
        this.params.put("decay", new Double(0.0));
        this.params.put("maxit", new Integer(100));
        this.params.put("Hess", new Boolean(false));
        this.params.put("trace", new Boolean(false)); // no need to see output
        this.params.put("MaxNWts", new Integer(1000));
        this.params.put("abstol", new Double(1.0e-4));
        this.params.put("reltol", new Double(1.0e-8));
    }        

    public CNNRegressionModel() {
        super();
        this.params = new HashMap();
        this.currentID = this.globalID;
        this.globalID++;
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
     * calls to setParameters(). A number of parameters are set to the
     * defaults as specified in the manpage for nnet.
     *
     * @param x An array of independent variables. Observations should be in
     * the rows and variables in the columns.
     * @param y An array (single column) of observed values
     * @param size The number of hidden layer neurons
     */
    public CNNRegressionModel(double[][] x, double[] y, int size) throws QSARModelException {
        super();
        this.params = new HashMap();
        this.currentID = this.globalID;
        this.globalID++;

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
            yy[i][1] = new Double(y[i]);
            for (int j = 0; j < ncol; j++) {
                xx[i][j] = new Double(x[i][j]);
            }
        }
        this.params.put("x", xx);
        this.params.put("y", yy);
        this.params.put("size", size);
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
     * calls to setParameters(). A number of parameters are set to the
     * defaults as specified in the manpage for nnet.
     *
     * @param x An array of independent variables. Observations should be in
     * the rows and variables in the columns.
     * @param y An array (multiple columns) of observed values
     * @param size The number of hidden layer neurons
     */
    public CNNRegressionModel(double[][] x, double[][] y, int size) throws QSARModelException{
        super();
        this.params = new HashMap();
        this.currentID = this.globalID;
        this.globalID++;

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
        this.params.put("size", size);
        this.setDefaults();
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
        return("cdkCNNModel"+currentID);
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
            if (!(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'y' object must be Double[][]");
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
     * to set the values of the independent variable for the new observations and the
     * interval type.
     */
    public void predict() throws QSARModelException {};
}
