/* RingPlacer.java
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
 *  
 */
 
package org.openscience.cdk.layout;

import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.geometry.*;
import javax.vecmath.*;
import java.util.Vector;
import java.lang.Math;
import java.awt.*;

/**
 * Methods for generating coordinates for ring atoms in various situations 
 * (condensation, spiro-attachment, etc.)
 * They can be used for Automated Structure Diagram Generation or in the interactive
 * buildup of ringsystems by the user. 
 **/

public class RingPlacer implements CDKConstants
{
	static boolean debug = false;
	public static int ISPLACED = 0;	
	
	private Molecule molecule; 
	
	public RingPlacer()
	{
	
	}


	/**
	 *
	 *
	 * @param   ring  
	 * @param   sharedAtoms  
	 * @param   sharedAtomsCenter  
	 * @param   ringCenterVector  
	 * @param   bondLength  
	 */
	public void placeRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
	{
		int sharedAtomCount = sharedAtoms.getAtomCount();
		if (debug) System.out.println("placeRing -> sharedAtomCount: " + sharedAtomCount);
		if (sharedAtomCount > 2) 
		{
			placeBridgedRing(ring, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
		}
		else if (sharedAtomCount == 2)
		{
			placeFusedRing(ring, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
		}
		else if (sharedAtomCount == 1)
		{
			placeSpiroRing(ring, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
		}

	}
	
	
	private  void placeBridgedRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		double radius = getNativeRingRadius(ring, bondLength);
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenterVector.normalize();
		if (debug) System.out.println("placeFusedRing->: ringCenterVector.length()" + ringCenterVector.length());	
		ringCenterVector.scale(radius);
		ringCenter.add(ringCenterVector);


		Atom[] bridgeAtoms = getBridgeAtoms(sharedAtoms);
		Atom bondAtom1 = bridgeAtoms[0];
		Atom bondAtom2 = bridgeAtoms[1];

		Vector2d bondAtom1Vector = new Vector2d(bondAtom1.getPoint2D());
		Vector2d bondAtom2Vector = new Vector2d(bondAtom2.getPoint2D());		
		Vector2d originRingCenterVector = new Vector2d(ringCenter);		

		bondAtom1Vector.sub(originRingCenterVector);
		bondAtom2Vector.sub(originRingCenterVector);		

		double occupiedAngle = bondAtom1Vector.angle(bondAtom2Vector);		
		
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize() - sharedAtoms.getAtomCount() + 1);

		if (debug) System.out.println("placeFusedRing->occupiedAngle: " + Math.toDegrees(occupiedAngle));
		if (debug) System.out.println("placeFusedRing->remainingAngle: " + Math.toDegrees(remainingAngle));

		if (debug) System.out.println("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));				


		Atom startAtom;

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		double xDiff = bondAtom1.getX2D() - bondAtom2.getX2D();
		double yDiff = bondAtom1.getY2D() - bondAtom2.getY2D();
		
		double startAngle;;	
		
		int direction = 1;
		// if bond is vertical
		if (xDiff == 0)
		{
			if (debug) System.out.println("placeFusedRing->Bond is vertical");
			//starts with the lower Atom
			if (bondAtom1.getY2D() > bondAtom2.getY2D())
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerX < bondAtom1.getX2D())
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}

		  // if bond is not vertical
		else
		{
			//starts with the left Atom
			if (bondAtom1.getX2D() > bondAtom2.getX2D())
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerY - bondAtom1.getY2D() > (centerX - bondAtom1.getX2D()) * yDiff / xDiff)
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}
		startAngle = GeometryTools.getAngle(startAtom.getX2D() - ringCenter.x, startAtom.getY2D() - ringCenter.y);

		Atom currentAtom = startAtom;
		Bond currentBond = sharedAtoms.getBondAt(0);
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount() - 2; i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		try
		{
			if (debug) System.out.println("placeFusedRing->startAtom is: " + molecule.getAtomNumber(startAtom));
		}
		catch(Exception exc)
		{
		
		}
		if (debug) System.out.println("placeFusedRing->startAngle: " + Math.toDegrees(startAngle));
		if (debug) System.out.println("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));		

		addAngle = addAngle * direction;
		drawPolygon(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	}
	
	private void placeSpiroRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
	{

		if (debug) System.out.println("placeSpiroRing");
		double radius = getNativeRingRadius(ring, bondLength);
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenterVector.normalize();
		ringCenterVector.scale(radius);
		ringCenter.add(ringCenterVector);
		double addAngle = 2 * Math.PI / ring.getRingSize();

		Atom startAtom = sharedAtoms.getAtomAt(0);

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		int direction = 1;

		Atom currentAtom = startAtom;
		double startAngle = GeometryTools.getAngle(startAtom.getX2D() - ringCenter.x, startAtom.getY2D() - ringCenter.y);
		/* 
		 * Get one bond connected to the spiro bridge atom.
		 * It doesn't matter in which direction we draw.
		 */ 
		Bond[] bonds = ring.getConnectedBonds(startAtom);
		if (debug) System.out.println(startAtom);
		Bond currentBond = bonds[0];
		if (debug) System.out.println(bonds.length + ", " + bonds[0] + ", " + bonds[1]);
		Vector atomsToDraw = new Vector();
		/* 
		 * Store all atoms to draw in consequtive order relative to the 
		 * chosen bond.
		 */ 
		for (int i = 0; i < ring.getBondCount(); i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		if (debug) System.out.println("currentAtom  "+currentAtom);
		if (debug) System.out.println("startAtom  "+startAtom);

		drawPolygon(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	
	}


	/**
	 *
	 *
	 * @param   ring  
	 * @param   sharedAtoms  
	 * @param   sharedAtomsCenter  
	 * @param   ringCenterVector  
	 * @param   bondLength  
	 */
	public  void placeFusedRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		double radius = getNativeRingRadius(ring, bondLength);
		double newRingPerpendicular = Math.sqrt(Math.pow(radius, 2) - Math.pow(bondLength/2, 2));
		ringCenterVector.normalize();
		if (debug) System.out.println("placeFusedRing->: ringCenterVector.length()" + ringCenterVector.length());	
		ringCenterVector.scale(newRingPerpendicular);
		ringCenter.add(ringCenterVector);

		Atom bondAtom1 = sharedAtoms.getAtomAt(0);
		Atom bondAtom2 = sharedAtoms.getAtomAt(1);

		Vector2d bondAtom1Vector = new Vector2d(bondAtom1.getPoint2D());
		Vector2d bondAtom2Vector = new Vector2d(bondAtom2.getPoint2D());		
		Vector2d originRingCenterVector = new Vector2d(ringCenter);		

		bondAtom1Vector.sub(originRingCenterVector);
		bondAtom2Vector.sub(originRingCenterVector);		

		double occupiedAngle = bondAtom1Vector.angle(bondAtom2Vector);		
		
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize()-1);
	
		if (debug) System.out.println("placeFusedRing->occupiedAngle: " + Math.toDegrees(occupiedAngle));
		if (debug) System.out.println("placeFusedRing->remainingAngle: " + Math.toDegrees(remainingAngle));
		if (debug) System.out.println("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));				


		Atom startAtom;

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		double xDiff = bondAtom1.getX2D() - bondAtom2.getX2D();
		double yDiff = bondAtom1.getY2D() - bondAtom2.getY2D();
		
		double startAngle;;	
		
		int direction = 1;
		// if bond is vertical
     	if (xDiff == 0)
		{
			if (debug) System.out.println("placeFusedRing->Bond is vertical");
			//starts with the lower Atom
			if (bondAtom1.getY2D() > bondAtom2.getY2D())
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerX < bondAtom1.getX2D())
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}

		  // if bond is not vertical
		else
		{
			//starts with the left Atom
			if (bondAtom1.getX2D() > bondAtom2.getX2D())
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerY - bondAtom1.getY2D() > (centerX - bondAtom1.getX2D()) * yDiff / xDiff)
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}
		startAngle = GeometryTools.getAngle(startAtom.getX2D() - ringCenter.x, startAtom.getY2D() - ringCenter.y);
	
		Atom currentAtom = startAtom;
		Bond currentBond = sharedAtoms.getBondAt(0);
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount() - 2; i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		try
		{
			if (debug) System.out.println("placeFusedRing->startAtom is: " + molecule.getAtomNumber(startAtom));
		}
		catch(Exception exc)
		{
		
		}
		if (debug) System.out.println("placeFusedRing->startAngle: " + Math.toDegrees(startAngle));
		if (debug) System.out.println("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));		

		addAngle = addAngle * direction;
		drawPolygon(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	}
	


	/**
	 *
	 *
	 * @param   startAtom  
	 * @param   atomsToDraw  
	 * @param   startAngle  
	 * @param   addAngle  
	 * @param   direction  
	 * @param   bondLength  
	 */
	public void drawPolygon(Vector atomsToDraw, Point2d rotationCenter, double startAngle, double addAngle, double radius)
	{
		Atom connectAtom = null;
		double angle = startAngle;
		double newX, newY, x, y;
		if (debug) System.out.println("drawPolygon->startAngle: " + Math.toDegrees(angle));
		for (int i = 0; i < atomsToDraw.size(); i++)
		{
			connectAtom = (Atom)atomsToDraw.elementAt(i);
			try
			{
				if (debug) System.out.println("drawPolygon->number of connectAtom: " + molecule.getAtomNumber(connectAtom));
			}
			catch(Exception exc)
			{
			
			}
		    angle = angle + addAngle;
			if (angle >= 2 * Math.PI)
			{
				angle -= 2*Math.PI;
			}
		    if (debug)  System.out.println("drawPolygon->angle: " +Math.toDegrees( angle));
		    x = Math.cos(angle) * radius;
		    if (debug) System.out.println("drawPolygon-> x " + x);
		    y = Math.sin(angle) * radius;
			if (debug) System.out.println("drawPolygon-> y " + y);
			newX = x + rotationCenter.x;
			newY = y + rotationCenter.y;
//			angle = angle + addAngle;
			if (connectAtom.getPoint2D() == null)
			{
				connectAtom.setPoint2D(new Point2d(newX, newY));				
			}
		}
		
	}
	
	

	/**
	 * True if coordinates have been assigned to all atoms in all rings. 
	 *
	 * @param   rs  The ringset to be checked
	 * @return  True if coordinates have been assigned to all atoms in all rings.    
	 */

	/**
	 *
	 *
	 * @param   rs  
	 * @return     
	 */
	public  boolean allPlaced(RingSet rs)
	{
		for (int i = 0; i < rs.size(); i++)
		{
			if (!((Ring)rs.elementAt(i)).flags[ISPLACED])
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 *
	 *
	 * @param   sharedAtoms  
	 * @return     
	 */
	private  Atom[] getBridgeAtoms(AtomContainer sharedAtoms)
	{
		Atom[] bridgeAtoms = new Atom[2];
		Atom atom;
		int counter = 0; 
		for (int f = 0; f < sharedAtoms.getAtomCount(); f++)
		{
			atom = sharedAtoms.getAtomAt(f);	
			if (sharedAtoms.getConnectedAtoms(atom).length == 1)
			{
				bridgeAtoms[counter] = atom;
				counter ++;
			}
		}
		return bridgeAtoms;
	}


	/**
	 * Returns the ring radius of a perfect polygons of size ring.getAtomCount()
	 * The ring radius is the distance of each atom to the ringcenter.
	 *
	 * @param   ring  The ring for which the radius is to calculated
	 * @param   bondLength  The bond length for each bond in the ring
	 * @return  The radius of the ring.   
	 */
	public  double getNativeRingRadius(Ring ring, double bondLength)
	{
		int size = ring.getAtomCount();
		double radius = bondLength / (2 * Math.sin((Math.PI) / size));
		return radius;
	}



	
	public Molecule getMolecule()
	{
		return this.molecule;
	}

	public void setMolecule(Molecule molecule)
	{
		this.molecule = molecule;
	}
}
