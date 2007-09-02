/* $Revision: 7636 $ $Author: egonw $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.controller;

/**
 * Class that will central interaction point between a mouse event throwing
 * widget (SWT or Swing) and the Controller2D modules.
 * 
 * <p>FIXME: will replace the old Controller2D class.
 * 
 * @author egonw
 */
public class Controller2DHub implements IMouseEventRelay {
	
	private Controller2DModel controllerModel; 
	
	/**
	 * Sets the <code>Controller2DModel</code> associated with
	 * this hub.
	 * 
	 * @param model
	 */
	public void setControllerModel(Controller2DModel model){
		this.controllerModel = model;
	}

	/**
	 * Returns the <code>Controller2DModel</code> associated with
	 * this hub.
	 * 
	 * @param model
	 */
	public Controller2DModel getControllerModel() {
		return this.controllerModel;
	}

	public void mouseClickedDouble(int screenCoordX, int screenCoordY) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClickedDown(int screenCoordX, int screenCoordY) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClickedUp(int screenCoordX, int screenCoordY) {
		// TODO Auto-generated method stub
		
	}

	public void mouseDrag(int screenCoordXFrom, int screenCoordYFrom, int screenCoordXTo, int screenCoordYTo) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEnter(int screenCoordX, int screenCoordY) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExit(int screenCoordX, int screenCoordY) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMove(int screenCoordX, int screenCoordY) {
		// TODO Auto-generated method stub
		
	}
	
}
