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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
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
        IChemObjectBuilder builder =
            NoNotificationChemObjectBuilder.getInstance();
        IMolecule methanol = builder.newMolecule();
        methanol.addAtom(builder.newAtom("C"));
        methanol.addAtom(builder.newAtom("O"));
        methanol.addBond(0, 1, IBond.Order.SINGLE);
        IDescriptorResult result = descriptor.calculate(methanol).getValue();
        Assert.assertTrue(result instanceof DoubleResult);
        Assert.assertEquals(1.46, ((DoubleResult)result).doubleValue(), 0.01);
    }

    @Test
    public void testMethane() {
        IChemObjectBuilder builder =
            NoNotificationChemObjectBuilder.getInstance();
        IMolecule methane = builder.newMolecule();
        methane.addAtom(builder.newAtom("C"));
        IDescriptorResult result = descriptor.calculate(methane).getValue();
        Assert.assertTrue(result instanceof DoubleResult);
        Assert.assertEquals(1.57, ((DoubleResult)result).doubleValue(), 0.01);
    }

    @Test
    public void testChloroform() {
        IChemObjectBuilder builder =
            NoNotificationChemObjectBuilder.getInstance();
        IMolecule chloroform = builder.newMolecule();
        chloroform.addAtom(builder.newAtom("C"));
        for (int i=0; i<3; i++) {
            chloroform.addAtom(builder.newAtom("Cl"));
            chloroform.addBond(0, (i+1), IBond.Order.SINGLE);
        }
        IDescriptorResult result = descriptor.calculate(chloroform).getValue();
        Assert.assertTrue(result instanceof DoubleResult);
        Assert.assertEquals(1.24, ((DoubleResult)result).doubleValue(), 0.01);
    }
}

