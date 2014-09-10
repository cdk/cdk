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

public class ChiPathClusterDescriptorTest extends MolecularDescriptorTest {

    public ChiPathClusterDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(ChiPathClusterDescriptor.class);
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

        Assert.assertEquals(0.0000, ret.get(0), 0.0001);
        Assert.assertEquals(0.0000, ret.get(1), 0.0001);
        Assert.assertEquals(0.0000, ret.get(2), 0.0001);
        Assert.assertEquals(0.0000, ret.get(3), 0.0001);
        Assert.assertEquals(0.0000, ret.get(4), 0.0001);
        Assert.assertEquals(0.0000, ret.get(5), 0.0001);
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

        Assert.assertEquals(0.7416, ret.get(0), 0.0001);
        Assert.assertEquals(1.0934, ret.get(1), 0.0001);
        Assert.assertEquals(1.0202, ret.get(2), 0.0001);
        Assert.assertEquals(0.4072, ret.get(3), 0.0001);
        Assert.assertEquals(0.5585, ret.get(4), 0.0001);
        Assert.assertEquals(0.4376, ret.get(5), 0.0001);
    }

    @Test
    public void testDan248() throws Exception {

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
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "C");
        a7.setPoint2d(new Point2d(-3.897114317029975, 2.249999999999999));
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "O");
        a8.setPoint2d(new Point2d(-1.2990381056766587, -2.25));
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "C");
        a9.setPoint2d(new Point2d(1.477211629518312, 1.2395277334996044));
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "C");
        a10.setPoint2d(new Point2d(0.5130302149885025, 2.909538931178863));
        mol.addAtom(a10);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a4, IBond.Order.DOUBLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a6, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a6, a1, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a7, a5, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a8, a3, IBond.Order.DOUBLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a9, a1, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a10, a1, IBond.Order.SINGLE);
        mol.addBond(b10);

        DoubleArrayResult ret = (DoubleArrayResult) descriptor.calculate(mol).getValue();

        Assert.assertEquals(1.6076, ret.get(0), 0.0001);
        Assert.assertEquals(3.6550, ret.get(1), 0.0001);

        Assert.assertEquals(3.2503, ret.get(2), 0.0001); // 3.3337 ?
        Assert.assertEquals(1.1410, ret.get(3), 0.0001);
        Assert.assertEquals(2.1147, ret.get(4), 0.0001);
        Assert.assertEquals(1.6522, ret.get(5), 0.0001); // 1.7148 ?
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

}
