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
package org.openscience.cdk.graph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.LonePair;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Checks the functionality of the ConnectivityChecker
 *
 * @author steinbeck
 * @cdk.created 2001-07-24
 */
class ConnectivityCheckerTest extends CDKTestCase {

    ConnectivityCheckerTest() {
        super();
    }

    /**
     * This test tests the function of the partitionIntoMolecule() method.
     */
    @Test
    void testPartitionIntoMolecules_IAtomContainer() {
        //logger.debug(atomCon);
        IAtomContainer atomCon = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atomCon.add(TestMoleculeFactory.make4x3CondensedRings());
        atomCon.add(TestMoleculeFactory.makeAlphaPinene());
        atomCon.add(TestMoleculeFactory.makeSpiroRings());
        IAtomContainerSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(atomCon);
        Assertions.assertNotNull(moleculeSet);
        Assertions.assertEquals(3, moleculeSet.getAtomContainerCount());
    }

    /**
     * Test for SF bug #903551
     */
    @Test
    void testPartitionIntoMoleculesKeepsAtomIDs() {
        IAtomContainer atomCon = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom1 = new Atom("C");
        atom1.setID("atom1");
        Atom atom2 = new Atom("C");
        atom2.setID("atom2");
        atomCon.addAtom(atom1);
        atomCon.addAtom(atom2);
        IAtomContainerSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(atomCon);
        Assertions.assertNotNull(moleculeSet);
        Assertions.assertEquals(2, moleculeSet.getAtomContainerCount());
        org.openscience.cdk.interfaces.IAtom copy1 = moleculeSet.getAtomContainer(0).getAtom(0);
        org.openscience.cdk.interfaces.IAtom copy2 = moleculeSet.getAtomContainer(1).getAtom(0);

        Assertions.assertEquals(atom1.getID(), copy1.getID());
        Assertions.assertEquals(atom2.getID(), copy2.getID());
    }

    /**
     * This test tests the consistency between isConnected() and
     * partitionIntoMolecules().
     */
    @Test
    void testPartitionIntoMolecules_IsConnected_Consistency() {
        //logger.debug(atomCon);
        IAtomContainer atomCon = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atomCon.add(TestMoleculeFactory.make4x3CondensedRings());
        atomCon.add(TestMoleculeFactory.makeAlphaPinene());
        atomCon.add(TestMoleculeFactory.makeSpiroRings());
        IAtomContainerSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(atomCon);
        Assertions.assertNotNull(moleculeSet);
        Assertions.assertEquals(3, moleculeSet.getAtomContainerCount());

        Assertions.assertTrue(ConnectivityChecker.isConnected(moleculeSet.getAtomContainer(0)));
        Assertions.assertTrue(ConnectivityChecker.isConnected(moleculeSet.getAtomContainer(1)));
        Assertions.assertTrue(ConnectivityChecker.isConnected(moleculeSet.getAtomContainer(2)));
    }

    /**
     * This test makes sure that it is checked that the partitionIntoMolecules()
     * method keeps LonePairs and SingleElectrons with its associated atoms.
     */
    @Test
    void testDontDeleteSingleElectrons() {
        IAtomContainer atomCon = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        // make two molecules; one with an LonePair, the other with a SingleElectron
        IAtomContainer mol1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom1 = new Atom("C");
        mol1.addAtom(atom1);
        LonePair lp1 = new LonePair(atom1);
        mol1.addLonePair(lp1);
        // mol2
        IAtomContainer mol2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom2 = new Atom("C");
        mol2.addAtom(atom2);
        SingleElectron se2 = new SingleElectron(atom2);
        mol2.addSingleElectron(se2);

        atomCon.add(mol1);
        atomCon.add(mol2);

        // now partition
        IAtomContainerSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(atomCon);
        Assertions.assertNotNull(moleculeSet);
        Assertions.assertEquals(2, moleculeSet.getAtomContainerCount());

        Assertions.assertTrue(ConnectivityChecker.isConnected(moleculeSet.getAtomContainer(0)));
        Assertions.assertTrue(ConnectivityChecker.isConnected(moleculeSet.getAtomContainer(1)));

        // make sure
        Assertions.assertEquals(1, moleculeSet.getAtomContainer(0).getAtomCount());
        Assertions.assertEquals(1, moleculeSet.getAtomContainer(0).getElectronContainerCount());
        Assertions.assertEquals(1, moleculeSet.getAtomContainer(1).getAtomCount());
        Assertions.assertEquals(1, moleculeSet.getAtomContainer(1).getElectronContainerCount());
        // we don't know which partition contains the LP and which the electron
        Assertions.assertTrue(moleculeSet.getAtomContainer(0).getConnectedSingleElectronsCount(
                moleculeSet.getAtomContainer(0).getAtom(0)) == 0
                || moleculeSet.getAtomContainer(1).getConnectedSingleElectronsCount(
                moleculeSet.getAtomContainer(1).getAtom(0)) == 0);
        Assertions.assertTrue(moleculeSet.getAtomContainer(0).getConnectedLonePairsCount(
                moleculeSet.getAtomContainer(0).getAtom(0)) == 0
                || moleculeSet.getAtomContainer(1).getConnectedLonePairsCount(
                moleculeSet.getAtomContainer(1).getAtom(0)) == 0);
    }

    /**
     * This test tests the algorithm behind isConnected().
     */
    @Test
    void testIsConnected_IAtomContainer() {
        IAtomContainer spiro = TestMoleculeFactory.makeSpiroRings();
        Assertions.assertTrue(ConnectivityChecker.isConnected(spiro));
    }

    @Test
    void testIsConnectedArtemisinin1() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = sp.parseSmiles("C1CN2CCN(CCCN(CCN(C1)Cc1ccccn1)CC2)C");
        Assertions.assertTrue(ConnectivityChecker.isConnected(container));
    }

    /**
     * @cdk.bug 2126904
     */
    @Test
    void testIsConnectedFromHINFile() throws Exception {
        // connectivity1.hin
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer m = builder.newAtomContainer();
        IAtom a1 = m.newAtom(IElement.C);
        IAtom a2 = m.newAtom(IElement.C);
        IAtom a3 = m.newAtom(IElement.N);
        IAtom a4 = m.newAtom(IElement.C);
        IAtom a5 = m.newAtom(IElement.C);
        IAtom a6 = m.newAtom(IElement.N);
        IAtom a7 = m.newAtom(IElement.C);
        IAtom a8 = m.newAtom(IElement.C);
        IAtom a9 = m.newAtom(IElement.C);
        IAtom a10 = m.newAtom(IElement.N);
        IAtom a11 = m.newAtom(IElement.C);
        IAtom a12 = m.newAtom(IElement.C);
        IAtom a13 = m.newAtom(IElement.N);
        IAtom a14 = m.newAtom(IElement.C);
        IAtom a15 = m.newAtom(IElement.C);
        IAtom a16 = m.newAtom(IElement.C);
        IAtom a17 = m.newAtom(IElement.C);
        IAtom a18 = m.newAtom(IElement.C);
        IAtom a19 = m.newAtom(IElement.C);
        IAtom a20 = m.newAtom(IElement.C);
        IAtom a21 = m.newAtom(IElement.C);
        IAtom a22 = m.newAtom(IElement.C);
        IAtom a23 = m.newAtom(IElement.C);
        IAtom a24 = m.newAtom(IElement.N);
        IAtom a25 = m.newAtom(IElement.H);
        IAtom a26 = m.newAtom(IElement.H);
        IAtom a27 = m.newAtom(IElement.H);
        IAtom a28 = m.newAtom(IElement.H);
        IAtom a29 = m.newAtom(IElement.H);
        IAtom a30 = m.newAtom(IElement.H);
        IAtom a31 = m.newAtom(IElement.H);
        IAtom a32 = m.newAtom(IElement.H);
        IAtom a33 = m.newAtom(IElement.H);
        IAtom a34 = m.newAtom(IElement.H);
        IAtom a35 = m.newAtom(IElement.H);
        IAtom a36 = m.newAtom(IElement.H);
        IAtom a37 = m.newAtom(IElement.H);
        IAtom a38 = m.newAtom(IElement.H);
        IAtom a39 = m.newAtom(IElement.H);
        IAtom a40 = m.newAtom(IElement.H);
        IAtom a41 = m.newAtom(IElement.H);
        IAtom a42 = m.newAtom(IElement.H);
        IAtom a43 = m.newAtom(IElement.H);
        IAtom a44 = m.newAtom(IElement.H);
        IAtom a45 = m.newAtom(IElement.H);
        IAtom a46 = m.newAtom(IElement.H);
        IAtom a47 = m.newAtom(IElement.H);
        IAtom a48 = m.newAtom(IElement.H);
        IAtom a49 = m.newAtom(IElement.H);
        IAtom a50 = m.newAtom(IElement.H);
        IAtom a51 = m.newAtom(IElement.H);
        IAtom a52 = m.newAtom(IElement.H);
        IAtom a53 = m.newAtom(IElement.H);
        IAtom a54 = m.newAtom(IElement.H);
        IAtom a55 = m.newAtom(IElement.H);
        IAtom a56 = m.newAtom(IElement.H);
        IAtom a57 = m.newAtom(IElement.H);
        m.newBond(a1, a2);
        m.newBond(a1, a14);
        m.newBond(a1, a25);
        m.newBond(a1, a26);
        m.newBond(a2, a3);
        m.newBond(a2, a27);
        m.newBond(a2, a28);
        m.newBond(a3, a4);
        m.newBond(a3, a15);
        m.newBond(a4, a5);
        m.newBond(a4, a29);
        m.newBond(a4, a30);
        m.newBond(a5, a6);
        m.newBond(a5, a31);
        m.newBond(a5, a32);
        m.newBond(a6, a7);
        m.newBond(a6, a17);
        m.newBond(a7, a8);
        m.newBond(a7, a33);
        m.newBond(a7, a34);
        m.newBond(a8, a9);
        m.newBond(a8, a35);
        m.newBond(a8, a36);
        m.newBond(a9, a10);
        m.newBond(a9, a37);
        m.newBond(a9, a38);
        m.newBond(a10, a11);
        m.newBond(a10, a16);
        m.newBond(a11, a12);
        m.newBond(a11, a39);
        m.newBond(a11, a40);
        m.newBond(a12, a13);
        m.newBond(a12, a41);
        m.newBond(a12, a42);
        m.newBond(a13, a14);
        m.newBond(a13, a18);
        m.newBond(a14, a43);
        m.newBond(a14, a44);
        m.newBond(a15, a16);
        m.newBond(a15, a45);
        m.newBond(a15, a46);
        m.newBond(a16, a47);
        m.newBond(a16, a48);
        m.newBond(a17, a49);
        m.newBond(a17, a50);
        m.newBond(a17, a51);
        m.newBond(a18, a19);
        m.newBond(a18, a52);
        m.newBond(a18, a53);
        m.newBond(a19, a20, IBond.Order.QUADRUPLE);
        m.newBond(a19, a24, IBond.Order.QUADRUPLE);
        m.newBond(a20, a21, IBond.Order.QUADRUPLE);
        m.newBond(a20, a54);
        m.newBond(a21, a22, IBond.Order.QUADRUPLE);
        m.newBond(a21, a55);
        m.newBond(a22, a23, IBond.Order.QUADRUPLE);
        m.newBond(a22, a56);
        m.newBond(a23, a24, IBond.Order.QUADRUPLE);
        m.newBond(a23, a57);
        Assertions.assertTrue(ConnectivityChecker.isConnected(m),
                              "Molecule appears not to be connected");
    }

    /**
     * @cdk.bug 2126904
     */
    @Test
    void testIsConnectedFromSDFile() throws Exception {
        String filename = "mdeotest.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = cList.get(0);

        Assertions.assertTrue(ConnectivityChecker.isConnected(ac), "Molecule appears not to be connected");
    }

    @Test
    void testPartitionExtendedTetrahedral() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = smipar.parseSmiles("CC=[C@]=CC.C");
        IAtomContainerSet containerSet = ConnectivityChecker.partitionIntoMolecules(container);
        assertThat(containerSet.getAtomContainerCount(), is(2));
        Assertions.assertTrue(containerSet.getAtomContainer(0).stereoElements().iterator().hasNext());
    }

    /**
     * @cdk.bug 2784209
     */
    @Test
    void testNoAtomsIsConnected() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(ConnectivityChecker.isConnected(container), "Molecule appears not to be connected");
    }

    @Test
    void copySgroups() throws Exception {
        String filename = "sgroup-split.mol";
        try (InputStream ins = this.getClass().getResourceAsStream(filename);
             ISimpleChemObjectReader reader = new MDLV2000Reader(ins)
        ) {
            ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
            List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
            IAtomContainer ac = cList.get(0);
            IAtomContainerSet containerSet = ConnectivityChecker.partitionIntoMolecules(ac);
            Assertions.assertEquals(2, containerSet.getAtomContainerCount());
            IAtomContainer container1 = containerSet.getAtomContainer(0);
            IAtomContainer container2 = containerSet.getAtomContainer(1);
            IAtomContainer h2o = container1.getAtomCount() <= 3 ? container1 : container2;
            Assertions.assertNull(h2o.getProperty(CDKConstants.CTAB_SGROUPS));
            IAtomContainer otherContainer = h2o == container1 ? container2 : container1;
            List<Sgroup> sgroups = otherContainer.getProperty(CDKConstants.CTAB_SGROUPS);
            Assertions.assertEquals(1, sgroups.size());
            Sgroup sgroup = sgroups.get(0);
            Assertions.assertEquals(SgroupType.CtabStructureRepeatUnit, sgroup.getType());
            Set<IAtom> atoms = sgroup.getAtoms();
            Assertions.assertEquals(2, atoms.size());

        }
    }

    @Test
    void splitSgroups() throws IOException, CDKException {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        try (InputStream in = getClass().getResourceAsStream("sgroup-frags.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {
            IAtomContainer mol = mdlr.read(builder.newAtomContainer());
            IAtomContainerSet acset = ConnectivityChecker.partitionIntoMolecules(mol);
            Assertions.assertEquals(2, acset.getAtomContainerCount());
            SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.CxSmiles);
            Assertions.assertEquals("CCOC |Sg:n:1,2:1:|", smigen.create(acset.getAtomContainer(0)));
            Assertions.assertEquals("C(NC)C |Sg:n:0,1:1:|", smigen.create(acset.getAtomContainer(1)));
        }
    }

    @Test
    void splitSgroupsParent() throws IOException, CDKException {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        try (InputStream in = getClass().getResourceAsStream("sgroup-mix.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {
            IAtomContainer mol = mdlr.read(builder.newAtomContainer());
            IAtomContainerSet acset = ConnectivityChecker.partitionIntoMolecules(mol);
            Assertions.assertEquals(2, acset.getAtomContainerCount());
            SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.CxSmiles);
            IAtomContainer part1 = acset.getAtomContainer(0);
            IAtomContainer part2 = acset.getAtomContainer(1);
            Assertions.assertEquals("C1CCCCC1 |Sg:c:0,1,2,3,4,5::|", smigen.create(part1));
            Assertions.assertEquals("CO |Sg:c:0,1::|", smigen.create(part2));
            List<Sgroup> sgroups1 = part1.getProperty(CDKConstants.CTAB_SGROUPS);
            List<Sgroup> sgroups2 = part2.getProperty(CDKConstants.CTAB_SGROUPS);
            Assertions.assertEquals(1, sgroups1.size());
            Assertions.assertEquals(0, sgroups1.get(0).getParents().size());
            Assertions.assertEquals(1, sgroups2.size());
            Assertions.assertEquals(0, sgroups2.get(0).getParents().size());
            List<Sgroup> orgSgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
            assertThat(orgSgroups.size(), is(3));
            assertNonEmptySgrpParent(orgSgroups);
        }
    }

    // check at least one of the sgroups has a non-empty parent
    private void assertNonEmptySgrpParent(List<Sgroup> orgSgroups) {
        boolean found = false;
        for (Sgroup orgSgroup : orgSgroups) {
            if (!orgSgroup.getParents().isEmpty())
                found = true;
        }
        Assertions.assertTrue(found);
    }

}
