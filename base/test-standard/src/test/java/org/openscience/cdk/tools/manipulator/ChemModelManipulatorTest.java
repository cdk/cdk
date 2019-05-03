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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLRXNV2000Reader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module test-standard
 */
public class ChemModelManipulatorTest extends CDKTestCase {

    private final static ILoggingTool logger      = LoggingToolFactory
                                                          .createLoggingTool(ChemModelManipulatorTest.class);

    IAtomContainer                    molecule1   = null;
    IAtomContainer                    molecule2   = null;
    IAtom                             atomInMol1  = null;
    IBond                             bondInMol1  = null;
    IAtom                             atomInMol2  = null;
    IAtomContainerSet                 moleculeSet = null;
    IReaction                         reaction    = null;
    IReactionSet                      reactionSet = null;
    IChemModel                        chemModel   = null;

    public ChemModelManipulatorTest() {
        super();
    }

    @Before
    public void setUp() {
        molecule1 = new AtomContainer();
        atomInMol1 = new Atom("Cl");
        atomInMol1.setCharge(-1.0);
        atomInMol1.setFormalCharge(-1);
        atomInMol1.setImplicitHydrogenCount(1);
        molecule1.addAtom(atomInMol1);
        molecule1.addAtom(new Atom("Cl"));
        bondInMol1 = new Bond(atomInMol1, molecule1.getAtom(1));
        molecule1.addBond(bondInMol1);
        molecule2 = new AtomContainer();
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
        chemModel = new ChemModel();
        chemModel.setMoleculeSet(moleculeSet);
        chemModel.setReactionSet(reactionSet);
    }

    @Test
    public void testGetAllAtomContainers_IChemModel() throws Exception {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemModel chemFile = (ChemModel) reader.read((ChemObject) new ChemModel());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemModelManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
    }

    @Test
    public void testGetAllAtomContainers_IChemModel_WithReactions() throws Exception {
        String filename = "data/mdl/0024.stg02.rxn";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        MDLRXNV2000Reader reader = new MDLRXNV2000Reader(ins, Mode.STRICT);
        ChemModel chemFile = (ChemModel) reader.read((ChemObject) new ChemModel());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemModelManipulator.getAllAtomContainers(chemFile);

        Assert.assertEquals(2, containersList.size());
    }

    @Test
    public void testNewChemModel_IAtomContainer() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        IChemModel model = ChemModelManipulator.newChemModel(ac);
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(ac.getAtomCount(), mol.getAtomCount());
    }

    @Test
    public void testGetAtomCount_IChemModel() {
        int count = ChemModelManipulator.getAtomCount(chemModel);
        Assert.assertEquals(6, count);
    }

    @Test
    public void testGetBondCount_IChemModel() {
        int count = ChemModelManipulator.getBondCount(chemModel);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testRemoveElectronContainer_IChemModel_IElectronContainer() {
        IAtomContainer mol1 = new AtomContainer();
        mol1.addAtom(new Atom("Cl"));
        mol1.addAtom(new Atom("Cl"));
        IBond bond1 = new Bond(mol1.getAtom(0), mol1.getAtom(1));
        mol1.addBond(bond1);
        IAtomContainer mol2 = new AtomContainer();
        mol2.addAtom(new Atom("I"));
        mol2.addAtom(new Atom("I"));
        IBond bond2 = new Bond(mol2.getAtom(0), mol2.getAtom(1));
        mol2.addBond(bond2);
        IAtomContainerSet molSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        molSet.addAtomContainer(mol1);
        IReaction r = new Reaction();
        r.addProduct(mol2);
        IReactionSet rSet = new ReactionSet();
        rSet.addReaction(r);
        IChemModel model = new ChemModel();
        model.setMoleculeSet(molSet);
        model.setReactionSet(rSet);
        IBond otherBond = new Bond();
        Assert.assertEquals(2, ChemModelManipulator.getBondCount(model));
        ChemModelManipulator.removeElectronContainer(model, otherBond);
        Assert.assertEquals(2, ChemModelManipulator.getBondCount(model));
        ChemModelManipulator.removeElectronContainer(model, bond1);
        Assert.assertEquals(1, ChemModelManipulator.getBondCount(model));
        ChemModelManipulator.removeElectronContainer(model, bond2);
        Assert.assertEquals(0, ChemModelManipulator.getBondCount(model));
    }

    @Test
    public void testRemoveAtomAndConnectedElectronContainers_IChemModel_IAtom() {
        IAtomContainer mol1 = new AtomContainer();
        IAtom atom1 = new Atom("Cl");
        mol1.addAtom(atom1);
        mol1.addAtom(new Atom("Cl"));
        IBond bond1 = new Bond(mol1.getAtom(0), mol1.getAtom(1));
        mol1.addBond(bond1);
        IAtomContainer mol2 = new AtomContainer();
        IAtom atom2 = new Atom("I");
        mol2.addAtom(atom2);
        mol2.addAtom(new Atom("I"));
        IBond bond2 = new Bond(mol2.getAtom(0), mol2.getAtom(1));
        mol2.addBond(bond2);
        IAtomContainerSet molSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        molSet.addAtomContainer(mol1);
        IReaction r = new Reaction();
        r.addProduct(mol2);
        IReactionSet rSet = new ReactionSet();
        rSet.addReaction(r);
        IChemModel model = new ChemModel();
        model.setMoleculeSet(molSet);
        model.setReactionSet(rSet);
        IAtom otherAtom = new Atom("Cl");
        Assert.assertEquals(2, ChemModelManipulator.getBondCount(model));
        Assert.assertEquals(4, ChemModelManipulator.getAtomCount(model));
        ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, otherAtom);
        Assert.assertEquals(2, ChemModelManipulator.getBondCount(model));
        Assert.assertEquals(4, ChemModelManipulator.getAtomCount(model));
        ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, atom1);
        Assert.assertEquals(1, ChemModelManipulator.getBondCount(model));
        Assert.assertEquals(3, ChemModelManipulator.getAtomCount(model));
        ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, atom2);
        Assert.assertEquals(0, ChemModelManipulator.getBondCount(model));
        Assert.assertEquals(2, ChemModelManipulator.getAtomCount(model));
    }

    @Test
    public void testSetAtomProperties_IChemModel_Object_Object() {
        String key = "key";
        String value = "value";
        ChemModelManipulator.setAtomProperties(chemModel, key, value);
        Assert.assertEquals(value, atomInMol1.getProperty(key));
        Assert.assertEquals(value, atomInMol2.getProperty(key));
    }

    @Test
    public void testGetRelevantAtomContainer_IChemModel_IAtom() {
        IAtomContainer ac1 = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInMol1);
        Assert.assertEquals(molecule1, ac1);
        IAtomContainer ac2 = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInMol2);
        Assert.assertEquals(molecule2, ac2);
    }

    @Test
    public void testGetRelevantAtomContainer_IChemModel_IBond() {
        IAtomContainer ac1 = ChemModelManipulator.getRelevantAtomContainer(chemModel, bondInMol1);
        Assert.assertEquals(molecule1, ac1);
    }

    @Test
    public void testGetAllChemObjects_IChemModel() {
        List<IChemObject> list = ChemModelManipulator.getAllChemObjects(chemModel);
        Assert.assertEquals(5, list.size());
        //int atomCount = 0; // not traversed
        //int bondCount = 0; // not traversed
        int molCount = 0;
        int molSetCount = 0;
        int reactionCount = 0;
        int reactionSetCount = 0;
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
            else
                Assert.fail("Unexpected Object of type " + o.getClass());
        }
        //Assert.assertEquals(3, atomCount);
        //Assert.assertEquals(1, bondCount);
        Assert.assertEquals(2, molCount);
        Assert.assertEquals(1, molSetCount);
        Assert.assertEquals(1, reactionCount);
        Assert.assertEquals(1, reactionSetCount);
    }

    @Test
    public void testCreateNewMolecule_IChemModel() {
        IChemModel model = new ChemModel();
        IAtomContainer ac = ChemModelManipulator.createNewMolecule(model);
        Assert.assertEquals(1, model.getMoleculeSet().getAtomContainerCount());
        Assert.assertEquals(ac, model.getMoleculeSet().getAtomContainer(0));
    }

    @Test
    public void testGetRelevantReaction_IChemModel_IAtom() {
        IReaction r = ChemModelManipulator.getRelevantReaction(chemModel, atomInMol1);
        Assert.assertNotNull(r);
        Assert.assertEquals(reaction, r);
    }

    @Test
    public void testGetAllIDs_IChemModel() {
        Assert.assertEquals(0, ChemModelManipulator.getAllIDs(chemModel).size());
        IDCreator.createIDs(chemModel);
        List<String> allIDs = ChemModelManipulator.getAllIDs(chemModel);
        Assert.assertEquals(16, ChemModelManipulator.getAllIDs(chemModel).size());
        Set<String> uniq = new HashSet<String>(allIDs);
        Assert.assertEquals(10, uniq.size());
    }

    /**
     * @cdk.bug 3530861
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRelevantAtomContainer_NonExistentAtom() {
        IChemModel model = new org.openscience.cdk.silent.ChemModel();
        ChemModelManipulator.getRelevantAtomContainer(model, new org.openscience.cdk.silent.Atom());
    }

}
