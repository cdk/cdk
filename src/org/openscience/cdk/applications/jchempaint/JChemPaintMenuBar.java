/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.jchempaint;

import java.lang.reflect.Field;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.openscience.cdk.applications.jchempaint.action.JCPAction;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  JChemPaint menu bar
 *
 * @author        steinbeck
 * @cdk.module    jchempaint
 */
public class JChemPaintMenuBar extends JMenuBar {

	private static final long serialVersionUID = -8358165408129203644L;

	private LoggingTool logger;

    private String guiString = "stable";

	/**
	 *  The default constructor method.
	 *
	 * @param  jcpPanel  Description of the Parameter
	 */
	public JChemPaintMenuBar(JChemPaintPanel jcpPanel) {
		this(jcpPanel, "stable");
	}

	
	/**
	 *  The more flexible constructor method.
	 *
	 * @param  jcpPanel       Description of the Parameter
	 * @param  guiString      Description of the Parameter
	 */
	public JChemPaintMenuBar(JChemPaintPanel jcpPanel, String guiString) {
		this(jcpPanel, null, guiString);
	}


	/**
	 *  Constructor for the JChemPaintMenuBar object
	 *
	 * @param  jcpPanel        Description of the Parameter
	 * @param  guiString       Description of the Parameter
	 * @param  menuDefinition  Description of the Parameter
	 */
	public JChemPaintMenuBar(JChemPaintPanel jcpPanel, String menuDefinition, String guiString) {
		logger = new LoggingTool(this);
		this.guiString = guiString;
    	createMenubar(jcpPanel, menuDefinition);
	}


	/**
	 *  Creates a JMenuBar with all the menues that are specified in the properties
	 *  file. <p>
	 *
	 *  The menu items in the bar are defined by the property 'menubar' in
	 *  org.openscience.cdk.applications.jchempaint.resources.JChemPaint.properties.
	 *
	 * @param  jcpPanel        Description of the Parameter
	 * @param  menuDefinition  Description of the Parameter
	 */
	protected void createMenubar(JChemPaintPanel jcpPanel, 
        String menuDefinition) {
		addNormalMenuBar(jcpPanel, menuDefinition);
		this.add(Box.createHorizontalGlue());
		this.add(createMenu(jcpPanel, "help"));
	}


	/**
	 *  Adds a feature to the NormalMenuBar attribute of the JChemPaintMenuBar
	 *  object
	 *
	 * @param  jcpPanel        The feature to be added to the NormalMenuBar
	 *      attribute
	 * @param  menuDefinition  The feature to be added to the NormalMenuBar
	 *      attribute
	 */
	private void addNormalMenuBar(JChemPaintPanel jcpPanel, String menuDefinition) {
		String definition = menuDefinition;
		if (definition == null) {
			definition = getMenuResourceString("menubar");
		}
		String[] menuKeys = StringHelper.tokenize(definition);
		for (int i = 0; i < menuKeys.length; i++) {
			JMenu m = createMenu(jcpPanel, menuKeys[i]);
			if (m != null) {
				this.add(m);
			}
		}
	}


	/**
	 *  Creates a JMenu which can be part of the menu of an application embedding jcp.
	 *
	 * @param  jcpPanel   Description of the Parameter
	 * @return            The created JMenu
	 */
	public JMenu getMenuForEmbedded(JChemPaintPanel jcpPanel) {
		String definition = getMenuResourceString("menubar");
		String[] menuKeys = StringHelper.tokenize(definition);
		JMenu superMenu = new JMenu("JChemPaint");
		for (int i = 0; i < menuKeys.length; i++) {
			JMenu m = createMenu(jcpPanel, menuKeys[i]);
			if (m != null) {
				superMenu.add(m);
			}
		}
		return (superMenu);
	}


	/**
	 *  Creates a JMenu given by a String with all the MenuItems specified in the
	 *  properties file.
	 *
	 * @param  key       The String used to identify the Menu
	 * @param  jcpPanel  Description of the Parameter
	 * @return           The created JMenu
	 */
	protected JMenu createMenu(JChemPaintPanel jcpPanel, String key) {
		logger.debug("Creating menu: ", key);
		String[] itemKeys = StringHelper.tokenize(getMenuResourceString(key));
		JMenu menu = new JMenu(JCPLocalizationHandler.getInstance().getString(key));
		for (int i = 0; i < itemKeys.length; i++) {
			if (itemKeys[i].equals("-")) {
				menu.addSeparator();
			}
			else if (itemKeys[i].startsWith("@")) {
				JMenu me = createMenu(jcpPanel, itemKeys[i].substring(1));
				menu.add(me);
			}
			else if (itemKeys[i].endsWith("+")) {
				JMenuItem mi = createMenuItem(jcpPanel,
						itemKeys[i].substring(0, itemKeys[i].length() - 1),
						true, false
						);
				if(itemKeys[i].substring(0, itemKeys[i].length() - 1).equals("addImplHydrogen"))
					((JCheckBoxMenuItem)mi).setSelected(true);
				if(itemKeys[i].substring(0, itemKeys[i].length() - 1).equals("insertstructure") && !jcpPanel.guiString.equals("applet"))
					((JCheckBoxMenuItem)mi).setSelected(true);
				// default off, because we cannot turn it on anywhere (yet)
				menu.add(mi);
			}
			else {
				JMenuItem mi = createMenuItem(jcpPanel, itemKeys[i], false, false);
				menu.add(mi);
			}
		}
		return menu;
	}


	/**
	 *  Gets the menuResourceString attribute of the JChemPaint object
	 *
	 * @param  key  Description of the Parameter
	 * @return      The menuResourceString value
	 */
	public String getMenuResourceString(String key) {
		String str;
		try {
			str = JCPPropertyHandler.getInstance().getGUIDefinition(this.guiString).getString(key);
		} catch (MissingResourceException mre) {
			str = null;
		}
		return str;
	}


	/**
	 *  Adds ShortCuts to the JChemPaintMenuBar object
	 *
	 * @param  cmd  String The Strin to identify the MenuItem
	 * @param  mi   the regarding MenuItem
	 * @param  jcp  The feature to be added to the ShortCuts attribute
	 */
	private void addShortCuts(String cmd, JMenuItem mi, JChemPaintPanel jcp) {
		Properties shortCutProps = JCPPropertyHandler.getInstance().getJCPShort_Cuts();
		String shortCuts = shortCutProps.getProperty(cmd);
		String charString = null;
		if (shortCuts != null) {
			try {
				String[] scStrings = shortCuts.trim().split(",");
				if (scStrings.length > 1) {
					charString = scStrings[1];
					String altKey = scStrings[0] + "_MASK";
					Field field = Class.forName("java.awt.event.InputEvent").getField(altKey);
					int i = field.getInt(Class.forName("java.awt.event.InputEvent"));
					mi.setAccelerator(KeyStroke.getKeyStroke(charString.charAt(0), i));
					jcp.registerKeyboardAction(mi.getActionListeners()[0], charString, KeyStroke.getKeyStroke(charString.charAt(0), i), JComponent.WHEN_IN_FOCUSED_WINDOW);
				}
				else {
					charString = "VK_" + scStrings[0];
					Field field = Class.forName("java.awt.event.KeyEvent").getField(charString);
					int i = field.getInt(Class.forName("java.awt.event.KeyEvent"));
					mi.setAccelerator(KeyStroke.getKeyStroke(i, 0));
					jcp.registerKeyboardAction(mi.getActionListeners()[0], charString, KeyStroke.getKeyStroke(i, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
				}
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			} catch (NoSuchFieldException nsfe) {
				nsfe.printStackTrace();
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			}
		}
	}

	/**
	 *  Craetes a JMenuItem given by a String and adds the right ActionListener to
	 *  it.
	 *
	 * @param  cmd         String The Strin to identify the MenuItem
	 * @param  jcpPanel    Description of the Parameter
	 * @param  isCheckBox  Description of the Parameter
	 * @param  isChecked   Description of the Parameter
	 * @return             JMenuItem The created JMenuItem
	 */
	protected JMenuItem createMenuItem(JChemPaintPanel jcpPanel, String cmd, boolean isCheckBox, boolean isChecked) {
		logger.debug("Creating menu item: ", cmd);
		String translation = "***" + cmd + "***";
		try {
			translation = JCPLocalizationHandler.getInstance().getString(cmd);
			logger.debug("Found translation: ", translation);
		} catch (MissingResourceException mre) {
			logger.error("Could not find translation for: " + cmd);
		}
		JMenuItem mi = null;
		if (isCheckBox) {
			mi = new JCheckBoxMenuItem(translation);
			mi.setSelected(isChecked);
		}
		else {
			mi = new JMenuItem(translation);
		}
		logger.debug("Created new menu item...");
		String astr = JCPPropertyHandler.getInstance().getResourceString(cmd + JCPAction.actionSuffix);
        if (astr == null) {
			astr = cmd;
		}
		mi.setActionCommand(astr);
		JCPAction action = jcpPanel.getJCPAction().getAction(jcpPanel, astr);
		if (action != null) {
			// sync some action properties with menu
			mi.setEnabled(action.isEnabled());
			mi.addActionListener(action);
			logger.debug("Coupled action to new menu item...");
		}
		else {
			logger.error("Could not find JCPAction class for:" + astr);
			mi.setEnabled(false);
		}
		addShortCuts(cmd, mi, jcpPanel);
		return mi;
	}

}

