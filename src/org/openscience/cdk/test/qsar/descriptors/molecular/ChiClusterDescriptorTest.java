package org.openscience.cdk.test.qsar.descriptors.molecular;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class ChiClusterDescriptorTest extends MolecularDescriptorTest {

    public ChiClusterDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(ChiClusterDescriptorTest.class);
    }

    public void setUp() {
    	descriptor = new ChiClusterDescriptor();
    }

    public void testDan64() throws CDKException {
        IMolecule mol = new Molecule();
        IAtom a1 = mol.getBuilder().newAtom("C");
        a1.setPoint2d(new Point2d(0.7500000000000004, 2.799038105676658));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newAtom("C");
        a2.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newAtom("C");
        a3.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newAtom("O");
        a4.setPoint2d(new Point2d(-1.2990381056766582, 0.7500000000000001));
        mol.addAtom(a4);
        IBond b1 = mol.getBuilder().newBond(a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a4, a2, IBond.Order.SINGLE);
        mol.addBond(b4);

        DoubleArrayResult ret = (DoubleArrayResult) descriptor.calculate(mol).getValue();

        assertEquals(0.2887, ret.get(0), 0.0001);
        assertEquals(0.0000, ret.get(1), 0.0001);
        assertEquals(0.0000, ret.get(2), 0.0001);
        assertEquals(0.0000, ret.get(3), 0.0001);
        assertEquals(0.1667, ret.get(4), 0.0001);
        assertEquals(0.0000, ret.get(5), 0.0001);
        assertEquals(0.0000, ret.get(6), 0.0001);
        assertEquals(0.0000, ret.get(7), 0.0001);
    }

    public void testDan154() throws CDKException {

        IMolecule mol = new Molecule();
        IAtom a1 = mol.getBuilder().newAtom("C");
        a1.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newAtom("C");
        a2.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newAtom("C");
        a3.setPoint2d(new Point2d(-1.2990381056766584, -0.7500000000000001));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newAtom("C");
        a4.setPoint2d(new Point2d(-2.598076211353316, -2.220446049250313E-16));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newAtom("C");
        a5.setPoint2d(new Point2d(-2.5980762113533165, 1.5));
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newAtom("C");
        a6.setPoint2d(new Point2d(-1.2990381056766582, 2.2500000000000004));
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newAtom("Cl");
        a7.setPoint2d(new Point2d(-1.2990381056766582, 3.7500000000000004));
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newAtom("Cl");
        a8.setPoint2d(new Point2d(1.2990381056766576, -0.7500000000000007));
        mol.addAtom(a8);
        IBond b1 = mol.getBuilder().newBond(a2, a1, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a6, a5, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newBond(a6, a1, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newBond(a7, a6, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newBond(a8, a2, IBond.Order.SINGLE);
        mol.addBond(b8);

        DoubleArrayResult ret = (DoubleArrayResult) descriptor.calculate(mol).getValue();

        assertEquals(0.5774, ret.get(0), 0.0001);
        assertEquals(0.0000, ret.get(1), 0.0001);
        assertEquals(0.0000, ret.get(2), 0.0001);
        assertEquals(0.0000, ret.get(3), 0.0001);
        assertEquals(0.3780, ret.get(4), 0.0001);
        assertEquals(0.0000, ret.get(5), 0.0001);
        assertEquals(0.0000, ret.get(6), 0.0001);
        assertEquals(0.0000, ret.get(7), 0.0001);
    }

    public void testDan248() throws CDKException {

        IMolecule mol = new Molecule();
        IAtom a1 = mol.getBuilder().newAtom("C");
        a1.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newAtom("C");
        a2.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newAtom("C");
        a3.setPoint2d(new Point2d(-1.2990381056766584, -0.7500000000000001));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newAtom("C");
        a4.setPoint2d(new Point2d(-2.598076211353316, -2.220446049250313E-16));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newAtom("C");
        a5.setPoint2d(new Point2d(-2.5980762113533165, 1.5));
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newAtom("C");
        a6.setPoint2d(new Point2d(-1.2990381056766582, 2.2500000000000004));
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newAtom("C");
        a7.setPoint2d(new Point2d(-3.897114317029975, 2.249999999999999));
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newAtom("O");
        a8.setPoint2d(new Point2d(-1.2990381056766587, -2.25));
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newAtom("C");
        a9.setPoint2d(new Point2d(1.477211629518312, 1.2395277334996044));
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newAtom("C");
        a10.setPoint2d(new Point2d(0.5130302149885025, 2.909538931178863));
        mol.addAtom(a10);
        IBond b1 = mol.getBuilder().newBond(a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a5, a4, IBond.Order.DOUBLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a6, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newBond(a6, a1, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newBond(a7, a5, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newBond(a8, a3, IBond.Order.DOUBLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newBond(a9, a1, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newBond(a10, a1, IBond.Order.SINGLE);
        mol.addBond(b10);

        DoubleArrayResult ret = (DoubleArrayResult) descriptor.calculate(mol).getValue();

        assertEquals(1.7845, ret.get(0), 0.0001);
        assertEquals(0.2500, ret.get(1), 0.0001);
        assertEquals(0.0000, ret.get(2), 0.0001);
        assertEquals(0.0000, ret.get(3), 0.0001);
        assertEquals(1.4946, ret.get(4), 0.0001);
        assertEquals(0.2500, ret.get(5), 0.0001);
        assertEquals(0.0000, ret.get(6), 0.0001);
        assertEquals(0.0000, ret.get(7), 0.0001);
    }

//    public void testDan277() throws CDKException {
//
//        IMolecule mol = null;
//
//        ChiClusterDescriptor desc = new ChiClusterDescriptor();
//        DoubleArrayResult ret = (DoubleArrayResult) desc.calculate(mol).getValue();
//
//        assertEquals(0.0000, ret.get(0), 0.0001);
//        assertEquals(0.0000, ret.get(1), 0.0001);
//        assertEquals(0.0000, ret.get(2), 0.0001);
//        assertEquals(0.08333, ret.get(3), 0.00001);
//        assertEquals(0.0000, ret.get(4), 0.0001);
//        assertEquals(0.0000, ret.get(5), 0.0001);
//        assertEquals(0.0000, ret.get(6), 0.0001);
//        assertEquals(0.02778, ret.get(7), 0.00001);
//    }

}
