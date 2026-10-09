/* Copyright (C) 2026  Syed Asad Rahman <s9asad@gmail.com>
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
package org.openscience.cdk.isomorphism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

/**
 * Finds the maximum common substructure (MCS) of a query and a target
 * molecule: the connected common substructure with the most atoms and, of
 * those, the most common bonds. The common substructure need not be induced:
 * a bond present on only one side, or whose matching fails, is left out.
 * Every compatible bond between mapped atom pairs is common, and these common
 * bonds keep the mapped atoms connected. A result is a mapping with one entry
 * per query atom, the index of the target atom it maps to or -1. For
 * example, N-methylacetamide {@code CC(=O)NC} and propanamide
 * {@code CCC(=O)N}, neither a substructure of the other, share an acetamide
 * {@code CC(=O)N}. With the first as the query, the only maximum mapping is
 * {@code [1, 2, 3, 4, -1]}, which leaves out the methyl on the nitrogen.
 * <br><br>
 * Usage:
 * <pre>{@code
 * MCS mcs = MCS.find(query).withTimeout(10, TimeUnit.SECONDS);
 * int[] mapping = mcs.match(target);     // Intractable after 10 seconds
 * for (int i = 0; i < mapping.length; i++) {
 *     if (mapping[i] >= 0) {
 *         // query.getAtom(i) is mapped to target.getAtom(mapping[i])
 *     }
 * }
 *
 * // the maximum mappings, e.g. both ways round a ring
 * for (int[] p : mcs.matchAll(target)) {
 *     // p is a mapping as above
 * }
 * }</pre>
 * Each {@code with...} method returns a new instance and leaves the old one
 * as it is. An instance keeps no other state, so one instance can be used for
 * many targets and shared between threads, except for queries with recursive
 * SMARTS in two threads at once.
 * <ul>
 *     <li>{@link #withCompleteRings()} maps rings only whole.</li>
 *     <li>{@link #withStereochemistry()} checks stereochemistry.</li>
 *     <li>{@link #withDisconnected(int)} finds a maximum common edge
 *         subgraph, whose common bonds may form several fragments.</li>
 * </ul>
 * <b>Matching</b>
 * <br><br>
 * By default atoms match by symbol, so pseudo atoms (R groups, {@code *})
 * match each other and no other atom, and bonds match if both are aromatic,
 * or both are not aromatic and have the same order. With
 * {@link #withMatching(Expr.Type...)} atoms and bonds match by the
 * expressions {@link QueryAtomContainer#create(IAtomContainer, Expr.Type...)}
 * builds from the query instead, for example:
 * <ul>
 *     <li>{@code ELEMENT}: atoms by element, bond orders ignored; a pseudo
 *         atom of the query matches any atom, and an element does not match
 *         a pseudo atom of the target;</li>
 *     <li>{@code ELEMENT, SINGLE_OR_AROMATIC}: like the default, except for
 *         pseudo atoms;</li>
 *     <li>{@code ELEMENT, SINGLE_OR_AROMATIC, IS_IN_RING, IS_IN_CHAIN}: like
 *         the default, and ring atoms and bonds map only to ring atoms and
 *         bonds, chain atoms and bonds only to chain ones (set the ring flags
 *         of both molecules first);</li>
 *     <li>{@code ELEMENT, SINGLE_OR_AROMATIC, FORMAL_CHARGE} (or
 *         {@code ISOTOPE}): like the default, and the charge (mass number) of
 *         a query atom, where it is set, must agree.</li>
 * </ul>
 * A query molecule, for example one parsed from SMARTS, is matched by its own
 * atom and bond predicates ({@link IQueryAtom}, {@link IQueryBond}). These are
 * evaluated without the configuration of the overall mapping, so top-level
 * SMARTS stereochemistry is not checked, and {@link #withStereochemistry()}
 * applies only to molecule queries. A recursive SMARTS predicate is evaluated
 * as a substructure search of its own, including its stereochemistry. The
 * result is the largest connected part satisfying these predicates. The
 * target must be a molecule, not a query, prepared as for a SMARTS search.
 * <pre>{@code
 * IAtomContainer query = builder.newAtomContainer();
 * Smarts.parse(query, "c1ccccc1C(=O)[N,O]");
 * SmartsPattern.prepare(target);
 * int[] mapping = MCS.find(query).match(target);
 * }</pre>
 * <b>Preparing the molecules</b>
 * <ul>
 *     <li>Aromaticity and ring flags are used as they are set. Perceive
 *         aromaticity with the same model on both molecules.</li>
 *     <li>Explicit hydrogens are atoms like any other: they count in the
 *         score, so the heavy atoms of such an MCS can differ from the
 *         heavy-atom MCS, and they multiply the number of equally good
 *         mappings. Suppress them for a heavy-atom MCS.</li>
 *     <li>For molecule queries, stereochemistry is checked only with
 *         {@link #withStereochemistry()}, from the stereo elements as set.</li>
 * </ul>
 * <b>Limits</b>
 * <br><br>
 * The search is exact. Large or highly symmetric molecules can take a long
 * time, so a time limit is recommended; there is none by default. A search
 * of more than 2<sup>30</sup> atom pairs, query atoms times target atoms
 * (about 32,000 atoms in each molecule), is refused. Searches requiring
 * more than {@link Integer#MAX_VALUE} entries in a bond adjacency or
 * compatibility table are also refused.
 * {@link #matchAll(IAtomContainer)} returns at most 1000 mappings,
 * {@link #matchAll(IAtomContainer, int)} as many as asked for. With
 * {@link #withCompleteRings()}, a ring system with more than 1024 relevant
 * rings, or a very large fused one such as a graphene sheet, is not searched.
 * <br><br>
 * <b>References</b>
 * <ul>
 *     <li>{@cdk.cite SMSD2009}</li>
 *     <li>{@cdk.cite SMSDPro2026}</li>
 *     <li>{@cdk.cite Cordella04}</li>
 * </ul>
 *
 * @author Syed Asad Rahman
 * @cdk.keyword maximum common substructure
 * @cdk.keyword MCS
 * @see Pattern
 * @see QueryAtomContainer#create(IAtomContainer, Expr.Type...)
 */
public final class MCS {

    /** The most atom pairs a search takes, query atoms times target atoms, as a pair is packed in an int. */
    private static final long MAX_PAIRS = 1L << 30;
    private static final int MAX_MAPPINGS = 1000;

    private final IAtomContainer query;
    /** The expression types for {@link QueryAtomContainer#create}, or null for the default matching. */
    private final Expr.Type[] matching;
    /** The time limit in nanoseconds, or -1 for none. */
    private final long timeLimit;
    private final boolean completeRings, checkStereo;
    /** The fewest common bonds of a fragment of a disconnected MCS, or 0 for the connected MCS. */
    private final int minBonds;

    private MCS(IAtomContainer query, Expr.Type[] matching, long timeLimit, boolean completeRings,
                boolean checkStereo, int minBonds) {
        this.query = query;
        this.matching = matching;
        this.timeLimit = timeLimit;
        this.completeRings = completeRings;
        this.checkStereo = checkStereo;
        this.minBonds = minBonds;
    }

    /**
     * Create an MCS search for the {@code query} with the default matching
     * and no time limit.
     *
     * @param query the query molecule
     * @return an MCS search for the query
     */
    public static MCS find(IAtomContainer query) {
        return new MCS(Objects.requireNonNull(query, "query"), null, -1, false, false, 0);
    }

    /**
     * Match atoms and bonds with the expressions that
     * {@link QueryAtomContainer#create(IAtomContainer, Expr.Type...)} makes
     * from the query and {@code opts}, at the start of each search. For
     * example {@code withMatching(ELEMENT)} ignores bond orders, and
     * {@code withMatching(ELEMENT, IS_IN_RING)} also maps ring atoms and bonds
     * of the query only to ring atoms and bonds. Without any types every atom
     * and bond matches. {@code STEREOCHEMISTRY} has no effect, see
     * {@link #withStereochemistry()}.
     *
     * @param opts the expression types to match
     * @return a new MCS search with this matching
     * @throws IllegalArgumentException the query has query atoms or bonds,
     *                                  which are matched by their own
     *                                  expressions
     */
    public MCS withMatching(Expr.Type... opts) {
        requireMolecule();
        Expr.Type[] copy = opts.clone();
        for (Expr.Type opt : copy) {
            Objects.requireNonNull(opt, "opts");
        }
        return new MCS(query, copy, timeLimit, completeRings, checkStereo, minBonds);
    }

    /**
     * Stop each search that runs longer than the given time, see
     * {@link #match(IAtomContainer)}. The limit is checked as the search
     * runs, including while it is set up, so a search can take slightly
     * longer. Building the query and target graphs beforehand, and the query
     * expressions of {@link #withMatching(Expr.Type...)}, is not counted.
     *
     * @param time the time limit, greater than zero
     * @param unit the unit of the time limit
     * @return a new MCS search with this time limit
     * @throws IllegalArgumentException the time limit is not greater than zero
     */
    public MCS withTimeout(long time, TimeUnit unit) {
        if (time <= 0) {
            throw new IllegalArgumentException("The time limit must be greater than zero: " + time);
        }
        return new MCS(query, matching, Objects.requireNonNull(unit, "unit").toNanos(time), completeRings, checkStereo,
                       minBonds);
    }

    /**
     * Map rings only whole: a ring atom or ring bond of either molecule is in
     * the common substructure only as part of a ring of that molecule whose
     * bonds are all common. This covers every common ring bond, including
     * additional bonds between mapped atoms. So ring atoms and bonds map only
     * onto ring ones, chain onto chain, and a lone ring atom, such as the ring
     * carbon a substituent is attached to, is left out. Rings are the relevant
     * rings {@cdk.cite Vismara97}, those of every smallest set of smallest
     * rings: benzene is a whole ring of naphthalene, cyclohexane is not one of
     * norbornane. This option neither reads nor sets ring flags. Perceive
     * aromaticity first: under the default matching, a ring drawn with other
     * alternating single and double bonds is not whole. The rings are found
     * as the graphs are built, which the time limit does not cover, so a very
     * large fused ring system can take seconds before the search starts.
     *
     * @return a new MCS search that maps only complete rings
     */
    public MCS withCompleteRings() {
        return new MCS(query, matching, timeLimit, true, checkStereo, minBonds);
    }

    /**
     * Check stereochemistry: a mapping must keep the configuration of each
     * tetrahedral centre and double bond that both molecules specify, as far as
     * the mapped atoms fix it. A centre is fixed once it and three of its
     * neighbours are mapped onto a centre and its neighbours, a double bond once
     * both ends and a neighbour of each end are. The arrangement of the mapped
     * neighbours is compared, not CIP labels. A configuration that only one
     * molecule specifies matches either way, and a racemic or relative group, as
     * read from a molfile without the chiral flag, may be inverted as a whole.
     * Other stereochemistry, such as allenes and atropisomers, is not checked.
     * This is a hard constraint: a mapping that fixes a configuration the
     * other way round is not admissible at all, so the result is the largest
     * stereo-consistent match, which can be smaller than the MCS found
     * without this option.
     *
     * @return a new MCS search that checks stereochemistry
     * @throws IllegalArgumentException the query has query atoms or bonds,
     *                                  as this option applies only to molecule
     *                                  queries
     */
    public MCS withStereochemistry() {
        requireMolecule();
        return new MCS(query, matching, timeLimit, completeRings, true, minBonds);
    }

    /**
     * Find a maximum common edge subgraph {@cdk.cite Raymond02} instead: the
     * mapping with the most common bonds and, of those, the most atoms. The
     * common bonds may form several fragments, each a connected piece of at
     * least {@code minBonds} common bonds, anywhere in either molecule. An
     * atom without a common bond is not mapped. A changed linker or a broken
     * bond then costs only the bonds involved. Under the default matching a
     * bond whose order changes is not common, so for reaction mapping ignore
     * bond orders with {@code withMatching(ELEMENT)}. This search is slower
     * than the connected one, so set a time limit.
     *
     * @param minBonds the fewest common bonds of a fragment, at least 1
     * @return a new MCS search for disconnected fragments
     * @throws IllegalArgumentException {@code minBonds} is less than 1
     */
    public MCS withDisconnected(int minBonds) {
        if (minBonds < 1) {
            throw new IllegalArgumentException("A fragment must have at least one bond: " + minBonds);
        }
        return new MCS(query, matching, timeLimit, completeRings, checkStereo, minBonds);
    }

    /**
     * Find a maximum common substructure of the query and the {@code target}.
     * The mapping has one entry per query atom: the index of the target atom
     * it is mapped to, or -1 if the atom is not in the common substructure.
     * If there is no common substructure, for example no atom in common, an
     * empty array is returned.
     *
     * @param target the target molecule
     * @return the mapping from the query to the target atoms
     * @throws Intractable the time limit was reached, the thread was
     *                     interrupted, or the molecules are too large
     */
    public int[] match(IAtomContainer target) throws Intractable {
        List<int[]> mappings = search(target, 1);
        return mappings.isEmpty() ? new int[0] : mappings.get(0);
    }

    /**
     * Find every maximum common substructure mapping of the query and the
     * {@code target}, up to 1000 of them; symmetric molecules can have far
     * more. All have the same number of atoms and common bonds.
     *
     * @param target the target molecule
     * @return the mappings from the query to the target atoms, see
     *         {@link #match(IAtomContainer)}; an unmodifiable list, empty if
     *         there is no common substructure
     * @throws Intractable the time limit was reached, the thread was
     *                     interrupted, or the molecules are too large
     */
    public List<int[]> matchAll(IAtomContainer target) throws Intractable {
        return matchAll(target, MAX_MAPPINGS);
    }

    /**
     * Find maximum common substructure mappings of the query and the
     * {@code target}, at most {@code limit} of them. Each is a maximum mapping,
     * and if there are no more than {@code limit}, all of them are returned;
     * {@link #matchAll(IAtomContainer)} has a limit of 1000. A smaller limit can
     * end the search sooner.
     *
     * @param target the target molecule
     * @param limit  the most mappings to return, greater than zero
     * @return the mappings from the query to the target atoms, see
     *         {@link #match(IAtomContainer)}; an unmodifiable list, empty if
     *         there is no common substructure
     * @throws IllegalArgumentException the limit is not greater than zero
     * @throws Intractable the time limit was reached, the thread was
     *                     interrupted, or the molecules are too large
     */
    public List<int[]> matchAll(IAtomContainer target, int limit) throws Intractable {
        if (limit <= 0) {
            throw new IllegalArgumentException("The limit must be greater than zero: " + limit);
        }
        return Collections.unmodifiableList(search(target, limit));
    }

    /** The mappings of a search, at most {@code maxMappings}, from the query to the target atoms. */
    private List<int[]> search(IAtomContainer target, int maxMappings) throws Intractable {
        Objects.requireNonNull(target, "target");
        if ((long) query.getAtomCount() * target.getAtomCount() > MAX_PAIRS) {
            throw new Intractable("MCS search too large: " + query.getAtomCount() + " x "
                    + target.getAtomCount() + " atoms");
        }
        // The smaller molecule makes the faster query. The mappings are turned round afterwards.
        boolean swap = matching == null && query.getAtomCount() > target.getAtomCount()
                && !hasQueryFeatures(query) && !hasQueryFeatures(target);
        int queryBonds = swap ? target.getBondCount() : query.getBondCount();
        int targetBonds = swap ? query.getBondCount() : target.getBondCount();
        // The two adjacency entries of each bond, and the rows of compatible bond pairs, must fit an int index.
        if (2L * queryBonds > Integer.MAX_VALUE || 2L * targetBonds > Integer.MAX_VALUE
            || queryBonds * ((targetBonds + 63L) >>> 6) > Integer.MAX_VALUE) {
            throw new Intractable("MCS search too large: " + queryBonds + " x " + targetBonds + " bonds");
        }
        IAtomContainer matched = matching != null ? QueryAtomContainer.create(query, matching) : query;
        MCSGraph graph = swap ? new MCSGraph(target, target, query, completeRings, checkStereo)
                              : new MCSGraph(matched, query, target, completeRings, checkStereo);
        int refused = completeRings ? Math.max(graph.query.rings.refusedAtoms, graph.target.rings.refusedAtoms) : 0;
        if (refused > 0) {
            throw new Intractable("MCS search too large: a ring system of " + refused + " atoms has too many rings");
        }
        long start = System.nanoTime();
        Thread thread = Thread.currentThread();
        MCSGraph.Clock clock = new MCSGraph.Clock(
                () -> thread.isInterrupted() || timeLimit >= 0 && System.nanoTime() - start > timeLimit);
        List<int[]> found;
        try {
            found = new MCSSearch(graph, clock, minBonds).run(maxMappings);
        } catch (MCSGraph.Stop e) {
            if (thread.isInterrupted()) {
                throw new Intractable("MCS search was interrupted");
            }
            throw Intractable.timeout("MCS search", TimeUnit.NANOSECONDS.toMillis(timeLimit));
        }
        if (!swap) {
            return found;
        }
        // The search ran from the target to the query.
        List<int[]> mappings = new ArrayList<>(found.size());
        for (int[] inverse : found) {
            int[] mapping = new int[query.getAtomCount()];
            Arrays.fill(mapping, -1);
            for (int i = 0; i < inverse.length; i++) {
                if (inverse[i] >= 0) {
                    mapping[inverse[i]] = i;
                }
            }
            mappings.add(mapping);
        }
        return mappings;
    }

    /** Refuses an option that applies only to a query molecule without query atoms or bonds. */
    private void requireMolecule() {
        if (hasQueryFeatures(query)) {
            throw new IllegalArgumentException("A query molecule is matched by its own expressions");
        }
    }

    /** Whether the molecule is a query container or has a query atom or bond. */
    private static boolean hasQueryFeatures(IAtomContainer molecule) {
        if (molecule instanceof IQueryAtomContainer) {
            return true;
        }
        for (IAtom atom : molecule.atoms()) {
            if (atom instanceof IQueryAtom) {
                return true;
            }
        }
        for (IBond bond : molecule.bonds()) {
            if (bond instanceof IQueryBond) {
                return true;
            }
        }
        return false;
    }
}
