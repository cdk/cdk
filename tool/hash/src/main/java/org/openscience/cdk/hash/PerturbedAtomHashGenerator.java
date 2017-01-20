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

import java.util.Arrays;
import java.util.Set;

/**
 * A perturbed hash generator {@cdk.cite Ihlenfeldt93} which differentiates
 * molecules with uniform atom environments and symmetry. The generator first
 * calculates the basic hash codes ({@link BasicAtomHashGenerator}) and then
 * checks for duplicate values (uniform environments). These duplicate values
 * are then filtered down ({@link EquivalentSetFinder}) to a set (<i>S</i>)
 * which can introduce systematic differences with. We then combine the
 * |<i>S</i>| different invariant values with the original value to produce a
 * unique value of each atom. There may still be duplicate values but providing
 * the depth is appropriate then the atoms are truly equivalent.
 * <p/><br/>
 * The class requires a lot of configuration however it can be easily built with
 * the {@link HashGeneratorMaker}.
 * <blockquote><pre>
 * MoleculeHashGenerator generator = new HashGeneratorMaker().depth(8)
 *                                                           .elemental()
 *                                                           .perturbed()
 *                                                           .molecular();
 * IAtomContainer molecule = ...;
 * long hash = generator.generate(molecule);
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module hash
 * @see org.openscience.cdk.hash.SeedGenerator
 * @see <a href="http://onlinelibrary.wiley.com/doi/10.1002/jcc.540150802/abstract">Original
 *      Publication</a>
 * @cdk.githash
 * @see HashGeneratorMaker
 */
final class PerturbedAtomHashGenerator extends AbstractHashGenerator implements AtomHashGenerator {

    /* creates stereo encoders for IAtomContainers */
    private final StereoEncoderFactory      factory;

    /* simple hash generator */
    private final AbstractAtomHashGenerator simple;

    /* seed generator */
    private final AtomHashGenerator         seeds;

    /* find the set of vertices in which we will add systematic differences */
    private final EquivalentSetFinder       finder;

    /* suppression of atoms */
    private final AtomSuppression           suppression;

    /**
     * Create a perturbed hash generator using the provided seed generator to
     * initialise atom invariants and using the provided stereo factory.
     *
     * @param simple        generator to encode the initial values of atoms
     * @param pseudorandom  pseudorandom number generator used to randomise hash
     *                      distribution
     * @param factory       a stereo encoder factory
     * @param finder        equivalent set finder for driving the systematic
     *                      perturbation
     * @param suppression   suppression of atoms (these atoms are 'ignored'
     *                      in the hash generation)
     * @throws IllegalArgumentException depth was less then 0
     * @throws NullPointerException     seed generator or pseudo random was
     *                                  null
     * @see org.openscience.cdk.hash.SeedGenerator
     */
    public PerturbedAtomHashGenerator(SeedGenerator seeds, AbstractAtomHashGenerator simple, Pseudorandom pseudorandom,
            StereoEncoderFactory factory, EquivalentSetFinder finder, AtomSuppression suppression) {

        super(pseudorandom);
        if (simple == null) throw new NullPointerException("no simple generator provided");
        if (seeds == null) throw new NullPointerException("no seed generator provided");
        if (suppression == null) throw new NullPointerException("no suppression provided, use AtomSuppression.none()");
        this.finder = finder;
        this.factory = factory;
        this.simple = simple;
        this.seeds = seeds;
        this.suppression = suppression;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public long[] generate(IAtomContainer container) {
        int[][] graph = toAdjList(container);
        return generate(container, seeds.generate(container), factory.create(container, graph), graph);
    }

    private long[] generate(IAtomContainer container, long[] seeds, StereoEncoder encoder, int[][] graph) {

        Suppressed suppressed = suppression.suppress(container);

        // compute original values then find indices equivalent values
        long[] original = simple.generate(seeds, encoder, graph, suppressed);
        Set<Integer> equivalentSet = finder.find(original, container, graph);
        Integer[] equivalents = equivalentSet.toArray(new Integer[equivalentSet.size()]);

        // size of the matrix we need to make
        int n = original.length;
        int m = equivalents.length;

        // skip when there are no equivalent atoms
        if (m < 2) return original;

        // matrix of perturbed values and identity values
        long[][] perturbed = new long[n][m + 1];

        // set the original values in the first column
        for (int i = 0; i < n; i++) {
            perturbed[i][0] = original[i];
        }

        // systematically perturb equivalent vertex
        for (int i = 0; i < m; i++) {

            int equivalentIndex = equivalents[i];

            // perturb the value and reset stereo configuration
            original[equivalentIndex] = rotate(original[equivalentIndex]);
            encoder.reset();

            // compute new hash codes and copy the values a column in the matrix
            long[] tmp = simple.generate(copy(original), encoder, graph, suppressed);
            for (int j = 0; j < n; j++) {
                perturbed[j][i + 1] = tmp[j];
            }

            // reset value
            original[equivalentIndex] = perturbed[equivalentIndex][0];
        }

        return combine(perturbed);
    }

    /**
     * Combines the values in an n x m matrix into a single array of size n.
     * This process scans the rows and xors all unique values in the row
     * together. If a duplicate value is found it is rotated using a
     * pseudorandom number generator.
     *
     * @param perturbed n x m, matrix
     * @return the combined values of each row
     */
    long[] combine(long[][] perturbed) {

        int n = perturbed.length;
        int m = perturbed[0].length;

        long[] combined = new long[n];
        long[] rotated = new long[m];

        for (int i = 0; i < n; i++) {

            Arrays.sort(perturbed[i]);

            for (int j = 0; j < m; j++) {
                // if non-unique, then get the next random number
                if (j > 0 && perturbed[i][j] == perturbed[i][j - 1]) {
                    combined[i] ^= rotated[j] = rotate(rotated[j - 1]);
                } else {
                    combined[i] ^= rotated[j] = perturbed[i][j];
                }
            }

        }

        return combined;
    }

}
