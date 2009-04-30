/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                    2009  Mark Rijnbeek <mark_rynbeek@users.sf.net>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.tools.LoggingTool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
 * <br>Also, by using the optional argument "maxRingSize" timeouts can possibly be avoided
 * because recursion depth will be limited accordingly.
 * Example: given a complex atom container and a maxRingSize of six, the find method 
 * will return all rings only of size six or smaller.
 *
 * @author        steinbeck
 * @cdk.created   2002-06-23
 * @cdk.module    standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.ringsearch.AllRingsFinderTest")
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
	List<Path> newPaths = new ArrayList<Path>();
	List<Path> potentialRings = new ArrayList<Path>();
	List<Path> removePaths = new ArrayList<Path>();


	/**
	 *  Returns a ringset containing all rings in the given AtomContainer
	 *  Calls {@link #findAllRings(IAtomContainer, Integer)} with max ring size argument set to null (=unlimited ring sizes)
   *  
	 *@param  atomContainer     The AtomContainer to be searched for rings
	 *@return                   A RingSet with all rings in the AtomContainer
	 *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
	 */
    @TestMethod("testFindAllRings_IAtomContainer,testBondsWithinRing")
    public IRingSet findAllRings(IAtomContainer atomContainer) throws CDKException
	{
	    return findAllRings(atomContainer, null);
	}

  /**
   *  Returns a ringset containing all rings up to a provided maximum size in a given AtomContainer
   *
   *@param atomContainer      The AtomContainer to be searched for rings
   *@param maxRingSize        Maximum ring size to consider. Provides a possible breakout from recursion for complex compounds.
   *@return                   A RingSet with all rings in the AtomContainer
   *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
   */
    public IRingSet findAllRings(IAtomContainer atomContainer, Integer maxRingSize) throws CDKException 
  {
        startTime = System.currentTimeMillis();
        SpanningTree spanningTree = new SpanningTree(atomContainer);
        IAtomContainer ringSystems = spanningTree.getCyclicFragmentsContainer();
        Iterator separateRingSystem = ConnectivityChecker.partitionIntoMolecules(ringSystems).molecules().iterator();
        IRingSet resultSet = atomContainer.getBuilder().newRingSet();
        while (separateRingSystem.hasNext()) {
            resultSet.add(findAllRingsInIsolatedRingSystem((IMolecule)separateRingSystem.next(), maxRingSize));
        }
        return resultSet;
  }


	/**
	 *  Fings the set of all rings in a molecule
   *  Calls {@link #findAllRingsInIsolatedRingSystem(IAtomContainer,Integer)} with max ring size argument set to null (=unlimited ring sizes)
	 *
	 *@param  atomContainer     the molecule to be searched for rings
	 *@return                   a RingSet containing the rings in molecule
	 *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
	 */

    public IRingSet findAllRingsInIsolatedRingSystem(IAtomContainer atomContainer) throws CDKException
	{
	    return findAllRingsInIsolatedRingSystem(atomContainer, null);
	}

  /**
   *Finds the set of all rings in a molecule
   *
   *@param  atomContainer     the molecule to be searched for rings
   *@param  maxRingSize       Maximum ring size to consider. Provides a possible breakout from recursion for complex compounds.
   *@return                   a RingSet containing the rings in molecule
   *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
   */
    public IRingSet findAllRingsInIsolatedRingSystem(IAtomContainer atomContainer, Integer maxRingSize) throws CDKException 
  {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        List<Path> paths = new ArrayList<Path>();
        IRingSet ringSet = atomContainer.getBuilder().newRingSet();
        IAtomContainer ac = atomContainer.getBuilder().newAtomContainer();
        originalAc = atomContainer;
        ac.add(atomContainer);
        doSearch(ac, paths, ringSet, maxRingSize);
        return ringSet;
  }


	/**
	 *@param  ac                The AtomContainer to be searched
	 *@param  paths            A vectoring storing all the paths
	 *@param  ringSet           A ringset to be extended while we search
	 *@exception  CDKException  An exception thrown if something goes wrong or if the timeout limit is reached
	 */
	private void doSearch(IAtomContainer ac, List<Path> paths, IRingSet ringSet, Integer maxPathLen) throws CDKException
	{
		IAtom atom;
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
				remove(atom, ac, paths, ringSet, maxPathLen);
			}
		} while (paths.size() > 0 && atom != null);
		logger.debug("paths.size(): ", paths.size());
		logger.debug("ringSet.size(): ", ringSet.getAtomContainerCount());
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
   *@param  maxPathLen        Max path length = max ring size detected = max recursion depth
	 *@exception  CDKException  Thrown if something goes wrong or if the timeout is exceeded
	 */
	private void remove(IAtom atom, IAtomContainer ac, List<Path> paths, IRingSet rings, Integer maxPathLen) throws CDKException
	{
		Path path1;
		Path path2;
		Path union;
		int intersectionSize;
		newPaths.clear();
		removePaths.clear();
		potentialRings.clear();
		logger.debug("*** Removing atom " + originalAc.getAtomNumber(atom) + " ***");

		for (int i = 0; i < paths.size(); i++)
		{
			path1 = paths.get(i);
			if (path1.firstElement() == atom || path1.lastElement() == atom)
			{
				for (int j = i + 1; j < paths.size(); j++)
				{
					//logger.debug(".");
					path2 = paths.get(j);
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
	  				    if (maxPathLen == null || union.size() <= (maxPathLen+1)) {
								   potentialRings.add(union);
                }
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
					if (timeout > 0) checkTimeout();
				}
			}
		}
        for (Path removePath : removePaths) {
            paths.remove(removePath);
        }
        for (Path newPath : newPaths) {
            if (maxPathLen == null || newPath.size() <= (maxPathLen+1)) {
                paths.add(newPath);
            }
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
	private void detectRings(List<Path> paths, IRingSet ringSet, IAtomContainer ac)
	{
		IRing ring;
		int bondNum;
		IAtom a1, a2 = null;
        for (Path path : paths) {
            if (path.size() > 3 && path.lastElement() == path.firstElement()) {
                logger.debug("Removing path " + path.toString(originalAc) + " which is a ring.");
                path.removeElementAt(0);
                ring = ac.getBuilder().newRing();
                for (int g = 0; g < path.size() - 1; g++) {
                    a1 = (IAtom) path.elementAt(g);
                    a2 = (IAtom) path.elementAt(g + 1);
                    ring.addAtom(a1);
                    bondNum = ac.getBondNumber(a1, a2);
                    //logger.debug("bondNum " + bondNum);
                    ring.addBond(ac.getBond(bondNum));
                }
                ring.addAtom(a2);
                a1 = (IAtom) path.elementAt(0);
                a2 = (IAtom) path.elementAt(path.size() - 1);
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
	private void initPathGraph(IAtomContainer ac, List<Path> paths)
	{
		Path path;

        Iterator bonds = ac.bonds().iterator();
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
		int degree;
		IAtom minAtom = null;
		IAtom atom;
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
    @TestMethod("testCheckTimeout")
    public void checkTimeout() throws CDKException
	{
		if (startTime == 0) return;
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
    @TestMethod("testSetTimeout_long")
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
    @TestMethod("testGetTimeout")
    public long getTimeout()
	{
		return timeout;
	}
}

