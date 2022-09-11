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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.vecmath.Point3d;

/**
 * @cdk.module test-pcore
 */
class PharmacophoreQueryAngleBondTest {

    @Test
    void testMatches() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(1, 1, 1));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(1, 0, 0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);

        PharmacophoreQueryAtom qatom1 = new PharmacophoreQueryAtom("Amine", "[CX2]N");
        PharmacophoreQueryAtom qatom2 = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        PharmacophoreQueryAtom qatom3 = new PharmacophoreQueryAtom("blah", "C");
        PharmacophoreQueryAngleBond qbond1 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 54.735);
        PharmacophoreQueryAngleBond qbond2 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 50, 60);
        PharmacophoreQueryAngleBond qbond3 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 60, 80);
        PharmacophoreQueryBond qbond4 = new PharmacophoreQueryBond(qatom1, qatom2, 1, 2);

        Assertions.assertTrue(qbond1.matches(pbond));
        Assertions.assertTrue(qbond2.matches(pbond));
        Assertions.assertFalse(qbond3.matches(pbond));
        Assertions.assertFalse(qbond4.matches(pbond));
    }

    @Test
    void testUpper() {
        PharmacophoreQueryAtom qatom1 = new PharmacophoreQueryAtom("Amine", "[CX2]N");
        PharmacophoreQueryAtom qatom2 = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        PharmacophoreQueryAtom qatom3 = new PharmacophoreQueryAtom("blah", "C");
        PharmacophoreQueryAngleBond qbond1 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 54.735);
        PharmacophoreQueryAngleBond qbond2 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 50, 60);

        Assertions.assertEquals(54.74, qbond1.getUpper(), 0.01);
        Assertions.assertEquals(60.00, qbond2.getUpper(), 0.01);
    }

    @Test
    void testLower() {
        PharmacophoreQueryAtom qatom1 = new PharmacophoreQueryAtom("Amine", "[CX2]N");
        PharmacophoreQueryAtom qatom2 = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        PharmacophoreQueryAtom qatom3 = new PharmacophoreQueryAtom("blah", "C");
        PharmacophoreQueryAngleBond qbond1 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 54.735);
        PharmacophoreQueryAngleBond qbond2 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 50, 60);

        Assertions.assertEquals(54.74, qbond1.getLower(), 0.01);
        Assertions.assertEquals(50.00, qbond2.getLower(), 0.01);
    }

    @Test
    void testToString() {
        PharmacophoreQueryAtom qatom1 = new PharmacophoreQueryAtom("Amine", "[CX2]N");
        PharmacophoreQueryAtom qatom2 = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        PharmacophoreQueryAtom qatom3 = new PharmacophoreQueryAtom("blah", "C");
        PharmacophoreQueryAngleBond qbond1 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 54.735);
        String repr = qbond1.toString();
        Assertions.assertEquals(repr, "AC::Amine [[CX2]N]::aromatic [c1ccccc1]::blah [C]::[54.74 - 54.74] ");
    }
}
