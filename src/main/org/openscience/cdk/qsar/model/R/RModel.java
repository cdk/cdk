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

import org.omegahat.R.Java.REvaluator;
import org.omegahat.R.Java.ROmegahatInterpreter;
import org.openscience.cdk.qsar.model.IModel;
import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.tools.LoggingTool;

import java.io.*;

/** Base class for modeling classes that use R as the backend.
 *
 * This cannot be directly instantiated as its sole function is
 * to initialize the SJava system and source R matcher/converter
 * functions into the loaded R session. The class variable <code>revaluator</code>
 * can be accessed from subclasses to make calls to the R session.
 *
 * Any class that builds models using R should be a subclass of this.
 *
 * An important feature to note when using the R backend is that the SJava
 * initialization must be done only <b>once</b> in a Java thread. As a result
 * when any model class based on RModel is instantiated the constructor for the
 * super class (i.e., Rmodel) makes sure that SJava is not already initialized.
 * <p>
 * By default the intialization uses a temporary file which is sourced in the
 * R session. In some cases, such as web applications, temporary files might be 
 * problematic. In this case the R backend can be initialized via strings. To 
 * do this the application should specify <b>-DinitRFromString=true</b> on the command 
 * line. Note that this approach will be slightly slower compared to initializsation
 * via a temporary file.
 * <p>
 * <b>NOTE</b>: For the R backend to work, ensure that R is correctly installed
 * and that SJava is also installed, using the -c option. Finally, ensure 
 * that the R_HOME environment variable points to the R installation.
 *
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 * @deprecated 
 */
public abstract class RModel implements IModel {

    private String modelName = null;

    /**
     * The object that performs the calls to the R engine.
     */
    public static REvaluator revaluator = null;
    /**
     * This object represents an instance of the R interpreter.
     *
     * Due to the design of R, only one interpreter can be instantiated in a given 
     * thread. That is, the underlying R engine is not thread safe. As a result
     * care must be taken to have only one instance of the interpreter.
     */
    public static ROmegahatInterpreter interp = null;

    /**
     * A boolean that indicates whether the R/Java subsystem has been initialized or not.
     */
    private static boolean doneInit = false;
    private LoggingTool logger;
        
    private void loadRFunctions(REvaluator evaluator) {
        String scriptLocator = "org/openscience/cdk/qsar/model/data/cdkSJava.R";
        try {
            File scriptFile = File.createTempFile("XXXXX",".R");
            scriptFile.deleteOnExit();

            InputStreamReader reader = new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(scriptLocator));
            BufferedReader inFile = new BufferedReader(reader);

            FileWriter outFile = new FileWriter(scriptFile);
            BufferedWriter outBuffer = new BufferedWriter(outFile);
            String inputLine;
            while ( (inputLine = inFile.readLine()) != null) {
                outBuffer.write(inputLine,0,inputLine.length());
                outBuffer.newLine();
            }
            outBuffer.close();
            inFile.close();
            outFile.close();

            evaluator.voidEval("source(\""+scriptFile.getAbsolutePath()+"\")");

        } catch (Exception exception) {
            logger.error("Could not load CDK-SJava R script: ", scriptLocator);
            logger.debug(exception);
        }
    }
    
    private void loadRFunctionsAsStrings(REvaluator evaluator) {
        String[] scripts = {
            "init_1.R", 
            "lm_2.R", 
            "cnn_3.R", "cnn_4.R",
            "pls_5.R",
            "register_999.R"
        };
        String scriptPrefix = "org/openscience/cdk/qsar/model/data/";
        for (int i = 0; i < scripts.length; i++) {
            
            String scriptLocator = scriptPrefix + scripts[i];
            try {
                InputStreamReader reader = new InputStreamReader(
                        this.getClass().getClassLoader().getResourceAsStream(scriptLocator));
                BufferedReader inFile = new BufferedReader(reader);

                StringWriter sw = new StringWriter();
                String inputLine;
                while ( (inputLine = inFile.readLine()) != null) {
                    sw.write(inputLine);
                    sw.write("\n");
                }
                sw.close();

                evaluator.voidEval("eval(parse(text=\""+sw.toString()+"\"))");

            } catch (Exception exception) {
                logger.error("Could not load CDK-SJava R scripts: ", scriptLocator);
                logger.debug(exception);
            }
            
        }
    }


    /**
     * Initializes SJava and R with the specified command line arguments (see R documentation).
     *
     * This constructor will initialize the R session via a temporary file
     * 
     * @param args A String[] containing the command line parameters as elements
     */
    public RModel(String[] args) {
        logger = new LoggingTool(this);

        String initRFromString = System.getProperty("initRFromString");
        boolean useDisk = true;
        if (initRFromString != null && initRFromString.equals("true")) {
            useDisk = false;
        } 

        if (!doneInit) {
            RModel.interp = new ROmegahatInterpreter(ROmegahatInterpreter.fixArgs(args), false);
            RModel.revaluator = new REvaluator();

            if (useDisk) {
                loadRFunctions(RModel.revaluator);
                logger.info("Initializing from disk");
            } else {
                loadRFunctionsAsStrings(RModel.revaluator);
                logger.info("Initializing from strings");
            }
            
            doneInit = true;
            logger.info("SJava initialized");
        } else {
            logger.info("SJava already initialized");
        }
    }

    /**
     * Initializes SJava with the <i>--vanilla, -q, --slave</i> flags.
     *
     * This constructor will initialize the R session via a temporary file
     */
    public RModel() {
        String[] args = {"--vanilla","-q", "--slave"};
        logger = new LoggingTool(this);

        String initRFromString = System.getProperty("initRFromString");
        boolean useDisk = true;
        if (initRFromString != null && initRFromString.equals("true")) {
            useDisk = false;
        } 
        
        if (!doneInit) {
            RModel.interp = new ROmegahatInterpreter(ROmegahatInterpreter.fixArgs(args), false);
            RModel.revaluator = new REvaluator();

            if (useDisk) {
                loadRFunctions(RModel.revaluator);
                logger.info("Initializing from disk");
            } else {
                loadRFunctionsAsStrings(RModel.revaluator);
                logger.info("Initializing from strings");
            }
            
            doneInit = true;
            logger.info("SJava initialized");
        } else {
            logger.info("SJava already initialized");
        }
    }


    /**
     * Saves a R model to disk.
     *
     * This function can be used to save models built in a session, and then loaded
     * again in a different session. 
     *
     * @param modelname The name of the model as returned by \code{getModelName}.
     * @param filename The file to which the model should be saved
     * @throws QSARModelException if the R session cannot save the model
     * @see #loadModel
     */
    public static void saveModel(String modelname, String filename) throws QSARModelException {
        if (filename.equals("") || filename == null) {
            filename = modelname+".rda";
        }
        //Boolean result = null;
        try {
        revaluator.call("saveModel",
                new Object[] { (Object)modelname, (Object)filename });
        } catch (Exception e) {
            System.out.println("Caught the exception");
            throw new QSARModelException("Error saving model");
        }
    }


    /**
     * Get the name of the model.
     *
     * This function returns the name of the variable that the actual
     * model is stored in within the R session. In general this is 
     * not used for the end user. In the future this might be changed 
     * to a private method.
     *
     * @return A String containing the name of the R variable
     * @see #setModelName
     */
    public String getModelName() {
        return(this.modelName);
    }

    /**
     * Set the name of the model.
     *
     * Ordinarily the user does not need to call this function as each model
     * is assigned a unique ID at instantiation. However, if a user saves a model
     * to disk and then later loads it, the loaded
     * model may overwrite a model in that session. In this situation, this method
     * can be used to assign a name to the model.
     *
     * @param newName The name of the model
     * @see #getModelName
     * @see #saveModel
     * @see #loadModel
     *
     */
    public void setModelName(String newName) {
        if (this.modelName != null && this.modelName.equals(newName)) return;
        String oldName = this.modelName;
        if (oldName != null) {
            revaluator.voidEval("if ('"+oldName+"' %in% ls()) {"+newName+"<-"+oldName+";rm("+oldName+")}");
        }
        this.modelName = newName;
    }

    abstract public void build() throws QSARModelException;
    abstract public void predict() throws QSARModelException;

    /**
     * Specifies the parameters value.
     *
     * @param key A String representing the name of the parameter (corresponding to the 
     * name described in the R manpages)
     * @param obj The value of the parameter
     * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
     * 
     */
    abstract public void setParameters(String key, Object obj) throws QSARModelException;

    /**
     * Abstract method to handle loading R models.
     *
     * This method can be used to load a previously saved R model object. Since
     * the user can save any arbitrary R object, checks must be made that the 
     * object being returned is an instance of one of the current modeling classes.
     * <p>
     * This is best achieved by forcing each modeling class to write its own loader.
     *
     * @param fileName The file containing the R object to load
     * @throws QSARModelException if the R session could not load the object or if the loaded model
     * does not correspond to the class that it was loaded from
     * @see #saveModel
     */
    abstract public void loadModel(String fileName) throws QSARModelException;
    /** 
     * Abstract method to handle loading R models that were previously serialized.
     *
     * This method can be used to load a previously serialized R model object (usinging
     * serialize()). Since
     * the user can save any arbitrary R object, checks must be made that the 
     * object being returned is an instance of one of the current modeling classes. 
     * This is best achieved by forcing each modeling class to write its own loader.
     * <p>
     * In addition
     * objects saved using serialize() do not have a name. As a result a name for the object must
     * be specified when using this method.
     *
     * @param serializedModel A String containing the ASCII sreialized R object
     * @param modelName The name of the model. (Within the R session, the model will be assigned to
     * a variable of this name)
     * @throws QSARModelException if the R session could not load the object or if the loaded model
     * does not correspond to the class that it was loaded from
     * @see #saveModel
     */
    abstract public void  loadModel(String serializedModel, String modelName) throws QSARModelException;
}


