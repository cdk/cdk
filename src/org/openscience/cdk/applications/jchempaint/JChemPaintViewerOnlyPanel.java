/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2006  The JChemPaint project
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

import java.awt.Dimension;
import java.util.MissingResourceException;

import org.openscience.cdk.tools.LoggingTool;


/**
 *  JPanel that contains a a viewer only JChemPaint panel.
 *
 *@author        steinbeck
 *@cdk.module    jchempaint
 */
public class JChemPaintViewerOnlyPanel extends JChemPaintPanel {

	private static final long serialVersionUID = -7964104199305244137L;
	private LoggingTool logger;
//	JPopupMenu popupMenu;
	

	/**
	 *  Constructor for the JChemPaintViewerOnlyPanel object
	 */
	public JChemPaintViewerOnlyPanel() {
		this(null, "stable");
	}


	/**
	 *  Constructor for the JChemPaintViewerOnlyPanel object
	 *
	 *@param  panelDimension  Description of the Parameter
	 */
	public JChemPaintViewerOnlyPanel(Dimension panelDimension, String guiString) {
		super();
		this.guiString=guiString;
		super.setJChemPaintModel(new JChemPaintModel());
		setViewerOnly();
//		buildFilePopUpMenu();
		logger = new LoggingTool(this);
		if (panelDimension != null) {
			super.getJChemPaintModel().getRendererModel().setBackgroundDimension(panelDimension);
			viewerDimension = new Dimension(((int) panelDimension.getWidth()) + 10, ((int) panelDimension.getHeight() + 10));
			super.setPreferredSize(viewerDimension);
			viewerDimension = getJChemPaintModel().getRendererModel().getBackgroundDimension();
		}
		else {
			viewerDimension = panelDimension;
		}
	}



	/**
	 *  Gets the menuResourceString attribute of the JChemPaintViewerOnlyPanel
	 *  object
	 *
	 *@param  key  Description of the Parameter
	 *@return      The menuResourceString value
	 */
	public String getMenuResourceString(String key) {
		String str;
		try {
			str = JCPPropertyHandler.getInstance().getGUIDefinition().getString(key);
		} catch (MissingResourceException mre) {
			str = null;
		}
		return str;
	}
}


