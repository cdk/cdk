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
import java.io.InputStreamReader;

import com.google.common.io.CharStreams;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.smiles.smarts.parser.SMARTSSearchTest.smarts;
import static org.openscience.cdk.smiles.smarts.parser.SMARTSSearchTest.smiles;

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

    public void match(String smarts, String smiles) throws Exception {
        SMARTSQueryTool sqt = new SMARTSQueryTool(smarts, DefaultChemObjectBuilder.getInstance());
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
        Aromaticity.cdkLegacy().apply(atomContainer);
        boolean status = sqt.matches(atomContainer);
        if (status) {
            nmatch = sqt.countMatches();
            nqmatch = sqt.getUniqueMatchingAtoms().size();
        } else {
            nmatch = 0;
            nqmatch = 0;
        }
    }

    @Test
    public void testRecursiveSmarts1() throws Exception {
        match("[$(*O);$(*CC)]", "O[Po]CC");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts2() throws Exception {
        match("[$(*O);$(*CC)]", "OCCC");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts3() throws Exception {
        match("[$(*O);$(*CC)]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts4() throws Exception {
        match("[$(*O);$(*CC)]", "c1ncccc1C1CCCN1C");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts5() throws Exception {
        match("[$(*O);$(*CC)]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts6() throws Exception {
        match("[$([CX3]=[CX1]),$([CX3+]-[CX1-])]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts7() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "c1ncccc1C1CCCN1C");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts8() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
        Assert.assertEquals(2, nmatch);
        Assert.assertEquals(2, nqmatch);
    }

    @Test
    public void testRecursiveSmarts9() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts10() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CC[C+]([O-])C");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts11() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CCCCC[C+]([O-])CCCC");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts12() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CCCCCC(=O)CCCC");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts13() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "c1c(C)c(O)c(N)cc1");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts14() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "Oc1c(C)cc(N)cc1");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts15() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "Oc1c(C)ccc(N)c1");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts16() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "c1c(C)c(N)c(O)cc1");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts17() throws Exception {
        match("[$(C(=O)O),$(P(=O)),$(S(=O)O)]", "CC(=O)O");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);

        match("[C&$(C(=O)O),P&$(P(=O)),S&$(S(=O)O)]", "CC(=O)O");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts18() throws Exception {
        match("[!$([#6,H0,-,-2,-3])]", "CCNC");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);

        match("[!$([#6,H0,-,-2,-3])]", "CCN(C)C");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts19() throws Exception {
        match("[!H0;#7,#8,#9]", "CCN(C)C");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);

        match("[!H0;#7,#8,#9]", "CC(=O)O");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts20() throws Exception {
        match("[C;D2;$(C(=C)(=C))]", "CCC=C=CC");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts21() throws Exception {
        match("[C;D2;H2;$(C(C)(C))]", "CC(C)CC");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);

        match("[C;D2;H2;$(C(C)(C))]", "CC(C)CCC");
        Assert.assertEquals(2, nmatch);
        Assert.assertEquals(2, nqmatch);
    }

    @Test
    public void testRecursiveSmarts22() throws Exception {
        match("[C;D3;H1;$(C(C)(C)(C))]", "C(C)(C)CC(C)(C)C");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);

        match("[C;D3;H1;$(C(C)(C)(C))]", "C(C)(C)C(C)(C)CC(C)C");
        Assert.assertEquals(2, nmatch);
        Assert.assertEquals(2, nqmatch);

        match("[C;D3;H1;$(C(C)(C)(C))]", "C(C)CC(C)(C)C");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts23() throws Exception {
        match("[C;D2;H2;$(C(C)(C))]", "C(C)CC(C)(C)C");
        Assert.assertEquals(2, nmatch);
        Assert.assertEquals(2, nqmatch);

        match("[C;D2;H2;$(C(C)(C))]", "C(C)(C)C(C)C(C)(C)C");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);

        match("[C;D2;H2;$(C(C)(C))]", "C(C)(C)C(C)C(C)CCCC");
        Assert.assertEquals(3, nmatch);
        Assert.assertEquals(3, nqmatch);

    }

    @Test
    public void testRecursiveSmarts24() throws Exception {
        match("[S;D2;$(S(C)(C))]", "CCSCC");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);

        match("[S;D2;$(S(C)(C))]", "CCS(=O)(=O)CC");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);

        match("[S;D2;$(S(C)(C))]", "CCCCC");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);

    }

    @Test
    public void testRecursiveSmarts25() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "Cc1nc2=NC3=C(C(n2[nH]1)c1cc(cc(c1)F)F)C(=O)CC(C3)c1ccco1");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts34() throws Exception {
        match("[NX3;h2,h1,H1,H2;!$(NC=O)]", "NC1CCCC1C(CCNC)Cc1ccccc1N");
        Assert.assertEquals(3, nmatch);
        Assert.assertEquals(3, nqmatch);
    }

    @Test
    public void testRecursiveSmarts30() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "CC1CCCC(C1)N1CCN(CC1)C1CCN(CC1)Cc1ccccc1");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts31() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "CCOc1cc2c(cc1/C=C/C(=O)c1ccc(cc1)S(=O)(=O)N1CCCC1)OC(C2)C");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts32() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "CN1CCc2cc3c(c(c2C1CC(=O)/C=C/c1ccco1)OC)OCO3");
        Assert.assertEquals(0, nmatch);
        Assert.assertEquals(0, nqmatch);
    }

    @Test
    public void testRecursiveSmarts33() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "Cc1nc2=NC3=C(C(n2[nH]1)c1cc(cc(c1)F)F)C(=O)CC(C3)c1ccco1");
        Assert.assertEquals(1, nmatch);
        Assert.assertEquals(1, nqmatch);
    }

    @Test
    public void testRecursiveSmarts26() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("CCCc1cc(=O)nc([nH]1)S");
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
    }

    @Test
    public void testRecursiveSmarts26_cdkAromaticModel() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("CCCc1cc(=O)nc([nH]1)S");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(1, result[1]);
    }

    @Test
    public void testRecursiveSmarts27_cdkAromaticModel() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("CCCc1nc(c2n1[nH]c(nc2=O)c1cc(ccc1OCC)S(=O)(=O)N1CCN(CC1)CC)C");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(1, result[1]);
    }

    @Test
    public void testRecursiveSmarts27() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("CCCc1nc(c2n1[nH]c(nc2=O)c1cc(ccc1OCC)S(=O)(=O)N1CCN(CC1)CC)C");
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
    }

    @Test
    public void testRecursive28() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("Cc1ccc[n+]2c1[nH]cc(c2=O)c1n[nH]nn1");
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
    }

    @Test
    public void testRecursive28_cdkAromaticModel() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("Cc1ccc[n+]2c1[nH]cc(c2=O)c1n[nH]nn1");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(1, result[1]);
    }

    @Test
    public void testRecursive29() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("Cc1cc(=O)c(c[nH]1)C(=O)NC(c1ccc(cc1)O)C(=O)NC1C(=O)N2C1SCC(=C2C(=O)O)CSc1nnnn1C");
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
    }

    @Test
    public void nestedRecursion() throws Exception {
        assertThat(SMARTSSearchTest.match("[$(*C[$(*C)$(**N)])]", "CCCCN"), is(new int[]{2, 2}));
        assertThat(SMARTSSearchTest.match("[$(*C[$(*C)$(**N)])]", "CCN"), is(new int[]{1, 1}));
    }

    @Test
    public void testRecursive29_cdkAromaticModel() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("Cc1cc(=O)c(c[nH]1)C(=O)NC(c1ccc(cc1)O)C(=O)NC1C(=O)N2C1SCC(=C2C(=O)O)CSc1nnnn1C");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(1, result[1]);
    }

    @Category(SlowTest.class)
    @Test
    public void testBasicAmineOnDrugs_cdkAromaticModel() throws Exception {
        String filename = "drugs.smi";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingSMILESReader reader = new IteratingSMILESReader(ins, DefaultChemObjectBuilder.getInstance());

        SMARTSQueryTool sqt = new SMARTSQueryTool("[NX3;H2,H1;!$(NC=O)]", DefaultChemObjectBuilder.getInstance());
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        int nmatch = 0;
        int nmol = 0;
        READ: while (reader.hasNext()) {
            IAtomContainer container = reader.next();
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);

            // skip un-typed atoms, they can't be run through the CDK aromatic
            // model
            for (IAtom atom : container.atoms()) {
                if (atom.getAtomTypeName() == null) {
                    continue READ;
                }
            }

            if (sqt.matches(container)) {
                nmatch++;
            }
            nmol++;
        }
        reader.close();
        Assert.assertEquals(137, nmol);
        Assert.assertEquals(4, nmatch);
    }

    @Category(SlowTest.class)
    @Test
    public void testBasicAmineOnDrugs() throws Exception {
        String filename = "drugs.smi";
        InputStream ins = this.getClass().getResourceAsStream(filename);

        SMARTSQueryTool sqt = new SMARTSQueryTool("[NX3;H2,H1;!$(NC=O)]", DefaultChemObjectBuilder.getInstance());

        // iterating SMILES reader doesn't allow us to turn off automatic aromaticity
        // perception
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        int nmatch = 0;
        int nmol = 0;
        for (String smi : CharStreams.readLines(new InputStreamReader(ins))) {
            IAtomContainer container = sp.parseSmiles(smi.split("\t")[0]);
            if (sqt.matches(container)) {
                nmatch++;
            }
            nmol++;
        }
        Assert.assertEquals(141, nmol);
        Assert.assertEquals(0, nmatch);
    }

    /**
     * @cdk.bug 1312
     */
    @Test
    public void recursiveComponentGrouping() throws Exception {
        assertThat(SMARTSSearchTest.match("[O;D1;$(([a,A]).([A,a]))][CH]=O", "OC=O.c1ccccc1"), is(new int[]{1, 1}));
        assertThat(SMARTSSearchTest.match("[O;D1;$(([a,A]).([A,a]))][CH]=O", "OC=O"), is(new int[]{0, 0}));
    }

    /**
     * @cdk.bug 844
      */
    @Test
    public void bug844() throws Exception {
        assertThat(SMARTSSearchTest.match("[*R0]-[$([NRD3][CR]=O)]", "N1(CC)C(=O)CCCC1"), is(new int[]{1, 1}));
    }
}
