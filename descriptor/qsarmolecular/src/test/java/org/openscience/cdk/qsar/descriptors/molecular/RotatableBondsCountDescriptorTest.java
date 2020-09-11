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
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.Matchers.is;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class RotatableBondsCountDescriptorTest extends MolecularDescriptorTest {

    public RotatableBondsCountDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(RotatableBondsCountDescriptor.class);
    }

    @Test
    public void testRotatableBondsCount() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {true, false};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC2CCC(C1CCCCC1)CC2"); // molecule with 2 bridged cicloexane and 1 methyl
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    private IAtomContainer makeEthane() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, Elements.CARBON));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, Elements.CARBON));
        container.addBond(0, 1, IBond.Order.SINGLE);
        return container;
    }

    private IAtomContainer makeButane() {
        IAtomContainer container = makeEthane();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, Elements.CARBON));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, Elements.CARBON));
        container.addBond(1, 2, IBond.Order.SINGLE);
        container.addBond(2, 3, IBond.Order.SINGLE);
        return container;
    }

    @Test
    public void testEthaneIncludeTerminals() throws Exception {
        IAtomContainer container = makeEthane();
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{true, false});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(1, ((IntegerResult) result.getValue()).intValue());
    }

    @Test
    public void testEthane() throws Exception {
        IAtomContainer container = makeEthane();
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{false, false});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(0, ((IntegerResult) result.getValue()).intValue());
    }

    @Test
    public void testButaneIncludeTerminals() throws Exception {
        IAtomContainer container = makeButane();
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{true, false});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(3, ((IntegerResult) result.getValue()).intValue());
    }

    @Test
    public void testButane() throws Exception {
        IAtomContainer container = makeButane();
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{false, false});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(1, ((IntegerResult) result.getValue()).intValue());
    }

    /**
     * @cdk.bug 2449257
     */
    @Test
    public void testEthaneIncludeTerminalsExplicitH() throws Exception {
        IAtomContainer container = makeEthane();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(container.getBuilder());
        adder.addImplicitHydrogens(container);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(container);
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{true, false});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(1, ((IntegerResult) result.getValue()).intValue());
    }

    /**
     * @cdk.bug 2449257
     */
    @Test
    public void testEthaneExplicitH() throws Exception {
        IAtomContainer container = makeEthane();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(container.getBuilder());
        adder.addImplicitHydrogens(container);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(container);
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{false, false});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(0, ((IntegerResult) result.getValue()).intValue());
    }

    /**
     * @cdk.bug 2449257
     */
    @Test
    public void testButaneIncludeTerminalsExplicitH() throws Exception {
        IAtomContainer container = makeButane();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(container.getBuilder());
        adder.addImplicitHydrogens(container);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(container);
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{true, false});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(3, ((IntegerResult) result.getValue()).intValue());
    }

    /**
     * @cdk.bug 2449257
     */
    @Test
    public void testButaneExplicitH() throws Exception {
        IAtomContainer container = makeButane();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(container.getBuilder());
        adder.addImplicitHydrogens(container);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(container);
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{false, false});
        DescriptorValue result = descriptor.calculate(container);
        Assert.assertEquals(1, ((IntegerResult) result.getValue()).intValue());
    }

    @Test
    public void testAmideIncluded() throws Exception {
        String amide = "CCNC(=O)CC(C)C"; // N-ethyl-3-methylbutanamide
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(amide);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{false, false});
        DescriptorValue result = descriptor.calculate(mol);
        org.hamcrest.MatcherAssert.assertThat(((IntegerResult) result.getValue()).intValue(), is(4));
    }

    @Test
    public void testAmideExcluded() throws Exception {
        String amide = "CCNC(=O)CC(C)C"; // N-ethyl-3-methylbutanamide
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(amide);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addExplicitHydrogens(mol);
        IMolecularDescriptor descriptor = new RotatableBondsCountDescriptor();
        descriptor.setParameters(new Object[]{false, true});
        DescriptorValue result = descriptor.calculate(mol);
        org.hamcrest.MatcherAssert.assertThat(((IntegerResult) result.getValue()).intValue(), is(3));
    }
}
