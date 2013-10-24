package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

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
 * ({@link IAtom#setProperty(Object, Object)}). <p/> Depending on the SMARTS
 * implementation different values for the ring information may be set. The
 * choice of ring set affects {@link #ringNumber()} and {@link #ringSize()}.
 * Some implementations store all ring sizes whilst others (Daylight) store only
 * the smallest. The {@link #degree()} also depends on whether hydrogens are                                                                          
 * suppressed or represented as explicit atoms. <p/> The {@link
 * #configureDaylightWithRingInfo(IAtomContainer)} and {@link
 * #configureDaylightWithoutRingInfo(IAtomContainer)} static utilities create
 * and set the invariants following the Daylight implementation. The invariants
 * are set on the {@link #KEY} property of each atom.
 *
 * @author John May
 * @cdk.module smarts
 */
@TestClass("org.openscience.cdk.isomorphism.matchers.smarts.DaylightSMARTSAtomInvariantsTest") 
final class SMARTSAtomInvariants {

    /** Property key to index the class by. */
    static String KEY = "SMARTS.INVARIANTS";

    /** Total number of bonds formed - also refereed to as bond order sum. */
    private final int valence;

    /** The number of rings this atom can be found in. */
    private final int ringNumber;

    /** The size of rings an atom is found in. */
    private final Set<Integer> ringSize;

    /** Total number of connected atoms including implicit hydrogens. */
    private final int connectivity;

    /** Total number of connected ring bonds. */
    private final int ringConnectivity;

    /** Total number of explicitly connected atoms. */
    private final int degree;

    /** The total number of hydrogens on an atom. */
    private final int totalHydrogenCount;

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
    SMARTSAtomInvariants(int valence,
                         int ringNumber,
                         Set<Integer> ringSize,
                         int ringConnectivity,
                         int degree,
                         int connectivity,
                         int totalHydrogenCount) {
        this.valence = valence;
        this.ringNumber = ringNumber;
        this.ringSize = ringSize;
        this.connectivity = connectivity;
        this.totalHydrogenCount = totalHydrogenCount;
        this.ringConnectivity = ringConnectivity;
        this.degree = degree;
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
    @TestMethod("valence") int valence() {
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
    @TestMethod("ringNumber,ringNumber_cyclophane") int ringNumber() {
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
    @TestMethod("ringSize,ringSize_cyclophane,ringSize_imidazole") Set<Integer> ringSize() {
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
    @TestMethod("ringConnectivity") int ringConnectivity() {
        return ringConnectivity;
    }

    /**
     * The number of connected bonds including those to hydrogens. This value is
     * matched by the {@code X<NUMBER>} token. This value depends on whether the
     * hydrogens have been suppressed or are represented as explicit atoms.
     *
     * @return connectivity
     */
    @TestMethod("connectivity") int connectivity() {
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
    @TestMethod("degree") int degree() {
        return degree;
    }

    /**
     * The total number of hydrogens attached to an atom.
     *
     * @return
     */
    @TestMethod("totalHydrogenCount") int totalHydrogenCount() {
        return totalHydrogenCount;
    }

    /**
     * Computes {@link SMARTSAtomInvariants} and stores on the {@link #KEY} or
     * each {@link IAtom} in the {@code container}. The {@link
     * CDKConstants#ISINRING} is also set for each bond. This configuration does
     * not include ring information and values are left as unset.
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
    @TestMethod("noRingInfo")
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
    @TestMethod("valence,degree")
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
    private static void configureDaylight(IAtomContainer container,
                                          int[][] graph,
                                          EdgeToBondMap bondMap,
                                          boolean ringInfo) {

        int nAtoms = container.getAtomCount();

        int[] valence = new int[nAtoms];
        int[] totalHCount = new int[nAtoms];
        int[] ringNumber = new int[nAtoms];
        int[] ringSize = new int[nAtoms];
        int[] ringConnections = new int[nAtoms];
        int[] degree = new int[nAtoms];

        Arrays.fill(ringSize, nAtoms + 1);

        if (ringInfo) {
            // non-unique but used by daylight
            for (int[] cycle : Cycles.sssr(container).paths()) {
                int size = cycle.length - 1;
                for (int i = 1; i < cycle.length; i++) {
                    int v = cycle[i];
                    if (size < ringSize[v])
                        ringSize[v] = size;
                    ringNumber[v]++;
                    bondMap.get(cycle[i], cycle[i - 1]).setFlag(CDKConstants.ISINRING, true);
                }
            }
        }

        for (int v = 0; v < nAtoms; v++) {

            IAtom atom = container.getAtom(v);

            int implHCount = checkNotNull(atom.getImplicitHydrogenCount(),
                                          "Implicit hydrogen count was not set.");

            totalHCount[v] += implHCount;
            valence[v] += implHCount;


            // increment any atoms adjacent to an explicit hydrogen
            if (atom.getAtomicNumber() == 1) {
                for (int w : graph[v]) {
                    totalHCount[w] += 1;
                }
            }

            // traverse bonds 
            for (int w : graph[v]) {
                if (w > v) {
                    IBond bond = bondMap.get(v, w);
                    IBond.Order order = bond.getOrder();

                    if (order == null || order == IBond.Order.UNSET)
                        throw new NullPointerException("Bond order was not set.");

                    valence[v] += order.numeric();
                    valence[w] += order.numeric();

                    degree[v]++;
                    degree[w]++;

                    if (bond.getFlag(CDKConstants.ISINRING)) {
                        ringConnections[v]++;
                        ringConnections[w]++;
                    }
                }
            }
        }

        for (int v = 0; v < nAtoms; v++) {
            IAtom atom = container.getAtom(v);
            SMARTSAtomInvariants inv = new SMARTSAtomInvariants(valence[v],
                                                                ringNumber[v],
                                                                ringSize[v] <= nAtoms ? Collections.singleton(ringSize[v])
                                                                                      : Collections.<Integer>emptySet(),
                                                                ringConnections[v],
                                                                degree[v],
                                                                degree[v] + atom.getImplicitHydrogenCount(),
                                                                totalHCount[v]);

            // if there was no properties a default size LinkedHashMap is created
            // automatically
            atom.setProperty(SMARTSAtomInvariants.KEY,
                             inv);
        }
    }
}
