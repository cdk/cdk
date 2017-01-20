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
 * defines which atoms are allowed and how many electron it contributes. There
 * are currently several models available.
 *
 * <ul>
 *     <li>{@link #cdk()}/{@link #cdkAllowingExocyclic()} - uses the information
 *     form the preset CDK atom types to determine how many electrons each atom
 *     should contribute. The model can either allow or exclude contributions
 *     from exocyclic pi bonds. This model requires that atom types have be
 *     perceived.
 * </li>
 *     <li>
 *      {@link #piBonds()} - a simple model only allowing cyclic pi bonds to
 *      contribute. This model only requires that bond orders are set.
 *     </li>
 *     <li>
 *      {@link #daylight()} - a model similar to that used by Daylight for SMILES.
 *      This model does not require atom types to be defined but every atom should
 *      have it's hydrogen count set.
 *     </li>
 * </ul>
 * 
 * To obtain an instance of the model simply invoke the named method.
 * <blockquote><pre>
 * ElectronDonation model = ElectronDonation.cdk();
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module standard
 * @cdk.githash
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
     * @return electron contribution of each atom (-1=none)
     */
    abstract int[] contribution(IAtomContainer container, RingSearch ringSearch);

    /**
     * Use the preset CDK atom types to determine the electron contribution of
     * atoms. If an atom type has not been perceived or hybridisation is unset a
     * runtime exception is thrown.  The model accepts cyclic atoms which
     * are {@link org.openscience.cdk.interfaces.IAtom.Hybridization#SP2} or
     * {@link org.openscience.cdk.interfaces.IAtom.Hybridization#PLANAR3}
     * hybridised. The {@link org.openscience.cdk.CDKConstants#PI_BOND_COUNT} and
     * {@link org.openscience.cdk.CDKConstants#LONE_PAIR_COUNT} to determine how
     * many electrons an atom type can contribute. Generally these values are
     * not automatically configured and so several atom types are cached
     * for lookup: <ul> <li>N.planar3: 2 electrons </li>
     * <li>N.minus.planar3: 2 electrons </li> <li>N.amide: 2 electrons </li>
     * <li>S.2: 2 electrons </li> <li>S.planar3: 2 electrons </li>
     * <li>C.minus.planar: 2 electrons </li> <li>O.planar3: 2 electrons </li>
     * <li>N.sp2.3: 1 electron </li> <li>C.sp2: 1 electron </li> </ul>
     * 
     * Exocyclic pi bonds are not allowed to contribute.
     *
     * @return electron donation model to use for aromaticity perception
     * @see org.openscience.cdk.interfaces.IAtom#getAtomTypeName()
     */
    public static ElectronDonation cdk() {
        return new AtomTypeModel(false);
    }

    /**
     * Use the preset CDK atom types to determine the electron contribution of
     * atoms. If an atom type has not been perceived or hybridisation is unset a
     * runtime exception is thrown.  The model accepts cyclic atoms which
     * are {@link org.openscience.cdk.interfaces.IAtom.Hybridization#SP2} or
     * {@link org.openscience.cdk.interfaces.IAtom.Hybridization#PLANAR3}
     * hybridised. The {@link org.openscience.cdk.CDKConstants#PI_BOND_COUNT} and
     * {@link org.openscience.cdk.CDKConstants#LONE_PAIR_COUNT} to determine how
     * many electrons an atom type can contribute. Generally these values are
     * not automatically configured and so several atom types are cached
     * for lookup: <ul> <li>N.planar3: 2 electrons </li>
     * <li>N.minus.planar3: 2 electrons </li> <li>N.amide: 2 electrons </li>
     * <li>S.2: 2 electrons </li> <li>S.planar3: 2 electrons </li>
     * <li>C.minus.planar: 2 electrons </li> <li>O.planar3: 2 electrons </li>
     * <li>N.sp2.3: 1 electron </li> <li>C.sp2: 1 electron </li> </ul>
     * 
     * Exocyclic pi bonds are not allowed to contribute.
     *
     * @return electron donation model to use for aromaticity perception
     * @see org.openscience.cdk.interfaces.IAtom#getAtomTypeName()
     */
    public static ElectronDonation cdkAllowingExocyclic() {
        return new AtomTypeModel(true);
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
     * 
     *
     * The model makes a couple of assumptions which it will not correct for.
     * Checked assumptions cause the model to throw a runtime exception. <ul>
     * <li>there should be no valence errors (unchecked)</li> <li>every atom has
     * a set implicit hydrogen count (checked)</li> <li>every bond has defined
     * order, single, double etc (checked)</li> <li>atomic number of non-pseudo
     * atoms is set (checked)</li> </ul> 
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
