/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.tools.*;
import org.openscience.cdk.graph.PathTools;
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
	public static boolean debug = false;
	
	/** The molecule to be laid out. To be assigned from outside */
	Molecule molecule = null;

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
	 */
	public void distributePartners(Atom atom, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, AtomContainer partners, double bondLength)
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

		/* get the two sharedAtom partners with the smallest distance to the new center */
		sortedAtoms = sharedAtoms.getAtoms();
		GeometryTools.sortBy2DDistance(sortedAtoms, distanceMeasure);
		Vector2d closestPoint1 = new Vector2d(sortedAtoms[0].getPoint2D());
		Vector2d closestPoint2 = new Vector2d(sortedAtoms[1].getPoint2D());			
		closestPoint1.sub(new Vector2d(atom.getPoint2D()));
		closestPoint2.sub(new Vector2d(atom.getPoint2D()));			
		occupiedAngle = closestPoint1.angle(occupiedDirection);
		occupiedAngle += closestPoint2.angle(occupiedDirection);
		
		
		double angle1 = GeometryTools.getAngle(sortedAtoms[0].getX2D() - atom.getX2D(), sortedAtoms[0].getY2D() - atom.getY2D());				
		double angle2 = GeometryTools.getAngle(sortedAtoms[1].getX2D() - atom.getX2D(), sortedAtoms[1].getY2D() - atom.getY2D());				
		double angle3 = GeometryTools.getAngle(distanceMeasure.x - atom.getX2D(), distanceMeasure.y - atom.getY2D());						
		if (debug)
		{
			try
			{
				System.out.println("distributePartners->sortedAtoms[0]: " + (molecule.getAtomNumber(sortedAtoms[0]) + 1));
				System.out.println("distributePartners->sortedAtoms[1]: " + (molecule.getAtomNumber(sortedAtoms[1]) + 1));		
				System.out.println("distributePartners->angle1: " + Math.toDegrees(angle1));
				System.out.println("distributePartners->angle2: " + Math.toDegrees(angle2));
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}
		}
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
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (partners.getAtomCount() + 1);
		if (debug)
		{
			try
			{
				System.out.println("distributePartners->startAtom: " + (molecule.getAtomNumber(startAtom) + 1));
				System.out.println("distributePartners->remainingAngle: " + Math.toDegrees(remainingAngle));
				System.out.println("distributePartners->addAngle: " + Math.toDegrees(addAngle));
				System.out.println("distributePartners-> partners.getAtomCount(): " + partners.getAtomCount());
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}

		}
		for (int f = 0; f < partners.getAtomCount(); f++)
		{
			atomsToDraw.addElement(partners.getAtomAt(f));
		}
		double radius = bondLength;
		double startAngle = GeometryTools.getAngle(startAtom.getX2D() - atom.getX2D(), startAtom.getY2D() - atom.getY2D());
		populatePolygonCorners(atomsToDraw, new Point2d(atom.getPoint2D()), startAngle, addAngle, radius);
		
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
			nextAtom.flags[CDKConstants.ISPLACED] = true;
			bondVector = getNextBondVector(nextAtom, atom, molecule.get2DCenter());
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
	 * @param   distanceMeasure  A point from which the next atom is to be farmost away
	 * @return  A vector pointing to the location of the next atom to draw   
	 */
	protected Vector2d getNextBondVector(Atom atom, Atom previousAtom, Point2d distanceMeasure)
	{
		double angle = GeometryTools.getAngle(previousAtom.getX2D() - atom.getX2D(), previousAtom.getY2D() - atom.getY2D());
		double addAngle = Math.toRadians(120);
		angle += addAngle;
		Vector2d vec1 = new Vector2d(Math.cos(angle), Math.sin(angle));
		Point2d point1 = new Point2d(atom.getPoint2D());
		point1.add(vec1);
		double distance1 = point1.distance(distanceMeasure);
		angle += addAngle;
		Vector2d vec2 = new Vector2d(Math.cos(angle), Math.sin(angle));
		Point2d point2 = new Point2d(atom.getPoint2D());
		point2.add(vec2);
		double distance2 = point2.distance(distanceMeasure);
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
		if (debug) System.out.println("populatePolygonCorners->startAngle: " + Math.toDegrees(angle));
		for (int i = 0; i < atomsToDraw.size(); i++)
		{
			connectAtom = (Atom)atomsToDraw.elementAt(i);
		    angle = angle + addAngle;
			if (angle >= 2 * Math.PI)
			{
				angle -= 2*Math.PI;
			}
		    if (debug)  System.out.println("populatePolygonCorners->angle: " +Math.toDegrees( angle));
		    x = Math.cos(angle) * radius;
		    y = Math.sin(angle) * radius;
		newX = x + rotationCenter.x;
		newY = y + rotationCenter.y;
		connectAtom.setPoint2D(new Point2d(newX, newY));				
			try
			{
				if (debug) System.out.println("populatePolygonCorners->connectAtom: " + (molecule.getAtomNumber(connectAtom) + 1)+ " placed at " + connectAtom.getPoint2D());
			}
			catch(Exception exc)
			{

			}
		
		connectAtom.flags[CDKConstants.ISPLACED] = true;
		}
	}

	/**
	 * Partition the bonding partners of a given atom into placed (coordinates assinged) and 
	 * not placed.
	 *
	 * @param   atom  The atom whose bonding partners are to be partitioned
	 * @param   unplacedPartners  A vector for the unplaced bonding partners to go in
	 * @param   placedPartners  A vector for the placed bonding partners to go in
	 */
	public void partitionPartners(Atom atom, AtomContainer unplacedPartners, AtomContainer placedPartners)
	{
		Atom[] atoms = molecule.getConnectedAtoms(atom);
		for (int i = 0; i < atoms.length; i++)
		{
			if (atoms[i].flags[CDKConstants.ISPLACED])
			{
				placedPartners.addAtom(atoms[i]);
			}
			else
			{
				unplacedPartners.addAtom(atoms[i]);
			}
		}
	}
	
	/**
	 * Search an aliphatic molecule for the longest chain. 
	 * This is the method to be used if there are no rings in 
	 * the molecule and you want to layout the longest chain in the molecule
	 * as a starting point of the structure diagram generation.
	 *
	 * @param   molecule  The molecule to be search for the longest unplaced chain
	 * @return  An AtomContainer holding the longest chain.   
	 */
	public AtomContainer getInitialLongestChain(Molecule molecule) throws org.openscience.cdk.exception.NoSuchAtomException
	{
		double [][] conMat = molecule.getConnectionMatrix();
		int [][] apsp  = PathTools.computeFloydAPSP(conMat);
		int maxPathLength = 0;
		int bestStartAtom = -1, bestEndAtom = -1;
		Atom atom = null, startAtom = null, endAtom =null;
		for (int f = 0; f < apsp.length; f++)
		{
			atom = molecule.getAtomAt(f);
			if (molecule.getBondCount(atom) == 1)
			{
				for (int g = 0; g < apsp.length; g++)
				{
					if (apsp[f][g] > maxPathLength)
					{
						maxPathLength = apsp[f][g];
						bestStartAtom = f;
						bestEndAtom = g;
					}
				}
			}
		}
		startAtom = molecule.getAtomAt(bestStartAtom);
		endAtom = molecule.getAtomAt(bestEndAtom);
		AtomContainer path = new AtomContainer();
		path.addAtom(startAtom);
		PathTools.depthFirstTargetSearch(molecule, startAtom, endAtom, path);
		return path;
	}
	



	/**
	 * Search a molecule for the longest unplaced, aliphatic chain in it.
	 * If an aliphatic chain encounters an unplaced ring atom, the ring atom is also 
	 * appended to allow for it to be laid out. This gives us a vector for attaching
	 * the unplaced ring later.
	 *
	 * @param   molecule  The molecule to be search for the longest unplaced chain
	 * @param   startAtom  A start atom from which the chain search starts
	 * @return  An AtomContainer holding the longest unplaced chain.   
	 * @exception   org.openscience.cdk.exception.NoSuchAtomException  If something goes wrong
	 */
	public AtomContainer getLongestUnplacedChain(Molecule molecule, Atom startAtom) throws org.openscience.cdk.exception.NoSuchAtomException
	{
		if (debug) System.out.println("Start of getLongestUnplacedChain.");
		//ConnectivityChecker cc = new ConnectivityChecker();
		int longest = 0;
		int longestPathLength = 0;
		AtomContainer[] pathes = new AtomContainer[molecule.getAtomCount()];
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			molecule.getAtomAt(f).flags[CDKConstants.VISITED] = false;
			pathes[f] = new AtomContainer();
			pathes[f].addAtom(startAtom);
			
		}
		Vector startSphere = new Vector();
		startSphere.addElement(startAtom);
		breadthFirstSearch(molecule, startSphere, pathes);
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{ 
			if (pathes[f].getAtomCount() > longestPathLength)
			{
				longest = f;
				longestPathLength = pathes[f].getAtomCount();
			}
		}
		return pathes[longest];
	}


	/**
	 * Performs a breadthFirstSearch in an AtomContainer starting with
	 * a particular sphere, which usually consists of one start atom, and 
	 * searches for the longest aliphatic chain which is yet unplaced.
	 * If the search encounters an unplaced ring atom, it is also 
	 * appended to the chain so that this last bond of the chain
	 * can also be laid out. This gives us the orientation for the 
	 * attachment of the ring system.
	 * 
	 *
	 * @param   ac  The AtomContainer to be searched
	 * @param   sphere  A sphere of atoms to start the search with
	 * @param   pathes  A vector of N pathes (N = no of heavy atoms). 
	 */
	public static void breadthFirstSearch(AtomContainer ac, Vector sphere, AtomContainer[] pathes) throws org.openscience.cdk.exception.NoSuchAtomException
	{
		Atom atom = null, nextAtom = null;
		int atomNr, nextAtomNr;
		AtomContainer path = null;
		Vector newSphere = new Vector();
		
		for (int f = 0; f < sphere.size(); f++)
		{
			atom = (Atom)sphere.elementAt(f);
			if (!atom.flags[CDKConstants.ISINRING])
			{
				atomNr = ac.getAtomNumber(atom);
				if (debug)
				{
					System.out.println("BreadthFirstSearch around atom " + (atomNr + 1));
				}

				Bond[] bonds = ac.getConnectedBonds(atom);
				for (int g = 0; g < bonds.length; g++)
				{
					nextAtom = bonds[g].getConnectedAtom(atom);
					if (!nextAtom.flags[CDKConstants.VISITED] && !nextAtom.flags[CDKConstants.ISPLACED])
					{
						if (debug)
						{
							System.out.println("BreadthFirstSearch is meeting new atom " + (ac.getAtomNumber(nextAtom) + 1));
						}

						nextAtomNr = ac.getAtomNumber(nextAtom);
						pathes[nextAtomNr] = (AtomContainer)pathes[atomNr].shallowCopy();
						pathes[nextAtomNr].addAtom(nextAtom);
						if (debug) System.out.println("Adding atom " + (nextAtomNr + 1) + " to path " + (nextAtomNr + 1));
						pathes[nextAtomNr].addBond(bonds[g]);
						if (ac.getBondCount(nextAtom) > 1) newSphere.addElement(nextAtom);
					}
				}
			}
		}
		if (newSphere.size() > 0)
		{
			for (int f = 0; f < newSphere.size(); f++)
			{
				((Atom)newSphere.elementAt(f)).flags[CDKConstants.VISITED] = true;			
			}
			breadthFirstSearch(ac, newSphere, pathes);
		}
	}

	/**
	 * Returns a string with the numbers of all placed atoms in an AtomContainer
	 *
	 * @param   ac  The AtomContainer for which the placed atoms are to be listed
	 * @return  A string with the numbers of all placed atoms in an AtomContainer   
	 */
	public String listPlaced(AtomContainer ac)
	{
		String s = "Placed: ";
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			if (ac.getAtomAt(f).flags[CDKConstants.ISPLACED]) 
			{
				s += (f + 1) + "+ ";
			}
			else
			{
				s += (f + 1) + "- ";
			}
		}
		return s;
	}

	 /**
	  * Returns a string with the numbers of all atoms in an AtomContainer
	  * relative to a given molecule. I.e. the number the is listesd is the position
	  * of each atom in the molecule.
	  *
	  * @param   ac  The AtomContainer for which the placed atoms are to be listed
	  * @return  A string with the numbers of all placed atoms in an AtomContainer   
	  * @exception   Exception thrown if the number cannot be determined
	  */
	public String listNumbers(Molecule mol, AtomContainer ac) throws java.lang.Exception
	{
		String s = "Numbers: ";
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			s += (mol.getAtomNumber(ac.getAtomAt(f)) + 1) +  " ";
		}
		return s;
	}

	/**
	 * Returns a string with the numbers of all atoms in a Vector
	 * relative to a given molecule. I.e. the number the is listesd is the position
	 * of each atom in the molecule.
	 *
	 * @param   ac  The Vector for which the placed atoms are to be listed
	 * @return  A string with the numbers of all placed atoms in an AtomContainer   
	 * @exception   Exception thrown if the number cannot be determined
	 */
	public String listNumbers(Molecule mol, Vector ac) throws java.lang.Exception
	{
	String s = "Numbers: ";
	for (int f = 0; f < ac.size(); f++)
	{
		s += (mol.getAtomNumber((Atom)ac.elementAt(f)) + 1) +  " ";
	}
	return s;
	}


	/**
	 * True is all the atoms in the given AtomContainer have been placed
	 *
	 * @param   ac  The AtomContainer to be searched
	 * @return  True is all the atoms in the given AtomContainer have been placed   
	 */
	public static boolean allPlaced(AtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			if (!ac.getAtomAt(f).flags[CDKConstants.ISPLACED]) return false;
		}
		return true;
	}

	/**
	 * Marks all the atoms in the given AtomContainer as not placed
	 *
	 * @param   ac  The AtomContainer whose atoms are to be marked
	 */
	public static void markNotPlaced(AtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			ac.getAtomAt(f).flags[CDKConstants.ISPLACED] = false;
		}

	}

	/**
	 * Marks all the atoms in the given AtomContainer as placed
	 *
	 * @param   ac  The AtomContainer whose atoms are to be marked
	 */

	public static void markPlaced(AtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			ac.getAtomAt(f).flags[CDKConstants.ISPLACED] = true;
		}
	}


	/**
	 * Get all the placed atoms in an AtomContainer
	 *
	 * @param   ac  The AtomContainer to be searched for placed atoms
	 * @return  An AtomContainer containing all the placed atoms
	 */
	public  AtomContainer getPlacedAtoms(AtomContainer ac)
	{
		AtomContainer ret = new AtomContainer();
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			if (ac.getAtomAt(f).flags[CDKConstants.ISPLACED])
			{
				ret.addAtom(ac.getAtomAt(f));
			}
		}
		return ret;
	}

}
