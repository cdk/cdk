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
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class TPSADescriptorTest extends MolecularDescriptorTest {

    private SmilesParser sp;

    public TPSADescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        setDescriptor(TPSADescriptor.class);
        Object[] params = {true};
        descriptor.setParameters(params);
    }

    @Test
    public void testTPSA1() throws Exception {
        IAtomContainer mol = sp.parseSmiles("O=C(O)CC");
        addExplicitHydrogens(mol);
        Assert.assertEquals(37.29, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    @Test
    public void testTPSA2() throws Exception {
        IAtomContainer mol = sp.parseSmiles("C=NC(CC#N)N(C)C");
        addExplicitHydrogens(mol);
        Assert.assertEquals(39.39, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    @Test
    public void testTPSA3() throws Exception {
        IAtomContainer mol = sp.parseSmiles("CCCN(=O)=O");
        addExplicitHydrogens(mol);
        Assert.assertEquals(45.82, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    @Test
    public void testTPSA4() throws Exception {
        IAtomContainer mol = sp.parseSmiles("C#N=CC(CNC)N1CC1");
        addExplicitHydrogens(mol);
        Assert.assertEquals(28.632, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    @Test
    public void testTPSA5() throws Exception {
        IAtomContainer mol = sp.parseSmiles("c1ccncc1");
        addExplicitHydrogens(mol);
        Assert.assertEquals(12.892, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    @Test
    public void testTPSA6() throws java.lang.Exception {
        IAtomContainer mol = sp.parseSmiles("[H][N+]([H])(C)C");//at:  16
        addExplicitHydrogens(mol);
        Assert.assertEquals(16.61, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    public void testTPSA7() throws java.lang.Exception {
        IAtomContainer mol = sp.parseSmiles("C(I)I");//at:  16
        addExplicitHydrogens(mol);
        Assert.assertEquals(0.0, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    public void testTPSA8() throws java.lang.Exception {
        IAtomContainer mol = sp.parseSmiles("C(O)O");//at:  16
        addExplicitHydrogens(mol);
        Assert.assertEquals(40.45, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    public void testRing() throws Exception {
        sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1CCCC1CCC2CCCNC2");
        addExplicitHydrogens(mol);
        DescriptorValue dv = descriptor.calculate(mol);
        Assert.assertNotNull(dv);
    }

}
