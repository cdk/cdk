package org.openscience.cdk.pharmacophore;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.pharmacophore.PharmacophoreAtom;
import org.openscience.cdk.pharmacophore.PharmacophoreBond;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryBond;

import javax.vecmath.Point3d;

/**
 * @cdk.module test-pcore
 */
public class PharmacophoreQueryBondTest {

    @Test
    public void testMatches() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0,0,0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(1,1,1));
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
}