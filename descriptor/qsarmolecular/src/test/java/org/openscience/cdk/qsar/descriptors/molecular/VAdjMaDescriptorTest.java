/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.Matchers.closeTo;

/**
 * TestSuite for the VAdjMaDescriptor.
 *
 * @cdk.module test-qsarmolecular
 */
public class VAdjMaDescriptorTest extends MolecularDescriptorTest {

    @Before
    public void setUp() throws Exception {
        setDescriptor(VAdjMaDescriptor.class);
    }

    public void ignoreCalculate_IAtomContainer() {
        Assert.fail("Not tested");
    }

    @Test
    public void testCyclic() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1CCC2CCCCC2C1");
        org.hamcrest.MatcherAssert.assertThat(((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), closeTo(4.459, 0.001));
    }

    @Test
    public void testLinear() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCCCCCCCC");
        org.hamcrest.MatcherAssert.assertThat(((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), closeTo(4.17, 0.001));
    }

    @Test
    public void testCompound() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCCC1CCCCC1");
        org.hamcrest.MatcherAssert.assertThat(((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), closeTo(4.322, 0.001));
    }
}
