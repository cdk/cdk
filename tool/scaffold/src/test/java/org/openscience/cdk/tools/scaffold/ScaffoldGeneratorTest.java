/*
 * Copyright (c) 2022 Julian Zander <zanderjulian@gmx.de>
 *                    Jonas Schaub <jonas.schaub@uni-jena.de>
 *                    Achim Zielesny <achim.zielesny@w-hs.de>
 *                    Christoph Steinbeck <christoph.steinbeck@uni-jena.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.tools.scaffold;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.aromaticity.Kekulization;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.FormatFactory;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV3000Reader;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * JUnit test class for the Scaffold Generator.
 *
 * @author Julian Zander, Jonas Schaub (zanderjulian@gmx.de, jonas.schaub@uni-jena.de)
 * @version 1.0.7.0
 */
public class ScaffoldGeneratorTest extends ScaffoldGenerator {
    /**
     * Test of ScaffoldGenerator.getScaffold() with V2000 and V3000 mol files.
     * Loads the 7 Test(Test1.mol-Test7.mol) mol files from the resources folder and creates the scaffolds
     * with getScaffold(). The correctness of the generated scaffolds is tested against unique SMILES codes from
     * previous runs that have been manually inspected.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void getScaffoldTest() throws Exception {
        HashMap<String, String> tmpFileNameToExpectedSmilesMap = new HashMap<>(10, 0.9f);
        tmpFileNameToExpectedSmilesMap.put("Test1", "O=C1c2cc3ccc(OC4OCCC(OC5OCCCC5)C4)cc3cc2CCC1OC6OCCC(OC7OCCC(OC8OCCCC8)C7)C6");
        tmpFileNameToExpectedSmilesMap.put("Test2", "O=C1C=CCOc2cc(ccc12)CC(=O)c3ccccc3");
        tmpFileNameToExpectedSmilesMap.put("Test3", "O=C(NC1C(=O)N2CCSC21)c3conc3-c4ccccc4");
        tmpFileNameToExpectedSmilesMap.put("Test4", "O=C1c2nccc3cccc(-c4ccccc14)c32");
        tmpFileNameToExpectedSmilesMap.put("Test5", "O=C(NC1C(=O)C(SC1)=C)c2ccccc2");
        tmpFileNameToExpectedSmilesMap.put("Test6", "c1ccc2cc3ccccc3cc2c1");
        tmpFileNameToExpectedSmilesMap.put("Test7", "O=C1C=Cc2cccc3cccc1c32");
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        for (int tmpCount = 1; tmpCount < 8; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from mol file
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMolecule);
            CDKHydrogenAdder.getInstance(tmpMolecule.getBuilder()).addImplicitHydrogens(tmpMolecule);
            //Generate scaffold
            IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            Assertions.assertEquals(tmpFileNameToExpectedSmilesMap.get(tmpFileName), tmpSmiGen.create(tmpScaffold));
        }
    }

    /**
     * Test of ScaffoldGenerator.getScaffold() with V2000 and V3000 mol files with the setting to fill open
     * valences from the scaffold generation with implicit hydrogen atoms turned off.
     * Loads the 7 Test(Test1.mol-Test7.mol) mol files from the resources folder and creates the scaffolds
     * with getScaffold(). The correctness of the generated scaffolds is tested against unique SMILES codes from
     * previous runs that have been manually inspected.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void getScaffoldWithoutHTest() throws Exception {
        HashMap<String, String> tmpFileNameToExpectedSmilesMap = new HashMap<>(10, 0.9f);
        tmpFileNameToExpectedSmilesMap.put("Test1", "O=C1c2[c]c3[c][c]c(OC4O[CH][CH]C(OC5O[CH][CH][CH]C5)C4)cc3cc2C[CH]C1OC6O[CH][CH]C(OC7O[CH][CH]C(OC8O[CH][CH][C]C8)C7)C6");
        tmpFileNameToExpectedSmilesMap.put("Test2", "O=C1C=[C]COc2cc(c[c]c12)CC(=O)c3cc[c]cc3");
        tmpFileNameToExpectedSmilesMap.put("Test3", "O=C(NC1C(=O)N2[CH][C]S[C]21)c3[c]onc3-c4[c]ccc[c]4");
        tmpFileNameToExpectedSmilesMap.put("Test4", "O=C1c2nccc3c[c][c]c(-c4c[c][c]cc14)c32");
        tmpFileNameToExpectedSmilesMap.put("Test5", "O=C(NC1C(=O)C(=[CH])SC1)c2ccccc2");
        tmpFileNameToExpectedSmilesMap.put("Test6", "c1ccc2cc3ccccc3cc2c1");
        tmpFileNameToExpectedSmilesMap.put("Test7", "O=C1[C]=Cc2cccc3cccc1c23");
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        for (int tmpCount = 1; tmpCount < 8; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from mol file
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMolecule);
            CDKHydrogenAdder.getInstance(tmpMolecule.getBuilder()).addImplicitHydrogens(tmpMolecule);
            //Generate scaffold
            IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
            Assertions.assertEquals(tmpFileNameToExpectedSmilesMap.get(tmpFileName), tmpSmiGen.create(tmpScaffold));
        }
    }

    /**
     * Test of ScaffoldGenerator.getScaffold() for three corner cases of multiple bonds connected to heteroatoms in a
     * ring.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScaffoldNonCTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        IAtomContainer tmpNTripleBondMolecule = tmpParser.parseSmiles("C1C(C(C(C1)=O)C)C[N](CC2CCC(C2)C)#C[H]"); //Triple bond
        IAtomContainer tmpPDoubleBondMolecule = tmpParser.parseSmiles("P=[P+]1CCCCC1"); // P=P Test
        IAtomContainer tmpSDoubleBondMolecule = tmpParser.parseSmiles("S=S1CCCCC1"); //S=S Test
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpNTripleBondMolecule, true);
        Assertions.assertEquals("O=C1CCC(C1)CN(#C)CC2CCCC2", tmpSmiGen.create(tmpScaffold));
        tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpPDoubleBondMolecule, true);
        Assertions.assertEquals("P=[P+]1CCCCC1", tmpSmiGen.create(tmpScaffold));
        tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpSDoubleBondMolecule, true);
        Assertions.assertEquals("S=S1CCCCC1", tmpSmiGen.create(tmpScaffold));
    }

    /**
     * Tests the correct performance of the internal ring removal method on one molecule.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void removeRingNonCTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CCN(C1=CC=CC(=C1)C2=CC=NC3=C(C=NN23)C#N)C(=O)C");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        Assertions.assertEquals("n1ccc(-c2ccccc2)n3nccc13", tmpSmiGen.create(tmpScaffold));
        //Generate rings
        List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRingsInternal(
                tmpScaffoldGenerator.getScaffold(tmpMolecule, true),true);
        int tmpCounter = 0;
        List<String> tmpExpectedRingSMILESList = new ArrayList<>(3);
        tmpExpectedRingSMILESList.add("n1cccn2nccc12");
        tmpExpectedRingSMILESList.add("n1ccc([nH][cH2]1)-c2ccccc2");
        tmpExpectedRingSMILESList.add("n1ccc[nH]1.c1ccccc1");
        List<String> tmpActualRingSMILESList = new ArrayList<>(3);
        for (IAtomContainer tmpRing : tmpRings) {
            IAtomContainer tmpRingRemoved =tmpScaffoldGenerator.removeRing(tmpScaffold, true, tmpRing);
            tmpActualRingSMILESList.add(tmpSmiGen.create(tmpRingRemoved));
            tmpCounter++;
        }
        Assertions.assertEquals(3, tmpCounter);
        Collections.sort(tmpExpectedRingSMILESList);
        Collections.sort(tmpActualRingSMILESList);
        for (int i = 0; i < tmpExpectedRingSMILESList.size(); i++) {
            Assertions.assertEquals(tmpExpectedRingSMILESList.get(i), tmpActualRingSMILESList.get(i));
        }
    }

    /**
     * Test of ring detection in Scaffold Generator with V2000 and V3000 mol files.
     * Loads the 7 Test(Test1.mol-Test7.mol) mol files from the resources folder and creates the rings of the
     * scaffold with getRings(). The correct number of detected rings is tested for each molecule.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void getRingsTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        HashMap<String, Integer> tmpFileNameToExpectedRingCountMap = new HashMap<>(10, 0.9f);
        tmpFileNameToExpectedRingCountMap.put("Test1", 8);
        tmpFileNameToExpectedRingCountMap.put("Test2", 3);
        tmpFileNameToExpectedRingCountMap.put("Test3", 4);
        tmpFileNameToExpectedRingCountMap.put("Test4", 4);
        tmpFileNameToExpectedRingCountMap.put("Test5", 2);
        tmpFileNameToExpectedRingCountMap.put("Test6", 3);
        tmpFileNameToExpectedRingCountMap.put("Test7", 3);
        for (int tmpCount = 1; tmpCount < 8; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from mol file
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate rings
            List<IAtomContainer> tmpRings =tmpScaffoldGenerator.getRings(tmpMolecule,true, true);
            Assertions.assertEquals((int) tmpFileNameToExpectedRingCountMap.get(tmpFileName), tmpRings.size());
        }
    }

    /**
     * Test of removeRing() with V2000 and V3000 mol files.
     * Loads the 7 Test(Test1.mol-Test7.mol) mol files from the resources folder and creates for each generated ring,
     * the corresponding remaining molecule with removed ring.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void removeRingTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        HashMap<String, String[]> tmpFileNameToRingRemovalProductsSmilesMap = new HashMap<>(10, 0.9f);
        tmpFileNameToRingRemovalProductsSmilesMap.put("Test1", new String[]
                {"O=C1c2cc3ccc(OC4OCCC(OC5OCCCC5)C4)cc3cc2CCC1OC6OCCC(OC7OCCC(OC8OCCCC8)C7)C6",
                "O=C1c2cc3ccccc3cc2CCC1OC4OCCC(OC5OCCC(OC6OCCCC6)C5)C4.O1CCC(OC2OCCCC2)CC1",
                "O=C1c2cc3ccc(OC4OCCCC4)cc3cc2CCC1OC5OCCC(OC6OCCC(OC7OCCCC7)C6)C5.O1CCCCC1"});
        tmpFileNameToRingRemovalProductsSmilesMap.put("Test2", new String[]
                {"O=C1C=CCOc2cc(ccc12)CC(=O)c3ccccc3",
                "O=C(c1ccccc1)Cc2ccc3c(OCC=CC3)c2",
                "O=Cc1ccccc1.O1c2ccccc2CC=CC1"});
        tmpFileNameToRingRemovalProductsSmilesMap.put("Test3", new String[]
                {"O=C1N2CCSC2C1.O.n1occc1-c2ccccc2",
                "n1occ(c1-c2ccccc2)CNC3CN4CCSC43",
                "O=C(N)c1conc1-c2ccccc2.O.S1CNCC1",
                "O=C(NC1C(=O)NC1)c2conc2-c3ccccc3"});
        tmpFileNameToRingRemovalProductsSmilesMap.put("Test4", new String[]
                {"n1ccc2cccc3-c4ccccc4Cc1c23",
                "O=C1c2nccc3cccc(-c4ccccc14)c32"});
        tmpFileNameToRingRemovalProductsSmilesMap.put("Test5", new String[]
                {"O=C(NC1CSC(=C)C1)c2ccccc2",
                "O=Cc1ccccc1.O=C1C(SCC1)=C"});
        tmpFileNameToRingRemovalProductsSmilesMap.put("Test6", new String[]
                {"c1ccc2ccccc2c1",
                "c1ccccc1.c1ccccc1"});
        tmpFileNameToRingRemovalProductsSmilesMap.put("Test7", new String[]
                {"c1cc2cccc3c2c(c1)C=CC3"});
        for (int tmpCount = 1; tmpCount < 8; tmpCount++) {
            String tmpFileName = "Test" + tmpCount ;
            //Load molecule from mol file
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate scaffold
            IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            //Generate Rings
            List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRings(tmpScaffold, true, true);
            for (IAtomContainer tmpRing : tmpRings) {
                /*Generate scaffold with removed ring*/
                IAtomContainer tmpRemovedRing = tmpScaffoldGenerator.removeRing(tmpScaffold, true, tmpRing);
                try {
                    Assertions.assertTrue(Arrays.asList(tmpFileNameToRingRemovalProductsSmilesMap.get(tmpFileName)).contains(tmpSmiGen.create(tmpRemovedRing)));
                } catch (AssertionError e) {
                    System.out.println(tmpFileName);
                    System.out.println(tmpSmiGen.create(tmpRemovedRing));
                }
            }
        }
    }

    /**
     * Test of isRingTerminal() with V2000 and V3000 mol files.
     * Loads the 7 Test(Test1.mol-Test7.mol) mol files from the resources folder and creates for each generated terminal
     * ring, the corresponding total molecule with removed ring.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void isRingTerminalTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        for (int tmpCount = 1; tmpCount < 8; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from mol file
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate scaffold
            IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            //Generate Rings
            List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRingsInternal(tmpScaffold, true);
            for (IAtomContainer tmpRing : tmpRings) {
                //Generate scaffold with removed ring
                IAtomContainer tmpRemovedRing =tmpScaffoldGenerator.removeRing(tmpScaffold, true, tmpRing);
                if(tmpScaffoldGenerator.isRingTerminal(tmpScaffold, tmpRing)) {
                    Assertions.assertFalse(tmpSmiGen.create(tmpRemovedRing).contains("."));
                }
            }
        }
    }

    /**
     * Test of getSideChains() with V2000 and V3000 mol files.
     * Loads the 7 Test(Test1.mol-Test7.mol) mol files from the resources folder.
     *
     * @throws Exception If anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void getSideChainsTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        HashMap<String, String[]> tmpFileNameToSideChainsSmilesMap = new HashMap<>(10, 0.9f);
        tmpFileNameToSideChainsSmilesMap.put("Test1", new String[]{"C", "O", "O=C(COC)C(O)C(O)C", "O=C(O)C", "OC"});
        tmpFileNameToSideChainsSmilesMap.put("Test2", new String[]{"C", "O", "OC"});
        tmpFileNameToSideChainsSmilesMap.put("Test3", new String[]{"C", "O=CO", "F", "Cl"});
        tmpFileNameToSideChainsSmilesMap.put("Test4", new String[]{"OC"});
        tmpFileNameToSideChainsSmilesMap.put("Test5", new String[]{"O=C(OC)CCC"});
        tmpFileNameToSideChainsSmilesMap.put("Test6", new String[]{});
        tmpFileNameToSideChainsSmilesMap.put("Test7", new String[]{"O"});
        for (int tmpCount = 1; tmpCount < 8; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from mol file
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate SideChains
            List<IAtomContainer> tmpSideChains = tmpScaffoldGenerator.getSideChains(tmpMolecule, true);
            for (IAtomContainer tmpSideChain : tmpSideChains) {
                Assertions.assertTrue(Arrays.asList(tmpFileNameToSideChainsSmilesMap.get(tmpFileName)).contains(tmpSmiGen.create(tmpSideChain)));
            }
        }
    }

    /**
     * Tests the correct generation of the different scaffold types on one test molecule.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void getScaffoldModeTest() throws Exception {
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        //Load molecule from mol file
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/Test3.mol");
        //Generate the scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)c3conc3-c4ccccc4", tmpSmiGen.create(tmpScaffold));
        //Generate Murcko framework
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
        IAtomContainer tmpMurckoFramework = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        Assertions.assertEquals("n1occ(c1-c2ccccc2)CNC3CN4CCSC43", tmpSmiGen.create(tmpMurckoFramework));
        //Generate elemental wireframe
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
        IAtomContainer tmpElementalWireframe = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        Assertions.assertEquals("O1NC(C(C1)CNC2CN3CCSC32)C4CCCCC4", tmpSmiGen.create(tmpElementalWireframe));
        //Generate basic framework
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.BASIC_FRAMEWORK);
        IAtomContainer tmpBasicFramework = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        Assertions.assertEquals("c1ccc(cc1)C2=CCC=C2CCC3CC4CCCC34", tmpSmiGen.create(tmpBasicFramework));
        //Generate basic wireframe
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.BASIC_WIRE_FRAME);
        IAtomContainer tmpBasicWireframe = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        Assertions.assertEquals("C1CCC(CC1)C2CCCC2CCC3CC4CCCC43", tmpSmiGen.create(tmpBasicWireframe));
    }

    /**
     * Test of getLinkers() with V2000 and V3000 mol files.
     * Loads the 7 Test(Test1.mol-Test7.mol) mol files from the resources folder.
     *
     * @throws Exception If anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void getLinkersTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        HashMap<String, String[]> tmpFileNameToLinkerSmilesMap = new HashMap<>(10, 0.9f);
        tmpFileNameToLinkerSmilesMap.put("Test1", new String[]{"O"});
        tmpFileNameToLinkerSmilesMap.put("Test2", new String[]{"O=CC"});
        tmpFileNameToLinkerSmilesMap.put("Test3", new String[]{"O=CN"});
        tmpFileNameToLinkerSmilesMap.put("Test4", new String[]{});
        tmpFileNameToLinkerSmilesMap.put("Test5", new String[]{"O=CN"});
        tmpFileNameToLinkerSmilesMap.put("Test6", new String[]{});
        tmpFileNameToLinkerSmilesMap.put("Test7", new String[]{});
        for (int tmpCount = 1; tmpCount < 8; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from mol file
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate Linker
            List<IAtomContainer> tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
            for (IAtomContainer tmpLinker : tmpLinkers) {
                Assertions.assertTrue(Arrays.asList(tmpFileNameToLinkerSmilesMap.get(tmpFileName)).contains(tmpSmiGen.create(tmpLinker)));
            }
        }
    }

    /**
     * Loads a molecule with a C=N linker from a SMILES string and generates the corresponding linker for the
     * different scaffold types. This case is relevant because the linker atoms are exchanged in some scaffold types.
     *
     * @throws Exception If anything goes wrong
     */
    @Test
    public void getLinkersDoubleBondTest() throws Exception {
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C=C1CCCCC1/N=C/C2CC(C)CCC2C");
        //Generate the scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        /*Generate the linkers of the scaffold*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.SCAFFOLD);
        List<IAtomContainer> tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        Assertions.assertEquals("N=C",tmpSmilesGenerator.create(tmpLinkers.get(0)));
        /*Generate the linkers of the Murcko Framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
        tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        Assertions.assertEquals("N=C",tmpSmilesGenerator.create(tmpLinkers.get(0)));
        /*Generate the linkers of the Elemental Wire Frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
        tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        Assertions.assertEquals("NC",tmpSmilesGenerator.create(tmpLinkers.get(0)));
        /*Generate the linkers of the Basic Framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.BASIC_FRAMEWORK);
        tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        Assertions.assertEquals("C=C",tmpSmilesGenerator.create(tmpLinkers.get(0)));
        /*Generate the linkers of the Basic Wire Frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.BASIC_WIRE_FRAME);
        tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        Assertions.assertEquals("CC",tmpSmilesGenerator.create(tmpLinkers.get(0)));
    }

    /**
     * Generates parent scaffolds with the enumerative routine from a V2000 or V3000 mol file and checks the generated fragments.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void getIterativeRemovalTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        //Load molecule from mol file
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/Test3.mol");
        List <IAtomContainer> tmpMoleculeList = tmpScaffoldGenerator.applyEnumerativeRemoval(tmpMolecule);
        for (IAtomContainer tmpScaffold : tmpMoleculeList) {
            System.out.println(tmpSmiGen.create(tmpScaffold));
        }
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)c3conc3-c4ccccc4", tmpSmiGen.create(tmpMoleculeList.get(0)));
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)c3cnoc3", tmpSmiGen.create(tmpMoleculeList.get(1)));
        Assertions.assertEquals("O=C(NC1C(=O)NC1)c2conc2-c3ccccc3", tmpSmiGen.create(tmpMoleculeList.get(2)));
        Assertions.assertEquals("O=C1N2CCSC2C1", tmpSmiGen.create(tmpMoleculeList.get(3)));
        Assertions.assertEquals("O=C(NC1C(=O)NC1)c2cnoc2", tmpSmiGen.create(tmpMoleculeList.get(4)));
        Assertions.assertEquals("n1occc1-c2ccccc2", tmpSmiGen.create(tmpMoleculeList.get(5)));
        Assertions.assertEquals("S1CNCC1", tmpSmiGen.create(tmpMoleculeList.get(6)));
        Assertions.assertEquals("O=C1NCC1", tmpSmiGen.create(tmpMoleculeList.get(7)));
        Assertions.assertEquals("n1occc1", tmpSmiGen.create(tmpMoleculeList.get(8)));
        Assertions.assertEquals("c1ccccc1", tmpSmiGen.create(tmpMoleculeList.get(9)));
    }

    /**
     * Creates a scaffold tree from a V2000 or V3000 mol file and checks the generated fragments.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void generateSchuffenhauerTreeTest() throws Exception {
        //Load molecule from mol file
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/Test3.mol");
        //Generate a tree of molecules with iteratively removed terminal rings following the Schuffenhauer rules
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        List <IAtomContainer> tmpMoleculeList = new ArrayList<>();
        for(ScaffoldNodeBase<IAtomContainer> tmpTestNodeBase : tmpScaffoldTree.getAllNodes()) {
            TreeNode<IAtomContainer> tmpTestNode = (TreeNode<IAtomContainer>) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = tmpTestNode.getMolecule();
            tmpMoleculeList.add(tmpTestMolecule);
            String tmpOrigin = tmpTestNodeBase.getOriginSmilesList().get(0);
            Assertions.assertEquals("[H]C12SC(C)(C)C(C(=O)O)N2C(=O)C1NC(=O)C=3C(=NOC3C)C=4C(F)=CC=CC4Cl", tmpOrigin);
        }
        Assertions.assertEquals("O=C1NCC1", tmpSmilesGenerator.create(tmpMoleculeList.get(0)));
        Assertions.assertEquals("O=C1N2CCSC2C1", tmpSmilesGenerator.create(tmpMoleculeList.get(1)));
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)C=3C=NOC3", tmpSmilesGenerator.create(tmpMoleculeList.get(2)));
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)C3=CON=C3C=4C=CC=CC4", tmpSmilesGenerator.create(tmpMoleculeList.get(3)));
    }

    /**
     * Creates a scaffold network from a V2000 or V3000 mol file and checks the generated fragments.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void generateScaffoldNetworkTest() throws Exception {
        //Load molecule from mol file
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/Test3.mol");
        //Generate a tree of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule);
        /*Remove some nodes. Nodes can be removed from the non-root end.
        If nodes are removed in the middle of the tree, it becomes invalid*/
        NetworkNode tmpRemoveNode = (NetworkNode) tmpScaffoldNetwork.getMatrixNode(9);
        tmpScaffoldNetwork.removeNode(tmpRemoveNode);
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        List <String> tmpStringList = new ArrayList<>();
        for(ScaffoldNodeBase<IAtomContainer> tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode<IAtomContainer> tmpTestNode = (NetworkNode<IAtomContainer>) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = tmpTestNode.getMolecule();
            tmpStringList.add(tmpSmilesGenerator.create(tmpTestMolecule));
            Assertions.assertEquals("[H]C12SC(C)(C)C(C(=O)O)N2C(=O)C1NC(=O)C=3C(=NOC3C)C=4C(F)=CC=CC4Cl", tmpTestNode.getOriginSmilesList().get(0));
            if(!tmpTestNode.getNonVirtualOriginCount().equals(0)) {
                Assertions.assertEquals("[H]C12SC(C)(C)C(C(=O)O)N2C(=O)C1NC(=O)C=3C(=NOC3C)C=4C(F)=CC=CC4Cl", tmpTestNode.getNonVirtualOriginSmilesList().get(0));
            }
        }
        Collections.sort(tmpStringList);
        Assertions.assertEquals("C=1C=CC=CC1", tmpStringList.get(0));
        Assertions.assertEquals("N=1OC=CC1", tmpStringList.get(1));
        Assertions.assertEquals("N=1OC=CC1C=2C=CC=CC2", tmpStringList.get(2));
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)C3=CON=C3C=4C=CC=CC4", tmpStringList.get(3));
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)C=3C=NOC3", tmpStringList.get(4));
        Assertions.assertEquals("O=C(NC1C(=O)NC1)C2=CON=C2C=3C=CC=CC3", tmpStringList.get(5));
        Assertions.assertEquals("O=C(NC1C(=O)NC1)C=2C=NOC2", tmpStringList.get(6));
        Assertions.assertEquals("O=C1N2CCSC2C1", tmpStringList.get(7));
        Assertions.assertEquals("O=C1NCC1", tmpStringList.get(8));
        Assertions.assertEquals("S1CNCC1", tmpStringList.get(9));
    }

    /**
     * Creates multiple scaffold trees and merges them.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeTreeTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("c2ccc(C1NCNN1)cc2");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c2ccc1CCCc1c2");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c3ccc(C2NC1SCNN1N2)cc3");
        //Generate a tree of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        ScaffoldTree tmpScaffoldTree2 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule2);
        ScaffoldTree tmpScaffoldTree3 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule3);
        ScaffoldTree tmpScaffoldTree4 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule4);
        ScaffoldTree tmpScaffoldTree5 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule5);
        ScaffoldTree tmpScaffoldTree6 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule6);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree2);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree3);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree4);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree5);
        ScaffoldTree tmpOverlapScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree2);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree3);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree4);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree6);
        tmpScaffoldTree.mergeTree(tmpOverlapScaffoldTree);
        ScaffoldTree tmpUnfitScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);
        /*Does the new tree fit in the old one*/
        Assertions.assertFalse(tmpScaffoldTree.mergeTree(tmpUnfitScaffoldTree));
        /*Check number of nodes*/
        Assertions.assertEquals(7, tmpScaffoldTree.getAllNodes().size());
        IAtomContainer tmpRootMolecule = (IAtomContainer) tmpScaffoldTree.getRoot().getMolecule();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        /*Check root*/
        Assertions.assertEquals("N1NCNC1", tmpSmilesGenerator.create(tmpRootMolecule));
        IAtomContainer tmpMoleculeFive = (IAtomContainer) tmpScaffoldTree.getMatrixNode(5).getMolecule();
        /*Check molecule 5*/
        Assertions.assertEquals("C=1C=CC(=CC1)C2NNC(N2)C=3C=CC=CC3", tmpSmilesGenerator.create(tmpMoleculeFive));
    }

    /**
     * Creates multiple scaffold networks and merges them.
     * A network is added here that has no connection to the rest of the network.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeNetworkTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        /*Generate IAtomContainer from SMILES*/
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        //Molecule without connection to the network
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("C3CC1CC1CC4CCC2CC2CC34");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        //Generate a Network of molecules with iteratively removed terminal rings
        /*Generate Networks*/
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule1);
        ScaffoldNetwork tmpScaffoldNetwork2 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule2);
        ScaffoldNetwork tmpScaffoldNetwork3 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule3);
        ScaffoldNetwork tmpScaffoldNetwork4 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule4);
        /*Merge Networks*/
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork2);
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork3);
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork4);
        /*Checks*/
        Assertions.assertEquals(5, tmpScaffoldNetwork.getRoots().size());
        Assertions.assertEquals(16, tmpScaffoldNetwork.getAllNodes().size());
        Assertions.assertEquals(6, tmpScaffoldNetwork.getAllNodesOnLevel(0).size());
        Assertions.assertEquals(3, tmpScaffoldNetwork.getMaxLevel());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        IAtomContainer tmpNodeMolecule = (IAtomContainer) tmpScaffoldNetwork.getMatrixNode(8).getMolecule();
        Assertions.assertEquals("S1C(NN2NC(NC12)C=3C=CC=CC3)C=4C=CC=CC4", tmpSmilesGenerator.create(tmpNodeMolecule));
    }

    /**
     * Loads two stereo-isomers as SMILES and joins them as a tree. Since the SMILESGenerator setting is "Isomeric",
     * the stereochemistry is kept in consideration and the two molecules are represented in the tree as two different ones.
     * The structure is similar to the method "merge*Non*StereoMoleculesToForestTest()" except for the setting.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeStereoMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpTestMoleculeList = new ArrayList<>();
        /*Loading and reading the library*/
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C4CCC(C/C(CC1CC[F+]CC1)=C(CC2CCCCC2)\\CC3CC[F+]CC3)CC4");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C4CCC(C/C(CC1CC[F+]CC1)=C(CC2CCCCC2)/CC3CC[F+]CC3)CC4");
        tmpTestMoleculeList.add(tmpMolecule);
        tmpTestMoleculeList.add(tmpMolecule1);
        tmpScaffoldGenerator.setSmilesGeneratorSetting(new SmilesGenerator(SmiFlavor.Isomeric));
        List<ScaffoldTree> tmpTestTreeList = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTestMoleculeList);
        Assertions.assertEquals(2, tmpTestMoleculeList.size());
        Assertions.assertEquals(1, tmpTestTreeList.size());
        ScaffoldTree tmpScaffoldTree = tmpTestTreeList.get(0);
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Isomeric);
        Assertions.assertEquals(5, tmpScaffoldTree.getAllNodes().size());
        Assertions.assertEquals("C1CC[F+]CC1", tmpSmilesGenerator.create((IAtomContainer) tmpScaffoldTree.getRoot().getMolecule()));
    }

    /**
     * Loads two stereo-isomers as SMILES and joins them as a tree. Since the SMILESGenerator setting is "Unique",
     * the stereochemistry is ignored and the two molecules are represented as one in the tree.
     * The structure is similar to the method "mergeStereoMoleculesToForestTest()" except for the setting.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeNonStereoMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpTestMoleculeList = new ArrayList<>();
        /*Loading and reading the library*/
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C4CCC(C/C(CC1CC[F+]CC1)=C(CC2CCCCC2)\\CC3CC[F+]CC3)CC4");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C4CCC(C/C(CC1CC[F+]CC1)=C(CC2CCCCC2)/CC3CC[F+]CC3)CC4");
        tmpTestMoleculeList.add(tmpMolecule);
        tmpTestMoleculeList.add(tmpMolecule1);
        tmpScaffoldGenerator.setSmilesGeneratorSetting(new SmilesGenerator(SmiFlavor.Unique));
        List<ScaffoldTree> tmpTestTreeList = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTestMoleculeList);
        Assertions.assertEquals(2, tmpTestMoleculeList.size());
        Assertions.assertEquals(1, tmpTestTreeList.size());
        ScaffoldTree tmpScaffoldTree = tmpTestTreeList.get(0);
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals(4, tmpScaffoldTree.getAllNodes().size());
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        Assertions.assertEquals("[F+]1CCCCC1", tmpSmilesGenerator.create((IAtomContainer) tmpScaffoldTree.getRoot().getMolecule()));
        Assertions.assertEquals("[F+]1CCC(CC(=C(CC2CC[F+]CC2)CC3CCCCC3)CC4CCCCC4)CC1", tmpSmilesGenerator.create(tmpScaffold));
    }

    /**
     * Loads a molecule without cycles from a mol file and checks its applySchuffenhauerRules/applyEnumerativeRemoval output.
     * The output should be on empty fragment in both cases.
     *
     * @throws Exception If anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void nonCyclicTest() throws Exception {
        //Load molecule from mol file
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/TestNonCyclic.mol");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpMoleculeList = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Check if there is only one empty fragment there*/
        Assertions.assertEquals(1, tmpMoleculeList.size());
        Assertions.assertEquals(0, tmpMoleculeList.get(0).getAtomCount());
        tmpMoleculeList = tmpScaffoldGenerator.applyEnumerativeRemoval(tmpMolecule);
        /*Check if there is only one empty fragment there*/
        Assertions.assertEquals(1, tmpMoleculeList.size());
        Assertions.assertEquals(0, tmpMoleculeList.get(0).getAtomCount());
    }

    /**
     * Loads adamantane as a mol file and checks its applySchuffenhauerRules/applyEnumerativeRemoval Output.
     * The output should be only one Fragment.
     *
     * @throws Exception If anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void adamantaneTest() throws Exception {
        //Load molecule from mol file
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/TestAdamantane.mol");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpMoleculeList = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Check if there is only one fragment there*/
        Assertions.assertEquals(1, tmpMoleculeList.size());
        Assertions.assertEquals(10, tmpMoleculeList.get(0).getAtomCount());
        tmpMoleculeList = tmpScaffoldGenerator.applyEnumerativeRemoval(tmpMolecule);
        /*Check if there is only one fragment there*/
        Assertions.assertEquals(1, tmpMoleculeList.size());
        Assertions.assertEquals(10, tmpMoleculeList.get(0).getAtomCount());
    }

    /**
     * Loads pyrene as a mol file and checks its Schuffenhauer fragments with/without determined aromaticity.
     * The output should be only one fragment.
     *
     * @throws Exception If anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void pyreneTest() throws Exception {
        //Load molecule from mol file
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/TestPyrene.mol");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        /*With aromaticity and with retaining only hybridisation at aromatic bonds*/
        tmpScaffoldGenerator.setDetermineAromaticitySetting(true);
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(true);
        List<IAtomContainer> tmpMoleculeList = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Check if there is only one fragment there*/
        Assertions.assertEquals(1, tmpMoleculeList.size());
        Assertions.assertEquals(16, tmpMoleculeList.get(0).getAtomCount());
        /*With aromaticity and without only retaining hybridisation at aromatic bonds*/
        tmpScaffoldGenerator.setDetermineAromaticitySetting(true);
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(false);
        tmpMoleculeList = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Check if there is only one fragment there*/
        Assertions.assertEquals(1, tmpMoleculeList.size());
        Assertions.assertEquals(16, tmpMoleculeList.get(0).getAtomCount());
        /*Without aromaticity and with only retaining hybridisation at aromatic bonds*/
        tmpScaffoldGenerator.setDetermineAromaticitySetting(false);
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(true);
        tmpMoleculeList = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*In this case, pyrene should be fragmented, check whether there are multiple parent scaffolds*/
        Assertions.assertEquals(4, tmpMoleculeList.size());
        Assertions.assertEquals(16, tmpMoleculeList.get(0).getAtomCount());
        /*Without aromaticity and without only retaining hybridisation at aromatic bonds*/
        tmpScaffoldGenerator.setDetermineAromaticitySetting(false);
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(false);
        tmpMoleculeList = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Check if there is only one fragment there*/
        Assertions.assertEquals(1, tmpMoleculeList.size());
        Assertions.assertEquals(16, tmpMoleculeList.get(0).getAtomCount());
    }

    /**
     * Tests the network decomposition on two intertwined ring systems.
     * None of the nodes should be empty.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void heteroThreeGenerateScaffoldNetworkTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("O=C(O)C(NC(=O)C(N)C(C)C)C1N2CC2C(N=C(N)N)C1");
        //Generate a network of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule1);
        Assertions.assertEquals(3, tmpScaffoldNetwork.getAllNodes().size());
        for(ScaffoldNodeBase<IAtomContainer> tmpNode : tmpScaffoldNetwork.getAllNodes()) {
            IAtomContainer tmpNodeMolecule = tmpNode.getMolecule();
            boolean tmpIsEmpty = tmpNodeMolecule.getAtomCount() == 0;
            Assertions.assertFalse(tmpIsEmpty);
        }
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("O=C1C=C2C(=CC=C1OC)C3=C(OC)C(=O)C(O)=CC34N5C2CC54C");
        tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule2);
        for(ScaffoldNodeBase<IAtomContainer> tmpNode : tmpScaffoldNetwork.getAllNodes()) {
            IAtomContainer tmpNodeMolecule = tmpNode.getMolecule();
            boolean tmpIsEmpty = tmpNodeMolecule.getAtomCount() == 0;
            Assertions.assertFalse(tmpIsEmpty);
        }
        Assertions.assertEquals(17, tmpScaffoldNetwork.getAllNodes().size());
    }

    /**
     * Tests the enumerative removal on two intertwined ring systems.
     * None of the nodes should be empty.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void heteroGenerateSchuffenhauerRemovalTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("O=C(O)C(NC(=O)C(N)C(C)C)C1N2CC2C(N=C(N)N)C1");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldTree tmpTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        for(ScaffoldNodeBase<IAtomContainer> tmpNode : tmpTree.getAllNodes()) {
            IAtomContainer tmpNodeMolecule = tmpNode.getMolecule();
            boolean tmpIsEmpty = tmpNodeMolecule.getAtomCount() == 0;
            Assertions.assertFalse(tmpIsEmpty);
        }
        Assertions.assertEquals(2, tmpTree.getAllNodes().size());
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("O=C1C=C2C(=CC=C1OC)C3=C(OC)C(=O)C(O)=CC34N5C2CC54C");
        tmpTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule2);
        for(ScaffoldNodeBase<IAtomContainer> tmpNode : tmpTree.getAllNodes()) {
            IAtomContainer tmpNodeMolecule = tmpNode.getMolecule();
            boolean tmpIsEmpty = tmpNodeMolecule.getAtomCount() == 0;
            Assertions.assertFalse(tmpIsEmpty);
        }
        Assertions.assertEquals(5, tmpTree.getAllNodes().size());
    }

    /**
     * Loads a molecule with two stereo-centers from a SMILES code.
     * Shows that one piece of information is retained even though the other has already been removed.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void stereoFragmentationTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setSmilesGeneratorSetting(new SmilesGenerator(SmiFlavor.Isomeric));
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles(
                "C8CCCC(/C(=C(C1CCCNCCC1)\\C6CCCCC(C5CCCC(C/C(CC2CCNCC2)=C(CC3CCCCC3)/CC4CCNCC4)C5)CC6)C7CCCCCNC7)CCC8");
        List<IAtomContainer> tmpFragmentList = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Isomeric);
        String[] tmpParentScaffoldsSmilesArray = new String[] {
                "C1CCCC(/C(=C(/C2CCCNCCC2)\\C3CCCCC(C4CCCC(C/C(/CC5CCNCC5)=C(\\CC6CCCCC6)/CC7CCNCC7)C4)CC3)/C8CCCCCNC8)CCC1",
                "C1CCCC(/C(=C(/C2CCCNCCC2)\\C3CCCCC(C4CCCC(C/C(/CC5CCNCC5)=C/CC6CCNCC6)C4)CC3)/C7CCCCCNC7)CCC1",
                "C1CCCC(/C(=C(/C2CCCNCCC2)\\C3CCCCC(C4CCCC(CC(CC5CCNCC5)=C)C4)CC3)/C6CCCCCNC6)CCC1",
                "C1CCCC(/C(=C(/C2CCCNCCC2)\\C3CCCCC(C4CCCCC4)CC3)/C5CCCCCNC5)CCC1",
                "C1CCCC(/C(=C(/C2CCCNCCC2)\\C3CCCCCCC3)/C4CCCCCNC4)CCC1",
                "C(=C(C1CCCNCCC1)C2CCCCCCC2)C3CCCCCNC3",
                "C(C1CCCNCCC1)(C2CCCCCCC2)=C",
                "C1CCCNCCC1"
        };
        for (int i = 0; i < tmpFragmentList.size(); i++) {
            Assertions.assertEquals(tmpParentScaffoldsSmilesArray[i], tmpSmilesGenerator.create(tmpFragmentList.get(i)));
        }
    }

    /**
     * Test of ScaffoldGenerator.applySchuffenhauerRules() with V2000 and V3000 mol files.
     * Loads the Test(Test1.mol-Test7.mol) mol files from the resources folder and compares their generated parent
     * scaffolds to defined structures that represented the expected results.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void applySchuffenhauerRulesTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        HashMap<String, String[]> tmpFileNameToParentScaffoldSmilesMap = new HashMap<>(10, 0.9f);
        tmpFileNameToParentScaffoldSmilesMap.put("Test1", new String[]
                {"O=C1c2cc3ccc(OC4OCCC(OC5OCCCC5)C4)cc3cc2CCC1OC6OCCC(OC7OCCC(OC8OCCCC8)C7)C6",
                "O=C1c2cc3ccc(OC4OCCC(OC5OCCCC5)C4)cc3cc2CCC1OC6OCCC(OC7OCCCC7)C6",
                "O=C1c2cc3ccc(OC4OCCC(OC5OCCCC5)C4)cc3cc2CCC1OC6OCCCC6",
                "O=C1c2cc3ccc(OC4OCCC(OC5OCCCC5)C4)cc3cc2CCC1",
                "O=C1c2cc3ccc(OC4OCCCC4)cc3cc2CCC1",
                "O=C1c2cc3ccccc3cc2CCC1",
                "O=C1c2ccccc2CCC1",
                "O=C1C=CCCC1"});
        tmpFileNameToParentScaffoldSmilesMap.put("Test2", new String[]
                {"O=C1C=CCOc2cc(ccc12)CC(=O)c3ccccc3",
                "O=C1C=CCOc2ccccc12",
                "O=C1C=COCC=C1"});
        tmpFileNameToParentScaffoldSmilesMap.put("Test3", new String[]
                {"O=C(NC1C(=O)N2CCSC21)c3conc3-c4ccccc4",
                "O=C(NC1C(=O)N2CCSC21)c3cnoc3",
                "O=C1N2CCSC2C1",
                "O=C1NCC1"});
        tmpFileNameToParentScaffoldSmilesMap.put("Test4", new String[]
                {"O=C1c2nccc3cccc(-c4ccccc14)c32",
                "O=C1C=Cc2cccc3ccnc1c32"});
        tmpFileNameToParentScaffoldSmilesMap.put("Test5", new String[]
                {"O=C(NC1C(=O)C(SC1)=C)c2ccccc2",
                "O=C1C(SCC1)=C"});
        tmpFileNameToParentScaffoldSmilesMap.put("Test6", new String[]
                {"c1ccc2cc3ccccc3cc2c1",
                "c1ccc2ccccc2c1",
                "c1ccccc1"});
        tmpFileNameToParentScaffoldSmilesMap.put("Test7", new String[]{"O=C1C=Cc2cccc3cccc1c32"});
        for (int tmpCount = 1; tmpCount < 8; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from mol file
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            List<IAtomContainer> tmpSchuffenhauerFragments = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
            for(int i = 0; i < tmpSchuffenhauerFragments.size(); i++) {
               Assertions.assertEquals(tmpFileNameToParentScaffoldSmilesMap.get(tmpFileName)[i],
                       tmpSmiGen.create(tmpSchuffenhauerFragments.get(i)));
            }
        }
    }

    /**
     * Test of ScaffoldGenerator.applySchuffenhauerRules() with a complex structure defined as SMILES code.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void applySchuffenhauerRulesSMILESTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C5CCCCCCCCCCCCCC1NC1CCCCCCCCCCCCCC(C3CC2CC2C4NC34)CC5");
        String[] tmpExpectedSmilesArray = new String[]{"N1C2CCCCCCCCCCCCCCCCC(CCCCCCCCCCCCCC12)C3CC4CC4C5NC53",
                "C1=CC2CC2CC1C3CCCCCCCCCCCCCCCCC4NC4CCCCCCCCCCCCC3",
                "C1=CCCCCCCCCCCCCCC(CCCCCCCCCCCCCCCC1)C2C=CC3CC3C2",
                "C1=CCCCCCCCCCCCCCC(CCCCCCCCCCCCCCCC1)C2C=CCCC2",
                "C1=CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC1"};
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setDetermineAromaticitySetting(true);
        List<IAtomContainer> tmpSchuffenhauerFragments = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        for(int i = 0; i < tmpExpectedSmilesArray.length; i++) {
            Assertions.assertEquals(tmpExpectedSmilesArray[i], tmpSmiGen.create(tmpSchuffenhauerFragments.get(i)));
        }
    }
    /**
     * Test of ScaffoldGenerator.getScaffold() with SMILES.
     * Loads scheme 1 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES and generates the
     * scaffold.
     * Flucloxacillin is generated from the SMILES and all terminal side chains are removed. Rings, linkers and double
     * bonds on these structures are obtained.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme1Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)c3conc3-c4ccccc4", tmpSmilesGenerator.create(tmpScaffold));
    }

    /**
     * Loads scheme 2b from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Adamantane is generated from the SMILES and it is checked whether rings can be removed. This should not be the case.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme2bTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1C2CC3CC1CC(C2)C3");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        //Get rings
        List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRingsInternal(tmpScaffold, true);
        for (IAtomContainer tmpRing : tmpRings) {
            boolean tmpIsRingRemovable = tmpScaffoldGenerator.isRingRemovable(tmpScaffold, tmpRings, tmpRing);
            /*Remove rings*/
            Assertions.assertFalse(tmpIsRingRemovable);
        }
    }

    /**
     * Loads scheme 3a from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * A molecule consisting of two rings is generated from a SMILES.
     * One of these rings is aromatic and has to be removed.
     * At the point where this aromatic ring was bound to the other ring, a double bond should be inserted.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme3aTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("c2ccc1CNCCc1c2");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        //Get rings
        List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRingsInternal(tmpScaffold, true);
        //Remove Ring
        IAtomContainer tmpRemovedRing =tmpScaffoldGenerator.removeRing(tmpScaffold, true, tmpRings.get(1));
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        //Kekulization works without a problem but the aromaticity flags have to be removed to show the inserted double bond
        Kekulization.kekulize(tmpRemovedRing);
        for (IAtom tmpAtom : tmpRemovedRing.atoms()) {
            if (tmpAtom.isAromatic()) {
                tmpAtom.setIsAromatic(false);
            }
        }
        for (IBond tmpBond : tmpRemovedRing.bonds()) {
            if (tmpBond.isAromatic()) {
                tmpBond.setIsAromatic(false);
            }
        }
        Assertions.assertEquals("C1=CCCNC1", tmpSmilesGenerator.create(tmpRemovedRing));
    }

    /**
     * Loads scheme 3b from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * A molecule consisting of three rings is generated from a SMILES. One of these rings is aromatic.
     * It is tested whether this aromatic ring can be removed. This should not be the case.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme3bTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("c1cc2CCCc3c[nH]c(c1)c23");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        //Get rings
        List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRingsInternal(tmpScaffold, true);
        Assertions.assertFalse(tmpScaffoldGenerator.isRingRemovable(tmpRings.get(0), tmpRings, tmpScaffold));
        Assertions.assertFalse(tmpScaffoldGenerator.isRingRemovable(tmpRings.get(1), tmpRings, tmpScaffold));
        Assertions.assertTrue(tmpScaffoldGenerator.isRingRemovable(tmpRings.get(2), tmpRings, tmpScaffold));
        IAtomContainer tmpOnlyPossibleParentScaffold = tmpScaffoldGenerator.removeRing(tmpScaffold, true, tmpRings.get(2));
        Assertions.assertEquals("c1ccc2[nH]ccc2c1", tmpSmiGen.create(tmpOnlyPossibleParentScaffold));
    }

    /**
     * Loads scheme 4 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Epothilone A is generated from a SMILES and the ring consisting of 3 atoms is removed.
     * The removal of this hetero ring should result in a double bond at the removed position.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme4Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1CCCC2C(O2)CC(OC(=O)CC(C(C(=O)C(C1O)C)(C)C)O)C(=CC3=CSC(=N3)C)C");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRuleOneOutput = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        //First scaffold is the direct scaffold, second one is the first parent scaffold which is on interest here
        Assertions.assertEquals("O=C1OC(C=CC=2N=CSC2)CC=CCCCCCCC(=O)CCC1", tmpSmilesGenerator.create(tmpRuleOneOutput.get(1)));
    }

    /**
     * Loads scheme 4 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Epothilone A is generated from a SMILES and the ring consisting of 3 atoms is removed.
     * The removal of this hetero ring should result in a double bond at the removed position.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme4Rule1OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1CCCC2C(O2)CC(OC(=O)CC(C(C(=O)C(C1O)C)(C)C)O)C(=CC3=CSC(=N3)C)C");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleOne(this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C1OC(C=CC=2N=CSC2)CC=CCCCCCCC(=O)CCC1", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 5 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Seglitide is generated from a SMILES and the two single rings connected via linker are removed.
     * Then, according to the second rule, the aromatic six-membered ring is removed and not the macro-ring.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme5Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1C(=O)NC(C(=O)NC(C(=O)NC(C(=O)NC(C(=O)NC(C(=O)N1C)CC2=CC=CC=C2)C(C)C)CCCCN)CC3=CNC4=CC=CC=C43)CC5=CC=C(C=C5)O");//Original
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        //Get rings
        List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRingsInternal(tmpScaffold, true).subList(1, 3);
        //Remove the two linked rings
        tmpScaffold = tmpScaffoldGenerator.removeRing(tmpScaffold, true, tmpRings.get(0));
        tmpScaffold = tmpScaffoldGenerator.removeRing(tmpScaffold, true, tmpRings.get(1));
        //Apply Schuffenhauer rules to the remaining structure to remove the benzene ring
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpScaffold);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C1NCC(=O)NCC(=O)NC(C(=O)NCC(=O)NCC(=O)NC1)CC=2C=CNC2", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads scheme 5 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Seglitide from which the single, linked rings (see scheme or test before) have already been removed is generated from a SMILES.
     * Then, according to the second rule, the aromatic six-membered ring is removed and not the macro-ring.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme5Rule2OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O=C1NCC(=O)NC(C(=O)NCC(=O)NC(C(=O)NC(C(=O)NC1)CC2=CNC=3C=CC=CC32)C)C");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovableRings = tmpScaffoldGenerator.applySchuffenhauerRuleTwo(this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovableRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovableRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C1NCC(=O)NCC(=O)NC(C(=O)NCC(=O)NCC(=O)NC1)CC=2C=CNC2", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 6  from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Flucloxacillin is generated from a SMILES and the benzene ring is removed according to the 3rd prioritization rule.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme6Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");//Original
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)C=3C=NOC3", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads scheme 6  from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Flucloxacillin is generated from a SMILES and the benzene ring is removed according to rule 3.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme6Rule3OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovableRings = tmpScaffoldGenerator.applySchuffenhauerRuleThree(tmpScaffold, this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovableRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovableRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)C=3C=NOC3", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 7 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Pentazocine is generated from a SMILES and the benzene ring is removed according to rule 4.
     * A double bond is inserted at the point where it was removed.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme7Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1C2CC3=C(C1(CCN2CC=C(C)C)C)C=C(C=C3)O");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1=CC2CCNC(C1)C2", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads scheme 7 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Pentazocine is generated from a SMILES and the benzene ring is removed according to rule 4.
     * A double bond is inserted at the point where it was removed.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme7Rule4and5OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1C2CC3=C(C1(CCN2CC=C(C)C)C)C=C(C=C3)O");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovableRings = tmpScaffoldGenerator.applySchuffenhauerRuleFourAndFive(tmpScaffold, this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovableRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovableRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1=CC2CCNC(C1)C2", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 8 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Sophocarpin is generated from a SMILES and the ring that is fused to only one other ring is removed according to rule 4.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme8Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CC2CN3C(CC=CC3=O)C4C2N(C1)CCC4");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("N1CC2CCCN3CCCC(C1)C32", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads scheme 8 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Sophocarpin is generated from a SMILES and the ring that is fused to only one other ring is removed according to rule 4.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme8Rule4and5OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CC2CN3C(CC=CC3=O)C4C2N(C1)CCC4");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovableRings = tmpScaffoldGenerator.applySchuffenhauerRuleFourAndFive(tmpScaffold, this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovableRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovableRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("N1CC2CCCN3CCCC(C1)C32", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 9 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Rhynchophylline is generated from a SMILES and in a first step, the benzene ring is removed.
     * Then, the remaining six-membered ring is removed from this fragment to preserve the spiro ring system
     * according to rule 4.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme9Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CCC1CN2CCC3(C2CC1C(=COC)C(=O)OC)C4=CC=CC=C4NC3=O");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C1NC=CC12CNCC2", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads scheme 9 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Rhynchophylline is generated from a SMILES and in a first step, the benzene ring is removed.
     * Then, the remaining six-membered ring is removed from this fragment to preserve the spiro ring system
     * according to rule 4.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme9Rule4and5OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O=C1NC=CC12CCN3CCCCC32"); //already parent scaffold
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleFourAndFive(tmpScaffold, this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C1NC=CC12CNCC2", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 10 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Cafestol is generated from a SMILES and two rings are removed in initial parent scaffold generation.
     * Then, ring A in the scheme is removed to preserve the bridged ring system according to rule 5.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme10Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC12CCC3=C(C1CCC45C2CCC(C4)C(C5)(CO)O)C=CO3");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1CC2CCC(C1)C2", tmpSmilesGenerator.create(tmpRule.get(3)));
    }

    /**
     * Loads scheme 10 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Cafestol is generated from a SMILES and two rings are removed in initial parent scaffold generation.
     * Then, ring A in the scheme is removed to preserve the bridged ring system according to rule 5.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme10Rule4and5OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CCC23CCC(CCC2C1)C3");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovableRings = tmpScaffoldGenerator.applySchuffenhauerRuleFourAndFive(tmpScaffold, this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovableRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovableRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1CC2CCC(C1)C2", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 11a from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Flucloxacillin is generated from a SMILES and the rings connected via linkers are removed in initial parent
     * scaffold generation steps.
     * Then, according to the sixth rule, the ring of size 5 is removed.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme11aTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(true);
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C1NCC1", tmpSmilesGenerator.create(tmpRule.get(3)));
    }

    /**
     * Loads scheme 11a from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Flucloxacillin is generated from a SMILES and the rings connected via linkers are removed in initial parent
     * scaffold generation steps.
     * Then, according to the sixth rule, the ring of size 5 is removed.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme11aRule6OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        //Two-ring parent scaffold of flucloxacillin
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O=C1N2CCSC2C1");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(true);
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleSix(this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, false, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C1NCC1", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 11b from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Epinastine is generated from a SMILES and the two benzene rings are removed in an initial parent scaffold generation step.
     * Then, according to the sixth rule, the five-membered ring is removed.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme11bTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1C2C3=CC=CC=C3CC4=CC=CC=C4N2C(=N1)N");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1=CCC=CCN1", tmpSmilesGenerator.create(tmpRule.get(3)));
    }

    /**
     * Loads scheme 11b from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Epinastine is generated from a SMILES and the two benzene rings are removed in an initial parent scaffold generation step.
     * Then, according to the sixth rule, the five-membered ring is removed.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme11bRule6OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("N1=CN2C=CCC=CC2C1");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleSix(this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1=CCC=CCN1", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads Scheme 12 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Zaleplon is generated from a SMILES and the benzene ring is removed in an initial parent scaffold generation step.
     * Then, according to the seventh rule, the six-membered ring is removed because the removal of the other ring would
     * destroy the aromatic system.
     * If rule 7 is turned off, the 5 ring is removed instead.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme12Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CCN(C1=CC=CC(=C1)C2=CC=NC3=C(C=NN23)C#N)C(=O)C");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("N1=CC=CN1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads Scheme 12 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Zaleplon is generated from a SMILES and the benzene ring is removed in an initial parent scaffold generation step.
     * Then, according to the seventh rule, the six-membered ring is removed because the removal of the other ring would
     * destroy the aromatic system.
     * If rule 7 is turned off, the 5 ring is removed instead.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme12Rule7OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("N1=CC=CN2N=CC=C12");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleSeven(tmpScaffold, this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("N1=CC=CN1", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 13 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Indole is generated from a SMILES and according to the eighth rule, the benzene ring is removed
     * because it has less hetero-atoms.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme13Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("c2ccc1[nH]ccc1c2");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C=1C=CNC1", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads scheme 13 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Indole is generated from a SMILES and according to the eighth rule, the benzene ring is removed
     * because it has less hetero-atoms.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme13Rule8OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("c2ccc1[nH]ccc1c2");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleEight(this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C=1C=CNC1", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads Scheme 14 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Ticlopidine is generated from a SMILES. First, the benzene ring is removed in an initial parent scaffold generation step.
     * According to the ninth rule, the sulfur-containing ring is removed then.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme14Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CN(CC2=C1SC=C2)CC3=CC=CC=C3Cl");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1=CCCNC1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads Scheme 14 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Ticlopidine is generated from a SMILES. First, the benzene ring is removed in an initial parent scaffold generation step.
     * According to the ninth rule, the sulfur-containing ring is removed then.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme14Rule9OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("S1C=CC2=C1CCNC2");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleNine(this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1=CCCNC1", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads a molecule to check the tenth rule from the "The Scaffold Tree" paper by Schuffenhauer et al. from SMILES.
     * A molecule with two seven-membered rings and one eight-membered ring is generated from a SMILES.
     * According to the tenth rule, the seven-membered rings are removed first.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @Tag("SlowTest")
    public void getRule10Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O=C3CC2C(CCCC1CCCCCCC12)C(=O)C(=O)C3=O");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1CCCCCCC1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads a molecule to check the tenth rule from the "The Scaffold Tree" paper by Schuffenhauer et al. from SMILES.
     * A molecule with two seven-membered rings and one eight-membered ring is generated from a SMILES.
     * According to the tenth rule, the seven-membered rings are removed first.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getRule10Rule10OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CCCC2CCCCCC2CC1");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleTen(this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1CCCCCCC1", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 15 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Sertraline is generated from a SMILES and the linker-bonded benzene ring is removed first.
     * According to the eleventh rule, the aromatic benzene ring is removed then.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme15Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CNC1CCC(C2=CC=CC=C12)C3=CC(=C(C=C3)Cl)Cl");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1=CCCCC1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads scheme 15 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Sertraline is generated from a SMILES and the linker-bonded benzene ring is removed first.
     * According to the eleventh rule, the aromatic benzene ring is removed then.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme15Rule11OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C=1C=CC2=C(C1)CCCC2");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleEleven(this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("C1=CCCCC1", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 16 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Deferasirox is generated from a SMILES.
     * According to the twelfth rule, the benzene ring connected to the central triazole ring via a nitrogen-bound linker
     * is removed.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme16Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1=CC=C(C(=C1)C2=NN(C(=N2)C3=CC=CC=C3O)C4=CC=C(C=C4)C(=O)O)O");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setRuleSevenAppliedSetting(true);
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("N=1NC(=NC1C=2C=CC=CC2)C=3C=CC=CC3", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads scheme 16 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Deferasirox is generated from a SMILES.
     * According to the twelfth rule, the benzene ring connected to the central triazole ring via a nitrogen-bound linker
     * is removed.
     * In this case, only the one rule to be checked is applied.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme16Rule12OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1=CC=C(C(=C1)C2=NN(C(=N2)C3=CC=CC=C3O)C4=CC=C(C=C4)C(=O)O)O");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setRuleSevenAppliedSetting(true);
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleTwelve(tmpScaffold, this.getRingsForSchuffenhauer(tmpScaffold));
        Assertions.assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("N=1NC(=NC1C=2C=CC=CC2)C=3C=CC=CC3", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 17 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Ormeloxifene is generated from a SMILES and the linker-connected pyrrolidine ring is removed in an initial parent
     * scaffold generation step.
     * The generated three-ring scaffold does not correspond to the illustration in the paper.
     * This is due to the fact that unique SMILES are generated for rule 13 in Scaffold Generator, but canonical SMILES
     * are used in the original implementation described in the paper.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme17Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1(C(C(C2=C(O1)C=C(C=C2)OC)C3=CC=C(C=C3)OCCN4CCCC4)C5=CC=CC=C5)C");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O1C=2C=CC=CC2C(C=3C=CC=CC3)CC1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads scheme 17 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Ormeloxifene is generated from a SMILES and the linker-connected pyrrolidine ring is removed in an initial parent
     * scaffold generation step.
     * The generated three-ring scaffold does not correspond to the illustration in the paper.
     * This is due to the fact that unique SMILES are generated for rule 13 in Scaffold Generator, but canonical SMILES
     * are used in the original implementation described in the paper.
     * In this case, only the one rule to be checked is applied.
     * 
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme17Rule13OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O1C=2C=CC=CC2C(C=3C=CC=CC3)C(C=4C=CC=CC4)C1");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        IAtomContainer tmpRingRemoved = tmpScaffoldGenerator.applySchuffenhauerRuleThirteen(tmpScaffold, this.getRingsForSchuffenhauer(tmpScaffold));
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffold(tmpRingRemoved, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O1C=2C=CC=CC2C(C=3C=CC=CC3)CC1", tmpSmilesGenerator.create(tmpScaffoldRingRemoved));
    }

    /**
     * Loads scheme 18 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Diazepam, bromazepam, zolazepam, and clotiazepam are generated from SMILES.
     * The Schuffenhauer rules are then applied to these molecules.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme18Test() throws Exception {
        /*-----Diazepam-----*/
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMoleculeDiazepam = tmpParser.parseSmiles("CN1C(=O)CN=C(C2=C1C=CC(=C2)Cl)C3=CC=CC=C3");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffoldDiazepam =tmpScaffoldGenerator.getScaffold(tmpMoleculeDiazepam, true);
        List<IAtomContainer> tmpStep2MolDiazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpScaffoldDiazepam);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C1NC=2C=CC=CC2C=NC1", tmpSmilesGenerator.create(tmpStep2MolDiazepam.get(1)));
        Assertions.assertEquals("O=C1NC=CC=NC1", tmpSmilesGenerator.create(tmpStep2MolDiazepam.get(2)));

        /*-----Bromazepam-----*/
        //SMILES to IAtomContainer
        IAtomContainer tmpMoleculeBromazepam = tmpParser.parseSmiles("C1C(=O)NC2=C(C=C(C=C2)Br)C(=N1)C3=CC=CC=N3");
        //Generate scaffold
        IAtomContainer tmpScaffoldBromazepam =tmpScaffoldGenerator.getScaffold(tmpMoleculeBromazepam, true);
        List<IAtomContainer> tmpStep2MolBromazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpScaffoldBromazepam);
        /*Generate and check SMILES*/
        Assertions.assertEquals("O=C1NC=2C=CC=CC2C=NC1", tmpSmilesGenerator.create(tmpStep2MolBromazepam.get(1)));
        Assertions.assertEquals("O=C1NC=CC=NC1", tmpSmilesGenerator.create(tmpStep2MolBromazepam.get(2)));

        /*-----Zolazepam-----*/
        //SMILES to IAtomContainer
        IAtomContainer tmpMoleculeZolazepam = tmpParser.parseSmiles("CC1=NN(C2=C1C(=NCC(=O)N2C)C3=CC=CC=C3F)C");
        //Generate scaffold
        IAtomContainer tmpScaffoldZolazepam =tmpScaffoldGenerator.getScaffold(tmpMoleculeZolazepam, true);
        List<IAtomContainer> tmpStep2MolZolazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpScaffoldZolazepam);
        /*Generate and check SMILES*/
        Assertions.assertEquals("O=C1NC=2NN=CC2C=NC1", tmpSmilesGenerator.create(tmpStep2MolZolazepam.get(1)));
        Assertions.assertEquals("O=C1NC=CC=NC1", tmpSmilesGenerator.create(tmpStep2MolZolazepam.get(2)));

        /*-----Clotiazepam-----*/
        //SMILES to IAtomContainer
        IAtomContainer tmpMoleculeClotiazepam = tmpParser.parseSmiles("CCC1=CC2=C(S1)N(C(=O)CN=C2C3=CC=CC=C3Cl)C");
        //Generate scaffold
        IAtomContainer tmpScaffoldClotiazepam =tmpScaffoldGenerator.getScaffold(tmpMoleculeClotiazepam, true);
        List<IAtomContainer> tmpStep2MolClotiazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpScaffoldClotiazepam);
        /*Generate and check SMILES*/
        Assertions.assertEquals("O=C1NC=2SC=CC2C=NC1", tmpSmilesGenerator.create(tmpStep2MolClotiazepam.get(1)));
        Assertions.assertEquals("O=C1NC=CC=NC1", tmpSmilesGenerator.create(tmpStep2MolClotiazepam.get(2)));
    }

    /**
     * Loads scheme 19 from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES.
     * Baccatin III is generated from a SMILES and decomposed according to the Schuffenahauer rules.<br>
     * -Step 1: The benzene ring is removed according to rule 3<br>
     * -Step 2: The 4-membered ring is removed according to rule 4<br>
     * -Step 3: The cyclohexane ring is removed according to rule 4<br>
     * -Step 4: The 6-membered ring with a double bond is removed according to rule 6
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme19Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C2C(C(=O)C3(C(CC4C(C3C(C(C2(C)C)(CC1O)O)OC(=O)C5=CC=CC=C5)(CO4)OC(=O)C)O)C)OC(=O)C");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate the scaffolds*/
        List<IAtomContainer> tmpScaffolds = tmpScaffoldGenerator.applySchuffenhauerRules(tmpScaffold);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C1CC2=CCCC(C2)CC3C1CCC4OCC43", tmpSmilesGenerator.create(tmpScaffolds.get(1)));
        Assertions.assertEquals("O=C1CC2=CCCC(C2)CC3CCCCC13", tmpSmilesGenerator.create(tmpScaffolds.get(2)));
        Assertions.assertEquals("O=C1CC2=CCCC(C2)CCC1", tmpSmilesGenerator.create(tmpScaffolds.get(3)));
        Assertions.assertEquals("O=C1CCCCCCC1", tmpSmilesGenerator.create(tmpScaffolds.get(4)));
    }

    /**
     * Test of ScaffoldGenerator.setRuleSevenAppliedSetting() with SMILES of scheme 12.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void setRuleSevenAppliedSettingTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CCN(C1=CC=CC(=C1)C2=CC=NC3=C(C=NN23)C#N)C(=O)C"); //Scheme12
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("N1=CC=CN1", tmpSmilesGenerator.create(tmpRule.get(2)));
        tmpScaffoldGenerator.setRuleSevenAppliedSetting(false);
        List<IAtomContainer> tmpRuleFalse = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        Assertions.assertEquals("N1=CC=CNC1", tmpSmilesGenerator.create(tmpRuleFalse.get(2)));
    }

    /**
     * Test of ScaffoldGenerator.setScaffoldModeSetting() with SMILES.
     * Loads scheme 1 (Flucloxacillin) from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES and
     * generates the scaffold, the Murcko framework, and the basic wire frame.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void setScaffoldModeSettingTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.SCAFFOLD);
        IAtomContainer tmpScaffoldSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C(NC1C(=O)N2CCSC21)C3=CON=C3C=4C=CC=CC4", tmpSmilesGenerator.create(tmpScaffoldSMILES));
        /*Generate Murcko framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.MURCKO_FRAMEWORK);
        IAtomContainer tmpMurckoSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate and check SMILES*/
        Assertions.assertEquals("N=1OC=C(C1C=2C=CC=CC2)CNC3CN4CCSC43", tmpSmilesGenerator.create(tmpMurckoSMILES));
        /*Generate basic wire frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.BASIC_WIRE_FRAME);
        IAtomContainer tmpBWFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate and check SMILES*/
        Assertions.assertEquals("C1CCC(CC1)C2CCCC2CCC3CC4CCCC43", tmpSmilesGenerator.create(tmpBWFSMILES));
        /*Generate elemental wire frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
        IAtomContainer tmpEWFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate and check SMILES*/
        Assertions.assertEquals("O1NC(C(C1)CNC2CN3CCSC32)C4CCCCC4", tmpSmilesGenerator.create(tmpEWFSMILES));
        /*Generate Basic framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.BASIC_FRAMEWORK);
        IAtomContainer tmpBFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate and check SMILES*/
        Assertions.assertEquals("C=1C=CC(=CC1)C2=CCC=C2CCC3CC4CCCC34", tmpSmilesGenerator.create(tmpBFSMILES));
    }

    /**
     * Test of ScaffoldGenerator.setScaffoldModeSetting() with SMILES.
     * Loads scheme 1 (Flucloxacillin) from the "The Scaffold Tree" paper by Schuffenhauer et al. as SMILES and
     * generates the scaffold, the Murcko framework and the basic wire frame.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void setScaffoldModeSettingWithoutHTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        //Generate scaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.SCAFFOLD);
        IAtomContainer tmpScaffoldSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("O=C(NC1C(=O)N2[CH][C]SC21)C3=[C]ON=C3C=4[C]=CC=C[C]4", tmpSmilesGenerator.create(tmpScaffoldSMILES));
        /*Generate Murcko framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.MURCKO_FRAMEWORK);
        IAtomContainer tmpMurckoSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate and check SMILES*/
        Assertions.assertEquals("[C]1SC2N([C]C2N[C]C3=[C]ON=C3C=4[C]=CC=C[C]4)[CH]1", tmpSmilesGenerator.create(tmpMurckoSMILES));
        /*Generate basic wire frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.BASIC_WIRE_FRAME);
        IAtomContainer tmpBWFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate and check SMILES*/
        Assertions.assertEquals("[C]1[C][C][C]([C][C]1)[C]2[C][C][C][C]2[C][C][C]3[C][C]4[C][C][C][C]43", tmpSmilesGenerator.create(tmpBWFSMILES));
        /*Generate elemental wire frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
        IAtomContainer tmpEWFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate and check SMILES*/
        Assertions.assertEquals("[C]1[C][C][C]([C][C]1)[C]2[N]O[C][C]2[C][N][C]3[C]N4[C][C]S[C]34", tmpSmilesGenerator.create(tmpEWFSMILES));
        /*Generate basic framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.BASIC_FRAMEWORK);
        IAtomContainer tmpBFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate and check SMILES*/
        Assertions.assertEquals("[C]1[C]=C([C][CH]C2[C][C]3[CH][C][C]C32)C(=[C]1)C4=[C]C=CC=[C]4", tmpSmilesGenerator.create(tmpBFSMILES));
    }

    /**
     * Test of ScaffoldGenerator.setNonAromaticDBObtainedSetting() with SMILES.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void setRetainOnlyHybridisationsAtAromaticBondsSettingTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C2=C1CCNCC1=CCC2");
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(true);
        List<IAtomContainer> tmpSchuffenhauerFragments = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        Assertions.assertEquals("N1CCCCC1", tmpSmilesGenerator.create(tmpSchuffenhauerFragments.get(1)));
        /*NonAromaticDBObtainedSetting turned on*/
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(false);
        tmpSchuffenhauerFragments = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate and check SMILES*/
        Assertions.assertEquals("C1=CCCNC1", tmpSmilesGenerator.create(tmpSchuffenhauerFragments.get(1)));
    }

    /**
     * Loads a mol file of a specific path and returns it as IAtomContainer.
     * Supports V2000 and V3000 mol files.
     *
     * @param aFilePath Path of the molecule to be loaded
     * @return IAtomContainer of the charged molecule
     * @throws Exception if anything goes wrong
     */
    protected IAtomContainer loadMolFile(String aFilePath) throws Exception {
        /*Get molecule path*/
        File tmpResourcesDirectory = new File(aFilePath);
        BufferedInputStream tmpInputStream = new BufferedInputStream(new FileInputStream(tmpResourcesDirectory));
        /*Get mol file version*/
        FormatFactory tmpFactory = new FormatFactory();
        IChemFormat tmpFormat = tmpFactory.guessFormat(tmpInputStream);
        IAtomContainer tmpMolecule = new AtomContainer();
        /*Load V2000 mol file*/
        if(tmpFormat.getReaderClassName().contains("V2000")) {
            MDLV2000Reader tmpReader = new MDLV2000Reader(tmpInputStream);
            IChemObjectBuilder tmpBuilder = SilentChemObjectBuilder.getInstance();
            tmpMolecule = tmpReader.read(tmpBuilder.newAtomContainer());
            /*Load V3000 mol file*/
        } else if(tmpFormat.getReaderClassName().contains("V3000")) {
            MDLV3000Reader tmpReader = new MDLV3000Reader(tmpInputStream);
            IChemObjectBuilder tmpBuilder = SilentChemObjectBuilder.getInstance();
            tmpMolecule = tmpReader.read(tmpBuilder.newAtomContainer());
        }
        return tmpMolecule;
    }

    /**
     * Returns a ScaffoldGenerator Object with test settings corresponding to the default settings
     *
     * @return a ScaffoldGenerator object with test settings
     * @throws Exception if anything goes wrong
     */
    protected ScaffoldGenerator getScaffoldGeneratorTestSettings() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        tmpScaffoldGenerator.setSmilesGeneratorSetting(tmpSmilesGenerator);
        return tmpScaffoldGenerator;
    }

    /**
     * Identifies all removable rings of a molecule and returns them. The individual Schuffenhauer rules can be applied
     * directly to the returned rings.
     * It is taken out of the applySchuffenhauerRules() method.
     *
     * @param aMolecule molecule whose removable rings are to be found.
     * @return List of removable rings.
     * @throws Exception if anything goes wrong
     */
    protected List<IAtomContainer> getRingsForSchuffenhauer(IAtomContainer aMolecule) throws Exception {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        List<IAtomContainer> tmpRemovableRings = new ArrayList<>();
        /*All molecules with an atom-to-ring ratio of less than 1.0 are assigned the CYCLE_FINDER_BACKUP_PROPERTY = true property,
         since to many rings were probably detected. The fact that a molecule has more rings than atoms seems concerning. That is why this value was chosen.*/
        int tmpRingNumber = this.getRingsInternal(tmpClonedMolecule, false).size();
        float tmpRingAtomRatio = (float) tmpClonedMolecule.getAtomCount() / tmpRingNumber;
        if(tmpRingAtomRatio < 1.0 ) {
            /*Change the property of all atoms of the molecule*/
            for(IAtom tmpAtom : tmpClonedMolecule.atoms()) {
                tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, true);
            }
            /*Apply the new cycle finder to the molecules*/
            tmpRingNumber = this.getRingsInternal(tmpClonedMolecule, false).size();
            tmpClonedMolecule = this.getScaffold(tmpClonedMolecule, true);
        }
        //List of all generated fragments
        List<IAtomContainer> tmpScaffoldFragments = new ArrayList<>(tmpRingNumber);
        tmpScaffoldFragments.add(tmpClonedMolecule);
        /*Go through all the fragments generated and try to break them down further*/
        for(int tmpCounter = 0 ; tmpCounter < tmpScaffoldFragments.size(); tmpCounter++) {
            List<IAtomContainer> tmpRings = this.getRingsInternal(tmpScaffoldFragments.get(tmpCounter), true);
            /*If the fragment has only one ring or no ring, it does not need to be disassembled further*/
            if (tmpRings.size() == 1 || tmpRings.size() == 0) {
                break;
            }
            /*Only the removable terminal rings are further investigated*/
            for (IAtomContainer tmpRing : tmpRings) {
                if (this.isRingTerminal(tmpScaffoldFragments.get(tmpCounter), tmpRing)
                        && this.isRingRemovable(tmpRing, tmpRings, tmpScaffoldFragments.get(tmpCounter))) {
                    tmpRemovableRings.add(tmpRing); //Add the candidate rings
                }
            }
        }
        return tmpRemovableRings;
    }
}