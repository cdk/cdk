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

/**
 * Generate a seed value for each atom of a molecule. The provided {@link
 * AtomEncoder} is used to encode invariant attributes of the atoms. This value
 * is then modified by the size of the molecule and pseudorandomly distributed.
 * The seed values should be used with another {@link AtomHashGenerator} which
 * will differentiate atoms experiencing different environments, such as, {@link
 * BasicAtomHashGenerator}.
 *
 * <blockquote><pre>
 *
 * // create a new seed generator
 * AtomEncoder       encoder   = ConjugatedAtomEncoder.create(ATOMIC_NUMBER,
 *                                                            MASS_NUMBER);
 * AtomHashGenerator generator = new SeedGenerator(encoder);
 *
 * // generate six hash codes for each atom of benzene
 * IAtomContainer benzene   = MoleculeFactory.makeBenzene();
 * long[]         hashCodes = generator.generate(benzene);
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module hash
 * @cdk.githash
 * @see BasicAtomHashGenerator
 * @see ConjugatedAtomEncoder
 */
final class SeedGenerator extends AbstractHashGenerator implements AtomHashGenerator {

    /* used to encode atom attributes */
    private final AtomEncoder     encoder;

    /** Optional suppression of atoms. */
    private final AtomSuppression suppression;

    /**
     * Create a new seed generator using the provided {@link AtomEncoder}.
     *
     * @param encoder a method for encoding atom invariant properties
     * @throws NullPointerException encoder was null
     * @see ConjugatedAtomEncoder
     */
    public SeedGenerator(AtomEncoder encoder) {
        this(encoder, new Xorshift(), AtomSuppression.unsuppressed());
    }

    /**
     * Create a new seed generator using the provided {@link AtomEncoder}.
     *
     * @param encoder a method for encoding atom invariant properties
     * @throws NullPointerException encoder was null
     * @see ConjugatedAtomEncoder
     */
    public SeedGenerator(AtomEncoder encoder, AtomSuppression suppression) {
        this(encoder, new Xorshift(), suppression);
    }

    /**
     * Create a new seed generator using the provided {@link AtomEncoder} and
     * pseudorandom number generator.
     *
     * @param encoder      a method for encoding atom invariant properties
     * @param pseudorandom number generator to randomise initial invariants
     * @param suppression  indicates which vertices should be suppressed
     * @throws NullPointerException encoder or pseudorandom number generator was
     *                              null
     */
    SeedGenerator(AtomEncoder encoder, Pseudorandom pseudorandom, AtomSuppression suppression) {
        super(pseudorandom);
        if (encoder == null) throw new NullPointerException("encoder cannot be null");
        if (suppression == null)
            throw new NullPointerException("suppression cannot be null, use AtomSuppression.unsuppressed()");
        this.encoder = encoder;
        this.suppression = suppression;
    }

    /**
     * @inheritDoc
     */
    @Override
    public long[] generate(IAtomContainer container) {

        Suppressed suppressed = suppression.suppress(container);

        int n = container.getAtomCount();
        int m = n - suppressed.count(); // number of non-suppressed vertices
        int seed = m > 1 ? 9803 % m : 1;

        long[] hashes = new long[n];

        for (int i = 0; i < n; i++) {
            hashes[i] = distribute(seed * encoder.encode(container.getAtom(i), container));
        }
        return hashes;
    }
}
