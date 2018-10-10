/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;

/**
 * Computes and stores atom invariants in a single object. The atom invariants
 * are utilised as additional information for the {@link SMARTSAtom}s to match.
 * The values provide additional invariants which are not defined in the {@link
 * IAtom} API and avoids storing multiple properties in a type unsafe map
 * ({@link IAtom#setProperty(Object, Object)}).  Depending on the SMARTS
 * implementation different values for the ring information may be set. The
 * choice of ring set affects {@link #ringNumber()} and {@link #ringSize()}.
 * Some implementations store all ring sizes whilst others (Daylight) store only
 * the smallest. The {@link #degree()} also depends on whether hydrogens are
 * suppressed or represented as explicit atoms.  The {@link
 * #configureDaylightWithRingInfo(IAtomContainer)} and {@link
 * #configureDaylightWithoutRingInfo(IAtomContainer)} static utilities create
 * and set the invariants following the Daylight implementation. The invariants
 * are set on the {@link #KEY} property of each atom.
 *
 * @author John May
 * @cdk.module smarts
 */
final class SMARTSAtomInvariants {

    /** Property key to index the class by. */
    static String                KEY = "SMARTS.INVARIANTS";

    /** the molecule which this atom belongs. */
    private final IAtomContainer target;

    /** Total number of bonds formed - also refereed to as bond order sum. */
    private final int            valence;

    /** The number of rings this atom can be found in. */
    private final int            ringNumber;

    /** The size of rings an atom is found in. */
    private final Set<Integer>   ringSize;

    /** Total number of connected atoms including implicit hydrogens. */
    private final int            connectivity;

    /** Total number of connected ring bonds. */
    private final int            ringConnectivity;

    /** Total number of explicitly connected atoms. */
    private final int            degree;

    /** The total number of hydrogens on an atom. */
    private final int            totalHydrogenCount;

    /**
     * Internal constructor - simple takes all the values.
     *
     * @param valence            the valence value
     * @param ringNumber         number of rings an atom belongs to (variable)
     * @param ringSize           the size of the rings (variable)
     * @param ringConnectivity   the number of connected ring bonds (or atoms)
     * @param degree             the degree of an atom
     * @param connectivity       the number of connections (degree + implicit H
     *                           count)
     * @param totalHydrogenCount the total number of hydrogens
     */
    SMARTSAtomInvariants(IAtomContainer target, int valence, int ringNumber, Set<Integer> ringSize,
            int ringConnectivity, int degree, int connectivity, int totalHydrogenCount) {
        this.target = target;
        this.valence = valence;
        this.ringNumber = ringNumber;
        this.ringSize = ringSize;
        this.connectivity = connectivity;
        this.totalHydrogenCount = totalHydrogenCount;
        this.ringConnectivity = ringConnectivity;
        this.degree = degree;
    }

    IAtomContainer target() {
        return target;
    }

    /**
     * Access the valence of this atom. The valence is matched by the {@code
     * v<NUMBER>} SMARTS token. The valence is the total number of bonds formed
     * by this atom and <b>NOT</b> the number of valence electrons. As such
     * {@code [v3]} will match a 3 valent nitrogen and {@code [v5]} will match a
     * 5 valent nitrogen. The value is separate from {@link IAtom#getValency()}
     * so it can be cleaned up after matching and avoid confusion with what the
     * value should be.
     *
     * @return the valence of the atom.
     */
    int valence() {
        return valence;
    }

    /**
     * The number of rings this atom belong to. The value is matched by the
     * {@code R<NUMBER>} token and depends on the ring set used. The Daylight
     * implementation uses the non-unique Smallest Set of Smallest Rings (SSSR)
     * which can lead to inconsistent matches.
     *
     * @return number or rings
     */
    int ringNumber() {
        return ringNumber;
    }

    /**
     * The sizes of rings this atoms belongs to. The value is matched by the
     * {@code r<NUMBER>} token and depends on the ring set used. The Daylight
     * implementation uses this value to match the smallest ring to which this
     * atom is a member. It may be beneficial to match multiple ring sizes (not
     * yet defined by OpenSMARTS).
     *
     * @return ring sizes
     */
    Set<Integer> ringSize() {
        return ringSize;
    }

    /**
     * The number of connected ring bonds (or atoms). This value is matched by
     * the {@code x<NUMBER>} token. The Daylight implementation counts the
     * number of connected ring bonds but it may be beneficial to match the atom
     * ring connectivity (not yet defined by OpenSMARTS).
     *
     * @return ring connectivity
     */
    int ringConnectivity() {
        return ringConnectivity;
    }

    /**
     * The number of connected bonds including those to hydrogens. This value is
     * matched by the {@code X<NUMBER>} token. This value depends on whether the
     * hydrogens have been suppressed or are represented as explicit atoms.
     *
     * @return connectivity
     */
    int connectivity() {
        return connectivity;
    }

    /**
     * The degree of a vertex defined as the number of explicit connected bonds.
     * This value is matched by the {@code D<NUMBER>} token. This value depends
     * on whether the hydrogens have been suppressed or are represented as
     * explicit atoms.
     *
     * @return connectivity
     */
    int degree() {
        return degree;
    }

    /**
     * The total number of hydrogens attached to an atom.
     *
     * @return
     */
    int totalHydrogenCount() {
        return totalHydrogenCount;
    }

    /**
     * Computes {@link SMARTSAtomInvariants} and stores on the {@link #KEY} or
     * each {@link IAtom} in the {@code container}. The {@link
     * CDKConstants#ISINRING} is also set for each bond. This configuration does
     * not include ring information and values are left as unset.
     * Ring membership is still configured but not ring size.
     *
     * <blockquote><pre>
     *     IAtomContainer container = ...;
     *     SMARTSAtomInvariants.configureDaylightWithoutRingInfo(container);
     *     for (IAtom atom : container.atoms()) {
     *         SMARTSAtomInvariants inv = atom.getProperty(SMARTSAtomInvariants.KEY);
     *     }
     * </pre></blockquote>
     *
     * @param container the container to configure
     */
    static void configureDaylightWithoutRingInfo(IAtomContainer container) {
        EdgeToBondMap map = EdgeToBondMap.withSpaceFor(container);
        int[][] graph = GraphUtil.toAdjList(container, map);
        configureDaylight(container, graph, map, false);
    }

    /**
     * Computes {@link SMARTSAtomInvariants} and stores on the {@link #KEY} or
     * each {@link IAtom} in the {@code container}. The {@link
     * CDKConstants#ISINRING} is also set for each bond. This configuration
     * includes the ring information as used by the Daylight implementation.
     * That is the Smallest Set of Smallest Rings (SSSR) is used and only the
     * smallest ring is stored for the {@link #ringSize()}.
     *
     * <blockquote><pre>
     *     IAtomContainer container = ...;
     *     SMARTSAtomInvariants.configureDaylightWithRingInfo(container);
     *     for (IAtom atom : container.atoms()) {
     *         SMARTSAtomInvariants inv = atom.getProperty(SMARTSAtomInvariants.KEY);
     *
     *     }
     * </pre></blockquote>
     *
     * @param container the container to configure
     */
    static void configureDaylightWithRingInfo(IAtomContainer container) {
        EdgeToBondMap map = EdgeToBondMap.withSpaceFor(container);
        int[][] graph = GraphUtil.toAdjList(container, map);
        configureDaylight(container, graph, map, true);
    }

    /**
     * Computes invariants - see {@link #configureDaylightWithRingInfo(IAtomContainer)}
     * and {@link #configureDaylightWithoutRingInfo(IAtomContainer)}.
     *
     * @param container the container to configure
     * @param graph     the graph for quick traversal
     * @param bondMap   the bond map for quick bond lookup
     * @param ringInfo  logical condition as whether ring info should be
     *                  included
     */
    private static void configureDaylight(IAtomContainer container, int[][] graph, EdgeToBondMap bondMap,
            boolean ringInfo) {

        int nAtoms = container.getAtomCount();

        int[] ringNumber = new int[nAtoms];
        int[] ringSize = new int[nAtoms];

        Arrays.fill(ringSize, nAtoms + 1);

        if (ringInfo) {
            // non-unique but used by daylight
            for (int[] cycle : Cycles.sssr(container).paths()) {
                int size = cycle.length - 1;
                for (int i = 1; i < cycle.length; i++) {
                    int v = cycle[i];
                    if (size < ringSize[v]) ringSize[v] = size;
                    ringNumber[v]++;
                    bondMap.get(cycle[i], cycle[i - 1]).setFlag(CDKConstants.ISINRING, true);
                }
            }
        } else {
            // ring membership is super cheap
            for (IBond bond : new RingSearch(container, graph).ringFragments().bonds()) {
                bond.setFlag(CDKConstants.ISINRING, true);
            }
        }

        for (int v = 0; v < nAtoms; v++) {

            IAtom atom = container.getAtom(v);

            int implHCount = checkNotNull(atom.getImplicitHydrogenCount(), "Implicit hydrogen count was not set.");

            int totalHCount = implHCount;
            int valence = implHCount;
            int degree = 0;
            int ringConnections = 0;

            // traverse bonds
            for (int w : graph[v]) {
                IBond bond = bondMap.get(v, w);
                IBond.Order order = bond.getOrder();

                if (order == null || order == IBond.Order.UNSET)
                    throw new NullPointerException("Bond order was not set.");

                valence += order.numeric();

                degree++;

                if (bond.getFlag(CDKConstants.ISINRING)) {
                    ringConnections++;
                }

                if (container.getAtom(w).getAtomicNumber() == 1) {
                    totalHCount++;
                }

            }

            SMARTSAtomInvariants inv = new SMARTSAtomInvariants(container, valence, ringNumber[v],
                    ringSize[v] <= nAtoms ? Collections.singleton(ringSize[v]) : Collections.<Integer> emptySet(),
                    ringConnections, degree, degree + implHCount, totalHCount);

            // if there was no properties a default size LinkedHashMap is created
            // automatically
            atom.setProperty(SMARTSAtomInvariants.KEY, inv);
        }
    }
}
