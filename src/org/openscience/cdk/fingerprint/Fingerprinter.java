/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
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
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.*;
import org.openscience.cdk.aromaticity.*;
import org.openscience.cdk.exception.*;
import java.util.*;

/**
 *  Generates a Fingerprint for a given AtomContainer. Fingerprints are one-dimensional 
 *  bit arrays, where bits are set according to a the occurence of a particular structural 
 *  feature (See for example the Daylight inc. theory manual for more information) Fingerprints
 *  allow for a fast screening step to excluded candidates for a substructure search in a 
 *  database. They are also a means for determining the similarity of chemical structures.
 *
 * @author     steinbeck
 * @created    24. Februar 2002
 *
 * @keyword    fingerprint
 * @keyword    similarity
 */
public class Fingerprinter 
{
	static int defaultSize = 1024;
	static int searchDepth = 6;
	static Hashtable pathes;
	static boolean debug = false;
	static int debugCounter = 0;


	/**
	 *  Generates a fingerprint of the default size for the given AtomContainer
	 *
	 * @param  ac  The AtomContainer for which a Fingerprint is generated
	 * @return     The Fingerprint (A one-dimensional bit array)
	 */
	public static BitSet getFingerprint(AtomContainer ac) throws NoSuchAtomException
	{
		return getFingerprint(ac, defaultSize);
	}


	/**
	 *  Generates a fingerprint of a given size for the given AtomContainer
	 *
	 * @param  ac    The AtomContainer for which a Fingerprint is generated
	 * @param  size  The desired size of the fingerprint
	 * @return       The Fingerprint (A one-dimensional bit array)
	 */
	public static BitSet getFingerprint(AtomContainer ac, int size) throws NoSuchAtomException
	{
		String path = null;
		int position = -1;
		boolean isAromatic = false;
		isAromatic = HueckelAromaticityDetector.detectAromaticity(ac);
		findPathes(ac);
		BitSet bs = new BitSet(size);
		for (Enumeration e = pathes.elements(); e.hasMoreElements(); )
		{
			path = (String) e.nextElement();
			position = new java.util.Random(path.hashCode()).nextInt(defaultSize); 
			if (debug) System.out.println("Setting bit " + position + " for " + path);
			bs.set(position);
		}
		return bs;
	}


	/**
	 *  Checks whether all the positive bits in BitSet bs2 occur in BitSet
	 *  bs1. If so, the molecular structure from which bs2 was generated is 
	 *  a possible substructure of bs1.
     *
     *  Example:
     *  <pre>
     *  Molecule mol = MoleculeFactory.makeIndole();
     *  BitSet bs = Fingerprinter.getFingerprint(mol);
     *  Molecule frag1 = MoleculeFactory.makePyrrole();
     *  BitSet bs1 = Fingerprinter.getFingerprint(frag1);
     *  if (Fingerprinter.isSubset(bs, bs1)) {
     *      System.out.println("Pyrrole is subset of Indole.");
     *  }
     *  </pre>
	 *
	 * @param  bs1  The reference BitSet
	 * @param  bs2  The BitSet which is compared with bs1
	 * @return      True, if bs2 is a subset of bs2
     *
     * @keyword substructure search
	 */
	public static boolean isSubset(BitSet bs1, BitSet bs2)
	{
		BitSet clone = (BitSet) bs1.clone();
		clone.and(bs2);
		if (clone.equals(bs2))
		{
			return true;
		}
		return false;
	}


	/**
	 * Gets all pathes of length 1 up to the length given by the 'searchDepth" parameter. 
     * The pathes are aquired by a number of depth first searches, one for each atom.
	 *
	 * @param  ac  The AtomContainer which is to be searched.
	 */
	static void findPathes(AtomContainer ac)
	{
		pathes = new Hashtable();
		debugCounter = 0;
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			checkAndStore(ac.getAtomAt(f).getSymbol());
			if (debug) System.out.println("Starting at atom " + f + " with symbol " + ac.getAtomAt(f).getSymbol());
			depthFirstSearch(ac, ac.getAtomAt(f), ac.getAtomAt(f).getSymbol(), 0);
		}
	}


	/**
	 *  Performs a recursive depth first search
	 *
	 * @param  ac            The AtomContainer to be searched
	 * @param  root          The Atom to start the search at
	 * @param  currentPath   The Path that has been generated so far
	 * @param  currentDepth  The current depth in this recursive search
	 */
	static void depthFirstSearch(AtomContainer ac, Atom root, String currentPath, int currentDepth)
	{
		Bond[] bonds = ac.getConnectedBonds(root);
		Atom nextAtom = null;
		String newPath = null;

		currentDepth++;
		for (int f = 0; f < bonds.length; f++)
		{
			if (currentDepth == 1)
			{
				for (int g = 0; g < ac.getAtomCount(); g++)
				{
					ac.getAtomAt(g).flags[CDKConstants.VISITED] = false;
				}
				root.flags[CDKConstants.VISITED] = true;
			}
			nextAtom = bonds[f].getConnectedAtom(root);
			if (!nextAtom.flags[CDKConstants.VISITED])
			{
				newPath = new String(currentPath);
				if (bonds[f].flags[CDKConstants.ISAROMATIC])
				{
					newPath += ":";
				}
				else if (bonds[f].getOrder() == 1)
				{
					newPath += "-";
				}
				else if (bonds[f].getOrder() == 2)
				{
					newPath += "=";
				}
				else if (bonds[f].getOrder() == 3)
				{
					newPath += "#";
				}

				newPath += nextAtom.getSymbol();
				nextAtom.flags[CDKConstants.VISITED] = true;
				checkAndStore(newPath);
				if (currentDepth == searchDepth)
				{
					return;
				}
				if (currentDepth < searchDepth)
				{
					depthFirstSearch(ac, nextAtom, newPath, currentDepth);
				}
			}
		}
	}
	
	private static void checkAndStore(String newPath)
	{
		String storePath = new String(newPath);
		String reversePath = new StringBuffer(storePath).reverse().toString();
		
		if (reversePath.compareTo(newPath) < 0)
		{
			/* reversePath is smaller than newPath
			   so we keep reversePath */
			storePath = reversePath;	
		}
		if (debug) System.out.println("Checking for existence of Path " +  storePath);				
		/* At this point we need to check wether the 
		same path has already been found in reversed order. If so, we need to store the 
		lexicographically smaller path (This is an arbitrary choice) */
		if (!pathes.containsKey(storePath))
		{
			pathes.put(storePath, storePath);
			if (debug)
			{
				debugCounter++;
				if (debug) System.out.println("Storing path no. " + debugCounter + ": " +  storePath + ", Hash: " + storePath.hashCode());
			}
		}
		
	}
}

