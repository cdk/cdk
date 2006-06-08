package org.openscience.cdk.qsar.model.R2;

import org.openscience.cdk.qsar.model.IModel;
import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.tools.LoggingTool;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import java.io.*;

/**
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 */
public abstract class RModel implements IModel {
    private String modelName = null;

    /**
     * The object that performs the calls to the R engine.
     */
    public static Rengine rengine = null;

    /**
     * A boolean that indicates whether the R/Java subsystem has been initialized or not.
     */
    private static boolean doneInit = false;
    private static LoggingTool logger;

    private void loadRFunctions(Rengine engine) {
        String scriptLocator = "org/openscience/cdk/qsar/model/data/helper.R";
        try {
            File scriptFile = File.createTempFile("XXXXX", ".R");
            scriptFile.deleteOnExit();

            InputStreamReader reader = new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(scriptLocator));
            BufferedReader inFile = new BufferedReader(reader);

            FileWriter outFile = new FileWriter(scriptFile);
            BufferedWriter outBuffer = new BufferedWriter(outFile);
            String inputLine;
            while ((inputLine = inFile.readLine()) != null) {
                outBuffer.write(inputLine, 0, inputLine.length());
                outBuffer.newLine();
            }
            outBuffer.close();
            inFile.close();
            outFile.close();

            engine.eval("source(\"" + scriptFile.getAbsolutePath() + "\")");

        } catch (Exception exception) {
            logger.error("Could not load helper R script for JRI: ", scriptLocator);
            logger.debug(exception);
        }
    }

    /**
     * Initializes SJava with the <i>--vanilla, -q, --slave</i> flags.
     * <p/>
     * This constructor will initialize the R session via a temporary file
     */
    public RModel() throws QSARModelException {
        String[] args = {"--vanilla", "-q", "--slave"};
        logger = new LoggingTool(this);

        String initRFromString = System.getProperty("initRFromString");
        boolean useDisk = true;
        if (initRFromString != null && initRFromString.equals("true")) {
            useDisk = false;
        }

        if (!doneInit) {
            rengine = new Rengine(args, false, null);
            if (!rengine.waitForR()) {
                throw new QSARModelException("Could not load rJava");
            }
            doneInit = true;
            if (useDisk) {
                loadRFunctions(rengine);
                logger.info("Initializing from disk");
            } else {
//                loadRFunctionsAsStrings(rengine);
                logger.info("Initializing from strings");
            }
            logger.info("rJava initialized");
        } else {
            logger.info("rjava already intialized");
        }
    }


    /**
     * Saves a R model to disk.
     * <p/>
     * This function can be used to save models built in a session, and then loaded
     * again in a different session.
     *
     * @param modelName The name of the model as returned by \code{getModelName}.
     * @param fileName  The file to which the model should be saved
     * @throws QSARModelException if the R session cannot save the model
     * @see #loadModel
     */
    public static void saveModel(String modelName, String fileName) throws QSARModelException {
        if (fileName.equals("") || fileName == null) {
            fileName = modelName + ".rda";
        }

        rengine.assign("tmpModelName", modelName);
        rengine.assign("tmpFileName", fileName);
        REXP result = rengine.eval("saveModel(tmpModelName, tmpFileName)");
        if (result == null) {
            logger.debug("Error in 'saveModel(tmpModelName, tmpFileName)'");
            throw new QSARModelException("Error saving model");
        }
    }


    /**
     * Get the name of the model.
     * <p/>
     * This function returns the name of the variable that the actual
     * model is stored in within the R session. In general this is
     * not used for the end user. In the future this might be changed
     * to a private method.
     *
     * @return A String containing the name of the R variable
     * @see #setModelName
     */
    public String getModelName() {
        return (this.modelName);
    }

    /**
     * Set the name of the model.
     * <p/>
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
     */
    public void setModelName(String newName) {
        if (this.modelName != null && this.modelName.equals(newName)) return;
        String oldName = this.modelName;
        if (oldName != null) {
            rengine.eval("if ('" + oldName + "' %in% ls()) {" + newName + "<-" + oldName + ";rm(" + oldName + ")}");
        }
        this.modelName = newName;
    }

    /**
     * Abstract method to handle loading R models.
     * <p/>
     * This method can be used to load a previously saved R model object. Since
     * the user can save any arbitrary R object, checks must be made that the
     * object being returned is an instance of one of the current modeling classes.
     * <p/>
     * This is best achieved by forcing each modeling class to write its own loader.
     *
     * @param fileName The file containing the R object to load
     * @throws org.openscience.cdk.qsar.model.QSARModelException
     *          if the R session could not load the object or if the loaded model
     *          does not correspond to the class that it was loaded from
     * @see #saveModel
     */
    abstract public void loadModel(String fileName) throws QSARModelException;

    /**
     * Abstract method to handle loading R models that were previously serialized.
     * <p/>
     * This method can be used to load a previously serialized R model object (usinging
     * serialize()). Since
     * the user can save any arbitrary R object, checks must be made that the
     * object being returned is an instance of one of the current modeling classes.
     * This is best achieved by forcing each modeling class to write its own loader.
     * <p/>
     * In addition
     * objects saved using serialize() do not have a name. As a result a name for the object must
     * be specified when using this method.
     *
     * @param serializedModel A String containing the ASCII sreialized R object
     * @param modelName       The name of the model. (Within the R session, the model will be assigned to
     *                        a variable of this name)
     * @throws QSARModelException if the R session could not load the object or if the loaded model
     *                            does not correspond to the class that it was loaded from
     * @see #saveModel
     */
    abstract public void loadModel(String serializedModel, String modelName) throws QSARModelException;


    abstract public void build() throws QSARModelException;

    abstract public void predict() throws QSARModelException;

    abstract protected void finalize();

}
