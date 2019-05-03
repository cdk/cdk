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

import java.util.BitSet;
import java.util.Set;

/**
 * A predicate for filtering atom-mapping results. This class is intended for
 * use with {@link Pattern}.
 *
 * <blockquote><pre>{@code
 *     Pattern     pattern = Ullmann.findSubstructure(query);
 *     List<int[]> unique  = FluentIterable.of(patter.matchAll(target))
 *                                         .filter(new UniqueAtomMatches())
 *                                         .toList();
 * }</pre></blockquote>
 *
 * @author John May
 * @cdk.module isomorphism
 */
final class UniqueAtomMatches implements Predicate<int[]> {

    /** Which mappings have we seen already. */
    private final Set<BitSet> unique;

    /**
     * Create filter for the expected number of unique matches. The number
     * of matches can grow if required.
     *
     * @param expectedHits expected number of unique matches
     */
    private UniqueAtomMatches(int expectedHits) {
        this.unique = Sets.newHashSetWithExpectedSize(expectedHits);
    }

    /**
     * Create filter for unique matches.
     */
    public UniqueAtomMatches() {
        this(10);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean apply(int[] input) {
        return unique.add(toBitSet(input));
    }

    /**
     * Convert a mapping to a bitset.
     *
     * @param mapping an atom mapping
     * @return a bit set of the mapped vertices (values in array)
     */
    private BitSet toBitSet(int[] mapping) {
        BitSet hits = new BitSet();
        for (int v : mapping)
            hits.set(v);
        return hits;
    }
}
