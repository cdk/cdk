/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.interfaces;

import java.util.Map;

/**
 * Stereochemistry specification for quadrivalent atoms. The data model defines the central, chiral
 * {@link IAtom}, and its four ligand {@link IAtom}s, directly bonded to the chiral atom via an
 * {@link IBond}. The ordering of the four ligands is important, and defines together with the
 * {@link Stereo} to spatial geometry around the chiral atom. The first ligand points towards to
 * observer, and the three other ligands point away from the observer; the {@link Stereo} then
 * defines the order of the second, third, and fourth ligand to be clockwise or anti-clockwise.
 *
 * <p>If the tetrahedral centre has an implicit hydrogen or lone pair then the chiral atom is also
 * stored as one of the ligands. This serves as a placeholder to indicate where the implicit
 * hydrogen or lone pair would be.
 *
 * @cdk.module interfaces
 * @cdk.githash
 */
public interface ITetrahedralChirality extends IStereoElement<IAtom, IAtom> {

    /** Enumeration that defines the two possible chiralities for this stereochemistry type. */
    enum Stereo {
        CLOCKWISE,
        ANTI_CLOCKWISE;

        public static int toConfig(Stereo stereo) {
            switch (stereo) {
                case ANTI_CLOCKWISE:
                    return LEFT;
                case CLOCKWISE:
                    return RIGHT;
                default:
                    throw new IllegalArgumentException("Unknown enum value: " + stereo);
            }
        }

        public static Stereo toStereo(int cfg) {
            switch (cfg) {
                case LEFT:
                    return ANTI_CLOCKWISE;
                case RIGHT:
                    return CLOCKWISE;
                default:
                    throw new IllegalArgumentException("Cannot map to enum value: " + cfg);
            }
        }

        /**
         * Invert this conformation, inv(clockwise) = anti_clockwise, inv(anti_clockwise) =
         * clockwise.
         *
         * @return the inverse conformation
         */
        public Stereo invert() {
            switch (this) {
                case CLOCKWISE:
                    return ANTI_CLOCKWISE;
                case ANTI_CLOCKWISE:
                    return CLOCKWISE;
            }
            return this;
        }
    }

    /**
     * Returns an array of ligand atoms around the chiral atom. If the chiral centre has an implicit
     * hydrogen or lone pair one of the ligands will be the chiral atom ({@link #getChiralAtom()}).
     *
     * @return an array of four {@link IAtom}s.
     */
    IAtom[] getLigands();

    /**
     * Atom that is the chirality center.
     *
     * @return the chiral {@link IAtom}.
     */
    IAtom getChiralAtom();

    /**
     * Defines the stereochemistry around the chiral atom. The value depends on the order of ligand
     * atoms.
     *
     * @return the {@link Stereo} for this stereo element.
     */
    Stereo getStereo();

    /**
     * Set the stereochemistry of this tetrahedral centre.
     *
     * @param stereo the new stereo assignment
     */
    void setStereo(Stereo stereo);

    /** {@inheritDoc} */
    @Override
    public ITetrahedralChirality map(Map<IAtom, IAtom> atoms, Map<IBond, IBond> bonds);
}
