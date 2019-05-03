/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @cdk.module test-qsarmolecular
 */
public class BasicGroupCountDescriptorTest extends MolecularDescriptorTest {

    @Before
    public void setUp() throws Exception {
        setDescriptor(BasicGroupCountDescriptor.class);
    }

    @Test
    public void testConstructor() throws Exception {
        Assert.assertNotNull(new BasicGroupCountDescriptor());
    }

    @Test
    public void testAmine() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NC");
        IntegerResult result = (IntegerResult) descriptor.calculate(mol).getValue();
        Assert.assertEquals(1, result.intValue());
    }

    @Test(expected = IllegalStateException.class)
    public void uninitalisedError() {
        new BasicGroupCountDescriptor().calculate(new AtomContainer());
    }

    /**
     * @cdk.inchi InChI=1S/C2H4N2/c1-4-2-3/h2-3H,1H2
     */
    @Test
    public void test() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "N");
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "N");
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a8);
        IBond b1 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a4, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a2, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a2, a8, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a3, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a4, a6, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a4, a7, IBond.Order.SINGLE);
        mol.addBond(b7);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);

        IntegerResult result = (IntegerResult) descriptor.calculate(mol).getValue();
        // two SMARTS matches
        Assert.assertEquals(2, result.intValue());
    }
}
