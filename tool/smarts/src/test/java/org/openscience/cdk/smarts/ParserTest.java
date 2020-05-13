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
package org.openscience.cdk.smarts;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.Smarts;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * JUnit test routines for the SMARTS parser.
 *
 * @author Egon Willighagen
 * @cdk.module test-smarts
 * @cdk.require ant1.6
 */
public class ParserTest extends CDKTestCase {

    private static final class InvalidSmarts extends Exception {
        public InvalidSmarts(String message) {
            super(message);
        }
    }

    private void parse(String smarts, int flav) throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        if (!Smarts.parse(builder.newAtomContainer(), smarts, flav))
            throw new InvalidSmarts(Smarts.getLastErrorMesg());
    }

    private void parse(String smarts) throws Exception {
        parse(smarts, Smarts.FLAVOR_LOOSE);
    }

    @Test
    public void errorHandling() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        if (!Smarts.parse(builder.newAtomContainer(), "CCCJCCC")) {
            assertThat(Smarts.getLastErrorMesg(),     CoreMatchers.is("Unexpected character"));
            assertThat(Smarts.getLastErrorLocation(), CoreMatchers.is("CCCJCCC\n   ^\n"));
        }
    }

    @Test
    public void testQueryAtomCreation() throws Exception {
        parse("*");
    }

    @Test
    public void testAliphaticAtom() throws Exception {
        parse("A");
    }

    @Test
    public void testAromaticAtom() throws Exception {
        parse("a");
    }

    @Test
    public void testDegree() throws Exception {
        parse("[D2]");
    }

    @Test
    public void testImplicitHCount() throws Exception {
        parse("[h3]");
    }

    @Test
    public void testTotalHCount() throws Exception {
        parse("[H2]");
    }

    /**
     * @cdk.bug 1760967
     */
    @Test
    public void testSingleBond() throws Exception {
        parse("C-C");
    }

    @Test
    public void testDoubleBond() throws Exception {
        parse("C=C");
    }

    @Test
    public void testTripleBond() throws Exception {
        parse("C#C");
    }

    @Test
    public void testAromaticBond() throws Exception {
        parse("C:C");
    }

    @Test
    public void testAnyOrderBond() throws Exception {
        parse("C~C");
    }

    /**
     * @cdk.bug 2786624
     */
    @Test
    public void test2LetterSMARTS() throws Exception {
        parse("Sc1ccccc1");
    }

    @Test
    public void testPattern1() throws Exception {
        parse("[CX4]");
    }

    @Test
    public void testPattern2() throws Exception {
        parse("[$([CX2](=C)=C)]");
    }

    @Test
    public void testPattern3() throws Exception {
        parse("[$([CX3]=[CX3])]");
    }

    @Test
    public void testPattern4() throws Exception {
        parse("[$([CX2]#C)]");
    }

    @Test
    public void testPattern5() throws Exception {
        parse("[CX3]=[OX1]");
    }

    @Test
    public void testPattern6() throws Exception {
        parse("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]");
    }

    @Test
    public void testPattern7() throws Exception {
        parse("[CX3](=[OX1])C");
    }

    @Test
    public void testPattern8() throws Exception {
        parse("[OX1]=CN");
    }

    @Test
    public void testPattern9() throws Exception {
        parse("[CX3](=[OX1])O");
    }

    @Test
    public void testPattern10() throws Exception {
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    @Test
    public void testPattern11() throws Exception {
        parse("[CX3H1](=O)[#6]");
    }

    @Test
    public void testPattern12() throws Exception {
        parse("[CX3](=[OX1])[OX2][CX3](=[OX1])");
    }

    @Test
    public void testPattern13() throws Exception {
        parse("[NX3][CX3](=[OX1])[#6]");
    }

    @Test
    public void testPattern14() throws Exception {
        parse("[NX3][CX3]=[NX3+]");
    }

    @Test
    public void testPattern15() throws Exception {
        parse("[NX3,NX4+][CX3](=[OX1])[OX2,OX1-]");
    }

    @Test
    public void testPattern16() throws Exception {
        parse("[NX3][CX3](=[OX1])[OX2H0]");
    }

    @Test
    public void testPattern17() throws Exception {
        parse("[NX3,NX4+][CX3](=[OX1])[OX2H,OX1-]");
    }

    @Test
    public void testPattern18() throws Exception {
        parse("[CX3](=O)[O-]");
    }

    @Test
    public void testPattern19() throws Exception {
        parse("[CX3](=[OX1])(O)O");
    }

    @Test
    public void testPattern20() throws Exception {
        parse("[CX3](=[OX1])([OX2])[OX2H,OX1H0-1]");
    }

    @Test
    public void testPattern21() throws Exception {
        parse("[CX3](=O)[OX2H1]");
    }

    @Test
    public void testPattern22() throws Exception {
        parse("[CX3](=O)[OX1H0-,OX2H1]");
    }

    @Test
    public void testPattern23() throws Exception {
        parse("[NX3][CX2]#[NX1]");
    }

    @Test
    public void testPattern24() throws Exception {
        parse("[#6][CX3](=O)[OX2H0][#6]");
    }

    @Test
    public void testPattern25() throws Exception {
        parse("[#6][CX3](=O)[#6]");
    }

    @Test
    public void testPattern26() throws Exception {
        parse("[OD2]([#6])[#6]");
    }

    @Test
    public void testPattern27() throws Exception {
        parse("[H]");
    }

    @Test
    public void testPattern28() throws Exception {
        parse("[!#1]");
    }

    @Test
    public void testPattern29() throws Exception {
        parse("[H+]");
    }

    @Test
    public void testPattern30() throws Exception {
        parse("[+H]");
    }

    @Test
    public void testPattern31() throws Exception {
        parse("[NX3;H2,H1;!$(NC=O)]");
    }

    @Test
    public void testPattern32() throws Exception {
        parse("[NX3][CX3]=[CX3]");
    }

    @Test
    public void testPattern33() throws Exception {
        parse("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }

    @Test
    public void testPattern34() throws Exception {
        parse("[NX3][$(C=C),$(cc)]");
    }

    @Test
    public void testPattern35() throws Exception {
        parse("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
    }

    @Test
    public void testPattern36() throws Exception {
        parse("[NX3H2,NH3X4+][CX4H]([*])[CX3](=[OX1])[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-]");
    }

    @Test
    public void testPattern37() throws Exception {
        parse("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    public void testPattern38() throws Exception {
        parse("[CH3X4]");
    }

    @Test
    public void testPattern39() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }

    @Test
    public void testPattern40() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[NX3H2]");
    }

    @Test
    public void testPattern41() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    public void testPattern42() throws Exception {
        parse("[CH2X4][SX2H,SX1H0-]");
    }

    @Test
    public void testPattern43() throws Exception {
        parse("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    public void testPattern44() throws Exception {
        parse("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    public void testPattern45() throws Exception {
        parse("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }

    @Test
    public void testPattern47() throws Exception {
        parse("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }

    @Test
    public void testPattern48() throws Exception {
        parse("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    public void testPattern49() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }

    @Test
    public void testPattern50() throws Exception {
        parse("[CH2X4][CH2X4][SX2][CH3X4]");
    }

    @Test
    public void testPattern51() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }

    @Test
    public void testPattern52() throws Exception {
        parse("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    public void testPattern53() throws Exception {
        parse("[CH2X4][OX2H]");
    }

    @Test
    public void testPattern54() throws Exception {
        parse("[NX3][CX3]=[SX1]");
    }

    @Test
    public void testPattern55() throws Exception {
        parse("[CHX4]([CH3X4])[OX2H]");
    }

    @Test
    public void testPattern56() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }

    @Test
    public void testPattern57() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }

    @Test
    public void testPattern58() throws Exception {
        parse("[CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    public void testPattern59() throws Exception {
        parse("[CH3X4]");
    }

    @Test
    public void testPattern60() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }

    @Test
    public void testPattern61() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[NX3H2]");
    }

    @Test
    public void testPattern62() throws Exception {
        parse("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    public void testPattern63() throws Exception {
        parse("[CH2X4][SX2H,SX1H0-]");
    }

    @Test
    public void testPattern64() throws Exception {
        parse("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }

    @Test
    public void testPattern65() throws Exception {
        parse("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }

    @Test
    public void testPattern67() throws Exception {
        parse("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }

    @Test
    public void testPattern68() throws Exception {
        parse("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    public void testPattern69() throws Exception {
        parse("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }

    @Test
    public void testPattern70() throws Exception {
        parse("[CH2X4][CH2X4][SX2][CH3X4]");
    }

    @Test
    public void testPattern71() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }

    @Test
    public void testPattern72() throws Exception {
        parse("[CH2X4][OX2H]");
    }

    @Test
    public void testPattern73() throws Exception {
        parse("[CHX4]([CH3X4])[OX2H]");
    }

    @Test
    public void testPattern74() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }

    @Test
    public void testPattern75() throws Exception {
        parse("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }

    @Test
    public void testPattern76() throws Exception {
        parse("[CHX4]([CH3X4])[CH3X4]");
    }

    @Test
    public void testPattern77() throws Exception {
        parse("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }

    @Test
    public void testPattern78() throws Exception {
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    @Test
    public void testPattern79() throws Exception {
        parse("[#7]");
    }

    @Test
    public void testPattern80() throws Exception {
        parse("[NX2]=N");
    }

    @Test
    public void testPattern81() throws Exception {
        parse("[NX2]=[NX2]");
    }

    @Test
    public void testPattern82() throws Exception {
        parse("[$([NX2]=[NX3+]([O-])[#6]),$([NX2]=[NX3+0](=[O])[#6])]");
    }

    @Test
    public void testPattern83() throws Exception {
        parse("[$([#6]=[N+]=[N-]),$([#6-]-[N+]#[N])]");
    }

    @Test
    public void testPattern84() throws Exception {
        parse("[$([nr5]:[nr5,or5,sr5]),$([nr5]:[cr5]:[nr5,or5,sr5])]");
    }

    @Test
    public void testPattern85() throws Exception {
        parse("[NX3][NX3]");
    }

    @Test
    public void testPattern86() throws Exception {
        parse("[NX3][NX2]=[*]");
    }

    @Test
    public void testPattern87() throws Exception {
        parse("[CX3;$([C]([#6])[#6]),$([CH][#6])]=[NX2][#6]");
    }

    @Test
    public void testPattern88() throws Exception {
        parse("[$([CX3]([#6])[#6]),$([CX3H][#6])]=[$([NX2][#6]),$([NX2H])]");
    }

    @Test
    public void testPattern89() throws Exception {
        parse("[NX3+]=[CX3]");
    }

    @Test
    public void testPattern90() throws Exception {
        parse("[CX3](=[OX1])[NX3H][CX3](=[OX1])");
    }

    @Test
    public void testPattern91() throws Exception {
        parse("[CX3](=[OX1])[NX3H0]([#6])[CX3](=[OX1])");
    }

    @Test
    public void testPattern92() throws Exception {
        parse("[CX3](=[OX1])[NX3H0]([NX3H0]([CX3](=[OX1]))[CX3](=[OX1]))[CX3](=[OX1])");
    }

    @Test
    public void testPattern93() throws Exception {
        parse("[$([NX3](=[OX1])(=[OX1])O),$([NX3+]([OX1-])(=[OX1])O)]");
    }

    @Test
    public void testPattern94() throws Exception {
        parse("[$([OX1]=[NX3](=[OX1])[OX1-]),$([OX1]=[NX3+]([OX1-])[OX1-])]");
    }

    @Test
    public void testPattern95() throws Exception {
        parse("[NX1]#[CX2]");
    }

    @Test
    public void testPattern96() throws Exception {
        parse("[CX1-]#[NX2+]");
    }

    @Test
    public void testPattern97() throws Exception {
        parse("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }

    @Test
    public void testPattern98() throws Exception {
        parse("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8].[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }

    @Test
    public void testPattern99() throws Exception {
        parse("[NX2]=[OX1]");
    }

    @Test
    public void testPattern101() throws Exception {
        parse("[$([#7+][OX1-]),$([#7v5]=[OX1]);!$([#7](~[O])~[O]);!$([#7]=[#7])]");
    }

    @Test
    public void testPattern102() throws Exception {
        parse("[OX2H]");
    }

    @Test
    public void testPattern103() throws Exception {
        parse("[#6][OX2H]");
    }

    @Test
    public void testPattern104() throws Exception {
        parse("[OX2H][CX3]=[OX1]");
    }

    @Test
    public void testPattern105() throws Exception {
        parse("[OX2H]P");
    }

    @Test
    public void testPattern106() throws Exception {
        parse("[OX2H][#6X3]=[#6]");
    }

    @Test
    public void testPattern107() throws Exception {
        parse("[OX2H][cX3]:[c]");
    }

    @Test
    public void testPattern108() throws Exception {
        parse("[OX2H][$(C=C),$(cc)]");
    }

    @Test
    public void testPattern109() throws Exception {
        parse("[$([OH]-*=[!#6])]");
    }

    @Test
    public void testPattern110() throws Exception {
        parse("[OX2,OX1-][OX2,OX1-]");
    }

    @Test
    public void testPattern111() throws Exception { // Phosphoric_acid groups.
        parse("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX"
                + "2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }

    @Test
    public void testPattern112() throws Exception { // Phosphoric_ester groups.
        parse("[$(P(=[OX1])([OX2][#6])([$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)]),$([P+]([OX1-])([OX2][#6])(["
                + "$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)])]");
    }

    @Test
    public void testPattern113() throws Exception {
        parse("[S-][CX3](=S)[#6]");
    }

    @Test
    public void testPattern114() throws Exception {
        parse("[#6X3](=[SX1])([!N])[!N]");
    }

    @Test
    public void testPattern115() throws Exception {
        parse("[SX2]");
    }

    @Test
    public void testPattern116() throws Exception {
        parse("[#16X2H]");
    }

    @Test
    public void testPattern117() throws Exception {
        parse("[#16!H0]");
    }

    @Test
    public void testPattern118() throws Exception {
        parse("[NX3][CX3]=[SX1]");
    }

    @Test
    public void testPattern119() throws Exception {
        parse("[#16X2H0]");
    }

    @Test
    public void testPattern120() throws Exception {
        parse("[#16X2H0][!#16]");
    }

    @Test
    public void testPattern121() throws Exception {
        parse("[#16X2H0][#16X2H0]");
    }

    @Test
    public void testPattern122() throws Exception {
        parse("[#16X2H0][!#16].[#16X2H0][!#16]");
    }

    @Test
    public void testPattern123() throws Exception {
        parse("[$([#16X3](=[OX1])[OX2H0]),$([#16X3+]([OX1-])[OX2H0])]");
    }

    @Test
    public void testPattern124() throws Exception {
        parse("[$([#16X3](=[OX1])[OX2H,OX1H0-]),$([#16X3+]([OX1-])[OX2H,OX1H0-])]");
    }

    @Test
    public void testPattern125() throws Exception {
        parse("[$([#16X4](=[OX1])=[OX1]),$([#16X4+2]([OX1-])[OX1-])]");
    }

    @Test
    public void testPattern126() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[#6]),$([#16X4+2]([OX1-])([OX1-])([#6])[#6])]");
    }

    @Test
    public void testPattern127() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }

    @Test
    public void testPattern128() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H0]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H0])]");
    }

    @Test
    public void testPattern129() throws Exception {
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[#6])]");
    }

    @Test
    public void testPattern130() throws Exception {
        parse("[SX4](C)(C)(=O)=N");
    }

    @Test
    public void testPattern131() throws Exception {
        parse("[$([SX4](=[OX1])(=[OX1])([!O])[NX3]),$([SX4+2]([OX1-])([OX1-])([!O])[NX3])]");
    }

    @Test
    public void testPattern132() throws Exception {
        parse("[$([#16X3]=[OX1]),$([#16X3+][OX1-])]");
    }

    @Test
    public void testPattern133() throws Exception {
        parse("[$([#16X3](=[OX1])([#6])[#6]),$([#16X3+]([OX1-])([#6])[#6])]");
    }

    @Test
    public void testPattern134() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([OX2H,OX1H0-])[OX2][#6]),$([#16X4+2]([OX1-])([OX1-])([OX2H,OX1H0-])[OX2][#6])]");
    }

    @Test
    public void testPattern135() throws Exception {
        parse("[$([SX4](=O)(=O)(O)O),$([SX4+2]([O-])([O-])(O)O)]");
    }

    @Test
    public void testPattern136() throws Exception {
        parse("[$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6]),$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6])]");
    }

    @Test
    public void testPattern137() throws Exception {
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2][#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2][#6])]");
    }

    @Test
    public void testPattern138() throws Exception {
        parse("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2H,OX1H0-]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2H,OX1H0-])]");
    }

    @Test
    public void testPattern139() throws Exception {
        parse("[#16X2][OX2H,OX1H0-]");
    }

    @Test
    public void testPattern140() throws Exception {
        parse("[#16X2][OX2H0]");
    }

    @Test
    public void testPattern141() throws Exception {
        parse("[#6][F,Cl,Br,I]");
    }

    @Test
    public void testPattern142() throws Exception {
        parse("[F,Cl,Br,I]");
    }

    @Test
    public void testPattern143() throws Exception {
        parse("[F,Cl,Br,I].[F,Cl,Br,I].[F,Cl,Br,I]");
    }

    @Test
    public void testPattern144() throws Exception {
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    @Test
    public void testPattern145() throws Exception {
        parse("[$([#6X4@](*)(*)(*)*),$([#6X4@H](*)(*)*)]");
    }

    @Test
    public void testPattern146() throws Exception {
        parse("[$([cX2+](:*):*)]");
    }

    @Test
    public void testPattern147() throws Exception {
        parse("[$([cX3](:*):*),$([cX2+](:*):*)]");
    }

    @Test
    public void testPattern148() throws Exception {
        parse("[$([cX3](:*):*),$([cX2+](:*):*),$([CX3]=*),$([CX2+]=*)]");
    }

    @Test
    public void testPattern149() throws Exception {
        parse("[$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)]");
    }

    @Test
    public void testPattern150() throws Exception {
        parse("[$([#1X1][$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)])]");
    }

    @Test
    public void testPattern151() throws Exception {
        parse("[$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)]");
    }

    @Test
    public void testPattern152() throws Exception {
        parse("[$([#1X1][$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)])]");
    }

    @Test
    public void testPattern153() throws Exception {
        parse("[$([$([NX3]=O),$([NX3+][O-])])]");
    }

    @Test
    public void testPattern154() throws Exception {
        parse("[$([$([NX4]=O),$([NX4+][O-])])]");
    }

    @Test
    public void testPattern155() throws Exception {
        parse("[$([$([NX4]=O),$([NX4+][O-,#0])])]");
    }

    @Test
    public void testPattern156() throws Exception {
        parse("[$([NX4+]),$([NX4]=*)]");
    }

    @Test
    public void testPattern157() throws Exception {
        parse("[$([SX3]=N)]");
    }

    @Test
    public void testPattern158() throws Exception {
        parse("[$([SX1]=[#6])]");
    }

    @Test
    public void testPattern159() throws Exception {
        parse("[$([NX1]#*)]");
    }

    @Test
    public void testPattern160() throws Exception {
        parse("[$([OX2])]");
    }

    @Test
    public void testPattern161() throws Exception {
        parse("[R0;D2][R0;D2][R0;D2][R0;D2]");
    }

    @Test
    public void testPattern162() throws Exception {
        parse("[R0;D2]~[R0;D2]~[R0;D2]~[R0;D2]");
    }

    @Test
    public void testPattern163() throws Exception {
        parse("[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]");
    }

    @Test
    public void testPattern164() throws Exception {
        parse("[!$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }

    @Test
    public void testPattern165() throws Exception {
        parse("[$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }

    @Test
    public void testPattern166() throws Exception {
        parse("[$([SX1]~P)]");
    }

    @Test
    public void testPattern167() throws Exception {
        parse("[$([NX3]C=N)]");
    }

    @Test
    public void testPattern168() throws Exception {
        parse("[$([NX3]N=C)]");
    }

    @Test
    public void testPattern169() throws Exception {
        parse("[$([NX3]N=N)]");
    }

    @Test
    public void testPattern170() throws Exception {
        parse("[$([OX2]C=N)]");
    }

    @Test
    public void testPattern171() throws Exception {
        parse("[!$(*#*)&!D1]-!@[!$(*#*)&!D1]");
    }

    @Test
    public void testPattern172() throws Exception {
        parse("[$([*R2]([*R])([*R])([*R]))].[$([*R2]([*R])([*R])([*R]))]");
    }

    @Test
    public void testPattern173() throws Exception {
        parse("*-!:aa-!:*");
    }

    @Test
    public void testPattern174() throws Exception {
        parse("*-!:aaa-!:*");
    }

    @Test
    public void testPattern175() throws Exception {
        parse("*-!:aaaa-!:*");
    }

    @Test
    public void testPattern176() throws Exception {
        parse("*-!@*");
    }

    @Test
    public void testPattern177() throws Exception { // CIS or TRANS double or aromatic bond in a ring
        parse("*/,\\[R]=,:;@[R]/,\\*");
    }

    @Test
    public void testPattern178() throws Exception { // Fused benzene rings
        parse("c12ccccc1cccc2");
    }

    @Test
    public void testPattern179() throws Exception {
        parse("[r;!r3;!r4;!r5;!r6;!r7]");
    }

    @Test
    public void testPattern180() throws Exception {
        parse("[sX2r5]");
    }

    @Test
    public void testPattern181() throws Exception {
        parse("[oX2r5]");
    }

    @Test
    public void testPattern182() throws Exception { // Unfused benzene ring
        parse("[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }

    @Test
    public void testPattern183() throws Exception { // Multiple non-fused benzene rings
        parse("[cR1]1[cR1][cR1][cR1][cR1][cR1]1.[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }

    @Test
    public void testPattern184() throws Exception { // Generic amino acid: low specificity.
        parse("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
    }

    @Test
    public void testPattern185() throws Exception { //Template for 20 standard a.a.s
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),"
                + "$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX"
                + "4H2][CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    public void testPattern186() throws Exception { // Proline
        parse("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    public void testPattern187() throws Exception { // Glycine
        parse("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    public void testPattern188() throws Exception { // Alanine
        parse("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([CH3X4])[CX3](=[OX1])[OX2H,OX1-,N]");
    }

    @Test
    public void testPattern189() throws Exception { //18_standard_aa_side_chains.
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
    public void testPattern190() throws Exception { // N in Any_standard_amino_acid.
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
    public void testPattern191() throws Exception { // Non-standard amino acid.
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
    public void testPattern192() throws Exception { //Azide group
        parse("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }

    @Test
    public void testPattern193() throws Exception { // Azide ion
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    @Test
    public void testPattern194() throws Exception { //Azide or azide ion
        parse("[$([$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]),$([$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])])]");
    }

    @Test
    public void testPattern195() throws Exception { // Sulfide
        parse("[#16X2H0]");
    }

    @Test
    public void testPattern196() throws Exception { // Mono-sulfide
        parse("[#16X2H0][!#16]");
    }

    @Test
    public void testPattern197() throws Exception { // Di-sulfide
        parse("[#16X2H0][#16X2H0]");
    }

    @Test
    public void testPattern198() throws Exception { // Two sulfides
        parse("[#16X2H0][!#16].[#16X2H0][!#16]");
    }

    @Test
    public void testPattern199() throws Exception { // Acid/conj-base
        parse("[OX2H,OX1H0-]");
    }

    @Test
    public void testPattern200() throws Exception { // Non-acid Oxygen
        parse("[OX2H0]");
    }

    @Test
    public void testPattern201() throws Exception { // Acid/base
        parse("[H1,H0-]");
    }

    @Test
    public void testPattern202() throws Exception {
        parse("([Cl!$(Cl~c)].[c!$(c~Cl)])");
    }

    @Test
    public void testPattern203() throws Exception {
        parse("([Cl]).([c])");
    }

    @Test
    public void testPattern204() throws Exception {
        parse("([Cl].[c])");
    }

    @Test
    public void testPattern205() throws Exception {
        parse("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }

    @Test
    public void testPattern206() throws Exception {
        parse("[#0]");
    }

    @Test
    public void testPattern207() throws Exception {
        parse("[*!H0,#1]");
    }

    @Test
    public void testPattern208() throws Exception {
        parse("[#6!H0,#1]");
    }

    @Test
    public void testPattern209() throws Exception {
        parse("[H,#1]");
    }

    @Test
    public void testPattern210() throws Exception {
        parse("[!H0;F,Cl,Br,I,N+,$([OH]-*=[!#6]),+]");
    }

    @Test
    public void testPattern211() throws Exception {
        parse("[CX3](=O)[OX2H1]");
    }

    @Test
    public void testPattern212() throws Exception {
        parse("[CX3](=O)[OX1H0-,OX2H1]");
    }

    @Test
    public void testPattern213() throws Exception {
        parse("[$([OH]-*=[!#6])]");
    }

    @Test
    public void testPattern214() throws Exception { // Phosphoric_Acid
        parse("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX"
                + "2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }

    @Test
    public void testPattern215() throws Exception { // Sulfonic Acid. High specificity.
        parse("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }

    @Test
    public void testPattern216() throws Exception { // Acyl Halide
        parse("[CX3](=[OX1])[F,Cl,Br,I]");
    }

    @Test
    public void testPattern217() throws Exception {
        parse("[NX2-]");
    }

    @Test
    public void testPattern218() throws Exception {
        parse("[OX2H+]=*");
    }

    @Test
    public void testPattern219() throws Exception {
        parse("[OX3H2+]");
    }

    @Test
    public void testPattern220() throws Exception {
        parse("[#6+]");
    }

    @Test
    public void testPattern221() throws Exception {
        parse("[$([cX2+](:*):*)]");
    }

    @Test
    public void testPattern222() throws Exception {
        parse("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }

    @Test
    public void testPattern223() throws Exception {
        parse("[+1]~*~*~[-1]");
    }

    @Test
    public void testPattern224() throws Exception {
        parse("[$([!-0!-1!-2!-3!-4]~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4])]");
    }

    @Test
    public void testPattern225() throws Exception {
        parse("([!-0!-1!-2!-3!-4].[!+0!+1!+2!+3!+4])");
    }

    @Test
    public void testPattern226() throws Exception { // Hydrogen-bond acceptor, Only hits carbonyl and nitroso
        parse("[#6,#7;R0]=[#8]");
    }

    @Test
    public void testPattern227() throws Exception { // Hydrogen-bond acceptor
        parse("[!$([#6,F,Cl,Br,I,o,s,nX3,#7v5,#15v5,#16v4,#16v6,*+1,*+2,*+3])]");
    }

    @Test
    public void testPattern228() throws Exception {
        parse("[!$([#6,H0,-,-2,-3])]");
    }

    @Test
    public void testPattern229() throws Exception {
        parse("[!H0;#7,#8,#9]");
    }

    @Test
    public void testPattern230() throws Exception {
        parse("[O,N;!H0]-*~*-*=[$([C,N;R0]=O)]");
    }

    @Test
    public void testPattern231() throws Exception {
        parse("[#6;X3v3+0]");
    }

    @Test
    public void testPattern232() throws Exception {
        parse("[#7;X2v4+0]");
    }

    @Test
    public void testPattern233() throws Exception { // Amino Acid
        parse("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2]["
                + "CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }

    @Test
    public void testPattern234() throws Exception {
        parse("[#6][CX3](=O)[$([OX2H0]([#6])[#6]),$([#7])]");
    }

    @Test
    public void testPattern235() throws Exception {
        parse("[#8]=[C,N]-aaa[F,Cl,Br,I]");
    }

    @Test
    public void testPattern236() throws Exception {
        parse("[O,N;!H0;R0]");
    }

    @Test
    public void testPattern237() throws Exception {
        parse("[#8]=[C,N]");
    }

    @Test
    public void testPattern238() throws Exception { // PCB
        parse("[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]-[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]");
    }

    @Test
    public void testPattern239() throws Exception { // Imidazolium Nitrogen
        parse("[nX3r5+]:c:n");
    }

    @Test
    public void testPattern240() throws Exception { // 1-methyl-2-hydroxy benzene with either a Cl or H at the 5 position.
        parse("Cc1:c(O):c:c:[$(cCl),$([cH])]:c1");
    }

    @Test
    public void testPattern241() throws Exception { // Nonstandard atom groups.
        parse("[!#1;!#2;!#3;!#5;!#6;!#7;!#8;!#9;!#11;!#12;!#15;!#16;!#17;!#19;!#20;!#35;!#53]");
    }

    @Test
    public void testRing() throws Exception {
        parse("[$([C;#12]=1CCCCC1)]");
    }

    @Test
    public void testHydrogen() throws Exception {
        parse("[H]");
    }

    @Test
    public void testHybridizationNumber1() throws Exception {
        parse("[^1]");
    }

    @Test
    public void testHybridizationNumber2() throws Exception {
        parse("[^1&N]");
    }

    @Test
    public void testHybridizationNumber3() throws Exception {
        parse("[^1&N,^2&C]");
    }

    @Test(expected = Exception.class)
    public void testHybridizationNumber4() throws Exception {
        parse("[^]");
    }

    @Test(expected = Exception.class)
    public void testHybridizationNumber5() throws Exception {
        parse("[^X]");
    }

    @Test(expected = Exception.class)
    public void testHybridizationNumber6() throws Exception {
        parse("[^0]");
    }

    @Test(expected = Exception.class)
    public void testHybridizationNumber7() throws Exception {
        parse("[^9]");
    }

    @Test
    public void testNonCHHeavyAtom1() throws Exception {
        parse("[#X]");
    }

    @Test
    public void testNonCHHeavyAtom2() throws Exception {
        parse("C#[#X]");
    }

    @Test
    public void testPeriodicGroupNumber1() throws Exception {
        parse("[G14]", Smarts.FLAVOR_CDK_LEGACY);
    }

    @Test
    public void testPeriodicGroupNumber2() throws Exception {
        parse("[G14,G15]", Smarts.FLAVOR_CDK_LEGACY);
    }

    @Test(expected = Exception.class)
    public void testPeriodicGroupNumber3() throws Exception {
        parse("[G19]", Smarts.FLAVOR_CDK_LEGACY);
    }

    @Test(expected = Exception.class)
    public void testPeriodicGroupNumber4() throws Exception {
        parse("[G0]", Smarts.FLAVOR_CDK_LEGACY);
    }

    @Test(expected = Exception.class)
    public void testPeriodicGroupNumber5() throws Exception {
        parse("[G345]", Smarts.FLAVOR_CDK_LEGACY);
    }

    @Test(expected = Exception.class)
    public void testPeriodicGroupNumber6() throws Exception {
        parse("[G]", Smarts.FLAVOR_CDK_LEGACY);
    }

    @Test(expected = Exception.class)
    public void testPeriodicGroupNumber7() throws Exception {
        parse("[GA]");
    }

    @Test
    public void testGroup5Elements() throws Exception {
        parse("[V,Cr,Mn,Nb,Mo,Tc,Ta,W,Re]");
    }
    
    @Test public void endOnSpace() throws Exception {
        parse("C ");
    }

    @Test public void endOnTab() throws Exception {
        parse("C\t");
    }

    @Test public void endOnNewline() throws Exception {
        parse("C\n");
    }

    @Test public void endOnCarriageReturn() throws Exception {
        parse("C\r");
    }

    @Test(expected = Exception.class)
    public void badReaction1() throws Exception {
        parse("C>");
    }

    @Test(expected = Exception.class)
    public void badReaction2() throws Exception {
        parse(">");
    }

    @Test(expected = Exception.class)
    public void badReaction3() throws Exception {
        parse(">C");
    }

    @Test(expected = Exception.class)
    public void badReaction4() throws Exception {
        parse("CC(C>C)C>CC");
    }

    @Test
    public void emptyReaction() throws Exception {
        parse(">>");
    }

    @Test
    public void atomMaps() throws Exception {
        parse("[C&x2X4:1]1-[C&x2X4:2]-[C&x2X4h2]-[N&x2X3h1]-[C&x2X4h2]-[C&x2X4h2]-1");
    }

    @Test
    public void atomMaps2() throws Exception {
        parse("[O!RX2:2]-[C&x2X3h0]1=[C&x2X3h1]-[C&x3X3h0]2=[C&x3X3h0](-[C&x2X4h2]-[C&x2X4h2]-[C&x2X3h0](=[O!RX&h0])-[N&x2X3h1]-2)-[C&x2X3h1]=[C&x2X3h1]-1");
    }

    /**
     * @cdk.bug 909
     */
    @Test public void bug909() throws Exception {
        parse("O=C1NCCSc2ccccc12");
    }

    @Test(expected = InvalidSmarts.class)
    public void testBondPrefix() throws Exception {
        parse("-CCO");
    }

    @Test(expected = InvalidSmarts.class)
    public void trailingBond() throws Exception {
        parse("CCO-");
    }

}
