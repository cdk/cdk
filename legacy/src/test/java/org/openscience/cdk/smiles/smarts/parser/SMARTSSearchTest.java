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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
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
public class SMARTSSearchTest extends CDKTestCase {

    private static ILoggingTool        logger = LoggingToolFactory.createLoggingTool(SMARTSSearchTest.class);

    private UniversalIsomorphismTester uiTester;

    @Before
    public void setUpUITester() {
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
    public void testMoleculeFromSDF() throws CDKException {
        String filename = "cnssmarts.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        DefaultChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content;
        content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer atomContainer = cList.get(0);

        SMARTSQueryTool sqt = new SMARTSQueryTool("[NX3;h1,h2,H1,H2;!$(NC=O)]", DefaultChemObjectBuilder.getInstance());
        boolean status = sqt.matches(atomContainer);
        Assert.assertEquals(true, status);

        int nmatch = sqt.countMatches();
        int nqmatch = sqt.getUniqueMatchingAtoms().size();

        Assert.assertEquals(3, nmatch);
        Assert.assertEquals(3, nqmatch);

        sqt.setSmarts("[ND3]");
        status = sqt.matches(atomContainer);
        Assert.assertEquals(false, status);
    }

    @Test
    public void testRGraphBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("CC=O", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query c:c: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC=O"); // benzene, aromatic
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testAromaticBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("c:c", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query c:c: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C1CCCCC1"); // hexane, not aromatic
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testSingleBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C-C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C-C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C=C");
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C#C");
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testDoubleBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C=C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C=C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C=C");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C#C");
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testTripleBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C#C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C#C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C=C");
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C#C");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testAnyOrderBond() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C~C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C~C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C=C");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("C#C");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testAnyAtom() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("C*C", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query C*C: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("CNC");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("CCN");
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testAliphaticAtom() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("CAC", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query CAC: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("CNC");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testAromaticAtom() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("aaa", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query CaC: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testSymbolQueryAtom() throws Exception {
        QueryAtomContainer query = SMARTSParser.parse("CCC", DefaultChemObjectBuilder.getInstance());
        logger.debug("Query CAC: " + query.toString());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer atomContainer = sp.parseSmiles("CCC");
        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("CNC");
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));

        atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
        Assert.assertFalse(uiTester.isSubgraph(atomContainer, query));
    }

    /**
     * From http://www.daylight.com/dayhtml_tutorials/languages/smarts/index.html
     */
    @Test
    public void testPropertyCharge1() throws Exception {
        int[] results = match("[+1]", "[OH-].[Mg+2]");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testPropertyCharge2() throws Exception {
        int[] results = match("[+1]", "COCC(O)Cn1ccnc1[N+](=O)[O-]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyCharge3() throws Exception {
        int[] results = match("[+1]", "[NH4+]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyCharge4() throws Exception {
        int[] results = match("[+1]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testPropertyCharge5() throws Exception {
        int[] results = match("[+1]", "[Cl-].[Cl-].NC(=O)c2cc[n+](COC[n+]1ccccc1C=NO)cc2");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testPropertyAromatic1() throws Exception {
        int[] results = match("[a]", "c1cc(C)c(N)cc1");
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(6, results[1]);
    }

    @Test
    public void testPropertyAromatic2() throws Exception {
        int[] results = match("[a]", "c1c(C)c(N)cnc1");
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(6, results[1]);
    }

    @Test
    public void testPropertyAromatic3() throws Exception {
        int[] results = match("[a]", "c1(C)c(N)cco1");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    @Test
    public void testPropertyAromatic4() throws Exception {
        int[] results = match("[a]", "c1c(C)c(N)c[nH]1");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    @Test
    public void testPropertyAromatic5() throws Exception {
        int[] results = match("[a]", "O=n1ccccc1");
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(6, results[1]);
    }

    @Test
    public void testPropertyAromatic6() throws Exception {
        int[] results = match("[a]", "[O-][n+]1ccccc1");
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(6, results[1]);
    }

    @Test
    public void testPropertyAromatic7() throws Exception {
        int[] results = match("[a]", "c1ncccc1C1CCCN1C");
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(6, results[1]);
    }

    @Test
    public void testPropertyAromatic8() throws Exception {
        int[] results = match("[a]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(6, results[1]);
    }

    @Test
    public void testPropertyAliphatic1() throws Exception {
        int[] results = match("[A]", "c1cc(C)c(N)cc1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testPropertyAliphatic2() throws Exception {
        int[] results = match("[A]", "CCO");
        Assert.assertEquals(3, results[0]);
        Assert.assertEquals(3, results[1]);
    }

    @Test
    public void testPropertyAliphatic3() throws Exception {
        int[] results = match("[A]", "C=CC=CC=C");
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(6, results[1]);
    }

    @Test
    public void testPropertyAliphatic4() throws Exception {
        int[] results = match("[A]", "CC(C)(C)C");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    @Test
    public void testPropertyAliphatic5() throws Exception {
        int[] results = match("[A]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assert.assertEquals(15, results[0]);
        Assert.assertEquals(15, results[1]);
    }

    @Test
    public void testPropertyAliphatic6() throws Exception {
        int[] results = match("[A]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assert.assertEquals(19, results[0]);
        Assert.assertEquals(19, results[1]);
    }

    @Test
    public void testPropertyAtomicNumber1() throws Exception {
        int[] results = match("[#6]", "c1cc(C)c(N)cc1");
        Assert.assertEquals(7, results[0]);
        Assert.assertEquals(7, results[1]);
    }

    @Test
    public void testPropertyAtomicNumber2() throws Exception {
        int[] results = match("[#6]", "CCO");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testPropertyAtomicNumber3() throws Exception {
        int[] results = match("[#6]", "C=CC=CC=C-O");
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(6, results[1]);
    }

    @Test
    public void testPropertyAtomicNumber4() throws Exception {
        int[] results = match("[#6]", "CC(C)(C)C");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    @Test
    public void testPropertyAtomicNumber5() throws Exception {
        int[] results = match("[#6]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assert.assertEquals(20, results[0]);
        Assert.assertEquals(20, results[1]);
    }

    @Test
    public void testPropertyAtomicNumber6() throws Exception {
        int[] results = match("[#6]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assert.assertEquals(17, results[0]);
        Assert.assertEquals(17, results[1]);
    }

    @Test
    public void testPropertyAtomicNumber7() throws Exception {
        int[] results = match("[#6]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5");
        Assert.assertEquals(21, results[0]);
        Assert.assertEquals(21, results[1]);
    }

    /**
     * @cdk.bug 2686473
     * @throws Exception
     */
    @Test
    public void testPropertyAtomicNumber8() throws Exception {
        int[] results = match("[#16]", "COC1C(C(C(C(O1)CO)OC2C(C(C(C(O2)CO)S)O)O)O)O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * @cdk.bug 2686473
     * @throws Exception
     */
    @Test
    public void testPropertyAtomicNumber9() throws Exception {
        int[] results = match("[#6]", "[*]");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testPropertyR1() throws Exception {
        int[] results = match("[R2]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assert.assertEquals(7, results[0]);
        Assert.assertEquals(7, results[1]);
    }

    @Test
    public void testPropertyR2() throws Exception {
        SMARTSQueryTool sqt = smarts("[R2]");
        sqt.useSmallestSetOfSmallestRings(); // default for daylight
        int[] results = match(sqt, smiles("COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34"));
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(6, results[1]);

    }

    @Ignore("This feature was removed - essential rings aren't useful really")
    @Test
    public void testPropertyR2_essentialRings() throws Exception {
        SMARTSQueryTool sqt = smarts("[R2]");
        sqt.useEssentialRings();
        int[] results = match(sqt, smiles("COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34"));
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Ignore("This feature is pending but will be the combinded in an 'OpenSMARTS'"
            + " configuration which uses the relevant rings.")
    @Test
    public void testPropertyR2_relevantRings() throws Exception {
        SMARTSQueryTool sqt = smarts("[R2]");
        sqt.useRelevantRings();
        int[] results = match(sqt, smiles("COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34"));
        Assert.assertEquals(8, results[0]);
        Assert.assertEquals(8, results[1]);
    }

    @Test
    public void testPropertyR3() throws Exception {
        int[] results = match("[R2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assert.assertEquals(4, results[0]);
        Assert.assertEquals(4, results[1]);
    }

    @Test
    public void testPropertyR4() throws Exception {
        int[] results = match("[R2]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5");
        Assert.assertEquals(4, results[0]);
        Assert.assertEquals(4, results[1]);
    }

    @Test
    public void testPropertyR5() throws Exception {
        int[] results = match("[R2]", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testPropertyr1() throws Exception {
        int[] results = match("[r5]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assert.assertEquals(9, results[0]);
        Assert.assertEquals(9, results[1]);
    }

    @Test
    public void testPropertyr2() throws Exception {
        int[] results = match("[r5]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testPropertyr3() throws Exception {
        int[] results = match("[r5]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    @Test
    public void testPropertyr4() throws Exception {
        int[] results = match("[r5]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    @Test
    public void testPropertyr5() throws Exception {
        int[] results = match("[r5]", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    @Test
    public void quadBond() throws Exception {
        int[] results = match("*$*", "[Re]$[Re]");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);
    }
    
    @Test
    public void testPropertyValence1() throws Exception {
        int[] results = match("[v4]", "C");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyValence2() throws Exception {
        int[] results = match("[v4]", "CCO");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testPropertyValence3() throws Exception {
        int[] results = match("[v4]", "[NH4+]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyValence4() throws Exception {
        int[] results = match("[v4]", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O");
        Assert.assertEquals(16, results[0]);
        Assert.assertEquals(16, results[1]);
    }

    @Test
    public void testPropertyValence5() throws Exception {
        int[] results = match("[v4]", "[Cl-].[Cl-].NC(=O)c2cc[n+](COC[n+]1ccccc1C=NO)cc2");
        Assert.assertEquals(16, results[0]);
        Assert.assertEquals(16, results[1]);
    }

    @Test
    public void testPropertyX1() throws Exception {
        int[] results = match("[X2]", "CCO");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyX2() throws Exception {
        int[] results = match("[X2]", "O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyX3() throws Exception {
        int[] results = match("[X2]", "CCC(=O)CC");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testPropertyX4() throws Exception {
        int[] results = match("[X2]", "FC(Cl)=C=C(Cl)F");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyX5() throws Exception {
        int[] results = match("[X2]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assert.assertEquals(3, results[0]);
        Assert.assertEquals(3, results[1]);
    }

    @Test
    public void testPropertyX6() throws Exception {
        int[] results = match("[X2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assert.assertEquals(3, results[0]);
        Assert.assertEquals(3, results[1]);
    }

    @Test
    public void testPropertyD1() throws Exception {
        int[] results = match("[D2]", "CCO");
        Assert.assertEquals(1, results[0]);
    }

    @Test
    public void testPropertyD2() throws Exception {
        int[] results = match("[D2]", "O");
        Assert.assertEquals(0, results[0]);
    }

    @Test
    public void testPropertyD3() throws Exception {
        int[] results = match("[D2]", "CCC(=O)CC");
        Assert.assertEquals(2, results[0]);
    }

    @Test
    public void testPropertyD4() throws Exception {
        int[] results = match("[D2]", "FC(Cl)=C=C(Cl)F");
        Assert.assertEquals(1, results[0]);
    }

    @Test
    public void testPropertyD5() throws Exception {
        int[] results = match("[D2]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assert.assertEquals(12, results[0]);
    }

    @Test
    public void testPropertyD6() throws Exception {
        int[] results = match("[D2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assert.assertEquals(8, results[0]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    public void testPropertyD7() throws Exception {
        int[] results = match("[ND3]", "CCN([H])([H])");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    public void testPropertyD8() throws Exception {
        int[] results = match("[OD1]", "CO[H]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    public void testPropertyD9() throws Exception {
        int[] results;

        results = match("[OD1H]", "CO");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    public void testPropertyD10() throws Exception {
        int[] results;

        results = match("[OD1H]", "CO[H]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489417
     */
    @Test
    public void testPropertyD11() throws Exception {
        int[] results;

        results = match("[OD1H]-*", "CCO");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * With '*' matching 'H', this smarts matches twice 'OC' and 'O[H]'.
     * @cdk.bug 2489417
     */
    @Test
    public void testPropertyD12() throws Exception {
        int[] results;

        results = match("[OD1H]-*", "CCO[H]");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);

    }

    @Test
    public void testPropertyHAtom1() throws Exception {
        int[] results = match("[H]", "[H+].[Cl-]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyHAtom2() throws Exception {
        int[] results = match("[H]", "[2H]");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testPropertyHAtom3() throws Exception {
        int[] results = match("[H]", "[H][H]");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testPropertyHAtom4() throws Exception {
        int[] results = match("[H]", "[CH4]");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testPropertyHAtom5() throws Exception {
        int[] results = match("[H]", "[H]C([H])([H])[H]");
        Assert.assertEquals(4, results[0]);
        Assert.assertEquals(4, results[1]);
    }

    @Test
    public void testPropertyHTotal1() throws Exception {
        int[] results = match("[H1]", "CCO");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyHTotal2() throws Exception {
        int[] results = match("[H1]", "[2H]C#C");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testPropertyHTotal3() throws Exception {
        int[] results = match("[H1]", "[H]C(C)(C)C");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyHTotal4() throws Exception {
        int[] results = match("[H1]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assert.assertEquals(11, results[0]);
        Assert.assertEquals(11, results[1]);
    }

    @Test
    public void testPropertyHTotal5() throws Exception {
        int[] results = match("[H1]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assert.assertEquals(10, results[0]);
        Assert.assertEquals(10, results[1]);
    }

    @Test
    public void testPropertyHTotal6() throws Exception {
        int[] results = match("[H1]", "[H][H]");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testPropertyAnyAtom1() throws Exception {
        int[] results = match("[*]", "C");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyAnyAtom2() throws Exception {
        int[] results = match("[*]", "[2H]C");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testPropertyAnyAtom3() throws Exception {
        int[] results = match("[*]", "[1H][1H]");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testPropertyAnyAtom4() throws Exception {
        int[] results = match("[*]", "[1H]C([1H])([1H])[1H]");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    @Test
    public void testPropertyAnyAtom5() throws Exception {
        int[] results = match("[*]", "[H][H]");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2489533
     */
    @Test
    public void testPropertyAnyAtom6() throws Exception {
        int[] result = match("*", "CO");
        Assert.assertEquals(2, result[0]);
        Assert.assertEquals(2, result[1]);
    }

    /**
     * Bug was mistaken - '*' does match explicit H but in DEPICTMATCH H's are
     * suppressed by default.
     *
     * @throws Exception
     * @cdk.bug 2489533
     */
    @Test
    public void testPropertyAnyAtom7() throws Exception {
        int[] result = match("*", "CO[H]");
        Assert.assertEquals(3, result[0]);
        Assert.assertEquals(3, result[1]);
    }

    /**
     * Bug was mistaken - '*' does match explicit H but in DEPICTMATCH H's are
     * suppressed by default.
     *
     * @throws Exception
     * @cdk.bug 2489533
     */
    @Test
    public void testPropertyAnyAtom8() throws Exception {
        int[] result = match("*", "[H]C([H])([H])[H]");
        Assert.assertEquals(5, result[0]);
        Assert.assertEquals(5, result[1]);
    }

    /**
     * Bug was mistaken - '*' does match explicit H but in DEPICTMATCH H's are
     * suppressed by default.
     *
     * @throws Exception
     * @cdk.bug 2489533
     */
    @Test
    public void testPropertyAnyAtom9() throws Exception {
        int[] result = match("*", "CCCC([2H])[H]");
        Assert.assertEquals(6, result[0]);
        Assert.assertEquals(6, result[1]);
    }

    @Test
    public void testPropertyAtomicMass1() throws Exception {
        int[] results = match("[13C]", "[13C]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyAtomicMass2() throws Exception {
        int[] results = match("[13C]", "[C]");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testPropertyAtomicMass3() throws Exception {
        int[] results = match("[13*]", "[13C]Cl");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyAtomicMass4() throws Exception {
        int[] results = match("[12C]", "CCl");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    /**
     * @cdk.bug 2490336
     * @throws Exception
     */
    @Test
    public void testPropertyAtomicMass5() throws Exception {
        int[] results = match("[2H]", "CCCC([2H])[H]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyAtomicMass6() throws Exception {
        int[] results = match("[H]", "CCCC([2H])[H]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPropertyAtomicMass7() throws Exception {
        int[] results = match("[3H]", "CCCC([2H])([3H])[3H]");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testBondSingle1() throws Exception {
        int[] results = match("CC", "C=C");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testBondSingle2() throws Exception {
        int[] results = match("CC", "C#C");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testBondSingle3() throws Exception {
        int[] results = match("CC", "CCO");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testBondSingle4() throws Exception {
        int[] results = match("CC", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
        Assert.assertEquals(28, results[0]);
        Assert.assertEquals(14, results[1]);
    }

    @Test
    public void testBondSingle5() throws Exception {
        int[] results = match("CC", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O");
        Assert.assertEquals(14, results[0]);
        Assert.assertEquals(7, results[1]);
    }

    @Test
    public void testBondAny1() throws Exception {
        int[] results = match("C~C", "C=C");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testBondAny2() throws Exception {
        int[] results = match("C~C", "C#C");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testBondAny3() throws Exception {
        int[] results = match("C~C", "CCO");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testBondAny4() throws Exception {
        int[] results = match("C~C", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
        Assert.assertEquals(38, results[0]);
        Assert.assertEquals(19, results[1]);
    }

    @Test
    public void testBondAny5() throws Exception {
        int[] results = match("[C,c]~[C,c]", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O");
        Assert.assertEquals(28, results[0]);
        Assert.assertEquals(14, results[1]);
    }

    @Test
    public void testBondRing1() throws Exception {
        int[] results = match("C@C", "C=C");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testBondRing2() throws Exception {
        int[] results = match("C@C", "C#C");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testBondRing3() throws Exception {
        int[] results = match("C@C", "C1CCCCC1");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(6, results[1]);
    }

    @Test
    public void testBondRing4() throws Exception {
        int[] results = match("[C,c]@[C,c]", "c1ccccc1Cc1ccccc1");
        Assert.assertEquals(24, results[0]);
        Assert.assertEquals(12, results[1]);
    }

    @Test
    public void testBondRing5() throws Exception {
        int[] results = match("[C,c]@[C,c]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assert.assertEquals(30, results[0]);
        Assert.assertEquals(15, results[1]);
    }

    @Test
    public void testBondRing6() throws Exception {
        int[] results = match("[C,c]@[C,c]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assert.assertEquals(44, results[0]);
        Assert.assertEquals(22, results[1]);
    }

    @Test
    public void testBondStereo1() throws Exception {
        int[] results = match("F/?C=C/Cl", "F/C=C/Cl");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testBondStereo2() throws Exception {
        int[] results = match("F/?C=C/Cl", "FC=C/Cl");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testBondStereo3() throws Exception {
        int[] results = match("F/?C=C/Cl", "FC=CCl");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testBondStereo4() throws Exception {
        int[] results = match("F/?C=C/Cl", "F\\C=C/Cl");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testLogicalNot1() throws Exception {
        int[] results = match("[!c]", "c1cc(C)c(N)cc1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testLogicalNot2() throws Exception {
        int[] results = match("[!c]", "c1c(C)c(N)cnc1");
        Assert.assertEquals(3, results[0]);
        Assert.assertEquals(3, results[1]);
    }

    @Test
    public void testLogicalNot3() throws Exception {
        int[] results = match("[!c]", "c1(C)c(N)cco1");
        Assert.assertEquals(3, results[0]);
        Assert.assertEquals(3, results[1]);
    }

    @Test
    public void testLogicalNot4() throws Exception {
        int[] results = match("[!c]", "c1c(C)c(N)c[nH]1");
        Assert.assertEquals(3, results[0]);
        Assert.assertEquals(3, results[1]);
    }

    @Test
    public void testLogicalNot5() throws Exception {
        int[] results = match("[!c]", "O=n1ccccc1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testLogicalNot6() throws Exception {
        int[] results = match("[!c]", "[O-][n+]1ccccc1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testLogicalNot7() throws Exception {
        int[] results = match("[!c]", "c1ncccc1C1CCCN1C");
        Assert.assertEquals(7, results[0]);
        Assert.assertEquals(7, results[1]);
    }

    @Test
    public void testLogicalNot8() throws Exception {
        int[] results = match("[!c]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
        Assert.assertEquals(16, results[0]);
        Assert.assertEquals(16, results[1]);
    }

    @Test
    public void testLogicalOr1() throws Exception {
        int[] results = match("[N,O,o]", "c1cc(C)c(N)cc1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testLogicalOr2() throws Exception {
        int[] results = match("[N,O,o]", "c1c(C)c(N)cnc1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testLogicalOr3() throws Exception {
        int[] results = match("[N,O,o]", "c1(C)c(N)cco1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testLogicalOr4() throws Exception {
        int[] results = match("[N,O,o]", "c1c(C)c(N)c[nH]1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testLogicalOr5() throws Exception {
        int[] results = match("[N,O,o]", "O=n1ccccc1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testLogicalOr6() throws Exception {
        int[] results = match("[N,O,o]", "[O-][n+]1ccccc1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testLogicalOr7() throws Exception {
        int[] results = match("[N,O,o]", "c1ncccc1C1CCCN1C");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testLogicalOr8() throws Exception {
        int[] results = match("[N,O,o]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    @Test
    public void testLogicalOr9() throws Exception {
        int[] results = match("[N]=[N]-,=[N]", "CCCC(=O)C=C");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testLogicalOr10() throws Exception {
        int[] results = match("[N;$([N!X4])]!@;-[N;$([N!X4])]", "CCCC(=O)C=C");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testLogicalOr11() throws Exception {
        int[] results = match("[#6]!:;=[#6][#6](=O)[!O]", "CCCC(=O)C=C");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testLogicalOr12() throws Exception {
        int[] results = match("C=,#C", "C=CCC#C");
        Assert.assertEquals(4, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testLogicalOrHighAnd1() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assert.assertEquals(24, results[0]);
        Assert.assertEquals(24, results[1]);
    }

    @Test
    public void testLogicalOrHighAnd2() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assert.assertEquals(25, results[0]);
        Assert.assertEquals(25, results[1]);
    }

    @Test
    public void testLogicalOrHighAnd3() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assert.assertEquals(24, results[0]);
        Assert.assertEquals(24, results[1]);
    }

    @Test
    public void testLogicalOrHighAnd4() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assert.assertEquals(21, results[0]);
        Assert.assertEquals(21, results[1]);
    }

    @Test
    public void testLogicalOrHighAnd5() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "N1N([Hg-][O+]=C1N=Nc2ccccc2)c3ccccc3");
        Assert.assertEquals(17, results[0]);
        Assert.assertEquals(17, results[1]);
    }

    @Test
    public void testLogicalOrHighAnd6() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "[Na+].[Na+].[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24");
        Assert.assertEquals(23, results[0]);
    }

    @Test
    public void testLogicalOrHighAnd7() throws Exception {
        int[] results = match("[N,#6&+1,+0]", "[Cl-].Clc1ccc([I+]c2cccs2)cc1");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(12, results[1]);
    }

    @Test
    public void testLogicalOrLowAnd1() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assert.assertEquals(15, results[0]);
        Assert.assertEquals(15, results[1]);
    }

    @Test
    public void testLogicalOrLowAnd2() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assert.assertEquals(17, results[0]);
        Assert.assertEquals(17, results[1]);
    }

    @Test
    public void testLogicalOrLowAnd3() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
        Assert.assertEquals(13, results[0]);
        Assert.assertEquals(13, results[1]);
    }

    @Test
    public void testLogicalOrLowAnd4() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(12, results[1]);
    }

    @Test
    public void testLogicalOrLowAnd5() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "N1N([Hg-][O+]=C1N=Nc2ccccc2)c3ccccc3");
        Assert.assertEquals(5, results[0]);
        Assert.assertEquals(5, results[1]);
    }

    /** The CDK aromaticity detection differs from Daylight - by persevering
     *  aromaticity from the SMILES we can match correctly.  */
    @Test
    public void testLogicalOrLowAnd6() throws Exception {
        SMARTSQueryTool sqt = smarts("[#7,C;+0,+1]");
        IAtomContainer smi = smiles("[Na+].[Na+].[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24");
        int[] results = match(sqt, smi);
        Assert.assertEquals(1, results[0]);
    }

    @Test
    public void testLogicalOrLowAnd6_cdkAromaticity() throws Exception {
        SMARTSQueryTool sqt = smarts("[#7,C;+0,+1]");
        IAtomContainer smi = smiles("[Na+].[Na+].[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] results = match(sqt, smi);
        Assert.assertEquals(8, results[0]);
    }

    @Test
    public void testLogicalOrLowAnd7() throws Exception {
        int[] results = match("[#7,C;+0,+1]", "[Cl-].Clc1ccc([I+]c2cccs2)cc1");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testRing1() throws Exception {
        int[] results = match("C1CCCCC1", "C1CCCCC1CCCC");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testRing2() throws Exception {
        int[] results = match("C1CCCCC1", "C1CCCCC1C1CCCCC1");
        Assert.assertEquals(24, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testRing3() throws Exception {
        int[] results = match("C1CCCCC1", "C1CCCC12CCCCC2");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testRing4() throws Exception {
        int[] results = match("C1CCCCC1", "c1ccccc1O");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testRing5() throws Exception {
        int[] results = match("C1CCCCC1", "c1ccccc1CCCCCC");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testRing_large() throws Exception {
        int[] results = match("C%10CCCCC%10", "C1CCCCC1O");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testRing_large2() throws Exception {
        int[] results = match("C%99CCCCC%99", "C1CCCCC1O");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testRing_large3() throws Exception {
        int[] results = match("C%991CCCCC%99CCCC1", "C12CCCCC2CCCC1");
        Assert.assertEquals(4, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testRing6() throws Exception {
        int[] results = match("C1CCCCC1", "CCCCCC");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testAromaticRing1() throws Exception {
        int[] results = match("c1ccccc1", "c1ccccc1");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAromaticRing2() throws Exception {
        int[] results = match("c1ccccc1", "c1cccc2c1cccc2");
        Assert.assertEquals(24, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testAromaticRing3() throws Exception {
        int[] results = match("c1ccccn1", "c1cccc2c1cccc2");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testAromaticRing4() throws Exception {
        int[] results = match("c1ccccn1", "c1cccc2c1cccn2");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid1() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid2() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCCNC(N)=N)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid3() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC(N)=O)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid4() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC(O)=O)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid5() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CS)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid6() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCC(N)=O)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid7() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCC(O)=O)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid8() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC([H])C(O)=O");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testAminoAcid9() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CNC=N1)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid10() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C(CC)C)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid11() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC(C)C)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid12() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCCCN)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid13() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCSC)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid14() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CC=CC=C1)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid15() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "OC(C1CCCN1)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid16() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CO)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid17() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C(C)O)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid18() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CNC2=C1C=CC=C2)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid19() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CC=C(O)C=C1)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testAminoAcid20() throws Exception {
        int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C(C)C)C(O)=O");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testCyclicUreas() throws Exception {
        int[] results = match("[$(C1CNC(=O)N1)]", "N1C(=O)NCC1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 1967468
     */
    @Test
    public void testAcyclicUreas() throws Exception {
        int[] results = match("[$(CC);$(C1CNC(=O)N1)]", "C1CC1NC(=O)Nc2ccccc2");
        //        int[] results = match("[$([CR][NR][CR](=O)[NR])]", "C1CC1NC(=O)Nc2ccccc2");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    /**
     * @cdk.bug 1985811
     * @throws Exception
     */
    @Test
    public void testIndoleAgainstIndole() throws Exception {
        int[] results = match("c1ccc2cc[nH]c2(c1)", "C1(NC=C2)=C2C=CC=C1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);

        results = match("c1ccc2cc[nH]c2(c1)", "c1ccc2cc[nH]c2(c1)");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);

    }

    /**
     * @cdk.bug 1985811
     * @throws Exception
     */
    @Test
    public void testPyridineAgainstPyridine() throws Exception {
        int[] results = match("c1ccncc1", "c1ccncc1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);

        results = match("c1ccncc1", "C1=NC=CC=C1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testGroup5Elements() throws Exception {
        int[] results = match("[V,Cr,Mn,Nb,Mo,Tc,Ta,W,Re]", "[W]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testPeriodicGroupNumber() throws Exception {
        int[] results = match("[G14]", "CCN");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);

        results = match("[G14,G15]", "CCN");
        Assert.assertEquals(3, results[0]);
        Assert.assertEquals(3, results[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidPeriodicGroupNumber() throws Exception {
        match("[G19]", "CCN");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidPeriodicGroupNumber_2() throws Exception {
        match("[G0]", "CCN");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidPeriodicGroupNumber_3() throws Exception {
        match("[G345]", "CCN");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNonPeriodicGroupNumber() throws Exception {
        match("[G]", "CCN"); // Should throw an exception if G is not followed by a number
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNonPeriodicGroupNumber_2() throws Exception {
        match("[GA]", "CCN"); // Should throw an exception if G is not followed by a number
    }

    @Test
    public void testNonCHHeavyAtom() throws Exception {
        int[] results = match("[#X]", "CCN");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);

        results = match("[#X]", "CCNC(=O)CCSF");
        Assert.assertEquals(4, results[0]);
        Assert.assertEquals(4, results[1]);

        results = match("C#[#X]", "CCNC(=O)C#N");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);

        results = match("C#[#X]", "CCNC(=O)C#C");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);

    }

    @Test
    public void testHybridizationNumber() throws Exception {
        int[] results = match(smarts("[^1]"), smilesAtomTyped("CCN"));
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);

        results = match(smarts("[^1]"), smilesAtomTyped("N#N"));
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);

        results = match(smarts("[^1&N]"), smilesAtomTyped("CC#C"));
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);

        results = match(smarts("[^1&N]"), smilesAtomTyped("CC#N"));
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);

        results = match(smarts("[^1&N,^2&C]"), smilesAtomTyped("CC(=O)CC(=O)CC#N"));
        Assert.assertEquals(3, results[0]);
        Assert.assertEquals(3, results[1]);

    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadHybridizationNumber() throws Exception {
    	match("[^]", "CCN"); // Should throw an exception if ^ is not followed by a number
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadHybridizationNumber_2() throws Exception {
    	match("[^X]", "CCN"); // Should throw an exception if ^ is not followed by a number
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadHybridizationNumber_3() throws Exception {
    	match("[^0]", "CCN"); // Should throw an exception if ^ is not followed by a number
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadHybridizationNumber_4() throws Exception {
    	match("[^9]", "CCN"); // Should throw an exception if ^ is not followed by a number
    }

    /**
     * @cdk.bug  2589807
     * @throws Exception
     */
    @Test
    public void testAromAliArom() throws Exception {
        int[] results = match("c-c", "COC1CN(CCC1NC(=O)C2=CC(=C(C=C2OC)N)Cl)CCCOC3=CC=C(C=C3)F");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);

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
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);

        results = match("c-c", "c1ccccc1-c1ccccc1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);

        results = match("cc", "c1ccccc1-c1ccccc1");
        Assert.assertEquals(26, results[0]);
        Assert.assertEquals(13, results[1]);

        results = match("cc", "c1ccccc1c2ccccc2");
        Assert.assertEquals(26, results[0]);
        Assert.assertEquals(13, results[1]);
    }

    @Test
    public void testUnspecifiedBond() throws Exception {
        int[] results = match("CC", "CCc1ccccc1");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(1, results[1]);

        results = match("[#6][#6]", "CCc1ccccc1");
        Assert.assertEquals(16, results[0]);
        Assert.assertEquals(8, results[1]);

        results = match("[#6]-[#6]", "CCc1ccccc1");
        Assert.assertEquals(4, results[0]);
        Assert.assertEquals(2, results[1]);

        results = match("[#6]:[#6]", "CCc1ccccc1");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(6, results[1]);

        results = match("cc", "CCc1ccccc1");
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(6, results[1]);

        results = match("c-c", "CCc1ccccc1");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);

        results = match("c-C", "CCc1ccccc1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2587204
     */
    @Test
    public void testLactamSimple() throws Exception {
        int[] results = match("[R0][ND3R][CR]=O", "N1(CC)C(=O)CCCC1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * @throws Exception
     * @cdk.bug 2587204
     */
    @Test
    public void testLactamRecursive() throws Exception {
        int[] results = match("[R0]-[$([NRD3][CR]=O)]", "N1(CC)C(=O)CCCC1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Test
    public void testLactamRecursiveAlternate() throws Exception {
        int[] results = match("[!R]-[$([NRD3][CR]=O)]", "N1(CC)C(=O)CCCC1");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * @cdk.bug 2898399
     * @throws Exception
     */
    @Test
    public void testHydrogen() throws Exception {
        int[] results = match("[H]", "[H]");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * @cdk.bug 2898399
     * @throws Exception
     */
    @Test
    public void testLeadingHydrogen() throws Exception {
        int[] results = match("[H][C@@]1(CCC(C)=CC1=O)C(C)=C", "[H][C@@]1(CCC(C)=CC1=O)C(C)=C");
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    /**
     * @cdk.bug 2871303
     * 
     * Note that this test passes, and really indicates that
     * the SMARTS below is not a correct one for vinylogous
     * esters
     */
    @Test
    public void testVinylogousEster() throws Exception {
        int[] results = match("[#6X3](=[OX1])[#6X3]=,:[#6X3][#6;!$(C=[O,N,S])]", "c1ccccc1C=O");
        Assert.assertEquals(2, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    /**
     * Check that bond order query respects aromaticity.
     *
     * @throws Exception
     */
    @Test
    public void testBondOrderQueryKekuleVsSmiles() throws Exception {
        int[] results = match("[#6]=[#6]", "c1ccccc1c2ccccc2");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);

        results = match("[#6]=[#6]", "C1=C(C=CC=C1)C2=CC=CC=C2");
        Assert.assertEquals(0, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testSubstructureBug20141125() throws Exception {
        int[] results = match("[#6]S[#6]", "CSCCSCO");
        Assert.assertEquals(4, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void testSubstructureBug20141125_2() throws Exception {
        int[] results = match("[#6]S[#6]", "CSCCSC(O)CCCSCC");
        Assert.assertEquals(6, results[0]);
        Assert.assertEquals(3, results[1]);
    }

    /**
     * Checks that when no number is specified for ring member ship any ring
     * atom is matched.
     *
     * @cdk.bug 1168
     */
    @Test
    public void unspecifiedRingMembership() throws Exception {
        assertThat(match("[#6+0&R]=[#6+0&!R]", "C1=C2CCCC2CCC1"), is(new int[]{0, 0}));
    }

    @Test
    public void cyclopropane() throws Exception {
        assertThat(match("**(*)*", "C1CC1"), is(new int[]{0, 0}));
    }

    @Test
    public void componentGrouping1() throws Exception {
        assertThat(match("[#8].[#8]", "O"), is(new int[]{0, 0}));
        assertThat(match("[#8].[#8]", "O=O"), is(new int[]{2, 1}));
        assertThat(match("[#8].[#8]", "OCCO"), is(new int[]{2, 1}));
        assertThat(match("[#8].[#8]", "O.CCO"), is(new int[]{2, 1}));
    }

    @Test
    public void componentGrouping2() throws Exception {
        assertThat(match("([#8].[#8])", "O"), is(new int[]{0, 0}));
        assertThat(match("([#8].[#8])", "O=O"), is(new int[]{2, 1}));
        assertThat(match("([#8].[#8])", "OCCO"), is(new int[]{2, 1}));
        assertThat(match("([#8].[#8])", "O.CCO"), is(new int[]{0, 0}));
    }

    @Test
    public void componentGrouping3() throws Exception {
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
    public void bug1364() throws Exception {
        assertThat(match("[!R0!R1]", "C[C@]12CC3CC([NH2+]CC(=O)NCC4CC4)(C1)C[C@@](C)(C3)C2"),
                   is(new int[]{7, 7}));
        assertThat(match("[R!R1]", "C[C@]12CC3CC([NH2+]CC(=O)NCC4CC4)(C1)C[C@@](C)(C3)C2"),
                   is(new int[]{7, 7}));
        assertThat(match("[r!R1]", "C[C@]12CC3CC([NH2+]CC(=O)NCC4CC4)(C1)C[C@@](C)(C3)C2"),
                   is(new int[]{7, 7}));
    }
}
