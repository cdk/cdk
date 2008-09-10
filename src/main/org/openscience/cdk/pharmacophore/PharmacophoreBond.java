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
 * Represents a distance relationship between two pharmacophore groups.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.svnrev  $Revision$
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreAtom
 */
@TestClass("org.openscience.cdk.pharmacophore.PharmacophoreBondTest")
public class PharmacophoreBond extends Bond {

    /**
     * Create a pharmacophore distance constraint.
     *
     * @param patom1 The first pharmacophore group
     * @param patom2 The second pharmacophore group
     */
    public PharmacophoreBond(PharmacophoreAtom patom1, PharmacophoreAtom patom2) {
        super(patom1, patom2);
    }

    /**
     * Get the distance between the two pharmacophore groups that make up the constraint.
     *
     * @return The distance between the two groups
     */
    @TestMethod("testGetBondLength")
    public double getBondLength() {
        PharmacophoreAtom atom1 = (PharmacophoreAtom) getAtom(0);
        PharmacophoreAtom atom2 = (PharmacophoreAtom) getAtom(1);
        return atom1.getPoint3d().distance(atom2.getPoint3d());
    }


}
