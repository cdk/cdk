package org.openscience.cdk.test.pharmacophore;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.pharmacophore.PharmacophoreAtom;

import javax.vecmath.Point3d;

/**
 * @cdk.module test-pcore
 */
public class PharmacophoreAtomTest {
      
    @Test
    public void testGetterSetter() {
        PharmacophoreAtom patom = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0,0,0));
        Assert.assertEquals("[CX2]N", patom.getSmarts());

        patom.setSmarts("[OX2]");
        Assert.assertEquals("[OX2]", patom.getSmarts());
    }

    @Test
    public void testMatchingAtoms() {
        PharmacophoreAtom patom = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0,0,0));
        patom.setMatchingAtoms(new int[] {1,4,5});
        int[] indices = patom.getMatchingAtoms();
        Assert.assertEquals(1, indices[0]);
        Assert.assertEquals(4, indices[1]);
        Assert.assertEquals(5, indices[2]);
    }

    @Test
    public void testEquals() {
        PharmacophoreAtom patom1 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0,0,0));
        patom1.setMatchingAtoms(new int[] {1,4,5});

        PharmacophoreAtom patom2 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0,0,0));
        patom2.setMatchingAtoms(new int[] {1,4,5});

        PharmacophoreAtom patom3 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0,1,0));
        patom3.setMatchingAtoms(new int[] {1,4,5});

        PharmacophoreAtom patom4 = new PharmacophoreAtom("[CX2]N", "Amine", new Point3d(0,0,0));
        patom4.setMatchingAtoms(new int[] {1,4,6});

        Assert.assertTrue(patom1.equals(patom2));
        Assert.assertFalse(patom1.equals(patom3));
        Assert.assertFalse(patom1.equals(patom4));
    }
}