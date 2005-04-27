/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.net.URL;
import java.awt.RenderingHints;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.controller.*;
import org.openscience.cdk.applications.jchempaint.action.*;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This class implements an editing JChemPaintPanel.
 *
 * @author        steinbeck
 * @created       16. Februar 2005
 * @cdk.module    jchempaint
 * @see           JChemPaintViewerPanel
 */
public class MainContainerPanel extends JPanel implements SwingConstants {

	ReallyPaintPanel drawingPanel;
	private static LoggingTool logger;
	/**  Description of the Field */
	public JButton selectButton;
	JToolBar chemjtoolbar;
	JChemPaintPanel jcpp;

	/**  Description of the Field */
	public final static String TIPSUFFIX = "Tooltip";

	/**
	 *Constructor for the MainContainerPanel object
	 *
	 * @param  inputAdapter  Description of the Parameter
	 * @param  jcpm          Description of the Parameter
	 * @param  jcpp          Description of the Parameter
	 */
	public MainContainerPanel(Controller2D inputAdapter, JChemPaintModel jcpm, JChemPaintPanel jcpp) {
		super();
		this.jcpp = jcpp;
		if (logger == null) {
			logger = new LoggingTool(this);
		}
		setLayout(new BorderLayout());
		chemjtoolbar = (JToolBar) createToolbar(HORIZONTAL, "toolbar");
		add(chemjtoolbar, BorderLayout.NORTH);
		drawingPanel = new ReallyPaintPanel(jcpm);
		drawingPanel.setOpaque(true);
		drawingPanel.setBackground(Color.white);
		drawingPanel.addMouseListener(inputAdapter);
		drawingPanel.addMouseMotionListener(inputAdapter);
		JScrollPane scrollPane = new JScrollPane(drawingPanel);
		add(scrollPane, BorderLayout.CENTER);
	}


	/**
	 *  Gets the toolbar attribute of the MainContainerPanel object
	 *
	 * @return    The toolbar value
	 */
	public JToolBar getToolbar() {
		return (chemjtoolbar);
	}


	/**
	 *  Gets the menuResourceString attribute of the JChemPaint object
	 *
	 * @param  key  Description of the Parameter
	 * @return      The menuResourceString value
	 */
	public String getToolbarResourceString(String key) {
		String str;
		try {
			str = JCPPropertyHandler.getInstance().getGUIDefinition().getString(key);
		} catch (MissingResourceException mre) {
			str = null;
		}
		return str;
	}


	/**
	 *  Creates a JButton given by a String with an Image and adds the right
	 *  ActionListener to it.
	 *
	 * @param  key  String The string used to identify the button
	 * @return      JButton The JButton with already added ActionListener
	 */

	protected JButton createToolbarButton(String key) {
		JCPPropertyHandler jcpph = JCPPropertyHandler.getInstance();
		logger.debug("Trying to find resource for key: ", key);
		URL url = jcpph.getResource(key + JCPAction.imageSuffix);
		logger.debug("Trying to find resource: ", url);
		if (url == null) {
			logger.error("Cannot find resource: ", key, JCPAction.imageSuffix);
			return null;
		}
		ImageIcon image = new ImageIcon(url);
		if (image == null) {
			logger.error("Cannot find image: ", url);
			return null;
		}
		JButton b =
			new JButton(image) {
				public float getAlignmentY() {
					return 0.5f;
				}
			};
		b.setRequestFocusEnabled(false);
		b.setMargin(new Insets(1, 1, 1, 1));
		String astr = jcpph.getResourceString(key + JCPAction.actionSuffix);
		if (astr == null) {
			astr = key;
		}
		JCPAction a = JCPAction.getAction(jcpp, astr);
		if (a != null) {
			b.setActionCommand(astr);
			logger.debug("Coupling action to button...");
			b.addActionListener(a);
			b.setEnabled(a.isEnabled());
		}
		else {
			logger.error("Could not find JCPAction class for:", astr);
			b.setEnabled(false);
		}
		try {
			String tip = JCPLocalizationHandler.getInstance().getString(key + TIPSUFFIX);
			if (tip != null) {
				b.setToolTipText(tip);
			}
		} catch (MissingResourceException e) {
			logger.warn("Could not find Tooltip resource for: ", key);
			logger.debug(e);
		}
		return b;
	}


	/**
	 *  Creates a toolbar given by a String with all the buttons that are specified
	 *  in the properties file.
	 *
	 * @param  orientation  int The orientation of the toolbar
	 * @param  kind         String The String used to identify the toolbar
	 * @return              Component The created toolbar
	 */
	private Component createToolbar(int orientation, String kind) {
		JToolBar toolbar2 = new JToolBar(orientation);
		String[] toolKeys = StringHelper.tokenize(getToolbarResourceString(kind));
		JButton button = null;

		if (toolKeys.length != 0) {
			String[] sdiToolKeys = new String[(toolKeys.length) - 4];
			for (int i = 4; i < toolKeys.length; i++) {
				int j = i - 4;
				sdiToolKeys[j] = toolKeys[i];
			}
			toolKeys = sdiToolKeys;
		}

		for (int i = 0; i < toolKeys.length; i++) {
			if (toolKeys[i].equals("-")) {
				if (orientation == HORIZONTAL) {
					toolbar2.add(Box.createHorizontalStrut(5));
				}
				else if (orientation == VERTICAL) {
					toolbar2.add(Box.createVerticalStrut(5));
				}
			}
			else {
				button = (JButton) createToolbarButton(toolKeys[i]);
				if (toolKeys[i].equals("lasso")) {
					selectButton = button;
				}
				if (button != null) {
					toolbar2.add(button);
					if (i == 0) {
						button.setBackground(Color.GRAY);
					}
					else {
						button.setBackground(Color.LIGHT_GRAY);
					}
				}
				else {
					logger.error("Could not create button");
				}
			}
		}
		if (orientation == HORIZONTAL) {
			toolbar2.add(Box.createHorizontalGlue());
		}
		return toolbar2;
	}
}

