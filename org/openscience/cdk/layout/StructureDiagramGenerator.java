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
import javax.vecmath.*;
import java.util.Vector;
import java.lang.Math;
import java.awt.*;

/*  Generates 2D coordinates for a molecule for which only connectivity is known
	or the coordinates have been discarded for some reason.
*/

public class StructureDiagramGenerator 
{
		
	Molecule molecule;
	RingSet sssr;
	double bondLength = 1;
	SSSRFinder sssrf = new SSSRFinder();

	public boolean debug = true;

	public StructureDiagramGenerator()
	{
		
	}

	public StructureDiagramGenerator(Molecule molecule)
	{
		this();
		setMolecule(molecule, false);		
	}
	
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
	
	public void setMolecule(Molecule molecule)
	{
		setMolecule(molecule, true);
	}
	
	
	
	public Molecule getMolecule()
	{
		return molecule;
	}

	/** Performs the actual calculation of the coordinated based on a 
	    given set of Nodes */
	
	public void generateCoordinates(){
	
		sssr = sssrf.findSSSR(molecule);
//		if (debug) System.out.println("\n\n" + sssr.size() + " rings found:\n\n");

		Ring ring = sssr.getMostComplexRing();
		/* Place the most complex ring at the origin of the coordinate system */
		placeFirstBondOfFirstRing(ring, new Vector2d(0, 1));
		RingPlacer.placeRing(ring, ring.getBondAt(0), getRingCenterOfFirstRing(ring), bondLength);
		ring.flags[RingPlacer.ISPLACED] = true;
//		if (debug) System.out.println("\n\nFirst ring placed:\n " + ring);
//		System.out.println("*** status of molecule before drawing ***\n");
//		System.out.println(molecule + "\n");
//		System.out.println("*** status of molecule before drawing ***\n");
		placeConnectedRings(sssr, ring);
		fixRest();
		
	}
	
	private void placeFirstBondOfFirstRing(Ring ring, Vector2d bondVector)
	{
		Atom atom;
		Bond bond = ring.getBondAt(0);
		Point2d point = new Point2d(0, 0);
		atom = bond.getAtomAt(0);
		atom.setPoint2D(point);
		point = new Point2d(0, 0);
		atom = bond.getAtomAt(1);
		bondVector.scale(bondLength);
		point.add(bondVector);
		atom.setPoint2D(point);
	}
	
	private Vector2d getRingCenterOfFirstRing(Ring ring)
	{
		int size = ring.getAtomCount();
		double angle = ((size - 2) * Math.PI / size);
		double ringCenterVectorSize = bondLength / 2 * Math.tan(angle/2);
		return new Vector2d(ringCenterVectorSize, 0);
	}
	
	private void placeConnectedRings(RingSet rs, Ring ring)
	{
		Vector connectedRings = rs.getConnectedRings(ring);
		Ring connectedRing;
		Bond bond;
		Point2d oldRingCenter, newRingCenter, bondCenter;
		Vector2d tempVector;
		 
		for (int i = 0; i < connectedRings.size(); i++)
		{
			connectedRing = (Ring)connectedRings.elementAt(i);
			if (!connectedRing.flags[RingPlacer.ISPLACED])
			{
				bond = ring.getFusionBond(connectedRing);
				bondCenter = bond.get2DCenter();
				oldRingCenter = ring.get2DCenter();
//				molecule.addAtom(new Atom(new Element("O"), new Point2d(oldRingCenter)));			
				tempVector = (new Vector2d(bondCenter));
				tempVector.sub(new Vector2d(oldRingCenter));
//				System.out.println("placeConnectedRing -> tempVector: " + tempVector + ", tempVector.length: " + tempVector.length());
				newRingCenter = new Point2d(bondCenter);
//				System.out.println("placeConnectedRing -> bondCenter: " + bondCenter);			
//				molecule.addAtom(new Atom(new Element("B"), new Point2d(bondCenter)));
				newRingCenter.add(tempVector);
//				molecule.addAtom(new Atom(new Element("N"), new Point2d(newRingCenter)));
				RingPlacer.placeRing(connectedRing, bond, new Vector2d(tempVector), bondLength);
				connectedRing.flags[RingPlacer.ISPLACED] = true;
				placeConnectedRings(rs, connectedRing);
			}
		}
	
	}
	
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
	
	
	private void markNotPlaced(RingSet rs)
	{
		for (int f = 0; f < rs.size(); f++)
		{
			((Ring)rs.elementAt(f)).flags[RingPlacer.ISPLACED] = false;
		}
	}

}
