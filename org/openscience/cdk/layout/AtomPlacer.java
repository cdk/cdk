/* AtomPlacer.java
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
 * Methods for generating coordinates for atoms in various situations 
 * They can be used for Automated Structure Diagram Generation or in the interactive
 * buildup of molecules by the user. 
 **/

public class AtomPlacer
{
	Molecule molecule;
	
	public void distributePartners(Atom atom, AtomContainer sharedAtoms, AtomContainer partners, double bondLength) throws java.lang.Exception
	{
		double occupiedAngle = 0;
		double smallestDistance = Double.MAX_VALUE;
		Atom[] nearestAtoms = new Atom[2];
		Atom[] sortedAtoms = null;
		/* calculate the direction away from the already placed partners of atom */
		Point2d sharedAtomsCenter = sharedAtoms.get2DCenter();
		Vector2d sharedAtomsCenterVector = new Vector2d(sharedAtomsCenter);		
		Vector2d newDirection = new Vector2d(atom.getPoint2D());
		Vector atomsToDraw = new Vector();
		/* if the least hindered side of the atom is clearly defined (bondLength / 10 is an arbitrary value that seemed reasonable) */	
		if (sharedAtomsCenter.distance(atom.getPoint2D()) > bondLength / 10)
		{
			newDirection.sub(sharedAtomsCenterVector);
			newDirection.normalize();
			newDirection.scale(bondLength);
			Point2d distanceMeasure = new Point2d(sharedAtomsCenter);	
			distanceMeasure.add(newDirection);
			/* get the two sharedAtom partners with the smallest distance to the new center */
			sortedAtoms = sharedAtoms.getAtoms();
			GeometryTools.sortBy2DDistance(sortedAtoms, distanceMeasure);
			Vector2d closestPoint1 = new Vector2d(sortedAtoms[0].getPoint2D());
			Vector2d closestPoint2 = new Vector2d(sortedAtoms[1].getPoint2D());			
			closestPoint1.sub(new Vector2d(atom.getPoint2D()));
			closestPoint2.sub(new Vector2d(atom.getPoint2D()));			
			occupiedAngle = closestPoint1.angle(closestPoint2);
			System.out.println("distributePartners->occupiedAngle: " + Math.toDegrees(occupiedAngle));
		
		}
		else
		{
			/* find the pair of partners from sharedAtoms
			 which has the largest angle between them */
		}


		double angle1 = GeometryTools.getAngle(sortedAtoms[0].getX2D() - atom.getX2D(), sortedAtoms[0].getY2D() - atom.getY2D());				
		double angle2 = GeometryTools.getAngle(sortedAtoms[1].getX2D() - atom.getX2D(), sortedAtoms[1].getY2D() - atom.getY2D());				
		Atom startAtom = null;		 
		if (angle1 > angle2)
		{
			if (angle1 - angle2 < Math.PI)
			{
				startAtom = sortedAtoms[0];
			}
			else
			{
				startAtom = sortedAtoms[1];
			}
		
		}
		else
		{
			if (angle2 - angle1 < Math.PI)
			{
				startAtom = sortedAtoms[1];
			}
			else
			{
				startAtom = sortedAtoms[0];
			}
		}

		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (partners.getAtomCount() + 1);

		for (int f = 0; f < partners.getAtomCount(); f++)
		{
			atomsToDraw.addElement(partners.getAtomAt(f));
		}
		double radius = bondLength;


		
		double startAngle = GeometryTools.getAngle(startAtom.getX2D() - atom.getX2D(), startAtom.getY2D() - atom.getY2D());
		/* 
		 * Get one bond connected to the spiro bridge atom.
		 * It doesn't matter in which direction we draw.
		 */ 
		new RingPlacer().drawPolygon(atomsToDraw, new Point2d(atom.getPoint2D()), startAngle, addAngle, radius);
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