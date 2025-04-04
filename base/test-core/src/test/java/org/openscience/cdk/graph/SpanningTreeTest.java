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
package org.openscience.cdk.graph;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 */
class SpanningTreeTest extends CDKTestCase {

    private static SpanningTree azulene = null;
    private static SpanningTree ethane  = null;

    @BeforeEach
    void setUp() throws Exception {
        if (azulene == null) {
            // load azulene
            String filename = "azulene.mol";
            InputStream ins = this.getClass().getResourceAsStream(filename);
            MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
            IChemFile chemFile = reader.read(new ChemFile());
            IChemSequence seq = chemFile.getChemSequence(0);
            IChemModel model = seq.getChemModel(0);
            IAtomContainer azuleneMolecule = model.getMoleculeSet().getAtomContainer(0);
            Assertions.assertEquals(10, azuleneMolecule.getAtomCount());
            Assertions.assertEquals(11, azuleneMolecule.getBondCount());
            azulene = new SpanningTree(azuleneMolecule);
        }
        if (ethane == null) {
            // create ethane
            IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
            IAtomContainer ethaneMolecule = builder.newInstance(IAtomContainer.class);
            ethaneMolecule.addAtom(builder.newInstance(IAtom.class, "C"));
            ethaneMolecule.addAtom(builder.newInstance(IAtom.class, "C"));
            ethaneMolecule.addBond(0, 1, IBond.Order.SINGLE);
            ethane = new SpanningTree(ethaneMolecule);
        }
    }

    @Test
    void testSpanningTree_IAtomContainer() {
        SpanningTree sTree = new SpanningTree(DefaultChemObjectBuilder.getInstance().newAtomContainer());
        Assertions.assertNotNull(sTree);
    }

    @Test
    void testGetCyclicFragmentsContainer() throws Exception {
        IAtomContainer ringSystems = azulene.getCyclicFragmentsContainer();
        Assertions.assertEquals(10, ringSystems.getAtomCount());
        Assertions.assertEquals(11, ringSystems.getBondCount());
    }

    @Test
    void testGetBondsCyclicCount() throws Exception {
        Assertions.assertEquals(11, azulene.getBondsCyclicCount());
        Assertions.assertEquals(0, ethane.getBondsCyclicCount());
    }

    @Test
    void testGetBondsAcyclicCount() throws Exception {
        Assertions.assertEquals(0, azulene.getBondsAcyclicCount());
        Assertions.assertEquals(1, ethane.getBondsAcyclicCount());
    }

    @Test
    void testGetPath_IAtomContainer_IAtom_IAtom() throws Exception {
        IAtomContainer ethaneMol = ethane.getSpanningTree();
        IAtomContainer path = ethane.getPath(ethaneMol, ethaneMol.getAtom(0), ethaneMol.getAtom(1));
        Assertions.assertEquals(2, path.getAtomCount());
        Assertions.assertEquals(1, path.getBondCount());

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer disconnectedStructure = builder.newInstance(IAtomContainer.class);
        disconnectedStructure.addAtom(builder.newInstance(IAtom.class, "Na"));
        disconnectedStructure.getAtom(0).setFormalCharge(+1);
        disconnectedStructure.addAtom(builder.newInstance(IAtom.class, "Cl"));
        disconnectedStructure.getAtom(1).setFormalCharge(-1);
        path = ethane
                .getPath(disconnectedStructure, disconnectedStructure.getAtom(0), disconnectedStructure.getAtom(1));
        Assertions.assertNotNull(path);
        Assertions.assertEquals(0, path.getAtomCount());
        Assertions.assertEquals(0, path.getBondCount());
    }

    @Test
    void testIsDisconnected() {
        Assertions.assertFalse(azulene.isDisconnected());

        IChemObjectBuilder builder = azulene.getSpanningTree().getBuilder();
        IAtomContainer disconnectedStructure = builder.newInstance(IAtomContainer.class);
        disconnectedStructure.addAtom(builder.newInstance(IAtom.class, "Na"));
        disconnectedStructure.getAtom(0).setFormalCharge(+1);
        disconnectedStructure.addAtom(builder.newInstance(IAtom.class, "Cl"));
        disconnectedStructure.getAtom(1).setFormalCharge(-1);
        SpanningTree stree = new SpanningTree(disconnectedStructure);
        Assertions.assertTrue(stree.isDisconnected());
    }

    @Test
    void testGetSpanningTree() {
        IAtomContainer container = azulene.getSpanningTree();
        Assertions.assertEquals(10, container.getAtomCount());
        Assertions.assertEquals(9, container.getBondCount()); // two rings to be broken to make a tree

        container = ethane.getSpanningTree();
        Assertions.assertEquals(2, container.getAtomCount());
        Assertions.assertEquals(1, container.getBondCount());
    }

    @Test
    void testGetBasicRings() throws Exception {
        IRingSet ringSet = azulene.getBasicRings();
        Assertions.assertEquals(2, ringSet.getAtomContainerCount());

        ringSet = ethane.getBasicRings();
        Assertions.assertEquals(0, ringSet.getAtomContainerCount());
    }

    @Test
    void testGetAllRings() throws Exception {
        IRingSet ringSet = azulene.getAllRings();
        Assertions.assertEquals(3, ringSet.getAtomContainerCount());

        ringSet = ethane.getAllRings();
        Assertions.assertEquals(0, ringSet.getAtomContainerCount());
    }

    @Test
    void testGetSpanningTreeSize() {
        Assertions.assertEquals(9, azulene.getSpanningTreeSize());
        Assertions.assertEquals(1, ethane.getSpanningTreeSize());
    }

    @Test
    void testGetSpanningTreeForPyridine() throws NoSuchAtomException {
        IAtomContainer mol = TestMoleculeFactory.makePyridine();
        SpanningTree spanningTree = new SpanningTree(mol);
        Assertions.assertEquals(6, spanningTree.getBondsCyclicCount());
        Assertions.assertEquals(6, spanningTree.getCyclicFragmentsContainer().getAtomCount());
        Assertions.assertEquals(0, spanningTree.getBondsAcyclicCount());
    }

}
