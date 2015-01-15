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
package org.openscience.cdk.geometry.cip;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

/**
 * Stereochemistry specification for quadrivalent atoms to be used for the CIP algorithm only.
 *
 * <p>The data model defines the central, chiral {@link IAtom},
 * and its four {@link ILigand}s, each of which has an ligand {@link IAtom}, directly bonded to the chiral atom via
 * an {@link IBond}. The ordering of the four ligands is important, and defines together with the {@link STEREO}
 * to spatial geometry around the chiral atom. The first ligand points towards to observer, and the three other
 * ligands point away from the observer; the {@link STEREO} then defines the order of the second, third, and
 * fourth ligand to be clockwise or anti-clockwise.
 *
 * @cdk.module cip
 * @cdk.githash
 */
class LigancyFourChirality {

    private IAtom                        chiralAtom;
    private ILigand[]                    ligands;
    private ITetrahedralChirality.Stereo stereo;

    /**
     * Creates a new data model for chirality for the CIP rules.
     *
     * @param chiralAtom The {@link IAtom} that is actually chiral.
     * @param ligands    An array with exactly four {@link ILigand}s.
     * @param stereo     A indication of clockwise or anticlockwise orientation of the atoms.
     *
     * @see ITetrahedralChirality.Stereo
     */
    public LigancyFourChirality(IAtom chiralAtom, ILigand[] ligands, ITetrahedralChirality.Stereo stereo) {
        this.chiralAtom = chiralAtom;
        this.ligands = ligands;
        this.stereo = stereo;
    }

    /**
     * Creates a new data model for chirality for the CIP rules based on a chirality definition
     * in the CDK data model with {@link ITetrahedralChirality}.
     *
     * @param container    {@link IAtomContainer} to which the chiral atom belongs.
     * @param cdkChirality {@link ITetrahedralChirality} object specifying the chirality.
     */
    public LigancyFourChirality(IAtomContainer container, ITetrahedralChirality cdkChirality) {
        this.chiralAtom = cdkChirality.getChiralAtom();
        IAtom[] ligandAtoms = cdkChirality.getLigands();
        this.ligands = new ILigand[ligandAtoms.length];
        VisitedAtoms visitedAtoms = new VisitedAtoms();
        for (int i = 0; i < ligandAtoms.length; i++) {
            // ITetrahedralChirality stores a impl hydrogen as the central atom
            if (ligandAtoms[i] == chiralAtom) {
                this.ligands[i] = new ImplicitHydrogenLigand(container, visitedAtoms, chiralAtom);
            } else {
                this.ligands[i] = new Ligand(container, visitedAtoms, chiralAtom, ligandAtoms[i]);
            }
        }
        this.stereo = cdkChirality.getStereo();
    }

    /**
     * Returns the four ligands for this chirality.
     *
     * @return An array of four {@link ILigand}s.
     */
    public ILigand[] getLigands() {
        return ligands;
    }

    /**
     * Returns the chiral {@link IAtom} to which the four ligands are connected..
     *
     * @return The chiral {@link IAtom}.
     */
    public IAtom getChiralAtom() {
        return chiralAtom;
    }

    /**
     * Returns the chirality value for this stereochemistry object.
     *
     * @return A {@link ITetrahedralChirality.Stereo} value.
     */
    public ITetrahedralChirality.Stereo getStereo() {
        return stereo;
    }

    /**
     * Recalculates the {@link LigancyFourChirality} based on the new, given atom ordering.
     *
     * @param newOrder new order of atoms
     * @return the chirality following the new atom order
     */
    public LigancyFourChirality project(ILigand[] newOrder) {
        ITetrahedralChirality.Stereo newStereo = this.stereo;
        // copy the current ordering, and work with that
        ILigand[] newAtoms = new ILigand[4];
        System.arraycopy(this.ligands, 0, newAtoms, 0, 4);

        // now move atoms around to match the newOrder
        for (int i = 0; i < 3; i++) {
            if (newAtoms[i].getLigandAtom() != newOrder[i].getLigandAtom()) {
                // OK, not in the right position
                // find the incorrect, old position
                for (int j = i; j < 4; j++) {
                    if (newAtoms[j].getLigandAtom() == newOrder[i].getLigandAtom()) {
                        // found the incorrect position
                        swap(newAtoms, i, j);
                        // and swap the stereochemistry
                        if (newStereo == Stereo.CLOCKWISE) {
                            newStereo = Stereo.ANTI_CLOCKWISE;
                        } else {
                            newStereo = Stereo.CLOCKWISE;
                        }
                    }
                }
            }
        }
        return new LigancyFourChirality(chiralAtom, newAtoms, newStereo);
    }

    private void swap(ILigand[] ligands, int first, int second) {
        ILigand tmpLigand = ligands[first];
        ligands[first] = ligands[second];
        ligands[second] = tmpLigand;
    }
}
