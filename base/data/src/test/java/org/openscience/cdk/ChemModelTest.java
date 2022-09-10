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
 *
 */

package org.openscience.cdk;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.test.interfaces.AbstractChemModelTest;

/**
 * Checks the functionality of the ChemModel class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ChemModel
 */
public class ChemModelTest extends AbstractChemModelTest {

    @BeforeAll
    public static void setUp() {
        setTestObjectBuilder(ChemModel::new);
    }

    @Test
    public void testChemModel() {
        IChemModel chemModel = new ChemModel();
        Assertions.assertNotNull(chemModel);
        Assertions.assertTrue(chemModel.isEmpty());

        IAtom atom = new Atom("N");
        IAtomContainer mol = new AtomContainer();
        IAtomContainerSet mset = new AtomContainerSet();
        mol.addAtom(atom);
        mset.addAtomContainer(mol);
        chemModel.setMoleculeSet(mset);
        Assertions.assertFalse(chemModel.isEmpty());
        mol.removeAtomOnly(atom);
        Assertions.assertFalse(chemModel.isEmpty());
        chemModel.setMoleculeSet(null);
        Assertions.assertTrue(chemModel.isEmpty());

        IChemModel model1 = new ChemModel();
        mol.addAtom(atom);
        IReaction react = new Reaction();
        react.addReactant(mol);
        IReactionSet rset = new ReactionSet();
        rset.addReaction(react);
        model1.setReactionSet(rset);
        Assertions.assertFalse(model1.isEmpty());
        mol.removeAtomOnly(atom);
        Assertions.assertFalse(model1.isEmpty());
        model1.setReactionSet(null);
        Assertions.assertTrue(model1.isEmpty());

        IChemModel model2 = new ChemModel();
        mol.addAtom(atom);
        IRingSet ringset = new RingSet();
        ringset.add(mset);
        model2.setRingSet(ringset);
        Assertions.assertFalse(model2.isEmpty());
        mol.removeAtomOnly(atom);
        Assertions.assertFalse(model2.isEmpty());
        model2.setRingSet(null);
        Assertions.assertTrue(model2.isEmpty());

        IChemModel model3 = new ChemModel();
        mol.addAtom(atom);
        ICrystal cry = new Crystal(mol);
        model3.setCrystal(cry);
        Assertions.assertFalse(model3.isEmpty());
        mol.removeAtomOnly(atom);
        Assertions.assertFalse(model3.isEmpty());
        model3.setCrystal(null);
        Assertions.assertTrue(model3.isEmpty());
    }

}
