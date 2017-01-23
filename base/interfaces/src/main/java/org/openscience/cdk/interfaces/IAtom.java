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
 * @cdk.module  interfaces
 *
 * @author      egonw
 * @cdk.created 2005-08-24
 * @cdk.keyword atom
 * @cdk.githash
 */
public interface IAtom extends IAtomType {

    /**
     * Sets the partial charge of this atom.
     *
     * @param  charge  The partial charge
     * @see    #getCharge
     */
    public void setCharge(Double charge);

    /**
     * Returns the partial charge of this atom.
     *
     * @return the charge of this atom
     * @see    #setCharge
     */
    public Double getCharge();

    /**
     * Sets the implicit hydrogen count of this atom.
     *
     * @param  hydrogenCount  The number of hydrogen atoms bonded to this atom.
     * @see    #getImplicitHydrogenCount
     */
    public void setImplicitHydrogenCount(Integer hydrogenCount);

    /**
     * Returns the implicit hydrogen count of this atom.
     *
     * @return    The hydrogen count of this atom.
     * @see       #setImplicitHydrogenCount
     */
    public Integer getImplicitHydrogenCount();

    /**
     * Sets a point specifying the location of this
     * atom in a 2D space.
     *
     * @param  point2d  A point in a 2D plane
     * @see    #getPoint2d
     */
    public void setPoint2d(Point2d point2d);

    /**
     * Sets a point specifying the location of this
     * atom in 3D space.
     *
     * @param  point3d  A point in a 3-dimensional space
     * @see    #getPoint3d
     */
    public void setPoint3d(Point3d point3d);

    /**
     * Sets a point specifying the location of this
     * atom in a Crystal unit cell.
     *
     * @param  point3d  A point in a 3d fractional unit cell space
     * @see    #getFractionalPoint3d
     * @see    org.openscience.cdk.Crystal
     */
    public void setFractionalPoint3d(Point3d point3d);

    /**
     * Sets the stereo parity for this atom.
     *
     * @param  stereoParity  The stereo parity for this atom
     * @see    org.openscience.cdk.CDKConstants for predefined values.
     * @see    #getStereoParity
     * @deprecated use {@link IStereoElement}s for storing stereochemistry
     */
    @Deprecated
    public void setStereoParity(Integer stereoParity);

    /**
     * Returns a point specifying the location of this
     * atom in a 2D space.
     *
     * @return    A point in a 2D plane. Null if unset.
     * @see       #setPoint2d
     */
    public Point2d getPoint2d();

    /**
     * Returns a point specifying the location of this
     * atom in a 3D space.
     *
     * @return    A point in 3-dimensional space. Null if unset.
     * @see       #setPoint3d
     */
    public Point3d getPoint3d();

    /**
     * Returns a point specifying the location of this
     * atom in a Crystal unit cell.
     *
     * @return    A point in 3d fractional unit cell space. Null if unset.
     * @see       #setFractionalPoint3d
     * @see       org.openscience.cdk.CDKConstants for predefined values.
     */
    public Point3d getFractionalPoint3d();

    /**
     * Returns the stereo parity of this atom. It uses the predefined values
     * found in CDKConstants.
     *
     * @return    The stereo parity for this atom
     * @see       org.openscience.cdk.CDKConstants
     * @see       #setStereoParity
     * @deprecated use {@link IStereoElement}s for storing stereochemistry
     */
    @Deprecated
    public Integer getStereoParity();

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
     *{@inheritDoc}
     */
    @Override
    public IAtom clone() throws CloneNotSupportedException;

}
