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
package org.openscience.cdk.stereo;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ITetrahedralChirality;

/**
 * Stereochemistry specification for quadrivalent atoms. See {@link org.openscience.cdk.interfaces.ITetrahedralChirality} for
 * further details.
 *
 * @cdk.module data
 *
 * @see org.openscience.cdk.interfaces.ITetrahedralChirality
 */
public class TetrahedralChirality implements ITetrahedralChirality {

    private IAtom chiralAtom;
    private IAtom[] ligandAtoms;
    private Stereo stereo;

    public TetrahedralChirality(IAtom chiralAtom, IAtom[] ligandAtoms, Stereo chirality) {
        this.chiralAtom = chiralAtom;
        this.ligandAtoms = ligandAtoms;
        this.stereo = chirality;
    }

    /**
     * Returns an array of ligand atoms around the chiral atom.
     *
     * @return an array of four {@link IAtom}s.
     */
    public IAtom[] getLigands() {
        IAtom[] arrayCopy = new IAtom[4];
        System.arraycopy(ligandAtoms, 0, arrayCopy, 0, 4);
        return arrayCopy;
    }

    /**
     * Atom that is the chirality center.
     *
     * @return the chiral {@link IAtom}.
     */
    public IAtom getChiralAtom() {
        return chiralAtom;
    }

    /**
     * Defines the stereochemistry around the chiral atom. The value depends on the order of ligand atoms.
     *
     * @return the {@link Stereo} for this stereo element.
     */
    public Stereo getStereo() {
        return stereo;
    }

    public IChemObjectBuilder getBuilder() {
        return DefaultChemObjectBuilder.getInstance();
    }

}
