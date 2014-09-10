/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar.descriptors.molecular;

import javax.vecmath.Point2d;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @cdk.module test-qsarmolecular
 */
public class AcidicGroupCountDescriptorTest extends MolecularDescriptorTest {

    @Before
    public void setUp() throws Exception {
        setDescriptor(AcidicGroupCountDescriptor.class);
    }

    @Test
    public void testConstructor() throws Exception {
        Assert.assertNotNull(new AcidicGroupCountDescriptor());
    }

    @Test(expected = IllegalStateException.class)
    public void uninitalisedError() {
        new BasicGroupCountDescriptor().calculate(new AtomContainer());
    }

    @Test
    public void testOneAcidGroup() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC(=O)O");
        IntegerResult result = (IntegerResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(1, result.intValue());
    }

    @Test
    public void testSulphurAcidGroup() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("OS(=O)(=O)O");
        IntegerResult result = (IntegerResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(2, result.intValue());
    }

    @Test
    public void testPhosphorusAcidGroup() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=P(=O)O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        IntegerResult result = (IntegerResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(1, result.intValue());
    }

    @Test
    public void testFancyGroup() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[NH](S(=O)=O)C(F)(F)F");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        IntegerResult result = (IntegerResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(1, result.intValue());
    }

    @Test
    public void testNitroRing() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[nH]1nnnc1");
        IntegerResult result = (IntegerResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(2, result.intValue());
    }

    /**
     * @cdk.inchi InChI=1S/C2H2N4O2/c7-2(8)1-3-5-6-4-1/h(H,7,8)(H,3,4,5,6)
     */
    @Test
    public void testTwoGroup() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "O");
        a1.setFormalCharge(0);
        a1.setPoint2d(new Point2d(5.9019, 0.5282));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "O");
        a2.setFormalCharge(0);
        a2.setPoint2d(new Point2d(5.3667, -1.1191));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "N");
        a3.setFormalCharge(0);
        a3.setPoint2d(new Point2d(3.3987, -0.4197));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "N");
        a4.setFormalCharge(0);
        a4.setPoint2d(new Point2d(2.5896, 0.1681));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "N");
        a5.setFormalCharge(0);
        a5.setPoint2d(new Point2d(3.8987, 1.1191));
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "N");
        a6.setFormalCharge(0);
        a6.setPoint2d(new Point2d(2.8987, 1.1191));
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        a7.setPoint2d(new Point2d(4.2077, 0.1681));
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "C");
        a8.setFormalCharge(0);
        a8.setPoint2d(new Point2d(5.1588, -0.141));
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "H");
        a9.setFormalCharge(0);
        a9.setPoint2d(new Point2d(2.0, -0.0235));
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "H");
        a10.setFormalCharge(0);
        a10.setPoint2d(new Point2d(6.4916, 0.3366));
        mol.addAtom(a10);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a8, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a10, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a2, a8, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a3, a7, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a4, a6, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a4, a9, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a5, a6, IBond.Order.DOUBLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a5, a7, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a7, a8, IBond.Order.SINGLE);
        mol.addBond(b10);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);

        IntegerResult result = (IntegerResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(3, result.intValue());
    }

    /**
     * @cdk.inchi InChI=1S/C6H12O10S/c7-2(1-16-17(13,14)15)3(8)4(9)5(10)6(11)12/h2-5,7-10H,1H2,(H,11,12)(H,13,14,15)/t2-,3-,4+,5-/m1/s1
     */
    @Test
    public void testCID() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "S");
        a1.setFormalCharge(0);
        a1.setPoint2d(new Point2d(9.4651, 0.25));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "O");
        a2.setFormalCharge(0);
        a2.setPoint2d(new Point2d(6.001, 1.25));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "O");
        a3.setFormalCharge(0);
        a3.setPoint2d(new Point2d(5.135, -1.25));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "O");
        a4.setFormalCharge(0);
        a4.setPoint2d(new Point2d(6.8671, -1.25));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "O");
        a5.setFormalCharge(0);
        a5.setPoint2d(new Point2d(8.5991, -0.25));
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "O");
        a6.setFormalCharge(0);
        a6.setPoint2d(new Point2d(4.269, 1.25));
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "O");
        a7.setFormalCharge(0);
        a7.setPoint2d(new Point2d(2.5369, 0.25));
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "O");
        a8.setFormalCharge(0);
        a8.setPoint2d(new Point2d(3.403, -1.25));
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "O");
        a9.setFormalCharge(0);
        a9.setPoint2d(new Point2d(10.3312, 0.75));
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "O");
        a10.setFormalCharge(0);
        a10.setPoint2d(new Point2d(9.9651, -0.616));
        mol.addAtom(a10);
        IAtom a11 = mol.getBuilder().newInstance(IAtom.class, "O");
        a11.setFormalCharge(0);
        a11.setPoint2d(new Point2d(8.9651, 1.116));
        mol.addAtom(a11);
        IAtom a12 = mol.getBuilder().newInstance(IAtom.class, "C");
        a12.setFormalCharge(0);
        a12.setPoint2d(new Point2d(6.001, 0.25));
        mol.addAtom(a12);
        IAtom a13 = mol.getBuilder().newInstance(IAtom.class, "C");
        a13.setFormalCharge(0);
        a13.setPoint2d(new Point2d(5.135, -0.25));
        mol.addAtom(a13);
        IAtom a14 = mol.getBuilder().newInstance(IAtom.class, "C");
        a14.setFormalCharge(0);
        a14.setPoint2d(new Point2d(6.8671, -0.25));
        mol.addAtom(a14);
        IAtom a15 = mol.getBuilder().newInstance(IAtom.class, "C");
        a15.setFormalCharge(0);
        a15.setPoint2d(new Point2d(4.269, 0.25));
        mol.addAtom(a15);
        IAtom a16 = mol.getBuilder().newInstance(IAtom.class, "C");
        a16.setFormalCharge(0);
        a16.setPoint2d(new Point2d(7.7331, 0.25));
        mol.addAtom(a16);
        IAtom a17 = mol.getBuilder().newInstance(IAtom.class, "C");
        a17.setFormalCharge(0);
        a17.setPoint2d(new Point2d(3.403, -0.25));
        mol.addAtom(a17);
        IAtom a18 = mol.getBuilder().newInstance(IAtom.class, "H");
        a18.setFormalCharge(0);
        a18.setPoint2d(new Point2d(6.538, 0.56));
        mol.addAtom(a18);
        IAtom a19 = mol.getBuilder().newInstance(IAtom.class, "H");
        a19.setFormalCharge(0);
        a19.setPoint2d(new Point2d(5.672, -0.56));
        mol.addAtom(a19);
        IAtom a20 = mol.getBuilder().newInstance(IAtom.class, "H");
        a20.setFormalCharge(0);
        a20.setPoint2d(new Point2d(6.3301, -0.56));
        mol.addAtom(a20);
        IAtom a21 = mol.getBuilder().newInstance(IAtom.class, "H");
        a21.setFormalCharge(0);
        a21.setPoint2d(new Point2d(4.8059, 0.56));
        mol.addAtom(a21);
        IAtom a22 = mol.getBuilder().newInstance(IAtom.class, "H");
        a22.setFormalCharge(0);
        a22.setPoint2d(new Point2d(8.1316, 0.7249));
        mol.addAtom(a22);
        IAtom a23 = mol.getBuilder().newInstance(IAtom.class, "H");
        a23.setFormalCharge(0);
        a23.setPoint2d(new Point2d(7.3346, 0.7249));
        mol.addAtom(a23);
        IAtom a24 = mol.getBuilder().newInstance(IAtom.class, "H");
        a24.setFormalCharge(0);
        a24.setPoint2d(new Point2d(6.538, 1.56));
        mol.addAtom(a24);
        IAtom a25 = mol.getBuilder().newInstance(IAtom.class, "H");
        a25.setFormalCharge(0);
        a25.setPoint2d(new Point2d(4.5981, -1.56));
        mol.addAtom(a25);
        IAtom a26 = mol.getBuilder().newInstance(IAtom.class, "H");
        a26.setFormalCharge(0);
        a26.setPoint2d(new Point2d(7.404, -1.56));
        mol.addAtom(a26);
        IAtom a27 = mol.getBuilder().newInstance(IAtom.class, "H");
        a27.setFormalCharge(0);
        a27.setPoint2d(new Point2d(3.732, 1.56));
        mol.addAtom(a27);
        IAtom a28 = mol.getBuilder().newInstance(IAtom.class, "H");
        a28.setFormalCharge(0);
        a28.setPoint2d(new Point2d(2.0, -0.06));
        mol.addAtom(a28);
        IAtom a29 = mol.getBuilder().newInstance(IAtom.class, "H");
        a29.setFormalCharge(0);
        a29.setPoint2d(new Point2d(10.8681, 0.44));
        mol.addAtom(a29);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a9, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a1, a10, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a1, a11, IBond.Order.DOUBLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a2, a12, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a2, a24, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a3, a13, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a3, a25, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a4, a14, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a4, a26, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a5, a16, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = mol.getBuilder().newInstance(IBond.class, a6, a15, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = mol.getBuilder().newInstance(IBond.class, a6, a27, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = mol.getBuilder().newInstance(IBond.class, a7, a17, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = mol.getBuilder().newInstance(IBond.class, a7, a28, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = mol.getBuilder().newInstance(IBond.class, a8, a17, IBond.Order.DOUBLE);
        mol.addBond(b16);
        IBond b17 = mol.getBuilder().newInstance(IBond.class, a9, a29, IBond.Order.SINGLE);
        mol.addBond(b17);
        IBond b18 = mol.getBuilder().newInstance(IBond.class, a12, a13, IBond.Order.SINGLE);
        mol.addBond(b18);
        IBond b19 = mol.getBuilder().newInstance(IBond.class, a12, a14, IBond.Order.SINGLE);
        mol.addBond(b19);
        IBond b20 = mol.getBuilder().newInstance(IBond.class, a12, a18, IBond.Order.SINGLE);
        mol.addBond(b20);
        IBond b21 = mol.getBuilder().newInstance(IBond.class, a13, a15, IBond.Order.SINGLE);
        mol.addBond(b21);
        IBond b22 = mol.getBuilder().newInstance(IBond.class, a13, a19, IBond.Order.SINGLE);
        mol.addBond(b22);
        IBond b23 = mol.getBuilder().newInstance(IBond.class, a14, a16, IBond.Order.SINGLE);
        mol.addBond(b23);
        IBond b24 = mol.getBuilder().newInstance(IBond.class, a14, a20, IBond.Order.SINGLE);
        mol.addBond(b24);
        IBond b25 = mol.getBuilder().newInstance(IBond.class, a15, a17, IBond.Order.SINGLE);
        mol.addBond(b25);
        IBond b26 = mol.getBuilder().newInstance(IBond.class, a15, a21, IBond.Order.SINGLE);
        mol.addBond(b26);
        IBond b27 = mol.getBuilder().newInstance(IBond.class, a16, a22, IBond.Order.SINGLE);
        mol.addBond(b27);
        IBond b28 = mol.getBuilder().newInstance(IBond.class, a16, a23, IBond.Order.SINGLE);
        mol.addBond(b28);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        IntegerResult result = (IntegerResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(2, result.intValue());
    }
}
