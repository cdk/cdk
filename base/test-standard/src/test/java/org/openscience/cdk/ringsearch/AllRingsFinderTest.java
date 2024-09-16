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
package org.openscience.cdk.ringsearch;

import java.io.InputStream;
import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Ring;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.ChemFile;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.ringsearch.AllRingsFinder.Threshold.PubChem_994;

/**
 * @cdk.module test-standard
 */
class AllRingsFinderTest extends CDKTestCase {

    private final boolean standAlone = false;

    AllRingsFinderTest() {
        super();
    }

    void setStandAlone(boolean standAlone) {
        // not-used
    }

    @Test
    void testAllRingsFinder() {
        AllRingsFinder arf = new AllRingsFinder();
        Assertions.assertNotNull(arf);
    }

    @Test
    void testFindAllRings_IAtomContainer() throws Exception {
        IRingSet ringSet;
        AllRingsFinder arf = new AllRingsFinder();
        IAtomContainer molecule = TestMoleculeFactory.makeEthylPropylPhenantren();
        //display(molecule);

        ringSet = arf.findAllRings(molecule);

        Assertions.assertEquals(6, ringSet.getAtomContainerCount());
    }

    /**
     * @cdk.bug 746067
     */
    @Test
    void testBondsWithinRing() throws Exception {
        IRingSet ringSet;
        AllRingsFinder arf = new AllRingsFinder();
        IAtomContainer molecule = TestMoleculeFactory.makeEthylPropylPhenantren();
        //display(molecule);

        ringSet = arf.findAllRings(molecule);
        for (int i = 0; i < ringSet.getAtomContainerCount(); i++) {
            Ring ring = (Ring) ringSet.getAtomContainer(i);
            for (int j = 0; j < ring.getBondCount(); j++) {
                IBond ec = ring.getBond(j);

                IAtom atom1 = ec.getBegin();
                IAtom atom2 = ec.getEnd();
                Assertions.assertTrue(ring.contains(atom1));
                Assertions.assertTrue(ring.contains(atom2));
            }
        }
    }

    @Test
    void testFindAllRings_IAtomContainer_boolean() throws Exception {
        AllRingsFinder arf = new AllRingsFinder();
        IAtomContainer molecule = TestMoleculeFactory.makeEthylPropylPhenantren();
        arf.findAllRings(molecule);
    }

    @Disabled("timeout not longer used")
    void testSetTimeout_long() throws Exception {
        AllRingsFinder arf = new AllRingsFinder();
        arf.setTimeout(1);
        IAtomContainer molecule = TestMoleculeFactory.makeEthylPropylPhenantren();
        arf.findAllRings(molecule);
    }

    @Disabled("timeout not longer used")
    void testCheckTimeout() throws Exception {
        AllRingsFinder arf = new AllRingsFinder();
        arf.setTimeout(3);
        arf.checkTimeout();
    }

    @Disabled("timeout not longer used")
    void testGetTimeout() {
        AllRingsFinder arf = new AllRingsFinder();
        arf.setTimeout(3);
        Assertions.assertEquals(3, arf.getTimeout(), 0.01);
    }

    @Test
    void testPorphyrine() throws Exception {
        IRingSet ringSet;
        AllRingsFinder arf = new AllRingsFinder();

        String filename = "porphyrin.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new org.openscience.cdk.ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer molecule = model.getMoleculeSet().getAtomContainer(0);

        ringSet = arf.findAllRings(molecule);
        Assertions.assertEquals(20, ringSet.getAtomContainerCount());
    }

    @Test
    void testBigRingSystem() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(500), () -> {
            Assumptions.assumeTrue(runSlowTests());

            IRingSet ringSet;
            AllRingsFinder arf = AllRingsFinder.usingThreshold(PubChem_994);

            String filename = "ring_03419.mol";
            InputStream ins = this.getClass().getResourceAsStream(filename);
            MDLV2000Reader reader = new MDLV2000Reader(ins);
            IChemFile chemFile = reader.read(new ChemFile());
            IChemSequence seq = chemFile.getChemSequence(0);
            IChemModel model = seq.getChemModel(0);
            IAtomContainer molecule = model.getMoleculeSet().getAtomContainer(0);

            ringSet = arf.findAllRings(molecule);
            // the 1976 value was empirically derived, and might not be accurate
            Assertions.assertEquals(1976, ringSet.getAtomContainerCount());
        });
    }

    @Test
    void testCholoylCoA() throws Exception {
        IRingSet ringSet;
        AllRingsFinder arf = new AllRingsFinder();

        String filename = "choloylcoa.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new org.openscience.cdk.ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer molecule = model.getMoleculeSet().getAtomContainer(0);

        ringSet = arf.findAllRings(molecule);
        Assertions.assertEquals(14, ringSet.getAtomContainerCount());
    }

    @Test
    void testAzulene() throws Exception {
        IRingSet ringSet;
        AllRingsFinder arf = new AllRingsFinder();

        String filename = "azulene.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new org.openscience.cdk.ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer molecule = model.getMoleculeSet().getAtomContainer(0);

        ringSet = arf.findAllRings(molecule);
        Assertions.assertEquals(3, ringSet.getAtomContainerCount());
    }

    /**
     * @cdk.inchi InChI=1S/C90H74O28/c91-48-18-8-36(28-58(48)101)81-64(107)34-35-4-3-6-43(80(35)114-81)66-44-14-24-55(98)72(87(44)115-83(75(66)108)38-10-20-50(93)60(103)30-38)68-46-16-26-57(100)74(89(46)117-85(77(68)110)40-12-22-52(95)62(105)32-40)70-47-17-27-56(99)73(90(47)118-86(79(70)112)41-13-23-53(96)63(106)33-41)69-45-15-25-54(97)71(88(45)116-84(78(69)111)39-11-21-51(94)61(104)31-39)67-42-5-1-2-7-65(42)113-82(76(67)109)37-9-19-49(92)59(102)29-37/h1-33,64,66-70,75-79,81-86,91-112H,34H2
     */
    @Test
    void testBigMoleculeWithIsolatedRings() throws Exception {
        IRingSet ringSet;
        AllRingsFinder arf = new AllRingsFinder();

        String filename = "isolated_ringsystems.cml";
        InputStream ins = this.getClass().getResourceAsStream(filename);

        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = reader.read(new org.openscience.cdk.ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);

        //logger.debug("Constructed Molecule");
        //logger.debug("Starting AllRingsFinder");
        ringSet = new AllRingsFinder().findAllRings(mol);
        //logger.debug("Finished AllRingsFinder");
        Assertions.assertEquals(24, ringSet.getAtomContainerCount());
        //display(mol);

        // check sizes of rings
        int[] ringSize = new int[mol.getAtomCount()];
        for (IAtomContainer ring : ringSet.atomContainers()) {
            ringSize[ring.getAtomCount()]++;
        }

        assertThat(ringSize[6], is(18));
        assertThat(ringSize[10], is(6));
    }

    /**
     * This test takes a very long time. It was to ensure that
     * AllRingsFinder actually stops for the given examples.
     * And it does, after a very long time.
     * So, the test is commented out because of its long runtime.
     *
     * @cdk.bug 777488
     */
    @Test
    void testBug777488() throws Exception {
        Assumptions.assumeTrue(runSlowTests());

        //String filename = "data/Bug646.cml";
        String filename = "testBug777488-1-AllRingsFinder.cml";
        //String filename = "data/NCI_diversity_528.mol.cml";
        //String filename = "data/NCI_diversity_978.mol.cml";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = reader.read(new org.openscience.cdk.ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        if (standAlone) System.out.println("Constructed Molecule");
        if (standAlone) System.out.println("Starting AllRingsFinder");
        IRingSet ringSet = new AllRingsFinder().findAllRings(mol);
        if (standAlone) System.out.println("Finished AllRingsFinder");
        if (standAlone) System.out.println("Found " + ringSet.getAtomContainerCount() + " rings.");

        //display(mol);
    }

    @Test
    void testRingFlags1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles("c1ccccc1");
        for (IAtom a : molecule.atoms())
            a.setFlag(IChemObject.IN_RING, false);
        AllRingsFinder arf = new AllRingsFinder();
        arf.findAllRings(molecule);

        int count = 0;
        for (IAtom atom : molecule.atoms()) {
            if (atom.getFlag(IChemObject.IN_RING)) count++;
        }
        Assertions.assertEquals(6, count, "All atoms in benzene were not marked as being in a ring");
    }

    @Test
    void testRingFlags2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles("C1CCCC1CC");
        for (IAtom a : molecule.atoms())
            a.setFlag(IChemObject.IN_RING, false);

        AllRingsFinder arf = new AllRingsFinder();
        arf.findAllRings(molecule);

        int count = 0;
        for (IAtom atom : molecule.atoms()) {
            if (atom.getFlag(IChemObject.IN_RING)) count++;
        }
        Assertions.assertEquals(5, count, "All atoms in 1-ethyl-cyclopentane were not marked as being in a ring");
    }

    @Test
    void testBigRingSystem_MaxRingSize6_03419() throws Exception {
        IRingSet ringSet;
        AllRingsFinder arf = new AllRingsFinder();
        String filename = "ring_03419.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer molecule = model.getMoleculeSet().getAtomContainer(0);
        ringSet = arf.findAllRings(molecule, 6);
        Assertions.assertEquals(12, ringSet.getAtomContainerCount());
    }

    @Test
    void testBigRingSystem_MaxRingSize4_fourRing5x10() throws Exception {
        IRingSet ringSet;
        AllRingsFinder arf = new AllRingsFinder();
        String filename = "four-ring-5x10.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer molecule = model.getMoleculeSet().getAtomContainer(0);
        // there are 5x10 squares (four-rings) in the 5x10 molecule
        ringSet = arf.findAllRings(molecule, 4);
        Assertions.assertEquals(50, ringSet.getAtomContainerCount());
    }

    @Test
    void testBigRingSystem_MaxRingSize6_fourRing5x10() throws Exception {
        IRingSet ringSet;
        AllRingsFinder arf = new AllRingsFinder();
        String filename = "four-ring-5x10.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer molecule = model.getMoleculeSet().getAtomContainer(0);
        // there are 5x10 four-rings (squares ) = 50
        // there are (9x5) + (4x10) six-rings   = 85
        // combined 135
        ringSet = arf.findAllRings(molecule, 6);
        Assertions.assertEquals(135, ringSet.getAtomContainerCount());
    }

}
