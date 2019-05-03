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

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Arrays;

/**
 * A generator for basic molecule hash codes {@cdk.cite Ihlenfeldt93}. The
 * provided {@link AtomHashGenerator} is used to produce individual atom hash
 * codes. These are then combined together in an order independent manner to
 * generate a single hash code for the molecule.
 *
 * <blockquote><pre>
 * AtomHashGenerator     atomGenerator = ...;
 * MoleculeHashGenerator generator     = new BasicMoleculeHashGenerator(atomGenerator)
 *
 * IAtomContainer benzene  = MoleculeFactory.makeBenzene();
 * long           hashCode = generator.generate(benzene);
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module hash
 * @see AtomHashGenerator
 * @see BasicAtomHashGenerator
 * @cdk.githash
 */
final class BasicMoleculeHashGenerator implements MoleculeHashGenerator {

    /* generator for atom hashes */
    private final AtomHashGenerator generator;

    /* pseudorandom number generator */
    private final Pseudorandom      pseudorandom;

    /**
     * Create a new molecule hash using the provided atom hash generator.
     *
     * @param generator a generator for atom hash codes
     * @throws NullPointerException no generator provided
     */
    public BasicMoleculeHashGenerator(AtomHashGenerator generator) {
        this(generator, new Xorshift());
    }

    /**
     * Create a new molecule hash using the provided atom hash generator and
     * pseudorandom number generator.
     *
     * @param generator    a generator for atom hash codes
     * @param pseudorandom pseudorandom number generator
     * @throws NullPointerException no atom hash generator or pseudorandom
     *                              number generator provided
     */
    BasicMoleculeHashGenerator(AtomHashGenerator generator, Pseudorandom pseudorandom) {
        if (generator == null) throw new NullPointerException("no AtomHashGenerator provided");
        if (pseudorandom == null) throw new NullPointerException("no Pseudorandom number generator provided");
        this.generator = generator;
        this.pseudorandom = pseudorandom;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public long generate(IAtomContainer container) {

        long[] hashes = generator.generate(container);
        long[] rotated = new long[hashes.length];

        Arrays.sort(hashes);

        // seed with Mersenne prime 2^31-1
        long hash = 2147483647L;

        for (int i = 0; i < hashes.length; i++) {

            // if non-unique, then get the next random number
            if (i > 0 && hashes[i] == hashes[i - 1]) {
                hash ^= rotated[i] = pseudorandom.next(rotated[i - 1]);
            } else {
                hash ^= rotated[i] = hashes[i];
            }
        }

        return hash;
    }
}
