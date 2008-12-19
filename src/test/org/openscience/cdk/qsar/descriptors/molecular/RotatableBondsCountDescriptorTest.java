/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.nonotify.NNAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class RotatableBondsCountDescriptorTest extends MolecularDescriptorTest {

    public RotatableBondsCountDescriptorTest() {
    }

    @Before
    public void setUp() throws Exception {
    	setDescriptor(RotatableBondsCountDescriptor.class);
    }

    @Test
    public void testRotatableBondsCount() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC2CCC(C1CCCCC1)CC2"); // molecule with 2 bridged cicloexane and 1 methyl
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    private IAtomContainer makeEthane() {
        IAtomContainer container = new NNAtomContainer();
        container.addAtom(container.getBuilder().newAtom(Elements.CARBON));
        container.addAtom(container.getBuilder().newAtom(Elements.CARBON));
        container.addBond(0, 1, IBond.Order.SINGLE);
        return container;
    }

    private IAtomContainer makeButane() {
        IAtomContainer container = makeEthane();
        container.addAtom(container.getBuilder().newAtom(Elements.CARBON));
        container.addAtom(container.getBuilder().newAtom(Elements.CARBON));
        container.addBond(1, 2, IBond.Order.SINGLE);
        container.addBond(2, 3, IBond.Order.SINGLE);
        return container;
    }

    @Test public void testEthaneIncludeTerminals() throws Exception {
        IAtomContainer container = makeEthane();
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{Boolean.TRUE});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(1, ((IntegerResult)result.getValue()).intValue());
    }

    @Test public void testEthane() throws Exception {
        IAtomContainer container = makeEthane();
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{Boolean.FALSE});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(0, ((IntegerResult)result.getValue()).intValue());
    }

    @Test public void testButaneIncludeTerminals() throws Exception {
        IAtomContainer container = makeButane();
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{Boolean.TRUE});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(3, ((IntegerResult)result.getValue()).intValue());
    }

    @Test public void testButane() throws Exception {
        IAtomContainer container = makeButane();
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{Boolean.FALSE});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(1, ((IntegerResult)result.getValue()).intValue());
    }
}

