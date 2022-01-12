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

/**
 * This class is for the early boundary conditions class of MAYGEN package. Users can define their
 * early boundary conditions for the structure generation process. This will help to avoid post
 * processing filtering.
 *
 * <p>For example, with detectTripleBonds, users can avoid the generation of molecular structures
 * with triple bonds. Users can add their functions, the early boundary conditions, here.
 *
 * @author MehmetAzizYirik mehmetazizyirik@outlook.com 0000-0001-7520-7215@orcid.org
 */
class BoundaryConditions {

    private BoundaryConditions() {}

    /**
     * No triple bonds.
     *
     * @param mat int[][] the adjacency matrix
     * @return boolean
     */
    public static boolean detectTripleBonds(int[][] mat) {
        int length = mat.length;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (mat[i][j] == 3) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * No adjacent double bonds.
     *
     * @param mat int[][] the adjacency matrix
     * @return boolean
     */
    public static boolean detectAdjacentDoubleBonds(int[][] mat) {
        boolean check = false;
        int count;
        for (int i = 0; i < mat.length; i++) {
            count = 0;
            for (int j = 0; j < mat.length; j++) {
                if (mat[i][j] == 2) count++;
            }
            if (count >= 2) {
                check = true;
                break;
            }
        }
        return check;
    }

    /**
     * No allenes.
     *
     * @param mat int[][]the adjacency matrix
     * @param symbols String[] atom symbols
     * @return boolean
     */
    public static boolean detectAllenes(int[][] mat, String[] symbols) {
        boolean check = false;
        int count;
        for (int i = 0; i < mat.length; i++) {
            count = 0;
            if (symbols[i].equals("C")) {
                for (int j = 0; j < mat.length; j++) {
                    if (mat[i][j] == 2 && symbols[j].equals("C")) {
                        count++;
                    }
                }
                if (count >= 2) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    /**
     * After defining the above early boundary conditions, they need to be added to the
     * boundaryConditionCheck function.
     *
     * @param mat int[][] adjacency matrix
     * @param symbolArray String[] symbolArray
     * @return boolean
     */
    public static boolean boundaryConditionCheck(int[][] mat, String[] symbolArray) {
        boolean check = true;

        /*
        Here, users can define the functions as the boundary conditions. Example conditions are
        given below.
        */

        if (detectAllenes(mat, symbolArray)) {
            check = false;
        }
        if (detectAdjacentDoubleBonds(mat)) {
            check = false;
        }
        if (detectTripleBonds(mat)) {
            check = false;
        }
        return check;
    }
}
