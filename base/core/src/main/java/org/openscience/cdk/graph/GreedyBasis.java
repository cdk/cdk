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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import static org.openscience.cdk.graph.InitialCycles.Cycle;

/**
 * Greedily compute a cycle basis from a provided set of initial cycles using
 * Gaussian elimination.
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 * @see RelevantCycles
 */
final class GreedyBasis {

    /** Cycles which are members of the basis */
    private final List<Cycle> basis;

    /** Edges of the current basis. */
    private final BitSet      edgesOfBasis;

    /** Number of edges */
    private final int         m;

    /**
     * Create a new basis for the <i>potential</i> number of cycles and the
     * <i>exact</i> number of edges. These values can be obtained from an {@link
     * InitialCycles} instance.
     *
     * @param n potential number of cycles in the basis
     * @param m number of edges in the graph
     * @see org.openscience.cdk.graph.InitialCycles#numberOfCycles()
     * @see org.openscience.cdk.graph.InitialCycles#numberOfEdges()
     */
    GreedyBasis(final int n, final int m) {
        this.basis = new ArrayList<Cycle>(n);
        this.edgesOfBasis = new BitSet(m);
        this.m = m;
    }

    /**
     * Access the members of the basis.
     *
     * @return cycles ordered by length
     */
    final List<Cycle> members() {
        return Collections.unmodifiableList(basis);
    }

    /**
     * The size of the basis.
     *
     * @return number of cycles in the basis
     */
    final int size() {
        return members().size();
    }

    /**
     * Add a cycle to the basis.
     *
     * @param cycle new basis member
     */
    final void add(final Cycle cycle) {
        basis.add(cycle);
        edgesOfBasis.or(cycle.edgeVector());
    }

    /**
     * Add all cycles to the basis.
     *
     * @param cycles new members of the basis
     */
    final void addAll(final Iterable<Cycle> cycles) {
        for (final Cycle cycle : cycles)
            add(cycle);
    }

    /**
     * Check if all the edges of the <i>cycle</i> are present in the current
     * <i>basis</i>.
     *
     * @param cycle an initial cycle
     * @return any edges of the basis are present
     */
    final boolean isSubsetOfBasis(final Cycle cycle) {
        final BitSet edgeVector = cycle.edgeVector();
        final int intersect = and(edgesOfBasis, edgeVector).cardinality();
        return intersect == cycle.length();
    }

    /**
     * Determine whether the <i>candidate</i> cycle is linearly
     * <i>independent</i> from the current basis.
     *
     * @param candidate a cycle not in currently in the basis
     * @return the candidate is independent
     */
    final boolean isIndependent(final Cycle candidate) {

        // simple checks for independence
        if (basis.isEmpty() || !isSubsetOfBasis(candidate)) return true;

        final BitMatrix matrix = BitMatrix.from(basis, candidate);

        // perform gaussian elimination
        matrix.eliminate();

        // if the last row (candidate) was eliminated it is not independent
        return !matrix.eliminated(basis.size());
    }

    /** and <i>s</i> and <i>t</i> without modifying <i>s</i> */
    private static final BitSet and(final BitSet s, final BitSet t) {
        final BitSet u = (BitSet) s.clone();
        u.and(t);
        return u;
    }
}
