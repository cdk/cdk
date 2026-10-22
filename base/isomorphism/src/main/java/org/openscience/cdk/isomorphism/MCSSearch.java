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
import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;

/**
 * The search behind {@link MCS}, on the int graphs of an {@link MCSGraph}. It
 * finds the connected mappings from the query to the target with the most
 * atoms and then the most common bonds or, for a maximum common edge subgraph
 * (MCES) whose common bonds may form several fragments, the most common bonds
 * and then the most atoms. When the whole query embeds in the target, the
 * embeddings are the answer ({@link Embedding}). Otherwise the query atoms are
 * ranked, fewest candidates first, greedy seeds give a score to beat, and a
 * depth-first branch and bound tries each query atom as a root, highest rank
 * first, and grows the mapping pair by pair on an explicit stack of
 * {@link Level}s, one for each mapped pair. After the first pair of a fragment,
 * each pair maps a query atom next to the fragment onto a free neighbour of the
 * image of one of its mapped neighbours, read from the neighbour lists as
 * {@link DfPattern} does. A level holds its pair, what taking it off restores,
 * the candidates and targets it has left to try, and a bound: from the pairs
 * its fragment reaches ({@link Reach}), in MCES from the bond labels of the open
 * atoms ({@link EdgeBound}) and, for large molecules, from the branches at chain
 * atoms ({@link BridgeBound}). A level that cannot beat the best score, or tie
 * with it while fewer mappings than wanted are held, is taken off, and the pair
 * it tried is excluded below its later siblings, so each mapping is found once.
 * A mapping that beats the best score replaces the mappings held, and one that
 * ties is added. Every loop ticks the {@link MCSGraph.Clock}, which throws
 * {@link MCSGraph.Stop} when the time limit is reached or the thread is
 * interrupted; the best mappings found by then are kept ({@link #found}). The
 * search follows SMSD {@cdk.cite SMSD2009}, and its label bound is that of
 * SMSD Pro {@cdk.cite SMSDPro2026}.
 *
 * @author Syed Asad Rahman
 */
final class MCSSearch {

    /** The most greedy seeds grown. */
    private static final int SEEDS = 64;
    /** The most free targets a seed looks at next to a hub. */
    private static final int HUB_TARGETS = 64;
    /** The levels of likeness of two atoms, from no radius alike to all of them. */
    private static final int LIKENESS_LEVELS = MCSGraph.RADII + 1;
    /** The fewest atoms in each molecule for the bridge bound. On smaller ones it costs more than it saves. */
    private static final int BRIDGE_BOUND_ATOMS = 64;
    /** The further pairs the connected bound reaches at a time. */
    private static final int REACH_BATCH = 64;
    /** The first capacity of a list of pairs that grows as needed. */
    private static final int INITIAL_PAIRS = 64;
    /** An empty list of atoms, shared by every level that has nothing to try. */
    private static final int[] NONE = new int[0];

    /** The graphs of both molecules, with their compatible pairs, bond classes and configurations. */
    private final MCSGraph graph;
    /** The query and target graphs of {@link #graph}. */
    private final MCSGraph.Side query, target;
    /** Counts the work of every loop and ends the search with {@link MCSGraph.Stop} when told to. */
    private final MCSGraph.Clock clock;
    /** In MCES, the fewest common bonds of a fragment; 0 for the connected MCS. */
    private final int minBonds;
    /** The numbers of query and target atoms, and of longs in a row of target atoms. */
    private final int queryCount, targetCount, words;
    /**
     * The compatible pairs that are not excluded: a row for each query atom,
     * with a bit for each target atom, see {@link MCSGraph#compatiblePairs}.
     */
    private final long[] allowed;
    /** The mapping both ways, -1 where unmapped. */
    private final int[] queryToTarget, targetToQuery;
    /** The mapped pairs in the order they were mapped, the first {@link #depth} of the array. */
    private final Level[] levels;
    /** The number of mapped pairs, and the common bonds between them. */
    private int depth, commonBonds;
    /** The pairs excluded so far, the first {@link #excludedCount}, put back as the levels that tried them go. */
    private int[] excluded = new int[INITIAL_PAIRS];
    /** The number of excluded pairs. */
    private int excludedCount;
    /** The atoms by rank, lowest first, and the rank of each atom, see {@link #rank}. */
    private int[] queryOrder, queryRank, targetOrder, targetRank;
    /** The cell of each atom: atoms that look alike share a cell. */
    private int[] queryCell, targetCell;
    /** The signatures of the atoms at each radius, which tell how alike two atoms are. */
    private long[][] querySignature, targetSignature;
    /** The next atom alike to each terminal atom, see {@link #twins}. */
    private int[] queryTwin, targetTwin;
    /** The best score so far, see {@link #score}, and a score that no mapping beats. */
    private long bestScore, upperBound;
    /** The mappings held: the embeddings found, or those with the best score so far, see {@link #record}. */
    private List<int[]> mappings;
    /** The most mappings wanted. */
    private int maxMappings;
    /** The seed, held at the front of the mappings from the start until the search finds it again. */
    private int[] heldSeed;
    /**
     * The mapping a stopped search falls back on while the pass under way
     * holds none: the seed or, in MCES, the first mapping of the best score;
     * null until the seed is grown. See {@link #found}.
     */
    private int[] fallback;
    /** The pairs reached from a state, for its bound. */
    private final Reach reach;
    /** The bound in MCES, or null in connected mode. */
    private final EdgeBound edgeBound;
    /**
     * The bound from the branches at chain atoms, see {@link BridgeBound}, or
     * null. It is built only when the whole query does not embed, as building
     * it can take far longer than finding an embedding.
     */
    private BridgeBound bridgeBound;

    /**
     * A search of {@code graph}, whose compatible pairs are worked out here.
     * With {@code minBonds} above 0 the search finds an MCES whose fragments
     * have that many common bonds or more.
     */
    MCSSearch(MCSGraph graph, MCSGraph.Clock clock, int minBonds) {
        this.graph = graph;
        this.clock = clock;
        this.minBonds = minBonds;
        query = graph.query;
        target = graph.target;
        queryCount = query.atomCount;
        targetCount = target.atomCount;
        words = MCSGraph.words(targetCount);
        allowed = graph.compatiblePairs(clock);
        queryToTarget = new int[queryCount];
        targetToQuery = new int[targetCount];
        Arrays.fill(queryToTarget, -1);
        Arrays.fill(targetToQuery, -1);
        levels = new Level[Math.min(queryCount, targetCount)];
        reach = new Reach();
        edgeBound = minBonds > 0 ? new EdgeBound() : null;
    }

    /**
     * The maximum mappings, at most {@code maxMappings}. There is none when no
     * pair is compatible or, in MCES, when either molecule has fewer bonds
     * than a fragment needs. When the whole query embeds, the embeddings are
     * the result; that search checks the time limit and stereochemistry as it
     * goes, and complete rings on each embedding. Otherwise the atoms are
     * ranked, a greedy seed gives the score to start from, and the branch and
     * bound collects the mappings.
     */
    List<int[]> run(int maxMappings) {
        clock.poll();
        if (!anyCompatible() || Math.min(query.bondCount, target.bondCount) < minBonds)
            return new ArrayList<>();
        Embedding embedding = embedding();
        List<int[]> embeddings = embedding != null ? embedding.all(maxMappings) : new ArrayList<>();
        if (!embeddings.isEmpty())
            return embeddings;
        rank();
        long bound = bestPossible();
        int[] seed = seed(bound);
        // A seed without complete rings is dropped. The search then starts from nothing.
        if (seed != null && query.rings != null && !complete(seed, false))
            seed = null;
        fallback = seed;
        long start = seed != null ? score(seed) : belowEveryMapping();
        return minBonds == 0 ? collectConnected(seed, start, bound, maxMappings)
                             : collectDisconnected(seed, start, bound, maxMappings);
    }

    /**
     * The best mappings found so far, for a search the clock stopped: the
     * mappings held or, while the pass under way holds none, the one it falls
     * back on. Empty when nothing was found. Each is complete, as a mapping is
     * held only once it is recorded whole.
     */
    List<int[]> found() {
        if (mappings != null && !mappings.isEmpty())
            return mappings;
        if (fallback != null)
            return Collections.singletonList(fallback);
        return Collections.emptyList();
    }

    /**
     * The connected mappings, at most {@code maxMappings}, from the score
     * {@code start} that the {@code seed} holds. The bridge bound is built
     * only when both molecules are large and the seed does not reach the
     * {@code bound}, as it costs more than a search that ends at once.
     */
    private List<int[]> collectConnected(int[] seed, long start, long bound, int maxMappings) {
        if (start < bound && Math.min(queryCount, targetCount) >= BRIDGE_BOUND_ATOMS)
            bridgeBound = BridgeBound.create(graph, clock);
        if (bridgeBound != null)
            bound = Math.min(bound, bridgeBound.upperBound);
        return collect(seed, start, bound, maxMappings);
    }

    /**
     * The MCES mappings, at most {@code maxMappings}. The best score is found
     * first, level by level of common bonds down from the {@code bound} to the
     * score {@code start} that the {@code seed} holds: each level asks for one
     * mapping with at least that many bonds, and without one the bound comes
     * down. Twins can then be skipped all along, see {@link #twinExcluded}. A
     * second search collects the mappings of that score in the order a search
     * from the seed finds them.
     */
    private List<int[]> collectDisconnected(int[] seed, long start, long bound, int maxMappings) {
        List<int[]> mapping = new ArrayList<>();
        for (int triedBonds = bonds(bound); mapping.isEmpty() && triedBonds > bonds(start); triedBonds--) {
            mapping = collect(null, score(0, triedBonds) - 1, bound, 1);
            // No mapping has that many bonds. With one bond fewer there are at most as many atoms, or one more for
            // each fragment of minBonds bonds.
            if (mapping.isEmpty())
                bound = score(Math.min(atoms(bound), triedBonds - 1 + (triedBonds - 1) / minBonds), triedBonds - 1);
        }
        if (mapping.isEmpty())
            mapping = collect(seed, start, bound, 1);
        if (maxMappings == 1)
            return mapping;
        if (bestScore == start)
            return collect(seed, start, bestScore, maxMappings);
        // The mapping found stands in for the seed until the second search finds one of its score.
        fallback = mapping.get(0);
        return collect(null, bestScore - 1, bestScore, maxMappings);
    }

    /** A score that no mapping beats, when the query does not fit whole. */
    private long bestPossible() {
        long bound = score(Math.min(queryCount, targetCount), Math.min(query.bondCount, target.bondCount));
        // Without an embedding no mapping has every query atom and bond. In MCES a query in parts may still have
        // every bond.
        if (minBonds > 0 ? queryConnected() : query.bondCount > 0)
            bound = Math.min(bound, score(queryCount, query.bondCount - 1));
        long label = graph.labelBound(minBonds == 0);
        if (label >= 0) {
            int labelAtoms = MCSGraph.atoms(label, false), labelBonds = MCSGraph.bonds(label, false);
            // In MCES a fragment of b common bonds has at most b + 1 atoms. Each has at least minBonds bonds, so
            // there are at most labelBonds / minBonds fragments.
            if (minBonds > 0)
                labelAtoms = Math.min(labelAtoms, labelBonds + labelBonds / minBonds);
            bound = Math.min(bound, score(labelAtoms, labelBonds));
        }
        return bound;
    }

    /** A score below that of every mapping. In MCES every mapping has at least minBonds common bonds. */
    private long belowEveryMapping() {
        return minBonds > 0 ? score(queryCount + 1, minBonds - 1) : 0;
    }

    /** Whether any pair of atoms is compatible. */
    private boolean anyCompatible() {
        for (long word : allowed) {
            if (word != 0)
                return true;
        }
        return false;
    }

    /**
     * Ranks the atoms and works out their signatures. Query atoms with the
     * fewest candidates, then the most bonds, rank highest; ties are broken by
     * colour refinement, so that the order is the same for the same graph.
     * The search tries the roots from the highest rank down, and below a root
     * maps only atoms of lower rank. Target atoms are ranked by their cell
     * only, in input order within it.
     */
    void rank() {
        querySignature = query.signatures(clock);
        targetSignature = target.signatures(clock);
        int[] queryChoices = new int[queryCount], targetChoices = new int[targetCount];
        for (int queryAtom = 0; queryAtom < queryCount; queryAtom++) {
            for (int targetAtom = nextBit(allowed, queryAtom * words, words, 0); targetAtom >= 0;
                 targetAtom = nextBit(allowed, queryAtom * words, words, targetAtom + 1)) {
                clock.tick();
                queryChoices[queryAtom]++;
                targetChoices[targetAtom]++;
            }
        }
        int[] colours = query.invariants(queryChoices);
        queryCell = MCSGraph.refine(query.start, query.neighbour, colours, false, clock);
        int[] canon = MCSGraph.refine(query.start, query.neighbour, colours, true, clock);
        // Ties among target atoms stay in input order. Breaking them one atom at a time costs a round per atom on a
        // symmetric target, and the order only has to be repeatable.
        targetCell = MCSGraph.refine(target.start, target.neighbour, target.invariants(targetChoices), false, clock);
        int[] next = new int[targetCount];
        targetRank = new int[targetCount];
        for (int targetAtom = 0; targetAtom < targetCount; targetAtom++)
            next[targetCell[targetAtom]] = targetCell[targetAtom];
        for (int targetAtom = 0; targetAtom < targetCount; targetAtom++)
            targetRank[targetAtom] = next[targetCell[targetAtom]]++;
        targetOrder = inverse(targetRank);
        Integer[] byRank = new Integer[queryCount];
        for (int queryAtom = 0; queryAtom < queryCount; queryAtom++)
            byRank[queryAtom] = queryAtom;
        // The most choices come first, then the fewest bonds, then the lowest canonical rank.
        Comparator<Integer> byChoices = Comparator.comparingInt(queryAtom -> -queryChoices[queryAtom]);
        Arrays.sort(byRank, byChoices.thenComparingInt(query::degree).thenComparingInt(queryAtom -> canon[queryAtom]));
        queryOrder = new int[queryCount];
        queryRank = new int[queryCount];
        for (int rank = 0; rank < queryCount; rank++) {
            queryOrder[rank] = byRank[rank];
            queryRank[queryOrder[rank]] = rank;
        }
    }

    /**
     * The best greedy seed, or null if there is none. A seed is a connected
     * mapping grown from a root pair, from each of at most {@link #SEEDS} of
     * them or until one reaches {@code bound}. In MCES it is a set of
     * fragments of at least {@code minBonds} common bonds, grown one after
     * another on the free atoms. It needs {@link #rank} first.
     */
    int[] seed(long bound) {
        long[] taken = new long[queryCount * words];
        int[] roots = new int[LIKENESS_LEVELS * SEEDS], rootCount = new int[LIKENESS_LEVELS];
        int[] frontier = new int[queryCount], mapped = new int[queryCount], added = new int[queryCount];
        long bestSeed = belowEveryMapping();
        int[] best = null;
        boolean extend = true;
        for (int round = 0, keptAtoms = 0, keptBonds = 0; ; ) {
            long before = bestSeed;
            seedRoots(roots, rootCount, taken);
            for (int level = MCSGraph.RADII, tried = 0; level >= 0 && bestSeed != bound; level--) {
                for (int rootIndex = 0; rootIndex < rootCount[level] && tried < SEEDS && bestSeed != bound;
                     rootIndex++, tried++) {
                    long grown = growSeed(roots[level * SEEDS + rootIndex], frontier, mapped, added, ++round);
                    long kept = cutBack(grown, mapped);
                    int seedAtoms = atoms(kept), seedBonds = bonds(kept);
                    if (seedBonds >= minBonds && score(keptAtoms + seedAtoms, keptBonds + seedBonds) > bestSeed) {
                        bestSeed = score(keptAtoms + seedAtoms, keptBonds + seedBonds);
                        best = queryToTarget.clone();
                        extend = kept == grown;
                    }
                    for (int mappedIndex = 0; mappedIndex < seedAtoms; mappedIndex++) {
                        targetToQuery[queryToTarget[mapped[mappedIndex]]] = -1;
                        queryToTarget[mapped[mappedIndex]] = -1;
                    }
                }
            }
            // Cutting can leave free pairs next to the seed. A fragment grown after it could then share a common
            // bond with it, so none is grown.
            if (minBonds == 0 || bestSeed == before || bestSeed == bound || !extend)
                break;
            // The fragments found so far stay mapped while the next one grows. It shares no common bond with them,
            // since each grew until no pair next to it was left.
            keptAtoms = atoms(bestSeed);
            keptBonds = bonds(bestSeed);
            System.arraycopy(best, 0, queryToTarget, 0, queryCount);
            for (int queryAtom = 0; queryAtom < queryCount; queryAtom++) {
                if (queryToTarget[queryAtom] >= 0)
                    targetToQuery[queryToTarget[queryAtom]] = queryAtom;
            }
        }
        Arrays.fill(queryToTarget, -1);
        Arrays.fill(targetToQuery, -1);
        return best;
    }

    /**
     * Cuts a seed that breaks a configuration back to its longest start that
     * keeps them. The seed has the score {@code grown}, and its query atoms
     * are in {@code mapped} in order. The result is the score of what is left.
     */
    private long cutBack(long grown, int[] mapped) {
        long kept = grown;
        while (!graph.stereoConsistent(queryToTarget, clock)) {
            int queryAtom = mapped[atoms(kept) - 1], targetAtom = queryToTarget[queryAtom];
            queryToTarget[queryAtom] = -1;
            targetToQuery[targetAtom] = -1;
            kept = score(atoms(kept) - 1, bonds(kept) - common(queryAtom, targetAtom));
        }
        return kept;
    }

    /**
     * Picks the seed roots by how alike their atoms are. The
     * {@code rootCount[level]} roots of each level are at
     * {@code roots[level * SEEDS]}. Alike cells grow alike seeds, so there is
     * one root for each pair of cells, marked in {@code taken}.
     */
    private void seedRoots(int[] roots, int[] rootCount, long[] taken) {
        Arrays.fill(taken, 0);
        Arrays.fill(rootCount, 0);
        int likest = MCSGraph.RADII;
        // The most alike roots are tried first. Once there are enough of them, the rest are not needed.
        for (int rank = queryCount - 1; rank >= 0 && rootCount[likest] < SEEDS; rank--) {
            int queryAtom = queryOrder[rank];
            for (int targetPosition = 0; targetPosition < targetCount && rootCount[likest] < SEEDS
                                         && queryToTarget[queryAtom] < 0; targetPosition++) {
                int targetAtom = targetOrder[targetPosition];
                clock.tick();
                if (targetToQuery[targetAtom] >= 0 || !has(allowed, queryAtom, targetAtom))
                    continue;
                int level = likeness(queryAtom, targetAtom);
                if (rootCount[level] < SEEDS && !has(taken, queryCell[queryAtom], targetCell[targetAtom])) {
                    set(taken, queryCell[queryAtom], targetCell[targetAtom]);
                    roots[level * SEEDS + rootCount[level]++] = pair(queryAtom, targetAtom);
                }
            }
        }
    }

    /**
     * Grows a seed from the root pair, each time by the best frontier pair, see
     * {@link #bestSeedPair}. The frontier is the free query atoms next to the
     * seed, each listed once by marking it with {@code round} in {@code added}.
     * The seed is left mapped, with its query atoms in {@code mapped} in order.
     * The result is its score.
     */
    private long growSeed(int root, int[] frontier, int[] mapped, int[] added, int round) {
        int count = 0, frontierSize = 0, seedBonds = 0;
        for (int pair = root; pair >= 0; ) {
            int queryAtom = queryAtom(pair), targetAtom = targetAtom(pair);
            queryToTarget[queryAtom] = targetAtom;
            targetToQuery[targetAtom] = queryAtom;
            mapped[count++] = queryAtom;
            for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
                int queryNeighbour = query.neighbour[queryPlace];
                if (queryToTarget[queryNeighbour] < 0 && added[queryNeighbour] != round) {
                    added[queryNeighbour] = round;
                    frontier[frontierSize++] = queryNeighbour;
                }
            }
            long best = -1;
            for (int frontierIndex = 0; frontierIndex < frontierSize; ) {
                int frontierAtom = frontier[frontierIndex];
                // The atom just mapped leaves the frontier.
                if (queryToTarget[frontierAtom] >= 0) {
                    frontier[frontierIndex] = frontier[--frontierSize];
                    continue;
                }
                frontierIndex++;
                best = bestSeedPair(frontierAtom, best);
            }
            pair = best < 0 ? -1 : (int) best;
            seedBonds += best < 0 ? 0 : (int) (best >>> 32);
        }
        return score(count, seedBonds);
    }

    /**
     * The best pair of {@code frontierAtom} with a free target next to the
     * image of one of its mapped neighbours, through a compatible bond, unless
     * the pair {@code held} beats it: the most alike pair, then the one with
     * the most common bonds. Ties go to the higher ranked query atom, then to
     * the lower ranked target atom. The pair is returned in the low int, with
     * its common bonds in the high int, or -1 if there is none; {@code held}
     * is read the same way. Next to a hub, the first few free targets are as
     * good for a seed as all of them.
     */
    private long bestSeedPair(int frontierAtom, long held) {
        int pair = held < 0 ? -1 : (int) held, links = held < 0 ? 0 : (int) (held >>> 32);
        int likest = pair < 0 ? -1 : likeness(queryAtom(pair), targetAtom(pair));
        for (int queryPlace = query.start[frontierAtom]; queryPlace < query.start[frontierAtom + 1]; queryPlace++) {
            int neighbourImage = queryToTarget[query.neighbour[queryPlace]];
            if (neighbourImage < 0)
                continue;
            for (int targetPlace = target.start[neighbourImage], freeCount = 0;
                 targetPlace < target.start[neighbourImage + 1] && freeCount < HUB_TARGETS; targetPlace++) {
                int candidateTarget = target.neighbour[targetPlace];
                clock.tick();
                if (targetToQuery[candidateTarget] >= 0 || !has(allowed, frontierAtom, candidateTarget)
                    || !graph.bondsMatch(query.neighbourBond[queryPlace], target.neighbourBond[targetPlace]))
                    continue;
                freeCount++;
                int level = likeness(frontierAtom, candidateTarget);
                if (level < likest)
                    continue;
                int newBonds = common(frontierAtom, candidateTarget);
                if (level > likest || newBonds > links
                    || newBonds == links && (queryRank[frontierAtom] > queryRank[queryAtom(pair)]
                        || frontierAtom == queryAtom(pair)
                           && targetRank[candidateTarget] < targetRank[targetAtom(pair)])) {
                    pair = pair(frontierAtom, candidateTarget);
                    likest = level;
                    links = newBonds;
                }
            }
        }
        return pair < 0 ? -1 : (long) links << 32 | pair;
    }

    /** The number of radii, from 0, at which {@code queryAtom} and {@code targetAtom} look alike. */
    private int likeness(int queryAtom, int targetAtom) {
        int level = 0;
        while (level < MCSGraph.RADII && querySignature[level][queryAtom] == targetSignature[level][targetAtom])
            level++;
        return level;
    }

    /**
     * The mappings with the best score, at most {@code maxMappings}. Each is
     * found once, as a mapping is reached only from its highest ranked query
     * atom and under the first sibling pair it contains. The search starts
     * from the score {@code start}, which the {@code seed} holds unless it is
     * null. It ends early once it holds enough mappings that reach
     * {@code upperBound}. It needs {@link #rank} first.
     */
    List<int[]> collect(int[] seed, long start, long upperBound, int maxMappings) {
        this.bestScore = start;
        this.upperBound = upperBound;
        this.maxMappings = maxMappings;
        heldSeed = seed;
        mappings = new ArrayList<>();
        if (seed != null)
            mappings.add(seed);
        // The twins are found once, before any pair is excluded. A query that fits whole never needs them.
        if (queryTwin == null) {
            queryTwin = twins(query, true);
            targetTwin = twins(target, false);
        }
        // Below a root only lower ranked query atoms are mapped, so a root of rank k has at most k + 1 atoms.
        for (int rank = queryCount - 1;
             rank >= 0 && !done() && (minBonds > 0 ? canImprove(rootBound(rank))
                                                   : Math.min(rank + 1, targetCount) >= atoms(bestScore));
             rank--) {
            int queryAtom = queryOrder[rank];
            for (int targetPosition = targetCount - 1; targetPosition >= 0 && !done(); targetPosition--) {
                clock.tick();
                if (has(allowed, queryAtom, targetOrder[targetPosition])
                    && push(queryAtom, targetOrder[targetPosition], 0, upperBound))
                    search();
            }
        }
        return mappings;
    }

    /**
     * Searches depth first from the root pair until it is taken off again.
     * The top level tries the targets of each of its candidates in turn, and
     * then in MCES the roots of new fragments. It is taken off once nothing is
     * left or its bound is too low. The levels are an explicit stack, so large
     * molecules do not run out of stack.
     */
    private void search() {
        while (depth > 0) {
            clock.tick();
            Level level = levels[depth - 1];
            if (done() || !canImprove(level.bound)) {
                pop();
            } else if (level.nextTarget < level.targets.length) {
                int queryAtom = level.triedAtom, targetAtom = level.targets[level.nextTarget++];
                if (twinExcluded(queryAtom, targetAtom)
                    || !push(queryAtom, targetAtom, level.fragmentStart, level.bound))
                    exclude(queryAtom, targetAtom);
            } else if (level.nextCandidate < level.candidates.length) {
                // The targets tried so far are now excluded, which can lower the bound.
                if (level.nextCandidate > 0 && (level.bound = bound(level.fragmentStart, level.bound)) < 0) {
                    pop();
                } else {
                    level.triedAtom = level.candidates[level.nextCandidate++];
                    level.targets = targets(level.triedAtom);
                    level.nextTarget = 0;
                }
            } else if (!nextRoot(level)) {
                pop();
            }
        }
    }

    /**
     * Maps {@code queryAtom} onto {@code targetAtom} as a new level of the
     * fragment that starts at level {@code fragmentStart}. The level has the
     * bound of the state it extends, or the bridge bound of the pair if that
     * is lower, until it has its own. False, with the pair left unmapped, if
     * the bound prunes it or it breaks a configuration.
     */
    private boolean push(int queryAtom, int targetAtom, int fragmentStart, long bound) {
        if (bridgeBound != null) {
            bound = Math.min(bound, bridgeBound.bound(queryAtom, targetAtom));
            if (!canImprove(bound))
                return false;
        }
        Level level = map(queryAtom, targetAtom, fragmentStart, bound);
        // A mapping that breaks a configuration still breaks it with any pair added. The pair is rejected at once.
        if (!graph.stereoConsistent(queryToTarget, clock)) {
            takeOff();
            return false;
        }
        record(fragmentStart);
        return isLeaf() || branch(level, fragmentStart, bound);
    }

    /**
     * Maps {@code queryAtom} onto {@code targetAtom} as the level at
     * {@link #depth}, of the fragment that starts at level {@code fragmentStart}
     * and with {@code bound} until the level has its own. The level records
     * what taking the pair off restores, the common bonds and the excluded
     * pairs before it, and for the bound the query and target bonds with no
     * end mapped once it is. It has nothing to try until {@link #branch} gives
     * it candidates.
     */
    private Level map(int queryAtom, int targetAtom, int fragmentStart, long bound) {
        if (levels[depth] == null)
            levels[depth] = new Level();
        Level level = levels[depth];
        level.queryAtom = queryAtom;
        level.targetAtom = targetAtom;
        level.commonBondsBefore = commonBonds;
        level.excludedBefore = excludedCount;
        level.freeQueryBonds = depth > 0 ? levels[depth - 1].freeQueryBonds : query.bondCount;
        level.freeTargetBonds = depth > 0 ? levels[depth - 1].freeTargetBonds : target.bondCount;
        for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
            if (queryToTarget[query.neighbour[queryPlace]] < 0)
                level.freeQueryBonds--;
        }
        for (int targetPlace = target.start[targetAtom]; targetPlace < target.start[targetAtom + 1]; targetPlace++) {
            if (targetToQuery[target.neighbour[targetPlace]] < 0)
                level.freeTargetBonds--;
        }
        level.fragmentStart = fragmentStart;
        level.bound = bound;
        level.candidates = level.targets = NONE;
        level.nextCandidate = level.nextTarget = level.rootRank = 0;
        commonBonds += common(queryAtom, targetAtom);
        queryToTarget[queryAtom] = targetAtom;
        targetToQuery[targetAtom] = queryAtom;
        depth++;
        return level;
    }

    /**
     * Whether the mapping is a leaf, with nothing left to try: the search
     * holds what it wants, every query atom up to the rank of the root is
     * mapped, or every target atom is, or the mapping has the atoms of the
     * upper bound, in MCES its common bonds.
     */
    private boolean isLeaf() {
        return done() || depth == queryRank[levels[0].queryAtom] + 1 || depth == targetCount
               || (minBonds > 0 ? commonBonds == bonds(upperBound) : depth == atoms(upperBound));
    }

    /**
     * Gives the top {@code level}, whose fragment starts at level
     * {@code fragmentStart} and whose bound is {@code bound} so far, its own
     * bound and the candidates whose targets it tries. In MCES a fragment with
     * enough bonds also offers the roots of new fragments once its candidates
     * are done; these rank below its own root. False, with the pair taken off,
     * if the bound prunes the level.
     */
    private boolean branch(Level level, int fragmentStart, long bound) {
        level.bound = bound(fragmentStart, bound);
        if (level.bound < 0) {
            takeOff();
            return false;
        }
        level.candidates = candidates();
        clock.tick(level.candidates.length);
        if (minBonds > 0 && fragmentBonds(fragmentStart) >= minBonds)
            level.rootRank = queryRank[levels[fragmentStart].queryAtom];
        return true;
    }

    /** The common bonds of the open fragment, which starts at level {@code fragmentStart}. */
    private int fragmentBonds(int fragmentStart) {
        return commonBonds - levels[fragmentStart].commonBondsBefore;
    }

    /** Takes the top level off, and excludes its pair for its later siblings and everything below them. */
    private void pop() {
        Level level = levels[depth - 1];
        takeOff();
        if (depth > 0)
            exclude(level.queryAtom, level.targetAtom);
    }

    /** Unmaps the pair of the top level, and allows again the pairs excluded since it was mapped. */
    private void takeOff() {
        Level level = levels[--depth];
        while (excludedCount > level.excludedBefore) {
            int pair = excluded[--excludedCount];
            set(allowed, queryAtom(pair), targetAtom(pair));
        }
        queryToTarget[level.queryAtom] = -1;
        targetToQuery[level.targetAtom] = -1;
        commonBonds = level.commonBondsBefore;
    }

    /** Excludes the pair of {@code queryAtom} and {@code targetAtom}, which must be allowed. */
    private void exclude(int queryAtom, int targetAtom) {
        clear(allowed, queryAtom, targetAtom);
        if (excludedCount == excluded.length)
            excluded = Arrays.copyOf(excluded, 2 * excluded.length);
        excluded[excludedCount++] = pair(queryAtom, targetAtom);
    }

    /**
     * In MCES, offers the next root of a new fragment once the candidates of
     * the level are done. This closes the fragment, as every pair that extends
     * it is excluded. A root is the next free query atom ranked below the root
     * of the closed fragment, so a mapping is reached only from the highest
     * ranked atom of each fragment. False when no root is left, or when the
     * bound of those left is too low. That bound falls with their rank.
     */
    private boolean nextRoot(Level level) {
        if (level.rootRank == 0)
            return false;
        // The fragment is closed once it is set to start at the next level, where the roots offered start fragments
        // of their own.
        if (level.fragmentStart < depth) {
            if (hasPendantPair(level.fragmentStart))
                return false;
            level.fragmentStart = depth;
        }
        while (--level.rootRank >= 0) {
            int queryAtom = queryOrder[level.rootRank];
            if (queryToTarget[queryAtom] >= 0)
                continue;
            level.bound = rootBound(level.rootRank);
            if (!canImprove(level.bound))
                return false;
            level.triedAtom = queryAtom;
            level.targets = freeTargets(queryAtom);
            level.nextTarget = 0;
            if (level.targets.length > 0)
                return true;
        }
        return false;
    }

    /**
     * Keeps the mapping if it beats the best score, or ties with it while
     * fewer than wanted are held. In MCES the open fragment, from level
     * {@code fragmentStart}, must have enough bonds. With complete rings the
     * mapping must be complete.
     */
    private void record(int fragmentStart) {
        long current = score(depth, commonBonds);
        if (current < bestScore || current == bestScore && mappings.size() == maxMappings)
            return;
        // In MCES a fragment needs minBonds common bonds. The open one may still get them further down.
        if (fragmentBonds(fragmentStart) < minBonds)
            return;
        if (query.rings != null && !complete(queryToTarget, false))
            return;
        if (current > bestScore) {
            bestScore = current;
            mappings.clear();
            heldSeed = null;
        } else if (heldSeed != null && Arrays.equals(queryToTarget, heldSeed)) {
            // The seed is held from the start, so it is not added again when found.
            heldSeed = null;
            return;
        }
        mappings.add(queryToTarget.clone());
    }

    /** Whether the search can end, as it holds as many mappings as wanted with a score that no mapping beats. */
    private boolean done() {
        return mappings.size() == maxMappings && bestScore == upperBound;
    }

    /**
     * The bound of the state whose open fragment starts at level
     * {@code fragmentStart}, or -1 if the state is pruned. A mapping below adds
     * only open pairs that the fragment reaches through compatible bonds, so
     * the bound counts these. The first layer of them gives the candidates. In
     * connected mode the bound only rises with more pairs. These are then
     * reached only until the state can improve, and {@code current} is kept.
     */
    private long bound(int fragmentStart, long current) {
        reach.start();
        int open = 0;
        for (int i = fragmentStart; i < depth; i++) {
            int queryLinks = reach.queryLinks, targetLinks = reach.targetLinks;
            reach.link(levels[i].queryAtom, levels[i].targetAtom);
            open += Math.min(reach.queryLinks - queryLinks, reach.targetLinks - targetLinks);
        }
        reach.firstLayer = reach.count;
        Level top = levels[depth - 1];
        // In MCES the atoms of the upper bound cap only a mapping with as many bonds. A mapping with fewer bonds
        // may have more atoms.
        int mostAtoms = minBonds > 0 ? Math.min(queryCount, targetCount) : atoms(upperBound);
        int mostBonds = commonBonds + open + Math.min(top.freeQueryBonds, top.freeTargetBonds);
        long result = canImprove(capped(mostAtoms, mostBonds)) ? current : -1;
        if (result >= 0 && minBonds > 0) {
            result = edgeBound.bound(queryRank[levels[fragmentStart].queryAtom], open);
            if (!canImprove(result))
                result = -1;
        } else if (result >= 0) {
            result = groupBound(open, current);
        }
        reach.finish();
        // In MCES a mapping below may add pairs that are not reached. Whether it can be complete is then not known.
        return result >= 0 && query.rings != null && minBonds == 0 && !complete(queryToTarget, true) ? -1 : result;
    }

    /** The bound in connected mode, from the groups of atoms the reached pairs join. Pairs are reached in batches. */
    private long groupBound(int open, long current) {
        int queryLinks = reach.queryLinks, targetLinks = reach.targetLinks;
        for (int linked = 0, joined = 0; ; ) {
            for (; joined < reach.count; joined++)
                reach.join(reach.pairs[joined]);
            int links = Math.min(reach.queryLinks - queryLinks, reach.targetLinks - targetLinks);
            long bound = capped(depth + reach.groupSum, commonBonds + open + links);
            if (linked == reach.count)
                return canImprove(bound) ? bound : -1;
            // With complete rings every pair is reached, as complete() reads the atoms of the reached pairs.
            if (query.rings == null && canImprove(bound))
                return current;
            for (int end = Math.min(linked + REACH_BATCH, reach.count); linked < end; linked++) {
                clock.tick();
                int pair = reach.pairs[linked];
                reach.link(queryAtom(pair), targetAtom(pair));
            }
        }
    }

    /** The bound of the roots offered up to {@code rankLimit}. The fragment is closed, so no bond links it. */
    private long rootBound(int rankLimit) {
        reach.start();
        return edgeBound.bound(rankLimit, 0);
    }

    /**
     * The score of a mapping of {@code atomCount} atoms with {@code bondCount}
     * common bonds, packed as in {@link MCSGraph#score}: by atoms first, or in
     * MCES by common bonds first, so that scores compare as longs.
     */
    private long score(int atomCount, int bondCount) {
        return MCSGraph.score(atomCount, bondCount, minBonds > 0);
    }

    /** The atoms of a score of this search. */
    private int atoms(long score) {
        return MCSGraph.atoms(score, minBonds > 0);
    }

    /** The common bonds of a score of this search. */
    private int bonds(long score) {
        return MCSGraph.bonds(score, minBonds > 0);
    }

    /**
     * The score of {@code mapping}: its atoms, and the compatible bonds
     * between them that the target has between their images.
     */
    long score(int[] mapping) {
        int atomCount = 0, bondCount = 0;
        for (int queryAtom = 0; queryAtom < queryCount; queryAtom++) {
            if (mapping[queryAtom] >= 0) {
                atomCount++;
                bondCount += commonBondsAt(mapping, queryAtom, null, null);
            }
        }
        return score(atomCount, bondCount);
    }

    /**
     * The compatible bonds from {@code queryAtom} to the lower mapped atoms of
     * {@code mapping} that the target has between their images, so that each
     * common bond counts once. Unless these are null, the bonds are also
     * marked in {@code queryCommon} and {@code targetCommon}.
     */
    private int commonBondsAt(int[] mapping, int queryAtom, boolean[] queryCommon, boolean[] targetCommon) {
        int targetAtom = mapping[queryAtom], count = 0;
        for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
            int other = query.neighbour[queryPlace];
            if (other >= queryAtom || mapping[other] < 0)
                continue;
            int targetBond = target.bondBetween(targetAtom, mapping[other]);
            if (targetBond < 0 || !graph.bondsMatch(query.neighbourBond[queryPlace], targetBond))
                continue;
            count++;
            if (queryCommon != null)
                queryCommon[query.neighbourBond[queryPlace]] = targetCommon[targetBond] = true;
        }
        return count;
    }

    /**
     * A bound, capped by the upper bound. A mapping with the atoms of the
     * upper bound has no more common bonds than it. In MCES a mapping with
     * the common bonds of the upper bound has no more atoms than it.
     */
    private long capped(int atomCount, int bondCount) {
        int mostAtoms = atoms(upperBound), mostBonds = bonds(upperBound);
        if (minBonds > 0 && bondCount >= mostBonds)
            return score(Math.min(atomCount, mostAtoms), mostBonds);
        if (minBonds == 0 && atomCount >= mostAtoms)
            return score(mostAtoms, Math.min(bondCount, mostBonds));
        return score(atomCount, bondCount);
    }

    /** Whether a state with this bound can hold a better mapping, or a tie while fewer than wanted are held. */
    private boolean canImprove(long bound) {
        return bound > bestScore || bound == bestScore && mappings.size() < maxMappings;
    }

    /** The query atoms of the first layer of reached pairs: not terminal first, then fewer pairs, then higher rank. */
    private int[] candidates() {
        int[] layer = Arrays.copyOf(reach.pairs, reach.firstLayer);
        Arrays.sort(layer);
        long[] keys = new long[layer.length];
        int count = 0;
        for (int from = 0, to; from < layer.length; from = to) {
            int queryAtom = queryAtom(layer[from]);
            to = from + 1;
            while (to < layer.length && queryAtom(layer[to]) == queryAtom)
                to++;
            keys[count++] = candidateKey(queryAtom, to - from);
        }
        Arrays.sort(keys, 0, count);
        int[] sorted = new int[count];
        for (int i = 0; i < count; i++)
            sorted[i] = candidateOf(keys[i]);
        return sorted;
    }

    /**
     * The sort key of candidate {@code queryAtom}: whether it is terminal, its
     * number of pairs, then its rank in reverse.
     */
    private long candidateKey(int queryAtom, int pairs) {
        return (query.degree(queryAtom) == 1 ? 1L << 62 : 0) | (long) pairs << 31
               | queryCount - 1 - queryRank[queryAtom];
    }

    /** The candidate of a key from {@link #candidateKey}. */
    private int candidateOf(long key) {
        return queryOrder[queryCount - 1 - (int) (key & Integer.MAX_VALUE)];
    }

    /**
     * The targets of query atom {@code queryAtom}. These are the free neighbours of
     * the images of its mapped neighbours, joined by compatible bonds, in pairs
     * not excluded. The most alike come first, then by descending rank. The
     * order decides only which mappings are found first.
     */
    private int[] targets(int queryAtom) {
        int capacity = 0;
        for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
            if (queryToTarget[query.neighbour[queryPlace]] >= 0)
                capacity += target.degree(queryToTarget[query.neighbour[queryPlace]]);
        }
        long[] keys = new long[capacity];
        int count = 0;
        for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
            int neighbourImage = queryToTarget[query.neighbour[queryPlace]];
            if (neighbourImage < 0)
                continue;
            clock.tick(1 + target.degree(neighbourImage));
            for (int targetPlace = target.start[neighbourImage]; targetPlace < target.start[neighbourImage + 1];
                 targetPlace++) {
                int targetAtom = target.neighbour[targetPlace];
                if (targetToQuery[targetAtom] < 0 && has(allowed, queryAtom, targetAtom)
                    && graph.bondsMatch(query.neighbourBond[queryPlace], target.neighbourBond[targetPlace]))
                    keys[count++] = targetKey(queryAtom, targetAtom);
            }
        }
        // A target next to two mapped neighbours is listed twice, with the same key.
        Arrays.sort(keys, 0, count);
        int[] targets = new int[count];
        int unique = 0;
        for (int i = 0; i < count; i++) {
            if (i == 0 || keys[i] != keys[i - 1])
                targets[unique++] = targetOf(keys[i]);
        }
        return Arrays.copyOf(targets, unique);
    }

    /**
     * The sort key of target {@code targetAtom} of query atom {@code queryAtom}:
     * how unlike the two are, then the rank in reverse.
     */
    private long targetKey(int queryAtom, int targetAtom) {
        return (long) (MCSGraph.RADII - likeness(queryAtom, targetAtom)) << 32
               | targetCount - 1 - targetRank[targetAtom];
    }

    /** The target of a key from {@link #targetKey}. */
    private int targetOf(long key) {
        return targetOrder[targetCount - 1 - (int) key];
    }

    /** The free targets of a root, by descending rank. */
    private int[] freeTargets(int queryAtom) {
        int[] targets = new int[targetCount];
        int count = 0;
        clock.tick(targetCount);
        for (int targetPosition = targetCount - 1; targetPosition >= 0; targetPosition--) {
            if (targetToQuery[targetOrder[targetPosition]] < 0 && has(allowed, queryAtom, targetOrder[targetPosition]))
                targets[count++] = targetOrder[targetPosition];
        }
        return Arrays.copyOf(targets, count);
    }

    /**
     * The compatible bonds from {@code queryAtom} to mapped atoms that the
     * target has from {@code targetAtom} to their images.
     */
    private int common(int queryAtom, int targetAtom) {
        int count = 0;
        for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
            int neighbourImage = queryToTarget[query.neighbour[queryPlace]];
            if (neighbourImage < 0)
                continue;
            clock.tick(1 + target.degree(targetAtom));
            int targetBond = target.bondBetween(targetAtom, neighbourImage);
            if (targetBond >= 0 && graph.bondsMatch(query.neighbourBond[queryPlace], targetBond))
                count++;
        }
        return count;
    }

    /**
     * The twins of one molecule, each linked to the next in a ring, -1 for
     * none. Twins are terminal atoms with the same neighbour that match the
     * same atoms by the same bonds, and that no configuration reads. Swapping
     * two takes every mapping onto one of the same score.
     */
    private int[] twins(MCSGraph.Side side, boolean onQuery) {
        int[] next = new int[side.atomCount];
        Arrays.fill(next, -1);
        for (int parent = 0; parent < side.atomCount; parent++) {
            for (int place = side.start[parent]; place < side.start[parent + 1]; place++) {
                int first = side.neighbour[place], last = first;
                if (next[first] >= 0 || side.degree(first) != 1 || side.configured(first))
                    continue;
                for (int laterPlace = place + 1; laterPlace < side.start[parent + 1]; laterPlace++) {
                    int other = side.neighbour[laterPlace];
                    if (next[other] < 0 && side.degree(other) == 1 && !side.configured(other)
                        && alike(onQuery, first, side.neighbourBond[place], other, side.neighbourBond[laterPlace])) {
                        next[last] = other;
                        last = other;
                    }
                }
                if (last != first)
                    next[last] = first;
            }
        }
        return next;
    }

    /** Whether {@code atom} and {@code other}, with their bonds {@code bond} and {@code otherBond}, match alike. */
    private boolean alike(boolean onQuery, int atom, int bond, int other, int otherBond) {
        clock.tick(words + queryCount + query.bondCount + target.bondCount);
        if (onQuery) {
            for (int word = 0; word < words; word++) {
                if (allowed[atom * words + word] != allowed[other * words + word])
                    return false;
            }
            for (int targetBond = 0; targetBond < target.bondCount; targetBond++) {
                if (graph.bondsMatch(bond, targetBond) != graph.bondsMatch(otherBond, targetBond))
                    return false;
            }
        } else {
            for (int queryAtom = 0; queryAtom < queryCount; queryAtom++) {
                if (has(allowed, queryAtom, atom) != has(allowed, queryAtom, other))
                    return false;
            }
            for (int queryBond = 0; queryBond < query.bondCount; queryBond++) {
                if (graph.bondsMatch(queryBond, bond) != graph.bondsMatch(queryBond, otherBond))
                    return false;
            }
        }
        return true;
    }

    /**
     * Whether the pair of {@code queryAtom} and {@code targetAtom} can be skipped, as a free
     * twin of one has its pair with the other excluded. Swapping the twins
     * turns every mapping below this pair into one of the same score below the
     * excluded pair, which was tried before. So this holds only while no
     * mapping of that score could be recorded. That is when as many are held
     * as wanted, and in MCES also when nothing but the seed is held, see
     * {@link #run}.
     */
    private boolean twinExcluded(int queryAtom, int targetAtom) {
        boolean full = mappings.size() == maxMappings;
        boolean onlySeed = minBonds > 0 && mappings.size() == (heldSeed != null ? 1 : 0);
        if (!full && !onlySeed)
            return false;
        for (int twin = queryTwin[queryAtom]; twin >= 0 && twin != queryAtom; twin = queryTwin[twin]) {
            if (queryToTarget[twin] < 0 && !has(allowed, twin, targetAtom))
                return true;
        }
        for (int twin = targetTwin[targetAtom]; twin >= 0 && twin != targetAtom; twin = targetTwin[twin]) {
            if (targetToQuery[twin] < 0 && !has(allowed, queryAtom, twin))
                return true;
        }
        return false;
    }

    /**
     * Whether the open fragment, from level {@code fragmentStart}, leaves out a
     * pendant pair. That is a free terminal query atom next to a mapped atom
     * whose image has a free terminal target atom it may map onto by a
     * compatible bond, with neither read by a configuration. No mapping below
     * the roots the level offers is then a maximum one, as adding that pair
     * beats it.
     */
    private boolean hasPendantPair(int fragmentStart) {
        for (int level = fragmentStart; level < depth; level++) {
            int queryAtom = levels[level].queryAtom, targetAtom = levels[level].targetAtom;
            clock.tick(query.degree(queryAtom) * target.degree(targetAtom));
            for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
                int queryNeighbour = query.neighbour[queryPlace];
                if (queryToTarget[queryNeighbour] >= 0 || query.degree(queryNeighbour) != 1
                    || query.configured(queryNeighbour))
                    continue;
                for (int targetPlace = target.start[targetAtom]; targetPlace < target.start[targetAtom + 1];
                     targetPlace++) {
                    int targetNeighbour = target.neighbour[targetPlace];
                    if (targetToQuery[targetNeighbour] < 0 && target.degree(targetNeighbour) == 1
                        && !target.configured(targetNeighbour)
                        && has(edgeBound.compatible, queryNeighbour, targetNeighbour)
                        && graph.bondsMatch(query.neighbourBond[queryPlace], target.neighbourBond[targetPlace]))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * With complete rings, whether both sides are covered, see
     * {@link MCSGraph.Rings#covers}. That is whether the mapping is complete
     * or, with {@code open}, whether a mapping below that adds only reached
     * pairs can be.
     */
    private boolean complete(int[] mapping, boolean open) {
        boolean[] queryMapped = new boolean[queryCount], targetMapped = new boolean[targetCount];
        boolean[] queryOpen = new boolean[queryCount], targetOpen = new boolean[targetCount];
        boolean[] queryCommon = new boolean[query.bondCount], targetCommon = new boolean[target.bondCount];
        for (int i = 0; open && i < reach.count; i++) {
            queryOpen[queryAtom(reach.pairs[i])] = true;
            targetOpen[targetAtom(reach.pairs[i])] = true;
        }
        for (int queryAtom = 0; queryAtom < queryCount; queryAtom++) {
            int targetAtom = mapping[queryAtom];
            if (targetAtom < 0)
                continue;
            queryMapped[queryAtom] = targetMapped[targetAtom] = true;
            clock.tick(1 + query.degree(queryAtom) + target.degree(targetAtom));
            commonBondsAt(mapping, queryAtom, queryCommon, targetCommon);
        }
        clock.tick(query.rings.rings.length + target.rings.rings.length);
        return query.rings.covers(queryMapped, queryOpen, queryCommon)
               && target.rings.covers(targetMapped, targetOpen, targetCommon);
    }

    /**
     * The search for the whole query, if it may fit. It may when the query is
     * connected and no larger than the target, and each query atom can have a
     * target atom of its own with at least as many bonds. Null if not.
     */
    Embedding embedding() {
        if (queryCount > targetCount || !queryConnected())
            return null;
        // A row for each number of bonds holds the target atoms with at least that many, up to the most a query atom
        // has. The last row is empty.
        int rows = Math.min(query.maxDegree(), target.maxDegree()) + 1;
        long[] atLeast = new long[(rows + 1) * words];
        for (int targetAtom = 0; targetAtom < targetCount; targetAtom++)
            for (int fewestBonds = Math.min(target.degree(targetAtom), rows); fewestBonds >= 0; fewestBonds--)
                set(atLeast, fewestBonds, targetAtom);
        long[] candidateRows = new long[queryCount * words];
        for (int queryAtom = 0; queryAtom < queryCount; queryAtom++) {
            clock.tick(words);
            int row = Math.min(query.degree(queryAtom), rows) * words;
            for (int i = 0; i < words; i++)
                candidateRows[queryAtom * words + i] = allowed[queryAtom * words + i] & atLeast[row + i];
        }
        return hasDistinctTargets(candidateRows, queryCount, words, clock) ? new Embedding(candidateRows) : null;
    }

    /** Whether every query atom is reached from the first by bonds. */
    private boolean queryConnected() {
        int[] queue = new int[queryCount];
        boolean[] reached = new boolean[queryCount];
        int count = 1;
        reached[0] = true;
        for (int head = 0; head < count; head++) {
            for (int place = query.start[queue[head]]; place < query.start[queue[head] + 1]; place++) {
                if (!reached[query.neighbour[place]]) {
                    reached[query.neighbour[place]] = true;
                    queue[count++] = query.neighbour[place];
                }
            }
        }
        return count == queryCount;
    }

    /**
     * The pair of {@code queryAtom} and {@code targetAtom} as one int,
     * {@code queryAtom * targetCount + targetAtom}, which fits as {@link MCS}
     * allows at most 2<sup>30</sup> pairs. Pairs sort by query atom, then by
     * target atom.
     */
    private int pair(int queryAtom, int targetAtom) {
        return queryAtom * targetCount + targetAtom;
    }

    /** The query atom of a packed pair. */
    private int queryAtom(int pair) {
        return pair / targetCount;
    }

    /** The target atom of a packed pair. */
    private int targetAtom(int pair) {
        return pair % targetCount;
    }

    /**
     * A mapped pair of the branch and bound, one for each depth, with what it
     * restores when taken off and what it has left to try. The levels are
     * made once and reused, see {@link #map}.
     */
    private static final class Level {

        /** The mapped pair. */
        int queryAtom, targetAtom;
        /** The common bonds and the excluded pairs before the pair was mapped, put back when it is taken off. */
        int commonBondsBefore, excludedBefore;
        /** The query and target bonds with no end mapped, once the pair is mapped. */
        int freeQueryBonds, freeTargetBonds;
        /** The level that starts the open fragment. In MCES, closing the fragment sets this to the next level. */
        int fragmentStart;
        /** A score that no mapping below the level beats, see {@link MCSSearch#bound}. */
        long bound;
        /** The candidates whose targets the level tries, in order, see {@link MCSSearch#candidates}. */
        int[] candidates;
        /** The place in {@link #candidates} of the next candidate to try. */
        int nextCandidate;
        /** The candidate being tried. */
        int triedAtom;
        /** The targets of {@link #triedAtom}, in order, see {@link MCSSearch#targets}. */
        int[] targets;
        /** The place in {@link #targets} of the next target to try. */
        int nextTarget;
        /** In MCES, once the fragment is closed, the roots of new fragments offered are ranked below this. */
        int rootRank;
    }

    /**
     * The open pairs reached from the open fragment of a state through
     * compatible bonds, see {@link #bound}. The bonds that link them are
     * counted once on each side. A union-find over their atoms, the target
     * atoms numbered after the query atoms, sums the fewer atoms of either
     * side over the groups.
     */
    private final class Reach {

        /** The reached pairs as bits, rows by query atom. */
        private final long[] seen = new long[queryCount * words];
        /** The reached pairs in the order they were reached, the first {@link #count} of the array. */
        private int[] pairs = new int[INITIAL_PAIRS];
        /** The number of reached pairs. */
        private int count;
        /** The number of pairs reached from the fragment itself, which come first and give the candidates. */
        private int firstLayer;
        /** The stamp of each query bond counted as a link, see {@link #stamp}. */
        private final int[] queryBondStamp = new int[query.bondCount];
        /** The stamp of each target bond counted as a link. */
        private final int[] targetBondStamp = new int[target.bondCount];
        /** The numbers of query and target bonds counted as links. */
        private int queryLinks, targetLinks;
        /** The stamp of each atom of the union-find, the target atoms numbered after the query atoms. */
        private final int[] atomStamp = new int[queryCount + targetCount];
        /** The parent of each atom in the union-find, itself for the root of a group. */
        private final int[] parent = new int[queryCount + targetCount];
        /** The query atoms in the group of each root. */
        private final int[] queryAtoms = new int[queryCount + targetCount];
        /** The target atoms in the group of each root. */
        private final int[] targetAtoms = new int[queryCount + targetCount];
        /** The sum, over the groups, of the fewer atoms of either side. */
        private int groupSum;
        /** The stamp of this use, so that no marks have to be cleared. */
        private int stamp;

        /** Starts a use with nothing reached. Every mark is cleared when the stamp would overflow. */
        void start() {
            if (++stamp == Integer.MAX_VALUE) {
                Arrays.fill(queryBondStamp, 0);
                Arrays.fill(targetBondStamp, 0);
                Arrays.fill(atomStamp, 0);
                stamp = 1;
            }
            count = queryLinks = targetLinks = groupSum = 0;
        }

        /** Reaches the open pairs next to the pair of {@code queryAtom} and {@code targetAtom}. */
        void link(int queryAtom, int targetAtom) {
            int from = target.start[targetAtom], to = target.start[targetAtom + 1];
            int rootRank = queryRank[levels[0].queryAtom];
            for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
                int queryNeighbour = query.neighbour[queryPlace], queryBond = query.neighbourBond[queryPlace];
                if (queryToTarget[queryNeighbour] >= 0 || queryRank[queryNeighbour] > rootRank)
                    continue;
                clock.tick(1 + to - from);
                for (int targetPlace = from; targetPlace < to; targetPlace++) {
                    int targetNeighbour = target.neighbour[targetPlace], targetBond = target.neighbourBond[targetPlace];
                    if (targetToQuery[targetNeighbour] >= 0 || !has(allowed, queryNeighbour, targetNeighbour)
                        || !graph.bondsMatch(queryBond, targetBond))
                        continue;
                    if (queryBondStamp[queryBond] != stamp) {
                        queryBondStamp[queryBond] = stamp;
                        queryLinks++;
                    }
                    if (targetBondStamp[targetBond] != stamp) {
                        targetBondStamp[targetBond] = stamp;
                        targetLinks++;
                    }
                    if (!has(seen, queryNeighbour, targetNeighbour)) {
                        set(seen, queryNeighbour, targetNeighbour);
                        if (count == pairs.length)
                            pairs = Arrays.copyOf(pairs, 2 * pairs.length);
                        pairs[count++] = pair(queryNeighbour, targetNeighbour);
                    }
                }
            }
        }

        /** Whether the bond of the query, or else of the target, links the reached pairs. */
        boolean isLink(boolean onQuery, int bond) {
            return (onQuery ? queryBondStamp : targetBondStamp)[bond] == stamp;
        }

        /** Joins the groups of the two atoms of {@code pair}, and updates the sum over the groups. */
        void join(int pair) {
            int queryRoot = root(queryAtom(pair)), targetRoot = root(queryCount + targetAtom(pair));
            if (queryRoot != targetRoot) {
                groupSum -= Math.min(queryAtoms[queryRoot], targetAtoms[queryRoot])
                            + Math.min(queryAtoms[targetRoot], targetAtoms[targetRoot]);
                parent[targetRoot] = queryRoot;
                queryAtoms[queryRoot] += queryAtoms[targetRoot];
                targetAtoms[queryRoot] += targetAtoms[targetRoot];
                groupSum += Math.min(queryAtoms[queryRoot], targetAtoms[queryRoot]);
            }
        }

        /** The root of the group of {@code atom}, which starts as a group of its own in each use. */
        private int root(int atom) {
            if (atomStamp[atom] != stamp) {
                atomStamp[atom] = stamp;
                parent[atom] = atom;
                queryAtoms[atom] = atom < queryCount ? 1 : 0;
                targetAtoms[atom] = 1 - queryAtoms[atom];
            }
            while (parent[atom] != atom)
                atom = parent[atom] = parent[parent[atom]];
            return atom;
        }

        /** Ends this use by clearing the bits of the reached pairs. */
        void finish() {
            for (int i = 0; i < count; i++)
                clear(seen, queryAtom(pairs[i]), targetAtom(pairs[i]));
        }
    }

    /**
     * The bound in MCES. An open atom is one ranked up to a limit that has a
     * pair not excluded, and a mapping below the state maps open atoms onto the
     * targets of these pairs. A common bond and its image share a label. An
     * open atom and its image get no more new bonds of a label than either has
     * open, so pairing the open atoms of each label bounds its new bonds. This
     * is at most the label-frequency bound of SMSD Pro {@cdk.cite SMSDPro2026}.
     */
    private final class EdgeBound {

        /** The compatible pairs before any is excluded, see {@link #hasPendantPair}. */
        private final long[] compatible = allowed.clone();
        /** The labels of the query and of the target, with the open atoms of the state being bounded. */
        private final Labels queryLabels, targetLabels;
        /** The symbol at each slot. A label has a slot for each end, the second -1 if both ends share a symbol. */
        private final int[] slotSymbol;
        /** The most bonds of one slot at an atom, plus one, which is the width of a histogram row. */
        private final int histogramWidth;
        /** Scratch of {@link #countOpen}: the open bonds of each slot at one atom, and the slots of that atom. */
        private final int[] slotCount, slotsAt;
        /** Scratch of {@link #bound}: the target atoms not used, and the targets of the open pairs. */
        private final long[] unusedTargets = new long[words], reachableTargets = new long[words];

        /**
         * Labels the bonds so that a common bond and its image share a label.
         * Atoms joined by compatible pairs share a symbol, and bonds joined by
         * compatible bond pairs share a class. A bond is labelled by the
         * symbols of its ends and its class, if both molecules have such a
         * bond. Under the default matching these are the element symbols and
         * bond classes.
         */
        EdgeBound() {
            int queryBonds = query.bondCount, bondTotal = queryBonds + target.bondCount;
            int[] symbol = atomSymbols(), bondSets = bondSets();
            int symbols = 0;
            for (int atomSymbol : symbol)
                symbols = Math.max(symbols, atomSymbol + 1);
            int[] querySymbol = Arrays.copyOfRange(symbol, 0, queryCount);
            int[] targetSymbol = Arrays.copyOfRange(symbol, queryCount, queryCount + targetCount);
            // The symbols at the ends of each bond, the lower first, or -1 if an end has none.
            int[] low = new int[bondTotal], high = new int[bondTotal];
            Arrays.fill(low, -1);
            endSymbols(query, querySymbol, 0, low, high);
            endSymbols(target, targetSymbol, queryBonds, low, high);
            // The keys sort the bonds by the symbols at their ends, then by their bond set.
            long[] keys = new long[bondTotal];
            for (int bond = 0; bond < bondTotal; bond++)
                keys[bond] = low[bond] < 0 ? -1 : ((long) low[bond] * symbols + high[bond]) * bondTotal
                                                  + find(bondSets, bond);
            int[] labelOf = labels(keys, queryBonds);
            int labels = 0;
            for (int bondLabel : labelOf)
                labels = Math.max(labels, bondLabel + 1);
            slotSymbol = new int[2 * labels];
            for (int bond = 0; bond < bondTotal; bond++) {
                if (labelOf[bond] >= 0) {
                    slotSymbol[2 * labelOf[bond]] = low[bond];
                    slotSymbol[2 * labelOf[bond] + 1] = low[bond] == high[bond] ? -1 : high[bond];
                }
            }
            histogramWidth = Math.max(query.maxDegree(), target.maxDegree()) + 1;
            int histogramSize = slotSymbol.length * histogramWidth;
            queryLabels = new Labels(querySymbol, slots(query, querySymbol, labelOf, 0), histogramSize, symbols);
            targetLabels = new Labels(targetSymbol, slots(target, targetSymbol, labelOf, queryBonds), histogramSize,
                                      symbols);
            slotCount = new int[slotSymbol.length];
            slotsAt = new int[histogramWidth];
        }

        /**
         * The symbol of each atom, the target atoms after the query atoms. Atoms
         * joined by compatible pairs share a symbol. An atom without a compatible
         * pair is never mapped and has none, -1.
         */
        private int[] atomSymbols() {
            int[] atomSets = singletons(queryCount + targetCount);
            for (int queryAtom = 0; queryAtom < queryCount; queryAtom++) {
                clock.tick(words);
                int row = queryAtom * words;
                for (int targetAtom = nextBit(allowed, row, words, 0); targetAtom >= 0;
                     targetAtom = nextBit(allowed, row, words, targetAtom + 1))
                    unite(atomSets, queryAtom, queryCount + targetAtom);
            }
            int[] members = new int[queryCount + targetCount], setSymbol = new int[queryCount + targetCount];
            int[] symbol = new int[queryCount + targetCount];
            int count = 0;
            for (int atom = 0; atom < queryCount + targetCount; atom++)
                members[find(atomSets, atom)]++;
            for (int atom = 0; atom < queryCount + targetCount; atom++)
                setSymbol[atom] = members[atom] > 1 ? count++ : -1;
            for (int atom = 0; atom < queryCount + targetCount; atom++)
                symbol[atom] = setSymbol[find(atomSets, atom)];
            return symbol;
        }

        /** The sets of bonds joined by compatible bond pairs, the target bonds after the query bonds. */
        private int[] bondSets() {
            int queryBonds = query.bondCount, targetBonds = target.bondCount;
            int[] bondSets = singletons(queryBonds + targetBonds);
            for (int queryBond = 0; queryBond < queryBonds; queryBond++) {
                clock.tick(targetBonds);
                for (int targetBond = 0; targetBond < targetBonds; targetBond++) {
                    if (graph.bondsMatch(queryBond, targetBond))
                        unite(bondSets, queryBond, queryBonds + targetBond);
                }
            }
            return bondSets;
        }

        /** Sets the symbols at the ends of each bond of {@code side} whose ends have symbols, the lower first. */
        private void endSymbols(MCSGraph.Side side, int[] symbol, int offset, int[] low, int[] high) {
            for (int atom = 0; atom < side.atomCount; atom++) {
                for (int place = side.start[atom]; place < side.start[atom + 1]; place++) {
                    int other = side.neighbour[place], bond = offset + side.neighbourBond[place];
                    if (other > atom && symbol[atom] >= 0 && symbol[other] >= 0) {
                        low[bond] = Math.min(symbol[atom], symbol[other]);
                        high[bond] = Math.max(symbol[atom], symbol[other]);
                    }
                }
            }
        }

        /**
         * The label of each bond, or -1 for none. Bonds of the same key share a
         * label if both molecules have such a bond.
         */
        private int[] labels(long[] keys, int queryBonds) {
            int[] rank = MCSGraph.denseRanks(keys), label = new int[keys.length], labelOf = new int[keys.length];
            boolean[] inQuery = new boolean[keys.length], inTarget = new boolean[keys.length];
            for (int bond = 0; bond < keys.length; bond++) {
                if (keys[bond] >= 0 && bond < queryBonds)
                    inQuery[rank[bond]] = true;
                else if (keys[bond] >= 0)
                    inTarget[rank[bond]] = true;
            }
            int labels = 0;
            for (int keyRank = 0; keyRank < keys.length; keyRank++)
                label[keyRank] = inQuery[keyRank] && inTarget[keyRank] ? labels++ : -1;
            for (int bond = 0; bond < keys.length; bond++)
                labelOf[bond] = keys[bond] < 0 ? -1 : label[rank[bond]];
            return labelOf;
        }

        /**
         * The bound of the state, where the open atoms are those ranked up to
         * {@code rankLimit}. The {@code open} new bonds at the mapped atoms are
         * already counted. A new atom has a new common bond and the symbol of
         * its image. A fragment of b new bonds has at most b + 1 atoms.
         */
        long bound(int rankLimit, int open) {
            Arrays.fill(unusedTargets, -1L);
            for (int i = 0; i < depth; i++)
                clear(unusedTargets, 0, levels[i].targetAtom);
            Arrays.fill(reachableTargets, 0);
            int added = open, newAtoms = 0;
            for (int rank = 0; rank <= rankLimit; rank++) {
                int queryAtom = queryOrder[rank];
                long any = 0;
                clock.tick(words);
                for (int word = 0; queryToTarget[queryAtom] < 0 && word < words; word++) {
                    long reachable = allowed[queryAtom * words + word] & unusedTargets[word];
                    reachableTargets[word] |= reachable;
                    any |= reachable;
                }
                if (any != 0)
                    queryLabels.markOpen(queryAtom);
            }
            for (int targetAtom = nextBit(reachableTargets, 0, words, 0); targetAtom >= 0;
                 targetAtom = nextBit(reachableTargets, 0, words, targetAtom + 1))
                targetLabels.markOpen(targetAtom);
            countOpen(queryLabels, query, true);
            countOpen(targetLabels, target, false);
            for (int slot = 0; slot < slotSymbol.length; slot += 2)
                added += slotSymbol[slot + 1] < 0 ? slotBonds(slot) / 2
                                                  : Math.min(slotBonds(slot), slotBonds(slot + 1));
            for (int symbol = 0; symbol < queryLabels.bySymbol.length; symbol++) {
                newAtoms += Math.min(queryLabels.bySymbol[symbol], targetLabels.bySymbol[symbol]);
                queryLabels.bySymbol[symbol] = targetLabels.bySymbol[symbol] = 0;
            }
            queryLabels.clearOpen();
            targetLabels.clearOpen();
            return capped(depth + Math.min(newAtoms, added + added / minBonds), commonBonds + added);
        }

        /**
         * Counts the open atoms by slot and by their number of open bonds in
         * it. Those with an open bond or a link are also counted by symbol.
         */
        private void countOpen(Labels labels, MCSGraph.Side side, boolean onQuery) {
            int work = labels.openCount;
            for (int openIndex = 0; openIndex < labels.openCount; openIndex++) {
                int atom = labels.openAtoms[openIndex], count = 0;
                boolean link = false;
                work += side.degree(atom);
                for (int place = side.start[atom]; place < side.start[atom + 1]; place++) {
                    int slot = labels.slot[place];
                    if (slot < 0 || !labels.isOpen[side.neighbour[place]])
                        link |= reach.isLink(onQuery, side.neighbourBond[place]);
                    else if (slotCount[slot]++ == 0)
                        slotsAt[count++] = slot;
                }
                for (int slotIndex = 0; slotIndex < count; slotIndex++) {
                    labels.histogram[slotsAt[slotIndex] * histogramWidth + slotCount[slotsAt[slotIndex]]]++;
                    slotCount[slotsAt[slotIndex]] = 0;
                }
                if (count > 0 || link)
                    labels.bySymbol[labels.symbol[atom]]++;
            }
            clock.tick(work);
        }

        /**
         * The most new bonds of a slot, pairing the open atoms with the most
         * bonds of it on either side. This clears the counts of the slot.
         */
        private int slotBonds(int slot) {
            int sum = 0;
            for (int openBonds = histogramWidth - 1, queryAtoms = 0, targetAtoms = 0; openBonds > 0; openBonds--) {
                queryAtoms += queryLabels.histogram[slot * histogramWidth + openBonds];
                targetAtoms += targetLabels.histogram[slot * histogramWidth + openBonds];
                queryLabels.histogram[slot * histogramWidth + openBonds] = 0;
                targetLabels.histogram[slot * histogramWidth + openBonds] = 0;
                sum += Math.min(queryAtoms, targetAtoms);
            }
            return sum;
        }

        /** The slot at each place, or -1 for none: twice the label of its bond, plus one at the second symbol. */
        private int[] slots(MCSGraph.Side side, int[] symbol, int[] labelOf, int offset) {
            int[] slot = new int[side.neighbour.length];
            for (int atom = 0; atom < side.atomCount; atom++) {
                for (int place = side.start[atom]; place < side.start[atom + 1]; place++) {
                    int label = labelOf[offset + side.neighbourBond[place]];
                    slot[place] = label < 0 ? -1 : 2 * label + (symbol[atom] == slotSymbol[2 * label] ? 0 : 1);
                }
            }
            return slot;
        }
    }

    /** The labels of one molecule in the MCES bound, and the open atoms of a state with their counts. */
    private static final class Labels {

        /** The symbol of each atom, and the slot at each place of the neighbour lists, see {@link EdgeBound}. */
        final int[] symbol, slot;
        /** The open atoms by slot and by the number of open bonds of that slot. */
        final int[] histogram;
        /** The open atoms by symbol that may get a new bond. */
        final int[] bySymbol;
        /** The open atoms, the first {@link #openCount} of the array. */
        final int[] openAtoms;
        /** Whether each atom is open. */
        final boolean[] isOpen;
        /** The number of open atoms. */
        int openCount;

        /**
         * The labels of a molecule from the {@code symbol} of each atom and the
         * {@code slot} at each place, with a histogram of {@code histogramSize}
         * and a count for each of the {@code symbols}.
         */
        Labels(int[] symbol, int[] slot, int histogramSize, int symbols) {
            this.symbol = symbol;
            this.slot = slot;
            histogram = new int[histogramSize];
            bySymbol = new int[symbols];
            openAtoms = new int[symbol.length];
            isOpen = new boolean[symbol.length];
        }

        /** Marks {@code atom}, which is not yet open, as open. */
        void markOpen(int atom) {
            isOpen[atom] = true;
            openAtoms[openCount++] = atom;
        }

        /** Closes every open atom. */
        void clearOpen() {
            for (int openIndex = 0; openIndex < openCount; openIndex++)
                isOpen[openAtoms[openIndex]] = false;
            openCount = 0;
        }
    }

    /**
     * A bound on the connected mappings, from the branches at the chain atoms
     * of each molecule. A chain atom has no ring bonds, so each of its bonds
     * leads to a branch joined to the rest through that bond only. When a
     * connected mapping maps one chain atom onto another, each query branch
     * maps into one target branch at most, and no two query branches into the
     * same one. The best pairing of the branches, each pair bounded in the
     * same way from the branches beyond it, then bounds every connected
     * mapping that contains the two atoms. A ring atom, or an atom with more
     * than {@link #MAX_BRANCHES} branches, is bounded only by the symbols and
     * bond labels that its part of the molecule has in common with the other.
     * The bound needs a query without expressions.
     */
    static final class BridgeBound {

        /** The most branches at an atom that are paired off. */
        private static final int MAX_BRANCHES = 8;
        /** The most pairs of query and target parts, 8 MB of bounds. */
        private static final long MAX_PART_PAIRS = 1_000_000;
        /** The most counts of symbols and bond labels for the parts of both molecules, 8 MB. */
        private static final long MAX_COUNTS = 2_000_000;
        /** The score of one atom, and of one bond, by atoms first; bounds add up in these units. */
        private static final long ONE_ATOM = MCSGraph.score(1, 0, false), ONE_BOND = MCSGraph.score(0, 1, false);

        /** The graphs, for their atom classes and compatible bonds. */
        private final MCSGraph graph;
        /** The parts of the query and of the target. */
        private final Parts queryParts, targetParts;
        /**
         * The bound of each pair of parts, in a row of target parts for each
         * query part, or 0 if their roots do not match. The first rows and
         * columns are the atoms, so a pair of atoms is read directly.
         */
        private final long[] pairBounds;
        /** Scratch of {@link #pairing}: the best score for each set of target branches used. */
        private final long[] bestByUsed = new long[1 << MAX_BRANCHES];
        /** A score that no connected mapping beats. */
        final long upperBound;

        /**
         * The bound, or null if the query has expressions, if every bond of
         * either molecule is on a ring, or if the tables would be too large.
         * The compatible pairs must have been worked out.
         */
        static BridgeBound create(MCSGraph graph, MCSGraph.Clock clock) {
            if (!graph.plainQuery)
                return null;
            MCSGraph.Side query = graph.query, target = graph.target;
            boolean[] queryRingBonds = query.ringBonds(), targetRingBonds = target.ringBonds();
            long queryPartCount = Parts.count(query, queryRingBonds);
            long targetPartCount = Parts.count(target, targetRingBonds);
            long labels = graph.atomClasses + query.bondCount + target.bondCount;
            if (queryPartCount == query.atomCount || targetPartCount == target.atomCount
                || queryPartCount * targetPartCount > MAX_PART_PAIRS
                || (queryPartCount + targetPartCount) * labels > MAX_COUNTS)
                return null;
            return new BridgeBound(graph, queryRingBonds, targetRingBonds, clock);
        }

        /**
         * Works out the bound of every pair of parts, the smallest parts
         * first so that the branches of a part are bounded before it, and
         * then the upper bound from the pairs of anchors.
         */
        private BridgeBound(MCSGraph graph, boolean[] queryRingBonds, boolean[] targetRingBonds,
                            MCSGraph.Clock clock) {
            this.graph = graph;
            // Only the bond labels of the target can be common.
            long[] labels = LongStream.of(graph.target.bondLabels(graph.atomClasses)).distinct().toArray();
            queryParts = new Parts(graph.query, queryRingBonds, graph.atomClasses, labels, clock);
            targetParts = new Parts(graph.target, targetRingBonds, graph.atomClasses, labels, clock);
            pairBounds = new long[queryParts.count * targetParts.count];
            for (int queryPart : queryParts.order) {
                for (int targetPart : targetParts.order) {
                    clock.tick();
                    pairBounds[queryPart * targetParts.count + targetPart] = pairBound(queryPart, targetPart, clock);
                }
            }
            // Without a pair of anchors, each mapped pair has an atom that is no anchor on one side or the other.
            long best = MCSGraph.score(queryParts.nonAnchors + targetParts.nonAnchors,
                                       Math.min(graph.query.bondCount, graph.target.bondCount), false);
            for (int queryAtom = 0; queryAtom < graph.query.atomCount; queryAtom++) {
                if (!queryParts.anchor[queryAtom])
                    continue;
                for (int targetAtom = 0; targetAtom < graph.target.atomCount; targetAtom++) {
                    clock.tick();
                    if (targetParts.anchor[targetAtom])
                        best = Math.max(best, bound(queryAtom, targetAtom));
                }
            }
            upperBound = best;
        }

        /** A score that no connected mapping of {@code queryAtom} onto {@code targetAtom} beats. */
        long bound(int queryAtom, int targetAtom) {
            return pairBounds[queryAtom * targetParts.count + targetAtom];
        }

        /**
         * The bound of two parts: the symbols and bond labels they have in
         * common and, for two chain atoms, the best pairing of their branches.
         */
        private long pairBound(int queryPart, int targetPart, MCSGraph.Clock clock) {
            int queryRoot = queryParts.root[queryPart], targetRoot = targetParts.root[targetPart];
            int atomClass = graph.query.atomClass[queryRoot];
            if (atomClass < 0 || atomClass != graph.target.atomClass[targetRoot])
                return 0;
            long bound = labelBound(queryPart, targetPart, clock);
            if (queryParts.chainAtom[queryRoot] && targetParts.chainAtom[targetRoot]
                && queryParts.branches[queryPart].length <= MAX_BRANCHES
                && targetParts.branches[targetPart].length <= MAX_BRANCHES)
                bound = Math.min(bound, ONE_ATOM + pairing(queryPart, targetPart, clock));
            return bound;
        }

        /**
         * The atoms and bonds two parts have in common by symbol and by bond
         * label, with no more bonds than the atoms can hold.
         */
        private long labelBound(int queryPart, int targetPart, MCSGraph.Clock clock) {
            int[] queryAtoms = queryParts.atomCounts[queryPart], targetAtoms = targetParts.atomCounts[targetPart];
            int[] queryBonds = queryParts.bondCounts[queryPart], targetBonds = targetParts.bondCounts[targetPart];
            clock.tick(queryAtoms.length + queryBonds.length);
            int atoms = 0;
            for (int atomClass = 0; atomClass < queryAtoms.length; atomClass++)
                atoms += Math.min(queryAtoms[atomClass], targetAtoms[atomClass]);
            int bonds = 0;
            for (int label = 0; label < queryBonds.length; label++)
                bonds += Math.min(queryBonds[label], targetBonds[label]);
            return MCSGraph.score(atoms, Math.min(bonds, atoms * (atoms - 1) / 2), false);
        }

        /**
         * The most that the branches of two chain atoms add to a mapping of
         * the two. Each query branch is paired with one target branch at most,
         * through matching bonds, and adds the bound of the pair and the bond
         * to it. A branch may also be left out.
         */
        private long pairing(int queryPart, int targetPart, MCSGraph.Clock clock) {
            int[] queryBranches = queryParts.branches[queryPart], targetBranches = targetParts.branches[targetPart];
            int sets = 1 << targetBranches.length;
            long[] best = bestByUsed;
            // A set that no pairing uses yet holds -1.
            Arrays.fill(best, 0, sets, -1);
            best[0] = 0;
            for (int queryBranch : queryBranches) {
                clock.tick(sets * (1 + targetBranches.length));
                int queryBond = queryParts.parentBond[queryBranch];
                // Supersets first, so that each query branch is paired at most once.
                for (int used = sets - 1; used >= 0; used--) {
                    if (best[used] < 0)
                        continue;
                    for (int branchIndex = 0; branchIndex < targetBranches.length; branchIndex++) {
                        int targetBranch = targetBranches[branchIndex];
                        long pair = pairBounds[queryBranch * targetParts.count + targetBranch];
                        if ((used & 1 << branchIndex) != 0 || pair == 0
                            || !graph.bondsMatch(queryBond, targetParts.parentBond[targetBranch]))
                            continue;
                        int with = used | 1 << branchIndex;
                        best[with] = Math.max(best[with], best[used] + pair + ONE_BOND);
                    }
                }
            }
            long most = 0;
            for (int used = 0; used < sets; used++)
                most = Math.max(most, best[used]);
            return most;
        }

        /**
         * The parts of one molecule, for {@link BridgeBound}. A part is a root
         * atom and the atoms reached from it. The first parts are the atoms,
         * each with every atom connected to it. Then each bond on no ring
         * gives two parts, the atoms on either side of it, rooted at the end
         * on that side.
         */
        private static final class Parts {

            /** The number of parts. */
            final int count;
            /** The root of each part, and the bond into it, or -1 for an atom with all its branches. */
            final int[] root, parentBond;
            /**
             * The branches at the root of each part: the parts beyond its
             * bonds on no ring, other than the bond into the part.
             */
            final int[][] branches;
            /** The atoms of each part by symbol class, and its bonds by label. */
            final int[][] atomCounts, bondCounts;
            /** The parts, the smallest first, so that a branch comes before the parts that hold it. */
            final int[] order;
            /** The atoms with no ring bonds. */
            final boolean[] chainAtom;
            /** The chain atoms whose pairs bound the mappings: all but a leaf on a ring atom. */
            final boolean[] anchor;
            /** The atoms that are no anchor. */
            final int nonAnchors;

            /** The parts of {@code side}, its bond labels numbered by their place in {@code labels}. */
            Parts(MCSGraph.Side side, boolean[] ringBond, int atomClasses, long[] labels, MCSGraph.Clock clock) {
                count = count(side, ringBond);
                root = new int[count];
                parentBond = new int[count];
                chainAtom = new boolean[side.atomCount];
                int[] beyond = numberParts(side, ringBond);
                anchor = new boolean[side.atomCount];
                int others = 0;
                for (int atom = 0; atom < side.atomCount; atom++) {
                    // A leaf on a ring atom is no anchor, as the branch from it holds the whole ring system.
                    boolean leafOnRing = side.degree(atom) == 1 && !chainAtom[side.neighbour[side.start[atom]]];
                    anchor[atom] = chainAtom[atom] && !leafOnRing;
                    if (!anchor[atom])
                        others++;
                }
                nonAnchors = others;
                branches = new int[count][];
                atomCounts = new int[count][atomClasses];
                bondCounts = new int[count][labels.length];
                int[] bondLabel = bondLabels(side, atomClasses, labels);
                int[] size = new int[count], queue = new int[side.atomCount], seen = new int[side.atomCount];
                for (int part = 0; part < count; part++) {
                    branches[part] = branchesAt(side, part, beyond);
                    size[part] = countLabels(side, part, bondLabel, queue, seen, clock);
                }
                order = bySize(size);
            }

            /** The number of parts: one for each atom, and two for each bond on no ring. */
            static int count(MCSGraph.Side side, boolean[] ringBond) {
                int count = side.atomCount;
                for (boolean ring : ringBond) {
                    if (!ring)
                        count += 2;
                }
                return count;
            }

            /**
             * Sets the root of each part and the bond into it, and marks the
             * chain atoms. Returns the part beyond each place of the neighbour
             * lists, or -1 for a ring bond.
             */
            private int[] numberParts(MCSGraph.Side side, boolean[] ringBond) {
                int[] beyond = new int[side.neighbour.length];
                Arrays.fill(beyond, -1);
                Arrays.fill(parentBond, -1);
                int next = side.atomCount;
                for (int atom = 0; atom < side.atomCount; atom++) {
                    root[atom] = atom;
                    chainAtom[atom] = true;
                    for (int place = side.start[atom]; place < side.start[atom + 1]; place++) {
                        if (ringBond[side.neighbourBond[place]]) {
                            chainAtom[atom] = false;
                        } else {
                            beyond[place] = next;
                            root[next] = side.neighbour[place];
                            parentBond[next] = side.neighbourBond[place];
                            next++;
                        }
                    }
                }
                return beyond;
            }

            /**
             * The branches at the root of {@code part}: the parts beyond its
             * bonds on no ring, other than the bond into the part.
             */
            private int[] branchesAt(MCSGraph.Side side, int part, int[] beyond) {
                int atom = root[part];
                int[] found = new int[side.degree(atom)];
                int size = 0;
                for (int i = side.start[atom]; i < side.start[atom + 1]; i++) {
                    if (beyond[i] >= 0 && side.neighbourBond[i] != parentBond[part])
                        found[size++] = beyond[i];
                }
                return Arrays.copyOf(found, size);
            }

            /**
             * Counts the atoms of {@code part} by symbol class and its bonds
             * by label, and returns its number of atoms. The atoms reached are
             * marked with {@code part + 1} in {@code seen}.
             */
            private int countLabels(MCSGraph.Side side, int part, int[] bondLabel, int[] queue, int[] seen,
                                    MCSGraph.Clock clock) {
                int size = 1;
                queue[0] = root[part];
                seen[root[part]] = part + 1;
                for (int head = 0; head < size; head++) {
                    int atom = queue[head];
                    clock.tick(1 + side.degree(atom));
                    if (side.atomClass[atom] >= 0)
                        atomCounts[part][side.atomClass[atom]]++;
                    for (int place = side.start[atom]; place < side.start[atom + 1]; place++) {
                        int other = side.neighbour[place], bond = side.neighbourBond[place];
                        if (bond == parentBond[part])
                            continue;
                        // Each bond is counted at its higher end.
                        if (other < atom && bondLabel[bond] >= 0)
                            bondCounts[part][bondLabel[bond]]++;
                        if (seen[other] != part + 1) {
                            seen[other] = part + 1;
                            queue[size++] = other;
                        }
                    }
                }
                return size;
            }

            /** The place of the label of each bond in {@code labels}, or -1 if it is not there. */
            private static int[] bondLabels(MCSGraph.Side side, int atomClasses, long[] labels) {
                int[] labelPlace = new int[side.bondCount];
                Arrays.fill(labelPlace, -1);
                for (int atom = 0; atom < side.atomCount; atom++) {
                    for (int place = side.start[atom]; place < side.start[atom + 1]; place++) {
                        int other = side.neighbour[place], bond = side.neighbourBond[place];
                        if (side.atomClass[atom] < 0 || side.atomClass[other] < 0)
                            continue;
                        int found = Arrays.binarySearch(labels, side.bondLabel(atom, other, bond, atomClasses));
                        if (found >= 0)
                            labelPlace[bond] = found;
                    }
                }
                return labelPlace;
            }

            /** The parts by size, the smallest first. */
            private static int[] bySize(int[] size) {
                Integer[] parts = new Integer[size.length];
                for (int part = 0; part < size.length; part++)
                    parts[part] = part;
                Arrays.sort(parts, Comparator.comparingInt(part -> size[part]));
                int[] order = new int[size.length];
                for (int position = 0; position < size.length; position++)
                    order[position] = parts[position];
                return order;
            }
        }
    }

    /** A union-find over {@code size} items, each in a set of its own: the parent of each item is itself. */
    private static int[] singletons(int size) {
        int[] parent = new int[size];
        for (int i = 0; i < size; i++)
            parent[i] = i;
        return parent;
    }

    /** The root of the set of {@code item}, halving the path to it on the way. */
    private static int find(int[] parent, int item) {
        while (parent[item] != item)
            item = parent[item] = parent[parent[item]];
        return item;
    }

    /** Joins the sets of {@code first} and {@code second}. */
    private static void unite(int[] parent, int first, int second) {
        parent[find(parent, first)] = find(parent, second);
    }

    /**
     * The embeddings of a connected query: maps of every query atom that take
     * each query bond onto a compatible target bond, with other target bonds
     * allowed. They are found by backtracking {@cdk.cite Ullmann76} with
     * forward checking {@cdk.cite HaralickElliott80}. Placing an atom narrows
     * the candidates of each unplaced neighbour to the free neighbours of its
     * target, and these neighbours must be able to have distinct targets. The
     * next atom placed is the one with the fewest free candidates. There is no
     * recursion, as the depth is the number of query atoms.
     */
    final class Embedding {

        /** The target atom each query atom is placed on, and the query atom placed on each target atom, or -1. */
        private final int[] placedOn, placedAtom;
        /**
         * The candidates of each query atom in the order they are tried, the
         * target atoms it may still have. They are set for the first atom at
         * the start, and for any other once a neighbour is placed. Until then
         * they are null.
         */
        private final int[][] candidates;
        /** The number of candidates of each unplaced query atom that are free. */
        private final int[] freeCount;
        /** The unplaced query atoms that have candidates, the first {@link #frontierSize}, in any order. */
        private final int[] frontier;
        /** The number of atoms on the frontier. */
        private int frontierSize;
        /** Room for the candidates of a neighbour while they are found, before they are copied. */
        private final int[] scratch;

        /** Starts from the query atom with the fewest targets it may have, then most bonds, then lowest index. */
        private Embedding(long[] candidateRows) {
            placedOn = new int[queryCount];
            placedAtom = new int[targetCount];
            Arrays.fill(placedOn, -1);
            Arrays.fill(placedAtom, -1);
            candidates = new int[queryCount][];
            freeCount = new int[queryCount];
            frontier = new int[queryCount];
            scratch = new int[target.maxDegree()];
            int root = 0, fewest = Integer.MAX_VALUE;
            for (int queryAtom = 0; queryAtom < queryCount; queryAtom++) {
                int count = 0;
                for (int i = 0; i < words; i++)
                    count += Long.bitCount(candidateRows[queryAtom * words + i]);
                if (count < fewest || count == fewest && query.degree(queryAtom) > query.degree(root)) {
                    root = queryAtom;
                    fewest = count;
                }
            }
            int[] targets = new int[fewest];
            int row = root * words;
            int filled = 0;
            for (int targetAtom = nextBit(candidateRows, row, words, 0); targetAtom >= 0;
                 targetAtom = nextBit(candidateRows, row, words, targetAtom + 1))
                targets[filled++] = targetAtom;
            candidates[root] = targets;
            freeCount[root] = fewest;
            frontier[frontierSize++] = root;
        }

        /**
         * The embeddings, at most {@code maxMappings}, held as the mappings as
         * they are found. Called once only, from the state the constructor
         * sets up.
         */
        List<int[]> all(int maxMappings) {
            mappings = new ArrayList<>();
            Placement[] placements = new Placement[queryCount];
            placements[0] = new Placement().enter(frontier[0], 0);
            for (int placed = 1; placed > 0; ) {
                clock.tick();
                Placement top = placements[placed - 1];
                if (placedOn[top.atom] >= 0)
                    unplace(top);
                int targetAtom = nextTarget(top);
                if (targetAtom < 0) {
                    placed--;
                } else if (place(top, targetAtom) && graph.stereoConsistent(placedOn, clock)) {
                    int next = choose();
                    if (next >= 0) {
                        if (placements[placed] == null)
                            placements[placed] = new Placement();
                        placements[placed++].enter(frontier[next], next);
                    } else if (placed == queryCount && (query.rings == null || complete(placedOn, false))) {
                        mappings.add(placedOn.clone());
                        if (mappings.size() == maxMappings)
                            return mappings;
                    }
                }
            }
            return mappings;
        }

        /** The next free candidate of the atom of {@code placement}, or -1 once it has tried them all. */
        private int nextTarget(Placement placement) {
            int[] targets = candidates[placement.atom];
            while (placement.nextCandidate < targets.length) {
                int targetAtom = targets[placement.nextCandidate++];
                if (placedAtom[targetAtom] < 0)
                    return targetAtom;
            }
            return -1;
        }

        /**
         * Places the atom of {@code placement} on {@code targetAtom} and records
         * the changes in {@code placement}, to be undone on failure.
         */
        private boolean place(Placement placement, int targetAtom) {
            int queryAtom = placement.atom;
            frontier[placement.frontierPlace] = frontier[--frontierSize];
            placedOn[queryAtom] = targetAtom;
            placedAtom[targetAtom] = queryAtom;
            take(placement, targetAtom);
            int from = target.start[targetAtom], degree = target.degree(targetAtom), open = 0, narrowed = 0;
            clock.tick(1 + query.degree(queryAtom));
            for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++)
                if (placedOn[query.neighbour[queryPlace]] < 0)
                    open++;
            // The candidates of each neighbour as places among the target's neighbours, for the check at the end.
            long[] rows = open > 1 && degree <= Long.SIZE ? new long[open] : null;
            for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
                int queryNeighbour = query.neighbour[queryPlace], count = 0;
                if (placedOn[queryNeighbour] >= 0)
                    continue;
                // A neighbour with candidates has another placed neighbour, so it closes a ring.
                boolean closes = candidates[queryNeighbour] != null;
                clock.tick(1 + (closes ? degree * (1 + query.degree(queryNeighbour)) : degree));
                long bits = 0;
                for (int offset = 0; offset < degree; offset++) {
                    int candidateTarget = target.neighbour[from + offset];
                    if (placedAtom[candidateTarget] < 0
                        && graph.bondsMatch(query.neighbourBond[queryPlace], target.neighbourBond[from + offset])
                        && mayMap(queryNeighbour, candidateTarget)
                        && (!closes || bondsFit(queryNeighbour, candidateTarget, queryAtom))) {
                        scratch[count++] = candidateTarget;
                        bits |= 1L << offset;
                    }
                }
                if (count == 0)
                    return false;
                if (!closes)
                    frontier[frontierSize++] = queryNeighbour;
                placement.remember(queryNeighbour, candidates[queryNeighbour], freeCount[queryNeighbour]);
                candidates[queryNeighbour] = Arrays.copyOf(scratch, count);
                freeCount[queryNeighbour] = count;
                if (rows != null)
                    rows[narrowed++] = bits;
            }
            return rows == null || hasDistinctTargets(rows, narrowed, 1, clock);
        }

        /** Takes the atom of {@code placement} off its target, and puts back everything that placing it changed. */
        private void unplace(Placement placement) {
            int queryAtom = placement.atom;
            while (placement.changedCount > 0) {
                int changed = --placement.changedCount, changedAtom = placement.changedAtoms[changed];
                // A neighbour that had no candidates came on the frontier last.
                if (placement.oldCandidates[changed] == null)
                    frontierSize--;
                candidates[changedAtom] = placement.oldCandidates[changed];
                freeCount[changedAtom] = placement.oldFreeCounts[changed];
            }
            placedAtom[placedOn[queryAtom]] = -1;
            placedOn[queryAtom] = -1;
            frontier[frontierSize++] = frontier[placement.frontierPlace];
            frontier[placement.frontierPlace] = queryAtom;
        }

        /**
         * The frontier place of the atom to place next, with the fewest free
         * candidates, then the most bonds, then the lowest index. It is -1 if
         * the frontier is empty or an atom on it has no free candidate left.
         */
        private int choose() {
            clock.tick(frontierSize);
            int pick = -1;
            for (int frontierIndex = 0; frontierIndex < frontierSize; frontierIndex++) {
                int candidate = frontier[frontierIndex], picked = pick < 0 ? -1 : frontier[pick];
                if (freeCount[candidate] == 0)
                    return -1;
                if (picked < 0 || freeCount[candidate] < freeCount[picked]
                    || freeCount[candidate] == freeCount[picked]
                       && (query.degree(candidate) > query.degree(picked)
                           || query.degree(candidate) == query.degree(picked) && candidate < picked))
                    pick = frontierIndex;
            }
            return pick;
        }

        /**
         * Takes target atom {@code targetAtom} from the free candidates of the unplaced
         * atoms that have it, remembering their counts in {@code placement}. Such an
         * atom may map onto {@code targetAtom} with compatible bonds to the targets of
         * all its placed neighbours, and is counted from the first of these.
         */
        private void take(Placement placement, int targetAtom) {
            int work = 1 + target.degree(targetAtom);
            for (int targetPlace = target.start[targetAtom]; targetPlace < target.start[targetAtom + 1];
                 targetPlace++) {
                int placedNeighbour = placedAtom[target.neighbour[targetPlace]];
                if (placedNeighbour < 0)
                    continue;
                for (int queryPlace = query.start[placedNeighbour]; queryPlace < query.start[placedNeighbour + 1];
                     queryPlace++) {
                    int queryNeighbour = query.neighbour[queryPlace];
                    work++;
                    if (placedOn[queryNeighbour] >= 0
                        || !graph.bondsMatch(query.neighbourBond[queryPlace], target.neighbourBond[targetPlace])
                        || !mayMap(queryNeighbour, targetAtom))
                        continue;
                    work += query.degree(queryNeighbour);
                    if (firstPlacedNeighbour(queryNeighbour) == placedNeighbour
                        && bondsFit(queryNeighbour, targetAtom, placedNeighbour)) {
                        placement.remember(queryNeighbour, candidates[queryNeighbour], freeCount[queryNeighbour]);
                        freeCount[queryNeighbour]--;
                    }
                }
            }
            clock.tick(work);
        }

        /** Whether {@code queryAtom} may map onto {@code targetAtom}: they match, and the target has as many bonds. */
        private boolean mayMap(int queryAtom, int targetAtom) {
            return has(allowed, queryAtom, targetAtom) && target.degree(targetAtom) >= query.degree(queryAtom);
        }

        /**
         * Whether {@code targetAtom} has compatible bonds to the images of the
         * placed neighbours of {@code queryAtom} other than {@code skipped}.
         */
        private boolean bondsFit(int queryAtom, int targetAtom, int skipped) {
            for (int queryPlace = query.start[queryAtom]; queryPlace < query.start[queryAtom + 1]; queryPlace++) {
                int queryNeighbour = query.neighbour[queryPlace], neighbourImage = placedOn[queryNeighbour];
                if (neighbourImage < 0 || queryNeighbour == skipped)
                    continue;
                // Search the shorter list of neighbours, as one may be a hub.
                int targetBond = target.degree(targetAtom) <= target.degree(neighbourImage)
                                 ? target.bondBetween(targetAtom, neighbourImage)
                                 : target.bondBetween(neighbourImage, targetAtom);
                if (targetBond < 0 || !graph.bondsMatch(query.neighbourBond[queryPlace], targetBond))
                    return false;
            }
            return true;
        }

        /** The first placed neighbour of query atom {@code queryAtom}, or -1 if none is placed. */
        private int firstPlacedNeighbour(int queryAtom) {
            for (int i = query.start[queryAtom]; i < query.start[queryAtom + 1]; i++) {
                if (placedOn[query.neighbour[i]] >= 0)
                    return query.neighbour[i];
            }
            return -1;
        }
    }

    /**
     * A placed atom of the whole-query search, one for each depth. It holds
     * the place the atom had on the frontier, the next of its candidates to
     * try, and the candidates and counts that placing it changed as they
     * were before, so that taking it off puts them back.
     */
    private static final class Placement {

        /** The placed atom, the place it had on the frontier, and the next of its candidates to try. */
        int atom, frontierPlace, nextCandidate;
        /** The number of changes recorded. */
        int changedCount;
        /** The atoms whose candidates or counts changed, the first {@link #changedCount}, and their old counts. */
        int[] changedAtoms = new int[4], oldFreeCounts = new int[4];
        /** The old candidates of each changed atom, null where it had none. */
        int[][] oldCandidates = new int[4][];

        /** Starts placing {@code atom}, from place {@code frontierPlace} of the frontier, at its first candidate. */
        Placement enter(int atom, int frontierPlace) {
            this.atom = atom;
            this.frontierPlace = frontierPlace;
            nextCandidate = 0;
            return this;
        }

        /** Remembers that {@code atom} had these candidates, and this count of free ones. */
        void remember(int atom, int[] candidates, int freeCount) {
            if (changedCount == changedAtoms.length) {
                changedAtoms = Arrays.copyOf(changedAtoms, 2 * changedCount);
                oldFreeCounts = Arrays.copyOf(oldFreeCounts, 2 * changedCount);
                oldCandidates = Arrays.copyOf(oldCandidates, 2 * changedCount);
            }
            changedAtoms[changedCount] = atom;
            oldCandidates[changedCount] = candidates;
            oldFreeCounts[changedCount++] = freeCount;
        }
    }

    /**
     * Whether each of the first {@code count} rows can have a column of its
     * own. Each row is {@code words} longs of {@code rows}, and its columns are
     * their bits. A row takes a free column, or else follows an augmenting
     * path. There is no recursion, as there may be a row for each query atom.
     */
    private static boolean hasDistinctTargets(long[] rows, int count, int words, MCSGraph.Clock clock) {
        int columns = words << 6;
        int[] owner = new int[columns], visited = new int[columns], path = new int[count], resume = new int[count];
        long[] freeColumns = new long[words];
        Arrays.fill(owner, -1);
        Arrays.fill(freeColumns, -1L);
        for (int newRow = 0; newRow < count; newRow++) {
            clock.tick(words);
            int column = -1, step = 0;
            for (int word = 0; word < words && column < 0; word++) {
                long freeBits = rows[newRow * words + word] & freeColumns[word];
                if (freeBits != 0)
                    column = (word << 6) + Long.numberOfTrailingZeros(freeBits);
            }
            path[0] = newRow;
            resume[0] = 0;
            while (column < 0) {
                clock.tick();
                int row = path[step];
                column = nextBit(rows, row * words, words, resume[step]);
                while (column >= 0 && visited[column] == newRow + 1) {
                    clock.tick();
                    column = nextBit(rows, row * words, words, column + 1);
                }
                if (column < 0) {
                    if (--step < 0)
                        return false;
                    continue;
                }
                resume[step] = column + 1;
                visited[column] = newRow + 1;
                if (owner[column] >= 0) {
                    path[++step] = owner[column];
                    resume[step] = 0;
                    column = -1;
                }
            }
            // Each row on the path takes the column it reached. The last row takes a free column.
            freeColumns[column >>> 6] &= ~(1L << column);
            owner[column] = path[step];
            for (int earlier = step - 1; earlier >= 0; earlier--)
                owner[resume[earlier] - 1] = path[earlier];
        }
        return true;
    }

    /** Whether bit {@code bit} of row {@code row} is set, in rows of {@link #words} longs as in {@link #allowed}. */
    private boolean has(long[] rows, int row, int bit) {
        return (rows[row * words + (bit >>> 6)] & 1L << bit) != 0;
    }

    /** Sets bit {@code bit} of row {@code row}. */
    private void set(long[] rows, int row, int bit) {
        rows[row * words + (bit >>> 6)] |= 1L << bit;
    }

    /** Clears bit {@code bit} of row {@code row}. */
    private void clear(long[] rows, int row, int bit) {
        rows[row * words + (bit >>> 6)] &= ~(1L << bit);
    }

    /** The first set bit of a row of {@code words} longs from {@code start}, at or after bit {@code from}, or -1. */
    private static int nextBit(long[] bits, int start, int words, int from) {
        int wordIndex = from >>> 6;
        if (wordIndex >= words)
            return -1;
        long word = bits[start + wordIndex] & -1L << from;
        while (word == 0) {
            if (++wordIndex == words)
                return -1;
            word = bits[start + wordIndex];
        }
        return (wordIndex << 6) + Long.numberOfTrailingZeros(word);
    }

    /** The inverse of a permutation: the position of each value. */
    private static int[] inverse(int[] permutation) {
        int[] inverse = new int[permutation.length];
        for (int i = 0; i < permutation.length; i++)
            inverse[permutation[i]] = i;
        return inverse;
    }
}
