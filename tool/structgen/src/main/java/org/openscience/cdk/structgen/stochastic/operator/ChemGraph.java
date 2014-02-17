/*  $Revision$ $Author$ $Date$    
 *
 *  Copyright (C) 1997-2007  The CDK project
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
 */
package org.openscience.cdk.structgen.stochastic.operator;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.math.RandomNumbersTool;

/**
 * @cdk.module     structgen
 * @cdk.githash
 */
public class ChemGraph
{
	/*Number of atoms in this structure*/
	protected int dim;
	/*Number of atoms needed to form subgraph*/
	protected int numAtoms;
	protected double[][] contab;
	/*Number of atoms that have been traversed */
	protected int travIndex;
	/*Flag: true if atom visited during a traversal*/
	protected boolean[] visited;
	/*Depth first traversal of the graph*/
	protected List<Integer> subGraph;
		
	public ChemGraph(IAtomContainer chrom)
	{
		dim = chrom.getAtomCount();
		numAtoms = (int)(dim/2);
		contab = new double[dim][dim];
		contab = ConnectionMatrix.getMatrix(chrom);
	}
	
	public List<Integer> pickDFgraph()
	{
		//depth first search from a randomly selected atom
		
		travIndex = 0;
		subGraph = new ArrayList<Integer>();		
		visited = new boolean[dim];			 		
		for (int atom = 0; atom < dim; atom++)	visited[atom] = false;
        int seedAtom = RandomNumbersTool.randomInt(0,dim-1);
		recursiveDFT(seedAtom);
	
		return subGraph;
	}

	private void recursiveDFT(int atom)
	{
		if ((travIndex < numAtoms)&&(!visited[atom]))
		{
			subGraph.add(Integer.valueOf(atom));
			travIndex++;
			visited[atom] = true;
			
//			for (int nextAtom = 0; nextAtom < dim; nextAtom++) //not generalized
//				if (contab[atom][nextAtom] != 0) recursiveDFT(nextAtom);
            List<Integer> adjSet = new ArrayList<Integer>();
            for (int nextAtom = 0; nextAtom < dim; nextAtom++)
            {
				if ((int)contab[atom][nextAtom] != 0)
				{
					adjSet.add(Integer.valueOf(nextAtom));
				}
            }
			while (adjSet.size() > 0)
			{
				int adjIndex = RandomNumbersTool.randomInt(0,adjSet.size()-1);
				recursiveDFT(((Integer)adjSet.get(adjIndex)).intValue());
				adjSet.remove(adjIndex);
			}
			
		}
	}
	
	public List<Integer> pickBFgraph()
	{
		//breadth first search from a randomly selected atom
		
		travIndex = 0;
		subGraph = new ArrayList<Integer>();		
		visited = new boolean[dim];			 		
		for (int atom = 0; atom < dim; atom++)	visited[atom] = false;
        int seedAtom = RandomNumbersTool.randomInt(0,dim-1);
		
		List<Integer> atomQueue = new ArrayList<Integer>();
		atomQueue.add(Integer.valueOf(seedAtom));
		visited[seedAtom] = true;		
		
		while (!atomQueue.isEmpty()&&(subGraph.size()<numAtoms))
		{
			int foreAtom = ((Integer)atomQueue.get(0)).intValue();
			subGraph.add(Integer.valueOf(foreAtom));
			atomQueue.remove(0);
			travIndex++;
			
			List<Integer> adjSet = new ArrayList<Integer>();
            for (int nextAtom = 0; nextAtom < dim; nextAtom++)
            {
				if (((int)contab[foreAtom][nextAtom] != 0)&&(!visited[nextAtom]))
				{
					adjSet.add(Integer.valueOf(nextAtom));
				}
            }
			while (adjSet.size() > 0)
			{
				int adjIndex = RandomNumbersTool.randomInt(0,adjSet.size()-1);
				atomQueue.add((Integer)adjSet.get(adjIndex));
				visited[((Integer)adjSet.get(adjIndex)).intValue()] = true;
				adjSet.remove(adjIndex);
			}

		}
		return subGraph;	
	}
	
	public List<Integer> getSubgraph()
	{
		return subGraph;
	}
	
	public void setSubgraph(List<Integer> subgraph)
	{
		subGraph = subgraph;
	}
	
	public int getNumAtoms()
	{
		return numAtoms;
	}
	
	public void setNumAtoms(int numatoms)
	{
		numAtoms = numatoms;
	}
}
