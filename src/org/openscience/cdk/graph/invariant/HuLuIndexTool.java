/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.graph.invariant;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.invariant.exception.BadMatrixFormatException;
import org.openscience.cdk.graph.invariant.exception.IndexOutOfBoundsException;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;

/**
 * Collection of methods for the calculation of topological indices of a 
 * molecular graph.
 */
public class HuLuIndexTool 
{
	/**
   * Calculates the extended adjacency matrix index.
   * An implementation of the algorithm published in {@cdk.cite HU96}.
   *
   * @cdk.keyword EAID number
   */
	public static double getEAIDNumber(AtomContainer atomContainer) throws org.openscience.cdk.exception.NoSuchAtomException,
			BadMatrixFormatException,IndexOutOfBoundsException
	{
		boolean debug = false;
		
		GIMatrix matrix = new GIMatrix(getExtendedAdjacenyMatrix(atomContainer));
		
		GIMatrix tempMatrix = matrix;
		GIMatrix fixedMatrix = matrix;
		for (int i = 2; i < atomContainer.getAtomCount(); i++)
		{
			tempMatrix = tempMatrix.multiply(fixedMatrix);
			matrix = matrix.add(tempMatrix);			
		}
		
		for (int i = 0; i < atomContainer.getAtomCount(); i++)
		{
			matrix.setValueAt(i,i,matrix.getValueAt(i,i)+1);		
		}
		double eaid = matrix.trace();
		
		if (debug)
		{
			System.out.println();
			System.out.println("final matrix - the sum of the powers of EA matrix: ");
			System.out.println();
			displayMatrix(matrix.getArrayValue());
			System.out.println();
			System.out.println("eaid number: "+ eaid);
			System.out.println();
		}

		return eaid;
	}


	public static double[][] getExtendedAdjacenyMatrix(AtomContainer atomContainer) 
        throws org.openscience.cdk.exception.NoSuchAtomException
	{
		boolean debug = false;
		double[][] adjaMatrix = ConnectionMatrix.getMatrix(atomContainer);
		if (debug)
		{
			System.out.println("adjacency matrix: ");
			System.out.println();
			displayMatrix(adjaMatrix);
			System.out.println();
		}
	
		double[] atomWeights = getAtomWeights(atomContainer);

		
		for (int i = 0; i < adjaMatrix.length; i++)
		{
			for (int j = 0; j < adjaMatrix.length; j++)
			{
				if (i==j)
				{
					if (atomContainer.getAtomAt(i).getSymbol()=="O")
					{
						adjaMatrix[i][j] = Math.sqrt(0.74)/6;						
					}
					else
					{
						adjaMatrix[i][j] = Math.sqrt(0.74)/6;
					}					
				}
				else
				{
					adjaMatrix[i][j] = (Math.sqrt(atomWeights[i]/atomWeights[j]) + Math.sqrt(atomWeights[j]/atomWeights[i])) * Math.sqrt(adjaMatrix[i][j])/6;					
				}			
			}
		}
		
		if (debug) 
		{
			System.out.println("extended adjacency matrix: ");
			System.out.println();
			displayMatrix(adjaMatrix);
			System.out.println();
		}
		return adjaMatrix;
	}
	
	public static double[] getAtomWeights(AtomContainer atomContainer) throws org.openscience.cdk.exception.NoSuchAtomException
	{
		boolean debug = false;
		org.openscience.cdk.interfaces.IAtom atom,headAtom,endAtom;
		org.openscience.cdk.interfaces.IBond bond;
		int headAtomPosition,endAtomPosition;

		int k = 0;
		double[] weightArray = new double[atomContainer.getAtomCount()];
		double[][] adjaMatrix = ConnectionMatrix.getMatrix(atomContainer);
		
		int[][] apspMatrix = PathTools.computeFloydAPSP(adjaMatrix);		
		int[] atomLayers = getAtomLayers(apspMatrix);
		
		int[] valenceSum;
		int[] interLayerBondSum;
		
		if (debug) 
		{
			System.out.println("adjacency matrix: ");
			System.out.println();
			displayMatrix(adjaMatrix);
			System.out.println();
			System.out.println("all-pairs-shortest-path matrix: ");
			System.out.println();
			displayMatrix(apspMatrix);
			System.out.println();
			System.out.println("atom layers: ");
			displayArray(atomLayers);
			System.out.println();
		}
		
		for (int i = 0; i < atomContainer.getAtomCount(); i++)
		{
			atom = atomContainer.getAtomAt(i);
			
			valenceSum = new int[atomLayers[i]];			
			for (int v = 0; v < valenceSum.length; v++)
			{
				valenceSum[v] = 0;	
			}
			
			interLayerBondSum = new int[atomLayers[i]-1];
			for (int v = 0; v < interLayerBondSum.length; v++)
			{
				interLayerBondSum[v] = 0;			
			}
			
			
			//weightArray[k] = atom.getValenceElectronsCount() - atom.getHydrogenCount(); // method unfinished
			if(atom.getSymbol()=="O")
				weightArray[i] = 6 - atom.getHydrogenCount();
			else
				weightArray[i] = 4 - atom.getHydrogenCount();			



			for (int j = 0; j < apspMatrix.length; j++)
			{
				if(atomContainer.getAtomAt(j).getSymbol()=="O")
					valenceSum[apspMatrix[j][i]] += 6 - atomContainer.getAtomAt(j).getHydrogenCount();
				else
					valenceSum[apspMatrix[j][i]] += 4 - atomContainer.getAtomAt(j).getHydrogenCount();					
			}
			org.openscience.cdk.interfaces.IBond[] bonds = atomContainer.getBonds();
			for (int j = 0; j < bonds.length; j++)
			{
				bond = bonds[j];
				headAtom = bond.getAtomAt(0);
				endAtom = bond.getAtomAt(1);
				
				
				headAtomPosition = atomContainer.getAtomNumber(headAtom);
				endAtomPosition = atomContainer.getAtomNumber(endAtom);
				
				


				if (Math.abs(apspMatrix[i][headAtomPosition] - apspMatrix[i][endAtomPosition]) == 1)
				{
					int min = Math.min(apspMatrix[i][headAtomPosition],apspMatrix[i][endAtomPosition]);
					interLayerBondSum[min] += bond.getOrder();
				}
			}
			
			
			for (int j = 0; j < interLayerBondSum.length; j++)
			{
				weightArray[i] += interLayerBondSum[j] * valenceSum[j+1] * Math.pow(10, -(j+1));
			}
			
			if (debug) 
			{
				System.out.println("valence sum: ");
				displayArray(valenceSum);
				System.out.println();
				System.out.println("inter-layer bond sum: ");
				displayArray(interLayerBondSum);
				System.out.println();
			}			
		}
		
		if (debug) 
		{
			System.out.println("weight array: ");
			System.out.println();
			displayArray(weightArray);
			System.out.println();
		}
		return weightArray;
	}

	public static int[] getAtomLayers(int[][]apspMatrix)
	{
		int[] atomLayers  = new int[apspMatrix.length];
		for(int i = 0; i < apspMatrix.length; i++)
		{
			atomLayers[i] = 0;
			for(int j = 0; j < apspMatrix.length; j++)
			{
				if(atomLayers[i] < 1+ apspMatrix[j][i] )
					atomLayers[i] = 1+ apspMatrix[j][i]; 			
			}
			
		}
		return atomLayers;
	}

	/** Lists a 2D double matrix to the System console */
	public static void displayMatrix(double[][] matrix){
		String line;
		for (int f = 0; f < matrix.length; f++)
		{
			line  = "";
			for (int g = 0; g < matrix.length; g++)
			{
				line += matrix[g][f] + " | ";
			}
			System.out.println(line);
		}
	}
	
	/** Lists a 2D int matrix to the System console */
	public static void displayMatrix(int[][] matrix){
		String line;
		for (int f = 0; f < matrix.length; f++)
		{
			line  = "";
			for (int g = 0; g < matrix.length; g++)
			{
				line += matrix[g][f] + " | ";
			}
			System.out.println(line);
		}
	}
	
	/** Lists a 1D array to the System console */
	public static void displayArray(int[] array){
		String line  = "";
		for (int f = 0; f < array.length; f++)
		{
			line += array[f] + " | ";			
		}
		System.out.println(line);
	}
	
	/** Lists a 1D array to the System console */
	public static void displayArray(double[] array){
		String line  = "";
		for (int f = 0; f < array.length; f++)
		{
			line += array[f] + " | ";			
		}
		System.out.println(line);
	}	

}  
