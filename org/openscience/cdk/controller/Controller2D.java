/* Controller2D.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.controller;


import org.openscience.cdk.renderer.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.*;
import java.awt.*;


public class Controller2D 
{
	boolean debug = false;
	Renderer2D renderer;
	Renderer2DModel r2dm;
	Molecule molecule;
	Container panel;
	
	
	public Controller2D(Container panel, Molecule molecule, Renderer2D renderer)
	{
		this.molecule = molecule;
		this.panel = panel;
		this.renderer = renderer;
		this.r2dm = renderer.r2dm;
	}
	
	public void mouseMoved(int mouseX, int mouseY)
	{
		/** highlighting **/
		renderer.colorHash.clear();
		double highlightRadius = r2dm.getHighlightRadius();
		double atomX = 0, atomY = 0;
		Atom closestAtom = GeometryTools.getClosestAtom(mouseX, mouseY, molecule);
		if (debug) System.out.println("closestAtom  "+ closestAtom);
		if (Math.sqrt(Math.pow(closestAtom.getX2D() - mouseX, 2) + Math.pow(closestAtom.getY2D() - mouseY, 2)) < highlightRadius)
		{
			renderer.colorHash.put(closestAtom, Color.red);
		}
		
		Bond closestBond = GeometryTools.getClosestBond(mouseX, mouseY, molecule);
		if (debug) System.out.println("closestBond  "+ closestBond);
		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(closestBond),highlightRadius);
		int[] xCoords = {coords[0],coords[2],coords[4],coords[6]};
		int[] yCoords = {coords[1],coords[3],coords[5],coords[7]};
		if ((new Polygon(xCoords, yCoords, 4)).contains(new Point(mouseX, mouseY)))
		{
			renderer.colorHash.put(closestBond, Color.green);;
		}		
		panel.repaint();
		
	}

}
