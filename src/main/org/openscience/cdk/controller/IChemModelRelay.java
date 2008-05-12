/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-09-02 11:46:10 +0100 (su, 02 sep 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.lists.sf>
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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.IJava2DRenderer;

/**
 * @cdk.module render
 */
public interface IChemModelRelay {

	public abstract IAtom getClosestAtom(Point2d worldCoord);
	
	public abstract void removeAtom(IAtom atom);

	public abstract IBond getClosestBond(Point2d worldCoord);

	public abstract void updateView();
	
	public abstract IController2DModel getController2DModel();

	public abstract IJava2DRenderer getIJava2DRenderer();
	public abstract IChemModel getIChemModel();
	public abstract void addAtom(String atomType, Point2d worldcoord);
	
}
