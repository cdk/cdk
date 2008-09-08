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
 * A modeling class that provides a PLS regression model.
 *
 * When instantiated this class ensures that the R/Java interface has been 
 * initialized. The response and independent variables can be specified at construction
 * time or via the <code>setParameters</code> method. 
 * The actual fitting procedure is carried out by <code>build</code>. 
 * <P><b>NOTE:</b> For this class to work, you must have the 
 * <a href="http://cran.r-project.org/src/contrib/Descriptions/pls.pcr.html" target="_top">pls.pcr</a>
 * package installed in your R library. 
 * <p>
 * When building the PLS model, parameters such as whether cross validation is to be used, the type of
 * PLS algorithm etc can be specified by making calls to <code>setParameters</code>. This method can also
 * be used to set a new X matrix for prediction.
 * The following table lists the parameters that can be set and their 
 * expected types. More detailed information is available in the R documentation.
 * <center>
 * <table border=1 cellpadding=5>
 * <THEAD>
 * <tr>
 * <th>Name</th><th>Java Type</th><th>Default</th><th>Notes</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>X</td><td>Double[][]</td><td>None</td><td>Variables should be in the columns, observations in the rows</td>
 * </tr>
 * <tr>
 * <td>Y</td><td>Double[][]</td><td>None</td><td>Length should be equal to the rows of X. Variables should be in the columns, observations in the rows</td>
 * </tr>
 * <tr>
 * <td>newX</td><td>Double[][]</td><td>None</td><td>A 2D array of values to make predictions for. Variables should be in the columns, observations in the rows</td>
 * </tr>
 * <tr>
 * <td>ncomp</td><td>Integer[]</td><td>{1,rank(X)}</td><td>This can be an array of length 1 or 2. If there is only one element 
 * then only the specified number of latent variables will be assessed during modeling. If 2 values are specified
 * then the model will use N1 to N2 latent variables where N1 and N2 are the first and second elements respectively</td>
 * </tr>
 * <tr>
 * <td>method</td><td>String</td><td>"SIMPLS"</td><td>The type of PLS algorithm to use (can be SIMPLS or kernelPLS)</td>
 * </tr>
 * <tr>
 * <td>validation</td><td>String</td><td>"none"</td><td>Indicates whether cross validation should be used. To enable cross validation set this to "CV"</td>
 * </tr>
 * <tr>
 * <td>grpsize</td><td>Integer</td><td>0</td><td>The group size for the "CV" validation. By default this is ignored and <code>niter</code> is used to determine the value of this argument</td>
 * </tr>
 * <tr>
 * <td>niter</td><td>Integer</td><td>10</td><td>The number of iterations in the cross-validation. Note that if <code>grpsize</code> is set to a non-zero value then the value of <code>niter</code> will be calculated from the value of <code>grpsize</code></td>
 * </tr>
 * <tr>
 * <td>nlv</td><td>Integer</td><td>None</td><td>The number of latent variables to use during prediction. By default this does not need to be specified and will be obtained from the fitted model</td>
 * </tr>
 * </tbody>
 * </table>
 * </center>
 * <p>
 * In general the <code>getFit*</code> methods provide access to results from the fit and 
 * <code>getPredict*</code> methods provide access to results from the prediction. In case validation is specified
 * then the results from the CV can be obtained via the <code>getValidation*</code> methods. 
 * The values returned correspond to the various 
 * values returned by the <a href="http://www.maths.lth.se/help/R/.R/library/pls.pcr/html/mvr.html" target="_top">pls</a> and 
 * <a href="http://www.maths.lth.se/help/R/.R/library/pls.pcr/html/mvr.html" target="_top">predict.mvr</a>
 * functions in R.  
 * <p>
 * See {@link RModel} for details regarding the R and SJava environment.
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 * 
 * @cdk.keyword partial least squares
 * @cdk.keyword PLS
 * @cdk.keyword regression
 * @deprecated 
 */
public class PLSRegressionModel extends RModel {

    private static int globalID = 0;
    private int currentID;
    private PLSRegressionModelFit modelfit = null;
    private PLSRegressionModelPredict modelpredict = null;

    private HashMap params = null;
    private int nvar = 0;
    
    private void setDefaults() {
        this.params.put("ncomp", new Boolean(false));
        this.params.put("method", "SIMPLS");
        this.params.put("validation", "none");
        this.params.put("grpsize", Integer.valueOf(0));
        this.params.put("niter", Integer.valueOf(10));
        this.params.put("nlv", new Boolean(false));
    }
    /**
     * Constructs a PLSRegressionModel object.
     *
     * The constructor simply instantiates the model ID. Dependent and independent variables
     * should be set via setParameters(). 
     */
    public PLSRegressionModel(){
        super();

        this.params = new HashMap();

        this.currentID = PLSRegressionModel.globalID;
        PLSRegressionModel.globalID++;
        this.setModelName("cdkPLSRegressionModel"+this.currentID);
        this.setDefaults();
    }

    /**
     * Constructs a PLSRegressionModel object.
     *
     * The constructor allows the user to specify the 
     * dependent and independent variables. The length of the dependent variable
     * array should equal the number of rows of the independent variable matrix. If this 
     * is not the case an exception will be thrown.
     *
     * @param xx An array of independent variables. The observations should be in the rows
     * and the variables should be in the columns
     * @param yy An array containing the dependent variable
     * @throws QSARModelException if the number of observations in x and y do not match
     */
    public PLSRegressionModel(double[][] xx, double[] yy) throws QSARModelException{
        super();

        this.params = new HashMap();

        this.currentID = PLSRegressionModel.globalID;
        PLSRegressionModel.globalID++;
        this.setModelName("cdkPLSRegressionModel"+this.currentID);
        this.setDefaults();

        int nrow = yy.length;
        this.nvar = xx[0].length;

        if (nrow != xx.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }
        
        Double[][] x = new Double[nrow][this.nvar];
        Double[][] y = new Double[nrow][1];

        for (int i = 0; i < nrow; i++) {
            y[i][1] = new Double(yy[i]);
            for (int j = 0; j < this.nvar; j++) 
                x[i][j] = new Double(xx[i][j]);
        }

        params.put("X", x);
        params.put("Y", y);
    }
        

    /**
     * Constructs a PLSRegressionModel object.
     *
     * The constructor allows the user to specify the 
     * dependent and independent variables. This constructor will accept a matrix
     * of Y values.
     * <p>
     * The length of the dependent variable
     * array should equal the number of rows of the independent variable matrix. If this 
     * is not the case an exception will be thrown.
     * 
     * @param xx An array of independent variables. The observations should be in the rows
     * and the variables should be in the columns
     * @param yy A 2D array containing the dependent variable
     * @throws QSARModelException if the number of observations in x and y do not match
     */
    public PLSRegressionModel(double[][] xx, double[][] yy) throws QSARModelException{
        super();

        this.params = new HashMap();

        this.currentID = PLSRegressionModel.globalID;
        PLSRegressionModel.globalID++;
        this.setModelName("cdkPLSRegressionModel"+this.currentID);
        this.setDefaults();

        int nrow = yy.length;
        int ncoly = yy[0].length;
        this.nvar = xx[0].length;

        if (nrow != xx.length) {
            throw new QSARModelException("The number of values for the dependent variable does not match the number of rows of the design matrix");
        }
        
        Double[][] x = new Double[nrow][this.nvar];
        Double[][] y = new Double[nrow][ncoly];
        //Double[] wts = new Double[nrow];

        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncoly; j++) {
                y[i][j] = new Double(yy[i][j]);
            }
        }
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < this.nvar; j++) 
                x[i][j] = new Double(xx[i][j]);
        }
        params.put("X", x);
        params.put("Y", y);
    }

    protected void finalize() {
        revaluator.voidEval("rm("+this.getModelName()+",pos=1)");
    }
        
    

    /**
     * Fits a PLS model.
     *
     * This method calls the R function to fit a PLS model
     * using the specified dependent and independent variables. If an error
     * occurs in the R session, an exception is thrown.
     */
    public void build() throws QSARModelException {
        // lets do some checks in case stuff was set via setParameters()
        Double[][] x,y;
        x = (Double[][])this.params.get("X");
        y = (Double[][])this.params.get("Y");
        if (this.nvar == 0) this.nvar = x[0].length;
        else {
            if (y.length != x.length) {
                throw new QSARModelException("Number of observations does no match number of rows in the design matrix");
            }
        }

        // lets build the model
        try {
            this.modelfit = (PLSRegressionModelFit)revaluator.call("buildPLS", 
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
     * to set the values of the independent variable for the new observations.
     */
    public void predict() throws QSARModelException {
        if (this.modelfit == null) 
            throw new QSARModelException("Before calling predict() you must fit the model using build()");

        Double[][] newx = (Double[][])this.params.get(new String("newX"));
        if (newx[0].length != this.nvar) {
            throw new QSARModelException("Number of independent variables used for prediction must match those used for fitting");
        }
            
        try {
            this.modelpredict = (PLSRegressionModelPredict)revaluator.call("predictPLS",
                    new Object[]{ getModelName(), this.params });
        } catch (Exception re) {
            throw new QSARModelException(re.toString());
        }
    }

    /**
     * Loads a PLSRegressionModel object from disk in to the current session.
     *
     * @param fileName The disk file containing the model
     * @throws QSARModelException if the model being loaded is not a PLS regression model
     * object
     */
    public void  loadModel(String fileName) throws QSARModelException {
        // should probably check that the filename does exist
        Object model = (Object)revaluator.call("loadModel", new Object[]{ (Object)fileName });
        String modelName = (String)revaluator.call("loadModel.getName", new Object[] { (Object)fileName });

        if (model.getClass().getName().equals("org.openscience.cdk.qsar.model.R.PLSRegressionModelFit")) {
            this.modelfit = (PLSRegressionModelFit)model;
            this.setModelName(modelName);
        } else throw new QSARModelException("The loaded model was not a PLSRegressionModel");
    }
    /**
     * Loads an PLSRegressionModel object from a serialized string into the current session.
     *
     * @param serializedModel A String containing the serialized version of the model
     * @param modelName A String indicating the name of the model in the R session
     * @throws QSARModelException if the model being loaded is not a PLS regression model
     * object
     */
    public void  loadModel(String serializedModel, String modelName) throws QSARModelException {
        // should probably check that the fileName does exist
        Object model = (Object)revaluator.call("unserializeModel", new Object[]{ (Object)serializedModel, (Object)modelName });
        String modelname = modelName;

        if (model.getClass().getName().equals("org.openscience.cdk.qsar.model.R.PLSRegressionModelFit")) {
            this.modelfit =(PLSRegressionModelFit)model;
            this.setModelName(modelname);
        } else throw new QSARModelException("The loaded model was not a PLSRegressionModel");
    }



    /**
     * Sets parameters required for building a PLS model or using one for prediction.
     *
     * This function allows the caller to set the various parameters available
     * for the pls()  and predict.mvr() R routines. See the R help pages for the details of the available
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

        if (key.equals("Y")) {
            if (!(obj instanceof Double[])) {
                throw new QSARModelException("The class of the 'Y' object must be Double[][]");
            }
        }
        if (key.equals("X")) {
            if (!(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'X' object must be Double[][]");
            }
        }
        if (key.equals("method")) {
            if (!(obj instanceof String)) {
                throw new QSARModelException("The class of the 'method' object must be String");
            }
            if (!(obj.equals("SIMPLS") || obj.equals("kernelPLS"))) {
                throw new QSARModelException("The value of method must be: SIMPLS or kernelPLS ");
            }
        }
        if (key.equals("validation")) {
            if (!(obj instanceof String)) {
                throw new QSARModelException("The class of the 'validation' object must be String");
            }
            if (!(obj.equals("none") || obj.equals("CV"))) {
                throw new QSARModelException("The value of validation must be: none or CV");
            }
        }
        
        if (key.equals("newX")) {
            if ( !(obj instanceof Double[][])) {
                throw new QSARModelException("The class of the 'newX' object must be Double[][]");
            }
        }
        if (key.equals("grpsize")) {
            if (!(obj instanceof Integer)) {
                throw new QSARModelException("The class of the 'grpsize' object must be Integer");
            }
        }
        if (key.equals("niter")) {
            if (!(obj instanceof Integer)) {
                throw new QSARModelException("The class of the 'niter' object must be Integer");
            }
        }
        if (key.equals("nlv")) {
            if (!(obj instanceof Integer)) {
                throw new QSARModelException("The class of the 'nlv' object must be Integer");
            }
        }
        
        if (key.equals("ncomp")) {
            if (!(obj instanceof Integer[])) {
                throw new QSARModelException("The class of the 'ncomp' object must be Integer[]");
            }
            Integer[] tmp = (Integer[])obj;
            if (tmp.length != 1 && tmp.length != 2) {
                throw new QSARModelException("The 'ncomp' array can have a length of 1 or 2. See documentation");
            }
        }
        
        this.params.put(key,obj);
    }
       
        

    /* interface to fit object */

    /**
     * The method used to build the PLS model.
     *
     * @return String containing 'SIMPLS' or 'kernelPLS'
     */
    public String getFitMethod() {
        return(this.modelfit.getMethod());
    }


    /**
     * Returns the fit NComp value.
     *
     * @return An array of integers indicating the number of components
     * (latent variables)
     */
    public int[] getFitNComp() {
        return(this.modelfit.getNComp());
    }

    /**
     * Gets the coefficents.
     *
     * The return value is a 3D array. The first dimension corresponds
     * to the specific number of LV's (1 or 2 or 3 and so on). The second
     * dimension corresponds to the independent variables and the third
     * dimension corresponds to the Y variables.
     *
     * @return double[][][] containing the coefficients
     */
    public double[][][] getFitB() {
        return(this.modelfit.getB());
    }
    
    /**
     * Get the Root Mean Square (RMS) error for the fit.
     *
     * @return A 2-dimensional array of RMS errors.
     */
    public double[][] getFitRMS() {
        return(this.modelfit.getTrainingRMS());
    }

    /**
     * Get the predicted Y's.
     *
     * Each set of latent variables is used to make predictions for all the
     * Y variables.
     *
     * @return A 3-dimensional array of doubles. The first dimension corresponds
     * to the set of latent variables and the remaining two correspond to the 
     * Y's themselves.
     */
    public double[][][] getFitYPred() {
        return(this.modelfit.getTrainingYPred());
    }

    /**
     * Get the X loadings.
     *
     * @return A 2-dimensional array of doubles containing the X loadings
     */
    public double[][] getFitXLoading() {
        return(this.modelfit.getXLoading());
    }
    /**
     * Get the Y loadings.
     *
     * @return A 2-dimensional array of doubles containing the Y loadings
     */
    public double[][] getFitYLoading() {
        return(this.modelfit.getYLoading());
    }
    /**
     * Get the X scores.
     *
     * @return A 2-dimensional array of doubles containing the X scores
     */
    public double[][] getFitXScores() {
        return(this.modelfit.getXScores());
    }
    /**
     * Get the Y scores.
     *
     * @return A 2-dimensional array of doubles containing the Y scores
     */
    public double[][] getFitYScores() {
        return(this.modelfit.getYScores());
    }
    /**
     * Indicates whether CV was used to build the model.
     *
     * @return A boolean indicating whether CV was used
     */
    public boolean getFitWasValidated() {
        return(this.modelfit.wasValidated());
    }


    /**
     * The number of iterations used during CV.
     *
     * @return An int value indicating the number of iterations in CV
     */
    public int getValidationIter() {
        return(this.modelfit.getValidationIter());
    }
    /**
     * The number of latent variables suggested by CV.
     *
     * @return An int value indicating the number of LV's
     */
    public int getValidationLV() {
        return(this.modelfit.getValidationLV());
    }

    /**
     * Get the R^2 value for validation.
     *
     * @return A 2-dimensional array of doubles
     */
    public double[][] getValidationR2() {
        return(this.modelfit.getValidationR2());
    }
    /**
     * Get the RMS value for validation.
     *
     * @return A 2-dimensional array of doubles
     */
    public double[][] getValidationRMS() {
        return(this.modelfit.getValidationRMS());
    }
    /**
     * Get the standard deviation of the RMS errrors for validation.
     *
     * @return A 2-dimensional array of doubles
     */
    public double[][] getValidationRMSsd() {
        return(this.modelfit.getValidationRMSSD());
    }
    /**
     * Get the predicted Y values from validation.
     *
     * @return A 2-dimensional array of doubles
     */
    public double[][][] getValidationYPred() {
        return(this.modelfit.getValidationYPred());
    }
    



    /* interface to predict object */

    /**
     * Returns the predicted values for the prediction set. 
     *
     * This function only returns meaningful results if the <code>predict</code>
     * method of this class has been called.
     *
     * @return A double[][] containing the predicted values
     */
    public  double[][] getPredictPredicted() {
        return(this.modelpredict.getPredictions());
    }
}
