/* Renderer2D.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
package org.openscience.cdk.renderer;


import java.awt.*;
import javax.vecmath.*;
import java.util.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;


/**
 * A Renderer class which draws 2D representations of molecules onto 
 * a given graphics objects using information from a Renderer2DModel
 * 
 * @keyword viewer, 2D-viewer
 */
public class Renderer2D 
{
	SSSRFinder sssrf = new SSSRFinder();
	public Renderer2DModel r2dm;
	Graphics g;
	AtomContainer atomCon;
		

	/**
	 * Constructs a Renderer2D
	 *
	 * @param   graphics    The graphics object 
	 */
	public Renderer2D()
	{
		r2dm = new Renderer2DModel();
	}


	/**
	 * Constructs a Renderer2D
	 *
	 * @param   graphics    The graphics object 
	 */
	public Renderer2D(Renderer2DModel r2dm)
	{
		this.r2dm = r2dm;
	}


	/**
	 * triggers the methods to make the molecule fit into the frame and to paint it.
	 * 
	 * @param   mol  The Molecule to be drawn
	 */
	public void paintMolecule(AtomContainer atomCon, Graphics g)
	{
		this.g = g;
		this.atomCon = atomCon;
		RingSet ringSet = new RingSet();
		Vector molecules = null;
		try
		{
			molecules = ConnectivityChecker.partitionIntoMolecules(atomCon);
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
		for (int i = 0; i < molecules.size(); i++)
		{
			ringSet.add(sssrf.findSSSR((Molecule)molecules.elementAt(i)));
		}
		if (r2dm.getPointerVectorStart() != null && r2dm.getPointerVectorEnd() != null) 
		{ 
			paintPointerVector();
		}
		paintBonds(atomCon, ringSet);
		paintAtoms(atomCon);
		if (r2dm.drawNumbers()) 
		{
			paintNumbers(atomCon.getAtoms(), atomCon.getAtomCount());
		}
		if (r2dm.getSelectRect() != null)
		{
			g.setColor(r2dm.getHighlightColor());
			g.drawPolygon(r2dm.getSelectRect());
		}
		paintLassoLines();
	}
	
	
	private void paintLassoLines()
	{
		Vector points = r2dm.getLassoPoints();
		if (points.size() > 1)
		{
			Point point1 = (Point)points.elementAt(0), point2;
			for (int i = 1; i < points.size(); i++)
			{
				point2 = (Point)points.elementAt(i);
				g.drawLine(point1.x, point1.y, point2.x, point2.y);
				point1 = point2;
			}
		}
	}
	
	/**
	 * Draw all numbers of all atoms in the molecule
	 *
	 * @param   atoms     The array of atoms
	 * @param   number    The number of atoms in this array
	 */
	private void paintNumbers(Atom[] atoms, int number)
	{
		for (int i = 0; i < number; i++)
		{
				paintNumber(atoms[i]);
		}
	}


   /*
	* Paints the numbers 
	*
	* @param   atom    The atom to be drawn
	*/
	private void paintNumber(Atom atom)
	{
		if (atom.getPoint2D() == null) return;
		FontMetrics fm = g.getFontMetrics();
		int fontSize = g.getFont().getSize();
		int xSymbOffset = (new Integer(fm.stringWidth(atom.getSymbol())/2)).intValue();
		int ySymbOffset = (new Integer(fm.getAscent()/2)).intValue();

		try
		{
			int i = atomCon.getAtomNumber(atom);
//			g.setColor(r2dm.getBackColor());
//			g.fillRect((int)(atom.getPoint2D().x - (xSymbOffset * 1.8)),(int)(atom.getPoint2D().y - (ySymbOffset * 0.8)),(int)fontSize,(int)fontSize); 
			g.setColor(r2dm.getForeColor());
			g.drawString(new Integer(i).toString(),(int)(atom.getPoint2D().x + (xSymbOffset)),(int)(atom.getPoint2D().y - (ySymbOffset)));
			g.setColor(r2dm.getBackColor());
			g.drawLine((int)atom.getPoint2D().x,(int)atom.getPoint2D().y,(int)atom.getPoint2D().x,(int)atom.getPoint2D().y);
		}
		catch(Exception exc)
		{
		
		}
	}

	/**
	 * Searches through all the atoms in the given array of atoms, triggers the
	 * paintColouredAtoms method if the atom has got a certain color and triggers
	 * the paintAtomSymbol method if the symbol of the atom is not C.
	 *
	 * @param   atoms     The array of atoms
	 * @param   number    The number of atoms in this array
	 */
	private void paintAtoms(AtomContainer atomCon)
	{
		Color atomColor; 
		Atom atom;
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			atom = atomCon.getAtomAt(i);
			atomColor = (Color)r2dm.getColorHash().get(atom);
			if (atom == r2dm.getHighlightedAtom()) atomColor = r2dm.getHighlightColor();
			if (atomColor != null)
			{
				paintColouredAtom(atom, atomColor);
			}
			else
			{
				atomColor = r2dm.getBackColor();
			}			
			if (!atom.getSymbol().equals("C"))
			{
				paintAtomSymbol(atom, atomColor);
			}
			else if (atomCon.getDegree(atom) == 0)
			{
				paintAtomSymbol(atom, atomColor);
			}
		}
	}


	/**
	 * Paints a rectangle of the given color at the position of the given atom.
	 * For example when the atom is highlighted.
	 *
	 * @param   atom  The atom to be drawn
	 * @param   color  The color of the atom to be drawn
	 */
	private void paintColouredAtom(Atom atom, Color color)
	{
		int atomRadius = r2dm.getAtomRadius();
		g.setColor(color);
		g.fillRect((int)atom.getX2D() - (atomRadius / 2), (int)atom.getY2D() - (atomRadius / 2), atomRadius, atomRadius);
	}
	
	/**
	 * Paints the given atom.
	 * first some empty space, slightly larger than the space
	 * that the symbol occupies, is drawn using the background color.
	 * The atom symbol is then printed into the empty space.
	 *
	 * @param   atom    The atom to be drawn
	 */
	private void paintAtomSymbol(Atom atom, Color backColor)
	{
		if (atom.getPoint2D() == null) return;
		FontMetrics fm = g.getFontMetrics();
		int fontSize = g.getFont().getSize();
		int xSymbOffset = (new Integer(fm.stringWidth(atom.getSymbol())/2)).intValue();
		int ySymbOffset = (new Integer(fm.getAscent()/2)).intValue();
		g.setColor(backColor);
		g.fillRect((int)(atom.getPoint2D().x - (xSymbOffset * 1.8)),(int)(atom.getPoint2D().y - (ySymbOffset * 0.8)),(int)fontSize,(int)fontSize); 
		g.setColor(r2dm.getForeColor());
		g.drawString(atom.getSymbol(),(int)(atom.getPoint2D().x - xSymbOffset),(int)(atom.getPoint2D().y + ySymbOffset));
//		g.setColor(r2dm.getBackColor());
//		g.drawLine((int)atom.getPoint2D().x,(int)atom.getPoint2D().y,(int)atom.getPoint2D().x,(int)atom.getPoint2D().y);
	}


	/**
	 * Triggers the suitable method to paint each of the given bonds and selects 
	 * the right color.
	 *
	 * @param   bonds   The bonds to be drawn
	 * @param   number  The number of bonds to be drawn
	 * @param   ringSet  The set of rings the molecule contains
	 */
	private void paintBonds(AtomContainer atomCon, RingSet ringSet)
	{
		Color bondColor;
		Ring ring;
		Bond bond;
		for (int i = 0; i < atomCon.getBondCount(); i++)
		{
			bond = atomCon.getBondAt(i);
			bondColor = (Color)r2dm.getColorHash().get(bond);
			if (bondColor == null) bondColor = r2dm.getForeColor();
			if (bond == r2dm.getHighlightedBond())
			{
				bondColor = r2dm.getHighlightColor();
				for (int j = 0; j < bond.getAtomCount(); j++)
				{
					paintColouredAtom(bond.getAtomAt(j),bondColor);
				}
			}
			ring = ringSet.getHeaviestRing(bond);
			if (ring != null)
			{
					paintRingBond(bond, ring, bondColor);

			}
			else
			{
				paintBond(bond, bondColor);
			}
		}
	}
	

	/**
	 * Triggers the paint method suitable to the bondorder of the given bond.
	 *
	 * @param   bond    The Bond to be drawn.
	 */
	private void paintBond(Bond bond, Color bondColor)
	{
//		System.out.println("Renderer2D: bondorder: " + bond.getOrder());

		if (bond.getAtomAt(0).getPoint2D() == null || bond.getAtomAt(1).getPoint2D() == null) return;
		
		if (bond.getOrder() == 1)
		{
			paintSingleBond(bond, bondColor);
		}
		else if (bond.getOrder() == 2)
		{
			paintDoubleBond(bond, bondColor);
		}
		else if (bond.getOrder() == 3)
		{
			paintTripleBond(bond, bondColor);
		}
	}
	
	
	/**
	 * Triggers the paint method suitable to the bondorder of the given bond
	 * that is part of a ring.
	 *
	 * @param   bond    The Bond to be drawn.
	 */
	private void paintRingBond(Bond bond, Ring ring, Color bondColor)
	{
		if (bond.getOrder() == 1)
		{
			paintSingleBond(bond, bondColor);
		}
		else if (bond.getOrder() == 2)
		{
			paintSingleBond(bond, bondColor);
			paintInnerBond(bond,ring, bondColor);
		}
		else if (bond.getOrder() == 3)
		{
			paintTripleBond(bond, bondColor);
		}
	}

	/**
	 * Paints the given singlebond.
	 *
	 * @param   bond  The singlebond to be drawn
	 */
	private void paintSingleBond(Bond bond, Color bondColor)
	{
		paintOneBond(GeometryTools.getBondCoordinates(bond), bondColor);
		
	}
	

	/**
	 * Paints The given doublebond.
	 *
	 * @param   bond  The doublebond to be drawn
	 */
	private void paintDoubleBond(Bond bond, Color bondColor)
	{
		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond),r2dm.getBondDistance()/2);
		
		int[] newCoords1 = {coords[0],coords[1],coords[6],coords[7]};
		paintOneBond(newCoords1, bondColor);
		
		int[] newCoords2 = {coords[2],coords[3],coords[4],coords[5]};
		paintOneBond(newCoords2, bondColor);
				
	}
	
	/**
	 * Paints the given triplebond.
	 *
	 * @param   bond  The triplebond to be drawn
	 */
	private void paintTripleBond(Bond bond, Color bondColor)
	{
		paintSingleBond(bond, bondColor);
		
		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond),(r2dm.getBondWidth()/2 + r2dm.getBondDistance()));
		
		int[] newCoords1 = {coords[0],coords[1],coords[6],coords[7]};
		paintOneBond(newCoords1, bondColor);
		
		int[] newCoords2 = {coords[2],coords[3],coords[4],coords[5]};
		paintOneBond(newCoords2, bondColor);
	}
	
	/**
	 * Paints the inner bond of a doublebond that is part of a ring.
	 *
	 * @param   bond  The bond to be drawn
	 * @param   ring  The ring the bond is part of
	 */
	private void paintInnerBond(Bond bond, Ring ring, Color bondColor)
	{
		Point2d center = ring.get2DCenter();

		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond),(r2dm.getBondWidth()/2 + r2dm.getBondDistance()));
		double dist1 = Math.sqrt(Math.pow((coords[0] - center.x),2) + Math.pow((coords[1] - center.y),2));
		double dist2 = Math.sqrt(Math.pow((coords[2] - center.x),2) + Math.pow((coords[3] - center.y),2));
		if (dist1 < dist2)	
		{
			int[] newCoords1 = {coords[0],coords[1],coords[6],coords[7]};
			paintOneBond(shortenBond(newCoords1, ring.getRingSize()), bondColor);
		}
		else
		{
			int[] newCoords2 = {coords[2],coords[3],coords[4],coords[5]};
			paintOneBond(shortenBond(newCoords2, ring.getRingSize()), bondColor);
		}	
	}
	

	/**
	 * Calculates the coordinates for the inner bond of a doublebond that is part of 
	 * a ring. It is drawn shorter than a normal bond.
	 *
	 * @param   coords  The original coordinates of the bond   
	 * @param   edges  Number of edges of the ring it is part of
	 * @return    The calculated coordinates of the now shorter bond 
	 */
	private int[] shortenBond(int[] coords, int edges)
	{
		int xDiff = (coords[0] - coords[2]) / (edges * 2);
		int yDiff = (coords[1] - coords[3]) / (edges * 2);
		int[] newCoords = {coords[0] - xDiff,coords[1] - yDiff,coords[2] + xDiff,coords[3] + yDiff};
		return newCoords;
	}

	/**
	 * Really paints the bond. It is triggered by all the other paintbond methods
	 * to draw a polygon as wide as bondwidth.  
	 *
	 * @param   coords  
	 */
	private void paintOneBond(int[] coords, Color bondColor)
	{
		g.setColor(bondColor);
		int[] newCoords = GeometryTools.distanceCalculator(coords, r2dm.getBondWidth()/2);
		int[] xCoords = {newCoords[0],newCoords[2],newCoords[4],newCoords[6]};
		int[] yCoords = {newCoords[1],newCoords[3],newCoords[5],newCoords[7]};
		g.fillPolygon(xCoords,yCoords,4);
	}



	/**
	 * Paints a line between the startpoint and endpoint of the pointervector
	 * that is stored in the Renderer2DModel.
	 */
	private void paintPointerVector()
	{
		Point startPoint = r2dm.getPointerVectorStart();
		Point endPoint = r2dm.getPointerVectorEnd();
		int[] points = {startPoint.x, startPoint.y, endPoint.x, endPoint.y};
		int[] newCoords = GeometryTools.distanceCalculator(points,r2dm.getBondWidth() / 2);
		int[] xCoords = {newCoords[0],newCoords[2],newCoords[4],newCoords[6]};
		int[] yCoords = {newCoords[1],newCoords[3],newCoords[5],newCoords[7]};
		g.setColor(r2dm.getForeColor());
		g.fillPolygon(xCoords,yCoords,4);
	}
	
	

	/**
	 * Returns the Renderer2DModel of this Renderer
	 *
	 * @return     the Renderer2DModel of this Renderer
	 */
	public Renderer2DModel getRenderer2DModel()
	{
		return this.r2dm;
	}



	/**
	 * Sets the Renderer2DModel of this Renderer
	 *
	 * @param   r2dm  the new Renderer2DModel for this Renderer
	 */
	public void setRenderer2DModel(Renderer2DModel r2dm)
	{
		this.r2dm = r2dm;
	}

}
