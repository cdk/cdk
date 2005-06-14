/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005  The JChemPaint project
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

import java.util.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;

import org.openscience.cdk.tools.*;
import org.openscience.cdk.applications.jchempaint.action.JCPAction;
import org.openscience.cdk.ChemModel;


/**
 *  JPanel that contains a a viewer only JChemPaint panel.
 *
 *@author        steinbeck
 *@created       22. April 2005
 *@cdk.module    jchempaint
 */
public class JChemPaintViewerOnlyPanel extends JChemPaintPanel {

	private LoggingTool logger;
	JPopupMenu viewerPanelPopupMenu;
	

	/**
	 *  Constructor for the JChemPaintViewerOnlyPanel object
	 */
	public JChemPaintViewerOnlyPanel() {
		this(null);
	}


	/**
	 *  Constructor for the JChemPaintViewerOnlyPanel object
	 *
	 *@param  panelDimension  Description of the Parameter
	 */
	public JChemPaintViewerOnlyPanel(Dimension panelDimension) {
		super();
		super.setJChemPaintModel(new JChemPaintModel());
		setShowToolBar(false);
		setShowStatusBar(false);
		setShowMenuBar(false);
		setViewerOnly();
		buildFilePopUpMenu();
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
	 *  Description of the Method
	 */
	private void buildFilePopUpMenu() {
		String key = "viewepopupmenubar";
		String[] itemKeys = StringHelper.tokenize(getMenuResourceString(key));
		viewerPanelPopupMenu = new JPopupMenu();
		for (int i = 0; i < itemKeys.length; i++) {
			String cmd = itemKeys[i];
			if (cmd.equals("-")) {
				viewerPanelPopupMenu.addSeparator();
				continue;
			}
			String translation = "***" + cmd + "***";
			try {
				translation = JCPLocalizationHandler.getInstance().getString(cmd);
			} catch (MissingResourceException mre) {
			}
			JMenuItem mi = new JMenuItem(translation);
			String astr = JCPPropertyHandler.getInstance().getResourceString(cmd + JCPAction.actionSuffix);
			if (astr == null) {
				astr = cmd;
			}
			mi.setActionCommand(astr);
			JCPAction action = this.getJCPAction().getAction(this, astr);
			if (action != null) {
				// sync some action properties with menu
				mi.setEnabled(action.isEnabled());
				mi.addActionListener(action);
			} else {
				logger.error("Could not find JCPAction class for:" + astr);
				mi.setEnabled(false);
			}
			viewerPanelPopupMenu.add(mi);
		}
		getDrawingPanel().add(viewerPanelPopupMenu);
		MouseListener popupListener = new PopupListener();
		getDrawingPanel().addMouseListener(popupListener);
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



	/**
	 *  If event is an closing event from JChemPaintEditorPanel which was opened by this viewer,
	 *  sync the JChemPaintModels
	 *  Description of the Class
	 *
	 *@author     thelmus
	 *@created    18. Mai 2005
	 */
	class PopupListener extends MouseAdapter {
		/**
		 *  Description of the Method
		 *
		 *@param  e  Description of the Parameter
		 */
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}


		/**
		 *  Description of the Method
		 *
		 *@param  e  Description of the Parameter
		 */
		public void mouseReleased(MouseEvent e) {
			//maybeShowPopup(e);
		}


		/**
		 *  Description of the Method
		 *
		 *@param  e  Description of the Parameter
		 */
		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				viewerPanelPopupMenu.show(e.getComponent(),
						e.getX(), e.getY());
			} else {
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					JChemPaintPanel viewerPanel = (JChemPaintPanel) getComponent(0).getParent();
					Object editorModel = viewerPanel.getJChemPaintModel().getChemModel().clone();
					JChemPaintModel thisModel = new JChemPaintModel();
					thisModel.setChemModel((ChemModel) editorModel);
					JFrame frame = JChemPaintEditorPanel.getNewFrame(thisModel);
					JChemPaintEditorPanel editorPanel = (JChemPaintEditorPanel) frame.getContentPane().getComponent(0);
					editorPanel.addChangeListener(viewerPanel);
					editorPanel.setIsOpenedByViewer(true);
					frame.show();
					frame.pack();
					setViewerOnly();
					editorPanel.setJChemPaintModel(thisModel);
				}
			}
		}
	}



	/**
	 *  If event is an closing event from JChemPaintEditorPanel which was opened by this viewer,
	 *  sync the JChemPaintModels
	 *
	 *@param  event  ChangeEvent
	 */
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() instanceof JChemPaintEditorPanel) {
			JChemPaintEditorPanel editorPanel = (JChemPaintEditorPanel) event.getSource();
			if (editorPanel.getLastEventReason() == JChemPaintEditorPanel.JCP_CLOSING) {
				setViewerOnly();
				setJChemPaintModel(editorPanel.getJChemPaintModel());
			}
		}
		super.stateChanged(event);
	}
}


