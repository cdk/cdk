/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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

package org.openscience.cdk.aromaticity;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ringsearch.RingSearch;

/**
 * Defines an electron donation model for perceiving aromatic systems. The model
 * defines which atoms are allowed and how many electron it contributes.
 * Currently the available models are.
 *
 * <ul> <li>CDK Atom Type - uses the information form the preset CDK atom types
 * to determine how many electrons each atom should contribute. The model can
 * either allow or exclude contributions from exocyclic pi bonds {@see
 * #cdkAtomTypes}. </li> </ul>
 *
 * @author John May
 * @cdk.module standard
 */
public abstract class ElectronDonation {

    /**
     * Determine the number 'p' electron contributed by each atom in the
     * provided {@code container}. A value of '0' indicates the atom can
     * contribute but that it contributes no electrons. A value of '-1'
     * indicates the atom should not contribute at all.
     *
     * @param container  molecule
     * @param ringSearch ring information
     * @return
     */
    abstract int[] contribution(IAtomContainer container, RingSearch ringSearch);

    /**
     * Use the preset CDK atom types to determine the electron contribution of
     * atoms. If an atom type has not been perceived or hybridisation is unset a
     * runtime exception is thrown.
     *
     * <i>TODO - flesh out model description</i>
     *
     * @param exocyclic allow exocyclic (sprouting) pi bonds to contribute
     * @return electron donation model to use for aromaticity perception
     * @see org.openscience.cdk.interfaces.IAtom#getAtomTypeName()
     */
    public static ElectronDonation cdkAtomTypes(boolean exocyclic) {
        return new AtomTypeModel(exocyclic);
    }

    /**
     * A very simple aromaticity model which only allows atoms adjacent to
     * cyclic pi bonds. Lone pairs are not consider and as such molecules like
     * furan and pyrrole are non-aromatic. The model is useful for storing
     * aromaticity in MDL and Mol2 file formats where aromatic systems involving
     * a lone pair can not be properly represented.
     *
     * @return electron donation model to use for aromaticity perception
     */
    public static ElectronDonation piBonds() {
        return new PiBondModel();
    }

    /**
     * Electron donation model closely mirroring the Daylight model for use in
     * generating SMILES. The model was interpreted from various resources and
     * as such may not match exactly. If you find an inconsistency please add a
     * request for enhancement to the patch tracker. One known limitation is
     * that this model does not currently consider unknown/pseudo atoms '*'.
     * <p/>
     *
     * The model makes a couple of assumptions which it will not correct for.
     * Checked assumptions cause the model to throw a runtime exception. <ul>
     * <li>there should be no valence errors (unchecked)</li> <li>every atom has
     * a set implicit hydrogen count (checked)</li> <li>every bond has defined
     * order, single, double etc (checked)</li> <li>atomic number of non-pseudo
     * atoms is set (checked)</li> </ul> <p/>
     *
     * The aromaticity model in SMILES was designed to simplify canonicalisation
     * and express symmetry in a molecule. The contributed electrons can be
     * summarised as follows (refer to code for exact specification): <ul>
     * <li>carbon, nitrogen, oxygen, phosphorus, sulphur, arsenic and selenium
     * are allow to be aromatic</li> <li>atoms should be Sp2 hybridised - not
     * actually computed</li> <li>atoms adjacent to a single cyclic pi bond
     * contribute 1 electron</li> <li>neutral or negatively charged atoms with a
     * lone pair contribute 2 electrons</li> <li>exocyclic pi bonds are allowed
     * but if the exocyclic atom is more electronegative it consumes an
     * electron. As an example ketone groups contribute '0'
     * electrons.</li></ul>
     */
    public static ElectronDonation daylight() {
        return new DaylightModel();
    }
}
