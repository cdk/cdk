/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.layout;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Helper class for Structure Diagram Generation. Resolves atom or bond
 * overlaps after the actual SDG was done
 *
 * @author      steinbeck
 * @cdk.created 2003-09-4
 * @cdk.keyword layout
 * @cdk.keyword 2D-coordinates
 * @cdk.module  sdg
 * @cdk.svnrev  $Revision$
 */
public class OverlapResolver
{

	private org.openscience.cdk.tools.LoggingTool logger = null;

	double bondLength = 1.5;
	int maxSteps = 10000;
	
	public OverlapResolver()
	{
		logger = new LoggingTool(this);	
	}
	
	
	/**
	 * Main method to be called to resolve overlap situations.
	 *
	 * @param  ac    The atomcontainer in which the atom or bond overlap exists
	 * @param  sssr  A ring set for this atom container if one exists, otherwhise null
	 */
	public double resolveOverlap(IAtomContainer ac, IRingSet sssr)
	{

		Vector overlappingAtoms = new Vector();
		Vector overlappingBonds = new Vector();
		logger.debug("Start of resolveOverlap");
		double overlapScore = getOverlapScore(ac, overlappingAtoms, overlappingBonds);
		if (overlapScore > 0)
		{
			overlapScore = displace(ac, overlappingAtoms, overlappingBonds);	
		}
		logger.debug("overlapScore = " + overlapScore);
		logger.debug("End of resolveOverlap");
		return overlapScore;
	}


	/**
	 *  Makes a small displacement to some atoms or rings in the given
	 *  atomcontainer.
	 *
	 *@param  ac                The AtomContainer to work on
	 *@param  overlappingAtoms  Description of the Parameter
	 *@param  overlappingBonds  Description of the Parameter
	 */
	public double displace(IAtomContainer ac, Vector overlappingAtoms, Vector overlappingBonds)
	{
		OverlapPair op = null;
		IAtom a1 = null, a2 = null;
		Vector2d v1 = null, v2 = null;
		int steps = 0;
		int p = 0;
		double overlapScore = 0;
		double choice = 0;
		logger.debug("We are here because of an overlap situation.");
		//logger.debug("Overlap score: " + overlapScore);

		
		do{
			/* we take a random overlapping 
			 * pair of atoms
			 */
			 p = (int)(Math.random() * overlappingAtoms.size());
			 logger.debug("Taking overlap pair no. " + p);
			op = (OverlapPair)overlappingAtoms.elementAt(p);
			/* Now we have an overlapping pair of atoms
			*  We calculate the 2D vector formed by the
			* positions of both and translate one of the atoms by 
			* one tenth of a bond length
			*/
			a1 = (IAtom)op.chemObject1;
			a2 = (IAtom)op.chemObject2;
			v1 = new Vector2d(a1.getPoint2d());
			v2 = new Vector2d(a2.getPoint2d());
			v2.sub(v1);
			v2.normalize();
			v2.scale(bondLength / 20.0);
			logger.debug("Calculation translation vector " + v2);
			choice = Math.random();
			if (choice > 0.5)
			{
				a2.getPoint2d().add(v2);
				logger.debug("Random variable: " + choice + ", displacing first atom");
			}
			else
			{
				a1.getPoint2d().sub(v2);
				logger.debug("Random variable: " + choice + ", displacing second atom");
			}
			overlapScore = getOverlapScore(ac, overlappingAtoms
			, overlappingBonds);
			steps ++;
		}while(overlapScore > 0 && !(steps > maxSteps));

		if (steps < 100)
		{
			logger.debug("Overlap situation resolved");
			logger.debug("Overlap score: " + overlapScore);
			logger.debug(steps + " steps needed to clear situation");
		}
		else
		{
			logger.debug("Could not resolve overlap situation");
			logger.debug("Number of " + steps + " steps taken exceeds limit of " + maxSteps);
			logger.debug("Overlap score: " + overlapScore);
		}
		return overlapScore;
	}


	
	/**
	 *  Calculates a score based on the overlap of atoms and intersection of bonds.
	 *  The overlap is calculated by summing up the distances between all pairs of
	 *  atoms, if they are less than half the standard bondlength apart.
	 *
	 *@param  ac                The Atomcontainer to work on
	 *@param  overlappingAtoms  Description of the Parameter
	 *@param  overlappingBonds  Description of the Parameter
	 *@return                   The overlapScore value
	 */
	public double getOverlapScore(IAtomContainer ac, Vector overlappingAtoms, Vector overlappingBonds)
	{
		double overlapScore = 0;
		overlapScore = getAtomOverlapScore(ac, overlappingAtoms);
		//overlapScore += getBondOverlapScore(ac, overlappingBonds);		
		return overlapScore;
	}


	/**
	 *  Calculates a score based on the overlap of atoms.
	 *  The overlap is calculated by summing up the distances between all pairs of
	 *  atoms, if they are less than half the standard bondlength apart.
	 *
	 *@param  ac                The Atomcontainer to work on
	 *@param  overlappingAtoms  Description of the Parameter
	 *@return                   The overlapScore value
	 */
	public double getAtomOverlapScore(IAtomContainer ac, Vector overlappingAtoms)
	{
		overlappingAtoms.removeAllElements();
		IAtom atom1 = null;
		IAtom atom2 = null;
		Point2d p1 = null;
		Point2d p2 = null;
		double distance = 0;
		double overlapScore = 0;
		double overlapCutoff = bondLength / 10;
		logger.debug("Bond length is set to " + bondLength);
		logger.debug("Now cyling through all pairs of atoms");
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			atom1 = ac.getAtom(f);
			p1 = atom1.getPoint2d();
			for (int g = f + 1; g < ac.getAtomCount(); g++)
			{
				atom2 = ac.getAtom(g);
				p2 = atom2.getPoint2d();
				distance = p1.distance(p2);
				if (distance < overlapCutoff)
				{
					logger.debug("Detected atom clash with distance: " + distance + ", which is smaller than overlapCutoff " + overlapCutoff);
					overlapScore += overlapCutoff;
					overlappingAtoms.addElement(new OverlapPair(atom1, atom2));
				}
			}
		}
		return overlapScore;
	}


	/**
	 *  Calculates a score based on the intersection of bonds.
	 *
	 *@param  ac                The Atomcontainer to work on
	 *@param  overlappingBonds  Description of the Parameter
	 *@return                   The overlapScore value
	 */
	public double getBondOverlapScore(IAtomContainer ac, Vector overlappingBonds)
	{
		overlappingBonds.removeAllElements();
		double overlapScore = 0;
		IBond bond1 = null;
		IBond bond2 = null;
		double overlapCutoff = bondLength / 2;
		for (int f = 0; f < ac.getBondCount(); f++)
		{
			bond1 = ac.getBond(f);
			for (int g = f; g < ac.getBondCount(); g++)
			{
				bond2 = ac.getBond(g);
				/* bonds must not be connected */
				if (!bond1.isConnectedTo(bond2))
				{
					if (areIntersected(bond1, bond2))
					{
						overlapScore += overlapCutoff;
						overlappingBonds.addElement(new OverlapPair(bond1, bond2));
					}
				}
			}
		}
		return overlapScore;
	}



	/**
	 *  Checks if two bonds cross each other. 
	 *
	 *@param  bond1  Description of the Parameter
	 *@param  bond2  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	public boolean areIntersected(IBond bond1, IBond bond2)
	{
		double x1 = 0, x2 = 0, x3 = 0, x4 = 0;
		double y1 = 0, y2 = 0, y3 = 0, y4 = 0;
		//Point2D.Double p1 = null, p2 = null, p3 = null, p4 = null;
		
		x1 = bond1.getAtom(0).getPoint2d().x;
		x2 = bond1.getAtom(1).getPoint2d().x;
		x3 = bond2.getAtom(0).getPoint2d().x;
		x4 = bond2.getAtom(1).getPoint2d().x;
		
		y1 = bond1.getAtom(0).getPoint2d().y;
		y2 = bond1.getAtom(1).getPoint2d().y;
		y3 = bond2.getAtom(0).getPoint2d().y;
		y4 = bond2.getAtom(1).getPoint2d().y;
		
		Line2D.Double line1 = new Line2D.Double(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
		Line2D.Double line2 = new Line2D.Double(new Point2D.Double(x3, y3), new Point2D.Double(x4, y4));

		if (line1.intersectsLine(line2))
		{
			logger.debug("Two intersecting bonds detected.");
			return true;
		}
		return false;
	}


	/**
	 *  A little helper class to store pairs of overlapping atoms.
	 *
	 *@author     steinbeck
	 *@cdk.created    October 1, 2003
	 */
	public class OverlapPair
	{
		IChemObject chemObject1 = null;
		IChemObject chemObject2 = null;


		/**
		 * Constructor for the OverlapPair object.
		 *
		 * @param  co1  Description of the Parameter
		 * @param  co2  Description of the Parameter
		 */
		public OverlapPair(IChemObject co1, IChemObject co2)
		{
			chemObject1 = co1;
			chemObject2 = co2;
		}
	}
}

