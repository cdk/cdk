/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2003  The Chemistry Development Kit (CDK) project
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

import java.net.*;
import java.io.*;
import java.util.*;

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
     *
     * @see #DEFAULT_STACK_LENGTH
     */
    public void setStackLength(int length) {
        this.stackLength = length;
    }
    
    public void dumpClasspath() {
        debug("java.class.path: " + System.getProperty("java.class.path"));
    }

    public void debug(Exception exception) {
        StackTraceElement[] stack = exception.getStackTrace();
        String string = "Exception: " + exception.toString();
        this.debug(string);
        for (int i=0; i<stack.length; i++) {
            string = "       in: " + stack[i].getClassName() +
                     "." + stack[i].getMethodName();
            String filename = stack[i].getFileName();
            if (filename != null) {
                string = string + "(" + filename + " line: " +
                         stack[i].getLineNumber() + ")";
            }
            this.debug(string);
            if (i == this.stackLength) i = stack.length;
        }
    }
    
    public void debug(String s) {
        if (debug) {
            if (tostdout) {
                toSTDOUT("DEBUG", s);
            } else {
                ((org.apache.log4j.Category)logger).debug(s);
            }
        }
    }

    public void error(String s) {
        if (debug) {
            if (tostdout) {
                toSTDOUT("ERROR", s);
            } else {
                ((org.apache.log4j.Category)logger).error(s);
            }
        }
    }

    public void fatal(String s) {
        if (debug) {
            if (tostdout) {
                toSTDOUT("FATAL", s);
            } else {
                ((org.apache.log4j.Category)logger).fatal(s);
            }
        }
    }

    public void info(String s) {
        if (debug) {
            if (tostdout) {
                toSTDOUT("INFO", s);
            } else {
                ((org.apache.log4j.Category)logger).info(s);
            }
        }
    }

    public void warn(String s) {
        if (debug) {
            if (tostdout) {
                toSTDOUT("WARN", s);
            } else {
                ((org.apache.log4j.Category)logger).warn(s);
            }
        }
    }
    
    private void toSTDOUT(String level, String message) {
        System.out.print(classname);
        System.out.print(" ");
        System.out.print(level);
        System.out.print(": ");
        System.out.println(message);
    }

}

