/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.geometry.volume;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * Calculates the Vanderwaals volume using the method proposed
 * in {@cdk.cite Zhao2003}. The method is limited to molecules
 * with the folowing elements: H, C, N, O, F, Cl, Br, I,
 * P, S, As, B, Si, Se, and Te.
 *
 * @cdk.module   standard
 * @cdk.keywords volume, molecular
 */
@TestClass("org.openscience.cdk.geometry.volume.VABCVolumeTest")
public class VABCVolume {

    /**
     * Calculates the volume for the given {@link IMolecule}.
     *
     * @param  molecule {@link IMolecule} to calculate the volume of.
     * @return          the volume in cubic &Aring;ngstr&ouml;m.
     */
    public static double calculate(IMolecule molecule) {
        return 0.0;
    }

}
