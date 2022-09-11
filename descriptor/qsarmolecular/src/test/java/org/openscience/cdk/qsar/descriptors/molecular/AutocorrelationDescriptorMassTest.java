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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;

/**
 * @cdk.module test-qsarmolecular
 */
class AutocorrelationDescriptorMassTest extends MolecularDescriptorTest {

    AutocorrelationDescriptorMassTest() {
        super();
    }

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(AutocorrelationDescriptorMass.class);
    }

    @Test
    void test1() throws Exception {
        String filename = "chlorobenzene.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer container = reader.read(new AtomContainer());
        DescriptorValue count = new AutocorrelationDescriptorMass().calculate(container);
        Assertions.assertEquals(5, count.getValue().length());
        Assertions.assertTrue(count.getValue() instanceof DoubleArrayResult);
        DoubleArrayResult result = (DoubleArrayResult) count.getValue();
        for (int i = 0; i < 5; i++) {
            Assertions.assertFalse(Double.isNaN(result.get(i)));
            Assertions.assertTrue(0.0 != result.get(i));
        }
    }

}
