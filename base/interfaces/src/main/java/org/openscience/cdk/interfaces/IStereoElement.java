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
 * @cdk.module interfaces
 * @cdk.githash
 *
 * @author      Egon Willighagen
 * @author      John Mayfield
 * @cdk.keyword stereochemistry
 */
public interface IStereoElement<F extends IChemObject, C extends IChemObject>
    extends ICDKObject {

    public static final int CLS_MASK = 0xff_00;
    public static final int CFG_MASK = 0x00_ff;

    public static final int LEFT        = 0x00_01;
    public static final int RIGHT       = 0x00_02;
    public static final int OPPOSITE    = LEFT;
    public static final int TOGETHER    = RIGHT;

    /*
     * Important! The forth nibble of the stereo-class defines the number of
     * carriers (or coordination number) the third nibble just increments when
     * there are two geometries with the same number of carriers.
     */

    /** Geometric CisTrans (e.g. but-2-ene) */
    public static final int CT   = 0x21_00;

    /** Tetrahedral (T-4) (e.g. butan-2-ol)*/
    public static final int TH   = 0x42_00;

    /** ExtendedTetrahedral (e.g. 2,3-pentadiene) */
    public static final int AL   = 0x43_00;

    /** Atropisomeric (e.g. BiNAP) */
    public static final int AT   = 0x44_00;

    /** Square Planar (SP-4) (e.g. cisplatin) */
    public static final int SP   = 0x45_00;

    /** Square Pyramidal (SPY-5) */
    public static final int SPY  = 0x51_00;

    /** Trigonal Bipyramidal (TBPY-5) */
    public static final int TBPY = 0x52_00;

    /** Octahedral (OC-6) */
    public static final int OC   = 0x61_00;

    /** Pentagonal Bipyramidal (PBPY-7) */
    public static final int PBPY = 0x71_00;

    /** Hexagonal Bipyramidal (HBPY-8) */
    public static final int HBPY8 = 0x81_00;

    /** Heptagonal Bipyramidal (HBPY-9) */
    public static final int HBPY9 = 0x91_00;

    /** Geometric CisTrans (e.g. but-2-ene) */
    public static final int CisTrans              = CT;

    /** Tetrahedral (T-4) (e.g. butan-2-ol)*/
    public static final int Tetrahedral           = TH;

    /** ExtendedTetrahedral (e.g. 2,3-pentadiene) */
    public static final int Allenal               = AL;

    /** Atropisomeric (e.g. BiNAP) */
    public static final int Atropisomeric         = AT;

    /** Square Planar (SP-4) (e.g. cisplatin) */
    public static final int SquarePlanar          = SP;

    /** Square Pyramidal (SPY-5) */
    public static final int SquarePyramidal       = SPY;

    /** Trigonal Bipyramidal (TBPY-5) */
    public static final int TrigonalBipyramidal   = TBPY;

    /** Octahedral (OC-6) */
    public static final int Octahedral            = OC;

    /** Pentagonal Bipyramidal (PBPY-7) */
    public static final int PentagonalBipyramidal = PBPY;

    /** Hexagonal Bipyramidal (HBPY-8) */
    public static final int HexagonalBipyramidal  = HBPY8;

    /** Heptagonal Bipyramidal (HBPY-9) */
    public static final int HeptagonalBipyramidal = HBPY9;

    /** Square Planar Configutation in U Shape */
    public static final int SPU = SP | 1;
    /** Square Planar Configutation in 4 Shape */
    public static final int SP4 = SP | 2;
    /** Square Planar Configutation in Z Shape */
    public static final int SPZ = SP | 3;

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
     * Does the stereo element contain the provided atom.
     *
     * @param atom an atom to test membership
     * @return whether the atom is present
     */
    boolean contains(final IAtom atom);

    IStereoElement<F,C> map(Map<IChemObject, IChemObject> chemobjs);
    /**
     * Map the atoms/bonds in this instance to a new stereo element using the
     * provided atom/bond mapping. This allows the stereo element to be transferred
     * between a cloned or aligned (i.e. isomorphic) chemical graph.
     * 
     * If no mapping is found for a given atom or bond it is left intact.
     * However the provided atom and bonds maps must not be null.
     *
     * @param atoms nullable atom mapping, used to convert the original atoms to their mapped
     *              counterparts
     * @param bonds nullable bond mapping, used to convert the original bonds to their mapped
     *              counterparts
     * @return a new stereo element in the same configuration but with atoms/bonds
     *         replaced with their mapped equivalence.
     */
    IStereoElement map(Map<IAtom, IAtom> atoms, Map<IBond, IBond> bonds);

}
