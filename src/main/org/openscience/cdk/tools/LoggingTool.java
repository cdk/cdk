/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.tools;

import org.apache.log4j.Logger;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * Useful for logging messages. Often used as a class static variable instantiated like:
 * <pre>
 * public class SomeClass {
 *     private static LoggingTool logger;
 *     public SomeClass() {
 *         logger = new LoggingTool(this);
 *     }
 * }
 * </pre>
 * There is no special reason not to make the logger private and static, as the logging
 * information is closely bound to one specific Class, not subclasses and not instances.
 *
 * <p>The logger has five logging levels:
 * <ul><dl>
 *  <dt>DEBUG
 *  <dd>Default mode. Used for information you might need to track down the cause of a
 *      bug in the source code, or to understand how an algorithm works.
 *  <dt>WARNING
 *  <dd>This indicates a special situation which is unlike to happen, but for which no
 *      special actions need to be taken. E.g. missing information in files, or an
 *      unknown atom type. The action is normally something user friendly.
 *  <dt>INFO
 *  <dd>For reporting informative information to the user that he might easily disregard.
 *      Real important information should be given to the user using a GUI element.
 *  <dt>FATAL
 *  <dd>This level is used for situations that should not have happened *and* that
 *      lead to a situation where this program can no longer function (rare in Java).
 *  <dt>ERROR
 *  <dd>This level is used for situations that should not have happened *and* thus
 *      indicate a bug.
 * </dl></ul>
 *
 * <p>Consider that the debugging will not always be turned on. Therefore, it is better
 * not to concatenate string in the logger.debug() call, but have the LoggingTool do
 * this when appropriate. In other words, use:
 * <pre>
 * logger.debug("The String X has this value: ", someString);
 * logger.debug("The int Y has this value: ", y);
 * </pre>
 * instead of:
 * <pre>
 * logger.debug("The String X has this value: " + someString);
 * logger.debug("The int Y has this value: " + y);
 * </pre>
 *
 * <p>For logging calls that require even more computation you can use the
 * <code>isDebugEnabled()</code> method:
 * <pre>
 * if (logger.isDebugEnabled()) {
 *   logger.info("The 1056389822th prime that is used is: ",
 *     calculatePrime(1056389822));
 * }
 * </pre>
 *
 * <p>The class uses log4j as a backend if available, and System.out otherwise.
 *
 * @cdk.module core
 * @cdk.svnrev  $Revision$
 * @cdk.builddepends log4j.jar
 */
@TestClass("org.openscience.cdk.tools.LoggingToolTest")
public class LoggingTool {

    private boolean doDebug = false;
    private boolean toSTDOUT = false;

    private Logger log4jLogger;
    private LoggingTool logger;
    private String classname;

    private int stackLength;  // NOPMD
    
    /** Default number of StackTraceElements to be printed by debug(Exception). */
    public final int DEFAULT_STACK_LENGTH = 5;

    /**
     * Constructs a LoggingTool which produces log lines without any special
     * indication which class the message originates from.
     */
    public LoggingTool() {
        this(LoggingTool.class);
    }

    /**
     * Constructs a LoggingTool which produces log lines indicating them to be
     * for the Class of the <code>Object</code>.
     *
     * @param object Object from which the log messages originate
     */
    public LoggingTool(Object object) {
        this(object.getClass());
    }
    
    /**
     * Constructs a LoggingTool which produces log lines indicating them to be
     * for the given Class.
     *
     * @param classInst Class from which the log messages originate
     */
    public LoggingTool(Class classInst) {
        this.logger = this;
        stackLength = DEFAULT_STACK_LENGTH;
        this.classname = classInst.getName();
        try {
            log4jLogger = Logger.getLogger(classname);
        } catch (NoClassDefFoundError e) {
            toSTDOUT = true;
            logger.debug("Log4J class not found!");
        } catch (NullPointerException e) { // NOPMD
            toSTDOUT = true;
            logger.debug("Properties file not found!");
        } catch (Exception e) {
            toSTDOUT = true;
            logger.debug("Unknown error occured: ", e.getMessage());
        }
        /****************************************************************
         * but some JVMs (i.e. MSFT) won't pass the SecurityException to
         * this exception handler. So we are going to check the JVM
         * version first
         ****************************************************************/
        doDebug = false;
        String strJvmVersion = System.getProperty("java.version");
        if (strJvmVersion.compareTo("1.2") >= 0) {
          // Use a try {} to catch SecurityExceptions when used in applets
          try {
            // by default debugging is set off, but it can be turned on
            // with starting java like "java -Dcdk.debugging=true"
            if (System.getProperty("cdk.debugging", "false").equals("true")) {
              doDebug = true;
            }
            if (System.getProperty("cdk.debug.stdout", "false").equals("true")) {
              toSTDOUT = true;
            }
          } catch (Exception e) {
            logger.debug("guessed what happened: security exception thrown by applet runner");
            logger.debug("  therefore, do not debug");
          }
        }
    }
    
    /**
     * Forces the <code>LoggingTool</code> to configurate the Log4J toolkit.
     * Normally this should be done by the application that uses the CDK library,
     * but is available for convenience.
     */
    @TestMethod("testConfigureLog4j")
    public static void configureLog4j() {
        LoggingTool localLogger = new LoggingTool(LoggingTool.class);
        try { // NOPMD
            org.apache.log4j.PropertyConfigurator.configure(
                    LoggingTool.class.getResource("/org/openscience/cdk/config/data/log4j.properties"));
        } catch (NullPointerException e) { // NOPMD
            localLogger.error("Properties file not found: ", e.getMessage());
            localLogger.debug(e);
        } catch (Exception e) {
            localLogger.error("Unknown error occured: ", e.getMessage());
            localLogger.debug(e);
        }
    }

    /**
     * Outputs system properties for the operating system and the java
     * version. More specifically: os.name, os.version, os.arch, java.version
     * and java.vendor.
     */
    @TestMethod("testDumpSystemProperties")
    public void dumpSystemProperties() {
        debug("os.name        : " + System.getProperty("os.name"));
        debug("os.version     : " + System.getProperty("os.version"));
        debug("os.arch        : " + System.getProperty("os.arch"));
        debug("java.version   : " + System.getProperty("java.version"));
        debug("java.vendor    : " + System.getProperty("java.vendor"));
    }

    /**
     * Sets the number of StackTraceElements to be printed in DEBUG mode when
     * calling <code>debug(Throwable)</code>.
     * The default value is DEFAULT_STACK_LENGTH.
     *
     * @param length the new stack length
     *
     * @see #DEFAULT_STACK_LENGTH
     */
    @TestMethod("testSetStackLength_int")
    public void setStackLength(int length) {
        this.stackLength = length;
    }
    
    /**
     * Outputs the system property for java.class.path.
     */
    @TestMethod("testDumpClasspath")
    public void dumpClasspath() {
        debug("java.class.path: " + System.getProperty("java.class.path"));
    }

    /**
     * Shows DEBUG output for the Object. If the object is an instanceof
     * Throwable it will output the trace. Otherwise it will use the
     * toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @TestMethod("testDebug_Object")
    public void debug(Object object) {
        if (doDebug) {
            if (object instanceof Throwable) {
                debugThrowable((Throwable)object);
            } else {
                debugString("" + object);
            }
        }
    }
    
    private void debugString(String string) {
        if (toSTDOUT) {
            printToSTDOUT("DEBUG", string);
        } else {
            log4jLogger.debug(string);
        }
    }
    
    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
     * @param object2 Object to apply toString() too and output
     */
    @TestMethod("testDebug_Object_Object")
    public void debug(Object object, Object object2) {
        if (doDebug) {
            debugString("" + object + object2);
        }
    }
    
    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param number int to concatenate to object
     */
    @TestMethod("testDebug_Object_int")
    public void debug(Object object, int number) {
        if (doDebug) {
            debugString("" + object + number);
        }
    }
    
    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param number int to concatenate to object
     */
    @TestMethod("testDebug_Object_double")
    public void debug(Object object, double number) {
        if (doDebug) {
            debugString("" + object + number);
        }
    }
    
    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param bool   boolean to concatenate to object
     */
    @TestMethod("testDebug_Object_boolean")
    public void debug(Object object, boolean bool) {
        if (doDebug) {
            debugString("" + object + bool);
        }
    }
    
    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     */
    @TestMethod("testDebug_Object_Object_Object")
    public void debug(Object obj, Object obj2, Object obj3) {
        if (doDebug) {
            debugString("" + obj + obj2 + obj3);
        }
    }
    
    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     * @param obj4 Object to apply toString() too and output
     */
    @TestMethod("testDebug_Object_Object_Object_Object")
    public void debug(Object obj, Object obj2, Object obj3, Object obj4) {
        if (doDebug) {
            debugString("" + obj + obj2 + obj3 + obj4);
        }
    }
    
    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     * @param obj4 Object to apply toString() too and output
     * @param obj5 Object to apply toString() too and output
     */
    @TestMethod("testDebug_Object_Object_Object_Object_Object")
    public void debug(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (doDebug) {
            debugString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }
    
    private void debugThrowable(Throwable problem) {
        if (problem != null) {
            if (problem instanceof Error) {
                debug("Error: ", problem.getMessage());
            } else {
                debug("Exception: ", problem.getMessage());
            }
            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
            problem.printStackTrace(new PrintWriter(stackTraceWriter));
            String trace = stackTraceWriter.toString();
            try {
                BufferedReader reader = new BufferedReader(new StringReader(trace));
                if (reader.ready()) {
                    String traceLine = reader.readLine();
                    int counter = 0;
                    while (reader.ready() && traceLine != null && 
                    		(counter < stackLength)) {
                        debug(traceLine);
                        traceLine = reader.readLine();
                        counter++;
                    }
                }
            } catch (Exception ioException) {
                error("Serious error in LoggingTool while printing exception stack trace: " + 
                      ioException.getMessage());
                logger.debug(ioException);
            }
            Throwable cause = problem.getCause(); 
            if (cause != null) {
            	debug("Caused by: ");
            	debugThrowable(cause);
            }
        }
    }
    
    /**
     * Shows ERROR output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @TestMethod("testError_Object")
    public void error(Object object) {
        if (doDebug) {
            errorString("" + object);
        }
    }

    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param number int to concatenate to object
     */
    @TestMethod("testError_Object_int")
    public void error(Object object, int number) {
        if (doDebug) {
            errorString("" + object + number);
        }
    }
    
    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param number double to concatenate to object
     */
    @TestMethod("testError_Object_double")
    public void error(Object object, double number) {
        if (doDebug) {
            errorString("" + object + number);
        }
    }
    
    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param bool   boolean to concatenate to object
     */
    @TestMethod("testError_Object_boolean")
    public void error(Object object, boolean bool) {
        if (doDebug) {
            errorString("" + object + bool);
        }
    }
    
    private void errorString(String string) {
        if (toSTDOUT) {
            printToSTDOUT("ERROR", string);
        } else {
            log4jLogger.error(string);
        }
    }
    
    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
     * @param object2 Object to apply toString() too and output
     */
    @TestMethod("testError_Object_Object")
    public void error(Object object, Object object2) {
        if (doDebug) {
            errorString("" + object + object2);
        }
    }
    
    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     */
    @TestMethod("testError_Object_Object_Object")
    public void error(Object obj, Object obj2, Object obj3) {
        if (doDebug) {
            errorString("" + obj + obj2 + obj3);
        }
    }
    
    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     * @param obj4 Object to apply toString() too and output
     */
    @TestMethod("testError_Object_Object_Object_Object")
    public void error(Object obj, Object obj2, Object obj3, Object obj4) {
        if (doDebug) {
            errorString("" + obj + obj2 + obj3 + obj4);
        }
    }
    
    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     * @param obj4 Object to apply toString() too and output
     * @param obj5 Object to apply toString() too and output
     */
    @TestMethod("testError_Object_Object_Object_Object_Object")
    public void error(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (doDebug) {
            errorString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }
    
    /**
     * Shows FATAL output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @TestMethod("testFatal_Object")
    public void fatal(Object object) {
        if (doDebug) {
            if (toSTDOUT) {
                printToSTDOUT("FATAL", object.toString());
            } else {
                log4jLogger.fatal("" + object.toString());
            }
        }
    }

    /**
     * Shows INFO output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @TestMethod("testInfo_Object")
    public void info(Object object) {
        if (doDebug) {
            infoString("" + object);
        }
    }

    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param number int to concatenate to object
     */
    @TestMethod("testInfo_Object_int")
    public void info(Object object, int number) {
        if (doDebug) {
            infoString("" + object + number);
        }
    }
    
    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param number double to concatenate to object
     */
    @TestMethod("testInfo_Object_double")
    public void info(Object object, double number) {
        if (doDebug) {
            infoString("" + object + number);
        }
    }
    
    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param bool   boolean to concatenate to object
     */
    @TestMethod("testInfo_Object_boolean")
    public void info(Object object, boolean bool) {
        if (doDebug) {
            infoString("" + object + bool);
        }
    }
    
    private void infoString(String string) {
        if (toSTDOUT) {
            printToSTDOUT("INFO", string);
        } else {
            log4jLogger.info(string);
        }
    }
    
    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
     * @param object2 Object to apply toString() too and output
     */
    @TestMethod("testInfo_Object_Object")
    public void info(Object object, Object object2) {
        if (doDebug) {
            infoString("" + object + object2);
        }
    }
    
    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     */
    @TestMethod("testInfo_Object_Object_Object")
    public void info(Object obj, Object obj2, Object obj3) {
        if (doDebug) {
            infoString("" + obj + obj2 + obj3);
        }
    }
    
    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     * @param obj4 Object to apply toString() too and output
     */
     @TestMethod("testInfo_Object_Object_Object_Object")
    public void info(Object obj, Object obj2, Object obj3, Object obj4) {
        if (doDebug) {
            infoString("" + obj + obj2 + obj3 + obj4);
        }
    }
    
    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     * @param obj4 Object to apply toString() too and output
     * @param obj5 Object to apply toString() too and output
     */
    @TestMethod("testInfo_Object_Object_Object_Object_Object")
    public void info(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (doDebug) {
            infoString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }

    /**
     * Shows WARN output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @TestMethod("testWarn_Object")
    public void warn(Object object) {
        if (doDebug) {
            warnString("" + object);
        }
    }
    
    private void warnString(String string) {
        if (toSTDOUT) {
            printToSTDOUT("WARN", string);
        } else {
            log4jLogger.warn(string);
        }
    }
    
    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param number int to concatenate to object
     */
    @TestMethod("testWarn_Object_int")
    public void warn(Object object, int number) {
        if (doDebug) {
            warnString("" + object + number);
        }
    }
    
    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param bool   boolean to concatenate to object
     */
    @TestMethod("testWarn_Object_boolean")
    public void warn(Object object, boolean bool) {
        if (doDebug) {
            warnString("" + object + bool);
        }
    }
    
    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param number double to concatenate to object
     */
    @TestMethod("testWarn_Object_number")
    public void warn(Object object, double number) {
        if (doDebug) {
            warnString("" + object + number);
        }
    }
    
    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
     * @param object2 Object to apply toString() too and output
     */
    @TestMethod("testWarn_Object_Object")
    public void warn(Object object, Object object2) {
        if (doDebug) {
            warnString("" + object + object2);
        }
    }
    
    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     */
    @TestMethod("testWarn_Object_Object_Object")
    public void warn(Object obj, Object obj2, Object obj3) {
        if (doDebug) {
            warnString("" + obj + obj2 + obj3);
        }
    }
    
    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     * @param obj4 Object to apply toString() too and output
     */
    @TestMethod("testWarn_Object_Object_Object_Object")
    public void warn(Object obj, Object obj2, Object obj3, Object obj4) {
        if (doDebug) {
            warnString("" + obj + obj2 + obj3 + obj4);
        }
    }
    
    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param obj  Object to apply toString() too and output
     * @param obj2 Object to apply toString() too and output
     * @param obj3 Object to apply toString() too and output
     * @param obj4 Object to apply toString() too and output
     * @param obj5 Object to apply toString() too and output
     */
    @TestMethod("testWarn_Object_Object_Object_Object_Object")
    public void warn(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (doDebug) {
            warnString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }

    /**
     * Use this method for computational demanding debug info.
     * For example:
     * <pre>
     * if (logger.isDebugEnabled()) {
     *   logger.info("The 1056389822th prime that is used is: ",
     *                calculatePrime(1056389822));
     * }
     * </pre>
     *
     * @return true, if debug is enabled
     */
    @TestMethod("testIsDebugEnabled")
    public boolean isDebugEnabled() {
        return doDebug;
    }
    
    private void printToSTDOUT(String level, String message) {
        System.out.print(classname);
        System.out.print(" ");
        System.out.print(level);
        System.out.print(": ");
        System.out.println(message);
    }

}

