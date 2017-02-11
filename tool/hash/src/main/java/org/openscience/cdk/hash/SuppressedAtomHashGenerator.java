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
 * A generator for atom hash codes where atoms maybe be <i>suppressed</i>. A
 * common usage would be compute the hash code for a molecule with explicit
 * hydrogens but ignore any values for the explicit hydrogens. This particularly
 * useful for stereo-centres where by removing explicit hydrogens could affect
 * the configuration.
 *
 * The suppress atom hashes are returned as '0'.
 *
 * @author John May
 * @cdk.module hash
 * @see org.openscience.cdk.hash.SeedGenerator
 * @cdk.githash
 */
final class SuppressedAtomHashGenerator extends AbstractAtomHashGenerator implements AtomHashGenerator {

    /* a generator for the initial atom seeds */
    private final AtomHashGenerator    seedGenerator;

    /* creates stereo encoders for IAtomContainers */
    private final StereoEncoderFactory factory;

    /* number of cycles to include adjacent invariants */
    private final int                  depth;

    /**
     * Function used to indicate which atoms should be suppressed. One can think
     * of this as 'masking' out a value.
     */
    private final AtomSuppression      suppression;

    /**
     * Create a basic hash generator using the provided seed generator to
     * initialise atom invariants and using the provided stereo factory.
     *
     * @param seedGenerator generator to seed the initial values of atoms
     * @param pseudorandom  pseudorandom number generator used to randomise hash
     *                      distribution
     * @param factory       a stereo encoder factory
     * @param suppression   defines which atoms are suppressed - that is
     *                      masked from the hash
     * @param depth         depth of the hashing function, larger values take
     *                      longer
     * @throws IllegalArgumentException depth was less then 0
     * @throws NullPointerException     seed generator or pseudo random was
     *                                  null
     * @see org.openscience.cdk.hash.SeedGenerator
     */
    public SuppressedAtomHashGenerator(AtomHashGenerator seedGenerator, Pseudorandom pseudorandom,
            StereoEncoderFactory factory, AtomSuppression suppression, int depth) {
        super(pseudorandom);
        if (seedGenerator == null) throw new NullPointerException("seed generator cannot be null");
        if (depth < 0) throw new IllegalArgumentException("depth cannot be less then 0");
        this.seedGenerator = seedGenerator;
        this.factory = factory;
        this.suppression = suppression;
        this.depth = depth;
    }

    /**
     * Create a basic hash generator using the provided seed generator to
     * initialise atom invariants and no stereo configuration.
     *
     * @param seedGenerator generator to seed the initial values of atoms
     * @param pseudorandom  pseudorandom number generator used to randomise hash
     *                      distribution
     * @param suppression   defines which atoms are suppressed (i.e. masked)
     *                      from the hash code
     * @param depth         depth of the hashing function, larger values take
     *                      longer
     * @throws IllegalArgumentException depth was less then 0
     * @throws NullPointerException     seed generator or pseudo random was
     *                                  null
     * @see org.openscience.cdk.hash.SeedGenerator
     */
    public SuppressedAtomHashGenerator(AtomHashGenerator seedGenerator, Pseudorandom pseudorandom,
            AtomSuppression suppression, int depth) {
        this(seedGenerator, pseudorandom, StereoEncoderFactory.EMPTY, suppression, depth);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public long[] generate(IAtomContainer container) {
        int[][] graph = toAdjList(container);
        Suppressed suppressed = suppression.suppress(container);
        return generate(seedGenerator.generate(container), factory.create(container, graph), graph, suppressed);
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

        // for the stereo perception depending on how the
        // (BasicPermutationParity) is done we need to set the value to be as
        // high (or low) as possible
        for (int i : suppressed.toArray()) {
            current[i] = Long.MAX_VALUE;
        }

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
                next[v] = next(graph, v, current, unique, included, suppressed);
            }

            copy(next, current);

            while (encoder.encode(current, next)) {
                copy(next, current);
            }

        }

        // zero all suppressed values so they are not combined in any molecule
        // hash
        for (int i : suppressed.toArray()) {
            current[i] = 0L;
        }

        return current;
    }

    /**
     * Determine the next value of the atom at index <i>v</i>. The value is
     * calculated by combining the current values of adjacent atoms. When a
     * duplicate value is found it can not be directly included and is
     * <i>rotated</i> the number of times it has previously been seen.
     *
     * @param graph      adjacency list representation of connected atoms
     * @param v          the atom to calculate the next value for
     * @param current    the current values
     * @param unique     buffer for working out which adjacent values are unique
     * @param included   buffer for storing the rotated <i>unique</i> value, this
     *                   value is <i>rotated</i> each time the same value is
     *                   found.
     * @param suppressed bit set indicates which atoms are 'suppressed'
     * @return the next value for <i>v</i>
     */
    long next(int[][] graph, int v, long[] current, long[] unique, long[] included, Suppressed suppressed) {

        if (suppressed.contains(v)) return current[v];

        long invariant = distribute(current[v]);
        int nUnique = 0;

        for (int w : graph[v]) {

            // skip suppressed atom
            if (suppressed.contains(w)) continue;

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
