/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-qsarmolecular
 */
public class VABCDescriptorTest extends MolecularDescriptorTest {

    @Before
    public void setUp() throws Exception {
        setDescriptor(VABCDescriptor.class);
    }

    @Test
    public void testIronChloride() throws InvalidSmilesException, CDKException {
        IAtomContainer ironChloride = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles("Cl[Fe]Cl");
        Assert.assertEquals(Double.NaN, ((DoubleResult) descriptor.calculate(ironChloride).getValue()).doubleValue(),
                0.01);
    }

}
