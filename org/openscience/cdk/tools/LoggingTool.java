/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

public class LoggingTool {

    private boolean tostdout = false;

    private Object logger;

    public LoggingTool() {
        this( LoggingTool.class.getName() );
    }

    public LoggingTool(String classname) {
        try {
            logger = org.apache.log4j.Category.getInstance( classname );

            // configure Log4J
            (org.apache.log4j.PropertyConfigurator).configure("org/openscience/cdk/config/log4j.properties");
        } catch (NoClassDefFoundError e) {
            tostdout = true;
        }
    }

    public void debug(String s) {
        if (tostdout) {
            System.out.print("DEBUG: ");
            System.out.println(s);
        } else {
            ((org.apache.log4j.Category)logger).debug(s);
        }
    }

    public void error(String s) {
        if (tostdout) {
            System.out.print("ERROR: ");
            System.out.println(s);
        } else {
            ((org.apache.log4j.Category)logger).error(s);
        }
    }

    public void fatal(String s) {
        if (tostdout) {
            System.out.print("FATAL: ");
            System.out.println(s);
        } else {
            ((org.apache.log4j.Category)logger).fatal(s);
        }
    }

    public void info(String s) {
        if (tostdout) {
            System.out.print("INFO: ");
            System.out.println(s);
        } else {
            ((org.apache.log4j.Category)logger).warn(s);
        }
    }

    public void warn(String s) {
        if (tostdout) {
            System.out.print("WARN: ");
            System.out.println(s);
        } else {
            ((org.apache.log4j.Category)logger).warn(s);
        }
    }

}

