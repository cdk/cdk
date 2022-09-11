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
class PharmacophoreQueryAtomTest {

    @Test
    void testGetSmarts() {
        PharmacophoreQueryAtom qatom = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        Assertions.assertEquals("c1ccccc1", qatom.getSmarts());
    }

    @Test
    void testMatches() {
        PharmacophoreQueryAtom qatom = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");

        PharmacophoreAtom patom1 = new PharmacophoreAtom("c1ccccc1", "aromatic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "hydrophobic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("Cc1ccccc1", "aromatic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom4 = new PharmacophoreAtom("[CX2]N", "amine", new Point3d(0, 0, 0));

        Assertions.assertTrue(qatom.matches(patom1));
        Assertions.assertFalse(qatom.matches(patom2));

        Assertions.assertTrue(qatom.matches(patom3));
        Assertions.assertFalse(qatom.matches(patom4));
    }

    @Test
    void testToString() {
        PharmacophoreQueryAtom qatom = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        String repr = qatom.toString();
        Assertions.assertEquals("aromatic [c1ccccc1]", repr);
    }
}
