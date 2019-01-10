/* Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;

import java.util.ArrayList;
import java.util.List;

/**
 * Tools class with methods for handling molecular graphs.
 *
 * @author      steinbeck
 * @cdk.module  core
 * @cdk.githash
 * @cdk.created 2001-06-17
 */
public class PathTools {

    /** Boolean with which debugging can be turned on. */
    public final static boolean DEBUG = false;

    /**
     * Sums up the columns in a 2D int matrix.
     *
     * @param apsp The 2D int matrix
     * @return A 1D matrix containing the column sum of the 2D matrix
     */
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
     * All-Pairs-Shortest-Path computation based on Floyd's
     * algorithm {@cdk.cite FLO62}. It takes an nxn
     * matrix C of edge costs and produces an nxn matrix A of lengths of shortest
     * paths.
     *
     * @param costMatrix edge cost matrix
     * @return the topological distance matrix
     */
    public static int[][] computeFloydAPSP(int costMatrix[][]) {
        int nrow = costMatrix.length;
        int[][] distMatrix = new int[nrow][nrow];
        //logger.debug("Matrix size: " + n);
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < nrow; j++) {
                if (costMatrix[i][j] == 0) {
                    distMatrix[i][j] = 999999999;
                } else {
                    distMatrix[i][j] = 1;
                }
            }
        }
        for (int i = 0; i < nrow; i++) {
            distMatrix[i][i] = 0;
            // no self cycle
        }
        for (int k = 0; k < nrow; k++) {
            for (int i = 0; i < nrow; i++) {
                for (int j = 0; j < nrow; j++) {
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
     * All-Pairs-Shortest-Path computation based on Floyd's
     * algorithm {@cdk.cite FLO62}. It takes an nxn
     * matrix C of edge costs and produces an nxn matrix A of lengths of shortest
     * paths.
     *
     * @param costMatrix edge cost matrix
     * @return the topological distance matrix
     */
    public static int[][] computeFloydAPSP(double costMatrix[][]) {
        int nrow = costMatrix.length;
        int[][] distMatrix = new int[nrow][nrow];
        //logger.debug("Matrix size: " + n);
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < nrow; j++) {
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
     * Recursively performs a depth first search in a molecular graphs contained in
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
    public static boolean depthFirstTargetSearch(IAtomContainer molecule, IAtom root, IAtom target, IAtomContainer path) {
        List<IBond> bonds = molecule.getConnectedBondsList(root);
        IAtom nextAtom;
        root.setFlag(CDKConstants.VISITED, true);
        boolean first = path.isEmpty();
        if (first)
            path.addAtom(root);
        for (IBond bond : bonds) {
            nextAtom = bond.getOther(root);
            if (!nextAtom.getFlag(CDKConstants.VISITED)) {
                path.addAtom(nextAtom);
                path.addBond(bond);
                if (nextAtom.equals(target)) {
                    if (first)
                        path.removeAtomOnly(root);
                    return true;
                } else {
                    if (!depthFirstTargetSearch(molecule, nextAtom, target, path)) {
                        // we did not find the target
                        path.removeAtomOnly(nextAtom);
                        path.removeBond(bond);
                    } else {
                        if (first)
                            path.removeAtomOnly(root);
                        return true;
                    }
                }
            }
        }
        if (first)
            path.removeAtomOnly(root);
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
    public static void breadthFirstSearch(IAtomContainer atomContainer, List<IAtom> sphere, IAtomContainer molecule) {
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
    public static IAtom[] findClosestByBond(IAtomContainer atomContainer, IAtom atom, int max) {
        IAtomContainer mol = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        List<IAtom> v = new ArrayList<IAtom>();
        v.add(atom);
        breadthFirstSearch(atomContainer, v, mol, max);
        IAtom[] returnValue = new IAtom[mol.getAtomCount() - 1];
        int k = 0;
        for (int i = 0; i < mol.getAtomCount(); i++) {
            if (!mol.getAtom(i).equals(atom)) {
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
    public static void breadthFirstSearch(IAtomContainer atomContainer, List<IAtom> sphere, IAtomContainer molecule,
            int max) {
        IAtom nextAtom;
        List<IAtom> newSphere = new ArrayList<IAtom>();
        for (IAtom atom : sphere) {
            //logger.debug("atoms  "+ atom + f);
            //logger.debug("sphere size  "+ sphere.size());
            molecule.addAtom(atom);
            // first copy LonePair's and SingleElectron's of this Atom as they need
            // to be copied too
            List<ILonePair> lonePairs = atomContainer.getConnectedLonePairsList(atom);
            //logger.debug("found #ec's: " + lonePairs.length);
            for (ILonePair lonePair : lonePairs)
                molecule.addLonePair(lonePair);

            List<ISingleElectron> singleElectrons = atomContainer.getConnectedSingleElectronsList(atom);
            for (ISingleElectron singleElectron : singleElectrons)
                molecule.addSingleElectron(singleElectron);

            // now look at bonds
            List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
            for (IBond bond : bonds) {
                nextAtom = bond.getOther(atom);
                if (!bond.getFlag(CDKConstants.VISITED)) {
                    molecule.addAtom(nextAtom);
                    molecule.addBond(bond);
                    bond.setFlag(CDKConstants.VISITED, true);
                }
                if (!nextAtom.getFlag(CDKConstants.VISITED)) {
                    //					logger.debug("wie oft???");
                    newSphere.add(nextAtom);
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                }
            }
            if (max > -1 && molecule.getAtomCount() > max) return;
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
    public static int breadthFirstTargetSearch(IAtomContainer atomContainer, List<IAtom> sphere, IAtom target,
            int pathLength, int cutOff) {
        if (pathLength == 0) resetFlags(atomContainer);
        pathLength++;
        if (pathLength > cutOff) {
            return -1;
        }

        IAtom nextAtom;
        List<IAtom> newSphere = new ArrayList<IAtom>();
        for (IAtom atom : sphere) {
            List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
            for (IBond bond : bonds) {
                if (!bond.getFlag(CDKConstants.VISITED)) {
                    bond.setFlag(CDKConstants.VISITED, true);
                }
                nextAtom = bond.getOther(atom);
                if (!nextAtom.getFlag(CDKConstants.VISITED)) {
                    if (nextAtom.equals(target)) {
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
     * 
     * In this method, d is the topological distance (ie edge count).
     *
     * @param atomContainer The molecule to consider
     * @param distance      The distance to consider
     * @return The number of vertices
     */
    public static int getVertexCountAtDistance(IAtomContainer atomContainer, int distance) {
        int natom = atomContainer.getAtomCount();

        int[][] admat = AdjacencyMatrix.getMatrix(atomContainer);
        int[][] distanceMatrix = computeFloydAPSP(admat);

        int matches = 0;

        for (int i = 0; i < natom; i++) {
            for (int j = 0; j < natom; j++) {
                if (distanceMatrix[i][j] == distance) matches++;
            }
        }
        return matches / 2;
    }

    /**
     * Returns a list of atoms in the shortest path between two atoms.
     * 
     * This method uses the Djikstra algorithm to find all the atoms in the shortest
     * path between the two specified atoms. The start and end atoms are also included
     * in the path returned
     *
     * @param atomContainer The molecule to search in
     * @param start         The starting atom
     * @param end           The ending atom
     * @return A <code>List</code> containing the atoms in the shortest path between <code>start</code> and
     *         <code>end</code> inclusive
     * @see ShortestPaths
     * @see ShortestPaths#atomsTo(IAtom)
     * @see AllPairsShortestPaths
     * @deprecated This implementation recalculates all shortest paths from the start atom
     *             for each method call and does not indicate if there are equally short paths
     *             from the start to the end. Replaced by {@link ShortestPaths#atomsTo(IAtom)}
     */
    @Deprecated
    public static List<IAtom> getShortestPath(IAtomContainer atomContainer, IAtom start, IAtom end) {
        int natom = atomContainer.getAtomCount();
        int endNumber = atomContainer.indexOf(end);
        int startNumber = atomContainer.indexOf(start);
        int[] dist = new int[natom];
        int[] previous = new int[natom];
        for (int i = 0; i < natom; i++) {
            dist[i] = 99999999;
            previous[i] = -1;
        }
        dist[atomContainer.indexOf(start)] = 0;

        List<Integer> qList = new ArrayList<Integer>();
        for (int i = 0; i < natom; i++)
            qList.add(i);

        while (true) {
            if (qList.size() == 0) break;

            // extract min
            int u = 999999;
            int index = 0;
            for (Integer tmp : qList) {
                if (dist[tmp] < u) {
                    u = dist[tmp];
                    index = tmp;
                }
            }
            qList.remove((Integer)index);
            if (index == endNumber) break;

            // relaxation
            List<IAtom> connected = atomContainer.getConnectedAtomsList(atomContainer.getAtom(index));
            for (IAtom aConnected : connected) {
                int anum = atomContainer.indexOf(aConnected);
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

    /**
     * Get a list of all the paths between two atoms.
     * 
     * If the two atoms are the same an empty list is returned. Note that this problem
     * is NP-hard and so can take a long time for large graphs.
     *
     * @param atomContainer The molecule to consider
     * @param start         The starting Atom of the path
     * @param end           The ending Atom of the path
     * @return A <code>List</code> containing all the paths between the specified atoms
     */
    public static List<List<IAtom>> getAllPaths(IAtomContainer atomContainer, IAtom start, IAtom end) {
        List<List<IAtom>> allPaths = new ArrayList<List<IAtom>>();
        if (start.equals(end)) return allPaths;
        findPathBetween(allPaths, atomContainer, start, end, new ArrayList<IAtom>());
        return allPaths;
    }

    private static void findPathBetween(List<List<IAtom>> allPaths, IAtomContainer atomContainer, IAtom start,
            IAtom end, List<IAtom> path) {
        if (start.equals(end)) {
            path.add(start);
            allPaths.add(new ArrayList<IAtom>(path));
            path.remove(path.size() - 1);
            return;
        }
        if (path.contains(start)) return;
        path.add(start);
        List<IAtom> nbrs = atomContainer.getConnectedAtomsList(start);
        for (IAtom nbr : nbrs)
            findPathBetween(allPaths, atomContainer, nbr, end, path);
        path.remove(path.size() - 1);
    }

    /**
     * Get the paths starting from an atom of specified length.
     * 
     * This method returns a set of paths. Each path is a <code>List</code> of atoms that
     * make up the path (ie they are sequentially connected).
     *
     * @param atomContainer The molecule to consider
     * @param start         The starting atom
     * @param length        The length of paths to look for
     * @return A  <code>List</code> containing the paths found
     */
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

    /**
     * Get all the paths starting from an atom of length 0 upto the specified length.
     * 
     * This method returns a set of paths. Each path is a <code>List</code> of atoms that
     * make up the path (ie they are sequentially connected).
     *
     * @param atomContainer The molecule to consider
     * @param start         The starting atom
     * @param length        The maximum length of paths to look for
     * @return A  <code>List</code> containing the paths found
     */
    public static List<List<IAtom>> getPathsOfLengthUpto(IAtomContainer atomContainer, IAtom start, int length) {
        List<IAtom> curPath = new ArrayList<IAtom>();
        List<List<IAtom>> paths = new ArrayList<List<IAtom>>();
        List<List<IAtom>> allpaths = new ArrayList<List<IAtom>>();
        curPath.add(start);
        paths.add(curPath);
        allpaths.add(curPath);
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
            allpaths.addAll(tmpList);
        }
        return (allpaths);
    }

    /**
     * Get all the paths starting from an atom of length 0 up to the specified
     * length. If the number of paths exceeds the the set {@code limit} then an
     * exception is thrown.  This method returns a set of paths. Each path
     * is a <code>List</code> of atoms that make up the path (ie they are
     * sequentially connected).
     *
     * @param atomContainer The molecule to consider
     * @param start         The starting atom
     * @param length        The maximum length of paths to look for
     * @param limit         Limit the number of paths - thrown an exception if
     *                      exceeded
     * @return A  <code>List</code> containing the paths found
     * @throws CDKException throw if the number of paths generated was larger
     *                      than the limit.
     */
    public static List<List<IAtom>> getLimitedPathsOfLengthUpto(IAtomContainer atomContainer, IAtom start, int length,
            int limit) throws CDKException {
        List<IAtom> curPath = new ArrayList<IAtom>();
        List<List<IAtom>> paths = new ArrayList<List<IAtom>>();
        List<List<IAtom>> allpaths = new ArrayList<List<IAtom>>();
        curPath.add(start);
        paths.add(curPath);
        allpaths.add(curPath);
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
            if (allpaths.size() + tmpList.size() > limit)
                throw new CDKException(
                        "Too many paths generate. We're working making this faster but for now try generating paths with a smaller length");

            paths.clear();
            paths.addAll(tmpList);
            allpaths.addAll(tmpList);
        }
        return (allpaths);
    }
}
