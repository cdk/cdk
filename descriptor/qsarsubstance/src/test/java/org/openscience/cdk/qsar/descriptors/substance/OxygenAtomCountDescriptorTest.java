/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.substance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Substance;
import org.openscience.cdk.interfaces.ISubstance;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

class OxygenAtomCountDescriptorTest extends SubstanceDescriptorTest {

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(OxygenAtomCountDescriptor.class);
    }

    @Test
    void testCalculate_ZnO() throws Exception {
        ISubstance material = new Substance();
        material.addAtomContainer(
            MolecularFormulaManipulator.getAtomContainer(
                "ZnO", DefaultChemObjectBuilder.getInstance()
            )
        );
        DescriptorValue value = descriptor.calculate(material);
        Assertions.assertNotNull(value);
        IDescriptorResult result = value.getValue();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, ((IntegerResult)result).intValue());
    }

    @Test
    void testCalculate_IronOxide() throws Exception {
        ISubstance material = new Substance();
        material.addAtomContainer(
            MolecularFormulaManipulator.getAtomContainer(
                "Fe3O4", DefaultChemObjectBuilder.getInstance()
            )
        );
        DescriptorValue value = descriptor.calculate(material);
        Assertions.assertNotNull(value);
        IDescriptorResult result = value.getValue();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, ((IntegerResult)result).intValue());
    }

}
