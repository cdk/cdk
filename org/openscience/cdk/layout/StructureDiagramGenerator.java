/* StructureDiagramGenerator.java
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


/* 
 * Generates 2D coordinates for a molecule for which only connectivity is known
 * or the coordinates have been discarded for some reason.
 * Usage: Create an instance of this class, thereby assigning a molecule, 
 * call generateCoordinates() and get your molecule back.
 */

public class StructureDiagramGenerator 
{

	Molecule molecule;
	RingSet sssr;
	double bondLength = 1;
	Vector2d firstBondVector;
	SSSRFinder sssrf = new SSSRFinder();

	public static boolean debug = true;
	static int SPIRO = 1;
	static int FUSED = 2;
	static int BRIDGED = 3;		

	/**
	 * The empty constructor
	 */
	public StructureDiagramGenerator()
	{

	}


	/**
	 * Creates an instance of this class while assigning a molecule to be layed out
	 *
	 * @param   molecule  The molecule to be layed out.
	 */
	public StructureDiagramGenerator(Molecule molecule)
	{
		this();
		setMolecule(molecule, false);		
	}


	/**
	 * Assings a molecule to be layed out. Call 
	 * generateCoordinates() to do the actual layout.
	 *
	 * @param   molecule  the molecule for which coordinates are to be generated.
	 * @param   clone  Should the whole process be performed with a cloned copy?
	 */
	public void setMolecule(Molecule molecule, boolean clone)
	{
		if (clone)
		{
			this.molecule = (Molecule)molecule.clone();
		}
		else
		{
			this.molecule = molecule;
		}
	}


	/**
	 /**
	  * Assings a molecule to be layed out. 
	  * Call generateCoordinates() to do the actual layout.
	  *
	  * @param   molecule  the molecule for which coordinates are to be generated.
	 */
	public void setMolecule(Molecule molecule)
	{
		setMolecule(molecule, true);
	}




	/**
	 * Returns the molecule, usually used after a call of 
	 * generateCoordinates()
	 *
	 * @return  The molecule with new coordinates (if generateCoordinates() had been called)   
	 */
	public Molecule getMolecule()
	{
		return molecule;
	}

	/**
	 * The main method of this StructurDiagramGenerator.
	 * Assign a molecule to the StructurDiagramGenerator, call 
	 * the generateCoordinates() method and get your molecule back.
	 */
	public void generateCoordinates(Vector2d firstBondVector)
	{
		handleRings(firstBondVector);
		handleAliphatics();
		fixRest();
		System.out.println(getMolecule());
	}

	/**
	 * The main method of this StructurDiagramGenerator.
	 * Assign a molecule to the StructurDiagramGenerator, call 
	 * the generateCoordinates() method and get your molecule back.
	 */
	public void generateCoordinates()
	{
		generateCoordinates(new Vector2d(0, 1));
	}

	/**
	 * Does a layout of all the non-ring parts of the molecule
	 */
	private void handleAliphatics()
	{

	}


	/**
	 * Does a layout of all the rings in the molecule
	 */
	private void handleRings(Vector2d firstBondVector)
	{
		RingSet rs;
		AtomContainer sharedAtoms;
		Bond bond;
		Vector2d ringCenterVector;
		Point2d ringCenter;
		/*
		 * Get the smallest set of smallest rings on this molecule
		 */
		sssr = sssrf.findSSSR(molecule);
		if (debug) System.out.println("StructureDiagramGenerator -> handleRings -> sssr.size(): " + sssr.size());
		/*
		 * Partition the smallest set of smallest rings into disconnected ring system.
		 * The RingPartioner returns a Vector containing RingSets. Each of the RingSets contains
		 * rings that are connected to each other either as bridged ringsystems, fused rings or 
		 * via spiro connections. 
		 */
		Vector ringSystems = RingPartitioner.partitionRings(sssr);
		if (debug) System.out.println("StructureDiagramGenerator -> handleRings -> ringSystems.size(): " + ringSystems.size());
		/*
		 * Do an independent layout of the each of the RingSets. They will be translated and rotated 
		 * to their final position later.
		 */
		for (int f = 0; f < 1; f++)
		{
			rs = (RingSet)ringSystems.elementAt(f); /* Get the f-th RingSet */
			Ring ring = rs.getMostComplexRing(); /* Get the most complex ring in this RingSet */
			System.out.println("Most complex ring: " + ring.toString(molecule));
			sharedAtoms = placeFirstBondOfFirstRing(ring,firstBondVector); /* Place the most complex ring at the origin of the coordinate system */
			/* 
			 * Call the method which lays out the new ring.
			 */
    		ringCenter = sharedAtoms.get2DCenter();
			ringCenterVector = getRingCenterOfFirstRing(ring, firstBondVector); 
			ringCenter.add(ringCenterVector);
			molecule.addAtom(new Atom(new Element("N"), ringCenter));
//			RingPlacer.placeRing(ring, sharedAtoms, sharedAtoms.get2DCenter(), ringCenterVector, bondLength);
			/* 
			 * Mark the ring as placed
			 */
//			ring.flags[RingPlacer.ISPLACED] = true;
			/* 
			 * Place all other rings in this ringsystem.
			 */

//			placeConnectedRings(rs, ring);
		}

	}


	/**
	 * Places the first bond of the first ring such that one atom 
	 * is at (0,0) and the other one at the position given by bondVector
	 *
	 * @param   ring  The ring for which the first bond is to be placed
	 * @param   bondVector  A 2D vector to point to the position of the second bond atom
	 */
	private AtomContainer placeFirstBondOfFirstRing(Ring ring, Vector2d bondVector)
	{
		AtomContainer sharedAtoms = null;
		try
		{
			bondVector.scale(bondLength/bondVector.length());

			Atom atom;
			Bond bond = ring.getBondAt(0);
			Point2d point = new Point2d(0, 0);
			atom = bond.getAtomAt(0);
			System.out.println("Atom 1 of first Bond: " + molecule.getAtomNumber(atom));
			atom.setPoint2D(point);
			point = new Point2d(0, 0);
			atom = bond.getAtomAt(1);
			System.out.println("Atom 2 of first Bond: " + molecule.getAtomNumber(atom));		
			bondVector.scale(bondLength);
			point.add(bondVector);
			atom.setPoint2D(point);
			/* 
			 * The new ring is layed out relativ to some shared atoms that have already been 
			 * placed. Usually this is another ring, that has already been draw and to which the new 
			 * ring is somehow connected, or some other system of atoms in an aliphatic chain.
			 * In this case, it's the first bond that we layout by hand.
			 */
			sharedAtoms = new AtomContainer();
			sharedAtoms.addBond(bond);
			sharedAtoms.addAtom(bond.getAtomAt(0));
			sharedAtoms.addAtom(bond.getAtomAt(1));			
		}
		catch(Exception exc)
		{
		
		}
		return sharedAtoms;		
	}


	/**
	 * Calculated the center for the first ring so that it can
	 * layed out. Only then, all other rings can be assigned
	 * coordinates relative to it. 
	 *
	 * @param   ring  The ring for which the center is to be calculated
	 * @return  A Vector2d pointing to the new ringcenter   
	 */
	private Vector2d getRingCenterOfFirstRing(Ring ring, Vector2d bondVector)
	{
		int size = ring.getAtomCount();
		double angle = Math.PI / ring.getAtomCount();
		double ringCenterVectorSize = bondLength / (2 * Math.tan(angle));
		/* Define a vector for the y axis */
		Vector2d xAxis = new Vector2d(1, 0);
		/* get the angle between the x axis and the bond vector */
		double rotangle = GeometryTools.getAngle(bondVector.x, bondVector.y);
		double atanrotangle = Math.atan(bondVector.y/bondVector.x);
		System.out.println("Vector angle between the x axis and the bond vector: " + rotangle);
		System.out.println("atan angle between the x axis and the bond vector: " + atanrotangle);		
		System.out.println("new method angle between the x axis and the bond vector: " + GeometryTools.getAngle(bondVector.x, bondVector.y));				
		/* Add 90 Degrees to this angle, this is supposed to be the new ringcenter vector */
		rotangle += Math.PI / 2;
		return new Vector2d(Math.cos(rotangle) * ringCenterVectorSize, Math.sin(rotangle) * ringCenterVectorSize);
	}


	/**
	 * Layout all rings in the given RingSet that are connected to a given Ring
	 *
	 * @param   rs  The RingSet to be searched for rings connected to Ring
	 * @param   ring  The Ring for which all connected rings in RingSet are to be layed out. 
	 */
	private void placeConnectedRings(RingSet rs, Ring ring)
	{
		Vector connectedRings = rs.getConnectedRings(ring);
		Ring connectedRing;
		AtomContainer sharedAtoms;
		int sac;
		Point2d oldRingCenter, newRingCenter, sharedAtomsCenter, tempPoint;
		Vector2d tempVector, oldRingCenterVector, newRingCenterVector;
		Bond bond;

		System.out.println(rs.reportRingList(molecule)); 
		for (int i = 0; i < connectedRings.size(); i++)
		{
			connectedRing = (Ring)connectedRings.elementAt(i);

			if (!connectedRing.flags[RingPlacer.ISPLACED])
			{

				System.out.println(ring.toString(molecule));
				System.out.println(connectedRing.toString(molecule));				
				sharedAtoms = ring.getIntersection(connectedRing);
				if (debug)
				{
					System.out.println("**** start of shared atoms");
					System.out.println(sharedAtoms);
					System.out.println("**** end of shared atoms");
				}
				if (sharedAtoms.getAtomCount() > 0)
				{
					sharedAtomsCenter = sharedAtoms.get2DCenter();
					if (debug) molecule.addAtom(new Atom(new Element("B"), new Point2d(sharedAtomsCenter)));
					oldRingCenter = ring.get2DCenter();
					if (debug) molecule.addAtom(new Atom(new Element("O"), new Point2d(oldRingCenter)));
					tempVector = (new Vector2d(sharedAtomsCenter));
					newRingCenterVector = new Vector2d(tempVector);
					newRingCenterVector.sub(new Vector2d(oldRingCenter));
					oldRingCenterVector = new Vector2d(newRingCenterVector);
					if (debug)
					{
						System.out.println("placeConnectedRing -> tempVector: " + tempVector + ", tempVector.length: " + tempVector.length()); System.out.println("placeConnectedRing -> tempVector: " + tempVector + ", tempVector.length: " + tempVector.length());
						System.out.println("placeConnectedRing -> bondCenter: " + sharedAtomsCenter);
						System.out.println("placeConnectedRing -> oldRingCenterVector.length(): " + oldRingCenterVector.length());
					}
					if (debug)
					{
						System.out.println("placeConnectedRing -> newRingCenterVector.length(): " + newRingCenterVector.length());					
					}
					tempPoint = new Point2d(sharedAtomsCenter);
					tempPoint.add(newRingCenterVector);
					if (debug) molecule.addAtom(new Atom(new Element("N"), tempPoint));
					RingPlacer.placeRing(connectedRing, sharedAtoms, sharedAtomsCenter, newRingCenterVector, bondLength);RingPlacer.placeRing(connectedRing, sharedAtoms, sharedAtomsCenter, newRingCenterVector, bondLength);
					connectedRing.flags[RingPlacer.ISPLACED] = true;
					placeConnectedRings(rs, connectedRing);
				}
			}
		}

	}


	/**
	 * This method will go as soon as the rest works. 
	 * It just assignes Point2d's of position (0,0) so that
	 * the molecule can be drawn.
	 */
	private void fixRest()
	{
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			if (molecule.getAtomAt(f).getPoint2D() == null)
			{
				molecule.getAtomAt(f).setPoint2D(new Point2d(0,0));
			}
		}
	}



	/**
	 * Initializes all rings in RingSet rs as not placed
	 *
	 * @param   rs  The RingSet to be initialized
	 */
	private void markNotPlaced(RingSet rs)
	{
		for (int f = 0; f < rs.size(); f++)
		{
			((Ring)rs.elementAt(f)).flags[RingPlacer.ISPLACED] = false;
		}
	}


}
