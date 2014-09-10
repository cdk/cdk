package org.openscience.cdk.qsar.descriptors.molecular;

import javax.vecmath.Point2d;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class ChiPathDescriptorTest extends MolecularDescriptorTest {

    public ChiPathDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(ChiPathDescriptor.class);
    }

    @Test
    public void testDan64() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        a1.setPoint2d(new Point2d(0.7500000000000004, 2.799038105676658));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        a2.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        a3.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "O");
        a4.setPoint2d(new Point2d(-1.2990381056766582, 0.7500000000000001));
        mol.addAtom(a4);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a4, a2, IBond.Order.SINGLE);
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

    @Test
    public void testDan80() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        a1.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        a2.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        a3.setPoint2d(new Point2d(-1.4265847744427305, -0.46352549156242084));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        a4.setPoint2d(new Point2d(-2.3082626528814396, 0.7500000000000002));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "O");
        a5.setPoint2d(new Point2d(-1.42658477444273, 1.9635254915624212));
        mol.addAtom(a5);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a5, a1, IBond.Order.SINGLE);
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

    @Test
    public void testDan81() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        a1.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        a2.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        a3.setPoint2d(new Point2d(-1.4265847744427305, -0.46352549156242084));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        a4.setPoint2d(new Point2d(-2.3082626528814396, 0.7500000000000002));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "S");
        a5.setPoint2d(new Point2d(-1.42658477444273, 1.9635254915624212));
        mol.addAtom(a5);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a5, a1, IBond.Order.SINGLE);
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

    @Test
    public void testDan82() throws Exception {

        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        a1.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        a2.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        a3.setPoint2d(new Point2d(-1.4265847744427305, -0.46352549156242084));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        a4.setPoint2d(new Point2d(-2.3082626528814396, 0.7500000000000002));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "O");
        a5.setPoint2d(new Point2d(-1.42658477444273, 1.9635254915624212));
        mol.addAtom(a5);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a5, a1, IBond.Order.SINGLE);
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

    @Test
    public void testDan154() throws Exception {

        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        a1.setPoint2d(new Point2d(0.0, 1.5));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        a2.setPoint2d(new Point2d(0.0, 0.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        a3.setPoint2d(new Point2d(-1.2990381056766584, -0.7500000000000001));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        a4.setPoint2d(new Point2d(-2.598076211353316, -2.220446049250313E-16));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "C");
        a5.setPoint2d(new Point2d(-2.5980762113533165, 1.5));
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "C");
        a6.setPoint2d(new Point2d(-1.2990381056766582, 2.2500000000000004));
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "Cl");
        a7.setPoint2d(new Point2d(-1.2990381056766582, 3.7500000000000004));
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "Cl");
        a8.setPoint2d(new Point2d(1.2990381056766576, -0.7500000000000007));
        mol.addAtom(a8);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a6, a5, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a6, a1, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a7, a6, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a8, a2, IBond.Order.SINGLE);
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

    /**
     * @cdk.bug 3023326
     */
    @Test
    public void testCovalentMetal() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC[Sn](CCCC)(CCCC)c1cc(Cl)c(Nc2nc(C)nc(N(CCC)CC3CC3)c2Cl)c(Cl)c1");
        DoubleArrayResult ret = (DoubleArrayResult) descriptor.calculate(mol).getValue();
        Assert.assertNotNull(ret);
    }

    /**
     * @cdk.bug 3023326
     */
    @Test(expected = NullPointerException.class)
    public void testCovalentPlatinum() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC1CN[Pt]2(N1)OC(=O)C(C)P(=O)(O)O2");
        descriptor.calculate(mol).getValue();
    }

    //    @Test public void testDan277() throws CDKException {
    //
    //        IAtomContainer molecule = null;
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
