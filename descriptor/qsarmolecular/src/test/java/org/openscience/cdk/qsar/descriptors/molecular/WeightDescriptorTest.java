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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs a test for the AtomCountDescriptor.
 *
 * @cdk.module test-qsarmolecular
 */
public class WeightDescriptorTest extends MolecularDescriptorTest {

    public WeightDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(WeightDescriptor.class);
    }

    @Test
    public void testWeightDescriptor() throws Exception {
        Object[] params = {"*"};
        descriptor.setParameters(params);
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = smilesParser.parseSmiles("CCC");
        Assert.assertEquals(44.095, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.001);
    }

    /**
     * @cdk.bug 2185475
     */
    @Test
    public void testNoHydrogens() throws Exception {
        Object[] params = {"*"};
        descriptor.setParameters(params);
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        Assert.assertEquals(12.0107, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.0001);
    }

    /**
     * @cdk.bug 2185475
     */
    @Test
    public void testExplicitHydrogens() throws Exception {
        Object[] params = {"*"};
        descriptor.setParameters(params);
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "H"));
        mol.addAtom(builder.newInstance(IAtom.class, "H"));
        mol.addAtom(builder.newInstance(IAtom.class, "H"));
        mol.addAtom(builder.newInstance(IAtom.class, "H"));
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);
        mol.addBond(0, 3, Order.SINGLE);
        mol.addBond(0, 4, Order.SINGLE);
        Assert.assertEquals(16.042, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.001);
    }

    /**
     * @cdk.bug 2185475
     */
    @Test
    public void testImplicitHydrogens() throws Exception {
        Object[] params = {"*"};
        descriptor.setParameters(params);
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.getAtom(0).setImplicitHydrogenCount(4);
        Assert.assertEquals(16.042, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.001);
    }

}
