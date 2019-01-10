/* Copyright (C) 2002-2003  Christoph Steinbeck <steinbeck@users.sf.net>
 *               2002-2008  Egon Willighagen <egonw@users.sf.net>
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

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Useful for logging messages. Often used as a class static variable instantiated like:
 * <pre>
 * public class SomeClass {
 *     private static ILoggingTool logger =
 *         LoggingToolFactory.createLoggingTool(SomeClass.class);
 * }
 * </pre>
 * There is no special reason not to make the logger private and static, as the logging
 * information is closely bound to one specific Class, not subclasses and not instances.
 *
 * <p>The logger has five logging levels:
 * <dl>
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
 * </dl>
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
 * <p>The class uses log4j as a backend if available, and {@link System#err} otherwise.
 *
 * @cdk.module log4j
 * @cdk.githash
 */
public class LoggingTool implements ILoggingTool {

    private boolean             toSTDOUT             = false;

    private Logger              log4jLogger;
    private static ILoggingTool logger;
    private String              classname;

    private int                 stackLength;                 // NOPMD

    /** Default number of StackTraceElements to be printed by debug(Exception). */
    public final int            DEFAULT_STACK_LENGTH = 5;

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
    public LoggingTool(Class<?> classInst) {
        LoggingTool.logger = this;
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
            logger.debug("Unknown error occurred: ", e.getMessage());
        }
        /* **************************************************************
         * but some JVMs (i.e. MSFT) won't pass the SecurityException to this
         * exception handler. So we are going to check the JVM version first
         * **************************************************************
         */
        String strJvmVersion = System.getProperty("java.version");
        if (strJvmVersion.compareTo("1.2") >= 0) {
            // Use a try {} to catch SecurityExceptions when used in applets
            try {
                // by default debugging is set off, but it can be turned on
                // with starting java like "java -Dcdk.debugging=true"
                if (System.getProperty("cdk.debugging", "false").equals("true")) {
                    log4jLogger.setLevel(Level.DEBUG);
                } else {
                    log4jLogger.setLevel(Level.WARN);
                }
                if (System.getProperty("cdk.debug.stdout", "false").equals("true")) {
                    toSTDOUT = true;
                }
            } catch (Exception e) {
                System.err.println("Could not read the System property used to determine "
                        + "if logging should be turned on. So continuing without logging.");
            }
        }
    }

    /**
     * Forces the <code>LoggingTool</code> to configure the Log4J toolkit.
     * Normally this should be done by the application that uses the CDK library,
     * but is available for convenience.
     */
    public static void configureLog4j() {
        LoggingTool localLogger = new LoggingTool(LoggingTool.class);
        try { // NOPMD
            org.apache.log4j.PropertyConfigurator.configure(LoggingTool.class
                    .getResource("/org/openscience/cdk/config/data/log4j.properties"));
        } catch (NullPointerException e) { // NOPMD
            localLogger.error("Properties file not found: ", e.getMessage());
            localLogger.debug(e);
        } catch (Exception e) {
            localLogger.error("Unknown error occurred: ", e.getMessage());
            localLogger.debug(e);
        }
    }

    /**
     * Outputs system properties for the operating system and the java
     * version. More specifically: os.name, os.version, os.arch, java.version
     * and java.vendor.
     */
    @Override
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
    @Override
    public void setStackLength(int length) {
        this.stackLength = length;
    }

    /**
     * Outputs the system property for java.class.path.
     */
    @Override
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
    @Override
    public void debug(Object object) {
        if (isDebugEnabled()) {
            if (object instanceof Throwable) {
                debugThrowable((Throwable) object);
            } else {
                debugString("" + object);
            }
        }
    }

    private void debugString(String string) {
        if (toSTDOUT) {
            printToStderr("DEBUG", string);
        } else {
            log4jLogger.debug(string);
        }
    }

    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
     * @param objects Object[] to apply toString() too and output
     */
    @Override
    public void debug(Object object, Object... objects) {
        if (isDebugEnabled()) {
            StringBuilder result = new StringBuilder();
            result.append(object.toString());
            for (Object obj : objects) {
                if (obj == null) {
                    result.append("null");
                } else {
                    result.append(obj.toString());
                }
            }
            debugString(result.toString());
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
                    while (reader.ready() && traceLine != null && (counter < stackLength)) {
                        debug(traceLine);
                        traceLine = reader.readLine();
                        counter++;
                    }
                }
            } catch (Exception ioException) {
                error("Serious error in LoggingTool while printing exception stack trace: " + ioException.getMessage());
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
    @Override
    public void error(Object object) {
        errorString("" + object);
    }

    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param objects Object[] to apply toString() too and output
     */
    @Override
    public void error(Object object, Object... objects) {
        if (getLevel() <= ERROR) {
            StringBuilder result = new StringBuilder();
            result.append(object.toString());
            for (Object obj : objects) {
                result.append(obj.toString());
            }
            errorString(result.toString());
        }
    }

    private void errorString(String string) {
        if (toSTDOUT) {
            printToStderr("ERROR", string);
        } else {
            log4jLogger.error(string);
        }
    }

    /**
     * Shows FATAL output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @Override
    public void fatal(Object object) {
        if (toSTDOUT) {
            printToStderr("FATAL", object.toString());
        } else {
            log4jLogger.fatal("" + object.toString());
        }
    }

    /**
     * Shows INFO output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @Override
    public void info(Object object) {
        infoString("" + object);
    }

    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param objects Object[] to apply toString() too and output
     */
    @Override
    public void info(Object object, Object... objects) {
        if (getLevel() <= INFO) {
            StringBuilder result = new StringBuilder();
            result.append(object.toString());
            for (Object obj : objects) {
                result.append(obj.toString());
            }
            infoString(result.toString());
        }
    }

    private void infoString(String string) {
        if (toSTDOUT) {
            printToStderr("INFO", string);
        } else {
            log4jLogger.info(string);
        }
    }

    /**
     * Shows WARN output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @Override
    public void warn(Object object) {
        warnString("" + object);
    }

    private void warnString(String string) {
        if (toSTDOUT) {
            printToStderr("WARN", string);
        } else {
            log4jLogger.warn(string);
        }
    }

    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() too and output
     * @param objects Object[] to apply toString() too and output
     */
    @Override
    public void warn(Object object, Object... objects) {
        if (getLevel() <= WARN) {
            StringBuilder result = new StringBuilder();
            result.append(object.toString());
            for (Object obj : objects) {
                result.append(obj.toString());
            }
            warnString(result.toString());
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
    @Override
    public boolean isDebugEnabled() {
        return log4jLogger.isDebugEnabled();
    }

    private void printToStderr(String level, String message) {
        System.err.print(classname);
        System.err.print(" ");
        System.err.print(level);
        System.err.print(": ");
        System.err.println(message);
    }

    /**
     * Creates a new {@link LoggingTool} for the given class.
     *
     * @param sourceClass Class for which logging messages are recorded.
     * @return            A {@link LoggingTool}.
     */
    public static ILoggingTool create(Class<?> sourceClass) {
        return new LoggingTool(sourceClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) {
        switch (level) {
            case TRACE:
                log4jLogger.setLevel(Level.TRACE);
                break;
            case DEBUG:
                log4jLogger.setLevel(Level.DEBUG);
                break;
            case INFO:
                log4jLogger.setLevel(Level.INFO);
                break;
            case WARN:
                log4jLogger.setLevel(Level.WARN);
                break;
            case ERROR:
                log4jLogger.setLevel(Level.ERROR);
                break;
            case FATAL:
                log4jLogger.setLevel(Level.FATAL);
                break;
            default:
                throw new IllegalArgumentException("Invalid log level: " + level);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        Level level = log4jLogger.getLevel();
        if (level == null)
            level = Logger.getRootLogger().getLevel();
        switch (level.toInt()) {
            case Level.TRACE_INT:
                return TRACE;
            case Level.DEBUG_INT:
                return DEBUG;
            case Level.INFO_INT:
                return INFO;
            case Level.WARN_INT:
                return WARN;
            case Level.ERROR_INT:
                return ERROR;
            case Level.FATAL_INT:
                return FATAL;
            default:
                throw new IllegalArgumentException("Unsupported log4j level: " + level);
        }
    }
}
