package org.openscience.cdk.pharmacophore;

import org.junit.Assert;
import org.junit.Test;

import javax.vecmath.Point3d;

/**
 * @cdk.module test-pcore
 */
public class PharmacophoreAngleBondTest {


    @Test
    public void testGetAngle1() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(1, 1, 1));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(2, 2, 2));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assert.assertEquals(180, pbond.getBondLength(), 0.00001);
    }

    @Test
    public void testGetAngle2() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0, 0, 0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(1, 1, 1));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(0,0,0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assert.assertEquals(0, pbond.getBondLength(), 0.00001);
    }

    @Test
    public void testGetAngle3() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0,1,0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(0,0,0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(1,0,0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assert.assertEquals(90.0, pbond.getBondLength(), 0.00001);
    }

    @Test
    public void testGetAngle4() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(1,1,0));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(0,0,0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(1,0,0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assert.assertEquals(45.0, pbond.getBondLength(), 0.00001);
    }

    @Test
    public void testGetAngle5() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(1,1,1));
        PharmacophoreAtom patom2 = new PharmacophoreAtom("c1ccccc1", "Aromatic", new Point3d(0,0,0));
        PharmacophoreAtom patom3 = new PharmacophoreAtom("C", "Blah", new Point3d(1,0,0));
        PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(patom1, patom2, patom3);
        Assert.assertEquals(54.7356, pbond.getBondLength(), 0.0001);
    }

}