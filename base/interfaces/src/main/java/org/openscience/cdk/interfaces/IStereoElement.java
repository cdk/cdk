/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *               2017  John Mayfield
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Representation of stereochemical configuration. The abstract configuration
 * is described by three pieces of information:
 * <ul>
 *  <li>the <b>focus</b> of the stereo chemistry</li>
 *  <li>the <b>carriers</b> of the configuration</li>
 *  <li>the <b>configuration</b> of the <i>carriers</i></li>
 * </ul>
 * <br>
 * The focus/carriers may be either atoms or bonds. For example in the case of
 * common tetrahedral stereochemistry the focus is the chiral atom, and the
 * carriers are the bonds (or atoms) connected to it. The configuration is then
 * either left-handed (anti-clockwise) or right-handed (clockwise).
 * <br><br>
 * <b><u>Configuration</u></b>
 * <br>
 * The configuration is stored as an integral value. Although the common
 * geometries like tetrahedral and cis/trans bonds only have 2 possible
 * configurations (e.g. left vs right) more complex geometries like square
 * planar and octahedral require more to describe. For convenience the
 * constants {@link #LEFT} and {@link #RIGHT} are provided but are synonymous
 * with the values {@code 1} (odd) and {@code 2} (even).<br>
 * Special values (e.g. 0) may be used to represent unknown/unspecified or
 * racemic in future but are currently undefined.
 * <br><br>
 * <b><u>Configuration Class</u></b>
 * <br>
 * There stereo class defines the type of stereochemistry/geometry that is
 * captured. The stereo class is also defined as a integral value. The
 * following classes are available with varied support through out the
 * toolkit. Each class is named with a short mnemonic code:
 * <ul>
 *     <li>{@link #TH}: Tetrahedral</li>
 *     <li>{@link #CT}: CisTrans a double-bond</li>
 *     <li>{@link #AL}: Extended Tetrahedral (Allenal)</li>
 *     <li>{@link #AT}: Atropisomeric</li>
 *     <li>{@link #SP}: Square Planar</li>
 *     <li>{@link #SPY}: Square Pyramidal</li>
 *     <li>{@link #TBPY}: Trigonal Bipyramidal</li>
 *     <li>{@link #PBPY}: Pentagonal Bipyramidal</li>
 *     <li>{@link #OC}: Octahedral</li>
 *     <li>{@link #HBPY8}: Hexagonal Bipyramidal</li>
 *     <li>{@link #HBPY9}: Heptagonal Bipyramidal</li>
 * </ul>
 *
 * <b><u>Stereo Groups (Enhanced stereo):</u></b>
 * Stereochemistry group information, aka "enhanced stereochemistry" in V3000 MOLFile etc allows you to specify
 * racemic and unknown enantiomers. In V2000 MOLfile if the chiral flag is 0 it indicates the structure is a mixture
 * of enantiomers. V3000 extended this concept to not only encode mixtures (and enantiomer) but also unknown
 * stereochemistry (or enantiomer) and to be per chiral centre allow representation of any epimers.
 * Reading an MDLV2000 molfile a chiral flag of 0 is equivalent to setting all stereocentres to {@link #GRP_RAC1}.
 * This information can also be encoded in CXSMILES. By default all stereocentres are {@link #GRP_ABS}.
 *
 * The stereo group information is stored in the high bytes of the stereo configuration. You can access the basic
 * information as follows:
 * <pre>{@code
 * int grpconfig = stereo.getGroupInfo();
 * if (grpconfig & IStereoElement.GRP_RAC1) {
 *     // group is RAC1
 * } else if (config & IStereoElement.GRP_REL1) {
 *     // group is OR1
 * }
 * }</pre>
 *
 * You can also unpack the various parts of the information manually.
 *
 * <pre>{@code
 * int grpconfig = stereo.getGroupInfo();
 * switch (grpconfig & IStereoElement.GRP_TYPE_MASK) {
 *   case IStereoElement.GRP_ABS:
 *   break;
 *   case IStereoElement.GRP_AND:
 *   break;
 *   case IStereoElement.GRP_OR:
 *   break;
 * }
 *
 * // the group number 1, 2, 3, 4 is a little more tricky, you can mask off the value as
 * // follows but it's shifted up into position
 * int num = grpconfig & IStereoElement.GRP_NUM_MASK;
 *
 * // to get the number 1, 2, 3, etc you can simply shift it down as follows
 * int num_act = grpconfig >>> IStereoElement.GRP_NUM_SHIFT;
 * }</pre>
 *
 * @cdk.module interfaces
 * @cdk.githash
 *
 * @author      Egon Willighagen
 * @author      John Mayfield
 * @cdk.keyword stereochemistry
 */
public interface IStereoElement<F extends IChemObject, C extends IChemObject>
    extends ICDKObject {

    int CLS_MASK = 0xff_00;
    int CFG_MASK = 0x00_ff;

    int LEFT        = 0x00_01;
    int RIGHT       = 0x00_02;
    int OPPOSITE    = LEFT;
    int TOGETHER    = RIGHT;

    /*
     * Important! The forth nibble of the stereo-class defines the number of
     * carriers (or coordination number) the third nibble just increments when
     * there are two geometries with the same number of carriers.
     */

    /** Geometric CisTrans (e.g. but-2-ene) */
    int CT   = 0x21_00;

    /** Tetrahedral (T-4) (e.g. butan-2-ol)*/
    int TH   = 0x42_00;

    /** ExtendedTetrahedral a.k.a. allene (e.g. 2,3-pentadiene) */
    int AL   = 0x43_00;

    /** ExtendedCisTrans a.k.a. cumulene (e.g. hexa-2,3,4-triene) */
    int CU   = 0x22_00;

    /** Atropisomeric (e.g. BiNAP) */
    int AT   = 0x44_00;

    /** Square Planar (SP-4) (e.g. cisplatin) */
    int SP   = 0x45_00;

    /** Square Pyramidal (SPY-5) */
    int SPY  = 0x51_00;

    /** Trigonal Bipyramidal (TBPY-5) */
    int TBPY = 0x52_00;

    /** Octahedral (OC-6) */
    int OC   = 0x61_00;

    /** Pentagonal Bipyramidal (PBPY-7) */
    int PBPY = 0x71_00;

    /** Hexagonal Bipyramidal (HBPY-8) */
    int HBPY8 = 0x81_00;

    /** Heptagonal Bipyramidal (HBPY-9) */
    int HBPY9 = 0x91_00;

    /** Geometric CisTrans (e.g. but-2-ene) */
    int CisTrans              = CT;

    /** Tetrahedral (T-4) (e.g. butan-2-ol)*/
    int Tetrahedral           = TH;

    /** ExtendedTetrahedral (e.g. 2,3-pentadiene) */
    int Allenal               = AL;

    /** Cumulene */
    int Cumulene              = CU;

    /** Atropisomeric (e.g. BiNAP) */
    int Atropisomeric         = AT;

    /** Square Planar (SP-4) (e.g. cisplatin) */
    int SquarePlanar          = SP;

    /** Square Pyramidal (SPY-5) */
    int SquarePyramidal       = SPY;

    /** Trigonal Bipyramidal (TBPY-5) */
    int TrigonalBipyramidal   = TBPY;

    /** Octahedral (OC-6) */
    int Octahedral            = OC;

    /** Pentagonal Bipyramidal (PBPY-7) */
    int PentagonalBipyramidal = PBPY;

    /** Hexagonal Bipyramidal (HBPY-8) */
    int HexagonalBipyramidal  = HBPY8;

    /** Heptagonal Bipyramidal (HBPY-9) */
    int HeptagonalBipyramidal = HBPY9;

    /** Square Planar Configutation in U Shape */
    int SPU = SP | 1;
    /** Square Planar Configutation in 4 Shape */
    int SP4 = SP | 2;
    /** Square Planar Configutation in Z Shape */
    int SPZ = SP | 3;

    /** Mask for the stereo group information */
    int GRP_MASK      = 0xff_0000;
    /** Mask for the stereo group type information, GRP_ABS, GRP_AND, GRP_OR */
    int GRP_TYPE_MASK = 0x03_0000;
    /** Mask for the stereo group number information, 0x0 .. 0xf (1..15) */
    int GRP_NUM_MASK   = 0xfc_0000;
    int GRP_NUM_SHIFT  = 18; // Integer.numberOfTrailingZeros(0xfc_0000);

    /** Absolute stereo group, the exact stereo configuration of this atom is known. */
    int GRP_ABS = 0x00_0000;
    /** Racemic stereo group type, the stereo configuration of this atom is a mixture of R/S. An atom can be  */
    int GRP_RAC = 0x01_0000;
    /**
     * Relative stereo group type, the stereo configuration of this atom is unknown but is relative to another
     * atom in the same group.
     */
    int GRP_REL = 0x02_0000;

    /** Convenience field for testing if the stereo is group RAC1 (&amp;1). */
    int GRP_RAC1 = GRP_RAC | (1 << GRP_NUM_SHIFT);
    /** Convenience field for testing if the stereo is group RAC2 (&amp;2). */
    int GRP_RAC2 = GRP_RAC | (2 << GRP_NUM_SHIFT);
    /** Convenience field for testing if the stereo is group RAC3 (&amp;3). */
    int GRP_RAC3 = GRP_RAC | (3 << GRP_NUM_SHIFT);
    /** Convenience field for testing if the stereo is group RAC4 (&amp;4). */
    int GRP_RAC4 = GRP_RAC | (4 << GRP_NUM_SHIFT);
    /** Convenience field for testing if the stereo is group RAC5 (&amp;5). */
    int GRP_RAC5 = GRP_RAC | (5 << GRP_NUM_SHIFT);

    /** Convenience field for testing if the stereo is group OR1 (&amp;1). */
    int GRP_REL1  = GRP_REL | (1 << GRP_NUM_SHIFT);
    /** Convenience field for testing if the stereo is group OR2 (&amp;2). */
    int GRP_REL2  = GRP_REL | (2 << GRP_NUM_SHIFT);
    /** Convenience field for testing if the stereo is group OR3 (&amp;3). */
    int GRP_REL3  = GRP_REL | (3 << GRP_NUM_SHIFT);
    /** Convenience field for testing if the stereo is group OR4 (&amp;4). */
    int GRP_REL4  = GRP_REL | (4 << GRP_NUM_SHIFT);
    /** Convenience field for testing if the stereo is group OR5 (&amp;5). */
    int GRP_REL5  = GRP_REL | (5 << GRP_NUM_SHIFT);

    /**
     * The focus atom or bond at the 'centre' of the stereo-configuration.
     * @return the focus
     */
    F getFocus();

    /**
     * The carriers of the stereochemistry
     * @return the carriers
     */
    List<C> getCarriers();

    /**
     * The configuration class of the stereochemistry.
     * @return configuration class
     */
    int getConfigClass();

    /**
     * The configuration order of the stereochemistry.
     * @return configuration
     */
    int getConfigOrder();

    /**
     * Set the configuration order of the stereochemistry.
     * @param cfg the new configuration
     */
    void setConfigOrder(int cfg);

    /**
     * Access the configuration order and class of the stereochemistry.
     * @return the configuration
     */
    int getConfig();

    /**
     * Access the stereo group information - see class doc.
     * @return the group info
     */
    int getGroupInfo();

    /**
     * Set the stereo group information - see class doc.
     * @param grp the group info
     */
    void setGroupInfo(int grp);

    /**
     * Does the stereo element contain the provided atom.
     *
     * @param atom an atom to test membership
     * @return whether the atom is present
     */
    boolean contains(final IAtom atom);

    /**
     * Update the stereo using the remapping of atoms/bonds in this instance to
     * a new stereo element using the provided atom/bond mapping. This allows
     * the stereo element to be transferred between a cloned or aligned (i.e.
     * isomorphic) chemical graph.
     *
     * If no mapping is found for a given atom or bond the existing atom/bond
     * it is left intact. If you want to remove stereo in such cases please use
     * {@link #mapStrict}.
     *
     * @param chemobjs chem object mapping
     * @return a new stereo element in the same configuration but with atoms/bonds
     *         replaced with their mapped equivalence.
     */
    IStereoElement<F,C> map(Map<IChemObject, IChemObject> chemobjs);

    /**
     * Update the stereo using the remapping of atoms/bonds in this instance to
     * a new stereo element using the provided atom/bond mapping. This allows
     * the stereo element to be transferred between a cloned or aligned (i.e.
     * isomorphic) chemical graph.
     *
     * <b>If no mapping is found for a given atom or bond a new element is NOT
     * created.</b>
     *
     * @param chemobjs chem object mapping
     * @return a new stereo element in the same configuration but with atoms/bonds
     *         replaced with their mapped equivalence.
     */
    IStereoElement<F,C> mapStrict(Map<IChemObject, IChemObject> chemobjs);

    /**
     * Map the atoms/bonds in this instance to a new stereo element using the
     * provided atom/bond mapping. This allows the stereo element to be transferred
     * between a cloned or aligned (i.e. isomorphic) chemical graph.
     * 
     * If no mapping is found for a given atom or bond it is left intact.
     * However the provided atom and bonds maps must not be null.
     *
     * @param atoms used to convert the original atoms to their mapped
     *              counterparts
     * @param bonds used to convert the original bonds to their mapped
     *              counterparts
     * @return a new stereo element in the same configuration but with atoms/bonds
     *         replaced with their mapped equivalence.
     */
    IStereoElement map(Map<IAtom, IAtom> atoms, Map<IBond, IBond> bonds);

    /*
     * Update the carriers replacing all occurrences of one item with another.
     *
     * @param remove the atom/bond to remove
     * @param add the atom/bond to replace it with
     */
    default void updateCarrier(IChemObject remove, IChemObject add) {
        @SuppressWarnings("unchecked")
        List<IChemObject> carriers = (List<IChemObject>)getCarriers();
        for (int i = 0; i < carriers.size(); i++) {
            if (remove.equals(carriers.get(i))) {
                carriers.set(i, add);
            }
        }
    }

    /*
     * Update the carriers replacing all occurrences of one item with items
     * on the provided iterable. Once the iterable is exhausted no more
     * replacements are made.
     *
     * @param remove the atom/bond to remove
     * @param add the atom/bonds to replace it with
     */
    default void updateCarriers(C remove, Iterable<C> adds) {
        Iterator<C> repIter = adds.iterator();
        List<C> carriers = getCarriers();
        for (int i = 0; i < carriers.size(); i++) {
            if (remove.equals(carriers.get(i)) && repIter.hasNext()) {
                carriers.set(i, repIter.next());
            }
        }
    }
}
