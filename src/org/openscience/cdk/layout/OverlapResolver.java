/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.layout;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.isomorphism.*;
import org.openscience.cdk.isomorphism.mcss.*;
import javax.vecmath.*;
import java.io.*;
import java.util.*;

/**
 *  Helper class for Structure Diagram Generation. Resolves atom or bond
 *  overlaps after the actual SDG was done
 *
 *@author     steinbeck
 *@created    September 4, 2003
 *@keyword    layout
 *@keyword    2D-coordinates
 */
public class OverlapResolver
{

	private  org.openscience.cdk.tools.LoggingTool logger = new org.openscience.cdk.tools.LoggingTool("OverlapResolver");

	 double bondLength = 1.5;


	/**
	 *  main method to be called to resolve overlap situations
	 *
	 *@param  ac    The atomcontainer in which the atom or bond overlap exists
	 *@param  sssr  A ring set for this atom container if one exists, otherwhise
	 *      null
	 */
	public void resolveOverlap(AtomContainer ac, RingSet sssr)
	{
		Vector overlappingAtoms = new Vector();
		Vector overlappingBonds = new Vector();
		logger.debug("Start of resolveOverlap");
		double overlapScore = getOverlapScore(ac, overlappingAtoms, overlappingBonds);
		logger.debug("overlapScore = " + overlapScore);
		logger.debug("End of resolveOverlap");
	}


	/**
	 *  Makes a small displacement to some atoms or rings in the given
	 *  atomcontainer
	 *
	 *@param  ac            The AtomContainer to work on
	 *@param  overlapatoms  A vector of overlapping atoms
	 *@param  overlapbonds  A vector of intersecting bonds
	 */
	public  void displace(AtomContainer ac, Vector overlappingAtoms, Vector overlappingBonds)
	{

	}


	/**
	 *  Calculates a score based on the overlap of atoms and intersection of bonds.
	 *  The overlap is calculated by summing up the distances between all pairs of
	 *  atoms, if they are less than half the standard bondlength apart.
	 *
	 *@param  ac  The Atomcontainer to work on
	 *@return     The overlapScore value
	 */
	public  double getOverlapScore(AtomContainer ac, Vector overlappingAtoms, Vector overlappingBonds)
	{
		Atom atom1 = null;
		Atom atom2 = null;
		Point2d p1 = null;
		Point2d p2 = null;
		double distance = 0;
		double overlapScore = 0;
		double overlapCutoff = bondLength / 2;
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			atom1 = ac.getAtomAt(f);
			p1 = atom1.getPoint2D();
			for (int g = f + 1; g < ac.getAtomCount(); g++)
			{
				atom2 = ac.getAtomAt(g);
				p2 = atom2.getPoint2D();
				distance = p1.distance(p2);
				if (distance < overlapCutoff)
				{
					overlapScore += overlapCutoff;
					overlappingAtoms.addElement(new OverlapPair(atom1, atom2));
				}
			}
		}
		return overlapScore;
	}

	public  boolean doIntersect(Bond bond1, Bond bond2)
	{
		return false;
	}

	/**
	 *  A little helper class to store pairs of overlapping atoms.
	 *
	 *@author     steinbeck
	 *@created    October 1, 2003
	 */
	public class OverlapPair
	{
		Atom atom1 = null;
		Atom atom2 = null;
		
		public OverlapPair(Atom a1, Atom a2)
		{
			atom1 = a1;
			atom2 = a2;
		}
	}
}

