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
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.controller.*;

/**
 * JChemPaint menu actions
 *
 * @author     nielsout
 */
public class JCPActionChangeMode extends AbstractAction
{

	private static final long serialVersionUID = -4056416630614934238L;
	
	
	protected static LoggingTool logger = null;

	/**
	 *  Description of the Field
	 */
	protected IController2DModule module;
	private String key;
	private Controller2DHub hub;
	
	/**
	 *  Description of the Field
	 */
	//protected JChemPaintPanel jcpPanel = null;
	protected JFrame jcpPanel = null;

	/**
	 *  Is this popup action assiociated with a PopupMenu or not.
	 */
	private boolean isPopupAction;

	public JCPActionChangeMode(Controller2DHub hub, String key)
	{
		this.hub = hub;
		this.key = key;
		System.out.println("the key: " + key);
		if (key.equals("move")) {
			module = new Controller2DModuleMove();
			System.out.println("Controller2DModuleMove button started..!");
		}
		else if (key.equals("eraser")) {
			module = new Controller2DModuleRemove();
			System.out.println("Controller2DModuleRemove button started..!");
		}
		else if (key.equals("plus")) {
			module = new Controller2DModuleChangeFormalC(1);
		}
		else if (key.equals("minus")) {
			module = new Controller2DModuleChangeFormalC(-1);
		}
		else if (key.length() == 1) {
			//I assume something with length of 1 is an atom name (C/H/O/N/etc.)
			module = new Controller2DModuleAddAtom(key);
		}
	}
	 public void actionPerformed(ActionEvent e) {
	       // logger.info("  module  ", module);
	       // logger.debug("  source ", e.getSource());
	    //    System.out.println("  type  " + module.toString());
	        
	    //    JChemPaintModel jcpModel = jcpPanel.getJChemPaintModel();
	    //    Controller2DModel renderModel = jcpModel.getControllerModel();
	    //    renderModel.setDrawElement(symbol);
	    //    renderModel.setDrawMode(Controller2DModel.DrawMode.ELEMENT);
	   //     ((JButton)jcpPanel.lastAction.get(0)).setBackground(Color.LIGHT_GRAY);
			((JComponent) e.getSource()).setBackground(Color.GRAY);
	  //      jcpPanel.lastAction.set(0,(JComponent) e.getSource());
			
			//FIXME: perhaps some 'default' controller should always stay
			hub.unRegisterAllControllerModule();

			if (module == null)
				System.out.println("empty module selected!");
			else {
				hub.registerGeneralControllerModule(module);
				System.out.println("module " + module + " listening now..");
			}
	    }


	
}

