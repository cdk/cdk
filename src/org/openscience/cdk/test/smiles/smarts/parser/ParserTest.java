/* $Revision: 8245 $ $Author: egonw $ $Date: 2007-04-22 17:20:57 -0400 (Sun, 22 Apr 2007) $
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
package org.openscience.cdk.test.smiles.smarts.parser;

import java.util.Calendar;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * JUnit test routines for the SMARTS parser.
 *
 * @cdk.module  test-smarts
 * @cdk.require ant1.6
 *
 * @author      Egon Willighagen
 */
public class ParserTest extends CDKTestCase {
    
    private int failed = 0;
    private int total = 0;
 
    public ParserTest() {}

    public ParserTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(ParserTest.class);
    }
    
    public void parse(String smarts) {
        boolean isFailed = false;
        total += 1;
        try {
            System.out.print(smarts);
            SMARTSParser.parse(smarts);
        } catch (Throwable ex) {
            System.out.println(": Failed " + ex.getMessage());
            failed += 1;
            isFailed = true;
        }
        if (!isFailed) {
            System.out.println(": Successful");
        }
        
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

    public void testSingleBond() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("C-C");
    	assertEquals(2, container.getAtomCount());
    	assertEquals(1, container.getBondCount());
    	org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
    	assertTrue(bond instanceof OrderQueryBond);
    	OrderQueryBond qBond = (OrderQueryBond)bond;
    	assertEquals(1.0, qBond.getOrder(), 0.001);
    }

    public void testDoubleBond() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("C=C");
    	assertEquals(2, container.getAtomCount());
    	assertEquals(1, container.getBondCount());
    	org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
    	assertTrue(bond instanceof OrderQueryBond);
    	OrderQueryBond qBond = (OrderQueryBond)bond;
    	assertEquals(2.0, qBond.getOrder(), 0.001);
    }

    public void testTripleBond() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("C#C");
    	assertEquals(2, container.getAtomCount());
    	assertEquals(1, container.getBondCount());
    	org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
    	assertTrue(bond instanceof OrderQueryBond);
    	OrderQueryBond qBond = (OrderQueryBond)bond;
    	assertEquals(3.0, qBond.getOrder(), 0.001);
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
    
    public void testBenchmark() {
        benchmark();
    }

    /**
     * From http://wiki.cubic.uni-koeln.de/cdkwiki/doku.php?id=parsertest.java.
     */
    public void benchmark() {
        long start = Calendar.getInstance().getTimeInMillis();
        parse("[CX4]");
        parse("[$([CX2](=C)=C)]");
        parse("[$([CX3]=[CX3])]");
        parse("[$([CX2]#C)]");
        parse("[CX3]=[OX1]");
        parse("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]");
        parse("[CX3](=[OX1])C");
        parse("[OX1]=CN");
        parse("[CX3](=[OX1])O");
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
        parse("[CX3H1](=O)[#6]");
        parse("[CX3](=[OX1])[OX2][CX3](=[OX1])");
        parse("[NX3][CX3](=[OX1])[#6]");
        parse("[NX3][CX3]=[NX3+]");
        parse("[NX3,NX4+][CX3](=[OX1])[OX2,OX1-]");
        parse("[NX3][CX3](=[OX1])[OX2H0]");
        parse("[NX3,NX4+][CX3](=[OX1])[OX2H,OX1-]");
        parse("[CX3](=O)[O-]");
        parse("[CX3](=[OX1])(O)O");
        parse("[CX3](=[OX1])([OX2])[OX2H,OX1H0-1]");
        parse("[CX3](=O)[OX2H1]");
        parse("[CX3](=O)[OX1H0-,OX2H1]");
        parse("[NX3][CX2]#[NX1]");
        parse("[#6][CX3](=O)[OX2H0][#6]");
        parse("[#6][CX3](=O)[#6]");
        parse("[OD2]([#6])[#6]");
        parse("[H]");
        parse("[!#1]");
        parse("[H+]");
        parse("[+H]");
        parse("[NX3;H2,H1;!$(NC=O)]");
        parse("[NX3][CX3]=[CX3]");
        parse("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
        parse("[NX3][$(C=C),$(cc)]");
        parse("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
        parse("[NX3H2,NH3X4+][CX4H]([*])[CX3](=[OX1])[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-]");
        parse("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N]");
        parse("[CH3X4]");
        parse("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
        parse("[CH2X4][CX3](=[OX1])[NX3H2]");
        parse("[CH2X4][CX3](=[OX1])[OH0-,OH]");
        parse("[CH2X4][SX2H,SX1H0-]");
        parse("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
        parse("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
        parse("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:");
        parse("[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
        parse("[CHX4]([CH3X4])[CH2X4][CH3X4]");
        parse("[CH2X4][CHX4]([CH3X4])[CH3X4]");
        parse("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
        parse("[CH2X4][CH2X4][SX2][CH3X4]");
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
        parse("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
        parse("[CH2X4][OX2H]");
        parse("[NX3][CX3]=[SX1]");
        parse("[CHX4]([CH3X4])[OX2H]");
        parse("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
        parse("[CHX4]([CH3X4])[CH3X4]");
        parse("[CH3X4]");
        parse("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
        parse("[CH2X4][CX3](=[OX1])[NX3H2]");
        parse("[CH2X4][CX3](=[OX1])[OH0-,OH]");
        parse("[CH2X4][SX2H,SX1H0-]");
        parse("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
        parse("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:");
        parse("[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
        parse("[CHX4]([CH3X4])[CH2X4][CH3X4]");
        parse("[CH2X4][CHX4]([CH3X4])[CH3X4]");
        parse("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
        parse("[CH2X4][CH2X4][SX2][CH3X4]");
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
        parse("[CH2X4][OX2H]");
        parse("[CHX4]([CH3X4])[OX2H]");
        parse("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
        parse("[CHX4]([CH3X4])[CH3X4]");
        parse("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
        parse("[#7]");
        parse("[NX2]=N");
        parse("[NX2]=[NX2]");
        parse("[$([NX2]=[NX3+]([O-])[#6]),$([NX2]=[NX3+0](=[O])[#6])]");
        parse("[$([#6]=[N+]=[N-]),$([#6-]-[N+]#[N])]");
        parse("[$([nr5]:[nr5,or5,sr5]),$([nr5]:[cr5]:[nr5,or5,sr5])]");
        parse("[NX3][NX3]");
        parse("[NX3][NX2]=[*]");
        parse("[CX3;$([C]([#6])[#6]),$([CH][#6])]=[NX2][#6]");
        parse("[$([CX3]([#6])[#6]),$([CX3H][#6])]=[$([NX2][#6]),$([NX2H])]");
        parse("[NX3+]=[CX3]");
        parse("[CX3](=[OX1])[NX3H][CX3](=[OX1])");
        parse("[CX3](=[OX1])[NX3H0]([#6])[CX3](=[OX1])");
        parse("[CX3](=[OX1])[NX3H0]([NX3H0]([CX3](=[OX1]))[CX3](=[OX1]))[CX3](=[OX1])");
        parse("[$([NX3](=[OX1])(=[OX1])O),$([NX3+]([OX1-])(=[OX1])O)]");
        parse("[$([OX1]=[NX3](=[OX1])[OX1-]),$([OX1]=[NX3+]([OX1-])[OX1-])]");
        parse("[NX1]#[CX2]");
        parse("[CX1-]#[NX2+]");
        parse("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8] Hits both forms.");
        parse("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8].[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
        parse("[NX2]=[OX1]");
        parse("[$([#7+][OX1-]),$([#7v5]=[OX1]);!$([#7](~[O])~[O]);!$([#7]=[#7])]");
        parse("[OX2H]");
        parse("[#6][OX2H]");
        parse("[OX2H][CX3]=[OX1]");
        parse("[OX2H]P");
        parse("[OX2H][#6X3]=[#6]");
        parse("[OX2H][cX3]:[c]");
        parse("[OX2H][$(C=C),$(cc)]");
        parse("[$([OH]-*=[!#6])]");
        parse("[OX2,OX1-][OX2,OX1-]");
        parse("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX 2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
        parse("[$(P(=[OX1])([OX2][#6])([$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)]),$([P+]([OX1-])([OX2][#6])([ $([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)])]");
        parse("[S-][CX3](=S)[#6]");
        parse("[#6X3](=[SX1])([!N])[!N]");
        parse("[SX2]");
        parse("[#16X2H]");
        parse("[#16!H0]");
        parse("[NX3][CX3]=[SX1]");
        parse("[#16X2H0]");
        parse("[#16X2H0][!#16]");
        parse("[#16X2H0][#16X2H0]");
        parse("[#16X2H0][!#16].[#16X2H0][!#16]");
        parse("[$([#16X3](=[OX1])[OX2H0]),$([#16X3+]([OX1-])[OX2H0])]");
        parse("[$([#16X3](=[OX1])[OX2H,OX1H0-]),$([#16X3+]([OX1-])[OX2H,OX1H0-])]");
        parse("[$([#16X4](=[OX1])=[OX1]),$([#16X4+2]([OX1-])[OX1-])]");
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[#6]),$([#16X4+2]([OX1-])([OX1-])([#6])[#6])]");
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H0]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H0])]");
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[#6])]");
        parse("[SX4](C)(C)(=O)=N");
        parse("[$([SX4](=[OX1])(=[OX1])([!O])[NX3]),$([SX4+2]([OX1-])([OX1-])([!O])[NX3])]");
        parse("[$([#16X3]=[OX1]),$([#16X3+][OX1-])]");
        parse("[$([#16X3](=[OX1])([#6])[#6]),$([#16X3+]([OX1-])([#6])[#6])]");
        parse("[$([#16X4](=[OX1])(=[OX1])([OX2H,OX1H0-])[OX2][#6]),$([#16X4+2]([OX1-])([OX1-])([OX2H,OX1H0-])[OX2][#6])]");
        parse("[$([SX4](=O)(=O)(O)O),$([SX4+2]([O-])([O-])(O)O)]");
        parse("[$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6]),$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6])]");
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2][#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2][#6])]");
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2H,OX1H0-]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2H,OX1H0-])]");
        parse("[#16X2][OX2H,OX1H0-]");
        parse("[#16X2][OX2H0]");
        parse("[#6][F,Cl,Br,I]");
        parse("[F,Cl,Br,I]");
        parse("[F,Cl,Br,I].[F,Cl,Br,I].[F,Cl,Br,I]");
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
        parse("[$([#6X4@](*)(*)(*)*),$([#6X4@H](*)(*)*)]");
        parse("[$([cX2+](:*):*)]");
        parse("[$([cX3](:*):*),$([cX2+](:*):*)]");
        parse("[$([cX3](:*):*),$([cX2+](:*):*),$([CX3]=*),$([CX2+]=*)]");
        parse("[$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)]");
        parse("[$([#1X1][$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)])]");
        parse("[$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)]");
        parse("[$([#1X1][$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)])]");
        parse("[$([$([NX3]=O),$([NX3+][O-])])]");
        parse("[$([$([NX4]=O),$([NX4+][O-])])]");
        parse("[$([$([NX4]=O),$([NX4+][O-,#0])])]");
        parse("[$([NX4+]),$([NX4]=*)]");
        parse("[$([SX3]=N)]");
        parse("[$([SX1]=[#6])]");
        parse("[$([NX1]#*)]");
        parse("[$([OX2])]");
        parse("[R0;D2][R0;D2][R0;D2][R0;D2]");
        parse("[R0;D2]~[R0;D2]~[R0;D2]~[R0;D2]");
        parse("[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]");
        parse("[!$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
        parse("[$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
        parse("[$([SX1]~P)]");
        parse("[$([NX3]C=N)]");
        parse("[$([NX3]N=C)]");
        parse("[$([NX3]N=N)]");
        parse("[$([OX2]C=N)]");
        parse("[!$(*#*)&!D1]-!@[!$(*#*)&!D1]");
        parse("[$([*R2]([*R])([*R])([*R]))].[$([*R2]([*R])([*R])([*R]))]");
        parse("*-!:aa-!:*");
        parse("*-!:aaa-!:*");
        parse("*-!:aaaa-!:*");
        parse("*-!@*");
        parse("*/,\\[R]=,:;@[R]/,\\*");
        parse("c12ccccc1cccc2");
        parse("[r;!r3;!r4;!r5;!r6;!r7]");
        parse("[sX2r5]");
        parse("[oX2r5]");
        parse("[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
        parse("[cR1]1[cR1][cR1][cR1][cR1][cR1]1.[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
        parse("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),");
        parse("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
        parse("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
        parse("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N]");
        parse("[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1),");
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3]");
        parse("[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1),");
        parse("[$([NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]);!$([$([$([NX3H,NX4H2+]),");
        parse("[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:");
        parse("[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),");
        parse("[#16X2H0]");
        parse("[#16X2H0][!#16]");
        parse("[#16X2H0][#16X2H0]");
        parse("[#16X2H0][!#16].[#16X2H0][!#16]");
        parse("[OX2H,OX1H0-]");
        parse("[OX2H0]");
        parse("[H1,H0-]");
        parse("([Cl!$(Cl~c)].[c!$(c~Cl)])");
        parse("([Cl]).([c])");
        parse("([Cl].[c])");
        parse("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
        parse("[#0]");
        parse("[*!H0,#1]");
        parse("[#6!H0,#1]");
        parse("[H,#1]");
        parse("[!H0;F,Cl,Br,I,N+,$([OH]-*=[!#6]),+]");
        parse("[CX3](=O)[OX2H1]");
        parse("[CX3](=O)[OX1H0-,OX2H1]");
        parse("[$([OH]-*=[!#6])]");
        parse("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX 2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
        parse("[NX2-]");
        parse("[OX2H+]=*");
        parse("[OX3H2+]");
        parse("[#6+]");
        parse("[$([cX2+](:*):*)]");
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
        parse("[+1]~*~*~[-1]");
        parse("[$([!-0!-1!-2!-3!-4]~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~[!+0!+1!+2!+3!+4]),$([ !-0!-1!-2!-3!-4]~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~[!+0!+1!+2!+3!+ 4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~ *~*~*~*~*~[!+0!+1!+2!+3!+4])]");
        parse("([!-0!-1!-2!-3!-4].[!+0!+1!+2!+3!+4])");
        parse("[#6,#7;R0]=[#8]");
        parse("[!$([#6,F,Cl,Br,I,o,s,nX3,#7v5,#15v5,#16v4,#16v6,*+1,*+2,*+3,])]");
        parse("[!$([#6,H0,-,-2,-3])]");
        parse("[!H0;#7,#8,#9]");
        parse("[O,N;!H0]-*~*-*=[$([C,N;R0]=O)]");
        parse("[#6;X3v3+0]");
        parse("[#7;X2v4+0]");
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][ CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])] i");
        parse("[#6][CX3](=O)[$([OX2H0]([#6])[#6]),$([#7])]");
        parse("[#8]=[C,N]-aaa[F,Cl,Br,I]");
        parse("[O,N;!H0;R0]");
        parse("[#8]=[C,N] or O=[C,N]");
        parse("[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]-[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]");
        parse("[nX3r5+]:c:n");
        parse("[c;$([*Cl]),$([*H1])]1ccc(O)c(C)c1 or Cc1:c(O):c:c:[$(cCl),$([cH])]:c1");
        parse("[!#1;!#2;!#3;!#5;!#6;!#7;!#8;!#9;!#11;!#12;!#15;!#16;!#17;!#19;!#20;!#35;!#53]");
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println("Total: " + total + "; Failed: " + failed);
        System.out.println("Time: " + (end - start) + " milli seconds");
    }

}
