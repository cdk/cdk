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
import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;

/**
 * The search behind {@link MCS}, on the int graphs of an {@link MCSGraph}.
 * It finds the connected mappings from the query to the target with the most
 * atoms and then the most common bonds. For a maximum common edge subgraph
 * (MCES), whose common bonds may form several fragments, it finds the most
 * common bonds and then the most atoms. When a connected query fits whole in
 * the target, its embeddings are the answer.
 * Otherwise a depth-first branch and bound grows the mappings pair by pair
 * from the neighbours of the mapped atoms, as VF2 does {@cdk.cite Cordella04},
 * starting from the score of a greedy seed. The search follows SMSD
 * {@cdk.cite SMSD2009}, and its label bound is that of SMSD Pro
 * {@cdk.cite SMSDPro2026}.
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
    private static final int INITIAL_PAIRS = 64;
    private static final int[] NONE = new int[0];

    private final MCSGraph graph;
    private final MCSGraph.Side query, target;
    private final MCSGraph.Clock clock;
    /** In MCES, the fewest common bonds of a fragment; 0 for the connected MCS. */
    private final int minBonds;
    /** The numbers of query and target atoms, and of longs in a row of target atoms. */
    private final int n, m, words;
    /** The bits a target atom takes in a pair, see {@link #pair}. */
    private final int targetBits;
    /** The compatible pairs that are not excluded: bit t of row q, see {@link MCSGraph#compatiblePairs}. */
    private final long[] allowed;
    /** The mapping both ways, -1 where unmapped. */
    private final int[] queryToTarget, targetToQuery;
    /** The mapped pairs in the order they were mapped, the first {@link #depth} of the array. */
    private final Level[] levels;
    private int depth, commonBonds;
    /** The pairs excluded so far, put back as the levels that tried them are taken off. */
    private int[] excluded = new int[INITIAL_PAIRS];
    private int excludedCount;
    /** The atoms by rank, lowest first, and the rank of each atom. */
    private int[] queryOrder, queryRank, targetOrder, targetRank;
    /** The cell of each atom: atoms that look alike share a cell. */
    private int[] queryCell, targetCell;
    /** The signatures of the atoms at each radius, which tell how alike two atoms are. */
    private long[][] querySignature, targetSignature;
    /** The next atom alike to each terminal atom, see {@link #twins}. */
    private int[] queryTwin, targetTwin;
    /** The best score so far, see {@link #score}, and a score that no mapping beats. */
    private long bestScore, upperBound;
    /** The mappings with the best score so far, and the most wanted. */
    private List<int[]> mappings;
    private int maxMappings;
    /** The seed, held at the front of the mappings from the start until the search finds it again. */
    private int[] heldSeed;
    /** The pairs reached from a state, for its bound, and in MCES the bound itself. */
    private final Reach reach;
    private final EdgeBound edgeBound;
    /** The bound from the branches at chain atoms, or null, see {@link BridgeBound}. */
    private BridgeBound bridgeBound;

    /** With {@code minBonds} above 0 the search finds an MCES whose fragments have that many common bonds or more. */
    MCSSearch(MCSGraph graph, MCSGraph.Clock clock, int minBonds) {
        this.graph = graph;
        this.clock = clock;
        this.minBonds = minBonds;
        query = graph.query;
        target = graph.target;
        n = query.atomCount;
        m = target.atomCount;
        words = MCSGraph.words(m);
        targetBits = 32 - Integer.numberOfLeadingZeros(Math.max(m - 1, 1));
        allowed = graph.compatiblePairs(clock);
        queryToTarget = new int[n];
        targetToQuery = new int[m];
        Arrays.fill(queryToTarget, -1);
        Arrays.fill(targetToQuery, -1);
        levels = new Level[Math.min(n, m)];
        reach = new Reach();
        edgeBound = minBonds > 0 ? new EdgeBound() : null;
    }

    /**
     * The maximum mappings, at most {@code maxMappings}. In MCES the best score
     * is found first, level by level of common bonds down from the upper bound.
     * Twins can then be skipped all along, see {@link #twinExcluded}. A second
     * search then collects the mappings of that score in the order a search
     * from the seed finds them.
     */
    List<int[]> run(int maxMappings) {
        clock.poll();
        // In MCES a fragment needs minBonds common bonds.
        if (!anyCompatible() || Math.min(query.bondCount, target.bondCount) < minBonds)
            return new ArrayList<>();
        // When the whole query embeds, those embeddings are the result. This search
        // checks the time limit and stereochemistry as it goes, and complete rings
        // on each embedding.
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
        long start = seed != null ? score(seed) : belowEveryMapping();
        if (minBonds == 0) {
            if (start < bound && Math.min(n, m) >= BRIDGE_BOUND_ATOMS)
                bridgeBound = BridgeBound.create(graph, clock);
            if (bridgeBound != null)
                bound = Math.min(bound, bridgeBound.upperBound);
            return collect(seed, start, bound, maxMappings);
        }
        List<int[]> mapping = new ArrayList<>();
        for (int b = bonds(bound); mapping.isEmpty() && b > bonds(start); b--) {
            mapping = collect(null, score(0, b) - 1, bound, 1);
            // No mapping has b common bonds, so the bound comes down to b - 1.
            if (mapping.isEmpty())
                bound = score(Math.min(atoms(bound), b - 1 + (b - 1) / minBonds), b - 1);
        }
        if (mapping.isEmpty())
            mapping = collect(seed, start, bound, 1);
        if (maxMappings == 1)
            return mapping;
        if (bestScore == start)
            return collect(seed, start, bestScore, maxMappings);
        return collect(null, bestScore - 1, bestScore, maxMappings);
    }

    /** A score that no mapping beats, when the query does not fit whole. */
    private long bestPossible() {
        long bound = score(Math.min(n, m), Math.min(query.bondCount, target.bondCount));
        // Without an embedding no mapping has every query atom and bond. In MCES a query in parts may still have
        // every bond.
        if (minBonds > 0 ? queryConnected() : query.bondCount > 0)
            bound = Math.min(bound, score(n, query.bondCount - 1));
        long label = graph.labelBound(minBonds == 0);
        if (label >= 0) {
            int labelAtoms = MCSGraph.atoms(label, false), labelBonds = MCSGraph.bonds(label, false);
            // In MCES a fragment of b common bonds has at most b + 1 atoms. Each has at least minBonds bonds, so
            // there are at most labelBonds / minBonds fragments.
            if (minBonds > 0)
                labelAtoms = Math.min(labelAtoms, labelBonds + labelBonds / minBonds);
            bound = Math.min(bound, score(labelAtoms, labelBonds));
        }
        if (minBonds == 0) {
            int mostAtoms = atoms(bound);
            int mostBonds = Math.min(query.bondBound(mostAtoms, clock), target.bondBound(mostAtoms, clock));
            bound = score(mostAtoms, Math.min(bonds(bound), mostBonds));
        }
        return bound;
    }

    /** A score below that of every mapping. In MCES every mapping has at least minBonds common bonds. */
    private long belowEveryMapping() {
        return minBonds > 0 ? score(n + 1, minBonds - 1) : 0;
    }

    private boolean anyCompatible() {
        for (long word : allowed) {
            if (word != 0)
                return true;
        }
        return false;
    }

    /**
     * Ranks the atoms and works out their signatures. Query atoms with the
     * fewest candidates, then the most bonds, rank highest.
     */
    void rank() {
        querySignature = query.signatures(clock);
        targetSignature = target.signatures(clock);
        int[] queryChoices = new int[n], targetChoices = new int[m];
        for (int q = 0; q < n; q++) {
            for (int t = nextBit(allowed, q * words, words, 0); t >= 0; t = nextBit(allowed, q * words, words, t + 1)) {
                clock.tick();
                queryChoices[q]++;
                targetChoices[t]++;
            }
        }
        int[] colours = query.invariants(queryChoices);
        queryCell = MCSGraph.refine(query.start, query.neighbour, colours, false, clock);
        int[] canon = MCSGraph.refine(query.start, query.neighbour, colours, true, clock);
        // Ties among target atoms stay in input order. Breaking them one atom at a time costs a round per atom on a
        // symmetric target, and the order only has to be repeatable.
        targetCell = MCSGraph.refine(target.start, target.neighbour, target.invariants(targetChoices), false, clock);
        int[] next = new int[m];
        targetRank = new int[m];
        for (int t = 0; t < m; t++)
            next[targetCell[t]] = targetCell[t];
        for (int t = 0; t < m; t++)
            targetRank[t] = next[targetCell[t]]++;
        targetOrder = inverse(targetRank);
        Integer[] byRank = new Integer[n];
        for (int q = 0; q < n; q++)
            byRank[q] = q;
        // The most choices come first, then the fewest bonds, then the lowest canonical rank.
        Comparator<Integer> byChoices = Comparator.comparingInt(q -> -queryChoices[q]);
        Arrays.sort(byRank, byChoices.thenComparingInt(query::degree).thenComparingInt(q -> canon[q]));
        queryOrder = new int[n];
        queryRank = new int[n];
        for (int k = 0; k < n; k++) {
            queryOrder[k] = byRank[k];
            queryRank[queryOrder[k]] = k;
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
        long[] taken = new long[n * words];
        int[] roots = new int[LIKENESS_LEVELS * SEEDS], rootCount = new int[LIKENESS_LEVELS];
        int[] frontier = new int[n], mapped = new int[n], added = new int[n];
        long bestSeed = belowEveryMapping();
        int[] best = null;
        boolean extend = true;
        for (int round = 0, keptAtoms = 0, keptBonds = 0; ; ) {
            long before = bestSeed;
            seedRoots(roots, rootCount, taken);
            for (int level = MCSGraph.RADII, tried = 0; level >= 0 && bestSeed != bound; level--) {
                for (int i = 0; i < rootCount[level] && tried < SEEDS && bestSeed != bound; i++, tried++) {
                    long grown = growSeed(roots[level * SEEDS + i], frontier, mapped, added, ++round);
                    long kept = cutBack(grown, mapped);
                    int seedAtoms = atoms(kept), seedBonds = bonds(kept);
                    if (seedBonds >= minBonds && score(keptAtoms + seedAtoms, keptBonds + seedBonds) > bestSeed) {
                        bestSeed = score(keptAtoms + seedAtoms, keptBonds + seedBonds);
                        best = queryToTarget.clone();
                        extend = kept == grown;
                    }
                    for (int a = 0; a < seedAtoms; a++) {
                        targetToQuery[queryToTarget[mapped[a]]] = -1;
                        queryToTarget[mapped[a]] = -1;
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
            System.arraycopy(best, 0, queryToTarget, 0, n);
            for (int q = 0; q < n; q++) {
                if (queryToTarget[q] >= 0)
                    targetToQuery[queryToTarget[q]] = q;
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
            int a = mapped[atoms(kept) - 1], v = queryToTarget[a];
            queryToTarget[a] = -1;
            targetToQuery[v] = -1;
            kept = score(atoms(kept) - 1, bonds(kept) - common(a, v));
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
        for (int k = n - 1; k >= 0 && rootCount[likest] < SEEDS; k--) {
            int q = queryOrder[k];
            for (int j = 0; j < m && rootCount[likest] < SEEDS && queryToTarget[q] < 0; j++) {
                int t = targetOrder[j];
                clock.tick();
                if (targetToQuery[t] >= 0 || !has(allowed, q, t))
                    continue;
                int level = likeness(q, t);
                if (rootCount[level] < SEEDS && !has(taken, queryCell[q], targetCell[t])) {
                    set(taken, queryCell[q], targetCell[t]);
                    roots[level * SEEDS + rootCount[level]++] = pair(q, t);
                }
            }
        }
    }

    /**
     * Grows a seed from the root pair, each time by the frontier pair most
     * alike, then with the most common bonds. The seed is left mapped, with its
     * query atoms in {@code mapped} in order. The result is its score.
     */
    private long growSeed(int root, int[] frontier, int[] mapped, int[] added, int round) {
        int count = 0, frontierSize = 0, seedBonds = 0;
        for (int p = root; p >= 0; ) {
            int u = queryAtom(p), v = targetAtom(p);
            queryToTarget[u] = v;
            targetToQuery[v] = u;
            mapped[count++] = u;
            for (int i = query.start[u]; i < query.start[u + 1]; i++) {
                int w = query.neighbour[i];
                if (queryToTarget[w] < 0 && added[w] != round) {
                    added[w] = round;
                    frontier[frontierSize++] = w;
                }
            }
            int likest = -1, links = 0;
            p = -1;
            for (int f = 0; f < frontierSize; ) {
                int a = frontier[f];
                if (queryToTarget[a] >= 0) {
                    frontier[f] = frontier[--frontierSize];
                    continue;
                }
                f++;
                for (int i = query.start[a]; i < query.start[a + 1]; i++) {
                    int x = queryToTarget[query.neighbour[i]];
                    // Next to a hub, the first few free targets are as good for a seed as all of them.
                    for (int j = x < 0 ? 0 : target.start[x], freeCount = 0;
                         x >= 0 && j < target.start[x + 1] && freeCount < HUB_TARGETS; j++) {
                        int b = target.neighbour[j];
                        clock.tick();
                        if (targetToQuery[b] >= 0 || !has(allowed, a, b)
                            || !graph.bondsMatch(query.neighbourBond[i], target.neighbourBond[j]))
                            continue;
                        freeCount++;
                        int l = likeness(a, b);
                        if (l < likest)
                            continue;
                        int c = common(a, b);
                        // Ties go to the higher ranked query atom, then to the lower ranked target atom.
                        if (l > likest || c > links || c == links && (queryRank[a] > queryRank[queryAtom(p)]
                                || a == queryAtom(p) && targetRank[b] < targetRank[targetAtom(p)])) {
                            p = pair(a, b);
                            likest = l;
                            links = c;
                        }
                    }
                }
            }
            seedBonds += links;
        }
        return score(count, seedBonds);
    }

    /** The number of radii, from 0, at which query atom {@code q} and target atom {@code t} look alike. */
    private int likeness(int q, int t) {
        int level = 0;
        while (level < MCSGraph.RADII && querySignature[level][q] == targetSignature[level][t])
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
        for (int k = n - 1; k >= 0 && !done() && (minBonds > 0 ? canImprove(rootBound(k))
                                                             : Math.min(k + 1, m) >= atoms(bestScore)); k--) {
            int u = queryOrder[k];
            for (int j = m - 1; j >= 0 && !done(); j--) {
                clock.tick();
                if (has(allowed, u, targetOrder[j]) && push(u, targetOrder[j], 0, 0, upperBound))
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
                int u = level.triedAtom, v = level.targets[level.nextTarget++];
                if (twinExcluded(u, v) || !push(u, v, level.fragmentStart, level.bondsBefore, level.bound))
                    exclude(u, v);
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
     * Maps {@code u} onto {@code v} as a new level. Its fragment starts at
     * level {@code fragmentStart}, after {@code bondsBefore} common bonds. The
     * level has the bound of the state it extends, or the bridge bound of the
     * pair if that is lower, until it has its own. False, with the pair taken
     * off again, if the bound prunes it or it breaks a configuration.
     */
    private boolean push(int u, int v, int fragmentStart, int bondsBefore, long bound) {
        if (bridgeBound != null) {
            bound = Math.min(bound, bridgeBound.bound(u, v));
            if (!canImprove(bound))
                return false;
        }
        if (levels[depth] == null)
            levels[depth] = new Level();
        Level level = levels[depth];
        level.queryAtom = u;
        level.targetAtom = v;
        level.addedBonds = common(u, v);
        level.freeQueryBonds = depth > 0 ? levels[depth - 1].freeQueryBonds : query.bondCount;
        level.freeTargetBonds = depth > 0 ? levels[depth - 1].freeTargetBonds : target.bondCount;
        for (int i = query.start[u]; i < query.start[u + 1]; i++) {
            if (queryToTarget[query.neighbour[i]] < 0)
                level.freeQueryBonds--;
        }
        for (int j = target.start[v]; j < target.start[v + 1]; j++) {
            if (targetToQuery[target.neighbour[j]] < 0)
                level.freeTargetBonds--;
        }
        level.excludedBefore = excludedCount;
        level.fragmentStart = fragmentStart;
        level.bondsBefore = bondsBefore;
        level.bound = bound;
        level.candidates = level.targets = NONE;
        level.nextCandidate = level.nextTarget = level.rootRank = 0;
        level.closed = false;
        queryToTarget[u] = v;
        targetToQuery[v] = u;
        depth++;
        commonBonds += level.addedBonds;
        // A mapping that breaks a configuration still breaks it with any pair added. The pair is rejected at once.
        if (!graph.stereoConsistent(queryToTarget, clock)) {
            takeOff();
            return false;
        }
        record(bondsBefore);
        // A leaf has nothing left to try. Every query atom up to the rank of the root is mapped, or every target
        // atom is, or the mapping has the atoms of the upper bound, in MCES its common bonds.
        if (done() || depth == queryRank[levels[0].queryAtom] + 1 || depth == m
            || (minBonds > 0 ? commonBonds == bonds(upperBound) : depth == atoms(upperBound)))
            return true;
        level.bound = bound(fragmentStart, bound);
        if (level.bound < 0) {
            takeOff();
            return false;
        }
        level.candidates = candidates();
        clock.tick(level.candidates.length);
        // In MCES a fragment with enough bonds offers new roots once its candidates are done. These rank below its
        // own root.
        if (minBonds > 0 && commonBonds - bondsBefore >= minBonds)
            level.rootRank = queryRank[levels[fragmentStart].queryAtom];
        return true;
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
            int p = excluded[--excludedCount];
            allow(queryAtom(p), targetAtom(p));
        }
        queryToTarget[level.queryAtom] = -1;
        targetToQuery[level.targetAtom] = -1;
        commonBonds -= level.addedBonds;
    }

    private void allow(int u, int v) {
        set(allowed, u, v);
    }

    /** Excludes the pair of {@code u} and {@code v}, which must be allowed. */
    private void exclude(int u, int v) {
        clear(allowed, u, v);
        if (excludedCount == excluded.length)
            excluded = Arrays.copyOf(excluded, 2 * excluded.length);
        excluded[excludedCount++] = pair(u, v);
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
        if (!level.closed) {
            if (hasPendantPair(level.fragmentStart))
                return false;
            // The roots offered start a fragment of their own.
            level.closed = true;
            level.fragmentStart = depth;
            level.bondsBefore = commonBonds;
        }
        while (--level.rootRank >= 0) {
            int u = queryOrder[level.rootRank];
            if (queryToTarget[u] >= 0)
                continue;
            level.bound = rootBound(level.rootRank);
            if (!canImprove(level.bound))
                return false;
            level.triedAtom = u;
            level.targets = freeTargets(u);
            level.nextTarget = 0;
            if (level.targets.length > 0)
                return true;
        }
        return false;
    }

    /**
     * Keeps the mapping if it beats the best score, or ties with it while
     * fewer than wanted are held. In MCES the open fragment must have enough
     * bonds. With complete rings the mapping must be complete.
     */
    private void record(int bondsBefore) {
        long current = score(depth, commonBonds);
        if (current < bestScore || current == bestScore && mappings.size() == maxMappings)
            return;
        // In MCES a fragment needs minBonds common bonds. The open one may still get them further down.
        if (commonBonds - bondsBefore < minBonds)
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
        int mostAtoms = minBonds > 0 ? Math.min(n, m) : atoms(upperBound);
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
        for (int i = 0, joined = 0; ; ) {
            for (; joined < reach.count; joined++)
                reach.join(reach.pairs[joined]);
            int links = Math.min(reach.queryLinks - queryLinks, reach.targetLinks - targetLinks);
            long bound = capped(depth + reach.groupSum, commonBonds + open + links);
            if (i == reach.count)
                return canImprove(bound) ? bound : -1;
            // With complete rings every pair is reached, as complete() reads the atoms of the reached pairs.
            if (query.rings == null && canImprove(bound))
                return current;
            for (int end = Math.min(i + REACH_BATCH, reach.count); i < end; i++) {
                clock.tick();
                int p = reach.pairs[i];
                reach.link(queryAtom(p), targetAtom(p));
            }
        }
    }

    /** The bound of the roots offered up to {@code rankLimit}. The fragment is closed, so no bond links it. */
    private long rootBound(int rankLimit) {
        reach.start();
        return edgeBound.bound(rankLimit, 0);
    }

    /** The score of a mapping, which compares by atoms first, or in MCES by common bonds first. */
    private long score(int atomCount, int bondCount) {
        return MCSGraph.score(atomCount, bondCount, minBonds > 0);
    }

    private int atoms(long score) {
        return MCSGraph.atoms(score, minBonds > 0);
    }

    private int bonds(long score) {
        return MCSGraph.bonds(score, minBonds > 0);
    }

    /**
     * The score of {@code mapping}: its atoms, and the compatible bonds
     * between them that the target has between their images.
     */
    long score(int[] mapping) {
        int atomCount = 0, bondCount = 0;
        for (int a = 0; a < n; a++) {
            int x = mapping[a];
            if (x < 0)
                continue;
            atomCount++;
            for (int i = query.start[a]; i < query.start[a + 1]; i++) {
                int b = query.neighbour[i], f = b < a && mapping[b] >= 0 ? target.bondBetween(x, mapping[b]) : -1;
                if (f >= 0 && graph.bondsMatch(query.neighbourBond[i], f))
                    bondCount++;
            }
        }
        return score(atomCount, bondCount);
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
        for (int i = 0, j; i < layer.length; i = j) {
            int u = queryAtom(layer[i]);
            j = i + 1;
            while (j < layer.length && queryAtom(layer[j]) == u)
                j++;
            keys[count++] = candidateKey(u, j - i);
        }
        Arrays.sort(keys, 0, count);
        int[] sorted = new int[count];
        for (int i = 0; i < count; i++)
            sorted[i] = candidateOf(keys[i]);
        return sorted;
    }

    /** The sort key of candidate {@code u}: whether it is terminal, its number of pairs, then its rank in reverse. */
    private long candidateKey(int u, int pairs) {
        return (query.degree(u) == 1 ? 1L << 62 : 0) | (long) pairs << 31 | n - 1 - queryRank[u];
    }

    private int candidateOf(long key) {
        return queryOrder[n - 1 - (int) (key & Integer.MAX_VALUE)];
    }

    /**
     * The targets of query atom {@code u}. These are the free neighbours of
     * the images of its mapped neighbours, joined by compatible bonds, in pairs
     * not excluded. The most alike come first, then by descending rank. The
     * order decides only which mappings are found first.
     */
    private int[] targets(int u) {
        int capacity = 0;
        for (int i = query.start[u]; i < query.start[u + 1]; i++) {
            if (queryToTarget[query.neighbour[i]] >= 0)
                capacity += target.degree(queryToTarget[query.neighbour[i]]);
        }
        long[] keys = new long[capacity];
        int count = 0;
        for (int i = query.start[u]; i < query.start[u + 1]; i++) {
            int x = queryToTarget[query.neighbour[i]];
            if (x < 0)
                continue;
            clock.tick(1 + target.degree(x));
            for (int j = target.start[x]; j < target.start[x + 1]; j++) {
                int v = target.neighbour[j];
                if (targetToQuery[v] < 0 && has(allowed, u, v)
                    && graph.bondsMatch(query.neighbourBond[i], target.neighbourBond[j]))
                    keys[count++] = targetKey(u, v);
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

    /** The sort key of target {@code v} of query atom {@code u}: how unlike the two are, then the rank in reverse. */
    private long targetKey(int u, int v) {
        return (long) (MCSGraph.RADII - likeness(u, v)) << 32 | m - 1 - targetRank[v];
    }

    private int targetOf(long key) {
        return targetOrder[m - 1 - (int) key];
    }

    /** The free targets of a root, by descending rank. */
    private int[] freeTargets(int u) {
        int[] targets = new int[m];
        int count = 0;
        clock.tick(m);
        for (int j = m - 1; j >= 0; j--) {
            if (targetToQuery[targetOrder[j]] < 0 && has(allowed, u, targetOrder[j]))
                targets[count++] = targetOrder[j];
        }
        return Arrays.copyOf(targets, count);
    }

    /** The compatible bonds from {@code u} to mapped atoms that the target has from {@code v} to their images. */
    private int common(int u, int v) {
        int count = 0;
        for (int i = query.start[u]; i < query.start[u + 1]; i++) {
            int x = queryToTarget[query.neighbour[i]];
            if (x < 0)
                continue;
            clock.tick(1 + target.degree(v));
            int f = target.bondBetween(v, x);
            if (f >= 0 && graph.bondsMatch(query.neighbourBond[i], f))
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
        for (int p = 0; p < side.atomCount; p++) {
            for (int i = side.start[p]; i < side.start[p + 1]; i++) {
                int a = side.neighbour[i], last = a;
                if (next[a] >= 0 || side.degree(a) != 1 || side.configured(a))
                    continue;
                for (int j = i + 1; j < side.start[p + 1]; j++) {
                    int b = side.neighbour[j];
                    if (next[b] < 0 && side.degree(b) == 1 && !side.configured(b)
                        && alike(onQuery, a, side.neighbourBond[i], b, side.neighbourBond[j])) {
                        next[last] = b;
                        last = b;
                    }
                }
                if (last != a)
                    next[last] = a;
            }
        }
        return next;
    }

    /** Whether atoms {@code a} and {@code b}, with their bonds {@code e} and {@code f}, match alike. */
    private boolean alike(boolean onQuery, int a, int e, int b, int f) {
        clock.tick(words + n + query.bondCount + target.bondCount);
        if (onQuery) {
            for (int x = 0; x < words; x++) {
                if (allowed[a * words + x] != allowed[b * words + x])
                    return false;
            }
            for (int x = 0; x < target.bondCount; x++) {
                if (graph.bondsMatch(e, x) != graph.bondsMatch(f, x))
                    return false;
            }
        } else {
            for (int x = 0; x < n; x++) {
                if (has(allowed, x, a) != has(allowed, x, b))
                    return false;
            }
            for (int x = 0; x < query.bondCount; x++) {
                if (graph.bondsMatch(x, e) != graph.bondsMatch(x, f))
                    return false;
            }
        }
        return true;
    }

    /**
     * Whether the pair of {@code u} and {@code v} can be skipped, as a free
     * twin of one has its pair with the other excluded. Swapping the twins
     * turns every mapping below this pair into one of the same score below the
     * excluded pair, which was tried before. So this holds only while no
     * mapping of that score could be recorded. That is when as many are held
     * as wanted, and in MCES also when nothing but the seed is held, see
     * {@link #run}.
     */
    private boolean twinExcluded(int u, int v) {
        boolean full = mappings.size() == maxMappings;
        boolean onlySeed = minBonds > 0 && mappings.size() == (heldSeed != null ? 1 : 0);
        if (!full && !onlySeed)
            return false;
        for (int w = queryTwin[u]; w >= 0 && w != u; w = queryTwin[w]) {
            if (queryToTarget[w] < 0 && !has(allowed, w, v))
                return true;
        }
        for (int y = targetTwin[v]; y >= 0 && y != v; y = targetTwin[y]) {
            if (targetToQuery[y] < 0 && !has(allowed, u, y))
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
        for (int k = fragmentStart; k < depth; k++) {
            int p = levels[k].queryAtom, x = levels[k].targetAtom;
            clock.tick(query.degree(p) * target.degree(x));
            for (int i = query.start[p]; i < query.start[p + 1]; i++) {
                int h = query.neighbour[i];
                if (queryToTarget[h] >= 0 || query.degree(h) != 1 || query.configured(h))
                    continue;
                for (int j = target.start[x]; j < target.start[x + 1]; j++) {
                    int y = target.neighbour[j];
                    if (targetToQuery[y] < 0 && target.degree(y) == 1 && !target.configured(y)
                        && has(edgeBound.compatible, h, y)
                        && graph.bondsMatch(query.neighbourBond[i], target.neighbourBond[j]))
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
        boolean[] queryMapped = new boolean[n], targetMapped = new boolean[m];
        boolean[] queryOpen = new boolean[n], targetOpen = new boolean[m];
        boolean[] queryCommon = new boolean[query.bondCount], targetCommon = new boolean[target.bondCount];
        for (int i = 0; open && i < reach.count; i++) {
            queryOpen[queryAtom(reach.pairs[i])] = true;
            targetOpen[targetAtom(reach.pairs[i])] = true;
        }
        for (int a = 0; a < n; a++) {
            int x = mapping[a];
            if (x < 0)
                continue;
            queryMapped[a] = targetMapped[x] = true;
            clock.tick(1 + query.degree(a) + target.degree(x));
            for (int i = query.start[a]; i < query.start[a + 1]; i++) {
                int b = query.neighbour[i], f = b < a && mapping[b] >= 0 ? target.bondBetween(x, mapping[b]) : -1;
                if (f >= 0 && graph.bondsMatch(query.neighbourBond[i], f))
                    queryCommon[query.neighbourBond[i]] = targetCommon[f] = true;
            }
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
        if (n > m || !queryConnected())
            return null;
        // Row k holds the target atoms with at least k bonds, up to the most a query atom has. The last row is empty.
        int rows = Math.min(query.maxDegree(), target.maxDegree()) + 1;
        long[] atLeast = new long[(rows + 1) * words];
        for (int t = 0; t < m; t++)
            for (int k = Math.min(target.degree(t), rows); k >= 0; k--)
                set(atLeast, k, t);
        long[] candidateRows = new long[n * words];
        for (int q = 0; q < n; q++) {
            clock.tick(words);
            int row = Math.min(query.degree(q), rows) * words;
            for (int i = 0; i < words; i++)
                candidateRows[q * words + i] = allowed[q * words + i] & atLeast[row + i];
        }
        return hasDistinctTargets(candidateRows, n, words, clock) ? new Embedding(candidateRows) : null;
    }

    private boolean queryConnected() {
        int[] queue = new int[n];
        boolean[] in = new boolean[n];
        int count = 1;
        in[0] = true;
        for (int i = 0; i < count; i++) {
            for (int j = query.start[queue[i]]; j < query.start[queue[i] + 1]; j++) {
                if (!in[query.neighbour[j]]) {
                    in[query.neighbour[j]] = true;
                    queue[count++] = query.neighbour[j];
                }
            }
        }
        return count == n;
    }

    /** A pair of query atom {@code q} and target atom {@code t} as one int, which fits as the pairs are limited. */
    private int pair(int q, int t) {
        return q << targetBits | t;
    }

    private int queryAtom(int pair) {
        return pair >>> targetBits;
    }

    private int targetAtom(int pair) {
        return pair & (1 << targetBits) - 1;
    }

    /** A mapped pair of the branch and bound, with what it restores when taken off and what it has left to try. */
    private static final class Level {

        /** The pair, and the common bonds it adds. */
        int queryAtom, targetAtom, addedBonds;
        /** The query and target bonds with no end mapped, once the pair is mapped. */
        int freeQueryBonds, freeTargetBonds;
        /** The number of excluded pairs before the pair was mapped. */
        int excludedBefore;
        /** The level that starts the open fragment, and the common bonds before it. */
        int fragmentStart, bondsBefore;
        long bound;
        /**
         * The candidates whose targets the level tries, the candidate being
         * tried, and its targets. The next candidate and the next target to
         * try are kept as places in these lists.
         */
        int[] candidates;
        int nextCandidate;
        int triedAtom;
        int[] targets;
        int nextTarget;
        /** In MCES, once the fragment is closed, the roots of new fragments offered are ranked below this. */
        int rootRank;
        boolean closed;
    }

    /**
     * The open pairs reached from the open fragment of a state through
     * compatible bonds, see {@link #bound}. The bonds that link them are
     * counted once on each side. A union-find over their atoms, with target
     * atom t as n + t, sums the fewer atoms of either side over the groups.
     */
    private final class Reach {

        /** The reached pairs as bits, rows by query atom. */
        private final long[] seen = new long[n * words];
        /** The reached pairs in the order they were reached, the first {@link #count} of the array. */
        private int[] pairs = new int[INITIAL_PAIRS];
        private int count;
        private int firstLayer;
        /** The stamp of each query and target bond counted as a link, and the numbers of these links. */
        private final int[] queryBondStamp = new int[query.bondCount];
        private final int[] targetBondStamp = new int[target.bondCount];
        private int queryLinks, targetLinks;
        /** The union-find: the stamp and parent of each atom, and the atoms of each side in each group. */
        private final int[] atomStamp = new int[n + m];
        private final int[] parent = new int[n + m];
        private final int[] queryAtoms = new int[n + m];
        private final int[] targetAtoms = new int[n + m];
        /** The sum, over the groups, of the fewer atoms of either side. */
        private int groupSum;
        /** The stamp of this use, so that no marks have to be cleared. */
        private int stamp;

        void start() {
            if (++stamp == Integer.MAX_VALUE) {
                Arrays.fill(queryBondStamp, 0);
                Arrays.fill(targetBondStamp, 0);
                Arrays.fill(atomStamp, 0);
                stamp = 1;
            }
            count = queryLinks = targetLinks = groupSum = 0;
        }

        /** Reaches the open pairs next to the pair of {@code a} and {@code x}. */
        void link(int a, int x) {
            int from = target.start[x], to = target.start[x + 1], rootRank = queryRank[levels[0].queryAtom];
            for (int i = query.start[a]; i < query.start[a + 1]; i++) {
                int u = query.neighbour[i], e = query.neighbourBond[i];
                if (queryToTarget[u] >= 0 || queryRank[u] > rootRank)
                    continue;
                clock.tick(1 + to - from);
                for (int j = from; j < to; j++) {
                    int v = target.neighbour[j], f = target.neighbourBond[j];
                    if (targetToQuery[v] >= 0 || !has(allowed, u, v) || !graph.bondsMatch(e, f))
                        continue;
                    if (queryBondStamp[e] != stamp) {
                        queryBondStamp[e] = stamp;
                        queryLinks++;
                    }
                    if (targetBondStamp[f] != stamp) {
                        targetBondStamp[f] = stamp;
                        targetLinks++;
                    }
                    if (!has(seen, u, v)) {
                        set(seen, u, v);
                        if (count == pairs.length)
                            pairs = Arrays.copyOf(pairs, 2 * pairs.length);
                        pairs[count++] = pair(u, v);
                    }
                }
            }
        }

        /** Whether the bond of the query, or else of the target, links the reached pairs. */
        boolean isLink(boolean onQuery, int bond) {
            return (onQuery ? queryBondStamp : targetBondStamp)[bond] == stamp;
        }

        void join(int pair) {
            int a = root(queryAtom(pair)), b = root(n + targetAtom(pair));
            if (a != b) {
                groupSum -= Math.min(queryAtoms[a], targetAtoms[a]) + Math.min(queryAtoms[b], targetAtoms[b]);
                parent[b] = a;
                queryAtoms[a] += queryAtoms[b];
                targetAtoms[a] += targetAtoms[b];
                groupSum += Math.min(queryAtoms[a], targetAtoms[a]);
            }
        }

        /** The root of the group of atom {@code x}, which starts as a group of its own in each use. */
        private int root(int x) {
            if (atomStamp[x] != stamp) {
                atomStamp[x] = stamp;
                parent[x] = x;
                queryAtoms[x] = x < n ? 1 : 0;
                targetAtoms[x] = 1 - queryAtoms[x];
            }
            while (parent[x] != x)
                x = parent[x] = parent[parent[x]];
            return x;
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
            for (int x : symbol)
                symbols = Math.max(symbols, x + 1);
            int[] querySymbol = Arrays.copyOfRange(symbol, 0, n), targetSymbol = Arrays.copyOfRange(symbol, n, n + m);
            // The symbols at the ends of each bond, the lower first, or -1 if an end has none.
            int[] low = new int[bondTotal], high = new int[bondTotal];
            Arrays.fill(low, -1);
            endSymbols(query, querySymbol, 0, low, high);
            endSymbols(target, targetSymbol, queryBonds, low, high);
            // The keys sort the bonds by the symbols at their ends, then by their bond set.
            long[] keys = new long[bondTotal];
            for (int e = 0; e < bondTotal; e++)
                keys[e] = low[e] < 0 ? -1 : ((long) low[e] * symbols + high[e]) * bondTotal + find(bondSets, e);
            int[] labelOf = labels(keys, queryBonds);
            int labels = 0;
            for (int l : labelOf)
                labels = Math.max(labels, l + 1);
            slotSymbol = new int[2 * labels];
            for (int e = 0; e < bondTotal; e++) {
                if (labelOf[e] >= 0) {
                    slotSymbol[2 * labelOf[e]] = low[e];
                    slotSymbol[2 * labelOf[e] + 1] = low[e] == high[e] ? -1 : high[e];
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
         * The symbol of each atom, with target atom b at n + b. Atoms joined by
         * compatible pairs share a symbol. An atom without a compatible pair is
         * never mapped and has none, -1.
         */
        private int[] atomSymbols() {
            int[] atomSets = singletons(n + m);
            for (int a = 0; a < n; a++) {
                clock.tick(words);
                int row = a * words;
                for (int b = nextBit(allowed, row, words, 0); b >= 0; b = nextBit(allowed, row, words, b + 1))
                    unite(atomSets, a, n + b);
            }
            int[] members = new int[n + m], setSymbol = new int[n + m], symbol = new int[n + m];
            int count = 0;
            for (int a = 0; a < n + m; a++)
                members[find(atomSets, a)]++;
            for (int a = 0; a < n + m; a++)
                setSymbol[a] = members[a] > 1 ? count++ : -1;
            for (int a = 0; a < n + m; a++)
                symbol[a] = setSymbol[find(atomSets, a)];
            return symbol;
        }

        /** The sets of bonds joined by compatible bond pairs, with target bond f after the query bonds. */
        private int[] bondSets() {
            int queryBonds = query.bondCount, targetBonds = target.bondCount;
            int[] bondSets = singletons(queryBonds + targetBonds);
            for (int e = 0; e < queryBonds; e++) {
                clock.tick(targetBonds);
                for (int f = 0; f < targetBonds; f++) {
                    if (graph.bondsMatch(e, f))
                        unite(bondSets, e, queryBonds + f);
                }
            }
            return bondSets;
        }

        /** Sets the symbols at the ends of each bond of {@code side} whose ends have symbols, the lower first. */
        private void endSymbols(MCSGraph.Side side, int[] symbol, int offset, int[] low, int[] high) {
            for (int a = 0; a < side.atomCount; a++) {
                for (int i = side.start[a]; i < side.start[a + 1]; i++) {
                    int b = side.neighbour[i], bond = offset + side.neighbourBond[i];
                    if (b > a && symbol[a] >= 0 && symbol[b] >= 0) {
                        low[bond] = Math.min(symbol[a], symbol[b]);
                        high[bond] = Math.max(symbol[a], symbol[b]);
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
            for (int e = 0; e < keys.length; e++) {
                if (keys[e] >= 0 && e < queryBonds)
                    inQuery[rank[e]] = true;
                else if (keys[e] >= 0)
                    inTarget[rank[e]] = true;
            }
            int labels = 0;
            for (int r = 0; r < keys.length; r++)
                label[r] = inQuery[r] && inTarget[r] ? labels++ : -1;
            for (int e = 0; e < keys.length; e++)
                labelOf[e] = keys[e] < 0 ? -1 : label[rank[e]];
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
            for (int r = 0; r <= rankLimit; r++) {
                int u = queryOrder[r];
                long any = 0;
                clock.tick(words);
                for (int i = 0; queryToTarget[u] < 0 && i < words; i++) {
                    long x = allowed[u * words + i] & unusedTargets[i];
                    reachableTargets[i] |= x;
                    any |= x;
                }
                if (any != 0)
                    queryLabels.markOpen(u);
            }
            for (int t = nextBit(reachableTargets, 0, words, 0); t >= 0; t = nextBit(reachableTargets, 0, words, t + 1))
                targetLabels.markOpen(t);
            countOpen(queryLabels, query, true);
            countOpen(targetLabels, target, false);
            for (int e = 0; e < slotSymbol.length; e += 2)
                added += slotSymbol[e + 1] < 0 ? slotBonds(e) / 2 : Math.min(slotBonds(e), slotBonds(e + 1));
            for (int c = 0; c < queryLabels.bySymbol.length; c++) {
                newAtoms += Math.min(queryLabels.bySymbol[c], targetLabels.bySymbol[c]);
                queryLabels.bySymbol[c] = targetLabels.bySymbol[c] = 0;
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
            for (int k = 0; k < labels.openCount; k++) {
                int a = labels.openAtoms[k], count = 0;
                boolean link = false;
                work += side.degree(a);
                for (int i = side.start[a]; i < side.start[a + 1]; i++) {
                    int slot = labels.slot[i];
                    if (slot < 0 || !labels.isOpen[side.neighbour[i]])
                        link |= reach.isLink(onQuery, side.neighbourBond[i]);
                    else if (slotCount[slot]++ == 0)
                        slotsAt[count++] = slot;
                }
                for (int j = 0; j < count; j++) {
                    labels.histogram[slotsAt[j] * histogramWidth + slotCount[slotsAt[j]]]++;
                    slotCount[slotsAt[j]] = 0;
                }
                if (count > 0 || link)
                    labels.bySymbol[labels.symbol[a]]++;
            }
            clock.tick(work);
        }

        /**
         * The most new bonds of a slot, pairing the open atoms with the most
         * bonds of it on either side. This clears the counts of the slot.
         */
        private int slotBonds(int slot) {
            int sum = 0;
            for (int c = histogramWidth - 1, queryAtoms = 0, targetAtoms = 0; c > 0; c--) {
                queryAtoms += queryLabels.histogram[slot * histogramWidth + c];
                targetAtoms += targetLabels.histogram[slot * histogramWidth + c];
                queryLabels.histogram[slot * histogramWidth + c] = 0;
                targetLabels.histogram[slot * histogramWidth + c] = 0;
                sum += Math.min(queryAtoms, targetAtoms);
            }
            return sum;
        }

        /** The slot at each place, or -1 for none: twice the label of its bond, plus one at the second symbol. */
        private int[] slots(MCSGraph.Side side, int[] symbol, int[] labelOf, int offset) {
            int[] slot = new int[side.neighbour.length];
            for (int a = 0; a < side.atomCount; a++) {
                for (int i = side.start[a]; i < side.start[a + 1]; i++) {
                    int l = labelOf[offset + side.neighbourBond[i]];
                    slot[i] = l < 0 ? -1 : 2 * l + (symbol[a] == slotSymbol[2 * l] ? 0 : 1);
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
        final boolean[] isOpen;
        int openCount;

        Labels(int[] symbol, int[] slot, int histogramSize, int symbols) {
            this.symbol = symbol;
            this.slot = slot;
            histogram = new int[histogramSize];
            bySymbol = new int[symbols];
            openAtoms = new int[symbol.length];
            isOpen = new boolean[symbol.length];
        }

        void markOpen(int a) {
            isOpen[a] = true;
            openAtoms[openCount++] = a;
        }

        void clearOpen() {
            for (int k = 0; k < openCount; k++)
                isOpen[openAtoms[k]] = false;
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
        /** The most pairs of query and target states, 8 MB of bounds. */
        private static final long MAX_PAIRS = 1_000_000;
        /** The most counts of symbols and bond labels for the states of both molecules, 8 MB. */
        private static final long MAX_COUNTS = 2_000_000;
        private static final long ONE_ATOM = MCSGraph.score(1, 0, false);
        private static final long ONE_BOND = MCSGraph.score(0, 1, false);

        private final MCSGraph graph;
        private final Branches query, target;
        /**
         * The bound of each pair of states, in a row of target states for each
         * query state, or 0 if their first atoms do not match. The first rows
         * and columns are the atoms, so a pair of atoms is read directly.
         */
        private final long[] stateBound;
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
            boolean[] queryRingBonds = MCSGraph.Rings.ringBonds(query.start, query.neighbour, query.neighbourBond,
                                                                query.bondCount);
            boolean[] targetRingBonds = MCSGraph.Rings.ringBonds(target.start, target.neighbour, target.neighbourBond,
                                                                 target.bondCount);
            long queryStates = Branches.count(query, queryRingBonds);
            long targetStates = Branches.count(target, targetRingBonds);
            long labels = graph.atomClasses + query.bondCount + target.bondCount;
            if (queryStates == query.atomCount || targetStates == target.atomCount
                || queryStates * targetStates > MAX_PAIRS || (queryStates + targetStates) * labels > MAX_COUNTS)
                return null;
            return new BridgeBound(graph, queryRingBonds, targetRingBonds, clock);
        }

        private BridgeBound(MCSGraph graph, boolean[] queryRingBonds, boolean[] targetRingBonds,
                            MCSGraph.Clock clock) {
            this.graph = graph;
            // Only the bond labels of the target can be common.
            long[] labels = LongStream.of(graph.target.bondLabels(graph.atomClasses)).distinct().toArray();
            query = new Branches(graph.query, queryRingBonds, graph.atomClasses, labels, clock);
            target = new Branches(graph.target, targetRingBonds, graph.atomClasses, labels, clock);
            stateBound = new long[query.count * target.count];
            for (int queryState : query.order) {
                for (int targetState : target.order) {
                    clock.tick();
                    stateBound[queryState * target.count + targetState] = pairBound(queryState, targetState, clock);
                }
            }
            // Without a pair of anchors, each mapped pair has an atom that is no anchor on one side or the other.
            long best = MCSGraph.score(query.nonAnchors + target.nonAnchors,
                                       Math.min(graph.query.bondCount, graph.target.bondCount), false);
            for (int q = 0; q < graph.query.atomCount; q++) {
                if (!query.anchor[q])
                    continue;
                for (int t = 0; t < graph.target.atomCount; t++) {
                    clock.tick();
                    if (target.anchor[t])
                        best = Math.max(best, bound(q, t));
                }
            }
            upperBound = best;
        }

        /** A score that no connected mapping that maps query atom {@code q} onto target atom {@code t} beats. */
        long bound(int q, int t) {
            return stateBound[q * target.count + t];
        }

        /**
         * The bound of two states: the symbols and bond labels they have in
         * common and, for two chain atoms, the best pairing of their branches.
         */
        private long pairBound(int queryState, int targetState, MCSGraph.Clock clock) {
            int q = query.root[queryState], t = target.root[targetState];
            int atomClass = graph.query.atomClass[q];
            if (atomClass < 0 || atomClass != graph.target.atomClass[t])
                return 0;
            long bound = labelBound(queryState, targetState, clock);
            if (query.chainAtom[q] && target.chainAtom[t]
                && query.branches[queryState].length <= MAX_BRANCHES
                && target.branches[targetState].length <= MAX_BRANCHES)
                bound = Math.min(bound, ONE_ATOM + pairing(queryState, targetState, clock));
            return bound;
        }

        /**
         * The atoms and bonds two states have in common by symbol and by bond
         * label, with no more bonds than the atoms can hold.
         */
        private long labelBound(int queryState, int targetState, MCSGraph.Clock clock) {
            int[] queryAtoms = query.atomCounts[queryState], targetAtoms = target.atomCounts[targetState];
            int[] queryBonds = query.bondCounts[queryState], targetBonds = target.bondCounts[targetState];
            clock.tick(queryAtoms.length + queryBonds.length);
            int atoms = 0;
            for (int c = 0; c < queryAtoms.length; c++)
                atoms += Math.min(queryAtoms[c], targetAtoms[c]);
            int bonds = 0;
            for (int l = 0; l < queryBonds.length; l++)
                bonds += Math.min(queryBonds[l], targetBonds[l]);
            return MCSGraph.score(atoms, Math.min(bonds, atoms * (atoms - 1) / 2), false);
        }

        /**
         * The most that the branches of two chain atoms add to a mapping of
         * the two. Each query branch is paired with one target branch at most,
         * through matching bonds, and adds the bound of the pair and the bond
         * to it. A branch may also be left out.
         */
        private long pairing(int queryState, int targetState, MCSGraph.Clock clock) {
            int[] queryBranches = query.branches[queryState], targetBranches = target.branches[targetState];
            int sets = 1 << targetBranches.length;
            long[] best = bestByUsed;
            // -1 where no pairing uses the set
            Arrays.fill(best, 0, sets, -1);
            best[0] = 0;
            for (int queryBranch : queryBranches) {
                clock.tick(sets * (1 + targetBranches.length));
                int queryBond = query.parentBond[queryBranch];
                // The larger sets first, so that the query branch is paired once.
                for (int used = sets - 1; used >= 0; used--) {
                    if (best[used] < 0)
                        continue;
                    for (int j = 0; j < targetBranches.length; j++) {
                        int targetBranch = targetBranches[j];
                        long pair = stateBound[queryBranch * target.count + targetBranch];
                        if ((used & 1 << j) != 0 || pair == 0
                            || !graph.bondsMatch(queryBond, target.parentBond[targetBranch]))
                            continue;
                        int with = used | 1 << j;
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
         * The states of one molecule, for {@link BridgeBound}. A state is a
         * first atom and the atoms reached from it. The first states are the
         * atoms, each with all the atoms joined to it. Then each bond on no
         * ring gives two states, the atoms on either side of it.
         */
        private static final class Branches {

            final int count;
            /** The first atom of each state, and the bond into it, or -1 for an atom with all its branches. */
            final int[] root, parentBond;
            /** The states of the branches at the first atom of each state, other than back through its bond. */
            final int[][] branches;
            /** The atoms of each state by symbol class, and its bonds by label. */
            final int[][] atomCounts, bondCounts;
            /** The states, the smallest first, so that a branch comes before the states that hold it. */
            final int[] order;
            /** The atoms with no ring bonds. */
            final boolean[] chainAtom;
            /** The chain atoms whose pairs bound the mappings: all but a leaf on a ring atom. */
            final boolean[] anchor;
            /** The atoms that are no anchor. */
            final int nonAnchors;

            /** The states of {@code side}, its bond labels numbered by their place in {@code labels}. */
            Branches(MCSGraph.Side side, boolean[] ringBond, int atomClasses, long[] labels, MCSGraph.Clock clock) {
                count = count(side, ringBond);
                root = new int[count];
                parentBond = new int[count];
                chainAtom = new boolean[side.atomCount];
                int[] beyond = numberStates(side, ringBond);
                anchor = new boolean[side.atomCount];
                int others = 0;
                for (int a = 0; a < side.atomCount; a++) {
                    // A leaf on a ring atom is no anchor, as the branch from it holds the whole ring system.
                    anchor[a] = chainAtom[a] && !(side.degree(a) == 1 && !chainAtom[side.neighbour[side.start[a]]]);
                    if (!anchor[a])
                        others++;
                }
                nonAnchors = others;
                branches = new int[count][];
                atomCounts = new int[count][atomClasses];
                bondCounts = new int[count][labels.length];
                int[] bondLabel = bondLabels(side, atomClasses, labels);
                int[] size = new int[count], queue = new int[side.atomCount], seen = new int[side.atomCount];
                for (int s = 0; s < count; s++) {
                    branches[s] = branchesAt(side, s, beyond);
                    size[s] = countLabels(side, s, bondLabel, queue, seen, clock);
                }
                order = bySize(size);
            }

            /** The number of states: one for each atom, and two for each bond on no ring. */
            static int count(MCSGraph.Side side, boolean[] ringBond) {
                int count = side.atomCount;
                for (boolean ring : ringBond) {
                    if (!ring)
                        count += 2;
                }
                return count;
            }

            /**
             * Sets the first atom and bond of each state and marks the chain
             * atoms. Returns the state beyond each place of the neighbour
             * lists, or -1 for a ring bond.
             */
            private int[] numberStates(MCSGraph.Side side, boolean[] ringBond) {
                int[] beyond = new int[side.neighbour.length];
                Arrays.fill(beyond, -1);
                Arrays.fill(parentBond, -1);
                int next = side.atomCount;
                for (int a = 0; a < side.atomCount; a++) {
                    root[a] = a;
                    chainAtom[a] = true;
                    for (int i = side.start[a]; i < side.start[a + 1]; i++) {
                        if (ringBond[side.neighbourBond[i]]) {
                            chainAtom[a] = false;
                        } else {
                            beyond[i] = next;
                            root[next] = side.neighbour[i];
                            parentBond[next] = side.neighbourBond[i];
                            next++;
                        }
                    }
                }
                return beyond;
            }

            /** The states of the branches at the first atom of state {@code s}, other than back through its bond. */
            private int[] branchesAt(MCSGraph.Side side, int s, int[] beyond) {
                int a = root[s];
                int[] found = new int[side.degree(a)];
                int size = 0;
                for (int i = side.start[a]; i < side.start[a + 1]; i++) {
                    if (beyond[i] >= 0 && side.neighbourBond[i] != parentBond[s])
                        found[size++] = beyond[i];
                }
                return Arrays.copyOf(found, size);
            }

            /**
             * Counts the atoms of state {@code s} by symbol class and its bonds
             * by label, and returns its number of atoms. The atoms reached are
             * marked with s + 1 in {@code seen}.
             */
            private int countLabels(MCSGraph.Side side, int s, int[] bondLabel, int[] queue, int[] seen,
                                    MCSGraph.Clock clock) {
                int size = 1;
                queue[0] = root[s];
                seen[root[s]] = s + 1;
                for (int k = 0; k < size; k++) {
                    int a = queue[k];
                    clock.tick(1 + side.degree(a));
                    if (side.atomClass[a] >= 0)
                        atomCounts[s][side.atomClass[a]]++;
                    for (int i = side.start[a]; i < side.start[a + 1]; i++) {
                        int b = side.neighbour[i], bond = side.neighbourBond[i];
                        if (bond == parentBond[s])
                            continue;
                        // Each bond is counted at its higher end.
                        if (b < a && bondLabel[bond] >= 0)
                            bondCounts[s][bondLabel[bond]]++;
                        if (seen[b] != s + 1) {
                            seen[b] = s + 1;
                            queue[size++] = b;
                        }
                    }
                }
                return size;
            }

            /** The place of the label of each bond in {@code labels}, or -1 if it is not there. */
            private static int[] bondLabels(MCSGraph.Side side, int atomClasses, long[] labels) {
                int[] place = new int[side.bondCount];
                Arrays.fill(place, -1);
                for (int a = 0; a < side.atomCount; a++) {
                    for (int i = side.start[a]; i < side.start[a + 1]; i++) {
                        int b = side.neighbour[i], bond = side.neighbourBond[i];
                        if (side.atomClass[a] < 0 || side.atomClass[b] < 0)
                            continue;
                        int found = Arrays.binarySearch(labels, side.bondLabel(a, b, bond, atomClasses));
                        if (found >= 0)
                            place[bond] = found;
                    }
                }
                return place;
            }

            /** The states by size, the smallest first. */
            private static int[] bySize(int[] size) {
                Integer[] states = new Integer[size.length];
                for (int s = 0; s < size.length; s++)
                    states[s] = s;
                Arrays.sort(states, Comparator.comparingInt(s -> size[s]));
                int[] order = new int[size.length];
                for (int k = 0; k < size.length; k++)
                    order[k] = states[k];
                return order;
            }
        }
    }

    private static int[] singletons(int size) {
        int[] parent = new int[size];
        for (int i = 0; i < size; i++)
            parent[i] = i;
        return parent;
    }

    private static int find(int[] parent, int a) {
        while (parent[a] != a)
            a = parent[a] = parent[parent[a]];
        return a;
    }

    private static void unite(int[] parent, int a, int b) {
        parent[find(parent, a)] = find(parent, b);
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
        private int frontierSize;
        /** Room for the candidates of a neighbour while they are found, before they are copied. */
        private final int[] scratch;

        /** Starts from the query atom with the fewest targets it may have, then most bonds, then lowest index. */
        private Embedding(long[] candidateRows) {
            placedOn = new int[n];
            placedAtom = new int[m];
            Arrays.fill(placedOn, -1);
            Arrays.fill(placedAtom, -1);
            candidates = new int[n][];
            freeCount = new int[n];
            frontier = new int[n];
            scratch = new int[target.maxDegree()];
            int root = 0, fewest = Integer.MAX_VALUE;
            for (int q = 0; q < n; q++) {
                int count = 0;
                for (int i = 0; i < words; i++)
                    count += Long.bitCount(candidateRows[q * words + i]);
                if (count < fewest || count == fewest && query.degree(q) > query.degree(root)) {
                    root = q;
                    fewest = count;
                }
            }
            int[] targets = new int[fewest];
            int row = root * words;
            int k = 0;
            for (int t = nextBit(candidateRows, row, words, 0); t >= 0; t = nextBit(candidateRows, row, words, t + 1))
                targets[k++] = t;
            candidates[root] = targets;
            freeCount[root] = fewest;
            frontier[frontierSize++] = root;
        }

        /** The embeddings, at most {@code maxMappings}. Called once only, from the state the constructor sets up. */
        List<int[]> all(int maxMappings) {
            List<int[]> embeddings = new ArrayList<>();
            Placement[] placements = new Placement[n];
            placements[0] = new Placement().enter(frontier[0], 0);
            for (int placed = 1; placed > 0; ) {
                clock.tick();
                Placement top = placements[placed - 1];
                if (placedOn[top.atom] >= 0)
                    unplace(top);
                int t = nextTarget(top);
                if (t < 0) {
                    placed--;
                } else if (place(top, t) && graph.stereoConsistent(placedOn, clock)) {
                    int next = choose();
                    if (next >= 0) {
                        if (placements[placed] == null)
                            placements[placed] = new Placement();
                        placements[placed++].enter(frontier[next], next);
                    } else if (placed == n && (query.rings == null || complete(placedOn, false))) {
                        embeddings.add(placedOn.clone());
                        if (embeddings.size() == maxMappings)
                            return embeddings;
                    }
                }
            }
            return embeddings;
        }

        /** The next free candidate of the atom of {@code p}, or -1 once it has tried them all. */
        private int nextTarget(Placement p) {
            int[] targets = candidates[p.atom];
            while (p.nextCandidate < targets.length) {
                int t = targets[p.nextCandidate++];
                if (placedAtom[t] < 0)
                    return t;
            }
            return -1;
        }

        /** Places the atom of {@code p} on {@code t} and records the changes in {@code p}, to be undone on failure. */
        private boolean place(Placement p, int t) {
            int q = p.atom;
            frontier[p.frontierPlace] = frontier[--frontierSize];
            placedOn[q] = t;
            placedAtom[t] = q;
            take(p, t);
            int from = target.start[t], degree = target.degree(t), open = 0, narrowed = 0;
            clock.tick(1 + query.degree(q));
            for (int i = query.start[q]; i < query.start[q + 1]; i++)
                if (placedOn[query.neighbour[i]] < 0)
                    open++;
            // The candidates of each neighbour, as places among the neighbours of t, for the check at the end.
            long[] rows = open > 1 && degree <= Long.SIZE ? new long[open] : null;
            for (int i = query.start[q]; i < query.start[q + 1]; i++) {
                int u = query.neighbour[i], count = 0;
                if (placedOn[u] >= 0)
                    continue;
                // A neighbour with candidates has another placed neighbour, so it closes a ring.
                boolean closes = candidates[u] != null;
                clock.tick(1 + (closes ? degree * (1 + query.degree(u)) : degree));
                long bits = 0;
                for (int j = 0; j < degree; j++) {
                    int v = target.neighbour[from + j];
                    if (placedAtom[v] < 0
                        && graph.bondsMatch(query.neighbourBond[i], target.neighbourBond[from + j]) && mayMap(u, v)
                        && (!closes || bondsFit(u, v, q))) {
                        scratch[count++] = v;
                        bits |= 1L << j;
                    }
                }
                if (count == 0)
                    return false;
                if (!closes)
                    frontier[frontierSize++] = u;
                p.remember(u, candidates[u], freeCount[u]);
                candidates[u] = Arrays.copyOf(scratch, count);
                freeCount[u] = count;
                if (rows != null)
                    rows[narrowed++] = bits;
            }
            return rows == null || hasDistinctTargets(rows, narrowed, 1, clock);
        }

        /** Takes the atom of {@code p} off its target, and puts back everything that placing it changed. */
        private void unplace(Placement p) {
            int q = p.atom;
            while (p.changedCount > 0) {
                int k = --p.changedCount, u = p.changedAtoms[k];
                // A neighbour that had no candidates came on the frontier last.
                if (p.oldCandidates[k] == null)
                    frontierSize--;
                candidates[u] = p.oldCandidates[k];
                freeCount[u] = p.oldFreeCounts[k];
            }
            placedAtom[placedOn[q]] = -1;
            placedOn[q] = -1;
            frontier[frontierSize++] = frontier[p.frontierPlace];
            frontier[p.frontierPlace] = q;
        }

        /**
         * The frontier place of the atom to place next, with the fewest free
         * candidates, then the most bonds, then the lowest index. It is -1 if
         * the frontier is empty or an atom on it has no free candidate left.
         */
        private int choose() {
            clock.tick(frontierSize);
            int pick = -1;
            for (int k = 0; k < frontierSize; k++) {
                int u = frontier[k], v = pick < 0 ? -1 : frontier[pick];
                if (freeCount[u] == 0)
                    return -1;
                if (v < 0 || freeCount[u] < freeCount[v] || freeCount[u] == freeCount[v]
                        && (query.degree(u) > query.degree(v) || query.degree(u) == query.degree(v) && u < v))
                    pick = k;
            }
            return pick;
        }

        /**
         * Takes target atom {@code t} from the free candidates of the unplaced
         * atoms that have it, remembering their counts in {@code p}. Such an
         * atom may map onto {@code t} with compatible bonds to the targets of
         * all its placed neighbours, and is counted from the first of these.
         */
        private void take(Placement p, int t) {
            int work = 1 + target.degree(t);
            for (int j = target.start[t]; j < target.start[t + 1]; j++) {
                int w = placedAtom[target.neighbour[j]];
                if (w < 0)
                    continue;
                for (int i = query.start[w]; i < query.start[w + 1]; i++) {
                    int u = query.neighbour[i];
                    work++;
                    if (placedOn[u] >= 0 || !graph.bondsMatch(query.neighbourBond[i], target.neighbourBond[j])
                        || !mayMap(u, t))
                        continue;
                    work += query.degree(u);
                    if (firstPlacedNeighbour(u) == w && bondsFit(u, t, w)) {
                        p.remember(u, candidates[u], freeCount[u]);
                        freeCount[u]--;
                    }
                }
            }
            clock.tick(work);
        }

        /** Whether query atom {@code u} may map onto target atom {@code v}: they match, and v has as many bonds. */
        private boolean mayMap(int u, int v) {
            return has(allowed, u, v) && target.degree(v) >= query.degree(u);
        }

        /** Whether {@code v} has compatible bonds to the images of the placed neighbours of {@code u} but {@code w}. */
        private boolean bondsFit(int u, int v, int w) {
            for (int i = query.start[u]; i < query.start[u + 1]; i++) {
                int x = query.neighbour[i], y = placedOn[x];
                if (y < 0 || x == w)
                    continue;
                // Search the shorter list of neighbours, as one may be a hub.
                int f = target.degree(v) <= target.degree(y) ? target.bondBetween(v, y) : target.bondBetween(y, v);
                if (f < 0 || !graph.bondsMatch(query.neighbourBond[i], f))
                    return false;
            }
            return true;
        }

        /** The first placed neighbour of query atom {@code u}, or -1 if none is placed. */
        private int firstPlacedNeighbour(int u) {
            for (int i = query.start[u]; i < query.start[u + 1]; i++) {
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

        int atom, frontierPlace, nextCandidate, changedCount;
        int[] changedAtoms = new int[4], oldFreeCounts = new int[4];
        int[][] oldCandidates = new int[4][];

        Placement enter(int atom, int frontierPlace) {
            this.atom = atom;
            this.frontierPlace = frontierPlace;
            nextCandidate = 0;
            return this;
        }

        /** Remembers that atom {@code u} had these candidates, and this count of free ones. */
        void remember(int u, int[] candidates, int freeCount) {
            if (changedCount == changedAtoms.length) {
                changedAtoms = Arrays.copyOf(changedAtoms, 2 * changedCount);
                oldFreeCounts = Arrays.copyOf(oldFreeCounts, 2 * changedCount);
                oldCandidates = Arrays.copyOf(oldCandidates, 2 * changedCount);
            }
            changedAtoms[changedCount] = u;
            oldCandidates[changedCount] = candidates;
            oldFreeCounts[changedCount++] = freeCount;
        }
    }

    /**
     * Whether each of the first {@code count} rows can have a column of its
     * own. The columns of row r are the bits of the {@code words} longs from
     * {@code rows[r * words]}. A row takes a free column, or else follows an
     * augmenting path. There is no recursion, as there may be a row for each
     * query atom.
     */
    private static boolean hasDistinctTargets(long[] rows, int count, int words, MCSGraph.Clock clock) {
        int columns = words << 6;
        int[] owner = new int[columns], visited = new int[columns], path = new int[count], resume = new int[count];
        long[] freeColumns = new long[words];
        Arrays.fill(owner, -1);
        Arrays.fill(freeColumns, -1L);
        for (int r = 0; r < count; r++) {
            clock.tick(words);
            int c = -1, step = 0;
            for (int i = 0; i < words && c < 0; i++) {
                long x = rows[r * words + i] & freeColumns[i];
                if (x != 0)
                    c = (i << 6) + Long.numberOfTrailingZeros(x);
            }
            path[0] = r;
            resume[0] = 0;
            while (c < 0) {
                clock.tick();
                int row = path[step];
                c = nextBit(rows, row * words, words, resume[step]);
                while (c >= 0 && visited[c] == r + 1) {
                    clock.tick();
                    c = nextBit(rows, row * words, words, c + 1);
                }
                if (c < 0) {
                    if (--step < 0)
                        return false;
                    continue;
                }
                resume[step] = c + 1;
                visited[c] = r + 1;
                if (owner[c] >= 0) {
                    path[++step] = owner[c];
                    resume[step] = 0;
                    c = -1;
                }
            }
            // Each row on the path takes the column it reached. The last row takes a free column.
            freeColumns[c >>> 6] &= ~(1L << c);
            owner[c] = path[step];
            for (int d = step - 1; d >= 0; d--)
                owner[resume[d] - 1] = path[d];
        }
        return true;
    }

    /** Whether bit {@code bit} of row {@code row} is set, in rows of {@link #words} longs as in {@link #allowed}. */
    private boolean has(long[] rows, int row, int bit) {
        return (rows[row * words + (bit >>> 6)] & 1L << bit) != 0;
    }

    private void set(long[] rows, int row, int bit) {
        rows[row * words + (bit >>> 6)] |= 1L << bit;
    }

    private void clear(long[] rows, int row, int bit) {
        rows[row * words + (bit >>> 6)] &= ~(1L << bit);
    }

    /** The first set bit of a row of {@code words} longs from {@code start}, at or after bit {@code from}, or -1. */
    private static int nextBit(long[] bits, int start, int words, int from) {
        int w = from >>> 6;
        if (w >= words)
            return -1;
        long word = bits[start + w] & -1L << from;
        while (word == 0) {
            if (++w == words)
                return -1;
            word = bits[start + w];
        }
        return (w << 6) + Long.numberOfTrailingZeros(word);
    }

    private static int[] inverse(int[] permutation) {
        int[] inverse = new int[permutation.length];
        for (int i = 0; i < permutation.length; i++)
            inverse[permutation[i]] = i;
        return inverse;
    }
}
