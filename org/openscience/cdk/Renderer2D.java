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
package org.openscience.cdk;


import java.awt.*;
import java.util.Vector;
import org.openscience.cdk.ringsearch.*;


public class Renderer2D implements Renderer2DSettings
{
	Graphics g;
	SSSRFinder sssrf = new SSSRFinder();
	
	

	/**
	 * Constructs a Renderer2D with a graphics object.
	 *
	 * @param   graphics    The graphics object 
	 */
	public Renderer2D(Graphics graphics)
	{
		this.g = graphics;
	}


	/**
	 * triggers the methods to make the molecule fit into the frame and to paint it.
	 * 
	 * @param   mol  The Molecule to be drawn
	 */
	public void paintMolecule(Molecule mol)
	{
		Molecule molecule = scaleMolecule(mol);
	    molecule = translateAllPositive(molecule);
		molecule = translate(molecule,20,20);
		RingSet ringSet = sssrf.findSSSR(mol);
		Vector ringBonds = ringSet.getBonds();
		paintBonds(molecule.getBonds(), molecule.getBondCount(), ringBonds);
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
	 * Searches through all the bonds in the given array and triggers the paintBond method.
	 *
	 * @param   bonds     The array of bonds
	 * @param   number    The number of bonds in this array 
	 * @param   ringBonds   The bonds participating in a ring
	 */
	private void paintBonds(Bond[] bonds, int number, Vector ringBonds)
	{
		for (int i = 0; i < number; i++)
		{
			if (ringBonds.contains(bonds[i]))
			{
				paintRingBond(bonds[i]);
			}
			else
			{
				paintBond(bonds[i]);
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
		g.setColor(Color.gray);
		g.fillRect((int)(atom.getPoint3D().x - (xSymbOffset * 1.8)),(int)(atom.getPoint3D().y - (ySymbOffset * 0.8)),(int)fontSize,(int)fontSize); 
		g.setColor(Color.black);
		g.drawString(atom.getElement().getSymbol(),(int)(atom.getPoint3D().x - xSymbOffset),(int)(atom.getPoint3D().y + ySymbOffset));
		g.setColor(Color.white);
		g.drawLine((int)atom.getPoint3D().x,(int)atom.getPoint3D().y,(int)atom.getPoint3D().x,(int)atom.getPoint3D().y);
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
	 * Triggers the paint method suitable to the bondorder of the given bond.
	 *
	 * @param   bond    The Bond to be drawn.
	 */
	private void paintRingBond(Bond bond)
	{
		if (bond.getOrder() == 1)
		{
			paintSingleBond(bond);
		}
		else if (bond.getOrder() == 2)
		{
			paintSingleBond(bond);
			paintInnerBond(bond);
		}
		else if (bond.getOrder() == 3)
		{
			paintTripleBond(bond);
		}
	}
	

	/**
	 * Writes the coordinates of the atoms participating the given bond into an array.
	 *
	 * @param   bond   The given bond 
	 * @return     The array with the coordinates
	 */
	private int[] getBondCoordinates(Bond bond)
	{
		int beginX = (int)bond.getAtomAt(0).getPoint3D().x;
		int endX = (int)bond.getAtomAt(1).getPoint3D().x;
		int beginY = (int)bond.getAtomAt(0).getPoint3D().y;
		int endY = (int)bond.getAtomAt(1).getPoint3D().y;
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
	    	angle = Math.atan((coords[3] - coords[1])/(coords[2] - coords[0]));
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

	/**
	 * Paints the given singlebond.
	 *
	 * @param   bond  The singlebond to be drawn
	 */
	private void paintSingleBond(Bond bond)
	{
		int[] coords = distanceCalculator(getBondCoordinates(bond),bondWidth/2);		
		int[] xCoords = {coords[0],coords[2],coords[4],coords[6]};
		int[] yCoords = {coords[1],coords[3],coords[5],coords[7]};
		g.fillPolygon(xCoords,yCoords,4);
		
	}
	

	/**
	 * Paints The given doublebond.
	 *
	 * @param   bond  The doublebond to be drawn
	 */
	private void paintDoubleBond(Bond bond)
	{
		int[] coords = distanceCalculator(getBondCoordinates(bond),bondDistance/2);
		
		int[] newCoords1 = {coords[0],coords[1],coords[6],coords[7]};
		int[] coords1 = distanceCalculator(newCoords1,bondWidth/2);
		int[] xCoords1 = {coords1[0],coords1[2],coords1[4],coords1[6]};
		int[] yCoords1 = {coords1[1],coords1[3],coords1[5],coords1[7]};
		g.fillPolygon(xCoords1,yCoords1,4);
		
		int[] newCoords2 = {coords[2],coords[3],coords[4],coords[5]};
		int[] coords2 = distanceCalculator(newCoords2,bondWidth/2);
		int[] xCoords2 = {coords2[0],coords2[2],coords2[4],coords2[6]};
		int[] yCoords2 = {coords2[1],coords2[3],coords2[5],coords2[7]};
		g.fillPolygon(xCoords2,yCoords2,4);
				
	}
	
	/**
	 * Paints The given triplebond.
	 *
	 * @param   bond  The triplebond to be drawn
	 */
	private void paintTripleBond(Bond bond)
	{
		int[] coords = distanceCalculator(getBondCoordinates(bond),bondWidth/2);
		int[] xCoords = {coords[0],coords[2],coords[4],coords[6]};
		int[] yCoords = {coords[1],coords[3],coords[5],coords[7]};
		g.fillPolygon(xCoords,yCoords,4);
		
		coords = distanceCalculator(getBondCoordinates(bond),(bondWidth/2 + bondDistance));
		
		int[] newCoords1 = {coords[0],coords[1],coords[6],coords[7]};
		int[] coords1 = distanceCalculator(newCoords1,bondWidth/2);
		int[] xCoords1 = {coords1[0],coords1[2],coords1[4],coords1[6]};
		int[] yCoords1 = {coords1[1],coords1[3],coords1[5],coords1[7]};
		g.fillPolygon(xCoords1,yCoords1,4);
		
		int[] newCoords2 = {coords[2],coords[3],coords[4],coords[5]};
		int[] coords2 = distanceCalculator(newCoords2,bondWidth/2);
		int[] xCoords2 = {coords2[0],coords2[2],coords2[4],coords2[6]};
		int[] yCoords2 = {coords2[1],coords2[3],coords2[5],coords2[7]};
		g.fillPolygon(xCoords2,yCoords2,4);
	}
	
	
	private void paintInnerBond(Bond bond)
	{
		int[] coords = distanceCalculator(getBondCoordinates(bond),(bondWidth/2 + bondDistance));
		int[] newCoords1 = {coords[0],coords[1],coords[6],coords[7]};
		int[] coords1 = distanceCalculator(newCoords1,bondWidth/2);
		int[] xCoords1 = {coords1[0],coords1[2],coords1[4],coords1[6]};
		int[] yCoords1 = {coords1[1],coords1[3],coords1[5],coords1[7]};
		g.fillPolygon(xCoords1,yCoords1,4);
	}
	
	/**
	 * Adds an automatically calculated offset to the coordinates of all atoms
	 * such that all coordinates are positive and the smallest x or y coordinate 
	 * is exactly zero.
	 *
	 * @param   molecule for which all the atoms are translated to positive coordinates
	 */
	private Molecule translateAllPositive(Molecule molecule)
	{
		double transX = 0,transY = 0;
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			if (molecule.getAtom(i).getPoint3D().x < transX)
			{
				transX = molecule.getAtom(i).getPoint3D().x;
			}
			if (molecule.getAtom(i).getPoint3D().y < transY)
			{
				transY = molecule.getAtom(i).getPoint3D().y;
			}
		}
		molecule = translate(molecule,transX * -1,transY * -1);
		return molecule;
	}
	

	/**
	 * Translates the given molecule by the given Vector.
	 *
	 * @param   molecule  The molecule to be translated
	 * @param   transX  translation in x direction
	 * @param   transY  translation in y direction
	 * @return    translsted molecule 
	 */
	private Molecule translate(Molecule molecule,double transX, double transY)
	{
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			molecule.getAtom(i).getPoint3D().x += transX;
			molecule.getAtom(i).getPoint3D().y += transY;
		}
		return molecule;
	}
	

	/**
	 * Multiplies all the coordinates of the atoms of the given molecule with the scalefactor.
	 *
	 * @param   molecule  The molecule to be scaled
	 * @return     The molecule with scaled coordinates
	 */
	private Molecule scaleMolecule(Molecule molecule)
	{
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			molecule.getAtom(i).getPoint3D().x *= scaleFactor;
			molecule.getAtom(i).getPoint3D().y *= scaleFactor;
		}
		return molecule;
	}
}


