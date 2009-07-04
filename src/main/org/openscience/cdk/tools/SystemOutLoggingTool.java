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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Implementation of the {@link ILoggingTool} interface that sends output to
 * the {@link System}.out channel.
 * 
 * @cdk.module core
 */
@TestClass("org.openscience.cdk.tools.SystemOutLoggingToolTest")
public class SystemOutLoggingTool implements ILoggingTool {

    /** Boolean which is true when debug messages are send to System.out. */
    private boolean doDebug = false;

    /** Logger used to report internal problems. */
    private static ILoggingTool logger;
    
    /** Name of the class for which this {@link ILoggingTool} is reporting. */
    private String classname;

    /** Length of the stack to print for reported {@link Exception}s. */
    private int stackLength;
    
    /**
     * Constructs a ILoggingTool which produces log lines indicating them to be
     * for the given Class.
     *
     * @param classInst Class from which the log messages originate
     */
    public SystemOutLoggingTool(Class<?> classInst) {
        this.classname = classInst.getName();
        doDebug = false;
        if (System.getProperty("cdk.debugging", "false").equals("true") ||
            System.getProperty("cdk.debug.stdout", "false").equals("true")) {
            doDebug = true;
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testDumpSystemProperties")
    public void dumpSystemProperties() {
        debug("os.name        : " + System.getProperty("os.name"));
        debug("os.version     : " + System.getProperty("os.version"));
        debug("os.arch        : " + System.getProperty("os.arch"));
        debug("java.version   : " + System.getProperty("java.version"));
        debug("java.vendor    : " + System.getProperty("java.vendor"));
    }

    /** {@inheritDoc} */
    @TestMethod("testSetStackLength_int")
    public void setStackLength(int length) {
        this.stackLength = length;
    }
    
    /** {@inheritDoc} */
    @TestMethod("testDumpClasspath")
    public void dumpClasspath() {
        debug("java.class.path: " + System.getProperty("java.class.path"));
    }

    /** {@inheritDoc} */
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
        printToSTDOUT("DEBUG", string);
    }
    
    /** {@inheritDoc} */
    @TestMethod("testDebug_Object_Object")
    public void debug(Object object, Object object2) {
        if (doDebug) {
            debugString("" + object + object2);
        }
    }
    
    /** {@inheritDoc} */
    @TestMethod("testDebug_Object_int")
    public void debug(Object object, int number) {
        if (doDebug) {
            debugString("" + object + number);
        }
    }
    
    /** {@inheritDoc} */
    @TestMethod("testDebug_Object_double")
    public void debug(Object object, double number) {
        if (doDebug) {
            debugString("" + object + number);
        }
    }
    
    /** {@inheritDoc} */
    @TestMethod("testDebug_Object_boolean")
    public void debug(Object object, boolean bool) {
        if (doDebug) {
            debugString("" + object + bool);
        }
    }
    
    /** {@inheritDoc} */
    @TestMethod("testDebug_Object_Object_Object")
    public void debug(Object obj, Object obj2, Object obj3) {
        if (doDebug) {
            debugString("" + obj + obj2 + obj3);
        }
    }
    
    /** {@inheritDoc} */
    @TestMethod("testDebug_Object_Object_Object_Object")
    public void debug(Object obj, Object obj2, Object obj3, Object obj4) {
        if (doDebug) {
            debugString("" + obj + obj2 + obj3 + obj4);
        }
    }

    /** {@inheritDoc} */
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
            StringWriter stackTraceWriter = new StringWriter();
            problem.printStackTrace(new PrintWriter(stackTraceWriter));
            String trace = stackTraceWriter.toString();
            try {
                BufferedReader reader = new BufferedReader(
                    new StringReader(trace)
                );
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
                error("Serious error in LoggingTool while printing exception " +
                      "stack trace: ", ioException.getMessage());
                logger.debug(ioException);
            }
            Throwable cause = problem.getCause(); 
            if (cause != null) {
            	debug("Caused by: ");
            	debugThrowable(cause);
            }
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testError_Object")
    public void error(Object object) {
        if (doDebug) {
            errorString("" + object);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testError_Object_int")
    public void error(Object object, int number) {
        if (doDebug) {
            errorString("" + object + number);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testError_Object_double")
    public void error(Object object, double number) {
        if (doDebug) {
            errorString("" + object + number);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testError_Object_boolean")
    public void error(Object object, boolean bool) {
        if (doDebug) {
            errorString("" + object + bool);
        }
    }
    
    private void errorString(String string) {
        printToSTDOUT("ERROR", string);
    }

    /** {@inheritDoc} */
    @TestMethod("testError_Object_Object")
    public void error(Object object, Object object2) {
        if (doDebug) {
            errorString("" + object + object2);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testError_Object_Object_Object")
    public void error(Object obj, Object obj2, Object obj3) {
        if (doDebug) {
            errorString("" + obj + obj2 + obj3);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testError_Object_Object_Object_Object")
    public void error(Object obj, Object obj2, Object obj3, Object obj4) {
        if (doDebug) {
            errorString("" + obj + obj2 + obj3 + obj4);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testError_Object_Object_Object_Object_Object")
    public void error(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (doDebug) {
            errorString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testFatal_Object")
    public void fatal(Object object) {
        if (doDebug) {
            printToSTDOUT("FATAL", object.toString());
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testInfo_Object")
    public void info(Object object) {
        if (doDebug) {
            infoString("" + object);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testInfo_Object_int")
    public void info(Object object, int number) {
        if (doDebug) {
            infoString("" + object + number);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testInfo_Object_double")
    public void info(Object object, double number) {
        if (doDebug) {
            infoString("" + object + number);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testInfo_Object_boolean")
    public void info(Object object, boolean bool) {
        if (doDebug) {
            infoString("" + object + bool);
        }
    }
    
    private void infoString(String string) {
        printToSTDOUT("INFO", string);
    }

    /** {@inheritDoc} */
    @TestMethod("testInfo_Object_Object")
    public void info(Object object, Object object2) {
        if (doDebug) {
            infoString("" + object + object2);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testInfo_Object_Object_Object")
    public void info(Object obj, Object obj2, Object obj3) {
        if (doDebug) {
            infoString("" + obj + obj2 + obj3);
        }
    }

    /** {@inheritDoc} */
     @TestMethod("testInfo_Object_Object_Object_Object")
    public void info(Object obj, Object obj2, Object obj3, Object obj4) {
        if (doDebug) {
            infoString("" + obj + obj2 + obj3 + obj4);
        }
    }

     /** {@inheritDoc} */
    @TestMethod("testInfo_Object_Object_Object_Object_Object")
    public void info(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (doDebug) {
            infoString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testWarn_Object")
    public void warn(Object object) {
        if (doDebug) {
            warnString("" + object);
        }
    }
    
    private void warnString(String string) {
        printToSTDOUT("WARN", string);
    }

    /** {@inheritDoc} */
    @TestMethod("testWarn_Object_int")
    public void warn(Object object, int number) {
        if (doDebug) {
            warnString("" + object + number);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testWarn_Object_boolean")
    public void warn(Object object, boolean bool) {
        if (doDebug) {
            warnString("" + object + bool);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testWarn_Object_number")
    public void warn(Object object, double number) {
        if (doDebug) {
            warnString("" + object + number);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testWarn_Object_Object")
    public void warn(Object object, Object object2) {
        if (doDebug) {
            warnString("" + object + object2);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testWarn_Object_Object_Object")
    public void warn(Object obj, Object obj2, Object obj3) {
        if (doDebug) {
            warnString("" + obj + obj2 + obj3);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testWarn_Object_Object_Object_Object")
    public void warn(Object obj, Object obj2, Object obj3, Object obj4) {
        if (doDebug) {
            warnString("" + obj + obj2 + obj3 + obj4);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testWarn_Object_Object_Object_Object_Object")
    public void warn(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (doDebug) {
            warnString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }

    /** {@inheritDoc} */
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

    /**
     * Creates a new {@link SystemOutLoggingTool} for the given class.
     *
     * @param sourceClass Class for which logging messages are recorded.
     * @return            A {@link SystemOutLoggingTool}.
     */
    @TestMethod("testCreate")
    public static ILoggingTool create(Class<?> sourceClass) {
        return new SystemOutLoggingTool(sourceClass);
    }

    /**
     * Protected method which must not be used, except for testing purposes.
     */
    @TestMethod("testIsDebugEnabled")
    protected void setDebugEnabled(boolean enabled) {
        doDebug = enabled;
    }

}
