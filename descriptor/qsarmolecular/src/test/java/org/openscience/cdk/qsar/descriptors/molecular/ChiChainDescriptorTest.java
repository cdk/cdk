package org.openscience.cdk.qsar.descriptors.molecular;

import javax.vecmath.Point2d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class ChiChainDescriptorTest extends MolecularDescriptorTest {

    ChiChainDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(ChiChainDescriptor.class);
    }

    @Test
    void testDan64() throws Exception {
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

        Assertions.assertEquals(0.2887, ret.get(0), 0.0001);
        Assertions.assertEquals(0.2887, ret.get(1), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(2), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(3), 0.0001);
        Assertions.assertEquals(0.1667, ret.get(5), 0.0001);
        Assertions.assertEquals(0.1667, ret.get(6), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(7), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(8), 0.0001);
    }

    @Test
    void testDan80() throws Exception {
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

        Assertions.assertEquals(0.0000, ret.get(0), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(1), 0.0001);
        Assertions.assertEquals(0.1768, ret.get(2), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(3), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(5), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(6), 0.0001);
        Assertions.assertEquals(0.04536, ret.get(7), 0.00001);
        Assertions.assertEquals(0.0000, ret.get(8), 0.0001);

    }

    @Test
    void testDan81() throws Exception {
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

        Assertions.assertEquals(0.0000, ret.get(0), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(1), 0.0001);
        Assertions.assertEquals(0.1768, ret.get(2), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(3), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(5), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(6), 0.0001);
        Assertions.assertEquals(0.1361, ret.get(7), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(8), 0.0001);
    }

    @Test
    void testDan82() throws Exception {

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

        Assertions.assertEquals(0.0000, ret.get(0), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(1), 0.0001);
        Assertions.assertEquals(0.1768, ret.get(2), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(3), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(5), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(6), 0.0001);
        Assertions.assertEquals(0.06804, ret.get(7), 0.00001);
        Assertions.assertEquals(0.0000, ret.get(8), 0.0001);
    }

    @Test
    void testDan154() throws Exception {

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

        Assertions.assertEquals(0.0000, ret.get(0), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(1), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(2), 0.0001);
        Assertions.assertEquals(0.08333, ret.get(3), 0.00001);
        Assertions.assertEquals(0.0000, ret.get(5), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(6), 0.0001);
        Assertions.assertEquals(0.0000, ret.get(7), 0.0001);
        Assertions.assertEquals(0.02778, ret.get(8), 0.00001);
    }

    /**
     * @cdk.bug 3023326
     */
    @Test
    void testCovalentMetal() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC[Sn](CCCC)(CCCC)c1cc(Cl)c(Nc2nc(C)nc(N(CCC)CC3CC3)c2Cl)c(Cl)c1");
        DoubleArrayResult ret = (DoubleArrayResult) descriptor.calculate(mol).getValue();
        Assertions.assertNotNull(ret);
    }

    /**
     * @cdk.bug 3023326
     */
    @Test
    void testCovalentPlatinum() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC1CN[Pt]2(N1)OC(=O)C(C)P(=O)(O)O2");
        Assertions.assertThrows(NullPointerException.class, () -> {
            descriptor.calculate(mol).getValue();
        });
    }

    //    @Test public void testDan277() throws CDKException {
    //
    //        IAtomContainer mol = null;
    //
    //        ChiChainDescriptor desc = new ChiChainDescriptor();
    //        DoubleArrayResult ret = (DoubleArrayResult) desc.calculate(mol).getValue();
    //
    //        Assert.assertEquals(0.0000, ret.get(0), 0.0001);
    //        Assert.assertEquals(0.0000, ret.get(1), 0.0001);
    //        Assert.assertEquals(0.0000, ret.get(2), 0.0001);
    //        Assert.assertEquals(0.08333, ret.get(3), 0.00001);
    //        Assert.assertEquals(0.0000, ret.get(4), 0.0001);
    //        Assert.assertEquals(0.0000, ret.get(5), 0.0001);
    //        Assert.assertEquals(0.0000, ret.get(6), 0.0001);
    //        Assert.assertEquals(0.02778, ret.get(7), 0.00001);
    //    }

}
