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
package org.openscience.cdk.qsar.descriptors.atomic;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
public class AtomHybridizationDescriptorTest extends AtomicDescriptorTest {

    public AtomHybridizationDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(AtomHybridizationDescriptor.class);
    }

    @Test
    public void testAtomHybridizationDescriptorTest() throws ClassNotFoundException, CDKException, java.lang.Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C#CC=CC"); //
        addExplicitHydrogens(mol);
        IAtomType.Hybridization[] expectedStates = new IAtomType.Hybridization[]{IAtomType.Hybridization.SP1,
                IAtomType.Hybridization.SP1, IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP3};
        for (int i = 0; i < expectedStates.length; i++) {
            Assert.assertEquals(expectedStates[i].ordinal(), ((IntegerResult) descriptor.calculate(mol.getAtom(i), mol)
                    .getValue()).intValue());
        }
    }

    @Test
    public void testBug1701073() throws Exception {

        String[] smiles = new String[]{"C1CCCC=2[C]1(C(=O)NN2)C", "C1CCCC=2[C]1(C(=O)NN2)O",
                "C[Si](C)(C)[CH](Br)CC(F)(Br)F", "c1(ccc(cc1)O)C#N", "CCN(CC)C#CC#CC(=O)OC",
                "C(#CN1CCCCC1)[Sn](C)(C)C", "c1([As+](c2ccccc2)(c2ccccc2)C)ccccc1.[I-]",
                "c1(noc(n1)CCC(=O)N(CC)CC)c1ccc(cc1)C", "c1c(c(ccc1)O)/C=N/CCCC", "c1(ccc(cc1)C#Cc1ccc(cc1)C#C)OC"};

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol;
        Iterator<IAtom> atoms;

        for (String smile : smiles) {
            mol = sp.parseSmiles(smile);
            addImplicitHydrogens(mol);
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
            atoms = mol.atoms().iterator();
            while (atoms.hasNext()) {
                IAtom atom = atoms.next();
                ((IntegerResult) descriptor.calculate(atom, mol).getValue()).intValue();
            }
        }

    }
}
