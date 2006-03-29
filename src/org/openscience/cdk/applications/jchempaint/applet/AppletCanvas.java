/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2006  The Jmol Development Team
 *
 *  Contact: jmol-developers@lists.sf.net
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
package org.openscience.cdk.applications.jchempaint.applet;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.vecmath.Point2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * The canvas for a JChempaint applet
 *
 * @cdk.module jchempaint.applet
 * @author     steinbeck
 */
public class AppletCanvas extends Canvas
{

	private Renderer2D renderer;
	private JChemPaintModel model;


	/**
	 *  Sets the renderer attribute of the AppletCanvas object
	 *
	 *@param  renderer  The new renderer value
	 */
	public void setRenderer(Renderer2D renderer)
	{
		this.renderer = renderer;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  g  Description of the Parameter
	 */
	public void update(Graphics g)
	{
		paint(g);
	}


	/**
	 *  Sets the jChemPaintModel attribute of the AppletCanvas object
	 *
	 *@param  model  The new jChemPaintModel value
	 */
	public void setJChemPaintModel(JChemPaintModel model)
	{
		this.model = model;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  g  Description of the Parameter
	 */
	public void paint(Graphics g)
	{
		renderer.getRenderer2DModel().setBackgroundDimension(getSize());
		IAtomContainer container = null;
		if (model != null)
		{
			container = ChemModelManipulator.getAllInOneContainer(model.getChemModel());
		} else
		{
			Atom carbon = new Atom("C");
			carbon.setHydrogenCount(4);
			carbon.setPoint2d(new Point2d(1.0, 1.0));
			container.addAtom(carbon);
		}
		/*
		 *  this code ensures that the molecule ends up somewhere in the model
		 *  of the view screen
		 */
		GeometryTools.translateAllPositive(container);
		double scaleFactor = GeometryTools.getScaleFactor(container, 40.0);
		GeometryTools.scaleMolecule(container, scaleFactor);
		GeometryTools.center(container, getSize());

		setBackground(Color.white);
		if (model != null)
		{
			renderer.paintChemModel(model.getChemModel(), (Graphics2D) g);
		} else
		{
			renderer.paintMolecule(container, (Graphics2D) g,false,true);
		}
	}

}

