/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

/**
 * Adapted version made for use in applets.
 */
public class LoggingTool {

    public LoggingTool() {
        this( LoggingTool.class.getName() );
    }

    public LoggingTool(String classname) {
        dumpSystemProperties();
    }

    public void dumpSystemProperties() {
        debug("os.name        : " + System.getProperty("os.name"));
        debug("os.version     : " + System.getProperty("os.version"));
        debug("os.arch        : " + System.getProperty("os.arch"));
        debug("java.version   : " + System.getProperty("java.version"));
        debug("java.vendor    : " + System.getProperty("java.vendor"));
    }

    public void dumpClasspath() {
        debug("java.class.path: " + System.getProperty("java.class.path"));
    }

    public void debug(String s) {
    }

    public void error(String s) {
        System.out.print("ERROR: ");
        System.out.println(s);
    }

    public void fatal(String s) {
        System.out.print("FATAL: ");
        System.out.println(s);
    }

    public void info(String s) {
    }

    public void warn(String s) {
        System.out.print("WARN: ");
        System.out.println(s);
    }

}

