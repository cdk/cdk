/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-qsaratomic
 */
public class ProtonTotalPartialChargeDescriptorTest extends AtomicDescriptorTest {

    public ProtonTotalPartialChargeDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(ProtonTotalPartialChargeDescriptor.class);
    }

    @Test
    public void testProtonTotalPartialChargeDescriptorTest() throws java.lang.Exception {
        double[] testResult = {0.07915, 0.05783, 0.05783, 0.05783};
        IAtomicDescriptor descriptor = new ProtonTotalPartialChargeDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CF");
        addExplicitHydrogens(mol);
        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(mol.getAtom(0), mol).getValue();
        for (int i = 0; i < testResult.length; ++i) {
            Assert.assertEquals(testResult[i], retval.get(i), 0.00001);
        }
    }

    /**
     * @cdk.bug 2039739
     */
    @Test
    public void testNaNs() throws java.lang.Exception {
        IAtomicDescriptor descriptor = new ProtonTotalPartialChargeDescriptor();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(F)(F)");
        addExplicitHydrogens(mol);
        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(mol.getAtom(0), mol).getValue();
        Assert.assertEquals(5, retval.length());
        Assert.assertTrue(Double.isNaN(retval.get(3)));
        Assert.assertTrue(Double.isNaN(retval.get(4)));
    }
}
