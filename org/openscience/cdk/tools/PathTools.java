/* PathTools.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The JChemPaint project
 * 
 * Contact: steinbeck@ice.mpg.de
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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

package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import java.util.*;
import java.io.*;

public class PathTools implements CDKConstants
{
	 public static boolean  debug = false; // minimum details

	/**
	 * All-Pairs-Shortest-Path computation based on Floyds algorithm
	 * Takes an nxn matrix C of edge costs and produces
	* an nxn matrix A of lengths of shortest paths.
	*
	 * @param   A  nxn matrix A of lengths of shortest paths
	 */
	public static int[][] computeFloydAPSP(int C[][])
	{
		int i,j,k;
		int n = C.length;
		int[][] A = new int[n][n]; 
		//System.out.println("Matrix size: " + n);
		for (i=0; i<n; i++)
		{
			for (j=0; j<n; j++)
			{
				if (C[i][j] == 0)
				{
				 A[i][j] = 999999999;
				}
				else
				{
				 A[i][j] = 1;
				}
			}
		}
		for (i=0; i<n; i++)
		{
			A[i][i] = 0;              // no self cycle
		}
		for (k=0; k<n; k++)
		{
			for (i=0; i<n; i++)
			{
				for (j=0; j<n; j++)
				{
					if (A[i][k]+A[k][j] < A[i][j])
					{
						A[i][j] = A[i][k] + A[k][j];
						//P[i][j] = k;        // k is included in the shortest path
					}
				}
			}
		}
		return A;
	}


	/**
	 * Sums up the columns in a 2D int matrix
	 *
	 * @param   apsp  The 2D int matrix	
	 * @return  A 1D matrix containing the column sum of the 2D matrix    
	 */
	public static int[] getInt2DColumnSum(int[][]apsp)
	{
		int[] colSum = new int[apsp.length];
		int sum = 0;
		for (int i = 0; i < apsp.length; i++)
		{
			sum = 0;
			for (int j = 0; j < apsp.length; j++)
			{
				sum += apsp[i][j];
			}
			colSum[i] = sum;
		}
		return colSum;
	}
	 
	 

	/**
	 * Recursivly perfoms a depth first search in a molecular graphs contained in the AtomContainer molecule, 
	 * starting at the root atom and returning when it hits the target atom.
	 *
	 * @param   molecule  The AtomContainer to be searched 
	 * @param   root  The root atom to start the search at
	 * @param   target  The target
	 * @param   path  
	 * @return     
	 */
	public static boolean depthFirstSearch(AtomContainer molecule, Atom root, Atom target, AtomContainer path) throws java.lang.Exception
	{	
		Bond[] bonds = molecule.getConnectedBonds(root);
		Atom nextAtom = null;
		root.flags[VISITED] = true;
		for(int f = 0; f < bonds.length; f++)
		{
			nextAtom = bonds[f].getConnectedAtom(root);
			if (!nextAtom.flags[VISITED])
			{
				path.addAtom(nextAtom);
				path.addBond(bonds[f]);
				if (nextAtom == target)
				{
					return true;
				}
				else
				{
					depthFirstSearch(molecule, nextAtom, target, path);
				}
				if (!path.contains(target))
				{
					path.removeAtom(nextAtom);
					path.removeBond(bonds[f]);
				}	
			}
		}
		return false;
	}

}








