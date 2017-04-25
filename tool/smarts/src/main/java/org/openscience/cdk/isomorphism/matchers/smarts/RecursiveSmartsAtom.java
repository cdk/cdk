/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.isomorphism.matchers.smarts;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.ComponentGrouping;
import org.openscience.cdk.isomorphism.SmartsStereoMatch;
import org.openscience.cdk.isomorphism.Ullmann;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.BitSet;

/**
 * This matches recursive smarts atoms.
 *
 * @cdk.module smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 */
public final class RecursiveSmartsAtom extends SMARTSAtom {

    private final static ILoggingTool                  logger = LoggingToolFactory
                                                                      .createLoggingTool(RecursiveSmartsAtom.class);

    /** The IQueryAtomContainer created by parsing the recursive smarts */
    private final IQueryAtomContainer                  query;

    /** Query cache. */
    private final LoadingCache<IAtomContainer, BitSet> cache;

    /**
     * Creates a new instance
     *
     * @param query
     */
    public RecursiveSmartsAtom(final IQueryAtomContainer query) {
        super(query.getBuilder());
        this.query = query;
        this.cache = CacheBuilder.newBuilder().maximumSize(42).weakKeys()
                .build(new CacheLoader<IAtomContainer, BitSet>() {

                    @Override
                    public BitSet load(IAtomContainer target) throws Exception {
                        BitSet hits = new BitSet();
                        for (int[] mapping : FluentIterable.from(Ullmann.findSubstructure(query).matchAll(target))
                                .filter(new SmartsStereoMatch(query, target))
                                .filter(new ComponentGrouping(query, target))) {
                            hits.set(mapping[0]);
                        }
                        return hits;
                    }
                });
    }

    /*
     * (non-Javadoc)
     * @see
     * org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org
     * .openscience.cdk.interfaces.IAtom)
     */
    @Override
    public boolean matches(IAtom atom) {

        if (!((IQueryAtom) query.getAtom(0)).matches(atom)) return false;

        if (query.getAtomCount() == 1) return true;

        IAtomContainer target = invariants(atom).target();

        return cache.getUnchecked(target).get(target.indexOf(atom));
    }
}
