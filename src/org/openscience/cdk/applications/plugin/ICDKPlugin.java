/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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

import javax.swing.JMenu;
import javax.swing.JPanel;

import org.openscience.cdk.interfaces.IChemObjectChangeEvent;

/**
 * Interface that CDK Plugins must implement. The version of this
 * interface is given below, and the plugin implementing this interface
 * should return this value in its <code>getAPIVersion()</code> method.
 *
 * <p>It is important to name your plugin like *Plugin.java, because the
 * CDKPluginManager used by Jmol and JChemPaint will only load
 * plugin classes which name end with 'Plugin'.
 *
 * <p>The plugin interacts with the application in which it is loaded
 * using an <code>CDKEditBus</code> object. Check its API to see how to
 * interact with the application.
 *
 * @cdk.module standard
 *
 * @version $Revision$
 *
 * @see org.openscience.cdk.applications.plugin.ICDKEditBus
 * @see org.openscience.cdk.applications.plugin.CDKPluginManager
 * @cdk.require swing
 */
public interface ICDKPlugin {

    /**
     * Should return the name of the plugin.
     */
    public String getName();
    
    /**
     * Should return the version of the implemented plugin API. The plugin
     * manager will only load plugins with a compatible API version.
     */
    public String getAPIVersion();
    
    /**
     * Should return the version of the plugin. This can be used to see if
     * a plugin has a newer version.
     */
    public String getPluginVersion();
    
    /**
     * Should return the license of the plugin.
     */
    public String getPluginLicense();
    
    /**
     * Should return a JPanel that provides access to the functionality
     * of this plugin.
     */
    public JPanel getPluginPanel();
    
    /**
     * Should return a JPanel that provides access to the configuration
     * of this plugin.
     */
    public JPanel getPluginConfigPanel();
    
    /**
     * Should return a JMenuItem that provides menu access to functionality
     * of this plugin.
     */
    public JMenu getMenu();
    
    /**
     * Sets the interface to the application.
     */
    public void setEditBus(ICDKEditBus editBus);

    /**
     * Sets the directory where the plugin's preferences can be found.
     */
    public void setPropertyDirectory(String directory);

    /**
     * Initializes the plugin.
     */
    public void start();

    /**
     * Closes down the plugin.
     */
    public void stop();
    
    /**
     * Method called when the active IChemObject is changed and is used
     * to keep the information in the plugin synchronized with the
     * application.
     */
    public void stateChanged(IChemObjectChangeEvent e);
}


