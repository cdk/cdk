/* $Revision: 8245 $ $Author: djiao $ $Date: 2007-04-22 17:20:57 -0400 (Sun, 22 Apr 2007) $
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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Test recursive smarts
 *
 * @author Dazhi Jiao
 * @cdk.module test-smarts
 * @cdk.require ant1.6
 */
public class RecursiveTest extends CDKTestCase {
    private int nmatch;
    private int nqmatch;

    public RecursiveTest() {
    }

    public RecursiveTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(RecursiveTest.class);
    }

    public void match(String smarts, String smiles) throws Exception {
        SMARTSQueryTool sqt = new SMARTSQueryTool(smarts, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(smiles);
        boolean status = sqt.matches(atomContainer);
        if (status) {
            nmatch = sqt.countMatches();
            nqmatch = sqt.getUniqueMatchingAtoms().size();
        } else {
            nmatch = 0;
            nqmatch = 0;
        }
    }

    public void testRecursiveSmarts1() throws Exception {
        match("[$(*O);$(*CC)]", "O[Po]CC");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts2() throws Exception {
        match("[$(*O);$(*CC)]", "OCCC");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts3() throws Exception {
        match("[$(*O);$(*CC)]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);
    }

    public void testRecursiveSmarts4() throws Exception {
        match("[$(*O);$(*CC)]", "c1ncccc1C1CCCN1C");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);
    }

    public void testRecursiveSmarts5() throws Exception {
        match("[$(*O);$(*CC)]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts6() throws Exception {
        match("[$([CX3]=[CX1]),$([CX3+]-[CX1-])]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);
    }

    public void testRecursiveSmarts7() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "c1ncccc1C1CCCN1C");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);
    }

    public void testRecursiveSmarts8() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
        assertEquals(2, nmatch);
        assertEquals(2, nqmatch);
    }

    public void testRecursiveSmarts9() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts10() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CC[C+]([O-])C");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts11() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CCCCC[C+]([O-])CCCC");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts12() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CCCCCC(=O)CCCC");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts13() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "c1c(C)c(O)c(N)cc1");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts14() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "Oc1c(C)cc(N)cc1");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts15() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "Oc1c(C)ccc(N)c1");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);
    }

    public void testRecursiveSmarts16() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "c1c(C)c(N)c(O)cc1");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);
    }

    public void testRecursiveSmarts17() throws Exception {
        match("[$(C(=O)O),$(P(=O)),$(S(=O)O)]", "CC(=O)O");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);

        match("[C&$(C(=O)O),P&$(P(=O)),S&$(S(=O)O)]", "CC(=O)O");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts18() throws Exception {
        match("[!$([#6,H0,-,-2,-3])]", "CCNC");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);

        match("[!$([#6,H0,-,-2,-3])]", "CCN(C)C");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);
    }


    public void testRecursiveSmarts19() throws Exception {
        match("[!H0;#7,#8,#9]", "CCN(C)C");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);

        match("[!H0;#7,#8,#9]", "CC(=O)O");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts20() throws Exception {
        match("[C;D2;$(C(=C)(=C))]", "CCC=C=CC");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);
    }

    public void testRecursiveSmarts21() throws Exception {
        match("[C;D2;H2;$(C(C)(C))]", "CC(C)CC");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);

        match("[C;D2;H2;$(C(C)(C))]", "CC(C)CCC");
        assertEquals(2, nmatch);
        assertEquals(2, nqmatch);
    }

    public void testRecursiveSmarts22() throws Exception {
        match("[C;D3;H1;$(C(C)(C)(C))]", "C(C)(C)CC(C)(C)C");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);

        match("[C;D3;H1;$(C(C)(C)(C))]", "C(C)(C)C(C)(C)CC(C)C");
        assertEquals(2, nmatch);
        assertEquals(2, nqmatch);

        match("[C;D3;H1;$(C(C)(C)(C))]", "C(C)CC(C)(C)C");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);
    }

    public void testRecursiveSmarts23() throws Exception {
        match("[C;D2;H2;$(C(C)(C))]", "C(C)CC(C)(C)C");
        assertEquals(2, nmatch);
        assertEquals(2, nqmatch);

        match("[C;D2;H2;$(C(C)(C))]", "C(C)(C)C(C)C(C)(C)C");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);

        match("[C;D2;H2;$(C(C)(C))]", "C(C)(C)C(C)C(C)CCCC");
        assertEquals(3, nmatch);
        assertEquals(3, nqmatch);

    }

    public void testRecursiveSmarts24() throws Exception {
        match("[S;D2;$(S(C)(C))]", "CCSCC");
        assertEquals(1, nmatch);
        assertEquals(1, nqmatch);

        match("[S;D2;$(S(C)(C))]", "CCS(=O)(=O)CC");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);

        match("[S;D2;$(S(C)(C))]", "CCCCC");
        assertEquals(0, nmatch);
        assertEquals(0, nqmatch);

    }

}
