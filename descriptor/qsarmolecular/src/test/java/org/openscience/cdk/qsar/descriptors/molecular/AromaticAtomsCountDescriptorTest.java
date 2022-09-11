/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

class AromaticAtomsCountDescriptorTest extends MolecularDescriptorTest {

    AromaticAtomsCountDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(AromaticAtomsCountDescriptor.class);
    }

    @Test
    void testAromaticAtomsCountDescriptor() throws java.lang.Exception {
        Object[] params = {true};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCOc1ccccc1"); // ethanol
        Assertions.assertEquals(6, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void testViaFlags() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();
        for (IAtom iAtom : molecule.atoms()) {
            iAtom.setFlag(CDKConstants.ISAROMATIC, true);
        }
        Assertions.assertEquals(6, ((IntegerResult) descriptor.calculate(molecule).getValue()).intValue());
    }
}
