/* Copyright (C) 2002-2003  Christoph Steinbeck <steinbeck@users.sf.net>
 *               2002-2008  Egon Willighagen <egonw@users.sf.net>
 *               2022       John Mayfield
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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides an adapter from the CDK {@link org.openscience.cdk.tools.ILoggingTool}
 * interface to Log4J. You can use it by including the <code>cdk-log4j</code>
 * <b>AND</b> <code>log4j-core</code> in you library.
 * <br/>
 * See interface {@link ILoggingTool} for more details.
 *
 */
final class Log4jLoggingTool implements ILoggingTool {

    private final Logger log4jLogger;

    private int stackLength;

    /**
     * Log4J2 has customer levels and no longer has "TRACE_INT" etc so we can't know the values at compile
     * time. It's therefore not possible to use a switch.
     */
    private static final Map<Level, Integer> LOG4J2_LEVEL_TO_CDK_LEVEL = new HashMap<>();

    static {
        LOG4J2_LEVEL_TO_CDK_LEVEL.put(Level.TRACE, TRACE);
        LOG4J2_LEVEL_TO_CDK_LEVEL.put(Level.DEBUG, DEBUG);
        LOG4J2_LEVEL_TO_CDK_LEVEL.put(Level.INFO, INFO);
        LOG4J2_LEVEL_TO_CDK_LEVEL.put(Level.WARN, WARN);
        LOG4J2_LEVEL_TO_CDK_LEVEL.put(Level.ERROR, ERROR);
        LOG4J2_LEVEL_TO_CDK_LEVEL.put(Level.FATAL, FATAL);
    }

    /**
     * Constructs a LoggingTool which produces log lines without any special
     * indication which class the message originates from.
     */
    public Log4jLoggingTool() {
        this(Log4jLoggingTool.class);
    }

    /**
     * Constructs a LoggingTool which produces log lines indicating them to be
     * for the Class of the <code>Object</code>.
     *
     * @param object Object from which the log messages originate
     */
    public Log4jLoggingTool(Object object) {
        this(object.getClass());
    }

    /**
     * Constructs a LoggingTool which produces log lines indicating them to be
     * for the given Class.
     *
     * @param classInst Class from which the log messages originate
     */
    public Log4jLoggingTool(Class<?> classInst) {
        stackLength = DEFAULT_STACK_LENGTH;
        log4jLogger = LogManager.getLogger(classInst);
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
     * The default value is {@link #DEFAULT_STACK_LENGTH}.
     *
     * @param length the new stack length
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
        log4jLogger.debug(string);
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
                    result.append(obj);
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
                debug(ioException);
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
     * @param object  Object to apply toString() too and output
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
        log4jLogger.error(string);
    }

    /**
     * Shows FATAL output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @Override
    public void fatal(Object object) {
        log4jLogger.fatal(object.toString());
    }

    /**
     * Shows INFO output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @Override
    public void info(Object object) {
        infoString(object.toString());
    }

    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
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
        log4jLogger.info(string);
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
        log4jLogger.warn(string);
    }

    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() too and output
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

    /**
     * Creates a new {@link Log4jLoggingTool} for the given class.
     *
     * @param sourceClass Class for which logging messages are recorded.
     * @return A {@link Log4jLoggingTool}.
     */
    public static ILoggingTool create(Class<?> sourceClass) {
        return new Log4jLoggingTool(sourceClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) {
        throw new UnsupportedOperationException("Log4J does not let you set the level at runtime via the API");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        Level level = log4jLogger.getLevel();
        if (level == null)
            level = LogManager.getRootLogger().getLevel();
        Integer res = LOG4J2_LEVEL_TO_CDK_LEVEL.get(level);
        if (res == null)
            throw new IllegalStateException("Unsupported log4j level: " + level);
        return res;
    }
}
