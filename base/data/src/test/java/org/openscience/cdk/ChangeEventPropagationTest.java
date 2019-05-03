/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 * Checks the propagation of ChangeEvents through a
 * nested set of objects.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ChemFile
 */
public class ChangeEventPropagationTest extends CDKTestCase {

    @BeforeClass
    public static void setUp() {}

    @Test
    public void testPropagation() {
        ChemFile cf = new ChemFile();
        ChemSequence cs = new ChemSequence();
        ChemModel cm = new ChemModel();
        IAtomContainerSet som = new AtomContainerSet();
        IAtomContainer mol = new AtomContainer();
        Atom a1 = new Atom("C");
        Atom a2 = new Atom("C");
        Bond b1 = new Bond(a1, a2);
        mol.addAtom(a1);
        mol.addAtom(a2);
        mol.addBond(b1);
        som.addAtomContainer(mol);
        cm.setMoleculeSet(som);
        cs.addChemModel(cm);
        cf.addChemSequence(cs);
        TestListener ts = new TestListener();
        cf.addListener(ts);
        a2.setSymbol("N");
        Assert.assertTrue(ts.changedObject instanceof Atom);
        Assert.assertEquals("N", ((Atom) ts.changedObject).getSymbol());
    }

    class TestListener implements IChemObjectListener {

        ChemObject changedObject = null;

        @Override
        public void stateChanged(IChemObjectChangeEvent evt) {
            changedObject = (ChemObject) evt.getSource();
        }
    }
}
