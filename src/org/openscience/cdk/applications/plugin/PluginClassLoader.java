/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.applications.plugin;

import java.net.URL;
import java.net.URLClassLoader;

import org.openscience.cdk.tools.LoggingTool;

/**
 * A class loader for loading classes from a plugin jar files.
 *
 * @cdk.module applications
 */
public class PluginClassLoader extends URLClassLoader {
    
    private static LoggingTool logger = null;

    public PluginClassLoader(URL url) {
        super(new URL[] { url });
        if (logger == null) logger = new LoggingTool(this);
    }

    public PluginClassLoader(URL url, ClassLoader parent) {
        super(new URL[] { url },parent);
        if (logger == null) logger = new LoggingTool(this);
    }
    
    /**
     * This class loading method overwrites the default behaviour and tries
     * to look in the plugin jar first. This allows that users put
     * plugins in their local plugin dir that come with the program by default
     * too. The newest plugin is then loaded.
     *
     * @param name the name of the main class
     * @exception ClassNotFoundException if the specified class could not
     *            be found
     */
    public Class loadClass(String name) throws ClassNotFoundException {
        logger.debug("Loading from plugin jar: " + name);
        Class _class = null;
        try {
            _class = super.findClass(name);
            logger.debug("  found: " + _class);
            return _class;
        } catch (ClassNotFoundException exc) {
            logger.debug("  not found in plugin jar");
        }
        try {
            _class = super.loadClass(name);
            logger.debug("  found: " + _class);
        } catch (ClassNotFoundException exc) {
            logger.error("  not found in elsewhere");
            logger.debug(exc);
            throw exc;
        }
        return _class;
    }

}

