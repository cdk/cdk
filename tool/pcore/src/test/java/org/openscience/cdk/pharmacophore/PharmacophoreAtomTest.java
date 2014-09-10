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

import org.junit.Assert;
import org.junit.Test;

import javax.vecmath.Point3d;

/**
 * @cdk.module test-pcore
 */
public class PharmacophoreAtomTest {

    @Test
    public void testGetterSetter() {
        PharmacophoreAtom patom = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        Assert.assertEquals("[CX2]N", patom.getSmarts());

        patom.setSmarts("[OX2]");
        Assert.assertEquals("[OX2]", patom.getSmarts());
    }

    @Test
    public void testMatchingAtoms() {
        PharmacophoreAtom patom = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        patom.setMatchingAtoms(new int[]{1, 4, 5});
        int[] indices = patom.getMatchingAtoms();
        Assert.assertEquals(1, indices[0]);
        Assert.assertEquals(4, indices[1]);
        Assert.assertEquals(5, indices[2]);
    }

    @Test
    public void testEquals() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        patom1.setMatchingAtoms(new int[]{1, 4, 5});

        PharmacophoreAtom patom2 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        patom2.setMatchingAtoms(new int[]{1, 4, 5});

        PharmacophoreAtom patom3 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 1, 0));
        patom3.setMatchingAtoms(new int[]{1, 4, 5});

        PharmacophoreAtom patom4 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        patom4.setMatchingAtoms(new int[]{1, 4, 6});

        Assert.assertEquals(patom2, patom1);
        Assert.assertNotSame(patom3, patom1);
        Assert.assertNotSame(patom4, patom1);
    }
}
