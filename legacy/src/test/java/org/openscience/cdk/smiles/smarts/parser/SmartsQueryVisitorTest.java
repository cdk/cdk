/* Copyright (C) 2004-2008  The Chemistry Development Kit (CDK) project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * (or see http://www.gnu.org/copyleft/lesser.html)
 */

package org.openscience.cdk.smiles.smarts.parser;

import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;

/**
 * JUnit testing routine for SmartsQueryVisitor
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-05-10
 * @cdk.module test-smarts
 * @cdk.keyword SMARTS
 */
class SmartsQueryVisitorTest extends CDKTestCase {

    void visit(String smarts) throws Exception {
        SMARTSParser parser = new SMARTSParser(new StringReader(smarts));
        ASTStart start = parser.Start();
        SmartsQueryVisitor visitor = new SmartsQueryVisitor(DefaultChemObjectBuilder.getInstance());
        visitor.visit(start, null);
    }

    @Test
    void testPattern1() throws Exception {
        visit("[CX4]");
    }

    @Test
    void testPattern2() throws Exception {
        visit("[$([CX2](=C)=C)]");
    }

    @Test
    void testPattern3() throws Exception {
        visit("[$([CX3]=[CX3])]");
    }

    @Test
    void testPattern4() throws Exception {
        visit("[$([CX2]#C)]");
    }

    @Test
    void testPattern5() throws Exception {
        visit("[CX3]=[OX1]");
    }

    @Test
    void testPattern6() throws Exception {
        visit("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]");
    }

    @Test
    void testPattern7() throws Exception {
        visit("[CX3](=[OX1])C");
    }

    @Test
    void testPattern8() throws Exception {
        visit("[OX1]=CN");
    }

    @Test
    void testPattern9() throws Exception {
        visit("[CX3](=[OX1])O");
    }

    @Test
    void testPattern10() throws Exception {
        visit("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    @Test
    void testPattern11() throws Exception {
        visit("[CX3H1](=O)[#6]");
    }

    @Test
    void testPattern12() throws Exception {
        visit("[CX3](=[OX1])[OX2][CX3](=[OX1])");
    }

    @Test
    void testPattern13() throws Exception {
        visit("[NX3][CX3](=[OX1])[#6]");
    }

    @Test
    void testPattern14() throws Exception {
        visit("[NX3][CX3]=[NX3+]");
    }

    @Test
    void testPattern15() throws Exception {
        visit("[NX3,NX4+][CX3](=[OX1])[OX2,OX1-]");
    }

    @Test
    void testPattern16() throws Exception {
        visit("[NX3][CX3](=[OX1])[OX2H0]");
    }

    @Test
    void testPattern17() throws Exception {
        visit("[NX3,NX4+][CX3](=[OX1])[OX2H,OX1-]");
    }

    @Test
    void testPattern18() throws Exception {
        visit("[CX3](=O)[O-]");
    }

    @Test
    void testPattern19() throws Exception {
        visit("[CX3](=[OX1])(O)O");
    }

    @Test
    void testPattern20() throws Exception {
        visit("[CX3](=[OX1])([OX2])[OX2H,OX1H0-1]");
    }

    @Test
    void testPattern21() throws Exception {
        visit("[CX3](=O)[OX2H1]");
    }

    @Test
    void testPattern22() throws Exception {
        visit("[CX3](=O)[OX1H0-,OX2H1]");
    }

    @Test
    void testPattern23() throws Exception {
        visit("[NX3][CX2]#[NX1]");
    }

    @Test
    void testPattern24() throws Exception {
        visit("[#6][CX3](=O)[OX2H0][#6]");
    }

    @Test
    void testPattern25() throws Exception {
        visit("[#6][CX3](=O)[#6]");
    }

    @Test
    void testPattern26() throws Exception {
        visit("[OD2]([#6])[#6]");
    }

    @Test
    void testPattern27() throws Exception {
        visit("[H]");
    }

    @Test
    void testPattern28() throws Exception {
        visit("[!#1]");
    }

    @Test
    void testPattern29() throws Exception {
        visit("[H+]");
    }

    @Test
    void testPattern30() throws Exception {
        visit("[+H]");
    }

    @Test
    void testPattern31() throws Exception {
        visit("[NX3;H2,H1;!$(NC=O)]");
    }

    @Test
    void testPattern32() throws Exception {
        visit("[NX3][CX3]=[CX3]");
    }

    @Test
    void testPattern33() throws Exception {
        visit("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }

    @Test
    void testPattern34() throws Exception {
        visit("[NX3][$(C=C),$(cc)]");
    }

    @Test
    void testPattern35() throws Exception {
        visit("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
    }

    @Test
    void testPattern36() throws Exception {
        visit("[NX3H2,NH3X4+][CX4H]([*])[CX3](=[OX1])[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-]");
    }

    @Test
    void testPattern37() throws Exception {
        visit("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    void testPattern38() throws Exception {
        visit("[CH3X4]");
    }

    @Test
    void testPattern39() throws Exception {
        visit("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }

    @Test
    void testPattern40() throws Exception {
        visit("[CH2X4][CX3](=[OX1])[NX3H2]");
    }

    @Test
    void testPattern41() throws Exception {
        visit("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    void testPattern42() throws Exception {
        visit("[CH2X4][SX2H,SX1H0-]");
    }

    @Test
    void testPattern43() throws Exception {
        visit("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    void testPattern44() throws Exception {
        visit("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    void testPattern45() throws Exception {
        visit("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }

    @Test
    void testPattern47() throws Exception {
        visit("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }

    @Test
    void testPattern48() throws Exception {
        visit("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    void testPattern49() throws Exception {
        visit("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }

    @Test
    void testPattern50() throws Exception {
        visit("[CH2X4][CH2X4][SX2][CH3X4]");
    }

    @Test
    void testPattern51() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }

    @Test
    void testPattern52() throws Exception {
        visit("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    void testPattern53() throws Exception {
        visit("[CH2X4][OX2H]");
    }

    @Test
    void testPattern54() throws Exception {
        visit("[NX3][CX3]=[SX1]");
    }

    @Test
    void testPattern55() throws Exception {
        visit("[CHX4]([CH3X4])[OX2H]");
    }

    @Test
    void testPattern56() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }

    @Test
    void testPattern57() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }

    @Test
    void testPattern58() throws Exception {
        visit("[CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    void testPattern59() throws Exception {
        visit("[CH3X4]");
    }

    @Test
    void testPattern60() throws Exception {
        visit("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }

    @Test
    void testPattern61() throws Exception {
        visit("[CH2X4][CX3](=[OX1])[NX3H2]");
    }

    @Test
    void testPattern62() throws Exception {
        visit("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    void testPattern63() throws Exception {
        visit("[CH2X4][SX2H,SX1H0-]");
    }

    @Test
    void testPattern64() throws Exception {
        visit("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    void testPattern65() throws Exception {
        visit("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }

    @Test
    void testPattern67() throws Exception {
        visit("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }

    @Test
    void testPattern68() throws Exception {
        visit("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    void testPattern69() throws Exception {
        visit("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }

    @Test
    void testPattern70() throws Exception {
        visit("[CH2X4][CH2X4][SX2][CH3X4]");
    }

    @Test
    void testPattern71() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }

    @Test
    void testPattern72() throws Exception {
        visit("[CH2X4][OX2H]");
    }

    @Test
    void testPattern73() throws Exception {
        visit("[CHX4]([CH3X4])[OX2H]");
    }

    @Test
    void testPattern74() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }

    @Test
    void testPattern75() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }

    @Test
    void testPattern76() throws Exception {
        visit("[CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    void testPattern77() throws Exception {
        visit("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }

    @Test
    void testPattern78() throws Exception {
        visit("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    @Test
    void testPattern79() throws Exception {
        visit("[#7]");
    }

    @Test
    void testPattern80() throws Exception {
        visit("[NX2]=N");
    }

    @Test
    void testPattern81() throws Exception {
        visit("[NX2]=[NX2]");
    }

    @Test
    void testPattern82() throws Exception {
        visit("[$([NX2]=[NX3+]([O-])[#6]),$([NX2]=[NX3+0](=[O])[#6])]");
    }

    @Test
    void testPattern83() throws Exception {
        visit("[$([#6]=[N+]=[N-]),$([#6-]-[N+]#[N])]");
    }

    @Test
    void testPattern84() throws Exception {
        visit("[$([nr5]:[nr5,or5,sr5]),$([nr5]:[cr5]:[nr5,or5,sr5])]");
    }

    @Test
    void testPattern85() throws Exception {
        visit("[NX3][NX3]");
    }

    @Test
    void testPattern86() throws Exception {
        visit("[NX3][NX2]=[*]");
    }

    @Test
    void testPattern87() throws Exception {
        visit("[CX3;$([C]([#6])[#6]),$([CH][#6])]=[NX2][#6]");
    }

    @Test
    void testPattern88() throws Exception {
        visit("[$([CX3]([#6])[#6]),$([CX3H][#6])]=[$([NX2][#6]),$([NX2H])]");
    }

    @Test
    void testPattern89() throws Exception {
        visit("[NX3+]=[CX3]");
    }

    @Test
    void testPattern90() throws Exception {
        visit("[CX3](=[OX1])[NX3H][CX3](=[OX1])");
    }

    @Test
    void testPattern91() throws Exception {
        visit("[CX3](=[OX1])[NX3H0]([#6])[CX3](=[OX1])");
    }

    @Test
    void testPattern92() throws Exception {
        visit("[CX3](=[OX1])[NX3H0]([NX3H0]([CX3](=[OX1]))[CX3](=[OX1]))[CX3](=[OX1])");
    }

    @Test
    void testPattern93() throws Exception {
        visit("[$([NX3](=[OX1])(=[OX1])O),$([NX3+]([OX1-])(=[OX1])O)]");
    }

    @Test
    void testPattern94() throws Exception {
        visit("[$([OX1]=[NX3](=[OX1])[OX1-]),$([OX1]=[NX3+]([OX1-])[OX1-])]");
    }

    @Test
    void testPattern95() throws Exception {
        visit("[NX1]#[CX2]");
    }

    @Test
    void testPattern96() throws Exception {
        visit("[CX1-]#[NX2+]");
    }

    @Test
    void testPattern97() throws Exception {
        visit("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }

    @Test
    void testPattern98() throws Exception {
        visit("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8].[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }

    @Test
    void testPattern99() throws Exception {
        visit("[NX2]=[OX1]");
    }

    @Test
    void testPattern101() throws Exception {
        visit("[$([#7+][OX1-]),$([#7v5]=[OX1]);!$([#7](~[O])~[O]);!$([#7]=[#7])]");
    }

    @Test
    void testPattern102() throws Exception {
        visit("[OX2H]");
    }

    @Test
    void testPattern103() throws Exception {
        visit("[#6][OX2H]");
    }

    @Test
    void testPattern104() throws Exception {
        visit("[OX2H][CX3]=[OX1]");
    }

    @Test
    void testPattern105() throws Exception {
        visit("[OX2H]P");
    }

    @Test
    void testPattern106() throws Exception {
        visit("[OX2H][#6X3]=[#6]");
    }

    @Test
    void testPattern107() throws Exception {
        visit("[OX2H][cX3]:[c]");
    }

    @Test
    void testPattern108() throws Exception {
        visit("[OX2H][$(C=C),$(cc)]");
    }

    @Test
    void testPattern109() throws Exception {
        visit("[$([OH]-*=[!#6])]");
    }

    @Test
    void testPattern110() throws Exception {
        visit("[OX2,OX1-][OX2,OX1-]");
    }

    @Test
    void testPattern111() throws Exception { // Phosphoric_acid groups.
        visit("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX"
                + "2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }

    @Test
    void testPattern112() throws Exception { // Phosphoric_ester groups.
        visit("[$(P(=[OX1])([OX2][#6])([$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)]),$([P+]([OX1-])([OX2][#6])(["
                + "$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)])]");
    }

    @Test
    void testPattern113() throws Exception {
        visit("[S-][CX3](=S)[#6]");
    }

    @Test
    void testPattern114() throws Exception {
        visit("[#6X3](=[SX1])([!N])[!N]");
    }

    @Test
    void testPattern115() throws Exception {
        visit("[SX2]");
    }

    @Test
    void testPattern116() throws Exception {
        visit("[#16X2H]");
    }

    @Test
    void testPattern117() throws Exception {
        visit("[#16!H0]");
    }

    @Test
    void testPattern118() throws Exception {
        visit("[NX3][CX3]=[SX1]");
    }

    @Test
    void testPattern119() throws Exception {
        visit("[#16X2H0]");
    }

    @Test
    void testPattern120() throws Exception {
        visit("[#16X2H0][!#16]");
    }

    @Test
    void testPattern121() throws Exception {
        visit("[#16X2H0][#16X2H0]");
    }

    @Test
    void testPattern122() throws Exception {
        visit("[#16X2H0][!#16].[#16X2H0][!#16]");
    }

    @Test
    void testPattern123() throws Exception {
        visit("[$([#16X3](=[OX1])[OX2H0]),$([#16X3+]([OX1-])[OX2H0])]");
    }

    @Test
    void testPattern124() throws Exception {
        visit("[$([#16X3](=[OX1])[OX2H,OX1H0-]),$([#16X3+]([OX1-])[OX2H,OX1H0-])]");
    }

    @Test
    void testPattern125() throws Exception {
        visit("[$([#16X4](=[OX1])=[OX1]),$([#16X4+2]([OX1-])[OX1-])]");
    }

    @Test
    void testPattern126() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([#6])[#6]),$([#16X4+2]([OX1-])([OX1-])([#6])[#6])]");
    }

    @Test
    void testPattern127() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }

    @Test
    void testPattern128() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H0]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H0])]");
    }

    @Test
    void testPattern129() throws Exception {
        visit("[$([#16X4]([NX3])(=[OX1])(=[OX1])[#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[#6])]");
    }

    @Test
    void testPattern130() throws Exception {
        visit("[SX4](C)(C)(=O)=N");
    }

    @Test
    void testPattern131() throws Exception {
        visit("[$([SX4](=[OX1])(=[OX1])([!O])[NX3]),$([SX4+2]([OX1-])([OX1-])([!O])[NX3])]");
    }

    @Test
    void testPattern132() throws Exception {
        visit("[$([#16X3]=[OX1]),$([#16X3+][OX1-])]");
    }

    @Test
    void testPattern133() throws Exception {
        visit("[$([#16X3](=[OX1])([#6])[#6]),$([#16X3+]([OX1-])([#6])[#6])]");
    }

    @Test
    void testPattern134() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([OX2H,OX1H0-])[OX2][#6]),$([#16X4+2]([OX1-])([OX1-])([OX2H,OX1H0-])[OX2][#6])]");
    }

    @Test
    void testPattern135() throws Exception {
        visit("[$([SX4](=O)(=O)(O)O),$([SX4+2]([O-])([O-])(O)O)]");
    }

    @Test
    void testPattern136() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6]),$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6])]");
    }

    @Test
    void testPattern137() throws Exception {
        visit("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2][#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2][#6])]");
    }

    @Test
    void testPattern138() throws Exception {
        visit("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2H,OX1H0-]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2H,OX1H0-])]");
    }

    @Test
    void testPattern139() throws Exception {
        visit("[#16X2][OX2H,OX1H0-]");
    }

    @Test
    void testPattern140() throws Exception {
        visit("[#16X2][OX2H0]");
    }

    @Test
    void testPattern141() throws Exception {
        visit("[#6][F,Cl,Br,I]");
    }

    @Test
    void testPattern142() throws Exception {
        visit("[F,Cl,Br,I]");
    }

    @Test
    void testPattern143() throws Exception {
        visit("[F,Cl,Br,I].[F,Cl,Br,I].[F,Cl,Br,I]");
    }

    @Test
    void testPattern144() throws Exception {
        visit("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    @Test
    void testPattern145() throws Exception {
        visit("[$([#6X4@](*)(*)(*)*),$([#6X4@H](*)(*)*)]");
    }

    @Test
    void testPattern146() throws Exception {
        visit("[$([cX2+](:*):*)]");
    }

    @Test
    void testPattern147() throws Exception {
        visit("[$([cX3](:*):*),$([cX2+](:*):*)]");
    }

    @Test
    void testPattern148() throws Exception {
        visit("[$([cX3](:*):*),$([cX2+](:*):*),$([CX3]=*),$([CX2+]=*)]");
    }

    @Test
    void testPattern149() throws Exception {
        visit("[$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)]");
    }

    @Test
    void testPattern150() throws Exception {
        visit("[$([#1X1][$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)])]");
    }

    @Test
    void testPattern151() throws Exception {
        visit("[$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)]");
    }

    @Test
    void testPattern152() throws Exception {
        visit("[$([#1X1][$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)])]");
    }

    @Test
    void testPattern153() throws Exception {
        visit("[$([$([NX3]=O),$([NX3+][O-])])]");
    }

    @Test
    void testPattern154() throws Exception {
        visit("[$([$([NX4]=O),$([NX4+][O-])])]");
    }

    @Test
    void testPattern155() throws Exception {
        visit("[$([$([NX4]=O),$([NX4+][O-,#0])])]");
    }

    @Test
    void testPattern156() throws Exception {
        visit("[$([NX4+]),$([NX4]=*)]");
    }

    @Test
    void testPattern157() throws Exception {
        visit("[$([SX3]=N)]");
    }

    @Test
    void testPattern158() throws Exception {
        visit("[$([SX1]=[#6])]");
    }

    @Test
    void testPattern159() throws Exception {
        visit("[$([NX1]#*)]");
    }

    @Test
    void testPattern160() throws Exception {
        visit("[$([OX2])]");
    }

    @Test
    void testPattern161() throws Exception {
        visit("[R0;D2][R0;D2][R0;D2][R0;D2]");
    }

    @Test
    void testPattern162() throws Exception {
        visit("[R0;D2]~[R0;D2]~[R0;D2]~[R0;D2]");
    }

    @Test
    void testPattern163() throws Exception {
        visit("[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]");
    }

    @Test
    void testPattern164() throws Exception {
        visit("[!$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }

    @Test
    void testPattern165() throws Exception {
        visit("[$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }

    @Test
    void testPattern166() throws Exception {
        visit("[$([SX1]~P)]");
    }

    @Test
    void testPattern167() throws Exception {
        visit("[$([NX3]C=N)]");
    }

    @Test
    void testPattern168() throws Exception {
        visit("[$([NX3]N=C)]");
    }

    @Test
    void testPattern169() throws Exception {
        visit("[$([NX3]N=N)]");
    }

    @Test
    void testPattern170() throws Exception {
        visit("[$([OX2]C=N)]");
    }

    @Test
    void testPattern171() throws Exception {
        visit("[!$(*#*)&!D1]-!@[!$(*#*)&!D1]");
    }

    @Test
    void testPattern172() throws Exception {
        visit("[$([*R2]([*R])([*R])([*R]))].[$([*R2]([*R])([*R])([*R]))]");
    }

    @Test
    void testPattern173() throws Exception {
        visit("*-!:aa-!:*");
    }

    @Test
    void testPattern174() throws Exception {
        visit("*-!:aaa-!:*");
    }

    @Test
    void testPattern175() throws Exception {
        visit("*-!:aaaa-!:*");
    }

    @Test
    void testPattern176() throws Exception {
        visit("*-!@*");
    }

    @Test
    void testPattern177() throws Exception { // CIS or TRANS double or aromatic bond in a ring
        visit("*/,\\[R]=,:;@[R]/,\\*");
    }

    @Test
    void testPattern178() throws Exception { // Fused benzene rings
        visit("c12ccccc1cccc2");
    }

    @Test
    void testPattern179() throws Exception {
        visit("[r;!r3;!r4;!r5;!r6;!r7]");
    }

    @Test
    void testPattern180() throws Exception {
        visit("[sX2r5]");
    }

    @Test
    void testPattern181() throws Exception {
        visit("[oX2r5]");
    }

    @Test
    void testPattern182() throws Exception { // Unfused benzene ring
        visit("[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }

    @Test
    void testPattern183() throws Exception { // Multiple non-fused benzene rings
        visit("[cR1]1[cR1][cR1][cR1][cR1][cR1]1.[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }

    @Test
    void testPattern184() throws Exception { // Generic amino acid: low specificity.
        visit("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
    }

    @Test
    void testPattern185() throws Exception { //Template for 20 standard a.a.s
        visit("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),"
                + "$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX"
                + "4H2][CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    void testPattern186() throws Exception { // Proline
        visit("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    void testPattern187() throws Exception { // Glycine
        visit("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    void testPattern188() throws Exception { // Alanine
        visit("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([CH3X4])[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    void testPattern189() throws Exception { //18_standard_aa_side_chains.
        visit("([$([CH3X4]),$([CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]),"
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
        visit("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3]"
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
        visit("[$([NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]);!$([$([$([NX3H,NX4H2+]),"
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
        visit("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }

    @Test
    void testPattern193() throws Exception { // Azide ion
        visit("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    @Test
    void testPattern194() throws Exception { //Azide or azide ion
        visit("[$([$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]),$([$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])])]");
    }

    @Test
    void testPattern195() throws Exception { // Sulfide
        visit("[#16X2H0]");
    }

    @Test
    void testPattern196() throws Exception { // Mono-sulfide
        visit("[#16X2H0][!#16]");
    }

    @Test
    void testPattern197() throws Exception { // Di-sulfide
        visit("[#16X2H0][#16X2H0]");
    }

    @Test
    void testPattern198() throws Exception { // Two sulfides
        visit("[#16X2H0][!#16].[#16X2H0][!#16]");
    }

    @Test
    void testPattern199() throws Exception { // Acid/conj-base
        visit("[OX2H,OX1H0-]");
    }

    @Test
    void testPattern200() throws Exception { // Non-acid Oxygen
        visit("[OX2H0]");
    }

    @Test
    void testPattern201() throws Exception { // Acid/base
        visit("[H1,H0-]");
    }

    @Test
    void testPattern202() throws Exception {
        visit("([Cl!$(Cl~c)].[c!$(c~Cl)])");
    }

    @Test
    void testPattern203() throws Exception {
        visit("([Cl]).([c])");
    }

    @Test
    void testPattern204() throws Exception {
        visit("([Cl].[c])");
    }

    @Test
    void testPattern205() throws Exception {
        visit("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }

    @Test
    void testPattern206() throws Exception {
        visit("[#0]");
    }

    @Test
    void testPattern207() throws Exception {
        visit("[*!H0,#1]");
    }

    @Test
    void testPattern208() throws Exception {
        visit("[#6!H0,#1]");
    }

    @Test
    void testPattern209() throws Exception {
        visit("[H,#1]");
    }

    @Test
    void testPattern210() throws Exception {
        visit("[!H0;F,Cl,Br,I,N+,$([OH]-*=[!#6]),+]");
    }

    @Test
    void testPattern211() throws Exception {
        visit("[CX3](=O)[OX2H1]");
    }

    @Test
    void testPattern212() throws Exception {
        visit("[CX3](=O)[OX1H0-,OX2H1]");
    }

    @Test
    void testPattern213() throws Exception {
        visit("[$([OH]-*=[!#6])]");
    }

    @Test
    void testPattern214() throws Exception { // Phosphoric_Acid
        visit("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX"
                + "2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }

    @Test
    void testPattern215() throws Exception { // Sulfonic Acid. High specificity.
        visit("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }

    @Test
    void testPattern216() throws Exception { // Acyl Halide
        visit("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    @Test
    void testPattern217() throws Exception {
        visit("[NX2-]");
    }

    @Test
    void testPattern218() throws Exception {
        visit("[OX2H+]=*");
    }

    @Test
    void testPattern219() throws Exception {
        visit("[OX3H2+]");
    }

    @Test
    void testPattern220() throws Exception {
        visit("[#6+]");
    }

    @Test
    void testPattern221() throws Exception {
        visit("[$([cX2+](:*):*)]");
    }

    @Test
    void testPattern222() throws Exception {
        visit("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    @Test
    void testPattern223() throws Exception {
        visit("[+1]~*~*~[-1]");
    }

    @Test
    void testPattern224() throws Exception {
        visit("[$([!-0!-1!-2!-3!-4]~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4])]");
    }

    @Test
    void testPattern225() throws Exception {
        visit("([!-0!-1!-2!-3!-4].[!+0!+1!+2!+3!+4])");
    }

    @Test
    void testPattern226() throws Exception { // Hydrogen-bond acceptor, Only hits carbonyl and nitroso
        visit("[#6,#7;R0]=[#8]");
    }

    @Test
    void testPattern227() throws Exception { // Hydrogen-bond acceptor
        visit("[!$([#6,F,Cl,Br,I,o,s,nX3,#7v5,#15v5,#16v4,#16v6,*+1,*+2,*+3])]");
    }

    @Test
    void testPattern228() throws Exception {
        visit("[!$([#6,H0,-,-2,-3])]");
    }

    @Test
    void testPattern229() throws Exception {
        visit("[!H0;#7,#8,#9]");
    }

    @Test
    void testPattern230() throws Exception {
        visit("[O,N;!H0]-*~*-*=[$([C,N;R0]=O)]");
    }

    @Test
    void testPattern231() throws Exception {
        visit("[#6;X3v3+0]");
    }

    @Test
    void testPattern232() throws Exception {
        visit("[#7;X2v4+0]");
    }

    @Test
    void testPattern233() throws Exception { // Amino Acid
        visit("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2]["
                + "CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    void testPattern234() throws Exception {
        visit("[#6][CX3](=O)[$([OX2H0]([#6])[#6]),$([#7])]");
    }

    @Test
    void testPattern235() throws Exception {
        visit("[#8]=[C,N]-aaa[F,Cl,Br,I]");
    }

    @Test
    void testPattern236() throws Exception {
        visit("[O,N;!H0;R0]");
    }

    @Test
    void testPattern237() throws Exception {
        visit("[#8]=[C,N]");
    }

    @Test
    void testPattern238() throws Exception { // PCB
        visit("[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]-[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]");
    }

    @Test
    void testPattern239() throws Exception { // Imidazolium Nitrogen
        visit("[nX3r5+]:c:n");
    }

    @Test
    void testPattern240() throws Exception { // 1-methyl-2-hydroxy benzene with either a Cl or H at the 5 position.
        visit("[c;$([*Cl]),$([*H1])]1ccc(O)c(C)c1");
        visit("Cc1:c(O):c:c:[$(cCl),$([cH])]:c1");
    }

    @Test
    void testPattern241() throws Exception { // Nonstandard atom groups.
        visit("[!#1;!#2;!#3;!#5;!#6;!#7;!#8;!#9;!#11;!#12;!#15;!#16;!#17;!#19;!#20;!#35;!#53]");
    }

    @Test
    void testRing() throws Exception {
        visit("[$([C;#12]=1CCCCC1)]");
    }

    @Test
    void reaction() throws Exception {
        visit("CCO.CC(=O)O>[H+]>CCOC(=O)C.O");
    }

    @Test
    void reactionNoAgents() throws Exception {
        visit("CCO.CC(=O)O>>CCOC(=O)C.O");
    }

    @Test
    void reactionNoProduct() throws Exception {
        visit("CCO.CC(=O)O>>");
    }

    @Test
    void reactionNoReactant() throws Exception {
        visit(">>CCOC(=O)C.O");
    }

    @Test
    void reactionOnlyAgents() throws Exception {
        visit(">>CCOC(=O)C.O");
    }
}
