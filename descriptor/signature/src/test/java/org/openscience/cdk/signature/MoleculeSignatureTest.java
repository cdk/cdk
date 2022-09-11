/* Copyright (C) 2009-2010 maclean {gilleain.torrance@gmail.com}
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
package org.openscience.cdk.signature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;

import signature.AbstractVertexSignature;

/**
 * @cdk.module test-signature
 * @author maclean
 *
 */
class MoleculeSignatureTest extends CDKTestCase {

    private SmilesParser       parser;

    private IChemObjectBuilder builder;

    private IAtomContainer     mol;

    private MoleculeSignature  molSig;

    @BeforeEach
    void setUp() {
        this.parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        this.builder = DefaultChemObjectBuilder.getInstance();
        mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        molSig = new MoleculeSignature(mol);
    }

    @Test
    void getVertexCountTest() {
        Assertions.assertEquals(mol.getAtomCount(), molSig.getVertexCount());
    }

    @Test
    void getSignatureStringForVertexTest() {
        Assertions.assertEquals("[C]([C])", molSig.signatureStringForVertex(0));
    }

    @Test
    void getSignatureStringForVertexTest_height() {
        Assertions.assertEquals("[C]", molSig.signatureStringForVertex(0, 0));
    }

    @Test
    void getSignatureForVertexTest() {
        Assertions.assertNotNull(molSig.getVertexSignatures());
    }

    @Test
    void calculateOrbitsTest() {
        Assertions.assertEquals(1, molSig.calculateOrbits().size());
    }

    @Test
    void fromSignatureStringTest() {
        String signatureString = molSig.toCanonicalString();
        IAtomContainer reconstructed = MoleculeSignature.fromSignatureString(signatureString, builder);
        Assertions.assertEquals(mol.getAtomCount(), reconstructed.getAtomCount());
    }

    @Test
    void toCanonicalSignatureStringTest() {
        Assertions.assertEquals("[C]", molSig.toCanonicalSignatureString(0));
    }

    void fullPermutationTest(IAtomContainer mol) {
        AtomContainerAtomPermutor permutor = new AtomContainerAtomPermutor(mol);
        String expected = new MoleculeSignature(mol).toCanonicalString();
        int numberOfPermutationsTried = 0;
        while (permutor.hasNext()) {
            IAtomContainer permutation = permutor.next();
            String actual = new MoleculeSignature(permutation).toCanonicalString();
            numberOfPermutationsTried++;
            String msg = "Failed on permutation " + numberOfPermutationsTried;
            Assertions.assertEquals(expected, actual, msg);
        }
    }

    String canonicalStringFromSmiles(String smiles) throws InvalidSmilesException {
        IAtomContainer mol = parser.parseSmiles(smiles);
        MoleculeSignature signature = new MoleculeSignature(mol);
        return signature.toCanonicalString();
    }

    String canonicalStringFromMolecule(IAtomContainer molecule) {
        MoleculeSignature signature = new MoleculeSignature(molecule);
        return signature.getGraphSignature();
    }

    String fullStringFromMolecule(IAtomContainer molecule) {
        MoleculeSignature molSig = new MoleculeSignature(molecule);
        return molSig.toFullString();
    }

    List<String> getAtomicSignatures(IAtomContainer molecule) {
        MoleculeSignature signature = new MoleculeSignature(molecule);
        return signature.getVertexSignatureStrings();
    }

    void addHydrogens(IAtomContainer mol, IAtom atom, int n) {
        for (int i = 0; i < n; i++) {
            IAtom h = builder.newInstance(IAtom.class, "H");
            mol.addAtom(h);
            mol.addBond(builder.newInstance(IBond.class, atom, h));
        }
    }

    @Test
    void testEmpty() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        MoleculeSignature signature = new MoleculeSignature(mol);
        String signatureString = signature.toCanonicalString();
        String expected = "";
        Assertions.assertEquals(expected, signatureString);
    }

    @Test
    void testSingleNode() throws Exception {
        String singleChild = "C";
        String signatureString = this.canonicalStringFromSmiles(singleChild);
        String expected = "[C]";
        Assertions.assertEquals(expected, signatureString);
    }

    @Test
    void testSingleChild() throws Exception {
        String singleChild = "CC";
        String signatureString = this.canonicalStringFromSmiles(singleChild);
        String expected = "[C]([C])";
        Assertions.assertEquals(expected, signatureString);
    }

    @Test
    void testMultipleChildren() throws Exception {
        String multipleChildren = "C(C)C";
        String signatureString = this.canonicalStringFromSmiles(multipleChildren);
        String expected = "[C]([C]([C]))";
        Assertions.assertEquals(expected, signatureString);
    }

    @Test
    void testThreeCycle() throws Exception {
        String fourCycle = "C1CC1";
        String signatureString = this.canonicalStringFromSmiles(fourCycle);
        String expected = "[C]([C]([C,0])[C,0])";
        Assertions.assertEquals(expected, signatureString);
    }

    @Test
    void testFourCycle() throws Exception {
        String fourCycle = "C1CCC1";
        String signatureString = this.canonicalStringFromSmiles(fourCycle);
        String expected = "[C]([C]([C,0])[C]([C,0]))";
        Assertions.assertEquals(expected, signatureString);
    }

    @Test
    void testMultipleFourCycles() throws Exception {
        String bridgedRing = "C1C(C2)CC12";
        String signatureString = this.canonicalStringFromSmiles(bridgedRing);
        String expected = "[C]([C]([C,0])[C]([C,0])[C]([C,0]))";
        Assertions.assertEquals(expected, signatureString);
    }

    @Test
    void testFiveCycle() throws Exception {
        String fiveCycle = "C1CCCC1";
        String signatureString = this.canonicalStringFromSmiles(fiveCycle);
        String expected = "[C]([C]([C]([C,0]))[C]([C,0]))";
        Assertions.assertEquals(expected, signatureString);
    }

    @Test
    void testMultipleFiveCycles() throws Exception {
        String multipleFiveCycle = "C1C(CC2)CCC12";
        String signatureString = this.canonicalStringFromSmiles(multipleFiveCycle);
        String expected = "[C]([C]([C]([C,0]))[C]([C]([C,0]))[C]([C,0]))";
        Assertions.assertEquals(expected, signatureString);
    }

    @Test
    void testCubane() {
        String expected = "[C]([C]([C,3]([C,2])[C,1]([C,2]))[C]([C,3][C,0]" + "([C,2]))[C]([C,0][C,1]))";
        IAtomContainer mol = AbstractSignatureTest.makeCubane();
        Assertions.assertEquals(expected, this.canonicalStringFromMolecule(mol));
    }

    @Test
    void testCage() {
        String expectedA = "[C]([C]([C]([C,4][C,3]([C,1]))[C]([C,5][C,3]))"
                + "[C]([C,4]([C]([C,2][C,1]))[C]([C,2]([C,0])[C,6]))" + "[C]([C,5]([C]([C,0][C,1]))[C,6]([C,0])))";

        String expectedB = "[C]([C]([C]([C]([C,1]([C,0])[C,4])[C,5])[C,7]"
                + "([C,4]([C,3])))[C]([C]([C,3]([C,0])[C,6])[C,7])" + "[C]([C,5]([C]([C,2][C,1]))[C,6]([C,2]([C,0]))))";
        IAtomContainer mol = AbstractSignatureTest.makeCage();
        String signature = this.canonicalStringFromMolecule(mol);
        Assertions.assertEquals(expectedA, signature);
        String fullSignature = fullStringFromMolecule(mol);
        String fullExpected = "8" + expectedA + " + 8" + expectedB;
        Assertions.assertEquals(fullExpected, fullSignature);
    }

    @Test
    void testPropellane() {
        String expectedA = "[C]([C]([C,0])[C]([C,0])[C]([C,0])[C,0])";
        String expectedB = "[C]([C]([C,2][C,1][C,0])[C,2]([C,1][C,0]))";
        IAtomContainer mol = AbstractSignatureTest.makePropellane();
        String signature = this.canonicalStringFromMolecule(mol);
        Assertions.assertEquals(expectedA, signature);
        String fullExpected = "2" + expectedA + " + 3" + expectedB;
        String fullSignature = fullStringFromMolecule(mol);
        Assertions.assertEquals(fullExpected, fullSignature);
    }

    @Test
    void testBridgedCycloButane() {
        String expected = "[C]([C]([C,0])[C]([C,0])[C,0])";
        IAtomContainer mol = AbstractSignatureTest.makeBridgedCyclobutane();
        String signature = this.canonicalStringFromMolecule(mol);
        Assertions.assertEquals(expected, signature);
    }

    @Test
    void testCyclohexaneWithHydrogens() {
        IAtomContainer cyclohexane = TestMoleculeFactory.makeCyclohexane();
        for (int i = 0; i < 6; i++) {
            addHydrogens(cyclohexane, cyclohexane.getAtom(i), 2);
        }
        String expected = "[C]([C]([C]([C,0]([H][H])[H][H])[H][H])" + "[C]([C]([C,0][H][H])[H][H])[H][H])";

        String actual = this.canonicalStringFromMolecule(cyclohexane);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testBenzeneWithDoubleBonds() {
        IAtomContainer benzene = builder.newInstance(IAtomContainer.class);
        AbstractSignatureTest.addCarbons(benzene, 6);
        for (int i = 0; i < 6; i++) {
            AbstractSignatureTest.addHydrogens(benzene, i, 1);
        }
        benzene.addBond(0, 1, IBond.Order.SINGLE);
        benzene.addBond(1, 2, IBond.Order.DOUBLE);
        benzene.addBond(2, 3, IBond.Order.SINGLE);
        benzene.addBond(3, 4, IBond.Order.DOUBLE);
        benzene.addBond(4, 5, IBond.Order.SINGLE);
        benzene.addBond(5, 0, IBond.Order.DOUBLE);

        MoleculeSignature signature = new MoleculeSignature(benzene);
        String carbonSignature = signature.signatureStringForVertex(0);
        for (int i = 1; i < 6; i++) {
            String carbonSignatureI = signature.signatureStringForVertex(i);
            Assertions.assertEquals(carbonSignature, carbonSignatureI);
        }
    }

    @Test
    void getAromaticEdgeLabelTest() {
        IAtomContainer benzeneRing = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 6; i++) {
            benzeneRing.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        for (int i = 0; i < 6; i++) {
            IAtom a = benzeneRing.getAtom(i);
            IAtom b = benzeneRing.getAtom((i + 1) % 6);
            IBond bond = builder.newInstance(IBond.class, a, b);
            benzeneRing.addBond(bond);
            bond.setFlag(CDKConstants.ISAROMATIC, true);
        }

        MoleculeSignature molSignature = new MoleculeSignature(benzeneRing);
        List<AbstractVertexSignature> signatures = molSignature.getVertexSignatures();
        for (AbstractVertexSignature signature : signatures) {
            for (int i = 0; i < 6; i++) {
                Assertions.assertEquals("p", ((AtomSignature) signature).getEdgeLabel(i, (i + 1) % 6), "Failed for " + i);
            }
        }
    }

    @Test
    void cyclobuteneTest() {
        String expectedA = "[C]([C]([C,0])=[C]([C,0]))";
        String expectedB = "[C]([C]([C,0])[C](=[C,0]))";
        IAtomContainer cyclobutene = builder.newInstance(IAtomContainer.class);
        AbstractSignatureTest.addCarbons(cyclobutene, 4);
        cyclobutene.addBond(0, 1, IBond.Order.SINGLE);
        cyclobutene.addBond(0, 2, IBond.Order.SINGLE);
        cyclobutene.addBond(1, 3, IBond.Order.DOUBLE);
        cyclobutene.addBond(2, 3, IBond.Order.SINGLE);
        Assertions.assertEquals(expectedA, canonicalStringFromMolecule(cyclobutene));

        String expectedFullString = "2" + expectedA + " + 2" + expectedB;
        String actualFullString = fullStringFromMolecule(cyclobutene);
        Assertions.assertEquals(expectedFullString, actualFullString);
    }

    @Test
    void methyleneCyclopropeneTest() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        AbstractSignatureTest.addCarbons(mol, 4);
        AbstractSignatureTest.addHydrogens(mol, 1, 2);
        AbstractSignatureTest.addHydrogens(mol, 2, 1);
        AbstractSignatureTest.addHydrogens(mol, 3, 1);
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.DOUBLE);
        MoleculeSignature molSig = new MoleculeSignature(mol);

        String sigFor2Height1 = molSig.signatureStringForVertex(2, 1);
        String sigFor3Height1 = molSig.signatureStringForVertex(3, 1);
        Assertions.assertTrue(sigFor2Height1.equals(sigFor3Height1), "Height 1 signatures for atoms 2 and 3" + " should be the same");

        String sigFor2Height2 = molSig.signatureStringForVertex(2, 1);
        String sigFor3Height2 = molSig.signatureStringForVertex(3, 1);
        Assertions.assertTrue(sigFor2Height2.equals(sigFor3Height2), "Height 2 signatures for atoms 2 and 3" + " should be the same");
    }

    @Test
    void fusedSquareMultipleBondTest() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        String expected = "[C]([C]([C,1])[C]([C,0])[C](=[C,1])[C](=[C,0]))";
        AbstractSignatureTest.addCarbons(mol, 7);
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.addBond(2, 5, IBond.Order.SINGLE);
        mol.addBond(3, 6, IBond.Order.SINGLE);
        mol.addBond(4, 6, IBond.Order.DOUBLE);
        MoleculeSignature molSig = new MoleculeSignature(mol);
        String sigFor0 = molSig.signatureStringForVertex(0);
        Assertions.assertEquals(expected, sigFor0);
    }

    int findFirstAtomIndexForSymbol(IAtomContainer container, String symbol) {
        for (int i = 0; i < container.getAtomCount(); i++) {
            if (container.getAtom(i).getSymbol().equals(symbol)) {
                return i;
            }
        }
        return -1;
    }

    @Test
    void methylFerroceneTest() throws Exception {
        String smiles = "CC12C3C4C5C1[Fe]23456789C%10C6C7C8C9%10";
        IAtomContainer mol = parser.parseSmiles(smiles);
        MoleculeSignature molSig = new MoleculeSignature(mol);
        int feIndex = findFirstAtomIndexForSymbol(mol, "Fe");
        String sigForIron = molSig.signatureStringForVertex(feIndex);
        String expected = "[Fe]([C]([C,3][C,4])[C,3]([C,1])[C,4]([C,0])" + "[C]([C,5][C,6])[C,5]([C,2])[C,6]([C,7])"
                + "[C,7]([C][C,2])[C,0]([C,1])[C,1][C,2])";
        Assertions.assertEquals(expected, sigForIron);
    }

    @Test
    void threeMethylSulphanylPropanal() throws Exception {
        String smiles = "O=CCCSC";
        fullPermutationTest(parser.parseSmiles(smiles));
    }

    @Test
    void cycleWheelTest() {
        IAtomContainer mol = AbstractSignatureTest.makeCycleWheel(3, 3);
        String expected = "[C]([C]([C]([C,2])[C,2])" + "[C]([C]([C,1])[C,1])" + "[C]([C]([C,0])[C,0]))";
        MoleculeSignature molSig = new MoleculeSignature(mol);
        String centralSignature = molSig.signatureStringForVertex(0);
        Assertions.assertEquals(expected, centralSignature);
    }

    @Test
    @Tag("SlowTest")
    void ttprTest() {
        String expected = "[Rh]([P]([C]([C]([C]([C,6]))" + "[C]([C]([C,6])))[C]([C]([C]([C,3]))"
                + "[C]([C]([C,3])))[C]([C]([C]([C,2]))" + "[C]([C]([C,2]))))[P]([C]([C]([C]([C,7]))"
                + "[C]([C]([C,7])))[C]([C]([C]([C,4]))" + "[C]([C]([C,4])))[C]([C]([C]([C,1]))"
                + "[C]([C]([C,1]))))[P]([C]([C]([C]([C,8]))" + "[C]([C]([C,8])))[C]([C]([C]([C,5]))"
                + "[C]([C]([C,5])))[C]([C]([C]([C,0]))" + "[C]([C]([C,0])))))";
        int phosphateCount = 3;
        int ringCount = 3;
        IAtomContainer ttpr = AbstractSignatureTest.makeRhLikeStructure(phosphateCount, ringCount);
        MoleculeSignature molSig = new MoleculeSignature(ttpr);
        String centralSignature = molSig.signatureStringForVertex(0);
        Assertions.assertEquals(expected, centralSignature);
    }

    @Test
    void napthaleneSkeletonHeightTest() {
        IAtomContainer napthalene = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 10; i++) {
            napthalene.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        napthalene.addBond(0, 1, IBond.Order.SINGLE);
        napthalene.addBond(0, 5, IBond.Order.SINGLE);
        napthalene.addBond(1, 2, IBond.Order.SINGLE);
        napthalene.addBond(1, 6, IBond.Order.SINGLE);
        napthalene.addBond(2, 3, IBond.Order.SINGLE);
        napthalene.addBond(2, 9, IBond.Order.SINGLE);
        napthalene.addBond(3, 4, IBond.Order.SINGLE);
        napthalene.addBond(4, 5, IBond.Order.SINGLE);
        napthalene.addBond(6, 7, IBond.Order.SINGLE);
        napthalene.addBond(7, 8, IBond.Order.SINGLE);
        napthalene.addBond(8, 9, IBond.Order.SINGLE);

        MoleculeSignature molSig = new MoleculeSignature(napthalene);
        int height = 2;
        Map<String, Orbit> orbits = new HashMap<>();
        for (int i = 0; i < napthalene.getAtomCount(); i++) {
            String signatureString = molSig.signatureStringForVertex(i, height);
            Orbit orbit;
            if (orbits.containsKey(signatureString)) {
                orbit = orbits.get(signatureString);
            } else {
                orbit = new Orbit(signatureString, height);
                orbits.put(signatureString, orbit);
            }
            orbit.addAtom(i);
        }
        Assertions.assertEquals(3, orbits.size());
    }

    @Test
    void napthaleneWithDoubleBondsAndHydrogenHeightTest() {
        IAtomContainer napthalene = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 10; i++) {
            napthalene.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        napthalene.addBond(0, 1, IBond.Order.SINGLE);
        napthalene.addBond(0, 5, IBond.Order.DOUBLE);
        napthalene.addBond(1, 2, IBond.Order.DOUBLE);
        napthalene.addBond(1, 6, IBond.Order.SINGLE);
        napthalene.addBond(2, 3, IBond.Order.SINGLE);
        napthalene.addBond(2, 9, IBond.Order.SINGLE);
        napthalene.addBond(3, 4, IBond.Order.DOUBLE);
        napthalene.addBond(4, 5, IBond.Order.SINGLE);
        napthalene.addBond(6, 7, IBond.Order.DOUBLE);
        napthalene.addBond(7, 8, IBond.Order.SINGLE);
        napthalene.addBond(8, 9, IBond.Order.DOUBLE);

        napthalene.addAtom(builder.newInstance(IAtom.class, "H"));
        napthalene.addBond(0, 10, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class, "H"));
        napthalene.addBond(3, 11, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class, "H"));
        napthalene.addBond(4, 12, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class, "H"));
        napthalene.addBond(5, 13, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class, "H"));
        napthalene.addBond(6, 14, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class, "H"));
        napthalene.addBond(7, 15, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class, "H"));
        napthalene.addBond(8, 16, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class, "H"));
        napthalene.addBond(9, 17, IBond.Order.SINGLE);

        int height = 2;
        SignatureQuotientGraph mqg = new SignatureQuotientGraph(napthalene, height);
        Assertions.assertEquals(4, mqg.getVertexCount());
        Assertions.assertEquals(6, mqg.getEdgeCount());
        Assertions.assertEquals(2, mqg.numberOfLoopEdges());
    }
}
