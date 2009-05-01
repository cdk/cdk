/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.invariant.exception.BadMatrixFormatException;
import org.openscience.cdk.graph.invariant.exception.IndexOutOfBoundsException;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.LoggingTool;

import java.util.Iterator;

/**
 * Collection of methods for the calculation of topological indices of a 
 * molecular graph.
 *
 * @cdk.svnrev  $Revision$
 */
public class HuLuIndexTool
{
	private final static LoggingTool logger = new LoggingTool(HuLuIndexTool.class);
	
    /**
   * Calculates the extended adjacency matrix index.
   * An implementation of the algorithm published in {@cdk.cite HU96}.
   *
   * @cdk.keyword EAID number
   */
    public static double getEAIDNumber(AtomContainer atomContainer) throws NoSuchAtomException,
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

        logger.debug("final matrix - the sum of the powers of EA matrix: ");
        displayMatrix(matrix.getArrayValue());
        logger.debug("eaid number: "+ eaid);

        return eaid;
    }


    public static double[][] getExtendedAdjacenyMatrix(AtomContainer atomContainer)
        throws NoSuchAtomException
    {
        boolean debug = false;
        double[][] adjaMatrix = ConnectionMatrix.getMatrix(atomContainer);

        logger.debug("adjacency matrix: ");
        displayMatrix(adjaMatrix);

        double[] atomWeights = getAtomWeights(atomContainer);


        for (int i = 0; i < adjaMatrix.length; i++)
        {
            for (int j = 0; j < adjaMatrix.length; j++)
            {
                if (i==j)
                {
                    if (atomContainer.getAtom(i).getSymbol()=="O")
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

        logger.debug("extended adjacency matrix: ");
        displayMatrix(adjaMatrix);

        return adjaMatrix;
    }

    public static double[] getAtomWeights(AtomContainer atomContainer) throws NoSuchAtomException
    {
        boolean debug = false;
        IAtom atom,headAtom,endAtom;        
        int headAtomPosition,endAtomPosition;

        //int k = 0;
        double[] weightArray = new double[atomContainer.getAtomCount()];
        double[][] adjaMatrix = ConnectionMatrix.getMatrix(atomContainer);

        int[][] apspMatrix = PathTools.computeFloydAPSP(adjaMatrix);
        int[] atomLayers = getAtomLayers(apspMatrix);

        int[] valenceSum;
        int[] interLayerBondSum;

        logger.debug("adjacency matrix: ");
        displayMatrix(adjaMatrix);
        logger.debug("all-pairs-shortest-path matrix: ");
        displayMatrix(apspMatrix);
        logger.debug("atom layers: ");
        displayArray(atomLayers);

        for (int i = 0; i < atomContainer.getAtomCount(); i++)
        {
            atom = atomContainer.getAtom(i);

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
                if(atomContainer.getAtom(j).getSymbol()=="O")
                    valenceSum[apspMatrix[j][i]] += 6 - atomContainer.getAtom(j).getHydrogenCount();
                else
                    valenceSum[apspMatrix[j][i]] += 4 - atomContainer.getAtom(j).getHydrogenCount();
            }

            Iterator bonds = atomContainer.bonds().iterator();
            while (bonds.hasNext()) {
                IBond bond = (IBond) bonds.next();

                headAtom = bond.getAtom(0);
                endAtom = bond.getAtom(1);


                headAtomPosition = atomContainer.getAtomNumber(headAtom);
                endAtomPosition = atomContainer.getAtomNumber(endAtom);




                if (Math.abs(apspMatrix[i][headAtomPosition] - apspMatrix[i][endAtomPosition]) == 1)
                {
                    int min = Math.min(apspMatrix[i][headAtomPosition],apspMatrix[i][endAtomPosition]);
                    if (bond.getOrder() == IBond.Order.SINGLE) {
                    	interLayerBondSum[min] += 1;
                    } else if (bond.getOrder() == IBond.Order.DOUBLE) {
                    	interLayerBondSum[min] += 2;
                    } else if (bond.getOrder() == IBond.Order.TRIPLE) {
                    	interLayerBondSum[min] += 3;
                    } else if (bond.getOrder() == IBond.Order.QUADRUPLE) {
                    	interLayerBondSum[min] += 4;
                    }
                }
            }


            for (int j = 0; j < interLayerBondSum.length; j++)
            {
                weightArray[i] += interLayerBondSum[j] * valenceSum[j+1] * Math.pow(10, -(j+1));
            }

            logger.debug("valence sum: ");
            displayArray(valenceSum);
            logger.debug("inter-layer bond sum: ");
            displayArray(interLayerBondSum);
        }

        logger.debug("weight array: ");
        displayArray(weightArray);

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
            logger.debug(line);
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
            logger.debug(line);
        }
    }

    /** Lists a 1D array to the System console */
    public static void displayArray(int[] array){
        String line  = "";
        for (int f = 0; f < array.length; f++)
        {
            line += array[f] + " | ";
        }
        logger.debug(line);
    }

    /** Lists a 1D array to the System console */
    public static void displayArray(double[] array){
        String line  = "";
        for (int f = 0; f < array.length; f++)
        {
            line += array[f] + " | ";
        }
        logger.debug(line);
    }

}  
