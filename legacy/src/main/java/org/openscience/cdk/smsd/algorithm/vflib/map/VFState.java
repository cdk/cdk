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
import java.util.List;
import java.util.Map;

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
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class VFState implements IState {

    private List<Match>       candidates;
    private IQuery            query;
    private TargetProperties  target;
    private List<INode>       queryPath;
    private List<IAtom>       targetPath;
    private Map<INode, IAtom> map;

    /**
     * Initialise the VFState with query and target
     * @param query
     * @param target
     */
    public VFState(IQuery query, TargetProperties target) {
        this.map = new HashMap<INode, IAtom>();
        this.queryPath = new ArrayList<INode>();
        this.targetPath = new ArrayList<IAtom>();

        this.query = query;
        this.target = target;
        this.candidates = new ArrayList<Match>();
        loadRootCandidates();

    }

    private VFState(VFState state, Match match) {
        this.candidates = new ArrayList<Match>();
        this.queryPath = new ArrayList<INode>(state.queryPath);
        this.targetPath = new ArrayList<IAtom>(state.targetPath);

        this.map = state.map;
        this.query = state.query;
        this.target = state.target;

        map.put(match.getQueryNode(), match.getTargetAtom());
        queryPath.add(match.getQueryNode());
        targetPath.add(match.getTargetAtom());
        loadCandidates(match);
    }

    /** {@inheritDoc} */
    @Override
    public void backTrack() {
        if (queryPath.isEmpty() || isGoal()) {
            map.clear();
            return;
        }
        if (isHeadMapped()) {
            return;
        }
        map.clear();
        for (int i = 0; i < queryPath.size() - 1; i++) {
            map.put(queryPath.get(i), targetPath.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<INode, IAtom> getMap() {
        return new HashMap<INode, IAtom>(map);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNextCandidate() {
        return !candidates.isEmpty();
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

    /** {@inheritDoc} */
    @Override
    public Match nextCandidate() {
        return candidates.remove(candidates.size() - 1);
    }

    /** {@inheritDoc} */
    @Override
    public IState nextState(Match match) {
        return new VFState(this, match);
    }

    private void loadRootCandidates() {
        for (int i = 0; i < query.countNodes(); i++) {
            for (int j = 0; j < target.getAtomCount(); j++) {
                Match match = new Match(query.getNode(i), target.getAtom(j));
                candidates.add(match);
            }
        }
    }

    //@TODO Asad Check the Neighbour count
    private void loadCandidates(Match lastMatch) {
        IAtom atom = lastMatch.getTargetAtom();
        List<IAtom> targetNeighbors = target.getNeighbors(atom);
        for (INode q : lastMatch.getQueryNode().neighbors()) {
            for (IAtom t : targetNeighbors) {
                Match match = new Match(q, t);
                if (candidateFeasible(match)) {
                    candidates.add(match);
                }
            }
        }
    }

    private boolean candidateFeasible(Match candidate) {
        for (INode queryAtom : map.keySet()) {
            if (queryAtom.equals(candidate.getQueryNode()) || map.get(queryAtom).equals(candidate.getTargetAtom())) {
                return false;
            }
        }
        return true;
    }

    //This function is updated by Asad to include more matches

    private boolean matchAtoms(Match match) {
        IAtom atom = match.getTargetAtom();
        if (match.getQueryNode().countNeighbors() > target.countNeighbors(atom)) {
            return false;
        }
        return match.getQueryNode().getAtomMatcher().matches(target, atom);
    }

    private boolean matchBonds(Match match) {
        if (queryPath.isEmpty()) {
            return true;
        }

        if (!matchBondsToHead(match)) {
            return false;
        }

        for (int i = 0; i < queryPath.size() - 1; i++) {
            IEdge queryBond = query.getEdge(queryPath.get(i), match.getQueryNode());
            IBond targetBond = target.getBond(targetPath.get(i), match.getTargetAtom());
            if (queryBond == null) {
                continue;
            }

            if (targetBond == null) {
                return false;
            }
            if (!matchBond(queryBond, targetBond)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchBond(IEdge edge, IBond targetBond) {
        return edge.getBondMatcher().matches(target, targetBond);
    }

    private boolean isHeadMapped() {
        INode head = queryPath.get(queryPath.size() - 1);
        for (INode neighbor : head.neighbors()) {
            if (!map.containsKey(neighbor)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchBondsToHead(Match match) {
        INode queryHead = getQueryPathHead();
        IAtom targetHead = getTargetPathHead();

        IEdge queryBond = query.getEdge(queryHead, match.getQueryNode());
        IBond targetBond = target.getBond(targetHead, match.getTargetAtom());

        if (queryBond == null || targetBond == null) {
            return false;
        }
        return matchBond(queryBond, targetBond);
    }

    private INode getQueryPathHead() {
        return queryPath.get(queryPath.size() - 1);
    }

    private IAtom getTargetPathHead() {
        return targetPath.get(targetPath.size() - 1);
    }
}
