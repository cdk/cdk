/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.manipulator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.tools.IDCreator;

/**
 * @cdk.module test-standard
 */
class ChemSequenceManipulatorTest extends CDKTestCase {

    private IAtomContainer    molecule1    = null;
    private IAtomContainer    molecule2    = null;
    private IAtom             atomInMol1   = null;
    private IBond             bondInMol1   = null;
    private IAtom             atomInMol2   = null;
    private IAtomContainerSet moleculeSet  = null;
    private IReaction         reaction     = null;
    private IReactionSet      reactionSet  = null;
    private IChemModel        chemModel1   = null;
    private IChemModel        chemModel2   = null;
    private IChemSequence     chemSequence = null;

    ChemSequenceManipulatorTest() {
        super();
    }

    @BeforeEach
    void setUp() {
        molecule1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atomInMol1 = new Atom("Cl");
        molecule1.addAtom(atomInMol1);
        molecule1.addAtom(new Atom("Cl"));
        bondInMol1 = new Bond(atomInMol1, molecule1.getAtom(1));
        molecule1.addBond(bondInMol1);
        molecule2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atomInMol2 = new Atom("O");
        atomInMol2.setImplicitHydrogenCount(2);
        molecule2.addAtom(atomInMol2);
        moleculeSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        moleculeSet.addAtomContainer(molecule1);
        moleculeSet.addAtomContainer(molecule2);
        reaction = new Reaction();
        reaction.addReactant(molecule1);
        reaction.addProduct(molecule2);
        reactionSet = new ReactionSet();
        reactionSet.addReaction(reaction);
        chemModel1 = new ChemModel();
        chemModel1.setMoleculeSet(moleculeSet);
        chemModel2 = new ChemModel();
        chemModel2.setReactionSet(reactionSet);
        chemSequence = new ChemSequence();
        chemSequence.addChemModel(chemModel1);
        chemSequence.addChemModel(chemModel2);
    }

    @Test
    void testGetAtomCount_IChemSequence() {
        int count = ChemSequenceManipulator.getAtomCount(chemSequence);
        Assertions.assertEquals(6, count);
    }

    @Test
    void testGetBondCount_IChemSequence() {
        int count = ChemSequenceManipulator.getBondCount(chemSequence);
        Assertions.assertEquals(2, count);
    }

    @Test
    void testGetAllAtomContainers_IChemSequence() {
        List<IAtomContainer> list = ChemSequenceManipulator.getAllAtomContainers(chemSequence);
        Assertions.assertEquals(4, list.size());
    }

    @Test
    void testGetAllChemObjects_IChemSequence() {
        List<IChemObject> list = ChemSequenceManipulator.getAllChemObjects(chemSequence);
        int molCount = 0;
        int molSetCount = 0;
        int reactionCount = 0;
        int reactionSetCount = 0;
        int chemModelCount = 0;
        for (Object o : list) {
            //if (o instanceof IAtom) ++atomCount;
            //if (o instanceof IBond) ++bondCount;
            if (o instanceof IAtomContainer)
                ++molCount;
            else if (o instanceof IAtomContainerSet)
                ++molSetCount;
            else if (o instanceof IReaction)
                ++reactionCount;
            else if (o instanceof IReactionSet)
                ++reactionSetCount;
            else if (o instanceof IChemModel)
                ++chemModelCount;
            else
                Assertions.fail("Unexpected Object of type " + o.getClass());
        }
        //Assert.assertEquals(3, atomCount);
        //Assert.assertEquals(1, bondCount);
        Assertions.assertEquals(2, molCount);
        Assertions.assertEquals(1, molSetCount);
        Assertions.assertEquals(1, reactionCount);
        Assertions.assertEquals(1, reactionSetCount);
        Assertions.assertEquals(2, chemModelCount);
    }

    @Test
    void testGetAllIDs_IChemSequence() {
        Assertions.assertEquals(0, ChemSequenceManipulator.getAllIDs(chemSequence).size());
        IDCreator.createIDs(chemSequence);
        List<String> allIDs = ChemSequenceManipulator.getAllIDs(chemSequence);
        Assertions.assertEquals(18, ChemSequenceManipulator.getAllIDs(chemSequence).size());
        Set<String> uniq = new HashSet<>(allIDs);
        Assertions.assertEquals(12, uniq.size());
    }

}
