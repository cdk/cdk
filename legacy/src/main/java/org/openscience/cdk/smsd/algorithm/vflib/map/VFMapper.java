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
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IMapper;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IState;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.tools.TimeManager;

/**
 * Enumerates injective, non-induced embeddings of a whole query graph.
 * <p>Every query atom and query bond must match; additional target bonds are allowed.
 * Disconnected queries are supported. Atom and bond compatibility is supplied by
 *  the compiled query, with predicates applied from query to target. Ordinary
 *  molecular queries use element matching and, when enabled, strict bond order and
 *  aromaticity. This class does not apply mapping-level stereochemistry, reaction
 *  mapping or component-group filters from {@link org.openscience.cdk.isomorphism.Pattern}.
 * <p>Returned maps and lists are mutable snapshots of the search results. Their
 *  node, atom and bond payloads are borrowed. Keep query/target graphs and matcher
 *  behavior stable during a search; a predicate may be evaluated more than once.
 *  A mapper instance must not be searched concurrently or recursively.
 * <p>Each search resets the calling thread's {@link TimeOut} flag and captures its
 *  cutoff after target preparation. Deadlines are cooperative: callbacks cannot
 *  be interrupted, and incomplete enumeration may return only the complete
 *  embeddings found before cancellation. Query compilation and target preparation
 *  are outside the search budget. An empty query has one empty embedding when the
 *  search completes.
 * <p>The thread-local timeout flag records this operation when it exits. An
 * independent nested search's recorded status does not cancel its caller.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD is deprecated in CDK. See the separate
 *             <a href="https://github.com/asad/smsd">SMSD implementation</a>.
 */
@Deprecated
public class VFMapper implements IMapper {

    private final IQuery                  query;
    private final List<Map<INode, IAtom>> maps;
    private final Set<Map<INode, IAtom>> uniqueMaps = new HashSet<>();
    private TimeManager searchClock;
    private double searchTimeout;
    private boolean searchTimedOut;
    private static final ThreadLocal<TimeManager> timeManager = new ThreadLocal<>();
    private static final ThreadLocal<double[]> timeLimit = ThreadLocal.withInitial(() -> new double[]{-1});

    /**
     * Return the cutoff configured for the calling thread.
     *
     * @return cutoff in minutes; a finite negative value disables timing
     */
    protected static double getTimeout() {
        return TimeOut.getInstance().getTimeOut();
    }

    /**
     * Return the compatibility clock recorded for the calling thread.
     *
     * @return most recently recorded clock, or {@code null} if no clock is set
     */
    protected static TimeManager getTimeManager() {
        return timeManager.get();
    }

    /**
     * Record a compatibility clock and the calling thread's current cutoff.
     * <p>This hook does not replace a mapper's instance-owned active search budget.
     *
     * @param aTimeManager clock to record, or {@code null} to disable elapsed-clock checks
     */
    protected static void setTimeManager(TimeManager aTimeManager) {
        recordTimeManager(aTimeManager, getTimeout());
    }

    private static void recordTimeManager(TimeManager clock, double timeout) {
        timeManager.set(clock);
        timeLimit.get()[0] = timeout;
    }

    /**
     * Create a mapper for a compiled directional query.
     *
     * @param query valid compiled query, retained for subsequent searches
     * @throws NullPointerException if {@code query} is null
     */
    public VFMapper(IQuery query) {
        this.query = Objects.requireNonNull(query, "Query must not be null");
        this.maps = new ArrayList<>();
    }

    /**
     * Compile a simple molecular query for whole-query embedding searches.
     *
     * @param queryMolecule query molecule with unique atoms and two-centre simple bonds
     * @param bondMatcher whether ordinary bond order and aromaticity must match;
     *                    explicit query predicates remain authoritative
     * @throws NullPointerException if the molecule, an atom, a bond or an endpoint is null
     * @throws IllegalArgumentException if atoms repeat or bonds form self-loops,
     *                                  parallel edges, foreign endpoints or nonbinary bonds
     */
    public VFMapper(IAtomContainer queryMolecule, boolean bondMatcher) {
        this.query = new QueryCompiler(queryMolecule, bondMatcher).compile();
        this.maps = new ArrayList<>();
    }

    /**
     * Determine whether the entire query embeds in the target.
     *
     * @param targetMolecule target molecule, prepared before the search budget starts
     * @return {@code true} if a complete embedding is found before cancellation
     * @throws NullPointerException if the target is null
     * @throws IllegalArgumentException if target topology is not a simple two-centre graph
     */
    @Override
    public boolean hasMap(IAtomContainer targetMolecule) {
        return hasMap(new TargetProperties(targetMolecule));
    }

    /**
     * Return all whole-query embeddings found before cancellation.
     * <p>Each entry maps query nodes to target atoms. An empty query contributes one
     *  empty map; absence of an embedding produces an empty list. Results are snapshots,
     *  and their iteration order is unspecified.
     *
     * @param target target molecule, prepared before the search budget starts
     * @return mutable list of mutable mapping snapshots, possibly incomplete on timeout
     * @throws NullPointerException if the target is null
     * @throws IllegalArgumentException if target topology is not a simple two-centre graph
     */
    @Override
    public List<Map<INode, IAtom>> getMaps(IAtomContainer target) {
        return getMaps(new TargetProperties(target));
    }

    /**
     * Return the first complete whole-query embedding found before cancellation.
     * <p>The choice among embeddings is unspecified. An empty map also represents an
     *  empty query, so use {@link #hasMap(IAtomContainer)} to distinguish that case from failure.
     *
     * @param target target molecule, prepared before the search budget starts
     * @return mutable query-node to target-atom snapshot, or an empty map if none is found
     * @throws NullPointerException if the target is null
     * @throws IllegalArgumentException if target topology is not a simple two-centre graph
     */
    @Override
    public Map<INode, IAtom> getFirstMap(IAtomContainer target) {
        return getFirstMap(new TargetProperties(target));
    }

    /**
     * Count whole-query embeddings found before cancellation.
     * <p>This method materializes mappings; it is not a constant-memory counting API.
     *  An empty query contributes one empty embedding.
     *
     * @param target target molecule, prepared before the search budget starts
     * @return number of complete embeddings found, possibly an undercount on timeout
     * @throws NullPointerException if the target is null
     * @throws IllegalArgumentException if target topology is not a simple two-centre graph
     */
    @Override
    public int countMaps(IAtomContainer target) {
        return countMaps(new TargetProperties(target));
    }

    /**
     * Determine whether the entire query embeds in the target.
     *
     * @param targetMolecule prepared target graph with stable borrowed atom/bond payloads
     * @return {@code true} if a complete embedding is found before cancellation
     * @throws NullPointerException if the target is null
     */
    @Override
    public boolean hasMap(TargetProperties targetMolecule) {
        Objects.requireNonNull(targetMolecule, "Target graph must not be null");
        resetSearch();
        return findFirst(targetMolecule);
    }

    /**
     * Return all whole-query embeddings found before cancellation.
     * <p>Each entry maps query nodes to target atoms. An empty query contributes one
     *  empty map; absence of an embedding produces an empty list. Results are snapshots,
     *  and their iteration order is unspecified.
     *
     * @param targetMolecule prepared target graph with stable borrowed atom/bond payloads
     * @return mutable list of mutable mapping snapshots, possibly incomplete on timeout
     * @throws NullPointerException if the target is null
     */
    @Override
    public List<Map<INode, IAtom>> getMaps(TargetProperties targetMolecule) {
        Objects.requireNonNull(targetMolecule, "Target graph must not be null");
        resetSearch();
        findAll(targetMolecule);
        return new ArrayList<>(maps);
    }

    /**
     * Return the first complete whole-query embedding found before cancellation.
     * <p>The choice among embeddings is unspecified. An empty map also represents an
     *  empty query, so use {@link #hasMap(TargetProperties)} to distinguish that case from failure.
     *
     * @param targetMolecule prepared target graph with stable borrowed atom/bond payloads
     * @return mutable query-node to target-atom snapshot, or an empty map if none is found
     * @throws NullPointerException if the target is null
     */
    @Override
    public Map<INode, IAtom> getFirstMap(TargetProperties targetMolecule) {
        Objects.requireNonNull(targetMolecule, "Target graph must not be null");
        resetSearch();
        findFirst(targetMolecule);
        return maps.isEmpty() ? new HashMap<>() : maps.get(0);
    }

    /**
     * Count whole-query embeddings found before cancellation.
     * <p>This method materializes mappings; it is not a constant-memory counting API.
     *  An empty query contributes one empty embedding.
     *
     * @param targetMolecule prepared target graph with stable borrowed atom/bond payloads
     * @return number of complete embeddings found, possibly an undercount on timeout
     * @throws NullPointerException if the target is null
     */
    @Override
    public int countMaps(TargetProperties targetMolecule) {
        Objects.requireNonNull(targetMolecule, "Target graph must not be null");
        resetSearch();
        findAll(targetMolecule);
        return maps.size();
    }

    private void resetSearch() {
        TimeOut.getInstance().setTimeOutFlag(false);
        resetSearch(new TimeManager(), getTimeout());
    }

    private void resetSearch(TimeManager clock, double timeout) {
        maps.clear();
        uniqueMaps.clear();
        searchClock = Objects.requireNonNull(clock, "Search clock must not be null");
        searchTimeout = timeout;
        searchTimedOut = false;
        recordTimeManager(clock, timeout);
    }

    // Nested probes share the caller's elapsed budget and preserve its timeout flag.
    List<Map<INode, IAtom>> getMaps(TargetProperties target, TimeManager clock) {
        return getMaps(target, clock, getTimeout());
    }

    List<Map<INode, IAtom>> getMaps(TargetProperties target, TimeManager clock, double timeout) {
        Objects.requireNonNull(target, "Target graph must not be null");
        resetSearch(clock, timeout);
        findAll(target);
        return new ArrayList<>(maps);
    }

    boolean hasMap(TargetProperties target, TimeManager clock) {
        return hasMap(target, clock, getTimeout());
    }

    boolean hasMap(TargetProperties target, TimeManager clock, double timeout) {
        Objects.requireNonNull(target, "Target graph must not be null");
        resetSearch(clock, timeout);
        return findFirst(target);
    }

    Map<INode, IAtom> getFirstMap(TargetProperties target, TimeManager clock) {
        return getFirstMap(target, clock, getTimeout());
    }

    Map<INode, IAtom> getFirstMap(TargetProperties target, TimeManager clock, double timeout) {
        Objects.requireNonNull(target, "Target graph must not be null");
        resetSearch(clock, timeout);
        findFirst(target);
        return maps.isEmpty() ? new HashMap<>() : maps.get(0);
    }

    private boolean hasTimedOut() {
        if (!searchTimedOut && searchTimeout >= 0
                && searchClock.getElapsedTimeInMinutes() > searchTimeout) {
            searchTimedOut = true;
        }
        return searchTimedOut;
    }

    private void finishSearch() {
        recordTimeManager(searchClock, searchTimeout);
        TimeOut.getInstance().setTimeOutFlag(hasTimedOut());
    }

    private boolean findFirst(TargetProperties target) {
        try {
            return mapFirst(new VFState(query, target));
        } finally {
            finishSearch();
        }
    }

    private void findAll(TargetProperties target) {
        try {
            mapAll(new VFState(query, target));
        } finally {
            finishSearch();
        }
    }

    private void mapAll(IState root) {
        Deque<IState> states = new ArrayDeque<>();
        states.push(root);
        try {
            while (!states.isEmpty() && !hasTimedOut()) {
                IState state = states.peek();
                if (state.isDead()) {
                    states.pop().backTrack();
                } else if (state.isGoal()) {
                    Map<INode, IAtom> mapping = state.getMap();
                    if (uniqueMaps.add(mapping)) maps.add(mapping);
                    states.pop().backTrack();
                } else if (!state.hasNextCandidate()) {
                    states.pop().backTrack();
                } else {
                    Match candidate = state.nextCandidate();
                    if (state.isMatchFeasible(candidate)) states.push(state.nextState(candidate));
                }
            }
        } finally {
            while (!states.isEmpty()) states.pop().backTrack();
        }
    }

    private boolean mapFirst(IState root) {
        Deque<IState> states = new ArrayDeque<>();
        states.push(root);
        try {
            while (!states.isEmpty() && !hasTimedOut()) {
                IState state = states.peek();
                if (state.isDead()) {
                    states.pop().backTrack();
                } else if (state.isGoal()) {
                    maps.add(state.getMap());
                    return true;
                } else if (!state.hasNextCandidate()) {
                    states.pop().backTrack();
                } else {
                    Match candidate = state.nextCandidate();
                    if (state.isMatchFeasible(candidate)) states.push(state.nextState(candidate));
                }
            }
            return false;
        } finally {
            while (!states.isEmpty()) states.pop().backTrack();
        }
    }

    /**
     * Check the calling thread's timeout flag and compatibility clock.
     * <p>The clock uses the cutoff captured when it was recorded. It continues running
     *  after a search returns, so a later call can set the flag because of caller idle
     *  time. Inspect {@link TimeOut#isTimeOutFlag()} immediately after a search for its
     *  recorded cancellation status. This helper sets that flag when its captured
     *  nonnegative cutoff is exceeded.
     *
     * @return whether the flag is set or the recorded clock has exceeded its cutoff
     */
    public static boolean isTimeOut() {
        if (TimeOut.getInstance().isTimeOutFlag()) return true;
        TimeManager clock = getTimeManager();
        if (clock == null) return false;
        double timeout = timeLimit.get()[0];
        if (timeout >= 0 && clock.getElapsedTimeInMinutes() > timeout) {
            TimeOut.getInstance().setTimeOutFlag(true);
            return true;
        }
        return false;
    }
}
