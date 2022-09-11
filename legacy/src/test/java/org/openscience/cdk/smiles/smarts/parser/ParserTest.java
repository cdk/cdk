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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;

import java.io.StringReader;

/**
 * JUnit test routines for the SMARTS parser.
 *
 * @author Egon Willighagen
 * @cdk.module test-smarts
 * @cdk.require ant1.6
 */
class ParserTest extends CDKTestCase {

    private void parse(String smarts) throws Exception {
        //SMARTSParser.parse(smarts);
        SMARTSParser parser = new SMARTSParser(new StringReader(smarts));
        parser.Start();
    }

    @Test
    void testQueryAtomCreation() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("*", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        Assertions.assertTrue(atom instanceof SMARTSAtom);
    }

    @Test
    void testAliphaticAtom() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("A", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        Assertions.assertTrue(atom instanceof SMARTSAtom);
    }

    @Test
    void testAromaticAtom() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("a", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        Assertions.assertTrue(atom instanceof SMARTSAtom);
    }

    @Test
    void testDegree() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("[D2]", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        Assertions.assertTrue(atom instanceof SMARTSAtom);
    }

    @Test
    void testImplicitHCount() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("[h3]", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        Assertions.assertTrue(atom instanceof SMARTSAtom);
    }

    @Test
    void testTotalHCount() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("[H2]", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        Assertions.assertTrue(atom instanceof SMARTSAtom);
    }

    /**
     * @cdk.bug 1760967
     */
    @Test
    void testSingleBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C-C", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(2, container.getAtomCount());
        Assertions.assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        Assertions.assertTrue(bond instanceof OrderQueryBond);
        OrderQueryBond qBond = (OrderQueryBond) bond;
        Assertions.assertEquals(IBond.Order.SINGLE, qBond.getOrder());
    }

    @Test
    void testDoubleBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C=C", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(2, container.getAtomCount());
        Assertions.assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        Assertions.assertTrue(bond instanceof OrderQueryBond);
        OrderQueryBond qBond = (OrderQueryBond) bond;
        Assertions.assertEquals(IBond.Order.DOUBLE, qBond.getOrder());
    }

    @Test
    void testTripleBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C#C", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(2, container.getAtomCount());
        Assertions.assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        Assertions.assertTrue(bond instanceof OrderQueryBond);
        OrderQueryBond qBond = (OrderQueryBond) bond;
        Assertions.assertEquals(IBond.Order.TRIPLE, qBond.getOrder());
    }

    @Test
    void testAromaticBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C:C", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(2, container.getAtomCount());
        Assertions.assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        Assertions.assertTrue(bond instanceof AromaticQueryBond);
    }

    @Test
    void testAnyOrderBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C~C", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(2, container.getAtomCount());
        Assertions.assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        Assertions.assertTrue(bond instanceof AnyOrderQueryBond);
    }

    /**
     * @cdk.bug 2786624
     */
    @Test
    void test2LetterSMARTS() throws CDKException {
        QueryAtomContainer query = SMARTSParser.parse("Sc1ccccc1", DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(7, query.getAtomCount());
        Assertions.assertEquals("S", query.getAtom(0).getSymbol());
    }

    @Test
    void testPattern1() throws Exception {
        parse("[CX4]");
    }

    @Test
    void testPattern2() throws Exception {
        parse("[$([CX2](=C)=C)]");
    }

    @Test
    void testPattern3() throws Exception {
        parse("[$([CX3]=[CX3])]");
    }

    @Test
    void testPattern4() throws Exception {
        parse("[$([CX2]#C)]");
    }

    @Test
    void testPattern5() throws Exception {
        parse("[CX3]=[OX1]");
    }

    @Test
    void testPattern6() throws Exception {
        parse("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]");
    }

    @Test
    void testPattern7() throws Exception {
        parse("[CX3](=[OX1])C");
    }

    @Test
    void testPattern8() throws Exception {
        parse("[OX1]=CN");
    }

    @Test
    void testPattern9() throws Exception {
        parse("[CX3](=[OX1])O");
    }

    @Test
    void testPattern10() throws Exception {
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    @Test
    void testPattern11() throws Exception {
        parse("[CX3H1](=O)[#6]");
    }

    @Test
    void testPattern12() throws Exception {
        parse("[CX3](=[OX1])[OX2][CX3](=[OX1])");
    }

    @Test
    void testPattern13() throws Exception {
        parse("[NX3][CX3](=[OX1])[#6]");
    }

    @Test
    void testPattern14() throws Exception {
        parse("[NX3][CX3]=[NX3+]");
    }

    @Test
    void testPattern15() throws Exception {
        parse("[NX3,NX4+][CX3](=[OX1])[OX2,OX1-]");
    }

    @Test
    void testPattern16() throws Exception {
        parse("[NX3][CX3](=[OX1])[OX2H0]");
    }

    @Test
    void testPattern17() throws Exception {
        parse("[NX3,NX4+][CX3](=[OX1])[OX2H,OX1-]");
    }

    @Test
    void testPattern18() throws Exception {
        parse("[CX3](=O)[O-]");
    }

    @Test
    void testPattern19() throws Exception {
        parse("[CX3](=[OX1])(O)O");
    }

    @Test
    void testPattern20() throws Exception {
        parse("[CX3](=[OX1])([OX2])[OX2H,OX1H0-1]");
    }

    @Test
    void testPattern21() throws Exception {
        parse("[CX3](=O)[OX2H1]");
    }

    @Test
    void testPattern22() throws Exception {
        parse("[CX3](=O)[OX1H0-,OX2H1]");
    }

    @Test
    void testPattern23() throws Exception {
        parse("[NX3][CX2]#[NX1]");
    }

    @Test
    void testPattern24() throws Exception {
        parse("[#6][CX3](=O)[OX2H0][#6]");
    }

    @Test
    void testPattern25() throws Exception {
        parse("[#6][CX3](=O)[#6]");
    }

    @Test
    void testPattern26() throws Exception {
        parse("[OD2]([#6])[#6]");
    }

    @Test
    void testPattern27() throws Exception {
        parse("[H]");
    }

    @Test
    void testPattern28() throws Exception {
        parse("[!#1]");
    }

    @Test
    void testPattern29() throws Exception {
        parse("[H+]");
    }

    @Test
    void testPattern30() throws Exception {
        parse("[+H]");
    }

    @Test
    void testPattern31() throws Exception {
        parse("[NX3;H2,H1;!$(NC=O)]");
    }

    @Test
    void testPattern32() throws Exception {
        parse("[NX3][CX3]=[CX3]");
    }

    @Test
    void testPattern33() throws Exception {
        parse("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }

    @Test
    void testPattern34() throws Exception {
        parse("[NX3][$(C=C),$(cc)]");
    }

    @Test
    void testPattern35() throws Exception {
        parse("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
    }

    @Test
    void testPattern36() throws Exception {
        parse("[NX3H2,NH3X4+][CX4H]([*])[CX3](=[OX1])[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-]");
    }

    @Test
    void testPattern37() throws Exception {
        parse("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    void testPattern38() throws Exception {
        parse("[CH3X4]");
    }

    @Test
    void testPattern39() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }

    @Test
    void testPattern40() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[NX3H2]");
    }

    @Test
    void testPattern41() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    void testPattern42() throws Exception {
        parse("[CH2X4][SX2H,SX1H0-]");
    }

    @Test
    void testPattern43() throws Exception {
        parse("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    void testPattern44() throws Exception {
        parse("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    void testPattern45() throws Exception {
        parse("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }

    @Test
    void testPattern47() throws Exception {
        parse("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }

    @Test
    void testPattern48() throws Exception {
        parse("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    void testPattern49() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }

    @Test
    void testPattern50() throws Exception {
        parse("[CH2X4][CH2X4][SX2][CH3X4]");
    }

    @Test
    void testPattern51() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }

    @Test
    void testPattern52() throws Exception {
        parse("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    void testPattern53() throws Exception {
        parse("[CH2X4][OX2H]");
    }

    @Test
    void testPattern54() throws Exception {
        parse("[NX3][CX3]=[SX1]");
    }

    @Test
    void testPattern55() throws Exception {
        parse("[CHX4]([CH3X4])[OX2H]");
    }

    @Test
    void testPattern56() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }

    @Test
    void testPattern57() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }

    @Test
    void testPattern58() throws Exception {
        parse("[CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    void testPattern59() throws Exception {
        parse("[CH3X4]");
    }

    @Test
    void testPattern60() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }

    @Test
    void testPattern61() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[NX3H2]");
    }

    @Test
    void testPattern62() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    void testPattern63() throws Exception {
        parse("[CH2X4][SX2H,SX1H0-]");
    }

    @Test
    void testPattern64() throws Exception {
        parse("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    void testPattern65() throws Exception {
        parse("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }

    @Test
    void testPattern67() throws Exception {
        parse("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }

    @Test
    void testPattern68() throws Exception {
        parse("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    void testPattern69() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }

    @Test
    void testPattern70() throws Exception {
        parse("[CH2X4][CH2X4][SX2][CH3X4]");
    }

    @Test
    void testPattern71() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }

    @Test
    void testPattern72() throws Exception {
        parse("[CH2X4][OX2H]");
    }

    @Test
    void testPattern73() throws Exception {
        parse("[CHX4]([CH3X4])[OX2H]");
    }

    @Test
    void testPattern74() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }

    @Test
    void testPattern75() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }

    @Test
    void testPattern76() throws Exception {
        parse("[CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    void testPattern77() throws Exception {
        parse("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }

    @Test
    void testPattern78() throws Exception {
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    @Test
    void testPattern79() throws Exception {
        parse("[#7]");
    }

    @Test
    void testPattern80() throws Exception {
        parse("[NX2]=N");
    }

    @Test
    void testPattern81() throws Exception {
        parse("[NX2]=[NX2]");
    }

    @Test
    void testPattern82() throws Exception {
        parse("[$([NX2]=[NX3+]([O-])[#6]),$([NX2]=[NX3+0](=[O])[#6])]");
    }

    @Test
    void testPattern83() throws Exception {
        parse("[$([#6]=[N+]=[N-]),$([#6-]-[N+]#[N])]");
    }

    @Test
    void testPattern84() throws Exception {
        parse("[$([nr5]:[nr5,or5,sr5]),$([nr5]:[cr5]:[nr5,or5,sr5])]");
    }

    @Test
    void testPattern85() throws Exception {
        parse("[NX3][NX3]");
    }

    @Test
    void testPattern86() throws Exception {
        parse("[NX3][NX2]=[*]");
    }

    @Test
    void testPattern87() throws Exception {
        parse("[CX3;$([C]([#6])[#6]),$([CH][#6])]=[NX2][#6]");
    }

    @Test
    void testPattern88() throws Exception {
        parse("[$([CX3]([#6])[#6]),$([CX3H][#6])]=[$([NX2][#6]),$([NX2H])]");
    }

    @Test
    void testPattern89() throws Exception {
        parse("[NX3+]=[CX3]");
    }

    @Test
    void testPattern90() throws Exception {
        parse("[CX3](=[OX1])[NX3H][CX3](=[OX1])");
    }

    @Test
    void testPattern91() throws Exception {
        parse("[CX3](=[OX1])[NX3H0]([#6])[CX3](=[OX1])");
    }

    @Test
    void testPattern92() throws Exception {
        parse("[CX3](=[OX1])[NX3H0]([NX3H0]([CX3](=[OX1]))[CX3](=[OX1]))[CX3](=[OX1])");
    }

    @Test
    void testPattern93() throws Exception {
        parse("[$([NX3](=[OX1])(=[OX1])O),$([NX3+]([OX1-])(=[OX1])O)]");
    }

    @Test
    void testPattern94() throws Exception {
        parse("[$([OX1]=[NX3](=[OX1])[OX1-]),$([OX1]=[NX3+]([OX1-])[OX1-])]");
    }

    @Test
    void testPattern95() throws Exception {
        parse("[NX1]#[CX2]");
    }

    @Test
    void testPattern96() throws Exception {
        parse("[CX1-]#[NX2+]");
    }

    @Test
    void testPattern97() throws Exception {
        parse("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }

    @Test
    void testPattern98() throws Exception {
        parse("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8].[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }

    @Test
    void testPattern99() throws Exception {
        parse("[NX2]=[OX1]");
    }

    @Test
    void testPattern101() throws Exception {
        parse("[$([#7+][OX1-]),$([#7v5]=[OX1]);!$([#7](~[O])~[O]);!$([#7]=[#7])]");
    }

    @Test
    void testPattern102() throws Exception {
        parse("[OX2H]");
    }

    @Test
    void testPattern103() throws Exception {
        parse("[#6][OX2H]");
    }

    @Test
    void testPattern104() throws Exception {
        parse("[OX2H][CX3]=[OX1]");
    }

    @Test
    void testPattern105() throws Exception {
        parse("[OX2H]P");
    }

    @Test
    void testPattern106() throws Exception {
        parse("[OX2H][#6X3]=[#6]");
    }

    @Test
    void testPattern107() throws Exception {
        parse("[OX2H][cX3]:[c]");
    }

    @Test
    void testPattern108() throws Exception {
        parse("[OX2H][$(C=C),$(cc)]");
    }

    @Test
    void testPattern109() throws Exception {
        parse("[$([OH]-*=[!#6])]");
    }

    @Test
    void testPattern110() throws Exception {
        parse("[OX2,OX1-][OX2,OX1-]");
    }

    @Test
    void testPattern111() throws Exception { // Phosphoric_acid groups.
        parse("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX"
                + "2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }

    @Test
    void testPattern112() throws Exception { // Phosphoric_ester groups.
        parse("[$(P(=[OX1])([OX2][#6])([$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)]),$([P+]([OX1-])([OX2][#6])(["
                + "$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)])]");
    }

    @Test
    void testPattern113() throws Exception {
        parse("[S-][CX3](=S)[#6]");
    }

    @Test
    void testPattern114() throws Exception {
        parse("[#6X3](=[SX1])([!N])[!N]");
    }

    @Test
    void testPattern115() throws Exception {
        parse("[SX2]");
    }

    @Test
    void testPattern116() throws Exception {
        parse("[#16X2H]");
    }

    @Test
    void testPattern117() throws Exception {
        parse("[#16!H0]");
    }

    @Test
    void testPattern118() throws Exception {
        parse("[NX3][CX3]=[SX1]");
    }

    @Test
    void testPattern119() throws Exception {
        parse("[#16X2H0]");
    }

    @Test
    void testPattern120() throws Exception {
        parse("[#16X2H0][!#16]");
    }

    @Test
    void testPattern121() throws Exception {
        parse("[#16X2H0][#16X2H0]");
    }

    @Test
    void testPattern122() throws Exception {
        parse("[#16X2H0][!#16].[#16X2H0][!#16]");
    }

    @Test
    void testPattern123() throws Exception {
        parse("[$([#16X3](=[OX1])[OX2H0]),$([#16X3+]([OX1-])[OX2H0])]");
    }

    @Test
    void testPattern124() throws Exception {
        parse("[$([#16X3](=[OX1])[OX2H,OX1H0-]),$([#16X3+]([OX1-])[OX2H,OX1H0-])]");
    }

    @Test
    void testPattern125() throws Exception {
        parse("[$([#16X4](=[OX1])=[OX1]),$([#16X4+2]([OX1-])[OX1-])]");
    }

    @Test
    void testPattern126() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[#6]),$([#16X4+2]([OX1-])([OX1-])([#6])[#6])]");
    }

    @Test
    void testPattern127() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }

    @Test
    void testPattern128() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H0]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H0])]");
    }

    @Test
    void testPattern129() throws Exception {
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[#6])]");
    }

    @Test
    void testPattern130() throws Exception {
        parse("[SX4](C)(C)(=O)=N");
    }

    @Test
    void testPattern131() throws Exception {
        parse("[$([SX4](=[OX1])(=[OX1])([!O])[NX3]),$([SX4+2]([OX1-])([OX1-])([!O])[NX3])]");
    }

    @Test
    void testPattern132() throws Exception {
        parse("[$([#16X3]=[OX1]),$([#16X3+][OX1-])]");
    }

    @Test
    void testPattern133() throws Exception {
        parse("[$([#16X3](=[OX1])([#6])[#6]),$([#16X3+]([OX1-])([#6])[#6])]");
    }

    @Test
    void testPattern134() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([OX2H,OX1H0-])[OX2][#6]),$([#16X4+2]([OX1-])([OX1-])([OX2H,OX1H0-])[OX2][#6])]");
    }

    @Test
    void testPattern135() throws Exception {
        parse("[$([SX4](=O)(=O)(O)O),$([SX4+2]([O-])([O-])(O)O)]");
    }

    @Test
    void testPattern136() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6]),$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6])]");
    }

    @Test
    void testPattern137() throws Exception {
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2][#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2][#6])]");
    }

    @Test
    void testPattern138() throws Exception {
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2H,OX1H0-]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2H,OX1H0-])]");
    }

    @Test
    void testPattern139() throws Exception {
        parse("[#16X2][OX2H,OX1H0-]");
    }

    @Test
    void testPattern140() throws Exception {
        parse("[#16X2][OX2H0]");
    }

    @Test
    void testPattern141() throws Exception {
        parse("[#6][F,Cl,Br,I]");
    }

    @Test
    void testPattern142() throws Exception {
        parse("[F,Cl,Br,I]");
    }

    @Test
    void testPattern143() throws Exception {
        parse("[F,Cl,Br,I].[F,Cl,Br,I].[F,Cl,Br,I]");
    }

    @Test
    void testPattern144() throws Exception {
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    @Test
    void testPattern145() throws Exception {
        parse("[$([#6X4@](*)(*)(*)*),$([#6X4@H](*)(*)*)]");
    }

    @Test
    void testPattern146() throws Exception {
        parse("[$([cX2+](:*):*)]");
    }

    @Test
    void testPattern147() throws Exception {
        parse("[$([cX3](:*):*),$([cX2+](:*):*)]");
    }

    @Test
    void testPattern148() throws Exception {
        parse("[$([cX3](:*):*),$([cX2+](:*):*),$([CX3]=*),$([CX2+]=*)]");
    }

    @Test
    void testPattern149() throws Exception {
        parse("[$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)]");
    }

    @Test
    void testPattern150() throws Exception {
        parse("[$([#1X1][$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)])]");
    }

    @Test
    void testPattern151() throws Exception {
        parse("[$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)]");
    }

    @Test
    void testPattern152() throws Exception {
        parse("[$([#1X1][$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)])]");
    }

    @Test
    void testPattern153() throws Exception {
        parse("[$([$([NX3]=O),$([NX3+][O-])])]");
    }

    @Test
    void testPattern154() throws Exception {
        parse("[$([$([NX4]=O),$([NX4+][O-])])]");
    }

    @Test
    void testPattern155() throws Exception {
        parse("[$([$([NX4]=O),$([NX4+][O-,#0])])]");
    }

    @Test
    void testPattern156() throws Exception {
        parse("[$([NX4+]),$([NX4]=*)]");
    }

    @Test
    void testPattern157() throws Exception {
        parse("[$([SX3]=N)]");
    }

    @Test
    void testPattern158() throws Exception {
        parse("[$([SX1]=[#6])]");
    }

    @Test
    void testPattern159() throws Exception {
        parse("[$([NX1]#*)]");
    }

    @Test
    void testPattern160() throws Exception {
        parse("[$([OX2])]");
    }

    @Test
    void testPattern161() throws Exception {
        parse("[R0;D2][R0;D2][R0;D2][R0;D2]");
    }

    @Test
    void testPattern162() throws Exception {
        parse("[R0;D2]~[R0;D2]~[R0;D2]~[R0;D2]");
    }

    @Test
    void testPattern163() throws Exception {
        parse("[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]");
    }

    @Test
    void testPattern164() throws Exception {
        parse("[!$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }

    @Test
    void testPattern165() throws Exception {
        parse("[$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }

    @Test
    void testPattern166() throws Exception {
        parse("[$([SX1]~P)]");
    }

    @Test
    void testPattern167() throws Exception {
        parse("[$([NX3]C=N)]");
    }

    @Test
    void testPattern168() throws Exception {
        parse("[$([NX3]N=C)]");
    }

    @Test
    void testPattern169() throws Exception {
        parse("[$([NX3]N=N)]");
    }

    @Test
    void testPattern170() throws Exception {
        parse("[$([OX2]C=N)]");
    }

    @Test
    void testPattern171() throws Exception {
        parse("[!$(*#*)&!D1]-!@[!$(*#*)&!D1]");
    }

    @Test
    void testPattern172() throws Exception {
        parse("[$([*R2]([*R])([*R])([*R]))].[$([*R2]([*R])([*R])([*R]))]");
    }

    @Test
    void testPattern173() throws Exception {
        parse("*-!:aa-!:*");
    }

    @Test
    void testPattern174() throws Exception {
        parse("*-!:aaa-!:*");
    }

    @Test
    void testPattern175() throws Exception {
        parse("*-!:aaaa-!:*");
    }

    @Test
    void testPattern176() throws Exception {
        parse("*-!@*");
    }

    @Test
    void testPattern177() throws Exception { // CIS or TRANS double or aromatic bond in a ring
        parse("*/,\\[R]=,:;@[R]/,\\*");
    }

    @Test
    void testPattern178() throws Exception { // Fused benzene rings
        parse("c12ccccc1cccc2");
    }

    @Test
    void testPattern179() throws Exception {
        parse("[r;!r3;!r4;!r5;!r6;!r7]");
    }

    @Test
    void testPattern180() throws Exception {
        parse("[sX2r5]");
    }

    @Test
    void testPattern181() throws Exception {
        parse("[oX2r5]");
    }

    @Test
    void testPattern182() throws Exception { // Unfused benzene ring
        parse("[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }

    @Test
    void testPattern183() throws Exception { // Multiple non-fused benzene rings
        parse("[cR1]1[cR1][cR1][cR1][cR1][cR1]1.[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }

    @Test
    void testPattern184() throws Exception { // Generic amino acid: low specificity.
        parse("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
    }

    @Test
    void testPattern185() throws Exception { //Template for 20 standard a.a.s
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),"
                + "$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX"
                + "4H2][CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    void testPattern186() throws Exception { // Proline
        parse("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    void testPattern187() throws Exception { // Glycine
        parse("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    void testPattern188() throws Exception { // Alanine
        parse("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([CH3X4])[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    void testPattern189() throws Exception { //18_standard_aa_side_chains.
        parse("([$([CH3X4]),$([CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]),"
                + "$([CH2X4][CX3](=[OX1])[NX3H2]),$([CH2X4][CX3](=[OX1])[OH0-,OH]),"
                + "$([CH2X4][SX2H,SX1H0-]),$([CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]),"
                + "$([CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:"
                + "[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1),"
                + "$([CHX4]([CH3X4])[CH2X4][CH3X4]),$([CH2X4][CHX4]([CH3X4])[CH3X4]),"
                + "$([CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]),$([CH2X4][CH2X4][SX2][CH3X4]),"
                + "$([CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1),$([CH2X4][OX2H]),"
                + "$([CHX4]([CH3X4])[OX2H]),$([CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12),"
                + "$([CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1),$([CHX4]([CH3X4])[CH3X4])])");
    }

    @Test
    void testPattern190() throws Exception { // N in Any_standard_amino_acid.
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3]"
                + "(=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3]"
                + "(=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([$([CH3X4]),"
                + "$([CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]),$"
                + "([CH2X4][CX3](=[OX1])[NX3H2]),$([CH2X4][CX3](=[OX1])[OH0-,OH]),"
                + "$([CH2X4][SX2H,SX1H0-]),$([CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]),"
                + "$([CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:"
                + "[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1),"
                + "$([CHX4]([CH3X4])[CH2X4][CH3X4]),$([CH2X4][CHX4]([CH3X4])[CH3X4]),"
                + "$([CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]),$([CH2X4][CH2X4][SX2][CH3X4]),"
                + "$([CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1),$([CH2X4][OX2H]),"
                + "$([CHX4]([CH3X4])[OX2H]),$([CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12),"
                + "$([CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1),"
                + "$([CHX4]([CH3X4])[CH3X4])])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    void testPattern191() throws Exception { // Non-standard amino acid.
        parse("[$([NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]);!$([$([$([NX3H,NX4H2+]),"
                + "$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),"
                + "$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N]),"
                + "$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([$([CH3X4]),$([CH2X4][CH2X4][CH2X4][NHX3][CH0X3]"
                + "(=[NH2X3+,NHX2+0])[NH2X3]),$([CH2X4][CX3](=[OX1])[NX3H2]),$([CH2X4][CX3](=[OX1])[OH0-,OH]),"
                + "$([CH2X4][SX2H,SX1H0-]),$([CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]),$([CH2X4][#6X3]1:"
                + "[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:"
                + "[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),"
                + "$([#7X3H])]:[#6X3H]1),$([CHX4]([CH3X4])[CH2X4][CH3X4]),$([CH2X4][CHX4]([CH3X4])[CH3X4]),"
                + "$([CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]),$([CH2X4][CH2X4][SX2][CH3X4]),"
                + "$([CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1),$([CH2X4][OX2H]),$([CHX4]([CH3X4])[OX2H]),"
                + "$([CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12),"
                + "$([CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1),"
                + "$([CHX4]([CH3X4])[CH3X4])])[CX3](=[OX1])[OX2H,OX1-,N])])]");
    }

    @Test
    void testPattern192() throws Exception { //Azide group
        parse("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }

    @Test
    void testPattern193() throws Exception { // Azide ion
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    @Test
    void testPattern194() throws Exception { //Azide or azide ion
        parse("[$([$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]),$([$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])])]");
    }

    @Test
    void testPattern195() throws Exception { // Sulfide
        parse("[#16X2H0]");
    }

    @Test
    void testPattern196() throws Exception { // Mono-sulfide
        parse("[#16X2H0][!#16]");
    }

    @Test
    void testPattern197() throws Exception { // Di-sulfide
        parse("[#16X2H0][#16X2H0]");
    }

    @Test
    void testPattern198() throws Exception { // Two sulfides
        parse("[#16X2H0][!#16].[#16X2H0][!#16]");
    }

    @Test
    void testPattern199() throws Exception { // Acid/conj-base
        parse("[OX2H,OX1H0-]");
    }

    @Test
    void testPattern200() throws Exception { // Non-acid Oxygen
        parse("[OX2H0]");
    }

    @Test
    void testPattern201() throws Exception { // Acid/base
        parse("[H1,H0-]");
    }

    @Test
    void testPattern202() throws Exception {
        parse("([Cl!$(Cl~c)].[c!$(c~Cl)])");
    }

    @Test
    void testPattern203() throws Exception {
        parse("([Cl]).([c])");
    }

    @Test
    void testPattern204() throws Exception {
        parse("([Cl].[c])");
    }

    @Test
    void testPattern205() throws Exception {
        parse("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }

    @Test
    void testPattern206() throws Exception {
        parse("[#0]");
    }

    @Test
    void testPattern207() throws Exception {
        parse("[*!H0,#1]");
    }

    @Test
    void testPattern208() throws Exception {
        parse("[#6!H0,#1]");
    }

    @Test
    void testPattern209() throws Exception {
        parse("[H,#1]");
    }

    @Test
    void testPattern210() throws Exception {
        parse("[!H0;F,Cl,Br,I,N+,$([OH]-*=[!#6]),+]");
    }

    @Test
    void testPattern211() throws Exception {
        parse("[CX3](=O)[OX2H1]");
    }

    @Test
    void testPattern212() throws Exception {
        parse("[CX3](=O)[OX1H0-,OX2H1]");
    }

    @Test
    void testPattern213() throws Exception {
        parse("[$([OH]-*=[!#6])]");
    }

    @Test
    void testPattern214() throws Exception { // Phosphoric_Acid
        parse("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX"
                + "2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }

    @Test
    void testPattern215() throws Exception { // Sulfonic Acid. High specificity.
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }

    @Test
    void testPattern216() throws Exception { // Acyl Halide
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    @Test
    void testPattern217() throws Exception {
        parse("[NX2-]");
    }

    @Test
    void testPattern218() throws Exception {
        parse("[OX2H+]=*");
    }

    @Test
    void testPattern219() throws Exception {
        parse("[OX3H2+]");
    }

    @Test
    void testPattern220() throws Exception {
        parse("[#6+]");
    }

    @Test
    void testPattern221() throws Exception {
        parse("[$([cX2+](:*):*)]");
    }

    @Test
    void testPattern222() throws Exception {
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    @Test
    void testPattern223() throws Exception {
        parse("[+1]~*~*~[-1]");
    }

    @Test
    void testPattern224() throws Exception {
        parse("[$([!-0!-1!-2!-3!-4]~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4])]");
    }

    @Test
    void testPattern225() throws Exception {
        parse("([!-0!-1!-2!-3!-4].[!+0!+1!+2!+3!+4])");
    }

    @Test
    void testPattern226() throws Exception { // Hydrogen-bond acceptor, Only hits carbonyl and nitroso
        parse("[#6,#7;R0]=[#8]");
    }

    @Test
    void testPattern227() throws Exception { // Hydrogen-bond acceptor
        parse("[!$([#6,F,Cl,Br,I,o,s,nX3,#7v5,#15v5,#16v4,#16v6,*+1,*+2,*+3])]");
    }

    @Test
    void testPattern228() throws Exception {
        parse("[!$([#6,H0,-,-2,-3])]");
    }

    @Test
    void testPattern229() throws Exception {
        parse("[!H0;#7,#8,#9]");
    }

    @Test
    void testPattern230() throws Exception {
        parse("[O,N;!H0]-*~*-*=[$([C,N;R0]=O)]");
    }

    @Test
    void testPattern231() throws Exception {
        parse("[#6;X3v3+0]");
    }

    @Test
    void testPattern232() throws Exception {
        parse("[#7;X2v4+0]");
    }

    @Test
    void testPattern233() throws Exception { // Amino Acid
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2]["
                + "CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    void testPattern234() throws Exception {
        parse("[#6][CX3](=O)[$([OX2H0]([#6])[#6]),$([#7])]");
    }

    @Test
    void testPattern235() throws Exception {
        parse("[#8]=[C,N]-aaa[F,Cl,Br,I]");
    }

    @Test
    void testPattern236() throws Exception {
        parse("[O,N;!H0;R0]");
    }

    @Test
    void testPattern237() throws Exception {
        parse("[#8]=[C,N]");
    }

    @Test
    void testPattern238() throws Exception { // PCB
        parse("[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]-[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]");
    }

    @Test
    void testPattern239() throws Exception { // Imidazolium Nitrogen
        parse("[nX3r5+]:c:n");
    }

    @Test
    void testPattern240() throws Exception { // 1-methyl-2-hydroxy benzene with either a Cl or H at the 5 position.
        parse("Cc1:c(O):c:c:[$(cCl),$([cH])]:c1");
    }

    @Test
    void testPattern241() throws Exception { // Nonstandard atom groups.
        parse("[!#1;!#2;!#3;!#5;!#6;!#7;!#8;!#9;!#11;!#12;!#15;!#16;!#17;!#19;!#20;!#35;!#53]");
    }

    @Test
    void testRing() throws Exception {
        parse("[$([C;#12]=1CCCCC1)]");
    }

    @Test
    void testHydrogen() throws Exception {
        parse("[H]");
    }

    @Test
    void testHybridizationNumber1() throws Exception {
        parse("[^1]");
    }

    @Test
    void testHybridizationNumber2() throws Exception {
        parse("[^1&N]");
    }

    @Test
    void testHybridizationNumber3() throws Exception {
        parse("[^1&N,^2&C]");
    }

    @Test
    void testHybridizationNumber4() throws Exception {
        Assertions.assertThrows(ParseException.class, () -> {
            parse("[^]");
        });
    }

    @Test
    void testHybridizationNumber5() throws Exception {
        Assertions.assertThrows(ParseException.class, () -> {
            parse("[^X]");
        });
    }

    @Test
    void testHybridizationNumber6() throws Exception {
        Assertions.assertThrows(ParseException.class, () -> {
            parse("[^0]");
        });
    }

    @Test
    void testHybridizationNumber7() throws Exception {
        Assertions.assertThrows(ParseException.class, () -> {
            parse("[^9]");
        });
    }

    @Test
    void testNonCHHeavyAtom1() throws Exception {
        parse("[#X]");
    }

    @Test
    void testNonCHHeavyAtom2() throws Exception {
        parse("C#[#X]");
    }

    @Test
    void testPeriodicGroupNumber1() throws Exception {
        parse("[G14]");
    }

    @Test
    void testPeriodicGroupNumber2() throws Exception {
        parse("[G14,G15]");
    }

    @Test
    void testPeriodicGroupNumber3() throws Exception {
        Assertions.assertThrows(ParseException.class, () -> {
            parse("[G19]");
        });
    }

    @Test
    void testPeriodicGroupNumber4() throws Exception {
        Assertions.assertThrows(ParseException.class, () -> {
            parse("[G0]");
        });
    }

    @Test
    void testPeriodicGroupNumber5() throws Exception {
        Assertions.assertThrows(ParseException.class, () -> {
            parse("[G345]");
        });
    }

    @Test
    void testPeriodicGroupNumber6() throws Exception {
        Assertions.assertThrows(ParseException.class, () -> {
            parse("[G]");
        });
    }

    @Test
    void testPeriodicGroupNumber7() throws Exception {
        Assertions.assertThrows(ParseException.class, () -> {
            parse("[GA]");
        });
    }

    @Test
    void testGroup5Elements() throws Exception {
        parse("[V,Cr,Mn,Nb,Mo,Tc,Ta,W,Re]");
    }
    
    @Test
    void endOnSpace() throws Exception {
        parse("C ");
    }

    @Test
    void endOnTab() throws Exception {
        parse("C\t");
    }

    @Test
    void endOnNewline() throws Exception {
        parse("C\n");
    }

    @Test
    void endOnCarriageReturn() throws Exception {
        parse("C\r");
    }

    /**
     * @cdk.bug 909
     */
    @Test
    void bug909() throws Exception {
        parse("O=C1NCCSc2ccccc12");
    }

}
