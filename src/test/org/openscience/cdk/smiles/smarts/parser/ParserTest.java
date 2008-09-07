/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

import java.io.StringReader;
import java.util.Iterator;

/**
 * JUnit test routines for the SMARTS parser.
 *
 * @author Egon Willighagen
 * @cdk.module test-smarts
 * @cdk.require ant1.6
 */
public class ParserTest extends CDKTestCase {
    public ParserTest() {
    }

    public ParserTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ParserTest.class);
    }

    public void parse(String smarts) throws Exception {
        //SMARTSParser.parse(smarts);
        SMARTSParser parser = new SMARTSParser(new StringReader(smarts));
        parser.Start();
    }

    public void testQueryAtomCreation() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("*");
        assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        assertTrue(atom instanceof SMARTSAtom);
    }

    public void testAliphaticAtom() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("A");
        assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        assertTrue(atom instanceof SMARTSAtom);
    }

    public void testAromaticAtom() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("a");
        assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        assertTrue(atom instanceof SMARTSAtom);
    }

    public void testDegree() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("[D2]");
        assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        assertTrue(atom instanceof SMARTSAtom);
    }

    public void testImplicitHCount() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("[h3]");
        assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        assertTrue(atom instanceof SMARTSAtom);
    }

    public void testTotalHCount() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("[H2]");
        assertEquals(1, container.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
        assertTrue(atom instanceof SMARTSAtom);
    }

    /**
     * @cdk.bug 1760967
     */
    public void testSingleBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C-C");
        assertEquals(2, container.getAtomCount());
        assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        assertTrue(bond instanceof OrderQueryBond);
        OrderQueryBond qBond = (OrderQueryBond) bond;
        assertEquals(IBond.Order.SINGLE, qBond.getOrder());
    }

    public void testDoubleBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C=C");
        assertEquals(2, container.getAtomCount());
        assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        assertTrue(bond instanceof OrderQueryBond);
        OrderQueryBond qBond = (OrderQueryBond) bond;
        assertEquals(IBond.Order.DOUBLE, qBond.getOrder());
    }

    public void testTripleBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C#C");
        assertEquals(2, container.getAtomCount());
        assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        assertTrue(bond instanceof OrderQueryBond);
        OrderQueryBond qBond = (OrderQueryBond) bond;
        assertEquals(IBond.Order.TRIPLE, qBond.getOrder());
    }

    public void testAromaticBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C:C");
        assertEquals(2, container.getAtomCount());
        assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        assertTrue(bond instanceof AromaticQueryBond);
    }

    public void testAnyOrderBond() throws Exception {
        QueryAtomContainer container = SMARTSParser.parse("C~C");
        assertEquals(2, container.getAtomCount());
        assertEquals(1, container.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
        assertTrue(bond instanceof AnyOrderQueryBond);
    }

    public void testPattern1() throws Exception {
        parse("[CX4]");
    }

    public void testPattern2() throws Exception {
        parse("[$([CX2](=C)=C)]");
    }

    public void testPattern3() throws Exception {
        parse("[$([CX3]=[CX3])]");
    }

    public void testPattern4() throws Exception {
        parse("[$([CX2]#C)]");
    }

    public void testPattern5() throws Exception {
        parse("[CX3]=[OX1]");
    }

    public void testPattern6() throws Exception {
        parse("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]");
    }

    public void testPattern7() throws Exception {
        parse("[CX3](=[OX1])C");
    }

    public void testPattern8() throws Exception {
        parse("[OX1]=CN");
    }

    public void testPattern9() throws Exception {
        parse("[CX3](=[OX1])O");
    }

    public void testPattern10() throws Exception {
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    public void testPattern11() throws Exception {
        parse("[CX3H1](=O)[#6]");
    }

    public void testPattern12() throws Exception {
        parse("[CX3](=[OX1])[OX2][CX3](=[OX1])");
    }

    public void testPattern13() throws Exception {
        parse("[NX3][CX3](=[OX1])[#6]");
    }

    public void testPattern14() throws Exception {
        parse("[NX3][CX3]=[NX3+]");
    }

    public void testPattern15() throws Exception {
        parse("[NX3,NX4+][CX3](=[OX1])[OX2,OX1-]");
    }

    public void testPattern16() throws Exception {
        parse("[NX3][CX3](=[OX1])[OX2H0]");
    }

    public void testPattern17() throws Exception {
        parse("[NX3,NX4+][CX3](=[OX1])[OX2H,OX1-]");
    }

    public void testPattern18() throws Exception {
        parse("[CX3](=O)[O-]");
    }

    public void testPattern19() throws Exception {
        parse("[CX3](=[OX1])(O)O");
    }

    public void testPattern20() throws Exception {
        parse("[CX3](=[OX1])([OX2])[OX2H,OX1H0-1]");
    }

    public void testPattern21() throws Exception {
        parse("[CX3](=O)[OX2H1]");
    }

    public void testPattern22() throws Exception {
        parse("[CX3](=O)[OX1H0-,OX2H1]");
    }

    public void testPattern23() throws Exception {
        parse("[NX3][CX2]#[NX1]");
    }

    public void testPattern24() throws Exception {
        parse("[#6][CX3](=O)[OX2H0][#6]");
    }

    public void testPattern25() throws Exception {
        parse("[#6][CX3](=O)[#6]");
    }

    public void testPattern26() throws Exception {
        parse("[OD2]([#6])[#6]");
    }

    public void testPattern27() throws Exception {
        parse("[H]");
    }

    public void testPattern28() throws Exception {
        parse("[!#1]");
    }

    public void testPattern29() throws Exception {
        parse("[H+]");
    }

    public void testPattern30() throws Exception {
        parse("[+H]");
    }

    public void testPattern31() throws Exception {
        parse("[NX3;H2,H1;!$(NC=O)]");
    }

    public void testPattern32() throws Exception {
        parse("[NX3][CX3]=[CX3]");
    }

    public void testPattern33() throws Exception {
        parse("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }

    public void testPattern34() throws Exception {
        parse("[NX3][$(C=C),$(cc)]");
    }

    public void testPattern35() throws Exception {
        parse("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
    }

    public void testPattern36() throws Exception {
        parse("[NX3H2,NH3X4+][CX4H]([*])[CX3](=[OX1])[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-]");
    }

    public void testPattern37() throws Exception {
        parse("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    public void testPattern38() throws Exception {
        parse("[CH3X4]");
    }

    public void testPattern39() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }

    public void testPattern40() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[NX3H2]");
    }

    public void testPattern41() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    public void testPattern42() throws Exception {
        parse("[CH2X4][SX2H,SX1H0-]");
    }

    public void testPattern43() throws Exception {
        parse("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    public void testPattern44() throws Exception {
        parse("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    public void testPattern45() throws Exception {
        parse("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }

    public void testPattern47() throws Exception {
        parse("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }

    public void testPattern48() throws Exception {
        parse("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }

    public void testPattern49() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }

    public void testPattern50() throws Exception {
        parse("[CH2X4][CH2X4][SX2][CH3X4]");
    }

    public void testPattern51() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }

    public void testPattern52() throws Exception {
        parse("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    public void testPattern53() throws Exception {
        parse("[CH2X4][OX2H]");
    }

    public void testPattern54() throws Exception {
        parse("[NX3][CX3]=[SX1]");
    }

    public void testPattern55() throws Exception {
        parse("[CHX4]([CH3X4])[OX2H]");
    }

    public void testPattern56() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }

    public void testPattern57() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }

    public void testPattern58() throws Exception {
        parse("[CHX4]([CH3X4])[CH3X4]");
    }

    public void testPattern59() throws Exception {
        parse("[CH3X4]");
    }

    public void testPattern60() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }

    public void testPattern61() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[NX3H2]");
    }

    public void testPattern62() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    public void testPattern63() throws Exception {
        parse("[CH2X4][SX2H,SX1H0-]");
    }

    public void testPattern64() throws Exception {
        parse("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    public void testPattern65() throws Exception {
        parse("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }

    public void testPattern67() throws Exception {
        parse("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }

    public void testPattern68() throws Exception {
        parse("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }

    public void testPattern69() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }

    public void testPattern70() throws Exception {
        parse("[CH2X4][CH2X4][SX2][CH3X4]");
    }

    public void testPattern71() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }

    public void testPattern72() throws Exception {
        parse("[CH2X4][OX2H]");
    }

    public void testPattern73() throws Exception {
        parse("[CHX4]([CH3X4])[OX2H]");
    }

    public void testPattern74() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }

    public void testPattern75() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }

    public void testPattern76() throws Exception {
        parse("[CHX4]([CH3X4])[CH3X4]");
    }

    public void testPattern77() throws Exception {
        parse("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }

    public void testPattern78() throws Exception {
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    public void testPattern79() throws Exception {
        parse("[#7]");
    }

    public void testPattern80() throws Exception {
        parse("[NX2]=N");
    }

    public void testPattern81() throws Exception {
        parse("[NX2]=[NX2]");
    }

    public void testPattern82() throws Exception {
        parse("[$([NX2]=[NX3+]([O-])[#6]),$([NX2]=[NX3+0](=[O])[#6])]");
    }

    public void testPattern83() throws Exception {
        parse("[$([#6]=[N+]=[N-]),$([#6-]-[N+]#[N])]");
    }

    public void testPattern84() throws Exception {
        parse("[$([nr5]:[nr5,or5,sr5]),$([nr5]:[cr5]:[nr5,or5,sr5])]");
    }

    public void testPattern85() throws Exception {
        parse("[NX3][NX3]");
    }

    public void testPattern86() throws Exception {
        parse("[NX3][NX2]=[*]");
    }

    public void testPattern87() throws Exception {
        parse("[CX3;$([C]([#6])[#6]),$([CH][#6])]=[NX2][#6]");
    }

    public void testPattern88() throws Exception {
        parse("[$([CX3]([#6])[#6]),$([CX3H][#6])]=[$([NX2][#6]),$([NX2H])]");
    }

    public void testPattern89() throws Exception {
        parse("[NX3+]=[CX3]");
    }

    public void testPattern90() throws Exception {
        parse("[CX3](=[OX1])[NX3H][CX3](=[OX1])");
    }

    public void testPattern91() throws Exception {
        parse("[CX3](=[OX1])[NX3H0]([#6])[CX3](=[OX1])");
    }

    public void testPattern92() throws Exception {
        parse("[CX3](=[OX1])[NX3H0]([NX3H0]([CX3](=[OX1]))[CX3](=[OX1]))[CX3](=[OX1])");
    }

    public void testPattern93() throws Exception {
        parse("[$([NX3](=[OX1])(=[OX1])O),$([NX3+]([OX1-])(=[OX1])O)]");
    }

    public void testPattern94() throws Exception {
        parse("[$([OX1]=[NX3](=[OX1])[OX1-]),$([OX1]=[NX3+]([OX1-])[OX1-])]");
    }

    public void testPattern95() throws Exception {
        parse("[NX1]#[CX2]");
    }

    public void testPattern96() throws Exception {
        parse("[CX1-]#[NX2+]");
    }

    public void testPattern97() throws Exception {
        parse("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }

    public void testPattern98() throws Exception {
        parse("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8].[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }

    public void testPattern99() throws Exception {
        parse("[NX2]=[OX1]");
    }

    public void testPattern101() throws Exception {
        parse("[$([#7+][OX1-]),$([#7v5]=[OX1]);!$([#7](~[O])~[O]);!$([#7]=[#7])]");
    }

    public void testPattern102() throws Exception {
        parse("[OX2H]");
    }

    public void testPattern103() throws Exception {
        parse("[#6][OX2H]");
    }

    public void testPattern104() throws Exception {
        parse("[OX2H][CX3]=[OX1]");
    }

    public void testPattern105() throws Exception {
        parse("[OX2H]P");
    }

    public void testPattern106() throws Exception {
        parse("[OX2H][#6X3]=[#6]");
    }

    public void testPattern107() throws Exception {
        parse("[OX2H][cX3]:[c]");
    }

    public void testPattern108() throws Exception {
        parse("[OX2H][$(C=C),$(cc)]");
    }

    public void testPattern109() throws Exception {
        parse("[$([OH]-*=[!#6])]");
    }

    public void testPattern110() throws Exception {
        parse("[OX2,OX1-][OX2,OX1-]");
    }

    public void testPattern111() throws Exception { // Phosphoric_acid groups.
        parse("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX" +
                "2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }

    public void testPattern112() throws Exception { // Phosphoric_ester groups.
        parse("[$(P(=[OX1])([OX2][#6])([$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)]),$([P+]([OX1-])([OX2][#6])([" +
                "$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)])]");
    }

    public void testPattern113() throws Exception {
        parse("[S-][CX3](=S)[#6]");
    }

    public void testPattern114() throws Exception {
        parse("[#6X3](=[SX1])([!N])[!N]");
    }

    public void testPattern115() throws Exception {
        parse("[SX2]");
    }

    public void testPattern116() throws Exception {
        parse("[#16X2H]");
    }

    public void testPattern117() throws Exception {
        parse("[#16!H0]");
    }

    public void testPattern118() throws Exception {
        parse("[NX3][CX3]=[SX1]");
    }

    public void testPattern119() throws Exception {
        parse("[#16X2H0]");
    }

    public void testPattern120() throws Exception {
        parse("[#16X2H0][!#16]");
    }

    public void testPattern121() throws Exception {
        parse("[#16X2H0][#16X2H0]");
    }

    public void testPattern122() throws Exception {
        parse("[#16X2H0][!#16].[#16X2H0][!#16]");
    }

    public void testPattern123() throws Exception {
        parse("[$([#16X3](=[OX1])[OX2H0]),$([#16X3+]([OX1-])[OX2H0])]");
    }

    public void testPattern124() throws Exception {
        parse("[$([#16X3](=[OX1])[OX2H,OX1H0-]),$([#16X3+]([OX1-])[OX2H,OX1H0-])]");
    }

    public void testPattern125() throws Exception {
        parse("[$([#16X4](=[OX1])=[OX1]),$([#16X4+2]([OX1-])[OX1-])]");
    }

    public void testPattern126() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[#6]),$([#16X4+2]([OX1-])([OX1-])([#6])[#6])]");
    }

    public void testPattern127() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }

    public void testPattern128() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H0]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H0])]");
    }

    public void testPattern129() throws Exception {
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[#6])]");
    }

    public void testPattern130() throws Exception {
        parse("[SX4](C)(C)(=O)=N");
    }

    public void testPattern131() throws Exception {
        parse("[$([SX4](=[OX1])(=[OX1])([!O])[NX3]),$([SX4+2]([OX1-])([OX1-])([!O])[NX3])]");
    }

    public void testPattern132() throws Exception {
        parse("[$([#16X3]=[OX1]),$([#16X3+][OX1-])]");
    }

    public void testPattern133() throws Exception {
        parse("[$([#16X3](=[OX1])([#6])[#6]),$([#16X3+]([OX1-])([#6])[#6])]");
    }

    public void testPattern134() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([OX2H,OX1H0-])[OX2][#6]),$([#16X4+2]([OX1-])([OX1-])([OX2H,OX1H0-])[OX2][#6])]");
    }

    public void testPattern135() throws Exception {
        parse("[$([SX4](=O)(=O)(O)O),$([SX4+2]([O-])([O-])(O)O)]");
    }

    public void testPattern136() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6]),$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6])]");
    }

    public void testPattern137() throws Exception {
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2][#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2][#6])]");
    }

    public void testPattern138() throws Exception {
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2H,OX1H0-]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2H,OX1H0-])]");
    }

    public void testPattern139() throws Exception {
        parse("[#16X2][OX2H,OX1H0-]");
    }

    public void testPattern140() throws Exception {
        parse("[#16X2][OX2H0]");
    }

    public void testPattern141() throws Exception {
        parse("[#6][F,Cl,Br,I]");
    }

    public void testPattern142() throws Exception {
        parse("[F,Cl,Br,I]");
    }

    public void testPattern143() throws Exception {
        parse("[F,Cl,Br,I].[F,Cl,Br,I].[F,Cl,Br,I]");
    }

    public void testPattern144() throws Exception {
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    public void testPattern145() throws Exception {
        parse("[$([#6X4@](*)(*)(*)*),$([#6X4@H](*)(*)*)]");
    }

    public void testPattern146() throws Exception {
        parse("[$([cX2+](:*):*)]");
    }

    public void testPattern147() throws Exception {
        parse("[$([cX3](:*):*),$([cX2+](:*):*)]");
    }

    public void testPattern148() throws Exception {
        parse("[$([cX3](:*):*),$([cX2+](:*):*),$([CX3]=*),$([CX2+]=*)]");
    }

    public void testPattern149() throws Exception {
        parse("[$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)]");
    }

    public void testPattern150() throws Exception {
        parse("[$([#1X1][$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)])]");
    }

    public void testPattern151() throws Exception {
        parse("[$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)]");
    }

    public void testPattern152() throws Exception {
        parse("[$([#1X1][$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)])]");
    }

    public void testPattern153() throws Exception {
        parse("[$([$([NX3]=O),$([NX3+][O-])])]");
    }

    public void testPattern154() throws Exception {
        parse("[$([$([NX4]=O),$([NX4+][O-])])]");
    }

    public void testPattern155() throws Exception {
        parse("[$([$([NX4]=O),$([NX4+][O-,#0])])]");
    }

    public void testPattern156() throws Exception {
        parse("[$([NX4+]),$([NX4]=*)]");
    }

    public void testPattern157() throws Exception {
        parse("[$([SX3]=N)]");
    }

    public void testPattern158() throws Exception {
        parse("[$([SX1]=[#6])]");
    }

    public void testPattern159() throws Exception {
        parse("[$([NX1]#*)]");
    }

    public void testPattern160() throws Exception {
        parse("[$([OX2])]");
    }

    public void testPattern161() throws Exception {
        parse("[R0;D2][R0;D2][R0;D2][R0;D2]");
    }

    public void testPattern162() throws Exception {
        parse("[R0;D2]~[R0;D2]~[R0;D2]~[R0;D2]");
    }

    public void testPattern163() throws Exception {
        parse("[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]");
    }

    public void testPattern164() throws Exception {
        parse("[!$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }

    public void testPattern165() throws Exception {
        parse("[$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }

    public void testPattern166() throws Exception {
        parse("[$([SX1]~P)]");
    }

    public void testPattern167() throws Exception {
        parse("[$([NX3]C=N)]");
    }

    public void testPattern168() throws Exception {
        parse("[$([NX3]N=C)]");
    }

    public void testPattern169() throws Exception {
        parse("[$([NX3]N=N)]");
    }

    public void testPattern170() throws Exception {
        parse("[$([OX2]C=N)]");
    }

    public void testPattern171() throws Exception {
        parse("[!$(*#*)&!D1]-!@[!$(*#*)&!D1]");
    }

    public void testPattern172() throws Exception {
        parse("[$([*R2]([*R])([*R])([*R]))].[$([*R2]([*R])([*R])([*R]))]");
    }

    public void testPattern173() throws Exception {
        parse("*-!:aa-!:*");
    }

    public void testPattern174() throws Exception {
        parse("*-!:aaa-!:*");
    }

    public void testPattern175() throws Exception {
        parse("*-!:aaaa-!:*");
    }

    public void testPattern176() throws Exception {
        parse("*-!@*");
    }

    public void testPattern177() throws Exception { // CIS or TRANS double or aromatic bond in a ring
        parse("*/,\\[R]=,:;@[R]/,\\*");
    }

    public void testPattern178() throws Exception { // Fused benzene rings
        parse("c12ccccc1cccc2");
    }

    public void testPattern179() throws Exception {
        parse("[r;!r3;!r4;!r5;!r6;!r7]");
    }

    public void testPattern180() throws Exception {
        parse("[sX2r5]");
    }

    public void testPattern181() throws Exception {
        parse("[oX2r5]");
    }

    public void testPattern182() throws Exception { // Unfused benzene ring
        parse("[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }

    public void testPattern183() throws Exception { // Multiple non-fused benzene rings
        parse("[cR1]1[cR1][cR1][cR1][cR1][cR1]1.[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }

    public void testPattern184() throws Exception { // Generic amino acid: low specificity.
        parse("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
    }

    public void testPattern185() throws Exception { //Template for 20 standard a.a.s
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N])," +
                "$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX" +
                "4H2][CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    public void testPattern186() throws Exception { // Proline
        parse("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    public void testPattern187() throws Exception { // Glycine
        parse("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    public void testPattern188() throws Exception {  // Alanine
        parse("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([CH3X4])[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    public void testPattern189() throws Exception { //18_standard_aa_side_chains.
        parse("([$([CH3X4]),$([CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3])," +
                "$([CH2X4][CX3](=[OX1])[NX3H2]),$([CH2X4][CX3](=[OX1])[OH0-,OH])," +
                "$([CH2X4][SX2H,SX1H0-]),$([CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH])," +
                "$([CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:" +
                "[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1)," +
                "$([CHX4]([CH3X4])[CH2X4][CH3X4]),$([CH2X4][CHX4]([CH3X4])[CH3X4])," +
                "$([CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]),$([CH2X4][CH2X4][SX2][CH3X4])," +
                "$([CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1),$([CH2X4][OX2H])," +
                "$([CHX4]([CH3X4])[OX2H]),$([CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12)," +
                "$([CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1),$([CHX4]([CH3X4])[CH3X4])])");
    }

    public void testPattern190() throws Exception { // N in Any_standard_amino_acid.
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3]" +
                "(=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3]" +
                "(=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([$([CH3X4])," +
                "$([CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]),$" +
                "([CH2X4][CX3](=[OX1])[NX3H2]),$([CH2X4][CX3](=[OX1])[OH0-,OH])," +
                "$([CH2X4][SX2H,SX1H0-]),$([CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH])," +
                "$([CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:" +
                "[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1)," +
                "$([CHX4]([CH3X4])[CH2X4][CH3X4]),$([CH2X4][CHX4]([CH3X4])[CH3X4])," +
                "$([CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]),$([CH2X4][CH2X4][SX2][CH3X4])," +
                "$([CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1),$([CH2X4][OX2H])," +
                "$([CHX4]([CH3X4])[OX2H]),$([CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12)," +
                "$([CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1)," +
                "$([CHX4]([CH3X4])[CH3X4])])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    public void testPattern191() throws Exception { // Non-standard amino acid.
        parse("[$([NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]);!$([$([$([NX3H,NX4H2+])," +
                "$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N])," +
                "$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])," +
                "$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([$([CH3X4]),$([CH2X4][CH2X4][CH2X4][NHX3][CH0X3]" +
                "(=[NH2X3+,NHX2+0])[NH2X3]),$([CH2X4][CX3](=[OX1])[NX3H2]),$([CH2X4][CX3](=[OX1])[OH0-,OH])," +
                "$([CH2X4][SX2H,SX1H0-]),$([CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]),$([CH2X4][#6X3]1:" +
                "[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:" +
                "[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H])," +
                "$([#7X3H])]:[#6X3H]1),$([CHX4]([CH3X4])[CH2X4][CH3X4]),$([CH2X4][CHX4]([CH3X4])[CH3X4])," +
                "$([CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]),$([CH2X4][CH2X4][SX2][CH3X4])," +
                "$([CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1),$([CH2X4][OX2H]),$([CHX4]([CH3X4])[OX2H])," +
                "$([CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12)," +
                "$([CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1)," +
                "$([CHX4]([CH3X4])[CH3X4])])[CX3](=[OX1])[OX2H,OX1-,N])])]");
    }

    public void testPattern192() throws Exception { //Azide group
        parse("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }

    public void testPattern193() throws Exception { // Azide ion
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    public void testPattern194() throws Exception { //Azide or azide ion
        parse("[$([$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]),$([$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])])]");
    }

    public void testPattern195() throws Exception { // Sulfide
        parse("[#16X2H0]");
    }

    public void testPattern196() throws Exception { // Mono-sulfide
        parse("[#16X2H0][!#16]");
    }

    public void testPattern197() throws Exception { // Di-sulfide
        parse("[#16X2H0][#16X2H0]");
    }

    public void testPattern198() throws Exception { // Two sulfides
        parse("[#16X2H0][!#16].[#16X2H0][!#16]");
    }

    public void testPattern199() throws Exception { // Acid/conj-base
        parse("[OX2H,OX1H0-]");
    }

    public void testPattern200() throws Exception { // Non-acid Oxygen
        parse("[OX2H0]");
    }

    public void testPattern201() throws Exception { // Acid/base
        parse("[H1,H0-]");
    }

    public void testPattern202() throws Exception {
        parse("([Cl!$(Cl~c)].[c!$(c~Cl)])");
    }

    public void testPattern203() throws Exception {
        parse("([Cl]).([c])");
    }

    public void testPattern204() throws Exception {
        parse("([Cl].[c])");
    }

    public void testPattern205() throws Exception {
        parse("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }

    public void testPattern206() throws Exception {
        parse("[#0]");
    }

    public void testPattern207() throws Exception {
        parse("[*!H0,#1]");
    }

    public void testPattern208() throws Exception {
        parse("[#6!H0,#1]");
    }

    public void testPattern209() throws Exception {
        parse("[H,#1]");
    }

    public void testPattern210() throws Exception {
        parse("[!H0;F,Cl,Br,I,N+,$([OH]-*=[!#6]),+]");
    }

    public void testPattern211() throws Exception {
        parse("[CX3](=O)[OX2H1]");
    }

    public void testPattern212() throws Exception {
        parse("[CX3](=O)[OX1H0-,OX2H1]");
    }

    public void testPattern213() throws Exception {
        parse("[$([OH]-*=[!#6])]");
    }

    public void testPattern214() throws Exception { // Phosphoric_Acid
        parse("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX" +
                "2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }

    public void testPattern215() throws Exception { // Sulfonic Acid. High specificity.
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }

    public void testPattern216() throws Exception { // Acyl Halide
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    public void testPattern217() throws Exception {
        parse("[NX2-]");
    }

    public void testPattern218() throws Exception {
        parse("[OX2H+]=*");
    }

    public void testPattern219() throws Exception {
        parse("[OX3H2+]");
    }

    public void testPattern220() throws Exception {
        parse("[#6+]");
    }

    public void testPattern221() throws Exception {
        parse("[$([cX2+](:*):*)]");
    }

    public void testPattern222() throws Exception {
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    public void testPattern223() throws Exception {
        parse("[+1]~*~*~[-1]");
    }

    public void testPattern224() throws Exception {
        parse("[$([!-0!-1!-2!-3!-4]~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4])]");
    }

    public void testPattern225() throws Exception {
        parse("([!-0!-1!-2!-3!-4].[!+0!+1!+2!+3!+4])");
    }

    public void testPattern226() throws Exception { // Hydrogen-bond acceptor, Only hits carbonyl and nitroso
        parse("[#6,#7;R0]=[#8]");
    }

    public void testPattern227() throws Exception { // Hydrogen-bond acceptor
        parse("[!$([#6,F,Cl,Br,I,o,s,nX3,#7v5,#15v5,#16v4,#16v6,*+1,*+2,*+3])]");
    }

    public void testPattern228() throws Exception {
        parse("[!$([#6,H0,-,-2,-3])]");
    }

    public void testPattern229() throws Exception {
        parse("[!H0;#7,#8,#9]");
    }

    public void testPattern230() throws Exception {
        parse("[O,N;!H0]-*~*-*=[$([C,N;R0]=O)]");
    }

    public void testPattern231() throws Exception {
        parse("[#6;X3v3+0]");
    }

    public void testPattern232() throws Exception {
        parse("[#7;X2v4+0]");
    }

    public void testPattern233() throws Exception { // Amino Acid
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][" +
                "CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    public void testPattern234() throws Exception {
        parse("[#6][CX3](=O)[$([OX2H0]([#6])[#6]),$([#7])]");
    }

    public void testPattern235() throws Exception {
        parse("[#8]=[C,N]-aaa[F,Cl,Br,I]");
    }

    public void testPattern236() throws Exception {
        parse("[O,N;!H0;R0]");
    }

    public void testPattern237() throws Exception {
        parse("[#8]=[C,N]");
    }

    public void testPattern238() throws Exception { // PCB
        parse("[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]-[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]");
    }

    public void testPattern239() throws Exception { // Imidazolium Nitrogen
        parse("[nX3r5+]:c:n");
    }

    public void testPattern240() throws Exception { // 1-methyl-2-hydroxy benzene with either a Cl or H at the 5 position.
        parse("Cc1:c(O):c:c:[$(cCl),$([cH])]:c1");
    }

    public void testPattern241() throws Exception { // Nonstandard atom groups.
        parse("[!#1;!#2;!#3;!#5;!#6;!#7;!#8;!#9;!#11;!#12;!#15;!#16;!#17;!#19;!#20;!#35;!#53]");
    }

    public void testRing() throws Exception {
        parse("[$([C;#12]=1CCCCC1)]");
    }

    public void testHydrogen() throws Exception {
        parse("[H]");
    }

    /**
     * From http://www.daylight.com/dayhtml_tutorials/languages/smarts/index.html
     *
     * @param dumpAtomTypes TODO
     */
    public int match(String smarts, String smiles, boolean dumpAtomTypes) throws Exception {
        SMARTSQueryTool sqt = new SMARTSQueryTool(smarts);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(smiles);
        if (dumpAtomTypes) {
            Iterator<IAtom> atoms = atomContainer.atoms().iterator();
            while (atoms.hasNext()) {
                IAtom nextAtom = atoms.next();
                System.out.println("atom: " + nextAtom.getSymbol() + " -> " +
                        nextAtom.getAtomTypeName() + " A:" +
                        nextAtom.getFlag(CDKConstants.ISAROMATIC));
            }
        }
        boolean status = sqt.matches(atomContainer);
        if (status) {
            int nmatch = sqt.countMatches();
            return nmatch;
        } else {
            return 0;
        }
    }

    public void testPropertyCharge1() throws Exception {
        assertEquals(0, match("[+1]", "[OH-].[Mg+2]", false));
    }

    public void testPropertyCharge2() throws Exception {
        assertEquals(1, match("[+1]", "COCC(O)Cn1ccnc1[N+](=O)[O-]", false));
    }

    public void testPropertyCharge3() throws Exception {
        assertEquals(1, match("[+1]", "[NH4+]", false));
    }

    public void testPropertyCharge4() throws Exception {
        assertEquals(0, match("[+1]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12", false));
    }

    public void testPropertyCharge5() throws Exception {
        assertEquals(2, match("[+1]", "[Cl-].[Cl-].NC(=O)c2cc[n+](COC[n+]1ccccc1C=NO)cc2", false));
    }

    public void testPropertyAromatic1() throws Exception {
        int m = match("[a]", "c1cc(C)c(N)cc1", true);
        assertEquals(6, m);
    }

    public void testPropertyAromatic2() throws Exception {
        int m = match("[a]", "c1c(C)c(N)cnc1", false);
        assertEquals(6, m);
    }

    public void testPropertyAromatic3() throws Exception {
        int m = match("[a]", "c1(C)c(N)cco1", false);
        assertEquals(5, m);
    }

    public void testPropertyAromatic4() throws Exception {
        int m = match("[a]", "c1c(C)c(N)c[nH]1", false);
        assertEquals(5, m);
    }

    public void testPropertyAromatic5() throws Exception {
        int m = match("[a]", "O=n1ccccc1", false);
        assertEquals(6, m);
    }

    public void testPropertyAromatic6() throws Exception {
        int m = match("[a]", "[O-][n+]1ccccc1", false);
        assertEquals(6, m);
    }

    public void testPropertyAromatic7() throws Exception {
        int m = match("[a]", "c1ncccc1C1CCCN1C", false);
        assertEquals(6, m);
    }

    public void testPropertyAromatic8() throws Exception {
        int m = match("[a]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC", false);
        assertEquals(6, m);
    }

    public void testPropertyAliphatic1() throws Exception {
        int m = match("[A]", "c1cc(C)c(N)cc1", false);
        assertEquals(2, m);
    }

    public void testPropertyAliphatic2() throws Exception {
        int m = match("[A]", "CCO", false);
        assertEquals(3, m);
    }

    public void testPropertyAliphatic3() throws Exception {
        int m = match("[A]", "C=CC=CC=C", false);
        assertEquals(6, m);
    }

    public void testPropertyAliphatic4() throws Exception {
        int m = match("[A]", "CC(C)(C)C", false);
        assertEquals(5, m);
    }

    public void testPropertyAliphatic5() throws Exception {
        int m = match("[A]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1", false);
        assertEquals(15, m);
    }

    public void testPropertyAliphatic6() throws Exception {
        int m = match("[A]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76", false);
        assertEquals(19, m);
    }

    //TODO: atomicnumber always 0 in smiles parsed atomcontainer
    public void testPropertyAtomicNumber1() throws Exception {
        int m = match("[#6]", "c1cc(C)c(N)cc1", false);
        assertEquals(7, m);
    }

    public void testPropertyAtomicNumber2() throws Exception {
        int m = match("[#6]", "CCO", false);
        assertEquals(2, m);
    }

    public void testPropertyAtomicNumber3() throws Exception {
        int m = match("[#6]", "C=CC=CC=C-O", false);
        assertEquals(6, m);
    }

    public void testPropertyAtomicNumber4() throws Exception {
        int m = match("[#6]", "CC(C)(C)C", false);
        assertEquals(5, m);
    }

    public void testPropertyAtomicNumber5() throws Exception {
        int m = match("[#6]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34", false);
        assertEquals(20, m);
    }

    public void testPropertyAtomicNumber6() throws Exception {
        int m = match("[#6]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5", false);
        assertEquals(17, m);
    }

    public void testPropertyAtomicNumber7() throws Exception {
        int m = match("[#6]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5", false);
        assertEquals(21, m);
    }

    public void testPropertyR1() throws Exception {
        int m = match("[R2]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76", false);
        assertEquals(7, m);
    }

    public void testPropertyR2() throws Exception {
        int m = match("[R2]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34", false);
        assertEquals(2, m);
    }

    public void testPropertyR3() throws Exception {
        int m = match("[R2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5", false);
        assertEquals(4, m);
    }

    public void testPropertyR4() throws Exception {
        int m = match("[R2]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5", false);
        assertEquals(4, m);
    }

    public void testPropertyR5() throws Exception {
        int m = match("[R2]", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1", false);
        assertEquals(0, m);
    }

    public void testPropertyr1() throws Exception {
        int m = match("[r5]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76", false);
        assertEquals(9, m);
    }

    public void testPropertyr2() throws Exception {
        int m = match("[r5]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34", false);
        assertEquals(0, m);
    }

    public void testPropertyr3() throws Exception {
        int m = match("[r5]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5", false);
        assertEquals(5, m);
    }

    public void testPropertyr4() throws Exception {
        int m = match("[r5]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5", false);
        assertEquals(5, m);
    }

    public void testPropertyr5() throws Exception {
        int m = match("[r5]", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1", false);
        assertEquals(5, m);
    }

    public void testPropertyValence1() throws Exception {
        int m = match("[v4]", "C", false);
        assertEquals(1, m);
    }

    public void testPropertyValence2() throws Exception {
        int m = match("[v4]", "CCO", false);
        assertEquals(2, m);
    }

    public void testPropertyValence3() throws Exception {
        int m = match("[v4]", "[NH4+]", false);
        assertEquals(1, m);
    }

    public void testPropertyValence4() throws Exception {
        int m = match("[v4]", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O", false);
        assertEquals(16, m);
    }

    public void testPropertyValence5() throws Exception {
        int m = match("[v4]", "[Cl-].[Cl-].NC(=O)c2cc[n+](COC[n+]1ccccc1C=NO)cc2", false);
        assertEquals(16, m);
    }

    public void testPropertyX1() throws Exception {
        int m = match("[X2]", "CCO", false);
        assertEquals(1, m);
    }

    public void testPropertyX2() throws Exception {
        int m = match("[X2]", "O", false);
        assertEquals(1, m);
    }

    public void testPropertyX3() throws Exception {
        int m = match("[X2]", "CCC(=O)CC", false);
        assertEquals(0, m);
    }

    public void testPropertyX4() throws Exception {
        int m = match("[X2]", "FC(Cl)=C=C(Cl)F", false);
        assertEquals(1, m);
    }

    public void testPropertyX5() throws Exception {
        int m = match("[X2]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34", false);
        assertEquals(3, m);
    }

    public void testPropertyX6() throws Exception {
        int m = match("[X2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5", false);
        assertEquals(3, m);
    }

    public void testPropertyHAtom1() throws Exception {
        int m = match("[H]", "[H+].[Cl-]", false);
        assertEquals(1, m);
    }

    public void testPropertyHAtom2() throws Exception {
        int m = match("[H]", "[2H]", false);
        assertEquals(1, m);
    }

    public void testPropertyHAtom3() throws Exception {
        int m = match("[H]", "[H][H]", false);
        assertEquals(2, m);
    }

    public void testPropertyHAtom4() throws Exception {
        int m = match("[H]", "[CH4]", false);
        assertEquals(0, m);
    }

    public void testPropertyHAtom5() throws Exception {
        int m = match("[H]", "[H]C([H])([H])[H]", false);
        assertEquals(0, m);
    }

    public void testPropertyHTotal1() throws Exception {
        int m = match("[H1]", "CCO", false);
        assertEquals(1, m);
    }

    public void testPropertyHTotal2() throws Exception {
        int m = match("[H1]", "[2H]C#C", false);
        assertEquals(2, m);
    }

    public void testPropertyHTotal3() throws Exception {
        int m = match("[H1]", "[H]C(C)(C)C", false);
        assertEquals(1, m);
    }

    public void testPropertyHTotal4() throws Exception {
        int m = match("[H1]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34", false);
        assertEquals(11, m);
    }

    public void testPropertyHTotal5() throws Exception {
        int m = match("[H1]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5", false);
        assertEquals(10, m);
    }

    public void testPropertyHTotal6() throws Exception {
        int m = match("[H1]", "[H][H]", false);
        assertEquals(2, m);
    }

    public void testPropertyAnyAtom1() throws Exception {
        int m = match("[*]", "C", false);
        assertEquals(1, m);
    }

    public void testPropertyAnyAtom2() throws Exception {
        int m = match("[*]", "[2H]C", false);
        assertEquals(2, m);
    }

    public void testPropertyAnyAtom3() throws Exception {
        int m = match("[*]", "[H][H]", false);
        assertEquals(2, m);
    }

    public void testPropertyAnyAtom4() throws Exception {
        int m = match("[*]", "[1H]C([1H])([1H])[1H]", false);
        assertEquals(5, m);
    }

    public void testBondSingle1() throws Exception {
        int m = match("CC", "C=C", false);
        assertEquals(0, m);
    }

    public void testBondSingle2() throws Exception {
        int m = match("CC", "C#C", false);
        assertEquals(0, m);
    }

    public void testBondSingle3() throws Exception {
        int m = match("CC", "CCO", false);
        assertEquals(2, m);
    }

    public void testBondSingle4() throws Exception {
        int m = match("CC", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1", false);
        assertEquals(28, m);
    }

    public void testBondSingle5() throws Exception {
        int m = match("CC", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O", false);
        assertEquals(14, m);
    }

    public void testBondAny1() throws Exception {
        int m = match("C~C", "C=C", false);
        assertEquals(2, m);
    }

    public void testBondAny2() throws Exception {
        int m = match("C~C", "C#C", false);
        assertEquals(2, m);
    }

    public void testBondAny3() throws Exception {
        int m = match("C~C", "CCO", false);
        assertEquals(2, m);
    }

    public void testBondAny4() throws Exception {
        int m = match("C~C", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1", false);
        assertEquals(38, m);
    }

    public void testBondAny5() throws Exception {
        int m = match("[C,c]~[C,c]", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O", false);
        assertEquals(28, m);
    }

    public void testBondRing1() throws Exception {
        int m = match("C@C", "C=C", false);
        assertEquals(0, m);
    }

    public void testBondRing2() throws Exception {
        int m = match("C@C", "C#C", false);
        assertEquals(0, m);
    }

    public void testBondRing3() throws Exception {
        int m = match("C@C", "C1CCCCC1", false);
        assertEquals(12, m);
    }

    public void testBondRing4() throws Exception {
        int m = match("[C,c]@[C,c]", "c1ccccc1Cc1ccccc1", false);
        assertEquals(24, m);
    }

    public void testBondRing5() throws Exception {
        int m = match("[C,c]@[C,c]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1", false);
        assertEquals(30, m);
    }

    public void testBondRing6() throws Exception {
        int m = match("[C,c]@[C,c]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76", false);
        assertEquals(44, m);
    }

    public void testBondStereo1() throws Exception { //TODO: Stereo bond not implemented in smiles parser?
        int m = match("F/?C=C/Cl", "F/C=C/Cl", false);
        assertEquals(1, m);
    }

    public void testBondStereo2() throws Exception {
        int m = match("F/?C=C/Cl", "FC=C/Cl", false);
        assertEquals(1, m);
    }

    public void testBondStereo3() throws Exception {
        int m = match("F/?C=C/Cl", "FC=CCl", false);
        assertEquals(1, m);
    }

    public void testBondStereo4() throws Exception {
        int m = match("F/?C=C/Cl", "F\\C=C/Cl", false);
        assertEquals(0, m);
    }

    public void testLogicalNot1() throws Exception {
        int m = match("[!c]", "c1cc(C)c(N)cc1", false);
        assertEquals(2, m);
    }

    public void testLogicalNot2() throws Exception {
        int m = match("[!c]", "c1c(C)c(N)cnc1", false);
        assertEquals(3, m);
    }

    public void testLogicalNot3() throws Exception {
        int m = match("[!c]", "c1(C)c(N)cco1", false);
        assertEquals(3, m);
    }

    public void testLogicalNot4() throws Exception {
        int m = match("[!c]", "c1c(C)c(N)c[nH]1", true);
        assertEquals(3, m);
    }

    public void testLogicalNot5() throws Exception {
        int m = match("[!C]", "ON1CCCCC1", false);
        assertEquals(2, m);
    }

    public void testLogicalNot6() throws Exception {
        int m = match("[!c]", "[O-][n+]1ccccc1", false);
        assertEquals(2, m);
    }

    public void testLogicalNot7() throws Exception {
        int m = match("[!c]", "c1ncccc1C1CCCN1C", false);
        assertEquals(7, m);
    }

    public void testLogicalNot8() throws Exception {
        int m = match("[!c]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC", false);
        assertEquals(16, m);
    }

    public void testLogicalNot9() throws Exception {
        int m = match("[!c]", "c1c(C)c(N)cn(C)1", false);
        assertEquals(4, m);
    }

    public void testLogicalOr1() throws Exception {
        int m = match("[N,O,o]", "c1cc(C)c(N)cc1", false);
        assertEquals(1, m);
    }

    public void testLogicalOr2() throws Exception {
        int m = match("[N,O,o]", "c1c(C)c(N)cnc1", false);
        assertEquals(1, m);
    }

    public void testLogicalOr3() throws Exception {
        int m = match("[N,O,o]", "c1(C)c(N)cco1", false);
        assertEquals(2, m);
    }

    public void testLogicalOr4() throws Exception {
        int m = match("[N,O,o]", "c1c(C)c(N)c[nH]1", false);
        assertEquals(1, m);
    }

    public void testLogicalOr5() throws Exception {
        int m = match("[N,O,o]", "NCc1ccccc1CO", false);
        assertEquals(2, m);
    }

    public void testLogicalOr6() throws Exception {
        int m = match("[N,O,o]", "[O-][n+]1ccccc1", false);
        assertEquals(1, m);
    }

    public void testLogicalOr7() throws Exception {
        int m = match("[N,O,o]", "c1ncccc1C1CCCN1C", false);
        assertEquals(1, m);
    }

    public void testLogicalOr8() throws Exception {
        int m = match("[N,O,o]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC", false);
        assertEquals(5, m);
    }

    public void testLogicalOrHighAnd1() throws Exception {
        int m = match("[N,#6&+1,+0]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1", false);
        assertEquals(24, m);
    }

    public void testLogicalOrHighAnd2() throws Exception {
        int m = match("[N,#6&+1,+0]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76", false);
        assertEquals(25, m);
    }

    public void testLogicalOrHighAnd3() throws Exception {
        int m = match("[N,#6&+1,+0]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34", false);
        assertEquals(24, m);
    }

    public void testLogicalOrHighAnd4() throws Exception {
        int m = match("[N,#6&+1,+0]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5", false);
        assertEquals(21, m);
    }

    public void testLogicalOrHighAnd5() throws Exception {
        int m = match("[N,#6&+1,+0]", "N1N([Hg-][O+]=C1N=Nc2ccccc2)c3ccccc3", false);
        assertEquals(17, m);
    }

    public void testLogicalOrHighAnd7() throws Exception {
        int m = match("[N,#6&+1,+0]", "[Cl-].Clc1ccc([I+]c2cccs2)cc1", false);
        assertEquals(12, m);
    }

    public void testLogicalOrLowAnd1() throws Exception {
        int m = match("[#7,C;+0,+1]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1", false);
        assertEquals(15, m);
    }

    public void testLogicalOrLowAnd2() throws Exception {
        int m = match("[#7,C;+0,+1]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76", false);
        assertEquals(17, m);
    }

    public void testLogicalOrLowAnd3() throws Exception {
        int m = match("[#7,C;+0,+1]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34", false);
        assertEquals(13, m);
    }

    public void testLogicalOrLowAnd4() throws Exception {
        int m = match("[#7,C;+0,+1]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5", false);
        assertEquals(12, m);
    }

    public void testLogicalOrLowAnd5() throws Exception {
        int m = match("[#7,C;+0,+1]", "N1N([Hg-][O+]=C1N=Nc2ccccc2)c3ccccc3", false);
        assertEquals(5, m);
    }
    /*
    public void testLogicalOrLowAnd6() throws Exception { //TODO: this takes very long
    	int m = match("[#7,C;+0,+1]", "[Na+].[Na+].[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24");
    	assertEquals(1, m);    	
    }
    public void testLogicalOrLowAnd7() throws Exception {
    	int m = match("[#7,C;+0,+1]", "[Cl-].Clc1ccc([I+]c2cccs2)cc1");
    	assertEquals(0, m);    	
    }
    */

    public void testRing1() throws Exception {
        int m = match("C1CCCCC1", "C1CCCCC1CCCC", false);
        assertEquals(12, m);
    }

    public void testRing2() throws Exception {
        int m = match("C1CCCCC1", "C1CCCCC1C1CCCCC1", false);
        assertEquals(24, m);
    }

    public void testRing3() throws Exception {
        int m = match("C1CCCCC1", "C1CCCC12CCCCC2", false);
        assertEquals(12, m);
    }

    public void testRing4() throws Exception {
        int m = match("C1CCCCC1", "c1ccccc1O", false);
        assertEquals(0, m);
    }

    public void testRing5() throws Exception {
        int m = match("C1CCCCC1", "c1ccccc1CCCCCC", false);
        assertEquals(0, m);
    }

    public void testRing6() throws Exception {
        int m = match("C1CCCCC1", "CCCCCC", false);
        assertEquals(0, m);
    }

    public void testRing7() throws Exception {
        int m = match("c1ccccc1", "c1ccccc1", false);
        assertEquals(12, m);
    }
}
