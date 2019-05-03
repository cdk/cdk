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
 * Defines a state for matching (subgraph-)isomorphism from a query graph
 * (<i>G1</i>) to a target graph (<i>G2</i>). The mutable state allows
 * generation and adding and removal of mappings. A mapping {n, m} indicates a
 * query vertex (from <i>G1</i>), n, is paired (mapped) with the target vertex,
 * m (from <i>G2</i>). Candidate pairs are generated using {@link #nextN(int)}
 * and {@link #nextM(int)}. Each candidate pair {n, m} is then {@link #add}ed if
 * the mapping was feasible.
 *
 * @author John May
 * @cdk.module isomorphism
 */
abstract class State {

    /**
     * Given the previous candidate generate the next query candidate. The first
     * candidate passed is always -1.
     *
     * @param n the previous candidate
     * @return next candidate
     */
    abstract int nextN(int n);

    /**
     * Given the previous candidate generate the next target candidate. The
     * first candidate passed is always -1.
     *
     * @param n the current n vertex
     * @param m the previous candidate
     * @return next candidate
     */
    abstract int nextM(int n, int m);

    /**
     * The max query candidate (number of vertices in the query).
     *
     * @return <i>|V| ∈ G1</i>
     */
    abstract int nMax();

    /**
     * The max target candidate (number of vertices in the target).
     *
     * @return <i>|V| ∈ G2</i>
     */
    abstract int mMax();

    /**
     * Add a mapping between n (a vertex G1) and m (a vertex in G2). If the
     * mapping was not feasible the mapping is not added.
     *
     * @param n a vertex in G1
     * @param m a vertex in G2
     * @return the mapping was added
     */
    abstract boolean add(int n, int m);

    /**
     * Remove a mapping (backtrack) between n (a vertex G1) and m (a vertex in
     * G2).
     *
     * @param n a vertex in G1
     * @param m a vertex in G2
     */
    abstract void remove(int n, int m);

    /**
     * Access a copy of the current mapping.
     *
     * @return mapping of vertices from <i>G1</i> to <i>G2</i>
     */
    abstract int[] mapping();

    /**
     * Current size of the state. If <i>size</i> is the current number of mapped
     * candidates.
     *
     * @return the size of the state
     */
    abstract int size();
}
