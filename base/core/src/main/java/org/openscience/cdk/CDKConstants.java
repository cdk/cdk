/* 
 * Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IBond;

/**
 * An interface providing predefined values for a number of
 * constants used throughout the CDK. Classes using these constants should
 * <b>not</b> implement this interface, but use it like:
 * <pre>
 *   IBond.Order singleBondOrder = CDKConstants.BONDORDER_SINGLE;
 * </pre>
 *
 * @cdk.module  core
 * @cdk.githash
 *
 * @cdk.keyword bond order
 * @cdk.keyword stereochemistry
 */
public class CDKConstants {

    public final static Object      UNSET                        = null;

    /** A bond of degree 1.0.
     *
     * @deprecated Use {@link IBond.Order} SINGLE directly.
     */
    public final static IBond.Order BONDORDER_SINGLE             = IBond.Order.SINGLE;

    /** A bond of degree 2.0.
     *
     * @deprecated Use {@link IBond.Order} DOUBLE directly.
     */
    public final static IBond.Order BONDORDER_DOUBLE             = IBond.Order.DOUBLE;

    /** A bond of degree 3.0.
     *
     * @deprecated Use {@link IBond.Order} TRIPLE directly.
     */
    public final static IBond.Order BONDORDER_TRIPLE             = IBond.Order.TRIPLE;

    /** A bond of degree 4.0.
    *
    * @deprecated Use {@link IBond.Order} QUADRUPLE directly.
    */
    public final static IBond.Order BONDORDER_QUADRUPLE          = IBond.Order.QUADRUPLE;

    /** A positive atom parity. */
    public final static int         STEREO_ATOM_PARITY_PLUS      = 1;
    /** A negative atom parity. */
    public final static int         STEREO_ATOM_PARITY_MINUS     = -1;
    /** A undefined atom parity. */
    public final static int         STEREO_ATOM_PARITY_UNDEFINED = 0;

    /** A undefined hybridization. */
    public final static int         HYBRIDIZATION_UNSET          = 0;
    /** A geometry of neighboring atoms when an s orbital is hybridized
     *  with one p orbital. */
    public final static int         HYBRIDIZATION_SP1            = 1;
    /** A geometry of neighboring atoms when an s orbital is hybridized
     *  with two p orbitals. */
    public final static int         HYBRIDIZATION_SP2            = 2;
    /** A geometry of neighboring atoms when an s orbital is hybridized
     *  with three p orbitals. */
    public final static int         HYBRIDIZATION_SP3            = 3;
    /** A geometry of neighboring atoms when an s orbital is hybridized
     *  with three p orbitals with one d orbital. */
    public final static int         HYBRIDIZATION_SP3D1          = 4;
    /** A geometry of neighboring atoms when an s orbital is hybridized
     *  with three p orbitals with two d orbitals. */
    public final static int         HYBRIDIZATION_SP3D2          = 5;
    /** A geometry of neighboring atoms when an s orbital is hybridized
     *  with three p orbitals with three d orbitals. */
    public final static int         HYBRIDIZATION_SP3D3          = 6;
    /** A geometry of neighboring atoms when an s orbital is hybridized
     *  with three p orbitals with four d orbitals. */
    public final static int         HYBRIDIZATION_SP3D4          = 7;
    /** A geometry of neighboring atoms when an s orbital is hybridized
     *  with three p orbitals with five d orbitals. */
    public final static int         HYBRIDIZATION_SP3D5          = 8;
    /**
     * Carbon NMR shift constant for use as a key in the
     * IChemObject.physicalProperties hashtable.
     * @see org.openscience.cdk.ChemObject
     */
    public final static String      NMRSHIFT_CARBON              = "carbon nmr shift";
    /** Hydrogen NMR shift constant for use as a key in the
     * IChemObject.physicalProperties hashtable.
     * @see org.openscience.cdk.ChemObject
     */
    public final static String      NMRSHIFT_HYDROGEN            = "hydrogen nmr shift";
    /** Nitrogen NMR shift constant for use as a key in the
     * IChemObject.physicalProperties hashtable.
     * @see org.openscience.cdk.ChemObject
     */
    public final static String      NMRSHIFT_NITROGEN            = "nitrogen nmr shift";

    /** Phosphorus NMR shift constant for use as a key in the
     * IChemObject.physicalProperties hashtable.
     * @see org.openscience.cdk.ChemObject
     */
    public final static String      NMRSHIFT_PHOSPORUS           = "phosphorus nmr shift";

    /** Fluorine NMR shift constant for use as a key in the
     * IChemObject.physicalProperties hashtable.
     * @see org.openscience.cdk.ChemObject
     */
    public final static String      NMRSHIFT_FLUORINE            = "fluorine nmr shift";

    /** Deuterium NMR shift constant for use as a key in the
     * IChemObject.physicalProperties hashtable.
     * @see org.openscience.cdk.ChemObject
     */
    public final static String      NMRSHIFT_DEUTERIUM           = "deuterium nmr shift";

    /**
     * Property key to store the CIP descriptor label for an atom / bond. The
     * label is a string.
     */
    public final static String      CIP_DESCRIPTOR               = "cip.label";

    /* **************************************
     * Some predefined flags - keep the * numbers below 50 free for other *
     * purposes ***************************************
     */

    /** Flag that is set if the chemobject is placed (somewhere).
     */
    public final static int         ISPLACED                     = 0x0001;                           // 1 << 0
    /** Flag that is set when the chemobject is part of a ring.
     */
    public final static int         ISINRING                     = 0x0002;                           // 1 << 1
    /** Flag that is set when the chemobject is part of a ring.
     */
    public final static int         ISNOTINRING                  = 0x0004;                           // 1 << 2
    /** Flag that is set if a chemobject is part of an aliphatic chain.
     */
    public final static int         ISALIPHATIC                  = 0x0008;                           // 1 << 3 etc.
    /** Flag is set if chemobject has been visited.
     */
    public final static int         VISITED                      = 0x0010;                           // Use in tree searches // 1 << 4
    /** Flag is set if chemobject is part of an aromatic system. */
    public final static int         ISAROMATIC                   = 0x0020;                           // 1 << 5
    /** Flag is set if chemobject is part of a conjugated system. */
    public final static int         ISCONJUGATED                 = 0x0040;                           // 1 << 6
    /** Flag is set if a chemobject is mapped to another chemobject.
     *  It is used for example in subgraph isomorphism search.
     */
    public final static int         MAPPED                       = 0x0080;                           // 1 << 7

    /** Sets to true if the atom is an hydrogen bond donor. */
    public final static int         IS_HYDROGENBOND_DONOR        = 0x0100;                           // 1 << 8
    /** Sets to true if the atom is an hydrogen bond acceptor. */
    public final static int         IS_HYDROGENBOND_ACCEPTOR     = 0x0200;                           // 1 << 9

    /** Flag is set if a chemobject has reactive center.
     *  It is used for example in reaction.
     */
    public static final int         REACTIVE_CENTER              = 0x0400;                           // 1 << 10
    /** Flag is set if an atom could be typed.
     */
    public static final int         IS_TYPEABLE                  = 0x0800;                           // 1 << 11

    /**
     * Flag used for marking uncertainty of the bond order.
     * If used on an
     * <ul>
     *  <li>{@link org.openscience.cdk.interfaces.IAtomContainer} it means that one or several of the bonds have
     * 		this flag raised (which may indicate aromaticity).</li>
     *  <li>{@link org.openscience.cdk.interfaces.IBond} it means that it's unclear whether the bond is a single or
     * 		double bond.</li>
     *  <li>{@link org.openscience.cdk.interfaces.IAtom} it is a way for the Smiles parser to indicate that this atom was
     * 		written with a lower case letter, e.g. 'c' rather than 'C'</li>
     * </ul>
     */
    public final static int         SINGLE_OR_DOUBLE             = 1 << 12;
    /**
     * Maximum flags array index. Please update this if the value exceeds 16 -
     * the flags are currently stored as a single short value (16-bit) in the
     * ChemObject implementations (default, silent and query).
     */
    public final static int         MAX_FLAG_INDEX               = 13;

    // array of flags is initialised using the static constructor
    public final static int[]       FLAG_MASKS                   = new int[MAX_FLAG_INDEX + 1];

    static {
        for (int i = 0; i < FLAG_MASKS.length; i++)
            FLAG_MASKS[i] = 1 << i;
    }

    /**
     * Flag used for JUnit testing the pointer functionality.
     */
    public final static int         DUMMY_POINTER                = 1;
    /**
     * Maximum pointers array index.
     */
    public final static int         MAX_POINTER_INDEX            = 1;

    /* **************************************
     * Some predefined property names for * ChemObjects *
     * **************************************
     */

    /** The title for a IChemObject. */
    public static final String      TITLE                        = "cdk:Title";

    /** A remark for a IChemObject.*/
    public static final String      REMARK                       = "cdk:Remark";

    /** A String comment. */
    public static final String      COMMENT                      = "cdk:Comment";

    /** A List of names. */
    public static final String      NAMES                        = "cdk:Names";

    /** A List of annotation remarks. */
    public static final String      ANNOTATIONS                  = "cdk:Annotations";

    /** A description for a IChemObject. */
    public static final String      DESCRIPTION                  = "cdk:Description";

    /* **************************************
     * Some predefined property names for * Molecules *
     * **************************************
     */

    /** The Daylight SMILES. */
    public static final String      SMILES                       = "cdk:SMILES";

    /** The IUPAC International Chemical Identifier. */
    public static final String      INCHI                        = "cdk:InChI";

    /** The Molecular Formula Identifier. */
    public static final String      FORMULA                      = "cdk:Formula";

    /** The IUPAC compatible name generated with AutoNom. */
    public static final String      AUTONOMNAME                  = "cdk:AutonomName";

    /** The Beilstein Registry Number. */
    public static final String      BEILSTEINRN                  = "cdk:BeilsteinRN";

    /** The CAS Registry Number. */
    public static final String      CASRN                        = "cdk:CasRN";

    /** A set of all rings computed for this molecule. */
    public static final String      ALL_RINGS                    = "cdk:AllRings";

    /** A smallest set of smallest rings computed for this molecule. */
    public static final String      SMALLEST_RINGS               = "cdk:SmallestRings";

    /** The essential rings computed for this molecule.
     *  The concept of Essential Rings is defined in
     *  SSSRFinder
     */
    public static final String      ESSENTIAL_RINGS              = "cdk:EssentialRings";

    /** The relevant rings computed for this molecule.
     *  The concept of relevant Rings is defined in
     *  SSSRFinder
     */
    public static final String      RELEVANT_RINGS               = "cdk:RelevantRings";

    /**
     * Property used for reactions when converted to/from molecules. It defines what role and atom
     * has an a reaction.
     *
     * Used in. ReactionManipulator.toMolecule and ReactionManipulator.toReaction.
     */
    public static final String      REACTION_ROLE                = "cdk:ReactionRole";

    /**
     * Property used for reactions when converted to/from molecules. It defines fragment grouping, for example
     * when handling ionic components.
     *
     * Used in. ReactionManipulator.toMolecule and ReactionManipulator.toReaction.
     */
    public static final String      REACTION_GROUP               = "cdk:ReactionGroup";

    /* **************************************
     * Some predefined property names for * Atoms *
     * **************************************
     */

    /**
     * This property will contain an ArrayList of Integers. Each
     * element of the list indicates the size of the ring the given
     * atom belongs to (if it is a ring atom at all).
     */
    public static final String      RING_SIZES                   = "cdk:RingSizes";

    /**
     * This property indicates how many ring bonds are connected to
     * the given atom.
     */
    public static final String      RING_CONNECTIONS             = "cdk:RingConnections";

    /*
     * This property indicate how many bond are present on the atom.
     */
    public static final String      TOTAL_CONNECTIONS            = "cdk:TotalConnections";
    /*
     * Hydrogen count
     */
    public static final String      TOTAL_H_COUNT                = "cdk:TotalHydrogenCount";

    /** The Isotropic Shielding, usually calculated by
      * a quantum chemistry program like Gaussian.
      * This is a property used for calculating NMR chemical
      * shifts by subtracting the value from the
      * isotropic shielding value of a standard (e.g. TMS).
      */
    public static final String      ISOTROPIC_SHIELDING          = "cdk:IsotropicShielding";

    /**
     * A property to indicate RestH being true or false. RestH is a term
     * used in RGroup queries: "if this property is applied ('on'), sites labelled
     * with Rgroup rrr may only be substituted with a member of the Rgroup or with H"
     */
    public static final String      REST_H                       = "cdk:RestH";

    public static final String      ATOM_ATOM_MAPPING            = "cdk:AtomAtomMapping";

    /**
     * Atom number/label that can be applied using the Manual Numbering 
     * Tool in ACD/ChemSketch.
     */
    public static final String      ACDLABS_LABEL                = "cdk:ACDLabsAtomLabel";


    /**
     * Key to store/fetch CTab Sgroups from Molfiles. Important! - Use at your own risk,
     * property is transitive and may be removed in future with a more specific accessor.
     */
    public static final String      CTAB_SGROUPS                 = "cdk:CtabSgroups";


    /**
     * Property for reaction objects where the conditions of reactions can be placed.
     */
    public static final String      REACTION_CONDITIONS          = "cdk:ReactionConditions";


    /* **************************************
     * Some predefined property names for * AtomTypes *
     * **************************************
     */

    /** Used as property key for indicating the ring size of a certain atom type. */
    public static final String      PART_OF_RING_OF_SIZE         = "cdk:Part of ring of size";

    /** Used as property key for indicating the chemical group of a certain atom type. */
    public static final String      CHEMICAL_GROUP_CONSTANT      = "cdk:Chemical Group";

    /** Used as property key for indicating the HOSE code for a certain atom type. */
    public static final String      SPHERICAL_MATCHER            = "cdk:HOSE code spherical matcher";

    /** Used as property key for indicating the HOSE code for a certain atom type. */
    public static final String      PI_BOND_COUNT                = "cdk:Pi Bond Count";

    /** Used as property key for indicating the HOSE code for a certain atom type. */
    public static final String      LONE_PAIR_COUNT              = "cdk:Lone Pair Count";

    /** Used as property key for indicating the number of single electrons on the atom type. */
    public static final String      SINGLE_ELECTRON_COUNT        = "cdk:Single Electron Count";

}
