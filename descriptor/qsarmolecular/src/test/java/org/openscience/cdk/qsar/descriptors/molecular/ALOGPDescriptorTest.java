/* Copyright (C) 2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Test suite for the alogp descriptor
 *
 * @cdk.module test-qsarmolecular
 */
public class ALOGPDescriptorTest extends MolecularDescriptorTest {

    private CDKHydrogenAdder hydrogenAdder;

    @Before
    public void setUp() throws Exception {
        setDescriptor(ALOGPDescriptor.class);
        hydrogenAdder = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
    }

    /**
     * This test is actually testing 1-cholorpropane.
     * @cdk.inchi InChI=1S/C3H7Cl/c1-2-3-4/h2-3H2,1H3
     */
    @Test
    public void testChloroButane() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom cl = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");
        mol.addAtom(c1);
        mol.addAtom(c2);
        mol.addAtom(c3);
        mol.addAtom(cl);
        mol.addBond(new Bond(c1, c2));
        mol.addBond(new Bond(c2, c3));
        mol.addBond(new Bond(c3, cl));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance())
                        .addImplicitHydrogens(mol);

        DescriptorValue v = descriptor.calculate(mol);
        Assert.assertEquals(1.719, ((DoubleArrayResult) v.getValue()).get(0), 0.01);
        Assert.assertEquals(20.585, ((DoubleArrayResult) v.getValue()).get(2), 0.01);
    }

}
