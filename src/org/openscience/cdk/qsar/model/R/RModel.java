package org.openscience.cdk.qsar.model.R;

import org.openscience.cdk.qsar.model.QSARModelException;
import org.openscience.cdk.qsar.model.Model;

import org.openscience.cdk.tools.LoggingTool;
import org.omegahat.R.Java.REvaluator;
import org.omegahat.R.Java.ROmegahatInterpreter;

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
 *
 * @param args  An array of String that contains command line parameters that
 *              would be processed by R
 *
 * @author Rajarshi Guha
 * @cdk.module qsar
 */
public abstract class RModel implements Model {

    public static REvaluator revaluator;
    public static ROmegahatInterpreter interp;
    private static boolean doneInit = false;
    private LoggingTool logger;
        
    private void loadRFunctions(REvaluator e) {
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

            e.voidEval("source(\""+scriptFile.getAbsolutePath()+"\")");

        } catch (Exception exception) {
            logger.error("Could not load CDK-SJava R script: ", scriptLocator);
            logger.debug(exception);
        }
    }

    /**
     * Initializes SJava and R with the specified command line arguments (see R documentation).
     *
     * @param args A String[] containing the command line parameters as elements
     */
    public RModel(String[] args) {
        logger = new LoggingTool(true);
        if (!doneInit) {
            this.interp = new ROmegahatInterpreter(ROmegahatInterpreter.fixArgs(args), false);
            this.revaluator = new REvaluator();
            loadRFunctions(this.revaluator);
            doneInit = true;
            logger.info("SJava initialized");
        } else {
            logger.info("SJava already initialized");
        }
    }

    /**
     * Initializes SJava with the <i>--vanilla, -q</i> flags.
     */
    public RModel() {
        String[] args = {"--vanilla","-q"};
        logger = new LoggingTool(true);
        if (!doneInit) {
            this.interp = new ROmegahatInterpreter(ROmegahatInterpreter.fixArgs(args), false);
            this.revaluator = new REvaluator();
            loadRFunctions(this.revaluator);
            doneInit = true;
            logger.info("SJava initialized");
        } else {
            logger.info("SJava already initialized");
        }
    }

    abstract public void build();
    abstract public void predict();
}


