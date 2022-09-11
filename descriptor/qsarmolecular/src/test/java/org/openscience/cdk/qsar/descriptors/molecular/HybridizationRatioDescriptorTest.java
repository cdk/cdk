/*
 * Copyright (C) 2010 Rajarshi Guha <rajarshi.guha@gmail.com>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs a test for the {@link HybridizationRatioDescriptor}.
 *
 * @cdk.module test-qsarmolecular
 */
class HybridizationRatioDescriptorTest extends MolecularDescriptorTest {

    HybridizationRatioDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(HybridizationRatioDescriptor.class);
    }

    @Test
    void testHybRatioDescriptor1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCC");
        Assertions.assertEquals(1.0, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1);
    }

    @Test
    void testHybRatioDescriptor2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1ccccc1");
        Assertions.assertEquals(0.0, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1);
    }

    @Test
    void testHybRatioDescriptor3() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[H]C#N");
        Assertions.assertEquals(Double.NaN, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1);
    }
}
