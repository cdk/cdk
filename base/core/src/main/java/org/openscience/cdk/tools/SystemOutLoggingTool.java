/* Copyright (C) 2003-2009  Egon Willighagen <egonw@users.sf.net>
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
import java.io.StringWriter;
import java.util.Locale;

/**
 * Implementation of the {@link ILoggingTool} interface that sends output to
 * the {@link System#err} channel.
 *
 * @cdk.module core
 * @cdk.githash
 */
public class SystemOutLoggingTool implements ILoggingTool {

    /** The logging level, default anything above warnings. */
    private int level = ILoggingTool.WARN;

    /** Name of the class for which this {@link ILoggingTool} is reporting. */
    private String              classname;

    /** Length of the stack to print for reported {@link Exception}s. */
    private int                 stackLength;

    /**
     * Constructs a ILoggingTool which produces log lines indicating them to be
     * for the given Class.
     *
     * @param classInst Class from which the log messages originate
     */
    public SystemOutLoggingTool(Class<?> classInst) {
        this.classname = classInst.getName();
        if (System.getProperty("cdk.debugging", "false").equals("true")
                || System.getProperty("cdk.debug.stdout", "false").equals("true")) {
            level = DEBUG;
        }
        // change logging level from system prop
        String val = System.getProperty("cdk.logging.level", "warn");
        switch (val.toLowerCase(Locale.ROOT)) {
            case "trace":
                level = ILoggingTool.TRACE;
                break;
            case "debug":
                level = ILoggingTool.DEBUG;
                break;
            case "info":
                level = ILoggingTool.INFO;
                break;
            case "warn":
                level = ILoggingTool.WARN;
                break;
            case "error":
                level = ILoggingTool.ERROR;
                break;
            case "fatal":
                level = ILoggingTool.FATAL;
                break;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void dumpSystemProperties() {
        debug("os.name        : " + System.getProperty("os.name"));
        debug("os.version     : " + System.getProperty("os.version"));
        debug("os.arch        : " + System.getProperty("os.arch"));
        debug("java.version   : " + System.getProperty("java.version"));
        debug("java.vendor    : " + System.getProperty("java.vendor"));
    }

    /** {@inheritDoc} */
    @Override
    public void setStackLength(int length) {
        this.stackLength = length;
    }

    /** {@inheritDoc} */
    @Override
    public void dumpClasspath() {
        debug("java.class.path: " + System.getProperty("java.class.path"));
    }

    /** {@inheritDoc} */
    @Override
    public void debug(Object object) {
        if (level <= DEBUG) {
            if (object instanceof Throwable) {
                debugThrowable((Throwable) object);
            } else {
                debugString("" + object);
            }
        }
    }

    private void debugString(String string) {
        printToStderr("DEBUG", string);
    }

    /** {@inheritDoc} */
    @Override
    public void debug(Object object, Object... objects) {
        if (level <= DEBUG) {
            StringBuilder result = new StringBuilder();
            result.append(object);
            for (Object obj : objects) {
                result.append(obj);
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
            StringWriter stackTraceWriter = new StringWriter();
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
                error("Serious error in LoggingTool while printing exception " + "stack trace: ",
                        ioException.getMessage());
                ioException.printStackTrace();
            }
            Throwable cause = problem.getCause();
            if (cause != null) {
                debug("Caused by: ");
                debugThrowable(cause);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void error(Object object) {
        if (level <= ERROR) {
            errorString("" + object);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void error(Object object, Object... objects) {
        if (level <= ERROR) {
            StringBuilder result = new StringBuilder();
            result.append(object);
            for (Object obj : objects) {
                result.append(obj);
            }
            errorString(result.toString());
        }
    }

    private void errorString(String string) {
        printToStderr("ERROR", string);
    }

    /** {@inheritDoc} */
    @Override
    public void fatal(Object object) {
        if (level <= FATAL) {
            printToStderr("FATAL", object.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void info(Object object) {
        if (level <= INFO) {
            infoString("" + object);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void info(Object object, Object... objects) {
        if (level <= INFO) {
            StringBuilder result = new StringBuilder();
            result.append(object);
            for (Object obj : objects) {
                result.append(obj);
            }
            infoString(result.toString());
        }
    }

    private void infoString(String string) {
        printToStderr("INFO", string);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(Object object) {
        if (level <= WARN) {
            warnString("" + object);
        }
    }

    private void warnString(String string) {
        printToStderr("WARN", string);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(Object object, Object... objects) {
        if (level <= WARN) {
            StringBuilder result = new StringBuilder();
            result.append(object);
            for (Object obj : objects) {
                result.append(obj);
            }
            warnString(result.toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDebugEnabled() {
        return level <= DEBUG;
    }

    private void printToStderr(String level, String message) {
        for (String line : message.split("\n"))
            System.err.println(classname + ' ' + level + ": " + line);
    }

    /**
     * Creates a new {@link SystemOutLoggingTool} for the given class.
     *
     * @param sourceClass Class for which logging messages are recorded.
     * @return            A {@link SystemOutLoggingTool}.
     */
    public static ILoggingTool create(Class<?> sourceClass) {
        return new SystemOutLoggingTool(sourceClass);
    }

    /**
     * Protected method which must not be used, except for testing purposes.
     * @deprecated use {@link #setLevel(int)}
     */
    @Deprecated
    protected void setDebugEnabled(boolean enabled) {
        if (enabled)
            setLevel(DEBUG);
        else
            setLevel(FATAL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        return level;
    }
}
