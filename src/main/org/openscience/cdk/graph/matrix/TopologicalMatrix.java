package org.openscience.cdk.graph.matrix;

import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;

/**
 * Calculator for a topological matrix representation of this AtomContainer. An
 * topological matrix is a matrix of quare NxN matrix, where N is the number of
 * atoms in the AtomContainer. The element i,j of the matrix is the distance between
 * two atoms in a molecule.
 * 
 * @author federico
 * @cdk.svnrev  $Revision$
 *
 */

public class TopologicalMatrix implements IGraphMatrix {
	
	/**
	 * Returns the topological matrix for the given AtomContainer.
	 *
     * @param  container The AtomContainer for which the matrix is calculated
	 * @return           A topological matrix representating this AtomContainer
	 */
	
	public static int[][] getMatrix(IAtomContainer container) {
			int[][]conMat = AdjacencyMatrix.getMatrix(container);
			int[][]TopolDistance = PathTools.computeFloydAPSP(conMat);
		
	return TopolDistance;
		}
	}