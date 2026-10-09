/*
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.smsd.algorithm.vflib.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IMapper;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.tools.TimeManager;

/**
 * This class finds the maximum common substructure (MCS) of a query and a
 * target molecule using the VF2 algorithm.
 * <p>
 * {@link #getMaps} and {@link #countMaps} find the connected common
 * substructures with the most atoms and, of those, the most common bonds;
 * bonds may be left out on either side. At most {@link #MAX_MAPPINGS}
 * mappings are returned or counted. If the search times out (see
 * {@link TimeOut}) the best mappings found so far are returned.
 * A mapper made with {@link #VFMCSMapper(IQuery, long, int)} has its own
 * time limit and maximum number of mappings instead, and neither reads nor
 * sets {@link TimeOut}.
 * {@link #hasMap} and {@link #getFirstMap} match the whole query, as
 * {@link VFMapper} does, with no time limit.
 * <p>
 * The search is exact. It can take long when many mappings are nearly as
 * good as the best one, e.g. for some large polycyclic molecules, so a time
 * limit is recommended. When the whole query does not fit, the search order
 * is taken from the structures, so it hardly depends on the order of their
 * atoms.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class VFMCSMapper implements IMapper {

    /**
     * Maximum number of equally good mappings kept; symmetric molecules
     * can have millions of them.
     */
    public static final int               MAX_MAPPINGS   = 1000;

    // maximum number of root pairs tried by the greedy mapping
    private static final int              GREEDY_ROOTS   = 64;

    private final IQuery                  query;
    private final List<Map<INode, IAtom>> maps           = new ArrayList<>();
    private int                           currentMCSSize = -1;
    private int                           currentBonds   = -1;
    // true if the global TimeOut applies, false for a mapper with its own limits
    private final boolean                 global;
    // time limit in ns, -1 for none (own limits only)
    private final long                    timeLimit;
    private final int                     maxMappings;
    private long                          start;
    private TimeManager                   clock;
    private boolean                       timedOut;
    private static TimeManager            timeManager    = null;

    /**
     * @return the timeout
     */
    protected synchronized static double getTimeout() {
        return TimeOut.getInstance().getTimeOut();
    }

    /**
     * @return the timeManager
     */
    protected synchronized static TimeManager getTimeManager() {
        return timeManager;
    }

    /**
     * @param aTimeManager the timeManager to set
     */
    protected synchronized static void setTimeManager(TimeManager aTimeManager) {
        TimeOut.getInstance().setTimeOutFlag(false);
        timeManager = aTimeManager;
    }

    /**
     * Creates a mapper that uses the global {@link TimeOut}.
     *
     * @param query the compiled query
     */
    public VFMCSMapper(IQuery query) {
        setTimeManager(new TimeManager());
        this.query = query;
        this.global = true;
        this.timeLimit = -1;
        this.maxMappings = MAX_MAPPINGS;
    }

    /**
     * Creates a mapper with its own limits. The global {@link TimeOut} is
     * neither read nor set. {@link #getMaps} and {@link #countMaps} stop when
     * the time limit is reached or the thread is interrupted, see
     * {@link #isTimedOut()}. Once {@code maxMappings} equally good mappings
     * are found, only better ones are searched for, so a limit of 1 finds one
     * maximum mapping fastest.
     *
     * @param query       the compiled query
     * @param timeLimit   time limit of each search in nanoseconds, or a
     *                    negative value for none
     * @param maxMappings the maximum number of equally good mappings to keep,
     *                    from 1 to {@link #MAX_MAPPINGS}
     * @throws IllegalArgumentException if {@code maxMappings} is not from 1
     *                                  to {@link #MAX_MAPPINGS}
     */
    public VFMCSMapper(IQuery query, long timeLimit, int maxMappings) {
        if (maxMappings < 1 || maxMappings > MAX_MAPPINGS) {
            throw new IllegalArgumentException("maxMappings must be from 1 to " + MAX_MAPPINGS + ": " + maxMappings);
        }
        this.query = query;
        this.global = false;
        this.timeLimit = timeLimit < 0 ? -1 : timeLimit;
        this.maxMappings = maxMappings;
    }

    /**
     * Creates a mapper that uses the global {@link TimeOut}.
     *
     * @param queryMolecule the query molecule
     * @param bondMatcher   true to match bonds by order and aromaticity
     */
    public VFMCSMapper(IAtomContainer queryMolecule, boolean bondMatcher) {
        setTimeManager(new TimeManager());
        this.query = new QueryCompiler(queryMolecule, bondMatcher).compile();
        this.global = true;
        this.timeLimit = -1;
        this.maxMappings = MAX_MAPPINGS;
    }

    /**
     * Returns whether the last {@link #getMaps} or {@link #countMaps} call
     * stopped before the search was complete. Unlike the static
     * {@link #isTimeOut()}, this applies to this mapper only.
     *
     * @return true if the time limit was reached or, for a mapper with its
     *         own limits, the thread was interrupted
     */
    public boolean isTimedOut() {
        return timedOut;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasMap(IAtomContainer targetMolecule) {
        return hasMap(new TargetProperties(targetMolecule));
    }

    /** {@inheritDoc} */
    @Override
    public List<Map<INode, IAtom>> getMaps(IAtomContainer target) {
        return getMaps(new TargetProperties(target));
    }

    /** {@inheritDoc} */
    @Override
    public Map<INode, IAtom> getFirstMap(IAtomContainer target) {
        return getFirstMap(new TargetProperties(target));
    }

    /** {@inheritDoc} */
    @Override
    public int countMaps(IAtomContainer target) {
        return countMaps(new TargetProperties(target));
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasMap(TargetProperties targetMolecule) {
        return new VFMapper(query, global).hasMap(targetMolecule);
    }

    /** {@inheritDoc} */
    @Override
    public List<Map<INode, IAtom>> getMaps(TargetProperties targetMolecule) {
        search(targetMolecule);
        return new ArrayList<>(maps);
    }

    /** {@inheritDoc} */
    @Override
    public Map<INode, IAtom> getFirstMap(TargetProperties targetMolecule) {
        return new VFMapper(query, global).getFirstMap(targetMolecule);
    }

    /** {@inheritDoc} */
    @Override
    public int countMaps(TargetProperties targetMolecule) {
        search(targetMolecule);
        return maps.size();
    }

    private void search(TargetProperties target) {
        maps.clear();
        timedOut = false;
        if (global) {
            clock = new TimeManager();
            setTimeManager(clock);
        } else {
            start = System.nanoTime();
        }
        try {
            // if the whole query fits, those mappings are the answer
            if (query.countNodes() <= target.getAtomCount() && isConnected() && canMapAllAtoms(target)) {
                VFMapper mapper = new VFMapper(query, global);
                List<Map<INode, IAtom>> complete = mapper.getMaps(target, this::timeOut, maxMappings);
                timedOut = mapper.isTimedOut();
                if (!complete.isEmpty()) {
                    maps.addAll(complete);
                    return;
                }
            }
            // with its own limits a stopped search has no result to keep
            if (stopEarly()) {
                return;
            }
            mapAll(new VFState(query, target, true), target);
        } finally {
            if (global) {
                TimeOut.getInstance().setTimeOutFlag(timedOut);
            }
        }
    }

    private boolean timeOut() {
        if (!timedOut) {
            if (global) {
                timedOut = getTimeout() > -1 && clock.getElapsedTimeInMinutes() > getTimeout();
            } else {
                timedOut = Thread.currentThread().isInterrupted()
                        || (timeLimit >= 0 && System.nanoTime() - start > timeLimit);
            }
        }
        return timedOut;
    }

    // stops the set-up early, for a mapper with its own limits only; with the
    // global TimeOut the set-up always completes, so a timeout can still
    // return the greedy mapping
    private boolean stopEarly() {
        return !global && timeOut();
    }

    private boolean isConnected() {
        return query.countNodes() > 0 && VFState.partSizes(query).get(query.getNode(0)) == query.countNodes();
    }

    // each query atom can be given its own target atom (a bipartite matching)
    private boolean canMapAllAtoms(TargetProperties target) {
        int n = query.countNodes();
        int m = target.getAtomCount();
        boolean[][] fits = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            if (stopEarly()) {
                return false;
            }
            INode node = query.getNode(i);
            for (int j = 0; j < m; j++) {
                IAtom atom = target.getAtom(j);
                fits[i][j] = node.countNeighbors() <= target.countNeighbors(atom)
                        && node.getAtomMatcher().matches(target, atom);
            }
        }
        int[] owner = new int[m];
        Arrays.fill(owner, -1);
        for (int i = 0; i < n; i++) {
            if (!VFState.augment(i, fits, owner, new boolean[m])) {
                return false;
            }
        }
        return true;
    }

    /**
     * A quick connected mapping, grown greedily from the root pairs whose
     * neighbourhoods look most alike, one root per pair of symmetry classes.
     * The search uses its size to prune; {@code score} receives its atom and
     * common bond counts.
     */
    private Map<INode, IAtom> greedyMapping(VFState root, TargetProperties target, int[] score) {
        int n = query.countNodes();
        int m = target.getAtomCount();
        // highest ranked query atoms first, so ties hardly depend on the input order
        INode[] ranked = root.rankedNodes();
        INode[] nodes = new INode[n];
        IAtom[] atoms = root.rankedAtoms();
        Map<INode, Integer> nodeIndex = new IdentityHashMap<>();
        for (int i = 0; i < n; i++) {
            nodes[i] = ranked[n - 1 - i];
            nodeIndex.put(nodes[i], i);
        }
        int[][] queryNbrs = new int[n][];
        int[][] targetNbrs = new int[m][];
        for (int i = 0; i < n; i++) {
            List<Integer> nbrs = new ArrayList<>();
            for (INode nbr : nodes[i].neighbors()) {
                nbrs.add(nodeIndex.get(nbr));
            }
            queryNbrs[i] = VFState.toArray(nbrs);
            Arrays.sort(queryNbrs[i]);
        }
        for (int j = 0; j < m; j++) {
            List<Integer> nbrs = new ArrayList<>();
            for (IAtom nbr : target.getNeighbors(atoms[j])) {
                nbrs.add(root.atomRank.get(AtomRef.deref(nbr)));
            }
            targetNbrs[j] = VFState.toArray(nbrs);
            Arrays.sort(targetNbrs[j]);
        }

        // atoms look alike up to radius r if their neighbourhood hashes agree
        long[] queryHash = new long[n];
        long[] targetHash = new long[m];
        for (int i = 0; i < n; i++) {
            queryHash[i] = 31L * Objects.hashCode(query.getAtom(nodes[i]).getSymbol()) + queryNbrs[i].length;
        }
        for (int j = 0; j < m; j++) {
            targetHash[j] = 31L * Objects.hashCode(atoms[j].getSymbol()) + targetNbrs[j].length;
        }
        long[] queryHashStart = queryHash;
        long[] targetHashStart = targetHash;
        int[][] alike = new int[n][m];
        for (int r = 0; r < 4; r++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (alike[i][j] == r && queryHash[i] == targetHash[j]) {
                        alike[i][j] = r + 1;
                    }
                }
            }
            queryHash = extend(queryHash, queryNbrs);
            targetHash = extend(targetHash, targetNbrs);
        }

        boolean[][] fits = new boolean[n][m];
        List<int[]> pairs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                fits[i][j] = nodes[i].getAtomMatcher().matches(target, atoms[j]);
                if (fits[i][j]) {
                    pairs.add(new int[]{i, j});
                }
            }
        }
        Collections.sort(pairs, (a, b) -> alike[b[0]][b[1]] - alike[a[0]][a[1]]);
        // symmetric roots grow the same mapping, so try a different pair of classes each time
        int[] queryClass = VFState.symmetryClasses(queryNbrs, invariants(queryHashStart));
        int[] targetClass = VFState.symmetryClasses(targetNbrs, invariants(targetHashStart));
        Set<Long> tried = new HashSet<>();
        List<int[]> roots = new ArrayList<>();
        for (int[] pair : pairs) {
            if (tried.add((long) queryClass[pair[0]] << 32 | targetClass[pair[1]])) {
                roots.add(pair);
            }
        }

        int[] best = null;
        score[0] = 0;
        score[1] = 0;
        for (int k = 0; k < Math.min(GREEDY_ROOTS, roots.size()) && !stopEarly(); k++) {
            int[] map = new int[n];
            boolean[] used = new boolean[m];
            Arrays.fill(map, -1);
            map[roots.get(k)[0]] = roots.get(k)[1];
            used[roots.get(k)[1]] = true;
            int size = 1;
            int bonds = 0;
            while (true) {
                // add the most alike pair with the most common bonds to the mapped atoms
                int bestI = -1;
                int bestJ = -1;
                int bestAlike = -1;
                int bestBonds = 0;
                for (int i = 0; i < n; i++) {
                    if (map[i] >= 0) {
                        continue;
                    }
                    for (int a : queryNbrs[i]) {
                        if (map[a] < 0) {
                            continue;
                        }
                        for (int j : targetNbrs[map[a]]) {
                            if (used[j] || !fits[i][j]) {
                                continue;
                            }
                            int common = 0;
                            for (int b : queryNbrs[i]) {
                                IBond bond = map[b] < 0 ? null : target.getBond(atoms[map[b]], atoms[j]);
                                if (bond != null
                                        && query.getEdge(nodes[b], nodes[i]).getBondMatcher().matches(target, bond)) {
                                    common++;
                                }
                            }
                            if (common > 0 && (alike[i][j] > bestAlike
                                    || (alike[i][j] == bestAlike && common > bestBonds))) {
                                bestI = i;
                                bestJ = j;
                                bestAlike = alike[i][j];
                                bestBonds = common;
                            }
                        }
                    }
                }
                if (bestI < 0) {
                    break;
                }
                map[bestI] = bestJ;
                used[bestJ] = true;
                size++;
                bonds += bestBonds;
            }
            if (size > score[0] || (size == score[0] && bonds > score[1])) {
                score[0] = size;
                score[1] = bonds;
                best = map;
            }
        }
        Map<INode, IAtom> mapping = new HashMap<>();
        for (int i = 0; best != null && i < n; i++) {
            if (best[i] >= 0) {
                mapping.put(nodes[i], atoms[best[i]]);
            }
        }
        return mapping;
    }

    // hash of an atom together with the sorted hashes of its neighbours
    private static long[] extend(long[] hash, int[][] nbrs) {
        long[] next = new long[hash.length];
        for (int i = 0; i < hash.length; i++) {
            long[] around = new long[nbrs[i].length];
            for (int k = 0; k < around.length; k++) {
                around[k] = hash[nbrs[i][k]];
            }
            Arrays.sort(around);
            next[i] = 1000003L * hash[i] + Arrays.hashCode(around);
        }
        return next;
    }

    // one invariant per atom, compared exactly
    private static int[][] invariants(long[] hash) {
        int[][] invariants = new int[hash.length][];
        for (int i = 0; i < hash.length; i++) {
            invariants[i] = new int[]{(int) (hash[i] >>> 32), (int) hash[i]};
        }
        return invariants;
    }

    private void addMapping(VFState state) {
        int size = state.size();
        int bonds = state.countCommonBonds();
        if (size > currentMCSSize || (size == currentMCSSize && bonds > currentBonds)) {
            maps.clear();
            currentMCSSize = size;
            currentBonds = bonds;
        }
        if (size == currentMCSSize && bonds == currentBonds && maps.size() < maxMappings) {
            maps.add(state.getMap());
        }
    }

    // depth first without recursion, each connected mapping is visited once
    private void mapAll(VFState root, TargetProperties target) {
        Deque<VFState> states = new ArrayDeque<>();
        // start from the greedy size, the search still finds every mapping at
        // least as good (the greedy one included)
        int[] score = new int[2];
        Map<INode, IAtom> greedy = greedyMapping(root, target, score);
        currentMCSSize = score[0];
        currentBonds = score[1];
        states.push(root);
        try {
            while (!states.isEmpty() && !timeOut()) {
                VFState state = states.peek();
                if (state.isGoal() || !state.hasNextCandidate() || state.maximumAtomCount() < currentMCSSize) {
                    states.pop().backTrack();
                    continue;
                }
                Match candidate = state.nextCandidate();
                // once enough mappings are kept only better ones are searched for
                boolean ties = maps.size() < maxMappings;
                if (state.isMatchFeasible(candidate)
                        && state.canImprove(candidate, currentMCSSize, currentBonds, ties)) {
                    VFState child = (VFState) state.nextState(candidate);
                    states.push(child);
                    addMapping(child);
                }
            }
        } finally {
            while (!states.isEmpty()) {
                states.pop().backTrack();
            }
            // timed out before reaching the greedy size, or nothing matched: return the greedy mapping
            if (maps.isEmpty()) {
                maps.add(greedy);
            }
        }
    }

    public synchronized static boolean isTimeOut() {
        if (getTimeout() > -1 && getTimeManager().getElapsedTimeInMinutes() > getTimeout()) {
            TimeOut.getInstance().setTimeOutFlag(true);
            return true;
        }
        return false;
    }
}
