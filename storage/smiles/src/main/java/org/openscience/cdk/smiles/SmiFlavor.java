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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 * Flags for customising SMILES generation.
 */
public final class SmiFlavor {

    private SmiFlavor() {
    }

    /**
     * Output SMILES in a canonical order. The order is not guaranteed to be
     * equivalent between releases.
     */
    public static final int Canonical          = 0x001;

    /**
     * Output SMILES in a canonical order using the InChI labelling algorithm.
     * @see #UniversalSmiles
     */
    public static final int InChILabelling     = 0x003;

    /**
     * Output atom-atom mapping for reactions and atom classes for molecules. The
     * map index is set on an atom with property {@link org.openscience.cdk.CDKConstants#ATOM_ATOM_MAPPING}
     * using {@link org.openscience.cdk.interfaces.IAtom#setProperty(Object, Object)}.
     */
    public static final int AtomAtomMap        = 0x004;

    /**
     * Output atomic mass on atoms. For historical reasons the atomic mass is
     * often set on all atoms in a CDK molecule. Therefore to avoid SMILES like
     * {@code [12CH3][12CH2][16OH]} major isotopes are not generated. If you
     * wish to generate SMILES with the major isotopes please use the flag
     * {@link #AtomicMassStrict} this will output all mass numbers and only be
     * omitted when the mass is unset (null).
     */
    public static final int AtomicMass         = 0x008;

    /**
     * Writes aromatic atoms as lower case letters. For portability
     * this option is not recommended.
     */
    public static final int UseAromaticSymbols = 0x010;

    // public static final int SuppressHydrogens  = 0x020;

    /**
     * Output tetrahedral stereochemistry on atoms as <code>@</code> and <code>@@</code>.
     * @see #Stereo
     */
    public static final int StereoTetrahedral   = 0x100;

    /**
     * Output cis-trans stereochemistry specified by directional <code>\</code>
     * of <code>/</code> bonds.
     * @see #Stereo
     */
    public static final int StereoCisTrans      = 0x200;

    /**
     * Output extended tetrahedral stereochemistry on atoms as <code>@</code> and
     * <code>@@</code>. Extended tetrahedral captures rotations around a cumulated
     * carbon: <code>CC=[C@]=CC</code>.
     * @see #Stereo
     */
    public static final int StereoExTetrahedral = 0x400;

    public static final int StereoExCisTrans    = 0x500;

    /**
     * Generate SMILES with the major isotopes, only omit mass numbers when it
     * is unset.
     * @deprecated no longer needed, default CDK behaviour is now to pass
     *             through mass numbers if non-null
     */
    @Deprecated
    public static final int AtomicMassStrict   = 0x800;


    /**
     * Output supported stereochemistry types.
     * @see #StereoTetrahedral
     * @see #StereoCisTrans
     * @see #StereoExTetrahedral
     */
    public static final int Stereo              = StereoTetrahedral |
                                                  StereoCisTrans |
                                                  StereoExTetrahedral |
                                                  StereoExCisTrans;

    /**
     * Output 2D coordinates.
     */
    public static final int Cx2dCoordinates     = 0x001000;

    /**
     * Output 3D coordinates.
     */
    public static final int Cx3dCoordinates     = 0x002000;

    /**
     * Output either 2D/3D coordinates.
     */
    public static final int CxCoordinates       = Cx3dCoordinates | Cx2dCoordinates;

    /**
     * Output atom labels, atom labels are specified by {@link IPseudoAtom#getLabel()}.
     */
    public static final int CxAtomLabel         = 0x008000;

    /**
     * Output atom values, atom values are specified by the property 
     * {@link org.openscience.cdk.CDKConstants#COMMENT} using 
     * {@link org.openscience.cdk.interfaces.IAtom#setProperty(Object, Object)}
     */
    public static final int CxAtomValue         = 0x010000;

    /**
     * Output radicals, radicals are specified by {@link IAtomContainer#getConnectedSingleElectronsCount(IAtom)}
     */
    public static final int CxRadical           = 0x020000;

    /**
     * Output multicenter bonds, positional variation is specified with {@link org.openscience.cdk.sgroup.Sgroup}s
     * of the type {@link org.openscience.cdk.sgroup.SgroupType#ExtMulticenter}.
     */
    public static final int CxMulticenter       = 0x040000;

    /**
     * Output polymer repeat units is specified with {@link org.openscience.cdk.sgroup.Sgroup}s.
     */
    public static final int CxPolymer           = 0x080000;

    /**
     * Output fragment grouping for reactions.
     */
    public static final int CxFragmentGroup     = 0x100000;

    /**
     * Output ligand order information.
     */
    public static final int CxLigandOrder       = 0x200000;

    /**
     * Renumber AtomAtomMaps during canonical generation
     */
    public static final int AtomAtomMapRenumber = Canonical | AtomAtomMap | 0x2000000;

    /**
     * Output data Sgroups.
     */
    public static final int CxDataSgroups       = 0x200000;

    /**
     * Output CXSMILES layers.
     */
    public static final int CxSmiles            = CxAtomLabel | CxAtomValue | CxRadical | CxFragmentGroup | CxMulticenter | CxPolymer | CxLigandOrder | CxDataSgroups;

    /**
     * Output CXSMILES layers and coordinates.
     */
    public static final int CxSmilesWithCoords  = CxSmiles | CxCoordinates;

    /**
     * Output non-canonical SMILES without stereochemistry, atomic masses.
     */
    public static final int Generic             = 0;

    /**
     * Output canonical SMILES without stereochemistry, atomic masses.
     */
    public static final int Unique              = Canonical;

    /**
     * Output non-canonical SMILES with stereochemistry, atomic masses.
     */
    public static final int Isomeric            = Stereo | AtomicMass;

    /**
     * Output canonical SMILES with stereochemistry, atomic masses.
     */
    public static final int Absolute            = Canonical | Isomeric;


    /**
     * Default SMILES output write Stereochemistry, Atomic Mass, and CXSMILES layers. The
     * ordering is not canonical.
     */
    public static final int Default             = Stereo | AtomicMass | CxSmiles;

    /**
     * Output canonical SMILES with stereochemistry and atomic masses, This output uses the
     * InChI labelling algorithm to generate a 'Universal SMILES' {@cdk.cite OBoyle12}.
     * <br>
     * Unfortunately there are several issues and general use is not recommended:
     * <ul>
     *  <li>MAJOR: Molecules with delocalised charges are generally non-canonical, e.g.
     *             <code>C(\C=C\N1CCCC1)=C/c2[n+](c3c(n2CC)nc4ccccc4n3)CC</code> will generate two different
     *             SMILES depending on input order</li>
     *  <li>MINOR: Double bond '/' '\' assignment is different from the original paper (O'Boyle) and
     *             will not match universal SMILES generated by Open Babel</li>
     *  <li>MINOR: SMILES with '*' atoms can not be canonicalised by default, to avoid this we use
     *             the 'Rf' atom as a substitute. Structures with an 'Rf' atom can still be generated
     *             providing there are no '*' atoms.</li>
     *  <li>MINOR: The InChI library (v1.03) is not thread safe</li>
     * </ul>
     *
     */
    public static final int UniversalSmiles     = InChILabelling | Isomeric;

    static boolean isSet(int opts, int opt) {
        return (opts & opt) != 0;
    }
}
