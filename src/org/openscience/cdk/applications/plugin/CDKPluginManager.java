/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.tools.LoggingTool;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * Manager that loads and maintains CDK plugins. In addition, it provides a JMenu 
 * to allow access to the plugin's functionality.
 */
public class CDKPluginManager {

    private LoggingTool logger;
    private Vector cdkPlugins;
    
    private String pluginDirName;
    private String pluginConfigDirName;
    private CDKEditBus editBus;
    
    /**
     * Instantiate a CDKPluginManager.
     *
     * @param pluginDirName       directory where the plugin jars can be found
     * @param pluginConfigDirName directory where the plugin config files can be found
     * @param editBus             object implementing the CDKEditBus interface
     *
     * @see   org.openscience.cdk.applications.plugin.CDKEditBus
     */
    public CDKPluginManager(String pluginDirName, String pluginConfigDirName,
                            CDKEditBus editBus) {
        this.logger = new LoggingTool(this.getClass().getName());
        this.editBus = editBus;
        this.pluginDirName = pluginDirName;
        this.pluginConfigDirName = pluginConfigDirName;
        loadPlugins();
    }
    
    /**
     * Returns a JMenu with submenus for each loaded plugin.
     */
    public JMenu getMenu() {
        JMenu menu = new JMenu("Plugins");
        Enumeration pluginsEnum = cdkPlugins.elements();
        while (pluginsEnum.hasMoreElements()) {
            CDKPluginInterface plugin = (CDKPluginInterface)pluginsEnum.nextElement();
            JMenu pluginMenu = new JMenu(plugin.getName());
            
            // add default items
            boolean hasOneOrMoreDefaultMenuItems = false;
            JPanel pluginPanel = plugin.getPluginPanel();
            if (pluginPanel != null) {
                // add action that fires up a window
                JMenuItem windowMenu = new JMenuItem("Plugin Window");
                windowMenu.addActionListener(
                    new PluginDialogAction(plugin)
                );
                pluginMenu.add(windowMenu);
                hasOneOrMoreDefaultMenuItems = true;
            }
            JPanel configPanel = plugin.getPluginConfigPanel();
            if (configPanel != null) {
                // add action that fires up a window
                pluginMenu.add(new JMenuItem("Config Window"));
                hasOneOrMoreDefaultMenuItems = true;
            }
            
            // try to plugin's private menu
            JMenu customPluginMenu = plugin.getMenu();
            if (customPluginMenu != null) {
                if (hasOneOrMoreDefaultMenuItems) {
                    pluginMenu.addSeparator();
                };
                pluginMenu.add(customPluginMenu);
            }
            menu.add(pluginMenu);
        }
        return menu;
    }
    
    /* Loads the plugins */
    private void loadPlugins() {
        cdkPlugins = new Vector();
        File uhome = new File(System.getProperty("user.home"));
        File pluginDir = new File(uhome + "/" + pluginDirName);
        logger.info("User dict dir: " + pluginDir);
        logger.debug("       exists: " + pluginDir.exists());
        logger.debug("  isDirectory: " + pluginDir.isDirectory());
        if (pluginDir.exists() && pluginDir.isDirectory()) {
            File[] plugins = pluginDir.listFiles();
            for (int i=0; i<plugins.length; i++) {
                // loop over these files and load them
                String pluginJarName = plugins[i].getName();
                if (pluginJarName.endsWith("jar")) {
                    logger.debug("Possible plugin found: " + pluginJarName);
                    try {
                        JarFile jarfile = new JarFile(plugins[i]);
                        Enumeration entries = jarfile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = (JarEntry)entries.nextElement();
                            if (entry.getName().endsWith("Plugin.class")) {
                                StringBuffer buffer = new StringBuffer(entry.getName());
                                int index = buffer.indexOf("/");
                                while (index != -1) {
                                    buffer.setCharAt(index, '.');
                                    index = buffer.indexOf("/");
                                }
                                String pluginName = buffer.toString().substring(0, buffer.indexOf(".class"));
                                logger.info("Plugin class found: " + pluginName);
                                try {
                                    // FIXME: use a classloader that loads the whole jar
                                    URL urlList[] = {
                                        plugins[i].toURL()
                                    };
                                    ClassLoader loader = new URLClassLoader(urlList);
                                    Class c = loader.loadClass(pluginName);
                                    Object plugin = c.newInstance();
                                    logger.info("  loaded.");
                                    if (plugin instanceof CDKPluginInterface) {
                                        CDKPluginInterface cdkPlugin = (CDKPluginInterface)plugin;
                                        cdkPlugin.setEditBus(editBus);
                                        cdkPlugin.start();
                                        cdkPlugins.addElement(plugin);
                                    } else {
                                        logger.info("Class is not type CDKPluginInterface");
                                    }
                                    break;
                                } catch (ClassNotFoundException exception) {
                                    System.err.println("Could not find class");
                                    exception.printStackTrace();
                                } catch (IllegalAccessException exception) {
                                    System.err.println("Don't have access to class");
                                    exception.printStackTrace();
                                } catch (InstantiationException exception) {
                                    System.err.println("Could not instantiate object");
                                    exception.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException exception) {
                        logger.error("Could not load plugin jar file: ");
                        logger.debug(exception.toString());
                    }
                }
            }
        }
    }
    
    /**
     * Action that creates a dialog with the content defined by the plugin for
     * which the dialog is created.
     */
    class PluginDialogAction extends AbstractAction {
        
        private CDKPluginInterface plugin;
        
        public PluginDialogAction(CDKPluginInterface plugin) {
            super("PluginDialog");
            this.plugin = plugin;
        }
        
        public void actionPerformed(ActionEvent e) {
            JPanel pluginPanel = plugin.getPluginPanel();
            if (pluginPanel != null) {
                JDialog pluginWindow = new JDialog();
                pluginWindow.getContentPane().add(pluginPanel);
                pluginWindow.pack();
                pluginWindow.show();
            }
        }
    }
    
}

