/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.tools.diff.AtomDiff;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IAtom} implementations.
 *
 */
public abstract class AbstractAtomTest extends AbstractAtomTypeTest {

    /**
     * Method to test the get/setCharge() methods.
     */
    @Test
    public void testSetCharge_Double() {
        double charge = 0.15;

        IAtom a = (IAtom) newChemObject();
        a.setCharge(charge);
        Assertions.assertEquals(charge, a.getCharge(), 0.001);
    }

    @Test
    public void testGetCharge() {
        testSetCharge_Double();
    }

    /**
     * Method to test the get/setHydrogenCount() methods.
     */
    @Test
    public void testSetImplicitHydrogenCount_Integer() {
        Integer count = 1;

        IAtom a = (IAtom) newChemObject();
        a.setImplicitHydrogenCount(count);
        Assertions.assertEquals(count, a.getImplicitHydrogenCount());
    }

    @Test
    public void testGetImplicitHydrogenCount() {
        // should be null by default
        IAtom a = (IAtom) newChemObject();
        Assertions.assertNull(a.getImplicitHydrogenCount());
    }

    /**
     * Method to test the setFractional3D() methods.
     */
    @Test
    public void testSetFractionalPoint3d_Point3d() {
        IAtom a = (IAtom) newChemObject();
        a.setFractionalPoint3d(new Point3d(0.5, 0.5, 0.5));
        Point3d fract = a.getFractionalPoint3d();
        Assertions.assertNotNull(fract);
        Assertions.assertEquals(0.5, fract.x, 0.001);
        Assertions.assertEquals(0.5, fract.y, 0.001);
        Assertions.assertEquals(0.5, fract.z, 0.001);
    }

    @Test
    public void testGetFractionalPoint3d() {
        testSetFractionalPoint3d_Point3d();
    }

    @Test
    public void testGetPoint3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IAtom a = (IAtom) newChemObject();
        a.setPoint3d(point3d);
        Assertions.assertNotNull(a.getPoint3d());
        assertEquals(point3d, a.getPoint3d(), 0.001);
    }

    @Test
    public void testSetPoint3d_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IAtom a = (IAtom) newChemObject();
        a.setPoint3d(point3d);
        Assertions.assertEquals(point3d, a.getPoint3d());
    }

    @Test
    public void testGetPoint2d() {
        Point2d point2d = new Point2d(1.0, 2.0);

        IAtom a = (IAtom) newChemObject();
        a.setPoint2d(point2d);
        Assertions.assertNotNull(a.getPoint2d());
        Assertions.assertEquals(point2d.x, a.getPoint2d().x, 0.001);
        Assertions.assertEquals(point2d.y, a.getPoint2d().y, 0.001);
    }

    @Test
    public void testSetPoint2d_Point2d() {
        Point2d point2d = new Point2d(1.0, 2.0);

        IAtom a = (IAtom) newChemObject();
        a.setPoint2d(point2d);
        Assertions.assertEquals(point2d, a.getPoint2d());
    }

    /**
     * Method to test the get/setHydrogenCount() methods.
     */
    @Test
    public void testSetStereoParity_Integer() {
        int parity = CDKConstants.STEREO_ATOM_PARITY_PLUS;

        IAtom a = (IAtom) newChemObject();
        a.setStereoParity(parity);
        Assertions.assertEquals(parity, a.getStereoParity().intValue());
    }

    @Test
    public void testGetStereoParity() {
        testSetStereoParity_Integer();
    }

    /**
     * Method to test the clone() method
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        Object clone = atom.clone();
        Assertions.assertTrue(clone instanceof IAtom);

        // test that everything has been cloned properly
        String diff = AtomDiff.diff(atom, (IAtom) clone);
        Assertions.assertNotNull(diff);
        Assertions.assertEquals(0, diff.length());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_Point2d() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setPoint2d(new Point2d(2, 3));
        IAtom clone = atom.clone();
        Assertions.assertEquals(clone.getPoint2d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_Point3d() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setPoint3d(new Point3d(2, 3, 4));
        IAtom clone = atom.clone();
        Assertions.assertEquals(clone.getPoint3d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_FractionalPoint3d() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setFractionalPoint3d(new Point3d(2, 3, 4));
        IAtom clone = atom.clone();
        Assertions.assertEquals(clone.getFractionalPoint3d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_HydrogenCount() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setImplicitHydrogenCount(3);
        IAtom clone = atom.clone();

        // test cloning
        atom.setImplicitHydrogenCount(4);
        Assertions.assertEquals(3, clone.getImplicitHydrogenCount().intValue());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_StereoParity() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setStereoParity(3);
        IAtom clone = atom.clone();

        // test cloning
        atom.setStereoParity(4);
        Assertions.assertEquals(3, clone.getStereoParity().intValue());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_Charge() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setCharge(1.0);
        IAtom clone = atom.clone();

        // test cloning
        atom.setCharge(5.0);
        Assertions.assertEquals(1.0, clone.getCharge(), 0.001);
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IAtom atom = (IAtom) newChemObject();
        String description = atom.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue('\n' != description.charAt(i));
            Assertions.assertTrue('\r' != description.charAt(i));
        }
    }

    @Test
    public void testToString_FractionalCoordinates() {
        IAtom atom = (IAtom) newChemObject();
        atom.setFractionalPoint3d(new Point3d(2, 3, 4));
        String description = atom.toString();
        Assertions.assertTrue(description.contains("F3D"));
    }

    /**
     * Checks that the default charge is set to NaN
     */
    @Test
    public void testDefaultChargeValue() {
        IAtom atom = (IAtom) newChemObject();
        Assertions.assertEquals(CDKConstants.UNSET, atom.getCharge());
        //        Assert.assertEquals(0.0, atom.getCharge(), 0.00000001);
    }
}
