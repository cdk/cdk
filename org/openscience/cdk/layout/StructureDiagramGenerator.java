/* StructureDiagramGenerator.java
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
 *  
 */
 
package org.openscience.cdk.layout;

import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.tools.*;
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

public class StructureDiagramGenerator implements CDKConstants
{

	Molecule molecule;
	RingSet sssr;
	double bondLength = 1.5;
	Vector2d firstBondVector;
	SSSRFinder sssrf = new SSSRFinder();
	RingPlacer ringPlacer = new RingPlacer();
	AtomPlacer atomPlacer = new AtomPlacer();	
	Vector ringSystems = null;
	public static boolean debug = false;

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
		Atom atom = null;
		if (clone)
		{
			this.molecule = (Molecule)molecule.clone();
		}
		else
		{
			this.molecule = molecule;
		}
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			atom = molecule.getAtomAt(f);
			atom.setPoint2D(null);
			atom.flags[ISPLACED] = false;
			atom.flags[VISITED] = false;
			atom.flags[ISINRING] = false;
			atom.flags[ISALIPHATIC] = false;
		}
		atomPlacer.setMolecule(this.molecule);
		ringPlacer.setMolecule(this.molecule);
		ringPlacer.setAtomPlacer(this.atomPlacer);
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
	public void generateCoordinates(Vector2d firstBondVector) throws java.lang.Exception
	{
		/* compute the minimum number of rings as 
		   given by Frerejacque, Bull. Soc. Chim. Fr., 5, 1008 (1939) */
		int nrOfEdges = molecule.getBondCount();
		Vector2d ringSystemVector = null, newRingSystemVector = null;
		this.firstBondVector = firstBondVector;

		double angle;
		
		int expectedRingCount = nrOfEdges - molecule.getAtomCount() + 1;

		if (expectedRingCount > 0)
		{
		
			/*
			 * Get the smallest set of smallest rings on this molecule
			 */
			sssr = sssrf.findSSSR(molecule);
			if (sssr.size() < 1)
			{
				return;
			}
			/* Mark all the atoms from the ring system as "ISINRING" */
			markRingAtoms(sssr);
			/* Give a handle of our molecule to the ringPlacer */
			ringPlacer.setMolecule(molecule);
			/*
			 * Partition the smallest set of smallest rings into disconnected ring system.
			 * The RingPartioner returns a Vector containing RingSets. Each of the RingSets contains
			 * rings that are connected to each other either as bridged ringsystems, fused rings or 
			 * via spiro connections. 
			 */
			ringSystems = RingPartitioner.partitionRings(sssr);
			
			/* Do the layout for the first connected ring system ... */
			layoutRingSet(firstBondVector, (RingSet)ringSystems.elementAt(0));
			/* and to the placement of all the directly connected atoms of this ringsystem */
			ringPlacer.placeRingSubstituents((RingSet)ringSystems.elementAt(0), bondLength);
		}
		else
		{
			/* We are here because there are no rings in the molecule
			 * so we get the longest chain in the molecule and placed in 
			 * on a horizontal axis
			 */
			AtomContainer longestChain = atomPlacer.getInitialLongestChain(molecule);
			longestChain.getAtomAt(0).setPoint2D(new Point2d(0,0));
			longestChain.getAtomAt(0).flags[ISPLACED] = true;
			/* place the first bond such that the whole chain will be horizontally
			 * alligned on the x axis
			 */
			angle = Math.toRadians(-30);
			atomPlacer.placeLinearChain(longestChain, new Vector2d(Math.cos(angle), Math.sin(angle)), bondLength);
		}

		/* Now, do the layout of the rest of the molecule */
		do
		{
			/* do layout for all aliphatic parts of the molecule which are 
			 * connected to the parts which have already been laid out.
			 */
			handleAliphatics();
			/* do layout for the next ring aliphatic parts of the molecule which are 
			 * connected to the parts which have already been laid out.
			 */
			layoutNextRingSystem();
		}while(!atomPlacer.allPlaced(molecule));		
		fixRest();
	}

	/**
	 * The main method of this StructurDiagramGenerator.
	 * Assign a molecule to the StructurDiagramGenerator, call 
	 * the generateCoordinates() method and get your molecule back.
	 */
	public void generateCoordinates() throws java.lang.Exception
	{
		generateCoordinates(new Vector2d(0, 1));
	}

	/**
	 * Does a layout of all the rings in a given connected RingSet
	 *
	 * @param   firstBondVector  A vector giving the placement for the first bond
	 * @param   rs  The connected RingSet for which the layout is to be done
	 */
	private void layoutRingSet(Vector2d firstBondVector, RingSet rs)
	{
		AtomContainer sharedAtoms;
		Bond bond;
		Vector2d ringCenterVector;
		Point2d ringCenter;
		int thisRing;
		Ring ring = rs.getMostComplexRing(); /* Get the most complex ring in this RingSet */
		sharedAtoms = placeFirstBond(ring.getBondAt(0),firstBondVector); /* Place the most complex ring at the origin of the coordinate system */
		/* 
		 * Call the method which lays out the new ring.
		 */
		ringCenterVector = ringPlacer.getRingCenterOfFirstRing(ring, firstBondVector, bondLength); 
		ringPlacer.placeRing(ring, sharedAtoms, sharedAtoms.get2DCenter(), ringCenterVector, bondLength);
		/* 
		 * Mark the ring as placed
		 */
		ring.flags[ISPLACED] = true;
		/* 
		 * Place all other rings in this ringsystem.
		 */
		thisRing = 0;
		do
		{
			if (ring.flags[ISPLACED])
			{
				ringPlacer.placeConnectedRings(rs, ring, ringPlacer.FUSED, bondLength);
				ringPlacer.placeConnectedRings(rs, ring, ringPlacer.BRIDGED, bondLength);
				ringPlacer.placeConnectedRings(rs, ring, ringPlacer.SPIRO, bondLength);
			}
			thisRing ++;
			if (thisRing == rs.size()) thisRing = 0;
			ring = (Ring)rs.elementAt(thisRing);
		}while(!allPlaced(rs));
	}

	
	

	/**
	 * Does a layout of all aliphatic parts connected to 
	 * the parts of the molecule that have already been laid out.
	 */
	private void handleAliphatics() throws org.openscience.cdk.exception.NoSuchAtomException
	{
		//System.out.println("Longest Chain has " + longestChain.getAtomCount() + " atoms.");
		Atom atom = null;
		double xDiff, yDiff;
		Atom[] atoms = null;
		Bond[] bonds = null;
		AtomContainer unplacedAtoms = null;
		AtomContainer placedAtoms = null;
		AtomContainer longestUnplacedChain = null;
		
		double startAngle = 0, addAngle = 0, unoccupiedAngle = 0;

		Vector2d direction = null;
		Vector2d startVector = null;
		boolean done;
		do
		{
			done = false;
			atom = getNextAtomWithAliphaticUnplacedNeigbors();
			if (atom != null)
			{
				unplacedAtoms = getUnplacedAtoms(atom);
				placedAtoms = getPlacedAtoms(atom);

				longestUnplacedChain = atomPlacer.getLongestUnplacedChain(molecule, atom);
		
				if (longestUnplacedChain.getAtomCount() > 1)
				{
					
					if (placedAtoms.getAtomCount() > 1)
					{
						atomPlacer.distributePartners(atom, placedAtoms, placedAtoms.get2DCenter(), unplacedAtoms, bondLength);
						direction = new Vector2d(longestUnplacedChain.getAtomAt(1).getPoint2D());
						startVector = new Vector2d(atom.getPoint2D());
						direction.sub(startVector);
					}
					else
					{
						direction = atomPlacer.getNextBondVector(atom, placedAtoms.getAtomAt(0), molecule.get2DCenter());
					}
					
					for (int f = 1; f < longestUnplacedChain.getAtomCount(); f++)
					{
						longestUnplacedChain.getAtomAt(f).flags[ISPLACED] = false;
					}

					atomPlacer.placeLinearChain(longestUnplacedChain, direction, bondLength);
				}
				else
				{
					done = true;
				}
			}
			else
			{
				done = true;
			}
		}while(!done);
	}
	



	/**
	 * Does the layout for the next RingSystem that is connected to 
	 * those parts of the molecule that have already been laid out.
	 */
	private void layoutNextRingSystem()
	{
		Atom vectorAtom1 = null, vectorAtom2 = null;
		Point2d oldPoint1 = null, newPoint1 = null, oldPoint2 = null, newPoint2 = null;
		RingSet nextRingSystem = null;
		AtomContainer ringSystem = null;		
		Bond nextRingAttachmentBond = null;	
		double angle, angle1, angle2;
	
		resetUnplacedRings();
		AtomContainer tempAc = atomPlacer.getPlacedAtoms(molecule);
		nextRingAttachmentBond = getNextBondWithUnplacedRingAtom();
		if (nextRingAttachmentBond != null)
		{
			vectorAtom2 = getRingAtom(nextRingAttachmentBond);
			if (nextRingAttachmentBond.getAtomAt(0) == vectorAtom1)
			{
				vectorAtom1 = nextRingAttachmentBond.getAtomAt(1);
			}						
			else
			{
				vectorAtom1 = nextRingAttachmentBond.getAtomAt(0);
			}						
			oldPoint2 = vectorAtom2.getPoint2D();
			oldPoint1 = vectorAtom1.getPoint2D();				

			angle1 = GeometryTools.getAngle(oldPoint2.x - oldPoint1.x, oldPoint2.y - oldPoint1.y);								
			nextRingSystem = getRingSystemOfAtom(ringSystems, vectorAtom2);
			ringSystem = new AtomContainer();
			ringSystem.add(nextRingSystem.getRingSetInAtomContainer());

			/* Do the layout of the next ring system */
			layoutRingSet(firstBondVector, nextRingSystem);
			/* Place all the substituents of next ring system */
			atomPlacer.markNotPlaced(tempAc);				
			ringSystem.add(ringPlacer.placeRingSubstituents(nextRingSystem, bondLength));
			atomPlacer.markPlaced(tempAc);				

			newPoint2 = vectorAtom2.getPoint2D();
			newPoint1 = vectorAtom1.getPoint2D();				

			angle2 = GeometryTools.getAngle(newPoint2.x - newPoint1.x, newPoint2.y - newPoint2.y);				
			Vector2d transVec = new Vector2d(oldPoint1);
			transVec.sub(new Vector2d(newPoint1));

			GeometryTools.translate2D(ringSystem, transVec);				
			GeometryTools.rotate(ringSystem, oldPoint1, angle1 - angle2);
			vectorAtom1.setPoint2D(oldPoint1);				
		}
	}

	/**
	 * Returns an AtomContainer with all the unplaced atoms connected to a given atom
	 *
	 * @param   atom  The Atom whose unplaced bonding partners are to be returned
	 * @return an AtomContainer with all the unplaced atoms connected to a given atom
	 */
	private AtomContainer getUnplacedAtoms(Atom atom)
	{
		AtomContainer unplacedAtoms = new AtomContainer();
		Bond[] bonds = molecule.getConnectedBonds(atom);
		Atom connectedAtom = null;
		for (int f = 0; f < bonds.length; f++)
		{
			connectedAtom = bonds[f].getConnectedAtom(atom);
			if (!connectedAtom.flags[ISPLACED])
			{
				unplacedAtoms.addAtom(connectedAtom);
			}
		}
		return unplacedAtoms;
	}
	

	/**
	 * Returns an AtomContainer with all the placed atoms connected to a given atom
	 *
	 * @param   atom  The Atom whose placed bonding partners are to be returned
	 * @return an AtomContainer with all the placed atoms connected to a given atom
	 */
	private AtomContainer getPlacedAtoms(Atom atom)
	{
		AtomContainer placedAtoms = new AtomContainer();
		Bond[] bonds = molecule.getConnectedBonds(atom);
		Atom connectedAtom = null;
		for (int f = 0; f < bonds.length; f++)
		{
			connectedAtom = bonds[f].getConnectedAtom(atom);
			if (connectedAtom.flags[ISPLACED])
			{
				placedAtoms.addAtom(connectedAtom);
			}
		}
		return placedAtoms;
	}



	/**
	 * Returns the next atom with unplaced aliphatic neighbors
	 *
	 * @return the next atom with unplaced aliphatic neighbors    
	 */
	private Atom getNextAtomWithAliphaticUnplacedNeigbors()
	{
		Bond bond = null; 
		for (int f = 0; f < molecule.getBondCount(); f++)
		{
			bond = molecule.getBondAt(f);
			if (bond.getAtomAt(1).flags[ISPLACED] && !bond.getAtomAt(0).flags[ISPLACED] )
			{
				return bond.getAtomAt(1);
			}
			
			if (bond.getAtomAt(0).flags[ISPLACED] &&  !bond.getAtomAt(1).flags[ISPLACED]  )
			{
				return bond.getAtomAt(0);
			}
		}
		return null;
	}



	/**
	 * Returns the next bond with an unplaced ring atom
	 *
	 * @return the next bond with an unplaced ring atom    
	 */
	private Bond getNextBondWithUnplacedRingAtom()
	{
		Bond bond = null; 
		for (int f = 0; f < molecule.getBondCount(); f++)
		{
			bond = molecule.getBondAt(f);
			if (bond.getAtomAt(1).flags[ISPLACED] && !bond.getAtomAt(0).flags[ISPLACED]  && bond.getAtomAt(0).flags[ISINRING])
			{
				return bond;
			}
			
			if (bond.getAtomAt(0).flags[ISPLACED] &&  !bond.getAtomAt(1).flags[ISPLACED]  && bond.getAtomAt(1).flags[ISINRING])
			{
				return bond;
			}
		}
		return null;
	}


	/**
	 * Places the first bond of the first ring such that one atom 
	 * is at (0,0) and the other one at the position given by bondVector
	 *
	 * @param   ring  The ring for which the first bond is to be placed
	 * @param   bondVector  A 2D vector to point to the position of the second bond atom
	 */
	private AtomContainer placeFirstBond(Bond bond, Vector2d bondVector)
	{
		AtomContainer sharedAtoms = null;
		try
		{
			bondVector.normalize();
			if (debug) System.out.println("placeFirstBondOfFirstRing->bondVector.length():" +  bondVector.length());
			bondVector.scale(bondLength);
			if (debug) System.out.println("placeFirstBondOfFirstRing->bondVector.length() after scaling:" +  bondVector.length());
			Atom atom;
			Point2d point = new Point2d(0, 0);
			atom = bond.getAtomAt(0);
			if (debug) System.out.println("Atom 1 of first Bond: " + molecule.getAtomNumber(atom));
			atom.setPoint2D(point);
			atom.flags[ISPLACED] = true;
			point = new Point2d(0, 0);
			atom = bond.getAtomAt(1);
			if (debug) System.out.println("Atom 2 of first Bond: " + molecule.getAtomNumber(atom));		
			point.add(bondVector);
			atom.setPoint2D(point);
			atom.flags[ISPLACED] = true;
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
			((Ring)rs.elementAt(f)).flags[ISPLACED] = false;
		}
	}

	/**
	 * Are all rings in the Vector placed?
	 *
	 * @param   rings  The Vector to be checked
	 */
	private boolean allPlaced(Vector rings)
	{
		for (int f = 0; f < rings.size(); f++)
		{
			if (!((Ring)rings.elementAt(f)).flags[ISPLACED])
			{
				if (debug) System.out.println("allPlaced->Ring " + f + " not placed");			
				return false;
			}				
		}
		
		return true;
	}


	/**
	 * Mark all atoms in the molecule as being part of a ring
	 *
	 * @param   rings  The Vector to be checked
	 */
	private void markRingAtoms(Vector rings)
	{
		Ring ring = null;
		for (int i = 0; i < rings.size(); i++)
		{
			ring = (Ring)rings.elementAt(i);
			for (int j = 0; j < ring.getAtomCount(); j++)
			{
				ring.getAtomAt(j).flags[ISINRING] = true;
			}
		}
	}



	/**
	 * Get the unplaced ring atom in this bond
	 *
	 * @param   bond  the bond to be search for the unplaced ring atom
	 * @return  the unplaced ring atom in this bond   
	 */
	private Atom getRingAtom(Bond bond)
	{
		if (bond.getAtomAt(0).flags[ISINRING] && !bond.getAtomAt(0).flags[ISPLACED]) return bond.getAtomAt(0);
		if (bond.getAtomAt(1).flags[ISINRING] && !bond.getAtomAt(1).flags[ISPLACED]) return bond.getAtomAt(1);
		return null;
	}
	

	/**
	 * Get the ring system of which the given atom is part of
	 *
	 * @param   ringSystems  A Vector of ring systems to be searched
	 * @param   ringAtom  The ring atom to be search in the ring system.
	 * @return the ring system of which the given atom is part of     
	 */
	private RingSet getRingSystemOfAtom(Vector ringSystems, Atom ringAtom)
	{
		RingSet ringSet = null;
		for (int f = 0; f < ringSystems.size(); f++)
		{
			ringSet = (RingSet)ringSystems.elementAt(f);
			if (ringSet.contains(ringAtom))
			{
				return ringSet;
			}
		}
		return null;
	}
	

	/**
	 * Set all the atoms in unplaced rings to be unplaced
	 *
	 */
	private void resetUnplacedRings()
	{
		Ring ring = null;
		if (sssr == null) return;
		for (int f = 0; f < sssr.size(); f++)
		{
			ring = (Ring)sssr.elementAt(f);
			if (!ring.flags[ISPLACED])
			{
				for (int g = 0; g < ring.getAtomCount(); g++)
				{
					ring.getAtomAt(g).flags[ISPLACED] = false;
				}
			}
		}
	}


}



