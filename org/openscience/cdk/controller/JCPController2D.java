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
	JCPController2DModel c2dm;
	boolean wasDragged = false;
	
	
	public JCPController2D(AtomContainer atomCon, Renderer2DModel r2dm, JCPController2DModel c2dm)
	{
		this.atomCon = atomCon;
		this.r2dm = r2dm;
		this.c2dm = c2dm;
		
	}
	

	public JCPController2D(AtomContainer atomCon, Renderer2DModel r2dm)
	{
		this.atomCon = atomCon;
		this.r2dm = r2dm;
		this.c2dm = new JCPController2DModel();
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
			int pointerVectorLength = c2dm.getBondPointerLength();
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
	
		/*************************************************************************
		 *                          RINGMODE                                     *
		 *************************************************************************/
	 	if (c2dm.getDrawMode() == c2dm.RING)
	 	{
		 	int endX = 0, endY = 0;
		 	double angle = 0;
		 	int pointerVectorLength = c2dm.getRingPointerLength();
		 	Point2d center = getHighlighted().get2DCenter();
		 	r2dm.setPointerVectorStart(new Point((int)center.x, (int)center.y));
		 	int startX = 0;
		 	int startY = 0;
		 	angle = GeometryTools.getAngle(center.x - mouseX, center.y - mouseY);
		 	endX = (int)center.x - (int)(Math.cos(angle) * pointerVectorLength);
		 	endY = (int)center.y - (int)(Math.sin(angle) * pointerVectorLength);
			r2dm.setPointerVectorEnd(new Point(endX, endY));
	 	}

		/*************************************************************************
		 *                          LASSOMODE                                     *
		 *************************************************************************/
    	if (c2dm.getDrawMode() == c2dm.LASSO)
	    {	
			 r2dm.addLassoPoint(new Point(mouseX, mouseY));
//		 	 r2dm.setSelectRect(new Polygon(xPoints, yPoints, number));
//			 int startX = r2dm.getPointerVectorStart().x;
//			 int startY = r2dm.getPointerVectorStart().y;
//			 Vector xPointsVector = new Vector();
//			 Vector yPointsVector = new Vector();
//			 r2dm.setLassoLines()
//			 int[] xPoints = {startX, startX, mouseX, mouseX};
//			 int[] yPoints = {startY, mouseY, mouseY, startY};
//			 r2dm.setSelectRect(new Polygon(xPoints, yPoints, 4));
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
				newAtom1 = new Atom(new Element(c2dm.getDefaultElementSymbol()), new Point2d(startX,startY));
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
					newAtom2 = new Atom(new Element(c2dm.getDefaultElementSymbol()), new Point2d(endX,endY));
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
			r2dm.setSelectedPart(getContainedAtoms(r2dm.getSelectRect()));
			r2dm.setSelectRect(null);
//			System.out.println("selected stuff  "+ selectedPart);
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
		 *                          RINGMODE                                     *
		 *************************************************************************/
		if (c2dm.getDrawMode() == c2dm.RING)
		{
			Ring newRing;
			Point2d sharedAtomsCenter;
			Vector2d ringCenterVector;
			double bondLength;
			int pointerMarkX, pointerMarkY;
			
			double ringRadius, angle, xDiff, yDiff, distance1 = 0, distance2 = 0;
			Atom firstAtom, secondAtom, spiroAtom;
			Point2d conAtomsCenter = null, newPoint1, newPoint2;
			
			RingPlacer ringPlacer = new RingPlacer();
			int ringSize = c2dm.getRingSize();
			String symbol = c2dm.getDefaultElementSymbol();
			AtomContainer sharedAtoms = getHighlighted();
			
			/******************** NO ATTACHMENT ************************************/
			if (sharedAtoms.getAtomCount() == 0)			
			{
				sharedAtoms = new AtomContainer();
				newRing = new Ring(ringSize, symbol);
				bondLength = c2dm.getDefaultBondLength();
				ringRadius = (bondLength / 2) /Math.sin(Math.PI / c2dm.getRingSize());
				sharedAtomsCenter = new Point2d(mouseX, mouseY - ringRadius);
				firstAtom = newRing.getAtomAt(0);
				firstAtom.setPoint2D(sharedAtomsCenter);
				sharedAtoms.addAtom(firstAtom);
				ringCenterVector = new Vector2d(new Point2d(mouseX, mouseY));
				ringCenterVector.sub(sharedAtomsCenter);
				ringPlacer.placeSpiroRing(newRing, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
				atomCon.add(newRing);
			}
			
			/*********************** SPIRO *****************************************/
			else if (sharedAtoms.getAtomCount() == 1)
			{
				spiroAtom = sharedAtoms.getAtomAt(0);
				sharedAtomsCenter = sharedAtoms.get2DCenter();
				newRing = createAttachRing(sharedAtoms, ringSize, symbol);
				bondLength = 50;
				conAtomsCenter = getConnectedAtomsCenter(sharedAtoms);				
				if (conAtomsCenter.equals(spiroAtom.getPoint2D()))
				{
					ringCenterVector = new Vector2d(0, 1);
				}
				else
				{
					ringCenterVector = new Vector2d(sharedAtomsCenter);
					ringCenterVector.sub(conAtomsCenter);
				}
				ringPlacer.placeSpiroRing(newRing, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
				// removes the highlighed atom from the ring to add only the new placed
			    // atoms to the AtomContainer.		
				try
				{
					newRing.removeAtom(spiroAtom);
				}
				catch (Exception exc)
				{
					exc.printStackTrace();
				}
				atomCon.add(newRing);
			}
			
			/*********************** FUSED *****************************************/
			else if (sharedAtoms.getAtomCount() == 2)
			{
				sharedAtomsCenter = sharedAtoms.get2DCenter();
				
				// calculate two points that are perpendicular to the highlighted bond
				// and have a certain distance from the bondcenter
				firstAtom = sharedAtoms.getAtomAt(0);
				secondAtom = sharedAtoms.getAtomAt(1);
				xDiff = secondAtom.getX2D() - firstAtom.getX2D();
				yDiff = secondAtom.getY2D() - firstAtom.getY2D();
				bondLength = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
				angle = GeometryTools.getAngle(xDiff, yDiff);
				newPoint1 = new Point2d((Math.cos(angle + (Math.PI / 2)) * bondLength / 4) + sharedAtomsCenter.x, (Math.sin(angle + (Math.PI / 2)) * bondLength / 4) + sharedAtomsCenter.y);
				newPoint2 = new Point2d((Math.cos(angle - (Math.PI / 2)) * bondLength / 4) + sharedAtomsCenter.x, (Math.sin(angle - (Math.PI / 2)) * bondLength / 4) + sharedAtomsCenter.y);

				if (wasDragged)
				{
					// check which one of the two points is nearest to the endpoint of the pointer
					// vector that was dragged to make the ringCenterVector point into the right direction.
					pointerMarkX = r2dm.getPointerVectorEnd().x;
					pointerMarkY = r2dm.getPointerVectorEnd().y;
					distance1 = -1 * (Math.sqrt(Math.pow(newPoint1.x - pointerMarkX, 2) + Math.pow(newPoint1.y - pointerMarkY, 2)));
					distance2 = -1 * (Math.sqrt(Math.pow(newPoint2.x - pointerMarkX, 2) + Math.pow(newPoint2.y - pointerMarkY, 2)));
					r2dm.setPointerVectorStart(null);
					r2dm.setPointerVectorEnd(null);
				}
				else
				{
					// check which one of the two points is nearest to the center of the
					// connected atoms to make the ringCenterVector point into the right direction.
					conAtomsCenter = getConnectedAtomsCenter(sharedAtoms);
					distance1 = Math.sqrt(Math.pow(newPoint1.x - conAtomsCenter.x, 2) + Math.pow(newPoint1.y - conAtomsCenter.y, 2));
					distance2 = Math.sqrt(Math.pow(newPoint2.x - conAtomsCenter.x, 2) + Math.pow(newPoint2.y - conAtomsCenter.y, 2));
				}
				ringCenterVector = new Vector2d(sharedAtomsCenter);	
				// no ring is attached if the two ditances are equal
				if (distance1 == distance2)
				{
					System.out.println("don't know where to draw the new Ring");
				}
				else
				{
					if (distance1 < distance2)
					{
						ringCenterVector.sub(newPoint1);
					}
					else if (distance2 < distance1)
					{
						ringCenterVector.sub(newPoint2);
					}
					
					// construct a new Ring that contains the highlighted bond an its two atoms
					newRing = createAttachRing(sharedAtoms, ringSize, symbol);
					
					// place the new atoms of the new ring to the right position
					ringPlacer.placeFusedRing(newRing, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
					
					// removes the highlighed bond and its atoms from the ring to add only
					// the new placed atoms to the AtomContainer.		
					try
					{
						newRing.remove(sharedAtoms);
					}
					catch (Exception exc)
					{
						exc.printStackTrace();
					}
					atomCon.add(newRing);
				}
			}
			r2dm.fireChange();
		}
		
	   /*************************************************************************
	    *                          LASSOMODE                                     *
	    *************************************************************************/
		if (c2dm.getDrawMode() == c2dm.LASSO)
		{
			Vector lassoPoints = r2dm.getLassoPoints();
			r2dm.addLassoPoint(new Point((Point)lassoPoints.elementAt(0)));
			int number = lassoPoints.size();
			int[] xPoints = new int[number];
			int[] yPoints = new int[number];
			Point currentPoint;
			for (int i = 0; i < number; i++)
			{
				currentPoint = (Point)lassoPoints.elementAt(i);
				xPoints[i] = currentPoint.x;
				yPoints[i] = currentPoint.y;
			}
			Polygon polygon = new Polygon(xPoints, yPoints, number);
			r2dm.setSelectedPart(getContainedAtoms(polygon));
			r2dm.getLassoPoints().removeAllElements();
			r2dm.fireChange();
		}
		wasDragged = false;
	}
	
	
	/**
	 * manages all actions that will be invoked when a mouse button is clicked
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void mouseClicked(MouseEvent e)
	{
	}
	
	/**
	 * manages all actions that will be invoked when a mouse enters a component
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void mouseEntered(MouseEvent e)
	{
	}
	
	/**
	 * manages all actions that will be invoked when a mouse exits a component
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void mouseExited(MouseEvent e)
	{
	}
	
	/**
	 * manages all actions that will be invoked when a key is released
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void keyReleased(KeyEvent e)
	{
	}
	
	/**
	 * manages all actions that will be invoked when a key is typed
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void keyTyped(KeyEvent e)
	{
	}
	
	/**
	 * manages all actions that will be invoked when a key is pressed
	 *
	 * @param   e	 MouseEvent object
	 **/
	public void keyPressed(KeyEvent e)
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
	
	

	/**
	 * Returns an Atom if it is in a certain range of the given point.
	 * Used to highlight an atom that is near the cursor.
	 *
	 * @param   mouseX  The x coordinate of the point
	 * @param   mouseY  The y coordinate of the point
	 * @return    An Atom if it is in a certain range of the given point 
	 */
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


	/**
	 * Returns a Bond if it is in a certain range of the given point.
	 * Used to highlight a bond that is near the cursor.
	 *
	 * @param   mouseX  The x coordinate of the point
	 * @param   mouseY  The y coordinate of the point
	 * @return    An Atom if it is in a certain range of the given point 
	 */
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
	

	/**
	 * Returns an AtomContainer that contains the atom or the the bond with its
	 * two atoms that are highlighted at the moment.
	 *
	 * @return  An AtomContainer containig the highlighted atom\atoms\bond  
	 */
	private AtomContainer getHighlighted()
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
			highlighted.addBond(highlightedBond);
			for (int i = 0; i < highlightedBond.getAtomCount(); i++)
			{
				highlighted.addAtom(highlightedBond.getAtomAt(i));
			}
		}
		if (debug) System.out.println("sharedAtoms  "+ highlighted);
		return highlighted;
	}
	

	/**
	 * Constructs a new Ring of a certain size that contains all the atoms and bonds
	 * of the given AtomContainer and is filled up with new Atoms and Bonds.
	 *
	 * @param   sharedAtoms  The AtomContainer containing the Atoms and bonds for the new Ring
	 * @param   ringSize  The size (number of Atoms) the Ring will have
	 * @param   symbol  The element symbol the new atoms will have
	 * @return     The constructed Ring
	 */
	private Ring createAttachRing(AtomContainer sharedAtoms, int ringSize, String symbol)
	{
		Ring newRing = new Ring(ringSize);
		Atom[] ringAtoms = new Atom[ringSize];
		for (int i = 0; i < sharedAtoms.getAtomCount(); i++)
		{
			ringAtoms[i] = sharedAtoms.getAtomAt(i);
		}
		for (int i = sharedAtoms.getAtomCount(); i < ringSize; i++)
		{
			ringAtoms[i] = new Atom(symbol);
		}
		for (int i = 0; i < sharedAtoms.getBondCount(); i++)
		{
			newRing.setBondAt(i, sharedAtoms.getBondAt(i));
		}
		for (int i = sharedAtoms.getBondCount(); i < ringSize - 1; i++)
		{
			newRing.setBondAt(i,new Bond(ringAtoms[i], ringAtoms[i + 1], 1));
		}
		newRing.setBondAt(ringSize - 1, new Bond(ringAtoms[ringSize - 1], ringAtoms[0], 1));
		newRing.setAtoms(ringAtoms);
		return newRing;
	}
	

	/**
	 * Searches all the atoms attached to the Atoms in the given AtomContainer
	 * and calculates the center point of them.
	 *
	 * @param   sharedAtoms   The Atoms the attached partners are searched of
	 * @return     The Center Point of all the atoms found
	 */
	private Point2d getConnectedAtomsCenter(AtomContainer sharedAtoms)
	{
		Atom currentAtom;
		Atom[] conAtomsArray;
		AtomContainer conAtoms = new AtomContainer();
		for (int i = 0; i < sharedAtoms.getAtomCount(); i++)
		{
			currentAtom = sharedAtoms.getAtomAt(i);
			conAtoms.addAtom(currentAtom);
			conAtomsArray = atomCon.getConnectedAtoms(currentAtom);
			for (int j = 0; j < conAtomsArray.length; j++)
			{
				conAtoms.addAtom(conAtomsArray[j]);
			}
		}
		return conAtoms.get2DCenter();
		
	}
	

	/**
	 * Returns an AtomContainer with all the atoms and bonds that are inside 
	 * a given polygon.
	 *
	 * @param   polygon  The given Polygon
	 * @return     AtomContainer with all atoms and bonds inside the polygon
	 */
	private AtomContainer getContainedAtoms(Polygon polygon)
	{
		Atom currentAtom;
		Bond currentBond;
		AtomContainer selectedPart = new AtomContainer();
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			currentAtom = atomCon.getAtomAt(i);
			if (polygon.contains(new Point((int)currentAtom.getX2D(), (int)currentAtom.getY2D())))
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
		return selectedPart;
	}
	
}
