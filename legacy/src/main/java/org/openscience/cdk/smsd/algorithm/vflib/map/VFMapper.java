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
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

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
 * This class finds mappings of the whole query (every query atom and bond)
 * onto the target molecule using the VF2 algorithm. At most
 * {@link VFMCSMapper#MAX_MAPPINGS} mappings are returned or counted. The
 * search has no time limit; the global {@link TimeOut} is not checked.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class VFMapper implements IMapper {

    private final IQuery                  query;
    private final List<Map<INode, IAtom>> maps        = new ArrayList<>();
    private BooleanSupplier               stop;
    private boolean                       timedOut;
    private static TimeManager            timeManager = null;

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
     * Creates a mapper for a compiled query.
     *
     * @param query the compiled query
     */
    public VFMapper(IQuery query) {
        setTimeManager(new TimeManager());
        this.query = query;
    }

    /**
     * Creates a mapper for a query molecule.
     *
     * @param queryMolecule the query molecule
     * @param bondMatcher   true to match bonds by order and aromaticity
     */
    public VFMapper(IAtomContainer queryMolecule, boolean bondMatcher) {
        setTimeManager(new TimeManager());
        this.query = new QueryCompiler(queryMolecule, bondMatcher).compile();
    }

    /**
     * Creates a mapper for {@link VFMCSMapper}.
     *
     * @param query  the compiled query
     * @param global true to reset the global {@link TimeOut} flag, as the
     *               public constructors do, false to leave it unchanged
     */
    VFMapper(IQuery query, boolean global) {
        if (global) {
            setTimeManager(new TimeManager());
        }
        this.query = query;
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
        search(targetMolecule, 1);
        return !maps.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public List<Map<INode, IAtom>> getMaps(TargetProperties targetMolecule) {
        search(targetMolecule, VFMCSMapper.MAX_MAPPINGS);
        return new ArrayList<>(maps);
    }

    /** {@inheritDoc} */
    @Override
    public Map<INode, IAtom> getFirstMap(TargetProperties targetMolecule) {
        search(targetMolecule, 1);
        return maps.isEmpty() ? new HashMap<>() : maps.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public int countMaps(TargetProperties targetMolecule) {
        search(targetMolecule, VFMCSMapper.MAX_MAPPINGS);
        return maps.size();
    }

    /**
     * Finds up to {@code limit} mappings for {@link VFMCSMapper}, stopping
     * early when {@code stop} returns true.
     *
     * @param target the target
     * @param stop   returns true when the search should stop
     * @param limit  the maximum number of mappings to find
     * @return the mappings found
     */
    List<Map<INode, IAtom>> getMaps(TargetProperties target, BooleanSupplier stop, int limit) {
        this.stop = stop;
        this.timedOut = false;
        maps.clear();
        mapAll(new VFState(query, target), limit);
        return new ArrayList<>(maps);
    }

    boolean isTimedOut() {
        return timedOut;
    }

    // the public searches have no time limit
    private void search(TargetProperties target, int limit) {
        this.stop = null;
        this.timedOut = false;
        maps.clear();
        mapAll(new VFState(query, target), limit);
    }

    private boolean timeOut() {
        if (!timedOut && stop != null && stop.getAsBoolean()) {
            timedOut = true;
        }
        return timedOut;
    }

    // depth first without recursion, states are backtracked when popped
    private void mapAll(IState root, int limit) {
        Deque<IState> states = new ArrayDeque<>();
        states.push(root);
        try {
            while (!states.isEmpty() && maps.size() < limit && !timeOut()) {
                IState state = states.peek();
                if (state.isDead()) {
                    states.pop().backTrack();
                } else if (state.isGoal()) {
                    maps.add(state.getMap());
                    states.pop().backTrack();
                } else if (!state.hasNextCandidate()) {
                    states.pop().backTrack();
                } else {
                    Match candidate = state.nextCandidate();
                    if (state.isMatchFeasible(candidate)) {
                        states.push(state.nextState(candidate));
                    }
                }
            }
        } finally {
            while (!states.isEmpty()) {
                states.pop().backTrack();
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
