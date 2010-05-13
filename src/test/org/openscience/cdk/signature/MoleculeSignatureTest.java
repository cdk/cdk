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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.signature.Orbit;
import org.openscience.cdk.signature.SignatureQuotientGraph;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * @cdk.module test-signature
 * @author maclean
 *
 */
public class MoleculeSignatureTest {
    
    private SmilesParser parser;
    
    private IChemObjectBuilder builder; 
    
    public MoleculeSignatureTest() {
        this.parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        this.builder = DefaultChemObjectBuilder.getInstance();
    }
    
    public void toMolfileString(IMolecule mol) {
        MDLWriter writer = new MDLWriter(System.out);
        try {
            writer.writeMolecule(mol);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void fullPermutationTest(IMolecule mol) {
        AtomContainerAtomPermutor permutor = new AtomContainerAtomPermutor(mol);
        String expected = new MoleculeSignature(mol).toCanonicalString();
        System.out.println("canonical = " + expected);
        int numberOfPermutationsTried = 0;
        while (permutor.hasNext()) {
            IAtomContainer permutation = permutor.next();
            String actual = 
                new MoleculeSignature(permutation).toCanonicalString();
            numberOfPermutationsTried++;
            String msg = "Failed on permutation " + numberOfPermutationsTried;
            Assert.assertEquals(msg, expected, actual);
        }
    }
    
    public void randomPermutationTest(IMolecule mol) {
        AtomContainerAtomPermutor permutor = new AtomContainerAtomPermutor(mol);
        String expected = new MoleculeSignature(mol).toCanonicalString();
        int numberOfPermutationsTried = 0;
        while (permutor.hasNext()) {
//            IAtomContainer permutation = permutor.randomNext();
//            String actual = 
//                new MoleculeSignature(permutation).toCanonicalString();
            numberOfPermutationsTried++;
//            String msg = "Failed on permutation " + numberOfPermutationsTried;
//            Assert.assertEquals(msg, expected, actual);
        }
        System.out.println(expected);
        System.out.println("Tried " + numberOfPermutationsTried);
    }
    
    public String canonicalStringFromSmiles(String smiles)
            throws InvalidSmilesException {
        IMolecule mol = parser.parseSmiles(smiles);
        MoleculeSignature signature = new MoleculeSignature(mol);
        return signature.toCanonicalString();
    }
    
    public String canonicalStringFromMolecule(IMolecule molecule) {
        MoleculeSignature signature = new MoleculeSignature(molecule);
//        return signature.toCanonicalString();
        return signature.getGraphSignature();
    }
    
    public String fullStringFromMolecule(IMolecule molecule) {
        MoleculeSignature molSig = new MoleculeSignature(molecule);
        return molSig.toFullString();
    }
    
    public List<String> getAtomicSignatures(IMolecule molecule) {
        MoleculeSignature signature = new MoleculeSignature(molecule);
        return signature.getVertexSignatureStrings();
    }
    
    public void addHydrogens(IMolecule mol, IAtom atom, int n) {
        for (int i = 0; i < n; i++) {
            IAtom h = builder.newInstance(IAtom.class,"H");
            mol.addAtom(h);
            mol.addBond(builder.newInstance(IBond.class, atom, h));
        }
    }
    
    @Test
    public void testEmpty() throws Exception {
       IMolecule mol = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class); 
       MoleculeSignature signature = new MoleculeSignature(mol);
       String signatureString = signature.toCanonicalString();
       String expected = "";
       Assert.assertEquals(expected, signatureString);
    }

    
    @Test
    public void testSingleNode() throws Exception {
       String singleChild = "C"; 
       String signatureString = this.canonicalStringFromSmiles(singleChild);
       String expected = "[C]";
       Assert.assertEquals(expected, signatureString);
    }

    
    @Test
    public void testSingleChild() throws Exception {
       String singleChild = "CC"; 
       String signatureString = this.canonicalStringFromSmiles(singleChild);
       String expected = "[C]([C])";
       Assert.assertEquals(expected, signatureString);
    }

    @Test
    public void testMultipleChildren() throws Exception {
       String multipleChildren = "C(C)C"; 
        String signatureString = 
            this.canonicalStringFromSmiles(multipleChildren);
       String expected = "[C]([C]([C]))";
       Assert.assertEquals(expected, signatureString);
    }

    @Test
    public void testThreeCycle() throws Exception {
       String fourCycle = "C1CC1"; 
       String signatureString = this.canonicalStringFromSmiles(fourCycle);
       String expected = "[C]([C,2]([C,1])[C,1])";
       Assert.assertEquals(expected, signatureString);
    }
    
    @Test
    public void testFourCycle() throws Exception {
       String fourCycle = "C1CCC1"; 
       String signatureString = this.canonicalStringFromSmiles(fourCycle);
       String expected = "[C]([C]([C,1])[C]([C,1]))";
       Assert.assertEquals(expected, signatureString);
    }
    
    @Test
    public void testMultipleFourCycles() throws Exception {
       String bridgedRing = "C1C(C2)CC12"; 
       String signatureString = this.canonicalStringFromSmiles(bridgedRing);
       String expected = "[C]([C]([C,1])[C]([C,1])[C]([C,1]))";
       Assert.assertEquals(expected, signatureString);
    }
    
    @Test
    public void testFiveCycle() throws Exception {
       String fiveCycle = "C1CCCC1"; 
       String signatureString = this.canonicalStringFromSmiles(fiveCycle);
       String expected = "[C]([C]([C,2]([C,1]))[C]([C,1]))";
       Assert.assertEquals(expected, signatureString);
    }

    @Test
    public void testMultipleFiveCycles() throws Exception {
       String multipleFiveCycle = "C1C(CC2)CCC12"; 
       String signatureString = 
            this.canonicalStringFromSmiles(multipleFiveCycle);
       String expected = "[C]([C]([C]([C,1]))[C]([C]([C,1]))[C]([C,1]))";
       Assert.assertEquals(expected, signatureString);
    }
    
    @Test
    public void testSandwich() {
        IMolecule sandwich = AbstractSignatureTest.makeSandwich(5, true);
//        IMolecule sandwich = AbstractSignatureTest.makeSandwich(5, false);
//        toMolfileString(sandwich);
        randomPermutationTest(sandwich);
    }
    
    @Test
    public void testCubane() {
        String expected = "[C]([C]([C,4]([C,3])[C,2]([C,3]))[C]([C,4][C,1]" +
                          "([C,3]))[C]([C,1][C,2]))";
        IMolecule mol = AbstractSignatureTest.makeCubane();
        Assert.assertEquals(expected, this.canonicalStringFromMolecule(mol));
    }
    
    @Test
    public void testCage() {
        String expectedA =  "[C]([C]([C]([C,5][C,4]([C,1]))[C]([C,6][C,4]))" +
                            "[C]([C,5]([C]([C,1][C,2]))[C]([C,2]([C,3])[C,7]))"+
                            "[C]([C,6]([C]([C,1][C,3]))[C,7]([C,3])))";
        String expectedB =  "[C]([C]([C]([C]([C,2]([C,1])[C,5])[C,6])[C,8]" +
                            "([C,5]([C,4])))[C]([C]([C,4]([C,1])[C,7])[C,8])" +
                            "[C]([C,6]([C]([C,3][C,2]))[C,7]([C,3]([C,1]))))";
        IMolecule mol = AbstractSignatureTest.makeCage();
        String signature = this.canonicalStringFromMolecule(mol);
        Assert.assertEquals(expectedA, signature);
        Assert.assertFalse(expectedB.equals(signature));
        String fullSignature = fullStringFromMolecule(mol);
        String fullExpected = "8" + expectedA + " + 8" + expectedB;
//        System.out.println(fullSignature);
        Assert.assertEquals(fullExpected, fullSignature);
    }
    
    @Test
    public void testPropellane() {
        String expectedA = "[C]([C]([C,3])[C,2]([C,1][C,3])[C,2][C,1])";
        String expectedB = "[C]([C]([C,1])[C]([C,1])[C]([C,1])[C,1])";
        IMolecule mol = AbstractSignatureTest.makePropellane();
        String signature = this.canonicalStringFromMolecule(mol);
        Assert.assertEquals(expectedB, signature);
        Assert.assertFalse(expectedA.equals(signature));
    }
    
    @Test
    public void testBridgedCycloButane() {
        String expected = "[C]([C]([C,1])[C]([C,1])[C,1])";
        IMolecule mol = AbstractSignatureTest.makeBridgedCyclobutane();
        String signature = this.canonicalStringFromMolecule(mol);
        for (String atomicSignature : this.getAtomicSignatures(mol)) {
            System.out.println(atomicSignature);
        }
        Assert.assertEquals(expected, signature);
    }
    
    @Test
    public void testCageAtVariousHeights() {
        IMolecule cage = AbstractSignatureTest.makeCage();
        MoleculeSignature molSig;
        molSig = new MoleculeSignature(cage, 2);
        System.out.println(molSig.signatureStringForVertex(0, 2));
        molSig = new MoleculeSignature(cage, 3);
        System.out.println(molSig.signatureStringForVertex(0, 3));
    }

    @Test
    public void testCyclohexaneWithHydrogens() {
        IMolecule cyclohexane = MoleculeFactory.makeCyclohexane();
        for (int i = 0; i < 6; i++) {
            addHydrogens(cyclohexane, cyclohexane.getAtom(i), 2);
        }
        String expected = "[C]([C]([C]([C,1]([H][H])[H][H])[H][H])" +
        		          "[C]([C]([C,1][H][H])[H][H])[H][H])";
        
        String actual = this.canonicalStringFromMolecule(cyclohexane);
        Assert.assertEquals(expected, actual);
    }
    
    public void testSmiles(String smiles) {
        try {
            IMolecule molecule = this.parser.parseSmiles(smiles);
            MoleculeSignature sig = new MoleculeSignature(molecule);
//            System.out.println(sig.toFullString());
            System.out.println(sig.toCanonicalString());
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testCuneane() {
        String cuneaneSmiles = "C1C2C3CC4C1C4C23";
        testSmiles(cuneaneSmiles);
    }
    
    @Test
    public void testBenzeneWithDoubleBonds() {
        IMolecule benzene = builder.newInstance(IMolecule.class);
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
            Assert.assertEquals(carbonSignature, carbonSignatureI);
        }
    }
    
    @Test
    public void cyclobuteneTest() {
        IMolecule cyclobutene = builder.newInstance(IMolecule.class);
        AbstractSignatureTest.addCarbons(cyclobutene, 4);
        cyclobutene.addBond(0, 1, IBond.Order.SINGLE);
        cyclobutene.addBond(0, 2, IBond.Order.SINGLE);
        cyclobutene.addBond(1, 3, IBond.Order.DOUBLE);
        cyclobutene.addBond(2, 3, IBond.Order.SINGLE);
//        toMolfileString(cyclobutene);
        randomPermutationTest(cyclobutene);
//        System.out.println(fullStringFromMolecule(cyclobutene));
    }
    
    @Test
    public void methyleneCyclopropeneTest() {
        IMolecule mol = builder.newInstance(IMolecule.class);
        AbstractSignatureTest.addCarbons(mol, 4);
        AbstractSignatureTest.addHydrogens(mol, 1, 2);
        AbstractSignatureTest.addHydrogens(mol, 2, 1);
        AbstractSignatureTest.addHydrogens(mol, 3, 1);
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.DOUBLE);
        MoleculeSignature molSig = new MoleculeSignature(mol);
        for (int i = 0; i < 4; i++) {
            String sigForIHeight2 = molSig.signatureStringForVertex(i, 2);
            System.out.println(i + " " + sigForIHeight2);
        }
//        AbstractSignatureTest.print(mol);
//        toMolfileString(mol);
    }
    
    @Test
    public void fusedSquareMultipleBondTest() {
        IMolecule mol = builder.newInstance(IMolecule.class);
        AbstractSignatureTest.addCarbons(mol, 7);
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.addBond(2, 5, IBond.Order.SINGLE);
        mol.addBond(3, 6, IBond.Order.SINGLE);
        mol.addBond(4, 6, IBond.Order.DOUBLE);
//        toMolfileString(mol);
//        MoleculeSignature molSig = new MoleculeSignature(mol);
//        String sigFor0 = molSig.signatureStringForVertex(0);
//        System.out.println(sigFor0);
        randomPermutationTest(mol);
    }
    
    @Test
    public void testPolyPhenylMolecule() throws Exception {
        String smiles = "C1=CC=C(C=C1)P(C2=CC=CC=C2)(C3=CC=CC=C3)[RhH]" +
        		"(P(C4=CC=CC=C4)(C5=CC=CC=C5)C6=CC=CC=C6)(P(C7=CC=CC=C7)" +
        		"(C8=CC=CC=C8)C9=CC=CC=C9)P(C%10=CC=CC=C%10)" +
        		"(C%11=CC=CC=C%11)C%12=CC=CC=C%12";
//        testSmiles(smiles);
        IMolecule mol = parser.parseSmiles(smiles);
        int rhIndex = 0;
        for (int i = 0; i < mol.getAtomCount(); i++) {
            if (mol.getAtom(i).getSymbol().equals("Rh")) {
                rhIndex = i;
                break;
            }
        }
//        System.out.println("rh index = " + rhIndex);
        MoleculeSignature molSig = new MoleculeSignature(mol);
        String signatureForRh = molSig.signatureStringForVertex(rhIndex);
        System.out.println(signatureForRh);
//        toMolfileString(mol);
    }
    
    @Test
    public void methylFerroceneTest() throws Exception {
        String smiles = "CC12C3C4C5C1[Fe]23456789C%10C6C7C8C9%10";
//        testSmiles(smiles);
        IMolecule mol = parser.parseSmiles(smiles);
//        toMolfileString(mol);
        randomPermutationTest(mol);
    }
    
    @Test
    public void threeMethylSulphanylPropanal() throws Exception {
        String smiles = "O=CCCSC";
        fullPermutationTest(parser.parseSmiles(smiles));
    }
    
    @Test
    public void cycleWheelTest() {
        IMolecule mol = AbstractSignatureTest.makeCycleWheel(3, 3);
//        AbstractSignatureTest.print(mol);
//        toMolfileString(mol);
        MoleculeSignature molSig = new MoleculeSignature(mol);
        String centralSignature = molSig.signatureStringForVertex(0);
        System.out.println(centralSignature);
//        for (String signature : getAtomicSignatures(threeThreeWheel)) {
//            System.out.println(signature);
//        }
    }
    
    @Test
    public void ttprTest() {
        int phosphateCount = 3;
        int ringCount = 3;
        IMolecule ttpr = 
           AbstractSignatureTest.makeRhLikeStructure(phosphateCount, ringCount);
//        toMolfileString(ttpr);
        MoleculeSignature molSig = new MoleculeSignature(ttpr);
        String centralSignature = molSig.signatureStringForVertex(0);
        System.out.println(centralSignature);
    }
    
    @Test
    public void napthaleneSkeletonHeightTest() {
        IMolecule napthalene = builder.newInstance(IMolecule.class);
        for (int i = 0; i < 10; i++) { 
            napthalene.addAtom(builder.newInstance(IAtom.class,"C")); 
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
        Map<String, Orbit> orbits = new HashMap<String, Orbit>();
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
//        for (String key : orbits.keySet()) {
//            System.out.println(orbits.get(key));
//        }
        Assert.assertEquals(3, orbits.size());
    }
    
    @Test
    public void napthaleneWithDoubleBondsAndHydrogenHeightTest() {
        IMolecule napthalene = builder.newInstance(IMolecule.class);
        for (int i = 0; i < 10; i++) { 
            napthalene.addAtom(builder.newInstance(IAtom.class,"C")); 
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
        
        napthalene.addAtom(builder.newInstance(IAtom.class,"H"));
        napthalene.addBond(0, 10, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class,"H"));
        napthalene.addBond(3, 11, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class,"H"));
        napthalene.addBond(4, 12, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class,"H"));
        napthalene.addBond(5, 13, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class,"H"));
        napthalene.addBond(6, 14, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class,"H"));
        napthalene.addBond(7, 15, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class,"H"));
        napthalene.addBond(8, 16, IBond.Order.SINGLE);
        napthalene.addAtom(builder.newInstance(IAtom.class,"H"));
        napthalene.addBond(9, 17, IBond.Order.SINGLE);
        
        int height = 2;
//        MoleculeSignature molSig = new MoleculeSignature(napthalene);
//        for (int i = 0; i < napthalene.getAtomCount(); i++) {
//            String sigString = molSig.signatureStringForVertex(i, height);
//            System.out.println(i + " " + sigString);
//        }
        SignatureQuotientGraph mqg = 
            new SignatureQuotientGraph(napthalene, height);
        System.out.println(mqg);
        
    }
}
