/* 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
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

import java.util.Iterator;

import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.invariant.exception.BadMatrixFormatException;
import org.openscience.cdk.graph.invariant.exception.IndexOutOfBoundsException;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Collection of methods for the calculation of topological indices of a
 * molecular graph.
 *
 * @cdk.githash
 */
public class HuLuIndexTool {

    private final static ILoggingTool logger = LoggingToolFactory.createLoggingTool(HuLuIndexTool.class);

    /**
    * Calculates the extended adjacency matrix index.
    * An implementation of the algorithm published in {@cdk.cite HU96}.
    *
    * @cdk.keyword EAID number
    */
    public static double getEAIDNumber(IAtomContainer atomContainer) throws NoSuchAtomException,
                                                                            BadMatrixFormatException, IndexOutOfBoundsException {
        GIMatrix matrix = new GIMatrix(getExtendedAdjacenyMatrix(atomContainer));

        GIMatrix tempMatrix = matrix;
        GIMatrix fixedMatrix = matrix;
        for (int i = 2; i < atomContainer.getAtomCount(); i++) {
            tempMatrix = tempMatrix.multiply(fixedMatrix);
            matrix = matrix.add(tempMatrix);
        }

        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            matrix.setValueAt(i, i, matrix.getValueAt(i, i) + 1);
        }
        double eaid = matrix.trace();

        logger.debug("final matrix - the sum of the powers of EA matrix: ");
        displayMatrix(matrix.getArrayValue());
        logger.debug("eaid number: " + eaid);

        return eaid;
    }

    public static double[][] getExtendedAdjacenyMatrix(IAtomContainer atomContainer) throws NoSuchAtomException {
        double[][] adjaMatrix = ConnectionMatrix.getMatrix(atomContainer);

        logger.debug("adjacency matrix: ");
        displayMatrix(adjaMatrix);

        double[] atomWeights = getAtomWeights(atomContainer);

        for (int i = 0; i < adjaMatrix.length; i++) {
            for (int j = 0; j < adjaMatrix.length; j++) {
                if (i == j) {
                    if ("O".equals(atomContainer.getAtom(i).getSymbol())) {
                        adjaMatrix[i][j] = Math.sqrt(0.74) / 6;
                    } else {
                        adjaMatrix[i][j] = Math.sqrt(0.74) / 6;
                    }
                } else {
                    adjaMatrix[i][j] = (Math.sqrt(atomWeights[i] / atomWeights[j]) + Math.sqrt(atomWeights[j]
                            / atomWeights[i]))
                            * Math.sqrt(adjaMatrix[i][j]) / 6;
                }
            }
        }

        logger.debug("extended adjacency matrix: ");
        displayMatrix(adjaMatrix);

        return adjaMatrix;
    }

    public static double[] getAtomWeights(IAtomContainer atomContainer) throws NoSuchAtomException {
        IAtom atom, headAtom, endAtom;
        int headAtomPosition, endAtomPosition;

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

        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            atom = atomContainer.getAtom(i);

            valenceSum = new int[atomLayers[i]];
            for (int v = 0; v < valenceSum.length; v++) {
                valenceSum[v] = 0;
            }

            interLayerBondSum = new int[atomLayers[i] - 1];
            for (int v = 0; v < interLayerBondSum.length; v++) {
                interLayerBondSum[v] = 0;
            }

            //weightArray[k] = atom.getValenceElectronsCount() - atom.getHydrogenCount(); // method unfinished
            if ("O".equals(atom.getSymbol()))
                weightArray[i] = 6 - atom.getImplicitHydrogenCount();
            else
                weightArray[i] = 4 - atom.getImplicitHydrogenCount();

            for (int j = 0; j < apspMatrix.length; j++) {
                if ("O".equals(atomContainer.getAtom(j).getSymbol()))
                    valenceSum[apspMatrix[j][i]] += 6 - atomContainer.getAtom(j).getImplicitHydrogenCount();
                else
                    valenceSum[apspMatrix[j][i]] += 4 - atomContainer.getAtom(j).getImplicitHydrogenCount();
            }

            Iterator<IBond> bonds = atomContainer.bonds().iterator();
            while (bonds.hasNext()) {
                IBond bond = bonds.next();

                headAtom = bond.getBegin();
                endAtom = bond.getEnd();

                headAtomPosition = atomContainer.indexOf(headAtom);
                endAtomPosition = atomContainer.indexOf(endAtom);

                if (Math.abs(apspMatrix[i][headAtomPosition] - apspMatrix[i][endAtomPosition]) == 1) {
                    int min = Math.min(apspMatrix[i][headAtomPosition], apspMatrix[i][endAtomPosition]);
                    IBond.Order order = bond.getOrder();
                    interLayerBondSum[min] += order == null ? 0 : order.numeric();
                }
            }

            for (int j = 0; j < interLayerBondSum.length; j++) {
                weightArray[i] += interLayerBondSum[j] * valenceSum[j + 1] * Math.pow(10, -(j + 1));
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

    public static int[] getAtomLayers(int[][] apspMatrix) {
        int[] atomLayers = new int[apspMatrix.length];
        for (int i = 0; i < apspMatrix.length; i++) {
            atomLayers[i] = 0;
            for (int j = 0; j < apspMatrix.length; j++) {
                if (atomLayers[i] < 1 + apspMatrix[j][i]) atomLayers[i] = 1 + apspMatrix[j][i];
            }

        }
        return atomLayers;
    }

    /** Lists a 2D double matrix to the System console. */
    public static void displayMatrix(double[][] matrix) {
        String line;
        for (int f = 0; f < matrix.length; f++) {
            line = "";
            for (int g = 0; g < matrix.length; g++) {
                line += matrix[g][f] + " | ";
            }
            logger.debug(line);
        }
    }

    /** Lists a 2D int matrix to the System console. */
    public static void displayMatrix(int[][] matrix) {
        String line;
        for (int f = 0; f < matrix.length; f++) {
            line = "";
            for (int g = 0; g < matrix.length; g++) {
                line += matrix[g][f] + " | ";
            }
            logger.debug(line);
        }
    }

    /** Lists a 1D array to the System console. */
    public static void displayArray(int[] array) {
        String line = "";
        for (int f = 0; f < array.length; f++) {
            line += array[f] + " | ";
        }
        logger.debug(line);
    }

    /** Lists a 1D array to the System console. */
    public static void displayArray(double[] array) {
        String line = "";
        for (int f = 0; f < array.length; f++) {
            line += array[f] + " | ";
        }
        logger.debug(line);
    }

}
