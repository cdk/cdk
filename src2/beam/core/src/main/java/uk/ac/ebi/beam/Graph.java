/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static uk.ac.ebi.beam.Element.Hydrogen;

/**
 * Defines a labelled graph with atoms as vertex labels and bonds as edge
 * labels. Topological information around atoms can also be stored.
 *
 * @author John May
 */
public final class Graph {

    /**
     * Indicate the graph has one or aromatic atoms.
     */
    public static final int HAS_AROM     = 0x1;
    
    public static final int HAS_ATM_STRO = 0x2;
    
    // extended stereo (across multiple atoms) e.g. @AL1/@Al2
    public static final int HAS_EXT_STRO = 0x4;

    public static final int HAS_BND_STRO = 0x8;

    public static final int HAS_STRO = HAS_ATM_STRO |  HAS_EXT_STRO | HAS_BND_STRO;

    /** The vertex labels, atoms. */
    private Atom[] atoms;

    private int[] degrees;

    private int[] valences;

    /** Incidence list storage of edges with attached bond labels. * */
    private Edge[][] edges;

    /** Topologies indexed by the atom which they describe. */
    private Topology topologies[];

    /** Vertex and edge counts. */
    private int order, size;

    /** Molecule flags. */
    private int flags = 0;
    
    /** Molecule title. */
    private String title;

    /**
     * Create a new chemical graph with expected size.
     *
     * @param expSize expected size
     */
    Graph(int expSize) {
        this.order = 0;
        this.size = 0;
        this.edges = new Edge[expSize][];
        for (int i = 0; i < expSize; i++)
            edges[i] = new Edge[4];
        this.atoms = new Atom[expSize];
        this.degrees = new int[expSize];
        this.valences = new int[expSize];
        this.topologies = new Topology[expSize];
    }

    /**
     * Copy constructor.
     *
     * @param org original graph
     */
    Graph(Graph org) {
        this.order      = org.order;
        this.size       = org.size;
        this.flags      = org.flags;
        this.atoms      = Arrays.copyOf(org.atoms, order);
        this.valences   = Arrays.copyOf(org.valences, order);
        this.degrees    = new int[order];
        this.edges      = new Edge[order][];
        this.topologies = Arrays.copyOf(org.topologies, org.topologies.length);
        
        for (int u = 0; u < order; u++) {
            final int deg = org.degrees[u];
            this.edges[u] = new Edge[deg];
            for (int j = 0; j < deg; ++j) {
                final Edge e = org.edges[u][j];
                final int  v = e.other(u);
                // important - we have made use edges are allocated
                if (u > v) {
                    Edge f = new Edge(e);
                    edges[u][degrees[u]++] = f;
                    edges[v][degrees[v]++] = f;
                }
            }
        }
    }

    /**
     * (internal) - set the atom label at position 'i'.
     *
     * @param i index
     * @param a atom
     */
    void setAtom(int i, Atom a) {
        atoms[i] = a;
    }

    /** Resize the graph if we are at maximum capacity. */
    private void ensureCapacity() {
        if (order >= atoms.length) {
            atoms      = Arrays.copyOf(atoms, order * 2);
            valences   = Arrays.copyOf(valences, order * 2);
            degrees    = Arrays.copyOf(degrees, order * 2);
            edges      = Arrays.copyOf(edges, order * 2);
            topologies = Arrays.copyOf(topologies, order * 2);
            for (int i = order; i < edges.length; i++)
                edges[i] = new Edge[4];
        }
    }

    /**
     * Add an atom to the graph and return the index to which the atom was added.
     *
     * @param a add an atom
     * @return index of the atom in the graph (vertex)
     */
    int addAtom(Atom a) {
        ensureCapacity();
        atoms[order++] = a;
        return order - 1;
    }

    /**
     * Access the atom at the specified index.
     *
     * @param i index of the atom to access
     * @return the atom at that index
     */
    public Atom atom(int i) {
        return atoms[i];
    }

    /**
     * Add an labelled edge to the graph.
     *
     * @param e new edge
     */
    void addEdge(Edge e) {
        int u = e.either(), v = e.other(u);
        ensureEdgeCapacity(u);
        ensureEdgeCapacity(v);
        edges[u][degrees[u]++] = e;
        edges[v][degrees[v]++] = e;
        int ord = e.bond().order();
        valences[u] += ord;
        valences[v] += ord;
        size++;
    }
    
    private void ensureEdgeCapacity(int i) {
        if (degrees[i] == edges[i].length)
            edges[i] = Arrays.copyOf(edges[i], degrees[i] + 2);
    }

    /**
     * Access the degree of vertex 'u'.
     *
     * @param u a vertex
     * @return the degree of the specified vertex
     */
    public int degree(int u) {
        return degrees[u];
    }

    /**
     * Access the bonded valence of vertex 'u'. This valence exclude any implicit hydrogen counts.
     *
     * @param u a vertex index
     * @return the bonded valence of the specified vertex
     */
    int bondedValence(int u) {
        return valences[u];
    }

    void updateBondedValence(int i, int x) {
        valences[i] += x;
    }

    /**
     * Access the edges of which vertex 'u' is an endpoint.
     *
     * @param u a vertex
     * @return edges incident to 'u'
     */
    public List<Edge> edges(int u) {
        return Arrays.asList(Arrays.copyOf(edges[u], degrees[u]));
    }

    /**
     * Access the vertices adjacent to 'u' in <b>sorted</b> order. This
     * convenience method is provided to assist in configuring atom-based stereo
     * using the {@link #configurationOf(int)} method. For general purpose
     * access to the neighbors of a vertex the {@link #edges(int)} is
     * preferred.
     *
     * @param u a vertex
     * @return fixed-size array of vertices
     * @see #configurationOf(int)
     */
    public int[] neighbors(int u) {
        List<Edge> es = edges(u);
        int[] vs = new int[es.size()];
        int deg = es.size();
        for (int i = 0; i < deg; i++)
            vs[i] = es.get(i).other(u);
        Arrays.sort(vs);
        return vs;
    }

    /**
     * Determine if the vertices 'u' and 'v' are adjacent and there is an edge
     * which connects them.
     *
     * @param u a vertex
     * @param v another vertex
     * @return whether they are adjacent
     */
    public boolean adjacent(int u, int v) {
        final int d = degrees[u];
        for (int j = 0; j < d; ++j) {
            Edge e = edges[u][j];
            if (e.other(u) == v)
                return true;
        }
        return false;
    }

    /**
     * The number of implied (or labelled) hydrogens for the vertex 'u'. Note
     * the count does not include any bonded vertices which may also be
     * hydrogen.
     *
     * @param u the vertex to access the implicit h count for.
     * @return the number of implicit hydrogens
     */
    public int implHCount(int u) {
        return atom(u).hydrogens(this, u);
    }

    /**
     * Access the edge connecting two adjacent vertices.
     *
     * @param u a vertex
     * @param v another vertex (adjacent to u)
     * @return the edge connected u and v
     * @throws IllegalArgumentException u and v are not adjacent
     */
    public Edge edge(int u, int v) {
        final int d = degrees[u];
        for (int j = 0; j < d; ++j) {
            Edge e = edges[u][j];
            if (e.other(u) == v)
                return e;
        }
        throw new IllegalArgumentException(u + ", " + v + " are not adjacent");
    }

    public Edge edgeAt(int u, int j) {
        return edges[u][j];
    }

    /**
     * Replace an edge in the graph.
     *
     * @param org the original edge
     * @param rep the replacement
     */
    void replace(Edge org, Edge rep) {

        int u = org.either();
        int v = org.other(u);

        for (int i = 0; i < degrees[u]; i++) {
            if (edges[u][i] == org) {
                edges[u][i] = rep;
            }
        }

        for (int i = 0; i < degrees[v]; i++) {
            if (edges[v][i] == org) {
                edges[v][i] = rep;
            }
        }

        int ord = rep.bond().order() - org.bond().order();
        valences[u] += ord;
        valences[v] += ord;
    }

    /**
     * Add a topology description to the graph. The topology describes the configuration around a
     * given atom.
     *
     * @param t topology
     * @return whether the topology replaced an existing configuration
     */
    void addTopology(Topology t) {
        if (t != null && t != Topology.unknown())
            topologies[t.atom()] = t;
    }

    void clearTopology(int v) {
        topologies[v] = null;
    }

    /**
     * Access the topology of the vertex 'u'. If no topology is defined then
     * {@link Topology#unknown()} is returned.
     *
     * @param u a vertex to access the topology of
     * @return the topology of vertex 'u'
     */
    public Topology topologyOf(int u) {
        if (topologies[u] == null)
            return Topology.unknown();
        return topologies[u];
    }

    /**
     * Provides the stereo-configuration of the atom label at vertex 'u'. The
     * configuration describes the relative-stereo as though the atoms were
     * arranged by atom number. <br><br>
     *
     * <b>Further Explanation for Tetrahedral Centres</b> As an example the
     * molecule {@code O[C@]12CCCC[C@@]1(O)CCCC2} has two tetrahedral centres.
     * <br> 1. The first one is on vertex '1' and looking from vertex '0' the
     * other neighbors [6, 11, 2] proceed anti-clockwise ('@') - note ring
     * bonds. It is easy to see that if we use the natural order of the molecule
     * and order the neighbor [2, 6, 11] the winding is still anti-clockwise and
     * '@TH1' is returned. 2. The second centre is on vertex '6' and looking
     * from vertex '5' the ordering proceeds as [1, 7, 8] with clockwise
     * winding. When we arrange the atoms by their natural order we will now be
     * looking from vertex '1' as it is the lowest. The other neighbors then
     * proceed in the order [5, 7, 8]. Drawing out the configuration it's clear
     * that we look from vertex '1' instead of '5' the winding is now
     * anti-clockwise and the configuration is also '@TH1'.
     *
     * @param u a vertex in the graph
     * @return The configuration around
     */
    public Configuration configurationOf(int u) {

        Topology t = topologyOf(u);

        if (t == Topology.unknown())
            return t.configuration();

        // identity permutation
        int[] p = new int[order];
        for (int i = 0; i < order; i++)
            p[i] = i;

        return t.orderBy(p).configuration();
    }

    /**
     * The order is the number vertices in the graph, |V|.
     *
     * @return number of vertices
     */
    public int order() {
        return order;
    }

    /**
     * The size is the number edges in the graph, |E|.
     *
     * @return number of edges
     */
    public int size() {
        return size;
    }

    /**
     * Convenience method to create a graph from a provided SMILES string.
     *
     * @param smi string containing SMILES line notation.
     * @return graph instance from the SMILES
     * @throws InvalidSmilesException thrown if there was a syntax error while
     *                                parsing the SMILES.
     */
    public static Graph fromSmiles(String smi) throws
                                               InvalidSmilesException {
        if (smi == null)
            throw new NullPointerException("no SMILES provided");
        Parser parser = new Parser(CharBuffer.fromString(smi), false);
        for (String warn : parser.getWarnings()) {
            for (String line : warn.split("\n"))
              System.err.println("SMILES Warning: " + line);
        }
        return parser.molecule();
    }

    public static Graph parse(String smi, boolean strict, Set<String> warnings) throws InvalidSmilesException {
        if (smi == null)
            throw new NullPointerException("no SMILES provided");
        Parser parser = new Parser(CharBuffer.fromString(smi), strict);
        warnings.addAll(parser.getWarnings());
        return parser.molecule();
    }

    /**
     * Convenience method to write a SMILES string for the current configuration
     * of the molecule.
     *
     * @return the SMILES string for the molecule.
     * @throws IOException a SMILES string could not be generated
     */
    public String toSmiles() throws IOException {
        return Generator.generate(this);
    }

    /**
     * Generate a SMILES for the Graph. The {@code visitedAt} is filled with
     * the output rank of each vertex in the graph. This allows one to know
     * the atom index when the SMILES in read in.
     * 
     * @param visitedAt vector to be filled with the output order
     * @return the SMILES string
     * @throws IOException a SMILES string could not be generated
     */
    public String toSmiles(int[] visitedAt) throws IOException {
        return Generator.generate(this, visitedAt);
    }

    /**
     * Delocalise a kekulé graph representation to one with <i>aromatic</i>
     * bonds. The original graph remains unchanged.
     *
     * TODO: more explanation
     *
     * @return aromatic representation
     */
    public Graph aromatic() {
        // note Daylight use SSSR - should update and use that by default but
        // provide the AllCycles method
        try {
            return AllCycles.daylightModel(this).aromaticForm();
        } catch (IllegalArgumentException e) {
            // too many cycles - use a simpler model which only allows rings of
            // size 6 (catches fullerenes)
            return AllCycles.daylightModel(this, 6).aromaticForm();
        }
    }

    /**
     * Resonate bond assignments in conjugate rings such that two SMILES with
     * the same ordering have the same kekulé assignment.
     * 
     * @return (self) - the graph is mutated
     */
    public Graph resonate() {
        return Localise.resonate(this);
    }

    /**
     * Localise delocalized (aromatic) bonds in this molecule producing the
     * Kekulé form. The original graph <b>is</b> modified.
     *
     * <blockquote><pre>
     * Graph furan        = Graph.fromSmiles("o1cccc1").kekule();
     * </pre></blockquote>
     *
     * If the graph could not be converted to a kekulé representation then a
     * checked exception is thrown. Graphs cannot be converted if their
     * structures are erroneous and there is no valid way to assign the
     * delocalised electrons. <br>
     *
     * Some reasons are shown below.
     *
     * <blockquote><pre>
     * n1cncc1             imidazole (incorrect) C1C=NC=N1 or N1C=CN=C1?
     * n1c[nH]cc1          imidazole (correct)
     *
     * [Hg+2][c-]1ccccc1   mercury(2+) ion benzenide (incorrect)
     * [Hg+2].[c-]1ccccc1  mercury(2+) ion benzenide (correct)
     * </pre></blockquote>
     *
     * @return kekulé representation
     * @throws InvalidSmilesException molecule exploded on contact with reality
     */
    public Graph kekule() throws InvalidSmilesException {
        return Localise.localiseInPlace(this);
    }


    /**
     * Verify that electrons can be assigned to any delocalised (aromatic)
     * bonds. This method is faster than doing a full kekulisation and allows
     * versification of aromatic structures without localising the bond orders.
     * However the method of determining the Kekulé structure is very similar
     * and often is preferable to provide a molecule with defined bond orders.
     *
     * @return electrons can be assigned
     * @see #kekule()
     */
    public boolean assignable() {
        return ElectronAssignment.verify(this);
    }

    /**
     * Permute the vertices of a graph using a given permutation.
     *
     * <blockquote><pre>
     * g = CNCO
     * h = g.permuate(new int[]{1, 0, 3, 2});
     * h = NCOC
     * </pre></blockquote>
     *
     * @param p a permutation mapping indicate the new index of each atom
     * @return a new chemical graph with the vertices permuted by the given
     *         ordering
     */
    public Graph permute(int[] p) {

        if (p.length != order)
            throw new IllegalArgumentException("permuation size should equal |V| (order)");

        Graph cpy = new Graph(order);
        cpy.flags = flags;
        cpy.order = order;
        cpy.size = size;

        for (int u = 0; u < order; u++) {
            int d = degrees[u];
            // v is the image of u in the permutation
            final int v = p[u];
            if (d > 4) cpy.edges[v] = new Edge[d];
            cpy.atoms[v]    = atoms[u];
            cpy.valences[v] = valences[u];
            cpy.addTopology(topologyOf(u).transform(p));
            while (--d >= 0) {
                final Edge e = edgeAt(u, d);

                // important this is the second time we have seen the edge
                // so the capacity must have been allocated. otherwise we
                // would get an index out of bounds
                if (u > e.other(u)) {
                    // w is the image of vertex adjacen to u
                    final int  w = p[e.other(u)];
                    final Edge f = new Edge(v, w, e.bond(u));
                    cpy.edges[v][cpy.degrees[v]++] = f;
                    cpy.edges[w][cpy.degrees[w]++] = f;
                    cpy.size++;
                }
            }
        }

        // ensure edges are in sorted order
        return cpy.sort(new CanOrderFirst());
    }

    /**
     * Access the atoms of the chemical graph.
     *
     * <blockquote><pre>
     * for (Atom a : g.atoms()) {
     *
     * }
     * </pre></blockquote>
     *
     * @return iterable of atoms
     */
    public Iterable<Atom> atoms() {
        return Arrays.asList(atoms).subList(0, order);
    }

    /**
     * Access the edges of the chemical graph.
     *
     * @return iterable of edges
     */
    public Iterable<Edge> edges() {
        List<Edge> es = new ArrayList<Edge>(size);
        for (int u = 0; u < order; u++) {
            final int d = degrees[u];
            for (int i = 0; i < d; ++i) {
                final Edge e = edges[u][i];
                if (e.other(u) < u)
                    es.add(e);
            }
        }
        return Collections.unmodifiableCollection(es);
    }

    /**
     * Apply a function to the chemical graph.
     *
     * @param f   a function which transforms a graph into something.
     * @param <T> output type of the function
     * @return the output of the function
     */
    <T> T apply(Function<Graph, T> f) throws Exception {
        return f.apply(this);
    }

    void clear() {
        Arrays.fill(topologies, Topology.unknown());
        for (int i = 0; i < order; i++) {
            atoms[i] = null;
            degrees[i] = 0;
        }
        order = 0;
        size = 0;
    }

    public int getFlags(final int mask) {
        return this.flags & mask;
    }

    public int getFlags() {
        return this.flags;
    }

    void addFlags(final int mask) {
        this.flags = flags | mask;
    }

    void setFlags(final int flags) {
        this.flags = flags;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }

    /**
     * Sort the edges of the graph to visit in a specific order. The graph is
     * modified.
     * 
     * @param comparator ordering on edges
     * @return the graph
     */
    public Graph sort(EdgeComparator comparator) {
        for (int u = 0; u < order; u++) {
            final Edge[] es = edges[u];

            // insertion sort as most atoms have small degree <= 4
            final int deg = degrees[u];
            for (int i = 1; i < deg; i++) {
                int j = i - 1;
                Edge e = es[i];
                while (j >= 0 && comparator.less(this, u, e, es[j])) {
                    es[j + 1] = es[j--];
                }
                es[j + 1] = e;
            }
        }
        return this;
    }

    /**
     * Defines a method for arranging the neighbors of an atom.
     */
    public static interface EdgeComparator {

        /**
         * Should the edge, e, be visited before f.
         * 
         * @param g graph
         * @param u the atom we are sorting from
         * @param e an edge adjacent to u
         * @param f an edge adjacent to u
         * @return edge e is less than edge f
         */
        boolean less(Graph g, int u, Edge e, Edge f);
    }

    /**
     * Sort the neighbors of each atom such that hydrogens are visited first and
     * deuterium before tritium. 
     */
    public static final class VisitHydrogenFirst implements EdgeComparator {

        /**
         * {@inheritDoc}
         */
        @Override public boolean less(Graph g, int u, Edge e, Edge f) {
            
            int v = e.other(u);
            int w = f.other(u);
            
            Element vElem = g.atom(v).element();
            Element wElem = g.atom(w).element();
            
            if (vElem == Hydrogen && wElem != Hydrogen)
                return true;
            if (vElem != Hydrogen && wElem == Hydrogen)
                return false;
            
            // sort hydrogens by isotope
            return vElem == Hydrogen && g.atom(v).isotope() < g.atom(w).isotope();
        }
    }

    /**
     * Visit high order bonds before low order bonds.
     */
    public static final class VisitHighOrderFirst implements EdgeComparator {
        
        /**
         * {@inheritDoc}
         */
        @Override public boolean less(Graph g, int u, Edge e, Edge f) {
            return e.bond().order() > f.bond().order();
        }
    }

    /**
     * Arrange neighbors in canonical order.
     */
    static final class CanOrderFirst implements EdgeComparator {

        /**
         * @inheritDoc
         */
        @Override public boolean less(Graph g, int u, Edge e, Edge f) {
            return e.other(u) < f.other(u);
        }
    }
}
