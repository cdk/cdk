/* $Revision$ $Author$ $Date$    
 *
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.graph;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tools class with methods for handling molecular graphs.
 *
 * @author      steinbeck
 * @cdk.module  core
 * @cdk.svnrev  $Revision$
 * @cdk.created 2001-06-17
 * @cdk.bug     1817487
 */
@TestClass("org.openscience.cdk.graph.PathToolsTest")
public class PathTools {

    public final static boolean DEBUG = false;

    /**
     * Sums up the columns in a 2D int matrix
     *
     * @param apsp The 2D int matrix
     * @return A 1D matrix containing the column sum of the 2D matrix
     */
    @TestMethod("testGetInt2DColumnSum_arrayintint")
    public static int[] getInt2DColumnSum(int[][] apsp) {
        int[] colSum = new int[apsp.length];
        int sum;
        for (int i = 0; i < apsp.length; i++) {
            sum = 0;
            for (int j = 0; j < apsp.length; j++) {
                sum += apsp[i][j];
            }
            colSum[i] = sum;
        }
        return colSum;
    }


    /**
     * All-Pairs-Shortest-Path computation based on Floyds algorithm. Takes an nxn
     * matrix C of edge costs and produces an nxn matrix A of lengths of shortest
     * paths.
     *
     * @param costMatrix edge cost matrix
     * @return the topological distance matrix
     */
    @TestMethod("testComputeFloydAPSP_arrayintint")
    public static int[][] computeFloydAPSP(int costMatrix[][]) {
        int i;
        int j;
        int k;
        int nrow = costMatrix.length;
        int[][] distMatrix = new int[nrow][nrow];
        //logger.debug("Matrix size: " + n);
        for (i = 0; i < nrow; i++) {
            for (j = 0; j < nrow; j++) {
                if (costMatrix[i][j] == 0) {
                    distMatrix[i][j] = 999999999;
                } else {
                    distMatrix[i][j] = 1;
                }
            }
        }
        for (i = 0; i < nrow; i++) {
            distMatrix[i][i] = 0;
            // no self cycle
        }
        for (k = 0; k < nrow; k++) {
            for (i = 0; i < nrow; i++) {
                for (j = 0; j < nrow; j++) {
                    if (distMatrix[i][k] + distMatrix[k][j] < distMatrix[i][j]) {
                        distMatrix[i][j] = distMatrix[i][k] + distMatrix[k][j];
                        //P[i][j] = k;        // k is included in the shortest path
                    }
                }
            }
        }
        return distMatrix;
    }

    /**
     * All-Pairs-Shortest-Path computation based on Floyds algorithm Takes an nxn
     * matrix C of edge costs and produces an nxn matrix A of lengths of shortest
     * paths.
     *
     * @param costMatrix edge cost matrix
     * @return the topological distance matrix
     */
    @TestMethod("testComputeFloydAPSP_arraydoubledouble")
    public static int[][] computeFloydAPSP(double costMatrix[][]) {
        int i;
        int j;
        int nrow = costMatrix.length;
        int[][] distMatrix = new int[nrow][nrow];
        //logger.debug("Matrix size: " + n);
        for (i = 0; i < nrow; i++) {
            for (j = 0; j < nrow; j++) {
                if (costMatrix[i][j] == 0) {
                    distMatrix[i][j] = 0;
                } else {
                    distMatrix[i][j] = 1;
                }
            }
        }
        return computeFloydAPSP(distMatrix);
    }


    /**
     * Recursivly perfoms a depth first search in a molecular graphs contained in
     * the AtomContainer molecule, starting at the root atom and returning when it
     * hits the target atom.
     * <p>
     * CAUTION: This recursive method sets the VISITED flag of each atom
     * does not reset it after finishing the search. If you want to do the
     * operation on the same collection of atoms more than once, you have
     * to set all the VISITED flags to false before each operation
     * by looping of the atoms and doing a
     * "atom.setFlag((CDKConstants.VISITED, false));"
     * <p>
     * Note that the path generated by the search will not contain the root atom,
     * but will contain the target atom
     *
     * @param molecule The AtomContainer to be searched
     * @param root     The root atom to start the search at
     * @param target   The target
     * @param path     An AtomContainer to be filled with the path
     * @return true if the target atom was found during this function call
     */
    @TestMethod("testDepthFirstTargetSearch_IAtomContainer_IAtom_IAtom_IAtomContainer")
    public static boolean depthFirstTargetSearch(IAtomContainer molecule, IAtom root, IAtom target, IAtomContainer path) {
        java.util.List<IBond> bonds = molecule.getConnectedBondsList(root);
        IAtom nextAtom;
        root.setFlag(CDKConstants.VISITED, true);
        for (IBond bond : bonds) {
            nextAtom = bond.getConnectedAtom(root);            
            if (!nextAtom.getFlag(CDKConstants.VISITED)) {
                path.addAtom(nextAtom);
                path.addBond(bond);
                if (nextAtom == target) {
                    return true;
                } else {
                    if (!depthFirstTargetSearch(molecule, nextAtom, target, path)) {
                        // we did not find the target
                        path.removeAtom(nextAtom);
                        path.removeBond(bond);
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Performs a breadthFirstSearch in an AtomContainer starting with a
     * particular sphere, which usually consists of one start atom. While
     * searching the graph, the method marks each visited atom. It then puts all
     * the atoms connected to the atoms in the given sphere into a new vector
     * which forms the sphere to search for the next recursive method call. All
     * atoms that have been visited are put into a molecule container. This
     * breadthFirstSearch does thus find the connected graph for a given start
     * atom.
     *
     * @param atomContainer The AtomContainer to be searched
     * @param sphere        A sphere of atoms to start the search with
     * @param molecule      A molecule into which all the atoms and bonds are stored
     *                      that are found during search
     */
    @TestMethod("testBreadthFirstSearch_IAtomContainer_List_IMolecule")
    public static void breadthFirstSearch(IAtomContainer atomContainer, List<IAtom> sphere, IMolecule molecule) {
        // logger.debug("Staring partitioning with this ac: " + ac);
        breadthFirstSearch(atomContainer, sphere, molecule, -1);
    }


    /**
     * Returns the atoms which are closest to an atom in an AtomContainer by bonds.
     * If number of atoms in or below sphere x&lt;max and number of atoms in or below sphere x+1&gt;max then
     * atoms in or below sphere x+1 are returned.
     *
     * @param atomContainer The AtomContainer to examine
     * @param atom          the atom to start from
     * @param max           the number of neighbours to return
     * @return the average bond length
     */
    @TestMethod("testFindClosestByBond")
    public static IAtom[] findClosestByBond(IAtomContainer atomContainer, IAtom atom, int max) {
        IMolecule mol = atomContainer.getBuilder().newMolecule();
        List<IAtom> v = new ArrayList<IAtom>();
        v.add(atom);
        breadthFirstSearch(atomContainer, v, mol, max);
        IAtom[] returnValue = new IAtom[mol.getAtomCount() - 1];
        int k = 0;
        for (int i = 0; i < mol.getAtomCount(); i++) {
            if (mol.getAtom(i) != atom) {
                returnValue[k] = mol.getAtom(i);
                k++;
            }
        }
        return (returnValue);
    }


    /**
     * Performs a breadthFirstSearch in an AtomContainer starting with a
     * particular sphere, which usually consists of one start atom. While
     * searching the graph, the method marks each visited atom. It then puts all
     * the atoms connected to the atoms in the given sphere into a new vector
     * which forms the sphere to search for the next recursive method call. All
     * atoms that have been visited are put into a molecule container. This
     * breadthFirstSearch does thus find the connected graph for a given start
     * atom.
     * 
     * <p>IMPORTANT: this method does not reset the VISITED flags, which must be
     * done if calling this method twice!
     *
     * @param atomContainer The AtomContainer to be searched
     * @param sphere        A sphere of atoms to start the search with
     * @param molecule      A molecule into which all the atoms and bonds are stored
     *                      that are found during search
     * @param max
     */
    @TestMethod("testBreadthFirstSearch_IAtomContainer_List_IMolecule_int")
    public static void breadthFirstSearch(IAtomContainer atomContainer, List<IAtom> sphere, IMolecule molecule, int max) {
        IAtom atom;
        IAtom nextAtom;
        List<IAtom> newSphere = new ArrayList<IAtom>();
        for (int f = 0; f < sphere.size(); f++) {
            atom = sphere.get(f);
            //logger.debug("atoms  "+ atom + f);
            //logger.debug("sphere size  "+ sphere.size());
            molecule.addAtom(atom);
            // first copy LonePair's and SingleElectron's of this Atom as they need
            // to be copied too
            List<ILonePair> lonePairs = atomContainer.getConnectedLonePairsList(atom);
            //logger.debug("found #ec's: " + lonePairs.length);
            for (ILonePair lonePair : lonePairs) molecule.addLonePair(lonePair);

            List<ISingleElectron> singleElectrons = atomContainer.getConnectedSingleElectronsList(atom);
            for (ISingleElectron singleElectron : singleElectrons) molecule.addSingleElectron(singleElectron);

            // now look at bonds
            List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
            for (IBond bond : bonds) {
                if (!bond.getFlag(CDKConstants.VISITED)) {
                    molecule.addBond(bond);
                    bond.setFlag(CDKConstants.VISITED, true);
                }
                nextAtom = bond.getConnectedAtom(atom);
                if (!nextAtom.getFlag(CDKConstants.VISITED)) {
//					logger.debug("wie oft???");
                    newSphere.add(nextAtom);
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                }
            }
            if (max > -1 && molecule.getAtomCount() > max)
                return;
        }
        if (newSphere.size() > 0) {
            breadthFirstSearch(atomContainer, newSphere, molecule, max);
        }
    }


    /**
     * Performs a breadthFirstTargetSearch in an AtomContainer starting with a
     * particular sphere, which usually consists of one start atom. While
     * searching the graph, the method marks each visited atom. It then puts all
     * the atoms connected to the atoms in the given sphere into a new vector
     * which forms the sphere to search for the next recursive method call.
     * The method keeps track of the sphere count and returns it as soon
     * as the target atom is encountered.
     *
     * @param atomContainer The AtomContainer in which the path search is to be performed.
     * @param sphere        The sphere of atoms to start with. Usually just the starting atom
     * @param target        The target atom to be searched
     * @param pathLength    The current path length, incremented and passed in recursive calls. Call this method with "zero".
     * @param cutOff        Stop the path search when this cutOff sphere count has been reatomContainerhed
     * @return The shortest path between the starting sphere and the target atom
     */
    @TestMethod("testBreadthFirstTargetSearch_IAtomContainer_List_IAtom_int_int")
    public static int breadthFirstTargetSearch(IAtomContainer atomContainer, List<IAtom> sphere, IAtom target, int pathLength, int cutOff) {
        if (pathLength == 0) resetFlags(atomContainer);
        pathLength++;
        if (pathLength > cutOff) {
            return -1;
        }
        IAtom atom;

        IAtom nextAtom;
        List<IAtom> newSphere = new ArrayList<IAtom>();
        for (int f = 0; f < sphere.size(); f++) {
            atom = sphere.get(f);
            List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
            for (IBond bond : bonds) {
                if (!bond.getFlag(CDKConstants.VISITED)) {
                    bond.setFlag(CDKConstants.VISITED, true);
                }
                nextAtom = bond.getConnectedAtom(atom);
                if (!nextAtom.getFlag(CDKConstants.VISITED)) {
                    if (nextAtom == target) {
                        return pathLength;
                    }
                    newSphere.add(nextAtom);
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                }
            }
        }
        if (newSphere.size() > 0) {
            return breadthFirstTargetSearch(atomContainer, newSphere, target, pathLength, cutOff);
        }
        return -1;
    }

    @TestMethod("testResetFlags_IAtomContainer")
    protected static void resetFlags(IAtomContainer atomContainer) {
        for (int f = 0; f < atomContainer.getAtomCount(); f++) {
            atomContainer.getAtom(f).setFlag(CDKConstants.VISITED, false);
        }
        for (int f = 0; f < atomContainer.getBondCount(); f++) {
            atomContainer.getBond(f).setFlag(CDKConstants.VISITED, false);
        }

    }

    /**
     * Returns the radius of the molecular graph.
     *
     * @param atomContainer The molecule to consider
     * @return The topological radius
     */
    @TestMethod("testGetMolecularGraphRadius_IAtomContainer")
    public static int getMolecularGraphRadius(IAtomContainer atomContainer) {
        int natom = atomContainer.getAtomCount();

        int[][] admat = AdjacencyMatrix.getMatrix(atomContainer);
        int[][] distanceMatrix = computeFloydAPSP(admat);

        int[] eta = new int[natom];
        for (int i = 0; i < natom; i++) {
            int max = -99999;
            for (int j = 0; j < natom; j++) {
                if (distanceMatrix[i][j] > max) max = distanceMatrix[i][j];
            }
            eta[i] = max;
        }
        int min = 999999;
        for (int anEta : eta) {
            if (anEta < min) min = anEta;
        }
        return min;
    }

    /**
     * Returns the diameter of the molecular graph.
     *
     * @param atomContainer The molecule to consider
     * @return The topological diameter
     */
    @TestMethod("testGetMolecularGraphDiameter_IAtomContainer")
    public static int getMolecularGraphDiameter(IAtomContainer atomContainer) {
        int natom = atomContainer.getAtomCount();

        int[][] admat = AdjacencyMatrix.getMatrix(atomContainer);
        int[][] distanceMatrix = computeFloydAPSP(admat);

        int[] eta = new int[natom];
        for (int i = 0; i < natom; i++) {
            int max = -99999;
            for (int j = 0; j < natom; j++) {
                if (distanceMatrix[i][j] > max) max = distanceMatrix[i][j];
            }
            eta[i] = max;
        }
        int max = -999999;
        for (int anEta : eta) {
            if (anEta > max) max = anEta;
        }
        return max;
    }

    /**
     * Returns the number of vertices that are a distance 'd' apart.
     * <p/>
     * In this method, d is the topological distance (ie edge count).
     *
     * @param atomContainer The molecule to consider
     * @param distance      The distance to consider
     * @return The number of vertices
     */
    @TestMethod("testGetVertexCountAtDistance_IAtomContainer_int")
    public static int getVertexCountAtDistance(IAtomContainer atomContainer, int distance) {
        int natom = atomContainer.getAtomCount();

        int[][] admat = AdjacencyMatrix.getMatrix(atomContainer);
        int[][] distanceMatrix = computeFloydAPSP(admat);

        int n = 0;

        for (int i = 0; i < natom; i++) {
            for (int j = 0; j < natom; j++) {
                if (distanceMatrix[i][j] == distance) n++;
            }
        }
        return n / 2;
    }

    /**
     * Returns a list of atoms in the shortest path between two atoms.
     * <p/>
     * This method uses the Djikstra algorithm to find all the atoms in the shortest
     * path between the two specified atoms. The start and end atoms are also included
     * in the path returned
     *
     * @param atomContainer The molecule to search in
     * @param start         The starting atom
     * @param end           The ending atom
     * @return A <code>List</code> containing the atoms in the shortest path between <code>start</code> and
     *         <code>end</code> inclusive
     */
    @TestMethod("testGetShortestPath_IAtomContainer_IAtom_IAtom")
    public static List<IAtom> getShortestPath(IAtomContainer atomContainer, IAtom start, IAtom end) {
        int natom = atomContainer.getAtomCount();
        int endNumber = atomContainer.getAtomNumber(end);
        int startNumber = atomContainer.getAtomNumber(start);
        int[] dist = new int[natom];
        int[] previous = new int[natom];
        for (int i = 0; i < natom; i++) {
            dist[i] = 99999999;
            previous[i] = -1;
        }
        dist[atomContainer.getAtomNumber(start)] = 0;

        List<IAtom> Slist = new ArrayList<IAtom>();
        List<Integer> Qlist = new ArrayList<Integer>();
        for (int i = 0; i < natom; i++) Qlist.add(i);

        while (true) {
            if (Qlist.size() == 0) break;

            // extract min
            int u = 999999;
            int index = 0;
            for (Integer tmp : Qlist) {
                if (dist[tmp] < u) {
                    u = dist[tmp];
                    index = tmp;
                }
            }
            Qlist.remove(Qlist.indexOf(index));
            Slist.add(atomContainer.getAtom(index));
            if (index == endNumber) break;

            // relaxation
            List<IAtom> connected = atomContainer.getConnectedAtomsList(atomContainer.getAtom(index));
            for (IAtom aConnected : connected) {
                int anum = atomContainer.getAtomNumber(aConnected);
                if (dist[anum] > dist[index] + 1) { // all edges have equals weights
                    dist[anum] = dist[index] + 1;
                    previous[anum] = index;
                }
            }
        }

        ArrayList<IAtom> tmp = new ArrayList<IAtom>();
        int tmpSerial = endNumber;
        while (true) {
            tmp.add(0, atomContainer.getAtom(tmpSerial));
            tmpSerial = previous[tmpSerial];
            if (tmpSerial == startNumber) {
                tmp.add(0, atomContainer.getAtom(tmpSerial));
                break;
            }
        }
        return tmp;
    }

    private static List<List<IAtom>> allPaths;

    /**
     * Get a list of all the paths between two atoms.
     * <p/>
     * If the two atoms are the same an empty list is returned. Note that this problem
     * is NP-hard and so can take a long time for large graphs.
     *
     * @param atomContainer The molecule to consider
     * @param start         The starting Atom of the path
     * @param end           The ending Atom of the path
     * @return A <code>List</code> containing all the paths between the specified atoms
     */
    @TestMethod("testGetAllPaths_IAtomContainer_IAtom_IAtom")
    public static List<List<IAtom>> getAllPaths(IAtomContainer atomContainer, IAtom start, IAtom end) {
        allPaths = new ArrayList<List<IAtom>>();
        if (start.equals(end)) return allPaths;
        findPathBetween(atomContainer, start, end, new ArrayList<IAtom>());
        return allPaths;
    }

    private static void findPathBetween(IAtomContainer atomContainer, IAtom start, IAtom end, List<IAtom> path) {
        if (start == end) {
            path.add(start);
            allPaths.add(new ArrayList<IAtom>(path));
            path.remove(path.size() - 1);
            return;
        }
        if (path.contains(start))
            return;
        path.add(start);
        List<IAtom> nbrs = atomContainer.getConnectedAtomsList(start);
        for (IAtom nbr : nbrs) findPathBetween(atomContainer, nbr, end, path);
        path.remove(path.size() - 1);
    }

    /**
     * Get the paths starting from an atom of specified length.
     * <p/>
     * This method returns a set of paths. Each path is a <code>List</code> of atoms that
     * make up the path (ie they are sequentially connected).
     *
     * @param atomContainer The molecule to consider
     * @param start         The starting atom
     * @param length        The length of paths to look for
     * @return A  <code>List</code> containing the paths found
     */
    @TestMethod("testGetPathsOfLength_IAtomContainer_IAtom_int")
    public static List<List<IAtom>> getPathsOfLength(IAtomContainer atomContainer, IAtom start, int length) {
        List<IAtom> curPath = new ArrayList<IAtom>();
        List<List<IAtom>> paths = new ArrayList<List<IAtom>>();
        curPath.add(start);
        paths.add(curPath);
        for (int i = 0; i < length; i++) {
            List<List<IAtom>> tmpList = new ArrayList<List<IAtom>>();
            for (List<IAtom> path : paths) {
                curPath = path;
                IAtom lastVertex = curPath.get(curPath.size() - 1);
                List<IAtom> neighbors = atomContainer.getConnectedAtomsList(lastVertex);
                for (IAtom neighbor : neighbors) {
                    List<IAtom> newPath = new ArrayList<IAtom>(curPath);
                    if (newPath.contains(neighbor)) continue;
                    newPath.add(neighbor);
                    tmpList.add(newPath);
                }
            }
            paths.clear();
            paths.addAll(tmpList);
        }
        return (paths);
    }


}

