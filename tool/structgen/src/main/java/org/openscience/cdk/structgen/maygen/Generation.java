/*
 MIT License

 Copyright (c) 2021 Mehmet Aziz Yirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge, publish, distribute,
 sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or
 substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package org.openscience.cdk.structgen.maygen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.group.Permutation;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * The generation process in maygen class is given here. Due to the parallelization, the generation
 * functions are kept in a separate class.
 *
 * @author MehmetAzizYirik mehmetazizyirik@outlook.com 0000-0001-7520-7215@orcid.org
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
        IAtomContainer atomContainer = maygen.getBuilder().newInstance(IAtomContainer.class);
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
        if (maygen.isWriteSDF()
                || maygen.isPrintSDF()
                || maygen.isWriteSMILES()
                || maygen.isPrintSMILES()) {
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
