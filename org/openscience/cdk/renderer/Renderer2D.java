/* Renderer2D.java
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
package org.openscience.cdk.renderer;


import java.awt.*;
import java.util.Vector;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.*;



public class Renderer2D 
{
	SSSRFinder sssrf = new SSSRFinder();
	Renderer2DModel r2dm;
	Graphics g;
	

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
	public void paintMolecule(Molecule molecule, Graphics g)
	{
		this.g = g;
		RingSet ringSet = sssrf.findSSSR(molecule);
		paintBonds(molecule.getBonds(), molecule.getBondCount(), ringSet);
		paintAtoms(molecule.getAtoms(), molecule.getAtomCount());
	}
	
	/**
	 * Searches through all the atoms in the given array of atoms and triggers
	 * the paintAtom method if the symbol of the atom is not C.
	 *
	 * @param   atoms     The array of atoms
	 * @param   number    The number of atoms in this array
	 */
	private void paintAtoms(Atom[] atoms, int number)
	{
		for (int i = 0; i < number; i++)
		{
			if (!atoms[i].getElement().getSymbol().equals("C"))
			{
				paintAtom(atoms[i]);
			}
		}
	}


	/**
	 * Paints the given atom.
	 * first some empty space, slightly larger than the space
	 * that the symbol occupies, is drawn using the background color.
	 * The atom symbol is then printed into the empty space.
	 *
	 * @param   atom    The atom to be drawn
	 */
	private void paintAtom(Atom atom)
	{
		FontMetrics fm = g.getFontMetrics();
		int fontSize = g.getFont().getSize();
		int xSymbOffset = (new Integer(fm.stringWidth(atom.getElement().getSymbol())/2)).intValue();
		int ySymbOffset = (new Integer(fm.getAscent()/2)).intValue();
		g.setColor(r2dm.getBackColor());
		g.fillRect((int)(atom.getPoint2D().x - (xSymbOffset * 1.8)),(int)(atom.getPoint2D().y - (ySymbOffset * 0.8)),(int)fontSize,(int)fontSize); 
		g.setColor(r2dm.getForeColor());
		g.drawString(atom.getElement().getSymbol(),(int)(atom.getPoint2D().x - xSymbOffset),(int)(atom.getPoint2D().y + ySymbOffset));
		g.setColor(r2dm.getBackColor());
		g.drawLine((int)atom.getPoint2D().x,(int)atom.getPoint2D().y,(int)atom.getPoint2D().x,(int)atom.getPoint2D().y);
	}

	private void paintBonds(Bond[] bonds, int number, RingSet ringSet)
	{
		Ring ring;
		for (int i = 0; i < number; i++)
		{
			ring = ringSet.getHeaviestRing(bonds[i]);
			if (ring != null)
			{
					paintRingBond(bonds[i], ring);

			}
			else
			{
				paintBond(bonds[i]);
			}
		}
	}
	

	/**
	 * Triggers the paint method suitable to the bondorder of the given bond.
	 *
	 * @param   bond    The Bond to be drawn.
	 */
	private void paintBond(Bond bond)
	{
		if (bond.getOrder() == 1)
		{
			paintSingleBond(bond);
		}
		else if (bond.getOrder() == 2)
		{
			paintDoubleBond(bond);
		}
		else if (bond.getOrder() == 3)
		{
			paintTripleBond(bond);
		}
	}
	
	
	/**
	 * Triggers the paint method suitable to the bondorder of the given bond
	 * that is part of a ring.
	 *
	 * @param   bond    The Bond to be drawn.
	 */
	private void paintRingBond(Bond bond, Ring ring)
	{
		if (bond.getOrder() == 1)
		{
			paintSingleBond(bond);
		}
		else if (bond.getOrder() == 2)
		{
			paintSingleBond(bond);
			paintInnerBond(bond,ring);
		}
		else if (bond.getOrder() == 3)
		{
			paintTripleBond(bond);
		}
	}

	/**
	 * Paints the given singlebond.
	 *
	 * @param   bond  The singlebond to be drawn
	 */
	private void paintSingleBond(Bond bond)
	{
		paintOneBond(getBondCoordinates(bond));
		
	}
	

	/**
	 * Paints The given doublebond.
	 *
	 * @param   bond  The doublebond to be drawn
	 */
	private void paintDoubleBond(Bond bond)
	{
		int[] coords = distanceCalculator(getBondCoordinates(bond),r2dm.getBondDistance()/2);
		
		int[] newCoords1 = {coords[0],coords[1],coords[6],coords[7]};
		paintOneBond(newCoords1);
		
		int[] newCoords2 = {coords[2],coords[3],coords[4],coords[5]};
		paintOneBond(newCoords2);
				
	}
	
	/**
	 * Paints the given triplebond.
	 *
	 * @param   bond  The triplebond to be drawn
	 */
	private void paintTripleBond(Bond bond)
	{
		paintSingleBond(bond);
		
		int[] coords = distanceCalculator(getBondCoordinates(bond),(r2dm.getBondWidth()/2 + r2dm.getBondDistance()));
		
		int[] newCoords1 = {coords[0],coords[1],coords[6],coords[7]};
		paintOneBond(newCoords1);
		
		int[] newCoords2 = {coords[2],coords[3],coords[4],coords[5]};
		paintOneBond(newCoords2);
	}
	
	/**
	 * Paints the inner bond of a doublebond that is part of a ring.
	 *
	 * @param   bond  The bond to be drawn
	 * @param   ring  The ring the bond is part of
	 */
	private void paintInnerBond(Bond bond, Ring ring)
	{
		Point center = ring.getCenter();

		int[] coords = distanceCalculator(getBondCoordinates(bond),(r2dm.getBondWidth()/2 + r2dm.getBondDistance()));
		double dist1 = Math.sqrt(Math.pow((coords[0] - center.x),2) + Math.pow((coords[1] - center.y),2));
		double dist2 = Math.sqrt(Math.pow((coords[2] - center.x),2) + Math.pow((coords[3] - center.y),2));
		if (dist1 < dist2)	
		{
			int[] newCoords1 = {coords[0],coords[1],coords[6],coords[7]};
			paintOneBond(shortenBond(newCoords1, ring.getRingSize()));
		}
		else
		{
			int[] newCoords2 = {coords[2],coords[3],coords[4],coords[5]};
			paintOneBond(shortenBond(newCoords2, ring.getRingSize()));
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
	private void paintOneBond(int[] coords)
	{
		int[] newCoords = distanceCalculator(coords, r2dm.getBondWidth()/2);
		int[] xCoords = {newCoords[0],newCoords[2],newCoords[4],newCoords[6]};
		int[] yCoords = {newCoords[1],newCoords[3],newCoords[5],newCoords[7]};
		g.fillPolygon(xCoords,yCoords,4);
	}
	

	/**
	 * Writes the coordinates of the atoms participating the given bond into an array.
	 *
	 * @param   bond   The given bond 
	 * @return     The array with the coordinates
	 */
	private int[] getBondCoordinates(Bond bond)
	{
		int beginX = (int)bond.getAtomAt(0).getPoint2D().x;
		int endX = (int)bond.getAtomAt(1).getPoint2D().x;
		int beginY = (int)bond.getAtomAt(0).getPoint2D().y;
		int endY = (int)bond.getAtomAt(1).getPoint2D().y;
		int[] coords = {beginX,beginY,endX,endY};
		return coords;
	}
	
	/**
	 * Gets the coordinates of two points (that represent a bond) and
	 * calculates for each the coordinates of two new points that have the given
	 * distance vertical to the bond.
	 *
	 * @param   coords  The coordinates of the two given points of the bond
	 * @param   dist  The distance between the given points and those to be calculated
	 * @return     The coordinates of the calculated four points
	 */
	private int[] distanceCalculator(int[] coords,double dist)
	{
		double angle;
		if ((coords[2] - coords[0]) == 0) angle = Math.PI/2;
		else
		{
			angle = Math.atan(((double)coords[3] - (double)coords[1]) / ((double)coords[2] - (double)coords[0]));
		}
		int begin1X = (int)(Math.cos(angle + Math.PI/2) * dist + coords[0]);
		int begin1Y = (int)(Math.sin(angle + Math.PI/2) * dist + coords[1]);
		int begin2X = (int)(Math.cos(angle - Math.PI/2) * dist + coords[0]);
		int begin2Y = (int)(Math.sin(angle - Math.PI/2) * dist + coords[1]);
		int end1X = (int)(Math.cos(angle - Math.PI/2) * dist + coords[2]);
		int end1Y = (int)(Math.sin(angle - Math.PI/2) * dist + coords[3]);
		int end2X = (int)(Math.cos(angle + Math.PI/2) * dist + coords[2]);
		int end2Y = (int)(Math.sin(angle + Math.PI/2) * dist + coords[3]);
		
		int[] newCoords = {begin1X,begin1Y,begin2X,begin2Y,end1X,end1Y,end2X,end2Y};
		return newCoords; 
	}
	
}
