/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.openscience.cdk.hash.stereo.StereoEncoder;
import org.openscience.cdk.hash.stereo.StereoEncoderFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A generator for basic atom hash codes. This implementation is based on the
 * description by {@cdk.cite Ihlenfeldt93}. The hash codes use an initial
 * generator to seed the values of each atom. The initial values are then
 * combined over a series of cycles up to a specified depth. At each cycle the
 * hash values of adjacent invariants are incorporated.
 *
 * <h4>Which depth should I use?</h4> The <i>depth</i> determines the number of
 * cycles and thus how <i>deep</i> the hashing is, larger values discriminate
 * more molecules but can take longer to compute. The original publication
 * recommends a depth of 32 however values as low as 6 can yield good results.
 * The actual depth required is related to the <i>diameter</i> of the chemical
 * graph. The <i>diameter</i> is the longest shortest path, that is, the
 * furthest distance one must travel between any two vertex. Unfortunately the
 * time complexity of finding the longest shortest path in an undirected graph
 * is O(n<sup>2</sup>) which is larger then the time required for this hash
 * function. Depending on the types of molecules in your data set the depth
 * should be adjusted accordingly. For example, a library of large-lipids would
 * require deeper hashing to discriminate differences in chain length.
 *
 * <h4>Usage</h4>
 * <blockquote><pre>
 * SeedGenerator     seeding   = ...
 * AtomHashGenerator generator = new BasicAtomHashGenerator(seeding,
 *                                                          new Xorshift(),
 *                                                          32);
 *
 * IAtomContainer benzene = MoleculeFactory.benzene();
 * long[]         hashes  = generator.generate(benzene);
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module hash
 * @see SeedGenerator
 * @see <a href="http://mathworld.wolfram.com/GraphDiameter.html">Graph
 *      Diameter</a>
 * @see <a href="http://onlinelibrary.wiley.com/doi/10.1002/jcc.540150802/abstract">Original
 *      Publication</a>
 * @cdk.githash
 */
final class BasicAtomHashGenerator extends AbstractAtomHashGenerator implements AtomHashGenerator {

    /* a generator for the initial atom seeds */
    private final AtomHashGenerator    seedGenerator;

    /* creates stereo encoders for IAtomContainers */
    private final StereoEncoderFactory factory;

    /* number of cycles to include adjacent invariants */
    private final int                  depth;

    /**
     * Create a basic hash generator using the provided seed generator to
     * initialise atom invariants and using the provided stereo factory.
     *
     * @param seedGenerator generator to seed the initial values of atoms
     * @param pseudorandom  pseudorandom number generator used to randomise hash
     *                      distribution
     * @param factory       a stereo encoder factory
     * @param depth         depth of the hashing function, larger values take
     *                      longer
     * @throws IllegalArgumentException depth was less then 0
     * @throws NullPointerException     seed generator or pseudo random was
     *                                  null
     * @see SeedGenerator
     */
    public BasicAtomHashGenerator(AtomHashGenerator seedGenerator, Pseudorandom pseudorandom,
            StereoEncoderFactory factory, int depth) {
        super(pseudorandom);
        if (seedGenerator == null) throw new NullPointerException("seed generator cannot be null");
        if (depth < 0) throw new IllegalArgumentException("depth cannot be less then 0");
        this.seedGenerator = seedGenerator;
        this.factory = factory;
        this.depth = depth;
    }

    /**
     * Create a basic hash generator using the provided seed generator to
     * initialise atom invariants and no stereo configuration.
     *
     * @param seedGenerator generator to seed the initial values of atoms
     * @param pseudorandom  pseudorandom number generator used to randomise hash
     *                      distribution
     * @param depth         depth of the hashing function, larger values take
     *                      longer
     * @throws IllegalArgumentException depth was less then 0
     * @throws NullPointerException     seed generator or pseudo random was
     *                                  null
     * @see SeedGenerator
     */
    public BasicAtomHashGenerator(AtomHashGenerator seedGenerator, Pseudorandom pseudorandom, int depth) {
        this(seedGenerator, pseudorandom, StereoEncoderFactory.EMPTY, depth);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public long[] generate(IAtomContainer container) {
        int[][] graph = toAdjList(container);
        return generate(seedGenerator.generate(container), factory.create(container, graph), graph, Suppressed.none());
    }

    /**
     * Package-private method for generating the hash for the given molecule.
     * The initial invariants are passed as to the method along with an
     * adjacency list representation of the graph.
     *
     * @param current initial invariants
     * @param graph   adjacency list representation
     * @return hash codes for atoms
     */
    @Override
    long[] generate(long[] current, StereoEncoder encoder, int[][] graph, Suppressed suppressed) {

        int n = graph.length;
        long[] next = copy(current);

        // buffers for including adjacent invariants
        long[] unique = new long[n];
        long[] included = new long[n];

        while (encoder.encode(current, next)) {
            copy(next, current);
        }

        for (int d = 0; d < depth; d++) {

            for (int v = 0; v < n; v++) {
                next[v] = next(graph, v, current, unique, included);
            }

            copy(next, current);

            while (encoder.encode(current, next)) {
                copy(next, current);
            }

        }

        return current;
    }

    /**
     * Determine the next value of the atom at index <i>v</i>. The value is
     * calculated by combining the current values of adjacent atoms. When a
     * duplicate value is found it can not be directly included and is
     * <i>rotated</i> the number of times it has previously been seen.
     *
     * @param graph    adjacency list representation of connected atoms
     * @param v        the atom to calculate the next value for
     * @param current  the current values
     * @param unique   buffer for working out which adjacent values are unique
     * @param included buffer for storing the rotated <i>unique</i> value, this
     *                 value is <i>rotated</i> each time the same value is
     *                 found.
     * @return the next value for <i>v</i>
     */
    long next(int[][] graph, int v, long[] current, long[] unique, long[] included) {

        long invariant = distribute(current[v]);
        int nUnique = 0;

        for (int w : graph[v]) {

            long adjInv = current[w];

            // find index of already included neighbor
            int i = 0;
            while (i < nUnique && unique[i] != adjInv) {
                ++i;
            }

            // no match, then the value is unique, use adjInv
            // match, then rotate the previously included value
            included[i] = (i == nUnique) ? unique[nUnique++] = adjInv : rotate(included[i]);

            invariant ^= included[i];
        }

        return invariant;
    }
}
