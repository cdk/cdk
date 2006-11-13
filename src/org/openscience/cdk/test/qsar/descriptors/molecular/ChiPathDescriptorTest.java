package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.test.CDKTestCase;

import javax.vecmath.Point2d;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class ChiPathDescriptorTest extends CDKTestCase {

    public ChiPathDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(ChiPathDescriptorTest.class);
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
        IBond b1 = mol.getBuilder().newBond(a2, a1, 1.0);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, 1.0);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a4, a2, 1.0);
        mol.addBond(b4);


        ChiPathDescriptor desc = new ChiPathDescriptor();
        DoubleArrayResult ret = (DoubleArrayResult) desc.calculate(mol).getValue();

        assertEquals(0.5774, ret.get(0), 0.0001);
        assertEquals(0.0000, ret.get(1), 0.0001);
        assertEquals(0.0000, ret.get(2), 0.0001);
        assertEquals(0.0000, ret.get(3), 0.0001);

        assertEquals(0.3333, ret.get(5), 0.0001);
        assertEquals(0.0000, ret.get(6), 0.0001);
        assertEquals(0.0000, ret.get(7), 0.0001);
        assertEquals(0.0000, ret.get(8), 0.0001);
    }

    public void testDan80() throws CDKException {
        IMolecule mol = new Molecule();
        IAtom a1 = mol.getBuilder().newAtom("C");
        a1.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newAtom("C");
        a2.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newAtom("C");
        a3.setPoint2d(new Point2d(-1.4265847744427305, -0.46352549156242084));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newAtom("C");
        a4.setPoint2d(new Point2d(-2.3082626528814396, 0.7500000000000002));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newAtom("O");
        a5.setPoint2d(new Point2d(-1.42658477444273, 1.9635254915624212));
        mol.addAtom(a5);
        IBond b1 = mol.getBuilder().newBond(a2, a1, 2.0);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, 2.0);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a5, a4, 1.0);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a5, a1, 1.0);
        mol.addBond(b5);

        ChiPathDescriptor desc = new ChiPathDescriptor();
        DoubleArrayResult ret = (DoubleArrayResult) desc.calculate(mol).getValue();

        assertEquals(1.2500, ret.get(0), 0.0001);
        assertEquals(0.8839, ret.get(1), 0.0001);
        assertEquals(0.0000, ret.get(2), 0.0001);
        assertEquals(0.0000, ret.get(3), 0.0001);

        assertEquals(0.4254, ret.get(5), 0.0001);
        assertEquals(0.2268, ret.get(6), 0.0001);
        assertEquals(0.0000, ret.get(7), 0.0001);
        assertEquals(0.0000, ret.get(8), 0.0001);

    }


    public void testDan81() throws CDKException {
        IMolecule mol = new Molecule();
        IAtom a1 = mol.getBuilder().newAtom("C");
        a1.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newAtom("C");
        a2.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newAtom("C");
        a3.setPoint2d(new Point2d(-1.4265847744427305, -0.46352549156242084));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newAtom("C");
        a4.setPoint2d(new Point2d(-2.3082626528814396, 0.7500000000000002));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newAtom("S");
        a5.setPoint2d(new Point2d(-1.42658477444273, 1.9635254915624212));
        mol.addAtom(a5);
        IBond b1 = mol.getBuilder().newBond(a2, a1, 2.0);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, 2.0);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a5, a4, 1.0);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a5, a1, 1.0);
        mol.addBond(b5);

        ChiPathDescriptor desc = new ChiPathDescriptor();
        DoubleArrayResult ret = (DoubleArrayResult) desc.calculate(mol).getValue();

        assertEquals(1.2500, ret.get(0), 0.0001);
        assertEquals(0.8839, ret.get(1), 0.0001);
        assertEquals(0.0000, ret.get(2), 0.0001);
        assertEquals(0.0000, ret.get(3), 0.0001);

        assertEquals(1.0539, ret.get(5), 0.0001);
        assertEquals(0.6804, ret.get(6), 0.0001);
        assertEquals(0.0000, ret.get(7), 0.0001);
        assertEquals(0.0000, ret.get(8), 0.0001);
    }

    public void testDan82() throws CDKException {

        IMolecule mol = new Molecule();
        IAtom a1 = mol.getBuilder().newAtom("C");
        a1.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newAtom("C");
        a2.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newAtom("C");
        a3.setPoint2d(new Point2d(-1.4265847744427305, -0.46352549156242084));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newAtom("C");
        a4.setPoint2d(new Point2d(-2.3082626528814396, 0.7500000000000002));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newAtom("O");
        a5.setPoint2d(new Point2d(-1.42658477444273, 1.9635254915624212));
        mol.addAtom(a5);
        IBond b1 = mol.getBuilder().newBond(a2, a1, 1.0);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, 2.0);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, 1.0);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a5, a4, 1.0);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a5, a1, 1.0);
        mol.addBond(b5);


        ChiPathDescriptor desc = new ChiPathDescriptor();
        DoubleArrayResult ret = (DoubleArrayResult) desc.calculate(mol).getValue();

        assertEquals(1.2500, ret.get(0), 0.0001);
        assertEquals(0.8839, ret.get(1), 0.0001);
        assertEquals(0.0000, ret.get(2), 0.0001);
        assertEquals(0.0000, ret.get(3), 0.0001);

        assertEquals(0.5948, ret.get(5), 0.0001);
        assertEquals(0.3402, ret.get(6), 0.0001);
        assertEquals(0.0000, ret.get(7), 0.0001);
        assertEquals(0.0000, ret.get(8), 0.0001);
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
        IBond b1 = mol.getBuilder().newBond(a2, a1, 2.0);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, 2.0);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a5, a4, 1.0);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a6, a5, 2.0);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newBond(a6, a1, 1.0);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newBond(a7, a6, 1.0);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newBond(a8, a2, 1.0);
        mol.addBond(b8);


        ChiPathDescriptor desc = new ChiPathDescriptor();
        DoubleArrayResult ret = (DoubleArrayResult) desc.calculate(mol).getValue();

        assertEquals(2.1986, ret.get(0), 0.0001);
        assertEquals(1.7374, ret.get(1), 0.0001);
        assertEquals(0.9714, ret.get(2), 0.0001);
        assertEquals(0.4512, ret.get(3), 0.0001);

        assertEquals(1.2568, ret.get(5), 0.0001);
        assertEquals(0.8963, ret.get(6), 0.0001);
        assertEquals(0.3849, ret.get(7), 0.0001);
        assertEquals(0.1878, ret.get(8), 0.0001);
    }

//    public void testDan277() throws CDKException {
//
//        IMolecule molecule = null;
//        ChiPathDescriptor desc = new ChiPathDescriptor();
//        DoubleArrayResult ret = (DoubleArrayResult) desc.calculate(mol).getValue();
//
//        assertEquals(4.1069, ret.get(0), 0.0001);
//        assertEquals(3.5527, ret.get(1), 0.0001);
//        assertEquals(2.0065, ret.get(2), 0.0001);
//        assertEquals(1.3853, ret.get(3), 0.00001);
//
//        assertEquals(2.6211, ret.get(5), 0.0001);
//        assertEquals(2.3405, ret.get(6), 0.0001);
//        assertEquals(0.88578, ret.get(7), 0.00001);
//        assertEquals(0.489996, ret.get(8), 0.00001);
//    }

}
