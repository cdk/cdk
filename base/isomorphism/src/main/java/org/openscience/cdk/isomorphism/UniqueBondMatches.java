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

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

/**
 * A predicate for filtering atom-mapping results for those which cover unique
 * edges. This class is intended for use with {@link Pattern}.
 *
 * <blockquote><pre>{@code
 *     Pattern     pattern = Ullmann.findSubstructure(query);
 *     List<int[]> unique  = FluentIterable.of(patter.matchAll(target))
 *                                         .filter(new UniqueBondMatches())
 *                                         .toList();
 * }</pre></blockquote>
 *
 * @author John May
 * @cdk.module isomorphism
 */
final class UniqueBondMatches implements Predicate<int[]> {

    /** Which mappings have we seen already. */
    private final Set<Set<Tuple>> unique;

    /** The query graph. */
    private final int[][]         g;

    /**
     * Create filter for the expected number of unique matches. The number of
     * matches can grow if required.
     *
     * @param expectedHits expected number of unique matches
     */
    private UniqueBondMatches(int[][] g, int expectedHits) {
        this.unique = Sets.newHashSetWithExpectedSize(expectedHits);
        this.g = g;
    }

    /** Create filter for unique matches. */
    public UniqueBondMatches(int[][] g) {
        this(g, 10);
    }

    /**{@inheritDoc} */
    @Override
    public boolean apply(int[] input) {
        return unique.add(toEdgeSet(input));
    }

    /**
     * Convert a mapping to a bitset.
     *
     * @param mapping an atom mapping
     * @return a bit set of the mapped vertices (values in array)
     */
    private Set<Tuple> toEdgeSet(int[] mapping) {
        Set<Tuple> edges = new HashSet<Tuple>(mapping.length * 2);
        for (int u = 0; u < g.length; u++) {
            for (int v : g[u]) {
                edges.add(new Tuple(mapping[u], mapping[v]));
            }
        }
        return edges;
    }

    /** Immutable helper class holds two vertices id's. */
    private static final class Tuple {

        /** Endpoints. */
        final int u, v;

        /**
         * Create the tuple
         *
         * @param u an endpoint
         * @param v another endpoint
         */
        private Tuple(int u, int v) {
            this.u = u;
            this.v = v;
        }

        /**{@inheritDoc} */
        @Override
        public int hashCode() {
            return u ^ v;
        }

        /**{@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple that = (Tuple) o;

            return this.u == that.u && this.v == that.v || this.u == that.v && this.v == that.u;
        }
    }
}
