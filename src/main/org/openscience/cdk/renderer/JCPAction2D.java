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
package org.openscience.cdk.renderer;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openscience.cdk.controller.CDKPopupMenu;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Superclass of all JChemPaint GUI actions
 *
 * @cdk.module  jchempaint
 * @cdk.githash
 * @author      steinbeck
 */
public class JCPAction2D extends AbstractAction
{

	private static final long serialVersionUID = -4056416630614934238L;
	
	/**
	 *  Description of the Field
	 */
	public final static String actionSuffix = "Action";
	/**
	 *  Description of the Field
	 */
	public final static String imageSuffix = "Image";
	/**
	 *  Description of the Field
	 */
	public final static String labelSuffix = "Label";

	/**
	 *  Description of the Field
	 */
	protected static ILoggingTool logger =
	    LoggingToolFactory.createLoggingTool(JCPAction2D.class);

	private Hashtable actions = null;
	private Hashtable popupActions = null;

	/**
	 *  Description of the Field
	 */
	protected String type;

	/**
	 *  Description of the Field
	 */
	//protected JChemPaintPanel jcpPanel = null;
	protected JFrame jcpPanel = null;

	/**
	 *  Is this popup action assiociated with a PopupMenu or not.
	 */
	private boolean isPopupAction;


	/**
	 *  Constructor for the JCPAction object
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  type           Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 */
	public JCPAction2D(JFrame jcpPanel, String type, boolean isPopupAction)
	{
		super();
		logger.debug("JCPAction->type: " + type);
		if (this.actions == null)
		{
			this.actions = new Hashtable();
		}
		if (this.popupActions == null)
		{
			this.popupActions = new Hashtable();
		}
		this.type = "";
		this.isPopupAction = isPopupAction;
	//	this.jcpPanel = jcpPanel;
	}


	/**
	 *  Constructor for the JCPAction object
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 */
	public JCPAction2D(JFrame jcpPanel, boolean isPopupAction)
	{
		this(jcpPanel, "", isPopupAction);
	}


	/**
	 *  Constructor for the JCPAction object
	 *
	 *@param  jcpPanel  Description of the Parameter
	 */
	public JCPAction2D(JFrame jcpPanel)
	{
		this(jcpPanel, false);
	}


	/**
	 *  Constructor for the JCPAction object
	 */
	public JCPAction2D()
	{
		this(null);
	}


	/**
	 *  Sets the type attribute of the JCPAction object
	 *
	 *@param  type  The new type value
	 */
	public void setType(String type)
	{
		this.type = type;
	}


	/**
	 *  Sets the jChemPaintPanel attribute of the JCPAction object
	 *
	 *@param  jcpPanel  The new jChemPaintPanel value
	 */
	public void setJChemPaintPanel(JFrame jcpPanel)
	{
		this.jcpPanel = jcpPanel;
	}


	/**
	 *  Is this action runnable?
	 *
	 *@return    The enabled value
	 */
	public boolean isEnabled()
	{
		return true;
	}


	/**
	 *  Gets the popupAction attribute of the JCPAction object
	 *
	 *@return    The popupAction value
	 */
	public boolean isPopupAction()
	{
		return isPopupAction;
	}


	/**
	 *  Sets the isPopupAction attribute of the JCPAction object
	 *
	 *@param  isPopupAction  The new isPopupAction value
	 */
	public void setIsPopupAction(boolean isPopupAction)
	{
		this.isPopupAction = isPopupAction;
	}


	/**
	 *  Dummy method.
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e)
	{
	}


	/**
	 *  Gets the source attribute of the JCPAction object
	 *
	 *@param  event  Description of the Parameter
	 *@return        The source value
	 */
	public IChemObject getSource(ActionEvent event)
	{
		Object source = event.getSource();
		logger.debug("event source: ", source);
		if (source instanceof JMenuItem)
		{
			Container parent = ((JMenuItem) source).getComponent().getParent();
			// logger.debug("event source parent: " + parent);
			if (parent instanceof CDKPopupMenu)
			{
				return ((CDKPopupMenu) parent).getSource();
			} else if (parent instanceof JPopupMenu)
			{
				// assume that the top menu is indeed a CDKPopupMenu
				logger.debug("Submenu... need to recurse into CDKPopupMenu...");
				while (!(parent instanceof CDKPopupMenu))
				{
					logger.debug("  Parent instanceof ", parent.getClass().getName());
					if (parent instanceof JPopupMenu)
					{
						parent = ((JPopupMenu) parent).getInvoker().getParent();
					} /*else if (parent instanceof JChemPaintMenuBar)
					{
						logger.warn(" Source is MenuBar. MenuBar items don't know about the source");
						return null;
					}*/ else
					{
						logger.error(" Cannot get parent!");
						return null;
					}
				}
				return ((CDKPopupMenu) parent).getSource();
			}
		}
		return null;
	}


	/**
	 *  Gets the action attribute of the JCPAction class
	 *
	 *@param  jcpPanel       Description of the Parameter
	 *@param  actionname     Description of the Parameter
	 *@param  isPopupAction  Description of the Parameter
	 *@return                The action value
	 */
	public JCPAction2D getAction(JFrame jcpPanel, String actionname, boolean isPopupAction)
	{
		// make sure logger and actions are instantiated
		JCPAction2D dummy = new JCPAction2D(jcpPanel);

		// extract type
		String type = "";
		String classname = "";
		int index = actionname.indexOf("@");
		if (index >= 0)
		{
			classname = actionname.substring(0, index);
			// FIXME: it should actually properly check whether there are more chars
			// than just the "@".
			type = actionname.substring(index + 1);
		} else
		{
			classname = actionname;
		}
		logger.debug("Action class: ", classname);
		logger.debug("Action type:  ", type);

		// now get actual JCPAction class
		if (!isPopupAction && actions.containsKey(actionname))
		{
			logger.debug("Taking JCPAction from action cache for:", actionname);
			return (JCPAction2D) actions.get(actionname);
		} else if (isPopupAction && popupActions.containsKey(actionname))
		{
			logger.debug("Taking JCPAction from popup cache for:", actionname);
			return (JCPAction2D) popupActions.get(actionname);
		} else
		{
			logger.debug("Loading JCPAction class for:", classname);
			Object o = null;
			try
			{
				// because 'this' is static, it cannot be used to get a classloader,
				// therefore use logger instead
				o = dummy.getClass().getClassLoader().loadClass(classname).newInstance();
			} catch (Exception exc)
			{
				logger.error("Could not find/instantiate class: ", classname);
				logger.debug(exc);
				return dummy;
			}
			if (o instanceof JCPAction2D)
			{
				JCPAction2D a = (JCPAction2D) o;
				a.setJChemPaintPanel(jcpPanel);
				if (type.length() > 0)
				{
					a.setType(type);
				}
				if (isPopupAction)
				{
					popupActions.put(actionname, a);
				} else
				{
					actions.put(actionname, a);
				}
				return a;
			} else
			{
				logger.error("Action is not a JCPAction!");
			}
		}
		return dummy;
	}


	/**
	 *  Gets the action attribute of the JCPAction class
	 *
	 *@param  jcpPanel    Description of the Parameter
	 *@param  actionname  Description of the Parameter
	 *@return             The action value
	 */
	public JCPAction2D getAction(JFrame jcpPanel, String actionname)
	{
		return getAction(jcpPanel, actionname, false);
	}
}

