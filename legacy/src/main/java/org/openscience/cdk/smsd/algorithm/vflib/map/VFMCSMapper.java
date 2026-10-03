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
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.tools.TimeManager;

/**
 * Enumerates maximum connected common-edge mappings from query to target.
 * <p>{@code getMaps} and {@code countMaps} maximize the number of mapped atoms,
 *  then the number of compatible common bonds. Extra, missing or incompatible
 *  bonds can be deleted on either side; this is not an induced-subgraph search.
 *  Connectivity is measured using retained compatible bonds, and a single atom is
 *  connected. Distinct injective atom assignments remain distinct results.
 * <p>{@code hasMap} and {@code getFirstMap} instead require an embedding of the
 *  whole query, including every query bond, and permit disconnected queries.
 *  Their whole-query results can therefore differ from {@code getMaps} results.
 * <p>Query predicates are directional and must be deterministic during a search.
 *  Ordinary molecular queries compare elements and optionally strict bond order
 *  and aromaticity. Mapping-level stereochemistry, reaction mapping and component
 *  filters from {@link org.openscience.cdk.isomorphism.Pattern} are not applied.
 * <p>Returned maps and lists are mutable snapshots, with borrowed node/atom/bond
 *  payloads. Keep the query, target payloads and matchers stable during a search.
 *  The same mapper instance must not be searched concurrently or recursively.
 * <p>Each search resets the calling thread's {@link TimeOut} flag and captures its
 *  cutoff after target preparation. Query compilation and target preparation are
 *  outside the search budget. Deadlines are cooperative and cannot interrupt a
 *  predicate. On cancellation the maps found so far may have a suboptimal score
 *  or omit tied optima; an empty list may be returned before any state is visited.
 *  Without cancellation, absence of compatible atoms produces one empty maximum
 *  mapping, as do empty query or target graphs.
 * <p>The thread-local timeout flag records this operation when it exits. An
 * independent nested search's recorded status does not cancel its caller.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD is deprecated in CDK. See the separate
 *             <a href="https://github.com/asad/smsd">SMSD implementation</a>.
 */
@Deprecated
public class VFMCSMapper implements IMapper {

    private final IQuery query;
    private final List<Map<INode, IAtom>> maps = new ArrayList<>();
    private final Set<Map<INode, IAtom>> uniqueMaps = new HashSet<>();
    private int currentMCSSize;
    private int currentBondCount;
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
     * @param clock clock to record, or {@code null} to disable elapsed-clock checks
     */
    protected static void setTimeManager(TimeManager clock) {
        recordTimeManager(clock, getTimeout());
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
    public VFMCSMapper(IQuery query) {
        this.query = Objects.requireNonNull(query, "Query must not be null");
    }

    /**
     * Compile a simple molecular query for connected common-edge searches.
     *
     * @param queryMolecule query molecule with unique atoms and two-centre simple bonds
     * @param bondMatcher whether ordinary bond order and aromaticity must match;
     *                    explicit query predicates remain authoritative
     * @throws NullPointerException if the molecule, an atom, a bond or an endpoint is null
     * @throws IllegalArgumentException if atoms repeat or bonds form self-loops,
     *                                  parallel edges, foreign endpoints or nonbinary bonds
     */
    public VFMCSMapper(IAtomContainer queryMolecule, boolean bondMatcher) {
        this(new QueryCompiler(queryMolecule, bondMatcher).compile());
    }

    /**
     * Determine whether the whole query embeds in the target.
     * <p>Every query bond must match, and disconnected queries are allowed. This method
     *  does not ask whether a partial common subgraph exists.
     *
     * @param target target molecule, prepared before the search budget starts
     * @return {@code true} if a complete embedding is found before cancellation
     * @throws NullPointerException if the target is null
     * @throws IllegalArgumentException if target topology is not a simple two-centre graph
     */
    @Override
    public boolean hasMap(IAtomContainer target) {
        return hasMap(new TargetProperties(target));
    }

    /**
     * Return connected common-edge mappings with maximum atom and then bond counts.
     * <p>Each map assigns query nodes to target atoms. With no timeout, every tied
     *  optimum is returned; absence of compatible atoms contributes one empty map.
     *  Mapping order is unspecified.
     *
     * @param target target molecule, prepared before the search budget starts
     * @return mutable list of mutable snapshots; potentially suboptimal or incomplete on timeout
     * @throws NullPointerException if the target is null
     * @throws IllegalArgumentException if target topology is not a simple two-centre graph
     */
    @Override
    public List<Map<INode, IAtom>> getMaps(IAtomContainer target) {
        return getMaps(new TargetProperties(target));
    }

    /**
     * Return the first embedding of the whole query found before cancellation.
     * <p>Every query bond must match, and disconnected queries are allowed. This method
     *  does not return the first connected MCS mapping. The choice of embedding is
     *  unspecified; an empty map also represents the empty query.
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
     * Count the retained maximum connected common-edge mappings.
     * <p>This method materializes mapping results. Absence of compatible atoms contributes
     *  one empty maximum mapping when the search completes. On timeout, the count can
     *  describe suboptimal mappings or an incomplete set of tied maxima.
     *
     * @param target target molecule, prepared before the search budget starts
     * @return number of retained mappings, including an empty maximum when applicable
     * @throws NullPointerException if the target is null
     * @throws IllegalArgumentException if target topology is not a simple two-centre graph
     */
    @Override
    public int countMaps(IAtomContainer target) {
        return countMaps(new TargetProperties(target));
    }

    /**
     * Determine whether the whole query embeds in the target.
     * <p>Every query bond must match, and disconnected queries are allowed. This method
     *  does not ask whether a partial common subgraph exists.
     *
     * @param target prepared target graph with stable borrowed atom/bond payloads
     * @return {@code true} if a complete embedding is found before cancellation
     * @throws NullPointerException if the target is null
     */
    @Override
    public boolean hasMap(TargetProperties target) {
        Objects.requireNonNull(target, "Target graph must not be null");
        resetSearch();
        try {
            return new VFMapper(query).hasMap(target, searchClock, searchTimeout);
        } finally {
            finishSearch();
        }
    }

    /**
     * Return connected common-edge mappings with maximum atom and then bond counts.
     * <p>Each map assigns query nodes to target atoms. With no timeout, every tied
     *  optimum is returned; absence of compatible atoms contributes one empty map.
     *  Mapping order is unspecified.
     *
     * @param target prepared target graph with stable borrowed atom/bond payloads
     * @return mutable list of mutable snapshots; potentially suboptimal or incomplete on timeout
     * @throws NullPointerException if the target is null
     */
    @Override
    public List<Map<INode, IAtom>> getMaps(TargetProperties target) {
        search(target);
        return new ArrayList<>(maps);
    }

    /**
     * Return the first embedding of the whole query found before cancellation.
     * <p>Every query bond must match, and disconnected queries are allowed. This method
     *  does not return the first connected MCS mapping. The choice of embedding is
     *  unspecified; an empty map also represents the empty query.
     *
     * @param target prepared target graph with stable borrowed atom/bond payloads
     * @return mutable query-node to target-atom snapshot, or an empty map if none is found
     * @throws NullPointerException if the target is null
     */
    @Override
    public Map<INode, IAtom> getFirstMap(TargetProperties target) {
        Objects.requireNonNull(target, "Target graph must not be null");
        resetSearch();
        try {
            return new VFMapper(query).getFirstMap(target, searchClock, searchTimeout);
        } finally {
            finishSearch();
        }
    }

    /**
     * Count the retained maximum connected common-edge mappings.
     * <p>This method materializes mapping results. Absence of compatible atoms contributes
     *  one empty maximum mapping when the search completes. On timeout, the count can
     *  describe suboptimal mappings or an incomplete set of tied maxima.
     *
     * @param target prepared target graph with stable borrowed atom/bond payloads
     * @return number of retained mappings, including an empty maximum when applicable
     * @throws NullPointerException if the target is null
     */
    @Override
    public int countMaps(TargetProperties target) {
        search(target);
        return maps.size();
    }

    private void resetSearch() {
        maps.clear();
        uniqueMaps.clear();
        currentMCSSize = -1;
        currentBondCount = -1;
        TimeOut.getInstance().setTimeOutFlag(false);
        searchClock = new TimeManager();
        searchTimeout = getTimeout();
        searchTimedOut = false;
        recordTimeManager(searchClock, searchTimeout);
    }

    private void search(TargetProperties target) {
        Objects.requireNonNull(target, "Target graph must not be null");
        resetSearch();
        try {
            if (query.countNodes() <= target.getAtomCount() && isConnectedQuery()
                    && hasCompleteAtomDomains(target)) {
                List<Map<INode, IAtom>> complete = new VFMapper(query).getMaps(target, searchClock, searchTimeout);
                if (!complete.isEmpty()) {
                    maps.addAll(complete);
                    currentMCSSize = query.countNodes();
                    currentBondCount = query.countEdges();
                    return;
                }
                if (hasTimedOut()) return;
            }
            mapAll(new VFState(query, target, true));
        } finally {
            finishSearch();
        }
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

    // A full embedding requires a feasible target for every query atom.
    // Evaluate the actual matcher so explicit predicates and custom queries are preserved.
    private boolean hasCompleteAtomDomains(TargetProperties target) {
        for (INode node : query.nodes()) {
            boolean found = false;
            for (int i = 0; i < target.getAtomCount(); i++) {
                if ((i & 255) == 0 && hasTimedOut()) return false;
                IAtom atom = target.getAtom(i);
                if (node.countNeighbors() <= target.countNeighbors(atom)
                        && node.getAtomMatcher().matches(target, atom)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private boolean isConnectedQuery() {
        if (query.countNodes() == 0) return false;
        Set<INode> reached = new HashSet<>();
        List<INode> pending = new ArrayList<>();
        pending.add(query.getNode(0));
        reached.add(query.getNode(0));
        for (int i = 0; i < pending.size(); i++) {
            for (INode neighbor : pending.get(i).neighbors()) {
                if (reached.add(neighbor)) pending.add(neighbor);
            }
        }
        return reached.size() == query.countNodes();
    }

    private void addMapping(VFState state) {
        int size = state.size();
        int bonds = state.countCommonBonds();
        if (size > currentMCSSize || (size == currentMCSSize && bonds > currentBondCount)) {
            maps.clear();
            uniqueMaps.clear();
            currentMCSSize = size;
            currentBondCount = bonds;
        }
        if (size == currentMCSSize && bonds == currentBondCount
                && !uniqueMaps.contains(state.mappingView())) {
            Map<INode, IAtom> snapshot = state.getMap();
            uniqueMaps.add(snapshot);
            maps.add(snapshot);
        }
    }

    private void mapAll(VFState root) {
        Deque<VFState> states = new ArrayDeque<>();
        try {
            if (hasTimedOut() || root.maximumAtomCount() < currentMCSSize) return;
            addMapping(root);
            states.push(root);
            while (!states.isEmpty() && !hasTimedOut()) {
                VFState state = states.peek();
                if (state.isGoal() || state.maximumAtomCount() < currentMCSSize || !state.hasNextCandidate()) {
                    states.pop().backTrack();
                    continue;
                }
                Match candidate = state.nextCandidate();
                if (state.maximumAtomCount() < currentMCSSize) {
                    states.pop().backTrack();
                } else if (state.isMatchFeasible(candidate)) {
                    VFState child = (VFState) state.nextState(candidate);
                    if (hasTimedOut() || child.maximumAtomCount() < currentMCSSize) {
                        child.backTrack();
                    } else {
                        states.push(child);
                        addMapping(child);
                    }
                }
            }
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
