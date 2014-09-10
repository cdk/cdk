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

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Describes a factory for stereo elements. The factory create encoders for
 * specific stereo elements.
 *
 * @author John May
 * @cdk.module hash
 * @cdk.githash
 */
public interface StereoEncoderFactory {

    /**
     * Empty factory for when stereo encoding is not required
     */
    public static StereoEncoderFactory EMPTY = new StereoEncoderFactory() {

                                                 @Override
                                                 public StereoEncoder create(IAtomContainer container, int[][] graph) {
                                                     return StereoEncoder.EMPTY;
                                                 }
                                             };

    /**
     * Create a stereo-encoder for possible stereo-chemical configurations.
     *
     * @param container the container
     * @param graph     adjacency list representation of the container
     * @return a new stereo encoder
     */
    public StereoEncoder create(IAtomContainer container, int[][] graph);

}
