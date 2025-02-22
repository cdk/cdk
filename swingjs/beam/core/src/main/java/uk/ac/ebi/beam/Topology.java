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

import java.util.Arrays;
import java.util.List;

import static uk.ac.ebi.beam.Configuration.AL1;
import static uk.ac.ebi.beam.Configuration.AL2;
import static uk.ac.ebi.beam.Configuration.ANTI_CLOCKWISE;
import static uk.ac.ebi.beam.Configuration.CLOCKWISE;
import static uk.ac.ebi.beam.Configuration.DB1;
import static uk.ac.ebi.beam.Configuration.DB2;
import static uk.ac.ebi.beam.Configuration.OH1;
import static uk.ac.ebi.beam.Configuration.OH2;
import static uk.ac.ebi.beam.Configuration.SP1;
import static uk.ac.ebi.beam.Configuration.SP2;
import static uk.ac.ebi.beam.Configuration.SP3;
import static uk.ac.ebi.beam.Configuration.TB1;
import static uk.ac.ebi.beam.Configuration.TB2;
import static uk.ac.ebi.beam.Configuration.TH1;
import static uk.ac.ebi.beam.Configuration.TH2;
import static uk.ac.ebi.beam.Configuration.Type.DoubleBond;
import static uk.ac.ebi.beam.Configuration.Type.ExtendedTetrahedral;
import static uk.ac.ebi.beam.Configuration.Type.Implicit;
import static uk.ac.ebi.beam.Configuration.Type.SquarePlanar;
import static uk.ac.ebi.beam.Configuration.Type.Tetrahedral;
import static uk.ac.ebi.beam.Configuration.Type.TrigonalBipyramidal;

/**
 * Defines the relative topology around a vertex (atom).
 *
 * @author John May
 */
abstract class Topology {

    /**
     * The vertex/atom which this topology describes.
     *
     * @return vertex
     * @throws IllegalArgumentException unknown topology
     */
    abstract int atom();

    /**
     * The configuration of the topology.
     *
     * @return configuration for this topology
     */
    abstract Configuration configuration();

    /**
     * The configuration of the topology when it's carriers have the specified
     * ranks.
     *
     * @return configuration for this topology
     */
    Configuration configurationOf(int[] rank) {
        Topology topology = orderBy(rank);
        return topology != null ? topology.configuration() : Configuration.UNKNOWN;
    }

    /**
     * What type of configuration is defined by this topology (e.g. Tetrahedral,
     * DoubleBond etc).
     *
     * @return the type of the configuration
     */
    Configuration.Type type() {
        return configuration().type();
    }

    /**
     * Arrange the topology relative to a given ranking of vertices.
     *
     * @param rank ordering of vertices
     * @return a new topology with the neighbors arranged by the given rank
     */
    abstract Topology orderBy(int[] rank);

    /**
     * Transform the topology to one with the given {@literal mapping}.
     *
     * @param mapping the mapping used to transform the topology
     * @return a new topology with it's vertices mapped
     */
    abstract Topology transform(int[] mapping);
    
    abstract void copy(int[] dest);

    /**
     * Compute the permutation parity of the vertices {@literal vs} for the
     * given {@literal rank}. The parity defines the oddness or evenness of a
     * permutation and is the number of inversions (swaps) one would need to
     * make to place the 'vs' in the order specified by rank.
     *
     * @param vs   array of vertices
     * @param rank rank of vertices, |R| = max(vs) + 1
     * @return sign of the permutation, -1=odd or 1=even
     * @see <a href="http://en.wikipedia.org/wiki/Parity_of_a_permutation>Parity
     *      of a Permutation</a>
     */
    static int parity(int[] vs, int[] rank) {
        // count elements which are out of order and by how much
        int count = 0;
        for (int i = 0; i < vs.length; i++) {
            for (int j = i + 1; j < vs.length; j++) {
                if (rank[vs[i]] > rank[vs[j]])
                    count++;
            }
        }
        // odd parity = -1, even parity = 1
        return (count & 0x1) == 1 ? -1 : 1;
    }

    // help the compiler, array is a fixed size!
    static int parity4(int[] vs, int[] rank) {
        // count elements which are out of order and by how much
        int count = 0;
        for (int i = 0; i < 4; i++) {
            final int prev = rank[vs[i]];
            for (int j = i + 1; j < 4; j++) {
                if (prev > rank[vs[j]])
                    count++;
            }
        }
            
        // odd parity = -1, even parity = 1
        return (count & 0x1) == 1 ? -1 : 1;
    }

    /**
     * Sorts the array {@literal vs} into the order given by the {@literal
     * rank}.
     *
     * @param vs   vertices to sort
     * @param rank rank of vertices
     * @return sorted array (cpy of vs)
     */
    static int[] sort(int[] vs, int[] rank) {
        int[] ws = Arrays.copyOf(vs, vs.length);

        // insertion sort using rank for the ordering
        for (int i = 0, j = i; i < vs.length - 1; j = ++i) {
            int v = ws[i + 1];
            while (rank[v] < rank[ws[j]]) {
                ws[j + 1] = ws[j];
                if (--j < 0)
                    break;
            }
            ws[j + 1] = v;
        }
        return ws;
    }

    /**
     * Specify unknown configuration on atom - there is no vertex data stored.
     *
     * @return unknown topology
     */
    static Topology unknown() {
        return UNKNOWN;
    }

    /**
     * Define tetrahedral topology of the given configuration.
     *
     * @param u             central atom
     * @param vs            vertices surrounding u, the first is the vertex we
     *                      are looking from
     * @param configuration the tetrahedral configuration, @TH1, @TH2, @ or @@
     * @return topology instance for that configuration
     * @see Configuration
     */
    static Topology tetrahedral(int u, int[] vs, Configuration configuration) {

        if (configuration.type() != Implicit
                && configuration.type() != Tetrahedral)
            throw new IllegalArgumentException(configuration.type()
                                                       + "invalid tetrahedral configuration");

        int p = configuration.shorthand() == CLOCKWISE ? 1 : -1;

        return new Tetrahedral(u,
                               Arrays.copyOf(vs, vs.length),
                               p);
    }
    
    static Topology extendedTetrahedral(int u, int[] vs, Configuration configuration) {

        if (configuration.type() != Implicit
                && configuration.type() != ExtendedTetrahedral)
            throw new IllegalArgumentException(configuration.type()
                                                       + "invalid extended tetrahedral configuration");

        int p = configuration.shorthand() == CLOCKWISE ? 1 : -1;

        return new ExtendedTetrahedral(u,
                                       Arrays.copyOf(vs, vs.length),
                                       p);
    }

    /**
     * Define trigonal topology of the given configuration.
     *
     * @param u             central atom
     * @param vs            vertices surrounding u, the first is the vertex we
     *                      are looking from
     * @param configuration the trigonal configuration, @DB1, @Db1, @ or @@
     * @return topology instance for that configuration
     * @see Configuration
     */
    static Topology trigonal(int u, int[] vs, Configuration configuration) {

        if (configuration.type() != Implicit
                && configuration.type() != DoubleBond)
            throw new IllegalArgumentException(configuration.type()
                                                       + "invalid tetrahedral configuration");

        int p = configuration.shorthand() == CLOCKWISE ? 1 : -1;

        return new Trigonal(u,
                            Arrays.copyOf(vs, vs.length),
                            p);
    }

    static Topology squarePlanar(int u, int[] vs, Configuration configuration) {
        switch (configuration) {
            case SP1:
                return new SquarePlanar(u,
                                        Arrays.copyOf(vs, vs.length),
                                        1);
            case SP2:
                return new SquarePlanar(u,
                                        Arrays.copyOf(vs, vs.length),
                                        2);
            case SP3:
                return new SquarePlanar(u,
                                        Arrays.copyOf(vs, vs.length),
                                        3);
            default:
                return null;
        }
    }

    static Topology trigonalBipyramidal(int u, int[] vs, Configuration c) {
        if (Configuration.TB1.ordinal() <= c.ordinal() &&
                Configuration.TB20.ordinal() >= c.ordinal()) {
            int order = 1 + c.ordinal() - Configuration.TB1.ordinal();
            return new TrigonalBipyramidal(u, vs, order);
        }
        return null;
    }

    static Topology octahedral(int u, int[] vs, Configuration c) {
        if (Configuration.OH1.ordinal() <= c.ordinal() &&
                Configuration.OH30.ordinal() >= c.ordinal()) {
            int order = 1 + c.ordinal() - Configuration.OH1.ordinal();
            return new Octahedral(u, vs, order);
        }
        return null;
    }

    /**
     * Convert an implicit configuration ('@' or '@@') c, to an explicit one
     * (e.g. @TH1).
     *
     * <blockquote><pre>
     * Implicit Valence Explicit Example
     *
     * @param g chemical graph
     * @param u the atom to which the configuration is associated
     * @param c implicit configuration ({@link Configuration#ANTI_CLOCKWISE or
     *          Configuration#CLOCKWISE})
     * @return an explicit configuration or {@link Configuration#UNKNOWN}
     * @ 4       @TH1     O[C@H](N)C or O[C@]([H])(N)C
     * @@ 4       @TH2     O[C@@H](N)C or O[C@@]([H])(N)C
     * @ 3       @TH1     C[S@](N)=O
     * @@ 3       @TH2     C[S@@](N)=O
     * @ 2       @AL1     OC=[C@]=CO
     * @ 2       @AL2     OC=[C@@]=CO
     * @ 5       @TB1     S[As@](F)(Cl)(Br)C=O
     * @@ 5       @TB2     S[As@@](F)(Cl)(Br)C=O
     * @ 5       @OH1     S[Co@@](F)(Cl)(Br)(I)C=O
     * @@ 5       @OH2     O=C[Co@](F)(Cl)(Br)(I)S </pre></blockquote>
     */
    static Configuration toExplicit(Graph g, int u, Configuration c) {

        // already explicit
        if (c.type() != Implicit)
            return c;

        int deg     = g.degree(u);
        int valence = deg + g.atom(u).hydrogens();

        // tetrahedral topology, square planar must always be explicit
        if (valence == 4) {
            return c == ANTI_CLOCKWISE ? TH1 : TH2;
        }

        // tetrahedral topology with implicit lone pair or double bond (Sp2)
        // atoms (todo)
        else if (valence == 3) {

            // XXX: sulfoxide and selenium special case... would be better to compute
            // hybridization don't really like doing this here but is sufficient
            // for now
            if (g.atom(u).element() == Element.Sulfur || g.atom(u).element() == Element.Selenium) {
                int sb = 0, db = 0;
                final int d = g.degree(u);
                for (int j=0; j<d; ++j) {
                    final Edge e = g.edgeAt(u, j);
                    if (e.bond().order() == 1)
                        sb++;
                    else if (e.bond().order() == 2)
                        db++;
                    else return Configuration.UNKNOWN;
                }
                int q = g.atom(u).charge();
                if ((q == 0 && sb == 2 && db == 1) || (q == 1 && sb == 3))
                    return c == ANTI_CLOCKWISE ? TH1 : TH2;
                else
                    return Configuration.UNKNOWN;
            }
            
            if (g.atom(u).element() == Element.Phosphorus ||
                g.atom(u).element() == Element.Nitrogen) {
                if (g.bondedValence(u) == 3 && g.implHCount(u) == 0 && g.atom(u).charge() == 0)  {
                    return c == ANTI_CLOCKWISE ? TH1 : TH2;
                }
            }

            // for the atom centric double bond configuration check there is
            // a double bond and it's not sill tetrahedral specification such
            // as [C@-](N)(O)C
            int nDoubleBonds = 0;
            final int d = g.degree(u);
            for (int j=0; j<d; ++j) {
                final Edge e = g.edgeAt(u, j);
                if (e.bond() == Bond.DOUBLE)
                    nDoubleBonds++;
            }

            if (nDoubleBonds == 1) {
                return c == ANTI_CLOCKWISE ? DB1 : DB2;
            } else {
                return Configuration.UNKNOWN;
            }
        }

        // odd number of cumulated double bond systems (e.g. allene)
        else if (deg == 2) {

            int nDoubleBonds = 0;

            // check both bonds are double
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge e = g.edgeAt(u, j);
                if (e.bond() != Bond.DOUBLE)
                    nDoubleBonds++;
            }

            if (nDoubleBonds == 1) {
                return c == ANTI_CLOCKWISE ? DB1 : DB2;
            } else {
                return c == ANTI_CLOCKWISE ? AL1 : AL2;
            }
        }

        // trigonal bipyramidal
        else if (valence == 5) {
            return c == ANTI_CLOCKWISE ? TB1 : TB2;
        }

        // octahedral
        else if (valence == 6) {
            return c == ANTI_CLOCKWISE ? OH1 : OH2;
        }

        return Configuration.UNKNOWN;
    }

    static Topology create(int u, int[] vs, List<Edge> es, Configuration c) {
        if (c.type() == Implicit)
            throw new IllegalArgumentException("configuration must be explicit, @TH1/@TH2 instead of @/@@");

        // only tetrahedral is handled for now
        if (c.type() == Tetrahedral) {
            return tetrahedral(u, vs, c);
        } else if (c.type() == DoubleBond) {
            return trigonal(u, vs, c);
        } else if (c.type() == ExtendedTetrahedral) {
            return extendedTetrahedral(u, vs, c);
        } else if (c.type() == SquarePlanar) {
            return squarePlanar(u, vs, c);
        } else if (c.type() == TrigonalBipyramidal) {
            return trigonalBipyramidal(u, vs, c);
        } else if (c.type() == Configuration.Type.Octahedral) {
            return octahedral(u, vs, c);
        }

        return unknown();
    }

    private static Topology UNKNOWN = new Topology() {
        @Override int atom() {
            throw new IllegalArgumentException("unknown topology");
        }

        @Override Configuration configuration() {
            return Configuration.UNKNOWN;
        }

        @Override Topology orderBy(int[] rank) {
            return this;
        }

        @Override Topology transform(int[] mapping) {
            return this;
        }

        @Override void copy(int[] dest) {
        }
    };

    private static final class Tetrahedral extends Topology {

        private final int   u;
        private final int[] vs;
        private final int   p;

        private Tetrahedral(int u, int[] vs, int p) {
            if (vs.length != 4)
                throw new IllegalArgumentException("Tetrahedral topology requires 4 vertices - use the 'centre' vertex to mark implicit verticies");
            this.u = u;
            this.vs = vs;
            this.p = p;
        }

        /** @inheritDoc */
        @Override int atom() {
            return u;
        }

        /** @inheritDoc */
        @Override Configuration configuration() {
            return p < 0 ? Configuration.TH1 : Configuration.TH2;
        }

        /** @inheritDoc */
        @Override Topology orderBy(int[] rank) {
            return new Tetrahedral(u,
                                   sort(vs, rank),
                                   p * parity4(vs, rank));
        }

        /** @inheritDoc */
        @Override Topology transform(final int[] mapping) {
            int[] ws = new int[vs.length];
            for (int i = 0; i < vs.length; i++)
                ws[i] = mapping[vs[i]];
            return new Tetrahedral(mapping[u], ws, p);
        }

        @Override void copy(int[] dest) {
            System.arraycopy(vs, 0, dest, 0, 4);
        }

        @Override Configuration configurationOf(int[] rank) {
            return p * parity4(vs, rank) < 0 ? TH1 : TH2;
        }

        public String toString() {
            return u + " " + Arrays.toString(vs) + ":" + p;
        }
    }

    private static final class ExtendedTetrahedral extends Topology {

        private final int   u;
        private final int[] vs;
        private final int   p;

        private ExtendedTetrahedral(int u, int[] vs, int p) {
            if (vs.length != 4)
                throw new IllegalArgumentException("Tetrahedral topology requires 4 vertices - use the 'centre' vertex to mark implicit verticies");
            this.u = u;
            this.vs = vs;
            this.p = p;
        }

        /** @inheritDoc */
        @Override int atom() {
            return u;
        }

        /** @inheritDoc */
        @Override Configuration configuration() {
            return p < 0 ? Configuration.AL1 : Configuration.AL2;
        }

        /** @inheritDoc */
        @Override Topology orderBy(int[] rank) {
            return new ExtendedTetrahedral(u,
                                           sort(vs, rank),
                                           p * parity4(vs, rank));
        }

        /** @inheritDoc */
        @Override Topology transform(final int[] mapping) {
            int[] ws = new int[vs.length];
            for (int i = 0; i < vs.length; i++)
                ws[i] = mapping[vs[i]];
            return new ExtendedTetrahedral(mapping[u], ws, p);
        }

        @Override void copy(int[] dest) {
            System.arraycopy(vs, 0, dest, 0, 4);
        }

        public String toString() {
            return u + " " + Arrays.toString(vs) + ":" + p;
        }
    }

    private static final int A = 0;
    private static final int B = 1;
    private static final int C = 2;
    private static final int D = 3;
    private static final int E = 4;
    private static final int F = 5;

    private static boolean check(int[] dest, int[] src, int[] perm, int step, int skip) {
        for (int i = 0; i < perm.length;) {
            int j;
            for (j = 0; j < step; j++) {
                if (dest[perm[i + j]] != src[j])
                    break;
            }
            if (j == 0)
                i += skip * step;
            else if (j == step)
                return true;
            else
                i+= step;
        }
        return false;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private static void indirectSort(int[] dst, int[] rank) {
        for (int i = 0; i < dst.length; i++)
            for (int j = i; j > 0 && rank[dst[j-1]] > rank[dst[j]]; j--)
                Topology.swap(dst, j, j - 1);
    }

    private static int[] applyInv(int[] src, int[] perm) {
        int[] res = new int[src.length];
        for (int i = 0; i < src.length; i++)
            res[i] = src[perm[i]];
        return res;
    }

    private static Integer[] toObjArray(int[] arr) {
        Integer[] res = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++)
            res[i] = arr[i];
        return res;
    }

    private static int[] toIntArray(Integer[] arr) {
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
            res[i] = arr[i];
        return res;
    }

    private static final class SquarePlanar extends Topology {
        private final int   u;
        private final int[] vs;
        private final int   order;

        private static final int[][] PERMUTATIONS = new int[][]{
                {A, B, C, D,  A, D, C, B,
                 B, C, D, A,  B, A, D, C,
                 C, D, A, B,  C, B, A, D,
                 D, C, B, A,  D, A, B, C}, // SP1 (U)
                {A, C, B, D,  A, D, B, C,
                 B, D, A, C,  B, C, A, D,
                 C, A, D, B,  C, B, D, A,
                 D, B, C, A,  D, A, C, B}, // SP2 (4)
                {A, B, D, C,  A, C, D, B,
                 B, A, C, D,  B, D, C, A,
                 C, D, B, A,  C, A, B, D,
                 D, C, A, B,  D, B, A, C}  // SP3 (Z)
        };

        private SquarePlanar(int u, int[] vs, int p) {
            if (vs.length != 4)
                throw new IllegalArgumentException("SquarePlanar topology requires 4 vertices");
            this.u = u;
            this.vs = vs;
            this.order = p;
        }

        /** @inheritDoc */
        @Override int atom() {
            return u;
        }

        /** @inheritDoc */
        @Override Configuration configuration() {
            switch (order) {
                case 1: return SP1;
                case 2: return SP2;
                case 3: return SP3;
                default: return Configuration.UNKNOWN;
            }
        }

        /** @inheritDoc */
        @Override Topology orderBy(final int[] rank) {

            int[] src = Topology.applyInv(vs, PERMUTATIONS[order - 1]);
            int[] dst = src.clone();
            indirectSort(dst, rank);
            if (order < 1 || order > 20)
                return null;

            for (int i = 1; i <= 3; i++) {
                if (Topology.check(dst, src, PERMUTATIONS[i-1], 4, 2))
                    return new SquarePlanar(u, dst, i);
            }

            return null;
        }

        /** @inheritDoc */
        @Override Topology transform(final int[] mapping) {
            int[] ws = new int[vs.length];
            for (int i = 0; i < vs.length; i++)
                ws[i] = mapping[vs[i]];
            return new SquarePlanar(mapping[u], ws, order);
        }

        @Override void copy(int[] dest) {
            System.arraycopy(vs, 0, dest, 0, vs.length);
        }

        public String toString() {
            return u + " " + Arrays.toString(vs) + ":" + order;
        }
    }

    private static final class TrigonalBipyramidal extends Topology {
        private final int   u;
        private final int[] vs;
        private final int   order;

        private static final int[][] PERMUTATIONS = new int[][]{
                {A, B, C, D, E,  A, C, D, B, E,  A, D, B, C, E,
                 E, D, C, B, A,  E, B, D, C, A,  E, C, B, D, A }, // TB1 a -> e @
                {A, D, C, B, E,  A, C, B, D, E,  A, B, D, C, E,
                 E, B, C, D, A,  E, D, B, C, A,  E, C, D, B, A }, // TB2 a -> e @@
                {A, B, C, E, D,  A, C, E, B, D,  A, E, B, C, D,
                 D, E, C, B, A,  D, B, E, C, A,  D, C, B, E, A }, // TB3 a -> d @
                {A, E, C, B, D,  A, C, B, E, D,  A, B, E, C, D,
                 D, B, C, E, A,  D, E, B, C, A,  D, C, E, B, A }, // TB4 a -> d @@
                {A, B, D, E, C,  A, D, E, B, C,  A, E, B, D, C,
                 C, E, D, B, A,  C, B, E, D, A,  C, D, B, E, A }, // TB5 a -> c @
                {A, E, D, B, C,  A, D, B, E, C,  A, B, E, D, C,
                 C, B, D, E, A,  C, E, B, D, A,  C, D, E, B, A }, // TB6 a -> c @@
                {A, C, D, E, B,  A, D, E, C, B,  A, E, C, D, B,
                 B, E, D, C, A,  B, C, E, D, A,  B, D, C, E, A }, // TB7 a -> b @
                {A, E, D, C, B,  A, D, C, E, B,  A, C, E, D, B,
                 B, C, D, E, A,  B, E, C, D, A,  B, D, E, C, A }, // TB8 a -> b @@
                {B, A, C, D, E,  B, C, D, A, E,  B, D, A, C, E,
                 E, D, C, A, B,  E, A, D, C, B,  E, C, A, D, B }, // TB9 b -> e @
                {B, A, C, E, D,  B, C, E, A, D,  B, E, A, C, D,
                 D, E, C, A, B,  D, A, E, C, B,  D, C, A, E, B }, // TB10 b -> d @
                {B, D, C, A, E,  B, C, A, D, E,  B, A, D, C, E,
                 E, A, C, D, B,  E, D, A, C, B,  E, C, D, A, B }, // TB11 b -> e @@
                {B, E, C, A, D,  B, C, A, E, D,  B, A, E, C, D,
                 D, A, C, E, B,  D, E, A, C, B,  D, C, E, A, B }, // TB12 b -> d @@
                {B, A, D, E, C,  B, D, E, A, C,  B, E, A, D, C,
                 C, E, D, A, B,  C, A, E, D, B,  C, D, A, E, B }, // TB13 b -> c @
                {B, E, D, A, C,  B, D, A, E, C,  B, A, E, D, C,
                 C, A, D, E, B,  C, E, A, D, B,  C, D, E, A, B }, // TB14 b -> c @@
                {C, A, B, D, E,  C, B, D, A, E,  C, D, A, B, E,
                 E, D, B, A, C,  E, A, D, B, C,  E, B, A, D, C }, // TB15 c -> e @
                {C, A, B, E, D,  C, B, E, A, D,  C, E, A, B, D,
                 D, E, B, A, C,  D, A, E, B, C,  D, B, A, E, C }, // TB16 c -> d @
                {D, A, B, C, E,  D, B, C, A, E,  D, C, A, B, E,
                 E, C, B, A, D,  E, A, C, B, D,  E, B, A, C, D }, // TB17 d -> e @
                {D, C, B, A, E,  D, B, A, C, E,  D, A, C, B, E,
                 E, A, B, C, D,  E, C, A, B, D,  E, B, C, A, D }, // TB18 d -> e @@
                {C, E, B, A, D,  C, B, A, E, D,  C, A, E, B, D,
                 D, A, B, E, C,  D, E, A, B, C,  D, B, E, A, C }, // TB19 c -> d @@
                {C, D, B, A, E,  C, B, A, D, E,  C, A, D, B, E,
                 E, A, B, D, C,  E, D, A, B, C,  E, B, D, A, C }, // TB20 c -> e @@
        };

        private TrigonalBipyramidal(int u, int[] vs, int order) {
            if (vs.length != 5)
                throw new IllegalArgumentException("TrigonalBipyramidal topology requires 5 vertices");
            this.u = u;
            this.vs = vs;
            this.order = order;
        }

        /** @inheritDoc */
        @Override int atom() {
            return u;
        }

        /** @inheritDoc */
        @Override Configuration configuration() {
            if (order >= 1 && order <= 20)
                return Configuration.values()[Configuration.TB1.ordinal() + order - 1];
            return Configuration.UNKNOWN;
        }

        /** @inheritDoc */
        @Override Topology orderBy(final int[] rank) {
            int[] src = Topology.applyInv(vs, PERMUTATIONS[order-1]);
            int[] dst = src.clone();
            indirectSort(dst, rank);

            for (int i = 1; i <= 20; i++) {
                if (Topology.check(dst, src, PERMUTATIONS[i-1], 5, 3))
                    return new TrigonalBipyramidal(u, dst, i);
            }

            return null;
        }

        /** @inheritDoc */
        @Override Topology transform(final int[] mapping) {
            int[] ws = new int[vs.length];
            for (int i = 0; i < vs.length; i++)
                ws[i] = mapping[vs[i]];
            return new TrigonalBipyramidal(mapping[u], ws, order);
        }

        @Override void copy(int[] dest) {
            System.arraycopy(vs, 0, dest, 0, vs.length);
        }

        public String toString() {
            return u + " " + Arrays.toString(vs) + ":" + order;
        }
    }


    private static final class Octahedral extends Topology {
        private final int   u;
        private final int[] vs;
        private final int   order;

        private static final int[][] PERMUTATIONS = new int[][]{// @OH1
                                                                {A, B, C, D, E, F,  A, C, D, E, B, F,  A, D, E, B, C, F,  A, E, B, C, D, F,
                                                                 B, A, E, F, C, D,  B, C, A, E, F, D,  B, E, F, C, A, D,  B, F, C, A, E, D,
                                                                 C, A, B, F, D, E,  C, B, F, D, A, E,  C, D, A, B, F, E,  C, F, D, A, B, E,
                                                                 D, A, C, F, E, B,  D, C, F, E, A, B,  D, E, A, C, F, B,  D, F, E, A, C, B,
                                                                 E, A, D, F, B, C,  E, B, A, D, F, C,  E, D, F, B, A, C,  E, F, B, A, D, C,
                                                                 F, B, E, D, C, A,  F, C, B, E, D, A,  F, D, C, B, E, A,  F, E, D, C, B, A},
                                                                // @OH2
                                                                {A, B, E, D, C, F,  A, C, B, E, D, F,  A, D, C, B, E, F,  A, E, D, C, B, F,
                                                                 B, A, C, F, E, D,  B, C, F, E, A, D,  B, E, A, C, F, D,  B, F, E, A, C, D,
                                                                 C, A, D, F, B, E,  C, B, A, D, F, E,  C, D, F, B, A, E,  C, F, B, A, D, E,
                                                                 D, A, E, F, C, B,  D, C, A, E, F, B,  D, E, F, C, A, B,  D, F, C, A, E, B,
                                                                 E, A, B, F, D, C,  E, B, F, D, A, C,  E, D, A, B, F, C,  E, F, D, A, B, C,
                                                                 F, B, C, D, E, A,  F, C, D, E, B, A,  F, D, E, B, C, A,  F, E, B, C, D, A},
                                                                // @OH3
                                                                {A, B, C, D, F, E,  A, C, D, F, B, E,  A, D, F, B, C, E,  A, F, B, C, D, E,
                                                                 B, A, F, E, C, D,  B, C, A, F, E, D,  B, E, C, A, F, D,  B, F, E, C, A, D,
                                                                 C, A, B, E, D, F,  C, B, E, D, A, F,  C, D, A, B, E, F,  C, E, D, A, B, F,
                                                                 D, A, C, E, F, B,  D, C, E, F, A, B,  D, E, F, A, C, B,  D, F, A, C, E, B,
                                                                 E, B, F, D, C, A,  E, C, B, F, D, A,  E, D, C, B, F, A,  E, F, D, C, B, A,
                                                                 F, A, D, E, B, C,  F, B, A, D, E, C,  F, D, E, B, A, C,  F, E, B, A, D, C},
                                                                // @OH4
                                                                {A, B, C, E, D, F,  A, C, E, D, B, F,  A, D, B, C, E, F,  A, E, D, B, C, F,
                                                                 B, A, D, F, C, E,  B, C, A, D, F, E,  B, D, F, C, A, E,  B, F, C, A, D, E,
                                                                 C, A, B, F, E, D,  C, B, F, E, A, D,  C, E, A, B, F, D,  C, F, E, A, B, D,
                                                                 D, A, E, F, B, C,  D, B, A, E, F, C,  D, E, F, B, A, C,  D, F, B, A, E, C,
                                                                 E, A, C, F, D, B,  E, C, F, D, A, B,  E, D, A, C, F, B,  E, F, D, A, C, B,
                                                                 F, B, D, E, C, A,  F, C, B, D, E, A,  F, D, E, C, B, A,  F, E, C, B, D, A},
                                                                // @OH5
                                                                {A, B, C, F, D, E,  A, C, F, D, B, E,  A, D, B, C, F, E,  A, F, D, B, C, E,
                                                                 B, A, D, E, C, F,  B, C, A, D, E, F,  B, D, E, C, A, F,  B, E, C, A, D, F,
                                                                 C, A, B, E, F, D,  C, B, E, F, A, D,  C, E, F, A, B, D,  C, F, A, B, E, D,
                                                                 D, A, F, E, B, C,  D, B, A, F, E, C,  D, E, B, A, F, C,  D, F, E, B, A, C,
                                                                 E, B, D, F, C, A,  E, C, B, D, F, A,  E, D, F, C, B, A,  E, F, C, B, D, A,
                                                                 F, A, C, E, D, B,  F, C, E, D, A, B,  F, D, A, C, E, B,  F, E, D, A, C, B},
                                                                // @OH6
                                                                {A, B, C, E, F, D,  A, C, E, F, B, D,  A, E, F, B, C, D,  A, F, B, C, E, D,
                                                                 B, A, F, D, C, E,  B, C, A, F, D, E,  B, D, C, A, F, E,  B, F, D, C, A, E,
                                                                 C, A, B, D, E, F,  C, B, D, E, A, F,  C, D, E, A, B, F,  C, E, A, B, D, F,
                                                                 D, B, F, E, C, A,  D, C, B, F, E, A,  D, E, C, B, F, A,  D, F, E, C, B, A,
                                                                 E, A, C, D, F, B,  E, C, D, F, A, B,  E, D, F, A, C, B,  E, F, A, C, D, B,
                                                                 F, A, E, D, B, C,  F, B, A, E, D, C,  F, D, B, A, E, C,  F, E, D, B, A, C},
                                                                // @OH7
                                                                {A, B, C, F, E, D,  A, C, F, E, B, D,  A, E, B, C, F, D,  A, F, E, B, C, D,
                                                                 B, A, E, D, C, F,  B, C, A, E, D, F,  B, D, C, A, E, F,  B, E, D, C, A, F,
                                                                 C, A, B, D, F, E,  C, B, D, F, A, E,  C, D, F, A, B, E,  C, F, A, B, D, E,
                                                                 D, B, E, F, C, A,  D, C, B, E, F, A,  D, E, F, C, B, A,  D, F, C, B, E, A,
                                                                 E, A, F, D, B, C,  E, B, A, F, D, C,  E, D, B, A, F, C,  E, F, D, B, A, C,
                                                                 F, A, C, D, E, B,  F, C, D, E, A, B,  F, D, E, A, C, B,  F, E, A, C, D, B},
                                                                // @OH8
                                                                {A, B, D, C, E, F,  A, C, E, B, D, F,  A, D, C, E, B, F,  A, E, B, D, C, F,
                                                                 B, A, E, F, D, C,  B, D, A, E, F, C,  B, E, F, D, A, C,  B, F, D, A, E, C,
                                                                 C, A, D, F, E, B,  C, D, F, E, A, B,  C, E, A, D, F, B,  C, F, E, A, D, B,
                                                                 D, A, B, F, C, E,  D, B, F, C, A, E,  D, C, A, B, F, E,  D, F, C, A, B, E,
                                                                 E, A, C, F, B, D,  E, B, A, C, F, D,  E, C, F, B, A, D,  E, F, B, A, C, D,
                                                                 F, B, E, C, D, A,  F, C, D, B, E, A,  F, D, B, E, C, A,  F, E, C, D, B, A},
                                                                // @OH9
                                                                {A, B, D, C, F, E,  A, C, F, B, D, E,  A, D, C, F, B, E,  A, F, B, D, C, E,
                                                                 B, A, F, E, D, C,  B, D, A, F, E, C,  B, E, D, A, F, C,  B, F, E, D, A, C,
                                                                 C, A, D, E, F, B,  C, D, E, F, A, B,  C, E, F, A, D, B,  C, F, A, D, E, B,
                                                                 D, A, B, E, C, F,  D, B, E, C, A, F,  D, C, A, B, E, F,  D, E, C, A, B, F,
                                                                 E, B, F, C, D, A,  E, C, D, B, F, A,  E, D, B, F, C, A,  E, F, C, D, B, A,
                                                                 F, A, C, E, B, D,  F, B, A, C, E, D,  F, C, E, B, A, D,  F, E, B, A, C, D},
                                                                // @OH10
                                                                {A, B, E, C, D, F,  A, C, D, B, E, F,  A, D, B, E, C, F,  A, E, C, D, B, F,
                                                                 B, A, D, F, E, C,  B, D, F, E, A, C,  B, E, A, D, F, C,  B, F, E, A, D, C,
                                                                 C, A, E, F, D, B,  C, D, A, E, F, B,  C, E, F, D, A, B,  C, F, D, A, E, B,
                                                                 D, A, C, F, B, E,  D, B, A, C, F, E,  D, C, F, B, A, E,  D, F, B, A, C, E,
                                                                 E, A, B, F, C, D,  E, B, F, C, A, D,  E, C, A, B, F, D,  E, F, C, A, B, D,
                                                                 F, B, D, C, E, A,  F, C, E, B, D, A,  F, D, C, E, B, A,  F, E, B, D, C, A},
                                                                // @OH11
                                                                {A, B, F, C, D, E,  A, C, D, B, F, E,  A, D, B, F, C, E,  A, F, C, D, B, E,
                                                                 B, A, D, E, F, C,  B, D, E, F, A, C,  B, E, F, A, D, C,  B, F, A, D, E, C,
                                                                 C, A, F, E, D, B,  C, D, A, F, E, B,  C, E, D, A, F, B,  C, F, E, D, A, B,
                                                                 D, A, C, E, B, F,  D, B, A, C, E, F,  D, C, E, B, A, F,  D, E, B, A, C, F,
                                                                 E, B, D, C, F, A,  E, C, F, B, D, A,  E, D, C, F, B, A,  E, F, B, D, C, A,
                                                                 F, A, B, E, C, D,  F, B, E, C, A, D,  F, C, A, B, E, D,  F, E, C, A, B, D},
                                                                // @OH12
                                                                {A, B, E, C, F, D,  A, C, F, B, E, D,  A, E, C, F, B, D,  A, F, B, E, C, D,
                                                                 B, A, F, D, E, C,  B, D, E, A, F, C,  B, E, A, F, D, C,  B, F, D, E, A, C,
                                                                 C, A, E, D, F, B,  C, D, F, A, E, B,  C, E, D, F, A, B,  C, F, A, E, D, B,
                                                                 D, B, F, C, E, A,  D, C, E, B, F, A,  D, E, B, F, C, A,  D, F, C, E, B, A,
                                                                 E, A, B, D, C, F,  E, B, D, C, A, F,  E, C, A, B, D, F,  E, D, C, A, B, F,
                                                                 F, A, C, D, B, E,  F, B, A, C, D, E,  F, C, D, B, A, E,  F, D, B, A, C, E},
                                                                // @OH13
                                                                {A, B, F, C, E, D,  A, C, E, B, F, D,  A, E, B, F, C, D,  A, F, C, E, B, D,
                                                                 B, A, E, D, F, C,  B, D, F, A, E, C,  B, E, D, F, A, C,  B, F, A, E, D, C,
                                                                 C, A, F, D, E, B,  C, D, E, A, F, B,  C, E, A, F, D, B,  C, F, D, E, A, B,
                                                                 D, B, E, C, F, A,  D, C, F, B, E, A,  D, E, C, F, B, A,  D, F, B, E, C, A,
                                                                 E, A, C, D, B, F,  E, B, A, C, D, F,  E, C, D, B, A, F,  E, D, B, A, C, F,
                                                                 F, A, B, D, C, E,  F, B, D, C, A, E,  F, C, A, B, D, E,  F, D, C, A, B, E},
                                                                // @OH14
                                                                {A, B, D, E, C, F,  A, C, B, D, E, F,  A, D, E, C, B, F,  A, E, C, B, D, F,
                                                                 B, A, C, F, D, E,  B, C, F, D, A, E,  B, D, A, C, F, E,  B, F, D, A, C, E,
                                                                 C, A, E, F, B, D,  C, B, A, E, F, D,  C, E, F, B, A, D,  C, F, B, A, E, D,
                                                                 D, A, B, F, E, C,  D, B, F, E, A, C,  D, E, A, B, F, C,  D, F, E, A, B, C,
                                                                 E, A, D, F, C, B,  E, C, A, D, F, B,  E, D, F, C, A, B,  E, F, C, A, D, B,
                                                                 F, B, C, E, D, A,  F, C, E, D, B, A,  F, D, B, C, E, A,  F, E, D, B, C, A},
                                                                // @OH15
                                                                {A, B, D, F, C, E,  A, C, B, D, F, E,  A, D, F, C, B, E,  A, F, C, B, D, E,
                                                                 B, A, C, E, D, F,  B, C, E, D, A, F,  B, D, A, C, E, F,  B, E, D, A, C, F,
                                                                 C, A, F, E, B, D,  C, B, A, F, E, D,  C, E, B, A, F, D,  C, F, E, B, A, D,
                                                                 D, A, B, E, F, C,  D, B, E, F, A, C,  D, E, F, A, B, C,  D, F, A, B, E, C,
                                                                 E, B, C, F, D, A,  E, C, F, D, B, A,  E, D, B, C, F, A,  E, F, D, B, C, A,
                                                                 F, A, D, E, C, B,  F, C, A, D, E, B,  F, D, E, C, A, B,  F, E, C, A, D, B},
                                                                // @OH16
                                                                {A, B, F, D, C, E,  A, C, B, F, D, E,  A, D, C, B, F, E,  A, F, D, C, B, E,
                                                                 B, A, C, E, F, D,  B, C, E, F, A, D,  B, E, F, A, C, D,  B, F, A, C, E, D,
                                                                 C, A, D, E, B, F,  C, B, A, D, E, F,  C, D, E, B, A, F,  C, E, B, A, D, F,
                                                                 D, A, F, E, C, B,  D, C, A, F, E, B,  D, E, C, A, F, B,  D, F, E, C, A, B,
                                                                 E, B, C, D, F, A,  E, C, D, F, B, A,  E, D, F, B, C, A,  E, F, B, C, D, A,
                                                                 F, A, B, E, D, C,  F, B, E, D, A, C,  F, D, A, B, E, C,  F, E, D, A, B, C},
                                                                // @OH17
                                                                {A, B, E, F, C, D,  A, C, B, E, F, D,  A, E, F, C, B, D,  A, F, C, B, E, D,
                                                                 B, A, C, D, E, F,  B, C, D, E, A, F,  B, D, E, A, C, F,  B, E, A, C, D, F,
                                                                 C, A, F, D, B, E,  C, B, A, F, D, E,  C, D, B, A, F, E,  C, F, D, B, A, E,
                                                                 D, B, C, F, E, A,  D, C, F, E, B, A,  D, E, B, C, F, A,  D, F, E, B, C, A,
                                                                 E, A, B, D, F, C,  E, B, D, F, A, C,  E, D, F, A, B, C,  E, F, A, B, D, C,
                                                                 F, A, E, D, C, B,  F, C, A, E, D, B,  F, D, C, A, E, B,  F, E, D, C, A, B},
                                                                // @OH18
                                                                {A, B, F, E, C, D,  A, C, B, F, E, D,  A, E, C, B, F, D,  A, F, E, C, B, D,
                                                                 B, A, C, D, F, E,  B, C, D, F, A, E,  B, D, F, A, C, E,  B, F, A, C, D, E,
                                                                 C, A, E, D, B, F,  C, B, A, E, D, F,  C, D, B, A, E, F,  C, E, D, B, A, F,
                                                                 D, B, C, E, F, A,  D, C, E, F, B, A,  D, E, F, B, C, A,  D, F, B, C, E, A,
                                                                 E, A, F, D, C, B,  E, C, A, F, D, B,  E, D, C, A, F, B,  E, F, D, C, A, B,
                                                                 F, A, B, D, E, C,  F, B, D, E, A, C,  F, D, E, A, B, C,  F, E, A, B, D, C},
                                                                // @OH19
                                                                {A, B, D, E, F, C,  A, D, E, F, B, C,  A, E, F, B, D, C,  A, F, B, D, E, C,
                                                                 B, A, F, C, D, E,  B, C, D, A, F, E,  B, D, A, F, C, E,  B, F, C, D, A, E,
                                                                 C, B, F, E, D, A,  C, D, B, F, E, A,  C, E, D, B, F, A,  C, F, E, D, B, A,
                                                                 D, A, B, C, E, F,  D, B, C, E, A, F,  D, C, E, A, B, F,  D, E, A, B, C, F,
                                                                 E, A, D, C, F, B,  E, C, F, A, D, B,  E, D, C, F, A, B,  E, F, A, D, C, B,
                                                                 F, A, E, C, B, D,  F, B, A, E, C, D,  F, C, B, A, E, D,  F, E, C, B, A, D},
                                                                // @OH20
                                                                {A, B, D, F, E, C,  A, D, F, E, B, C,  A, E, B, D, F, C,  A, F, E, B, D, C,
                                                                 B, A, E, C, D, F,  B, C, D, A, E, F,  B, D, A, E, C, F,  B, E, C, D, A, F,
                                                                 C, B, E, F, D, A,  C, D, B, E, F, A,  C, E, F, D, B, A,  C, F, D, B, E, A,
                                                                 D, A, B, C, F, E,  D, B, C, F, A, E,  D, C, F, A, B, E,  D, F, A, B, C, E,
                                                                 E, A, F, C, B, D,  E, B, A, F, C, D,  E, C, B, A, F, D,  E, F, C, B, A, D,
                                                                 F, A, D, C, E, B,  F, C, E, A, D, B,  F, D, C, E, A, B,  F, E, A, D, C, B},
                                                                // @OH21
                                                                {A, B, E, D, F, C,  A, D, F, B, E, C,  A, E, D, F, B, C,  A, F, B, E, D, C,
                                                                 B, A, F, C, E, D,  B, C, E, A, F, D,  B, E, A, F, C, D,  B, F, C, E, A, D,
                                                                 C, B, F, D, E, A,  C, D, E, B, F, A,  C, E, B, F, D, A,  C, F, D, E, B, A,
                                                                 D, A, E, C, F, B,  D, C, F, A, E, B,  D, E, C, F, A, B,  D, F, A, E, C, B,
                                                                 E, A, B, C, D, F,  E, B, C, D, A, F,  E, C, D, A, B, F,  E, D, A, B, C, F,
                                                                 F, A, D, C, B, E,  F, B, A, D, C, E,  F, C, B, A, D, E,  F, D, C, B, A, E},
                                                                // @OH22
                                                                {A, B, F, D, E, C,  A, D, E, B, F, C,  A, E, B, F, D, C,  A, F, D, E, B, C,
                                                                 B, A, E, C, F, D,  B, C, F, A, E, D,  B, E, C, F, A, D,  B, F, A, E, C, D,
                                                                 C, B, E, D, F, A,  C, D, F, B, E, A,  C, E, D, F, B, A,  C, F, B, E, D, A,
                                                                 D, A, F, C, E, B,  D, C, E, A, F, B,  D, E, A, F, C, B,  D, F, C, E, A, B,
                                                                 E, A, D, C, B, F,  E, B, A, D, C, F,  E, C, B, A, D, F,  E, D, C, B, A, F,
                                                                 F, A, B, C, D, E,  F, B, C, D, A, E,  F, C, D, A, B, E,  F, D, A, B, C, E},
                                                                // @OH23
                                                                {A, B, E, F, D, C,  A, D, B, E, F, C,  A, E, F, D, B, C,  A, F, D, B, E, C,
                                                                 B, A, D, C, E, F,  B, C, E, A, D, F,  B, D, C, E, A, F,  B, E, A, D, C, F,
                                                                 C, B, D, F, E, A,  C, D, F, E, B, A,  C, E, B, D, F, A,  C, F, E, B, D, A,
                                                                 D, A, F, C, B, E,  D, B, A, F, C, E,  D, C, B, A, F, E,  D, F, C, B, A, E,
                                                                 E, A, B, C, F, D,  E, B, C, F, A, D,  E, C, F, A, B, D,  E, F, A, B, C, D,
                                                                 F, A, E, C, D, B,  F, C, D, A, E, B,  F, D, A, E, C, B,  F, E, C, D, A, B},
                                                                // @OH24
                                                                {A, B, F, E, D, C,  A, D, B, F, E, C,  A, E, D, B, F, C,  A, F, E, D, B, C,
                                                                 B, A, D, C, F, E,  B, C, F, A, D, E,  B, D, C, F, A, E,  B, F, A, D, C, E,
                                                                 C, B, D, E, F, A,  C, D, E, F, B, A,  C, E, F, B, D, A,  C, F, B, D, E, A,
                                                                 D, A, E, C, B, F,  D, B, A, E, C, F,  D, C, B, A, E, F,  D, E, C, B, A, F,
                                                                 E, A, F, C, D, B,  E, C, D, A, F, B,  E, D, A, F, C, B,  E, F, C, D, A, B,
                                                                 F, A, B, C, E, D,  F, B, C, E, A, D,  F, C, E, A, B, D,  F, E, A, B, C, D},
                                                                // @OH25
                                                                {A, C, D, E, F, B,  A, D, E, F, C, B,  A, E, F, C, D, B,  A, F, C, D, E, B,
                                                                 B, C, F, E, D, A,  B, D, C, F, E, A,  B, E, D, C, F, A,  B, F, E, D, C, A,
                                                                 C, A, F, B, D, E,  C, B, D, A, F, E,  C, D, A, F, B, E,  C, F, B, D, A, E,
                                                                 D, A, C, B, E, F,  D, B, E, A, C, F,  D, C, B, E, A, F,  D, E, A, C, B, F,
                                                                 E, A, D, B, F, C,  E, B, F, A, D, C,  E, D, B, F, A, C,  E, F, A, D, B, C,
                                                                 F, A, E, B, C, D,  F, B, C, A, E, D,  F, C, A, E, B, D,  F, E, B, C, A, D},
                                                                // @OH26
                                                                {A, C, D, F, E, B,  A, D, F, E, C, B,  A, E, C, D, F, B,  A, F, E, C, D, B,
                                                                 B, C, E, F, D, A,  B, D, C, E, F, A,  B, E, F, D, C, A,  B, F, D, C, E, A,
                                                                 C, A, E, B, D, F,  C, B, D, A, E, F,  C, D, A, E, B, F,  C, E, B, D, A, F,
                                                                 D, A, C, B, F, E,  D, B, F, A, C, E,  D, C, B, F, A, E,  D, F, A, C, B, E,
                                                                 E, A, F, B, C, D,  E, B, C, A, F, D,  E, C, A, F, B, D,  E, F, B, C, A, D,
                                                                 F, A, D, B, E, C,  F, B, E, A, D, C,  F, D, B, E, A, C,  F, E, A, D, B, C},
                                                                // @OH27
                                                                {A, C, E, D, F, B,  A, D, F, C, E, B,  A, E, D, F, C, B,  A, F, C, E, D, B,
                                                                 B, C, F, D, E, A,  B, D, E, C, F, A,  B, E, C, F, D, A,  B, F, D, E, C, A,
                                                                 C, A, F, B, E, D,  C, B, E, A, F, D,  C, E, A, F, B, D,  C, F, B, E, A, D,
                                                                 D, A, E, B, F, C,  D, B, F, A, E, C,  D, E, B, F, A, C,  D, F, A, E, B, C,
                                                                 E, A, C, B, D, F,  E, B, D, A, C, F,  E, C, B, D, A, F,  E, D, A, C, B, F,
                                                                 F, A, D, B, C, E,  F, B, C, A, D, E,  F, C, A, D, B, E,  F, D, B, C, A, E},
                                                                // @OH28
                                                                {A, C, F, D, E, B,  A, D, E, C, F, B,  A, E, C, F, D, B,  A, F, D, E, C, B,
                                                                 B, C, E, D, F, A,  B, D, F, C, E, A,  B, E, D, F, C, A,  B, F, C, E, D, A,
                                                                 C, A, E, B, F, D,  C, B, F, A, E, D,  C, E, B, F, A, D,  C, F, A, E, B, D,
                                                                 D, A, F, B, E, C,  D, B, E, A, F, C,  D, E, A, F, B, C,  D, F, B, E, A, C,
                                                                 E, A, D, B, C, F,  E, B, C, A, D, F,  E, C, A, D, B, F,  E, D, B, C, A, F,
                                                                 F, A, C, B, D, E,  F, B, D, A, C, E,  F, C, B, D, A, E,  F, D, A, C, B, E},
                                                                // @OH29
                                                                {A, C, E, F, D, B,  A, D, C, E, F, B,  A, E, F, D, C, B,  A, F, D, C, E, B,
                                                                 B, C, D, F, E, A,  B, D, F, E, C, A,  B, E, C, D, F, A,  B, F, E, C, D, A,
                                                                 C, A, D, B, E, F,  C, B, E, A, D, F,  C, D, B, E, A, F,  C, E, A, D, B, F,
                                                                 D, A, F, B, C, E,  D, B, C, A, F, E,  D, C, A, F, B, E,  D, F, B, C, A, E,
                                                                 E, A, C, B, F, D,  E, B, F, A, C, D,  E, C, B, F, A, D,  E, F, A, C, B, D,
                                                                 F, A, E, B, D, C,  F, B, D, A, E, C,  F, D, A, E, B, C,  F, E, B, D, A, C},
                                                                // @OH30
                                                                {A, C, F, E, D, B,  A, D, C, F, E, B,  A, E, D, C, F, B,  A, F, E, D, C, B,
                                                                 B, C, D, E, F, A,  B, D, E, F, C, A,  B, E, F, C, D, A,  B, F, C, D, E, A,
                                                                 C, A, D, B, F, E,  C, B, F, A, D, E,  C, D, B, F, A, E,  C, F, A, D, B, E,
                                                                 D, A, E, B, C, F,  D, B, C, A, E, F,  D, C, A, E, B, F,  D, E, B, C, A, F,
                                                                 E, A, F, B, D, C,  E, B, D, A, F, C,  E, D, A, F, B, C,  E, F, B, D, A, C,
                                                                 F, A, C, B, E, D,  F, B, E, A, C, D,  F, C, B, E, A, D,  F, E, A, C, B, D}
        };

        private Octahedral(int u, int[] vs, int order) {
            if (vs.length != 6)
                throw new IllegalArgumentException("Octahedral topology requires 6 vertices");
            this.u = u;
            this.vs = vs;
            this.order = order;
        }

        /** @inheritDoc */
        @Override int atom() {
            return u;
        }

        /** @inheritDoc */
        @Override Configuration configuration() {
            if (order >= 1 && order <= 30)
                return Configuration.values()[Configuration.OH1.ordinal() + order - 1];
            return Configuration.UNKNOWN;
        }

        /** @inheritDoc */
        @Override Topology orderBy(final int[] rank) {
            int[] src = Topology.applyInv(vs, PERMUTATIONS[order-1]);
            int[] dst = src.clone();
            indirectSort(dst, rank);

            for (int i = 1; i <= 30; i++) {
                if (Topology.check(dst, src, PERMUTATIONS[i-1], 6, 4))
                    return new Octahedral(u, dst, i);
            }

            return null;
        }

        /** @inheritDoc */
        @Override Topology transform(final int[] mapping) {
            int[] ws = new int[vs.length];
            for (int i = 0; i < vs.length; i++)
                ws[i] = mapping[vs[i]];
            return new Octahedral(mapping[u], ws, order);
        }

        @Override void copy(int[] dest) {
            System.arraycopy(vs, 0, dest, 0, vs.length);
        }

        public String toString() {
            return u + " " + Arrays.toString(vs) + ":" + order;
        }
    }

    private static final class Trigonal extends Topology {
        private final int   u;
        private final int[] vs;
        private final int   p;

        private Trigonal(int u, int[] vs, int p) {
            if (vs.length != 3)
                throw new IllegalArgumentException("Trigonal topology requires 3 vertices - use the 'centre' vertex to mark implicit verticies");
            this.u = u;
            this.vs = vs;
            this.p = p;
        }

        /** @inheritDoc */
        @Override int atom() {
            return u;
        }

        /** @inheritDoc */
        @Override Configuration configuration() {
            return p < 0 ? Configuration.DB1 : Configuration.DB2;
        }

        /** @inheritDoc */
        @Override Topology orderBy(int[] rank) {
            return new Trigonal(u,
                                sort(vs, rank),
                                p * parity(vs, rank));
        }

        /** @inheritDoc */
        @Override Topology transform(final int[] mapping) {
            int[] ws = new int[vs.length];
            for (int i = 0; i < vs.length; i++)
                ws[i] = mapping[vs[i]];
            return new Trigonal(mapping[u], ws, p);
        }

        @Override void copy(int[] dest) {
            System.arraycopy(vs, 0, dest, 0, 3);
        }

        public String toString() {
            return u + " " + Arrays.toString(vs) + ":" + p;
        }
    }
}
