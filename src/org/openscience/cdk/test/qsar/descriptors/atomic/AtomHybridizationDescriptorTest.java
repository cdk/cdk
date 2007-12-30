/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.qsar.descriptors.atomic;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.AtomHybridizationDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
public class AtomHybridizationDescriptorTest extends AtomicDescriptorTest {

    public AtomHybridizationDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(AtomHybridizationDescriptorTest.class);
    }

    public void testAtomHybridizationDescriptorTest() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IAtomicDescriptor descriptor = new AtomHybridizationDescriptor();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C#CC=C"); //
        addExplicitHydrogens(mol);
        assertEquals(1, ((IntegerResult) descriptor.calculate(mol.getAtom(0), mol).getValue()).intValue());
    }

    public void testBug1701073() throws Exception {

        String[] smiles = new String[]
                {
                        "C1CCCC=2[C]1(C(=O)NN2)C",
                        "C1CCCC=2[C]1(C(=O)NN2)O",
                        "C[Si](C)(C)[CH](Br)CC(F)(Br)F",
                        "c1(ccc(cc1)O)C#N",
                        "CCN(CC)C#CC#CC(=O)OC",
                        "C(#CN1CCCCC1)[Sn](C)(C)C",
                        "c1([As+](c2ccccc2)(c2ccccc2)C)ccccc1.[I-]",
                        "c1(noc(n1)CCC(=O)N(CC)CC)c1ccc(cc1)C",
                        "c1c(c(ccc1)O)/C=N/CCCC",
                        "c1(ccc(cc1)C#Cc1ccc(cc1)C#C)OC"
                };

        IAtomicDescriptor descriptor = new AtomHybridizationDescriptor();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol;
        Iterator atoms;

        for (int i = 0; i < smiles.length; i++) {
            mol = sp.parseSmiles(smiles[i]);
            addImplicitHydrogens(mol);
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
            atoms = mol.atoms();
            while (atoms.hasNext()) {
                IAtom atom = (IAtom) atoms.next();
                int htype = ((IntegerResult) descriptor.calculate(atom, mol).getValue()).intValue();
            }
        }


    }
}
