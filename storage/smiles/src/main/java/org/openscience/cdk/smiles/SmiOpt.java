/*
 * Copyright (c) 2016 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.smiles;

import org.openscience.cdk.interfaces.IAtomContainer;

public final class SmiOpt {

    private SmiOpt() {
    }

    public static final int Canonical          = 0x001;
    public static final int InChILabelling     = 0x003;
    public static final int AtomAtomMap        = 0x004;
    public static final int Isotope            = 0x008;
    public static final int UseAromaticSymbols = 0x010;
    public static final int SuppressHydrogens  = 0x020;

    public static final int StereoTetrahedral   = 0x100;
    public static final int StereoCisTrans      = 0x200;
    public static final int StereoExTetrahedral = 0x400;
    public static final int Stereo              = StereoTetrahedral | StereoCisTrans | StereoExTetrahedral;

    public static final int Cx2dCoordinates    = 0x01000;
    public static final int Cx3dCoordinates    = 0x02000;
    public static final int CxCoordinates      = Cx3dCoordinates | Cx2dCoordinates;
    public static final int CxAtomLabel        = 0x008000;
    public static final int CxAtomValue        = 0x010000;
    public static final int CxRadical          = 0x020000;
    public static final int CxMulticenter      = 0x030000;
    public static final int CxPolymer          = 0x080000;
    public static final int CxFragmentGroup    = 0x100000;
    public static final int CxSmiles           = CxAtomLabel | CxAtomValue | CxRadical | CxFragmentGroup | CxMulticenter | CxPolymer;
    public static final int CxSmilesWithCoords = CxSmiles | CxCoordinates;

    public static final int UniversalSmiles = InChILabelling | Stereo | Isotope;
    public static final int Absolute        = UniversalSmiles;
    public static final int Isomeric        = Stereo | Isotope;

    static boolean isSet(int opts, int opt) {
        return (opts & opt) != 0;
    }
}
