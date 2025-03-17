/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IPDBAtom;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IPDBAtom} implementations.
 *
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
        Assertions.assertNotNull(fract);
        Assertions.assertEquals(0.5, fract.x, 0.001);
        Assertions.assertEquals(0.5, fract.y, 0.001);
        Assertions.assertEquals(0.5, fract.z, 0.001);
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
        Assertions.assertNotNull(a.getPoint3d());
        assertEquals(point3d, a.getPoint3d(), 0.001);
    }

    @Test
    @Override
    public void testSetPoint3d_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IPDBAtom a = (IPDBAtom) newChemObject();
        a.setSymbol("C");
        a.setPoint3d(point3d);
        Assertions.assertEquals(point3d, a.getPoint3d());
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
        Assertions.assertTrue(clone instanceof IAtom);
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
        IAtom clone = atom.clone();
        Assertions.assertEquals(clone.getPoint3d().x, 2.0, 0.001);
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
        IAtom clone = atom.clone();
        Assertions.assertEquals(clone.getFractionalPoint3d().x, 2.0, 0.001);
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
            Assertions.assertTrue('\n' != description.charAt(i));
            Assertions.assertTrue('\r' != description.charAt(i));
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
        Assertions.assertEquals(0.00, atom.getCharge(), 0.00000001);
    }

    @Test
    public void testGetRecord() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setRecord("ATOM 1635 N PHE 105 -3.504 9.019 -14.276 1.00 0.00 N");
        Assertions.assertEquals("ATOM 1635 N PHE 105 -3.504 9.019 -14.276 1.00 0.00 N", atom.getRecord());
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
        Assertions.assertEquals(atom.getTempFactor(), 0.0, 001);
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
        Assertions.assertEquals("PHE", atom.getResName());
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
        Assertions.assertEquals("123", atom.getICode());
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
        Assertions.assertEquals("123", atom.getChainID());
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
        Assertions.assertEquals("123", atom.getAltLoc());
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
        Assertions.assertEquals("123", atom.getSegID());
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
        Assertions.assertEquals(123, atom.getSerial().intValue());
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
        Assertions.assertEquals("123", atom.getResSeq());
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
        Assertions.assertTrue(atom.getOxt());
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
        Assertions.assertTrue(atom.getHetAtom());
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
        Assertions.assertEquals(atom.getOccupancy(), 1.0, 0.01);
    }

    @Test
    public void testGetName() {
        IPDBAtom atom = (IPDBAtom) newChemObject();
        atom.setSymbol("C");
        atom.setName("123");
        Assertions.assertEquals("123", atom.getName());
    }

    @Test
    public void testSetName_String() {
        testGetName();
    }
}
