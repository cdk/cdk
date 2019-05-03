/* Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
public class ProtonAffinityHOSEDescriptorTest extends AtomicDescriptorTest {

    ProtonAffinityHOSEDescriptor            descriptor;
    LonePairElectronChecker                 lpcheck = new LonePairElectronChecker();
    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  Constructor for the ProtonAffinityHOSEDescriptorTest object
     *
     */
    public ProtonAffinityHOSEDescriptorTest() {
        descriptor = new ProtonAffinityHOSEDescriptor();
    }

    @Before
    public void setUp() throws Exception {
        setDescriptor(ProtonAffinityHOSEDescriptor.class);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testProtonAffinityHOSEDescriptor() throws Exception {
        IAtomicDescriptor descriptor = new ProtonAffinityHOSEDescriptor();
        Assert.assertNotNull(descriptor);
    }

    /**
     *  A unit test for JUnit with
     *
     *  @cdk.inchi InChI=1/C6H5Cl/c7-6-4-2-1-3-5-6/h1-5H
     */
    @Test
    public void testAffinityDescriptor1() throws Exception {

        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(2, 3, IBond.Order.DOUBLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(4, 5, IBond.Order.DOUBLE);
        mol.addBond(5, 0, IBond.Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "Cl"));
        mol.addBond(0, 6, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        addExplicitHydrogens(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getAtom(6), mol).getValue()).doubleValue();
        double resultAccordingNIST = 753.1;

        Assert.assertEquals(resultAccordingNIST, result, 0.00001);
    }

    /**
     *  A unit test for JUnit with
     *
     *  @cdk.inchi InChI=1/C2H5Cl/c1-2-3/h2H2,1H3
     */
    @Test
    public void testAffinityDescriptor2() throws Exception {

        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "Cl"));
        mol.addBond(1, 2, IBond.Order.SINGLE);

        addExplicitHydrogens(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        lpcheck.saturate(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getAtom(2), mol).getValue()).doubleValue();
        double resultAccordingNIST = 693.4;

        Assert.assertEquals(resultAccordingNIST, result, 0.00001);
    }

}
