/* Copyright (C) 2026 John Mayfield
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Older versions of the MDL/CTfile format would store mass as "difference from
 * mass in periodic table". Fortunately most common elements have well known
 * standard masses but for some rare elements toolkits don't always agree
 * {@cdk.cite Mayfield2017}.
 * <p/>
 * @author John Mayfield
 */
final class MDLIsotope {

    private MDLIsotope() {}

    // Isotopic mass can be stored as a relative value, this table defines
    // what the mass is relative to. It is not always the major isotope and
    // different toolkits disagree on some elements.
    // Ultimately absolute mass values specified with M  ISO are preferred
    // Note: 98-Tc, 162-Dy, 252-Es, 262-Lr differ from MDL/Symyx, they were
    //       99-Tc, 163-Dy, 254-Es, 260-Lr
    // Note: Nh, Mc, Ts, Og are not confirmed but reasonable guesses are made
    private static final int[] REFERENCE = new int[]{
            0,                                                                                                                                                              // R
            1,                                                                                                                                                           4, // H  ... He
            7, 9,                                                                                                                                   11, 12, 14, 16, 19, 20, // Li ... Ne
            23, 24,                                                                                                                                 27, 28, 31, 32, 35, 40, // Na ... Ar
            39, 40, 45,                                                                                         48, 51, 52, 55, 56, 59, 59, 64, 65, 70, 73, 75, 79, 80, 84, // K  ... Kr
            85, 88, 89,                                                                              91, 93, 96, 98, 101, 103, 106, 108, 112, 115, 119, 122, 128, 127, 131, // Rb ... Xe
            133, 137, 139, 140, 141, 144, 145, 150, 152, 157, 159, 162, 165, 167, 169, 173, 175, 178, 181, 184, 186, 190, 192, 195, 197, 201, 204, 207, 209, 209, 210, 222, // Cs ... Rn
            223, 226, 227, 232, 231, 238, 237, 244, 243, 247, 247, 251, 252, 257, 258, 259, 262, 261, 262, 266, 264, 277, 268, 280, 280, 285, 286, 289, 289, 293, 294, 294  // Fr ... Og
    };

    /**
     * Set the absolute mass of an atom given a difference, e.g. Carbon and +1
     * means C-13 isotope.
     *
     * @param atom the atom
     * @param diff the mass difference
     * @return the mass was set, if false the element is not supported
     */
    public static boolean setAbsoluteMass(IAtom atom, int diff) {
        int elem = atom.getAtomicNumber();
        if (elem < 0 || elem >= REFERENCE.length)
            return false;
        atom.setMassNumber(REFERENCE[elem] + diff);
        return true;
    }

    /**
     * Get the relative mass of an atom given an atom with absolute atomic mass,
     * e.g. C-13 isotope +1. If the mass difference is &lt;-3 or &gt4 0 is
     * returned.
     *
     * @param atom the atom
     * @return the mass was set, if false the element is not supported
     */
    public static int getRelativeMass(IAtom atom) {
        Integer massNumber = atom.getMassNumber();
        if (massNumber == null || massNumber == 0)
            return 0;
        int elem = atom.getAtomicNumber();
        if (elem < 0 || elem >= REFERENCE.length)
            return 0;
        int delta = massNumber - REFERENCE[elem];
        return delta >= -3 && delta <= 4 ? delta : 0;
    }
}
