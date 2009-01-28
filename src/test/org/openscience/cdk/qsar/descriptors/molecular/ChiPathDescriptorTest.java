package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.result.DoubleArrayResult;

import javax.vecmath.Point2d;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class ChiPathDescriptorTest extends MolecularDescriptorTest {

    public ChiPathDescriptorTest() {
    }

    @Before
    public void setUp() throws Exception {
    	setDescriptor(ChiPathClusterDescriptor.class);
    }

    @Test
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

        Assert.assertEquals(2.9916, ret.get(0), 0.0001);
        Assert.assertEquals(1.8938, ret.get(1), 0.0001);
        Assert.assertEquals(1.6825, ret.get(2), 0.0001);
        Assert.assertEquals(0.5773, ret.get(3), 0.0001);

        Assert.assertEquals(0.0000, ret.get(5), 0.0001);
        Assert.assertEquals(0.0000, ret.get(6), 0.0001);
        Assert.assertEquals(0.0000, ret.get(7), 0.0001);
        Assert.assertEquals(2.6927, ret.get(8), 0.0001);
        Assert.assertEquals(1.5099, ret.get(9), 0.0001);
        Assert.assertEquals(1.1439, ret.get(10), 0.0001);
    }

    @Test public void testDan80() throws CDKException {
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
        IBond b1 = mol.getBuilder().newBond(a2, a1, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a5, a1, IBond.Order.SINGLE);
        mol.addBond(b5);

        DoubleArrayResult ret = (DoubleArrayResult) descriptor.calculate(mol).getValue();

        Assert.assertEquals(3.5355, ret.get(0), 0.0001);
        Assert.assertEquals(2.5000, ret.get(1), 0.0001);
        Assert.assertEquals(1.7678, ret.get(2), 0.0001);
        Assert.assertEquals(1.25, ret.get(3), 0.0001);

        Assert.assertEquals(0.0000, ret.get(5), 0.0001);
        Assert.assertEquals(2.7176, ret.get(8), 0.0001);
        Assert.assertEquals(1.4714, ret.get(9), 0.0001);
        Assert.assertEquals(0.7931, ret.get(10), 0.0001);

    }


    @Test public void testDan81() throws CDKException {
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
        IBond b1 = mol.getBuilder().newBond(a2, a1, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a5, a1, IBond.Order.SINGLE);
        mol.addBond(b5);

        DoubleArrayResult ret = (DoubleArrayResult) descriptor.calculate(mol).getValue();

        Assert.assertEquals(3.5355, ret.get(0), 0.0001);
        Assert.assertEquals(2.5000, ret.get(1), 0.0001);
        Assert.assertEquals(1.7678, ret.get(2), 0.0001);
        Assert.assertEquals(1.2500, ret.get(3), 0.0001);

        Assert.assertEquals(3.5341, ret.get(8), 0.0001);
        Assert.assertEquals(2.4142, ret.get(9), 0.0001);
        Assert.assertEquals(1.6096, ret.get(10), 0.0001);
        Assert.assertEquals(1.0539, ret.get(11), 0.0001);
    }

    @Test public void testDan82() throws CDKException {

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
        IBond b1 = mol.getBuilder().newBond(a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a5, a1, IBond.Order.SINGLE);
        mol.addBond(b5);

        DoubleArrayResult ret = (DoubleArrayResult) descriptor.calculate(mol).getValue();

        Assert.assertEquals(3.5355, ret.get(0), 0.0001);
        Assert.assertEquals(2.5000, ret.get(1), 0.0001);
        Assert.assertEquals(1.7678, ret.get(2), 0.0001);
        Assert.assertEquals(1.2500, ret.get(3), 0.0001);

        Assert.assertEquals(2.9772, ret.get(8), 0.0001);
        Assert.assertEquals(1.7272, ret.get(9), 0.0001);
        Assert.assertEquals(1.0089, ret.get(10), 0.0001);
        Assert.assertEquals(0.5948, ret.get(11), 0.0001);
    }

    @Test public void testDan154() throws CDKException {

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

        Assert.assertEquals(5.9831, ret.get(0), 0.0001);
        Assert.assertEquals(3.7877, ret.get(1), 0.0001);
        Assert.assertEquals(3.3769, ret.get(2), 0.0001);
        Assert.assertEquals(2.1985, ret.get(3), 0.0001);

        Assert.assertEquals(0.9714, ret.get(5), 0.0001);
        Assert.assertEquals(0.4512, ret.get(6), 0.0001);
        Assert.assertEquals(0.0000, ret.get(7), 0.0001);
        Assert.assertEquals(5.5772, ret.get(8), 0.0001);
    }

//    @Test public void testDan277() throws CDKException {
//
//        IMolecule molecule = null;
//        ChiPathDescriptor desc = new ChiPathDescriptor();
//        DoubleArrayResult ret = (DoubleArrayResult) desc.calculate(mol).getValue();
//
//        Assert.assertEquals(4.1069, ret.get(0), 0.0001);
//        Assert.assertEquals(3.5527, ret.get(1), 0.0001);
//        Assert.assertEquals(2.0065, ret.get(2), 0.0001);
//        Assert.assertEquals(1.3853, ret.get(3), 0.00001);
//
//        Assert.assertEquals(2.6211, ret.get(5), 0.0001);
//        Assert.assertEquals(2.3405, ret.get(6), 0.0001);
//        Assert.assertEquals(0.88578, ret.get(7), 0.00001);
//        Assert.assertEquals(0.489996, ret.get(8), 0.00001);
//    }

}
