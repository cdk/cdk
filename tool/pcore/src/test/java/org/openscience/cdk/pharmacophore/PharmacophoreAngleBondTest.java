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
class PharmacophoreAngleBondTest {

    @Test
    void testGetAngle1() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(1, 1, 1));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(2, 2, 2));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assertions.assertEquals(180, pbond.getBondLength(), 0.00001);
    }

    @Test
    void testGetAngle2() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(1, 1, 1));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(0, 0, 0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assertions.assertEquals(0, pbond.getBondLength(), 0.00001);
    }

    @Test
    void testGetAngle3() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 1, 0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(1, 0, 0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assertions.assertEquals(90.0, pbond.getBondLength(), 0.00001);
    }

    @Test
    void testGetAngle4() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(1, 1, 0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(1, 0, 0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assertions.assertEquals(45.0, pbond.getBondLength(), 0.00001);
    }

    @Test
    void testGetAngle5() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(1, 1, 1));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(1, 0, 0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assertions.assertEquals(54.7356, pbond.getBondLength(), 0.0001);
    }

}
