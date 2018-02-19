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
 * Represents the idea of an chemical atom.
 *
 * @author egonw
 * @cdk.module interfaces
 * @cdk.created 2005-08-24
 * @cdk.keyword atom
 * @cdk.githash
 */
public interface IAtom extends IAtomType {

    /**
     * Sets the partial charge of this atom.
     *
     * @param charge The partial charge
     * @see #getCharge
     */
    void setCharge(Double charge);

    /**
     * Returns the partial charge of this atom.
     *
     * @return the charge of this atom
     * @see #setCharge
     */
    Double getCharge();

    /**
     * Sets the implicit hydrogen count of this atom.
     *
     * @param hydrogenCount The number of hydrogen atoms bonded to this atom.
     * @see #getImplicitHydrogenCount
     */
    void setImplicitHydrogenCount(Integer hydrogenCount);

    /**
     * Returns the implicit hydrogen count of this atom.
     *
     * @return The hydrogen count of this atom.
     * @see #setImplicitHydrogenCount
     */
    Integer getImplicitHydrogenCount();

    /**
     * Sets a point specifying the location of this
     * atom in a 2D space.
     *
     * @param point2d A point in a 2D plane
     * @see #getPoint2d
     */
    void setPoint2d(Point2d point2d);

    /**
     * Sets a point specifying the location of this
     * atom in 3D space.
     *
     * @param point3d A point in a 3-dimensional space
     * @see #getPoint3d
     */
    void setPoint3d(Point3d point3d);

    /**
     * Sets a point specifying the location of this
     * atom in a Crystal unit cell.
     *
     * @param point3d A point in a 3d fractional unit cell space
     * @see #getFractionalPoint3d
     * @see org.openscience.cdk.Crystal
     */
    void setFractionalPoint3d(Point3d point3d);

    /**
     * Sets the stereo parity for this atom.
     *
     * @param stereoParity The stereo parity for this atom
     * @see org.openscience.cdk.CDKConstants for predefined values.
     * @see #getStereoParity
     * @deprecated use {@link IStereoElement}s for storing stereochemistry
     */
    @Deprecated
    void setStereoParity(Integer stereoParity);

    /**
     * Returns a point specifying the location of this
     * atom in a 2D space.
     *
     * @return A point in a 2D plane. Null if unset.
     * @see #setPoint2d
     */
    Point2d getPoint2d();

    /**
     * Returns a point specifying the location of this
     * atom in a 3D space.
     *
     * @return A point in 3-dimensional space. Null if unset.
     * @see #setPoint3d
     */
    Point3d getPoint3d();

    /**
     * Returns a point specifying the location of this
     * atom in a Crystal unit cell.
     *
     * @return A point in 3d fractional unit cell space. Null if unset.
     * @see #setFractionalPoint3d
     * @see org.openscience.cdk.CDKConstants for predefined values.
     */
    Point3d getFractionalPoint3d();

    /**
     * Returns the stereo parity of this atom. It uses the predefined values
     * found in CDKConstants.
     *
     * @return The stereo parity for this atom
     * @see org.openscience.cdk.CDKConstants
     * @see #setStereoParity
     * @deprecated use {@link IStereoElement}s for storing stereochemistry
     */
    @Deprecated
    Integer getStereoParity();

    /**
     * Access the {@link IAtomContainer} of which this atom is a member of. Because atoms
     * can be in multiple molecules this method will only work if the atom has been accessed
     * in the context of an {@link IAtomContainer}, for example:
     *
     * <pre>{@code
     * IAtomContainer mol  = new AtomContainer();
     * IAtom          atom = new Atom(6);
     *
     * atom.getContainer(); // null
     * mol.add(atom);
     * atom.getContainer(); // still null
     * mol.getAtom(0).getContainer(); // not-null, returns 'mol'
     * }</pre>
     *
     * @return the atom container or null if not accessed in the context of a
     *         container
     */
    IAtomContainer getContainer();

    /**
     * Acces the index of an atom in the context of an {@link IAtomContainer}. If the
     * index is not known, &lt; 0 is returned.
     *
     * @return atom index or &lt; 0 if the index is not known
     */
    int getIndex();

    /**
     * Returns the bonds connected to this atom. If the bonds are not
     * known an exception is thrown. This method will only throw an exception
     * if {@link #getIndex()} returns &lt; 0 or {@link #getContainer()} returns null.
     *
     * <pre>{@code
     *
     * IAtom atom = ...;
     *
     * if (atom.getIndex() >= 0) {
     *   for (IBond bond : atom.bonds()) {
     *
     *   }
     * }
     *
     * if (atom.getContainer() != null) {
     *   for (IBond bond : atom.bonds()) {
     *
     *   }
     * }
     *
     * IAtomContainer mol = ...;
     * // guaranteed not throw an exception
     * for (IBond bond : mol.getAtom(i).bonds()) {
     *
     * }
     * }</pre>
     *
     * @return iterable of bonds
     * @throws UnsupportedOperationException thrown if the bonds are not known
     */
    Iterable<IBond> bonds();

    /**
     * Get the number of explicit bonds connected to this atom.
     * @return the total bond count
     */
    int getBondCount();

    /**
     * Returns the bond connecting 'this' atom to the provided atom. If the
     * atoms are not bonded, null is returned.
     * @param atom the other atom
     * @return the bond connecting the atoms
     * @throws UnsupportedOperationException thrown if the bonds are not known
     */
    IBond getBond(IAtom atom);

    /**
     * Access whether this atom has been marked as aromatic. The default
     * value is false and you must explicitly perceive aromaticity with
     * one of the available models.
     *
     * @return aromatic status
     * @see #getFlag(int)
     * @see org.openscience.cdk.aromaticity.Aromaticity
     */
    boolean isAromatic();

    /**
     * Mark this atom as being aromatic.
     *
     * @param arom aromatic status
     * @see #setFlag(int, boolean)
     */
    void setIsAromatic(boolean arom);

    /**
     * Access whether this atom has been flagged as in a ring. The default
     * value is false and you must explicitly find rings first.
     *
     * @return ring status
     * @see #getFlag(int)
     * @see org.openscience.cdk.ringsearch.RingSearch
     */
    boolean isInRing();

    /**
     * Mark this atom as being in a ring.
     *
     * @param ring ring status
     * @see #setFlag(int, boolean)
     */
    void setIsInRing(boolean ring);

    /**
     * {@inheritDoc}
     */
    @Override
    IAtom clone() throws CloneNotSupportedException;

}
