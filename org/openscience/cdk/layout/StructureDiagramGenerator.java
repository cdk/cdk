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

	static int FUSED = 0;
	static int BRIDGED = 1;		
	static int SPIRO = 2;
	
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
		atomPlacer.setMolecule(this.molecule);
		ringPlacer.setMolecule(this.molecule);
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
		Bond nextRingAttachmentBond = null;
		Vector2d ringSystemVector = null, newRingSystemVector = null;
		
		Atom ringAtom = null;
		RingSet nextRingSystem = null;
		int expectedRingCount = nrOfEdges - molecule.getAtomCount() + 1;
		// if there are rings, get them...
		if (debug) System.out.println("StructureDiagramGenerator->: " + expectedRingCount + " rings expected");
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
			markRingAtoms(sssr);
			ringPlacer.setMolecule(molecule);
			if (debug) System.out.println("StructureDiagramGenerator -> handleRings -> sssr.size(): " + sssr.size());
			/*
			 * Partition the smallest set of smallest rings into disconnected ring system.
			 * The RingPartioner returns a Vector containing RingSets. Each of the RingSets contains
			 * rings that are connected to each other either as bridged ringsystems, fused rings or 
			 * via spiro connections. 
			 */
			ringSystems = RingPartitioner.partitionRings(sssr);
			if (debug) System.out.println("StructureDiagramGenerator -> handleRings -> ringSystems.size(): " + ringSystems.size());
			
			handleRings(firstBondVector, (RingSet)ringSystems.elementAt(0));
			placeRingSubstituents((RingSet)ringSystems.elementAt(0));
			
		}
		else
		{
			AtomContainer longestChain = atomPlacer.getInitialLongestChain(molecule);
			longestChain.getAtomAt(0).setPoint2D(new Point2d(0,0));
			longestChain.getAtomAt(0).flags[ISPLACED] = true;
			/* place the first bond such that the whole chain will be vertically 
			 * alligned on the x axis
			 */
			double angle = Math.toRadians(-30);
			atomPlacer.placeLinearChain(longestChain, new Vector2d(Math.cos(angle), Math.sin(angle)), bondLength);
		}
		do
		{
			handleAliphatics();
			resetUnplacedRings();
			nextRingAttachmentBond = getNextBondWithUnplacedRingAtom();

			if (nextRingAttachmentBond != null)
			{
				if (nextRingAttachmentBond.getAtomAt(0).flags[ISINRING])
				{
					ringSystemVector = new Vector2d(nextRingAttachmentBond.getAtomAt(1).getPoint2D());
					ringSystemVector.sub(new Vector2d(nextRingAttachmentBond.getAtomAt(0).getPoint2D()));
				}						
				else
				{
					ringSystemVector = new Vector2d(nextRingAttachmentBond.getAtomAt(0).getPoint2D());
					ringSystemVector.sub(new Vector2d(nextRingAttachmentBond.getAtomAt(1).getPoint2D()));
				}						

				ringAtom = getRingAtom(nextRingAttachmentBond);
				nextRingSystem = getRingSystemOfAtom(ringSystems, ringAtom);
				handleRings(firstBondVector, nextRingSystem);
				placeRingSubstituents(nextRingSystem);

				if (nextRingAttachmentBond.getAtomAt(0).flags[ISINRING])
				{
					newRingSystemVector = new Vector2d(nextRingAttachmentBond.getAtomAt(1).getPoint2D());
					newRingSystemVector.sub(new Vector2d(nextRingAttachmentBond.getAtomAt(0).getPoint2D()));
				}						
				else
				{
					newRingSystemVector = new Vector2d(nextRingAttachmentBond.getAtomAt(0).getPoint2D());
					newRingSystemVector.sub(new Vector2d(nextRingAttachmentBond.getAtomAt(1).getPoint2D()));
				}						
				System.out.println("Angle: " + ringSystemVector.angle(newRingSystemVector));
			}

//		}while(!allPlaced(molecule));		
		}while(!true);				
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
	 * Does a layout of all the rings in the molecule
	 */
	private void handleRings(Vector2d firstBondVector, RingSet rs)
	{
		AtomContainer sharedAtoms;
		Bond bond;
		Vector2d ringCenterVector;
		Point2d ringCenter;
		int thisRing;
		Ring ring = rs.getMostComplexRing(); /* Get the most complex ring in this RingSet */
//			if (debug) System.out.println("Most complex ring: " + ring.toString(molecule));
		sharedAtoms = placeFirstBond(ring.getBondAt(0),firstBondVector); /* Place the most complex ring at the origin of the coordinate system */
		/* 
		 * Call the method which lays out the new ring.
		 */
		ringCenterVector = getRingCenterOfFirstRing(ring, firstBondVector); 
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
				placeConnectedRings(rs, ring, FUSED);
				placeConnectedRings(rs, ring, BRIDGED);
				placeConnectedRings(rs, ring, SPIRO);
			}
			thisRing ++;
			if (thisRing == rs.size()) thisRing = 0;
			ring = (Ring)rs.elementAt(thisRing);
		}while(!allPlaced(rs));
	}

	/**
	 * Positions the aliphatic substituents of a ring system
	 *
	 * @exception   Exception  Thrown if something goes wrong.
	 */
	public void placeRingSubstituents(RingSet rs) throws java.lang.Exception
	{
		Ring ring = null;
		Atom atom = null;
		RingSet rings = null;
		AtomContainer aliphaticPartners = new AtomContainer();;
		AtomContainer sharedAtoms = new AtomContainer();
		AtomContainer primaryAtoms = new AtomContainer();
		Point2d centerOfRingGravity = null;
		for (int j = 0; j < rs.size(); j++)
		{
			ring = (Ring)rs.elementAt(j); /* Get the j-th Ring in RingSet rs */
			
			for (int k = 0; k < ring.getAtomCount(); k++)
			{
			
				aliphaticPartners.removeAllElements();
				sharedAtoms.removeAllElements();
				primaryAtoms.removeAllElements();
				if (debug) System.out.println("k = " + k + ", aliphaticPartners.getAtomCount(): " + aliphaticPartners.getAtomCount());
				atom = ring.getAtomAt(k);
				rings = rs.getRings(atom);
				centerOfRingGravity = rings.get2DCenter();
				atomPlacer.partitionPartners(atom, aliphaticPartners, sharedAtoms);
				markNotPlaced(aliphaticPartners);
			
				if (aliphaticPartners.getAtomCount() > 0)
				{
					System.out.println("aliphaticPartners");
					listNumbers(aliphaticPartners);
					atomPlacer.distributePartners(atom, sharedAtoms, centerOfRingGravity, aliphaticPartners, bondLength);
				}
			}
		}
	}
	
	

	/**
	 * Does a layout of all the non-ring parts of the molecule
	 */
	private void handleAliphatics() throws java.lang.Exception
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
				System.out.println("getNextAtomWithAliphaticUnplacedNeigbors(): " + molecule.getAtomNumber(atom));
				unplacedAtoms = getUnplacedAtoms(atom);
				placedAtoms = getPlacedAtoms(atom);

				longestUnplacedChain = atomPlacer.getLongestUnplacedChain(molecule, atom);
				listNumbers(longestUnplacedChain);
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
						direction = atomPlacer.getNextBondVector(atom, placedAtoms.getAtomAt(0), placedAtoms.getAtomAt(0));
					}
					
					for (int f = 1; f < longestUnplacedChain.getAtomCount(); f++)
					{
						longestUnplacedChain.getAtomAt(f).flags[ISPLACED] = false;
					}

					atomPlacer.placeLinearChain(longestUnplacedChain, direction, bondLength);
//					if (longestUnplacedChain.getLastAtom().flags[ISINRING])
//					{
//						System.out.println("Atom " + molecule.getAtomNumber(longestUnplacedChain.getLastAtom()) + "is in a ring ");
//						longestUnplacedChain.getLastAtom().flags[ISPLACED] = false;
//						done = true;
//					}
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


	private Atom getNextAtomWithAliphaticUnplacedNeigbors()
	{
		Bond bond = null; 
		for (int f = 0; f < molecule.getBondCount(); f++)
		{
			bond = molecule.getBondAt(f);
			if (bond.getAtomAt(1).flags[ISPLACED] && !bond.getAtomAt(0).flags[ISPLACED]  && !bond.getAtomAt(0).flags[ISINRING])
			{
				try
				{
					System.out.println("Atom " + molecule.getAtomNumber(bond.getAtomAt(1)) + " is placed");
					System.out.println("Atom " + molecule.getAtomNumber(bond.getAtomAt(0)) + " is not placed");
					System.out.println("Atom " + molecule.getAtomNumber(bond.getAtomAt(0)) + " is not in Ring");								
				}
				catch(Exception exc)
				{
						
				}

				return bond.getAtomAt(1);
			}
			
			if (bond.getAtomAt(0).flags[ISPLACED] &&  !bond.getAtomAt(1).flags[ISPLACED]  && !bond.getAtomAt(1).flags[ISINRING])
			{
				try
				{
					System.out.println("Atom " + molecule.getAtomNumber(bond.getAtomAt(1)) + " is placed");
					System.out.println("Atom " + molecule.getAtomNumber(bond.getAtomAt(0)) + " is not placed");
					System.out.println("Atom " + molecule.getAtomNumber(bond.getAtomAt(0)) + " is not in Ring");								
				}
				catch(Exception exc)
				{
						
				}

				return bond.getAtomAt(0);
			}
		}
		return null;
	}


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
		double radius = bondLength / (2 * Math.sin((Math.PI) / size));
		double newRingPerpendicular = Math.sqrt(Math.pow(radius, 2) - Math.pow(bondLength/2, 2));		
		if (debug) System.out.println("getRingCenterOfFirstRing->radius: " + radius);
		if (debug) System.out.println("getRingCenterOfFirstRing->newRingPerpendicular: " + newRingPerpendicular);		
		/* get the angle between the x axis and the bond vector */
		double rotangle = GeometryTools.getAngle(bondVector.x, bondVector.y);
		/* Add 90 Degrees to this angle, this is supposed to be the new ringcenter vector */
		rotangle += Math.PI / 2;
		return new Vector2d(Math.cos(rotangle) * newRingPerpendicular, Math.sin(rotangle) * newRingPerpendicular);
	}


	/**
	 * Layout all rings in the given RingSet that are connected to a given Ring
	 *
	 * @param   rs  The RingSet to be searched for rings connected to Ring
	 * @param   ring  The Ring for which all connected rings in RingSet are to be layed out. 
	 */
	private void placeConnectedRings(RingSet rs, Ring ring, int handleType)
	{
		Vector connectedRings = rs.getConnectedRings(ring);
		Ring connectedRing;
		AtomContainer sharedAtoms;
		int sac;
		Point2d oldRingCenter, newRingCenter, sharedAtomsCenter, tempPoint;
		Vector2d tempVector, oldRingCenterVector, newRingCenterVector;
		Bond bond;

//		if (debug) System.out.println(rs.reportRingList(molecule)); 
		for (int i = 0; i < connectedRings.size(); i++)
		{
			connectedRing = (Ring)connectedRings.elementAt(i);
			if (!connectedRing.flags[ISPLACED])
			{
//				if (debug) System.out.println(ring.toString(molecule));
//				if (debug) System.out.println(connectedRing.toString(molecule));				
				sharedAtoms = ring.getIntersection(connectedRing);
				sac = sharedAtoms.getAtomCount();
				if (debug) System.out.println("placeConnectedRings-> connectedRing: " + (ring.toString(molecule)));
				if ((sac == 2 && handleType == FUSED) ||(sac == 1 && handleType == SPIRO)||(sac > 2 && handleType == BRIDGED))
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
					ringPlacer.placeRing(connectedRing, sharedAtoms, sharedAtomsCenter, newRingCenterVector, bondLength);ringPlacer.placeRing(connectedRing, sharedAtoms, sharedAtomsCenter, newRingCenterVector, bondLength);
					connectedRing.flags[ISPLACED] = true;
					placeConnectedRings(rs, connectedRing, handleType);
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

	private Atom getComplexCentralAtom() throws java.lang.Exception
	{
		int[][] conMat = molecule.getConnectionMatrix();
		int[][] apsp = PathTools.computeFloydAPSP(conMat);
		int[] apspCol = PathTools.getInt2DColumnSum(apsp);
		int position = 0;
		int max = molecule.getAtomCount() * molecule.getAtomCount();
		Atom atom = null, mostComplexAtom = null;
		
		
		Vector[] complexity = new Vector[10];
		for (int i = 0; i < 10; i++)
		{
			complexity[i] = new Vector();
		}		
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			complexity[molecule.getDegree(i)].addElement(molecule.getAtomAt(i));
		}
		for (int i = 9; i >= 0; i--)
		{
			if (complexity[i].size() > 0)
			{
				/* This is the Vector with the most substituted atoms
				 * in the molecule.
				 */
				for (int j = 0; j < complexity[i].size(); j++)
				{
				    atom = (Atom)complexity[i].elementAt(j);
					if (debug) System.out.println(i + ", " + j + ", " + apspCol[molecule.getAtomNumber(atom)]);
					/* for each atom the molecule check its apsp distance sum */
					if (apspCol[molecule.getAtomNumber(atom)] < max)
					{
						max = apspCol[molecule.getAtomNumber(atom)];
						mostComplexAtom = atom;
					}
				}
				break;
			}
		}
		try
		{
			if (debug) System.out.println(molecule.getAtomNumber(mostComplexAtom));
		}
		catch(Exception exc)
		{
			
		}
		return mostComplexAtom;
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

	private Atom getRingAtom(Bond bond)
	{
		if (bond.getAtomAt(0).flags[ISINRING]) return bond.getAtomAt(0);
		if (bond.getAtomAt(1).flags[ISINRING]) return bond.getAtomAt(1);
		return null;
	}
	
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
	
	private void resetUnplacedRings()
	{
		Ring ring = null;
		
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

	
	private boolean allPlaced(AtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			if (!ac.getAtomAt(f).flags[ISPLACED]) return false;
		}
		return true;
	}

	private void markNotPlaced(AtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			ac.getAtomAt(f).flags[ISPLACED] = false;
		}

	}


	private void listPlaced(AtomContainer ac)
	{
		String s = "Placed: ";
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			if (ac.getAtomAt(f).flags[ISPLACED]) 
			{
				s += f + "+ ";
			}
			else
			{
				s += f + "- ";
			}
		}
		System.out.println(s);
	}

	private String listNumbers(AtomContainer ac) throws java.lang.Exception
	{
		String s = "Numbers: ";
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			s += molecule.getAtomNumber(ac.getAtomAt(f)) +  " ";
		}
		return s;
	}


}



