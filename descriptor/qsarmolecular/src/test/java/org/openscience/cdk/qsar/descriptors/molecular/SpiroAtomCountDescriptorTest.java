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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs a test for the SpiroAtomCountDescriptor.
 *
 * @cdk.module test-qsarmolecular
 */

public class SpiroAtomCountDescriptorTest extends MolecularDescriptorTest {

    public SpiroAtomCountDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(SpiroAtomCountDescriptor.class);
    }

    @Test
    public void testDecalin() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1CCC2CCCCC2C1"); // ethanol
        DescriptorValue value = descriptor.calculate(mol);
        Assert.assertEquals(0, ((IntegerResult) value.getValue()).intValue());
        Assert.assertEquals(1, value.getNames().length);
        Assert.assertEquals("nSpiroAtoms", value.getNames()[0]);
        Assert.assertEquals(descriptor.getDescriptorNames()[0], value.getNames()[0]);
    }

    @Test
    public void testNorbornane() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1CC2CCC1C2"); // ethanol
        DescriptorValue value = descriptor.calculate(mol);
        Assert.assertEquals(0, ((IntegerResult) value.getValue()).intValue());
    }

    @Test
    public void testSpiroUndecane() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1CCC2(CC1)CCCCC2"); // ethanol
        DescriptorValue value = descriptor.calculate(mol);
        Assert.assertEquals(1, ((IntegerResult) value.getValue()).intValue());
    }

    @Test
    public void testDiSpiroPentane() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC1C[C]11(CC1)[C]123CC1.C2C3"); // ethanol
        DescriptorValue value = descriptor.calculate(mol);
        Assert.assertEquals(2, ((IntegerResult) value.getValue()).intValue());
    }

}
