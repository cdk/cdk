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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class BondCountDescriptorTest extends MolecularDescriptorTest {

    private static final SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

    public BondCountDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(BondCountDescriptor.class);
    }

    @Test
    public void testBondCountDescriptor() throws Exception {
        Assert.assertNotNull(descriptor);
    }

    @Test
    public void testSingleBondCount() throws ClassNotFoundException, CDKException, java.lang.Exception {
        descriptor.setParameters(new String[]{"s"});
        IAtomContainer mol = sp.parseSmiles("CCO"); // ethanol
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("C=C=C");
        Assert.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void testDoubleBondCount() throws ClassNotFoundException, CDKException, java.lang.Exception {
        descriptor.setParameters(new String[]{"d"});
        IAtomContainer mol = sp.parseSmiles("CCO"); // ethanol
        Assert.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("C=C=C");
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    /**
     * The default setting should be to count *all* bonds.
     *
     *
     * @cdk.bug 1651263
     */
    @Test
    public void testDefaultSetting() throws Exception {
        IMolecularDescriptor descriptor = new BondCountDescriptor();
        IAtomContainer mol = sp.parseSmiles("CCO"); // ethanol
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("C=C=C");
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("CC=O");
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("CC#N");
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }
}
