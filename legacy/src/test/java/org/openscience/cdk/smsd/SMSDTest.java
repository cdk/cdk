/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlusHandlerTest;
import org.openscience.cdk.smsd.interfaces.Algorithm;
import org.openscience.cdk.smsd.tools.ExtAtomContainerManipulator;
import org.openscience.cdk.smsd.tools.MolHandler;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
class SMSDTest {

    public SMSDTest() {}

    @BeforeAll
    static void setUpClass() throws Exception {}

    @AfterAll
    static void tearDownClass() throws Exception {}

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    /**
     * Test of init method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testInit_3args_1() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, false, false);
        Assertions.assertNotNull(smsd1.getReactantMolecule());
        Assertions.assertNotNull(smsd1.getProductMolecule());
    }

    /**
     * Test of init method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testInit_3args_2() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, false, false);
        Assertions.assertNotNull(smsd1.getReactantMolecule());
        Assertions.assertNotNull(smsd1.getProductMolecule());
    }

    /**
     * Test of searchMCS method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testSearchMCS() throws CDKException {
        try {
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            sp.kekulise(false);
            IAtomContainer target;
            target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
            IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(queryac);
            Aromaticity.cdkLegacy().apply(target);
            Aromaticity.cdkLegacy().apply(queryac);
            Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
            smsd1.init(queryac, target, true, true);
            smsd1.setChemFilters(true, true, true);
            Assertions.assertEquals(7, smsd1.getFirstAtomMapping().size());
            Assertions.assertEquals(2, smsd1.getAllAtomMapping().size());
            Assertions.assertNotNull(smsd1.getFirstMapping());
        } catch (InvalidSmilesException ex) {
            Logger.getLogger(MCSPlusHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of set method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testSet_IAtomContainer_IAtomContainer() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        Assertions.assertNotNull(smsd1.getFirstMapping());

    }

    /**
     * Test of set method, of class Isomorphism.
     * @throws CDKException
     * @throws IOException
     */
    @Test
    void testSet_String_String() throws CDKException, IOException {
        String molfile = "org/openscience/cdk/smsd/algorithm/decalin.mol";
        String queryfile = "org/openscience/cdk/smsd/algorithm/decalin.mol";
        IAtomContainer query = new AtomContainer();
        IAtomContainer target = new AtomContainer();

        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(molfile);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(query);
        ins = this.getClass().getClassLoader().getResourceAsStream(queryfile);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(target);

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(query, target, true, true);
        smsd1.setChemFilters(true, true, true);
        double score = 1.0;
        Assertions.assertEquals(score, smsd1.getTanimotoSimilarity(), 0.0001);
    }

    /**
     * Test of set method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testSet_MolHandler_MolHandler() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer target1 = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        MolHandler source = new MolHandler(queryac, true, true);
        MolHandler target = new MolHandler(target1, true, true);
        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(source.getMolecule(), target.getMolecule(), true, true);
        smsd1.setChemFilters(true, true, true);
        Assertions.assertNotNull(smsd1.getFirstMapping());
    }

    /**
     * Test of getAllAtomMapping method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testGetAllAtomMapping() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(queryac);
        Aromaticity.cdkLegacy().apply(target);
        Aromaticity.cdkLegacy().apply(queryac);

        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(queryac);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);

        //	Calling the main algorithm to perform MCS cearch

        Aromaticity.cdkLegacy().apply(queryac);
        Aromaticity.cdkLegacy().apply(target);

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        Assertions.assertNotNull(smsd1.getFirstMapping());
        Assertions.assertEquals(2, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of getAllMapping method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testGetAllMapping() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(queryac);
        Aromaticity.cdkLegacy().apply(target);
        Aromaticity.cdkLegacy().apply(queryac);

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        Assertions.assertNotNull(smsd1.getFirstMapping());

        Assertions.assertEquals(2, smsd1.getAllMapping().size());
    }

    /**
     * Test of getFirstAtomMapping method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testGetFirstAtomMapping() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        Assertions.assertNotNull(smsd1.getFirstMapping());

        Assertions.assertEquals(7, smsd1.getFirstAtomMapping().size());
    }

    /**
     * Test of getFirstMapping method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testGetFirstMapping() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        Assertions.assertNotNull(smsd1.getFirstMapping());

        Assertions.assertEquals(7, smsd1.getFirstMapping().size());
    }

    /**
     * Test of setChemFilters method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testSetChemFilters() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        Assertions.assertEquals(1, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of getFragmentSize method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testGetFragmentSize() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(false, true, false);
        Integer score = 2;
        Assertions.assertEquals(score, smsd1.getFragmentSize(0));
    }

    /**
     * Test of getStereoScore method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testGetStereoScore() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, false, false);
        Integer score = 1048;
        Assertions.assertEquals(score, smsd1.getStereoScore(0));
    }

    /**
     * Test of getEnergyScore method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testGetEnergyScore() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(false, false, true);
        Double score = 610.0;
        Assertions.assertEquals(score, smsd1.getEnergyScore(0));
    }

    /**
     * Test of getReactantMolecule method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testGetReactantMolecule() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        Assertions.assertEquals(7, smsd1.getReactantMolecule().getAtomCount());
    }

    /**
     * Test of getProductMolecule method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testGetProductMolecule() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        Assertions.assertEquals(20, smsd1.getProductMolecule().getAtomCount());
    }

    /**
     * Test of getTanimotoSimilarity method, of class Isomorphism.
     * @throws Exception
     */
    @Test
    void testGetTanimotoSimilarity() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        double score = 0.35;
        Assertions.assertEquals(score, smsd1.getTanimotoSimilarity(), 0);
    }

    /**
     * Test of isStereoMisMatch method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testIsStereoMisMatch() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        Assertions.assertEquals(false, smsd1.isStereoMisMatch());
    }

    /**
     * Test of isSubgraph method, of class Isomorphism.
     * @throws CDKException
     */
    @Test
    void testIsSubgraph() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.SubStructure, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        Assertions.assertEquals(true, smsd1.isSubgraph());
    }

    /**
     * Test of getEuclideanDistance method, of class Isomorphism.
     * @throws Exception
     */
    @Test
    void testGetEuclideanDistance() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.SubStructure, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        double score = 3.605;
        Assertions.assertEquals(score, smsd1.getEuclideanDistance(), 0.005);

        Isomorphism smsd2 = new Isomorphism(Algorithm.VFLibMCS, true);
        smsd2.init(queryac, target, true, true);
        smsd2.setChemFilters(true, true, true);

        Assertions.assertEquals(score, smsd2.getEuclideanDistance(), 0.005);
    }

    @Test
    void testQueryAtomContainerDefault() throws CDKException {
        Isomorphism smsd = new Isomorphism(Algorithm.DEFAULT, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target, false, true);
        boolean foundMatches = smsd.isSubgraph();
        Assertions.assertTrue(foundMatches);

        IQueryAtomContainer queryContainer = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(query);
        smsd.init(queryContainer, target);
        foundMatches = smsd.isSubgraph();
        Assertions.assertTrue(foundMatches);
    }

    @Test
    void testQueryAtomContainerMCSPLUS() throws CDKException {
        Isomorphism smsd = new Isomorphism(Algorithm.MCSPlus, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target, false, true);
        boolean foundMatches = smsd.isSubgraph();
        Assertions.assertTrue(foundMatches);

        IQueryAtomContainer queryContainer = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(query);
        smsd.init(queryContainer, target);
        foundMatches = smsd.isSubgraph();
        Assertions.assertTrue(foundMatches);
    }

    @Test
    void testQueryAtomContainerSubstructure() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(query);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);

        //	Calling the main algorithm to perform MCS cearch

        Aromaticity.cdkLegacy().apply(query);
        Aromaticity.cdkLegacy().apply(target);

        Isomorphism smsd = new Isomorphism(Algorithm.SubStructure, true);
        smsd.init(query, target, false, true);
        boolean foundMatches = smsd.isSubgraph();
        Assertions.assertTrue(foundMatches);

        //        IQueryAtomContainer queryContainer = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(query);
        //
        //        Isomorphism smsd1 = new Isomorphism(Algorithm.SubStructure, true);
        //        smsd1.init(queryContainer, target, true, true);
        //        smsd1.setChemFilters(true, true, true);
        //        foundMatches = smsd1.isSubgraph();
        //        Assert.assertFalse(foundMatches);
    }

    void testQueryAtomCount() throws CDKException {
        Isomorphism smsd = new Isomorphism(Algorithm.DEFAULT, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target, false, true);
        boolean foundMatches = smsd.isSubgraph();
        Assertions.assertEquals(18, smsd.getAllAtomMapping().size());
        Assertions.assertTrue(foundMatches);

        IQueryAtomContainer queryContainer = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(query);
        smsd.init(queryContainer, target);
        foundMatches = smsd.isSubgraph();
        Assertions.assertTrue(foundMatches);
    }

    @Test
    void testMatchCount() throws CDKException {
        Isomorphism smsd = new Isomorphism(Algorithm.DEFAULT, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target, false, true);
        boolean foundMatches = smsd.isSubgraph();
        Assertions.assertEquals(18, smsd.getAllAtomMapping().size());
        Assertions.assertTrue(foundMatches);

        IQueryAtomContainer queryContainer = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(query);
        smsd.init(queryContainer, target);
        foundMatches = smsd.isSubgraph();
        Assertions.assertTrue(foundMatches);
    }

    @Test
    void testMatchCountCDKMCS() throws CDKException {
        Isomorphism smsd = new Isomorphism(Algorithm.CDKMCS, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target, false, true);
        boolean foundMatches = smsd.isSubgraph();
        Assertions.assertEquals(18, smsd.getAllAtomMapping().size());
        Assertions.assertTrue(foundMatches);

        IQueryAtomContainer queryContainer = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(query);
        smsd.init(queryContainer, target);
        foundMatches = smsd.isSubgraph();
        Assertions.assertTrue(foundMatches);
    }

    @Test
    void testImpossibleQuery() throws CDKException {
        Isomorphism smsd = new Isomorphism(Algorithm.DEFAULT, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C");

        smsd.init(query, target, false, true);
        boolean foundMatches = smsd.isSubgraph();
        Assertions.assertFalse(foundMatches);
    }
}
