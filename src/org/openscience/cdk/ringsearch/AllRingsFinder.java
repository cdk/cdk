/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.ringsearch;

import java.util.Enumeration;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;

/** 
 * Finds the Set of all Rings.
 *  This is an implementation of the algorithm published in {@cdk.cite HAN96}.
 *  Some of the comments refer to pseudo code fragments listed in this article.
 *  The concept is that a regular molecular graph is converted into a path graph first,
 *  i.e. a graph where the edges are actually pathes, i.e. can list several
 *  nodes that are implicitly connecting the two nodes between the path is formed.
 *  The pathes that join one endnode are step by step fused and the joined nodes
 *  deleted from the pathgraph. What remains is a graph of pathes that have the
 *  same start and endpoint and are thus rings.
 *
 * @cdk.module standard
 */
public class AllRingsFinder
{
	public boolean debug = false;
	
	/* used for storing the original atomContainer for 
	 * reference purposes (printing)
	 */
	AtomContainer originalAc = null; 
	Vector newPathes = new Vector();
	Vector potentialRings = new Vector();
	Vector removePathes = new Vector();
	
    public RingSet findAllRings(AtomContainer atomContainer) throws org.openscience.cdk.exception.NoSuchAtomException
    {
        return findAllRings(atomContainer, true);
    }
    
	/**
	 * Fings the set of all rings in a molecule 
	 *
	 * @param   atomContainer the molecule to be searched for rings
     * @param   useSSSR       use the SSSRFinder & RingPartitioner as pre-filter
	 * @return                a RingSet containing the rings in molecule    
	 */
	public RingSet findAllRings(AtomContainer atomContainer, boolean useSSSR) throws org.openscience.cdk.exception.NoSuchAtomException
	{
		Vector pathes = new Vector();
		RingSet ringSet = new RingSet();
		AtomContainer ac = new AtomContainer();
		originalAc = atomContainer;
		ac.add(atomContainer);
		if (debug) System.out.println("AtomCount before removal of aliphatic atoms: " + ac.getAtomCount());
		removeAliphatic(ac);
		if (debug) System.out.println("AtomCount after removal of aliphatic atoms: " + ac.getAtomCount());
		
        if (useSSSR) {
            SSSRFinder sssrf = new SSSRFinder(atomContainer);
            RingSet sssr = sssrf.findSSSR();
            Vector ringSets = RingPartitioner.partitionRings(sssr);
            
            for (int r = 0; r < ringSets.size(); r++) {
                AtomContainer tempAC = RingPartitioner.convertToAtomContainer((RingSet)ringSets.get(r));
                doSearch(tempAC, pathes, ringSet);
            }
        } else {
            doSearch(ac, pathes, ringSet);
        }
		return ringSet;	  
	}

    private void doSearch(AtomContainer ac, Vector pathes, RingSet ringSet) throws org.openscience.cdk.exception.NoSuchAtomException
    {
        Atom atom = null;
        /*
        * First we convert the molecular graph into a a path graph by
        * creating a set of two membered pathes from all the bonds in the molecule
        */
        initPathGraph(ac, pathes);
        if (debug) System.out.println("BondCount: " + ac.getBondCount() + ", PathCount: " + pathes.size());
        do
        {
            atom = selectAtom(ac);
            if (atom != null) remove(atom, ac, pathes, ringSet);
        }
        while(pathes.size() > 0 && atom != null);
        if (debug) System.out.println("pathes.size(): " + pathes.size());
        if (debug) System.out.println("ringSet.size(): " + ringSet.size());
    }
    
	private void removeAliphatic(AtomContainer ac) throws org.openscience.cdk.exception.NoSuchAtomException
	{
		boolean removedSomething;
		Atom atom = null;
		do
		{
			removedSomething = false;
			for (Enumeration e = ac.atoms(); e.hasMoreElements();)
			{
				atom = (Atom)e.nextElement();
				if (ac.getBondCount(atom) == 1)
				{
					ac.removeAtomAndConnectedElectronContainers(atom);
					removedSomething = true;
				}
			}
		}while(removedSomething);
	}
	
	private void remove(Atom atom, AtomContainer ac, Vector pathes, RingSet	rings) throws org.openscience.cdk.exception.NoSuchAtomException
	{
		Path path1 = null;
		Path path2 = null;
		Path union = null;
		int intersectionSize = 0;
		newPathes.removeAllElements();
		removePathes.removeAllElements();
		potentialRings.removeAllElements();
		if (debug) System.out.println("*** Removing atom " + originalAc.getAtomNumber(atom) +  " ***");
				
		for (int i = 0; i < pathes.size(); i++)
		{
			path1 = (Path)pathes.elementAt(i);
			if (path1.firstElement() == atom || path1.lastElement() == atom)
			{
				for (int j = i + 1; j < pathes.size(); j++)
				{
					//System.out.print(".");
					path2 = (Path)pathes.elementAt(j);
					if (path2.firstElement() == atom || path2.lastElement() == atom)
					{
						intersectionSize = path1.getIntersectionSize(path2);
						if (intersectionSize < 3)
						{
							//if (debug) System.out.println("Joining " + path1.toString(originalAc) + " and " + path2.toString(originalAc));
							union = Path.join(path1, path2, atom);
							if (intersectionSize == 1)
							{
								newPathes.addElement(union);
							}
							else
							{
								potentialRings.add(union);	
							}
							//if (debug) System.out.println("Intersection Size: " + intersectionSize);
							//if (debug) System.out.println("Union: " + union.toString(originalAc));
							/* Now we know that path1 and
							 * path2 share the Atom atom. 
							 */
							 removePathes.addElement(path1);
							 removePathes.addElement(path2);
						}
					}
				}
			}
		}
		for (int f = 0; f < removePathes.size(); f++)
		{
			pathes.remove(removePathes.elementAt(f));	
		}
		for (int f = 0; f < newPathes.size(); f++)
		{
			pathes.addElement(newPathes.elementAt(f));	
		}
		detectRings(potentialRings, rings, originalAc);
		ac.removeAtomAndConnectedElectronContainers(atom);
		if (debug) System.out.println("\n" + pathes.size() + " pathes and " + ac.getAtomCount() + " atoms left.");
	}
	
	private void detectRings(Vector pathes, RingSet ringSet, AtomContainer ac)
	{
		Path path = null;
		Ring ring = null;
		Bond bond = null;
		for (int f = 0; f < pathes.size(); f ++)
		{
			path = (Path)pathes.elementAt(f);
			if (path.size() > 3 && path.lastElement() == path.firstElement())
			{
				if (debug) System.out.println("Removing path " + path.toString(originalAc) + " which is a ring.");
				path.removeElementAt(0);
				ring = new Ring();
				for (int g = 0; g < path.size(); g++)
				{
					ring.addAtom((Atom)path.elementAt(g));
				}
				Bond[] bonds = ac.getBonds();
				for (int g = 0; g < bonds.length; g++)
				{
					bond = bonds[g];
					if (ring.contains(bond.getAtomAt(0)) && ring.contains(bond.getAtomAt(1)))
					{
						ring.addBond(bond);
					}
				}
				ringSet.add(ring);
			}
		}
	}
	
	private void initPathGraph(AtomContainer ac, Vector pathes)
	{
		Bond bond = null;
		Path path = null;
        Bond[] bonds = ac.getBonds();
		for (int f = 0; f < bonds.length; f++)
		{
			bond = bonds[f];
			path = new Path(bond.getAtomAt(0), bond.getAtomAt(1));
			pathes.add(path);
			if (debug) System.out.println("initPathGraph: " + path.toString(originalAc));
		}
	}
	
	private Atom selectAtom(AtomContainer ac)
	{
		int minDegree = 999; // :-)
		int degree = minDegree;
		Atom minAtom = null;
		Atom atom = null;
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			atom = ac.getAtomAt(f);
			degree = ac.getBondCount(atom);
			
			if (degree < minDegree)
			{
				minAtom = atom;
				minDegree = degree; 
			}
		}
		try
		{
			//if (debug) System.out.println("Selected atom no " + originalAc.getAtomNumber(minAtom) + " for removal.");
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		
		return minAtom;
	}
}
