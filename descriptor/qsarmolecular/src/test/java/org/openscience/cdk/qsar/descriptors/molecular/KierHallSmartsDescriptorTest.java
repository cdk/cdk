/* Copyright (C) 2008 Rajarshi Guha
 *
 * Contact: rajarshi@users.sourceforge.net
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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all test for the KierHallSmartsDescriptor
 *
 * @cdk.module test-qsarmolecular
 */
class KierHallSmartsDescriptorTest extends MolecularDescriptorTest {

    private String[] names;

    KierHallSmartsDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(KierHallSmartsDescriptor.class);
        names = descriptor.getDescriptorNames();
    }

    private int getIndex(String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) return i;
        }
        return -1;
    }

    @Test
    void test1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCO");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        DescriptorValue value = descriptor.calculate(mol);
        IntegerArrayResult result = (IntegerArrayResult) value.getValue();

        Assertions.assertEquals(79, result.length());
        Assertions.assertEquals(1, result.get(getIndex("khs.sOH")));
    }

    @Test
    void test2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1c(CN)cc(CCNC)cc1C(CO)CC(=O)CCOCCCO");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        DescriptorValue value = descriptor.calculate(mol);
        IntegerArrayResult result = (IntegerArrayResult) value.getValue();

        Assertions.assertEquals(79, result.length());
        Assertions.assertEquals(2, result.get(getIndex("khs.sOH")));
        Assertions.assertEquals(1, result.get(getIndex("khs.dO")));
        Assertions.assertEquals(1, result.get(getIndex("khs.ssO")));
        Assertions.assertEquals(1, result.get(getIndex("khs.sNH2")));
        Assertions.assertEquals(1, result.get(getIndex("khs.ssNH")));
    }

    @Test
    void test3() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C#CC(C)(C)C(C)(C)C#C");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        DescriptorValue value = descriptor.calculate(mol);
        IntegerArrayResult result = (IntegerArrayResult) value.getValue();

        Assertions.assertEquals(79, result.length());
        Assertions.assertEquals(2, result.get(getIndex("khs.tsC")));
        Assertions.assertEquals(2, result.get(getIndex("khs.ssssC")));
    }
}
