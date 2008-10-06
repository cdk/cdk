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

import javax.vecmath.Point2d;

/**
 * Interface that Controller2D modules must implement. Each module is
 * associated with an editing mode (DRAWMODE_*), as given in
 * Controller2DModel.
 * 
 * @author egonw
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.module  control
 *
 * @see    Controller2DModel
 */
public interface IController2DModule {

	public abstract void setChemModelRelay(IChemModelRelay relay);
	//public abstract void setEventRelay(IViewEventRelay relay);
	/**
	 * @param worldCoord
	 */
	public abstract void mouseClickedUp(Point2d worldCoord);

	/**
	 * @param worldCoord
	 */
	public abstract void mouseClickedDown(Point2d worldCoord);

	/**
	 * @param worldCoord
	 */
	public abstract void mouseClickedDouble(Point2d worldCoord);

	/**
	 * @param worldCoord
	 */
	public abstract void mouseMove(Point2d worldCoord);

	/**
	 * @param worldCoord
	 */
	public abstract void mouseEnter(Point2d worldCoord);

	/**
	 * @param worldCoord
	 */
	public abstract void mouseExit(Point2d worldCoord);

	/**
	 * @param worldCoordFrom
	 * @param worldCoordTo
	 */
	public abstract void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo);

}
