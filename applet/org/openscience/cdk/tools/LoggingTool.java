/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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
 * <p>Jmol version of LoggingTool.
 *
 * @cdk.module standard
 */
public class LoggingTool {

    private boolean debug = false;
    private boolean tostdout = true;

    private String classname;
    
    /** Default number of StackTraceElements to be printed by debug(Exception) */
    private final int DEFAULT_STACK_LENGTH = 5;
     
    private int stackLength = DEFAULT_STACK_LENGTH;

    public LoggingTool() {
    }

    public LoggingTool(Object object) {
    }
    
    public LoggingTool(Class classInst) {
    }

    public static void configureLog4j() {
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
    }
    
    private void debugString(String string) {
    }
    
    public void debug(Object object, Object object2) {
    }
    
    public void debug(Object object, int number) {
    }
    
    public void debug(Object object, double number) {
    }
    
    public void debug(Object obj, Object obj2, Object obj3) {
    }
    
    public void debug(Object obj, Object obj2, Object obj3, Object obj4) {
    }
    
    public void debug(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
    }
    
    private void debugThrowable(Throwable problem) {
    }
    
    public void error(Object object) {
        if (debug) {
            errorString(object.toString());
        }
    }

    public void error(Object object, int number) {
        if (debug) {
            errorString(object.toString() + number);
        }
    }
    
    public void error(Object object, double number) {
        if (debug) {
            errorString(object.toString() + number);
        }
    }
    
    private void errorString(String string) {
        toSTDOUT("ERROR", string);
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
        toSTDOUT("FATAL", object.toString());
    }

    public void info(Object object) {
        if (debug) {
            infoString(object.toString());
        }
    }

    public void info(Object object, int number) {
        if (debug) {
            infoString(object.toString() + number);
        }
    }
    
    public void info(Object object, double number) {
        if (debug) {
            infoString(object.toString() + number);
        }
    }
    
    private void infoString(String string) {
        toSTDOUT("INFO", string);
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
    }
    
    private void warnString(String string) {
    }
    
    public void warn(Object object, int number) {
    }
    
    public void warn(Object object, double number) {
    }
    
    public void warn(Object object, Object object2) {
    }
    
    public void warn(Object obj, Object obj2, Object obj3) {
    }
    
    public void warn(Object obj, Object obj2, Object obj3, Object obj4) {
    }
    
    public void warn(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
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