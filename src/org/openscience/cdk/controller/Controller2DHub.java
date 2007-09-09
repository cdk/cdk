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

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.progz.IJava2DRenderer;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Class that will central interaction point between a mouse event throwing
 * widget (SWT or Swing) and the Controller2D modules.
 * 
 * <p>FIXME: will replace the old Controller2D class.
 * 
 * @author egonw
 */
public class Controller2DHub implements IMouseEventRelay, IChemModelRelay {
	
	private IChemModel chemModel;
	
	private Controller2DModel controllerModel; 
	private IJava2DRenderer renderer;
	
	private List<IController2DModule> generalModules;
	private Map<Controller2DModel.DrawMode,IController2DModule> drawModeModules;
	
	public Controller2DHub(Controller2DModel controllerModel,
		                   IJava2DRenderer renderer,
		                   IChemModel chemModel) {
		this.controllerModel = controllerModel;
		this.renderer = renderer;
		this.chemModel = chemModel;
		
		drawModeModules = new HashMap<Controller2DModel.DrawMode,IController2DModule>();
		generalModules = new ArrayList<IController2DModule>();
	}
	//FIXME: make this attempt to repaint work
	public Controller2DHub(Controller2DModel controllerModel,
            IJava2DRenderer renderer,
            IChemModel chemModel, String painter) {
this.controllerModel = controllerModel;
this.renderer = renderer;
this.chemModel = chemModel;

drawModeModules = new HashMap<Controller2DModel.DrawMode,IController2DModule>();
generalModules = new ArrayList<IController2DModule>();
}
	/**
	 * Register a draw mode that you want to have active for this Controller2DHub.
	 * 
	 * @param drawMode
	 * @param module
	 */
	public void registerDrawModeControllerModule(
		Controller2DModel.DrawMode drawMode, IController2DModule module) {
		module.setChemModelRelay(this);
		drawModeModules.put(drawMode, module);
	}
	
	/**
	 * Adds a general IController2DModule which will catch all mouse events.
	 */
	public void registerGeneralControllerModule(IController2DModule module) {
		module.setChemModelRelay(this);
		generalModules.add(module);
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

	public void mouseDrag(int screenCoordXFrom, int screenCoordYFrom, int screenCoordXTo, int screenCoordYTo, MouseEvent event) {
		// TODO Auto-generated method stub
		Point2d worldCoordFrom = renderer.getCoorFromScreen(screenCoordXFrom, screenCoordYFrom);
		Point2d worldCoordTo = renderer.getCoorFromScreen(screenCoordXTo, screenCoordYTo);
		
	
		// Relay the mouse event to the general handlers
		for (IController2DModule module : generalModules) {
			module.mouseDrag(worldCoordFrom, worldCoordTo, event);
		}

		// Relay the mouse event to the active 
		IController2DModule activeModule = getActiveDrawModule();
		if (activeModule != null) activeModule.mouseDrag(worldCoordFrom, worldCoordTo, event);
	}

	public void mouseEnter(int screenCoordX, int screenCoordY) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExit(int screenCoordX, int screenCoordY) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMove(int screenCoordX, int screenCoordY) {
		Point2d worldCoord = renderer.getCoorFromScreen(screenCoordX, screenCoordY);
	//	System.out.println("Mouse move detected: " + worldCoord);
		
		// Relay the mouse event to the general handlers
		for (IController2DModule module : generalModules) {
			module.mouseMove(worldCoord);
		}

		// Relay the mouse event to the active 
		IController2DModule activeModule = getActiveDrawModule();
		if (activeModule != null) activeModule.mouseMove(worldCoord);
	}

	private IController2DModule getActiveDrawModule() {
		return drawModeModules.get(controllerModel.getDrawMode());
	}

	public IAtom getClosestAtom(Point2d worldCoord) {
		IAtom closestAtom = null;
		double closestDistance = Double.MAX_VALUE;
		
		Iterator<IAtomContainer> containers = ChemModelManipulator.getAllAtomContainers(chemModel).iterator();
		while (containers.hasNext()) {
			Iterator<IAtom> atoms = containers.next().atoms();
			while (atoms.hasNext()) {
				IAtom nextAtom = atoms.next();
				double distance = nextAtom.getPoint2d().distance(worldCoord);
				if (distance <= renderer.getRenderer2DModel().getHighlightRadiusModel() &&
					distance < closestDistance) {
					closestAtom = nextAtom;
					closestDistance = distance;
				}
			}
		}
		
		return closestAtom;
	}

	public IBond getClosestBond(Point2d worldCoord) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
