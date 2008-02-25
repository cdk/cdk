package org.openscience.cdk.pharmacophore;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.pharmacophore.PharmacophoreAtom;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom;

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
    public void testSetOperator() {

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
}