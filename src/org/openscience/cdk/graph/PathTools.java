/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 2001-2006  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.graph;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Tools class with methods for handling molecular graphs.
 *
 * @author steinbeck
 * @cdk.module standard
 * @cdk.created 2001-06-17
 */
public class PathTools {

    public final static boolean debug = false;

    /**
     * Sums up the columns in a 2D int matrix
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
     * All-Pairs-Shortest-Path computation based on Floyds algorithm Takes an nxn
     * matrix C of edge costs and produces an nxn matrix A of lengths of shortest
     * paths.
     */
    public static int[][] computeFloydAPSP(int C[][]) {
        int i;
        int j;
        int k;
        int n = C.length;
        int[][] A = new int[n][n];
        //System.out.println("Matrix size: " + n);
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                if (C[i][j] == 0) {
                    A[i][j] = 999999999;
                } else {
                    A[i][j] = 1;
                }
            }
        }
        for (i = 0; i < n; i++) {
            A[i][i] = 0;
            // no self cycle
        }
        for (k = 0; k < n; k++) {
            for (i = 0; i < n; i++) {
                for (j = 0; j < n; j++) {
                    if (A[i][k] + A[k][j] < A[i][j]) {
                        A[i][j] = A[i][k] + A[k][j];
                        //P[i][j] = k;        // k is included in the shortest path
                    }
                }
            }
        }
        return A;
    }

    /**
     * All-Pairs-Shortest-Path computation based on Floyds algorithm Takes an nxn
     * matrix C of edge costs and produces an nxn matrix A of lengths of shortest
     * paths.
     */
    public static int[][] computeFloydAPSP(double C[][]) {
        int i;
        int j;
        int n = C.length;
        int[][] A = new int[n][n];
        //System.out.println("Matrix size: " + n);
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                if (C[i][j] == 0) {
                    A[i][j] = 0;
                } else {
                    A[i][j] = 1;
                }
            }
        }
        return computeFloydAPSP(A);
    }


    /**
     * Recursivly perfoms a depth first search in a molecular graphs contained in
     * the AtomContainer molecule, starting at the root atom and returning when it
     * hits the target atom.
     * CAUTION: This recursive method sets the VISITED flag of each atom
     * does not reset it after finishing the search. If you want to do the
     * operation on the same collection of atoms more than once, you have
     * to set all the VISITED flags to false before each operation
     * by looping of the atoms and doing a
     * "atom.setFlag((CDKConstants.VISITED, false));"
     *
     * @param molecule The
     *                 AtomContainer to be searched
     * @param root     The root atom
     *                 to start the search at
     * @param target   The target
     * @param path     An
     *                 AtomContainer to be filled with the path
     * @return true if the
     *         target atom was found during this function call
     */
    public static boolean depthFirstTargetSearch(IAtomContainer molecule, IAtom root, IAtom target, IAtomContainer path) throws NoSuchAtomException {
        IBond[] bonds = molecule.getConnectedBonds(root);
        IAtom nextAtom;
        root.setFlag(CDKConstants.VISITED, true);
        for (int f = 0; f < bonds.length; f++) {
            nextAtom = bonds[f].getConnectedAtom(root);
            if (!nextAtom.getFlag(CDKConstants.VISITED)) {
                path.addAtom(nextAtom);
                path.addBond(bonds[f]);
                if (nextAtom == target) {
                    return true;
                } else {
                    if (!depthFirstTargetSearch(molecule, nextAtom, target, path)) {
                        // we did not find the target
                        path.removeAtom(nextAtom);
                        path.removeElectronContainer(bonds[f]);
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
     * @param ac       The AtomContainer to be searched
     * @param sphere   A sphere of atoms to start the search with
     * @param molecule A molecule into which all the atoms and bonds are stored
     *                 that are found during search
     */
    public static void breadthFirstSearch(IAtomContainer ac, Vector sphere, IMolecule molecule) {
        // System.out.println("Staring partitioning with this ac: " + ac);
        breadthFirstSearch(ac, sphere, molecule, -1);
    }


    /**
     * Returns the atoms which are closest to an atom in an AtomContainer by bonds.
     * If number of atoms in or below sphere x&lt;max andnumber of atoms in or below sphere x+1&gt;max then atoms in or below sphere x+1 are returned.
     *
     * @param ac  The AtomContainer to examine
     * @param a   the atom to start from
     * @param max the number of neighbours to return
     * @return the average bond length
     */
    public static IAtom[] findClosestByBond(IAtomContainer ac, IAtom a, int max) {
        IMolecule mol = ac.getBuilder().newMolecule();
        Vector v = new Vector();
        v.add(a);
        breadthFirstSearch(ac, v, mol, max);
        IAtom[] returnValue = new IAtom[mol.getAtoms().length - 1];
        int k = 0;
        for (int i = 0; i < mol.getAtoms().length; i++) {
            if (mol.getAtoms()[i] != a) {
                returnValue[k] = mol.getAtoms()[i];
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
     * @param ac       The AtomContainer to be searched
     * @param sphere   A sphere of atoms to start the search with
     * @param molecule A molecule into which all the atoms and bonds are stored
     *                 that are found during search
     */
    public static void breadthFirstSearch(IAtomContainer ac, Vector sphere, IMolecule molecule, int max) {
        IAtom atom;
        IAtom nextAtom;
        Vector newSphere = new Vector();
        for (int f = 0; f < sphere.size(); f++) {
            atom = (IAtom) sphere.elementAt(f);
            //System.out.println("atoms  "+ atom + f);
            //System.out.println("sphere size  "+ sphere.size());
            molecule.addAtom(atom);
            // first copy LonePair's and SingleElectron's of this Atom as they need
            // to be copied too
            IElectronContainer[] eContainers = ac.getConnectedElectronContainers(atom);
            //System.out.println("found #ec's: " + eContainers.length);
            for (int i = 0; i < eContainers.length; i++) {
                if (!(eContainers[i] instanceof IBond)) {
                    // ok, no bond, thus LonePair or SingleElectron
                    // System.out.println("adding non bond " + eContainers[i]);
                    molecule.addElectronContainer(eContainers[i]);
                }
            }
            // now look at bonds
            org.openscience.cdk.interfaces.IBond[] bonds = ac.getConnectedBonds(atom);
            for (int g = 0; g < bonds.length; g++) {
                if (!bonds[g].getFlag(CDKConstants.VISITED)) {
                    molecule.addBond(bonds[g]);
                    bonds[g].setFlag(CDKConstants.VISITED, true);
                }
                nextAtom = bonds[g].getConnectedAtom(atom);
                if (!nextAtom.getFlag(CDKConstants.VISITED)) {
//					System.out.println("wie oft???");
                    newSphere.addElement(nextAtom);
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                }
            }
            if (max > -1 && molecule.getAtomCount() > max)
                return;
        }
        if (newSphere.size() > 0) {
            breadthFirstSearch(ac, newSphere, molecule, max);
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
     * @param ac         The AtomContainer in which the path search is to be performed.
     * @param sphere     The sphere of atoms to start with. Usually just the starting atom
     * @param target     The target atom to be searched
     * @param pathLength The current path length, incremented and passed in recursive calls. Call this method with "zero".
     * @param cutOff     Stop the path search when this cutOff sphere count has been reached
     * @return The shortest path between the starting sphere and the target atom
     */
    public static int breadthFirstTargetSearch(IAtomContainer ac, Vector sphere, IAtom target, int pathLength, int cutOff) {
        if (pathLength == 0) resetFlags(ac);
        pathLength++;
        if (pathLength > cutOff) {
            return -1;
        }
        IAtom atom;

        IAtom nextAtom;
        Vector newSphere = new Vector();
        for (int f = 0; f < sphere.size(); f++) {
            atom = (IAtom) sphere.elementAt(f);
            IBond[] bonds = ac.getConnectedBonds(atom);
            for (int g = 0; g < bonds.length; g++) {
                if (!bonds[g].getFlag(CDKConstants.VISITED)) {
                    bonds[g].setFlag(CDKConstants.VISITED, true);
                }
                nextAtom = bonds[g].getConnectedAtom(atom);
                if (!nextAtom.getFlag(CDKConstants.VISITED)) {
                    if (nextAtom == target) {
                        return pathLength;
                    }
                    newSphere.addElement(nextAtom);
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                }
            }
        }
        if (newSphere.size() > 0) {
            return breadthFirstTargetSearch(ac, newSphere, target, pathLength, cutOff);
        }
        return -1;
    }

    public static void resetFlags(IAtomContainer ac) {
        for (int f = 0; f < ac.getAtomCount(); f++) {
            ac.getAtomAt(f).setFlag(CDKConstants.VISITED, false);
        }
        for (int f = 0; f < ac.getElectronContainerCount(); f++) {
            ac.getElectronContainerAt(f).setFlag(CDKConstants.VISITED, false);
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
        for (int i = 0; i < eta.length; i++) {
            if (eta[i] < min) min = eta[i];
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
        for (int i = 0; i < eta.length; i++) {
            if (eta[i] > max) max = eta[i];
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
     *
     * This method uses the Djikstra algorithm to find all the atoms in the shortest
     * path between the two specified atoms. The start and end atoms are also included
     * in the path returned
     *
     * @param atomContainer The molecule to search in
     * @param start The starting atom
     * @param end The ending atom
     * @return A <code>List</code> containing the atoms in the shortest path between <code>start</code> and
     * <code>end</code> inclusive
     */
    public static List getShortestPath(IAtomContainer atomContainer, IAtom start, IAtom end) {
        int natom = atomContainer.getAtomCount();
        int endNumber = atomContainer.getAtomNumber(end);
        int startNumber = atomContainer.getAtomNumber(start);
        int[] d = new int[natom];
        int[] previous = new int[natom];
        for (int i = 0; i < natom; i++) {
            d[i] = 99999999;
            previous[i] = -1;
        }
        d[atomContainer.getAtomNumber(start)] = 0;

        ArrayList S = new ArrayList();
        ArrayList Q = new ArrayList();
        for (int i = 0; i < natom; i++) Q.add(new Integer(i));

        while (true) {
            if (Q.size() == 0) break;

            // extract min
            int u = 999999;
            int index = 0;
            for (int i = 0; i < Q.size(); i++) {
                int tmp = ((Integer)Q.get(i)).intValue();
                if (d[tmp] < u) {
                    u = d[tmp];
                    index = i;
                }
            }
            Q.remove(index);
            S.add(atomContainer.getAtomAt(u));
            if (u == endNumber) break;

            // relaxation
            IAtom[] connected = atomContainer.getConnectedAtoms( atomContainer.getAtomAt(u) );
            for (int i = 0; i < connected.length; i++) {
                int anum = atomContainer.getAtomNumber(connected[i]);
                if (d[anum] > d[u] + 1) { // all edges have equals weights
                    d[anum] = d[u] + 1;
                    previous[anum] = u;
                }
            }
        }

        ArrayList tmp = new ArrayList();
        int u = endNumber;
        while (true) {
            tmp.add(0, atomContainer.getAtomAt(u));
            u = previous[u];
            if (u == startNumber){
                tmp.add(0, atomContainer.getAtomAt(u));
                break;
            }
        }
        return tmp;
    }

    private static List allPaths;

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
    public static List getAllPaths(IAtomContainer atomContainer, IAtom start, IAtom end) {
        allPaths = new ArrayList();
        if (start.equals(end)) return allPaths;
        findPathBetween(atomContainer, start, end, new ArrayList());
        return allPaths;
    }

    private static void findPathBetween(IAtomContainer atomContainer, IAtom start, IAtom end, List path) {
        if (start == end) {
            path.add(start);
            allPaths.add(new ArrayList(path));
            path.remove(path.size() - 1);
            return;
        }
        if (path.contains(start))
            return;
        path.add(start);
        Vector nbrs = atomContainer.getConnectedAtomsVector(start);
        for (Iterator i = nbrs.iterator(); i.hasNext();)
            findPathBetween(atomContainer, (IAtom) i.next(), end, path);
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
    public static List getPathsOfLength(IAtomContainer atomContainer, IAtom start, int length) {
        ArrayList curPath = new ArrayList();
        ArrayList paths = new ArrayList();
        curPath.add(start);
        paths.add(curPath);
        for (int i = 0; i < length; i++) {
            ArrayList tmpList = new ArrayList();
            for (int j = 0; j < paths.size(); j++) {
                curPath = (ArrayList) paths.get(j);
                IAtom lastVertex = (IAtom) curPath.get(curPath.size() - 1);
                List neighbors = atomContainer.getConnectedAtomsVector(lastVertex);
                for (int k = 0; k < neighbors.size(); k++) {
                    ArrayList newPath = new ArrayList(curPath);
                    if (newPath.contains(neighbors.get(k))) continue;
                    newPath.add(neighbors.get(k));
                    tmpList.add(newPath);
                }
            }
            paths.clear();
            paths.addAll(tmpList);
        }
        return (paths);
    }


}

