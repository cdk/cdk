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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IEdge;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IState;

/**
 * This class finds mapping states between query and target
 * molecules.
 * <p>
 * A state shares its mapping with its child states, so a child must be
 * backtracked before the parent is used again. The public constructor
 * creates a state for matching the whole query (substructure search);
 * {@link VFMCSMapper} uses partial states for the connected MCS search.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class VFState implements IState {

    private final List<Match>         candidates;
    private final IQuery              query;
    private final TargetProperties    target;
    private final INode               mappedNode;
    private final boolean             partial;
    private final Map<INode, IAtom>   map;
    private final Set<IAtom>          used;
    private final int                 commonBonds;
    private final INode[]             order;
    private final IAtom[]             atomOrder;
    final Map<IAtom, Integer>         atomRank;
    private final Map<INode, Integer> partSize;
    private final Exclusions          exclusions;
    private final Bound               bound;
    private final int                 checkpoint;
    private Match                     previousCandidate;
    private int                       rootQueryIndex  = -1;
    private int                       rootTargetIndex = -1;

    /**
     * Creates the root state for mapping the whole query onto the target
     * (substructure search).
     *
     * @param query  the compiled query
     * @param target the target molecule
     */
    public VFState(IQuery query, TargetProperties target) {
        this(query, target, false);
    }

    /**
     * Creates a root state. A partial state maps a connected part of the
     * query: each pair after the first must share at least one common bond
     * with the pairs mapped so far. A full state maps every query atom.
     *
     * @param query   the compiled query
     * @param target  the target molecule
     * @param partial true to map a connected part of the query, false to map all of it
     */
    VFState(IQuery query, TargetProperties target, boolean partial) {
        this.query = query;
        this.target = target;
        this.partial = partial;
        this.map = new HashMap<>();
        this.used = new HashSet<>();
        this.mappedNode = null;
        this.commonBonds = 0;
        if (partial) {
            int[][] choices = countChoices(query, target);
            this.order = rankNodes(query, choices[0]);
            this.atomOrder = rankAtoms(target, choices[1]);
            this.atomRank = new IdentityHashMap<>();
            for (int j = 0; j < atomOrder.length; j++) {
                atomRank.put(AtomRef.deref(atomOrder[j]), j);
            }
        } else {
            this.order = null;
            this.atomOrder = null;
            this.atomRank = null;
        }
        this.partSize = partial ? null : partSizes(query);
        this.bound = partial ? new Bound(query, target, order) : null;
        this.exclusions = partial ? new Exclusions(order, bound) : null;
        this.checkpoint = 0;
        this.candidates = new ArrayList<>();
        loadRootCandidates();
    }

    private VFState(VFState state, Match match) {
        this.query = state.query;
        this.target = state.target;
        this.partial = state.partial;
        this.map = state.map;
        this.used = state.used;
        this.mappedNode = match.getQueryNode();
        this.commonBonds = state.commonBonds + (partial ? countCommonBonds(match) : 0);
        this.order = state.order;
        this.atomOrder = state.atomOrder;
        this.atomRank = state.atomRank;
        this.partSize = state.partSize;
        this.exclusions = state.exclusions;
        this.bound = state.bound;
        this.checkpoint = partial ? exclusions.checkpoint() : 0;
        this.candidates = new ArrayList<>();
        map.put(match.getQueryNode(), match.getTargetAtom());
        used.add(match.getTargetAtom());
        if (partial) {
            bound.push(match);
        }
        try {
            loadCandidates(match);
        } catch (RuntimeException e) {
            // the caller never gets this state, so undo the pair here
            backTrack();
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void backTrack() {
        if (partial) {
            exclusions.restore(checkpoint);
        }
        if (mappedNode != null) {
            used.remove(map.remove(mappedNode));
            if (partial) {
                bound.pop();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<INode, IAtom> getMap() {
        return new HashMap<>(map);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNextCandidate() {
        return !candidates.isEmpty() || (rootQueryIndex >= 0 && rootTargetIndex >= 0);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDead() {
        return query.countNodes() > target.getAtomCount();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isGoal() {
        return map.size() == query.countNodes();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMatchFeasible(Match match) {
        if (partial && exclusions.contains(match)) {
            return false;
        }
        if (map.containsKey(match.getQueryNode()) || used.contains(match.getTargetAtom())) {
            return false;
        }
        return matchAtoms(match) && matchBonds(match);
    }

    /** {@inheritDoc} */
    @Override
    public Match nextCandidate() {
        if (previousCandidate != null) {
            // the previous pair has been searched, later siblings skip it
            exclusions.exclude(previousCandidate);
            previousCandidate = null;
        }
        Match match;
        if (!candidates.isEmpty()) {
            match = candidates.remove(candidates.size() - 1);
        } else {
            if (partial) {
                // a connected mapping is found once, from its highest ranked query node
                exclusions.rootLimit = rootQueryIndex;
            }
            INode root = partial ? order[rootQueryIndex] : query.getNode(rootQueryIndex);
            IAtom atom = partial ? atomOrder[rootTargetIndex] : target.getAtom(rootTargetIndex);
            rootTargetIndex--;
            match = new Match(root, atom);
            if (rootTargetIndex < 0) {
                rootQueryIndex = partial ? rootQueryIndex - 1 : -1;
                rootTargetIndex = target.getAtomCount() - 1;
            }
        }
        if (partial && mappedNode != null) {
            previousCandidate = match;
        }
        return match;
    }

    /** {@inheritDoc} */
    @Override
    public IState nextState(Match match) {
        return new VFState(this, match);
    }

    int size() {
        return map.size();
    }

    // common bonds between the pairs mapped so far
    int countCommonBonds() {
        return commonBonds;
    }

    // no query node above the root can be mapped in a partial search
    int maximumAtomCount() {
        return Math.min(exclusions.rootLimit + 1, target.getAtomCount());
    }

    /**
     * Checks whether adding the feasible candidate can still lead to a mapping
     * with more atoms than {@code atoms}, or with as many atoms and more common
     * bonds than {@code bonds} (or as many, when {@code ties} is set).
     */
    boolean canImprove(Match candidate, int atoms, int bonds, boolean ties) {
        return bound.canImprove(candidate, exclusions, atoms,
                bonds - commonBonds - countCommonBonds(candidate), ties);
    }

    // query nodes from lowest to highest rank, for the partial search
    INode[] rankedNodes() {
        return order;
    }

    // target atoms in canonical order (see rankAtoms), for the partial search
    IAtom[] rankedAtoms() {
        return atomOrder;
    }

    // [0][i]: target atoms query node i matches, [1][j]: query nodes target atom j matches
    private static int[][] countChoices(IQuery query, TargetProperties target) {
        int[][] choices = new int[2][];
        choices[0] = new int[query.countNodes()];
        choices[1] = new int[target.getAtomCount()];
        for (int i = 0; i < choices[0].length; i++) {
            INode node = query.getNode(i);
            for (int j = 0; j < choices[1].length; j++) {
                if (node.getAtomMatcher().matches(target, target.getAtom(j))) {
                    choices[0][i]++;
                    choices[1][j]++;
                }
            }
        }
        return choices;
    }

    /**
     * Orders the query nodes from lowest to highest rank. Each connected
     * mapping is found once, from its highest ranked query node. Nodes with
     * the fewest matching target atoms rank highest, so the search starts
     * where there is least choice. Ties are broken by the number of
     * neighbours (more rank higher), then by {@link #canonicalRanks}, so the
     * order hardly depends on the input order.
     */
    private static INode[] rankNodes(IQuery query, final int[] choices) {
        int n = query.countNodes();
        Map<INode, Integer> index = new IdentityHashMap<>();
        for (int i = 0; i < n; i++) {
            index.put(query.getNode(i), i);
        }
        final int[][] nbrs = new int[n][];
        int[][] invariants = new int[n][];
        for (int i = 0; i < n; i++) {
            INode node = query.getNode(i);
            List<Integer> list = new ArrayList<>();
            for (INode nbr : node.neighbors()) {
                list.add(index.get(nbr));
            }
            nbrs[i] = toArray(list);
            invariants[i] = new int[]{choices[i], nbrs[i].length,
                    Objects.hashCode(query.getAtom(node).getSymbol())};
        }
        final int[] canon = canonicalRanks(nbrs, invariants);
        List<Integer> ranked = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ranked.add(i);
        }
        Collections.sort(ranked, (a, b) -> choices[a] != choices[b] ? choices[b] - choices[a]
                : nbrs[a].length != nbrs[b].length ? nbrs[a].length - nbrs[b].length : canon[a] - canon[b]);
        INode[] order = new INode[n];
        for (int r = 0; r < n; r++) {
            order[r] = query.getNode(ranked.get(r));
        }
        return order;
    }

    // target atoms in canonical order
    private static IAtom[] rankAtoms(TargetProperties target, int[] choices) {
        int m = target.getAtomCount();
        Map<IAtom, Integer> index = new IdentityHashMap<>();
        for (int j = 0; j < m; j++) {
            index.put(AtomRef.deref(target.getAtom(j)), j);
        }
        int[][] nbrs = new int[m][];
        int[][] invariants = new int[m][];
        for (int j = 0; j < m; j++) {
            IAtom atom = target.getAtom(j);
            List<Integer> list = new ArrayList<>();
            for (IAtom nbr : target.getNeighbors(atom)) {
                list.add(index.get(AtomRef.deref(nbr)));
            }
            nbrs[j] = toArray(list);
            invariants[j] = new int[]{choices[j], nbrs[j].length, Objects.hashCode(atom.getSymbol())};
        }
        int[] canon = canonicalRanks(nbrs, invariants);
        IAtom[] atoms = new IAtom[m];
        for (int j = 0; j < m; j++) {
            atoms[canon[j]] = target.getAtom(j);
        }
        return atoms;
    }

    /**
     * Computes distinct ranks 0..n-1 for the atoms of a graph from the
     * invariants, refined by the ranks of the neighbours. Atoms that are still
     * tied are split off one at a time, starting with the first atom (in input
     * order) of the lowest tied rank. Tied atoms are usually symmetric, and
     * then the ranks depend only on the structure (up to that symmetry), not
     * on the input order.
     *
     * @param nbrs       the neighbours of each atom
     * @param invariants the starting invariants of each atom
     * @return the rank of each atom
     */
    static int[] canonicalRanks(int[][] nbrs, int[][] invariants) {
        int n = nbrs.length;
        int[] rank = symmetryClasses(nbrs, invariants);
        while (countRanks(rank) < n) {
            int[] size = new int[n];
            for (int i = 0; i < n; i++) {
                size[rank[i]]++;
            }
            int tied = -1;
            int atom = -1;
            for (int i = 0; i < n; i++) {
                if (size[rank[i]] > 1 && (tied < 0 || rank[i] < tied)) {
                    tied = rank[i];
                    atom = i;
                }
            }
            // the first atom of the lowest tied rank goes before the others
            final int[] prev = rank;
            final int t = tied;
            final int v = atom;
            rank = refine(nbrs, sortRanks(n, (a, b) -> Long.compare(2L * prev[a] + (prev[a] == t && a != v ? 1 : 0),
                    2L * prev[b] + (prev[b] == t && b != v ? 1 : 0))));
        }
        return rank;
    }

    // atoms that still look alike once the invariants are refined by the neighbours share a class
    static int[] symmetryClasses(int[][] nbrs, final int[][] invariants) {
        return refine(nbrs, sortRanks(nbrs.length, (a, b) -> compare(invariants[a], invariants[b])));
    }

    // split the ranks by the sorted ranks of the neighbours until no rank splits
    private static int[] refine(int[][] nbrs, int[] rank) {
        int n = nbrs.length;
        int count = countRanks(rank);
        while (count < n) {
            final int[] prev = rank;
            final int[][] around = new int[n][];
            for (int i = 0; i < n; i++) {
                around[i] = new int[nbrs[i].length];
                for (int k = 0; k < around[i].length; k++) {
                    around[i][k] = prev[nbrs[i][k]];
                }
                Arrays.sort(around[i]);
            }
            rank = sortRanks(n, (a, b) -> prev[a] != prev[b] ? Integer.compare(prev[a], prev[b])
                    : compare(around[a], around[b]));
            int next = countRanks(rank);
            if (next == count) {
                break;
            }
            count = next;
        }
        return rank;
    }

    // dense ranks from 0 in the order given by cmp; atoms that compare equal share a rank
    private static int[] sortRanks(int n, Comparator<Integer> cmp) {
        Integer[] atoms = new Integer[n];
        for (int i = 0; i < n; i++) {
            atoms[i] = i;
        }
        Arrays.sort(atoms, cmp);
        int[] rank = new int[n];
        for (int k = 1; k < n; k++) {
            rank[atoms[k]] = rank[atoms[k - 1]] + (cmp.compare(atoms[k - 1], atoms[k]) != 0 ? 1 : 0);
        }
        return rank;
    }

    private static int countRanks(int[] rank) {
        int max = -1;
        for (int r : rank) {
            max = Math.max(max, r);
        }
        return max + 1;
    }

    private static int compare(int[] a, int[] b) {
        if (a.length != b.length) {
            return Integer.compare(a.length, b.length);
        }
        for (int k = 0; k < a.length; k++) {
            if (a[k] != b[k]) {
                return Integer.compare(a[k], b[k]);
            }
        }
        return 0;
    }

    static int[] toArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private void loadRootCandidates() {
        rootQueryIndex = query.countNodes() - 1;
        rootTargetIndex = target.getAtomCount() - 1;
        if (!partial && rootQueryIndex >= 0) {
            // a full search maps every query node, so one root is enough: the most
            // connected atom of the largest part, provided all atoms can be placed
            INode root = nextPartStart();
            rootQueryIndex = remainingFit() ? indexOf(root) : -1;
        }
    }

    private int indexOf(INode node) {
        for (int i = 0; i < query.countNodes(); i++) {
            if (query.getNode(i) == node) {
                return i;
            }
        }
        return -1;
    }

    // number of atoms in the connected part of the query each atom belongs to
    static Map<INode, Integer> partSizes(IQuery query) {
        Map<INode, Integer> sizes = new IdentityHashMap<>();
        for (INode node : query.nodes()) {
            if (sizes.containsKey(node)) {
                continue;
            }
            List<INode> part = new ArrayList<>();
            Set<INode> seen = new HashSet<>();
            part.add(node);
            seen.add(node);
            for (int i = 0; i < part.size(); i++) {
                for (INode nbr : part.get(i).neighbors()) {
                    if (seen.add(nbr)) {
                        part.add(nbr);
                    }
                }
            }
            for (INode member : part) {
                sizes.put(member, part.size());
            }
        }
        return sizes;
    }

    // where to start the next part: larger parts first, single atoms last
    private INode nextPartStart() {
        INode best = null;
        for (INode node : query.nodes()) {
            if (!map.containsKey(node) && (best == null || partSize.get(node) > partSize.get(best)
                    || (partSize.get(node).equals(partSize.get(best))
                            && node.countNeighbors() > best.countNeighbors()))) {
                best = node;
            }
        }
        return best;
    }

    // each unmapped query atom can still be given its own unused target atom
    private boolean remainingFit() {
        List<INode> open = new ArrayList<>();
        for (INode node : query.nodes()) {
            if (!map.containsKey(node)) {
                open.add(node);
            }
        }
        List<IAtom> free = new ArrayList<>();
        for (int j = 0; j < target.getAtomCount(); j++) {
            if (!used.contains(target.getAtom(j))) {
                free.add(target.getAtom(j));
            }
        }
        if (open.size() > free.size()) {
            return false;
        }
        boolean[][] fits = new boolean[open.size()][free.size()];
        for (int i = 0; i < open.size(); i++) {
            for (int j = 0; j < free.size(); j++) {
                fits[i][j] = open.get(i).countNeighbors() <= target.countNeighbors(free.get(j))
                        && open.get(i).getAtomMatcher().matches(target, free.get(j));
            }
        }
        int[] owner = new int[free.size()];
        Arrays.fill(owner, -1);
        for (int i = 0; i < open.size(); i++) {
            if (!augment(i, fits, owner, new boolean[free.size()])) {
                return false;
            }
        }
        return true;
    }

    private IAtom findAnchor(INode node) {
        IAtom anchor = null;
        for (INode neighbor : node.neighbors()) {
            IAtom mapped = map.get(neighbor);
            if (mapped != null && (anchor == null || target.countNeighbors(mapped) < target.countNeighbors(anchor))) {
                anchor = mapped;
            }
        }
        return anchor;
    }

    private void addCandidates(INode node, Iterable<IAtom> atoms) {
        for (IAtom atom : atoms) {
            Match candidate = new Match(node, atom);
            if (isMatchFeasible(candidate)) {
                candidates.add(candidate);
            }
        }
    }

    private void loadCandidates(Match lastMatch) {
        if (partial) {
            // any unmapped neighbour of the mapped atoms, next to any of their
            // images; the query atom with the fewest feasible target atoms is
            // tried first, atoms with one neighbour (e.g. hydrogens) last
            List<List<Match>> groups = new ArrayList<>();
            for (INode node : order) {
                if (map.containsKey(node)) {
                    continue;
                }
                Set<IAtom> atoms = null;
                for (INode neighbor : node.neighbors()) {
                    IAtom mapped = map.get(neighbor);
                    if (mapped != null) {
                        if (atoms == null) {
                            atoms = new LinkedHashSet<>();
                        }
                        atoms.addAll(target.getNeighbors(mapped));
                    }
                }
                if (atoms != null) {
                    List<IAtom> sorted = new ArrayList<>(atoms);
                    Collections.sort(sorted, Comparator.comparingInt(atom -> atomRank.get(AtomRef.deref(atom))));
                    List<Match> group = new ArrayList<>();
                    for (IAtom atom : sorted) {
                        Match candidate = new Match(node, atom);
                        if (isMatchFeasible(candidate)) {
                            group.add(candidate);
                        }
                    }
                    if (!group.isEmpty()) {
                        groups.add(group);
                    }
                }
            }
            // candidates are taken from the end
            Collections.sort(groups, (x, y) -> {
                boolean xTerminal = x.get(0).getQueryNode().countNeighbors() == 1;
                boolean yTerminal = y.get(0).getQueryNode().countNeighbors() == 1;
                return xTerminal != yTerminal ? (xTerminal ? -1 : 1) : y.size() - x.size();
            });
            for (List<Match> group : groups) {
                candidates.addAll(group);
            }
            return;
        }
        INode next = nextNode(lastMatch);
        if (next != null) {
            addCandidates(next, target.getNeighbors(findAnchor(next)));
            return;
        }
        // the next disconnected part of the query can start anywhere, if the
        // atoms left can still be placed
        INode start = nextPartStart();
        if (start != null && remainingFit()) {
            for (int i = 0; i < target.getAtomCount(); i++) {
                Match candidate = new Match(start, target.getAtom(i));
                if (isMatchFeasible(candidate)) {
                    candidates.add(candidate);
                }
            }
        }
    }

    // the next query atom to map, bonded to a mapped one (neighbours of the last
    // mapped atom first); atoms with one neighbour (e.g. hydrogens) come last,
    // as a mapping rarely fails on them
    private INode nextNode(Match lastMatch) {
        INode terminal = null;
        for (INode node : lastMatch.getQueryNode().neighbors()) {
            if (!map.containsKey(node)) {
                if (node.countNeighbors() > 1) {
                    return node;
                }
                if (terminal == null) {
                    terminal = node;
                }
            }
        }
        for (INode node : query.nodes()) {
            if (!map.containsKey(node) && findAnchor(node) != null) {
                if (node.countNeighbors() > 1) {
                    return node;
                }
                if (terminal == null) {
                    terminal = node;
                }
            }
        }
        return terminal;
    }

    private boolean matchAtoms(Match match) {
        IAtom atom = match.getTargetAtom();
        if (!partial && !neighborsFit(match.getQueryNode(), atom)) {
            return false;
        }
        return match.getQueryNode().getAtomMatcher().matches(target, atom);
    }

    /**
     * Checks, for a full search where every query atom must be mapped, that
     * each unmapped neighbour of the query atom can go to its own unused
     * neighbour of the target atom through a matching bond.
     */
    private boolean neighborsFit(INode node, IAtom atom) {
        if (node.countNeighbors() > target.countNeighbors(atom)) {
            return false;
        }
        List<INode> open = new ArrayList<>();
        for (INode nbr : node.neighbors()) {
            if (!map.containsKey(nbr)) {
                open.add(nbr);
            }
        }
        List<IAtom> free = new ArrayList<>();
        for (IAtom nbr : target.getNeighbors(atom)) {
            if (!used.contains(nbr)) {
                free.add(nbr);
            }
        }
        if (open.size() > free.size()) {
            return false;
        }
        boolean[][] fits = new boolean[open.size()][free.size()];
        for (int i = 0; i < open.size(); i++) {
            IEdge edge = query.getEdge(node, open.get(i));
            for (int j = 0; j < free.size(); j++) {
                fits[i][j] = open.get(i).getAtomMatcher().matches(target, free.get(j))
                        && edge.getBondMatcher().matches(target, target.getBond(atom, free.get(j)));
            }
        }
        int[] owner = new int[free.size()];
        Arrays.fill(owner, -1);
        for (int i = 0; i < open.size(); i++) {
            if (!augment(i, fits, owner, new boolean[free.size()])) {
                return false;
            }
        }
        return true;
    }

    // tries to match row i through an augmenting path; owner[j] is the row matched to column j (or -1)
    static boolean augment(int i, boolean[][] fits, int[] owner, boolean[] seen) {
        for (int j = 0; j < owner.length; j++) {
            if (fits[i][j] && !seen[j]) {
                seen[j] = true;
                if (owner[j] < 0 || augment(owner[j], fits, owner, seen)) {
                    owner[j] = i;
                    return true;
                }
            }
        }
        return false;
    }

    // common bonds the pair would add to the current mapping
    private int countCommonBonds(Match match) {
        int count = 0;
        for (INode neighbor : match.getQueryNode().neighbors()) {
            IAtom mapped = map.get(neighbor);
            if (mapped != null) {
                IBond bond = target.getBond(mapped, match.getTargetAtom());
                if (bond != null
                        && query.getEdge(neighbor, match.getQueryNode()).getBondMatcher().matches(target, bond)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean matchBonds(Match match) {
        if (partial) {
            return map.isEmpty() || countCommonBonds(match) > 0;
        }
        for (INode neighbor : match.getQueryNode().neighbors()) {
            IAtom mapped = map.get(neighbor);
            if (mapped != null) {
                IBond bond = target.getBond(mapped, match.getTargetAtom());
                if (bond == null) {
                    return false;
                }
                if (!query.getEdge(neighbor, match.getQueryNode()).getBondMatcher().matches(target, bond)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Tracks the pairs that may not be added below the current state: pairs
     * already tried as an earlier child of the current state or one of its
     * ancestors (their subtrees have been searched), and every pair whose
     * query node is ranked above the root. Exclusions are undone in reverse
     * order when states backtrack.
     */
    private static final class Exclusions {

        private final Map<INode, Integer> index;
        private final Map<IAtom, Integer> targetIndex;
        private final int                 m;
        // pair q * m + t, with the query node by rank and the target atom as in the bound
        private final boolean[]           excluded;
        private int[]                     undo = new int[16];
        private int                       size;
        // rank of the current root query node, set by nextCandidate; higher ranked nodes are excluded
        int                               rootLimit;

        Exclusions(INode[] order, Bound bound) {
            index = bound.queryIndex;
            targetIndex = bound.targetIndex;
            m = bound.atoms.length;
            excluded = new boolean[order.length * m];
            rootLimit = order.length - 1;
        }

        int checkpoint() {
            return size;
        }

        boolean contains(Match match) {
            int q = index.get(match.getQueryNode());
            return q > rootLimit || excluded[q * m + targetIndex.get(AtomRef.deref(match.getTargetAtom()))];
        }

        boolean forbids(int q, int t) {
            return excluded[q * m + t];
        }

        void exclude(Match match) {
            int pair = index.get(match.getQueryNode()) * m + targetIndex.get(AtomRef.deref(match.getTargetAtom()));
            if (!excluded[pair]) {
                excluded[pair] = true;
                if (size == undo.length) {
                    undo = Arrays.copyOf(undo, 2 * size);
                }
                undo[size++] = pair;
            }
        }

        void restore(int checkpoint) {
            while (size > checkpoint) {
                excluded[undo[--size]] = false;
            }
        }
    }

    /**
     * Computes an upper bound on the atoms and common bonds the partial search
     * can still add below a state.
     * <p>
     * Below a state only open pairs are added: both atoms unmapped and
     * matching, the query node not above the root and the pair not excluded
     * (exclusions only grow further down). Each added pair has a common bond
     * to a mapped pair, so it is reached from the mapping by a path of open
     * pairs linked by a matching query and target bond. A mapping is one to
     * one, so each connected group of reached pairs adds at most
     * min(query atoms, target atoms) of the group, and each added common bond
     * is a different query bond and target bond seen along those links.
     */
    private static final class Bound {

        private final Map<INode, Integer> queryIndex  = new IdentityHashMap<>();
        private final Map<IAtom, Integer> targetIndex = new IdentityHashMap<>();
        private final INode[]             nodes;
        private final IAtom[]             atoms;
        // neighbours and bond ids; query bonds are 0..nQueryBonds-1, target bonds start at nQueryBonds
        private final int[][]             queryNbrs;
        private final int[][]             queryBonds;
        private final int[][]             targetNbrs;
        private final int[][]             targetBonds;
        private final boolean[][]         atomMatch;
        private final boolean[][]         bondMatch;
        private final int[]               queue;
        private final int[]               parent;
        private final int[]               queryCount;
        private final int[]               targetCount;
        private final int[]               pairMark;
        private final int[]               atomMark;
        private final int[]               bondMark;
        private final int[]               groupMark;
        // the mapped pairs as q * m + t
        private int[]                     mapped = new int[16];
        private int                       mappedCount;
        private final int                 nQueryBonds;
        private int                       stamp;

        Bound(IQuery query, TargetProperties target, INode[] order) {
            int n = query.countNodes();
            int m = target.getAtomCount();
            nodes = order;
            atoms = new IAtom[m];
            for (int i = 0; i < n; i++) {
                queryIndex.put(order[i], i);
            }
            for (int j = 0; j < m; j++) {
                atoms[j] = target.getAtom(j);
                targetIndex.put(AtomRef.deref(atoms[j]), j);
            }

            Map<IEdge, Integer> edgeIds = new IdentityHashMap<>();
            List<IEdge> edges = new ArrayList<>();
            queryNbrs = new int[n][];
            queryBonds = new int[n][];
            for (int i = 0; i < n; i++) {
                List<INode> nbrs = new ArrayList<>();
                for (INode nbr : nodes[i].neighbors()) {
                    nbrs.add(nbr);
                }
                queryNbrs[i] = new int[nbrs.size()];
                queryBonds[i] = new int[nbrs.size()];
                for (int k = 0; k < nbrs.size(); k++) {
                    IEdge edge = query.getEdge(nodes[i], nbrs.get(k));
                    if (!edgeIds.containsKey(edge)) {
                        edgeIds.put(edge, edges.size());
                        edges.add(edge);
                    }
                    queryNbrs[i][k] = queryIndex.get(nbrs.get(k));
                    queryBonds[i][k] = edgeIds.get(edge);
                }
            }
            nQueryBonds = edges.size();

            Map<IBond, Integer> bondIds = new IdentityHashMap<>();
            List<IBond> bonds = new ArrayList<>();
            targetNbrs = new int[m][];
            targetBonds = new int[m][];
            for (int j = 0; j < m; j++) {
                List<IAtom> nbrs = target.getNeighbors(atoms[j]);
                targetNbrs[j] = new int[nbrs.size()];
                targetBonds[j] = new int[nbrs.size()];
                for (int k = 0; k < nbrs.size(); k++) {
                    IBond bond = target.getBond(atoms[j], nbrs.get(k));
                    if (!bondIds.containsKey(bond)) {
                        bondIds.put(bond, bonds.size());
                        bonds.add(bond);
                    }
                    targetNbrs[j][k] = targetIndex.get(AtomRef.deref(nbrs.get(k)));
                    targetBonds[j][k] = nQueryBonds + bondIds.get(bond);
                }
            }

            atomMatch = new boolean[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    atomMatch[i][j] = nodes[i].getAtomMatcher().matches(target, atoms[j]);
                }
            }
            bondMatch = new boolean[nQueryBonds][bonds.size()];
            for (int i = 0; i < nQueryBonds; i++) {
                for (int j = 0; j < bonds.size(); j++) {
                    bondMatch[i][j] = edges.get(i).getBondMatcher().matches(target, bonds.get(j));
                }
            }

            queue = new int[n * m];
            pairMark = new int[n * m];
            atomMark = new int[n + m];
            bondMark = new int[nQueryBonds + bonds.size()];
            parent = new int[n + m];
            queryCount = new int[n + m];
            targetCount = new int[n + m];
            groupMark = new int[n + m];
        }

        void push(Match match) {
            if (mappedCount == mapped.length) {
                mapped = Arrays.copyOf(mapped, 2 * mappedCount);
            }
            mapped[mappedCount++] = queryIndex.get(match.getQueryNode()) * atoms.length
                    + targetIndex.get(AtomRef.deref(match.getTargetAtom()));
        }

        void pop() {
            mappedCount--;
        }

        boolean canImprove(Match extra, Exclusions exclusions, int atoms, int bonds, boolean ties) {
            int n = nodes.length;
            int m = this.atoms.length;
            int tail = 0;
            int queryBondCount = 0;
            int targetBondCount = 0;
            if (++stamp == Integer.MAX_VALUE) {
                Arrays.fill(pairMark, 0);
                Arrays.fill(atomMark, 0);
                Arrays.fill(bondMark, 0);
                Arrays.fill(groupMark, 0);
                stamp = 1;
            }
            for (int i = 0; i < mappedCount; i++) {
                tail = seed(mapped[i] / m, mapped[i] % m, tail);
            }
            tail = seed(queryIndex.get(extra.getQueryNode()),
                    targetIndex.get(AtomRef.deref(extra.getTargetAtom())), tail);
            int seeded = tail;

            // breadth first over the open pairs
            for (int head = 0; head < tail; head++) {
                int q = queue[head] / m;
                int t = queue[head] % m;
                for (int k = 0; k < queryNbrs[q].length; k++) {
                    int qNbr = queryNbrs[q][k];
                    int qBond = queryBonds[q][k];
                    if (atomMark[qNbr] == stamp || qNbr > exclusions.rootLimit) {
                        continue;
                    }
                    for (int l = 0; l < targetNbrs[t].length; l++) {
                        int tNbr = targetNbrs[t][l];
                        int tBond = targetBonds[t][l];
                        if (atomMark[n + tNbr] == stamp || !bondMatch[qBond][tBond - nQueryBonds]) {
                            continue;
                        }
                        int pair = qNbr * m + tNbr;
                        if (pairMark[pair] != stamp) {
                            if (!atomMatch[qNbr][tNbr] || exclusions.forbids(qNbr, tNbr)) {
                                continue;
                            }
                            pairMark[pair] = stamp;
                            queue[tail++] = pair;
                        }
                        if (bondMark[qBond] != stamp) {
                            bondMark[qBond] = stamp;
                            queryBondCount++;
                        }
                        if (bondMark[tBond] != stamp) {
                            bondMark[tBond] = stamp;
                            targetBondCount++;
                        }
                    }
                }
            }

            // group the reached pairs, each group adds at most min(query atoms, target atoms)
            for (int i = seeded; i < tail; i++) {
                int x = find(group(queue[i] / m));
                int y = find(group(n + queue[i] % m));
                if (x != y) {
                    parent[x] = y;
                    queryCount[y] += queryCount[x];
                    targetCount[y] += targetCount[x];
                }
            }
            int atomBound = seeded;
            for (int i = seeded; i < tail; i++) {
                int root = find(queue[i] / m);
                atomBound += Math.min(queryCount[root], targetCount[root]);
                queryCount[root] = 0;
            }

            if (atomBound != atoms) {
                return atomBound > atoms;
            }
            int bondBound = Math.min(queryBondCount, targetBondCount);
            return ties ? bondBound >= bonds : bondBound > bonds;
        }

        private int seed(int q, int t, int tail) {
            atomMark[q] = stamp;
            atomMark[nodes.length + t] = stamp;
            queue[tail] = q * atoms.length + t;
            return tail + 1;
        }

        // the group of atom x, started the first time this call sees x
        private int group(int x) {
            if (groupMark[x] != stamp) {
                groupMark[x] = stamp;
                parent[x] = x;
                queryCount[x] = x < nodes.length ? 1 : 0;
                targetCount[x] = x < nodes.length ? 0 : 1;
            }
            return x;
        }

        private int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
        }
    }
}
