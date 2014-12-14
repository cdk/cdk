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
public class PharmacophoreQueryAtomTest {

    @Test
    public void testGetSmarts() {
        PharmacophoreQueryAtom qatom = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        Assert.assertEquals("c1ccccc1", qatom.getSmarts());
    }

    @Test
    public void testMatches() {
        PharmacophoreQueryAtom qatom = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");

        PharmacophoreAtom patom1 = new PharmacophoreAtom("c1ccccc1", "aromatic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "hydrophobic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("Cc1ccccc1", "aromatic", new Point3d(0, 0, 0));
        PharmacophoreAtom patom4 = new PharmacophoreAtom("[CX2]N", "amine", new Point3d(0, 0, 0));

        Assert.assertTrue(qatom.matches(patom1));
        Assert.assertFalse(qatom.matches(patom2));

        Assert.assertTrue(qatom.matches(patom3));
        Assert.assertFalse(qatom.matches(patom4));
    }

    @Test
    public void testToString() {
        PharmacophoreQueryAtom qatom = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        String repr = qatom.toString();
        Assert.assertEquals("aromatic [c1ccccc1]", repr);
    }
}
