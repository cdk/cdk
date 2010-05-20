/* Copyright (C) 2009-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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
 */
package org.openscience.cdk.smsd.algorithm.vflib.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IMapper;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IState;
import org.openscience.cdk.smsd.algorithm.vflib.query.TemplateCompiler;
import org.openscience.cdk.smsd.algorithm.vflib.validator.VFMatch;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * This class finds MCS between query and target molecules
 * This is an extension of published VF2 algorithm for finding
 * MCS.
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public class VFMCSMapper implements IMapper {

    private IQuery query;
    private List<Map<INode, IAtom>> maps;
    private int currentMCSSize = -1;

    /**
     *
     * @param query
     */
    public VFMCSMapper(IQuery query) {
        this.query = query;
        this.maps = new ArrayList<Map<INode, IAtom>>();
    }

    /**
     *
     * @param molecule 
     */
    public VFMCSMapper(IAtomContainer molecule) {
        this.query = TemplateCompiler.compile(molecule);
        this.maps = new ArrayList<Map<INode, IAtom>>();
    }

    /** {@inheritDoc}
     * @param target target graph
     */
    @Override
    public boolean hasMap(IAtomContainer target) {
        VFMCSState state = new VFMCSState(query, target);
        maps.clear();

        return mapFirst(state);
    }

    /** {@inheritDoc}
     */
    @Override
    public List<Map<INode, IAtom>> getMaps(IAtomContainer target) {
        VFMCSState state = new VFMCSState(query, target);
        maps.clear();
        mapAll(state);
        return new ArrayList<Map<INode, IAtom>>(maps);
    }

    /** {@inheritDoc}
     *
     * @param target
     *
     */
    @Override
    public Map<INode, IAtom> getFirstMap(IAtomContainer target) {
        VFMCSState state = new VFMCSState(query, target);
        maps.clear();
        mapFirst(state);
        return maps.isEmpty() ? new HashMap<INode, IAtom>() : maps.get(0);
    }

    /** {@inheritDoc}
     */
    @Override
    public int countMaps(IAtomContainer target) {
        VFMCSState state = new VFMCSState(query, target);
        maps.clear();
        mapAll(state);
        return maps.size();
    }

    private void addMapping(IState state, boolean isGoal) {

        Map<INode, IAtom> map = state.getMap();
        if ((isGoal && isMCS(map))
                || (!isGoal && !map.isEmpty() && isMCS(map))) {
            maps.add(map);
        }
    }

    private void mapAll(IState state) {
        if (state.isDead()) {
            return;
        }

        if (state.isGoal()) {
            addMapping(state, true);
            return;
        } else {
            addMapping(state, false);
        }

        while (state.hasNextCandidate()) {
            VFMatch candidate = state.nextCandidate();

            if (state.isMatchFeasible(candidate)) {
                IState nextState = state.nextState(candidate);
                mapAll(nextState);
                nextState.backTrack();
            }
        }
    }

    private boolean mapFirst(IState state) {
        if (state.isDead()) {
            return false;
        }

        if (state.isGoal()) {
            maps.add(state.getMap());
            return true;
        } else {
            addMapping(state, false);
        }

        boolean found = false;

        while (!found && state.hasNextCandidate()) {
            VFMatch candidate = state.nextCandidate();
            if (state.isMatchFeasible(candidate)) {
                IState nextState = state.nextState(candidate);
                found = mapFirst(nextState);
                nextState.backTrack();

            }
        }
        return found;
    }

    //Method added by Asad
    private boolean isMCS(Map<INode, IAtom> map) {

        boolean flag = true;
        int mapSize = map.size();

        if (!maps.isEmpty() && currentMCSSize > mapSize) {
            flag = false;
        }
        //Comment this if to get all the subgraphs
        if (mapSize > currentMCSSize) {
            currentMCSSize = mapSize;
            maps.clear();
        }
        return flag;
    }

    private boolean hasMap(Map<INode, IAtom> map) {
        for (Map<INode, IAtom> storedMap : maps) {
            if (storedMap.equals(map)) {
                return true;
            }
        }
        return false;
    }
}
