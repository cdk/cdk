/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.graph;

import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;

/**
 * Tools class with methods for handling molecular graphs.
 *
 * @cdk.module standard
 *
 * @author     steinbeck
 * @cdk.created    2001-06-17
 */
public class PathTools  {
	
    public static boolean debug = false;

	/**
	 *  Sums up the columns in a 2D int matrix
	 *
	 *@param  apsp  The 2D int matrix
	 *@return       A 1D matrix containing the column sum of the 2D matrix
	 */
	public static int[] getInt2DColumnSum(int[][] apsp) {
		int[] colSum = new int[apsp.length];
		int sum = 0;
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
	 *  All-Pairs-Shortest-Path computation based on Floyds algorithm Takes an nxn
	 *  matrix C of edge costs and produces an nxn matrix A of lengths of shortest
	 *  paths.
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
				}
				else {
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
	 *  All-Pairs-Shortest-Path computation based on Floyds algorithm Takes an nxn
	 *  matrix C of edge costs and produces an nxn matrix A of lengths of shortest
	 *  paths.
	 */
	public static int[][] computeFloydAPSP(double C[][]) {
		int i;
		int j;
		int k;
		int n = C.length;
		int[][] A = new int[n][n];
		//System.out.println("Matrix size: " + n);
		for (i = 0; i < n; i++) {
			for (j = 0; j < n; j++) {
				if (C[i][j] == 0) {
					A[i][j] = 0;
				}
				else {
					A[i][j] = 1;
				}
			}
		}
		return computeFloydAPSP(A);
	}

	
	/**
	 *  Recursivly perfoms a depth first search in a molecular graphs contained in
	 *  the AtomContainer molecule, starting at the root atom and returning when it
	 *  hits the target atom.
	 *
	 *@param  molecule                                               The
	 *      AtomContainer to be searched
	 *@param  root                                                   The root atom
	 *      to start the search at
	 *@param  target                                                 The target
	 *@param  path                                                   An
	 *      AtomContainer to be filled with the path
	 *@return                                                        true if the
	 *      target atom was found during this function call
	 */
	public static boolean depthFirstTargetSearch(AtomContainer molecule, Atom root, Atom target, AtomContainer path) throws org.openscience.cdk.exception.NoSuchAtomException {
		Bond[] bonds = molecule.getConnectedBonds(root);
		Atom nextAtom = null;
		root.setFlag(CDKConstants.VISITED, true);
		for (int f = 0; f < bonds.length; f++) {
			nextAtom = bonds[f].getConnectedAtom(root);
			if (!nextAtom.getFlag(CDKConstants.VISITED)) {
				path.addAtom(nextAtom);
				path.addBond(bonds[f]);
				if (nextAtom == target) {
					return true;
				}
				else {
					if (!depthFirstTargetSearch(molecule, nextAtom, target, path)) {
						// we did not find the target
						path.removeAtom(nextAtom);
						path.removeElectronContainer(bonds[f]);
					}
					else {
						return true;
					}
				}
			}
		}
		return false;
	}


	/**
	 *  Performs a breadthFirstSearch in an AtomContainer starting with a
	 *  particular sphere, which usually consists of one start atom. While
	 *  searching the graph, the method marks each visited atom. It then puts all
	 *  the atoms connected to the atoms in the given sphere into a new vector
	 *  which forms the sphere to search for the next recursive method call. All
	 *  atoms that have been visited are put into a molecule container. This
	 *  breadthFirstSearch does thus find the connected graph for a given start
	 *  atom.
	 *
	 *@param  ac        The AtomContainer to be searched
	 *@param  sphere    A sphere of atoms to start the search with
	 *@param  molecule  A molecule into which all the atoms and bonds are stored
	 *      that are found during search
	 */
	public static void breadthFirstSearch(AtomContainer ac, Vector sphere, Molecule molecule) {
		Atom atom = null;
		Atom nextAtom = null;
		Vector newSphere = new Vector();
		for (int f = 0; f < sphere.size(); f++) {
			atom = (Atom) sphere.elementAt(f);
//			System.out.println("atoms  "+ atom + f);
//			System.out.println("sphere size  "+ sphere.size());
			molecule.addAtom(atom);
			Bond[] bonds = ac.getConnectedBonds(atom);
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
		}
		if (newSphere.size() > 0) {
			breadthFirstSearch(ac, newSphere, molecule);
		}
	}



	/**
	 *  Performs a breadthFirstTargetSearch in an AtomContainer starting with a
	 *  particular sphere, which usually consists of one start atom. While
	 *  searching the graph, the method marks each visited atom. It then puts all
	 *  the atoms connected to the atoms in the given sphere into a new vector
	 *  which forms the sphere to search for the next recursive method call.
	 *  The method keeps track of the sphere count and returns it as soon
	 *  as the target atom is encountered. 
	 *
	 *@param  ac          The AtomContainer in which the path search is to be performed. 
	 *@param  sphere      The sphere of atoms to start with. Usually just the starting atom
	 *@param  target      The target atom to be searched
	 *@param  pathLength  The current path length, incremented and passed in recursive calls. Call this method with "zero".
	 *@param  cutOff      Stop the path search when this cutOff sphere count has been reached
	 *@return             The shortest path between the starting sphere and the target atom
	 */
	public static int breadthFirstTargetSearch(AtomContainer ac, Vector sphere, Atom target, int pathLength, int cutOff) {
		if (pathLength == 0) resetFlags(ac);
		pathLength++;
		if (pathLength > cutOff) {
			return -1;
		}
		Atom atom = null;

		Atom nextAtom = null;
		Vector newSphere = new Vector();
		for (int f = 0; f < sphere.size(); f++) {
			atom = (Atom) sphere.elementAt(f);
			Bond[] bonds = ac.getConnectedBonds(atom);
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
	
	public static void resetFlags(AtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			ac.getAtomAt(f).setFlag(CDKConstants.VISITED, false);
		}
		for (int f = 0; f < ac.getElectronContainerCount(); f++)
		{
			ac.getElectronContainerAt(f).setFlag(CDKConstants.VISITED, false);
		}
		
	}
}
