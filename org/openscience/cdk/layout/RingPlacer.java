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

	public static void placeRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
	{
//		Point2d ringCenter = new Point2d(sharedAtomsCenter);
//		if (debug) System.out.println("placeRing -> bondCenter " + sharedAtomsCenter);		
//		if (debug) System.out.println("placeRing -> ringCenterVector: " + ringCenterVector + ", placeRing -> ringCenterVector.length(): " + ringCenterVector.length());
//		ringCenter.add(ringCenterVector);
//		double ringRadius = Math.sqrt(Math.pow(ringCenterVector.length(), 2) + Math.pow(bondLength / 2, 2));
//		if (debug) System.out.println("ringRadius: " + ringRadius);
//		double occupiedAngle = 2 * Math.asin((bondLength / 2) / ringRadius);
//		if (debug) System.out.println("occupiedAngle: " + occupiedAngle + " (" + occupiedAngle / Math.PI * 180 + ")");
//		double remainingAngle = (2 * Math.PI) - occupiedAngle;
//		if (debug) System.out.println("remainingAngle: " + remainingAngle + " (" + remainingAngle / Math.PI * 180 + ")");
//		double addAngle = remainingAngle / (ring.getRingSize() - (sharedAtoms.getAtomCount() - 1));
//		if (debug) System.out.println("addAngle: " + addAngle + " (" + addAngle / Math.PI * 180 + ")");

		int sharedAtomCount = sharedAtoms.getAtomCount();
		if (sharedAtomCount > 2) 
		{
		
		}
		else if (sharedAtomCount == 2)
		{
			completeFusedRing(ring, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
		}
		else if (sharedAtomCount == 1)
		{
		
		}

	}
	
	
//	completeBridgedRing()
//	{
//	}
//	
//	completeSpiroRing()
//	{
//	}

	public static void completeFusedRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenter.add(ringCenterVector);
		double occupiedAngle = 2 * Math.atan((bondLength / 2) / ringCenterVector.length());
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize() - (sharedAtoms.getAtomCount() - 1));
	
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
		Bond currentBond = sharedAtoms.getBondAt(0);
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount() - 2; i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		if (debug) System.out.println("currentAtom  "+currentAtom);
		if (debug) System.out.println("startAtom  "+startAtom);
	
		drawPolygon(startAtom, atomsToDraw, startAngle, addAngle, direction, bondLength);
	}
	


	private static void drawPolygon(Atom startAtom, Vector atomsToDraw, double startAngle, double addAngle, double direction, double bondLength)
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
	 * True if coordinates have been assigned to all atoms in all rings. 
	 *
	 * @param   rs  The ringset to be checked
	 * @return  True if coordinates have been assigned to all atoms in all rings.    
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


}
