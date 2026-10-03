/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMCSMapper;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.tools.TimeManager;

/**
 * MCSPlus entry point for connected common substructures. Results maximize
 * atoms, then compatible common bonds. The shared search also considers bond
 * deletion, which cannot be covered by extending only maximum induced cliques.
 * Ordinary atoms match by element and bonds by strict order/aromaticity when
 * requested; explicit query predicates are always applied directionally.
 *
 * <p>The inputs are borrowed simple two-centre graphs and must remain stable
 * during a search. Mapping-level stereochemistry and conformer geometry are not
 * checked. A captured cooperative budget returns the best mappings found so far
 * when interrupted; an incomplete result is not a certified optimum. Query
 * compilation and result conversion also contribute to this entry point's
 * legacy elapsed-clock check.</p>
 *
 * @see MCSPlusHandler
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class MCSPlus {

    private static final ThreadLocal<TimeManager> timeManager = new ThreadLocal<>();
    private static final ThreadLocal<double[]> timeout = ThreadLocal.withInitial(() -> new double[]{-1});

    /** Creates the legacy connected-overlap entry point. */
    public MCSPlus() {
    }

    /**
     * Returns the calling thread's configuration for a subsequent search.
     *
     * @return timeout in minutes; a finite negative value disables checking
     */
    protected static double getTimeout() {
        return TimeOut.getInstance().getTimeOut();
    }

    /**
     * Returns the calling thread's compatibility clock.
     *
     * @return the clock, or null if none has been assigned
     */
    protected static TimeManager getTimeManager() {
        return timeManager.get();
    }

    /**
     * Assigns a compatibility clock and captures the configured cutoff.
     * This also clears the calling thread's recorded timeout flag.
     *
     * @param aTimeManager clock to inspect, or null to remove the clock
     */
    protected static void setTimeManager(TimeManager aTimeManager) {
        TimeOut.getInstance().setTimeOutFlag(false);
        timeManager.set(aTimeManager);
        timeout.get()[0] = getTimeout();
    }

    /**
     * Return source/target index pairs for the maximum connected overlap.
     * On timeout the flag is set and the best mappings found so far are returned.
     * Every output row is a mutable flat source-index/target-index pair list;
     * indices are zero-based in the supplied containers, including reversed
     * ordinary-graph searches. An empty overlap is represented by an empty list.
     *
     * @param source source container; explicit predicates stay on this side
     * @param target target container
     * @param shouldMatchBonds whether ordinary order/aromaticity must match
     * @return all tied maximum mappings, or best-so-far mappings on timeout
     * @throws NullPointerException if either container is null
     * @throws IllegalArgumentException if an input is not a supported simple graph
     * @throws CDKException retained for legacy subclass/source compatibility
     */
    protected List<List<Integer>> getOverlaps(IAtomContainer source, IAtomContainer target,
                                             boolean shouldMatchBonds) throws CDKException {
        Objects.requireNonNull(source, "source container");
        Objects.requireNonNull(target, "target container");
        TimeManager searchClock = new TimeManager();
        double searchTimeout = getTimeout();
        setTimeManager(searchClock);
        boolean solverEntered = false;
        boolean solverCompleted = false;
        boolean solverTimedOut = false;
        try {
            // An ordinary graph may be reversed to reduce the search. Predicates
            // belong to the query and must never move to the target side.
            boolean reverse = source.getAtomCount() > target.getAtomCount()
                    && !hasPredicates(source) && !hasPredicates(target);
            IAtomContainer queryMolecule = reverse ? target : source;
            IAtomContainer targetMolecule = reverse ? source : target;
            IQuery query = new QueryCompiler(queryMolecule, shouldMatchBonds).compile();
            TargetProperties preparedTarget = new TargetProperties(targetMolecule);
            solverEntered = true;
            List<Map<INode, IAtom>> embeddings = new VFMCSMapper(query).getMaps(preparedTarget);
            solverTimedOut = TimeOut.getInstance().isTimeOutFlag();
            solverCompleted = true;
            List<List<Integer>> result = new ArrayList<>();
            for (Map<INode, IAtom> embedding : embeddings) {
                if (embedding.isEmpty()) continue;
                List<Integer> mapping = new ArrayList<>(embedding.size() * 2);
                for (Map.Entry<INode, IAtom> entry : embedding.entrySet()) {
                    int queryIndex = queryMolecule.indexOf(query.getAtom(entry.getKey()));
                    int targetIndex = targetMolecule.indexOf(entry.getValue());
                    mapping.add(reverse ? targetIndex : queryIndex);
                    mapping.add(reverse ? queryIndex : targetIndex);
                }
                result.add(mapping);
            }
            return result;
        } finally {
            // A predicate may have run another entry point or changed configuration.
            // Restore this operation's compatibility clock and captured cutoff.
            timeManager.set(searchClock);
            timeout.get()[0] = searchTimeout;
            // Preparation/conversion callbacks must not replace the solver status.
            TimeOut.getInstance().setTimeOutFlag(solverCompleted ? solverTimedOut
                    : solverEntered && TimeOut.getInstance().isTimeOutFlag());
            isTimeOut();
        }
    }

    private static boolean hasPredicates(IAtomContainer molecule) {
        for (IAtom atom : molecule.atoms()) {
            if (AtomRef.deref(atom) instanceof IQueryAtom) return true;
        }
        for (IBond bond : molecule.bonds()) {
            if (BondRef.deref(bond) instanceof IQueryBond) return true;
        }
        return false;
    }

    /**
     * Checks the calling thread's flag and captured compatibility clock budget.
     * Configuration changes do not change the cutoff paired with that clock.
     * Elapsed time is measured until this method is invoked, including idle time
     * after a result; use {@link TimeOut#isTimeOutFlag()} immediately after a
     * search for its recorded completion status.
     *
     * @return whether a timeout was recorded or the captured clock has expired
     */
    public static boolean isTimeOut() {
        if (TimeOut.getInstance().isTimeOutFlag()) return true;
        double captured = timeout.get()[0];
        if (captured >= 0 && getTimeManager() != null
                && getTimeManager().getElapsedTimeInMinutes() > captured) {
            TimeOut.getInstance().setTimeOutFlag(true);
            return true;
        }
        return false;
    }
}
