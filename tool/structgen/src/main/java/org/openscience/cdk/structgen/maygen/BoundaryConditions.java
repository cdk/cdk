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

/**
 * This class is for the early boundary conditions class of MAYGEN package. Users can define their
 * early boundary conditions for the structure generation process. This will help to avoid post
 * processing filtering.
 *
 * <p>For example, with detectTripleBonds, users can avoid the generation of molecular structures
 * with triple bonds. Users can add their functions, the early boundary conditions, here.
 *
 * @author MehmetAzizYirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
 * @cdk.module structgen
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
        for (int[] ints : mat) {
            for (int j = 0; j < length; j++) {
                if (ints[j] == 3) {
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
        for (int[] ints : mat) {
            count = 0;
            for (int j = 0; j < mat.length; j++) {
                if (ints[j] == 2) count++;
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
