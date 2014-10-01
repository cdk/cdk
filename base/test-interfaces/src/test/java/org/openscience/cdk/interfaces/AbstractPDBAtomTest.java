/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
 *
 */
package org.openscience.cdk.interfaces;

import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IPDBAtom} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractPDBAtomTest extends AbstractAtomTest {

    /**
     * Method to test the setFractional3D() methods.
     */
    @Test
    @Override
    public void testSetFractionalPoint3d_Point3d() {
        IPDBAtom a = (IPDBAtom) newChemObject();
        a.setSymbol("C");
        a.setFractionalPoint3d(new Point3d(0.5, 0.5, 0.5));
        Point3d fract = a.getFractionalPoint3d();
        Assert.assertNotNull(fract);
        Assert.assertEquals(0.5, fract.x, 0.001);
        Assert.assertEquals(0.5, fract.y, 0.001);
        Assert.assertEquals(0.5, fract.z, 0.001);
    }

    @Test
    @Override
    public void testGetFractionalPoint3d() {
        testSetFractionalPoint3d_Point3d();
    }

    @Test
    @Override
    public void testGetPoint3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IPDBAtom a = (IPDBAtom) newChemObject();
        a.setPoint3d(point3d);
        Assert.assertNotNull(a.getPoint3d());
        assertEquals(point3d, a.getPoint3d(), 0.001);
    }

    @Test
    @Override
    public void testSetPoint3d_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IPDBAtom a = (IPDBAtom) newChemObject();
        a.setSymbol("C");
        a.setPoint3d(point3d);
        Assert.assertEquals(point3d, a.getPoint3d());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        Object clone = atom.clone();
        Assert.assertTrue(clone instanceof IAtom);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    @Override
    public void testClone_Point3d() throws Exception {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setPoint3d(new Point3d(2, 3, 4));
        IAtom clone = (IAtom) atom.clone();
        Assert.assertEquals(clone.getPoint3d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    @Override
    public void testClone_FractionalPoint3d() throws Exception {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setFractionalPoint3d(new Point3d(2, 3, 4));
        IAtom clone = (IAtom) atom.clone();
        Assert.assertEquals(clone.getFractionalPoint3d().x, 2.0, 0.001);
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        String description = atom.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }

    /**
     * Checks that the default charge is set to NaN
     */
    @Test
    @Override
    public void testDefaultChargeValue() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        Assert.assertEquals(0.00, atom.getCharge(), 0.00000001);
    }

    @Test
    public void testGetRecord() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setRecord("ATOM 1635 N PHE 105 -3.504 9.019 -14.276 1.00 0.00 N");
        Assert.assertEquals("ATOM 1635 N PHE 105 -3.504 9.019 -14.276 1.00 0.00 N", atom.getRecord());
    }

    @Test
    public void testSetRecord_String() {
        testGetRecord();
    }

    @Test
    public void testGetTempFactor() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setTempFactor(0.0);
        Assert.assertEquals(atom.getTempFactor(), 0.0, 001);
    }

    @Test
    public void testSetTempFactor_Double() {
        testGetTempFactor();
    }

    @Test
    public void testSetResName_String() {
        testGetResName();
    }

    @Test
    public void testGetResName() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setResName("PHE");
        Assert.assertEquals("PHE", atom.getResName());
    }

    @Test
    public void testSetICode_String() {
        testGetICode();
    }

    @Test
    public void testGetICode() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setICode("123");
        Assert.assertEquals("123", atom.getICode());
    }

    @Test
    public void testSetChainID_String() {
        testGetChainID();
    }

    @Test
    public void testGetChainID() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setChainID("123");
        Assert.assertEquals("123", atom.getChainID());
    }

    @Test
    public void testSetAltLoc_String() {
        testGetAltLoc();
    }

    @Test
    public void testGetAltLoc() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setAltLoc("123");
        Assert.assertEquals("123", atom.getAltLoc());
    }

    @Test
    public void testSetSegID_String() {
        testGetSegID();
    }

    @Test
    public void testGetSegID() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setSegID("123");
        Assert.assertEquals("123", atom.getSegID());
    }

    @Test
    public void testSetSerial_Integer() {
        testGetSerial();
    }

    @Test
    public void testGetSerial() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setSerial(123);
        Assert.assertEquals(123, atom.getSerial().intValue());
    }

    @Test
    public void testSetResSeq_String() {
        testGetResSeq();
    }

    @Test
    public void testGetResSeq() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setResSeq("123");
        Assert.assertEquals("123", atom.getResSeq());
    }

    @Test
    public void testSetOxt_Boolean() {
        testGetOxt();
    }

    @Test
    public void testGetOxt() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setOxt(true);
        Assert.assertTrue(atom.getOxt());
    }

    @Test
    public void testSetHetAtom_Boolean() {
        testGetHetAtom();
    }

    @Test
    public void testGetHetAtom() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setHetAtom(true);
        Assert.assertTrue(atom.getHetAtom());
    }

    @Test
    public void testSetOccupancy_Double() {
        testGetOccupancy();
    }

    @Test
    public void testGetOccupancy() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setOccupancy(1.0);
        Assert.assertEquals(atom.getOccupancy(), 1.0, 0.01);
    }

    @Test
    public void testGetName() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setName("123");
        Assert.assertEquals("123", atom.getName());
    }

    @Test
    public void testSetName_String() {
        testGetName();
    }
}
