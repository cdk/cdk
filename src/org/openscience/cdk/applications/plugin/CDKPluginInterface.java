/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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

import javax.swing.JMenu;
import javax.swing.JPanel;

/**
 * Interface that CDK Plugins must implement. The version of this
 * interface is given below.
 *
 * @cdkPackage applications
 *
 * @version $Revision$
 */
public interface CDKPluginInterface {

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
    public void setEditBus(CDKEditBus editBus);

    /**
     * Initializes the plugin.
     */
    public void start();

    /**
     * Closes down the plugin.
     */
    public void stop();
    
}


