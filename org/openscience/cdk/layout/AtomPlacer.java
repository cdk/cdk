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

public class AtomPlacer implements CDKConstants
{
	Molecule molecule;
	public boolean debug = false;
	
	
	

	/**
	 * Return the molecule the AtomPlacer currently works with
	 *
	 * @return the molecule the AtomPlacer currently works with    
	 */
	public Molecule getMolecule()
	{
		return this.molecule;
	}


	/**
	 * Sets the molecule the AtomPlacer currently works with
	 *
	 * @param   molecule  the molecule the AtomPlacer currently works with
	 */
	public void setMolecule(Molecule molecule)
	{
		this.molecule = molecule;
	}

	

	/**
	 * Distribute the bonding partners of an atom such that they fill
	 * the remaining space around an atom in a geometrically nice way
	 *
	 * @param   atom  The atom whose partners are to be placed
	 * @param   sharedAtoms  The atoms which are already placed
	 * @param   partners  The partners to be placed
	 * @param   bondLength  The standared bondlength
	 * @exception   Exception  An exception if something goes wrong
	 */
	public void distributePartners(Atom atom, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, AtomContainer partners, double bondLength) throws java.lang.Exception
	{
		double occupiedAngle = 0;
		double smallestDistance = Double.MAX_VALUE;
		Atom[] nearestAtoms = new Atom[2];
		Atom[] sortedAtoms = null;
		/* calculate the direction away from the already placed partners of atom */
		//Point2d sharedAtomsCenter = sharedAtoms.get2DCenter();
		Vector2d sharedAtomsCenterVector = new Vector2d(sharedAtomsCenter);	

		Vector2d newDirection = new Vector2d(atom.getPoint2D());
		Vector2d occupiedDirection = new Vector2d(sharedAtomsCenter);
		occupiedDirection.sub(newDirection);
		Vector atomsToDraw = new Vector();
		/* if the least hindered side of the atom is clearly defined (bondLength / 10 is an arbitrary value that seemed reasonable) */	
		//newDirection.sub(sharedAtomsCenterVector);
		sharedAtomsCenterVector.sub(newDirection);
		newDirection = sharedAtomsCenterVector;
		newDirection.normalize();
		newDirection.scale(bondLength);
		newDirection.negate();
		Point2d distanceMeasure = new Point2d(atom.getPoint2D());	
		distanceMeasure.add(newDirection);

//		Atom marker = new Atom(new Element("O"), new Point2d(sharedAtomsCenter));
//		marker.flags[ISPLACED] = true;
//		molecule.addAtom(marker);			
		
		/* get the two sharedAtom partners with the smallest distance to the new center */
		sortedAtoms = sharedAtoms.getAtoms();
		GeometryTools.sortBy2DDistance(sortedAtoms, distanceMeasure);
		System.out.println(molecule.getAtomNumber(sortedAtoms[0]));
		System.out.println(molecule.getAtomNumber(sortedAtoms[1]));			
		Vector2d closestPoint1 = new Vector2d(sortedAtoms[0].getPoint2D());
		Vector2d closestPoint2 = new Vector2d(sortedAtoms[1].getPoint2D());			
		closestPoint1.sub(new Vector2d(atom.getPoint2D()));
		closestPoint2.sub(new Vector2d(atom.getPoint2D()));			
		occupiedAngle = closestPoint1.angle(occupiedDirection);
		occupiedAngle += closestPoint2.angle(occupiedDirection);
		
		
		double angle1 = GeometryTools.getAngle(sortedAtoms[0].getX2D() - atom.getX2D(), sortedAtoms[0].getY2D() - atom.getY2D());				
		double angle2 = GeometryTools.getAngle(sortedAtoms[1].getX2D() - atom.getX2D(), sortedAtoms[1].getY2D() - atom.getY2D());				
		double angle3 = GeometryTools.getAngle(distanceMeasure.x - atom.getX2D(), distanceMeasure.y - atom.getY2D());						
		System.out.println("distributePartners->sortedAtoms[0]: " + molecule.getAtomNumber(sortedAtoms[0]));
		System.out.println("distributePartners->sortedAtoms[1]: " + molecule.getAtomNumber(sortedAtoms[1]));		
		System.out.println("distributePartners->angle1: " + Math.toDegrees(angle1));
		System.out.println("distributePartners->angle2: " + Math.toDegrees(angle2));

		Atom startAtom = null;
		
		if (angle1 > angle3)
		{
			if (angle1 - angle3 < Math.PI)
			{
				startAtom = sortedAtoms[1];
			}
			else
			{
				// 12 o'clock is between the two vectors
				startAtom = sortedAtoms[0];
			}
		
		}
		else
		{
			if (angle3 - angle1 < Math.PI)
			{
				startAtom = sortedAtoms[0];
			}
			else
			{
				// 12 o'clock is between the two vectors
				startAtom = sortedAtoms[1];
			}
		}
		
				 
		System.out.println("distributePartners->startAtom: " + molecule.getAtomNumber(startAtom));

		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (partners.getAtomCount() + 1);

		System.out.println("distributePartners->remainingAngle: " + Math.toDegrees(remainingAngle));
		System.out.println("distributePartners->addAngle: " + Math.toDegrees(addAngle));
		System.out.println("distributePartners-> partners.getAtomCount(): " + partners.getAtomCount());
		
		for (int f = 0; f < partners.getAtomCount(); f++)
		{
			atomsToDraw.addElement(partners.getAtomAt(f));
		}
		double radius = bondLength;
		double startAngle = GeometryTools.getAngle(startAtom.getX2D() - atom.getX2D(), startAtom.getY2D() - atom.getY2D());
		populatePolygonCorners(atomsToDraw, new Point2d(atom.getPoint2D()), startAngle, addAngle, radius);
		
	}

	/**
	 * Grows a chain. 
	 * Expects the first atom to be placed and places the next 
	 * atom according to initialBondVector.
	 * The rest of the chain is placed such that it is as linear as possible
	 * (in the overall result, the angles in the chain are set to 120 Deg.)
	 *
	 * @param   ac  The AtomContainer containing the chain atom to be placed
	 * @param   initialBondVector  The Vector indicating the direction of the first bond
	 */
	public void growChain(AtomContainer ac, Atom atom, Vector2d initialBondVector, double bondLength)
	{
		Vector2d bondVector = initialBondVector;
		Point2d atomPoint = null;
		Point2d nextAtomPoint = null;
		Atom nextAtom = null;		
		Atom rootAtom = ac.getAtomAt(0);
		Point2d tempAtomPoint = null;
		Bond[] bonds;
		for (int f = 0; f < ac.getAtomCount() - 1; f++)
		{
			bonds= molecule.getConnectedBonds(atom);
			for (int g = 0; g < bonds.length; g++)
			{
				nextAtom = bonds[g].getConnectedAtom(atom);
				if (!nextAtom.flags[VISITED]) break;
			}
			nextAtom.flags[VISITED] = true;
			atomPoint = new Point2d(atom.getPoint2D());
			bondVector.normalize();
			bondVector.scale(bondLength);	
			atomPoint.add(bondVector);
			nextAtom.setPoint2D(atomPoint);		
			bondVector = getNextBondVector(nextAtom, atom, rootAtom);
		}
	}
	
	

	/**
	 * Places the atoms in a linear chain. 
	 * Expects the first atom to be placed and places the next 
	 * atom according to initialBondVector.
	 * The rest of the chain is placed such that it is as linear as possible
	 * (in the overall result, the angles in the chain are set to 120 Deg.)
	 *
	 * @param   ac  The AtomContainer containing the chain atom to be placed
	 * @param   initialBondVector  The Vector indicating the direction of the first bond
	 */
	public void placeLinearChain(AtomContainer ac, Vector2d initialBondVector, double bondLength)
	{
		Vector2d bondVector = initialBondVector;
		Atom atom = null; 
		Point2d atomPoint = null;
		Point2d nextAtomPoint = null;
		Atom nextAtom = null;		
		Atom rootAtom = ac.getAtomAt(0);
		Point2d tempAtomPoint = null;
		for (int f = 0; f < ac.getAtomCount() - 1; f++)
		{
			atom = ac.getAtomAt(f);
			nextAtom = ac.getAtomAt(f + 1);
			atomPoint = new Point2d(atom.getPoint2D());
			bondVector.normalize();
			bondVector.scale(bondLength);	
			atomPoint.add(bondVector);
			nextAtom.setPoint2D(atomPoint);		
			nextAtom.flags[ISPLACED] = true;
			bondVector = getNextBondVector(nextAtom, atom, rootAtom);
		}
	}
	

	/**
	 * Returns the next bond vector needed for drawing an 
	 * extended linear chain of atoms. It assumes an angle
	 * of 120 deg for a nice chain layout and calculates the 
	 * two possible placments for the next atom. It returns the
	 * vector pointing farmost away from a given start atom.
	 *
	 * @param   atom  An atom for which the vector to the next atom to draw is calculated
	 * @param   previousAtom  The preceding atom for angle calculation
	 * @param   rootAtom  A root atom from which the next atom is to be farmost away
	 * @return  A vector pointing to the location of the next atom to draw   
	 */
	protected Vector2d getNextBondVector(Atom atom, Atom previousAtom, Atom rootAtom)
	{
		double angle = GeometryTools.getAngle(previousAtom.getX2D() - atom.getX2D(), previousAtom.getY2D() - atom.getY2D());
		double addAngle = Math.toRadians(120);
		angle += addAngle;
		Vector2d vec1 = new Vector2d(Math.cos(angle), Math.sin(angle));
		Point2d point1 = new Point2d(atom.getPoint2D());
		point1.add(vec1);
		double distance1 = point1.distance(rootAtom.getPoint2D());
		angle += addAngle;
		Vector2d vec2 = new Vector2d(Math.cos(angle), Math.sin(angle));
		Point2d point2 = new Point2d(atom.getPoint2D());
		point2.add(vec2);
		double distance2 = point2.distance(rootAtom.getPoint2D());
		if (distance2 > distance1) return vec2;
		return vec1;
		
		
		
	}

	/**
	 * Populates the corners of a polygon with atoms. Used to place
	 * atoms in a geometrically regular way around a ring center or
	 * another atom
	 *
	 * @param   startAtom  The first atom to draw
	 * @param   atomsToDraw  All the atoms to draw
	 * @param   startAngle  A start angle, giving the angle of the most clockwise atom which has already been placed
	 * @param   addAngle An angle to be added to startAngle for each atom from atomsToDraw 
	 * @param   direction  -1 or +1 to indicate whether addAngle has to be added or subtracted from startAngle
	 * @param   bondLength The standard bondLength  
	 */
	public void populatePolygonCorners(Vector atomsToDraw, Point2d rotationCenter, double startAngle, double addAngle, double radius)
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
			try
			{
				System.out.println("populatePolygonCorners->connectAtom: " + molecule.getAtomNumber(connectAtom));
			}
			catch(Exception e)
			{
			}
			if (connectAtom.getPoint2D() == null)
			{
				connectAtom.setPoint2D(new Point2d(newX, newY));				
				connectAtom.flags[ISPLACED] = true;
			}
		}
		
	}

	public Atom getMostClockwiseConnectedAtom(Atom atom)
	{
		Atom mostClockwiseAtom = null, tempAtom = null;
		Point2d mostClockwisePoint = null, atomPoint  = null, tempPoint = null;
		double mostClockwiseAngle = -1, angle = -1;
		atomPoint = atom.getPoint2D();
		if (atomPoint != null)
		{
			Bond[] bonds = molecule.getConnectedBonds(atom);
			for (int f = 0; f < bonds.length; f++)
			{
				tempAtom = bonds[f].getConnectedAtom(atom);
				tempPoint = tempAtom.getPoint2D();
				if (tempPoint != null)
				{
					angle = GeometryTools.getAngle(tempPoint.x - atomPoint.x, tempPoint.y - atomPoint.y);
					if ((angle > mostClockwiseAngle && angle - mostClockwiseAngle < Math.PI)||(angle < mostClockwiseAngle && mostClockwiseAngle - angle > Math.PI))
					{
						mostClockwiseAngle = angle;
						mostClockwiseAtom = tempAtom;
					}
				
				}
			}
		}
		return mostClockwiseAtom;
	}

	public Atom[] getLargestUnoccupiedAngleAtoms(Atom atom)
	{
		AtomContainer ac = new AtomContainer();
		Point2d atomPoint = atom.getPoint2D(), unoccupiedAngleCenter = null;
		Vector2d placedAtomsVector = null, unoccupiedAngleVector = null; 
		Atom[] atoms = new Atom[2];
		Atom atom1 = null, atom2 = null;
		if (atomPoint != null)
		{
			Bond[] bonds = molecule.getConnectedBonds(atom);
			for (int f = 0; f < bonds.length; f++)
			{
				atom1 = bonds[f].getConnectedAtom(atom);
				if (atom1.getPoint2D() != null)
				{
					ac.addAtom(atom1);
				}
			}
			if (ac.getAtomCount() < 2)
			{
				return null;
			} 
			placedAtomsVector = new Vector2d(ac.get2DCenter());
			molecule.addAtom(new Atom(new Element("S"), new Point2d(ac.get2DCenter())));
			unoccupiedAngleVector = new Vector2d(atomPoint);
			unoccupiedAngleVector.sub(placedAtomsVector);
			unoccupiedAngleCenter  = new Point2d(atomPoint);
			unoccupiedAngleCenter.add(unoccupiedAngleVector);
			/* Now we have a point in the middle of the
			 * largest unoccupied angle and can get the two atoms with 
			 * the smallest distance to it
			 */
			 molecule.addAtom(new Atom(new Element("O"), new Point2d(unoccupiedAngleCenter)));
			 for (int f = 0; f < ac.getAtomCount(); f++)
			 {
			 	if (atoms[0] == null)
				{
					atoms[0] = ac.getAtomAt(f);
				}
				else if (unoccupiedAngleCenter.distance(ac.getAtomAt(f).getPoint2D()) < unoccupiedAngleCenter.distance(atoms[0].getPoint2D()))
				{
					atoms[1] = atoms[0];
					atoms[0] = ac.getAtomAt(f);
				}
				else if (atoms[1] == null)
				{
					atoms[1] = ac.getAtomAt(f);				
				}
				else if (unoccupiedAngleCenter.distance(ac.getAtomAt(f).getPoint2D()) < unoccupiedAngleCenter.distance(atoms[1].getPoint2D()))
				{
					atoms[1] = ac.getAtomAt(f);
				}
			 }
		}
		
		return atoms;
	}

}
