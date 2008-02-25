package org.openscience.cdk.pharmacophore;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.pharmacophore.PharmacophoreAtom;
import org.openscience.cdk.pharmacophore.PharmacophoreBond;

import javax.vecmath.Point3d;

/**
 * @cdk.module test-pcore
 */
public class PharmacophoreBondTest {


    @Test
    public void testGetBondLength() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0,0,0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(1,1,1));
        PharmacophoreBond pbond = new PharmacophoreBond(patom1, patom2);
        Assert.assertEquals(1.732051, pbond.getBondLength(), 0.00001);
    }


}