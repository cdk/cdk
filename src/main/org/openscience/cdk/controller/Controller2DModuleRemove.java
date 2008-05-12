/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
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

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;


/**
 * deletes closest atom on click
 * 
 * @author Niels Out
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.module  control
 */
public class Controller2DModuleRemove implements IController2DModule {

	private IChemModelRelay chemObjectRelay;
	/*private IViewEventRelay eventRelay;
	public void setEventRelay(IViewEventRelay relay) {
		this.eventRelay = relay;
	}*/
	
	public void mouseClickedDouble(Point2d worldCoord) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClickedDown(Point2d worldCoord) {
		// TODO Auto-generated method stub
		IAtom atom = chemObjectRelay.getClosestAtom(worldCoord);
		System.out.println("trying to remove: " + atom);
		if (atom != null) {
			chemObjectRelay.removeAtom(atom);
			chemObjectRelay.updateView();
		}
			
	}

	public void mouseClickedUp(Point2d worldCoord) {
		// TODO Auto-generated method stub
		
	}

	public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo) {
	
	}

	public void mouseEnter(Point2d worldCoord) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExit(Point2d worldCoord) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMove(Point2d worldCoord) {
		
	}

	public void setChemModelRelay(IChemModelRelay relay) {
		this.chemObjectRelay = relay;
	}
	
}
