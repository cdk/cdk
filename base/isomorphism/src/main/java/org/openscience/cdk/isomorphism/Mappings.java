/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * A fluent interface for handling (sub)-graph mappings from a query to a target
 * structure. The utility allows one to modify the mappings and provides
 * convenience utilities. {@link Mappings} are obtained from a (sub)-graph
 * matching using {@link Pattern}.
 *
 * <blockquote><pre>
 * IAtomContainer query  = ...;
 * IAtomContainer target = ...;
 *
 * Mappings mappings = Pattern.findSubstructure(query)
 *                            .matchAll(target);
 * </pre></blockquote>
 *
 * The primary function is to provide an iterable of matches - each match is
 * a permutation (mapping) of the query graph indices (atom indices).
 *
 * <blockquote><pre>{@code
 * for (int[] p : mappings) {
 *     for (int i = 0; i < p.length; i++)
 *         // query.getAtom(i) is mapped to target.getAtom(p[i]);
 * }
 * }</pre></blockquote>
 *
 * The matches can be filtered to provide only those that have valid
 * stereochemistry.
 *
 * <blockquote><pre>
 * for (int[] p : mappings.stereochemistry()) {
 *     // ...
 * }
 * </pre></blockquote>
 *
 * Unique matches can be obtained for both atoms and bonds.
 *
 * <blockquote><pre>
 * for (int[] p : mappings.uniqueAtoms()) {
 *     // ...
 * }
 *
 * for (int[] p : mappings.uniqueBonds()) {
 *     // ...
 * }
 * </pre></blockquote>
 *
 * As matches may be lazily generated - iterating over the match twice (as
 * above) will actually perform two graph matchings. If the mappings are needed
 * for subsequent use the {@link #toArray()} provides the permutations as a
 * fixed size array.
 *
 * <blockquote><pre>
 * int[][] ps = mappings.toArray();
 * for (int[] p : ps) {
 *    // ...
 * }
 * </pre></blockquote>
 *
 * Graphs with a high number of automorphisms can produce many valid matchings.
 * Operations can be combined such as to limit the number of matches we
 * retrieve.
 *
 * <blockquote><pre>
 * // first ten matches
 * for (int[] p : mappings.limit(10)) {
 *     // ...
 * }
 *
 * // first 10 unique matches
 * for (int[] p : mappings.uniqueAtoms()
 *                        .limit(10)) {
 *     // ...
 * }
 *
 * // ensure we don't waste memory and only 'fix' up to 100 unique matches
 * int[][] ps = mappings.uniqueAtoms()
 *                      .limit(100)
 *                      .toArray();
 * </pre></blockquote>
 *
 * There is no restrictions on which operation can be applied and how many times
 * but the order of operations may change the result.
 *
 * <blockquote><pre>
 * // first 100 unique matches
 * Mappings m = mappings.uniqueAtoms()
 *                      .limit(100);
 *
 * // unique matches in the first 100 matches
 * Mappings m = mappings.limit(100)
 *                      .uniqueAtoms();
 *
 * // first 10 unique matches in the first 100 matches
 * Mappings m = mappings.limit(100)
 *                      .uniqueAtoms()
 *                      .limit(10);
 *
 * // number of unique atom matches
 * int n = mappings.countUnique();
 *
 * // number of unique atom matches with correct stereochemistry
 * int n = mappings.stereochemistry()
 *                 .countUnique();
 *
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module isomorphism
 * @see Pattern
 * @cdk.keyword substructure search
 * @cdk.keyword structure search
 * @cdk.keyword mappings
 * @cdk.keyword matching
 * @cdk.githash
 */
public final class Mappings implements Iterable<int[]> {

    /** Iterable permutations of the query vertices. */
    private final Iterable<int[]> iterable;

    /** Query and target structures. */
    private IAtomContainer        query, target;

    /**
     * Create a fluent mappings instance for the provided query / target and an
     * iterable of permutations on the query vertices (specified as indices).
     *
     * @param query    the structure to be found
     * @param target   the structure being searched
     * @param iterable iterable of permutation
     * @see Pattern
     */
    Mappings(IAtomContainer query, IAtomContainer target, Iterable<int[]> iterable) {
        this.query = query;
        this.target = target;
        this.iterable = iterable;
    }

    /**
     * Filter the mappings and keep only those which match the provided
     * predicate (Guava).
     *
     * <blockquote><pre>{@code
     *
     *     final IAtomContainer query;
     *     final IAtomContainer target;
     *
     *     // obtain only the mappings where the first atom in the query is
     *     // mapped to the first atom in the target
     *     Mappings mappings = Pattern.findSubstructure(query)
     *                                .matchAll(target)
     *                                .filter(new Predicate<int[]>() {
     *                                    public boolean apply(int[] input) {
     *                                        return input[0] == 0;
     *                                    }});
     *
     * }</pre></blockquote>
     *
     * @param predicate a predicate
     * @return fluent-api reference
     */
    public Mappings filter(final Predicate<int[]> predicate) {
        return new Mappings(query, target, Iterables.filter(iterable, predicate));
    }

    /**
     * Map the mappings to another type. Each mapping is transformed using the
     * provided function.
     *
     * <blockquote><pre>{@code
     *
     *     final IAtomContainer query;
     *     final IAtomContainer target;
     *
     *     Mappings mappings = Pattern.findSubstructure(query)
     *                                .matchAll(target);
     *
     *     // a string that indicates the mapping of atom elements and numbers
     *     Iterable&lt;String&gt; strs = mappings.map(new Function<int[], String>() {
     *         public String apply(int[] input) {
     *             StringBuilder sb = new StringBuilder();
     *             for (int i = 0; i &lt; input.length; i++) {
     *                 if (i > 0) sb.append(", ");
     *                 sb.append(query.getAtom(i))
     *                   .append(i + 1)
     *                   .append(" -> ")
     *                   .append(target.getAtom(input[i]))
     *                   .append(input[i] + 1);
     *             }
     *             return sb.toString();
     *         }});
     *
     * }</pre></blockquote>
     *
     * @param f function to transform a mapping
     * @return iterable of the transformed type
     */
    public <T> Iterable<T> map(final Function<int[], T> f) {
        return Iterables.transform(iterable, f);
    }

    /**
     * Limit the number of mappings - only this number of mappings will be
     * generate.
     *
     * @param limit the number of mappings
     * @return fluent-api instance
     */
    public Mappings limit(int limit) {
        return new Mappings(query, target, Iterables.limit(iterable, limit));
    }

    /**
     * Filter the mappings for those which preserve stereochemistry specified in
     * the query.
     *
     * @return fluent-api instance
     * @deprecated Results now automatically consider stereo if it's present, to
     *             match without stereochemistry remove the stereo features.
     */
    @Deprecated
    public Mappings stereochemistry() {
        // query structures currently have special requirements (i.e. SMARTS)
        if (query instanceof IQueryAtomContainer) return this;
        return filter(new StereoMatch(query, target));
    }

    /**
     * Filter the mappings for those which cover a unique set of atoms in the
     * target. The unique atom mappings are a subset of the unique bond
     * matches.
     *
     * @return fluent-api instance
     * @see #uniqueBonds()
     */
    public Mappings uniqueAtoms() {
        // we need the unique predicate to be reset for each new iterator -
        // otherwise multiple iterations are always filtered (seen before)
        return new Mappings(query, target, new Iterable<int[]>() {

            @Override
            public Iterator<int[]> iterator() {
                return Iterators.filter(iterable.iterator(), new UniqueAtomMatches());
            }
        });
    }

    /**
     * Filter the mappings for those which cover a unique set of bonds in the
     * target.
     *
     * @return fluent-api instance
     * @see #uniqueAtoms()
     */
    public Mappings uniqueBonds() {
        // we need the unique predicate to be reset for each new iterator -
        // otherwise multiple iterations are always filtered (seen before)
        final int[][] g = GraphUtil.toAdjList(query);
        return new Mappings(query, target, new Iterable<int[]>() {

            @Override
            public Iterator<int[]> iterator() {
                return Iterators.filter(iterable.iterator(), new UniqueBondMatches(g));
            }
        });
    }

    /**
     * Mappings are lazily generated and best used in a loop. However if all
     * mappings are required this method can provide a fixed size array of
     * mappings.
     *
     * <blockquote><pre>
     *
     * IAtomContainer query  = ...;
     * IAtomContainer target = ...;
     *
     * Pattern pat = Pattern.findSubstructure(query);
     *
     * // lazily iterator
     * for (int[] mapping : pat.matchAll(target)) {
     *     // logic...
     * }
     *
     * int[][] mappings = pat.matchAll(target)
     *                       .toArray();
     *
     * // same as lazy iterator but we now can refer to and parse 'mappings'
     * // to other methods without regenerating the graph match
     * for (int[] mapping : mappings) {
     *     // logic...
     * }
     * </pre></blockquote>
     *
     * The method can be used in combination with other modifiers.
     *
     * <blockquote><pre>
     *
     * IAtomContainer query  = ...;
     * IAtomContainer target = ...;
     *
     * Pattern pat = Pattern.findSubstructure(query);
     *
     * // array of the first 5 unique atom mappings
     * int[][] mappings = pat.matchAll(target)
     *                       .uniqueAtoms()
     *                       .limit(5)
     *                       .toArray();
     * </pre></blockquote>
     *
     * @return array of mappings
     */
    public int[][] toArray() {
        int[][] res = new int[14][];
        int size = 0;
        for (int[] map : this) {
            if (size == res.length)
                res = Arrays.copyOf(res, size + (size >> 1));
            res[size++] = map.clone();
        }
        return Arrays.copyOf(res, size);
    }

    /**
     * Convert the permutations to a atom-atom map.
     *
     * <blockquote><pre>
     * for (Map&lt;IAtom,IAtom&gt; map : mappings.toAtomMap()) {
     *     for (Map.Entry&lt;IAtom,IAtom&gt; e : map.entrySet()) {
     *         IAtom queryAtom  = e.getKey();
     *         IAtom targetAtom = e.getValue();
     *     }
     * }
     * </pre></blockquote>
     *
     * @return iterable of atom-atom mappings
     */
    public Iterable<Map<IAtom, IAtom>> toAtomMap() {
        return map(new ToAtomMap(query, target));
    }

    /**
     * Convert the permutations to a bond-bond map.
     *
     * <blockquote><pre>
     * for (Map&lt;IBond,IBond&gt; map : mappings.toBondMap()) {
     *     for (Map.Entry&lt;IBond,IBond&gt; e : map.entrySet()) {
     *         IBond queryBond  = e.getKey();
     *         IBond targetBond = e.getValue();
     *     }
     * }
     * </pre></blockquote>
     *
     * @return iterable of bond-bond mappings
     */
    public Iterable<Map<IBond, IBond>> toBondMap() {
        return map(new ToBondMap(query, target));
    }

    /**
     * Convert the permutations to an atom-atom bond-bond map.
     *
     * <blockquote><pre>
     * for (Map&lt;IChemObject,IChemObject&gt; map : mappings.toBondMap()) {
     *     for (Map.Entry&lt;IChemObject,IChemObject&gt; e : map.entrySet()) {
     *         IChemObject queryObj  = e.getKey();
     *         IChemObject targetObj = e.getValue();
     *     }
     *
     *     IAtom matchedAtom = map.get(query.getAtom(i));
     *     IBond matchedBond = map.get(query.getBond(i));
     * }
     * </pre></blockquote>
     *
     * @return iterable of atom-atom and bond-bond mappings
     */
    public Iterable<Map<IChemObject, IChemObject>> toAtomBondMap() {
        return map(new ToAtomBondMap(query, target));
    }

    /**
     * Obtain the chem objects (atoms and bonds) that have 'hit' in the target molecule.
     *
     * <blockquote><pre>
     * for (IChemObject obj : mappings.toChemObjects()) {
     *   if (obj instanceof IAtom) {
     *      // this atom was 'hit' by the pattern
     *   }
     * }
     * </pre></blockquote>
     *
     * @return lazy iterable of chem objects
     */
    public Iterable<IChemObject> toChemObjects() {
        return FluentIterable.from(map(new ToAtomBondMap(query, target)))
                             .transformAndConcat(new Function<Map<IChemObject, IChemObject>, Iterable<? extends IChemObject>>() {
            @Override
            public Iterable<? extends IChemObject> apply(Map<IChemObject, IChemObject> map) {
                return map.values();
            }
        });
    }

    /**
     * Obtain the mapped substructures (atoms/bonds) of the target compound. The atoms
     * and bonds are the same as in the target molecule but there may be less of them.
     *
     * <blockquote><pre>
     * IAtomContainer query, target
     * Mappings mappings = ...;
     * for (IAtomContainer mol : mol.toSubstructures()) {
     *    for (IAtom atom : mol.atoms())
     *      target.contains(atom); // always true
     *    for (IAtom atom : target.atoms())
     *      mol.contains(atom): // not always true
     * }
     * </pre></blockquote>
     *
     * @return lazy iterable of molecules
     */
    public Iterable<IAtomContainer> toSubstructures() {
        return FluentIterable.from(map(new ToAtomBondMap(query, target)))
                             .transform(new Function<Map<IChemObject, IChemObject>, IAtomContainer>() {
                                 @Override
                                 public IAtomContainer apply(Map<IChemObject, IChemObject> map) {
                                     final IAtomContainer submol = target.getBuilder()
                                                                         .newInstance(IAtomContainer.class,
                                                                                      query.getAtomCount(), target.getBondCount(), 0, 0);
                                     for (IAtom atom : query.atoms())
                                         submol.addAtom((IAtom)map.get(atom));
                                     for (IBond bond : query.bonds())
                                         submol.addBond((IBond)map.get(bond));
                                     return submol;
                                 }
                             });
    }

    /**
     * Efficiently determine if there are at least 'n' matches
     *
     * <blockquote><pre>
     * Mappings mappings = ...;
     *
     * if (mappings.atLeast(5))
     *    // set bit flag etc.
     *
     * // are the at least 5 unique matches?
     * if (mappings.uniqueAtoms().atLeast(5))
     *    // set bit etc.
     * </pre></blockquote>
     *
     * @param n number of matches
     * @return there are at least 'n' matches
     */
    public boolean atLeast(int n) {
        return limit(n).count() == n;
    }

    /**
     * Obtain the first match - if there is no first match an empty array is
     * returned.
     *
     * @return first match
     */
    public int[] first() {
        return Iterables.getFirst(iterable, new int[0]);
    }

    /**
     * Convenience method to count the number mappings. Note mappings are lazily
     * generated and checking the count and then iterating over the mappings
     * currently performs two searches. If the mappings are also needed, it is
     * more efficient to check the mappings and count manually.
     *
     * @return number of matches
     */
    public int count() {
        return Iterables.size(iterable);
    }

    /**
     * Convenience method to count the number of unique atom mappings. Note
     * mappings are lazily generated and checking the count and then iterating
     * over the mappings currently performs two searches. If the mappings are
     * also needed, it is more efficient to check the mappings and count
     * manually.
     *
     * The method is simply invokes <pre>mappings.uniqueAtoms().count()</pre>.
     *
     * @return number of matches
     */
    public int countUnique() {
        return uniqueAtoms().count();
    }

    /**{@inheritDoc} */
    @Override
    public Iterator<int[]> iterator() {
        return iterable.iterator();
    }

    /** Utility to transform a permutation into the atom-atom map. */
    private final class ToAtomMap implements Function<int[], Map<IAtom, IAtom>> {

        /** Query/target containers from the graph matching. */
        private final IAtomContainer query, target;

        /**
         * Use the provided query and target to obtain the atom instances.
         *
         * @param query  the structure to be found
         * @param target the structure being searched
         */
        private ToAtomMap(IAtomContainer query, IAtomContainer target) {
            this.query = query;
            this.target = target;
        }

        /**{@inheritDoc} */
        @Override
        public Map<IAtom, IAtom> apply(int[] mapping) {
            ImmutableMap.Builder<IAtom, IAtom> map = ImmutableMap.builder();
            for (int i = 0; i < mapping.length; i++)
                map.put(query.getAtom(i), target.getAtom(mapping[i]));
            return map.build();
        }
    }

    /** Utility to transform a permutation into the bond-bond map. */
    private final class ToBondMap implements Function<int[], Map<IBond, IBond>> {

        /** The query graph - indicates a presence of edges. */
        private final int[][] g1;

        /** Bond look ups for the query and target. */
        private final GraphUtil.EdgeToBondMap bonds1, bonds2;

        /**
         * Use the provided query and target to obtain the bond instances.
         *
         * @param query  the structure to be found
         * @param target the structure being searched
         */
        private ToBondMap(IAtomContainer query, IAtomContainer target) {
            this.bonds1 = GraphUtil.EdgeToBondMap.withSpaceFor(query);
            this.bonds2 = GraphUtil.EdgeToBondMap.withSpaceFor(target);
            this.g1 = GraphUtil.toAdjList(query, bonds1);
            GraphUtil.toAdjList(target, bonds2);
        }

        /**{@inheritDoc} */
        @Override
        public Map<IBond, IBond> apply(int[] mapping) {
            ImmutableMap.Builder<IBond, IBond> map = ImmutableMap.builder();
            for (int u = 0; u < g1.length; u++) {
                for (int v : g1[u]) {
                    if (v > u) {
                        map.put(bonds1.get(u, v), bonds2.get(mapping[u], mapping[v]));
                    }
                }
            }
            return map.build();
        }
    }

    /** Utility to transform a permutation into an atom-atom and bond-bond map. */
    private final class ToAtomBondMap implements Function<int[], Map<IChemObject, IChemObject>> {

        /** The query graph - indicates a presence of edges. */
        private final int[][] g1;

        /** Bond look ups for the query and target. */
        private final GraphUtil.EdgeToBondMap bonds1, bonds2;

        /**
         * Use the provided query and target to obtain the bond instances.
         *
         * @param query  the structure to be found
         * @param target the structure being searched
         */
        private ToAtomBondMap(IAtomContainer query, IAtomContainer target) {
            this.bonds1 = GraphUtil.EdgeToBondMap.withSpaceFor(query);
            this.bonds2 = GraphUtil.EdgeToBondMap.withSpaceFor(target);
            this.g1 = GraphUtil.toAdjList(query, bonds1);
            GraphUtil.toAdjList(target, bonds2);
        }

        /**{@inheritDoc} */
        @Override
        public Map<IChemObject, IChemObject> apply(int[] mapping) {
            ImmutableMap.Builder<IChemObject, IChemObject> map = ImmutableMap.builder();
            for (int u = 0; u < g1.length; u++) {
                map.put(query.getAtom(u), target.getAtom(mapping[u]));
                for (int v : g1[u]) {
                    if (v > u) {
                        map.put(bonds1.get(u, v), bonds2.get(mapping[u], mapping[v]));
                    }
                }
            }
            return map.build();
        }
    }
}
