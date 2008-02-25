package org.openscience.cdk.smiles.smarts.parser.visitor;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.smiles.smarts.parser.ASTStart;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.smiles.smarts.parser.visitor.SmartsQueryVisitor;
import org.openscience.cdk.CDKTestCase;

/**
 * JUnit testing routine for SmartsQueryVisitor
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-05-10
 * @cdk.module test-smarts
 * @cdk.keyword SMARTS 
 */
public class SmartsQueryVisitorTest extends CDKTestCase {
    public SmartsQueryVisitorTest() {}

    public SmartsQueryVisitorTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(SmartsQueryVisitorTest.class);
    }
    
    public void visit(String smarts) throws Exception {
    	SMARTSParser parser = new SMARTSParser(new StringReader(smarts));
    	ASTStart start = parser.Start();
    	SmartsQueryVisitor visitor = new SmartsQueryVisitor();
    	visitor.visit(start, null);
    }
    
    public void testPattern1() throws Exception {
        visit("[CX4]");
    }
    public void testPattern2() throws Exception {
        visit("[$([CX2](=C)=C)]");
    }
    public void testPattern3() throws Exception {
        visit("[$([CX3]=[CX3])]");
    }
    public void testPattern4() throws Exception {
        visit("[$([CX2]#C)]");
    }
    public void testPattern5() throws Exception {
        visit("[CX3]=[OX1]");
    }
    public void testPattern6() throws Exception {
        visit("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]");
    }
    public void testPattern7() throws Exception {
        visit("[CX3](=[OX1])C");
    }
    public void testPattern8() throws Exception {
        visit("[OX1]=CN");
    }
    public void testPattern9() throws Exception {
        visit("[CX3](=[OX1])O");
    }
    public void testPattern10() throws Exception {
        visit("[CX3](=[OX1])[F,Cl,Br,I]");
    }
    public void testPattern11() throws Exception {
        visit("[CX3H1](=O)[#6]");
    }
    public void testPattern12() throws Exception {
        visit("[CX3](=[OX1])[OX2][CX3](=[OX1])");
    }
    public void testPattern13() throws Exception {
        visit("[NX3][CX3](=[OX1])[#6]");
    }
    public void testPattern14() throws Exception {
        visit("[NX3][CX3]=[NX3+]");
    }
    public void testPattern15() throws Exception {
        visit("[NX3,NX4+][CX3](=[OX1])[OX2,OX1-]");
    }
    public void testPattern16() throws Exception {
        visit("[NX3][CX3](=[OX1])[OX2H0]");
    }
    public void testPattern17() throws Exception {
        visit("[NX3,NX4+][CX3](=[OX1])[OX2H,OX1-]");
    }
    public void testPattern18() throws Exception {
        visit("[CX3](=O)[O-]");
    }
    public void testPattern19() throws Exception {
        visit("[CX3](=[OX1])(O)O");
    }
    public void testPattern20() throws Exception {
        visit("[CX3](=[OX1])([OX2])[OX2H,OX1H0-1]");
    }
    public void testPattern21() throws Exception {
        visit("[CX3](=O)[OX2H1]");
    }
    public void testPattern22() throws Exception {
        visit("[CX3](=O)[OX1H0-,OX2H1]");
    }
    public void testPattern23() throws Exception {
        visit("[NX3][CX2]#[NX1]");
    }
    public void testPattern24() throws Exception {
        visit("[#6][CX3](=O)[OX2H0][#6]");
    }
    public void testPattern25() throws Exception {
        visit("[#6][CX3](=O)[#6]");
    }
    public void testPattern26() throws Exception {
        visit("[OD2]([#6])[#6]");
    }
    public void testPattern27() throws Exception {
        visit("[H]");
    }
    public void testPattern28() throws Exception {
        visit("[!#1]");
    }
    public void testPattern29() throws Exception {
        visit("[H+]");
    }
    public void testPattern30() throws Exception {
        visit("[+H]");
    }
    public void testPattern31() throws Exception {
        visit("[NX3;H2,H1;!$(NC=O)]");
    }
    public void testPattern32() throws Exception {
        visit("[NX3][CX3]=[CX3]");
    }
    public void testPattern33() throws Exception {
        visit("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }
    public void testPattern34() throws Exception {
        visit("[NX3][$(C=C),$(cc)]");
    }
    public void testPattern35() throws Exception {
        visit("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]");
    }
    public void testPattern36() throws Exception {
        visit("[NX3H2,NH3X4+][CX4H]([*])[CX3](=[OX1])[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-]");
    }
    public void testPattern37() throws Exception {
        visit("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N]");
    }
    public void testPattern38() throws Exception {
        visit("[CH3X4]");
    }
    public void testPattern39() throws Exception {
        visit("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }
    public void testPattern40() throws Exception {
        visit("[CH2X4][CX3](=[OX1])[NX3H2]");
    }
    public void testPattern41() throws Exception {
        visit("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }
    public void testPattern42() throws Exception {
        visit("[CH2X4][SX2H,SX1H0-]");
    }
    public void testPattern43() throws Exception {
        visit("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }
    public void testPattern44() throws Exception {
        visit("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }
    public void testPattern45() throws Exception {
        visit("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }
    public void testPattern47() throws Exception {
        visit("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }
    public void testPattern48() throws Exception {
        visit("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }
    public void testPattern49() throws Exception {
        visit("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }
    public void testPattern50() throws Exception {
        visit("[CH2X4][CH2X4][SX2][CH3X4]");
    }
    public void testPattern51() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }
    public void testPattern52() throws Exception {
        visit("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }
    public void testPattern53() throws Exception {
        visit("[CH2X4][OX2H]");
    }
    public void testPattern54() throws Exception {
        visit("[NX3][CX3]=[SX1]");
    }
    public void testPattern55() throws Exception {
        visit("[CHX4]([CH3X4])[OX2H]");
    }
    public void testPattern56() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }
    public void testPattern57() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }
    public void testPattern58() throws Exception {
        visit("[CHX4]([CH3X4])[CH3X4]");
    }
    public void testPattern59() throws Exception {
        visit("[CH3X4]");
    }
    public void testPattern60() throws Exception {
        visit("[CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3]");
    }
    public void testPattern61() throws Exception {
        visit("[CH2X4][CX3](=[OX1])[NX3H2]");
    }
    public void testPattern62() throws Exception {
        visit("[CH2X4][CX3](=[OX1])[OH0-,OH]");
    }
    public void testPattern63() throws Exception {
        visit("[CH2X4][SX2H,SX1H0-]");
    }
    public void testPattern64() throws Exception {
        visit("[CH2X4][CH2X4][CX3](=[OX1])[OH0-,OH]");
    }
    public void testPattern65() throws Exception {
        visit("[CH2X4][#6X3]1:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]:[$([#7X3H+,#7X2H0+0]:[#6X3H]:[#7X3H]),$([#7X3H])]:[#6X3H]1");
    }
    public void testPattern67() throws Exception {
        visit("[CHX4]([CH3X4])[CH2X4][CH3X4]");
    }
    public void testPattern68() throws Exception {
        visit("[CH2X4][CHX4]([CH3X4])[CH3X4]");
    }
    public void testPattern69() throws Exception {
        visit("[CH2X4][CH2X4][CH2X4][CH2X4][NX4+,NX3+0]");
    }
    public void testPattern70() throws Exception {
        visit("[CH2X4][CH2X4][SX2][CH3X4]");
    }
    public void testPattern71() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][cX3H][cX3H][cX3H][cX3H]1");
    }
    public void testPattern72() throws Exception {
        visit("[CH2X4][OX2H]");
    }
    public void testPattern73() throws Exception {
        visit("[CHX4]([CH3X4])[OX2H]");
    }
    public void testPattern74() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][nX3H][cX3]2[cX3H][cX3H][cX3H][cX3H][cX3]12");
    }
    public void testPattern75() throws Exception {
        visit("[CH2X4][cX3]1[cX3H][cX3H][cX3]([OHX2,OH0X1-])[cX3H][cX3H]1");
    }
    public void testPattern76() throws Exception {
        visit("[CHX4]([CH3X4])[CH3X4]");
    }
    public void testPattern77() throws Exception {
        visit("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }
    public void testPattern78() throws Exception {
        visit("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }
    public void testPattern79() throws Exception {
        visit("[#7]");
    }
    public void testPattern80() throws Exception {
        visit("[NX2]=N");
    }
    public void testPattern81() throws Exception {
        visit("[NX2]=[NX2]");
    }
    public void testPattern82() throws Exception {
        visit("[$([NX2]=[NX3+]([O-])[#6]),$([NX2]=[NX3+0](=[O])[#6])]");
    }
    public void testPattern83() throws Exception {
        visit("[$([#6]=[N+]=[N-]),$([#6-]-[N+]#[N])]");
    }
    public void testPattern84() throws Exception {
        visit("[$([nr5]:[nr5,or5,sr5]),$([nr5]:[cr5]:[nr5,or5,sr5])]");
    }
    public void testPattern85() throws Exception {
        visit("[NX3][NX3]");
    }
    public void testPattern86() throws Exception {
        visit("[NX3][NX2]=[*]");
    }
    public void testPattern87() throws Exception {
        visit("[CX3;$([C]([#6])[#6]),$([CH][#6])]=[NX2][#6]");
    }
    public void testPattern88() throws Exception {
        visit("[$([CX3]([#6])[#6]),$([CX3H][#6])]=[$([NX2][#6]),$([NX2H])]");
    }
    public void testPattern89() throws Exception {
        visit("[NX3+]=[CX3]");
    }
    public void testPattern90() throws Exception {
        visit("[CX3](=[OX1])[NX3H][CX3](=[OX1])");
    }
    public void testPattern91() throws Exception {
        visit("[CX3](=[OX1])[NX3H0]([#6])[CX3](=[OX1])");
    }
    public void testPattern92() throws Exception {
        visit("[CX3](=[OX1])[NX3H0]([NX3H0]([CX3](=[OX1]))[CX3](=[OX1]))[CX3](=[OX1])");
    }
    public void testPattern93() throws Exception {
        visit("[$([NX3](=[OX1])(=[OX1])O),$([NX3+]([OX1-])(=[OX1])O)]");
    }
    public void testPattern94() throws Exception {
        visit("[$([OX1]=[NX3](=[OX1])[OX1-]),$([OX1]=[NX3+]([OX1-])[OX1-])]");
    }
    public void testPattern95() throws Exception {
        visit("[NX1]#[CX2]");
    }
    public void testPattern96() throws Exception {
        visit("[CX1-]#[NX2+]");
    }
    public void testPattern97() throws Exception {
        visit("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }
    public void testPattern98() throws Exception {
        visit("[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8].[$([NX3](=O)=O),$([NX3+](=O)[O-])][!#8]");
    }
    public void testPattern99() throws Exception {
        visit("[NX2]=[OX1]");
    }
    public void testPattern101() throws Exception {
        visit("[$([#7+][OX1-]),$([#7v5]=[OX1]);!$([#7](~[O])~[O]);!$([#7]=[#7])]");
    }
    public void testPattern102() throws Exception {
        visit("[OX2H]");
    }
    public void testPattern103() throws Exception {
        visit("[#6][OX2H]");
    }
    public void testPattern104() throws Exception {
        visit("[OX2H][CX3]=[OX1]");
    }
    public void testPattern105() throws Exception {
        visit("[OX2H]P");
    }
    public void testPattern106() throws Exception {
        visit("[OX2H][#6X3]=[#6]");
    }
    public void testPattern107() throws Exception {
        visit("[OX2H][cX3]:[c]");
    }
    public void testPattern108() throws Exception {
        visit("[OX2H][$(C=C),$(cc)]");
    }
    public void testPattern109() throws Exception {
        visit("[$([OH]-*=[!#6])]");
    }
    public void testPattern110() throws Exception {
        visit("[OX2,OX1-][OX2,OX1-]");
    }
    public void testPattern111() throws Exception { // Phosphoric_acid groups.
        visit("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX" +
        		"2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }
    public void testPattern112() throws Exception { // Phosphoric_ester groups.
        visit("[$(P(=[OX1])([OX2][#6])([$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)]),$([P+]([OX1-])([OX2][#6])([" +
        		"$([OX2H]),$([OX1-]),$([OX2][#6])])[$([OX2H]),$([OX1-]),$([OX2][#6]),$([OX2]P)])]");
    }
    public void testPattern113() throws Exception {
        visit("[S-][CX3](=S)[#6]");
    }
    public void testPattern114() throws Exception {
        visit("[#6X3](=[SX1])([!N])[!N]");
    }
    public void testPattern115() throws Exception {
        visit("[SX2]");
    }
    public void testPattern116() throws Exception {
        visit("[#16X2H]");
    }
    public void testPattern117() throws Exception {
        visit("[#16!H0]");
    }
    public void testPattern118() throws Exception {
        visit("[NX3][CX3]=[SX1]");
    }
    public void testPattern119() throws Exception {
        visit("[#16X2H0]");
    }
    public void testPattern120() throws Exception {
        visit("[#16X2H0][!#16]");
    }
    public void testPattern121() throws Exception {
        visit("[#16X2H0][#16X2H0]");
    }
    public void testPattern122() throws Exception {
        visit("[#16X2H0][!#16].[#16X2H0][!#16]");
    }
    public void testPattern123() throws Exception {
        visit("[$([#16X3](=[OX1])[OX2H0]),$([#16X3+]([OX1-])[OX2H0])]");
    }
    public void testPattern124() throws Exception {
        visit("[$([#16X3](=[OX1])[OX2H,OX1H0-]),$([#16X3+]([OX1-])[OX2H,OX1H0-])]");
    }
    public void testPattern125() throws Exception {
        visit("[$([#16X4](=[OX1])=[OX1]),$([#16X4+2]([OX1-])[OX1-])]");
    }
    public void testPattern126() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([#6])[#6]),$([#16X4+2]([OX1-])([OX1-])([#6])[#6])]");
    }
    public void testPattern127() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }
    public void testPattern128() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H0]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H0])]");
    }
    public void testPattern129() throws Exception {
        visit("[$([#16X4]([NX3])(=[OX1])(=[OX1])[#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[#6])]");
    }
    public void testPattern130() throws Exception {
        visit("[SX4](C)(C)(=O)=N");
    }
    public void testPattern131() throws Exception {
        visit("[$([SX4](=[OX1])(=[OX1])([!O])[NX3]),$([SX4+2]([OX1-])([OX1-])([!O])[NX3])]");
    }
    public void testPattern132() throws Exception {
        visit("[$([#16X3]=[OX1]),$([#16X3+][OX1-])]");
    }
    public void testPattern133() throws Exception {
        visit("[$([#16X3](=[OX1])([#6])[#6]),$([#16X3+]([OX1-])([#6])[#6])]");
    }
    public void testPattern134() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([OX2H,OX1H0-])[OX2][#6]),$([#16X4+2]([OX1-])([OX1-])([OX2H,OX1H0-])[OX2][#6])]");
    }
    public void testPattern135() throws Exception {
        visit("[$([SX4](=O)(=O)(O)O),$([SX4+2]([O-])([O-])(O)O)]");
    }
    public void testPattern136() throws Exception {
        visit("[$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6]),$([#16X4](=[OX1])(=[OX1])([OX2][#6])[OX2][#6])]");
    }
    public void testPattern137() throws Exception {
        visit("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2][#6]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2][#6])]");
    }
    public void testPattern138() throws Exception {
        visit("[$([#16X4]([NX3])(=[OX1])(=[OX1])[OX2H,OX1H0-]),$([#16X4+2]([NX3])([OX1-])([OX1-])[OX2H,OX1H0-])]");
    }
    public void testPattern139() throws Exception {
        visit("[#16X2][OX2H,OX1H0-]");
    }
    public void testPattern140() throws Exception {
        visit("[#16X2][OX2H0]");
    }
    public void testPattern141() throws Exception {
        visit("[#6][F,Cl,Br,I]");
    }
    public void testPattern142() throws Exception {
        visit("[F,Cl,Br,I]");
    }
    public void testPattern143() throws Exception {
        visit("[F,Cl,Br,I].[F,Cl,Br,I].[F,Cl,Br,I]");
    }
    public void testPattern144() throws Exception {
        visit("[CX3](=[OX1])[F,Cl,Br,I]");
    }
    public void testPattern145() throws Exception {
        visit("[$([#6X4@](*)(*)(*)*),$([#6X4@H](*)(*)*)]");
    }
    public void testPattern146() throws Exception {
        visit("[$([cX2+](:*):*)]");
    }
    public void testPattern147() throws Exception {
        visit("[$([cX3](:*):*),$([cX2+](:*):*)]");
    }
    public void testPattern148() throws Exception {
        visit("[$([cX3](:*):*),$([cX2+](:*):*),$([CX3]=*),$([CX2+]=*)]");
    }
    public void testPattern149() throws Exception {
        visit("[$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)]");
    }
    public void testPattern150() throws Exception {
        visit("[$([#1X1][$([nX3](:*):*),$([nX2](:*):*),$([#7X2]=*),$([NX3](=*)=*),$([#7X3+](-*)=*),$([#7X3+H]=*)])]");
    }
    public void testPattern151() throws Exception {
        visit("[$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)]");
    }
    public void testPattern152() throws Exception {
        visit("[$([#1X1][$([NX4+]),$([NX3]);!$(*=*)&!$(*:*)])]");
    }
    public void testPattern153() throws Exception {
        visit("[$([$([NX3]=O),$([NX3+][O-])])]");
    }
    public void testPattern154() throws Exception {
        visit("[$([$([NX4]=O),$([NX4+][O-])])]");
    }
    public void testPattern155() throws Exception {
        visit("[$([$([NX4]=O),$([NX4+][O-,#0])])]");
    }
    public void testPattern156() throws Exception {
        visit("[$([NX4+]),$([NX4]=*)]");
    }
    public void testPattern157() throws Exception {
        visit("[$([SX3]=N)]");
    }
    public void testPattern158() throws Exception {
        visit("[$([SX1]=[#6])]");
    }
    public void testPattern159() throws Exception {
        visit("[$([NX1]#*)]");
    }
    public void testPattern160() throws Exception {
        visit("[$([OX2])]");
    }
    public void testPattern161() throws Exception {
        visit("[R0;D2][R0;D2][R0;D2][R0;D2]");
    }
    public void testPattern162() throws Exception {
        visit("[R0;D2]~[R0;D2]~[R0;D2]~[R0;D2]");
    }
    public void testPattern163() throws Exception {
        visit("[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]~[AR0]");
    }
    public void testPattern164() throws Exception {
        visit("[!$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }
    public void testPattern165() throws Exception {
        visit("[$([#6+0]);!$(C(F)(F)F);!$(c(:[!c]):[!c])!$([#6]=,#[!#6])]");
    }
    public void testPattern166() throws Exception {
        visit("[$([SX1]~P)]");
    }
    public void testPattern167() throws Exception {
        visit("[$([NX3]C=N)]");
    }
    public void testPattern168() throws Exception {
        visit("[$([NX3]N=C)]");
    }
    public void testPattern169() throws Exception {
        visit("[$([NX3]N=N)]");
    }
    public void testPattern170() throws Exception {
        visit("[$([OX2]C=N)]");
    }
    public void testPattern171() throws Exception {
        visit("[!$(*#*)&!D1]-!@[!$(*#*)&!D1]");
    }
    public void testPattern172() throws Exception {
        visit("[$([*R2]([*R])([*R])([*R]))].[$([*R2]([*R])([*R])([*R]))]");
    }
    public void testPattern173() throws Exception {
        visit("*-!:aa-!:*");
    }
    public void testPattern174() throws Exception {
        visit("*-!:aaa-!:*");
    }
    public void testPattern175() throws Exception {
        visit("*-!:aaaa-!:*");
    }
    public void testPattern176() throws Exception {
        visit("*-!@*");
    } 
    public void testPattern177() throws Exception { // CIS or TRANS double or aromatic bond in a ring
        visit("*/,\\[R]=,:;@[R]/,\\*");
    }
    public void testPattern178() throws Exception { // Fused benzene rings
        visit("c12ccccc1cccc2");
    }
    public void testPattern179() throws Exception {
        visit("[r;!r3;!r4;!r5;!r6;!r7]");
    }
    public void testPattern180() throws Exception {
        visit("[sX2r5]");
    }
    public void testPattern181() throws Exception {
        visit("[oX2r5]");
    }
    public void testPattern182() throws Exception { // Unfused benzene ring
        visit("[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }
    public void testPattern183() throws Exception { // Multiple non-fused benzene rings
        visit("[cR1]1[cR1][cR1][cR1][cR1][cR1]1.[cR1]1[cR1][cR1][cR1][cR1][cR1]1");
    }
    public void testPattern184() throws Exception { // Generic amino acid: low specificity.
        visit("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]"); 
    }
    public void testPattern185() throws Exception { //Template for 20 standard a.a.s
        visit("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N])," +
        		"$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX" +
        		"4H2][CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }
    public void testPattern186() throws Exception { // Proline
        visit("[$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]");
    }
    public void testPattern187() throws Exception { // Glycine
        visit("[$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][CX3](=[OX1])[OX2H,OX1-,N])]");
    }
    public void testPattern188() throws Exception {  // Alanine
        visit("[$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([CH3X4])[CX3](=[OX1])[OX2H,OX1-,N]");
    }
    public void testPattern189() throws Exception { //18_standard_aa_side_chains.
        visit("([$([CH3X4]),$([CH2X4][CH2X4][CH2X4][NHX3][CH0X3](=[NH2X3+,NHX2+0])[NH2X3])," + 
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
    	visit("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3]" + 
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
        visit("[$([NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]);!$([$([$([NX3H,NX4H2+])," + 
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
        visit("[$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]");
    }
    public void testPattern193() throws Exception { // Azide ion
        visit("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }
    public void testPattern194() throws Exception { //Azide or azide ion
        visit("[$([$(*-[NX2-]-[NX2+]#[NX1]),$(*-[NX2]=[NX2+]=[NX1-])]),$([$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])])]");
    }

    public void testPattern195() throws Exception { // Sulfide
        visit("[#16X2H0]");
    }
    public void testPattern196() throws Exception { // Mono-sulfide
        visit("[#16X2H0][!#16]");
    }
    public void testPattern197() throws Exception { // Di-sulfide
        visit("[#16X2H0][#16X2H0]");
    }
    public void testPattern198() throws Exception { // Two sulfides
        visit("[#16X2H0][!#16].[#16X2H0][!#16]");
    }
    public void testPattern199() throws Exception { // Acid/conj-base
        visit("[OX2H,OX1H0-]");
    }
    public void testPattern200() throws Exception { // Non-acid Oxygen
        visit("[OX2H0]");
    }
    public void testPattern201() throws Exception { // Acid/base
        visit("[H1,H0-]");
    }
    public void testPattern202() throws Exception {
        visit("([Cl!$(Cl~c)].[c!$(c~Cl)])");
    }
    public void testPattern203() throws Exception {
        visit("([Cl]).([c])");
    }
    public void testPattern204() throws Exception {
        visit("([Cl].[c])");
    }
    public void testPattern205() throws Exception {
        visit("[NX3;H2,H1;!$(NC=O)].[NX3;H2,H1;!$(NC=O)]");
    }
    public void testPattern206() throws Exception {
        visit("[#0]");
    }
    public void testPattern207() throws Exception {
        visit("[*!H0,#1]");
    }
    public void testPattern208() throws Exception {
        visit("[#6!H0,#1]");
    }
    public void testPattern209() throws Exception {
        visit("[H,#1]");
    }
    public void testPattern210() throws Exception {
        visit("[!H0;F,Cl,Br,I,N+,$([OH]-*=[!#6]),+]");
    }
    public void testPattern211() throws Exception {
        visit("[CX3](=O)[OX2H1]");
    }
    public void testPattern212() throws Exception {
        visit("[CX3](=O)[OX1H0-,OX2H1]");
    }
    public void testPattern213() throws Exception {
        visit("[$([OH]-*=[!#6])]");
    }
    public void testPattern214() throws Exception { // Phosphoric_Acid 
        visit("[$(P(=[OX1])([$([OX2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)]),$([P+]([OX1-])([$([OX" +
        		"2H]),$([OX1-]),$([OX2]P)])([$([OX2H]),$([OX1-]),$([OX2]P)])[$([OX2H]),$([OX1-]),$([OX2]P)])]");
    }
    public void testPattern215() throws Exception { // Sulfonic Acid. High specificity.
        visit("[$([#16X4](=[OX1])(=[OX1])([#6])[OX2H,OX1H0-]),$([#16X4+2]([OX1-])([OX1-])([#6])[OX2H,OX1H0-])]");
    }
    public void testPattern216() throws Exception { // Acyl Halide
        visit("[CX3](=[OX1])[F,Cl,Br,I]");
    }
    public void testPattern217() throws Exception {
        visit("[NX2-]");
    }
    public void testPattern218() throws Exception {
        visit("[OX2H+]=*");
    }
    public void testPattern219() throws Exception {
        visit("[OX3H2+]");
    }
    public void testPattern220() throws Exception {
        visit("[#6+]");
    }
    public void testPattern221() throws Exception {
        visit("[$([cX2+](:*):*)]");
    }
    public void testPattern222() throws Exception {
        visit("[$([NX1-]=[NX2+]=[NX1-]),$([NX1]#[NX2+]-[NX1-2])]");
    }
    public void testPattern223() throws Exception {
        visit("[+1]~*~*~[-1]");
    }
    public void testPattern224() throws Exception {
        visit("[$([!-0!-1!-2!-3!-4]~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4]),$([!-0!-1!-2!-3!-4]~*~*~*~*~*~*~*~*~*~[!+0!+1!+2!+3!+4])]");
    }
    public void testPattern225() throws Exception {
        visit("([!-0!-1!-2!-3!-4].[!+0!+1!+2!+3!+4])");
    }
    public void testPattern226() throws Exception { // Hydrogen-bond acceptor, Only hits carbonyl and nitroso
        visit("[#6,#7;R0]=[#8]");
    }
    public void testPattern227() throws Exception { // Hydrogen-bond acceptor
        visit("[!$([#6,F,Cl,Br,I,o,s,nX3,#7v5,#15v5,#16v4,#16v6,*+1,*+2,*+3])]");
    }
    public void testPattern228() throws Exception {
        visit("[!$([#6,H0,-,-2,-3])]");
    }
    public void testPattern229() throws Exception {
        visit("[!H0;#7,#8,#9]");
    }
    public void testPattern230() throws Exception {
        visit("[O,N;!H0]-*~*-*=[$([C,N;R0]=O)]");
    }
    public void testPattern231() throws Exception {
        visit("[#6;X3v3+0]");
    }
    public void testPattern232() throws Exception {
        visit("[#7;X2v4+0]");
    }
    public void testPattern233() throws Exception { // Amino Acid
        visit("[$([$([NX3H,NX4H2+]),$([NX3](C)(C)(C))]1[CX4H]([CH2][CH2][CH2]1)[CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H2][" + 
        		"CX3](=[OX1])[OX2H,OX1-,N]),$([$([NX3H2,NX4H3+]),$([NX3H](C)(C))][CX4H]([*])[CX3](=[OX1])[OX2H,OX1-,N])]");
    }
    public void testPattern234() throws Exception {
        visit("[#6][CX3](=O)[$([OX2H0]([#6])[#6]),$([#7])]");
    }
    public void testPattern235() throws Exception {
        visit("[#8]=[C,N]-aaa[F,Cl,Br,I]");
    }
    public void testPattern236() throws Exception {
        visit("[O,N;!H0;R0]");
    }
    public void testPattern237() throws Exception {
        visit("[#8]=[C,N]");
    }
    public void testPattern238() throws Exception { // PCB
        visit("[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]-[$(c:cCl),$(c:c:cCl),$(c:c:c:cCl)]");
    }
    public void testPattern239() throws Exception { // Imidazolium Nitrogen
        visit("[nX3r5+]:c:n");
    }
    public void testPattern240() throws Exception { // 1-methyl-2-hydroxy benzene with either a Cl or H at the 5 position.
        visit("[c;$([*Cl]),$([*H1])]1ccc(O)c(C)c1");
        visit("Cc1:c(O):c:c:[$(cCl),$([cH])]:c1");
    }
    public void testPattern241() throws Exception { // Nonstandard atom groups.
        visit("[!#1;!#2;!#3;!#5;!#6;!#7;!#8;!#9;!#11;!#12;!#15;!#16;!#17;!#19;!#20;!#35;!#53]");
    } 
    public void testRing() throws Exception {
    	visit("[$([C;#12]=1CCCCC1)]");
    }    
    

}