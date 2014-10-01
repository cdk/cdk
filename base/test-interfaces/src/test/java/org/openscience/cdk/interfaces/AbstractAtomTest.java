/* Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.interfaces;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.tools.diff.AtomDiff;

/**
 * Checks the functionality of {@link IAtom} implementations.
 *
 * @cdk.module test-interfaces
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
        Assert.assertEquals(charge, a.getCharge(), 0.001);
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
        Assert.assertEquals(count, a.getImplicitHydrogenCount());
    }

    @Test
    public void testGetImplicitHydrogenCount() {
        // should be null by default
        IAtom a = (IAtom) newChemObject();
        Assert.assertNull(a.getImplicitHydrogenCount());
    }

    /**
     * Method to test the setFractional3D() methods.
     */
    @Test
    public void testSetFractionalPoint3d_Point3d() {
        IAtom a = (IAtom) newChemObject();
        a.setFractionalPoint3d(new Point3d(0.5, 0.5, 0.5));
        Point3d fract = a.getFractionalPoint3d();
        Assert.assertNotNull(fract);
        Assert.assertEquals(0.5, fract.x, 0.001);
        Assert.assertEquals(0.5, fract.y, 0.001);
        Assert.assertEquals(0.5, fract.z, 0.001);
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
        Assert.assertNotNull(a.getPoint3d());
        assertEquals(point3d, a.getPoint3d(), 0.001);
    }

    @Test
    public void testSetPoint3d_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IAtom a = (IAtom) newChemObject();
        a.setPoint3d(point3d);
        Assert.assertEquals(point3d, a.getPoint3d());
    }

    @Test
    public void testGetPoint2d() {
        Point2d point2d = new Point2d(1.0, 2.0);

        IAtom a = (IAtom) newChemObject();
        a.setPoint2d(point2d);
        Assert.assertNotNull(a.getPoint2d());
        Assert.assertEquals(point2d.x, a.getPoint2d().x, 0.001);
        Assert.assertEquals(point2d.y, a.getPoint2d().y, 0.001);
    }

    @Test
    public void testSetPoint2d_Point2d() {
        Point2d point2d = new Point2d(1.0, 2.0);

        IAtom a = (IAtom) newChemObject();
        a.setPoint2d(point2d);
        Assert.assertEquals(point2d, a.getPoint2d());
    }

    /**
     * Method to test the get/setHydrogenCount() methods.
     */
    @Test
    public void testSetStereoParity_Integer() {
        int parity = CDKConstants.STEREO_ATOM_PARITY_PLUS;

        IAtom a = (IAtom) newChemObject();
        a.setStereoParity(parity);
        Assert.assertEquals(parity, a.getStereoParity().intValue());
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
        Assert.assertTrue(clone instanceof IAtom);

        // test that everything has been cloned properly
        String diff = AtomDiff.diff(atom, (IAtom) clone);
        Assert.assertNotNull(diff);
        Assert.assertEquals(0, diff.length());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_Point2d() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setPoint2d(new Point2d(2, 3));
        IAtom clone = (IAtom) atom.clone();
        Assert.assertEquals(clone.getPoint2d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_Point3d() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setPoint3d(new Point3d(2, 3, 4));
        IAtom clone = (IAtom) atom.clone();
        Assert.assertEquals(clone.getPoint3d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_FractionalPoint3d() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setFractionalPoint3d(new Point3d(2, 3, 4));
        IAtom clone = (IAtom) atom.clone();
        Assert.assertEquals(clone.getFractionalPoint3d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_HydrogenCount() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setImplicitHydrogenCount(Integer.valueOf(3));
        IAtom clone = (IAtom) atom.clone();

        // test cloning
        atom.setImplicitHydrogenCount(Integer.valueOf(4));
        Assert.assertEquals(3, clone.getImplicitHydrogenCount().intValue());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_StereoParity() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setStereoParity(3);
        IAtom clone = (IAtom) atom.clone();

        // test cloning
        atom.setStereoParity(4);
        Assert.assertEquals(3, clone.getStereoParity().intValue());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_Charge() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setCharge(1.0);
        IAtom clone = (IAtom) atom.clone();

        // test cloning
        atom.setCharge(5.0);
        Assert.assertEquals(1.0, clone.getCharge(), 0.001);
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
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }

    @Test
    public void testToString_FractionalCoordinates() {
        IAtom atom = (IAtom) newChemObject();
        atom.setFractionalPoint3d(new Point3d(2, 3, 4));
        String description = atom.toString();
        Assert.assertTrue(description.contains("F3D"));
    }

    /**
     * Checks that the default charge is set to NaN
     */
    @Test
    public void testDefaultChargeValue() {
        IAtom atom = (IAtom) newChemObject();
        Assert.assertEquals(CDKConstants.UNSET, atom.getCharge());
        //        Assert.assertEquals(0.0, atom.getCharge(), 0.00000001);
    }
}
