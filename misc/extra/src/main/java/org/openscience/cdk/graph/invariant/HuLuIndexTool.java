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

import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.invariant.exception.BadMatrixFormatException;
import org.openscience.cdk.graph.invariant.exception.IndexOutOfBoundsException;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;

/**
 * Collection of methods for the calculation of topological indices of a
 * molecular graph as described in {@cdk.cite HU96}.
 *
 * @cdk.githash
 * @author Mark Vine
 * @author John Mayfield
 */
public class HuLuIndexTool {

    // Figure 1. in paper, could precompute the sqrt but hopefully the compiler
    // does that for us, https://en.wikipedia.org/wiki/Covalent_radius provides
    // a more accurate list but this tool was for generating "unique" indexes.
    // based on the example in the paper it appears they use 0.7709 for the covalent
    // radius of C rather than 0.74 - this allows us to exactly match the proposed
    // numbers
    public static double getSqrtRadii(IAtom atom) {
        if (atom.getAtomicNumber() == null)
            throw new NullPointerException("Atomic Number not set");
        switch (atom.getAtomicNumber()) {
            case IElement.H:
                return Math.sqrt(0.37);
            case IElement.Li:
                return Math.sqrt(1.225);
            case IElement.Be:
                return Math.sqrt(0.889);
            case IElement.B:
                return Math.sqrt(0.80);
            case IElement.C:
                return Math.sqrt(0.7709999); // 0.74 in fig. 1
            case IElement.N:
                return Math.sqrt(0.74);
            case IElement.O:
                return Math.sqrt(0.74);
            case IElement.F:
                return Math.sqrt(0.72);
            case IElement.Na:
                return Math.sqrt(1.572);
            case IElement.Mg:
                return Math.sqrt(1.364);
            case IElement.Al:
                return Math.sqrt(1.248);
            case IElement.Si:
                return Math.sqrt(1.173);
            case IElement.P:
                return Math.sqrt(1.10);
            case IElement.S:
                return Math.sqrt(1.04);
            case IElement.Cl:
                return Math.sqrt(0.994);
            case IElement.Br:
                return Math.sqrt(1.142);
            case IElement.I:
                return Math.sqrt(1.334);
            default:
                throw new IllegalArgumentException("Unsupported element: " + atom.getSymbol());
        }
    }

    private static int sigma(IAtomContainer mol, IAtom atom) {
        int hcnt = atom.getImplicitHydrogenCount();
        for (IBond bond : mol.getConnectedBondsList(atom)) {
            IAtom nbor = bond.getOther(atom);
            if (nbor.getAtomicNumber() == IElement.H)
                hcnt++;
        }
        switch (atom.getAtomicNumber()) {
            case IElement.Li:
                return 1 - hcnt;
            case IElement.Na:
                return 1 - hcnt;
            case IElement.Be:
                return 2 - hcnt;
            case IElement.Mg:
                return 2 - hcnt;
            case IElement.B:
                return 3 - hcnt;
            case IElement.Al:
                return 3 - hcnt;
            case IElement.C:
                return 4 - hcnt;
            case IElement.N:
                return 5 - hcnt;
            case IElement.P:
                return 5 - hcnt;
            case IElement.O:
                return 6 - hcnt;
            case IElement.S:
                return 6 - hcnt;
            case IElement.F:
                return 7 - hcnt;
            case IElement.Cl:
                return 7 - hcnt;
            case IElement.Br:
                return 7 - hcnt;
            case IElement.I:
                return 7 - hcnt;
            default:
                throw new IllegalArgumentException("Unsupported atom: " + atom.getAtomicNumber());
        }
    }

    private static double getWeight(IBond bond) {
        if (bond.isAromatic())
            return 1.5d;
        switch (bond.getOrder()) {
            case SINGLE:
                return 1;
            case DOUBLE:
                return 2;
            case TRIPLE:
                return 3;
            default:
                throw new IllegalArgumentException("Unsupported bond type: " + bond);
        }
    }

    // like ConnectionMatrix.getMatrix but need 1.5 for aromatic
    private static double[][] getAdjacencyMatrix(IAtomContainer mol) {
        int acount = mol.getAtomCount();
        double[][] adjMat = new double[acount][acount];
        for (IBond bond : mol.bonds()) {
            int i = bond.getBegin().getIndex();
            int j = bond.getEnd().getIndex();
            adjMat[i][j] = adjMat[j][i] = getWeight(bond);
        }
        return adjMat;
    }

    static double[] getAtomWeights(IAtomContainer mol) {

        //int k = 0;
        double[] weightArray = new double[mol.getAtomCount()];
        double[][] adjaMatrix = ConnectionMatrix.getMatrix(mol);

        int[][] apspMatrix = PathTools.computeFloydAPSP(adjaMatrix);
        int[] atomLayers = getAtomLayers(apspMatrix);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);

            if (atomLayers[i] > mol.getAtomCount())
                continue;

            int[] cvm = new int[atomLayers[i]];
            int[] interLayerBondSum = new int[atomLayers[i] - 1];

            weightArray[i] = sigma(mol, atom);
            for (int j = 0; j < apspMatrix.length; j++) {
                if (apspMatrix[j][i] <= mol.getAtomCount())
                    cvm[apspMatrix[j][i]] += sigma(mol, mol.getAtom(j));
            }

            for (IBond bond : mol.bonds()) {
                IAtom headAtom = bond.getBegin();
                IAtom endAtom = bond.getEnd();

                int headAtomPosition = mol.indexOf(headAtom);
                int endAtomPosition = mol.indexOf(endAtom);

                if (Math.abs(apspMatrix[i][headAtomPosition] - apspMatrix[i][endAtomPosition]) == 1) {
                    int min = Math.min(apspMatrix[i][headAtomPosition],
                            apspMatrix[i][endAtomPosition]);
                    interLayerBondSum[min] += getWeight(bond);
                }
            }

            for (int j = 0; j < interLayerBondSum.length; j++) {
                weightArray[i] += interLayerBondSum[j] * cvm[j + 1] * Math.pow(10, -(j + 1));
            }
        }

        return weightArray;
    }

    static double[][] getWeightMatrix(IAtomContainer mol) {
        int acount = mol.getAtomCount();
        double[][] matrix = new double[acount][acount];
        double[] weights = getAtomWeights(mol);
        for (int i = 0; i < acount; i++) {
            for (int j = i + 1; j < acount; j++) {
                // Wij = sqrt(s[i]/s[j]) + sqrt(s[j]/s[i])
                matrix[i][j] = matrix[j][i]
                        = Math.sqrt(weights[i] / weights[j]) +
                        Math.sqrt(weights[j] / weights[i]);
            }
        }
        return matrix;
    }

    /**
     * Compute the extended adjacency matrix as described in {@cdk.cite HU96}.
     *
     * @param mol the molecule
     * @return extended adjacency matrix
     */
    public static double[][] getExtendedAdjacencyMatrix(IAtomContainer mol) {

        int acount = mol.getAtomCount();
        double[][] extAdjMat = new double[acount][acount];
        double[][] adjMat = getAdjacencyMatrix(mol);
        double[][] wgtMat = getWeightMatrix(mol);

        for (int i = 0; i < acount; i++) {
            extAdjMat[i][i] = getSqrtRadii(mol.getAtom(i)) / 6;
            for (int j = i + 1; j < acount; j++) {
                extAdjMat[j][i] = extAdjMat[i][j] =
                        (Math.sqrt(adjMat[i][j]) * wgtMat[i][j]) / 6;
            }
        }

        return extAdjMat;
    }

    public static int[] getAtomLayers(int[][] apspMatrix) {
        int[] atomLayers = new int[apspMatrix.length];
        for (int i = 0; i < apspMatrix.length; i++) {
            atomLayers[i] = 0;
            for (int[] matrix : apspMatrix) {
                // 999999 used for not connected
                if (matrix[i] > apspMatrix.length)
                    continue;
                if (atomLayers[i] < 1 + matrix[i])
                    atomLayers[i] = 1 + matrix[i];
            }

        }
        return atomLayers;
    }

    /**
     * Calculates the extended adjacency matrix index.
     * An implementation of the algorithm published in {@cdk.cite HU96}.
     *
     * @cdk.keyword EAID number
     */
    public static double getEAIDNumber(IAtomContainer atomContainer) throws NoSuchAtomException,
            BadMatrixFormatException, IndexOutOfBoundsException {
        GIMatrix matrix = new GIMatrix(getExtendedAdjacencyMatrix(atomContainer));

        GIMatrix tempMatrix = matrix;
        GIMatrix fixedMatrix = matrix;
        for (int i = 2; i < atomContainer.getAtomCount(); i++) {
            tempMatrix = tempMatrix.multiply(fixedMatrix);
            matrix = matrix.add(tempMatrix);
        }

        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            matrix.setValueAt(i, i, matrix.getValueAt(i, i) + 1);
        }
        return matrix.trace();
    }
}
