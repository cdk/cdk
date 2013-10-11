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
     * @return
     * @see org.openscience.cdk.interfaces.IAtom#getAtomTypeName()
     */
    static ElectronDonation cdkAtomTypes(boolean exocyclic) {
        return new AtomTypeModel(exocyclic);
    }
}
