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
 * private static org.openscience.cdk.tools.LoggingTool logger = new LoggingTool(ThisClass.class.getName(), true);
 * </pre>
 *
 * <p>Uses log4j as a backend if available, and System.out otherwise.
 *
 * @cdkPackage standard
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

    public LoggingTool(String classname) {
        this(classname, false);
    }
    
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
                debugString(object.toString());
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
            debugString(object.toString() + object2.toString());
        }
    }
    
    public void debug(Object obj, Object obj2, Object obj3) {
        if (debug) {
            debugString(obj.toString() + obj2.toString() + obj3.toString());
        }
    }
    
    public void debug(Object obj, Object obj2, Object obj3, Object obj4) {
        if (debug) {
            debugString(obj.toString() + obj2.toString() + obj3.toString() +
                  obj4.toString());
        }
    }
    
    public void debug(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (debug) {
            debugString(obj.toString() + obj2.toString() + obj3.toString() +
                  obj4.toString() + obj5.toString());
        }
    }
    
    private void debugThrowable(Throwable problem) {
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
    
    public void error(Object object) {
        if (debug) {
            errorString(object.toString());
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
            errorString(object.toString() + object2.toString());
        }
    }
    
    public void error(Object obj, Object obj2, Object obj3) {
        if (debug) {
            errorString(obj.toString() + obj2.toString() + obj3.toString());
        }
    }
    
    public void error(Object obj, Object obj2, Object obj3, Object obj4) {
        if (debug) {
            errorString(obj.toString() + obj2.toString() + obj3.toString() +
                  obj4.toString());
        }
    }
    
    public void error(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (debug) {
            errorString(obj.toString() + obj2.toString() + obj3.toString() +
                  obj4.toString() + obj5.toString());
        }
    }
    
    public void fatal(Object object) {
        if (debug) {
            if (tostdout) {
                toSTDOUT("FATAL", object.toString());
            } else {
                ((org.apache.log4j.Category)logger).fatal(object.toString());
            }
        }
    }

    public void info(Object object) {
        if (debug) {
            infoString(object.toString());
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
            infoString(object.toString() + object2.toString());
        }
    }
    
    public void info(Object obj, Object obj2, Object obj3) {
        if (debug) {
            infoString(obj.toString() + obj2.toString() + obj3.toString());
        }
    }
    
    public void info(Object obj, Object obj2, Object obj3, Object obj4) {
        if (debug) {
            infoString(obj.toString() + obj2.toString() + obj3.toString() +
                  obj4.toString());
        }
    }
    
    public void info(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (debug) {
            infoString(obj.toString() + obj2.toString() + obj3.toString() +
                  obj4.toString() + obj5.toString());
        }
    }
    public void warn(Object object) {
        if (debug) {
            warnString(object.toString());
        }
    }
    
    private void warnString(String string) {
        if (tostdout) {
            toSTDOUT("WARN", string);
        } else {
            ((org.apache.log4j.Category)logger).warn(string);
        }
    }
    
    public void warn(Object object, Object object2) {
        if (debug) {
            warnString(object.toString() + object2.toString());
        }
    }
    
    public void warn(Object obj, Object obj2, Object obj3) {
        if (debug) {
            warnString(obj.toString() + obj2.toString() + obj3.toString());
        }
    }
    
    public void warn(Object obj, Object obj2, Object obj3, Object obj4) {
        if (debug) {
            warnString(obj.toString() + obj2.toString() + obj3.toString() +
                  obj4.toString());
        }
    }
    
    public void warn(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (debug) {
            warnString(obj.toString() + obj2.toString() + obj3.toString() +
                  obj4.toString() + obj5.toString());
        }
    }

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

