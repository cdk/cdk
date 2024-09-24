/* Copyright (C) 2022 John Mayfield
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * This class provides an adapter from the CDK {@link org.openscience.cdk.tools.ILoggingTool}
 * interface to SLF4J. You can use it by including the <code>cdk-slf4j</code>
 * <b>AND</b> some implementation e.g. <code>slf4j-simple</code>,
 * <code>log4j-over-slf4j</code>, etc. in you library.
 * <br/>
 * See interface {@link ILoggingTool} for more details.
 *
 * @cdk.module cdk-slf4j
 * @cdk.githash
 */
final class Slf4jLoggingTool implements ILoggingTool {

    private final Logger slf4jlogger;

    private int stackLength;

    /**
     * Default number of StackTraceElements to be printed by debug(Exception).
     */
    public final int DEFAULT_STACK_LENGTH = 5;


    private final Marker fatal = MarkerFactory.getMarker("FATAL");

    /**
     * Constructs a LoggingTool which produces log lines without any special
     * indication which class the message originates from.
     */
    public Slf4jLoggingTool() {
        this(Slf4jLoggingTool.class);
    }

    /**
     * Constructs a LoggingTool which produces log lines indicating them to be
     * for the Class of the <code>Object</code>.
     *
     * @param object Object from which the log messages originate
     */
    public Slf4jLoggingTool(Object object) {
        this(object.getClass());
    }

    /**
     * Constructs a LoggingTool which produces log lines indicating them to be
     * for the given Class.
     *
     * @param classInst Class from which the log messages originate
     */
    public Slf4jLoggingTool(Class<?> classInst) {
        stackLength = DEFAULT_STACK_LENGTH;
        slf4jlogger = LoggerFactory.getLogger(classInst);
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
        slf4jlogger.debug(string);
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
        slf4jlogger.error(string);
    }

    /**
     * Shows FATAL output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    @Override
    public void fatal(Object object)
    {
        slf4jlogger.error(fatal, "" + object.toString());
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
        slf4jlogger.info(string);
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
        slf4jlogger.warn(string);
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
        return slf4jlogger.isDebugEnabled();
    }

    /**
     * Creates a new {@link org.openscience.cdk.tools.Slf4jLoggingTool} for the given class.
     *
     * @param sourceClass Class for which logging messages are recorded.
     * @return A {@link org.openscience.cdk.tools.Slf4jLoggingTool}.
     */
    public static ILoggingTool create(Class<?> sourceClass) {
        return new Slf4jLoggingTool(sourceClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) {
        throw new IllegalArgumentException("slf4j does not let you set the level at runtime via the API");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        if (slf4jlogger.isTraceEnabled()) {
            return TRACE;
        }
        else if (slf4jlogger.isDebugEnabled()) {
            return DEBUG;
        }
        else if (slf4jlogger.isInfoEnabled()) {
            return INFO;
        }
        else if (slf4jlogger.isWarnEnabled()) {
            return WARN;
        }
        else if (slf4jlogger.isErrorEnabled()) {
            return ERROR;
        }
        return OFF;
    }
}
