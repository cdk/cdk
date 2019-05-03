/* Copyright (C) 2007  Federico
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;

/**
 * @cdk.module test-qsarmolecular
 */
public class AutocorrelationDescriptorMassTest extends MolecularDescriptorTest {

    public AutocorrelationDescriptorMassTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        setDescriptor(AutocorrelationDescriptorMass.class);
    }

    @Test
    public void test1() throws Exception {
        String filename = "data/mdl/clorobenzene.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer container = reader.read(new AtomContainer());
        DescriptorValue count = new AutocorrelationDescriptorMass().calculate(container);
        Assert.assertEquals(5, count.getValue().length());
        Assert.assertTrue(count.getValue() instanceof DoubleArrayResult);
        DoubleArrayResult result = (DoubleArrayResult) count.getValue();
        for (int i = 0; i < 5; i++) {
            Assert.assertFalse(Double.isNaN(result.get(i)));
            Assert.assertTrue(0.0 != result.get(i));
        }
    }

}
