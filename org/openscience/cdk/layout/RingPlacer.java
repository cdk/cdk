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
 * Methods for generating coordinates for ring atoms in various situations 
 * (condensation, spiro-attachment, etc.)
 * They can be used for Automated Structure Diagram Generation or in the interactive
 * buildup of ringsystems by the user. 
 **/

public class RingPlacer implements CDKConstants
{
	static boolean debug = true;

	private Molecule molecule; 
	
	private AtomPlacer atomPlacer = new AtomPlacer();
	

	/**
	 * Generated coordinates for a given ring. Multiplexes to special handlers 
	 * for the different possible situations (spiro-, fusion-, bridged attachement)
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
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
	
	
	/**
	 * Positions the aliphatic substituents of a ring system
	 *
	 * @param   rs The RingSystem for which the substituents are to be laid out 
	 * @return  A list of atoms that where laid out   
	 * @exception   Exception  
	 */
	public AtomContainer placeRingSubstituents(RingSet rs, double bondLength) throws java.lang.Exception
	{
		Ring ring = null;
		Atom atom = null;
		RingSet rings = null;
		AtomContainer unplacedPartners = new AtomContainer();;
		AtomContainer sharedAtoms = new AtomContainer();
		AtomContainer primaryAtoms = new AtomContainer();
		AtomContainer treatedAtoms = new AtomContainer();
		Point2d centerOfRingGravity = null;
		for (int j = 0; j < rs.size(); j++)
		{
			ring = (Ring)rs.elementAt(j); /* Get the j-th Ring in RingSet rs */
			System.out.println(atomPlacer.listNumbers(ring));
			for (int k = 0; k < ring.getAtomCount(); k++)
			{
			
				unplacedPartners.removeAllElements();
				sharedAtoms.removeAllElements();
				primaryAtoms.removeAllElements();
				if (debug) System.out.println("k = " + k + ", unplacedPartners.getAtomCount(): " + unplacedPartners.getAtomCount());
				atom = ring.getAtomAt(k);
				rings = rs.getRings(atom);
				centerOfRingGravity = rings.get2DCenter();
				atomPlacer.partitionPartners(atom, unplacedPartners, sharedAtoms);
//				partitionNonRingPartners(atom, ring, sharedAtoms, unplacedPartners);
				atomPlacer.markNotPlaced(unplacedPartners);
				treatedAtoms.add(unplacedPartners);
				if (unplacedPartners.getAtomCount() > 0)
				{
					System.out.println("unplacedPartners: " + atomPlacer.listNumbers(unplacedPartners));
					atomPlacer.distributePartners(atom, sharedAtoms, centerOfRingGravity, unplacedPartners, bondLength);
				}
			}
		}
		return treatedAtoms;
	}
	
	
	/**
	 * Generated coordinates for a given ring, which is connected to another ring a bridged ring, 
	 * i.e. it shares more than two atoms with another ring.
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
	 */
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
		atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	}
	
	/**
	 * Generated coordinates for a given ring, which is connected to another ring a spiro ring, 
	 * i.e. it shares exactly one atom with another ring.
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
	 */
	public void placeSpiroRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
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

		atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	
	}


	/**
	 * Generated coordinates for a given ring, which is connected to another ring a fused ring, 
	 * i.e. it shares exactly on bond with another ring.
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
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
		atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	}
	

	/**
	 * True if coordinates have been assigned to all atoms in all rings. 
	 *
	 * @param   rs  The ringset to be checked
	 * @return  True if coordinates have been assigned to all atoms in all rings.    
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
	 * Returns the bridge atoms, that is the outermost atoms in
	 * the chain of more than two atoms which are shared by two rings
	 *
	 * @param   sharedAtoms  The atoms (n > 2) which are shared by two rings
	 * @return  The bridge atoms, i.e. the outermost atoms in the chain of more than two atoms which are shared by two rings  
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
	 * Get all atoms bonded to a given atom in a given ring, which are not part of this ring
	 *
	 * 
	 */
	public void partitionNonRingPartners(Atom atom, Ring ring, AtomContainer ringAtoms, AtomContainer unPlacedPartners) throws java.lang.Exception
	{
		Atom[] atoms = molecule.getConnectedAtoms(atom);
		for (int i = 0; i < atoms.length; i++)
		{
			if (!ring.contains(atoms[i]))
			{
				unPlacedPartners.addAtom(atoms[i]);
			}
			else
			{
				ringAtoms.addAtom(atoms[i]);
			}
		}
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

	
	public AtomPlacer getAtomPlacer()
	{
		return this.atomPlacer;
	}

	public void setAtomPlacer(AtomPlacer atomPlacer)
	{
		this.atomPlacer = atomPlacer;
	}
}
