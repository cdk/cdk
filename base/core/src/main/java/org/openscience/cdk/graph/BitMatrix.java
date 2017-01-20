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
package org.openscience.cdk.graph;

import java.util.BitSet;

import static org.openscience.cdk.graph.InitialCycles.Cycle;

/**
 * Mutable bit matrix which can eliminate linearly dependent rows and check
 * which rows were eliminated. These operations are useful when constructing a
 * cycle basis. From a graph we can represent the cycles as a binary vector of
 * incidence (edges). When processing cycles as these vectors we determine
 * whether a cycle can be made of other cycles in our basis. In the example
 * below each row can be made by XORing the other two rows.
 *
 * <blockquote><pre>
 * 1:   111000111   (can be made by 2 XOR 3)
 * 2:   111000000   (can be made by 1 XOR 3)
 * 3:   000000111   (can be made by 1 XOR 2)
 * </pre></blockquote>
 *
 * <blockquote><pre>
 * BitMatrix m = new BitMatrix(9, 3);
 * m.add(toBitSet("111000111"));
 * m.add(toBitSet("111000000"));
 * m.add(toBitSet("111000000"));
 * if (m.eliminate() < 3){
 *   // rows are not independent
 * }
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 */
final class BitMatrix {

    /** rows of the matrix. */
    private final BitSet[] rows;

    /** keep track of row swaps. */
    private final int[]    indices;

    /** maximum number of rows. */
    private final int      max;

    /** number of columns. */
    private final int      n;

    /** current number of rows. */
    private int            m;

    /**
     * Create a new bit matrix with the given number of columns and rows. Note
     * the rows is the <i>maximum</i> number of rows we which to store. The
     * actual row count only increases with {@link #add(java.util.BitSet)}.
     *
     * @param columns number of columns
     * @param rows    number of rows
     */
    BitMatrix(final int columns, final int rows) {
        this.n = columns;
        this.max = rows;
        this.rows = new BitSet[rows];
        this.indices = new int[rows];
    }

    /**
     * Swap the rows {@literal i} and {@literal j}, the swap is kept track of
     * internally allowing {@link #row(int)} and {@link #eliminated(int)} to
     * access the index of the original row.
     *
     * @param i row index
     * @param j row index
     */
    void swap(int i, int j) {
        BitSet row = rows[i];
        int k = indices[i];
        rows[i] = rows[j];
        indices[i] = indices[j];
        rows[j] = row;
        indices[j] = k;
    }

    /**
     * Find the current index of row {@literal j}.
     *
     * @param j original row index to find
     * @return the index now or < 0 if not found
     */
    private int rowIndex(int j) {
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] == j) return i;
        }
        return -1;
    }

    /**
     * Access the row which was added at index {@literal j}.
     *
     * @param j index of row
     * @return the row which was added at index j
     */
    public BitSet row(int j) {
        return rows[rowIndex(j)];
    }

    /**
     * Check whether the row which was added at index {@literal j} has been
     * eliminated. {@link #eliminate()} should be invoked first.
     *
     * @param j row index
     * @return whether the row was eliminated
     * @see #eliminate()
     */
    public boolean eliminated(int j) {
        return row(j).isEmpty();
    }

    /** Clear the matrix, setting the number of rows to 0. */
    public void clear() {
        m = 0;
    }

    /**
     * Add a row to the matrix.
     *
     * @param row the row
     */
    public void add(BitSet row) {
        if (m >= max) throw new IndexOutOfBoundsException("initalise matrix with more rows");
        rows[m] = row;
        indices[m] = m;
        m++;
    }

    /**
     * Eliminate rows from the matrix which can be made by linearly combinations
     * of other rows.
     *
     * @return rank of the matrix
     * @see #eliminated(int)
     */
    public int eliminate() {
        return eliminate(0, 0);
    }

    /**
     * Gaussian elimination.
     *
     * @param x current column index
     * @param y current row index
     * @return the rank of the matrix
     */
    private int eliminate(int x, int y) {

        while (x < n && y < m) {

            int i = indexOf(x, y);

            if (i < 0) return eliminate(x + 1, y);

            // reorder rows
            if (i != y) swap(i, y);

            // xor row with all vectors that have x set
            // note: rows above y are not touched, this isn't an issue in
            //       it's current use (cycle basis) as we only care about
            //       new additions being independent. However starting from
            //       j = 0 allows you to change this but of course is slower.
            for (int j = y + 1; j < m; j++)
                if (rows[j].get(x)) rows[j] = xor(rows[j], rows[y]);

            y++;
        }
        return y;
    }

    /**
     * Index of the the first row after {@literal y} where {@literal x} is set.
     *
     * @param x column index
     * @param y row index
     * @return the first index where {@literal x} is set, index is < 0 if none
     */
    int indexOf(int x, int y) {
        for (int j = y; j < m; j++) {
            if (rows[j].get(x)) return j;
        }
        return -1;
    }

    /**{@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder((4 + n) * m);
        for (int j = 0; j < m; j++) {
            sb.append(indices[j]).append(": ");
            for (int i = 0; i < n; i++) {
                sb.append(rows[j].get(i) ? '1' : '-');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Utility method xors the vectors {@literal u} and {@literal v}. Neither
     * input is modified.
     *
     * @param u a bit set
     * @param v a bit set
     * @return the 'xor' of {@literal u} and {@literal v}
     */
    static BitSet xor(BitSet u, BitSet v) {
        BitSet w = (BitSet) u.clone();
        w.xor(v);
        return w;
    }

    /**
     * Simple creation of a BitMatrix from a collection of cycles.
     *
     * @param cycles cycles to create the matrix from
     * @return instance of a BitMatrix for the cycles
     */
    static BitMatrix from(final Iterable<Cycle> cycles) {

        int rows = 0, cols = 0;
        for (final Cycle c : cycles) {
            if (c.edgeVector().length() > cols) cols = c.edgeVector().length();
            rows++;
        }

        final BitMatrix matrix = new BitMatrix(cols, rows);
        for (final Cycle c : cycles)
            matrix.add(c.edgeVector());
        return matrix;
    }

    /**
     * Simple creation of a BitMatrix from a collection of cycles. The final
     * cycle will be added as the last row of the matrix. The <i>cycle</i>
     * should no be found in <i>cycles</i>.
     *
     * @param cycles cycles to create
     * @param cycle  final cycle to add
     * @return instance of a BitMatrix for the cycles
     */
    static BitMatrix from(final Iterable<Cycle> cycles, Cycle cycle) {

        int rows = 1, cols = cycle.edgeVector().length();
        for (final Cycle c : cycles) {
            if (c.edgeVector().length() > cols) cols = c.edgeVector().length();
            rows++;
        }

        final BitMatrix matrix = new BitMatrix(cols, rows);
        for (final Cycle c : cycles)
            matrix.add(c.edgeVector());
        matrix.add(cycle.edgeVector());
        return matrix;
    }
}
