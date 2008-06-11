package org.openscience.cdk.pharmacophore;

import org.junit.Assert;
import org.junit.Test;

import javax.vecmath.Point3d;

/**
 * @cdk.module test-pcore
 */
public class PharmacophoreQueryAngleBondTest {

    @Test
    public void testMatches() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(1,1,1));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(0,0,0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(1,0,0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);

        PharmacophoreQueryAtom qatom1 = new PharmacophoreQueryAtom("Amine", "[CX2]N");
        PharmacophoreQueryAtom qatom2 = new PharmacophoreQueryAtom("aromatic", "c1ccccc1");
        PharmacophoreQueryAtom qatom3 = new PharmacophoreQueryAtom("blah", "C");
        PharmacophoreQueryAngleBond qbond1 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 54.735);
        PharmacophoreQueryAngleBond qbond2 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 50,60);
        PharmacophoreQueryAngleBond qbond3 = new PharmacophoreQueryAngleBond(qatom1, qatom2, qatom3, 60,80);

        Assert.assertTrue(qbond1.matches(pbond));
        Assert.assertTrue(qbond2.matches(pbond));
        Assert.assertFalse(qbond3.matches(pbond));

    }
}