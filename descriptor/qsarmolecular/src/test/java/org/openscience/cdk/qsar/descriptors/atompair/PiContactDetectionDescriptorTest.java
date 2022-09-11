/* Copyright (C) 2021 kazuyaujihara
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
package org.openscience.cdk.qsar.descriptors.atompair;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomPairDescriptor;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.test.qsar.DescriptorTest;

class PiContactDetectionDescriptorTest extends DescriptorTest<IAtomPairDescriptor> {

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(PiContactDetectionDescriptor.class);
    }

    @Test
    void testButadiene() {
        IAtomContainer mol = TestMoleculeFactory.makeAlkane(4);
        mol.getBond(0).setOrder(IBond.Order.DOUBLE);
        mol.getBond(2).setOrder(IBond.Order.DOUBLE);
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                Assertions.assertTrue(checkAtomAtom(mol, i, j));
    }

    @Test
    void test137() throws CDKException {
        IAtomContainer mol = TestMoleculeFactory.makeAlkane(8);
        mol.getBond(0).setOrder(IBond.Order.DOUBLE);
        mol.getBond(2).setOrder(IBond.Order.DOUBLE);
        mol.getBond(6).setOrder(IBond.Order.DOUBLE);
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                Assertions.assertTrue(checkAtomAtom(mol, i, j), i + "," + j + " should have pi contact");
        for (int i = 0; i < 5; i++)
            for (int j = 5; j < 8; j++)
                Assertions.assertFalse(checkAtomAtom(mol, i, j), i + "," + j + " should NOT have pi contact");
        // a single double bond is not considered see
        Assertions.assertFalse(checkAtomAtom(mol, 6, 7));
    }

    private boolean checkAtomAtom(IAtomContainer mol, int i, int j) {
        DescriptorValue result = descriptor.calculate(mol.getAtom(i), mol.getAtom(j), mol);
        BooleanResult val = (BooleanResult)result.getValue();
        return val.booleanValue();
    }
}