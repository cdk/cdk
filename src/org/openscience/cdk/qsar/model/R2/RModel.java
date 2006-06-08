package org.openscience.cdk.qsar.model.R2;

import org.openscience.cdk.qsar.model.IModel;
import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.tools.LoggingTool;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * <b>NOTE</b>: For the R backend to work, ensure that R is correctly installed
 * and that rJava is also installed. Other requirements are
 * <ul>
 * <li>LD_LIBRARY_PATH should include the directory that contains libjri.so  as well
 * as the dierctory that contains libR.so
 * <li>R_HOME should be set to the appropriate location
 * </ul>
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 */
public abstract class RModel implements IModel {
    private String modelName = null;

    protected HashMap params = null;


    /**
     * The object that performs the calls to the R engine.
     */
    protected static Rengine rengine = null;

    /**
     * A boolean that indicates whether the R/Java subsystem has been initialized or not.
     */
    private static boolean doneInit = false;
    private static LoggingTool logger;

    private void initRengine() throws QSARModelException {
        String[] args = {"--vanilla", "--quiet", "--slave"};
        boolean useDisk = false;
        initRengine(args, useDisk);
    }

    private void initRengine(String[] args, boolean useDisk) throws QSARModelException {
        if (!doneInit) {
            rengine = new Rengine(args, false, new TextConsole());
            if (!rengine.waitForR()) {
                throw new QSARModelException("Could not load rJava");
            } else {
                logger.debug("Started R");
            }
            doneInit = true;
            if (useDisk) {
                loadRFunctions(rengine);
                logger.info("Initializing from disk");
            } else {
                loadRFunctionsAsStrings(rengine);
                logger.info("Initializing from strings");
            }
            logger.info("rJava initialized");
        } else {
            logger.info("rjava already intialized");
        }
    }

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

    private void loadRFunctionsAsStrings(Rengine evaluator) {
        String[] scripts = {
                "helper.R",
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
                while ((inputLine = inFile.readLine()) != null) {
                    sw.write(inputLine);
                    sw.write("\n");
                }
                sw.close();

                evaluator.eval("eval(parse(text=\"" + sw.toString() + "\"))");

            } catch (Exception exception) {
                logger.error("Could not load CDK-rJava R scripts: ", scriptLocator);
                logger.debug(exception);
            }

        }
    }

    /**
     * Initializes SJava with the <i>--vanilla, --quiet, --slave</i> flags.
     * <p/>
     * This constructor will initialize the R session via a temporary file  or
     * from a String depending on whether the symbol <code>initRFromString</code>
     * is specified on the command line
     */
    public RModel() throws QSARModelException {

        // check that the JRI jar and .so match
        if (!Rengine.versionCheck()) {
            logger.debug("API version of the JRI library does not match that of the native binary");
            throw new QSARModelException("API version of the JRI library does not match that of the native binary");
        }


        params = new HashMap();
        String[] args = {"--vanilla", "--quiet", "--slave"};
        logger = new LoggingTool(this);

        String initRFromString = System.getProperty("initRFromString");
        boolean useDisk = true;
        if (initRFromString != null && initRFromString.equals("true")) {
            useDisk = false;
        }
        initRengine(args, useDisk);
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
        if (fileName == null || fileName.equals("")) {
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
     * Get the instance of the <code>Rengine</code>.
     * <p/>
     * In case the R engine has not been initialized, it is initialized before
     * returning the object.
     *
     * @return The Rengine object
     * @throws QSARModelException if initialization fails.
     */
    public Rengine getRengine() throws QSARModelException {
        if (rengine == null) initRengine();
        return rengine;
    }

    /**
     * Get a unique String value.
     * <p/>
     * This method can be used to get unique variable names for use in an R session. The
     * String is generated from a combination of the prefix, the system time and a random
     * portion.
     *
     * @param prefix Any value. If empty or null, <code>"var"</code> is used.
     * @return A unique String value
     */
    public String getUniqueVariableName(String prefix) {
        if (prefix == null || prefix.equals("")) prefix = "var";
        Random rnd = new Random();
        long uid = ((System.currentTimeMillis() >>> 16) << 16) + rnd.nextLong();
        return prefix + uid;
    }

    /**
     * Loads the parameters for a model into a <code>list</code> object in the R session.
     * <p/>
     * The method assigns the list to a (relatively) unique variable name and returns
     * the variable name to the caller so that the list can be accessed later on.
     *
     * @return
     * @throws QSARModelException if there are any problems within the R session.
     */
    protected String loadParametersIntoRSession() throws QSARModelException {
        REXP result;
        Set keys = params.keySet();
        String paramVariableName = getUniqueVariableName("param");

        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            Object value = params.get(name);

            if (value instanceof Integer) {
                Integer tmp1 = (Integer) value;
                int[] tmp2 = new int[]{tmp1.intValue()};
                rengine.assign(name, tmp2);
            } else if (value instanceof String) {
                rengine.assign(name, (String) value);
            } else if (value instanceof Boolean) {
                Boolean tmp1 = (Boolean) value;
                if (tmp1.booleanValue()) result = rengine.eval(name + "<- TRUE");
                else result = rengine.eval(name + "<- FALSE");
                if (result == null) throw new QSARModelException("Error assigning a boolean");
            } else if (value instanceof Double) {
                Double tmp1 = (Double) value;
                double[] tmp2 = new double[]{tmp1.doubleValue()};
                rengine.assign(name, tmp2);
            } else if (value instanceof Integer[]) {
                Integer[] tmp1 = (Integer[]) value;
                int[] tmp2 = new int[tmp1.length];
                for (int i = 0; i < tmp1.length; i++) tmp2[i] = tmp1[i].intValue();
                rengine.assign(name, tmp2);
            } else if (value instanceof Double[]) {
                Double[] tmp1 = (Double[]) value;
                double[] tmp2 = new double[tmp1.length];
                for (int i = 0; i < tmp1.length; i++) tmp2[i] = tmp1[i].intValue();
                rengine.assign(name, tmp2);
            } else if (value instanceof Integer[][]) {
                Integer[][] tmp1 = (Integer[][]) value;
                int nrow = tmp1.length;
                int ncol = tmp1[0].length;
                int[] tmp2 = new int[nrow * ncol];
                for (int i = 0; i < ncol; i++) {
                    for (int j = 0; j < nrow; j++) {
                        tmp2[i * ncol + j] = (tmp1[j][i]).intValue();
                    }
                }
                rengine.assign(name, tmp2);
                result = rengine.eval(name + "<- matrix(" + name + ", nrow=" + nrow + ")");
                if (result == null) throw new QSARModelException("Error assigning a int[][]");
            } else if (value instanceof Double[][]) {
                Double[][] tmp1 = (Double[][]) value;
                int nrow = tmp1.length;
                int ncol = tmp1[0].length;
                double[] tmp2 = new double[nrow * ncol];
                for (int i = 0; i < ncol; i++) {
                    for (int j = 0; j < nrow; j++) {
                        tmp2[i * ncol + j] = (tmp1[j][i]).doubleValue();
                    }
                }
                rengine.assign(name, tmp2);
                result = rengine.eval(name + "<- matrix(" + name + ", nrow=" + nrow + ")");
                if (result == null) throw new QSARModelException("Error assigning a double[][]");
            }
        }

        // make the list command
        String cmd = paramVariableName + " <- list(";
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            cmd = cmd + name + " = " + name + ", ";
        }
        cmd = cmd + ")";

        // now eval the command
        result = rengine.eval(cmd);
        if (result == null) throw new QSARModelException("Error making the parameter list");

        // now lets remove all the variables we had assigned
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            rengine.eval("rm(" + name + ")");
        }

        return paramVariableName;
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

    /**
     * Specifies the parameters value.
     *
     * @param key A String representing the name of the parameter (corresponding to the
     *            name described in the R manpages)
     * @param obj The value of the parameter
     * @throws QSARModelException if the parameters are of the wrong type for the given modeling function
     */
    abstract public void setParameters(String key, Object obj) throws QSARModelException;

    abstract public void build() throws QSARModelException;

    abstract public void predict() throws QSARModelException;

    abstract protected void finalize();


    class TextConsole implements RMainLoopCallbacks {
        public void rWriteConsole(Rengine re, String text) {
            System.out.print(text);
        }

        public void rBusy(Rengine re, int which) {
            System.out.println("rBusy(" + which + ")");
        }

        public String rReadConsole(Rengine re, String prompt, int addToHistory) {
            System.out.print(prompt);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String s = br.readLine();
                return (s == null || s.length() == 0) ? s : s + "\n";
            } catch (Exception e) {
                System.out.println("jriReadConsole exception: " + e.getMessage());
            }
            return null;
        }

        public void rShowMessage(Rengine re, String message) {
            System.out.println("rShowMessage \"" + message + "\"");
        }

        public String rChooseFile(Rengine re, int newFile) {
            FileDialog fd = new FileDialog(new Frame(), (newFile == 0) ? "Select a file" : "Select a new file", (newFile == 0) ? FileDialog.LOAD : FileDialog.SAVE);
            fd.pack();
            fd.setVisible(true);
            String res = null;
            if (fd.getDirectory() != null) res = fd.getDirectory();
            if (fd.getFile() != null) res = (res == null) ? fd.getFile() : (res + fd.getFile());
            return res;
        }

        public void rFlushConsole(Rengine re) {
        }

        public void rLoadHistory(Rengine re, String filename) {
        }

        public void rSaveHistory(Rengine re, String filename) {
        }
    }

}
