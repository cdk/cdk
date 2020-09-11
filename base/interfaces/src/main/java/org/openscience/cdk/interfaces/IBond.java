/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 * Implements the concept of a covalent bond between two or more atoms. A bond is
 * considered to be a number of electrons connecting two ore more atoms.
 * type filter text
 *
 * @author egonw
 * @cdk.module interfaces
 * @cdk.githash
 * @cdk.created 2005-08-24
 * @cdk.keyword bond
 * @cdk.keyword atom
 * @cdk.keyword electron
 */
public interface IBond extends IElectronContainer {

    /**
     * A list of permissible bond orders.
     */
    enum Order {
        SINGLE(1), DOUBLE(2), TRIPLE(3), QUADRUPLE(4), QUINTUPLE(5), SEXTUPLE(6), UNSET(0);

        private final Integer bondedElectronPairs;

        Order(Integer bondedElectronPairs) {
            this.bondedElectronPairs = bondedElectronPairs;
        }

        /**
         * Access a numeric value for the number of bonded electron pairs.
         * <p>
         * <pre>{@code
         * Order.SINGLE.numeric()    // 1
         * Order.DOUBLE.numeric()    // 2
         * Order.TRIPLE.numeric()    // 3
         * Order.QUADRUPLE.numeric() // 4
         * Order.QUINTUPLE.numeric() // 5
         * Order.SEXTUPLE.numeric()  // 6
         * Order.UNSET.numeric()     // 0
         * }</pre>
         *
         * @return number of bonded electron pairs
         */
        public Integer numeric() {
            return bondedElectronPairs;
        }

    }

    /**
     * Enumeration of possible stereo types of two-atom bonds. The
     * Stereo type defines not just define the stereochemistry, but also the
     * which atom is the stereo center for which the Stereo is defined.
     * The first atom in the IBond (index = 0) is the <i>start</i> atom, while
     * the second atom (index = 1) is the <i>end</i> atom.
     */
    enum Stereo {
        /**
         * A bond for which there is no stereochemistry.
         */
        NONE,
        /**
         * A bond pointing up of which the start atom is the stereocenter and
         * the end atom is above the drawing plane.
         */
        UP,
        /**
         * A bond pointing up of which the end atom is the stereocenter and
         * the start atom is above the drawing plane.
         */
        UP_INVERTED,
        /**
         * A bond pointing down of which the start atom is the stereocenter
         * and the end atom is below the drawing plane.
         */
        DOWN,
        /**
         * A bond pointing down of which the end atom is the stereocenter and
         * the start atom is below the drawing plane.
         */
        DOWN_INVERTED,
        /**
         * A bond for which there is stereochemistry, we just do not know
         * if it is UP or DOWN. The start atom is the stereocenter.
         */
        UP_OR_DOWN,
        /**
         * A bond for which there is stereochemistry, we just do not know
         * if it is UP or DOWN. The end atom is the stereocenter.
         */
        UP_OR_DOWN_INVERTED,
        /**
         * Indication that this double bond has a fixed, but unknown E/Z
         * configuration.
         */
        E_OR_Z,
        /**
         * Indication that this double bond has a E configuration.
         */
        E,
        /**
         * Indication that this double bond has a Z configuration.
         */
        Z,
        /**
         * Indication that this double bond has a fixed configuration, defined
         * by the 2D and/or 3D coordinates.
         */
        E_Z_BY_COORDINATES
    }

    /**
     * Bond display style, controlling how bonds appear in a 2D depiction.
     */
    enum Display {
        /** A solid line (default). */
        Solid,
        /** A dashed line. */
        Dash,
        /** A hashed line (bold dashed). */
        Hash,
        /** A bold line. */
        Bold,
        /** A wavy line. */
        Wavy,
        /** A dotted line. */
        Dot,
        /**
         * Display as a hashed wedge, with the narrow end
         * towards the begin atom of the bond ({@link IBond#getBegin()}).
         */
        WedgedHashBegin,
        /**
         * Display as a hashed wedge, with the narrow end
         * towards the end atom of the bond ({@link IBond#getEnd()}).
         */
        WedgedHashEnd,
        /**
         * Display as a bold wedge, with the narrow end
         * towards the begin atom of the bond ({@link IBond#getBegin()}).
         */
        WedgeBegin,
        /**
         * Display as a bold wedge, with the narrow end
         * towards the end atom of the bond ({@link IBond#getEnd()}).
         */
        WedgeEnd,
        /**
         * Display as an arrow (e.g. co-ordination bond), the arrow points
         * to the begin ({@link IBond#getBegin()}) atom.
         */
        ArrowBeg,
        /**
         * Display as an arrow (e.g. co-ordination bond), the arrow points
         * to the end ({@link IBond#getEnd()}) atom.
         */
        ArrowEnd
    }

    /**
     * Returns the Iterable to atoms making up this bond.
     *
     * @return An Iterable to atoms participating in this bond
     * @see #setAtoms
     */
    Iterable<IAtom> atoms();

    /**
     * Sets the array of atoms making up this bond.
     *
     * @param atoms An array of atoms that forms this bond
     * @see #atoms
     */
    void setAtoms(IAtom[] atoms);

    /**
     * Access the begin (or first) atom of the bond.
     *
     * @return the begin atom
     */
    IAtom getBegin();

    /**
     * Access the end (or second) atom of the bond.
     *
     * @return the end atom
     */
    IAtom getEnd();

    int getIndex();

    IAtomContainer getContainer();

    /**
     * Returns the number of Atoms in this Bond.
     *
     * @return The number of Atoms in this Bond
     */
    int getAtomCount();

    /**
     * Returns an Atom from this bond.
     *
     * @param position The position in this bond where the atom is
     * @return The atom at the specified position
     * @see #setAtom
     */
    IAtom getAtom(int position);

    /**
     * Returns the other atom in the bond, the atom is connected to the given atom. This
     * method is only correct for two-centre bonds, for n-centre bonds the behaviour is undefined
     * and the more correct {@link #getConnectedAtoms(IAtom)} should be used.
     * <p>
     * <pre>{@code
     * IAtom beg = bond.getBegin();
     * IAtom end = bond.getEnd();
     * // bond.getConnectedAtom(beg) == end
     * // bond.getConnectedAtom(end) == beg
     * }</pre>
     *
     * @param atom The atom the bond partner is searched of
     * @return the connected atom or null if the given atom is not part of the bond
     * @deprecated use the method {@link #getOther(IAtom)}
     */
    @Deprecated
    IAtom getConnectedAtom(IAtom atom);

    /**
     * Returns the other atom in the bond, the atom is connected to the given atom.This
     * method is only correct for two-centre bonds, for n-centre bonds the behaviour is undefined
     * and the more correct {@link #getConnectedAtoms(IAtom)} should be used.
     * <p>
     * <pre>{@code
     * IAtom beg = bond.getBegin();
     * IAtom end = bond.getEnd();
     * // bond.getOther(beg) == end
     * // bond.getOther(end) == beg
     * }</pre>
     *
     * @param atom The atom the bond partner is searched of
     * @return the connected atom or null if the given atom is not part of the bond
     */
    IAtom getOther(IAtom atom);

    /**
     * Returns all the atoms in the bond connected to the given atom.
     *
     * @param atom The atoms the bond partner is searched of
     * @return the connected atoms or null  if the given atom is not part of the bond
     */
    IAtom[] getConnectedAtoms(IAtom atom);

    /**
     * Returns true if the given atom participates in this bond.
     *
     * @param atom The atom to be tested if it participates in this bond
     * @return true if the atom participates in this bond
     */
    boolean contains(IAtom atom);

    /**
     * Sets an Atom in this bond.
     *
     * @param atom     The atom to be set
     * @param position The position in this bond where the atom is to be inserted
     * @see #getAtom
     */
    void setAtom(IAtom atom, int position);

    /**
     * Returns the bond order of this bond.
     *
     * @return The bond order of this bond
     * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants
     * for predefined values.
     * @see #setOrder
     */
    Order getOrder();

    /**
     * Sets the bond order of this bond.
     *
     * @param order The bond order to be assigned to this bond
     * @see org.openscience.cdk.CDKConstants for predefined values.
     * @see #getOrder
     */
    void setOrder(Order order);

    /**
     * Returns the stereo descriptor for this bond.
     *
     * @return The stereo descriptor for this bond
     * @see #setStereo
     */
    IBond.Stereo getStereo();

    /**
     * Sets the stereo descriptor for this bond. Note this function will
     * also modify the bond display style.
     *
     * @param stereo The stereo descriptor to be assigned to this bond.
     * @see #getStereo
     * @see #setDisplay(Display)
     */
    void setStereo(IBond.Stereo stereo);

    /**
     * Access the bond display style.
     * @return the bond display
     */
    IBond.Display getDisplay();

    /**
     * Set the bond display style.
     * @param display the display
     */
    void setDisplay(IBond.Display display);

    /**
     * Returns the geometric 2D center of the bond.
     *
     * @return The geometric 2D center of the bond
     */
    Point2d get2DCenter();

    /**
     * Returns the geometric 3D center of the bond.
     *
     * @return The geometric 3D center of the bond
     */
    Point3d get3DCenter();

    /**
     * Compares a bond with this bond.
     *
     * @param object Object of type Bond
     * @return Return true, if the bond is equal to this bond
     */
    boolean compare(Object object);

    /**
     * Checks whether a bond is connected to another one.
     * This can only be true if the bonds have an Atom in common.
     *
     * @param bond The bond which is checked to be connect with this one
     * @return True, if the bonds share an atom, otherwise false
     */
    boolean isConnectedTo(IBond bond);

    /**
     * Access whether this bond has been marked as aromatic. The default
     * value is false and you must explicitly perceive aromaticity with
     * one of the available models.
     *
     * @return aromatic status
     * @see #getFlag(int)
     * @see org.openscience.cdk.aromaticity.Aromaticity
     */
    boolean isAromatic();

    /**
     * Mark this bond as being aromatic.
     *
     * @param arom aromatic status
     * @see #setFlag(int, boolean)
     */
    void setIsAromatic(boolean arom);

    /**
     * Access whether this bond has been flagged as in a ring. The default
     * value is false and you must explicitly find rings first.
     *
     * @return ring status
     * @see #getFlag(int)
     * @see org.openscience.cdk.ringsearch.RingSearch
     */
    boolean isInRing();

    /**
     * Mark this bond as being in a ring.
     *
     * @param ring ring status
     * @see #setFlag(int, boolean)
     */
    void setIsInRing(boolean ring);

    /**
     * {@inheritDoc}
     */
    @Override
    IBond clone() throws CloneNotSupportedException;
}
