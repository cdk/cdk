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

import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import java.util.Iterator;

import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;

/**
 * A structure pattern which utilises the Ullmann algorithm {@cdk.cite
 * Ullmann76}.
 *
 * 
 *
 * Find and count the number molecules which contain the query substructure.
 *
 * <blockquote><pre>
 * IAtomContainer query   = ...;
 * Pattern        pattern = Ullmann.findSubstructure(query);
 *
 * int hits = 0;
 * for (IAtomContainer m : ms)
 *     if (pattern.matches(m))
 *         hits++;
 * </pre></blockquote>
 * 
 *
 * Finding the matching to molecules which contain the query substructure. It is
 * more efficient to obtain the {@link #match} and check it's size rather than
 * test if it {@link #matches} first. These methods automatically verify
 * stereochemistry.
 *
 * <blockquote><pre>{@code
 * IAtomContainer query   = ...;
 * Pattern        pattern = Ullmann.findSubstructure(query);
 *
 * int hits = 0;
 * for (IAtomContainer m : ms) {
 *     int[] match = pattern.match(m);
 *     if (match.length > 0)
 *         hits++;
 * }
 * }</pre></blockquote>
 *
 * @author John May
 * @cdk.module isomorphism
 */
public final class Ullmann extends Pattern {

    /** The query structure. */
    private final IAtomContainer query;

    /** The query structure adjacency list. */
    private final int[][]        g1;

    /** The bonds of the query structure. */
    private final EdgeToBondMap  bonds1;

    /** The atom matcher to determine atom feasibility. */
    private final AtomMatcher    atomMatcher;

    /** The bond matcher to determine atom feasibility. */
    private final BondMatcher    bondMatcher;

    /** Is the query matching query atoms/bonds etc? */
    private final boolean        queryMatching;

    /**
     * Non-public constructor for-now the atom/bond semantics are fixed.
     *
     * @param query       the query structure
     * @param atomMatcher how atoms should be matched
     * @param bondMatcher how bonds should be matched
     */
    private Ullmann(IAtomContainer query, AtomMatcher atomMatcher, BondMatcher bondMatcher) {
        this.query = query;
        this.atomMatcher = atomMatcher;
        this.bondMatcher = bondMatcher;
        this.bonds1 = EdgeToBondMap.withSpaceFor(query);
        this.g1 = GraphUtil.toAdjList(query, bonds1);
        this.queryMatching = query instanceof IQueryAtomContainer;
    }

    @Override
    public int[] match(IAtomContainer target) {
        return matchAll(target).stereochemistry().first();
    }

    @Override
    public Mappings matchAll(IAtomContainer target) {
        EdgeToBondMap bonds2 = EdgeToBondMap.withSpaceFor(target);
        int[][] g2 = GraphUtil.toAdjList(target, bonds2);
        Iterable<int[]> iterable = new UllmannIterable(query, target, g1, g2, bonds1, bonds2, atomMatcher, bondMatcher);
        return new Mappings(query, target, iterable);
    }

    /**
     * Create a pattern which can be used to find molecules which contain the
     * {@code query} structure.
     *
     * @param query the substructure to find
     * @return a pattern for finding the {@code query}
     */
    public static Pattern findSubstructure(IAtomContainer query) {
        boolean isQuery = query instanceof IQueryAtomContainer;
        return new Ullmann(query, isQuery ? AtomMatcher.forQuery() : AtomMatcher.forElement(),
                isQuery ? BondMatcher.forQuery() : BondMatcher.forOrder());
    }

    /** Iterable matcher for Ullmann. */
    private static final class UllmannIterable implements Iterable<int[]> {

        /** Query and target containers. */
        private final IAtomContainer container1, container2;

        /** Query and target adjacency lists. */
        private final int[][]        g1, g2;

        /** Query and target bond lookup. */
        private final EdgeToBondMap  bonds1, bonds2;

        /** How are atoms are matched. */
        private final AtomMatcher    atomMatcher;

        /** How are bonds are match. */
        private final BondMatcher    bondMatcher;

        /**
         * Create a match for the following parameters.
         *
         * @param container1  query structure
         * @param container2  target structure
         * @param g1          query adjacency list
         * @param g2          target adjacency list
         * @param bonds1      query bond map
         * @param bonds2      target bond map
         * @param atomMatcher how atoms are matched
         * @param bondMatcher how bonds are matched
         */
        private UllmannIterable(IAtomContainer container1, IAtomContainer container2, int[][] g1, int[][] g2,
                EdgeToBondMap bonds1, EdgeToBondMap bonds2, AtomMatcher atomMatcher, BondMatcher bondMatcher) {
            this.container1 = container1;
            this.container2 = container2;
            this.g1 = g1;
            this.g2 = g2;
            this.bonds1 = bonds1;
            this.bonds2 = bonds2;
            this.atomMatcher = atomMatcher;
            this.bondMatcher = bondMatcher;
        }

        /**{@inheritDoc} */
        @Override
        public Iterator<int[]> iterator() {
            return new StateStream(new UllmannState(container1, container2, g1, g2, bonds1, bonds2, atomMatcher,
                    bondMatcher));
        }
    }
}
