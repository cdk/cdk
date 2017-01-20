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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

import java.util.Map;

/**
 * Stereochemistry specification for quadrivalent atoms. See {@link ITetrahedralChirality} for
 * further details.
 *
 * @cdk.module core
 * @cdk.githash
 *
 * @see org.openscience.cdk.interfaces.ITetrahedralChirality
 */
public class TetrahedralChirality implements ITetrahedralChirality {

    private IAtom              chiralAtom;
    private IAtom[]            ligandAtoms;
    private Stereo             stereo;
    private IChemObjectBuilder builder;

    /**
     * Constructor to create a new {@link ITetrahedralChirality} implementation instance.
     *
     * @param chiralAtom  The chiral {@link IAtom}.
     * @param ligandAtoms The ligand atoms around the chiral atom.
     * @param chirality   The {@link Stereo} chirality.
     */
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
    @Override
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
    @Override
    public IAtom getChiralAtom() {
        return chiralAtom;
    }

    /**
     * Defines the stereochemistry around the chiral atom. The value depends on the order of ligand atoms.
     *
     * @return the {@link Stereo} for this stereo element.
     */
    @Override
    public Stereo getStereo() {
        return stereo;
    }

    /** {@inheritDoc} */
    @Override public void setStereo(Stereo stereo) {
        this.stereo = stereo;
    }

    /**
     * Sets a new {@link IChemObjectBuilder}.
     *
     * @param builder the new {@link IChemObjectBuilder} to be returned
     * @see #getBuilder()
     */
    public void setBuilder(IChemObjectBuilder builder) {
        this.builder = builder;
    }

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return builder;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean contains(IAtom atom) {
        if (chiralAtom.equals(atom)) return true;
        for (IAtom ligand : ligandAtoms)
            if (ligand.equals(atom)) return true;
        return false;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public ITetrahedralChirality map(Map<IAtom, IAtom> atoms, Map<IBond, IBond> bonds) {

        // don't check bond map as we don't use it
        if (atoms == null) throw new IllegalArgumentException("null atom mapping provided");

        // convert the chiral atom and it's ligands to their equivalent
        IAtom chiral = chiralAtom != null ? atoms.get(chiralAtom) : null;
        IAtom[] ligands = new IAtom[ligandAtoms.length];

        for (int i = 0; i < ligands.length; i++) {
            if (ligandAtoms[i] != null) ligands[i] = atoms.get(ligandAtoms[i]);
        }

        // create a new tetrahedral instance with the mapped chiral atom and ligands
        return new TetrahedralChirality(chiral, ligands, stereo);

    }

    /**
     * Returns a {@link String} representation of this chiral element.
     *
     * @return the String representation
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tetrahedral{").append(this.hashCode()).append(", ");
        builder.append(this.getStereo()).append(", ");
        builder.append("c:").append(this.getChiralAtom()).append(", ");
        IAtom[] ligands = this.getLigands();
        for (int i = 0; i < ligands.length; i++) {
            builder.append(i + 1).append(':').append(ligands[i]).append(", ");
        }
        builder.append('}');
        return builder.toString();
    }
}
