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
 * An encoder for stereo chemistry. The stereo configuration is encoded by
 * checking the {@code current[]} invariant values. If there is a configuration
 * then the appropriate value is the {@code next[]} is modified.
 *
 * @author John May
 * @cdk.module hash
 */
public interface StereoEncoder {

    /**
     * empty stereo encoder when no stereo can be perceived
     */
    public static StereoEncoder EMPTY = new StereoEncoder() {

                                          @Override
                                          public boolean encode(long[] current, long[] next) {
                                              return false;
                                          }

                                          @Override
                                          public void reset() {}
                                      };

    /**
     * Encode one or more stereo elements based on the current invariants. If
     * any stereo element are uncovered then the corresponding value in the
     * next[] array is modified.
     *
     * @param current current invariants
     * @param next    next invariants
     * @return whether any stereo configurations were encoded
     */
    public boolean encode(long[] current, long[] next);

    /**
     * Reset the stereo-encoders, any currently perceived configurations will be
     * re-activated.
     */
    public void reset();

}
