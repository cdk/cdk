/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-04 18:26:00 +0100 (do, 04 jan 2007) $
 *  $Revision: 7634 $
 *
 *  Copyright (C) 1997-2007  The JChemPaint project
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
package org.openscience.cdk.renderer.progz;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.net.URL;
import java.util.MissingResourceException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
//import javax.swing.JFrame;

import org.openscience.cdk.tools.LoggingTool;

import org.openscience.cdk.applications.jchempaint.JCPPropertyHandler;
import org.openscience.cdk.applications.jchempaint.StringHelper;

import org.openscience.cdk.applications.jchempaint.JCPLocalizationHandler;
import org.openscience.cdk.applications.jchempaint.JCPConstants;
import org.openscience.cdk.controller.Controller2DHub;

/**
 *  This class makes the JCPToolBar
 *
 *@author        steinbeck
 *@author 		nielsout (changes for java2D version)
 * @cdk.module   jchempaint
 * @cdk.svnrev  $Revision: 9162 $
 */
public class SomeToolBar
{

	private static LoggingTool logger;
	/**
	 *  Gets the toolbar attribute of the MainContainerPanel object
	 *
	 *@return    The toolbar value
	 */
	public static JToolBar getToolbar(TestEditor editor, int lines)
	{
		//Controller2DHub hub
		if (logger == null)
		{
			logger = new LoggingTool(SomeToolBar.class);
		}
		
		JToolBar maintoolbar=(JToolBar)createToolbar(SwingConstants.HORIZONTAL, "toolbar", editor, lines, false);
		JToolBar elementtoolbar=(JToolBar)createToolbar(SwingConstants.HORIZONTAL, "elementtoolbar", editor, lines*2, true);
		maintoolbar.add(elementtoolbar);
		return maintoolbar;
	}


	/**
	 *  Gets the menuResourceString attribute of the JChemPaint object
	 *
	 *@param  key  Description of the Parameter
	 *@return      The menuResourceString value
	 */
	static String getToolbarResourceString(String key)
	{
		String str;
		try
		{
			str = JCPPropertyHandler.getInstance().getGUIDefinition().getString(key);
		} catch (MissingResourceException mre)
		{
			str = null;
		}
		return str;
	}


	/**
	 *  Creates a JButton given by a String with an Image and adds the right
	 *  ActionListener to it.
	 *
	 *@param  key  String The string used to identify the button
	 *@param  elementtype  If true a special type of button for element symbols will be created
	 *@return      JButton The JButton with already added ActionListener
	 */

	static JButton createToolbarButton(String key, TestEditor editor, boolean elementtype)
	{
		JCPPropertyHandler jcpph = JCPPropertyHandler.getInstance();
		JButton b = null;
		if(!elementtype){
			logger.debug("Trying to find resource for key: ", key);
			URL url = jcpph.getResource(key + JCPAction2D.imageSuffix);
			logger.debug("Trying to find resource: ", url);
			if (url == null)
			{
				logger.error("Cannot find resource: ", key, JCPAction2D.imageSuffix);
				return null;
			}
			ImageIcon image = new ImageIcon(url);
			if (image == null)
			{
				logger.error("Cannot find image: ", url);
				return null;
			}
			b =
				new JButton(image)
				{
					private static final long serialVersionUID = 1478990892406874403L;

					public float getAlignmentY()
					{
						return 0.5f;
					}
				};
				String astr = jcpph.getResourceString(key + JCPAction2D.actionSuffix);
				if (astr == null)
				{
					astr = key;
				}
				//FIXME: make it choose the correct controller?
				System.out.println("astr: " + astr + " key: " + key);
				JCPActionChangeMode a = new JCPActionChangeMode(editor, key);
				if (a != null)
				{
					b.setActionCommand(astr);
					logger.debug("Coupling action to button...");
					b.addActionListener(a);
					b.setEnabled(a.isEnabled());
				} else
				{
					logger.error("Could not find JCPAction class for:", astr);
					b.setEnabled(false);
				}
				try
				{
					String tip = JCPLocalizationHandler.getInstance().getString(key + JCPConstants.TIPSUFFIX);
					if (tip != null)
					{
						b.setToolTipText(tip);
					}
				} catch (MissingResourceException e)
				{
					logger.warn("Could not find Tooltip resource for: ", key);
					logger.debug(e);
				}
		}else{
			//FIXME: make the correct actionlistener for the atom types? or whatever these are....

			b=new JButton(key);
			JCPActionChangeMode a = new JCPActionChangeMode(editor, key);
			b.addActionListener(a);
			b.setEnabled(a.isEnabled());
			if(JCPLocalizationHandler.getInstance().getString("singleelementTooltip")!=null)
				b.setToolTipText(JCPLocalizationHandler.getInstance().getString("singleelementTooltip")+" "+key);
		 
			b.setSize(30,15);
			b.setPreferredSize(new Dimension(30,15));
			b.setMaximumSize(new Dimension(30,15));
		}
		b.setRequestFocusEnabled(false);
		b.setMargin(new Insets(1, 1, 1, 1));
	/*a	if(key.equals("bond"))
			jcpp.lastAction.add(b);
		if(key.equals("move"))
			jcpp.moveButton=b;*/
		return b;
	}


	/**
	 *  Creates a toolbar given by a String with all the buttons that are specified
	 *  in the properties file.
	 *
	 *@param  orientation  int The orientation of the toolbar
	 *@param  kind         String The String used to identify the toolbar
	 *@param  elementtype  If true a special type of toolbar for element symbols will be created
	 *@return              Component The created toolbar
	 */
	public static Component createToolbar(int orientation, String kind, TestEditor editor, int lines, boolean elementtype)
	{
		JToolBar toolbar2 = new JToolBar(orientation);
		String[] toolKeys = StringHelper.tokenize(getToolbarResourceString(kind));
		JButton button = null;

		if (toolKeys.length != 0)
		{
			String[] sdiToolKeys = new String[(toolKeys.length)];
			for (int i = 0; i < toolKeys.length; i++)
			{
				int j = i - 0;
				sdiToolKeys[j] = toolKeys[i];
			}
			toolKeys = sdiToolKeys;
		}

		Box box=null;
		int counter=0;
		for (int i = 0; i < toolKeys.length; i++)
		{
			if (toolKeys[i].equals("-"))
			{
				toolbar2.add(box);
				if (orientation == SwingConstants.HORIZONTAL)
				{
					toolbar2.add(Box.createHorizontalStrut(5));
				} else if (orientation == SwingConstants.VERTICAL)
				{
					toolbar2.add(Box.createVerticalStrut(5));
				}
				counter=0;
			} 
			else
			{
				if(counter % lines==0){
					if(box!=null)
						toolbar2.add(box);
					box=new Box(BoxLayout.Y_AXIS);
				}
				button = (JButton) createToolbarButton(toolKeys[i], editor, elementtype);
				/*if (toolKeys[i].equals("lasso"))
				{
					selectButton = button;
				}*/
				if (button != null)
				{
					box.add(button);
					if (toolKeys[i].equals("bond") && !elementtype)
					{
						button.setBackground(Color.GRAY);
					} else
					{
						button.setBackground(Color.LIGHT_GRAY);
					}
				} else
				{
					logger.error("Could not create button"+toolKeys[i]);
				}
				counter++;
			}
		}
		if(box!=null)
				toolbar2.add(box);
		if (orientation == SwingConstants.HORIZONTAL)
		{
			toolbar2.add(Box.createHorizontalGlue());
		}
		return toolbar2;
	}
}

