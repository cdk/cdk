package org.openscience.cdk.structgen.stochastic;

import gacdk.*;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import java.lang.Math.*;

/* @author     Yongquan Han 
 * @Copyright (c) 2000-2001 SENECA Project, MPI of Chemical Ecology, Jena.
 * All Rights Reserved.
 */
public class ConvertTools
{
	/** Constructs the connection table from a chromosome */
	public static int[][] createConnectionTable(AtomContainer chrom)
	{
		int dim =chrom.getAtomCount();
		int contab[][] = new int[dim][dim];
		
		for (int j = 0; j < dim; j++){
			for (int i = 0; i < dim; i++)
				contab[i][j] = 0;
		}		
		
		for (int f = 0; f < chrom.getBondCount(); f++)
		{
			Bond bond = chrom.getBondAt(f);
			try
			{
				int i = chrom.getAtomNumber(bond.getAtomAt(0));
				int j = chrom.getAtomNumber(bond.getAtomAt(1));
				contab[i][j] = (int)bond.getOrder();
				contab[j][i] = (int)bond.getOrder();
			}
			catch(NoSuchAtomException e){}			
		}
		return contab;
	}	

	/** create the adjacency matrix of a bondMatrix */
	public static int[][] getAdjacMatrix(int[][] initMatrix)
	{
		int[][] aM = new int[initMatrix.length][initMatrix.length];
		
		for (int j=0; j < initMatrix.length; j++){
			for (int i=0;i < initMatrix.length; i++){
				if (initMatrix[i][j] > 0) aM[i][j] = 1;
				else aM[i][j] = 0;   			
			}
		}
		return aM;
	}
	
	/**
	 * Analog of Math.max that returns the largest int value in an array of ints
	 *
	 * @param   values  the values to be searched for the largest value among them
	 * @return   the largest value among a set of given values  
	 */
	public static double max(double[] values)
	{
		double max = values[0];
		for (int f = 0; f < values.length; f++)
			if (values[f] > max)
				max = values[f];
			return max;
	}

	/**
	 * Analog of Math.min that returns the largest int value in an array of ints
	 *
	 * @param   values  the values to be searched for the smallest value among them
	 * @return   the smallest value among a set of given values  
	 */
	public static double min(double[] values)
	{
		double min = values[0];
		for (int f = 0; f < values.length; f++)
			if (values[f] < min)
				min = values[f];
			return min;
	}		
}




