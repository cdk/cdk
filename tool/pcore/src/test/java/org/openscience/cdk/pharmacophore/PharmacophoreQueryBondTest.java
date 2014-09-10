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
public class PharmacophoreQueryBondTest {

    @Test
    public void testMatches() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(1, 1, 1));
        PharmacophoreBond pbond = new PharmacophoreBond(patom1, patom2);

        PharmacophoreQueryAtom qatom1 = new PharmacophoreQueryAtom("Amine", "[CX2]N");
        PharmacophoreQueryAtom qatom2 = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        PharmacophoreQueryBond qbond1 = new PharmacophoreQueryBond(qatom1, qatom2, 1.0, 2.0);
        PharmacophoreQueryBond qbond2 = new PharmacophoreQueryBond(qatom1, qatom2, 1.732);
        PharmacophoreQueryBond qbond3 = new PharmacophoreQueryBond(qatom1, qatom2, 0.1, 1.0);

        Assert.assertTrue(qbond1.matches(pbond));
        Assert.assertTrue(qbond2.matches(pbond));
        Assert.assertFalse(qbond3.matches(pbond));

    }

    @Test
    public void testUpper() {
        PharmacophoreQueryAtom qatom1 = new PharmacophoreQueryAtom("Amine", "[CX2]N");
        PharmacophoreQueryAtom qatom2 = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        PharmacophoreQueryBond qbond1 = new PharmacophoreQueryBond(qatom1, qatom2, 1.0, 2.0);
        PharmacophoreQueryBond qbond2 = new PharmacophoreQueryBond(qatom1, qatom2, 1.732);

        Assert.assertEquals(2.0, qbond1.getUpper(), 0.01);
        Assert.assertEquals(1.732, qbond2.getUpper(), 0.01);
    }

    @Test
    public void testLower() {
        PharmacophoreQueryAtom qatom1 = new PharmacophoreQueryAtom("Amine", "[CX2]N");
        PharmacophoreQueryAtom qatom2 = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        PharmacophoreQueryBond qbond1 = new PharmacophoreQueryBond(qatom1, qatom2, 1.0, 2.0);
        PharmacophoreQueryBond qbond2 = new PharmacophoreQueryBond(qatom1, qatom2, 1.732);

        Assert.assertEquals(1.0, qbond1.getLower(), 0.01);
        Assert.assertEquals(1.732, qbond2.getLower(), 0.01);
    }

    @Test
    public void testToString() {
        PharmacophoreQueryAtom qatom1 = new PharmacophoreQueryAtom("Amine", "[CX2]N");
        PharmacophoreQueryAtom qatom2 = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        PharmacophoreQueryBond qbond1 = new PharmacophoreQueryBond(qatom1, qatom2, 1.0, 2.0);
        String repr = qbond1.toString();
        Assert.assertEquals(repr, "DC::Amine [[CX2]N]::aromatic [c1ccccc1]::[1.0 - 2.0] ");
    }
}
