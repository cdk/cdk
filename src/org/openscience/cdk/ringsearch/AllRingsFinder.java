/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
 */
package org.openscience.cdk.ringsearch;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Finds the Set of all Rings. This is an implementation of the algorithm
 * published in {@cdk.cite HAN96}. Some of the comments refer to pseudo code
 * fragments listed in this article. The concept is that a regular molecular
 * graph is converted into a path graph first, i.e. a graph where the edges are
 * actually paths, i.e. can list several nodes that are implicitly connecting
 * the two nodes between the path is formed. The paths that join one endnode
 * are step by step fused and the joined nodes deleted from the pathgraph. What
 * remains is a graph of paths that have the same start and endpoint and are
 * thus rings.
 *
 * <p><b>WARNING</b>: This class has now a timeout of 5 seconds, after which it aborts
 * its ringsearch. The timeout value can be customized by the setTimeout()
 * method of this class.  
 *
 * @author        steinbeck
 * @cdk.created   2002-06-23
 * @cdk.module    standard
 */
public class AllRingsFinder
{
	private final LoggingTool logger = new LoggingTool(AllRingsFinder.class);
	
	public boolean debug = false;
	private long timeout = 5000;
	private long startTime;

	/*
	 *  used for storing the original atomContainer for
	 *  reference purposes (printing)
	 */
	IAtomContainer originalAc = null;
	List newPaths = new ArrayList();
	List potentialRings = new ArrayList();
	List removePaths = new ArrayList();


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
		SpanningTree spanningTree = new SpanningTree(atomContainer);
		IAtomContainer ringSystems = spanningTree.getCyclicFragmentsContainer();
		Iterator separateRingSystem = ConnectivityChecker.partitionIntoMolecules(ringSystems).molecules();
		IRingSet resultSet = atomContainer.getBuilder().newRingSet();
		while (separateRingSystem.hasNext()) {
			IMolecule singleRingSystem = (IMolecule)separateRingSystem.next();
			if (singleRingSystem.getBondCount() < 37) {
				resultSet.add(findAllRings(singleRingSystem, false));
			} else { 
				resultSet.add(findAllRings(singleRingSystem, true));
			}
		}
		return resultSet;
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
		List paths = new ArrayList();
		IRingSet ringSet = atomContainer.getBuilder().newRingSet();
		IAtomContainer ac = atomContainer.getBuilder().newAtomContainer();
		originalAc = atomContainer;
		ac.add(atomContainer);
		logger.debug("AtomCount before removal of aliphatic atoms: " + ac.getAtomCount());
		removeAliphatic(ac);
		logger.debug("AtomCount after removal of aliphatic atoms: " + ac.getAtomCount());
		if (useSSSR)
		{
			SSSRFinder sssrf = new SSSRFinder(atomContainer);
			IRingSet sssr = sssrf.findSSSR();
			List ringSets = RingPartitioner.partitionRings(sssr);

			for (int r = 0; r < ringSets.size(); r++)
			{
				IAtomContainer tempAC
						 = RingPartitioner.convertToAtomContainer((IRingSet) ringSets.get(r));

				doSearch(tempAC, paths, ringSet);

			}
		} else
		{
			doSearch(ac, paths, ringSet);
		}
//		atomContainer.setProperty(CDKConstants.ALL_RINGS, ringSet);
		return ringSet;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  ac                The AtomContainer to be searched
	 *@param  paths            A vectoring storing all the paths
	 *@param  ringSet           A ringset to be extended while we search
	 *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
	 */
	private void doSearch(IAtomContainer ac, List paths, IRingSet ringSet) throws CDKException
	{
		IAtom atom = null;
		/*
		 *  First we convert the molecular graph into a a path graph by
		 *  creating a set of two membered paths from all the bonds in the molecule
		 */
		initPathGraph(ac, paths);
		logger.debug("BondCount: ", ac.getBondCount());
		logger.debug("PathCount: ", paths.size());
		do
		{
			atom = selectAtom(ac);
			if (atom != null)
			{
				remove(atom, ac, paths, ringSet);
			}
		} while (paths.size() > 0 && atom != null);
		logger.debug("paths.size(): ", paths.size());
		logger.debug("ringSet.size(): ", ringSet.getAtomContainerCount());
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
			for (Iterator e = ac.atoms(); e.hasNext(); )
			{
				atom = (IAtom) e.next();
				if (ac.getConnectedBondsCount(atom) == 1)
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
	 *@param  paths            The paths to manipulate
	 *@param  rings             The ringset to be extended
	 *@exception  CDKException  Thrown if something goes wrong or if the timeout is exceeded
	 */
	private void remove(IAtom atom, IAtomContainer ac, List paths, IRingSet rings) throws CDKException
	{
		Path path1 = null;
		Path path2 = null;
		Path union = null;
		int intersectionSize = 0;
		newPaths.clear();
		removePaths.clear();
		potentialRings.clear();
		logger.debug("*** Removing atom " + originalAc.getAtomNumber(atom) + " ***");

		for (int i = 0; i < paths.size(); i++)
		{
			path1 = (Path) paths.get(i);
			if (path1.firstElement() == atom || path1.lastElement() == atom)
			{
				for (int j = i + 1; j < paths.size(); j++)
				{
					//logger.debug(".");
					path2 = (Path) paths.get(j);
					if (path2.firstElement() == atom || path2.lastElement() == atom)
					{
						intersectionSize = path1.getIntersectionSize(path2);
						if (intersectionSize < 3)
						{
							logger.debug("Joining " + path1.toString(originalAc) + " and " + path2.toString(originalAc));
							union = Path.join(path1, path2, atom);
							if (intersectionSize == 1)
							{
								newPaths.add(union);
							} else
							{
								potentialRings.add(union);
							}
							//logger.debug("Intersection Size: " + intersectionSize);
							logger.debug("Union: ", union.toString(originalAc));
							/*
							 *  Now we know that path1 and
							 *  path2 share the Atom atom.
							 */
							removePaths.add(path1);
							removePaths.add(path2);
						}
					}
					checkTimeout();
				}
			}
		}
		for (int f = 0; f < removePaths.size(); f++)
		{
			paths.remove(removePaths.get(f));
		}
		for (int f = 0; f < newPaths.size(); f++)
		{
			paths.add(newPaths.get(f));
		}
		detectRings(potentialRings, rings, originalAc);
		ac.removeAtomAndConnectedElectronContainers(atom);
		logger.debug("\n" + paths.size() + " paths and " + ac.getAtomCount() + " atoms left.");
	}


	/**
	 *  Checks the paths if a ring has been found
	 *
	 *@param  paths   The paths to check for rings
	 *@param  ringSet  The ringset to add the detected rings to
	 *@param  ac       The AtomContainer with the original structure
	 */
	private void detectRings(List paths, IRingSet ringSet, IAtomContainer ac)
	{
		Path path = null;
		IRing ring = null;
		int bondNum;
		IAtom a1 = null, a2 = null;
		for (int f = 0; f < paths.size(); f++)
		{
			path = (Path) paths.get(f);
			if (path.size() > 3 && path.lastElement() == path.firstElement())
			{
				logger.debug("Removing path " + path.toString(originalAc) + " which is a ring.");
				path.removeElementAt(0);
				ring = ac.getBuilder().newRing();
				for (int g = 0; g < path.size() - 1; g++)
				{
					a1 = (IAtom) path.elementAt(g);
					a2 = (IAtom) path.elementAt(g + 1);
					ring.addAtom(a1);
					bondNum = ac.getBondNumber(a1, a2);
					//logger.debug("bondNum " + bondNum);
					ring.addBond(ac.getBond(bondNum));
				}
				ring.addAtom(a2);
				a1 = (IAtom) path.elementAt(0);
				a2 = (IAtom) path.elementAt(path.size()-1);
				ring.addAtom(a1);
				bondNum = ac.getBondNumber(a1, a2);
				//logger.debug("bondNum " + bondNum);
				ring.addBond(ac.getBond(bondNum));

				/*
				 * The following code had a problem when two atom in the ring 
				 * found are connected the in orignal graph but do not belong
				 * to this particular ring.
				 IBond[] bonds = ac.getBonds();
				for (int g = 0; g < bonds.length; g++)
				{
					bond = bonds[g];
					if (ring.contains(bond.getAtom(0)) && ring.contains(bond.getAtom(1)))
					{
						ring.addBond(bond);
					}
				}*/
				ringSet.addAtomContainer(ring);
			}
		}
	}


	/**
	 *  Initialized the path graph
	 *  See {@cdk.cite HAN96} for details
	 *
	 *@param  ac      The AtomContainer with the original structure
	 *@param  paths  The paths to initialize
	 */
	private void initPathGraph(IAtomContainer ac, List paths)
	{
		Path path = null;

        Iterator bonds = ac.bonds();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();                    
			path = new Path(bond.getAtom(0), bond.getAtom(1));
			paths.add(path);
			logger.debug("initPathGraph: " + path.toString(originalAc));
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
			atom = ac.getAtom(f);
			degree = ac.getConnectedBondsCount(atom);

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

