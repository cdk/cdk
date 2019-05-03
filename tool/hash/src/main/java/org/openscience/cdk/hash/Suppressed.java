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

package org.openscience.cdk.hash;


import java.util.BitSet;

/**
 * Defines a structure which indicates whether a vertex (int id) is suppressed
 * when computing an atomic/molecular hash code.
 *
 * @author John May
 * @cdk.module hash
 */
abstract class Suppressed {

    /**
     * Is the vertex 'i' contained in the vertices which should be suppressed.
     *
     * @param i vertex index
     * @return the vertex is supressed
     */
    abstract boolean contains(int i);

    /**
     * The total number of suppressed vertices.
     *
     * @return number of suppressed vertices 0 .. |V|
     */
    abstract int count();

    /**
     * Access which vertices are suppressed as a fixed-size array.
     *
     * @return the suppressed vertices
     */
    abstract int[] toArray();

    /** Default 'empty' implementation always returns false. */
    private static final class Empty extends Suppressed {

        /**{@inheritDoc} */
        @Override
        boolean contains(int i) {
            return false;
        }

        /**{@inheritDoc} */
        @Override
        int count() {
            return 0;
        }

        /**{@inheritDoc} */
        @Override
        int[] toArray() {
            return new int[0];
        }
    }

    /**
     * Implementation where the suppressed vertices are indicated with a
     * BitSet.
     */
    private static final class SuppressedBitSet extends Suppressed {

        /** Bits indicate suppressed vertices. */
        private final BitSet set;

        /**
         * Create a new suppressed instance with the specified vertices
         * suppressed.
         *
         * @param set bits indicates suppressed
         */
        private SuppressedBitSet(BitSet set) {
            this.set = set;
        }

        /**{@inheritDoc} */
        @Override
        boolean contains(int i) {
            return set.get(i);
        }

        /**{@inheritDoc} */
        @Override
        int count() {
            return set.cardinality();
        }

        /**{@inheritDoc} */
        @Override
        int[] toArray() {
            int[] xs = new int[count()];
            int n = 0;
            for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
                xs[n++] = i;
            }
            return xs;
        }
    }

    /** default implementation. */
    private static final Empty empty = new Empty();

    /**
     * Access a suppressed implementation where no vertices are suppressed.
     *
     * @return implementation where all vertices are unsuppressed
     */
    static Suppressed none() {
        return empty;
    }

    /**
     * Create a suppressed implementation for the provided BitSet.
     *
     * @param set bits indicated suppressed vertices
     * @return implementation using the BitSet to lookup suppressed vertices
     */
    static Suppressed fromBitSet(BitSet set) {
        return new SuppressedBitSet(set);
    }
}
