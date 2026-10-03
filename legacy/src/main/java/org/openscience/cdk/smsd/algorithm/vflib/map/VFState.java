/*
 *
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
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining atom copy
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
 */
package org.openscience.cdk.smsd.algorithm.vflib.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IState;

/**
 * Represents one depth-first state of a directional graph embedding search.
 * <p>Publicly constructed states require every query atom and bond to embed, while
 *  allowing additional target bonds and disconnected query components. Candidate
 *  matching uses the compiled query's atom and bond predicates; mapping-level
 *  stereochemistry and other Pattern filters are not applied here.
 * <p>Related states share a mutable backtracking map. Visit a child and backtrack it
 *  before exploring another child, and unwind children before parents. Do not use
 *  a state concurrently or mutate its query, target payloads or predicate behavior
 *  during traversal. This state does not enforce a timeout; mapper traversal owns
 *  the search budget. Returned mapping objects are mutable snapshots with borrowed
 *  node and atom payloads.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD is deprecated in CDK. See the separate
 *             <a href="https://github.com/asad/smsd">SMSD implementation</a>.
 */
@Deprecated
public class VFState implements IState {

    private final List<Match>       candidates;
    private final IQuery            query;
    private final TargetProperties  target;
    private final INode mappedNode;
    private final boolean allowPartial;
    private final Map<INode, IAtom> map;
    private final int commonBondCount;
    private final PairExclusions exclusions;
    private final int exclusionCheckpoint;
    private Match previousCandidate;
    private int rootQueryIndex = -1;
    private int rootTargetIndex = -1;

    /**
     * Create a root state for a whole-query embedding search.
     *
     * @param query valid compiled query graph with stable directional matchers
     * @param target prepared target graph with stable borrowed atom/bond payloads
     * @throws NullPointerException if the query or target is null
     */
    public VFState(IQuery query, TargetProperties target) {
        this(query, target, false);
    }

    VFState(IQuery query, TargetProperties target, boolean allowPartial) {
        this.allowPartial = allowPartial;
        this.map = new HashMap<>();
        this.mappedNode = null;
        this.commonBondCount = 0;
        this.exclusions = allowPartial ? new PairExclusions(query) : null;
        this.exclusionCheckpoint = 0;

        this.query = query;
        this.target = target;
        this.candidates = new ArrayList<>();
        loadRootCandidates();

    }

    private VFState(VFState state, Match match) {
        this.candidates = new ArrayList<>();
        this.mappedNode = match.getQueryNode();
        this.allowPartial = state.allowPartial;
        this.exclusions = state.exclusions;
        this.exclusionCheckpoint = allowPartial ? exclusions.checkpoint() : 0;

        this.map = state.map;
        this.query = state.query;
        this.target = state.target;

        this.commonBondCount = state.commonBondCount + (allowPartial ? countCommonBonds(match) : 0);
        map.put(match.getQueryNode(), match.getTargetAtom());
        try {
            loadCandidates(match);
        } catch (RuntimeException | Error failure) {
            // A failing constructor never reaches the mapper's explicit stack.
            map.remove(mappedNode);
            if (allowPartial) exclusions.restore(exclusionCheckpoint);
            throw failure;
        }
    }

    /**
     * Undo the pair and sibling exclusions owned by this state.
     * <p>Children must be backtracked first. The root owns no pair, so backtracking
     *  the root alone does not unwind an active child.
     */
    @Override
    public void backTrack() {
        if (allowPartial) exclusions.restore(exclusionCheckpoint);
        if (mappedNode != null) map.remove(mappedNode);
    }

    /**
     * Copy the current shared mapping.
     *
     * @return mutable query-node to target-atom snapshot with borrowed payloads
     */
    @Override
    public Map<INode, IAtom> getMap() {
        return new HashMap<>(map);
    }

    // Borrowed solely for read-only hash lookups; never retain this backtracking map.
    // Partial descendants cannot use query nodes above the canonical root.
    int maximumAtomCount() {
        return Math.min(allowPartial ? exclusions.rootQueryLimit + 1 : query.countNodes(),
                        target.getAtomCount());
    }

    Map<INode, IAtom> mappingView() {
        return map;
    }

    int size() {
        return map.size();
    }

    int countCommonBonds() {
        return commonBondCount;
    }

    /**
     * Determine whether another candidate pair remains in this state.
     *
     * @return {@code true} if {@link #nextCandidate()} can return a candidate
     */
    @Override
    public boolean hasNextCandidate() {
        return !candidates.isEmpty() || (rootQueryIndex >= 0 && rootTargetIndex >= 0);
    }

    /**
     * Determine whether the query has more atoms than the target.
     *
     * @return {@code true} if atom counts prevent a whole-query embedding
     */
    @Override
    public boolean isDead() {
        return query.countNodes() > target.getAtomCount();
    }

    /**
     * Determine whether every query node has been assigned.
     *
     * @return {@code true} if the current mapping contains every query node
     */
    @Override
    public boolean isGoal() {
        return map.size() == query.countNodes();
    }

    /**
     * Test a candidate's atom, bond and injectivity constraints.
     * <p>This check can evaluate user predicates without changing the mapping. A
     *  candidate must refer to a node in this query and an atom in this target.
     *
     * @param match candidate pair to test
     * @return {@code true} if the candidate can extend this state
     * @throws NullPointerException if {@code match} is null
     */
    @Override
    public boolean isMatchFeasible(Match match) {
        if (allowPartial && exclusions.contains(match)) return false;
        if (map.containsKey(match.getQueryNode()) || map.containsValue(match.getTargetAtom())) {
            return false;
        }
        if (!matchAtoms(match)) {
            return false;
        }
        if (!matchBonds(match)) {
            return false;
        }
        return true;
    }

    /**
     * Remove and return the next candidate pair.
     * <p>Call only when {@link #hasNextCandidate()} is true. A returned candidate still
     *  requires a feasibility check before a child state is constructed.
     *
     * @return next query-node and target-atom candidate
     * @throws IndexOutOfBoundsException if no candidate remains
     */
    @Override
    public Match nextCandidate() {
        if (allowPartial && mappedNode != null && previousCandidate != null) {
            // Its subtree has been visited; later siblings must not add this pair again.
            exclusions.exclude(previousCandidate);
            previousCandidate = null;
        }
        Match match;
        if (!candidates.isEmpty()) {
            match = candidates.remove(candidates.size() - 1);
        } else {
            // Each connected mapping has exactly one highest query node and one target for it.
            if (allowPartial) exclusions.rootQueryLimit = rootQueryIndex;
            match = new Match(query.getNode(rootQueryIndex), target.getAtom(rootTargetIndex--));
            if (rootTargetIndex < 0) {
                rootQueryIndex = allowPartial ? rootQueryIndex - 1 : -1;
                rootTargetIndex = target.getAtomCount() - 1;
            }
        }
        if (allowPartial && mappedNode != null) previousCandidate = match;
        return match;
    }

    /**
     * Construct a child state with the supplied pair inserted.
     * <p>The caller must first verify the candidate with {@link #isMatchFeasible(Match)}.
     *  The child shares its parent's map and must be backtracked before the parent is
     *  reused. Predicate exceptions during construction are propagated after removing
     *  the child's insertion.
     *
     * @param match feasible query-node and target-atom pair
     * @return child state sharing this state's backtracking map
     * @throws NullPointerException if {@code match} is null
     */
    @Override
    public IState nextState(Match match) {
        return new VFState(this, match);
    }

    private void loadRootCandidates() {
        rootQueryIndex = query.countNodes() - 1;
        rootTargetIndex = target.getAtomCount() - 1;
        if (!allowPartial && rootQueryIndex >= 0) {
            // A complete embedding contains every node: one fixed root is sufficient.
            // Prefer the highest degree to reject incompatible targets earlier.
            for (int i = 0; i < query.countNodes(); i++) {
                if (query.getNode(i).countNeighbors() > query.getNode(rootQueryIndex).countNeighbors()) {
                    rootQueryIndex = i;
                }
            }
        }
    }

    private IAtom findAnchor(INode node) {
        IAtom anchor = null;
        for (INode neighbor : node.neighbors()) {
            IAtom mapped = map.get(neighbor);
            if (mapped != null && (anchor == null
                    || target.countNeighbors(mapped) < target.countNeighbors(anchor))) {
                anchor = mapped;
            }
        }
        return anchor;
    }

    private void addCandidates(INode node, Iterable<IAtom> atoms) {
        if (map.containsKey(node)) return;
        for (IAtom atom : atoms) {
            if (map.containsValue(atom)) continue;
            Match candidate = new Match(node, atom);
            if (isMatchFeasible(candidate)) {
                candidates.add(candidate);
            }
        }
    }

    private void loadCandidates(Match lastMatch) {
        if (allowPartial) {
            for (INode node : query.nodes()) {
                if (map.containsKey(node)) continue;
                IAtom anchor = null;
                Set<IAtom> adjacent = null;
                for (INode neighbor : node.neighbors()) {
                    IAtom mapped = map.get(neighbor);
                    if (mapped == null) continue;
                    if (anchor == null) {
                        anchor = mapped;
                    } else {
                        if (adjacent == null) adjacent = new LinkedHashSet<>(target.getNeighbors(anchor));
                        adjacent.addAll(target.getNeighbors(mapped));
                    }
                }
                // Every extension must retain a common edge to the connected mapping.
                if (anchor != null) addCandidates(node, adjacent == null ? target.getNeighbors(anchor) : adjacent);
            }
            return;
        }
        for (INode node : lastMatch.getQueryNode().neighbors()) {
            if (!map.containsKey(node)) {
                addCandidates(node, target.getNeighbors(findAnchor(node)));
                return;
            }
        }
        for (INode node : query.nodes()) {
            if (map.containsKey(node)) continue;
            IAtom anchor = findAnchor(node);
            if (anchor != null) {
                addCandidates(node, target.getNeighbors(anchor));
                return;
            }
        }
        // A strict embedding must also include each disconnected query component.
        for (INode node : query.nodes()) {
            if (map.containsKey(node)) continue;
            for (int i = 0; i < target.getAtomCount(); i++) {
                Match candidate = new Match(node, target.getAtom(i));
                if (isMatchFeasible(candidate)) candidates.add(candidate);
            }
            return;
        }
    }

    private boolean matchAtoms(Match match) {
        IAtom atom = match.getTargetAtom();
        if (!allowPartial && match.getQueryNode().countNeighbors() > target.countNeighbors(atom)) {
            return false;
        }
        return match.getQueryNode().getAtomMatcher().matches(target, atom);
    }

    private int countCommonBonds(Match match) {
        int count = 0;
        for (INode neighbor : match.getQueryNode().neighbors()) {
            IAtom mapped = map.get(neighbor);
            if (mapped == null) continue;
            IBond bond = target.getBond(mapped, match.getTargetAtom());
            if (bond != null && query.getEdge(neighbor, match.getQueryNode())
                    .getBondMatcher().matches(target, bond)) count++;
        }
        return count;
    }

    private boolean matchBonds(Match match) {
        if (allowPartial) return map.isEmpty() || countCommonBonds(match) > 0;
        for (INode neighbor : match.getQueryNode().neighbors()) {
            IAtom mapped = map.get(neighbor);
            if (mapped == null) continue;
            IBond targetBond = target.getBond(mapped, match.getTargetAtom());
            if (targetBond == null || !query.getEdge(neighbor, match.getQueryNode())
                    .getBondMatcher().matches(target, targetBond)) return false;
        }
        return true;
    }

    /** Sparse sibling exclusions with ownership recorded on one shared undo stack. */
    private static final class PairExclusions {
        private final Map<INode, Integer> queryIndices = new IdentityHashMap<>();
        private final Map<INode, Set<IAtom>> forbidden = new IdentityHashMap<>();
        private final List<Match> undo = new ArrayList<>();
        private int rootQueryLimit;

        PairExclusions(IQuery query) {
            for (int i = 0; i < query.countNodes(); i++) queryIndices.put(query.getNode(i), i);
            rootQueryLimit = query.countNodes() - 1;
        }

        int checkpoint() {
            return undo.size();
        }

        boolean contains(Match match) {
            if (queryIndices.get(match.getQueryNode()) > rootQueryLimit) return true;
            Set<IAtom> atoms = forbidden.get(match.getQueryNode());
            return atoms != null && atoms.contains(match.getTargetAtom());
        }

        void exclude(Match match) {
            Set<IAtom> atoms = forbidden.get(match.getQueryNode());
            if (atoms == null) {
                atoms = new HashSet<>();
                forbidden.put(match.getQueryNode(), atoms);
            }
            // Never record an ancestor's exclusion: restoring this state must preserve it.
            if (atoms.add(match.getTargetAtom())) undo.add(match);
        }

        void restore(int checkpoint) {
            while (undo.size() > checkpoint) {
                Match match = undo.remove(undo.size() - 1);
                Set<IAtom> atoms = forbidden.get(match.getQueryNode());
                atoms.remove(match.getTargetAtom());
                if (atoms.isEmpty()) forbidden.remove(match.getQueryNode());
            }
        }
    }

}
