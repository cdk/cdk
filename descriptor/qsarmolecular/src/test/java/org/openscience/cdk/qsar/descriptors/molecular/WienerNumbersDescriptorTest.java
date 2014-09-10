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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @cdk.module test-qsarmolecular
 */
public class WienerNumbersDescriptorTest extends MolecularDescriptorTest {

    public WienerNumbersDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(WienerNumbersDescriptor.class);
    }

    @Test
    public void testWienerNumbersDescriptor() throws Exception {
        double[] testResult = {18, 2};
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[H]C([H])([H])C([H])([H])C(=O)O");
        AtomContainerManipulator.removeHydrogens(mol);
        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(testResult[0], retval.get(0), 0.0001);
        Assert.assertEquals(testResult[1], retval.get(1), 0.0001);
    }

    /**
     * Test if the descriptor returns the same results with and without explicit hydrogens.
     */
    @Test
    public void testWithExplicitHydrogens() throws Exception {
        double[] testResult = {18, 2};
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[H]C([H])([H])C([H])([H])C(=O)O");
        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(testResult[0], retval.get(0), 0.0001);
        Assert.assertEquals(testResult[1], retval.get(1), 0.0001);
    }

    /**
     * Numbers extracted from {@cdk.cite Wiener1947}.
     */
    @Test
    public void testOriginalWienerPaperCompounds() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        double[] testResult = {10, 20, 35, 56, 84, 120, 165, 220, 286};
        String smiles = "CCC";
        for (int i = 0; i < testResult.length; i++) {
            smiles += "C"; // create the matching paraffin
            IAtomContainer mol = sp.parseSmiles(smiles);
            DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(mol).getValue();
            Assert.assertEquals(testResult[i], retval.get(0), 0.0001);
        }
    }
}
