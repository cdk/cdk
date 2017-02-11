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

package org.openscience.cdk.hash.stereo;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * A multiple stereo encoder. Given a list of other encoders this class wraps
 * them up into a single method call. Once each encoder has been configured it
 * is marked and will not be visited again unless the encoder is {@link
 * #reset()}.
 *
 * @author John May
 * @cdk.module hash
 */
final class MultiStereoEncoder implements StereoEncoder {

    /* indices of unconfigured encoders */
    private final BitSet              unconfigured;

    /* list of encoders */
    private final List<StereoEncoder> encoders;

    /**
     * Create a new multiple stereo encoder from a single list of encoders
     */
    public MultiStereoEncoder(List<StereoEncoder> encoders) {
        if (encoders.isEmpty()) throw new IllegalArgumentException("no stereo encoders provided");
        this.encoders = Collections.unmodifiableList(new ArrayList<StereoEncoder>(encoders));
        this.unconfigured = new BitSet(encoders.size());
        unconfigured.flip(0, encoders.size());
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean encode(long[] current, long[] next) {
        boolean configured = false;

        for (int i = unconfigured.nextSetBit(0); i >= 0; i = unconfigured.nextSetBit(i + 1)) {

            if (encoders.get(i).encode(current, next)) {
                unconfigured.clear(i); // don't configure again (unless reset)
                configured = true;
            }
        }
        return configured;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void reset() {
        // mark all as unperceived and reset
        for (int i = 0; i < encoders.size(); i++) {
            unconfigured.set(i);
            encoders.get(i).reset();
        }
    }
}
