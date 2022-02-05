/*
 * Copyright (c) 2021 Mehmet Aziz Yirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
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
 */

package org.openscience.cdk.structgen.maygen;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.group.Permutation;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The generation process in maygen class is given here. Due to the parallelization, the generation
 * functions are kept in a separate class.
 *
 * @author MehmetAzizYirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
 * @cdk.module structgen
 */
class Generation {
    private final Maygen maygen;

    public Generation(Maygen maygen) {
        this.maygen = maygen;
    }

    /**
     * The initial function of the generation process
     *
     * @param degree int[] the list of node degrees ( the atom valences )
     */
    public void run(int[] degree) {
        IAtomContainer atomContainer = maygen.getBuilder().newAtomContainer();
        int[] partSize = new int[] {0};
        int[] r = new int[] {0};
        int[] y = new int[] {0};
        int[] z = new int[] {0};
        int[][] ys = new int[][] {new int[0]};
        int[][] zs = new int[][] {new int[0]};
        boolean[] learningFromCanonicalTest = new boolean[] {false};
        boolean[] learningFromConnectivity = new boolean[] {false};
        int[] nonCanonicalIndices = new int[2];
        List<ArrayList<Permutation>> formerPermutations = new ArrayList<>();
        int[] hydrogens = maygen.setHydrogens(degree);
        int[] newPartition = maygen.getPartition(degree);
        final int[] initialPartition;
        String[] symbolArrayCopy = maygen.getSymbolArray();
        if (maygen.getConsumer() != Maygen.NOOP_CONSUMER) {
            symbolArrayCopy =
                    Arrays.copyOf(maygen.getSymbolArray(), maygen.getSymbolArray().length);
            initialPartition =
                    maygen.sortWithPartition(newPartition, degree, symbolArrayCopy, hydrogens);
            atomContainer = maygen.initAC(atomContainer, symbolArrayCopy);
        } else {
            initialPartition =
                    maygen.sortWithPartition(
                            newPartition, degree, maygen.getSymbolArray(), hydrogens);
        }
        int[] connectivityIndices = new int[2];
        int[][] partitionList = new int[maygen.getSize() + 1][1];
        try {
            partSize[0] = partSize[0] + (maygen.findZeros(initialPartition) - 1);
            maygen.setYZValues(initialPartition, ys, zs);
            partitionList[0] = initialPartition;
            maygen.generate(
                    atomContainer,
                    symbolArrayCopy,
                    degree,
                    initialPartition,
                    partitionList,
                    connectivityIndices,
                    learningFromConnectivity,
                    nonCanonicalIndices,
                    formerPermutations,
                    hydrogens,
                    partSize,
                    r,
                    y,
                    z,
                    ys,
                    zs,
                    learningFromCanonicalTest);
        } catch (IOException | CloneNotSupportedException | CDKException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }
}
