/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

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
 *   logger.info("The 1056389822 prime that is used is: ",
 *               calculatePrime(1056389822));
 * }
 * </pre>
 *
 * <p>The class uses log4j as a backend if available, and System.out otherwise.
 *
 * @cdk.module standard
 */
public class LoggingTool {

    private boolean debug = false;
    private boolean tostdout = false;

    private Object logger;
    private String classname;
    
    /** Default number of StackTraceElements to be printed by debug(Exception) */
    private final int DEFAULT_STACK_LENGTH = 5;
     
    private int stackLength = DEFAULT_STACK_LENGTH;

    public LoggingTool() {
        this( LoggingTool.class.getName() );
    }

    public LoggingTool(boolean useConfig) {
        this( LoggingTool.class.getName(), useConfig );
    }

    /**
     * Instantiate a LoggingTool which produces log lines indicating them to be
     * for the Class with the name <code>classname</code>.
     */
    public LoggingTool(String classname) {
        this(classname, false);
    }
    
    /**
     * Instantiate a LoggingTool which produces log lines indicating them to be
     * for the Class given by <code>object</code>.
     */
    public LoggingTool(Object object) {
        this(object.getClass().getName());
    }
    
    public LoggingTool(Object object, boolean useConfig) {
        this(object.getClass().getName(), useConfig);
    }

    public LoggingTool(String classname, boolean useConfig) {
        this.classname = classname;
        try {
            logger = org.apache.log4j.Category.getInstance( classname );
            /****************************************************************
             * believe it or not this code has a purpose
             * The MSFT jvm throws a ClassNotFoundException instead of
             * a NoClassDefFoundError. But the compiler will not allow the
             * catch of ClassNotFoundException because it doesn't think
             * that anybody is going to throw it. So, we will put in this
             * little trick ...
             ****************************************************************/
            if (false)
              throw new ClassNotFoundException();
            if (useConfig) {
                // configure Log4J
                URL url = getClass().getClassLoader().getResource("/org/openscience/cdk/config/log4j.properties");
                InputStream ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/config/log4j.properties");
                Properties props = new Properties();
                props.load(ins);
                org.apache.log4j.PropertyConfigurator.configure(props);
            }
        } catch (ClassNotFoundException e) {
            tostdout = true;
            debug("Log4J class not found!");
        } catch (NoClassDefFoundError e) {
            tostdout = true;
            debug("Log4J class not found!");
        } catch (NullPointerException e) {
            tostdout = true;
            debug("Properties file not found!");
        } catch (Exception e) {
            tostdout = true;
        }
        /****************************************************************
         * but some JVMs (i.e. MSFT) won't pass the SecurityException to
         * this exception handler. So we are going to check the JVM
         * version first
         ****************************************************************/
        debug = false;
        String strJvmVersion = System.getProperty("java.version");
        if (strJvmVersion.compareTo("1.2") >= 0) {
          // Use a try {} to catch SecurityExceptions when used in applets
          try {
            // by default debugging is set off, but it can be turned on
            // with starting java like "java -Dcdk.debugging=true"
            if (System.getProperty("cdk.debugging", "false").equals("true")) {
              debug = true;
            }
            if (System.getProperty("cdk.debug.stdout", "false").equals("true")) {
              tostdout = true;
            }
          } catch (Exception e) {
            // guess what happens: security exception from applet runner
            // do not debug in those cases
          }
        }
    }

    public void dumpSystemProperties() {
        debug("os.name        : " + System.getProperty("os.name"));
        debug("os.version     : " + System.getProperty("os.version"));
        debug("os.arch        : " + System.getProperty("os.arch"));
        debug("java.version   : " + System.getProperty("java.version"));
        debug("java.vendor    : " + System.getProperty("java.vendor"));
    }

    /**
     * Sets the number of StackTraceElements to be printed in DEBUG mode.
     * Defaults to DEFAULT_STACK_LENGTH.
     */
    public void setStackLength(int length) {
        this.stackLength = length;
    }
    
    public void dumpClasspath() {
        debug("java.class.path: " + System.getProperty("java.class.path"));
    }

    /**
     * Shows debug output for the Object. If the object is an instanceof
     * Throwable it will output the trace. Otherwise it will use the
     * toString() method.
     */
    public void debug(Object object) {
        if (debug) {
            if (object instanceof Throwable) {
                debugThrowable((Throwable)object);
            } else {
                debugString("" + object.toString());
            }
        }
    }
    
    private void debugString(String string) {
        if (tostdout) {
            toSTDOUT("DEBUG", string);
        } else {
            ((org.apache.log4j.Category)logger).debug(string);
        }
    }
    
    public void debug(Object object, Object object2) {
        if (debug) {
            debugString("" + object + object2);
        }
    }
    
    public void debug(Object object, int number) {
        if (debug) {
            debugString("" + object + number);
        }
    }
    
    public void debug(Object object, double number) {
        if (debug) {
            debugString("" + object + number);
        }
    }
    
    public void debug(Object object, boolean bool) {
        if (debug) {
            debugString("" + object + bool);
        }
    }
    
    public void debug(Object obj, Object obj2, Object obj3) {
        if (debug) {
            debugString("" + obj + obj2 + obj3);
        }
    }
    
    public void debug(Object obj, Object obj2, Object obj3, Object obj4) {
        if (debug) {
            debugString("" + obj + obj2 + obj3 + obj4);
        }
    }
    
    public void debug(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (debug) {
            debugString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }
    
    private void debugThrowable(Throwable problem) {
        if (problem != null) {
            if (problem instanceof Error) {
                debug("Error: " + problem.getMessage());
            } else {
                debug("Exception: " + problem.getMessage());
            }
            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
            problem.printStackTrace(new PrintWriter(stackTraceWriter));
            String trace = stackTraceWriter.toString();
            try {
                BufferedReader reader = new BufferedReader(new StringReader(trace));
                if (reader.ready()) {
                    String traceLine = reader.readLine();
                    while (reader.ready() && traceLine != null) {
                        debug(traceLine);
                        traceLine = reader.readLine();
                    }
                }
            } catch (Exception ioException) {
                error("Serious error in LoggingTool while printing exception stack trace: " + 
                ioException.getMessage());
            }
        }
    }
    
    public void error(Object object) {
        if (debug) {
            errorString("" + object);
        }
    }

    public void error(Object object, int number) {
        if (debug) {
            errorString("" + object + number);
        }
    }
    
    public void error(Object object, double number) {
        if (debug) {
            errorString("" + object + number);
        }
    }
    
    private void errorString(String string) {
        if (tostdout) {
            toSTDOUT("ERROR", string);
        } else {
            ((org.apache.log4j.Category)logger).error(string);
        }
    }
    
    public void error(Object object, Object object2) {
        if (debug) {
            errorString("" + object + object2);
        }
    }
    
    public void error(Object obj, Object obj2, Object obj3) {
        if (debug) {
            errorString("" + obj + obj2 + obj3);
        }
    }
    
    public void error(Object obj, Object obj2, Object obj3, Object obj4) {
        if (debug) {
            errorString("" + obj + obj2 + obj3 + obj4);
        }
    }
    
    public void error(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (debug) {
            errorString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }
    
    public void fatal(Object object) {
        if (debug) {
            if (tostdout) {
                toSTDOUT("FATAL", object.toString());
            } else {
                ((org.apache.log4j.Category)logger).fatal("" + object.toString());
            }
        }
    }

    public void info(Object object) {
        if (debug) {
            infoString("" + object);
        }
    }

    public void info(Object object, int number) {
        if (debug) {
            infoString("" + object + number);
        }
    }
    
    public void info(Object object, double number) {
        if (debug) {
            infoString("" + object + number);
        }
    }
    
    private void infoString(String string) {
        if (tostdout) {
            toSTDOUT("INFO", string);
        } else {
            ((org.apache.log4j.Category)logger).info(string);
        }
    }
    
    public void info(Object object, Object object2) {
        if (debug) {
            infoString("" + object + object2);
        }
    }
    
    public void info(Object obj, Object obj2, Object obj3) {
        if (debug) {
            infoString("" + obj + obj2 + obj3);
        }
    }
    
    public void info(Object obj, Object obj2, Object obj3, Object obj4) {
        if (debug) {
            infoString("" + obj + obj2 + obj3 + obj4);
        }
    }
    
    public void info(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (debug) {
            infoString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }
    public void warn(Object object) {
        if (debug) {
            warnString("" + object);
        }
    }
    
    private void warnString(String string) {
        if (tostdout) {
            toSTDOUT("WARN", string);
        } else {
            ((org.apache.log4j.Category)logger).warn(string);
        }
    }
    
    public void warn(Object object, int number) {
        if (debug) {
            warnString("" + object + number);
        }
    }
    
    public void warn(Object object, double number) {
        if (debug) {
            warnString("" + object + number);
        }
    }
    
    public void warn(Object object, Object object2) {
        if (debug) {
            warnString("" + object + object2);
        }
    }
    
    public void warn(Object obj, Object obj2, Object obj3) {
        if (debug) {
            warnString("" + obj + obj2 + obj3);
        }
    }
    
    public void warn(Object obj, Object obj2, Object obj3, Object obj4) {
        if (debug) {
            warnString("" + obj + obj2 + obj3 + obj4);
        }
    }
    
    public void warn(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (debug) {
            warnString("" + obj + obj2 + obj3 + obj4 + obj5);
        }
    }

    /**
     * Use this method for computational demanding debug info.
     * For example:
     * <pre>
     * if (logger.isDebugEnabled()) {
     *   logger.info("The 1056389822 prime that is used is: ",
     *               calculatePrime(1056389822));
     * }
     * </pre>
     */
    public boolean isDebugEnabled() {
        return debug;
    }
    
    private void toSTDOUT(String level, String message) {
        System.out.print(classname);
        System.out.print(" ");
        System.out.print(level);
        System.out.print(": ");
        System.out.println(message);
    }

}

