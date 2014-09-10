/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar.descriptors.bond;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-qsarbond
 */
public class AtomicNumberDifferenceDescriptorTest extends BondDescriptorTest {

    /**
     *  Constructor for the MassNumberDifferenceDescriptorTest object
     *
     */
    public AtomicNumberDifferenceDescriptorTest() {

    }

    @Before
    public void setUp() throws Exception {
        setDescriptor(AtomicNumberDifferenceDescriptor.class);
    }

    @Test
    public void testDescriptor1() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles("CC");
        double value = ((DoubleResult) descriptor.calculate(mol1.getBond(0), mol1).getValue()).doubleValue();
        Assert.assertEquals(0, value, 0.0000);
    }

    @Test
    public void testDescriptor2() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles("CO");
        double value = ((DoubleResult) descriptor.calculate(mol1.getBond(0), mol1).getValue()).doubleValue();
        Assert.assertEquals(2, value, 0.0000);
    }

}
