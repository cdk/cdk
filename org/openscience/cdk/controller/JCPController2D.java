/* JCPController2D.java
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


import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.vecmath.*;



public class JCPController2D 
{
	boolean debug = false;
	Renderer2DModel r2dm;
	AtomContainer atomCon;
	JCPController2DModel c2dm = new JCPController2DModel();
	boolean wasDragged = false;
	
	
	

	public JCPController2D(AtomContainer atomCon, Renderer2DModel r2dm)
	{
		this.atomCon = atomCon;
		this.r2dm = r2dm;
	}
	

	/**
	 * manages all actions that will be invoked when the mouse is moved
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void mouseMoved(MouseEvent e)
	{
		double highlightRadius = r2dm.getHighlightRadius();
		int mouseX = e.getX(), mouseY = e.getY();
		Atom atomInRange;
		Bond bondInRange;
		
		/** highlighting **/
		atomInRange = getAtomInRange(mouseX, mouseY);
		if (atomInRange != null)
		{
			r2dm.setHighlightedAtom(atomInRange);
			r2dm.setHighlightedBond(null);
		}
		
		else
		{
			r2dm.setHighlightedAtom(null);
			bondInRange = getBondInRange(mouseX, mouseY);
			if (bondInRange != null)
			{
				r2dm.setHighlightedBond(bondInRange);
			}
			else
			{
				r2dm.setHighlightedBond(null);
			}	
		}
	}
	
	
	/**
	 * manages all actions that will be invoked when the mouse is dragged
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void mouseDragged(MouseEvent e)
	{
		int mouseX = e.getX(), mouseY = e.getY();
		wasDragged = true;
		
		
		/*************************************************************************
		 *                       DRAWBONDMODE                                    *
		 *************************************************************************/
		if (c2dm.getDrawMode() == c2dm.DRAWBOND)
		{
			int endX = 0, endY = 0;
			int pointerVectorLength = r2dm.getPointerVectorLength();
			double angle = 0;
			int startX = r2dm.getPointerVectorStart().x;
			int startY = r2dm.getPointerVectorStart().y;
			Atom atomInRange;

			angle = GeometryTools.getAngle(startX - mouseX, startY - mouseY);
			if (c2dm.getSnapToGridAngle())
			{
				angle = snapAngle(angle);
			}
			atomInRange = getAtomInRange(mouseX, mouseY);
			if (atomInRange != null)
			{
				endX = (int)atomInRange.getX2D();
				endY = (int)atomInRange.getY2D();
			}
			else
			{
				endX = startX - (int)(Math.cos(angle) * pointerVectorLength);
				endY = startY - (int)(Math.sin(angle) * pointerVectorLength);
			}
			r2dm.setPointerVectorEnd(new Point(endX, endY));
		}
		
		/*************************************************************************
		 *                       SELECTMODE                                      *
		 *************************************************************************/
		if (c2dm.getDrawMode() == c2dm.SELECT)
		{
			int startX = r2dm.getPointerVectorStart().x;
			int startY = r2dm.getPointerVectorStart().y;
			int[] xPoints = {startX, startX, mouseX, mouseX};
			int[] yPoints = {startY, mouseY, mouseY, startY};
			r2dm.setSelectRect(new Polygon(xPoints, yPoints, 4));
		}	
	}
	
	/**
	 * manages all actions that will be invoked when a mouse button is pressed
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void mousePressed(MouseEvent e)
	{
		Atom atomInRange;
		int mouseX = e.getX(), mouseY = e.getY(), startX = 0, startY = 0;
		r2dm.setPointerVectorStart(null);
		r2dm.setPointerVectorEnd(null);
//		if (c2dm.getDrawMode() == c2dm.DRAWBOND)
//		{
			atomInRange = getAtomInRange(mouseX, mouseY);
			if (atomInRange != null)
			{
				startX = (int)atomInRange.getX2D();
				startY = (int)atomInRange.getY2D();
				r2dm.setPointerVectorStart(new Point(startX, startY));
			}
			else
			{
				r2dm.setPointerVectorStart(new Point(mouseX, mouseY));
			}			
//		}
	}
	

	/**
	 * manages all actions that will be invoked when a mouse button is released
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void mouseReleased(MouseEvent e)
	{
		int mouseX = e.getX(), mouseY = e.getY();

		/*************************************************************************
		 *                       DRAWBONDMODE                                    *
		 *************************************************************************/
		if (c2dm.getDrawMode() == c2dm.DRAWBOND)
		{
			Atom atomInRange, newAtom1, newAtom2;
			Bond newBond;
			int startX = r2dm.getPointerVectorStart().x;
			int startY = r2dm.getPointerVectorStart().y;
			atomInRange = getAtomInRange(startX, startY);
			if (atomInRange != null) 
			{
				newAtom1 = atomInRange;
			}
			else
			{
				newAtom1 = new Atom(new Element(r2dm.getDefaultElementSymbol()), new Point2d(startX,startY));
				atomCon.addAtom(newAtom1);
			}
			
			if (wasDragged)
			{
				int endX = r2dm.getPointerVectorEnd().x;
				int endY = r2dm.getPointerVectorEnd().y;
				atomInRange = getAtomInRange(endX, endY);
				if (atomInRange != null) 
				{
					newAtom2 = atomInRange;
				}
				else
				{
					newAtom2 = new Atom(new Element(r2dm.getDefaultElementSymbol()), new Point2d(endX,endY));
					atomCon.addAtom(newAtom2);
				}
				newBond = new Bond(newAtom1, newAtom2, 1);
				atomCon.addBond(newBond);
			}
			r2dm.fireChange();
		}
		
		/*************************************************************************
		 *                       SELECTMODE                                      *
		 *************************************************************************/
		if (c2dm.getDrawMode() == c2dm.SELECT && wasDragged)
		{
			Atom currentAtom;
			Bond currentBond;
			AtomContainer selectedPart = new AtomContainer();
			r2dm.setSelectedPart(selectedPart);
			for (int i = 0; i < atomCon.getAtomCount(); i++)
			{
				currentAtom = atomCon.getAtomAt(i);
				if (r2dm.getSelectRect().contains(new Point((int)currentAtom.getX2D(), (int)currentAtom.getY2D())))
				{
					selectedPart.addAtom(currentAtom);
				}
			}
			for (int i = 0; i < atomCon.getAtomCount(); i++)
			{
				currentBond = atomCon.getBondAt(i);
				for (int j = 0; j < selectedPart.getAtomCount(); j++)
				{
					currentAtom = selectedPart.getAtomAt(j);
					if (selectedPart.contains(currentBond.getConnectedAtom(currentAtom)))
					{
						selectedPart.addBond(currentBond);
						break;
					}
				}
				
			}
			r2dm.setSelectedPart(selectedPart);
			r2dm.setSelectRect(null);
			System.out.println("selected stuff  "+ selectedPart);
		}
		
		/*************************************************************************
		 *                       ERASERMODE                                      *
		 *************************************************************************/
		if (c2dm.getDrawMode() == c2dm.ERASER)
		{
			Atom highlightedAtom = r2dm.getHighlightedAtom();
			Bond highlightedBond = r2dm.getHighlightedBond();
			if (highlightedAtom != null)
			{
				try
				{
					atomCon.removeAtom(highlightedAtom);
					Bond[] conBonds = atomCon.getConnectedBonds(highlightedAtom);
					for (int i = 0; i < conBonds.length; i++)
					{
						atomCon.removeBond(conBonds[i]);
					}
				}
				catch (Exception exc)
				{
					exc.printStackTrace();
				}
			}
			else if (highlightedBond != null)
			{
				atomCon.removeBond(highlightedBond);
			}
			r2dm.fireChange();
		}
		
		/*************************************************************************
		 *                       POLYGONMODE                                     *
		 *************************************************************************/
		if (c2dm.getDrawMode() >= c2dm.SQUARE && c2dm.getDrawMode() <= c2dm.OCTAGON)
		{
			Ring newRing = new Ring(c2dm.getDrawMode(), r2dm.getDefaultElementSymbol());
			AtomContainer highlighted = getHighlightedAtoms();
		}
		wasDragged = false;
	}
	
	
	
	public void mouseClicked(MouseEvent e)
	{
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
	public void keyPressed(KeyEvent e)
	{
	}
	
	public void mouseEntered(MouseEvent e)
	{
	}
	
	public void mouseExited(MouseEvent e)
	{
	}
	
	private double snapAngle(double angle)
	{
		double div = (Math.PI / 180) * c2dm.getSnapAngle();
		return (Math.rint(angle / div)) * div;
	}
	
	private int snapCartesian(int position)
	{
		int div = c2dm.getSnapCartesian();
		return (int)(Math.rint(position / div)) * div;
	}
	
	
	private Atom getAtomInRange(int mouseX, int mouseY)
	{
		double highlightRadius = r2dm.getHighlightRadius();
		Atom closestAtom = GeometryTools.getClosestAtom(mouseX, mouseY, atomCon);
		if (closestAtom == null) return null;
		if (debug) System.out.println("closestAtom  "+ closestAtom);
		if (Math.sqrt(Math.pow(closestAtom.getX2D() - mouseX, 2) + Math.pow(closestAtom.getY2D() - mouseY, 2)) < highlightRadius)
		{
			return closestAtom;
		}
		return null;
	}

	private Bond getBondInRange(int mouseX, int mouseY)
	{	
		double highlightRadius = r2dm.getHighlightRadius();
		Bond closestBond = GeometryTools.getClosestBond(mouseX, mouseY, atomCon);
		if (closestBond == null) return null;
		if (debug) System.out.println("closestBond  "+ closestBond);
		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(closestBond),highlightRadius);
		int[] xCoords = {coords[0],coords[2],coords[4],coords[6]};
		int[] yCoords = {coords[1],coords[3],coords[5],coords[7]};
		if ((new Polygon(xCoords, yCoords, 4)).contains(new Point(mouseX, mouseY)))
		{
			return closestBond;
		}
		return null;
	}
	
	private AtomContainer getHighlightedAtoms()
	{
		AtomContainer highlighted = new AtomContainer();
		Atom highlightedAtom = r2dm.getHighlightedAtom();
		Bond highlightedBond = r2dm.getHighlightedBond();
		if (highlightedAtom != null)
		{
			highlighted.addAtom(highlightedAtom);
		}
		else if (highlightedBond != null)
		{
//			highlighted.addBond(highlightedBond);
			for (int i = 0; i < highlightedBond.getAtoms().length; i++)
			{
				highlighted.addAtom(highlightedBond.getAtomAt(i));
			}
		}
		if (debug) System.out.println("sharedAtoms  "+ highlighted);
		return highlighted;
	}
}
