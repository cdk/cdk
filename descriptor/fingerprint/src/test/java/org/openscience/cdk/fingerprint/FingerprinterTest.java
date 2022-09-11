/* Copyright (C) 1997-2007,2011  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.fingerprint;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDK;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.graph.AtomContainerBondPermutor;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLRXNV2000Reader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-standard
 */
class FingerprinterTest extends AbstractFixedLengthFingerprinterTest {

    boolean                     standAlone = false;
    private static final ILoggingTool logger     = LoggingToolFactory.createLoggingTool(FingerprinterTest.class);

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new Fingerprinter();
    }

    @Test
    void testRegression() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeIndole();
        IAtomContainer mol2 = TestMoleculeFactory.makePyrrole();
        Fingerprinter fingerprinter = new Fingerprinter(1024, 8);
        IBitFingerprint bs1 = fingerprinter.getBitFingerprint(mol1);
        Assertions.assertEquals(33, bs1.cardinality(), "Seems the fingerprint code has changed. This will cause a number of other tests to fail too!");
        IBitFingerprint bs2 = fingerprinter.getBitFingerprint(mol2);
        Assertions.assertEquals(13, bs2.cardinality(), "Seems the fingerprint code has changed. This will cause a number of other tests to fail too!");
    }

    @Test
    void testGetSize() throws java.lang.Exception {
        IFingerprinter fingerprinter = new Fingerprinter(512);
        Assertions.assertNotNull(fingerprinter);
        Assertions.assertEquals(512, fingerprinter.getSize());
    }

    @Test
    void testGetSearchDepth() throws java.lang.Exception {
        Fingerprinter fingerprinter = new Fingerprinter(512, 3);
        Assertions.assertNotNull(fingerprinter);
        Assertions.assertEquals(3, fingerprinter.getSearchDepth());
    }

    @Test
    void testgetBitFingerprint_IAtomContainer() throws java.lang.Exception {
        Fingerprinter fingerprinter = new Fingerprinter();

        IAtomContainer mol = TestMoleculeFactory.makeIndole();
        IBitFingerprint bs = fingerprinter.getBitFingerprint(mol);
        Assertions.assertNotNull(bs);
        Assertions.assertEquals(fingerprinter.getSize(), bs.size());
    }

    @Test
    void testFingerprinter() throws java.lang.Exception {
        Fingerprinter fingerprinter = new Fingerprinter();
        Assertions.assertNotNull(fingerprinter);

        IAtomContainer mol = TestMoleculeFactory.makeIndole();
        BitSet bs = fingerprinter.getBitFingerprint(mol).asBitSet();
        IAtomContainer frag1 = TestMoleculeFactory.makePyrrole();
        BitSet bs1 = fingerprinter.getBitFingerprint(frag1).asBitSet();
        Assertions.assertTrue(FingerprinterTool.isSubset(bs, bs1));
    }

    @Test
    void testFingerprinter_int() throws java.lang.Exception {
        Fingerprinter fingerprinter = new Fingerprinter(512);
        Assertions.assertNotNull(fingerprinter);

        IAtomContainer mol = TestMoleculeFactory.makeIndole();
        BitSet bs = fingerprinter.getBitFingerprint(mol).asBitSet();
        IAtomContainer frag1 = TestMoleculeFactory.makePyrrole();
        BitSet bs1 = fingerprinter.getBitFingerprint(frag1).asBitSet();
        Assertions.assertTrue(FingerprinterTool.isSubset(bs, bs1));
    }

    @Test
    void testFingerprinter_int_int() throws java.lang.Exception {
        Fingerprinter fingerprinter = new Fingerprinter(1024, 7);
        Assertions.assertNotNull(fingerprinter);

        IAtomContainer mol = TestMoleculeFactory.makeIndole();
        BitSet bs = fingerprinter.getBitFingerprint(mol).asBitSet();
        IAtomContainer frag1 = TestMoleculeFactory.makePyrrole();
        BitSet bs1 = fingerprinter.getBitFingerprint(frag1).asBitSet();
        Assertions.assertTrue(FingerprinterTool.isSubset(bs, bs1));
    }

    @Test
    void testFingerprinterBitSetSize() throws Exception {
        Fingerprinter fingerprinter = new Fingerprinter(1024, 7);
        Assertions.assertNotNull(fingerprinter);
        IAtomContainer mol = TestMoleculeFactory.makeIndole();
        BitSet bs = fingerprinter.getBitFingerprint(mol).asBitSet();
        Assertions.assertEquals(994, bs.length()); // highest set bit
        Assertions.assertEquals(1024, bs.size()); // actual bit set size
    }

    /**
     * @cdk.bug 1851202
     */
    @Test
    void testBug1851202() throws Exception {
        String filename1 = "0002.stg01.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getResourceAsStream(filename1);
        MDLRXNV2000Reader reader = new MDLRXNV2000Reader(ins1, Mode.STRICT);
        IReaction reaction = reader.read(new Reaction());
        Assertions.assertNotNull(reaction);

        IAtomContainer reactant = reaction.getReactants().getAtomContainer(0);
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);

        Fingerprinter fingerprinter = new Fingerprinter(64 * 26, 8);
        Assertions.assertNotNull(fingerprinter.getBitFingerprint(reactant));
        Assertions.assertNotNull(fingerprinter.getBitFingerprint(product));
    }

    @Tag("SlowTest")
    @Test
    void testbug2917084() throws Exception {
        String filename1 = "boronBuckyBall.mol";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getResourceAsStream(filename1);
        MDLV2000Reader reader = new MDLV2000Reader(ins1, Mode.STRICT);
        IChemFile chemFile = reader.read(new ChemFile());
        Assertions.assertNotNull(chemFile);
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);
        Fingerprinter fingerprinter = new Fingerprinter(1024, 8);
        Assertions.assertThrows(CDKException.class, () -> {
            fingerprinter.getBitFingerprint(mol);
        });
    }

    /**
     * @throws org.openscience.cdk.exception.CDKException
     * @cdk.bug 2819557
     */
    @Test
    void testBug2819557() throws CDKException {
        IAtomContainer butane = makeButane();
        IAtomContainer propylAmine = makePropylAmine();

        Fingerprinter fp = new Fingerprinter();
        BitSet b1 = fp.getBitFingerprint(butane).asBitSet();
        BitSet b2 = fp.getBitFingerprint(propylAmine).asBitSet();

        Assertions.assertFalse(FingerprinterTool.isSubset(b2, b1), "butane should not be a substructure of propylamine");
    }

    @Test
    void testBondPermutation() throws CDKException {
        IAtomContainer pamine = makePropylAmine();
        Fingerprinter fp = new Fingerprinter();
        IBitFingerprint bs1 = fp.getBitFingerprint(pamine);

        AtomContainerBondPermutor acp = new AtomContainerBondPermutor(pamine);
        while (acp.hasNext()) {
            IAtomContainer container = acp.next();
            IBitFingerprint bs2 = fp.getBitFingerprint(container);
            Assertions.assertTrue(bs1.equals(bs2));
        }
    }

    @Test
    void testAtomPermutation() throws CDKException {
        IAtomContainer pamine = makePropylAmine();
        Fingerprinter fp = new Fingerprinter();
        IBitFingerprint bs1 = fp.getBitFingerprint(pamine);

        AtomContainerAtomPermutor acp = new AtomContainerAtomPermutor(pamine);
        while (acp.hasNext()) {
            IAtomContainer container = acp.next();
            IBitFingerprint bs2 = fp.getBitFingerprint(container);
            Assertions.assertTrue(bs1.equals(bs2));
        }
    }

    @Test
    void testBondPermutation2() throws CDKException {
        IAtomContainer pamine = TestMoleculeFactory.makeCyclopentane();
        Fingerprinter fp = new Fingerprinter();
        IBitFingerprint bs1 = fp.getBitFingerprint(pamine);

        AtomContainerBondPermutor acp = new AtomContainerBondPermutor(pamine);
        while (acp.hasNext()) {
            IAtomContainer container = acp.next();
            IBitFingerprint bs2 = fp.getBitFingerprint(container);
            Assertions.assertTrue(bs1.equals(bs2));
        }
    }

    @Test
    void testAtomPermutation2() throws CDKException {
        IAtomContainer pamine = TestMoleculeFactory.makeCyclopentane();
        Fingerprinter fp = new Fingerprinter();
        IBitFingerprint bs1 = fp.getBitFingerprint(pamine);

        AtomContainerAtomPermutor acp = new AtomContainerAtomPermutor(pamine);
        while (acp.hasNext()) {
            IAtomContainer container = acp.next();
            IBitFingerprint bs2 = fp.getBitFingerprint(container);
            Assertions.assertTrue(bs1.equals(bs2));
        }
    }

    static IAtomContainer makeFragment1() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(0, 2, IBond.Order.SINGLE); // 2
        mol.addBond(0, 3, IBond.Order.SINGLE); // 3
        mol.addBond(0, 4, IBond.Order.SINGLE); // 4
        mol.addBond(3, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 6, IBond.Order.DOUBLE); // 6
        return mol;
    }

    static IAtomContainer makeFragment4() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        return mol;
    }

    static IAtomContainer makeFragment2() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("S")); // 3
        mol.addAtom(new Atom("O")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(0, 2, IBond.Order.SINGLE); // 2
        mol.addBond(0, 3, IBond.Order.SINGLE); // 3
        mol.addBond(0, 4, IBond.Order.SINGLE); // 4
        mol.addBond(3, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 6, IBond.Order.DOUBLE); // 6
        mol.addBond(5, 6, IBond.Order.DOUBLE); // 7
        return mol;
    }

    static IAtomContainer makeFragment3() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(0, 2, IBond.Order.SINGLE); // 2
        mol.addBond(0, 3, IBond.Order.SINGLE); // 3
        mol.addBond(0, 4, IBond.Order.SINGLE); // 4
        mol.addBond(3, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 6, IBond.Order.SINGLE); // 6
        return mol;
    }

    static IAtomContainer makeButane() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3

        return mol;
    }

    static IAtomContainer makePropylAmine() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("N")); // 3

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3

        return mol;
    }

    @Test
    void pseudoAtomFingerprint() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        final String query  = "*1CCCC1";
        final String indole = "N1CCCC1";
        IAtomContainer queryMol  = smipar.parseSmiles(query);
        IAtomContainer indoleMol = smipar.parseSmiles(indole);
        Fingerprinter fpr = new Fingerprinter();
        BitSet fp1 = fpr.getFingerprint(queryMol);
        BitSet fp2 = fpr.getFingerprint(indoleMol);
        Assertions.assertTrue(FingerprinterTool.isSubset(fp2, fp1));
        Assertions.assertFalse(FingerprinterTool.isSubset(fp1, fp2));
        fpr.setHashPseudoAtoms(true);
        BitSet fp3 = fpr.getFingerprint(queryMol);
        BitSet fp4 = fpr.getFingerprint(indoleMol);
        Assertions.assertFalse(FingerprinterTool.isSubset(fp4, fp3));
        Assertions.assertFalse(FingerprinterTool.isSubset(fp3, fp4));
    }

    @Test
    void pseudoAtomFingerprintArom() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        final String query  = "*1cnccc1";
        final String indole = "n1cnccc1";
        IAtomContainer queryMol  = smipar.parseSmiles(query);
        IAtomContainer indoleMol = smipar.parseSmiles(indole);
        Fingerprinter fpr = new Fingerprinter();
        BitSet fp1 = fpr.getFingerprint(queryMol);
        BitSet fp2 = fpr.getFingerprint(indoleMol);
        Assertions.assertTrue(FingerprinterTool.isSubset(fp2, fp1));
        Assertions.assertFalse(FingerprinterTool.isSubset(fp1, fp2));
        fpr.setHashPseudoAtoms(true);
        BitSet fp3 = fpr.getFingerprint(queryMol);
        BitSet fp4 = fpr.getFingerprint(indoleMol);
        Assertions.assertFalse(FingerprinterTool.isSubset(fp4, fp3));
        Assertions.assertFalse(FingerprinterTool.isSubset(fp3, fp4));
    }

    @Test
    void testVersion() {
        Fingerprinter fpr = new Fingerprinter(1024, 7);
        fpr.setPathLimit(2000);
        fpr.setHashPseudoAtoms(true);
        String expected = "CDK-Fingerprinter/" + CDK.getVersion() + " searchDepth=7 pathLimit=2000 hashPseudoAtoms=true";
        assertThat(fpr.getVersionDescription(),
                   CoreMatchers.is(expected));
    }

    // manually review once generated
    static void makeTest(Map<String,Integer> map) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparingInt(o -> o.getKey().length()));
        System.err.println("Map<String,Integer> expected = new HashMap<>();");
        for (Map.Entry<String,Integer> e : entries) {
            System.err.println("expected.put(\"" + e.getKey() + "\", " + e.getValue() + ");");
        }
        System.err.println("Assert.assertEquals(expected, actual);");
    }

    @Test
    void rawFpTestLinear() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        Fingerprinter fpr = new Fingerprinter(1024, 7);
        fpr.setPathLimit(2000);
        final String smi  = "CCC=O";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        Map<String,Integer> actual = fpr.getRawFingerprint(mol);
        Map<String,Integer> expected = new HashMap<>();
        expected.put("C", 3);
        expected.put("O", 1);
        expected.put("O=C", 1);
        expected.put("C-C", 2);
        expected.put("C-C-C", 1);
        expected.put("O=C-C", 1);
        expected.put("O=C-C-C", 1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void rawFpTestBranching() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        Fingerprinter fpr = new Fingerprinter(1024, 7);
        fpr.setPathLimit(2000);
        final String smi  = "CCC(O)=O";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        Map<String,Integer> actual = fpr.getRawFingerprint(mol);
        Map<String,Integer> expected = new HashMap<>();
        expected.put("C", 3);
        expected.put("O", 2);
        expected.put("O=C", 1);
        expected.put("O-C", 1);
        expected.put("C-C", 2);
        expected.put("O=C-O", 1);
        expected.put("O-C-C", 1);
        expected.put("C-C-C", 1);
        expected.put("O=C-C", 1);
        expected.put("O=C-C-C", 1);
        expected.put("O-C-C-C", 1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void rawFpTestBranching2() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        Fingerprinter fpr = new Fingerprinter(1024, 7);
        fpr.setPathLimit(2000);
        final String smi  = "C(Cl)CC(O)=O";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        Map<String,Integer> actual = fpr.getRawFingerprint(mol);
        Map<String,Integer> expected = new HashMap<>();
        expected.put("C", 3);
        expected.put("O", 2);
        expected.put("X", 1);
        expected.put("O-C", 1);
        expected.put("X-C", 1);
        expected.put("O=C", 1);
        expected.put("C-C", 2);
        expected.put("O=C-O", 1);
        expected.put("X-C-C", 1);
        expected.put("C-C-C", 1);
        expected.put("O-C-C", 1);
        expected.put("O=C-C", 1);
        expected.put("O=C-C-C", 1);
        expected.put("O-C-C-C", 1);
        expected.put("X-C-C-C", 1);
        expected.put("X-C-C-C=O", 1);
        expected.put("X-C-C-C-O", 1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void rawFpTestRings() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        Fingerprinter fpr = new Fingerprinter(1024, 7);
        fpr.setPathLimit(2000);
        final String smi  = "c1cc(Cl)c(C)cc1";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        Map<String,Integer> actual = fpr.getRawFingerprint(mol);
        Map<String,Integer> expected = new HashMap<>();
        expected.put("C", 7);
        expected.put("X", 1);
        expected.put("C:C", 6);
        expected.put("X-C", 1);
        expected.put("C-C", 1);
        expected.put("C:C-C", 2);
        expected.put("X-C:C", 2);
        expected.put("C:C:C", 6);
        expected.put("C:C:C-C", 2);
        expected.put("X-C:C-C", 1);
        expected.put("C:C:C:C", 6);
        expected.put("X-C:C:C", 2);
        expected.put("C:C:C:C-C", 2);
        expected.put("X-C:C:C:C", 2);
        expected.put("C:C:C:C:C", 6);
        expected.put("C:C:C:C:C:C", 6);
        expected.put("X-C:C:C:C:C", 2);
        expected.put("C:C:C:C:C-C", 2);
        expected.put("X-C:C:C:C:C:C", 2);
        expected.put("C:C:C:C:C:C-C", 2);
        expected.put("X-C:C:C:C:C:C-C", 1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void rawFpTestDepth() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        Fingerprinter fpr = new Fingerprinter(1024, 7); // 7 bonds
        fpr.setPathLimit(2000);
        final String smi  = "CCCCCCCCCC";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        Map<String,Integer> actual = fpr.getRawFingerprint(mol);
        Map<String,Integer> expected = new HashMap<>();
        expected.put("C", 10);
        expected.put("C-C", 9);
        expected.put("C-C-C", 8);
        expected.put("C-C-C-C", 7);
        expected.put("C-C-C-C-C", 6);
        expected.put("C-C-C-C-C-C", 5);
        expected.put("C-C-C-C-C-C-C", 4);
        expected.put("C-C-C-C-C-C-C-C", 3);
        Assertions.assertEquals(8, actual.size());
        Assertions.assertEquals(expected, actual);
    }

    @Test public void testGetRawFingerprint() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        Fingerprinter fpr = new Fingerprinter(1024, 7); // 7 bonds
        fpr.setPathLimit(2000);
        final String smi  = "CC(=O)OC1=CC=CC=C1C(=O)O";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        Map<String,Integer> actual = fpr.getRawFingerprint(mol);
        Map<String,Integer> expected = new HashMap<>();
        expected.put("C", 9);
        expected.put("O", 4);
        expected.put("O=C", 2);
        expected.put("C-C", 2);
        expected.put("O-C", 3);
        expected.put("C:C", 6);
        expected.put("O-C:C", 2);
        expected.put("O=C-O", 2);
        expected.put("C:C-C", 2);
        expected.put("O-C-C", 2);
        expected.put("C:C:C", 6);
        expected.put("O=C-C", 2);
        expected.put("C-O-C", 1);
        expected.put("C:C:C-C", 2);
        expected.put("C:C:C:C", 6);
        expected.put("O-C:C-C", 1);
        expected.put("O-C:C:C", 2);
        expected.put("C:C-O-C", 2);
        expected.put("O=C-O-C", 1);
        expected.put("C-O-C-C", 1);
        expected.put("O=C-C:C", 2);
        expected.put("O-C-C:C", 2);
        expected.put("O-C-C:C:C", 2);
        expected.put("C:C:C:C:C", 6);
        expected.put("O-C:C:C:C", 2);
        expected.put("C:C-O-C-C", 2);
        expected.put("C:C:C:C-C", 2);
        expected.put("O=C-C:C:C", 2);
        expected.put("C:C:C-O-C", 2);
        expected.put("C-O-C:C-C", 1);
        expected.put("O=C-C:C-O", 1);
        expected.put("O-C:C-C-O", 1);
        expected.put("O=C-O-C:C", 2);
        expected.put("C:C:C:C:C:C", 6);
        expected.put("O-C:C:C:C:C", 2);
        expected.put("O=C-C:C:C:C", 2);
        expected.put("O-C-C:C:C:C", 2);
        expected.put("C:C:C:C:C-C", 2);
        expected.put("O=C-C:C-O-C", 1);
        expected.put("O-C-C:C-O-C", 1);
        expected.put("O=C-O-C:C:C", 2);
        expected.put("C-C:C-O-C-C", 1);
        expected.put("O=C-O-C:C-C", 1);
        expected.put("C:C:C-O-C-C", 2);
        expected.put("C:C:C:C-O-C", 2);
        expected.put("O=C-C:C-O-C-C", 1);
        expected.put("O=C-O-C:C-C-O", 1);
        expected.put("O=C-O-C:C-C=O", 1);
        expected.put("O-C-C:C-O-C-C", 1);
        expected.put("C:C:C:C:C-O-C", 2);
        expected.put("O=C-C:C:C:C:C", 2);
        expected.put("C:C:C:C-O-C-C", 2);
        expected.put("O-C-C:C:C:C:C", 2);
        expected.put("O=C-O-C:C:C:C", 2);
        expected.put("C:C:C:C:C:C-C", 2);
        expected.put("O-C:C:C:C:C:C", 2);
        expected.put("O-C:C:C:C:C:C-C", 1);
        expected.put("O-C-C:C:C:C:C:C", 2);
        expected.put("C:C:C:C:C-O-C-C", 2);
        expected.put("C:C:C:C:C:C-O-C", 2);
        expected.put("O=C-O-C:C:C:C:C", 2);
        expected.put("O=C-C:C:C:C:C:C", 2);
        Assertions.assertEquals(expected, actual);
    }

    private BitSet foldFp(ICountFingerprint fp, int size) {
        BitSet bs = new BitSet();
        Random rand = new Random();
        for (int i = 0; i < fp.numOfPopulatedbins(); i++) {
            int hash = fp.getHash(i);
            rand.setSeed(hash);
            int hashFolded = rand.nextInt(1024);
            bs.set(hashFolded);
        }
        return bs;
    }

    @Test public void testGetCountFingerprint() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        Fingerprinter fpr = new Fingerprinter(1024, 7); // 7 bonds
        fpr.setPathLimit(2000);
        final String smi  = "CC(=O)OC1=CC=CC=C1C(=O)O";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        ICountFingerprint cntFp = fpr.getCountFingerprint(mol);
        IBitFingerprint expBitset = fpr.getBitFingerprint(mol);
        Assertions.assertEquals(62, cntFp.numOfPopulatedbins());
        BitSet actBitset = foldFp(cntFp, 1024);
        Assertions.assertEquals(expBitset.asBitSet(), actBitset);
    }
}
