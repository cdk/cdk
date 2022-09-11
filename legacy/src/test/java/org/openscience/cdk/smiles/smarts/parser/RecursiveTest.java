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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
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
class RecursiveTest extends CDKTestCase {

    private int nmatch;
    private int nqmatch;

    void match(String smarts, String smiles) throws Exception {
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
    void testRecursiveSmarts1() throws Exception {
        match("[$(*O);$(*CC)]", "O[Po]CC");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts2() throws Exception {
        match("[$(*O);$(*CC)]", "OCCC");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts3() throws Exception {
        match("[$(*O);$(*CC)]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts4() throws Exception {
        match("[$(*O);$(*CC)]", "c1ncccc1C1CCCN1C");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts5() throws Exception {
        match("[$(*O);$(*CC)]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts6() throws Exception {
        match("[$([CX3]=[CX1]),$([CX3+]-[CX1-])]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts7() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "c1ncccc1C1CCCN1C");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts8() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
        Assertions.assertEquals(2, nmatch);
        Assertions.assertEquals(2, nqmatch);
    }

    @Test
    void testRecursiveSmarts9() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts10() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CC[C+]([O-])C");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts11() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CCCCC[C+]([O-])CCCC");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts12() throws Exception {
        match("[$([CX3]=[OX1]),$([CX3+]-[OX1-])]", "CCCCCC(=O)CCCC");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts13() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "c1c(C)c(O)c(N)cc1");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts14() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "Oc1c(C)cc(N)cc1");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts15() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "Oc1c(C)ccc(N)c1");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts16() throws Exception {
        match("[$([C]aaO);$([C]aaaN)]", "c1c(C)c(N)c(O)cc1");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts17() throws Exception {
        match("[$(C(=O)O),$(P(=O)),$(S(=O)O)]", "CC(=O)O");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);

        match("[C&$(C(=O)O),P&$(P(=O)),S&$(S(=O)O)]", "CC(=O)O");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts18() throws Exception {
        match("[!$([#6,H0,-,-2,-3])]", "CCNC");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);

        match("[!$([#6,H0,-,-2,-3])]", "CCN(C)C");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts19() throws Exception {
        match("[!H0;#7,#8,#9]", "CCN(C)C");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);

        match("[!H0;#7,#8,#9]", "CC(=O)O");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts20() throws Exception {
        match("[C;D2;$(C(=C)(=C))]", "CCC=C=CC");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts21() throws Exception {
        match("[C;D2;H2;$(C(C)(C))]", "CC(C)CC");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);

        match("[C;D2;H2;$(C(C)(C))]", "CC(C)CCC");
        Assertions.assertEquals(2, nmatch);
        Assertions.assertEquals(2, nqmatch);
    }

    @Test
    void testRecursiveSmarts22() throws Exception {
        match("[C;D3;H1;$(C(C)(C)(C))]", "C(C)(C)CC(C)(C)C");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);

        match("[C;D3;H1;$(C(C)(C)(C))]", "C(C)(C)C(C)(C)CC(C)C");
        Assertions.assertEquals(2, nmatch);
        Assertions.assertEquals(2, nqmatch);

        match("[C;D3;H1;$(C(C)(C)(C))]", "C(C)CC(C)(C)C");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts23() throws Exception {
        match("[C;D2;H2;$(C(C)(C))]", "C(C)CC(C)(C)C");
        Assertions.assertEquals(2, nmatch);
        Assertions.assertEquals(2, nqmatch);

        match("[C;D2;H2;$(C(C)(C))]", "C(C)(C)C(C)C(C)(C)C");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);

        match("[C;D2;H2;$(C(C)(C))]", "C(C)(C)C(C)C(C)CCCC");
        Assertions.assertEquals(3, nmatch);
        Assertions.assertEquals(3, nqmatch);

    }

    @Test
    void testRecursiveSmarts24() throws Exception {
        match("[S;D2;$(S(C)(C))]", "CCSCC");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);

        match("[S;D2;$(S(C)(C))]", "CCS(=O)(=O)CC");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);

        match("[S;D2;$(S(C)(C))]", "CCCCC");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);

    }

    @Test
    void testRecursiveSmarts25() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "Cc1nc2=NC3=C(C(n2[nH]1)c1cc(cc(c1)F)F)C(=O)CC(C3)c1ccco1");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts34() throws Exception {
        match("[NX3;h2,h1,H1,H2;!$(NC=O)]", "NC1CCCC1C(CCNC)Cc1ccccc1N");
        Assertions.assertEquals(3, nmatch);
        Assertions.assertEquals(3, nqmatch);
    }

    @Test
    void testRecursiveSmarts30() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "CC1CCCC(C1)N1CCN(CC1)C1CCN(CC1)Cc1ccccc1");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts31() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "CCOc1cc2c(cc1/C=C/C(=O)c1ccc(cc1)S(=O)(=O)N1CCCC1)OC(C2)C");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts32() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "CN1CCc2cc3c(c(c2C1CC(=O)/C=C/c1ccco1)OC)OCO3");
        Assertions.assertEquals(0, nmatch);
        Assertions.assertEquals(0, nqmatch);
    }

    @Test
    void testRecursiveSmarts33() throws Exception {
        match("[NX3;H2,H1;!$(NC=O)]", "Cc1nc2=NC3=C(C(n2[nH]1)c1cc(cc(c1)F)F)C(=O)CC(C3)c1ccco1");
        Assertions.assertEquals(1, nmatch);
        Assertions.assertEquals(1, nqmatch);
    }

    @Test
    void testRecursiveSmarts26() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("CCCc1cc(=O)nc([nH]1)S");
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assertions.assertEquals(0, result[0]);
        Assertions.assertEquals(0, result[1]);
    }

    @Test
    void testRecursiveSmarts26_cdkAromaticModel() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("CCCc1cc(=O)nc([nH]1)S");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assertions.assertEquals(1, result[0]);
        Assertions.assertEquals(1, result[1]);
    }

    @Test
    void testRecursiveSmarts27_cdkAromaticModel() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("CCCc1nc(c2n1[nH]c(nc2=O)c1cc(ccc1OCC)S(=O)(=O)N1CCN(CC1)CC)C");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assertions.assertEquals(1, result[0]);
        Assertions.assertEquals(1, result[1]);
    }

    @Test
    void testRecursiveSmarts27() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("CCCc1nc(c2n1[nH]c(nc2=O)c1cc(ccc1OCC)S(=O)(=O)N1CCN(CC1)CC)C");
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assertions.assertEquals(0, result[0]);
        Assertions.assertEquals(0, result[1]);
    }

    @Test
    void testRecursive28() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("Cc1ccc[n+]2c1[nH]cc(c2=O)c1n[nH]nn1");
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assertions.assertEquals(0, result[0]);
        Assertions.assertEquals(0, result[1]);
    }

    @Test
    void testRecursive28_cdkAromaticModel() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("Cc1ccc[n+]2c1[nH]cc(c2=O)c1n[nH]nn1");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assertions.assertEquals(1, result[0]);
        Assertions.assertEquals(1, result[1]);
    }

    @Test
    void testRecursive29() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("Cc1cc(=O)c(c[nH]1)C(=O)NC(c1ccc(cc1)O)C(=O)NC1C(=O)N2C1SCC(=C2C(=O)O)CSc1nnnn1C");
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assertions.assertEquals(0, result[0]);
        Assertions.assertEquals(0, result[1]);
    }

    @Test
    void nestedRecursion() throws Exception {
        assertThat(SMARTSSearchTest.match("[$(*C[$(*C)$(**N)])]", "CCCCN"), is(new int[]{2, 2}));
        assertThat(SMARTSSearchTest.match("[$(*C[$(*C)$(**N)])]", "CCN"), is(new int[]{1, 1}));
    }

    @Test
    void testRecursive29_cdkAromaticModel() throws Exception {
        SMARTSQueryTool sqt = smarts("[NX3;H2,H1;!$(NC=O)]");
        IAtomContainer smi = smiles("Cc1cc(=O)c(c[nH]1)C(=O)NC(c1ccc(cc1)O)C(=O)NC1C(=O)N2C1SCC(=C2C(=O)O)CSc1nnnn1C");
        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(smi);
        int[] result = SMARTSSearchTest.match(sqt, smi);
        Assertions.assertEquals(1, result[0]);
        Assertions.assertEquals(1, result[1]);
    }

    @Tag("SlowTest")
    @Test
    void testBasicAmineOnDrugs_cdkAromaticModel() throws Exception {
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
        Assertions.assertEquals(141, nmol);
        Assertions.assertEquals(4, nmatch);
    }

    @Tag("SlowTest")
    @Test
    void testBasicAmineOnDrugs() throws Exception {
        String filename = "drugs.smi";
        InputStream ins = this.getClass().getResourceAsStream(filename);

        SMARTSQueryTool sqt = new SMARTSQueryTool("[NX3;H2,H1;!$(NC=O)]", DefaultChemObjectBuilder.getInstance());

        // iterating SMILES reader doesn't allow us to turn off automatic aromaticity
        // perception
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        int nmatch = 0;
        int nmol = 0;
        for (String smi : new BufferedReader(new InputStreamReader(ins)).lines()
                                                                        .collect(Collectors.toList())) {
            IAtomContainer container = sp.parseSmiles(smi.split("\t")[0]);
            if (sqt.matches(container)) {
                nmatch++;
            }
            nmol++;
        }
        Assertions.assertEquals(141, nmol);
        Assertions.assertEquals(0, nmatch);
    }

    /**
     * @cdk.bug 1312
     */
    @Test
    void recursiveComponentGrouping() throws Exception {
        assertThat(SMARTSSearchTest.match("[O;D1;$(([a,A]).([A,a]))][CH]=O", "OC=O.c1ccccc1"), is(new int[]{1, 1}));
        assertThat(SMARTSSearchTest.match("[O;D1;$(([a,A]).([A,a]))][CH]=O", "OC=O"), is(new int[]{0, 0}));
    }

    /**
     * @cdk.bug 844
      */
    @Test
    void bug844() throws Exception {
        assertThat(SMARTSSearchTest.match("[*R0]-[$([NRD3][CR]=O)]", "N1(CC)C(=O)CCCC1"), is(new int[]{1, 1}));
    }
}
