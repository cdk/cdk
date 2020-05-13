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
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.Matchers.is;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class RuleOfFiveDescriptorTest extends MolecularDescriptorTest {

    public RuleOfFiveDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(RuleOfFiveDescriptor.class);
    }

    @Test
    public void testRuleOfFiveDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC(OCC)OCC(c1cccc2ccccc12)C4CCC(CCCO)C(CC3CNCNC3)C4"); //
        addExplicitHydrogens(mol);
        Assert.assertEquals(3, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void testRuleOfFiveRotatableBonds() throws java.lang.Exception {
        Object[] params = {true};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC1=CC(NC(=O)CC)=CC(CCC)=C1"); // nRot = 10 (excl. amide C-N bond)
        addExplicitHydrogens(mol);
        org.hamcrest.MatcherAssert.assertThat(((IntegerResult) descriptor.calculate(mol).getValue()).intValue(), is(0));
    }

    @Test
    public void testRuleOfFiveRotatableBondsViolated() throws java.lang.Exception {
        Object[] params = {true};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCCC1=CC(CCC)=CC(NC(=O)CC)=C1"); // nRot = 11 (excl. amide C-N bond)
        addExplicitHydrogens(mol);
        org.hamcrest.MatcherAssert.assertThat(((IntegerResult) descriptor.calculate(mol).getValue()).intValue(), is(1));
    }
}
