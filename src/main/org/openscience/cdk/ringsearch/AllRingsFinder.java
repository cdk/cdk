/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                    2009  Mark Rijnbeek <mark_rynbeek@users.sf.net>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Finds the Set of all Rings. This is an implementation of the algorithm
 * published in {@cdk.cite HAN96}. Some of the comments refer to pseudo code
 * fragments listed in this article. The concept is that a regular molecular
 * graph is converted into a path graph first, i.e. a graph where the edges are
 * actually paths, i.e. can list several nodes that are implicitly connecting
 * the two nodes between the path is formed. The paths that join one endnode are
 * step by step fused and the joined nodes deleted from the pathgraph. What
 * remains is a graph of paths that have the same start and endpoint and are
 * thus rings.
 *
 * <p><b>WARNING</b>: This class has now a timeout of 5 seconds, after which it
 * aborts its ringsearch. The timeout value can be customized by the
 * setTimeout() method of this class. <br>Also, by using the optional argument
 * "maxRingSize" timeouts can possibly be avoided because recursion depth will
 * be limited accordingly. Example: given a complex atom container and a
 * maxRingSize of six, the find method will return all rings only of size six or
 * smaller.
 *
 * @author steinbeck
 * @cdk.created 2002-06-23
 * @cdk.module standard
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.ringsearch.AllRingsFinderTest")
public class AllRingsFinder {

    /** Logger for the class. */
    private final ILoggingTool logger = LoggingToolFactory
            .createLoggingTool(AllRingsFinder.class);

    /** Precomputed threshold - stops the computation running forever. */
    private final Threshold threshold;

    /*
     *  used for storing the original atomContainer for
     *  reference purposes (printing)
     */
    IAtomContainer originalAc     = null;
    List<Path>     newPaths       = new ArrayList<Path>();
    List<Path>     potentialRings = new ArrayList<Path>();
    List<Path>     removePaths    = new ArrayList<Path>();

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

    /** Constructor for the AllRingsFinder with logging. */
    public AllRingsFinder() {
        this(Threshold.PubChem_99);
    }

    private AllRingsFinder(Threshold threshold) {
        this.threshold = threshold;
    }

    /**
     * Returns a ringset containing all rings in the given AtomContainer Calls
     * {@link #findAllRings(IAtomContainer, Integer)} with max ring size
     * argument set to null (=unlimited ring sizes)
     *
     * @param atomContainer The AtomContainer to be searched for rings
     * @return A RingSet with all rings in the AtomContainer
     * @throws CDKException An exception thrown if something goes wrong or if
     *                      the timeout limit is reached
     */
    @TestMethod("testFindAllRings_IAtomContainer,testBondsWithinRing")
    public IRingSet findAllRings(IAtomContainer atomContainer) throws
                                                               CDKException {
        return findAllRings(atomContainer, null);
    }

    /**
     * Returns a ringset containing all rings up to a provided maximum size in a
     * given AtomContainer
     *
     * @param atomContainer The AtomContainer to be searched for rings
     * @param maxRingSize   Maximum ring size to consider. Provides a possible
     *                      breakout from recursion for complex compounds.
     * @return A RingSet with all rings in the AtomContainer
     * @throws CDKException An exception thrown if something goes wrong or if
     *                      the timeout limit is reached
     */
    public IRingSet findAllRings(IAtomContainer atomContainer, Integer maxRingSize) throws
                                                                                    CDKException {
        SpanningTree spanningTree = new SpanningTree(atomContainer);
        IAtomContainer ringSystems = spanningTree.getCyclicFragmentsContainer();
        Iterator<IAtomContainer> separateRingSystem = ConnectivityChecker
                .partitionIntoMolecules(ringSystems).atomContainers()
                .iterator();
        IRingSet resultSet = atomContainer.getBuilder()
                                          .newInstance(IRingSet.class);
        while (separateRingSystem.hasNext()) {
            resultSet
                    .add(findAllRingsInIsolatedRingSystem((IAtomContainer) separateRingSystem
                            .next(), maxRingSize));
        }
        return resultSet;
    }


    /**
     * Fings the set of all rings in a molecule Calls {@link
     * #findAllRingsInIsolatedRingSystem(IAtomContainer, Integer)} with max ring
     * size argument set to null (=unlimited ring sizes)
     *
     * @param atomContainer the molecule to be searched for rings
     * @return a RingSet containing the rings in molecule
     * @throws CDKException An exception thrown if something goes wrong or if
     *                      the timeout limit is reached
     */

    public IRingSet findAllRingsInIsolatedRingSystem(IAtomContainer atomContainer) throws
                                                                                   CDKException {
        return findAllRingsInIsolatedRingSystem(atomContainer, null);
    }

    /**
     * Finds the set of all rings in a molecule
     *
     * @param atomContainer the molecule to be searched for rings
     * @param maxRingSize   Maximum ring size to consider. Provides a possible
     *                      breakout from recursion for complex compounds.
     * @return a RingSet containing the rings in molecule
     * @throws CDKException An exception thrown if something goes wrong or if
     *                      the timeout limit is reached
     */
    public IRingSet findAllRingsInIsolatedRingSystem(IAtomContainer atomContainer, Integer maxRingSize) throws
                                                                                                        CDKException {
        List<Path> paths = new ArrayList<Path>();
        IRingSet ringSet = atomContainer.getBuilder()
                                        .newInstance(IRingSet.class);
        IAtomContainer ac = atomContainer.getBuilder()
                                         .newInstance(IAtomContainer.class);
        originalAc = atomContainer;
        ac.add(atomContainer);
        doSearch(ac, paths, ringSet, maxRingSize);
        return ringSet;
    }


    /**
     * @param ac      The AtomContainer to be searched
     * @param paths   A vectoring storing all the paths
     * @param ringSet A ringset to be extended while we search
     * @throws CDKException An exception thrown if something goes wrong or if the
     *                      timeout limit is reached
     */
    private void doSearch(IAtomContainer ac, List<Path> paths, IRingSet ringSet, Integer maxPathLen) throws
                                                                                                     CDKException {
        IAtom atom;
        /*
		 *  First we convert the molecular graph into a a path graph by
		 *  creating a set of two membered paths from all the bonds in the molecule
		 */
        initPathGraph(ac, paths);
        if (logger != null) {
            logger.debug("BondCount: ", ac.getBondCount());
            logger.debug("PathCount: ", paths.size());
        }
        do {
            atom = selectAtom(ac);
            if (atom != null) {
                remove(atom, ac, paths, ringSet, maxPathLen);
            }
        } while (paths.size() > 0 && atom != null);
        if (logger != null) {
            logger.debug("paths.size(): ", paths.size());
            logger.debug("ringSet.size(): ", ringSet.getAtomContainerCount());
        }
    }


    /**
     * Removes an atom from the AtomContainer under certain conditions. See
     * {@cdk.cite HAN96} for details
     *
     * @param atom       The atom to be removed
     * @param ac         The AtomContainer to work on
     * @param paths      The paths to manipulate
     * @param rings      The ringset to be extended
     * @param maxPathLen Max path length = max ring size detected = max
     *                   recursion depth
     * @throws CDKException Thrown if something goes wrong or if the timeout is
     *                      exceeded
     */
    private void remove(IAtom atom, IAtomContainer ac, List<Path> paths, IRingSet rings, Integer maxPathLen) throws
                                                                                                             CDKException {
        Path path1;
        Path path2;
        Path union;
        int intersectionSize;
        newPaths.clear();
        removePaths.clear();
        potentialRings.clear();
        if (logger != null)
            logger.debug("*** Removing atom " + originalAc
                    .getAtomNumber(atom) + " ***");

        for (int i = 0; i < paths.size(); i++) {
            path1 = paths.get(i);
            if (path1.firstElement() == atom || path1.lastElement() == atom) {
                for (int j = i + 1; j < paths.size(); j++) {
                    //logger.debug(".");
                    path2 = paths.get(j);
                    if (path2.firstElement() == atom || path2
                            .lastElement() == atom) {
                        intersectionSize = path1.getIntersectionSize(path2);
                        if (intersectionSize < 3) {
                            if (logger != null) {
                                logger.debug("Joining " + path1
                                        .toString(originalAc) + " and " + path2
                                        .toString(originalAc));
                            }
                            union = Path.join(path1, path2, atom);
                            if (intersectionSize == 1) {
                                newPaths.add(union);
                            } else {
                                if (maxPathLen == null || union
                                        .size() <= (maxPathLen + 1)) {
                                    potentialRings.add(union);
                                }
                            }
                            //logger.debug("Intersection Size: " + intersectionSize);
                            if (logger != null) {
                                logger.debug("Union: ", union
                                        .toString(originalAc));
                            }
							/*
							 *  Now we know that path1 and
							 *  path2 share the Atom atom.
							 */
                            removePaths.add(path1);
                            removePaths.add(path2);
                        }
                    }
                }
            }
        }
        for (Path removePath : removePaths) {
            paths.remove(removePath);
        }
        for (Path newPath : newPaths) {
            if (maxPathLen == null || newPath.size() <= (maxPathLen + 1)) {
                paths.add(newPath);
            }
        }
        detectRings(potentialRings, rings, originalAc);
        ac.removeAtomAndConnectedElectronContainers(atom);
        if (logger != null)
            logger.debug("\n" + paths.size() + " paths and " + ac
                    .getAtomCount() + " atoms left.");
    }


    /**
     * Checks the paths if a ring has been found
     *
     * @param paths   The paths to check for rings
     * @param ringSet The ringset to add the detected rings to
     * @param ac      The AtomContainer with the original structure
     */
    private void detectRings(List<Path> paths, IRingSet ringSet, IAtomContainer ac) {
        IRing ring;
        int bondNum;
        IAtom a1, a2 = null;
        for (Path path : paths) {
            if (path.size() > 3 && path.lastElement() == path.firstElement()) {
                if (logger != null)
                    logger.debug("Removing path " + path
                            .toString(originalAc) + " which is a ring.");
                path.removeElementAt(0);
                ring = ac.getBuilder().newInstance(IRing.class);
                for (int g = 0; g < path.size() - 1; g++) {
                    a1 = (IAtom) path.elementAt(g);
                    a2 = (IAtom) path.elementAt(g + 1);
                    ring.addAtom(a1);
                    bondNum = ac.getBondNumber(a1, a2);
                    //logger.debug("bondNum " + bondNum);
                    ring.addBond(ac.getBond(bondNum));
                }
                ring.addAtom(a2);
                a1 = (IAtom) path.elementAt(0);
                a2 = (IAtom) path.elementAt(path.size() - 1);
                ring.addAtom(a1);
                bondNum = ac.getBondNumber(a1, a2);
                //logger.debug("bondNum " + bondNum);
                ring.addBond(ac.getBond(bondNum));

                /*
                     * The following code had a problem when two atom in the ring
                     * found are connected the in orignal graph but do not belong
                     * to this particular ring.
                     IBond[] bonds = ac.getBonds();
                    for (int g = 0; g < bonds.length; g++)
                    {
                        bond = bonds[g];
                        if (ring.contains(bond.getAtom(0)) && ring.contains(bond.getAtom(1)))
                        {
                            ring.addBond(bond);
                        }
                    }*/
                ringSet.addAtomContainer(ring);
            }
        }
    }


    /**
     * Initialized the path graph See {@cdk.cite HAN96} for details
     *
     * @param ac    The AtomContainer with the original structure
     * @param paths The paths to initialize
     */
    private void initPathGraph(IAtomContainer ac, List<Path> paths) {
        Path path;

        Iterator<IBond> bonds = ac.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            path = new Path(bond.getAtom(0), bond.getAtom(1));
            paths.add(path);
            if (logger != null)
                logger.debug("initPathGraph: " + path.toString(originalAc));
        }
    }


    /**
     * Selects an optimal atom for removal See {@cdk.cite HAN96} for details
     *
     * @param ac The AtomContainer to search
     * @return The selected Atom
     */
    private IAtom selectAtom(IAtomContainer ac) {
        int minDegree = 999;
        // :-)
        int degree;
        IAtom minAtom = null;
        IAtom atom;
        for (int f = 0; f < ac.getAtomCount(); f++) {
            atom = ac.getAtom(f);
            degree = ac.getConnectedBondsCount(atom);

            if (degree < minDegree) {
                minAtom = atom;
                minDegree = degree;
            }
        }

        return minAtom;
    }


    /**
     * Checks if the timeout has been reached and throws an exception if so.
     * This is used to prevent this AllRingsFinder to run for ages in certain
     * rare cases with ring systems of large size or special topology.
     *
     * @throws CDKException The exception thrown in case of hitting the timeout
     * @deprecated
     */
    @TestMethod("testCheckTimeout")
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
    @TestMethod("testSetTimeout_long")
    @Deprecated
    public AllRingsFinder setTimeout(long timeout) {
        System.err.println("AllRingsFinder.setTimeout() is not used, please" +
                                   "use the new threshold values");
        return this;
    }


    /**
     * Gets the timeout values in milliseconds of the AllRingsFinder object
     *
     * @return The timeout value
     * @deprecated timeout not long used
     */
    @TestMethod("testGetTimeout")
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
    private IRing toRing(IAtomContainer container,
                         Map<Edge, IBond> edges,
                         int[] cycle) {
        IRing ring = container.getBuilder().newInstance(IRing.class, 0);

        int len = cycle.length - 1;

        IAtom[] atoms = new IAtom[len];
        IBond[] bonds = new IBond[len];

        for (int i = 0; i < len; i++) {
            atoms[i] = container.getAtom(cycle[i]);
            bonds[i] = edges.get(new Edge(cycle[i], cycle[i + 1]));
        }

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
    private IRing toRing(IAtomContainer container,
                         Map<Edge, IBond> edges,
                         int[] cycle,
                         int[] mapping) {
        IRing ring = container.getBuilder().newInstance(IRing.class, 0);

        int len = cycle.length - 1;

        IAtom[] atoms = new IAtom[len];
        IBond[] bonds = new IBond[len];

        for (int i = 0; i < len; i++) {
            atoms[i] = container.getAtom(mapping[cycle[i]]);
            bonds[i] = edges.get(new Edge(mapping[cycle[i]],
                                          mapping[cycle[i + 1]]));
        }

        return ring;
    }

    /**
     * Convert the container to an int[][] adjacency list. The bonds of each
     * edge ar indexed in the {@literal edges} map.
     *
     * @param container molecule
     * @param edges     map of edges to bonds
     * @return adjacency list representation
     */
    private int[][] toGraph(IAtomContainer container, Map<Edge, IBond> edges) {

        if (container == null)
            throw new NullPointerException("atom container was null");

        int n = container.getAtomCount();

        int[][] graph = new int[n][4];
        int[] degree = new int[n];

        for (IBond bond : container.bonds()) {

            int v = container.getAtomNumber(bond.getAtom(0));
            int w = container.getAtomNumber(bond.getAtom(1));

            edges.put(new Edge(v, w), bond);

            if (v < 0 || w < 0)
                throw new IllegalArgumentException("bond at index " + container
                        .getBondNumber(bond)
                                                           + " contained an atom not pressent in molecule");

            graph[v][degree[v]++] = w;
            graph[w][degree[w]++] = v;

            // if the vertex degree of v or w reaches capacity, double the size
            if (degree[v] == graph[v].length)
                graph[v] = Arrays.copyOf(graph[v], degree[v] * 2);
            if (degree[w] == graph[w].length)
                graph[w] = Arrays.copyOf(graph[w], degree[w] * 2);
        }

        for (int v = 0; v < n; v++) {
            graph[v] = Arrays.copyOf(graph[v], degree[v]);
        }

        return graph;
    }

    /**
     * Simple class to allow undirected indexing of edges. One day... should
     * part of public api - but that day is not now.
     */
    private final class Edge {

        /** Endpoints. */
        private final int u, v;

        /**
         * Create a new edge from two endpoints.
         *
         * @param u an endpoint
         * @param v another endpoint
         */
        private Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }

        /** @inheritDoc */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge that = (Edge) o;
            return (this.u == that.u && this.v == that.v) ||
                    (this.u == that.v && this.v == that.u);
        }

        /** @inheritDoc */
        @Override
        public int hashCode() {
            return u ^ v;
        }
    }

    /**
     * The threshold values provide a limit at which the computation stops.
     * There will always be some ring systems in which we cannot compute every
     * possible ring (e.g. Fullerenes). This limit replaces the previous timeout
     * and provides a more meaningful measure of what to expect based on
     * precomputed percentiles. It is important to consider that, higher is not
     * always better - generally the large values generate many more rings then
     * can be reasonably be handled.<br/>
     *
     * The latest results were calculated on PubChem Compound (Dec' 12) and
     * summarised below.
     *
     * <table style="width: 100%;"> <tr><th>Maximum Degree</th><th>Percent
     * (%)</th><th>Completed<br /> (ring systems)</th><th>Uncompleted<br />
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

