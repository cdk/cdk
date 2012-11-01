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
 * Stereochemistry specification for quadrivalent atoms. The data model defines the central, chiral {@link IAtom},
 * and its four ligand {@link IAtom}s, directly bonded to the chiral atom via an {@link IBond}. The ordering of the
 * four ligands is important, and defines together with the {@link Stereo} to spatial geometry around the chiral atom.
 * The first ligand points towards to observer, and the three other ligands point away from the observer; the
 * {@link Stereo} then defines the order of the second, third, and fourth ligand to be clockwise or anti-clockwise.
 *
 * @cdk.module interfaces
 * @cdk.githash
 */
public interface ITetrahedralChirality extends IStereoElement {

    /**
     * Enumeration that defines the two possible chiralities for this stereochemistry type.
     */
    public enum Stereo {
        CLOCKWISE,
        ANTI_CLOCKWISE
    }

    /**
     * Returns an array of ligand atoms around the chiral atom.
     *
     * @return an array of four {@link IAtom}s.
     */
    public IAtom[] getLigands();

    /**
     * Atom that is the chirality center.
     *
     * @return the chiral {@link IAtom}.
     */
    public IAtom getChiralAtom();

    /**
     * Defines the stereochemistry around the chiral atom. The value depends on the order of ligand atoms.
     *
     * @return the {@link Stereo} for this stereo element.
     */
    public Stereo getStereo();


    /**
     * @inheritDoc
     */
    @Override
    public ITetrahedralChirality map(Map<IAtom, IAtom> atoms, Map<IBond, IBond> bonds);


}
