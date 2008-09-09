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

/**
 * Represents an angle relationship between three pharmacophore groups.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.svnrev  $Revision$
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see PharmacophoreAtom
 */
@TestClass("org.openscience.cdk.pharmacophore.PharmacophoreAngleBondTest")
public class PharmacophoreAngleBond extends Bond {

    /**
     * Create a pharmacophore distance constraint.
     *
     * @param patom1 The first pharmacophore group
     * @param patom2 The second pharmacophore group
     * @param patom3 The third pharmacophore group
     */
    public PharmacophoreAngleBond(PharmacophoreAtom patom1, PharmacophoreAtom patom2, PharmacophoreAtom patom3) {
        super(new PharmacophoreAtom[] {patom1, patom2, patom3});
    }

    /**
     * Get the angle between the three pharmacophore groups that make up the constraint.
     *
     * @return The angle in degrees between the two groups
     */
    @TestMethod("testGetAngle1,testGetAngle2,testGetAngle3,testGetAngle4,testGetAngle5")
    public double getBondLength() {
        double epsilon = 1e-3;
        PharmacophoreAtom atom1 = (PharmacophoreAtom) getAtom(0);
        PharmacophoreAtom atom2 = (PharmacophoreAtom) getAtom(1);
        PharmacophoreAtom atom3 = (PharmacophoreAtom) getAtom(2);

        double a2 = atom3.getPoint3d().distanceSquared(atom1.getPoint3d());
        double b2 = atom3.getPoint3d().distanceSquared(atom2.getPoint3d());
        double c2 = atom2.getPoint3d().distanceSquared(atom1.getPoint3d());

        double cosangle = (b2+c2-a2) / (2*Math.sqrt(b2)*Math.sqrt(c2));
        if (-1.0-epsilon < cosangle && -1.0+epsilon > cosangle) return 180.0;
        if (1.0-epsilon < cosangle && 1.0+epsilon > cosangle) return 0.0;

        return Math.acos(cosangle) * 180.0 / Math.PI;
    }


}