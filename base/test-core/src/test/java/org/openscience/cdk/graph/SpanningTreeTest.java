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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
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
 * @cdk.module test-core
 */
public class SpanningTreeTest extends CDKTestCase {

    private static SpanningTree azulene = null;
    private static SpanningTree ethane  = null;

    @Before
    public void setUp() throws Exception {
        if (azulene == null) {
            // load azulene
            String filename = "data/mdl/azulene.mol";
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
            MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
            IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
            IChemSequence seq = chemFile.getChemSequence(0);
            IChemModel model = seq.getChemModel(0);
            IAtomContainer azuleneMolecule = model.getMoleculeSet().getAtomContainer(0);
            Assert.assertEquals(10, azuleneMolecule.getAtomCount());
            Assert.assertEquals(11, azuleneMolecule.getBondCount());
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
    public void testSpanningTree_IAtomContainer() {
        SpanningTree sTree = new SpanningTree(new AtomContainer());
        Assert.assertNotNull(sTree);
    }

    @Test
    public void testGetCyclicFragmentsContainer() throws Exception {
        IAtomContainer ringSystems = azulene.getCyclicFragmentsContainer();
        Assert.assertEquals(10, ringSystems.getAtomCount());
        Assert.assertEquals(11, ringSystems.getBondCount());
    }

    @Test
    public void testGetBondsCyclicCount() throws Exception {
        Assert.assertEquals(11, azulene.getBondsCyclicCount());
        Assert.assertEquals(0, ethane.getBondsCyclicCount());
    }

    @Test
    public void testGetBondsAcyclicCount() throws Exception {
        Assert.assertEquals(0, azulene.getBondsAcyclicCount());
        Assert.assertEquals(1, ethane.getBondsAcyclicCount());
    }

    @Test
    public void testGetPath_IAtomContainer_IAtom_IAtom() throws Exception {
        IAtomContainer ethaneMol = ethane.getSpanningTree();
        IAtomContainer path = ethane.getPath(ethaneMol, ethaneMol.getAtom(0), ethaneMol.getAtom(1));
        Assert.assertEquals(2, path.getAtomCount());
        Assert.assertEquals(1, path.getBondCount());

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer disconnectedStructure = builder.newInstance(IAtomContainer.class);
        disconnectedStructure.addAtom(builder.newInstance(IAtom.class, "Na"));
        disconnectedStructure.getAtom(0).setFormalCharge(+1);
        disconnectedStructure.addAtom(builder.newInstance(IAtom.class, "Cl"));
        disconnectedStructure.getAtom(1).setFormalCharge(-1);
        path = ethane
                .getPath(disconnectedStructure, disconnectedStructure.getAtom(0), disconnectedStructure.getAtom(1));
        Assert.assertNotNull(path);
        Assert.assertEquals(0, path.getAtomCount());
        Assert.assertEquals(0, path.getBondCount());
    }

    @Test
    public void testIsDisconnected() {
        Assert.assertFalse(azulene.isDisconnected());

        IChemObjectBuilder builder = azulene.getSpanningTree().getBuilder();
        IAtomContainer disconnectedStructure = builder.newInstance(IAtomContainer.class);
        disconnectedStructure.addAtom(builder.newInstance(IAtom.class, "Na"));
        disconnectedStructure.getAtom(0).setFormalCharge(+1);
        disconnectedStructure.addAtom(builder.newInstance(IAtom.class, "Cl"));
        disconnectedStructure.getAtom(1).setFormalCharge(-1);
        SpanningTree stree = new SpanningTree(disconnectedStructure);
        Assert.assertTrue(stree.isDisconnected());
    }

    @Test
    public void testGetSpanningTree() {
        IAtomContainer container = azulene.getSpanningTree();
        Assert.assertEquals(10, container.getAtomCount());
        Assert.assertEquals(9, container.getBondCount()); // two rings to be broken to make a tree

        container = ethane.getSpanningTree();
        Assert.assertEquals(2, container.getAtomCount());
        Assert.assertEquals(1, container.getBondCount());
    }

    @Test
    public void testGetBasicRings() throws Exception {
        IRingSet ringSet = azulene.getBasicRings();
        Assert.assertEquals(2, ringSet.getAtomContainerCount());

        ringSet = ethane.getBasicRings();
        Assert.assertEquals(0, ringSet.getAtomContainerCount());
    }

    @Test
    public void testGetAllRings() throws Exception {
        IRingSet ringSet = azulene.getAllRings();
        Assert.assertEquals(3, ringSet.getAtomContainerCount());

        ringSet = ethane.getAllRings();
        Assert.assertEquals(0, ringSet.getAtomContainerCount());
    }

    @Test
    public void testGetSpanningTreeSize() {
        Assert.assertEquals(9, azulene.getSpanningTreeSize());
        Assert.assertEquals(1, ethane.getSpanningTreeSize());
    }

    @Test
    public void testGetSpanningTreeForPyridine() throws NoSuchAtomException {
        IAtomContainer mol = TestMoleculeFactory.makePyridine();
        SpanningTree spanningTree = new SpanningTree(mol);
        Assert.assertEquals(6, spanningTree.getBondsCyclicCount());
        Assert.assertEquals(6, spanningTree.getCyclicFragmentsContainer().getAtomCount());
        Assert.assertEquals(0, spanningTree.getBondsAcyclicCount());
    }

}
