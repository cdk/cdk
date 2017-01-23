/* Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                    2009  Mark Rijnbeek <mark_rynbeek@users.sf.net>
 *                    2013  European Bioinformatics Institute (EMBL-EBI)
 *                          John May <jwmay@users.sf.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
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
package org.openscience.cdk.ringsearch;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AllCycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;

/**
 * Compute the set of all rings in a molecule. This set includes <i>every</i>
 * cyclic path of atoms. As the set is exponential it can be very large and is
 * often impractical (e.g. fullerenes). 
 *
 * To avoid combinatorial explosion there is a configurable threshold, at which
 * the computation aborts. The {@link Threshold} values have been precomputed on
 * PubChem-Compound and can be used with the {@link AllRingsFinder#usingThreshold(Threshold)}.
 * Alternatively, other ring sets which are a subset of this set offer a
 * tractable alternative. 
 *
 * <blockquote><pre>
 * AllRingsFinder arf = new AllRingsFinder();
 * for (IAtomContainer m : ms) {
 *     try {
 *         IRingSet rs = arf.findAllRings(m);
 *     } catch (CDKException e) {
 *         // molecule was too complex, handle error
 *     }
 * }
 * </pre></blockquote>
 *
 * @author steinbeck
 * @author johnmay
 * @cdk.module standard
 * @cdk.githash
 * @cdk.keyword rings
 * @cdk.keyword all rings
 * @see AllCycles
 */
public final class AllRingsFinder {

    /** Precomputed threshold - stops the computation running forever. */
    private final Threshold threshold;

    /**
     * Constructor for the AllRingsFinder.
     *
     * @param logging true=logging will be done (slower), false = no logging.
     * @deprecated turn logging off by setting the level in the logger
     *             implementation
     */
    @Deprecated
    public AllRingsFinder(boolean logging) {
        this(Threshold.PubChem_99);
    }

    /** Default constructor using a threshold of {@link Threshold#PubChem_99}. */
    public AllRingsFinder() {
        this(Threshold.PubChem_99);
    }

    /** Internal constructor. */
    private AllRingsFinder(Threshold threshold) {
        this.threshold = threshold;
    }

    /**
     * Compute all rings in the given {@link IAtomContainer}. The container is
     * first partitioned into ring systems which are then processed separately.
     * If the molecule has already be partitioned, consider using {@link
     * #findAllRingsInIsolatedRingSystem(IAtomContainer)}.
     *
     * @param container The AtomContainer to be searched for rings
     * @return A RingSet with all rings in the AtomContainer
     * @throws CDKException An exception thrown if the threshold was exceeded
     * @see #findAllRings(IAtomContainer, int)
     * @see #findAllRingsInIsolatedRingSystem(IAtomContainer)
     */
    public IRingSet findAllRings(IAtomContainer container) throws CDKException {
        return findAllRings(container, container.getAtomCount());
    }

    /**
     * Compute all rings up to and including the {@literal maxRingSize}. The
     * container is first partitioned into ring systems which are then processed
     * separately. If the molecule has already be partitioned, consider using
     * {@link #findAllRingsInIsolatedRingSystem(IAtomContainer, int)}.
     *
     * @param container   The AtomContainer to be searched for rings
     * @param maxRingSize Maximum ring size to consider. Provides a possible
     *                    breakout from recursion for complex compounds.
     * @return A RingSet with all rings in the AtomContainer
     * @throws CDKException An exception thrown if the threshold was exceeded
     */
    public IRingSet findAllRings(IAtomContainer container, int maxRingSize) throws CDKException {

        final EdgeToBondMap edges = EdgeToBondMap.withSpaceFor(container);
        final int[][] graph = GraphUtil.toAdjList(container, edges);

        RingSearch rs = new RingSearch(container, graph);

        IRingSet ringSet = container.getBuilder().newInstance(IRingSet.class);

        // don't need to run on isolated rings, just need to put vertices in
        // cyclic order
        for (int[] isolated : rs.isolated()) {
            if (isolated.length <= maxRingSize) {
                IRing ring = toRing(container, edges, GraphUtil.cycle(graph, isolated));
                ringSet.addAtomContainer(ring);
            }
        }

        // for each set of fused cyclic vertices run the separate search
        for (int[] fused : rs.fused()) {

            AllCycles ac = new AllCycles(GraphUtil.subgraph(graph, fused), Math.min(maxRingSize, fused.length),
                    threshold.value);

            if (!ac.completed()) throw new CDKException("Threshold exceeded for AllRingsFinder");

            for (int[] path : ac.paths()) {
                IRing ring = toRing(container, edges, path, fused);
                ringSet.addAtomContainer(ring);
            }
        }

        return ringSet;
    }

    /**
     * Compute all rings in the given {@link IAtomContainer}. No pre-processing
     * is done on the container.
     *
     * @param container The Atom Container to find the ring systems of
     * @return RingSet for the container
     * @throws CDKException An exception thrown if the threshold was exceeded
     */
    public IRingSet findAllRingsInIsolatedRingSystem(IAtomContainer container) throws CDKException {
        return findAllRingsInIsolatedRingSystem(container, container.getAtomCount());
    }

    /**
     * Compute all rings up to an including the {@literal maxRingSize}. No
     * pre-processing is done on the container.
     *
     * @param atomContainer the molecule to be searched for rings
     * @param maxRingSize   Maximum ring size to consider. Provides a possible
     *                      breakout from recursion for complex compounds.
     * @return a RingSet containing the rings in molecule
     * @throws CDKException An exception thrown if the threshold was exceeded
     */
    public IRingSet findAllRingsInIsolatedRingSystem(IAtomContainer atomContainer, int maxRingSize) throws CDKException {

        final EdgeToBondMap edges = EdgeToBondMap.withSpaceFor(atomContainer);
        final int[][] graph = GraphUtil.toAdjList(atomContainer, edges);

        AllCycles ac = new AllCycles(graph, maxRingSize, threshold.value);

        if (!ac.completed()) throw new CDKException("Threshold exceeded for AllRingsFinder");

        IRingSet ringSet = atomContainer.getBuilder().newInstance(IRingSet.class);

        for (int[] path : ac.paths()) {
            ringSet.addAtomContainer(toRing(atomContainer, edges, path));
        }

        return ringSet;
    }

    /**
     * Checks if the timeout has been reached and throws an exception if so.
     * This is used to prevent this AllRingsFinder to run for ages in certain
     * rare cases with ring systems of large size or special topology.
     *
     * @throws CDKException The exception thrown in case of hitting the timeout
     * @deprecated timeout not used
     */
    @Deprecated
    public void checkTimeout() throws CDKException {
        // unused
    }

    /**
     * Sets the timeout value in milliseconds of the AllRingsFinder object This
     * is used to prevent this AllRingsFinder to run for ages in certain rare
     * cases with ring systems of large size or special topology
     *
     * @param timeout The new timeout value
     * @return a reference to the instance this method was called for
     * @deprecated use the new threshold (during construction)
     */
    @Deprecated
    public AllRingsFinder setTimeout(long timeout) {
        System.err.println("AllRingsFinder.setTimeout() is not used, please " + "use the new threshold values");
        return this;
    }

    /**
     * Gets the timeout values in milliseconds of the AllRingsFinder object
     *
     * @return The timeout value
     * @deprecated timeout not used
     */
    @Deprecated
    public long getTimeout() {
        return 0;
    }

    /**
     * Convert a cycle in {@literal int[]} representation to an {@link IRing}.
     *
     * @param container atom container
     * @param edges     edge map
     * @param cycle     vertex walk forming the cycle, first and last vertex the
     *                  same
     * @return a new ring
     */
    private IRing toRing(IAtomContainer container, EdgeToBondMap edges, int[] cycle) {
        IRing ring = container.getBuilder().newInstance(IRing.class, 0);

        int len = cycle.length - 1;

        IAtom[] atoms = new IAtom[len];
        IBond[] bonds = new IBond[len];

        for (int i = 0; i < len; i++) {
            atoms[i] = container.getAtom(cycle[i]);
            bonds[i] = edges.get(cycle[i], cycle[i + 1]);
            atoms[i].setFlag(CDKConstants.ISINRING, true);
        }

        ring.setAtoms(atoms);
        ring.setBonds(bonds);

        return ring;
    }

    /**
     * Convert a cycle in {@literal int[]} representation to an {@link IRing}
     * but first map back using the given {@literal mapping}.
     *
     * @param container atom container
     * @param edges     edge map
     * @param cycle     vertex walk forming the cycle, first and last vertex the
     *                  same
     * @return a new ring
     */
    private IRing toRing(IAtomContainer container, EdgeToBondMap edges, int[] cycle, int[] mapping) {
        IRing ring = container.getBuilder().newInstance(IRing.class, 0);

        int len = cycle.length - 1;

        IAtom[] atoms = new IAtom[len];
        IBond[] bonds = new IBond[len];

        for (int i = 0; i < len; i++) {
            atoms[i] = container.getAtom(mapping[cycle[i]]);
            bonds[i] = edges.get(mapping[cycle[i]], mapping[cycle[i + 1]]);
            atoms[i].setFlag(CDKConstants.ISINRING, true);
        }

        ring.setAtoms(atoms);
        ring.setBonds(bonds);

        return ring;
    }

    /**
     * The threshold values provide a limit at which the computation stops.
     * There will always be some ring systems in which we cannot compute every
     * possible ring (e.g. Fullerenes). This limit replaces the previous timeout
     * and provides a more meaningful measure of what to expect based on
     * precomputed percentiles. It is important to consider that, higher is not
     * always better - generally the large values generate many more rings then
     * can be reasonably be handled.<br>
     *
     * The latest results were calculated on PubChem Compound (Dec' 12) and
     * summarised below.
     *
     * <table style="width: 100%;"> <tr><th>Maximum Degree</th><th>Percent
     * (%)</th><th>Completed<br> (ring systems)</th><th>Uncompleted<br>
     * (ring systems)</th></tr> <tr><td>&nbsp;</td></tr>
     * <tr><td>72</td><td>99.95</td><td>17834013</td><td>8835</td></tr>
     * <tr><td>84</td><td>99.96</td><td>17835876</td><td>6972</td></tr>
     * <tr><td>126</td><td>99.97</td><td>17837692</td><td>5156</td></tr>
     * <tr><td>216</td><td>99.98</td><td>17839293</td><td>3555</td></tr>
     * <tr><td>684</td><td>99.99 (default)</td><td>17841065</td><td>1783</td></tr>
     * <tr><td>&nbsp;</td></tr> <tr><td>882</td><td>99.991</td><td>17841342</td><td>1506</td></tr>
     * <tr><td>1062</td><td>99.992</td><td>17841429</td><td>1419</td></tr>
     * <tr><td>1440</td><td>99.993</td><td>17841602</td><td>1246</td></tr>
     * <tr><td>3072</td><td>99.994</td><td>17841789</td><td>1059</td></tr>
     * </table>
     *
     * @see <a href="http://efficientbits.blogspot.co.uk/2013/06/allringsfinder-sport-edition.html">AllRingsFinder,
     *      Sport Edition</a>
     */
    public enum Threshold {

        /**
         * Based on PubChem Compound (Dec '12), perception will complete for
         * 99.95% of ring systems.
         */
        PubChem_95(72),
        /**
         * Based on PubChem Compound (Dec '12), perception will complete for
         * 99.96% of ring systems.
         */
        PubChem_96(84),
        /**
         * Based on PubChem Compound (Dec '12), perception will complete for
         * 99.97% of ring systems.
         */
        PubChem_97(126),
        /**
         * Based on PubChem Compound (Dec '12), perception will complete for
         * 99.98% of ring systems.
         */
        PubChem_98(216),
        /**
         * Based on PubChem Compound (Dec '12), perception will complete for
         * 99.99% of ring systems.
         */
        PubChem_99(684),
        /**
         * Based on PubChem Compound (Dec '12), perception will complete for
         * 99.991% of ring systems.
         */
        PubChem_991(882),
        /**
         * Based on PubChem Compound (Dec '12), perception will complete for
         * 99.992% of ring systems.
         */
        PubChem_992(1062),
        /**
         * Based on PubChem Compound (Dec '12), perception will complete for
         * 99.993% of ring systems.
         */
        PubChem_993(1440),
        /**
         * Based on PubChem Compound (Dec '12), perception will complete for
         * 99.994% of ring systems.
         */
        PubChem_994(3072),
        /** Run without any threshold, possibly until the end of time itself. */
        None(Integer.MAX_VALUE);

        private final int value;

        private Threshold(int value) {
            this.value = value;
        }
    }

    /**
     * Create an {@link AllRingsFinder} instance using the given threshold.
     *
     * <blockquote><pre>
     * // import static AllRingsFinder.Threshold.PubChem_99;
     * AllRingsFinder arf = AllRingsFinder.usingThreshold(PubChem_99);
     * </pre></blockquote>
     *
     * @param threshold the threshold value
     * @return instance with the set threshold
     */
    public static AllRingsFinder usingThreshold(Threshold threshold) {
        return new AllRingsFinder(threshold);
    }
}
