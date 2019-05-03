/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * TestSuite that runs unit tests.
 *
 * @cdk.module test-qsarmolecular
 * @see MannholdLogPDescriptor
 */
public class MannholdLogPDescriptorTest extends MolecularDescriptorTest {

    @Before
    public void setUp() throws Exception {
        setDescriptor(MannholdLogPDescriptor.class);
    }

    @Test
    public void testMethanol() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer methanol = builder.newInstance(IAtomContainer.class);
        methanol.addAtom(builder.newInstance(IAtom.class, "C"));
        methanol.addAtom(builder.newInstance(IAtom.class, "O"));
        methanol.addBond(0, 1, IBond.Order.SINGLE);
        IDescriptorResult result = descriptor.calculate(methanol).getValue();
        Assert.assertTrue(result instanceof DoubleResult);
        Assert.assertEquals(1.46, ((DoubleResult) result).doubleValue(), 0.01);
    }

    @Test
    public void testMethane() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer methane = builder.newInstance(IAtomContainer.class);
        methane.addAtom(builder.newInstance(IAtom.class, "C"));
        IDescriptorResult result = descriptor.calculate(methane).getValue();
        Assert.assertTrue(result instanceof DoubleResult);
        Assert.assertEquals(1.57, ((DoubleResult) result).doubleValue(), 0.01);
    }

    @Test
    public void testChloroform() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer chloroform = builder.newInstance(IAtomContainer.class);
        chloroform.addAtom(builder.newInstance(IAtom.class, "C"));
        for (int i = 0; i < 3; i++) {
            chloroform.addAtom(builder.newInstance(IAtom.class, "Cl"));
            chloroform.addBond(0, (i + 1), IBond.Order.SINGLE);
        }
        IDescriptorResult result = descriptor.calculate(chloroform).getValue();
        Assert.assertTrue(result instanceof DoubleResult);
        Assert.assertEquals(1.24, ((DoubleResult) result).doubleValue(), 0.01);
    }
}
