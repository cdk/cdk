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
	static boolean debug = true;
	public static int ISPLACED = 0;	


	/**
	 *
	 *
	 * @param   ring  
	 * @param   sharedAtoms  
	 * @param   sharedAtomsCenter  
	 * @param   ringCenterVector  
	 * @param   bondLength  
	 */
	public static void placeRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
	{
		int sharedAtomCount = sharedAtoms.getAtomCount();
		System.out.println("placeRing -> sharedAtomCount: " + sharedAtomCount);
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
	
	
	private static void placeBridgedRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenter.add(ringCenterVector);
		double occupiedAngle = 2 * Math.atan((bondLength / 2) / ringCenterVector.length());
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize() - (sharedAtoms.getAtomCount() - 1));
	
		Atom[] bridgeAtoms = getBridgeAtoms(sharedAtoms);
//		Atom bondAtom1 = bridgeAtoms[0];
//		Atom bondAtom2 = bridgeAtoms[1];
		Atom bondAtom1 = sharedAtoms.getAtomAt(0);
		Atom bondAtom2 = sharedAtoms.getAtomAt(1);
		
		Atom startAtom;

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		double xDiff = bondAtom1.getX2D() - bondAtom2.getX2D();
		double yDiff = bondAtom1.getY2D() - bondAtom2.getY2D();
		
		double startAngle = Math.atan(yDiff/xDiff);	
		
		int direction = 1;
		// if bond is vertical
		  if (xDiff == 0)
		{
			startAngle = Math.abs(startAngle);
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
		
		Atom currentAtom = startAtom;
		Bond[] bonds = sharedAtoms.getConnectedBonds(currentAtom);
		Bond currentBond = bonds[0];
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount() - 2; i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		if (debug) System.out.println("currentAtom  "+currentAtom);
		if (debug) System.out.println("startAtom  "+startAtom);
	
		drawPolygon(atomsToDraw, ringCenter, startAngle, addAngle, bondLength);
	}
	
	private static void placeSpiroRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
	{
		System.out.println("placeSpiroRing");
		double ringRadius = getNativeRingRadius(ring, bondLength);
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenterVector.scale(ringRadius/ringCenterVector.length());
		ringCenter.add(ringCenterVector);
		double addAngle = 2 * Math.PI / ring.getRingSize();

		Atom startAtom = sharedAtoms.getAtomAt(0);

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		double startAngle = addAngle;	
		
		int direction = 1;

		Atom currentAtom = startAtom;
		/* 
		 * Get one bond connected to the spiro bridge atom.
		 * It doesn't matter in which direction we draw.
		 */ 
		Bond[] bonds = ring.getConnectedBonds(startAtom);
		System.out.println(startAtom);
		Bond currentBond = bonds[0];
		System.out.println(bonds.length + ", " + bonds[0] + ", " + bonds[1]);
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

		drawPolygon(atomsToDraw, ringCenter, startAngle, addAngle, ringRadius);
	
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
	public static void placeFusedRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		double newRingPerpendicular = Math.sqrt(Math.pow(getNativeRingRadius(ring, bondLength), 2) - Math.pow(bondLength/2, 2));
		ringCenterVector.scale(newRingPerpendicular/ringCenterVector.length());

		ringCenter.add(ringCenterVector);

		double occupiedAngle = Math.PI * 2 / (ring.getAtomCount());
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize() - 1);
		double ringRadius = Math.sqrt(Math.pow(ringCenterVector.length(), 2) + Math.pow(bondLength/2, 2));
	
	
	
		System.out.println("remainingAngle: " + remainingAngle);
		System.out.println("addAngle: " + addAngle);				
	
	
	
		Atom bondAtom1 = sharedAtoms.getAtomAt(0);
		Atom bondAtom2 = sharedAtoms.getAtomAt(1);

		Vector2d atom1Vector = new Vector2d(bondAtom1.getPoint2D());
		Vector2d atom2Vector = new Vector2d(bondAtom2.getPoint2D());		
		Vector2d originRingCenterVector = new Vector2d(ringCenter);
		
		Vector2d yAxisVector = new Vector2d(0, 1);

		/* Now we have two vectors pointing from the ring center to 
		 * each of the two existing atoms. 
		 */
		atom1Vector.sub(originRingCenterVector);
		atom2Vector.sub(originRingCenterVector);		
		/* Get the angle between the two atoms and the y axis.  */
		occupiedAngle = atom2Vector.angle(atom1Vector);
		double angle1 = atom1Vector.angle(yAxisVector);
		double angle2 = atom2Vector.angle(yAxisVector);		
		System.out.println("angle 1: " + angle1);
		System.out.println("angle 2: " + angle2);


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
			System.out.println("Bond is vertical");
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
				direction = -1;
			}
			else
			{
				direction = 1;
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
		System.out.println("ringCenterVector: " + ringCenterVector);		
		System.out.println("Angle to X axis: " + ringCenterVector.angle(new Vector2d(1,0)));
		System.out.println("Angle to Y axis: " + ringCenterVector.angle(new Vector2d(0,1)));		
		if (debug) System.out.println("startAtom  "+startAtom);

		ringCenterVector.negate();
		startAngle = Math.atan(ringCenterVector.y / ringCenterVector.x) + (addAngle / 2  * direction);
		System.out.println("atan Angle to x axis: " + Math.atan(ringCenterVector.y / ringCenterVector.x));		
		
		Atom currentAtom = startAtom;
		Bond currentBond = sharedAtoms.getBondAt(0);
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount() - 2; i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		System.out.println("startAngle: " + startAngle);
		System.out.println("addAngle: " + addAngle);		

		addAngle = addAngle * direction;
		drawPolygon(atomsToDraw, ringCenter, startAngle, addAngle, ringRadius);
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
	public static void oldPlaceFusedRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenter.add(ringCenterVector);
		double occupiedAngle = Math.PI * 2 / (ring.getAtomCount());
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize() - 1);
		double ringRadius = Math.sqrt(Math.pow(ringCenterVector.length(), 2) + Math.pow(bondLength/2, 2));
	
//		newRingPerpendicular = Math.sqrt(Math.pow(getNativeRingRadius(connectedRing, bondLength), 2) - Math.pow(bondLength/2, 2));
//		newRingCenterVector.scale(newRingPerpendicular/oldRingCenterVector.length());
	
	
		System.out.println("occupiedAngle: " + occupiedAngle);
		System.out.println("remainingAngle: " + remainingAngle);
		System.out.println("addAngle: " + addAngle);				
	
	
	
		Atom bondAtom1 = sharedAtoms.getAtomAt(0);
		Atom bondAtom2 = sharedAtoms.getAtomAt(1);
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
			System.out.println("Bond is vertical");
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
				direction = -1;
			}
			else
			{
				direction = 1;
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
		System.out.println("ringCenterVector: " + ringCenterVector);		
		System.out.println("Angle to X axis: " + ringCenterVector.angle(new Vector2d(1,0)));
		System.out.println("Angle to Y axis: " + ringCenterVector.angle(new Vector2d(0,1)));		
		if (debug) System.out.println("startAtom  "+startAtom);

		ringCenterVector.negate();
		startAngle = Math.atan(ringCenterVector.y / ringCenterVector.x) + (addAngle / 2  * direction);
		System.out.println("atan Angle to x axis: " + Math.atan(ringCenterVector.y / ringCenterVector.x));		
		
		Atom currentAtom = startAtom;
		Bond currentBond = sharedAtoms.getBondAt(0);
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount() - 2; i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		System.out.println("startAngle: " + startAngle);
		System.out.println("addAngle: " + addAngle);		

		addAngle = addAngle * direction;
		drawPolygon(atomsToDraw, ringCenter, startAngle, addAngle, ringRadius);
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
	private static void drawOuterAnglePolygon(Atom startAtom, Vector atomsToDraw, double startAngle, double addAngle, double direction, double bondLength)
	{
		Atom connectAtom = null;
		double angle = startAngle;
		double sumX = startAtom.getX2D(), sumY = startAtom.getY2D(), x = 0, y = 0;
		Atom[] connectedAtoms;
		for (int i = 0; i < atomsToDraw.size(); i++)
		{
			connectAtom = (Atom)atomsToDraw.elementAt(i);
		    angle = angle + addAngle * direction;
		    x = Math.cos(angle) * bondLength;
		    y = Math.sin(angle) * bondLength;
			sumX = sumX + x;
			sumY = sumY + y;
			if (connectAtom.getPoint2D() == null)
			{
				connectAtom.setPoint2D(new Point2d(sumX, sumY));				
			}
		}
		
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
	private static void drawPolygon(Vector atomsToDraw, Point2d ringCenter, double startAngle, double addAngle, double ringRadius)
	{
		Atom connectAtom = null;
		double angle = startAngle;
		double newX, newY, x, y;
		System.out.println("drawPolygon->angle: " + angle);
		for (int i = 0; i < atomsToDraw.size() - 0; i++)
		{
			connectAtom = (Atom)atomsToDraw.elementAt(i);
		    angle = angle + addAngle;
		    System.out.println("drawPolygon->angle: " + angle);
		    x = Math.cos(angle) * ringRadius;
		    System.out.println("drawPolygon-> x " + x);
		    y = Math.sin(angle) * ringRadius;
			System.out.println("drawPolygon-> y " + y);
			newX = x + ringCenter.x;
			newY = y + ringCenter.y;
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
	public static boolean allPlaced(RingSet rs)
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
	private static Atom[] getBridgeAtoms(AtomContainer sharedAtoms)
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
	public static double getNativeRingRadius(Ring ring, double bondLength)
	{
		int size = ring.getAtomCount();
		double angle = 2 * Math.PI / size;
		double ringRadius = bondLength / (2 * Math.sin(angle/2));
		return ringRadius;
	}


}
