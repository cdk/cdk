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
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
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
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.ChemFile;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.ringsearch.AllRingsFinder.Threshold.PubChem_994;

/**
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

        // testBug777488-1-AllRingsFinder.cml
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer m = builder.newAtomContainer();
        IAtom a1 = m.newAtom(IElement.C);
        IAtom a2 = m.newAtom(IElement.C);
        IAtom a3 = m.newAtom(IElement.C, 1);
        IAtom a4 = m.newAtom(IElement.C);
        IAtom a5 = m.newAtom(IElement.O);
        IAtom a6 = m.newAtom(IElement.C);
        IAtom a8 = m.newAtom(IElement.C);
        IAtom a9 = m.newAtom(IElement.C);
        IAtom a10 = m.newAtom(IElement.C);
        IAtom a11 = m.newAtom(IElement.C);
        IAtom a13 = m.newAtom(IElement.O);
        IAtom a14 = m.newAtom(IElement.C);
        IAtom a15 = m.newAtom(IElement.C);
        IAtom a16 = m.newAtom(IElement.C);
        IAtom a17 = m.newAtom(IElement.C);
        IAtom a18 = m.newAtom(IElement.C);
        IAtom a19 = m.newAtom(IElement.C);
        IAtom a20 = m.newAtom(IElement.O);
        IAtom a21 = m.newAtom(IElement.O);
        IAtom a22 = m.newAtom(IElement.C);
        IAtom a23 = m.newAtom(IElement.C);
        IAtom a24 = m.newAtom(IElement.C);
        IAtom a25 = m.newAtom(IElement.C);
        IAtom a26 = m.newAtom(IElement.O);
        IAtom a27 = m.newAtom(IElement.C);
        IAtom a28 = m.newAtom(IElement.O);
        IAtom a29 = m.newAtom(IElement.C);
        IAtom a30 = m.newAtom(IElement.C);
        IAtom a31 = m.newAtom(IElement.C);
        IAtom a32 = m.newAtom(IElement.C, 2);
        IAtom a34 = m.newAtom(IElement.O, 1);
        IAtom a35 = m.newAtom(IElement.C);
        IAtom a36 = m.newAtom(IElement.C);
        IAtom a37 = m.newAtom(IElement.C);
        IAtom a38 = m.newAtom(IElement.C);
        IAtom a39 = m.newAtom(IElement.C);
        IAtom a40 = m.newAtom(IElement.C);
        IAtom a41 = m.newAtom(IElement.O);
        IAtom a42 = m.newAtom(IElement.O);
        IAtom a43 = m.newAtom(IElement.C);
        IAtom a44 = m.newAtom(IElement.C, 1);
        IAtom a45 = m.newAtom(IElement.C);
        IAtom a46 = m.newAtom(IElement.C);
        IAtom a47 = m.newAtom(IElement.O);
        IAtom a48 = m.newAtom(IElement.C);
        IAtom a49 = m.newAtom(IElement.O);
        IAtom a50 = m.newAtom(IElement.C);
        IAtom a51 = m.newAtom(IElement.C);
        IAtom a52 = m.newAtom(IElement.C);
        IAtom a53 = m.newAtom(IElement.C);
        IAtom a55 = m.newAtom(IElement.O);
        IAtom a56 = m.newAtom(IElement.C);
        IAtom a57 = m.newAtom(IElement.C);
        IAtom a58 = m.newAtom(IElement.C);
        IAtom a59 = m.newAtom(IElement.C);
        IAtom a60 = m.newAtom(IElement.C);
        IAtom a61 = m.newAtom(IElement.C);
        IAtom a62 = m.newAtom(IElement.O);
        IAtom a63 = m.newAtom(IElement.O);
        IAtom a64 = m.newAtom(IElement.C);
        IAtom a65 = m.newAtom(IElement.C);
        IAtom a66 = m.newAtom(IElement.C);
        IAtom a67 = m.newAtom(IElement.C, 1);
        IAtom a68 = m.newAtom(IElement.C);
        IAtom a69 = m.newAtom(IElement.C);
        IAtom a70 = m.newAtom(IElement.O);
        IAtom a71 = m.newAtom(IElement.C);
        IAtom a72 = m.newAtom(IElement.O);
        IAtom a73 = m.newAtom(IElement.C);
        IAtom a75 = m.newAtom(IElement.C);
        IAtom a76 = m.newAtom(IElement.O);
        IAtom a77 = m.newAtom(IElement.C);
        IAtom a78 = m.newAtom(IElement.C);
        IAtom a79 = m.newAtom(IElement.C);
        IAtom a80 = m.newAtom(IElement.C);
        IAtom a81 = m.newAtom(IElement.C);
        IAtom a82 = m.newAtom(IElement.C);
        IAtom a83 = m.newAtom(IElement.O);
        IAtom a84 = m.newAtom(IElement.O);
        IAtom a85 = m.newAtom(IElement.C);
        IAtom a86 = m.newAtom(IElement.C);
        IAtom a87 = m.newAtom(IElement.C);
        IAtom a88 = m.newAtom(IElement.C);
        IAtom a89 = m.newAtom(IElement.C, 1);
        IAtom a90 = m.newAtom(IElement.C);
        IAtom a91 = m.newAtom(IElement.O);
        IAtom a92 = m.newAtom(IElement.C);
        IAtom a93 = m.newAtom(IElement.O);
        IAtom a94 = m.newAtom(IElement.C);
        IAtom a96 = m.newAtom(IElement.C);
        IAtom a97 = m.newAtom(IElement.O);
        IAtom a98 = m.newAtom(IElement.C);
        IAtom a99 = m.newAtom(IElement.C, 2);
        IAtom a100 = m.newAtom(IElement.C);
        IAtom a101 = m.newAtom(IElement.C);
        IAtom a102 = m.newAtom(IElement.C);
        IAtom a103 = m.newAtom(IElement.C);
        IAtom a104 = m.newAtom(IElement.O);
        IAtom a105 = m.newAtom(IElement.O);
        IAtom a106 = m.newAtom(IElement.C);
        IAtom a107 = m.newAtom(IElement.C);
        IAtom a108 = m.newAtom(IElement.C);
        IAtom a109 = m.newAtom(IElement.C);
        IAtom a110 = m.newAtom(IElement.C);
        IAtom a111 = m.newAtom(IElement.C, 2);
        IAtom a112 = m.newAtom(IElement.O);
        IAtom a113 = m.newAtom(IElement.O);
        IAtom a114 = m.newAtom(IElement.C);
        IAtom a115 = m.newAtom(IElement.C, 1);
        IAtom a117 = m.newAtom(IElement.C);
        IAtom a119 = m.newAtom(IElement.C);
        IAtom a120 = m.newAtom(IElement.C);
        IAtom a121 = m.newAtom(IElement.C);
        IAtom a122 = m.newAtom(IElement.C);
        IAtom a123 = m.newAtom(IElement.C);
        IAtom a124 = m.newAtom(IElement.C);
        IAtom a125 = m.newAtom(IElement.O);
        IAtom a126 = m.newAtom(IElement.O);
        m.newBond(a56, a58, IBond.Order.DOUBLE);
        m.newBond(a57, a59, IBond.Order.DOUBLE);
        m.newBond(a58, a60);
        m.newBond(a59, a61);
        m.newBond(a59, a62);
        m.newBond(a61, a63);
        m.newBond(a60, a61, IBond.Order.DOUBLE);
        m.newBond(a24, a28);
        m.newBond(a29, a25);
        m.newBond(a25, a30, IBond.Order.DOUBLE);
        m.newBond(a31, a26);
        m.newBond(a32, a29);
        m.newBond(a27, a30);
        m.newBond(a32, a31);
        m.newBond(a3, a6, IBond.Order.DOUBLE);
        m.newBond(a31, a35);
        m.newBond(a4, a8);
        m.newBond(a64, a65);
        m.newBond(a66, a64);
        m.newBond(a65, a67, IBond.Order.DOUBLE);
        m.newBond(a65, a68);
        m.newBond(a66, a69);
        m.newBond(a66, a70);
        m.newBond(a67, a71);
        m.newBond(a68, a73, IBond.Order.DOUBLE);
        m.newBond(a71, a75, IBond.Order.DOUBLE);
        m.newBond(a75, a76);
        m.newBond(a69, a72);
        m.newBond(a73, a75);
        m.newBond(a14, a15, IBond.Order.DOUBLE);
        m.newBond(a69, a77);
        m.newBond(a14, a16);
        m.newBond(a15, a17);
        m.newBond(a16, a18, IBond.Order.DOUBLE);
        m.newBond(a17, a19, IBond.Order.DOUBLE);
        m.newBond(a35, a36, IBond.Order.DOUBLE);
        m.newBond(a35, a37);
        m.newBond(a36, a38);
        m.newBond(a77, a78, IBond.Order.DOUBLE);
        m.newBond(a77, a79);
        m.newBond(a78, a80);
        m.newBond(a79, a81, IBond.Order.DOUBLE);
        m.newBond(a80, a82, IBond.Order.DOUBLE);
        m.newBond(a80, a83);
        m.newBond(a82, a84);
        m.newBond(a81, a82);
        m.newBond(a37, a39, IBond.Order.DOUBLE);
        m.newBond(a38, a40, IBond.Order.DOUBLE);
        m.newBond(a38, a41);
        m.newBond(a40, a42);
        m.newBond(a39, a40);
        m.newBond(a17, a20);
        m.newBond(a19, a21);
        m.newBond(a18, a19);
        m.newBond(a4, a9, IBond.Order.DOUBLE);
        m.newBond(a10, a5);
        m.newBond(a11, a8);
        m.newBond(a11, a13);
        m.newBond(a85, a86);
        m.newBond(a87, a85);
        m.newBond(a86, a88, IBond.Order.DOUBLE);
        m.newBond(a87, a90);
        m.newBond(a87, a91);
        m.newBond(a88, a92);
        m.newBond(a88, a93);
        m.newBond(a89, a94, IBond.Order.DOUBLE);
        m.newBond(a96, a97);
        m.newBond(a90, a93);
        m.newBond(a94, a96);
        m.newBond(a6, a9);
        m.newBond(a90, a98);
        m.newBond(a10, a11);
        m.newBond(a10, a14);
        m.newBond(a1, a2, IBond.Order.DOUBLE);
        m.newBond(a43, a44, IBond.Order.DOUBLE);
        m.newBond(a43, a45);
        m.newBond(a98, a99, IBond.Order.DOUBLE);
        m.newBond(a98, a100);
        m.newBond(a100, a102, IBond.Order.DOUBLE);
        m.newBond(a101, a103, IBond.Order.DOUBLE);
        m.newBond(a101, a104);
        m.newBond(a103, a105);
        m.newBond(a102, a103);
        m.newBond(a44, a47);
        m.newBond(a45, a48, IBond.Order.DOUBLE);
        m.newBond(a45, a49);
        m.newBond(a50, a46);
        m.newBond(a46, a51, IBond.Order.DOUBLE);
        m.newBond(a52, a47);
        m.newBond(a53, a50);
        m.newBond(a53, a55);
        m.newBond(a48, a51);
        m.newBond(a53, a52);
        m.newBond(a1, a3);
        m.newBond(a52, a56);
        m.newBond(a106, a107);
        m.newBond(a108, a106);
        m.newBond(a107, a109, IBond.Order.DOUBLE);
        m.newBond(a108, a111);
        m.newBond(a108, a112);
        m.newBond(a109, a113);
        m.newBond(a109, a114);
        m.newBond(a114, a117, IBond.Order.DOUBLE);
        m.newBond(a111, a113);
        m.newBond(a115, a117);
        m.newBond(a2, a4);
        m.newBond(a2, a5);
        m.newBond(a22, a23, IBond.Order.DOUBLE);
        m.newBond(a22, a24);
        m.newBond(a23, a25);
        m.newBond(a23, a26);
        m.newBond(a24, a27, IBond.Order.DOUBLE);
        m.newBond(a56, a57);
        m.newBond(a119, a120, IBond.Order.DOUBLE);
        m.newBond(a119, a121);
        m.newBond(a120, a122);
        m.newBond(a121, a123, IBond.Order.DOUBLE);
        m.newBond(a122, a124, IBond.Order.DOUBLE);
        m.newBond(a122, a125);
        m.newBond(a124, a126);
        m.newBond(a123, a124);
        m.newBond(a106, a92);
        m.newBond(a85, a71);
        m.newBond(a64, a43);
        m.newBond(a50, a22);
        m.newBond(a29, a1);
        m.newBond(a115, a110, IBond.Order.DOUBLE);
        m.newBond(a110, a107);
        m.newBond(a96, a92, IBond.Order.DOUBLE);
        m.newBond(a89, a86);
        m.newBond(a99, a101);
        m.newBond(a44, a46);
        m.newBond(a67, a72);
        m.newBond(a111, a119);
        m.newBond(a32, a34);

        ringSet = new AllRingsFinder().findAllRings(m);
        //logger.debug("Finished AllRingsFinder");
        Assertions.assertEquals(24, ringSet.getAtomContainerCount());
        //display(mol);

        // check sizes of rings
        int[] ringSize = new int[m.getAtomCount()];
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
        // file: testBug777488-1-AllRingsFinder.cml
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer m = builder.newAtomContainer();
        IAtom a8540106 = m.newAtom(IElement.N);
        IAtom a21257520 = m.newAtom(IElement.C);
        IAtom a21109443 = m.newAtom(IElement.C);
        IAtom a15403331 = m.newAtom(IElement.C);
        IAtom a11357376 = m.newAtom(IElement.C);
        IAtom a25821384 = m.newAtom(IElement.C);
        IAtom a20547265 = m.newAtom(IElement.C);
        IAtom a29230394 = m.newAtom(IElement.C);
        IAtom a4797476 = m.newAtom(IElement.C);
        IAtom a33442171 = m.newAtom(IElement.C);
        IAtom a25843025 = m.newAtom(IElement.C);
        IAtom a15165386 = m.newAtom(IElement.C);
        IAtom a5188440 = m.newAtom(IElement.C);
        IAtom a27711753 = m.newAtom(IElement.C);
        IAtom a15922242 = m.newAtom(IElement.C);
        IAtom a8537510 = m.newAtom(IElement.C);
        IAtom a11193060 = m.newAtom(IElement.C);
        IAtom a15622119 = m.newAtom(IElement.C);
        IAtom a31079762 = m.newAtom(IElement.C);
        IAtom a15732464 = m.newAtom(IElement.C);
        IAtom a6608127 = m.newAtom(IElement.O);
        IAtom a31186344 = m.newAtom(IElement.C);
        IAtom a28671243 = m.newAtom(IElement.C);
        IAtom a2390201 = m.newAtom(IElement.C);
        IAtom a7457060 = m.newAtom(IElement.N);
        IAtom a5018139 = m.newAtom(IElement.C);
        IAtom a17579060 = m.newAtom(IElement.C);
        IAtom a4498301 = m.newAtom(IElement.C);
        IAtom a4825665 = m.newAtom(IElement.C);
        IAtom a3895851 = m.newAtom(IElement.C);
        IAtom a12877520 = m.newAtom(IElement.O);
        IAtom a6404420 = m.newAtom(IElement.C);
        IAtom a30037221 = m.newAtom(IElement.C);
        IAtom a9157535 = m.newAtom(IElement.C);
        IAtom a30068290 = m.newAtom(IElement.C);
        IAtom a28016950 = m.newAtom(IElement.C);
        IAtom a11544067 = m.newAtom(IElement.C);
        IAtom a9411749 = m.newAtom(IElement.C);
        IAtom a7677533 = m.newAtom(IElement.C);
        IAtom a19506413 = m.newAtom(IElement.C);
        IAtom a17494358 = m.newAtom(IElement.C);
        IAtom a23749737 = m.newAtom(IElement.C);
        IAtom a31864456 = m.newAtom(IElement.C);
        IAtom a17180410 = m.newAtom(IElement.C);
        IAtom a15264957 = m.newAtom(IElement.C);
        IAtom a953749 = m.newAtom(IElement.C);
        IAtom a24211668 = m.newAtom(IElement.C);
        IAtom a10911340 = m.newAtom(IElement.C);
        IAtom a11931089 = m.newAtom(IElement.O);
        IAtom a4540152 = m.newAtom(IElement.C);
        IAtom a3560270 = m.newAtom(IElement.C);
        IAtom a9916954 = m.newAtom(IElement.C);
        IAtom a9383262 = m.newAtom(IElement.C);
        IAtom a32209327 = m.newAtom(IElement.O);
        IAtom a8511886 = m.newAtom(IElement.C);
        IAtom a16738370 = m.newAtom(IElement.C);
        IAtom a1442064 = m.newAtom(IElement.C);
        IAtom a10475532 = m.newAtom(IElement.C);
        IAtom a2173256 = m.newAtom(IElement.C);
        IAtom a18704986 = m.newAtom(IElement.C);
        IAtom a3227753 = m.newAtom(IElement.C);
        IAtom a24889937 = m.newAtom(IElement.O);
        IAtom a2072861 = m.newAtom(IElement.C);
        IAtom a9090971 = m.newAtom(IElement.C);
        IAtom a18631013 = m.newAtom(IElement.O);
        IAtom a1491444 = m.newAtom(IElement.C);
        IAtom a1541241 = m.newAtom(IElement.C);
        IAtom a33183294 = m.newAtom(IElement.C);
        IAtom a3423477 = m.newAtom(IElement.C);
        IAtom a26082305 = m.newAtom(IElement.C);
        IAtom a10210932 = m.newAtom(IElement.H);
        IAtom a17783924 = m.newAtom(IElement.H);
        IAtom a25095750 = m.newAtom(IElement.H);
        IAtom a5062832 = m.newAtom(IElement.H);
        IAtom a30540747 = m.newAtom(IElement.H);
        IAtom a16190602 = m.newAtom(IElement.H);
        IAtom a22563579 = m.newAtom(IElement.H);
        IAtom a27447689 = m.newAtom(IElement.H);
        IAtom a6991232 = m.newAtom(IElement.H);
        IAtom a27574573 = m.newAtom(IElement.H);
        IAtom a25599567 = m.newAtom(IElement.H);
        IAtom a16995665 = m.newAtom(IElement.H);
        IAtom a30822381 = m.newAtom(IElement.H);
        IAtom a18443320 = m.newAtom(IElement.H);
        IAtom a1037618 = m.newAtom(IElement.H);
        IAtom a24508656 = m.newAtom(IElement.H);
        IAtom a2784068 = m.newAtom(IElement.H);
        IAtom a16958729 = m.newAtom(IElement.H);
        IAtom a14026420 = m.newAtom(IElement.H);
        IAtom a22169642 = m.newAtom(IElement.H);
        IAtom a16774520 = m.newAtom(IElement.H);
        IAtom a5026935 = m.newAtom(IElement.H);
        IAtom a31193341 = m.newAtom(IElement.H);
        IAtom a12040277 = m.newAtom(IElement.H);
        IAtom a27711461 = m.newAtom(IElement.H);
        IAtom a11009871 = m.newAtom(IElement.H);
        IAtom a23775752 = m.newAtom(IElement.H);
        IAtom a32897510 = m.newAtom(IElement.H);
        IAtom a32085005 = m.newAtom(IElement.H);
        IAtom a32960722 = m.newAtom(IElement.H);
        m.newBond(a8540106, a21257520);
        m.newBond(a8540106, a21109443);
        m.newBond(a8540106, a10210932);
        m.newBond(a21257520, a15403331, IBond.Order.DOUBLE);
        m.newBond(a21257520, a11357376);
        m.newBond(a21109443, a25821384);
        m.newBond(a21109443, a20547265, IBond.Order.DOUBLE);
        m.newBond(a15403331, a29230394);
        m.newBond(a15403331, a4797476);
        m.newBond(a11357376, a33442171, IBond.Order.DOUBLE);
        m.newBond(a11357376, a17783924);
        m.newBond(a25821384, a25843025, IBond.Order.DOUBLE);
        m.newBond(a25821384, a15165386);
        m.newBond(a20547265, a5188440);
        m.newBond(a20547265, a25095750);
        m.newBond(a29230394, a27711753);
        m.newBond(a29230394, a15922242, IBond.Order.DOUBLE);
        m.newBond(a4797476, a8537510);
        m.newBond(a4797476, a11193060, IBond.Order.DOUBLE);
        m.newBond(a33442171, a15622119);
        m.newBond(a33442171, a8537510);
        m.newBond(a25843025, a31079762);
        m.newBond(a25843025, a15732464);
        m.newBond(a15165386, a6608127, IBond.Order.DOUBLE);
        m.newBond(a15165386, a31186344);
        m.newBond(a5188440, a15732464, IBond.Order.DOUBLE);
        m.newBond(a5188440, a5062832);
        m.newBond(a27711753, a28671243);
        m.newBond(a27711753, a2390201, IBond.Order.DOUBLE);
        m.newBond(a15922242, a7457060);
        m.newBond(a15922242, a5018139);
        m.newBond(a8537510, a17579060, IBond.Order.DOUBLE);
        m.newBond(a11193060, a4498301);
        m.newBond(a11193060, a2390201);
        m.newBond(a15622119, a4825665);
        m.newBond(a15622119, a3895851, IBond.Order.DOUBLE);
        m.newBond(a31079762, a12877520, IBond.Order.DOUBLE);
        m.newBond(a31079762, a6404420);
        m.newBond(a15732464, a30540747);
        m.newBond(a31186344, a30037221, IBond.Order.DOUBLE);
        m.newBond(a31186344, a6404420);
        m.newBond(a28671243, a9157535);
        m.newBond(a28671243, a30068290, IBond.Order.DOUBLE);
        m.newBond(a2390201, a28016950);
        m.newBond(a7457060, a11544067);
        m.newBond(a7457060, a16190602);
        m.newBond(a5018139, a9157535, IBond.Order.DOUBLE);
        m.newBond(a5018139, a22563579);
        m.newBond(a17579060, a9411749);
        m.newBond(a17579060, a7677533);
        m.newBond(a4498301, a7677533, IBond.Order.DOUBLE);
        m.newBond(a4498301, a27447689);
        m.newBond(a4825665, a19506413, IBond.Order.DOUBLE);
        m.newBond(a4825665, a9411749);
        m.newBond(a3895851, a17494358);
        m.newBond(a3895851, a6991232);
        m.newBond(a6404420, a23749737, IBond.Order.DOUBLE);
        m.newBond(a30037221, a31864456);
        m.newBond(a30037221, a27574573);
        m.newBond(a9157535, a17180410);
        m.newBond(a30068290, a15264957);
        m.newBond(a30068290, a953749);
        m.newBond(a28016950, a953749, IBond.Order.DOUBLE);
        m.newBond(a28016950, a25599567);
        m.newBond(a11544067, a24211668);
        m.newBond(a11544067, a10911340, IBond.Order.DOUBLE);
        m.newBond(a9411749, a11931089, IBond.Order.DOUBLE);
        m.newBond(a7677533, a16995665);
        m.newBond(a19506413, a4540152);
        m.newBond(a19506413, a30822381);
        m.newBond(a17494358, a4540152, IBond.Order.DOUBLE);
        m.newBond(a17494358, a18443320);
        m.newBond(a23749737, a3560270);
        m.newBond(a23749737, a1037618);
        m.newBond(a31864456, a3560270, IBond.Order.DOUBLE);
        m.newBond(a31864456, a24508656);
        m.newBond(a17180410, a9916954);
        m.newBond(a17180410, a9383262, IBond.Order.DOUBLE);
        m.newBond(a15264957, a32209327, IBond.Order.DOUBLE);
        m.newBond(a15264957, a9916954);
        m.newBond(a953749, a2784068);
        m.newBond(a24211668, a8511886, IBond.Order.DOUBLE);
        m.newBond(a24211668, a16738370);
        m.newBond(a10911340, a1442064);
        m.newBond(a10911340, a16958729);
        m.newBond(a4540152, a14026420);
        m.newBond(a3560270, a22169642);
        m.newBond(a9916954, a10475532, IBond.Order.DOUBLE);
        m.newBond(a9383262, a2173256);
        m.newBond(a9383262, a16774520);
        m.newBond(a8511886, a18704986);
        m.newBond(a8511886, a3227753);
        m.newBond(a16738370, a24889937, IBond.Order.DOUBLE);
        m.newBond(a16738370, a2072861);
        m.newBond(a1442064, a3227753, IBond.Order.DOUBLE);
        m.newBond(a1442064, a5026935);
        m.newBond(a10475532, a9090971);
        m.newBond(a10475532, a31193341);
        m.newBond(a2173256, a9090971, IBond.Order.DOUBLE);
        m.newBond(a2173256, a12040277);
        m.newBond(a18704986, a18631013, IBond.Order.DOUBLE);
        m.newBond(a18704986, a1491444);
        m.newBond(a3227753, a27711461);
        m.newBond(a2072861, a1541241, IBond.Order.DOUBLE);
        m.newBond(a2072861, a1491444);
        m.newBond(a9090971, a11009871);
        m.newBond(a1491444, a33183294, IBond.Order.DOUBLE);
        m.newBond(a1541241, a3423477);
        m.newBond(a1541241, a23775752);
        m.newBond(a33183294, a26082305);
        m.newBond(a33183294, a32897510);
        m.newBond(a3423477, a26082305, IBond.Order.DOUBLE);
        m.newBond(a3423477, a32085005);
        m.newBond(a26082305, a32960722);
        IRingSet ringSet = new AllRingsFinder().findAllRings(m);
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
