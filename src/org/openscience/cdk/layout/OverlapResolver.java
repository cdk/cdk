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
 *  Helper class for Structure Diagram Generation. Resolves overlaps
 *  after the actual SDG was done
 *
 *@author     steinbeck
 *@created    September 4, 2003
 *@keyword    layout
 *@keyword    2D-coordinates
 */
public class OverlapResolver
{

	private static org.openscience.cdk.tools.LoggingTool logger = new org.openscience.cdk.tools.LoggingTool("OverlapResolver");;

	static double bondLength = 1.5;
	
	public static void resolveOverlap(AtomContainer ac, RingSet sssr)
	{
		logger.debug("Start of resolveOverlap");
		double overlapScore = getOverlapScore(ac);
		logger.debug("overlapScore = " + overlapScore);
		logger.debug("End of resolveOverlap");
	}

	public static double getOverlapScore(AtomContainer ac)
	{
		Atom atom1 = null, atom2 = null;
		Point2d p1 = null, p2 = null;
		double distance = 0, overlapScore = 0;
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
				}
			}
		}
		return overlapScore;
	}
}

