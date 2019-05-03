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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;

/**
 * A structure pattern which utilises the Vento-Foggia (VF) algorithm {@cdk.cite
 * Cordella04}.
 *
 * 
 *
 * Find and count the number molecules which contain the query substructure.
 *
 * <blockquote><pre>
 * IAtomContainer query   = ...;
 * Pattern        pattern = VentoFoggia.findSubstructure(query);
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
 * test if it {@link #matches}. These methods automatically verify
 * stereochemistry.
 *
 * <blockquote><pre>{@code
 * IAtomContainer query   = ...;
 * Pattern        pattern = VentoFoggia.findSubstructure(query);
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
public final class VentoFoggia extends Pattern {

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

    /** Search for a subgraph. */
    private final boolean        subgraph;

    /**
     * Non-public constructor for-now the atom/bond semantics are fixed.
     *
     * @param query        the query structure
     * @param atomMatcher  how atoms should be matched
     * @param bondMatcher  how bonds should be matched
     * @param substructure substructure search
     */
    private VentoFoggia(IAtomContainer query, AtomMatcher atomMatcher, BondMatcher bondMatcher, boolean substructure) {
        this.query = query;
        this.atomMatcher = atomMatcher;
        this.bondMatcher = bondMatcher;
        this.bonds1 = EdgeToBondMap.withSpaceFor(query);
        this.g1 = GraphUtil.toAdjList(query, bonds1);
        this.subgraph = substructure;
        determineFilters(query);
    }

    /**{@inheritDoc} */
    @Override
    public int[] match(IAtomContainer target) {
        return matchAll(target).first();
    }

    /**{@inheritDoc} */
    @Override
    public Mappings matchAll(final IAtomContainer target) {

        final EdgeToBondMap bonds2;
        final int[][]       g2;

        AdjListCache cached = target.getProperty(AdjListCache.class.getName());
        if (cached == null || !cached.validate(target)) {
            cached = new AdjListCache(target);
            target.setProperty(AdjListCache.class.getName(), cached);
        }

        bonds2 = cached.bmap;
        g2 = cached.g;

        Iterable<int[]> iterable = new VFIterable(query, target,
                                                  g1, g2,
                                                  bonds1, bonds2,
                                                  atomMatcher, bondMatcher,
                                                  subgraph);

        Mappings mappings = new Mappings(query, target, iterable);
        return filter(mappings, query, target);
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
        return findSubstructure(query,
                                isQuery ? AtomMatcher.forQuery() : AtomMatcher.forElement(),
                                isQuery ? BondMatcher.forQuery() : BondMatcher.forOrder());
    }

    /**
     * Create a pattern which can be used to find molecules which are the same
     * as the {@code query} structure.
     *
     * @param query the substructure to find
     * @return a pattern for finding the {@code query}
     */
    public static Pattern findIdentical(IAtomContainer query) {
        boolean isQuery = query instanceof IQueryAtomContainer;
        return findIdentical(query,
                             isQuery ? AtomMatcher.forQuery() : AtomMatcher.forElement(),
                             isQuery ? BondMatcher.forQuery() : BondMatcher.forOrder());
    }

    /**
     * Create a pattern which can be used to find molecules which contain the
     * {@code query} structure.
     *
     * @param query the substructure to find
     * @param atomMatcher how atoms are matched
     * @param bondMatcher how bonds are matched
     * @return a pattern for finding the {@code query}
     */
    public static Pattern findSubstructure(IAtomContainer query, AtomMatcher atomMatcher, BondMatcher bondMatcher) {
        return new VentoFoggia(query, atomMatcher, bondMatcher, true);
    }

    /**
     * Create a pattern which can be used to find molecules which are the same
     * as the {@code query} structure.
     *
     * @param query the substructure to find
     * @param atomMatcher how atoms are matched
     * @param bondMatcher how bonds are matched
     * @return a pattern for finding the {@code query}
     */
    public static Pattern findIdentical(IAtomContainer query, AtomMatcher atomMatcher, BondMatcher bondMatcher) {
        return new VentoFoggia(query, atomMatcher, bondMatcher, false);
    }

    private static final class VFIterable implements Iterable<int[]> {

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

        /** The query is a subgraph. */
        private final boolean        subgraph;

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
         * @param subgraph    perform subgraph search
         */
        private VFIterable(IAtomContainer container1, IAtomContainer container2, int[][] g1, int[][] g2,
                EdgeToBondMap bonds1, EdgeToBondMap bonds2, AtomMatcher atomMatcher, BondMatcher bondMatcher,
                boolean subgraph) {
            this.container1 = container1;
            this.container2 = container2;
            this.g1 = g1;
            this.g2 = g2;
            this.bonds1 = bonds1;
            this.bonds2 = bonds2;
            this.atomMatcher = atomMatcher;
            this.bondMatcher = bondMatcher;
            this.subgraph = subgraph;
        }

        /**{@inheritDoc} */
        @Override
        public Iterator<int[]> iterator() {
            if (subgraph) {
                return new StateStream(new VFSubState(container1, container2, g1, g2, bonds1, bonds2, atomMatcher,
                        bondMatcher));
            }
            return new StateStream(
                    new VFState(container1, container2, g1, g2, bonds1, bonds2, atomMatcher, bondMatcher));
        }
    }

    private static final class AdjListCache {

        // 100 ms max age
        private static final long MAX_AGE = TimeUnit.MILLISECONDS.toNanos(100);

        private final int[][] g;
        private final EdgeToBondMap bmap;
        private final int numAtoms, numBonds;
        private final long tInit;

        private AdjListCache(IAtomContainer mol) {
            this.bmap = EdgeToBondMap.withSpaceFor(mol);
            this.g = GraphUtil.toAdjList(mol, bmap);
            this.numAtoms = mol.getAtomCount();
            this.numBonds = mol.getBondCount();
            this.tInit = System.nanoTime();
        }

        private boolean validate(IAtomContainer mol) {
            return mol.getAtomCount() == numAtoms &&
                   mol.getBondCount() == numBonds &&
                   (System.nanoTime() - tInit) < MAX_AGE;
        }
    }
}
