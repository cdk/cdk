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
 * <p>
 * <b>NOTE</b>: For the R backend to work, ensure that R is correctly installed
 * and that SJava is also installed, using the -c option. Finally, ensure 
 * that the R_HOME environment variable points to the R installation.
 *
 * @param args  An array of String that contains command line parameters that
 *              would be processed by R
 *
 * @author Rajarshi Guha
 * @cdk.require r-project
 * @cdk.module qsar
 */
public abstract class RModel implements Model {

    /**
     * The object that performs the calls to the R engine
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

    /**
     * Initializes SJava and R with the specified command line arguments (see R documentation).
     *
     * @param args A String[] containing the command line parameters as elements
     */
    public RModel(String[] args) {
        logger = new LoggingTool(this);
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
        logger = new LoggingTool(this);
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

    abstract public void build() throws QSARModelException;
    abstract public void predict() throws QSARModelException;
    abstract public void setParameters(String key, Object obj) throws QSARModelException;
}


