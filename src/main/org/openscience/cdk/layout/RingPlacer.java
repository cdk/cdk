/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2008  Gilleain Torrance <gilleain@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  
 */
package org.openscience.cdk.layout;

import java.util.List;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Class providing methods for generating coordinates for ring atoms.
 * Various situations are supported, like condensation, spiro-attachment, etc.
 * They can be used for Automated Structure Diagram Generation or in the interactive
 * buildup of ringsystems by the user.
 * 
 * @cdk.module sdg
 * @cdk.svnrev  $Revision$
 **/
public class RingPlacer 
{
	final static boolean debug = false;
	private LoggingTool logger;
	
	private IMolecule molecule; 
	
	private AtomPlacer atomPlacer = new AtomPlacer();
	
	static int FUSED = 0;
	static int BRIDGED = 1;		
	static int SPIRO = 2;

	/**
	 * The empty constructor.
	 */
	public RingPlacer() 
	{
		logger = new LoggingTool(this);
	}


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
	public void placeRing(IRing ring, IAtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
	{
		int sharedAtomCount = sharedAtoms.getAtomCount();
		logger.debug("placeRing -> sharedAtomCount: " + sharedAtomCount);
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
	
	public void placeRing(IRing ring, Point2d ringCenter, double bondLength) {
	    double radius = this.getNativeRingRadius(ring, bondLength);
        double addAngle = 2 * Math.PI / ring.getRingSize();

        IAtom startAtom = ring.getFirstAtom();
        Point2d p = new Point2d(ringCenter.x + radius, ringCenter.y);
        startAtom.setPoint2d(p);
        double startAngle = 0.0;
        List<IBond> bonds = ring.getConnectedBondsList(startAtom);
        /*
         * Store all atoms to draw in consecutive order relative to the
         * chosen bond.
         */
        Vector<IAtom> atomsToDraw = new Vector<IAtom>();
        IAtom currentAtom = startAtom;
        IBond currentBond = (IBond)bonds.get(0);
        for (int i = 0; i < ring.getBondCount(); i++) {
            currentBond = ring.getNextBond(currentBond, currentAtom);
            currentAtom = currentBond.getConnectedAtom(currentAtom);
            atomsToDraw.addElement(currentAtom);
        }
        atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	}
	
	/**
	 * Positions the aliphatic substituents of a ring system
	 *
	 * @param   rs The RingSystem for which the substituents are to be laid out 
	 * @return  A list of atoms that where laid out   
	 */
	public IAtomContainer placeRingSubstituents(IRingSet rs, double bondLength)
	{
		logger.debug("RingPlacer.placeRingSubstituents() start");
		IRing ring = null;
		IAtom atom = null;
		IRingSet rings = null;
		IAtomContainer unplacedPartners = rs.getBuilder().newAtomContainer();
		IAtomContainer sharedAtoms = rs.getBuilder().newAtomContainer();
		IAtomContainer primaryAtoms = rs.getBuilder().newAtomContainer();
		IAtomContainer treatedAtoms = rs.getBuilder().newAtomContainer();
		Point2d centerOfRingGravity = null;
		for (int j = 0; j < rs.getAtomContainerCount(); j++)
		{
			ring = (IRing)rs.getAtomContainer(j); /* Get the j-th Ring in RingSet rs */
			for (int k = 0; k < ring.getAtomCount(); k++)
			{
				unplacedPartners.removeAllElements();
				sharedAtoms.removeAllElements();
				primaryAtoms.removeAllElements();
				atom = ring.getAtom(k);
				rings = rs.getRings(atom);
				centerOfRingGravity = GeometryTools.get2DCenter(rings);
				atomPlacer.partitionPartners(atom, unplacedPartners, sharedAtoms);
				atomPlacer.markNotPlaced(unplacedPartners);
				try
				{
						for (int f = 0; f < unplacedPartners.getAtomCount(); f++)
						{
							logger.debug("placeRingSubstituents->unplacedPartners: " + (molecule.getAtomNumber(unplacedPartners.getAtom(f)) + 1));
						}
				}
				catch(Exception exc)
				{
				}
				
				treatedAtoms.add(unplacedPartners);
				if (unplacedPartners.getAtomCount() > 0)
				{
					atomPlacer.distributePartners(atom, sharedAtoms, centerOfRingGravity, unplacedPartners, bondLength);
				}
			}
		}
		logger.debug("RingPlacer.placeRingSubstituents() end");
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
	private  void placeBridgedRing(IRing ring, IAtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		double radius = getNativeRingRadius(ring, bondLength);
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenterVector.normalize();
		logger.debug("placeBridgedRing->: ringCenterVector.length()" + ringCenterVector.length());	
		ringCenterVector.scale(radius);
		ringCenter.add(ringCenterVector);


		IAtom[] bridgeAtoms = getBridgeAtoms(sharedAtoms);
		IAtom bondAtom1 = bridgeAtoms[0];
		IAtom bondAtom2 = bridgeAtoms[1];

		Vector2d bondAtom1Vector = new Vector2d(bondAtom1.getPoint2d());
		Vector2d bondAtom2Vector = new Vector2d(bondAtom2.getPoint2d());		
		Vector2d originRingCenterVector = new Vector2d(ringCenter);		

		bondAtom1Vector.sub(originRingCenterVector);
		bondAtom2Vector.sub(originRingCenterVector);		

		double occupiedAngle = bondAtom1Vector.angle(bondAtom2Vector);		
		
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize() - sharedAtoms.getAtomCount() + 1);

		logger.debug("placeBridgedRing->occupiedAngle: " + Math.toDegrees(occupiedAngle));
		logger.debug("placeBridgedRing->remainingAngle: " + Math.toDegrees(remainingAngle));

		logger.debug("placeBridgedRing->addAngle: " + Math.toDegrees(addAngle));				


		IAtom startAtom;

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		double xDiff = bondAtom1.getPoint2d().x - bondAtom2.getPoint2d().x;
		double yDiff = bondAtom1.getPoint2d().y - bondAtom2.getPoint2d().y;
		
		double startAngle;
		
		int direction = 1;
		// if bond is vertical
		if (xDiff == 0)
		{
			logger.debug("placeBridgedRing->Bond is vertical");
			//starts with the lower Atom
			if (bondAtom1.getPoint2d().y > bondAtom2.getPoint2d().y)
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerX < bondAtom1.getPoint2d().x)
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
			if (bondAtom1.getPoint2d().x > bondAtom2.getPoint2d().x)
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerY - bondAtom1.getPoint2d().y > (centerX - bondAtom1.getPoint2d().x) * yDiff / xDiff)
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}
		startAngle = GeometryTools.getAngle(startAtom.getPoint2d().x - ringCenter.x, startAtom.getPoint2d().y - ringCenter.y);

		IAtom currentAtom = startAtom;
        // determine first bond in Ring
        int k = 0;
//        for (k = 0; k < ring.getElectronContainerCount(); k++) {
//            if (ring.getElectronContainer(k) instanceof IBond) break;
//        }
        IBond currentBond = sharedAtoms.getBond(0);
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount(); i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			if (!sharedAtoms.contains(currentAtom))
			{
				atomsToDraw.addElement(currentAtom);
			}
		}
			try
			{
				logger.debug("placeBridgedRing->atomsToPlace: " + atomPlacer.listNumbers(molecule, atomsToDraw));
				logger.debug("placeBridgedRing->startAtom is: " + (molecule.getAtomNumber(startAtom) + 1));
				logger.debug("placeBridgedRing->startAngle: " + Math.toDegrees(startAngle));
				logger.debug("placeBridgedRing->addAngle: " + Math.toDegrees(addAngle));		
			}
			catch(Exception exc)
			{
				logger.debug("Caught an exception while logging in RingPlacer");
			}
		
		addAngle = addAngle * direction;
		atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	}
	
	/**
	 * Generated coordinates for a given ring, which is connected to a spiro ring.
	 * The rings share exactly one atom.
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
	 */
	public void placeSpiroRing(IRing ring, IAtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
	{

		logger.debug("placeSpiroRing");
		double radius = getNativeRingRadius(ring, bondLength);
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenterVector.normalize();
		ringCenterVector.scale(radius);
		ringCenter.add(ringCenterVector);
		double addAngle = 2 * Math.PI / ring.getRingSize();

		IAtom startAtom = sharedAtoms.getAtom(0);

		//double centerX = ringCenter.x;
		//double centerY = ringCenter.y;
		
		//int direction = 1;

		IAtom currentAtom = startAtom;
		double startAngle = GeometryTools.getAngle(startAtom.getPoint2d().x - ringCenter.x, startAtom.getPoint2d().y - ringCenter.y);
		/* 
		 * Get one bond connected to the spiro bridge atom.
		 * It doesn't matter in which direction we draw.
		 */ 
		java.util.List bonds = ring.getConnectedBondsList(startAtom);
		
		IBond currentBond = (IBond)bonds.get(0);
		
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
		logger.debug("currentAtom  "+currentAtom);
		logger.debug("startAtom  "+startAtom);

		atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	
	}


	/**
	 * Generated coordinates for a given ring, which is fused to another ring.
	 * The rings share exactly one bond.
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
	 */
	public  void placeFusedRing(IRing ring, IAtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		logger.debug("RingPlacer.placeFusedRing() start");
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		double radius = getNativeRingRadius(ring, bondLength);
		double newRingPerpendicular = Math.sqrt(Math.pow(radius, 2) - Math.pow(bondLength/2, 2));
		ringCenterVector.normalize();
		logger.debug("placeFusedRing->: ringCenterVector.length()" + ringCenterVector.length());	
		ringCenterVector.scale(newRingPerpendicular);
		ringCenter.add(ringCenterVector);

		IAtom bondAtom1 = sharedAtoms.getAtom(0);
		IAtom bondAtom2 = sharedAtoms.getAtom(1);

		Vector2d bondAtom1Vector = new Vector2d(bondAtom1.getPoint2d());
		Vector2d bondAtom2Vector = new Vector2d(bondAtom2.getPoint2d());		
		Vector2d originRingCenterVector = new Vector2d(ringCenter);		

		bondAtom1Vector.sub(originRingCenterVector);
		bondAtom2Vector.sub(originRingCenterVector);		

		double occupiedAngle = bondAtom1Vector.angle(bondAtom2Vector);		
		
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize()-1);
	
		logger.debug("placeFusedRing->occupiedAngle: " + Math.toDegrees(occupiedAngle));
		logger.debug("placeFusedRing->remainingAngle: " + Math.toDegrees(remainingAngle));
		logger.debug("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));				


		IAtom startAtom;

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		double xDiff = bondAtom1.getPoint2d().x - bondAtom2.getPoint2d().x;
		double yDiff = bondAtom1.getPoint2d().y - bondAtom2.getPoint2d().y;
		
		double startAngle;;	
		
		int direction = 1;
		// if bond is vertical
     	if (xDiff == 0)
		{
			logger.debug("placeFusedRing->Bond is vertical");
			//starts with the lower Atom
			if (bondAtom1.getPoint2d().y > bondAtom2.getPoint2d().y)
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerX < bondAtom1.getPoint2d().x)
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
			if (bondAtom1.getPoint2d().x > bondAtom2.getPoint2d().x)
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerY - bondAtom1.getPoint2d().y > (centerX - bondAtom1.getPoint2d().x) * yDiff / xDiff)
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}
		startAngle = GeometryTools.getAngle(startAtom.getPoint2d().x - ringCenter.x, startAtom.getPoint2d().y - ringCenter.y);
	
		IAtom currentAtom = startAtom;
        // determine first bond in Ring
//        int k = 0;
//        for (k = 0; k < ring.getElectronContainerCount(); k++) {
//            if (ring.getElectronContainer(k) instanceof IBond) break;
//        }
        IBond currentBond = sharedAtoms.getBond(0);
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount() - 2; i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		addAngle = addAngle * direction;
			try
			{
				logger.debug("placeFusedRing->startAngle: " + Math.toDegrees(startAngle));
				logger.debug("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));		
				logger.debug("placeFusedRing->startAtom is: " + (molecule.getAtomNumber(startAtom) + 1));
				logger.debug("AtomsToDraw: " + atomPlacer.listNumbers(molecule, atomsToDraw));
			}
			catch(Exception exc)
			{
				logger.debug("Caught an exception while logging in RingPlacer");
			}
		atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);
	}
	

	/**
	 * True if coordinates have been assigned to all atoms in all rings. 
	 *
	 * @param   rs  The ringset to be checked
	 * @return  True if coordinates have been assigned to all atoms in all rings.    
	 */

	public  boolean allPlaced(IRingSet rs)
	{
		for (int i = 0; i < rs.getAtomContainerCount(); i++)
		{
			if (!((IRing)rs.getAtomContainer(i)).getFlag(CDKConstants.ISPLACED)) 
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Walks throught the atoms of each ring in a ring set and marks 
	 * a ring as PLACED if all of its atoms have been placed.
	 *
	 * @param   rs  The ringset to be checked
	 */
	public void checkAndMarkPlaced(IRingSet rs)
	{
		IRing ring = null;
		boolean allPlaced = true;
		for (int i = 0; i < rs.getAtomContainerCount(); i++)
		{
			ring = (IRing)rs.getAtomContainer(i);
			allPlaced = true;
			for (int j = 0; j < ring.getAtomCount(); j++)
			{
				if (!((IAtom)ring.getAtom(j)).getFlag(CDKConstants.ISPLACED))
				{
					allPlaced = false; 
					break;
				}
			}
			ring.setFlag(CDKConstants.ISPLACED, allPlaced);
		}
	}


	/**
	 * Returns the bridge atoms, that is the outermost atoms in
	 * the chain of more than two atoms which are shared by two rings
	 *
	 * @param   sharedAtoms  The atoms (n > 2) which are shared by two rings
	 * @return  The bridge atoms, i.e. the outermost atoms in the chain of more than two atoms which are shared by two rings  
	 */
	private  IAtom[] getBridgeAtoms(IAtomContainer sharedAtoms)
	{
		IAtom[] bridgeAtoms = new IAtom[2];
		IAtom atom;
		int counter = 0; 
		for (int f = 0; f < sharedAtoms.getAtomCount(); f++)
		{
			atom = sharedAtoms.getAtom(f);	
			if (sharedAtoms.getConnectedAtomsList(atom).size() == 1)
			{
				bridgeAtoms[counter] = atom;
				counter ++;
			}
		}
		return bridgeAtoms;
	}

	/**
	 * Partition the bonding partners of a given atom into ring atoms and non-ring atoms
	 *
	 * @param   atom  The atom whose bonding partners are to be partitioned
	 * @param   ring  The ring against which the bonding partners are checked
	 * @param   ringAtoms  An AtomContainer to store the ring bonding partners
	 * @param   nonRingAtoms  An AtomContainer to store the non-ring bonding partners
	 */
	public void partitionNonRingPartners(IAtom atom, IRing ring, IAtomContainer ringAtoms, IAtomContainer nonRingAtoms)
	{
		java.util.List atoms = molecule.getConnectedAtomsList(atom);
		for (int i = 0; i < atoms.size(); i++)
		{
			IAtom curAtom = (IAtom)atoms.get(i);
			if (!ring.contains(curAtom))
			{
				nonRingAtoms.addAtom(curAtom);
			}
			else
			{
				ringAtoms.addAtom(curAtom);
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
	public double getNativeRingRadius(IRing ring, double bondLength)
	{
		int size = ring.getAtomCount();
		double radius = bondLength / (2 * Math.sin((Math.PI) / size));
		return radius;
	}




	/**
	 * Calculated the center for the first ring so that it can
	 * layed out. Only then, all other rings can be assigned
	 * coordinates relative to it. 
	 *
	 * @param   ring  The ring for which the center is to be calculated
	 * @return  A Vector2d pointing to the new ringcenter   
	 */
	Vector2d getRingCenterOfFirstRing(IRing ring, Vector2d bondVector, double bondLength)
	{
		int size = ring.getAtomCount();
		double radius = bondLength / (2 * Math.sin((Math.PI) / size));
		double newRingPerpendicular = Math.sqrt(Math.pow(radius, 2) - Math.pow(bondLength/2, 2));		
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
	void placeConnectedRings(IRingSet rs, IRing ring, int handleType, double bondLength)
	{
		IRingSet connectedRings = rs.getConnectedRings(ring);
		IRing connectedRing;
		IAtomContainer sharedAtoms;
		int sac;
		Point2d oldRingCenter, sharedAtomsCenter, tempPoint;
		Vector2d tempVector, oldRingCenterVector, newRingCenterVector;

//		logger.debug(rs.reportRingList(molecule));
		for (IAtomContainer container : connectedRings.atomContainers()) {
			connectedRing = (IRing)container;
			if (!connectedRing.getFlag(CDKConstants.ISPLACED))
			{
//				logger.debug(ring.toString(molecule));
//				logger.debug(connectedRing.toString(molecule));				
				sharedAtoms = AtomContainerManipulator.getIntersection(ring, connectedRing);
				sac = sharedAtoms.getAtomCount();
				logger.debug("placeConnectedRings-> connectedRing: " + (ring.toString()));
				if ((sac == 2 && handleType == FUSED) ||(sac == 1 && handleType == SPIRO)||(sac > 2 && handleType == BRIDGED))
				{
					sharedAtomsCenter = GeometryTools.get2DCenter(sharedAtoms);
					oldRingCenter = GeometryTools.get2DCenter(ring);
					tempVector = (new Vector2d(sharedAtomsCenter));
					newRingCenterVector = new Vector2d(tempVector);
					newRingCenterVector.sub(new Vector2d(oldRingCenter));
					oldRingCenterVector = new Vector2d(newRingCenterVector);
					logger.debug("placeConnectedRing -> tempVector: " + tempVector + ", tempVector.length: " + tempVector.length()); 
					logger.debug("placeConnectedRing -> bondCenter: " + sharedAtomsCenter);
					logger.debug("placeConnectedRing -> oldRingCenterVector.length(): " + oldRingCenterVector.length());
					logger.debug("placeConnectedRing -> newRingCenterVector.length(): " + newRingCenterVector.length());					
					tempPoint = new Point2d(sharedAtomsCenter);
					tempPoint.add(newRingCenterVector);
					placeRing(connectedRing, sharedAtoms, sharedAtomsCenter, newRingCenterVector, bondLength);
					connectedRing.setFlag(CDKConstants.ISPLACED, true);
					placeConnectedRings(rs, connectedRing, handleType, bondLength);
				}
			}
		}
	}

	public IMolecule getMolecule()
	{
		return this.molecule;
	}

	public void setMolecule(IMolecule molecule)
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
