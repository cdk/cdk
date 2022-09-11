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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-qsarmolecular
 */
class VABCDescriptorTest extends MolecularDescriptorTest {

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(VABCDescriptor.class);
    }

    @Test
    void testIronChloride() throws CDKException {
        IAtomContainer ironChloride = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles("Cl[Fe]Cl");
        Assertions.assertEquals(Double.NaN, ((DoubleResult) descriptor.calculate(ironChloride).getValue()).doubleValue(), 0.01);
    }

}
