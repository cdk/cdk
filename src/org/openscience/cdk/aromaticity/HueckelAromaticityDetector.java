/*
 *  $RCSfile$    $Author$    $Date$    $Revision$
 *
 *  Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
package org.openscience.cdk.aromaticity;

import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import java.util.Vector;
import java.io.*;

/**
 *  Detects aromaticity based on some simple 
 *  Hueckel Rules
 *
 * @author     steinbeck
 * @created    September 4, 2001
 */
public class HueckelAromaticityDetector
{
	static boolean debug = false;
	
	/**
	 * Retrieves the set of all rings and performs an aromaticity detection
	 * based on Hueckels 2n + 1 rule.
	 * Atoms and Bonds are marked by setting the aromaticity flag
	 * (Atom.flags[ISAROMATIC]).
	 */
	public static boolean detectAromaticity(AtomContainer ac) throws org.openscience.cdk.exception.NoSuchAtomException
	{	
		RingSet ringSet = new AllRingsFinder().findAllRings(ac);
		if (ringSet.size() > 0)
		{
			return detectAromaticity(ac, ringSet);
		}
		return false;
	}

	/**
	 * A time-saving version for aromaticity detection 
	 * that uses an already computed set of rings
	 *
	 */
	public static boolean detectAromaticity(AtomContainer ac, RingSet ringSet)
	{	
		boolean foundSomething = false;
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			ac.getAtomAt(f).flags[Atom.ISAROMATIC] = false;		
		}
		for (int f = 0; f < ac.getBondCount(); f++)
		{
			ac.getBondAt(f).flags[Bond.ISAROMATIC] = false;		
		}
		
		Ring ring = null;
		Atom atom = null;
		ringSet.sort();
		for (int f = ringSet.size() - 1; f >= 0 ; f--) 
		{
			ring = (Ring)ringSet.elementAt(f);
			if (debug)System.out.println("Testing ring no " + f + " for aromaticity:");
			if (isAromatic(ac, ringSet, ring))
			{
				for (int g = 0; g < ring.getAtomCount(); g++)
				{
					ring.getAtomAt(g).flags[Atom.ISAROMATIC] = true;
				}
				for (int g = 0; g < ring.getBondCount(); g++)
				{
					ring.getBondAt(g).flags[Bond.ISAROMATIC] = true;
				}
				foundSomething = true;
				if (debug)System.out.println("Ring no " + f + " is aromatic.");				
			}
			else
			{
				if (debug)System.out.println("Ring no " + f + " is not aromatic.");	
			}
		}
		return foundSomething;
	}	

	public static boolean isAromatic(AtomContainer ac, RingSet ringSet, Ring ring)
	{
		int piElectronCount = 0;
		int freeElectronPairCount = 0;
		Atom atom = null;
		Bond bond = null;
		int aromaCounter = 0;
		if (debug) System.out.println("isAromatic() -> ring.size(): " + ring.getAtomCount());
		for (int g = 0; g < ring.getAtomCount(); g++)
		{
			atom = ring.getAtomAt(g);
			/* The following if-clause needs to be refined 
			 * I guess there should be a more rigorous way 
			 * of determining whether a hetero atom
			 * contributes a free electron or not. 
			 * Think of the role of nitrogen in 
			 * pyrrole and pyrridine
			 */
			if ("O-N-S-P".indexOf(atom.getSymbol()) > -1)
			{
				freeElectronPairCount += 1;	
			}
			if (atom.flags[Atom.ISAROMATIC])
			{
				aromaCounter ++;	
			}
			
		}
		for (int g = 0; g < ring.getBondCount(); g++)
		{
			bond = ring.getBondAt(g);
			if (bond.getOrder() > 1)
			{
				piElectronCount += 2;
			}
		}
		for (int f = 0; f < ((ring.getAtomCount() - 2)/4) + 2; f ++)
		{
			if (debug) System.out.println("isAromatic() -> freeElectronPairCount: " + freeElectronPairCount);
			if (debug) System.out.println("isAromatic() -> piElectronCount: " + piElectronCount);
			if (debug) System.out.println("isAromatic() -> f: " + f);						
			if (debug) System.out.println("isAromatic() -> (4 * f) + 2: " + ((4 * f) + 2));			
			if (debug) System.out.println("isAromatic() -> ring.size(): " + ring.getAtomCount());	
			if (debug) System.out.println("isAromatic() -> aromaCounter: " + aromaCounter);								
			if (aromaCounter == ring.getAtomCount()) return true;
			else if ((piElectronCount == ring.getAtomCount())&&((4 * f) + 2 == piElectronCount)) return true; 
			else if ((4 * f) + 2 == piElectronCount + (freeElectronPairCount * 2) && ring.getAtomCount() < piElectronCount + (freeElectronPairCount * 2)) return true;
		}
		return false;
	}

}
