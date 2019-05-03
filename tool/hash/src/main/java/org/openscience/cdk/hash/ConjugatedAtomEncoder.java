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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An atom encoder which takes several atom encodes and combines the encodings
 * into a single encoder. The order of the encoders matter and for persistent
 * results should be ordered before construction.
 *
 * <blockquote><pre>
 * // import org.openscience.cdk.hash.seed.BasicAtomEncoder.*
 * AtomEncoder encoder = new ConjugatedAtomEncoder(Arrays.asList(ATOMIC_NUMBER,
 *                                                               FORMAL_CHARGE));
 *
 * // convenience constructor using var-args
 * AtomEncoder encoder = ConjugatedAtomEncoder.create(ATOMIC_NUMBER,
 *                                                    FORMAL_CHARGE);
 *
 * // specifying a custom encoder
 * AtomEncoder encoder =
 *   ConjugatedAtomEncoder.create(ATOMIC_NUMBER,
 *                                FORMAL_CHARGE,
 *                                new AtomEncoder(){
 *                                  public int encode(IAtom a, IAtomContainer c){
 *                                    return a.getSymbol().hashCode();
 *                                  }
 *                                });
 *
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module hash
 * @cdk.githash
 */
final class ConjugatedAtomEncoder implements AtomEncoder {

    /* ordered list of encoders */
    private final List<AtomEncoder> encoders;

    /**
     * Create a new conjugated encoder for the specified list of atom encoders.
     * The encoders are combined in an order dependant manner.
     *
     * @param encoders non-empty list of encoders
     * @throws NullPointerException     the list of encoders was null
     * @throws IllegalArgumentException the list of encoders was empty
     */
    public ConjugatedAtomEncoder(List<AtomEncoder> encoders) {
        if (encoders == null) throw new NullPointerException("null list of encoders");
        if (encoders.isEmpty()) throw new IllegalArgumentException("no encoders provided");
        this.encoders = Collections.unmodifiableList(new ArrayList<AtomEncoder>(encoders));
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int encode(IAtom atom, IAtomContainer container) {
        int hash = 179426549;
        for (AtomEncoder encoder : encoders)
            hash = 31 * hash + encoder.encode(atom, container);
        return hash;
    }

    /**
     * Convenience method for creating a conjugated encoder from one or more
     * {@link AtomEncoder}s.
     *
     * <blockquote><pre>
     * // import org.openscience.cdk.hash.seed.BasicAtomEncoder.*
     * AtomEncoder encoder = ConjugatedAtomEncoder.create(ATOMIC_NUMBER,
     *                                                    FORMAL_CHARGE);
     * </pre></blockquote>
     *
     * @param encoder  the first encoder
     * @param encoders the other encoders
     * @return a new conjugated encoder
     * @throws NullPointerException either argument was null
     */
    public static AtomEncoder create(AtomEncoder encoder, AtomEncoder... encoders) {
        if (encoder == null || encoders == null) throw new NullPointerException("null encoders provided");
        List<AtomEncoder> tmp = new ArrayList<AtomEncoder>(encoders.length + 1);
        tmp.add(encoder);
        for (AtomEncoder e : encoders)
            tmp.add(e);
        return new ConjugatedAtomEncoder(tmp);
    }
}
