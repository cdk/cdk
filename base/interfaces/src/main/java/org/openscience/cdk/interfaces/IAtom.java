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
     * Byte enum constants for element atomic numbers
     */
    public static final byte Wildcard = 0;
    public static final byte H        = 1;
    public static final byte He       = 2;
    public static final byte Li       = 3;
    public static final byte Be       = 4;
    public static final byte B        = 5;
    public static final byte C        = 6;
    public static final byte N        = 7;
    public static final byte O        = 8;
    public static final byte F        = 9;
    public static final byte Ne       = 10;
    public static final byte Na       = 11;
    public static final byte Mg       = 12;
    public static final byte Al       = 13;
    public static final byte Si       = 14;
    public static final byte P        = 15;
    public static final byte S        = 16;
    public static final byte Cl       = 17;
    public static final byte Ar       = 18;
    public static final byte K        = 19;
    public static final byte Ca       = 20;
    public static final byte Sc       = 21;
    public static final byte Ti       = 22;
    public static final byte V        = 23;
    public static final byte Cr       = 24;
    public static final byte Mn       = 25;
    public static final byte Fe       = 26;
    public static final byte Co       = 27;
    public static final byte Ni       = 28;
    public static final byte Cu       = 29;
    public static final byte Zn       = 30;
    public static final byte Ga       = 31;
    public static final byte Ge       = 32;
    public static final byte As       = 33;
    public static final byte Se       = 34;
    public static final byte Br       = 35;
    public static final byte Kr       = 36;
    public static final byte Rb       = 37;
    public static final byte Sr       = 38;
    public static final byte Y        = 39;
    public static final byte Zr       = 40;
    public static final byte Nb       = 41;
    public static final byte Mo       = 42;
    public static final byte Tc       = 43;
    public static final byte Ru       = 44;
    public static final byte Rh       = 45;
    public static final byte Pd       = 46;
    public static final byte Ag       = 47;
    public static final byte Cd       = 48;
    public static final byte In       = 49;
    public static final byte Sn       = 50;
    public static final byte Sb       = 51;
    public static final byte Te       = 52;
    public static final byte I        = 53;
    public static final byte Xe       = 54;
    public static final byte Cs       = 55;
    public static final byte Ba       = 56;
    public static final byte La       = 57;
    public static final byte Ce       = 58;
    public static final byte Pr       = 59;
    public static final byte Nd       = 60;
    public static final byte Pm       = 61;
    public static final byte Sm       = 62;
    public static final byte Eu       = 63;
    public static final byte Gd       = 64;
    public static final byte Tb       = 65;
    public static final byte Dy       = 66;
    public static final byte Ho       = 67;
    public static final byte Er       = 68;
    public static final byte Tm       = 69;
    public static final byte Yb       = 70;
    public static final byte Lu       = 71;
    public static final byte Hf       = 72;
    public static final byte Ta       = 73;
    public static final byte W        = 74;
    public static final byte Re       = 75;
    public static final byte Os       = 76;
    public static final byte Ir       = 77;
    public static final byte Pt       = 78;
    public static final byte Au       = 79;
    public static final byte Hg       = 80;
    public static final byte Tl       = 81;
    public static final byte Pb       = 82;
    public static final byte Bi       = 83;
    public static final byte Po       = 84;
    public static final byte At       = 85;
    public static final byte Rn       = 86;
    public static final byte Fr       = 87;
    public static final byte Ra       = 88;
    public static final byte Ac       = 89;
    public static final byte Th       = 90;
    public static final byte Pa       = 91;
    public static final byte U        = 92;
    public static final byte Np       = 93;
    public static final byte Pu       = 94;
    public static final byte Am       = 95;
    public static final byte Cm       = 96;
    public static final byte Bk       = 97;
    public static final byte Cf       = 98;
    public static final byte Es       = 99;
    public static final byte Fm       = 100;
    public static final byte Md       = 101;
    public static final byte No       = 102;
    public static final byte Lr       = 103;
    public static final byte Rf       = 104;
    public static final byte Db       = 105;
    public static final byte Sg       = 106;
    public static final byte Bh       = 107;
    public static final byte Hs       = 108;
    public static final byte Mt       = 109;
    public static final byte Ds       = 110;
    public static final byte Rg       = 111;
    public static final byte Cn       = 112;
    public static final byte Nh       = 113;
    public static final byte Fl       = 114;
    public static final byte Mc       = 115;
    public static final byte Lv       = 116;
    public static final byte Ts       = 117;
    public static final byte Og       = 118;

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
