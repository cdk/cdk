/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
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

import org.openscience.cdk.Bond;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

/**
 * Represents a pharmacophore query angle constraint.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.svnrev $Revision$
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see PharmacophoreQueryAtom
 * @see PharmacophoreMatcher
 * @see org.openscience.cdk.isomorphism.matchers.QueryAtomContainer
 */
@TestClass("org.openscience.cdk.pharmacophore.PharmacophoreQueryAngleBondTest")
public class PharmacophoreQueryAngleBond extends Bond implements IQueryBond {
    private double upper;
    private double lower;

    public PharmacophoreQueryAngleBond() {
    }

    /**
     * Create a query angle constraint between three query groups.
     * <p/>
     * Note that the angle is only considered upto 2 decimal places.
     *
     * @param atom1 The first pharmacophore group
     * @param atom2 The second pharmacophore group
     * @param atom3 The third pharmacophore group
     * @param lower The lower bound of the angle between the three groups
     * @param upper The upper bound of the angle between the three groups
     */
    public PharmacophoreQueryAngleBond(PharmacophoreQueryAtom atom1,
                                       PharmacophoreQueryAtom atom2,
                                       PharmacophoreQueryAtom atom3,
                                       double lower, double upper) {
        super(new IAtom[]{atom1, atom2, atom3});
        this.upper = round(upper, 2);
        this.lower = round(lower, 2);
    }

    /**
     * Create a query angle constraint between three query groups.
     * <p/>
     * This constructor allows you to define a query angle constraint
     * such that the angle between the three query groups is exact
     * (i.e., not a range).
     * <p/>
     * Note that the angle is only considered upto 2 decimal places.
     *
     * @param atom1 The first pharmacophore group
     * @param atom2 The second pharmacophore group
     * @param atom3 The third pharmacophore group
     * @param angle The exact angle between the two groups
     */
    public PharmacophoreQueryAngleBond(PharmacophoreQueryAtom atom1,
                                       PharmacophoreQueryAtom atom2,
                                       PharmacophoreQueryAtom atom3,
                                       double angle) {
        super(new PharmacophoreQueryAtom[]{atom1, atom2, atom3});
        this.upper = round(angle, 2);
        this.lower = round(angle, 2);
    }

    /**
     * Checks whether the query angle constraint matches a target distance.
     * <p/>
     * This method checks whether a query constraint is satisfied by an observed
     * angle (represented by a {@link org.openscience.cdk.pharmacophore.PharmacophoreAngleBond} in the target molecule.
     * Note that angles are compared upto 2 decimal places.
     *
     * @param bond The angle relationship in a target molecule
     * @return true if the target angle lies within the range of the query constraint
     */
    @TestMethod("testMatches")
    public boolean matches(IBond bond) {
        if (bond instanceof PharmacophoreAngleBond) {
            PharmacophoreAngleBond pbond = (PharmacophoreAngleBond) bond;
            double bondLength = round(pbond.getBondLength(), 2);
            return bondLength >= lower && bondLength <= upper;
        } else return false;
    }

    @TestMethod("testUpper")
    public double getUpper() {
        return upper;
    }

    @TestMethod("testLower")
    public double getLower() {
        return lower;
    }

    private double round(double val, int places) {
        long factor = (long) Math.pow(10, places);
        val = val * factor;
        long tmp = Math.round(val);
        return (double) tmp / factor;
    }

}