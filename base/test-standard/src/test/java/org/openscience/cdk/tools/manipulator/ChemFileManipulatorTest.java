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

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 */
class ChemFileManipulatorTest extends CDKTestCase {

    private final static ILoggingTool logger        = LoggingToolFactory
                                                            .createLoggingTool(ChemFileManipulatorTest.class);

    private IAtomContainer                    molecule1     = null;
    private IAtomContainer                    molecule2     = null;
    private IAtom                             atomInMol1    = null;
    private IBond                             bondInMol1    = null;
    private IAtom                             atomInMol2    = null;
    private IAtomContainerSet                 moleculeSet   = null;
    private IReaction                         reaction      = null;
    private IReactionSet                      reactionSet   = null;
    private IChemModel                        chemModel     = null;
    private IChemSequence                     chemSequence1 = null;
    private IChemSequence                     chemSequence2 = null;
    private IChemFile                         chemFile      = null;

    ChemFileManipulatorTest() {
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
        moleculeSet = new AtomContainerSet();
        moleculeSet.addAtomContainer(molecule1);
        moleculeSet.addAtomContainer(molecule2);
        reaction = new Reaction();
        reaction.addReactant(molecule1);
        reaction.addProduct(molecule2);
        reactionSet = new ReactionSet();
        reactionSet.addReaction(reaction);
        chemModel = new ChemModel();
        chemModel.setMoleculeSet(moleculeSet);
        chemModel.setReactionSet(reactionSet);
        chemSequence1 = new ChemSequence();
        chemSequence1.addChemModel(chemModel);
        chemSequence2 = new ChemSequence();
        chemFile = new ChemFile();
        chemFile.addChemSequence(chemSequence1);
        chemFile.addChemSequence(chemSequence2);
    }

    @Test
    void testGetAllAtomContainers_IChemFile() throws Exception {
        String filename = "prev2000.sd";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);

        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(2, containersList.size());
    }

    @Test
    void testGetAllIDs_IChemFile() {
        Assertions.assertEquals(0, ChemFileManipulator.getAllIDs(chemFile).size());
        IDCreator.createIDs(chemFile);
        List<String> allIDs = ChemFileManipulator.getAllIDs(chemFile);
        Assertions.assertEquals(19, ChemFileManipulator.getAllIDs(chemFile).size());
        Set<String> uniq = new HashSet<>(allIDs);
        Assertions.assertEquals(13, uniq.size());
    }

    @Test
    void testGetAtomCount_IChemFile() {
        int count = ChemFileManipulator.getAtomCount(chemFile);
        Assertions.assertEquals(6, count);
    }

    @Test
    void testGetBondCount_IChemFile() {
        int count = ChemFileManipulator.getBondCount(chemFile);
        Assertions.assertEquals(2, count);
    }

    @Test
    void testGetAllChemObjects_IChemFile() {
        List<IChemObject> list = ChemFileManipulator.getAllChemObjects(chemFile);
        Assertions.assertEquals(8, list.size()); // not the file itself
        int atomCount = 0;
        int bondCount = 0;
        int molCount = 0;
        int molSetCount = 0;
        int reactionCount = 0;
        int reactionSetCount = 0;
        int chemModelCount = 0;
        int chemSequenceCount = 0;
        for (Object o : list) {
            if (o instanceof IAtom) ++atomCount;
            if (o instanceof IBond) ++bondCount;
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
            else if (o instanceof IChemSequence)
                ++chemSequenceCount;
            else
                Assertions.fail("Unexpected Object of type " + o.getClass());
        }
        Assertions.assertEquals(0, atomCount); /// it does not recurse into IAtomContainer
        Assertions.assertEquals(0, bondCount);
        Assertions.assertEquals(2, molCount);
        Assertions.assertEquals(1, molSetCount);
        Assertions.assertEquals(1, reactionCount);
        Assertions.assertEquals(1, reactionSetCount);
        Assertions.assertEquals(1, chemModelCount);
        Assertions.assertEquals(2, chemSequenceCount);
    }

    @Test
    void testGetAllChemModels_IChemFile() {
        List<IChemModel> list = ChemFileManipulator.getAllChemModels(chemFile);
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void testGetAllReactions_IChemFile() {
        List<IReaction> list = ChemFileManipulator.getAllReactions(chemFile);
        Assertions.assertEquals(1, list.size());
    }

}
