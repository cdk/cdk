
/* Copyright (C) 2009-2010 Syed Asad Rahman {asad@ebi.ac.uk}
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
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import static org.junit.Assert.*;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlusHandlerTest;
import org.openscience.cdk.smsd.helper.MolHandler;
import org.openscience.cdk.smsd.interfaces.Algorithm;

/**
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
public class SMSDTest {

    public SMSDTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testInit_3args_1() throws InvalidSmilesException, CDKException {
        System.out.println("init");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule target = sp.parseSmiles("C\\C=C/OCC=C");
        IMolecule queryac = sp.parseSmiles("CCCOCC(C)=C");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, false, false);
        assertNotNull(smsd1.getReactantMolecule());
        assertNotNull(smsd1.getProductMolecule());
    }

    /**
     * Test of init method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testInit_3args_2() throws InvalidSmilesException, CDKException {
        System.out.println("init");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, false, false);
        assertNotNull(smsd1.getReactantMolecule());
        assertNotNull(smsd1.getProductMolecule());
    }

    /**
     * Test of searchMCS method, of class SMSD.
     * @throws CDKException
     */
    @Test
    public void testSearchMCS() throws CDKException {
        try {
            System.out.println("searchMCS");
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            IAtomContainer target = null;
            target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
            IAtomContainer queryac = null;
            queryac = sp.parseSmiles("Nc1ccccc1");
            SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
            smsd1.init(queryac, target, true);
            smsd1.setChemFilters(true, true, true);
            assertNotNull(smsd1.getFirstMapping());
        } catch (InvalidSmilesException ex) {
            Logger.getLogger(MCSPlusHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of set method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testSet_IAtomContainer_IAtomContainer() throws InvalidSmilesException, CDKException {
        System.out.println("set");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IMolecule queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);
        assertNotNull(smsd1.getFirstMapping());

    }

    /**
     * Test of set method, of class SMSD.
     * @throws Exception
     */
    @Test
    public void testSet_IMolecule_IMolecule() throws Exception {
        System.out.println("set");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IMolecule queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);
        assertNotNull(smsd1.getFirstMapping());
    }

    /**
     * Test of set method, of class SMSD.
     * @throws CDKException
     * @throws IOException
     */
    @Test
    public void testSet_String_String() throws CDKException, IOException {
        System.out.println("set");
        String molfile = "data/mdl/decalin.mol";
        String queryfile = "data/mdl/decalin.mol";
        Molecule query = new Molecule();
        Molecule target = new Molecule();

        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(molfile);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(query);
        ins = this.getClass().getClassLoader().getResourceAsStream(queryfile);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(target);

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(query, target, true);
        smsd1.setChemFilters(true, true, true);
        double score = 1.0;
        assertEquals(score, smsd1.getTanimotoSimilarity(), 0.0001);
    }

    /**
     * Test of set method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testSet_MolHandler_MolHandler() throws InvalidSmilesException, CDKException {
        System.out.println("set");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer target1 = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        MolHandler source = new MolHandler(queryac, true);
        MolHandler target = new MolHandler(target1, true);
        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(source.getMolecule(), target.getMolecule(), true);
        smsd1.setChemFilters(true, true, true);
        assertNotNull(smsd1.getFirstMapping());
    }

    /**
     * Test of getAllAtomMapping method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetAllAtomMapping() throws InvalidSmilesException, CDKException {
        System.out.println("getAllAtomMapping");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(2, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of getAllMapping method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetAllMapping() throws InvalidSmilesException, CDKException {
        System.out.println("getAllMapping");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(2, smsd1.getAllMapping().size());
    }

    /**
     * Test of getFirstAtomMapping method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetFirstAtomMapping() throws InvalidSmilesException, CDKException {
        System.out.println("getFirstAtomMapping");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(7, smsd1.getFirstAtomMapping().size());
    }

    /**
     * Test of getFirstMapping method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetFirstMapping() throws InvalidSmilesException, CDKException {
        System.out.println("getFirstMapping");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(7, smsd1.getFirstMapping().size());
    }

    /**
     * Test of setChemFilters method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testSetChemFilters() throws InvalidSmilesException, CDKException {
        System.out.println("setChemFilters");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);
        assertEquals(1, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of getFragmentSize method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetFragmentSize() throws InvalidSmilesException, CDKException {
        System.out.println("getFragmentSize");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(false, true, false);
        Integer score = 2;
        assertEquals(score, smsd1.getFragmentSize(0));
    }

    /**
     * Test of getStereoScore method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetStereoScore() throws InvalidSmilesException, CDKException {
        System.out.println("getStereoScore");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, false, false);
        Integer score = 30;
        assertEquals(score, smsd1.getStereoScore(0));
    }

    /**
     * Test of getEnergyScore method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetEnergyScore() throws InvalidSmilesException, CDKException {
        System.out.println("getEnergyScore");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(false, false, true);
        Double score = 610.0;
        assertEquals(score, smsd1.getEnergyScore(0));
    }

    /**
     * Test of getReactantMolecule method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetReactantMolecule() throws InvalidSmilesException, CDKException {
        System.out.println("getReactantMolecule");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);

        assertEquals(7, smsd1.getReactantMolecule().getAtomCount());
    }

    /**
     * Test of getProductMolecule method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetProductMolecule() throws InvalidSmilesException, CDKException {
        System.out.println("getProductMolecule");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);

        assertEquals(20, smsd1.getProductMolecule().getAtomCount());
    }

    /**
     * Test of getTanimotoSimilarity method, of class SMSD.
     * @throws Exception
     */
    @Test
    public void testGetTanimotoSimilarity() throws Exception {
        System.out.println("getTanimotoSimilarity");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);

        double score = 0.35;
        assertEquals(score, smsd1.getTanimotoSimilarity(), 0);
    }

    /**
     * Test of isStereoMisMatch method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testIsStereoMisMatch() throws InvalidSmilesException, CDKException {
        System.out.println("isStereoMisMatch");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);
        assertEquals(false, smsd1.isStereoMisMatch());
    }

    /**
     * Test of isSubgraph method, of class SMSD.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testIsSubgraph() throws InvalidSmilesException, CDKException {
        System.out.println("isSubgraph");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.SubStructure, false);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);
        assertEquals(true, smsd1.isSubgraph());
    }

    /**
     * Test of getEuclideanDistance method, of class SMSD.
     * @throws Exception
     */
    @Test
    public void testGetEuclideanDistance() throws Exception {
        System.out.println("getEuclideanDistance");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        SMSD smsd1 = new SMSD(Algorithm.SubStructure, true);
        smsd1.init(queryac, target, true);
        smsd1.setChemFilters(true, true, true);

        double score = 3.605;
        assertEquals(score, smsd1.getEuclideanDistance(), 0.005);

        SMSD smsd2 = new SMSD(Algorithm.VFLibMCS, true);
        smsd2.init(queryac, target, true);
        smsd2.setChemFilters(true, true, true);

        assertEquals(score, smsd2.getEuclideanDistance(), 0.005);
    }

    @Test
    public void testQueryAtomContainerDefault() throws CDKException {
        SMSD smsd = new SMSD(Algorithm.DEFAULT, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target, false);
        boolean foundMatches = smsd.isSubgraph();
        Assert.assertTrue(foundMatches);

        IQueryAtomContainer query2 = SMARTSParser.parse("CC");
        smsd.init(query2, target, false);
        foundMatches = smsd.isSubgraph();
        Assert.assertTrue(foundMatches);
    }

    @Test
    public void testQueryAtomContainerMCSPLUS() throws CDKException {
        SMSD smsd = new SMSD(Algorithm.MCSPlus, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target, false);
        boolean foundMatches = smsd.isSubgraph();
        Assert.assertTrue(foundMatches);

        IQueryAtomContainer query2 = SMARTSParser.parse("CC");
        smsd.init(query2, target, false);
        foundMatches = smsd.isSubgraph();
        Assert.assertTrue(foundMatches);
    }

    @Test
    public void testQueryAtomContainerSubstructure() throws CDKException {
        SMSD smsd = new SMSD(Algorithm.SubStructure, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target, false);
        boolean foundMatches = smsd.isSubgraph();
        Assert.assertTrue(foundMatches);

        IQueryAtomContainer query2 = SMARTSParser.parse("CC");
        smsd.init(query2, target, false);
        foundMatches = smsd.isSubgraph();
        Assert.assertTrue(foundMatches);
    }

    @Test
    public void testQueryAtomContainerTurbo() throws CDKException {
        SMSD smsd = new SMSD(Algorithm.TURBOMCS, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target, false);
        boolean foundMatches = smsd.isSubgraph();
        Assert.assertTrue(foundMatches);

        IQueryAtomContainer query2 = SMARTSParser.parse("CC");
        smsd.init(query2, target, false);
        foundMatches = smsd.isSubgraph();
        Assert.assertTrue(foundMatches);
    }
}
