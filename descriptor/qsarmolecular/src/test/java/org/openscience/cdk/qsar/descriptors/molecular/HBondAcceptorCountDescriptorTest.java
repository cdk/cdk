/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Point3d;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class HBondAcceptorCountDescriptorTest extends MolecularDescriptorTest {

    public HBondAcceptorCountDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(HBondAcceptorCountDescriptor.class);
    }

    @Test
    public void testHBondAcceptorCountDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        // original molecule O=N(=O)c1cccc2cn[nH]c12 - correct kekulisation will give
        // the same result. this test though should depend on kekulisation working
        IAtomContainer mol = sp.parseSmiles("O=N(=O)C1=C2NN=CC2=CC=C1");
        Assert.assertEquals(1, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    /**
     * @cdk.bug   3133610
     * @cdk.inchi InChI=1S/C2H3N3/c1-3-2-5-4-1/h1-2H,(H,3,4,5)
     */
    @Test
    public void testCID9257() throws CDKException {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "N");
        a1.setFormalCharge(0);
        a1.setPoint3d(new Point3d(0.5509, 0.9639, 0.0));
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "N");
        a2.setFormalCharge(0);
        a2.setPoint3d(new Point3d(1.1852, -0.2183, 1.0E-4));
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "N");
        a3.setFormalCharge(0);
        a3.setPoint3d(new Point3d(-1.087, -0.4827, 2.0E-4));
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        a4.setPoint3d(new Point3d(-0.7991, 0.7981, -1.0E-4));
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        a5.setPoint3d(new Point3d(0.15, -1.0609, -2.0E-4));
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "H");
        a6.setFormalCharge(0);
        a6.setPoint3d(new Point3d(1.094, 1.8191, 1.0E-4));
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "H");
        a7.setFormalCharge(0);
        a7.setPoint3d(new Point3d(-1.4981, 1.6215, -2.0E-4));
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "H");
        a8.setFormalCharge(0);
        a8.setPoint3d(new Point3d(0.3019, -2.13, -2.0E-4));
        mol.addAtom(a8);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a2, a5, IBond.Order.DOUBLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a3, a4, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a3, a5, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a4, a7, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a5, a8, IBond.Order.SINGLE);
        mol.addBond(b8);

        Object[] params = {new Boolean(true)};
        descriptor.setParameters(params);
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    /**
     * @see <a href="https://github.com/cdk/cdk/issues/495">Issue 495</a>
     */
    @Test
    public void exocyclicOxygenInAromaticRing() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer m = sp.parseSmiles("Cn1c2nc([nH]c2c(=O)n(c1=O)C)C1CCCC1");

        HBondAcceptorCountDescriptor hbond_acceptor_desc = new HBondAcceptorCountDescriptor();
        int actual = ((IntegerResult)hbond_acceptor_desc.calculate(m).getValue()).intValue();
        org.hamcrest.MatcherAssert.assertThat(actual, CoreMatchers.is(3));
    }
}
