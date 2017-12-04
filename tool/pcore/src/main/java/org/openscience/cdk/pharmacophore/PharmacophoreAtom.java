/* Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.AtomRef;

import javax.vecmath.Point3d;
import java.util.Arrays;

/**
 * A representation of a pharmacophore group.
 * 
 * In general this class is used internally for pharmacophore matchin and does not be instantiated
 * by the user. However after a successful match the user will get access to objects of this class
 * which match parts of a query.
 * 
 * The main features of a pharmacophore group are the SMARTS pattern defining what the group
 * is meant to identify and the atoms of a molecule that correspond to the SMARTS pattern.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.githash
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreMatcher
 * @see org.openscience.cdk.pharmacophore.PharmacophoreBond
 */
public class PharmacophoreAtom extends Atom {

    private String smarts;
    private int[]  matchingAtoms;

    /**
     * Create a pharmacophore group.
     *
     * @param smarts      The SMARTS pattern for the group
     * @param symbol      The label for this group.
     * @param coordinates The coordinates for the group. Note that since a pharmacophore group may match
     *                    multiple atoms (say a c1ccccc1 group), the coordinates for the group are the effective coordinates
     *                    of all the atoms for the group. In effect this means that for multi-atom groups, the coordinate
     *                    is simply the mean of the coordinates of the individual atoms for the group.
     */
    public PharmacophoreAtom(String smarts, String symbol, Point3d coordinates) {
        this.smarts = smarts;
        this.symbol = symbol;
        setPoint3d(coordinates);
    }

    /**
     * Create a pharmacophore group.
     *
     * @param pharmacophoreAtom A previously created pharmacophore group
     */
    public PharmacophoreAtom(PharmacophoreAtom pharmacophoreAtom) {
        this.smarts = pharmacophoreAtom.getSmarts();
        this.symbol = pharmacophoreAtom.getSymbol();
        setPoint3d(new Point3d(pharmacophoreAtom.getPoint3d()));
        if (pharmacophoreAtom.getMatchingAtoms() != null) {
            int[] indices = pharmacophoreAtom.getMatchingAtoms();
            matchingAtoms = new int[indices.length];
            System.arraycopy(indices, 0, matchingAtoms, 0, indices.length);
        }
    }

    public static PharmacophoreAtom get(IAtom atom) {
        if (atom instanceof PharmacophoreAtom)
            return (PharmacophoreAtom) atom;
        if (atom instanceof AtomRef)
            return get(((AtomRef) atom).deref());
        return null;
    }

    /**
     * Set the SMARTS for the group.
     *
     * @param smarts The SMARTS pattern
     */
    public void setSmarts(String smarts) {
        this.smarts = smarts;
    }

    /**
     * Get the SMARTS for the group.
     *
     * @return The SMARTS pattern
     * @see #setSmarts(String)
     */
    public String getSmarts() {
        return smarts;
    }

    /**
     * Set the atoms of a target molecule that correspond to this group.
     * 
     * This method is generally only useful in the context of pharmacophore matching
     *
     * @param atomIndices The indicies of the atoms in a molecule that match
     *                    the pattern for this group.
     * @see #getMatchingAtoms()
     * @see org.openscience.cdk.pharmacophore.PharmacophoreMatcher
     */
    public void setMatchingAtoms(int[] atomIndices) {
        this.matchingAtoms = new int[atomIndices.length];
        System.arraycopy(atomIndices, 0, this.matchingAtoms, 0, atomIndices.length);
        Arrays.sort(matchingAtoms);
    }

    /**
     * Get the atoms of a target molecule that correspond to this group.
     * 
     * This method is generally only useful in the context of pharmacophore matching
     *
     * @return The indices of the atoms, in a molecule, that match the pattern for this group.
     * @see #setMatchingAtoms(int[])
     * @see org.openscience.cdk.pharmacophore.PharmacophoreMatcher
     */
    public int[] getMatchingAtoms() {
        return matchingAtoms;
    }

    @Override public int hashCode() {
        int result = smarts != null ? smarts.hashCode() : 0;
        result = 31 * result + (matchingAtoms != null ? Arrays.hashCode(matchingAtoms) : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PharmacophoreAtom)) return false;

        PharmacophoreAtom that = (PharmacophoreAtom) o;
        return smarts.equals(that.getSmarts()) && symbol.equals(that.getSymbol())
                && point3d.equals(that.getPoint3d()) && Arrays.equals(this.matchingAtoms, that.matchingAtoms);
    }

}
