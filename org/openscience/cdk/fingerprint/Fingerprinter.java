/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.*;
import java.util.*;

/**
 *  Generates a Fingerprint for a given AtomContainer. 
 *  Fingerprints are one-dimensional bit arrays, where bits are set according to
 *  a the occurence of a particular structural feature (See for example the 
 *  Daylight inc. theory manual for more information)
 *  Fingerprints allow for a fast screening step to excluded candidates 
 *  for a substructure search in a database. 
 *  They are also a means for determining the similarity of chemical
 *  structures.
 *
 *@author     steinbeck
 *@created    24. Februar 2002
 */
public class Fingerprinter implements CDKConstants
{
	static int defaultSize = 1024;
	static int searchDepth = 7;
	static Hashtable pathes;


	/**
	 *  Generates a fingerprint of the default size for the given AtomContainer
	 *
	 *@param  ac  The AtomContainer for which a Fingerprint is generated
	 *@return     The Fingerprint (A one-dimensional bit array)
	 */
	public static BitSet getFingerprint(AtomContainer ac)
	{
		return getFingerprint(ac, defaultSize);
	}


	/**
	 *  Generates a fingerprint of a given size for the given AtomContainer
	 *
	 *@param  ac  The AtomContainer for which a Fingerprint is generated
	 *@param  size  The desired size of the fingerprint
	 *@return     The Fingerprint (A one-dimensional bit array)
	 */
	public static BitSet getFingerprint(AtomContainer ac, int size)
	{
		String path = null;
		findPathes(ac);
		BitSet bs = new BitSet(size);
		for (Enumeration e = pathes.elements(); e.hasMoreElements(); )
		{
			path = (String) e.nextElement();
			bs.set(new java.util.Random(path.hashCode()).nextInt(defaultSize));
		}
		return bs;
	}


	/**
	 
	 *  Gets all pathes of length 1 up to the length given by the
	 *  'searchDepth" parameter. The pathes are aquired by a 
	 *  number of depth first searches, one for each atom.
	 *
	 *@param  ac  The AtomContainer which is to be searched.
	 */
	static void findPathes(AtomContainer ac)
	{
		pathes = new Hashtable();
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			for (int g = 0; g < ac.getAtomCount(); g++)
			{
				ac.getAtomAt(g).flags[VISITED] = false;
			}
			depthFirstSearch(ac, ac.getAtomAt(f), "", 0);
		}
	}


	/**
	 *  Performs a recursive depth first search
	 *
	 *@param  ac            The AtomContainer to be searched
	 *@param  root          The Atom to start the search at
	 *@param  currentPath   The Path that has been generated so far
	 *@param  currentDepth  The current depth in this recursive search
	 */
	static void depthFirstSearch(AtomContainer ac, Atom root, String currentPath, int currentDepth)
	{
		Bond[] bonds = ac.getConnectedBonds(root);
		Atom nextAtom = null;
		root.flags[VISITED] = true;
		currentDepth++;
		String newPath = new String(currentPath);
		for (int f = 0; f < bonds.length; f++)
		{
			nextAtom = bonds[f].getConnectedAtom(root);
			if (!nextAtom.flags[VISITED])
			{
				newPath += nextAtom.getSymbol();
				if (!pathes.containsKey(newPath))
				{
					pathes.put(newPath, newPath);
					System.out.println(newPath + ", Hash: " + newPath.hashCode());
				}
				if (currentDepth == searchDepth)
				{
					return;
				}
				if (bonds[f].getOrder() == 1)
				{
					newPath += "-";
				}
				if (bonds[f].getOrder() == 1.5)
				{
					newPath += ":";
				}
				if (bonds[f].getOrder() == 2)
				{
					newPath += "=";
				}
				if (bonds[f].getOrder() == 3)
				{
					newPath += "#";
				}

				if (currentDepth < searchDepth - 1)
				{
					depthFirstSearch(ac, nextAtom, newPath, currentDepth);
				}
			}
		}
	}
}

