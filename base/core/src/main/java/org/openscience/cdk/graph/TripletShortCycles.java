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
package org.openscience.cdk.graph;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Compute the shortest cycles through each vertex triple. This allows one to
 * directly obtain the envelope rings of bicyclic fused system. These cycles
 * can be thought of as the 'ESSSR' (extended smallest set of smallest rings)
 * and 'envelope' rings as used by PubChem fingerprints (CACTVS Substructure
 * Keys). The PubChem fingerprint documentation exclusively refers to the ESSSR
 * and envelopes as just the ESSSR and the rest of this documentation does the
 * same. This class provides the cycles (vertex paths) for each ring in the
 * ESSSR.
 *
 * The ESSSR should not be confused with the extended set of smallest rings
 * (ESSR) {@cdk.cite Downs89}. 
 *
 * <b>Algorithm</b>  To our knowledge no algorithm has been published for
 * the ESSSR. The <a href="ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem_fingerprints.pdf">PubChem
 * Specifications</a> states - <i>"An ESSSR ring is any ring which does not
 * share three consecutive atoms with any other ring in the chemical structure.
 * For example, naphthalene has three ESSSR rings (two phenyl fragments and the
 * 10-membered envelope), while biphenyl will yield a count of only two ESSSR
 * rings"</i>. The name implies the use of the smallest set of smallest rings
 * (SSSR). Not every graph has an SSSR and so the minimum cycle basis is used
 * instead. With this modification the algorithm is outlined below. <ol>
 * <li>Compute a minimum cycle basis (or SSSR) of the graph (may not be
 * unique)</li> <li>For each vertex <i>v</i> and two adjacent vertices (<i>u</i>
 * and <i>w</i>) check if the path <i>-u-v-w-</i> belongs to any cycles already
 * in the basis</li> <li>If no such cycle can be found compute the shortest
 * cycle which travels through <i>-u-v-w-</i> and add it to the basis. The
 * shortest cycle is the shortest path from <i>u</i> to <i>w</i> which does not
 * travel through <i>v</i></li> </ol>  In the case of <i>naphthalene</i> the
 * minimum cycle basis is the two phenyl rings. Taking either bridgehead atom of
 * <i>naphthalene</i> to be <i>v</i> and choosing <i>u</i> and <i>w</i> to be in
 * different phenyl rings it is easy to see the shortest cycle through
 * <i>-u-v-w-</i> is the 10 member envelope ring.
 *
 * <b>Canonical and Non-Canonical Generation</b>
 *
 * The algorithm can generate a canonical or non-canonical (preferred) set of
 * cycles. As one can see from the above description depending on the order we
 * check each triple (-u-v-w-) and add it to basis we may end up with a
 * different set.
 * 
 *
 * To avoid this PubChem fingerprints uses a canonical labelling ensuring the
 * vertices are always checked in the same order. The vertex order used by this
 * class is the natural order of the vertices as provided in the graph. To
 * ensure the generated set is always the same vertices should be ordered
 * beforehand or the non-canonical option should be used.
 *
 * Although this canonical sorting allows one to reliable generate the same set
 * of cycles for a graph this is not true for subgraphs. For two graphs
 * <i>G</i>, <i>H</i> and a canonical ordering (<i>π</i>). If <i>H</i> is a
 * subgraph of <i>G</i> then for two vertices <i>u</i>, <i>v</i>. It follows
 * that <i>π(u)</i> &lt; <i>π(v)</i> ∈ <i>H</i> ⇏ <i>π(u)</i> &lt; <i>π(v)</i> ∈
 * <i>G</i>. In other words, we can canonically label a graph and inspect the
 * ordering of vertices <i>u</i> and <i>v</i>. We now take a subgraph which
 * contains both <i>u</i> and <i>v</i> - the ordering does not need to be the
 * same as the full graph. This means that a subgraph may contain a ring in its
 * ESSSR which does not belong to the ESSSR of the full graph.
 *
 * To resolve this problem you can turn off the <i>canonical</i> option. This
 * relaxes the existing condition (Step 2.) and adds all shortest cycles through
 * each triple (-u-v-w-) to the basis. The number of cycles generated may be
 * larger however it is now possible to ensure that if <i>H</i> is a subgraph of
 * <i>G</i> then ESSSR of <i>H</i> will be a subset of the ESSSR or <i>G</i>.
 * Alternatively one may consider using the {@link RelevantCycles} which is the
 * the smallest set of short cycles which is <i>uniquely</i> defined for a
 * graph. 
 *
 * To better explain the issue with the canonical labelling several examples are
 * shown below. The table outlining the size of rings found for each molecule
 * when using canonical and non-canonical generation. Also shown are the sizes
 * of rings stored in the PubChem fingerprint associated with the entry. The
 * fingerprints were obtained directly from PubChem and decoded using the <a
 * href= "ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem_fingerprints.pdf">
 * specification</a>. Sizes underlined and coloured red represent rings which may
 * or may not be present depending on the atom ordering. It can be seen from the
 * PubChem fingerprint that even using a consistent canonical labelling rings
 * may be absent which would be present if the subgraph was used.
 *
 * <table style="font-family: courier; font-size: 9pt; color: #666666;">
 * <caption></caption>
 * <tr><th>PubChem CID</th><th>Diagram</th><th rowspan="2">Size of Rings in
 * ESSSR <br>(fingerprints only store cycles |C| &lt;=
 * 10)</th><th>Source</th></tr>
 * <tr></tr>
 * <tr>
 * <td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=135973">135973</a></td>
 * <td><img src="http://pubchem.ncbi.nlm.nih.gov/image/imgsrv.fcgi?cid=135973" alt="Compound Image"></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>{3, 3, 4}</td></tr>
 * <tr><td>{3, 3, 4}</td></tr>
 * <tr><td>{3, 3, 4}</td></tr>
 * </table></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>Canonical</td></tr>
 * <tr><td>Non-canonical</td></tr>
 * <tr><td>PubChem Fingerprint</td></tr>
 * </table></td>
 * </tr>
 * <tr>
 * <td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=9249">9249</a></td>
 * <td><img src="http://pubchem.ncbi.nlm.nih.gov/image/imgsrv.fcgi?cid=9249"  alt="Compound Image"></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>{3, 3, <b style="color: #FF4444;"><u>4</u></b>, 6, 6}</td> </tr>
 * <tr><td>{3, 3, 4, 6, 6}</td></tr>
 * <tr><td>{3, 3, 6, 6}</td></tr>
 * </table></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>Canonical - <i>4 member cycle only added if found before larger 6
 * member cycles</i></td></tr>
 * <tr><td>Non-canonical</td></tr>
 * <tr><td>PubChem Fingerprint - <i>4 member cycle not found</i> </td></tr>
 * </table></td>
 * </tr>
 * <tr>
 * <td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=931">931</a></td>
 * <td><img src="http://pubchem.ncbi.nlm.nih.gov/image/imgsrv.fcgi?cid=931"  alt="Compound Image"></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>{6, 6, 10}</td></tr>
 * <tr><td>{6, 6, 10}</td></tr>
 * <tr><td>{6, 6, 10}</td></tr>
 * </table></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>Canonical</td></tr>
 * <tr><td>Non-canonical</td></tr>
 * <tr><td>PubChem Fingerprint</td></tr>
 * </table></td>
 * </tr>
 * <tr>
 * <td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=5702">5702</a></td>
 * <td><img src="http://pubchem.ncbi.nlm.nih.gov/image/imgsrv.fcgi?cid=5702"  alt="Compound Image"></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>{6, 6, 6, 6, <b style="color: #FF4444;"><u>10</u></b>, <b
 * style="color: #FF4444;"><u>10</u></b>, 20, 22, 22, 24, 24}</td></tr>
 * <tr><td>{6, 6, 6, 6, 10, 10, 20, 22, 22, 24, 24}</td></tr>
 * <tr><td>{6, 6, 6, 6}</td></tr>
 * </table></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>Canonical - <i>10 member cycles only added if found before larger
 * cycles</i></td></tr>
 * <tr><td>Non-canonical</td></tr>
 * <tr><td>PubChem Fingerprint - <i>10 member cycles not found</i> </td></tr>
 * </table></td>
 * </tr>
 * <tr>
 * <td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=1211">1211</a></td>
 * <td><img src="http://pubchem.ncbi.nlm.nih.gov/image/imgsrv.fcgi?cid=1211"  alt="Compound Image"></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>{6, 6, 6, 6, 6, 6, <b style="color: #FF4444;"><u>10</u></b>, <b
 * style="color: #FF4444;"><u>10</u></b>, 18, 18, 20, 20, 22, 22, 22}</td></tr>
 * <tr><td>{6, 6, 6, 6, 6, 6, 10, 10, 18, 18, 20, 20, 22, 22, 22}</td></tr>
 * <tr><td>{6, 6, 6, 6, 6, 6, 10, 10}</td></tr>
 * </table></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>Canonical - <i>10 member cycles only added if found before larger
 * cycles</i></td></tr>
 * <tr><td>Non-canonical</td></tr>
 * <tr><td>PubChem Fingerprint - <i>10 member cycles were found</i> </td></tr>
 * </table></td>
 * </tr>
 * <tr>
 * <td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=17858819">17858819</a></td>
 * <td><img src="http://pubchem.ncbi.nlm.nih.gov/image/imgsrv.fcgi?cid=17858819" alt="Compound Image"></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>{5, 6, 9}</td></tr>
 * <tr><td>{5, 6, 9}</td></tr>
 * <tr><td>{5, 6, 9}</td></tr>
 * </table></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>Canonical</td></tr>
 * <tr><td>Non-canonical</td></tr>
 * <tr><td>PubChem Fingerprint</td></tr>
 * </table></td>
 * </tr>
 * <tr>
 * <td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=1909">1909</a></td>
 * <td><img src="http://pubchem.ncbi.nlm.nih.gov/image/imgsrv.fcgi?cid=1909" alt="Compound Image"></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>{5, 5, 5, 6, <b style="color: #FF4444;"><u>9</u></b>, 16, 17, 17,
 * 17,
 * 18}</td></tr>
 * <tr><td>{5, 5, 5, 6, 9, 16, 17, 17, 17, 18}</td></tr>
 * <tr><td>{5, 5, 5, 6}</td></tr>
 * </table></td>
 * <td><table style="font-family: courier; font-size: 9pt; color: #666666;"><caption></caption>
 * <tr><td>Canonical - <i>9 member cycle only added if found before larger
 * cycles</i></td></tr>
 * <tr><td>Non-canonical</td></tr>
 * <tr><td>PubChem Fingerprint - <i>9 member cycle not found</i></td></tr>
 * </table></td>
 * </tr>
 * </table>
 *
 * @author John May
 * @cdk.module core
 * @cdk.keyword ESSSR
 * @cdk.keyword ring
 * @cdk.keyword cycle
 * @see MinimumCycleBasis
 * @see RelevantCycles
 * @cdk.githash
 */
public final class TripletShortCycles {

    /** Adjacency list representation of the graph. */
    private final int[][]   graph;

    /**
     * Whether the basis should be canonical. By definition a canonical set
     * depends on the atom order.
     */
    private final boolean   canonical;

    /** The current cycle basis. */
    private final Set<Path> basis = new TreeSet<Path>();

    /**
     * Compute the cycles of the extended smallest set of smallest rings (ESSSR)
     * for an existing minimum cycle basis. Choosing the set to be canonical
     * means the set depends on the order of the vertices and may <b>not</b> be
     * consistent in subgraphs. Given a different order of vertices the same
     * cycles may not be found.
     *
     * @param mcb       minimum cycle basis
     * @param canonical should the set be canonical (non-unique)
     */
    public TripletShortCycles(final MinimumCycleBasis mcb, final boolean canonical) {

        // don't reorder neighbors as the MCB was already done on this ordering
        this.graph = copy(mcb.graph);
        this.canonical = canonical;

        // all minimum cycle basis paths belong to the set
        for (final int[] path : mcb.paths())
            basis.add(new Path(Arrays.copyOf(path, path.length - 1)));

        // count the number of cycles each vertex belongs to and try to find a
        // cycle though the triple of 'v' and two of it's neighbors
        final int ord = graph.length;
        final int[] nCycles = nCycles(basis, ord);
        for (int v = 0; v < ord; v++) {
            if (nCycles[v] > 1) findTriple(v);
        }
    }

    /**
     * Access the vertex paths for all cycles of the basis.
     *
     * @return paths of the basis
     */
    public int[][] paths() {
        int i = 0;
        int[][] paths = new int[size()][];

        for (final Path path : basis)
            paths[i++] = path.toArray();

        return paths;
    }

    /**
     * Size of the cycle basis, cardinality of the ESSSR.
     *
     * @return number of cycles in the basis
     */
    public int size() {
        return basis.size();
    }

    /**
     * Try and find cycles through the triple formed from <i>v</i> and any two
     * of it's neighbours.
     *
     * @param v a vertex in the graph
     */
    private void findTriple(final int v) {

        int[] ws = graph[v];
        int deg = ws.length;

        // disconnect 'v' from its neighbors 'ws'
        disconnect(ws, v);

        // for every pair of neighbors (u,w) connected to v try and find the
        // shortest path that doesn't travel through 'v'. If a path can be found
        // this is the shortest cycle through the three vertices '-u-v-w-'
        // where u = ws[i] and w = ws[j]
        for (int i = 0; i < deg; i++) {

            ShortestPaths sp = new ShortestPaths(graph, null, ws[i]);

            for (int j = i + 1; j < deg; j++) {

                // ignore if there is an exciting cycle through the the triple
                if (canonical && exists(ws[i], v, ws[j])) continue;

                // if there is a path between u and w, form a cycle by appending
                // v and storing in the basis
                if (sp.nPathsTo(ws[j]) > 0) {

                    // canonic, use the a shortest path (dependant on vertex
                    // order) - non-canonic, use all possible shortest paths
                    int[][] paths = canonical ? new int[][]{sp.pathTo(ws[j])} : sp.pathsTo(ws[j]);
                    for (int[] path : paths)
                        basis.add(new Path(append(path, v)));
                }
            }
        }

        reconnect(ws, v);
    }

    /**
     * Is there a cycle already in the basis in which vertices <i>u</i>,
     * <i>v</i> and <i>w</i> can be found in succession.
     *
     * @param u a vertex adjacent to <i>v</i>
     * @param v a vertex adjacent to <i>u</i> and <i>w</i>
     * @param w a vertex adjacent to <i>v</i>
     * @return whether a member of basis contains -u-v-w- in succession
     */
    private boolean exists(final int u, final int v, final int w) {
        for (final Path path : basis) {
            if (path.contains(u, v, w)) return true;
        }
        return false;
    }

    /**
     * Temporarily disconnect <i>v</i> from the <i>graph</i> by forming loops
     * for each of it's neighbours, <i>ws</i>. A loop is an edge in which both
     * end points are the. Technically <i>v</i> is never removed but we can't
     * reach <i>v</i> from any other vertex which is sufficient to trace the
     * triple cycles using {@link ShortestPaths}.
     *
     * @param ws vertices adjacent to <i>v</i>
     * @param v  a vertex <i>v</i>
     * @see #reconnect(int[], int)
     */
    private void disconnect(final int[] ws, final int v) {
        for (final int w : ws) {
            final int deg = graph[w].length;
            for (int i = 0; i < deg; i++) {
                if (graph[w][i] == v) graph[w][i] = w;
            }
        }
    }

    /**
     * Reconnect <i>v</i> with the <i>graph</i> by un-looping each of it's
     * neighbours, <i>ws</i>.
     *
     * @param ws vertices adjacent to <i>v</i>
     * @param v  a vertex <i>v</i>
     * @see #disconnect(int[], int)
     */
    private void reconnect(final int[] ws, final int v) {
        for (final int w : ws) {
            final int deg = graph[w].length;
            for (int i = 0; i < deg; i++) {
                if (graph[w][i] == w) graph[w][i] = v;
            }
        }
    }

    /**
     * Append the vertex <i>v</i> to the end of the path <i>p</i>.
     *
     * @param p a path
     * @param v a vertex to append
     * @return the path with v appended
     */
    private static int[] append(final int[] p, final int v) {
        final int[] q = Arrays.copyOf(p, p.length + 1);
        q[p.length] = v;
        return q;
    }

    /**
     * Count how many cycles each vertex belongs to in the given basis.
     *
     * @param basis current basis
     * @param ord   order of the graph
     */
    private static int[] nCycles(final Iterable<Path> basis, int ord) {

        final int[] nCycles = new int[ord];

        for (final Path path : basis)
            for (final int v : path.vertices)
                nCycles[v]++;

        return nCycles;
    }

    /**
     * Transform the cycle to that of lowest lexicographic rank. For example the
     * paths {3,2,1,0} , {3,0,1,2} and {2,1,0,3} are all the same and in the
     * lexicographic order are {0,1,2,3}.
     *
     * @param p path forming a simple cycle
     * @return path of lowest rank
     */
    static int[] lexicographic(final int[] p) {

        // find min value (new start vertex)
        int off = min(p);
        int len = p.length;

        // if proceeding value in cycle > preceding value in cycle... reverse
        boolean rev = p[(off + 1) % len] > p[(len + off - 1) % len];

        int[] q = new int[len];

        // copy data offset by the min into 'q', reverse if needed
        if (rev) {
            for (int i = 0; i < len; i++)
                q[(len - i) % len] = p[(off + i) % len];
        } else {
            for (int i = 0; i < len; i++)
                q[i] = p[(off + i) % len];
        }

        return q;
    }

    /**
     * Find the index of lowest value in array.
     *
     * @param xs array of integers
     * @return minimum value
     */
    private static int min(final int[] xs) {
        int min = 0;
        for (int i = 0; i < xs.length; i++) {
            if (xs[i] < xs[min]) min = i;
        }
        return min;
    }

    /**
     * Copy the graph <i>g</i>.
     *
     * @param g graph
     * @return copy of the graph
     */
    private static int[][] copy(final int[][] g) {

        int ord = g.length;
        int[][] h = new int[ord][];

        for (int v = 0; v < ord; v++)
            h[v] = Arrays.copyOf(g[v], g[v].length);

        return h;
    }

    /**
     * Simple wrapper class for a path of vertices (specified as an int[]). The
     * class provides comparison with other paths. This is required as the
     * algorithm can generate the same cycle more then once, and so the cycles
     * must be stored in a {@link Set}.
     */
    private static class Path implements Comparable<Path> {

        /** Path of vertices. */
        private int[] vertices;

        /**
         * Create a new path from the given vertices.
         *
         * @param vertices vertices
         */
        private Path(final int[] vertices) {
            this.vertices = lexicographic(vertices);
        }

        /**
         * Does this path contain the vertices <i>u</i>, <i>v</i> and <i>w</i>
         * in succession.
         *
         * @param u a vertex connected to <i>v</i>
         * @param v a vertex connected to <i>u</i> and <i>w</i>
         * @param w a vertex connected to <i>v</i>
         * @return whether the path contains the triple in succession
         */
        private boolean contains(final int u, final int v, final int w) {
            int len = vertices.length;
            for (int i = 0; i < len; i++) {
                if (vertices[i] == v) {
                    // check the next and previous vertices
                    int next = vertices[(i + 1) % len];
                    int prev = vertices[(len + i - 1) % len];
                    return (prev == u && next == w) || (prev == w && next == u);
                }
            }
            return false;
        }

        /**
         * Length of the path.
         *
         * @return length of the path
         */
        private int len() {
            return vertices.length;
        }

        /**
         * The path as an array of vertices.
         *
         * @return array of vertices
         */
        private int[] toArray() {
            int[] p = Arrays.copyOf(vertices, len() + 1);
            p[len()] = p[0]; // closed walk
            return p;
        }

        /**{@inheritDoc} */
        @Override
        public int compareTo(final Path that) {
            if (this.len() > that.len()) return +1;
            if (this.len() < that.len()) return -1;
            for (int i = 0; i < len(); i++) {
                if (this.vertices[i] > that.vertices[i]) return +1;
                if (this.vertices[i] < that.vertices[i]) return -1;
            }
            return 0;
        }
    }
}
