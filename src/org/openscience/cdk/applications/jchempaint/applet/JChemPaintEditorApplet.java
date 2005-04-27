/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2005  The Jmol Development Team
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.applet;

import java.awt.*;

import javax.vecmath.Point2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.applications.jchempaint.*;

import javax.swing.JApplet;

/**
 * The
 *
 * @cdk.module jchempaint.applet
 * @author     steinbeck
 * @created    22. April 2005
 */
public class JChemPaintEditorApplet extends JApplet
{
	JChemPaintEditorPanel jcpep = null;
	
	public void init()
	{
		getContentPane().setLayout(new BorderLayout());
		jcpep = new JChemPaintEditorPanel();
		getContentPane().add(jcpep, BorderLayout.CENTER);
	}
	
	public void start()
	{
		//Parameter parsing goes here
	
	}
	
	public void stop()
	{
	
	}

}

