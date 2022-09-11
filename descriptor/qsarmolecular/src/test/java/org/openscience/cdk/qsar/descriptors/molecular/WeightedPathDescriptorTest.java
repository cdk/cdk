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

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

class WeightedPathDescriptorTest extends MolecularDescriptorTest {

    WeightedPathDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(WeightedPathDescriptor.class);
    }

    private void assertWeights(IAtomContainer mol, double ... expected) {
        DescriptorValue value = descriptor.calculate(mol);
        DoubleArrayResult actual = (DoubleArrayResult) value.getValue();
        Assertions.assertEquals(expected[0], actual.get(0), 0.00001);
        Assertions.assertEquals(expected[1], actual.get(1), 0.00001);
        Assertions.assertEquals(expected[2], actual.get(2), 0.00001);
        Assertions.assertEquals(expected[3], actual.get(3), 0.00001);
        Assertions.assertEquals(expected[4], actual.get(4), 0.00001);
    }

    @Test
    void testButane() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC");
        assertWeights(mol, 6.871320, 1.717830, 0.0, 0.0, 0.0);
    }

    @Test
    void testPyrrole() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[nH]1cccc1");
        assertWeights(mol, 9.6875,1.9375,2.875,0.0,2.875);
    }

    @Test
    void testFuran() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("o1cccc1");
        assertWeights(mol, 9.6875,1.9375,2.875,2.875,0.0);
    }

    @Test
    void testIndole() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[nH]1ccc2c1cccc2");
        assertWeights(mol, 18.55905,2.06211,3.05042,0.0,3.05042);
    }

    @Test
    void testAbilify() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1CC(=O)NC2=C1C=CC(=C2)OCCCCN3CCN(CC3)C4=C(C(=CC=C4)Cl)Cl");
        assertWeights(mol, 61.78692,2.05956,20.71642,5.6356,10.01866);
    }

    @Test
    void testWpo() throws Exception {
        try (InputStream ins = this.getClass().getResourceAsStream("wpo.sdf");
             MDLV2000Reader reader = new MDLV2000Reader(ins)) {
            IChemFile content = reader.read(new org.openscience.cdk.ChemFile());
            List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
            IAtomContainer mol = cList.get(0);
            AtomContainerManipulator.suppressHydrogens(mol);
            assertWeights(mol, 18.42026, 1.842026, 13.45733, 13.45733, 0);
        }
    }

    @Test
    void testWPN() throws Exception {
        try (InputStream ins = this.getClass().getResourceAsStream("wpn.sdf");
             MDLV2000Reader reader = new MDLV2000Reader(ins)) {
            IChemFile content = reader.read(new org.openscience.cdk.ChemFile());
            List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
            IAtomContainer mol = cList.get(0);
            AtomContainerManipulator.suppressHydrogens(mol);
            assertWeights(mol, 26.14843, 1.867746, 19.02049, 0, 19.02049);
        }
    }
}
