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


/**
 * Describes the geometric parity of a stereo configuration.
 *
 * @author John May
 * @cdk.module hash
 * @cdk.githash
 */
abstract class GeometricParity {

    /**
     * Calculate the geometric parity.
     *
     * @return -1 odd, +1 even and 0 none
     */
    abstract int parity();

    /**
     * Simple implementation allows us to wrap a predefined parity up for access
     * later. See {@link TetrahedralElementEncoderFactory} for usage example.
     */
    private static final class Predefined extends GeometricParity {

        /** the value which will be returned */
        private final int parity;

        /**
         * Create a new predefined geometric parity.
         *
         * @param parity value of the parity
         */
        private Predefined(int parity) {
            this.parity = parity;
        }

        /**{@inheritDoc} */
        @Override
        int parity() {
            return parity;
        }
    }

    /**
     * Create a geometric parity from a pre-stored value (-1, 0, +1).
     *
     * @param parity existing parity
     * @return instance which when invoked will return the value
     */
    static GeometricParity valueOf(int parity) {
        return new Predefined(parity);
    }
}
