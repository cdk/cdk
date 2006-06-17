/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.ringsearch;

import java.util.Enumeration;
import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

/**
 *  Finds the Set of all Rings. This is an implementation of the algorithm
 *  published in {@cdk.cite HAN96}. Some of the comments refer to pseudo code
 *  fragments listed in this article. The concept is that a regular molecular
 *  graph is converted into a path graph first, i.e. a graph where the edges are
 *  actually pathes, i.e. can list several nodes that are implicitly connecting
 *  the two nodes between the path is formed. The pathes that join one endnode
 *  are step by step fused and the joined nodes deleted from the pathgraph. What
 *  remains is a graph of pathes that have the same start and endpoint and are
 *  thus rings.
 *
 *  <p><b>WARNING</b>: This class has now a timeout of 5 seconds, after which it aborts
 *  its ringsearch. The timeout value can be customized by the setTimeout()
 *  method of this class.  
 *
 *@author        steinbeck
 *@cdk.created       3. Juni 2005
 *@cdk.module    standard
 */
public class AllRingsFinder
{
	public boolean debug = false;
	private long timeout = 5000;
	private long startTime;

	/*
	 *  used for storing the original atomContainer for
	 *  reference purposes (printing)
	 */
	IAtomContainer originalAc = null;
	Vector newPathes = new Vector();
	Vector potentialRings = new Vector();
	Vector removePathes = new Vector();


	/**
	 *  Returns a ringset containing all rings in the given AtomContainer
	 *
	 *@param  atomContainer     The AtomContainer to be searched for rings
	 *@return                   A RingSet with all rings in the AtomContainer
	 *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
	 */
	public IRingSet findAllRings(IAtomContainer atomContainer) throws CDKException
	{
		startTime = System.currentTimeMillis();
		SpanningTree spanningTree;
		try {
			spanningTree = new SpanningTree((IAtomContainer) atomContainer.clone());
		} catch (CloneNotSupportedException e) {
			throw new CDKException("Could not clone IAtomContainer!", e);
		}
		spanningTree.identifyBonds();
		if (spanningTree.getBondsCyclicCount() < 37)
		{
			findAllRings(atomContainer, false);
		}
		return findAllRings(atomContainer, true);
	}


	/**
	 *  Fings the set of all rings in a molecule
	 *
	 *@param  atomContainer     the molecule to be searched for rings
	 *@param  useSSSR           use the SSSRFinder & RingPartitioner as pre-filter
	 *@return                   a RingSet containing the rings in molecule
	 *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
	 */
	public IRingSet findAllRings(IAtomContainer atomContainer, boolean useSSSR) throws CDKException
	{
		if (startTime == 0)
		{
			startTime = System.currentTimeMillis();
		}
		Vector pathes = new Vector();
		IRingSet ringSet = atomContainer.getBuilder().newRingSet();
		IAtomContainer ac = atomContainer.getBuilder().newAtomContainer();
		originalAc = atomContainer;
		ac.add(atomContainer);
		if (debug)
		{
			System.out.println("AtomCount before removal of aliphatic atoms: " + ac.getAtomCount());
		}
		removeAliphatic(ac);
		if (debug)
		{
			System.out.println("AtomCount after removal of aliphatic atoms: " + ac.getAtomCount());
		}
		if (useSSSR)
		{
			SSSRFinder sssrf = new SSSRFinder(atomContainer);
			IRingSet sssr = sssrf.findSSSR();
			Vector ringSets = RingPartitioner.partitionRings(sssr);

			for (int r = 0; r < ringSets.size(); r++)
			{
				IAtomContainer tempAC
						 = RingPartitioner.convertToAtomContainer((IRingSet) ringSets.get(r));

				doSearch(tempAC, pathes, ringSet);

			}
		} else
		{
			doSearch(ac, pathes, ringSet);
		}
		atomContainer.setProperty(CDKConstants.ALL_RINGS, ringSet);
		return ringSet;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  ac                The AtomContainer to be searched
	 *@param  pathes            A vectoring storing all the pathes
	 *@param  ringSet           A ringset to be extended while we search
	 *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
	 */
	private void doSearch(IAtomContainer ac, Vector pathes, IRingSet ringSet) throws CDKException
	{
		IAtom atom = null;
		/*
		 *  First we convert the molecular graph into a a path graph by
		 *  creating a set of two membered pathes from all the bonds in the molecule
		 */
		initPathGraph(ac, pathes);
		if (debug)
		{
			System.out.println("BondCount: " + ac.getBondCount() + ", PathCount: " + pathes.size());
		}
		do
		{
			atom = selectAtom(ac);
			if (atom != null)
			{
				remove(atom, ac, pathes, ringSet);
			}
		} while (pathes.size() > 0 && atom != null);
		if (debug)
		{
			System.out.println("pathes.size(): " + pathes.size());
		}
		if (debug)
		{
			System.out.println("ringSet.size(): " + ringSet.getAtomContainerCount());
		}
	}


	/**
	 *  Removes all external aliphatic chains by chopping them off from the
	 *  ends
	 *
	 *@param  ac                The AtomContainer to work with
	 *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
	 */
	private void removeAliphatic(IAtomContainer ac) throws CDKException
	{
		boolean removedSomething;
		IAtom atom = null;
		do
		{
			removedSomething = false;
			for (Enumeration e = ac.atoms(); e.hasMoreElements(); )
			{
				atom = (IAtom) e.nextElement();
				if (ac.getBondCount(atom) == 1)
				{
					ac.removeAtomAndConnectedElectronContainers(atom);
					removedSomething = true;
				}
			}
		} while (removedSomething);
	}


	/**
	 *  Removes an atom from the AtomContainer under certain conditions.
	 *  See {@cdk.cite HAN96} for details
	 *  
	 *
	 *@param  atom              The atom to be removed
	 *@param  ac                The AtomContainer to work on
	 *@param  pathes            The pathes to manipulate
	 *@param  rings             The ringset to be extended
	 *@exception  CDKException  Thrown if something goes wrong or if the timeout is exceeded
	 */
	private void remove(IAtom atom, IAtomContainer ac, Vector pathes, IRingSet rings) throws CDKException
	{
		Path path1 = null;
		Path path2 = null;
		Path union = null;
		int intersectionSize = 0;
		newPathes.removeAllElements();
		removePathes.removeAllElements();
		potentialRings.removeAllElements();
		if (debug)
		{
			System.out.println("*** Removing atom " + originalAc.getAtomNumber(atom) + " ***");
		}

		for (int i = 0; i < pathes.size(); i++)
		{
			path1 = (Path) pathes.elementAt(i);
			if (path1.firstElement() == atom || path1.lastElement() == atom)
			{
				for (int j = i + 1; j < pathes.size(); j++)
				{
					//System.out.print(".");
					path2 = (Path) pathes.elementAt(j);
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
							} else
							{
								potentialRings.add(union);
							}
							//if (debug) System.out.println("Intersection Size: " + intersectionSize);
							//if (debug) System.out.println("Union: " + union.toString(originalAc));
							/*
							 *  Now we know that path1 and
							 *  path2 share the Atom atom.
							 */
							removePathes.addElement(path1);
							removePathes.addElement(path2);
						}
					}
					checkTimeout();
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
		if (debug)
		{
			System.out.println("\n" + pathes.size() + " pathes and " + ac.getAtomCount() + " atoms left.");
		}
	}


	/**
	 *  Checks the pathes if a ring has been found
	 *
	 *@param  pathes   The pathes to check for rings
	 *@param  ringSet  The ringset to add the detected rings to
	 *@param  ac       The AtomContainer with the original structure
	 */
	private void detectRings(Vector pathes, IRingSet ringSet, IAtomContainer ac)
	{
		Path path = null;
		IRing ring = null;
		IBond bond = null;
		for (int f = 0; f < pathes.size(); f++)
		{
			path = (Path) pathes.elementAt(f);
			if (path.size() > 3 && path.lastElement() == path.firstElement())
			{
				if (debug)
				{
					System.out.println("Removing path " + path.toString(originalAc) + " which is a ring.");
				}
				path.removeElementAt(0);
				ring = ac.getBuilder().newRing();
				for (int g = 0; g < path.size(); g++)
				{
					ring.addAtom((IAtom) path.elementAt(g));
				}
				IBond[] bonds = ac.getBonds();
				for (int g = 0; g < bonds.length; g++)
				{
					bond = bonds[g];
					if (ring.contains(bond.getAtomAt(0)) && ring.contains(bond.getAtomAt(1)))
					{
						ring.addBond(bond);
					}
				}
				ringSet.addAtomContainer(ring);
			}
		}
	}


	/**
	 *  Initialized the path graph
	 *  See {@cdk.cite HAN96} for details
	 *
	 *@param  ac      The AtomContainer with the original structure
	 *@param  pathes  The pathes to initialize
	 */
	private void initPathGraph(IAtomContainer ac, Vector pathes)
	{
		IBond bond = null;
		Path path = null;
		IBond[] bonds = ac.getBonds();
		for (int f = 0; f < bonds.length; f++)
		{
			bond = bonds[f];
			path = new Path(bond.getAtomAt(0), bond.getAtomAt(1));
			pathes.add(path);
			if (debug)
			{
				System.out.println("initPathGraph: " + path.toString(originalAc));
			}
		}
	}


	/**
	 *  Selects an optimal atom for removal
	 *  See {@cdk.cite HAN96} for details
	 *
	 *@param  ac  The AtomContainer to search
	 *@return     The selected Atom
	 */
	private IAtom selectAtom(IAtomContainer ac)
	{
		int minDegree = 999;
		// :-)
		int degree = minDegree;
		IAtom minAtom = null;
		IAtom atom = null;
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

		return minAtom;
	}


	/**
	 *  Checks if the timeout has been reached and throws an 
	 *  exception if so. This is used to prevent this AllRingsFinder
	 *  to run for ages in certain rare cases with ring systems of
	 *  large size or special topology.
	 *
	 *@exception  CDKException  The exception thrown in case of hitting the timeout
	 */
	public void checkTimeout() throws CDKException
	{
		long time = System.currentTimeMillis();
		if (time - startTime > timeout)
		{
			throw new CDKException("Timeout for AllringsFinder exceeded");
		}
	}


	/**
	 *  Sets the timeout value in milliseconds of the AllRingsFinder object
	 *  This is used to prevent this AllRingsFinder
	 *  to run for ages in certain rare cases with ring systems of
	 *  large size or special topology
	 *
	 *@param  timeout  The new timeout value
   * @return a reference to the instance this method was called for
	 */
	public AllRingsFinder setTimeout(long timeout)
	{
		this.timeout = timeout;
    return this;
	}


	/**
	 *  Gets the timeout values in milliseconds of the AllRingsFinder object
	 *
	 *@return    The timeout value
	 */
	public long getTimeout()
	{
		return timeout;
	}
}

