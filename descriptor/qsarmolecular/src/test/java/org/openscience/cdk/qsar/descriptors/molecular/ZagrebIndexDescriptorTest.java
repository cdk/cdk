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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class ZagrebIndexDescriptorTest extends MolecularDescriptorTest {

    public ZagrebIndexDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(ZagrebIndexDescriptor.class);
    }

    @Test
    public void testZagrebIndexDescriptor() throws java.lang.Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C(O)CC");
        Assert.assertEquals(16, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.0001);
    }

    @Test
    public void test2Dvs3D() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O1C2C34C(C(C1O)CCCc1cc(cc(c1)C(F)(F)F)C(F)(F)F)CCC(C3CCC(O2)(OO4)C)C");

        addExplicitHydrogens(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        double value2D = ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue();

        String filename = "data/mdl/cpsa-uncharged.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        mol = (IAtomContainer) cList.get(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        double value3D = ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue();

        Assert.assertEquals(value2D, value3D, 0.001);

    }
}
