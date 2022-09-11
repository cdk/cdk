/* Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.smiles.smarts.parser;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * JUnit test routines for the SMARTS substructure search.
 *
 * @author Dazhi Jiao
 * @cdk.module test-smarts
 * @cdk.require ant1.6
 */
class SMARTSSearchTest extends CDKTestCase {

    private static final ILoggingTool        logger = LoggingToolFactory.createLoggingTool(SMARTSSearchTest.class);

    private UniversalIsomorphismTester uiTester;

    @BeforeEach
    void setUpUITester() {
        uiTester = new UniversalIsomorphismTester();
    }

    static IAtomContainer smiles(String smiles) throws InvalidSmilesException {
        return smiles(smiles, false);
    }

    static IAtomContainer smilesAtomTyped(String smiles) throws CDKException {
        IAtomContainer molecule = smiles(smiles, false);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        return molecule;
    }

    static IAtomContainer smiles(String smiles, boolean perserveAromaticity) throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(!perserveAromaticity);
        return sp.parseSmiles(smiles);
    }

    static SMARTSQueryTool smarts(String smarts) {
        SMARTSQueryTool sqt = new SMARTSQueryTool(smarts, DefaultChemObjectBuilder.getInstance());
        return sqt;
    }

    static int[] match(SMARTSQueryTool sqt, IAtomContainer m) throws CDKException {
        boolean status = sqt.matches(m);
        if (status) {
            return new int[]{sqt.countMatches(), sqt.getUniqueMatchingAtoms().size()};
        } else {
            return new int[]{0, 0};
        }
    }

    static int[] match(String smarts, String smiles) throws Exception {
        return match(smarts(smarts), smiles(smiles));
    }

    @Test
    void testMoleculeFromSDF() throws CDKException {
        String filename = "cnssmarts.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        DefaultChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content;
        content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer atomContainer = cList.get(0);

        SMARTSQueryTool sqt = new SMARTSQueryTool("[NX3;h1,h2,H1,H2;!$(NC=O)]", DefaultChemObjectBuilder.getInstance());
        boolean status = sqt.matches(atomContainer);
        Assertions.assertEquals(true, status);

        int nmatch = sqt.countMatches();
        int nqmatch = sqt.getUniqueMatchingAtoms().size();

        Assertions.assertEquals(3, nmatch);
        Assertions.assertEquals(3, nqmatch);

        sqt.setSmarts("[ND3]");
        status = sqt.matches(atomContainer);
        Assertions.assertEquals(false, status);
    }

    @Test
    void testRGraphBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("CC=O", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query c:c: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC=O"); // benzene, aromatic
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testAromaticBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("c:c", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query c:c: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C1CCCCC1"); // hexane, not aromatic
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testSingleBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C-C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C-C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C=C");
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C#C");
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testDoubleBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C=C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C=C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C=C");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C#C");
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testTripleBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C#C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C#C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C=C");
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C#C");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testAnyOrderBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C~C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C~C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C=C");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C#C");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testAnyAtom() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C*C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C*C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("CNC");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("CCN");
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testAliphaticAtom() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("CAC", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query CAC: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("CNC");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testAromaticAtom() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("aaa", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query CaC: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testSymbolQueryAtom() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("CCC", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query CAC: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("CNC");
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
        Assertions.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    /**
     * From http://www.daylight.com/dayhtml_tutorials/languages/smarts/index.html
     */
    @Test
    void testPropertyCharge1() throws Exception {
        int[] results = match("[+1]", "[OH-].[Mg+2]");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testPropertyCharge2() throws Exception {
        int[] results = match("[+1]", "COCC(O)Cn1ccnc1[N+](=O)[O-]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyCharge3() throws Exception {
        int[] results = match("[+1]", "[NH4+]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyCharge4() throws Exception {
        int[] results = match("[+1]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testPropertyCharge5() throws Exception {
        int[] results = match("[+1]", "[Cl-].[Cl-].NC(=O)c2cc[n+](COC[n+]1ccccc1C=NO)cc2");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testPropertyAromatic1() throws Exception {
        int[] results = match("[a]", "c1cc(C)c(N)cc1");
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(6, results[1]);
    }

    @Test
    void testPropertyAromatic2() throws Exception {
        int[] results = match("[a]", "c1c(C)c(N)cnc1");
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(6, results[1]);
    }

    @Test
    void testPropertyAromatic3() throws Exception {
        int[] results = match("[a]", "c1(C)c(N)cco1");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    @Test
    void testPropertyAromatic4() throws Exception {
        int[] results = match("[a]", "c1c(C)c(N)c[nH]1");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    @Test
    void testPropertyAromatic5() throws Exception {
        int[] results = match("[a]", "O=n1ccccc1");
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(6, results[1]);
    }

    @Test
    void testPropertyAromatic6() throws Exception {
        int[] results = match("[a]", "[O-][n+]1ccccc1");
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(6, results[1]);
    }

    @Test
    void testPropertyAromatic7() throws Exception {
        int[] results = match("[a]", "c1ncccc1C1CCCN1C");
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(6, results[1]);
    }

    @Test
    void testPropertyAromatic8() throws Exception {
        int[] results = match("[a]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(6, results[1]);
    }

    @Test
    void testPropertyAliphatic1() throws Exception {
        int[] results = match("[A]", "c1cc(C)c(N)cc1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testPropertyAliphatic2() throws Exception {
        int[] results = match("[A]", "CCO");
        Assertions.assertEquals(3, results[0]);
        Assertions.assertEquals(3, results[1]);
    }

    @Test
    void testPropertyAliphatic3() throws Exception {
        int[] results = match("[A]", "C=CC=CC=C");
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(6, results[1]);
    }

    @Test
    void testPropertyAliphatic4() throws Exception {
        int[] results = match("[A]", "CC(C)(C)C");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    @Test
    void testPropertyAliphatic5() throws Exception {
        int[] results = match("[A]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assertions.assertEquals(15, results[0]);
        Assertions.assertEquals(15, results[1]);
    }

    @Test
    void testPropertyAliphatic6() throws Exception {
        int[] results = match("[A]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assertions.assertEquals(19, results[0]);
        Assertions.assertEquals(19, results[1]);
    }

    @Test
    void testPropertyAtomicNumber1() throws Exception {
        int[] results = match("[#6]", "c1cc(C)c(N)cc1");
        Assertions.assertEquals(7, results[0]);
        Assertions.assertEquals(7, results[1]);
    }

    @Test
    void testPropertyAtomicNumber2() throws Exception {
        int[] results = match("[#6]", "CCO");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testPropertyAtomicNumber3() throws Exception {
        int[] results = match("[#6]", "C=CC=CC=C-O");
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(6, results[1]);
    }

    @Test
    void testPropertyAtomicNumber4() throws Exception {
        int[] results = match("[#6]", "CC(C)(C)C");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    @Test
    void testPropertyAtomicNumber5() throws Exception {
        int[] results = match("[#6]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assertions.assertEquals(20, results[0]);
        Assertions.assertEquals(20, results[1]);
    }

    @Test
    void testPropertyAtomicNumber6() throws Exception {
        int[] results = match("[#6]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assertions.assertEquals(17, results[0]);
        Assertions.assertEquals(17, results[1]);
    }

    @Test
    void testPropertyAtomicNumber7() throws Exception {
        int[] results = match("[#6]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5");
        Assertions.assertEquals(21, results[0]);
        Assertions.assertEquals(21, results[1]);
    }

    /**
     * @cdk.bug 2686473
     * @throws Exception
     */
    @Test
    void testPropertyAtomicNumber8() throws Exception {
        int[] results = match("[#16]", "COC1C(C(C(C(O1)CO)OC2C(C(C(C(O2)CO)S)O)O)O)O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * @cdk.bug 2686473
     * @throws Exception
     */
    @Test
    void testPropertyAtomicNumber9() throws Exception {
        int[] results = match("[#6]", "[*]");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testPropertyR1() throws Exception {
        int[] results = match("[R2]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assertions.assertEquals(7, results[0]);
        Assertions.assertEquals(7, results[1]);
    }

    @Test
    void testPropertyR2() throws Exception {
        SMARTSQueryTool sqt = smarts("[R2]");
        sqt.useSmallestSetOfSmallestRings(); // default for daylight
        int[] results = match(sqt, smiles("COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34"));
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(6, results[1]);

    }

    @Disabled("This feature was removed - essential rings aren't useful really")
    @Test
    void testPropertyR2_essentialRings() throws Exception {
        SMARTSQueryTool sqt = smarts("[R2]");
        sqt.useEssentialRings();
        int[] results = match(sqt, smiles("COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34"));
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Disabled("This feature is pending but will be the combinded in an 'OpenSMARTS'"
            + " configuration which uses the relevant rings.")
    @Test
    void testPropertyR2_relevantRings() throws Exception {
        SMARTSQueryTool sqt = smarts("[R2]");
        sqt.useRelevantRings();
        int[] results = match(sqt, smiles("COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34"));
        Assertions.assertEquals(8, results[0]);
        Assertions.assertEquals(8, results[1]);
    }

    @Test
    void testPropertyR3() throws Exception {
        int[] results = match("[R2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assertions.assertEquals(4, results[0]);
        Assertions.assertEquals(4, results[1]);
    }

    @Test
    void testPropertyR4() throws Exception {
        int[] results = match("[R2]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5");
        Assertions.assertEquals(4, results[0]);
        Assertions.assertEquals(4, results[1]);
    }

    @Test
    void testPropertyR5() throws Exception {
        int[] results = match("[R2]", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testPropertyr1() throws Exception {
        int[] results = match("[r5]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assertions.assertEquals(9, results[0]);
        Assertions.assertEquals(9, results[1]);
    }

    @Test
    void testPropertyr2() throws Exception {
        int[] results = match("[r5]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testPropertyr3() throws Exception {
        int[] results = match("[r5]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    @Test
    void testPropertyr4() throws Exception {
        int[] results = match("[r5]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    @Test
    void testPropertyr5() throws Exception {
        int[] results = match("[r5]", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    @Test
    void quadBond() throws Exception {
        int[] results = match("*$*", "[Re]$[Re]");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);
    }
    
    @Test
    void testPropertyValence1() throws Exception {
        int[] results = match("[v4]", "C");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyValence2() throws Exception {
        int[] results = match("[v4]", "CCO");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testPropertyValence3() throws Exception {
        int[] results = match("[v4]", "[NH4+]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyValence4() throws Exception {
        int[] results = match("[v4]", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O");
        Assertions.assertEquals(16, results[0]);
        Assertions.assertEquals(16, results[1]);
    }

    @Test
    void testPropertyValence5() throws Exception {
        int[] results = match("[v4]", "[Cl-].[Cl-].NC(=O)c2cc[n+](COC[n+]1ccccc1C=NO)cc2");
        Assertions.assertEquals(16, results[0]);
        Assertions.assertEquals(16, results[1]);
    }

    @Test
    void testPropertyX1() throws Exception {
        int[] results = match("[X2]", "CCO");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyX2() throws Exception {
        int[] results = match("[X2]", "O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyX3() throws Exception {
        int[] results = match("[X2]", "CCC(=O)CC");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testPropertyX4() throws Exception {
        int[] results = match("[X2]", "FC(Cl)=C=C(Cl)F");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyX5() throws Exception {
        int[] results = match("[X2]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assertions.assertEquals(3, results[0]);
        Assertions.assertEquals(3, results[1]);
    }

    @Test
    void testPropertyX6() throws Exception {
        int[] results = match("[X2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assertions.assertEquals(3, results[0]);
        Assertions.assertEquals(3, results[1]);
    }

    @Test
    void testPropertyD1() throws Exception {
        int[] results = match("[D2]", "CCO");
        Assertions.assertEquals(1, results[0]);
    }

    @Test
    void testPropertyD2() throws Exception {
        int[] results = match("[D2]", "O");
        Assertions.assertEquals(0, results[0]);
    }

    @Test
    void testPropertyD3() throws Exception {
        int[] results = match("[D2]", "CCC(=O)CC");
        Assertions.assertEquals(2, results[0]);
    }

    @Test
    void testPropertyD4() throws Exception {
        int[] results = match("[D2]", "FC(Cl)=C=C(Cl)F");
        Assertions.assertEquals(1, results[0]);
    }

    @Test
    void testPropertyD5() throws Exception {
        int[] results = match("[D2]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assertions.assertEquals(12, results[0]);
    }

    @Test
    void testPropertyD6() throws Exception {
        int[] results = match("[D2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assertions.assertEquals(8, results[0]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    void testPropertyD7() throws Exception {
        int[] results = match("[ND3]", "CCN([H])([H])");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    void testPropertyD8() throws Exception {
        int[] results = match("[OD1]", "CO[H]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    void testPropertyD9() throws Exception {
        int[] results;

        results = match("[OD1H]", "CO");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    void testPropertyD10() throws Exception {
        int[] results;

        results = match("[OD1H]", "CO[H]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    void testPropertyD11() throws Exception {
        int[] results;

        results = match("[OD1H]-*", "CCO");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * With '*' matching 'H', this smarts matches twice 'OC' and 'O[H]'.
     * @cdk.bug 2489417
     */
    @Test
    void testPropertyD12() throws Exception {
        int[] results;

        results = match("[OD1H]-*", "CCO[H]");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);

    }

    @Test
    void testPropertyHAtom1() throws Exception {
        int[] results = match("[H]", "[H+].[Cl-]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyHAtom2() throws Exception {
        int[] results = match("[H]", "[2H]");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testPropertyHAtom3() throws Exception {
        int[] results = match("[H]", "[H][H]");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testPropertyHAtom4() throws Exception {
        int[] results = match("[H]", "[CH4]");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testPropertyHAtom5() throws Exception {
        int[] results = match("[H]", "[H]C([H])([H])[H]");
        Assertions.assertEquals(4, results[0]);
        Assertions.assertEquals(4, results[1]);
    }

    @Test
    void testPropertyHTotal1() throws Exception {
        int[] results = match("[H1]", "CCO");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyHTotal2() throws Exception {
        int[] results = match("[H1]", "[2H]C#C");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testPropertyHTotal3() throws Exception {
        int[] results = match("[H1]", "[H]C(C)(C)C");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyHTotal4() throws Exception {
        int[] results = match("[H1]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assertions.assertEquals(11, results[0]);
        Assertions.assertEquals(11, results[1]);
    }

    @Test
    void testPropertyHTotal5() throws Exception {
        int[] results = match("[H1]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assertions.assertEquals(10, results[0]);
        Assertions.assertEquals(10, results[1]);
    }

    @Test
    void testPropertyHTotal6() throws Exception {
        int[] results = match("[H1]", "[H][H]");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testPropertyAnyAtom1() throws Exception {
        int[] results = match("[*]", "C");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyAnyAtom2() throws Exception {
        int[] results = match("[*]", "[2H]C");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testPropertyAnyAtom3() throws Exception {
        int[] results = match("[*]", "[1H][1H]");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testPropertyAnyAtom4() throws Exception {
        int[] results = match("[*]", "[1H]C([1H])([1H])[1H]");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    @Test
    void testPropertyAnyAtom5() throws Exception {
        int[] results = match("[*]", "[H][H]");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489533
     */
    @Test
    void testPropertyAnyAtom6() throws Exception {
        int[] result = match("*", "CO");
        Assertions.assertEquals(2, result[0]);
        Assertions.assertEquals(2, result[1]);
    }

    /**
     * Bug was mistaken - '*' does match explicit H but in DEPICTMATCH H's are
     * suppressed by default.
     *
     * @throws Exception
     * @cdk.bug 2489533
     */
    @Test
    void testPropertyAnyAtom7() throws Exception {
        int[] result = match("*", "CO[H]");
        Assertions.assertEquals(3, result[0]);
        Assertions.assertEquals(3, result[1]);
    }

    /**
     * Bug was mistaken - '*' does match explicit H but in DEPICTMATCH H's are
     * suppressed by default.
     *
     * @throws Exception
     * @cdk.bug 2489533
     */
    @Test
    void testPropertyAnyAtom8() throws Exception {
        int[] result = match("*", "[H]C([H])([H])[H]");
        Assertions.assertEquals(5, result[0]);
        Assertions.assertEquals(5, result[1]);
    }

    /**
     * Bug was mistaken - '*' does match explicit H but in DEPICTMATCH H's are
     * suppressed by default.
     *
     * @throws Exception
     * @cdk.bug 2489533
     */
    @Test
    void testPropertyAnyAtom9() throws Exception {
        int[] result = match("*", "CCCC([2H])[H]");
        Assertions.assertEquals(6, result[0]);
        Assertions.assertEquals(6, result[1]);
    }

    @Test
    void testPropertyAtomicMass1() throws Exception {
        int[] results = match("[13C]", "[13C]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyAtomicMass2() throws Exception {
        int[] results = match("[13C]", "[C]");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testPropertyAtomicMass3() throws Exception {
        int[] results = match("[13*]", "[13C]Cl");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyAtomicMass4() throws Exception {
        int[] results = match("[12C]", "CCl");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    /**
     * @cdk.bug 2490336
     * @throws Exception
     */
    @Test
    void testPropertyAtomicMass5() throws Exception {
        int[] results = match("[2H]", "CCCC([2H])[H]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyAtomicMass6() throws Exception {
        int[] results = match("[H]", "CCCC([2H])[H]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPropertyAtomicMass7() throws Exception {
        int[] results = match("[3H]", "CCCC([2H])([3H])[3H]");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testBondSingle1() throws Exception {
        int[] results = match("CC", "C=C");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testBondSingle2() throws Exception {
        int[] results = match("CC", "C#C");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testBondSingle3() throws Exception {
        int[] results = match("CC", "CCO");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testBondSingle4() throws Exception {
        int[] results = match("CC", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
        Assertions.assertEquals(28, results[0]);
        Assertions.assertEquals(14, results[1]);
    }

    @Test
    void testBondSingle5() throws Exception {
        int[] results = match("CC", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O");
        Assertions.assertEquals(14, results[0]);
        Assertions.assertEquals(7, results[1]);
    }

    @Test
    void testBondAny1() throws Exception {
        int[] results = match("C~C", "C=C");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testBondAny2() throws Exception {
        int[] results = match("C~C", "C#C");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testBondAny3() throws Exception {
        int[] results = match("C~C", "CCO");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testBondAny4() throws Exception {
        int[] results = match("C~C", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
        Assertions.assertEquals(38, results[0]);
        Assertions.assertEquals(19, results[1]);
    }

    @Test
    void testBondAny5() throws Exception {
        int[] results = match("[C,c]~[C,c]", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O");
        Assertions.assertEquals(28, results[0]);
        Assertions.assertEquals(14, results[1]);
    }

    @Test
    void testBondRing1() throws Exception {
        int[] results = match("C@C", "C=C");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testBondRing2() throws Exception {
        int[] results = match("C@C", "C#C");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testBondRing3() throws Exception {
        int[] results = match("C@C", "C1CCCCC1");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(6, results[1]);
    }

    @Test
    void testBondRing4() throws Exception {
        int[] results = match("[C,c]@[C,c]", "c1ccccc1Cc1ccccc1");
        Assertions.assertEquals(24, results[0]);
        Assertions.assertEquals(12, results[1]);
    }

    @Test
    void testBondRing5() throws Exception {
        int[] results = match("[C,c]@[C,c]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assertions.assertEquals(30, results[0]);
        Assertions.assertEquals(15, results[1]);
    }

    @Test
    void testBondRing6() throws Exception {
        int[] results = match("[C,c]@[C,c]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assertions.assertEquals(44, results[0]);
        Assertions.assertEquals(22, results[1]);
    }

    @Test
    void testBondStereo1() throws Exception {
        int[] results = match("F/?C=C/Cl", "F/C=C/Cl");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testBondStereo2() throws Exception {
        int[] results = match("F/?C=C/Cl", "FC=C/Cl");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testBondStereo3() throws Exception {
        int[] results = match("F/?C=C/Cl", "FC=CCl");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testBondStereo4() throws Exception {
        int[] results = match("F/?C=C/Cl", "F\\C=C/Cl");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testLogicalNot1() throws Exception {
        int[] results = match("[!c]", "c1cc(C)c(N)cc1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testLogicalNot2() throws Exception {
        int[] results = match("[!c]", "c1c(C)c(N)cnc1");
        Assertions.assertEquals(3, results[0]);
        Assertions.assertEquals(3, results[1]);
    }

    @Test
    void testLogicalNot3() throws Exception {
        int[] results = match("[!c]", "c1(C)c(N)cco1");
        Assertions.assertEquals(3, results[0]);
        Assertions.assertEquals(3, results[1]);
    }

    @Test
    void testLogicalNot4() throws Exception {
        int[] results = match("[!c]", "c1c(C)c(N)c[nH]1");
        Assertions.assertEquals(3, results[0]);
        Assertions.assertEquals(3, results[1]);
    }

    @Test
    void testLogicalNot5() throws Exception {
        int[] results = match("[!c]", "O=n1ccccc1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testLogicalNot6() throws Exception {
        int[] results = match("[!c]", "[O-][n+]1ccccc1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testLogicalNot7() throws Exception {
        int[] results = match("[!c]", "c1ncccc1C1CCCN1C");
        Assertions.assertEquals(7, results[0]);
        Assertions.assertEquals(7, results[1]);
    }

    @Test
    void testLogicalNot8() throws Exception {
        int[] results = match("[!c]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
        Assertions.assertEquals(16, results[0]);
        Assertions.assertEquals(16, results[1]);
    }

    @Test
    void testLogicalOr1() throws Exception {
        int[] results = match("[N,O,o]", "c1cc(C)c(N)cc1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testLogicalOr2() throws Exception {
        int[] results = match("[N,O,o]", "c1c(C)c(N)cnc1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testLogicalOr3() throws Exception {
        int[] results = match("[N,O,o]", "c1(C)c(N)cco1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testLogicalOr4() throws Exception {
        int[] results = match("[N,O,o]", "c1c(C)c(N)c[nH]1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testLogicalOr5() throws Exception {
        int[] results = match("[N,O,o]", "O=n1ccccc1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testLogicalOr6() throws Exception {
        int[] results = match("[N,O,o]", "[O-][n+]1ccccc1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testLogicalOr7() throws Exception {
        int[] results = match("[N,O,o]", "c1ncccc1C1CCCN1C");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testLogicalOr8() throws Exception {
        int[] results = match("[N,O,o]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    @Test
    void testLogicalOr9() throws Exception {
        int[] results = match("[N]=[N]-,=[N]", "CCCC(=O)C=C");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testLogicalOr10() throws Exception {
        int[] results = match("[N;$([N!X4])]!@;-[N;$([N!X4])]", "CCCC(=O)C=C");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testLogicalOr11() throws Exception {
        int[] results = match("[#6]!:;=[#6][#6](=O)[!O]", "CCCC(=O)C=C");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testLogicalOr12() throws Exception {
        int[] results = match("C=,#C", "C=CCC#C");
        Assertions.assertEquals(4, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testLogicalOrHighAnd1() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assertions.assertEquals(24, results[0]);
        Assertions.assertEquals(24, results[1]);
    }

    @Test
    void testLogicalOrHighAnd2() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assertions.assertEquals(25, results[0]);
        Assertions.assertEquals(25, results[1]);
    }

    @Test
    void testLogicalOrHighAnd3() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assertions.assertEquals(24, results[0]);
        Assertions.assertEquals(24, results[1]);
    }

    @Test
    void testLogicalOrHighAnd4() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assertions.assertEquals(21, results[0]);
        Assertions.assertEquals(21, results[1]);
    }

    @Test
    void testLogicalOrHighAnd5() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "N1N([Hg-][O+]=C1N=Nc2ccccc2)c3ccccc3");
        Assertions.assertEquals(17, results[0]);
        Assertions.assertEquals(17, results[1]);
    }

    @Test
    void testLogicalOrHighAnd6() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "[Na+].[Na+].[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24");
        Assertions.assertEquals(23, results[0]);
    }

    @Test
    void testLogicalOrHighAnd7() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "[Cl-].Clc1ccc([I+]c2cccs2)cc1");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(12, results[1]);
    }

    @Test
    void testLogicalOrLowAnd1() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assertions.assertEquals(15, results[0]);
        Assertions.assertEquals(15, results[1]);
    }

    @Test
    void testLogicalOrLowAnd2() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assertions.assertEquals(17, results[0]);
        Assertions.assertEquals(17, results[1]);
    }

    @Test
    void testLogicalOrLowAnd3() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assertions.assertEquals(13, results[0]);
        Assertions.assertEquals(13, results[1]);
    }

    @Test
    void testLogicalOrLowAnd4() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(12, results[1]);
    }

    @Test
    void testLogicalOrLowAnd5() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "N1N([Hg-][O+]=C1N=Nc2ccccc2)c3ccccc3");
        Assertions.assertEquals(5, results[0]);
        Assertions.assertEquals(5, results[1]);
    }

    /** The CDK aromaticity detection differs from Daylight - by persevering
     *  aromaticity from the SMILES we can match correctly.  */
    @Test
    void testLogicalOrLowAnd6() throws Exception {
        SMARTSQueryTool sqt = smarts("[#7,C;+0,+1]");
        IAtomContainer smi = smiles("[Na+].[Na+].[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24");
        int[] results = match(sqt, smi);
        Assertions.assertEquals(1, results[0]);
    }

    @Test
    void testLogicalOrLowAnd6_cdkAromaticity() throws Exception {
        SMARTSQueryTool sqt = smarts("[#7,C;+0,+1]");
        IAtomContainer smi = smiles("[Na+].[Na+].[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] results = match(sqt, smi);
        Assertions.assertEquals(8, results[0]);
    }

    @Test
    void testLogicalOrLowAnd7() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "[Cl-].Clc1ccc([I+]c2cccs2)cc1");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testRing1() throws Exception {
        int[] results = match("C1CCCCC1", "C1CCCCC1CCCC");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testRing2() throws Exception {
        int[] results = match("C1CCCCC1", "C1CCCCC1C1CCCCC1");
        Assertions.assertEquals(24, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testRing3() throws Exception {
        int[] results = match("C1CCCCC1", "C1CCCC12CCCCC2");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testRing4() throws Exception {
        int[] results = match("C1CCCCC1", "c1ccccc1O");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testRing5() throws Exception {
        int[] results = match("C1CCCCC1", "c1ccccc1CCCCCC");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testRing_large() throws Exception {
        int[] results = match("C%10CCCCC%10", "C1CCCCC1O");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testRing_large2() throws Exception {
        int[] results = match("C%99CCCCC%99", "C1CCCCC1O");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testRing_large3() throws Exception {
        int[] results = match("C%991CCCCC%99CCCC1", "C12CCCCC2CCCC1");
        Assertions.assertEquals(4, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testRing6() throws Exception {
        int[] results = match("C1CCCCC1", "CCCCCC");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testAromaticRing1() throws Exception {
        int[] results = match("c1ccccc1", "c1ccccc1");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAromaticRing2() throws Exception {
        int[] results = match("c1ccccc1", "c1cccc2c1cccc2");
        Assertions.assertEquals(24, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testAromaticRing3() throws Exception {
        int[] results = match("c1ccccn1", "c1cccc2c1cccc2");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testAromaticRing4() throws Exception {
        int[] results = match("c1ccccn1", "c1cccc2c1cccn2");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid1() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid2() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCCNC(N)=N)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid3() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC(N)=O)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid4() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC(O)=O)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid5() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CS)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid6() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCC(N)=O)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid7() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCC(O)=O)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid8() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC([H])C(O)=O");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testAminoAcid9() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CNC=N1)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid10() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C(CC)C)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid11() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC(C)C)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid12() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCCCN)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid13() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCSC)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid14() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CC=CC=C1)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid15() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "OC(C1CCCN1)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid16() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CO)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid17() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C(C)O)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid18() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CNC2=C1C=CC=C2)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid19() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CC=C(O)C=C1)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testAminoAcid20() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C(C)C)C(O)=O");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testCyclicUreas() throws Exception {
        int[] results = match("[$(C1CNC(=O)N1)]", "N1C(=O)NCC1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 1967468
     */
    @Test
    void testAcyclicUreas() throws Exception {
        int[] results = match("[$(CC);$(C1CNC(=O)N1)]", "C1CC1NC(=O)Nc2ccccc2");
        //        int[] results = match("[$([CR][NR][CR](=O)[NR])]", "C1CC1NC(=O)Nc2ccccc2");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    /**
     * @cdk.bug 1985811
     * @throws Exception
     */
    @Test
    void testIndoleAgainstIndole() throws Exception {
        int[] results = match("c1ccc2cc[nH]c2(c1)", "C1(NC=C2)=C2C=CC=C1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);

        results = match("c1ccc2cc[nH]c2(c1)", "c1ccc2cc[nH]c2(c1)");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);

    }

    /**
     * @cdk.bug 1985811
     * @throws Exception
     */
    @Test
    void testPyridineAgainstPyridine() throws Exception {
        int[] results = match("c1ccncc1", "c1ccncc1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);

        results = match("c1ccncc1", "C1=NC=CC=C1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testGroup5Elements() throws Exception {
        int[] results = match("[V,Cr,Mn,Nb,Mo,Tc,Ta,W,Re]", "[W]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testPeriodicGroupNumber() throws Exception {
        int[] results = match("[G14]", "CCN");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);

        results = match("[G14,G15]", "CCN");
        Assertions.assertEquals(3, results[0]);
        Assertions.assertEquals(3, results[1]);
    }

    @Test
    void testInvalidPeriodicGroupNumber() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            match("[G19]", "CCN");
        });
    }

    @Test
    void testInvalidPeriodicGroupNumber_2() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            match("[G0]", "CCN");
        });
    }

    @Test
    void testInvalidPeriodicGroupNumber_3() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            match("[G345]", "CCN");
        });
    }

    @Test
    void testNonPeriodicGroupNumber() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            match("[G]", "CCN"); // Should throw an exception if G is not followed by a number
        });
    }

    @Test
    void testNonPeriodicGroupNumber_2() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            match("[GA]", "CCN"); // Should throw an exception if G is not followed by a number
        });
    }

    @Test
    void testNonCHHeavyAtom() throws Exception {
        int[] results = match("[#X]", "CCN");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);

        results = match("[#X]", "CCNC(=O)CCSF");
        Assertions.assertEquals(4, results[0]);
        Assertions.assertEquals(4, results[1]);

        results = match("C#[#X]", "CCNC(=O)C#N");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);

        results = match("C#[#X]", "CCNC(=O)C#C");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);

    }

    @Test
    void testHybridizationNumber() throws Exception {
        int[] results = match(smarts("[^1]"), smilesAtomTyped("CCN"));
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);

        results = match(smarts("[^1]"), smilesAtomTyped("N#N"));
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);

        results = match(smarts("[^1&N]"), smilesAtomTyped("CC#C"));
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);

        results = match(smarts("[^1&N]"), smilesAtomTyped("CC#N"));
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);

        results = match(smarts("[^1&N,^2&C]"), smilesAtomTyped("CC(=O)CC(=O)CC#N"));
        Assertions.assertEquals(3, results[0]);
        Assertions.assertEquals(3, results[1]);

    }

    @Test
    void testBadHybridizationNumber() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            match("[^]", "CCN"); // Should throw an exception if ^ is not followed by a number
        });
    }

    @Test
    void testBadHybridizationNumber_2() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            match("[^X]", "CCN"); // Should throw an exception if ^ is not followed by a number
        });
    }

    @Test
    void testBadHybridizationNumber_3() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            match("[^0]", "CCN"); // Should throw an exception if ^ is not followed by a number
        });
    }

    @Test
    void testBadHybridizationNumber_4() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            match("[^9]", "CCN"); // Should throw an exception if ^ is not followed by a number
        });
    }

    /**
     * @cdk.bug  2589807
     * @throws Exception
     */
    @Test
    void testAromAliArom() throws Exception {
        int[] results = match("c-c", "COC1CN(CCC1NC(=O)C2=CC(=C(C=C2OC)N)Cl)CCCOC3=CC=C(C=C3)F");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);

        IAtomContainer m = smiles("c1ccccc1c2ccccc2");

        // note - missing explicit single bond, SMILES preserves the
        // aromatic specification but in this case we want the single
        // bond. as the molecule as assigned bond orders we can easily
        // remove the flags and reassign them correctly
        for (IBond bond : m.bonds())
            bond.setFlag(CDKConstants.ISAROMATIC, false);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
        Aromaticity.cdkLegacy().apply(m);

        results = match(smarts("c-c"), m);
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);

        results = match("c-c", "c1ccccc1-c1ccccc1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);

        results = match("cc", "c1ccccc1-c1ccccc1");
        Assertions.assertEquals(26, results[0]);
        Assertions.assertEquals(13, results[1]);

        results = match("cc", "c1ccccc1c2ccccc2");
        Assertions.assertEquals(26, results[0]);
        Assertions.assertEquals(13, results[1]);
    }

    @Test
    void testUnspecifiedBond() throws Exception {
        int[] results = match("CC", "CCc1ccccc1");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(1, results[1]);

        results = match("[#6][#6]", "CCc1ccccc1");
        Assertions.assertEquals(16, results[0]);
        Assertions.assertEquals(8, results[1]);

        results = match("[#6]-[#6]", "CCc1ccccc1");
        Assertions.assertEquals(4, results[0]);
        Assertions.assertEquals(2, results[1]);

        results = match("[#6]:[#6]", "CCc1ccccc1");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(6, results[1]);

        results = match("cc", "CCc1ccccc1");
        Assertions.assertEquals(12, results[0]);
        Assertions.assertEquals(6, results[1]);

        results = match("c-c", "CCc1ccccc1");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);

        results = match("c-C", "CCc1ccccc1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2587204
     */
    @Test
    void testLactamSimple() throws Exception {
        int[] results = match("[R0][ND3R][CR]=O", "N1(CC)C(=O)CCCC1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2587204
     */
    @Test
    void testLactamRecursive() throws Exception {
        int[] results = match("[R0]-[$([NRD3][CR]=O)]", "N1(CC)C(=O)CCCC1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    @Test
    void testLactamRecursiveAlternate() throws Exception {
        int[] results = match("[!R]-[$([NRD3][CR]=O)]", "N1(CC)C(=O)CCCC1");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * @cdk.bug 2898399
     * @throws Exception
     */
    @Test
    void testHydrogen() throws Exception {
        int[] results = match("[H]", "[H]");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * @cdk.bug 2898399
     * @throws Exception
     */
    @Test
    void testLeadingHydrogen() throws Exception {
        int[] results = match("[H][C@@]1(CCC(C)=CC1=O)C(C)=C", "[H][C@@]1(CCC(C)=CC1=O)C(C)=C");
        Assertions.assertEquals(1, results[0]);
        Assertions.assertEquals(1, results[1]);
    }

    /**
     * @cdk.bug 2871303
     * 
     * Note that this test passes, and really indicates that
     * the SMARTS below is not a correct one for vinylogous
     * esters
     */
    @Test
    void testVinylogousEster() throws Exception {
        int[] results = match("[#6X3](=[OX1])[#6X3]=,:[#6X3][#6;!$(C=[O,N,S])]", "c1ccccc1C=O");
        Assertions.assertEquals(2, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    /**
     * Check that bond order query respects aromaticity.
     *
     * @throws Exception
     */
    @Test
    void testBondOrderQueryKekuleVsSmiles() throws Exception {
        int[] results = match("[#6]=[#6]", "c1ccccc1c2ccccc2");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);

        results = match("[#6]=[#6]", "C1=C(C=CC=C1)C2=CC=CC=C2");
        Assertions.assertEquals(0, results[0]);
        Assertions.assertEquals(0, results[1]);
    }

    @Test
    void testSubstructureBug20141125() throws Exception {
        int[] results = match("[#6]S[#6]", "CSCCSCO");
        Assertions.assertEquals(4, results[0]);
        Assertions.assertEquals(2, results[1]);
    }

    @Test
    void testSubstructureBug20141125_2() throws Exception {
        int[] results = match("[#6]S[#6]", "CSCCSC(O)CCCSCC");
        Assertions.assertEquals(6, results[0]);
        Assertions.assertEquals(3, results[1]);
    }

    /**
     * Checks that when no number is specified for ring member ship any ring
     * atom is matched.
     *
     * @cdk.bug 1168
     */
    @Test
    void unspecifiedRingMembership() throws Exception {
        assertThat(match("[#6+0&R]=[#6+0&!R]", "C1=C2CCCC2CCC1"), is(new int[]{0, 0}));
    }

    @Test
    void cyclopropane() throws Exception {
        assertThat(match("**(*)*", "C1CC1"), is(new int[]{0, 0}));
    }

    @Test
    void componentGrouping1() throws Exception {
        assertThat(match("[#8].[#8]", "O"), is(new int[]{0, 0}));
        assertThat(match("[#8].[#8]", "O=O"), is(new int[]{2, 1}));
        assertThat(match("[#8].[#8]", "OCCO"), is(new int[]{2, 1}));
        assertThat(match("[#8].[#8]", "O.CCO"), is(new int[]{2, 1}));
    }

    @Test
    void componentGrouping2() throws Exception {
        assertThat(match("([#8].[#8])", "O"), is(new int[]{0, 0}));
        assertThat(match("([#8].[#8])", "O=O"), is(new int[]{2, 1}));
        assertThat(match("([#8].[#8])", "OCCO"), is(new int[]{2, 1}));
        assertThat(match("([#8].[#8])", "O.CCO"), is(new int[]{0, 0}));
    }

    @Test
    void componentGrouping3() throws Exception {
        assertThat(match("([#8]).([#8])", "O"), is(new int[]{0, 0}));
        assertThat(match("([#8]).([#8])", "O=O"), is(new int[]{0, 0}));
        assertThat(match("([#8]).([#8])", "OCCO"), is(new int[]{0, 0}));
        assertThat(match("([#8]).([#8])", "O.CCO"), is(new int[]{2, 1}));
    }

    /**
     * Ensure 'r' without a size is equivalent to !R0 and R.
     * @cdk.bug 1364
     */
    @Test
    void bug1364() throws Exception {
        assertThat(match("[!R0!R1]", "C[C@]12CC3CC([NH2+]CC(=O)NCC4CC4)(C1)C[C@@](C)(C3)C2"),
                   is(new int[]{7, 7}));
        assertThat(match("[R!R1]", "C[C@]12CC3CC([NH2+]CC(=O)NCC4CC4)(C1)C[C@@](C)(C3)C2"),
                   is(new int[]{7, 7}));
        assertThat(match("[r!R1]", "C[C@]12CC3CC([NH2+]CC(=O)NCC4CC4)(C1)C[C@@](C)(C3)C2"),
                   is(new int[]{7, 7}));
    }
}
