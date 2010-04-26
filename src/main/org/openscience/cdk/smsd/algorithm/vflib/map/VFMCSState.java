/**
 *
 * Copyright (C) 2009-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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
 * You should have received atom copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.vflib.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IEdge;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IState;
import org.openscience.cdk.smsd.algorithm.vflib.validator.VFMatch;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * This class finds MCS mapping states between query and target
 * molecules.
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public class VFMCSState implements IState {

    private List<VFMatch> candidates;
    private IQuery query;
    private IAtomContainer target;
    private List<INode> queryPath;
    private List<IAtom> targetPath;
    private Map<INode, IAtom> map;
    private int targetAtomCount = 0;

    /**
     * Initialise the VFMCSState with query and target
     * @param query
     * @param target
     */
    public VFMCSState(IQuery query, IAtomContainer target) {
        this.map = new HashMap<INode, IAtom>();
        this.queryPath = new ArrayList<INode>();
        this.targetPath = new ArrayList<IAtom>();

        this.query = query;
        this.target = target;
        this.candidates = new ArrayList<VFMatch>();
        this.targetAtomCount = target.getAtomCount();

        loadRootCandidates();
    }

    private VFMCSState(VFMCSState state, VFMatch match) {
        this.candidates = new ArrayList<VFMatch>();
        this.queryPath = new ArrayList<INode>(state.queryPath);
        this.targetPath = new ArrayList<IAtom>(state.targetPath);

        this.targetAtomCount = state.target.getAtomCount();
        this.map = state.map;
        this.query = state.query;
        this.target = state.target;

        map.put(match.getQueryNode(), match.getTargetAtom());
        queryPath.add(match.getQueryNode());
        targetPath.add(match.getTargetAtom());

        loadCandidates(match);
    }

    /** {@inheritDoc}
     */
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

    /** {@inheritDoc}
     */
    @Override
    public Map<INode, IAtom> getMap() {
        return new HashMap<INode, IAtom>(map);
    }

    /** {@inheritDoc}
     */
    @Override
    public boolean hasNextCandidate() {
        return !candidates.isEmpty();
    }

    /** {@inheritDoc}
     */
    @Override
    public boolean isDead() {
        return query.countNodes() > this.targetAtomCount;//target.getAtomCount();
    }

    /** {@inheritDoc}
     */
    @Override
    public boolean isGoal() {
        return map.size() == query.countNodes();
    }

    /** {@inheritDoc}
     */
    @Override
    public boolean isMatchFeasible(VFMatch match) {
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

    /** {@inheritDoc}
     */
    @Override
    public VFMatch nextCandidate() {
        return candidates.remove(candidates.size() - 1);
    }

    /** {@inheritDoc}
     */
    @Override
    public IState nextState(VFMatch match) {
        return new VFMCSState(this, match);
    }

    private void loadRootCandidates() {
        for (int i = 0; i < query.countNodes(); i++) {
            for (int j = 0; j < target.getAtomCount(); j++) {
                VFMatch match = new VFMatch(query.getNode(i), target.getAtom(j));
                candidates.add(match);
            }
        }
    }

//@TODO Asad Check the Neighbour count
    private void loadCandidates(VFMatch lastMatch) {
        IAtom atom = lastMatch.getTargetAtom();
        List<IAtom> targetNeighbors = target.getConnectedAtomsList(atom);

        for (INode q : lastMatch.getQueryNode().neighbors()) {
            for (IAtom t : targetNeighbors) {
                VFMatch match = new VFMatch(q, t);

                if (candidateFeasible(match)) {
                    candidates.add(match);
                }
            }
        }


    }

    private boolean candidateFeasible(VFMatch candidate) {
        for (INode queryAtom : map.keySet()) {
            if (queryAtom.equals(candidate.getQueryNode())
                    || map.get(queryAtom).equals(candidate.getTargetAtom())) {
                return false;
            }
        }

        return true;
    }

    private boolean matchAtoms(VFMatch match) {
        IAtom atom = match.getTargetAtom();
        if (match.getQueryNode().countNeighbors() > target.getConnectedAtomsCount(atom)) {
            return false;
        }
        return match.getQueryNode().getAtomMatcher().matches(atom);
    }

    private boolean matchBonds(VFMatch match) {
        if (queryPath.isEmpty()) {
            return true;
        }

        if (!matchBondsToHead(match)) {
            return false;
        }
        for (int i = 0; i < queryPath.size() - 1; i++) {
            IEdge queryBond = query.getEdge(queryPath.get(i), match.getQueryNode());
            IBond targetBond = target.getBond(targetPath.get(i), match.getTargetAtom());
//          return false else it keeps on searching, fixed by Asad
            if (queryBond == null) {
//                continue;
                return false;
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
        return edge.getBondMatcher().matches(targetBond);
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

    private boolean matchBondsToHead(VFMatch match) {
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
