/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.isomorphism;


/**
 * A compatibility matrix defines which query vertices (rows) could possible be
 * mapped to a target vertex (columns). The matrix is used in the Ullmann and
 * Ullmann-like algorithms to provide top-down pruning.
 *
 * Instead of using a binary matrix this implementation uses int values. This
 * allows us to remove a mapping but put it back in later (backtrack).
 *
 * @author John May
 * @cdk.module isomorphism
 * @see UllmannState
 */
final class CompatibilityMatrix {

    /** Value storage. */
    final int[] data;

    /** Size of the matrix. */
    final int   nRows, mCols;

    /**
     * Create a matrix of the given size.
     *
     * @param nRows number of rows
     * @param mCols number of columns
     */
    CompatibilityMatrix(int nRows, int mCols) {
        this.data = new int[nRows * mCols];
        this.nRows = nRows;
        this.mCols = mCols;
    }

    /**
     * Set the value in row, i and column j.
     *
     * @param i row index
     * @param j column index
     */
    void set(int i, int j) {
        data[(i * mCols) + j] = 1;
    }

    /**
     * Access the value at index i, values wrap around to the next row.
     *
     * @param i index
     * @return the value is set
     */
    boolean get(int i) {
        return data[i] > 0;
    }

    /**
     * Access the value at row i and column j. The values wrap around to the
     * next row.
     *
     * @param i index
     * @return the value is set
     */
    boolean get(int i, int j) {
        return get((i * mCols) + j);
    }

    /**
     * Mark the value in row i and column j allowing it to be reset later.
     *
     * @param i       row index
     * @param j       column index
     * @param marking the marking to store (should be negative)
     */
    void mark(int i, int j, int marking) {
        data[(i * mCols) + j] = marking;
    }

    /**
     * Mark all values in row i allowing it to be reset later.
     *
     * @param i       row index
     * @param marking the marking to store (should be negative)
     */
    void markRow(int i, int marking) {
        for (int j = (i * mCols), end = j + mCols; j < end; j++)
            if (data[j] > 0) data[j] = marking;
    }

    /**
     * Reset all values marked with (marking) from row i onwards.
     *
     * @param i       row index
     * @param marking the marking to reset (should be negative)
     */
    void resetRows(int i, int marking) {
        for (int j = (i * mCols); j < data.length; j++)
            if (data[j] == marking) data[j] = 1;
    }

    /**
     * Create a fixed-size 2D array of the matrix (useful for debug).
     *
     * @return a fixed version of the matrix
     */
    int[][] fix() {
        int[][] m = new int[nRows][mCols];
        for (int i = 0; i < nRows; i++)
            System.arraycopy(data, (i * mCols), m[i], 0, mCols);
        return m;
    }
}
