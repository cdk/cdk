/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.applications.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.JarURLConnection;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.Attributes;
import java.io.IOException;

/**
 * A class loader for loading classes from a plugin jar files.
 *
 * @cdk.module applications
 */
public class PluginClassLoader extends URLClassLoader {
    
    private URL url;

    public PluginClassLoader(URL url) {
        super(new URL[] { url });
        this.url = url;
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
        System.out.println("Loading from plugin jar: " + name);
        Class _class = null;
        ClassNotFoundException exception = null;
        try {
            _class = super.findClass(name);
            System.out.println("  found: " + _class);
            return _class;
        } catch (ClassNotFoundException exc) {
            exception = exc;
            System.out.println("  not found in plugin jar");
        }
        try {
            _class = super.loadClass(name);
            System.out.println("  found: " + _class);
        } catch (ClassNotFoundException exc) {
            System.out.println("  not found in elsewhere");
            throw exception;
        }
        return _class;
    }

}

